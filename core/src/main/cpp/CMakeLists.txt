# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

cmake_minimum_required(VERSION 3.10.2)

project("native_core")

add_library( # Sets the name of the library.
        native_core

        # Sets the library as a shared library.
        SHARED

        # Provides a relative path to your source file(s).
        native-lib.cpp
        ed25519/curve25519-donna-32bit.h
        ed25519/curve25519-donna-64bit.h
        ed25519/curve25519-donna-helpers.h
        ed25519/ed25519.c
        ed25519/ed25519.h
        ed25519/ed25519-donna.h
        ed25519/ed25519-donna-32bit-sse2.h
        ed25519/ed25519-donna-32bit-tables.h
        ed25519/ed25519-donna-64bit-tables.h
        ed25519/ed25519-donna-64bit-x86.h
        ed25519/ed25519-donna-64bit-x86-32bit.h
        ed25519/ed25519-donna-basepoint-table.h
        ed25519/ed25519-donna-batchverify.h
        ed25519/ed25519-donna-impl-base.h
        ed25519/ed25519-donna-portable.h
        ed25519/ed25519-donna-portable-identify.h
        ed25519/ed25519-hash.h
        ed25519/ed25519-randombytes.h
        ed25519/modm-donna-32bit.h
        ed25519/modm-donna-64bit.h
        sha512.c
        sha512.h
        sha512_hmac.c
        sha512_hmac.h
)

find_library( # Sets the name of the path variable.
        log-lib

        # Specifies the name of the NDK library that
        # you want CMake to locate.
        log)

target_link_libraries( # Specifies the target library.
        native_core

        # Links the target library to the log library
        # included in the NDK.
        ${log-lib})