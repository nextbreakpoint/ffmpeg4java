#!/bin/sh

FFMPEG_HOME="Z:/Documents/projects/github/ffmpeg4java/ffmpeg4java-windows/native/ffmpeg"
INCLUDE_PATH="C:/Users/andrea/Documents/cygwin/usr/include"

export PATH=$PATH:${JEXTRACT_HOME}/bin

jextract --dump-includes includes.txt --include-dir "${FFMPEG_HOME}" --include-dir "${INCLUDE_PATH}" libffmpeg.h