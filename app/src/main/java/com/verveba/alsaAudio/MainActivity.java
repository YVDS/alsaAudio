package com.verveba.alsaAudio;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaRecorder;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;
import android.os.Bundle;
import android.widget.TextView;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import java.io.File;

public class MainActivity extends AppCompatActivity
        implements ActivityCompat.OnRequestPermissionsResultCallback {

    private static final int AUDIO_ECHO_REQUEST = 0;
    private MediaRecorder mediaRecorder;
    boolean isRecording;
    public State recorderState;
    TextView statusView;
    private String filePath = "/storage/emulated/0/Music/";
    AudioManager audioManager;

    public enum State {
        IDLE,
        INIT,
        //READY,
        //RECORDING,
        //STOPPED,
        ERROR
    }

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("nativeLib");
        System.loadLibrary("libtinyalsa");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        isRecording = false;
        recorderState = State.IDLE;

        /*
        // Example of a call to a native method
        TextView tv = (TextView) findViewById(R.id.sample_text);
        tv.setText(stringFromJNI());
        */
    }

    @Override
    protected void onDestroy() {
            if (isRecording) {
                stopRecording();
            }
            isRecording = false;
            recorderState = State.IDLE;

        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void startRecording(View view) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[] { Manifest.permission.RECORD_AUDIO },
                    AUDIO_ECHO_REQUEST);
            statusView.setText("Requesting RECORD_AUDIO Permission...");
            return;
        }
        startRecording();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (AUDIO_ECHO_REQUEST != requestCode) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        if (grantResults.length != 1  ||
                grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            statusView.setText("Error: Permission for RECORD_AUDIO was denied");
            Toast.makeText(getApplicationContext(),
                    getString(R.string.NeedRecordAudioPermission),
                    Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        statusView.setText("RECORD_AUDIO permission granted, touch " +
                getString(R.string.StartRecord) + " to begin");

        startRecording();
    }

    public void recorderInit(Context context) {
        audioManager = (AudioManager) context.getApplicationContext().getSystemService(AUDIO_SERVICE);
        mediaRecorder = new MediaRecorder();
        recorderState = State.INIT;
    }

    public boolean recorderSetup() {
        boolean result = true;

        audioManager.setMode(AudioManager.MODE_IN_CALL);
        audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL,
                                        audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL),
                                        0);

        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.VOICE_COMMUNICATION);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mediaRecorder.setOutputFile(filePath+"test.wav");

        try {
            mediaRecorder.prepare();
        } catch (Exception e) {
            result = false;
        }

        return result;
    }

    public void startRecording() {
        if(isRecording) return;

        boolean result;

        if(recorderState == State.IDLE) {
            recorderInit(this);
        }

        result = recorderSetup();

        if(result) {
            mediaRecorder.start();
            isRecording = true;
        } else {
            resetRecorder();
        }
    }

    public void stopRecording(View view) {
        stopRecording();
    }

    public void stopRecording() {
        mediaRecorder.stop();
        resetRecorder();
        isRecording = false;
    }


    public void resetRecorder() {
        audioManager.setMode(AudioManager.MODE_NORMAL);
        mediaRecorder.reset();
        mediaRecorder.release();
        recorderState = State.INIT;
    }

    /** JNI calls **/
    public native boolean startAlsaRecording();
    public native boolean stopAlsaRecording();
}
