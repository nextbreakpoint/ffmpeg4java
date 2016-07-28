#/bin/sh
export BUILD_DIR=`pwd`/build

cd ffmpeg

make -f Makefile.lib.linux ffmpeg

make -f Makefile.jni.linux all
