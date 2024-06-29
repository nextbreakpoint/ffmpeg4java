#!/bin/sh

set -e

FFMPEG_HOME=$(pwd)/ffmpeg

LIB_TYPE=64
LIB_ARCH=x86_$LIB_TYPE

cd "$FFMPEG_HOME" || exit

./configure \
  --cross-prefix=${LIB_ARCH}-w64-mingw32- \
  --enable-cross-compile \
  --target-os=mingw32 \
  --extra-cflags="-m${LIB_TYPE}" \
  --extra-ldflags="-m${LIB_TYPE}" \
  --arch=${LIB_ARCH} \
  --enable-static \
  --disable-shared \
  --enable-pic \
  --disable-programs \
  --disable-debug \
  --disable-mmx