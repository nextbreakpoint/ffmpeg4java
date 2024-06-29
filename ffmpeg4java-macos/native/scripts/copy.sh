#!/bin/sh

set -e

mkdir -p "$1" && cp ffmpeg/libffmpeg4java.dylib "$1"
