#/bin/sh
export BUILD_DIR=`pwd`/build

cd ffmpeg

make -f Makefile.lib.mingw64 ffmpeg

make -f Makefile.jni.mingw64 all
