package com.nextbreakpoint.ffmpeg4java.macos;

import org.assertj.core.data.Offset;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.util.function.BiConsumer;

import static com.nextbreakpoint.ffmpeg4java.macos.Libffmpeg.avcodec_alloc_context3;
import static com.nextbreakpoint.ffmpeg4java.macos.Libffmpeg.avcodec_close;
import static com.nextbreakpoint.ffmpeg4java.macos.Libffmpeg.avcodec_open2;
import static com.nextbreakpoint.ffmpeg4java.macos.Libffmpeg.avcodec_parameters_from_context;
import static com.nextbreakpoint.ffmpeg4java.macos.Libffmpeg.avcodec_parameters_to_context;
import static com.nextbreakpoint.ffmpeg4java.macos.Libffmpeg.avcodec_receive_frame;
import static com.nextbreakpoint.ffmpeg4java.macos.Libffmpeg.avcodec_receive_packet;
import static com.nextbreakpoint.ffmpeg4java.macos.Libffmpeg.avcodec_send_frame;
import static com.nextbreakpoint.ffmpeg4java.macos.Libffmpeg.avcodec_send_packet;
import static com.nextbreakpoint.ffmpeg4java.macos.Libffmpeg.sws_freeContext;
import static com.nextbreakpoint.ffmpeg4java.macos.Libffmpeg.sws_getCachedContext;
import static com.nextbreakpoint.ffmpeg4java.macos.Libffmpeg.sws_scale;
import static com.nextbreakpoint.ffmpeg4java.macos.Libffmpeg_1.AV_CODEC_ID_NONE;
import static com.nextbreakpoint.ffmpeg4java.macos.Libffmpeg_1.AV_PKT_DATA_CPB_PROPERTIES;
import static com.nextbreakpoint.ffmpeg4java.macos.Libffmpeg_1.av_dump_format;
import static com.nextbreakpoint.ffmpeg4java.macos.Libffmpeg_1.av_frame_alloc;
import static com.nextbreakpoint.ffmpeg4java.macos.Libffmpeg_1.av_guess_format;
import static com.nextbreakpoint.ffmpeg4java.macos.Libffmpeg_1.av_packet_alloc;
import static com.nextbreakpoint.ffmpeg4java.macos.Libffmpeg_1.av_packet_rescale_ts;
import static com.nextbreakpoint.ffmpeg4java.macos.Libffmpeg_1.av_packet_unref;
import static com.nextbreakpoint.ffmpeg4java.macos.Libffmpeg_1.av_read_frame;
import static com.nextbreakpoint.ffmpeg4java.macos.Libffmpeg_1.av_stream_new_side_data;
import static com.nextbreakpoint.ffmpeg4java.macos.Libffmpeg_1.av_write_frame;
import static com.nextbreakpoint.ffmpeg4java.macos.Libffmpeg_1.av_write_trailer;
import static com.nextbreakpoint.ffmpeg4java.macos.Libffmpeg_1.avcodec_find_decoder;
import static com.nextbreakpoint.ffmpeg4java.macos.Libffmpeg_1.avcodec_find_encoder;
import static com.nextbreakpoint.ffmpeg4java.macos.Libffmpeg_1.avformat_alloc_context;
import static com.nextbreakpoint.ffmpeg4java.macos.Libffmpeg_1.avformat_close_input;
import static com.nextbreakpoint.ffmpeg4java.macos.Libffmpeg_1.avformat_find_stream_info;
import static com.nextbreakpoint.ffmpeg4java.macos.Libffmpeg_1.avformat_free_context;
import static com.nextbreakpoint.ffmpeg4java.macos.Libffmpeg_1.avformat_new_stream;
import static com.nextbreakpoint.ffmpeg4java.macos.Libffmpeg_1.avformat_open_input;
import static com.nextbreakpoint.ffmpeg4java.macos.Libffmpeg_1.avformat_write_header;
import static com.nextbreakpoint.ffmpeg4java.macos.Libffmpeg_1.avio_close;
import static com.nextbreakpoint.ffmpeg4java.macos.Libffmpeg_1.avio_open2;
import static com.nextbreakpoint.ffmpeg4java.macos.Libffmpeg_2.AVIO_FLAG_WRITE;
import static com.nextbreakpoint.ffmpeg4java.macos.Libffmpeg_2.AVMEDIA_TYPE_VIDEO;
import static com.nextbreakpoint.ffmpeg4java.macos.Libffmpeg_2.AV_PIX_FMT_RGB24;
import static com.nextbreakpoint.ffmpeg4java.macos.Libffmpeg_2.AV_PIX_FMT_YUV420P;
import static com.nextbreakpoint.ffmpeg4java.macos.Libffmpeg_2.C_POINTER;
import static com.nextbreakpoint.ffmpeg4java.macos.Libffmpeg_2.SWS_BILINEAR;
import static com.nextbreakpoint.ffmpeg4java.macos.Libffmpeg_2.av_free;
import static com.nextbreakpoint.ffmpeg4java.macos.Libffmpeg_2.av_image_alloc;
import static com.nextbreakpoint.ffmpeg4java.macos.Libffmpeg_2.av_image_fill_arrays;
import static com.nextbreakpoint.ffmpeg4java.macos.Libffmpeg_2.av_image_get_buffer_size;
import static java.lang.foreign.MemorySegment.NULL;
import static org.assertj.core.api.Assertions.assertThat;

