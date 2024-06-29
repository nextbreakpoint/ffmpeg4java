#!/bin/bash

set -e

FFMPEG_HOME=$(pwd)/ffmpeg

if [ ! -d "$FFMPEG_HOME" ]; then
  git clone https://git.ffmpeg.org/ffmpeg.git "$FFMPEG_HOME"
else
  pushd "$FFMPEG_HOME"
  git fetch
  popd
fi

pushd "$FFMPEG_HOME"
git checkout af25a4bfd2503caf3ee485b27b99b620302f5718
popd
