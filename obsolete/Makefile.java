BUILD_DIR?=Build
OUTPUT_DIR=$(BUILD_DIR)/com.nextbreakpoint.ffmpeg4java

all:
	mkdir -p $(OUTPUT_DIR)/src/main/java/com/nextbreakpoint/ffmpeg4java && swig -v -java -o $(BUILD_DIR)/ffmpeg4java_wrap.c -outdir $(OUTPUT_DIR)/src/main/java/com/nextbreakpoint/ffmpeg4java -package com.nextbreakpoint.ffmpeg4java ffmpeg4java.i
