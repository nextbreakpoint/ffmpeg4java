#!/bin/sh

FFMPEG_HOME="Z:/Documents/projects/github/ffmpeg4java/ffmpeg4java-windows/native/ffmpeg"
INCLUDE_PATH="C:/Users/andrea/Documents/cygwin/usr/include"

export PATH=$PATH:${JEXTRACT_HOME}/bin

jextract @includes.txt \
  --output src/main/java \
  --target-package com.nextbreakpoint.ffmpeg4java.windows \
  --include-dir "${FFMPEG_HOME}" \
  --include-dir "${INCLUDE_PATH}" \
  --use-system-load-library \
  --library ffmpeg4java \
  --header-class-name Libffmpeg \
  libffmpeg.h