class FFmpeg4JavaTest {
    @Test
    public void shouldDecode48Frames() {
        int frameCount = loadVideo(getFileName("fixture-video-48.mpg"), FFmpeg4JavaTest::printFrame);
        assertThat(frameCount).isEqualTo(48);
    }

    @Test
    public void shouldEncode48Frames() {
        int fps = 24;
        int frames = 48;
        int frameWidth = 640;
        int frameHeight = 480;
        int frameCount = saveVideo(getFileName("test-video-48.mpg"), fps, frames, frameWidth, frameHeight, FFmpeg4JavaTest::printFrame);
        assertThat(frameCount).isEqualTo(frames);
    }

    @Test
    public void shouldDecodeFrames() throws IOException {
        int frameWidth = 640;
        int frameHeight = 480;
        final BufferedImage image = new BufferedImage(frameWidth, frameHeight, BufferedImage.TYPE_INT_ARGB);
        loadVideo(getFileName("fixture-video-48.mpg"), (frameCount, bytes) -> saveFrame(frameCount, image, bytes));
        assertThat(error(convertFormat(loadImage(getFileName("fixture-frame-10.png"))), convertFormat(loadImage(getFileName("test-frame-10.png"))))).isCloseTo(0.0, Offset.offset(10.0));
        assertThat(error(convertFormat(loadImage(getFileName("fixture-frame-20.png"))), convertFormat(loadImage(getFileName("test-frame-20.png"))))).isCloseTo(0.0, Offset.offset(10.0));
        assertThat(error(convertFormat(loadImage(getFileName("fixture-frame-30.png"))), convertFormat(loadImage(getFileName("test-frame-30.png"))))).isCloseTo(0.0, Offset.offset(10.0));
        assertThat(error(convertFormat(loadImage(getFileName("fixture-frame-40.png"))), convertFormat(loadImage(getFileName("test-frame-40.png"))))).isCloseTo(0.0, Offset.offset(10.0));
    }

    @Test
    public void shouldEncodeFrames() throws IOException {
        int fps = 24;
        int frames = 48;
        int frameWidth = 640;
        int frameHeight = 480;
        final BufferedImage image = new BufferedImage(frameWidth, frameHeight, BufferedImage.TYPE_INT_ARGB);
        saveVideo(getFileName("test-video-48.mpg"), fps, frames, frameWidth, frameHeight, (frameCount, bytes) -> printFrame(frameCount, bytes, frames));
        loadVideo(getFileName("test-video-48.mpg"), (frameCount, bytes) -> saveFrame(frameCount, image, bytes));
        assertThat(error(convertFormat(loadImage(getFileName("fixture-frame-10.png"))), convertFormat(loadImage(getFileName("test-frame-10.png"))))).isCloseTo(0.0, Offset.offset(10.0));
        assertThat(error(convertFormat(loadImage(getFileName("fixture-frame-20.png"))), convertFormat(loadImage(getFileName("test-frame-20.png"))))).isCloseTo(0.0, Offset.offset(10.0));
        assertThat(error(convertFormat(loadImage(getFileName("fixture-frame-30.png"))), convertFormat(loadImage(getFileName("test-frame-30.png"))))).isCloseTo(0.0, Offset.offset(10.0));
        assertThat(error(convertFormat(loadImage(getFileName("fixture-frame-40.png"))), convertFormat(loadImage(getFileName("test-frame-40.png"))))).isCloseTo(0.0, Offset.offset(10.0));
    }

