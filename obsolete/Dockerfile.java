FROM ffmpeg4java-base:latest

COPY ffmpeg4java.i ffmpeg4java.i 

COPY Makefile.java Makefile.java

COPY build-java.sh build-java.sh

RUN chmod 700 build-java.sh

CMD ["sh", "-c", "/build-java.sh"]
