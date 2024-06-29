#!/bin/sh

FFMPEG_HOME="$(cygpath -w $(pwd)/native/ffmpeg)"
INCLUDE_PATH="$(cygpath -w $CYGWIN_HOME/usr/include)"

export PATH=$PATH:${JEXTRACT_HOME}/bin

jextract --dump-includes includes.txt \
  --include-dir "${FFMPEG_HOME}" \
  --include-dir "${INCLUDE_PATH}" \
  libffmpeg.h