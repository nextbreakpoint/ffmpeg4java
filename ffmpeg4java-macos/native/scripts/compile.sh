#!/bin/sh

set -e

FFMPEG_HOME=$(pwd)/ffmpeg

LIB_TYPE=64

cd "$FFMPEG_HOME" || exit

git reset --hard
patch -p1 -N < ../ffmpeg.patch

make

gcc --verbose \
  -o libffmpeg4java.dylib \
  -dynamiclib \
  -m${LIB_TYPE} \
  -Wall \
  -fpic \
  -Wl,-force_load \
  libavformat/libavformat.a \
  -Wl,-force_load \
  libavdevice/libavdevice.a \
  -Wl,-force_load \
  libavfilter/libavfilter.a \
  -Wl,-force_load \
  libavcodec/libavcodec.a \
  -Wl,-force_load \
  libavutil/libavutil.a \
  -Wl,-force_load \
  libswscale/libswscale.a \
  -Wl,-force_load \
  libswresample/libswresample.a \
  -lm \
  -lz \
  -lbz2 \
  -liconv \
  -framework VideoToolbox \
  -framework AudioToolbox \
  -framework AVFoundation \
  -framework Foundation \
  -framework CoreImage \
  -framework CoreGraphics \
  -framework CoreMedia \
  -framework QuartzCore \
  -framework OpenGL \
  -framework OpenAL \
  -framework VideoDecodeAcceleration \
  -framework QTKit \
  -framework AppKit \
  -framework Security