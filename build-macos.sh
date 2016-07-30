#/bin/sh
export BUILD_DIR=`pwd`/build

rm -fR ffmpeg

git clone https://git.ffmpeg.org/ffmpeg.git ffmpeg

cp Makefile.lib.macos ffmpeg/Makefile.lib.macos

cp Makefile.jni.macos ffmpeg/Makefile.jni.macos

cd ffmpeg

make -f Makefile.lib.macos ffmpeg

make -f Makefile.jni.macos all
