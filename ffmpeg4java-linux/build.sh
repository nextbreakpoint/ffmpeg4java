#!/bin/sh

mkdir -p "$(pwd)/build" && docker run --name ffmpeg4java-build --rm -i --volume="$(pwd)/build:/build" .
