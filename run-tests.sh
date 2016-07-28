#/bin/sh
export TESTS_DIR=`pwd`/tests

cd $TESTS_DIR

mvn clean verify $1
