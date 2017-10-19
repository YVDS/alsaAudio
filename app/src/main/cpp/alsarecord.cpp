//
// Created by Yannick on 10/12/2017.
//

#include <string.h>
#include <jni.h>
#include "pcm.h"
#include <stdio.h>
#include <stdlib.h>

static int write_file(const void *frames, size_t size)
{
    FILE *output_file = fopen("audio.raw", "wb");
    if (output_file == NULL) {
        perror("failed to open 'audio.raw' for writing");
        return EXIT_FAILURE;
    }
    fwrite(frames, 1, size, output_file);
    fclose(output_file);
    return 0;
}

static size_t read_frames(void **frames)
{
    unsigned int card = 0;
    unsigned int device = 0;
    int flags = PCM_IN;

    const struct pcm_config config = {
            .channels = 2,
            .rate = 48000,
            .format = PCM_FORMAT_S32_LE,
            .period_size = 1024,
            .period_count = 2,
            .start_threshold = 1024,
            .silence_threshold = 1024 * 2,
            .stop_threshold = 1024 * 2
    };

    struct pcm *pcm = pcm_open(card, device, flags, &config);
    if (pcm == NULL) {
        fprintf(stderr, "failed to allocate memory for PCM\n");
        return EXIT_FAILURE;
    } else if (!pcm_is_ready(pcm)){
        pcm_close(pcm);
        fprintf(stderr, "failed to open PCM\n");
        return EXIT_FAILURE;
    }

    unsigned int frame_size = pcm_frames_to_bytes(pcm, 1);
    unsigned int frames_per_sec = pcm_get_rate(pcm);

    *frames = malloc(frame_size * frames_per_sec);
    if (*frames == NULL) {
        fprintf(stderr, "failed to allocate frames\n");
        pcm_close(pcm);
        return EXIT_FAILURE;
    }

    int read_count = pcm_readi(pcm, *frames, frames_per_sec);

    size_t byte_count = pcm_frames_to_bytes(pcm, read_count);

    pcm_close(pcm);

    return byte_count;
}

static jboolean startRecordPCM() {

    void *frames;
    size_t size;

    size = read_frames(&frames);
    if (size == 0) {
        return EXIT_FAILURE;
    }

    if (write_file(frames, size) < 0) {
        free(frames);
        return EXIT_FAILURE;
    }

    free(frames);

    return EXIT_SUCCESS;

}

static jboolean stopRecordPCM() {

}

JNIEXPORT jboolean JNICALL
Java_com_verveba_tapasaudiorec_MainActivity_startAlsaRecording( JNIEnv* env,
                                                  jobject thiz )
{
    return startRecordPCM();

}

JNIEXPORT jboolean JNICALL
Java_com_verveba_tapasaudiorec_MainActivity_stopAlsaRecording( JNIEnv* env,
                                                  jobject thiz )
{
    return stopRecordPCM();
}
