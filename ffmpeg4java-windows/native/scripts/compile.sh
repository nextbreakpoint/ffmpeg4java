#!/bin/sh

set -e

FFMPEG_HOME=$(pwd)/ffmpeg

LIB_TYPE=64

cd "$FFMPEG_HOME" || exit

git reset --hard
patch -p1 -N < ../ffmpeg.patch

make

x86_64-w64-mingw32-gcc --verbose \
  -o ffmpeg4java.dll \
  -shared \
  -m${LIB_TYPE} \
  -Wall \
  -fpic \
  -Wl,--out-implib=ffmpeg4java.dll.a \
  -Wl,-add-stdcall-alias \
  -Wl,--no-undefined \
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
  -Wl,-Bstatic \
  -lm \
  -lpthread \
  -Wl,-Bdynamic \
  -lws2_32 \
  -lbcrypt \
  -luuid \
  -lsecur32 \
  -lshlwapi \
  -lgdi32 \
  -lstrmiids \
  -lole32 \
  -loleaut32 \
  -lavicap32