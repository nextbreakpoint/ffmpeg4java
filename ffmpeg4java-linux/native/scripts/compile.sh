#!/bin/sh

set -e

FFMPEG_HOME=$(pwd)/ffmpeg

LIB_TYPE=64

cd "$FFMPEG_HOME" || exit

git reset --hard
patch -p1 -N < ../ffmpeg.patch

make

gcc --verbose \
  -o libffmpeg4java.so \
  -shared \
  -m${LIB_TYPE} \
  -Wall \
  -fpic \
  -Wl,-Bsymbolic \
  -Wl,--whole-archive \
  libavformat/libavformat.a \
  libavdevice/libavdevice.a \
  libavfilter/libavfilter.a \
  libavcodec/libavcodec.a \
  libavutil/libavutil.a \
  libswscale/libswscale.a \
  libswresample/libswresample.a \
  -Wl,--no-whole-archive \
  -lm
