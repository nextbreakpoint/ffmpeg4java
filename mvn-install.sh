#/bin/sh
export BUILD_DIR=`pwd`/build

mkdir -p $BUILD_DIR/com.nextbreakpoint.ffmpeg4java

cp maven/com.nextbreakpoint.ffmpeg4java/pom.xml $BUILD_DIR/com.nextbreakpoint.ffmpeg4java

cd $BUILD_DIR/com.nextbreakpoint.ffmpeg4java

mvn clean install
