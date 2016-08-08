#/bin/sh
export BUILD_DIR=`pwd`/build

patch -p0 < Patch.mingw64

cd ffmpeg

make -f Makefile.lib.mingw64 ffmpeg
make -f Makefile.jni.mingw64 all
