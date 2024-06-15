#/bin/sh

mkdir -p `pwd`/build && docker run --name ffmpeg4java-mingw64 --rm -i --volume=`pwd`/build:/build ffmpeg4java-mingw64