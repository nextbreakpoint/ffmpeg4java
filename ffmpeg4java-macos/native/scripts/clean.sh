#!/bin/sh

set -e

FFMPEG_HOME=$(pwd)/ffmpeg

cd "$FFMPEG_HOME" || exit

make clean