    private static int loadVideo(String fileName, BiConsumer<Integer, byte[]> consumer) {
        int frameCount = 0;

        try (var arena = Arena.ofConfined()) {
            var ppFormatCtx = NULL;
            var pCodecCtx = NULL;
            var pRGBFrame = NULL;
            var pYUVFrame = NULL;
            var pSwsContext = NULL;
            var pRGBBuffer = NULL;
            var pPacket = NULL;

            try {
                ppFormatCtx = arena.allocate(C_POINTER);

                final var inputFileName = arena.allocateFrom(fileName);

                if (avformat_open_input(ppFormatCtx, inputFileName, NULL, NULL) != 0) {
                    throw new RuntimeException(formatLoadVideoError(fileName, "Can't open file"));
                }

                final var pFormatCtx = ppFormatCtx.get(C_POINTER, 0);

                if (avformat_find_stream_info(pFormatCtx, NULL) != 0) {
                    throw new RuntimeException(formatLoadVideoError(fileName, "Can't find stream info"));
                }

                av_dump_format(pFormatCtx, 0, inputFileName, 0);

                final int nbStreams = AVFormatContext.nb_streams(pFormatCtx);
                final var pStreams = AVFormatContext.streams(pFormatCtx);

                var pCodecParameters = NULL;
                int videoStreamIndex = -1;
                var pCodec = NULL;

                for (int i = 0; i < nbStreams; i++) {
                    final var pStream = pStreams.getAtIndex(C_POINTER, i);

                    final var pVideoCodecParameters = AVStream.codecpar(pStream);

                    final var codecType = AVCodecParameters.codec_type(pVideoCodecParameters);

                    if (codecType == AVMEDIA_TYPE_VIDEO()) {
                        videoStreamIndex = i;
                        pCodecParameters = pVideoCodecParameters;
                        pCodec = avcodec_find_decoder(AVCodecParameters.codec_id(pVideoCodecParameters));
                        break;
                    }
                }

                if (videoStreamIndex == -1) {
                    throw new RuntimeException(formatLoadVideoError(fileName, "Can't find video stream"));
                }

                if (pCodec.equals(NULL)) {
                    throw new RuntimeException(formatLoadVideoError(fileName, "Can't find codec"));
                }

                pCodecCtx = avcodec_alloc_context3(pCodec);

                if (avcodec_parameters_to_context(pCodecCtx, pCodecParameters) != 0) {
                    throw new RuntimeException(formatLoadVideoError(fileName, "Can't copy codec parameters"));
                }

                if (pCodecCtx.equals(NULL)) {
                    throw new RuntimeException(formatLoadVideoError(fileName, "Can't allocate codec context"));
                }

                if (avcodec_open2(pCodecCtx, pCodec, NULL) < 0) {
                    throw new RuntimeException(formatLoadVideoError(fileName, "Can't open codec"));
                }

                final int frameWidth = AVCodecContext.width(pCodecCtx);
                final int frameHeight = AVCodecContext.height(pCodecCtx);
                final int pixelFormat = AVCodecContext.pix_fmt(pCodecCtx);

                pSwsContext = sws_getCachedContext(NULL, frameWidth, frameHeight, pixelFormat, frameWidth, frameHeight, AV_PIX_FMT_RGB24(), SWS_BILINEAR(), NULL, NULL, NULL);

                if (pSwsContext.equals(NULL)) {
                    throw new RuntimeException(formatLoadVideoError(fileName, "Can't allocate scale context"));
                }

                pRGBFrame = av_frame_alloc();
                pYUVFrame = av_frame_alloc();

                if (pRGBFrame.equals(NULL)) {
                    throw new RuntimeException(formatLoadVideoError(fileName, "Can't allocate RGB frame"));
                }

                if (pYUVFrame.equals(NULL)) {
                    throw new RuntimeException(formatLoadVideoError(fileName, "Can't allocate YUV frame"));
                }

                final int rgbByteSize = av_image_get_buffer_size(AV_PIX_FMT_RGB24(), frameWidth, frameHeight, 1);

                pRGBBuffer = arena.allocate(ValueLayout.OfByte.JAVA_BYTE, rgbByteSize);

                if (pRGBBuffer.equals(NULL)) {
                    throw new RuntimeException(formatLoadVideoError(fileName, "Can't allocate RGB buffer"));
                }

                av_image_fill_arrays(AVFrame.data(pRGBFrame), AVFrame.linesize(pRGBFrame), pRGBBuffer, AV_PIX_FMT_RGB24(), frameWidth, frameHeight, 1);

                pPacket = av_packet_alloc();

                if (pPacket.equals(NULL)) {
                    throw new RuntimeException(formatLoadVideoError(fileName, "Can't allocate packet"));
                }

                if (rgbByteSize != 3 * frameWidth * frameHeight) {
                    throw new RuntimeException(formatLoadVideoError(fileName, "Unexpected buffer size"));
                }

                final byte[] buffer = new byte[rgbByteSize];

                while (av_read_frame(pFormatCtx, pPacket) == 0) {
                    if (AVPacket.stream_index(pPacket) == videoStreamIndex) {
                        if (avcodec_send_packet(pCodecCtx, pPacket) == 0) {
                            while (avcodec_receive_frame(pCodecCtx, pYUVFrame) == 0) {
                                sws_scale(pSwsContext, AVFrame.data(pYUVFrame), AVFrame.linesize(pYUVFrame), 0, frameHeight, AVFrame.data(pRGBFrame), AVFrame.linesize(pRGBFrame));

                                var pData = AVFrame.data(pRGBFrame);
                                MemorySegment.copy(pData.get(C_POINTER, 0), 0, MemorySegment.ofArray(buffer), 0, rgbByteSize);

                                consumer.accept(++frameCount, buffer);
                            }
                        }
                    }
                }

                if (avcodec_send_packet(pCodecCtx, NULL) == 0) {
                    while (avcodec_receive_frame(pCodecCtx, pYUVFrame) == 0) {
                        sws_scale(pSwsContext, AVFrame.data(pYUVFrame), AVFrame.linesize(pYUVFrame), 0, frameHeight, AVFrame.data(pRGBFrame), AVFrame.linesize(pRGBFrame));

                        var pData = AVFrame.data(pRGBFrame);
                        MemorySegment.copy(pData.get(C_POINTER, 0), 0, MemorySegment.ofArray(buffer), 0, rgbByteSize);

                        consumer.accept(++frameCount, buffer);
                    }
                }
            } catch (Exception e) {
                if (!pCodecCtx.equals(NULL)) {
                    avcodec_close(pCodecCtx);
                }

                if (!pSwsContext.equals(NULL)) {
                    sws_freeContext(pSwsContext);
                }

                if (!pPacket.equals(NULL)) {
                    av_packet_unref(pPacket);
                }

                if (!pYUVFrame.equals(NULL)) {
                    av_free(pYUVFrame);
                }

                if (!pRGBFrame.equals(NULL)) {
                    av_free(pRGBFrame);
                }

                if (!ppFormatCtx.equals(NULL)) {
                    avformat_close_input(ppFormatCtx);
                }
            }
        }
        return frameCount;
    }

