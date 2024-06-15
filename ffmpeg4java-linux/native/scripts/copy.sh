#!/bin/sh

set -e

mkdir -p "$1" && cp ffmpeg/libffmpeg4java.so "$1"
