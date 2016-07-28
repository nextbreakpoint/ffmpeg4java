#/bin/sh

mkdir -p `pwd`/build && docker run --name ffmpeg4java-linux --rm -i --volume=`pwd`/build:/build ffmpeg4java-linux
