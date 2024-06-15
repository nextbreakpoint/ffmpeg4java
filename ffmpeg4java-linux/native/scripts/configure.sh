#!/bin/sh

set -e

FFMPEG_HOME=$(pwd)/ffmpeg

LIB_TYPE=64
LIB_ARCH=x86_$LIB_TYPE

cd "$FFMPEG_HOME" || exit

./configure \
  --extra-cflags="-m${LIB_TYPE}" \
  --extra-ldflags="-m${LIB_TYPE}" \
  --arch=${LIB_ARCH} \
  --enable-static \
  --disable-shared \
  --enable-pic \
  --enable-pthreads \
  --disable-programs \
  --disable-debug \
  --disable-mmx
