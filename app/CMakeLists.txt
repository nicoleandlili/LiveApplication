cmake_minimum_required(VERSION 3.4.1)


find_library(
        log-lib
        log )

include_directories(
        src/main/jni/x264/include)

include_directories(
        src/main/jni)

set(distribution_DIR ../../../../src/main/jni)


add_library(x264
        STATIC
        IMPORTED)
set_target_properties(x264
        PROPERTIES IMPORTED_LOCATION
        ${distribution_DIR}/jni/x264/libx264.a)

add_library(
        live
        SHARED
        src/main/jni/dn_live.c)

target_link_libraries(
        live
        android
        ${log-lib}
        ${jnigraphics-lib})
