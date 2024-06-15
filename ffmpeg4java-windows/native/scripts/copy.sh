#!/bin/sh

set -e

mkdir -p "$1" && cp ffmpeg/ffmpeg4java.dll "$1"