    private static int saveVideo(String fileName, int fps, int frames, int frameWidth, int frameHeight, BiConsumer<Integer, byte[]> consumer) {
        int frameCount = 0;

        try (var arena = Arena.ofConfined()) {
            var ppAVIOCtx = NULL;
            var pFormatCtx = NULL;
            var pOutputFormat = NULL;
            var pCodecCtx = NULL;
            var pRGBFrame = NULL;
            var pYUVFrame = NULL;
            var pSwsContext = NULL;
            var pRGBBuffer = NULL;
            var pYUVBuffer = NULL;
            var pProperties = NULL;

            try {
                pFormatCtx = avformat_alloc_context();

                if (pFormatCtx.equals(NULL)) {
                    throw new RuntimeException(formatSaveVideoError(fileName, "Can't allocate format context"));
                }

                var outputFileName = arena.allocateFrom(fileName);

                pOutputFormat = av_guess_format(NULL, outputFileName, NULL);

                if (pOutputFormat.equals(NULL)) {
                    throw new RuntimeException(formatSaveVideoError(fileName, "Can't allocate output format"));
                }

                if (AVOutputFormat.video_codec(pOutputFormat) == AV_CODEC_ID_NONE()) {
                    throw new RuntimeException(formatSaveVideoError(fileName, "Can't find codec"));
                }

                var pTimeBase = arena.allocate(AVRational.layout());
                AVRational.num(pTimeBase, 1);
                AVRational.den(pTimeBase, fps);

                AVFormatContext.oformat(pFormatCtx, pOutputFormat);

                var pCodec = avcodec_find_encoder(AVOutputFormat.video_codec(pOutputFormat));

                if (pCodec.equals(NULL)) {
                    throw new RuntimeException(formatSaveVideoError(fileName, "Can't find codec"));
                }

                pCodecCtx = avcodec_alloc_context3(pCodec);

                if (pCodecCtx.equals(NULL)) {
                    throw new RuntimeException(formatSaveVideoError(fileName, "Can't allocate codec context"));
                }

                AVCodecContext.codec_id(pCodecCtx, AVOutputFormat.video_codec(pOutputFormat));
                AVCodecContext.codec_type(pCodecCtx, AVMEDIA_TYPE_VIDEO());
                AVCodecContext.width(pCodecCtx, frameWidth);
                AVCodecContext.height(pCodecCtx, frameHeight);
                AVCodecContext.pix_fmt(pCodecCtx, AV_PIX_FMT_YUV420P());
                AVCodecContext.time_base(pCodecCtx, pTimeBase);
                AVCodecContext.gop_size(pCodecCtx, 4);
                AVCodecContext.bit_rate(pCodecCtx, 4096);
                AVCodecContext.max_b_frames(pCodecCtx, 2);

                final var pStream = avformat_new_stream(pFormatCtx, pCodec);

                if (pStream.equals(NULL)) {
                    throw new RuntimeException(formatSaveVideoError(fileName, "Can't create stream"));
                }

                if (AVFormatContext.nb_streams(pFormatCtx) != 1) {
                    throw new RuntimeException(formatSaveVideoError(fileName, "Unexpected number of streams"));
                }

                var pCodecParameters = arena.allocate(AVCodecParameters.layout());

                if (pCodecParameters.equals(NULL)) {
                    throw new RuntimeException(formatSaveVideoError(fileName, "Can't allocate codec parameters"));
                }

                if (avcodec_parameters_from_context(pCodecParameters, pCodecCtx) != 0) {
                    throw new RuntimeException(formatLoadVideoError(fileName, "Can't copy codec parameters"));
                }

                AVCodecParameters.codec_id(pCodecParameters, AVOutputFormat.video_codec(pOutputFormat));
                AVCodecParameters.codec_type(pCodecParameters, AVMEDIA_TYPE_VIDEO());
                AVCodecParameters.width(pCodecParameters, frameWidth);
                AVCodecParameters.height(pCodecParameters, frameHeight);
                AVStream.codecpar(pStream, pCodecParameters);
                AVStream.time_base(pStream, pTimeBase);

                pProperties = av_stream_new_side_data(pStream, AV_PKT_DATA_CPB_PROPERTIES(), AVCPBProperties.sizeof());
                AVCPBProperties.buffer_size(pProperties, frameWidth * frameHeight * 3L * 2L);

                if (avcodec_open2(pCodecCtx, pCodec, NULL) != 0) {
                    throw new RuntimeException(formatSaveVideoError(fileName, "Can't open codec"));
                }

                ppAVIOCtx = arena.allocate(C_POINTER);

                if (avio_open2(ppAVIOCtx, outputFileName, AVIO_FLAG_WRITE(), NULL, NULL) < 0) {
                    throw new RuntimeException(formatSaveVideoError(fileName, "Can't open file"));
                }

                final var pAVIOCtx = ppAVIOCtx.get(C_POINTER, 0);

                if (pAVIOCtx.equals(NULL)) {
                    throw new RuntimeException(formatLoadVideoError(fileName, "Can't allocate IO context"));
                }

                AVFormatContext.pb(pFormatCtx, pAVIOCtx);

                pSwsContext = sws_getCachedContext(NULL, frameWidth, frameHeight, AV_PIX_FMT_RGB24(), frameWidth, frameHeight, AV_PIX_FMT_YUV420P(), SWS_BILINEAR(), NULL, NULL, NULL);

                if (pSwsContext.equals(NULL)) {
                    throw new RuntimeException(formatLoadVideoError(fileName, "Can't allocate scale context"));
                }

                pRGBFrame = av_frame_alloc();
                pYUVFrame = av_frame_alloc();

                if (pRGBFrame.equals(NULL)) {
                    throw new RuntimeException(formatLoadVideoError(fileName, "Can't allocate RGB frame"));
                }

                if (pYUVFrame.equals(NULL)) {
                    throw new RuntimeException(formatLoadVideoError(fileName, "Can't allocate YUV frame"));
                }

                AVFrame.width(pRGBFrame, frameWidth);
                AVFrame.height(pRGBFrame, frameHeight);
                AVFrame.format(pRGBFrame, AV_PIX_FMT_RGB24());
                AVFrame.width(pYUVFrame, frameWidth);
                AVFrame.height(pYUVFrame, frameHeight);
                AVFrame.format(pYUVFrame, AV_PIX_FMT_YUV420P());

                av_image_alloc(AVFrame.data(pRGBFrame), AVFrame.linesize(pRGBFrame), frameWidth, frameHeight, AV_PIX_FMT_RGB24(), 1);
                av_image_alloc(AVFrame.data(pYUVFrame), AVFrame.linesize(pYUVFrame), frameWidth, frameHeight, AV_PIX_FMT_YUV420P(), 1);

                final int rgbByteSize = av_image_get_buffer_size(AV_PIX_FMT_RGB24(), frameWidth, frameHeight, 1);
                final int yuvByteSize = av_image_get_buffer_size(AV_PIX_FMT_YUV420P(), frameWidth, frameHeight, 1);

                pRGBBuffer = arena.allocate(rgbByteSize);
                pYUVBuffer = arena.allocate(yuvByteSize);

                if (pRGBBuffer.equals(NULL)) {
                    throw new RuntimeException(formatLoadVideoError(fileName, "Can't allocate RGB buffer"));
                }

                if (pYUVBuffer.equals(NULL)) {
                    throw new RuntimeException(formatLoadVideoError(fileName, "Can't allocate YUV buffer"));
                }

                av_image_fill_arrays(AVFrame.data(pRGBFrame), AVFrame.linesize(pRGBFrame), pRGBBuffer, AV_PIX_FMT_RGB24(), frameWidth, frameHeight, 1);
                av_image_fill_arrays(AVFrame.data(pYUVFrame), AVFrame.linesize(pYUVFrame), pYUVBuffer, AV_PIX_FMT_YUV420P(), frameWidth, frameHeight, 1);

                final var pPacket = av_packet_alloc();

                if (pPacket.equals(NULL)) {
                    throw new RuntimeException(formatLoadVideoError(fileName, "Can't allocate packet"));
                }

                AVPacket.stream_index(pPacket, AVStream.index(pStream));

                if (rgbByteSize != 3 * frameWidth * frameHeight) {
                    throw new RuntimeException(formatLoadVideoError(fileName, "Unexpected buffer size"));
                }

                byte[] buffer = new byte[rgbByteSize];

                avformat_write_header(pFormatCtx, NULL);

                for (int frame = 0; frame < frames; frame++) {
                    var pData = AVFrame.data(pRGBFrame);
                    fillPixels(buffer, frameWidth, frameHeight, ((float) frame / (frames - 1)));
                    MemorySegment.copy(MemorySegment.ofArray(buffer), 0, pData.get(C_POINTER, 0), 0, rgbByteSize);

                    consumer.accept(++frameCount, buffer);

                    sws_scale(pSwsContext, AVFrame.data(pRGBFrame), AVFrame.linesize(pRGBFrame), 0, frameHeight, AVFrame.data(pYUVFrame), AVFrame.linesize(pYUVFrame));

                    if (avcodec_send_frame(pCodecCtx, pYUVFrame) == 0) {
                        while (avcodec_receive_packet(pCodecCtx, pPacket) == 0) {
                            av_packet_rescale_ts(pPacket, AVCodecContext.time_base(pCodecCtx), AVStream.time_base(pStream));
                            av_write_frame(pFormatCtx, pPacket);
                        }
                        av_write_frame(pFormatCtx, NULL);
                    }
                }

                if (avcodec_send_frame(pCodecCtx, NULL) == 0) {
                    while (avcodec_receive_packet(pCodecCtx, pPacket) == 0) {
                        av_packet_rescale_ts(pPacket, AVCodecContext.time_base(pCodecCtx), AVStream.time_base(pStream));
                        av_write_frame(pFormatCtx, pPacket);
                    }
                    av_write_frame(pFormatCtx, NULL);
                }

                av_write_trailer(pFormatCtx);
            } catch (Exception e) {
                if (!ppAVIOCtx.equals(NULL)) {
                    avio_close(ppAVIOCtx);
                }

                if (!pCodecCtx.equals(NULL)) {
                    avcodec_close(pCodecCtx);
                }

                if (!pSwsContext.equals(NULL)) {
                    sws_freeContext(pSwsContext);
                }

                if (!pProperties.equals(NULL)) {
                    av_free(pProperties);
                }

                if (!pYUVFrame.equals(NULL)) {
                    av_free(pYUVFrame);
                }

                if (!pRGBFrame.equals(NULL)) {
                    av_free(pRGBFrame);
                }

                if (!pOutputFormat.equals(NULL)) {
                    av_free(pOutputFormat);
                }

                if (!pFormatCtx.equals(NULL)) {
                    avformat_free_context(pFormatCtx);
                }
            }
        }

        return frameCount;
    }

