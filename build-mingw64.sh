#/bin/sh
export BUILD_DIR=`pwd`/build

cd ffmpeg

git checkout ce36e74e75751c721185fbebaa4ee8714b44c5a5

cd ..

patch -p0 < ffmpeg/Patch.mingw64

cd ffmpeg

make -f Makefile.lib.mingw64 ffmpeg

make -f Makefile.jni.mingw64 all
