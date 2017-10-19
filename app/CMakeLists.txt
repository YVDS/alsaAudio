cmake_minimum_required(VERSION 3.4.1)

set(JNILIBS_DIR ${PROJECT_SOURCE_DIR}/src/main/jniLibs)

add_library(lib_tinyalsa SHARED IMPORTED)
set_target_properties(lib_tinyalsa PROPERTIES IMPORTED_LOCATION
    ${JNILIBS_DIR}/libtinyalsa.so)


add_library(alsarecord SHARED
            ${PROJECT_SOURCE_DIR}/src/main/cpp/alsarecord.cpp)

target_include_directories(alsarecord PRIVATE
                            ${PROJECT_SOURCE_DIR}/src/main/cpp)

# Include libraries needed for alsarecord
target_link_libraries(alsarecord
                      android
                      lib_tinyalsa
                      log)

