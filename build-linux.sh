#/bin/sh
export BUILD_DIR=`pwd`/build

cd ffmpeg

git checkout ce36e74e75751c721185fbebaa4ee8714b44c5a5

cd ..

Patch.linux ffmpeg/Patch.linux
patch -p0 < ffmpeg/Patch.linux

cd ffmpeg

make -f Makefile.lib.linux ffmpeg

make -f Makefile.jni.linux all
