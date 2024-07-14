# FFmpeg4Java 7.0.1-1.2

FFmpeg4Java provides a Java wrapper of FFmpeg library version 7.0.1.

Please note that starting from version 7.0.1-1.0 the wrapper requires Java 22 and the Java FFM API (which is still in preview). 
Older versions of the wrapper still work with Java 11 or later, but they use JNI with an old version of FFmpeg.     


## License

FFmpeg4Java is distributed under the terms of BSD 3-Clause License.

    Copyright (c) 2016-2024, Andrea Medeghini
    All rights reserved.

    Redistribution and use in source and binary forms, with or without
    modification, are permitted provided that the following conditions are met:

    * Redistributions of source code must retain the above copyright notice, this
        list of conditions and the following disclaimer.

    * Redistributions in binary form must reproduce the above copyright notice,
        this list of conditions and the following disclaimer in the documentation
        and/or other materials provided with the distribution.

    * Neither the name of FFmpeg4Java nor the names of its
        contributors may be used to endorse or promote products derived from
        this software without specific prior written permission.

    THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
    AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
    IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
    DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
    FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
    DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
    SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
    CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
    OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
    OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.


## Notice

FFmpeg4Java is distributed as independent JAR files, one for each supported operating system.
Each JAR file contains a native library which must be installed in the library path of the Java runtime.

Please note that the native library is statically liked with FFmpeg library under the terms of GNU Lesser General Public License, version 2.1. 
You should have received a copy of the LGPL v2.1 license with FFmpeg4Java. See http://ffmpeg.org for more information about FFmpeg.


## Get binaries 

