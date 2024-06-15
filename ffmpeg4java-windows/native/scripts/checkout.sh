#!/bin/sh

set -e

test ! -d "$(pwd)/ffmpeg" && git clone https://git.ffmpeg.org/ffmpeg.git "$(pwd)/ffmpeg"

cd "$(pwd)/ffmpeg" && git checkout af25a4bfd2503caf3ee485b27b99b620302f5718
