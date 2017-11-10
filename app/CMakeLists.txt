cmake_minimum_required(VERSION 3.4.1)

set(JNILIBS_DIR ${PROJECT_SOURCE_DIR}/src/main/jniLibs/armeabi-v7a/)

add_library(lib_tinyalsa SHARED IMPORTED)
set_target_properties(lib_tinyalsa PROPERTIES IMPORTED_LOCATION ${JNILIBS_DIR}/libtinyalsa.so)

add_library(lib_cutils SHARED IMPORTED)
set_target_properties(lib_cutils PROPERTIES IMPORTED_LOCATION ${JNILIBS_DIR}/libcutils.so)

add_library(lib_cplusplus SHARED IMPORTED)
set_target_properties(lib_cplusplus PROPERTIES IMPORTED_LOCATION ${JNILIBS_DIR}/libc++.so)

add_library(lib_utils SHARED IMPORTED)
set_target_properties(lib_utils PROPERTIES IMPORTED_LOCATION ${JNILIBS_DIR}/libutils.so)

add_library(lib_backtrace SHARED IMPORTED)
set_target_properties(lib_backtrace PROPERTIES IMPORTED_LOCATION ${JNILIBS_DIR}/libbacktrace.so)

add_library(lib_base SHARED IMPORTED)
set_target_properties(lib_base PROPERTIES IMPORTED_LOCATION ${JNILIBS_DIR}/libbase.so)

add_library(lib_unwind SHARED IMPORTED)
set_target_properties(lib_unwind PROPERTIES IMPORTED_LOCATION ${JNILIBS_DIR}/libunwind.so)

add_library(lib_crypto SHARED IMPORTED)
set_target_properties(lib_crypto PROPERTIES IMPORTED_LOCATION ${JNILIBS_DIR}/libcrypto.so)

add_library(lib_binder SHARED IMPORTED)
set_target_properties(lib_binder PROPERTIES IMPORTED_LOCATION ${JNILIBS_DIR}/libbinder.so)

add_library(lib_lzma SHARED IMPORTED)
set_target_properties(lib_lzma PROPERTIES IMPORTED_LOCATION ${JNILIBS_DIR}/liblzma.so)

add_library(alsarecord SHARED ${PROJECT_SOURCE_DIR}/src/main/cpp/alsarecord.cpp)

target_include_directories(alsarecord PRIVATE ${PROJECT_SOURCE_DIR}/src/main/cpp)

# Include libraries needed for alsarecord
target_link_libraries(  alsarecord
                        android
                        lib_tinyalsa
                        lib_cutils
                        lib_cplusplus
                        lib_utils
                        lib_backtrace
                        lib_base
                        lib_unwind
                        lib_crypto
                        lib_binder
                        lib_lzma
                        log)