FFmpeg4Java is available in Maven Central Repository and [GitHub](https://github.com/nextbreakpoint/ffmpeg4java).

If you are using Maven, add one of the following dependencies in your POM:

    <dependency>
        <groupId>com.nextbreakpoint</groupId>
        <artifactId>com.nextbreakpoint.ffmpeg4java.macos</artifactId>
        <version>7.0.1-1.2</version>
    </dependency>
    
    <dependency>
        <groupId>com.nextbreakpoint</groupId>
        <artifactId>com.nextbreakpoint.ffmpeg4java.linux</artifactId>
        <version>7.0.1-1.2</version>
    </dependency>
    
    <dependency>
        <groupId>com.nextbreakpoint</groupId>
        <artifactId>com.nextbreakpoint.ffmpeg4java.windows</artifactId>
        <version>7.0.1-1.2</version>
    </dependency>

Also, add one of the following to download the native library:

    <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-dependency-plugin</artifactId>
        <executions>
            <execution>
                <id>copy-lib</id>
                <phase>package</phase>
                <goals>
                    <goal>unpack</goal>
                </goals>
                <configuration>
                    <artifactItems>
                        <artifactItem>
                            <groupId>com.nextbreakpoint</groupId>
                            <artifactId>com.nextbreakpoint.ffmpeg4java.macos</artifactId>
                            <classifier>x86_64</classifier>
                            <type>jar</type>
                            <overWrite>true</overWrite>
                            <outputDirectory>${project.build.directory}/lib</outputDirectory>
                        </artifactItem>
                    </artifactItems>
                    <includes>**/*.dylib</includes>
                </configuration>
            </execution>
        </executions>
    </plugin>

    <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-dependency-plugin</artifactId>
        <executions>
            <execution>
                <id>copy-lib</id>
                <phase>package</phase>
                <goals>
                    <goal>unpack</goal>
                </goals>
                <configuration>
                    <artifactItems>
                        <artifactItem>
                            <groupId>com.nextbreakpoint</groupId>
                            <artifactId>com.nextbreakpoint.ffmpeg4java.linux</artifactId>
                            <classifier>x86_64</classifier>
                            <type>jar</type>
                            <overWrite>true</overWrite>
                            <outputDirectory>${project.build.directory}/lib</outputDirectory>
                        </artifactItem>
                    </artifactItems>
                    <includes>**/*.so</includes>
                </configuration>
            </execution>
        </executions>
    </plugin>

    <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-dependency-plugin</artifactId>
        <executions>
            <execution>
                <id>copy-lib</id>
                <phase>package</phase>
                <goals>
                    <goal>unpack</goal>
                </goals>
                <configuration>
                    <artifactItems>
                        <artifactItem>
                            <groupId>com.nextbreakpoint</groupId>
                            <artifactId>com.nextbreakpoint.ffmpeg4java.windows</artifactId>
                            <classifier>x86_64</classifier>
                            <type>jar</type>
                            <overWrite>true</overWrite>
                            <outputDirectory>${project.build.directory}/lib</outputDirectory>
                        </artifactItem>
                    </artifactItems>
                    <includes>**/*.dll</includes>
                </configuration>
            </execution>
        </executions>
    </plugin>


## Build libraries

We recommend to use a Mac to build FFmpeg4Java, because you can compile the library for all systems in one place, but that is not strictly required. 
A Linux system can be used to compile the library for Linux and Windows (via cross compilation), and use a Mac system only to compile the library for MacOS. 

We assume you have a Mac, however the same build instructions work for Linux (but only for compiling the library for Linux and Windows).

Make sue you have installed Xcode command line tools (only for MacOS), Docker, git, make, maven, and Java 22.

    We use Docker to simplify the toolchain configuration and ensure to have a consistent environment

### Build library for MacOS

Compile and install the library:

    make -C ffmpeg4java-macos/native build-lib install

The library will be installed in the resources directory of the module:

    ffmpeg4java-macos/src/main/resources/libffmpeg4java.dylib

Inspect the symbols exported by the library:

    nm -gU ffmpeg4java-macos/src/main/resources/libffmpeg4java.dylib

Inspect the libraries linked with the library: 

    otool -L ffmpeg4java-macos/src/main/resources/libffmpeg4java.dylib

### Build library for Linux

Compile and install the library:

    make -C ffmpeg4java-linux/native build-lib install

The library will be installed in the resources directory of the module:

    ffmpeg4java-linux/src/main/resources/libffmpeg4java.so

Inspect the symbols exported by the library:

    objdump -t ffmpeg4java-linux/src/main/resources/libffmpeg4java.so

Inspect the libraries linked with the library:

    objdump -p ffmpeg4java-linux/src/main/resources/libffmpeg4java.so | grep .so

### Build library for Windows

Compile and install the library:

    make -C ffmpeg4java-windows/native build-lib install

The library will be installed in the resources directory of the module:

    ffmpeg4java-windows/src/main/resources/ffmpeg4java.dll

Inspect the symbols exported by the library:

    objdump -t ffmpeg4java-windows/src/main/resources/ffmpeg4java.dll

Inspect the libraries linked with the library:

    objdump -p ffmpeg4java-windows/src/main/resources/ffmpeg4java.dll | grep .dll

## Build Java code

Compile and package the JARs:

    make clean package

The artifacts will be created in the target directory of each module:

    ffmpeg4java-macos/target/com.nextbreakpoint.ffmpeg4java.macos-7.0.1-1.2-x86_64.jar
    ffmpeg4java-macos/target/com.nextbreakpoint.ffmpeg4java.macos-7.0.1-1.2-x86_64-x86_64.jar

    ffmpeg4java-linux/target/com.nextbreakpoint.ffmpeg4java.linux-7.0.1-1.2-x86_64.jar
    ffmpeg4java-linux/target/com.nextbreakpoint.ffmpeg4java.linux-7.0.1-1.2-x86_64-x86_64.jar

    ffmpeg4java-windows/target/com.nextbreakpoint.ffmpeg4java.windows-7.0.1-1.2-x86_64.jar
    ffmpeg4java-windows/target/com.nextbreakpoint.ffmpeg4java.windows-7.0.1-1.2-x86_64-x86_64.jar

List the content of the JAR files:

    unzip -t ffmpeg4java-macos/target/com.nextbreakpoint.ffmpeg4java.macos-7.0.1-1.2.jar
    unzip -t ffmpeg4java-macos/target/com.nextbreakpoint.ffmpeg4java.macos-7.0.1-1.2-x86_64.jar

    unzip -t ffmpeg4java-linux/target/com.nextbreakpoint.ffmpeg4java.linux-7.0.1-1.2.jar
    unzip -t ffmpeg4java-linux/target/com.nextbreakpoint.ffmpeg4java.linux-7.0.1-1.2-x86_64.jar

    unzip -t ffmpeg4java-windows/target/com.nextbreakpoint.ffmpeg4java.windows-7.0.1-1.2.jar
    unzip -t ffmpeg4java-windows/target/com.nextbreakpoint.ffmpeg4java.windows-7.0.1-1.2-x86_64.jar

Install the artifacts in your local Maven repository:

    make install

## Test Java code

Test the MacOS module (must be executed on MacOS):

    make verify system=macos

Test the Linux module (must be executed on Linux):

    make verify system=linux

Test the Windows module (must be executed on Windows):

    make verify system=windows

## Generate Java code (only if you know what you are doing)

The Java code of all modules is generated from the FFmpeg header files by jextract.  

Download and install jextract for Java 22 for all target systems (see links below). 

Edit the source file in the target module updating the path of jextract and java.   

Generate the code for MacOS (must be executed on MacOS):

    eval $(cat ffmpeg4java-macos/source) && make extract system=macos

Generate the code for Linux (must be executed on Linux):

    eval $(cat ffmpeg4java-linux/source) && make extract system=linux

Generate the code for Windows (must be executed on Windows):

    eval $(cat ffmpeg4java-windows/source) && make extract system=windows


## Code examples

A simple example is provided for the module ffmpeg4Java-examples.

See the POM file for details about how to download the native library and execute the example:

    ffmpeg4java-examples/pom.xml


## References

Java Foreign Function and Memory API:
https://openjdk.org/jeps/454

jextract download:
https://jdk.java.net/jextract/

jextract guide:
https://github.com/openjdk/jextract/blob/master/doc/GUIDE.md

FFmpeg documentation:
https://ffmpeg.org/documentation.html

FFmpeg source code:
https://ffmpeg.org/download.html#get-sources

FFmpeg build notes:
https://ffmpeg.org/platform.html
