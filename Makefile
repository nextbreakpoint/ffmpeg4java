.PHONY: clean
clean:
	mvn clean

.PHONY: compile
compile:
	mvn compile

.PHONY: package
package:
	mvn package -DskipTests=true

.PHONY: install
install:
	mvn install -DskipTests=true

.PHONY: extract
extract:
	test $(system)
	@echo System = $(system)
	cd ffmpeg4java-$(system) && ./extract.sh

.PHONY: verify
verify:
	test $(system)
	@echo System = $(system)
	#MAVEN_OPTS="--enable-preview --enable-native-access=ALL-UNNAMED -Xlog:library -Djava.library.path=ffmpeg4java-$(system)-test/target/lib -Djextract.trace.downcalls=true" mvn verify -P$(system)
	MAVEN_OPTS="--enable-preview --enable-native-access=ALL-UNNAMED -Djava.library.path=ffmpeg4java-$(system)-test/target/lib -Djextract.trace.downcalls=false" mvn verify -P$(system)

