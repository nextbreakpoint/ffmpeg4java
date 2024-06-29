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

.PHONY: dump
dump:
	test $(system)
	@echo System = $(system)
	cd ffmpeg4java-$(system) && ./dump.sh

.PHONY: verify
verify:
	test $(system)
	@echo System = $(system)
	#MAVEN_OPTS="--enable-preview --enable-native-access=ALL-UNNAMED -Djava.library.path=ffmpeg4java-$(system)-test/target/lib -Djextract.trace.downcalls=false -Xlog:library" mvn verify -P$(system)
	MAVEN_OPTS="--enable-preview --enable-native-access=ALL-UNNAMED -Djava.library.path=ffmpeg4java-$(system)-test/target/lib -Djextract.trace.downcalls=false $(properties)" mvn verify -P$(system)

.PHONY: copy-legal
copy-legal:
	cp legal/*.txt ffmpeg4java-macos/src/main/resources/META-INF/
	cp legal/*.txt ffmpeg4java-linux/src/main/resources/META-INF/
	cp legal/*.txt ffmpeg4java-windows/src/main/resources/META-INF/

.PHONY: copy-dist
copy-dist: clean package
	rm -fR dist
	mkdir -p dist
	cp ffmpeg4java-macos/target/com.nextbreakpoint.ffmpeg4java.macos-*.jar dist
	cp ffmpeg4java-linux/target/com.nextbreakpoint.ffmpeg4java.linux-*.jar dist
	cp ffmpeg4java-windows/target/com.nextbreakpoint.ffmpeg4java.windows-*.jar dist
