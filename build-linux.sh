#/bin/sh
export BUILD_DIR=`pwd`/build

patch -p0 < Patch.linux

cd ffmpeg

make -f Makefile.lib.linux ffmpeg
make -f Makefile.jni.linux all
