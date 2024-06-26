#!/bin/bash

set -e

if [ ! -d "$(pwd)/ffmpeg" ]; then
  git clone https://git.ffmpeg.org/ffmpeg.git "$(pwd)/ffmpeg"
else
  pushd "$(pwd)/ffmpeg"
  git fetch
  popd
fi

pushd "$(pwd)/ffmpeg"
git checkout af25a4bfd2503caf3ee485b27b99b620302f5718
popd
