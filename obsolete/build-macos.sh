#/bin/sh
export BUILD_DIR=`pwd`/build

rm -fR FFmpeg

sh checkout.sh

cp Makefile.lib.macos FFmpeg/Makefile.lib.macos
cp Makefile.jni.macos FFmpeg/Makefile.jni.macos

patch -p0 < Patch.macos

cd FFmpeg

make -f Makefile.lib.macos ffmpeg
make -f Makefile.jni.macos all
