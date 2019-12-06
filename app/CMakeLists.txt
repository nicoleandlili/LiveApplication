cmake_minimum_required(VERSION 3.4.1)


find_library(
        log-lib
        log )

include_directories(
        src/main/jni/faac/include)
include_directories(
        src/main/jni/rtmpdump/include)
include_directories(
        src/main/jni/x264/include)

include_directories(
        src/main/jni)

set(distribution_DIR ../../../../src/main/jni)


add_library(faac
        STATIC
        IMPORTED)
set_target_properties(faac
        PROPERTIES IMPORTED_LOCATION
        ${distribution_DIR}/faac/libfaac.a)

add_library(rtmp
        STATIC
        IMPORTED)
set_target_properties(rtmp
        PROPERTIES IMPORTED_LOCATION
        ${distribution_DIR}/rtmpdump/librtmp.a)

add_library(x264
        STATIC
        IMPORTED)
set_target_properties(x264
        PROPERTIES IMPORTED_LOCATION
        ${distribution_DIR}/x264/libx264.a)

add_library(
        live
        SHARED
        src/main/jni/dn_live.c)

target_link_libraries(
        live
        x264
        faac
        rtmp
        android
        ${log-lib}
        ${jnigraphics-lib})
#x264
#faac
#rtmp