    private static String formatLoadVideoError(String fileName, String message) {
        return STR."Load video failed (file = \{fileName}). \{message}";
    }

    private static String formatSaveVideoError(String fileName, String message) {
        return STR."Save video failed (file = \{fileName}). \{message}";
    }

    private static void fillPixels(byte[] data, int width, int height, float color) {
        byte p = (byte) (255 * color);
        for (int x = 0; x < width; x += 1) {
            for (int y = 0; y < height; y += 1) {
                int j = (y * width + x) * 3;
                if ((x / 5) % 2 == 0 || (y / 5) % 2 == 1) {
                    data[j + 0] = p;
                    data[j + 1] = p;
                    data[j + 2] = p;
                } else {
                    data[j + 0] = 0;
                    data[j + 1] = 0;
                    data[j + 2] = 0;
                }
            }
        }
    }

    private static void writeImage(BufferedImage image, int frame, String name) {
        if (frame % 10 == 0) {
            try (FileOutputStream output = new FileOutputStream(name)) {
                ImageIO.write(image, "png", output);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void copyPixelsIntoImage(byte[] data, BufferedImage image) {
        int[] rgb_data = ((DataBufferInt) image.getData().getDataBuffer()).getData();
        for (int t = 0, k = 0; k < data.length; k += 3) {
            rgb_data[t++] = 0xFF000000 | ((((int) data[k]) << 16) & 0x00FF0000) | ((((int) data[k]) << 8) & 0x0000FF00) | ((((int) data[k])) & 0x000000FF);
        }
        image.setRGB(0, 0, image.getWidth(), image.getHeight(), rgb_data, 0, image.getWidth());
    }

    private static int[] loadImage(String file) throws IOException {
        BufferedImage image = ImageIO.read(new File(file));
        return image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth());
    }

    private static double error(byte[] data1, byte[] data2) {
        double error = 0;
        for (int j = 0; j < data1.length; j += 3) {
            error += distance(data1, data2, j);
        }
        return error / (data1.length / 3.0);
    }

    private static double distance(byte[] data1, byte[] data2, int i) {
        return Math.sqrt(Math.pow(data1[i + 0] - data2[i + 0], 2) + Math.pow(data1[i + 1] - data2[i + 1], 2) + Math.pow(data1[i + 2] - data2[i + 2], 2));
    }

    private static byte[] convertFormat(int[] data) {
        byte[] buffer = new byte[data.length * 3];
        for (int j = 0; j < data.length; j += 1) {
            buffer[j * 3 + 0] = (byte) (data[j] >> 16);
            buffer[j * 3 + 1] = (byte) (data[j] >> 8);
            buffer[j * 3 + 2] = (byte) (data[j] >> 0);
        }
        return buffer;
    }

    private static void printFrame(int frameCount, byte[] bytes) {
        System.out.println(STR."Frame \{frameCount}");
    }

    private static void printFrame(int frameCount, byte[] bytes, int frames) {
        System.out.println(STR."Frame \{frameCount} [ \{frameCount * 100 / frames}% ]");
    }

    private static String getFileName(String file) {
        return STR."\{System.getProperty("basedir", ".")}/\{file}";
    }

    private static void saveFrame(Integer frameCount, BufferedImage image, byte[] bytes) {
        copyPixelsIntoImage(bytes, image);
        writeImage(image, frameCount, getFileName(STR."test-frame-\{frameCount}.png"));
    }
}