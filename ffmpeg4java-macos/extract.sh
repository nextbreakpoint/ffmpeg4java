#!/bin/sh

FFMPEG_HOME=$(pwd)/native/ffmpeg

export PATH=$PATH:${JEXTRACT_HOME}/bin

jextract @includes.txt \
  --output src/main/java \
  --target-package com.nextbreakpoint.ffmpeg4java.macos \
  --include-dir "${FFMPEG_HOME}" \
  --use-system-load-library \
  --library ffmpeg4java \
  --header-class-name Libffmpeg \
  libffmpeg.h