# FFmpeg4Java 7.0.1-1.0

FFmpeg4Java provides a Java wrapper of FFmpeg library version 7.0.1.


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

FFmpeg4Java is distributed as JAR file which contains Java classes and a native library. The native library must be installed in the library path of the Java runtime.   
Please note that the native library is statically liked with FFmpeg library under the terms of GNU Lesser General Public License, version 2.1. 
You should have received a copy of the LGPL v2.1 license with FFmpeg4Java. See http://ffmpeg.org for more information about FFmpeg.


## Binary distribution 

FFmpeg4Java is available in Maven Central Repository and [GitHub](https://github.com/nextbreakpoint/ffmpeg4java).

If you are using Maven, add one of the following dependencies in your POM:

    <dependency>
        <groupId>com.nextbreakpoint</groupId>
        <artifactId>com.nextbreakpoint.ffmpeg4java.macos</artifactId>
        <version>7.0.1-1.0</version>
    </dependency>
    
    <dependency>
        <groupId>com.nextbreakpoint</groupId>
        <artifactId>com.nextbreakpoint.ffmpeg4java.linux</artifactId>
        <version>7.0.1-1.0</version>
    </dependency>
    
    <dependency>
        <groupId>com.nextbreakpoint</groupId>
        <artifactId>com.nextbreakpoint.ffmpeg4java.windows</artifactId>
        <version>7.0.1-1.0</version>
    </dependency>


## Build

We recommend to use a Mac to build FFmpeg4Java because you can compile the library for all systems in one place, but that is not required. 
A Linux system con be used to compile the library for Linux and Windows (via cross compilation). You will need a Mac only to compile the library for MacOS. 

We assume you have a Mac, however the same instructions work for Linux. 
Make sue you have installed Xcode command line tools and Docker (or just Docker if you are on Linux).
We use Docker to simplify the toolchain configuration and ensure to have a consistent environment.

## Build the library for MacOS

Compile and install the library:

    make -C ffmpeg4java-macos/native build-lib install

The library will be installed in the resources directory of the module:

    ffmpeg4java-macos/src/main/resources/libffmpeg4java.dylib

Inspect the symbols exported by the library:

    nm -gU ffmpeg4java-macos/src/main/resources/libffmpeg4java.dylib

Inspect the libraries linked with the library: 

    otool -L ffmpeg4java-macos/src/main/resources/libffmpeg4java.dylib

## Build the library for Linux

Compile and install the library:

    make -C ffmpeg4java-linux/native build-lib install

The library will be installed in the resources directory of the module:

    ffmpeg4java-linux/src/main/resources/libffmpeg4java.so

Inspect the symbols exported by the library:

    objdump -t ffmpeg4java-linux/src/main/resources/libffmpeg4java.so

Inspect the libraries linked with the library:

    objdump -p ffmpeg4java-linux/src/main/resources/libffmpeg4java.so | grep .so

## Build the library for Windows

Compile and install the library:

    make -C ffmpeg4java-windows/native build-lib install

The library will be installed in the resources directory of the module:

    ffmpeg4java-windows/src/main/resources/ffmpeg4java.dll

Inspect the symbols exported by the library:

    objdump -t ffmpeg4java-windows/src/main/resources/ffmpeg4java.dll

Inspect the libraries linked with the library:

    objdump -p ffmpeg4java-windows/src/main/resources/ffmpeg4java.dll | grep .dll

## Build the Java code

Compile and package the JARs:

    make clean package

The artifacts will be created in the target directory of each module:

    ffmpeg4java-macos/target/com.nextbreakpoint.ffmpeg4java.macos-7.0.1-1.0-x86_64.jar
    ffmpeg4java-linux/target/com.nextbreakpoint.ffmpeg4java.linux-7.0.1-1.0-x86_64.jar
    ffmpeg4java-windows/target/com.nextbreakpoint.ffmpeg4java.windows-7.0.1-1.0-x86_64.jar

Install the artifacts in your local Maven repository:

    make install

## Test the Java code

Test the MacOS module (must be executed on MacOS):

    make verify system=macos

Test the Linux module (must be executed on Linux):

    make verify system=linux

Test the Windows module (must be executed on Windows):

    make verify system=windows

## Generate Java code

The Java code of all modules is generated from the FFmpeg header files by jextract.  

Download and install jextract for Java 22 for all target systems (see links below). 

Edit the source file in the target module with the correct path of jextract and java.   

Generate the code for MacOS (must be executed on MacOS):

    eval $(cat ffmpeg4java-macos/source) && make extract system=macos

Generate the code for Linux (must be executed on Linux):

    eval $(cat ffmpeg4java-linux/source) && make extract system=linux

Generate the code for Windows (must be executed on Windows):

    eval $(cat ffmpeg4java-windows/source) && make extract system=windows


# Examples

See the code of the tests for an example of how to use the wrapper. 


# Reference

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
