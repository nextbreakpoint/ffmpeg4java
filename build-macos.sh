#/bin/sh
export BUILD_DIR=`pwd`/build

rm -fR ffmpeg

sh checkout.sh

cp Makefile.lib.macos ffmpeg/Makefile.lib.macos
cp Makefile.jni.macos ffmpeg/Makefile.jni.macos

patch -p0 < Patch.macos

cd ffmpeg

make -f Makefile.lib.macos ffmpeg
make -f Makefile.jni.macos all
