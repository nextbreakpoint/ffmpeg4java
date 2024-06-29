#!/bin/sh

FFMPEG_HOME="$(cygpath -w $(pwd)/native/ffmpeg)"
INCLUDE_PATH="$(cygpath -w $CYGWIN_HOME/usr/include)"

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