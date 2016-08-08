# FFmpeg4Java

FFmpeg4Java provides a JNI wrapper of ffmpeg library.


## License

FFmpeg4Java is distributed under the terms of BSD 3-Clause License.

    Copyright (c) 2016, Andrea Medeghini
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

FFmpeg4Java depends on FFmpeg library. See http://ffmpeg.org for details.

FFmpeg is used under the terms of GNU Lesser General Public License, version 2.1.

    FFmpeg library is statically linked with FFmpeg4Java's JNI library


## Generate JNI code

We use docker for generating JNI code.

    Before you continue install docker on your system

Create base image:

    sh create-image-base.sh

Create image for generating code:

    sh create-image-java.sh

Run a container from the image:

    sh run-image-java.sh

The Java code will be generated in directory:

    build/com.nextbreakpoint.ffmpeg4java/src/main/java

The JNI code will be generated in file:

    build/ffmpeg4java_wrap.cxx


## Build library for MacOS

Download and install command line tools from Apple Developer Center.

    Generate JNI code before compiling library for MacOS

Execute build script:

    sh build-macos.sh

The native library will be created in directory:

    build/com.nextbreakpoint.ffmpeg4java/src/main/resources/macos


## Build library for Linux

We use docker for compiling Linux library.

    Generate JNI code before compiling library for Linux

Create image for building library:

    sh create-image-linux.sh

Run a container from the image:

    sh run-image-linux.sh

The native library will be created in directory:

    build/com.nextbreakpoint.ffmpeg4java/src/main/resources/linux


## Build library for Windows (cross-compilation)

We use docker for cross-compiling Windows library using Mingw-w64.

    Generate JNI code before compiling library for Windows

Create image for building library:

    sh create-image-mingw64.sh

Run a container from the image:

    sh run-image-mingw64.sh

The native library will be created in directory:

    build/com.nextbreakpoint.ffmpeg4java/src/main/resources/win32


## Create and install artifacts 

We use Apache Maven to compile and package the artifacts.

    Generate JNI code and build libraries before creating artifacts

Create artifacts:

    sh mvn-package.sh

The artifacts will be created in directory:

    build/com.nextbreakpoint.ffmpeg4java/target

Install artifacts:

    sh mvn-install.sh

The artifacts will be installed in local maven repository (~/.m2/repository).


## Run tests

We use Apache Maven to run the tests.

    Install artifacts in local repository before running tests

Run tests on MacOS:

    sh run-tests.sh -Pmacos

Run tests on Linux:

    sh run-tests.sh -Plinux

Run tests on Windows:

    sh run-tests.sh -Pwin32


## TODO

- Add sample application to show how to use the library

