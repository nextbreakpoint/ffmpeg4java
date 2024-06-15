#!/bin/sh

FFMPEG_HOME=$(pwd)/native/ffmpeg

export PATH=$PATH:${JEXTRACT_HOME}/bin

jextract --dump-includes includes.txt --include-dir "${FFMPEG_HOME}" libffmpeg.h