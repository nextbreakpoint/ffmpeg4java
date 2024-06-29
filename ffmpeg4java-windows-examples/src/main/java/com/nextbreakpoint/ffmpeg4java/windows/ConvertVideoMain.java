
package com.nextbreakpoint.ffmpeg4java.windows;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;

import static com.nextbreakpoint.ffmpeg4java.windows.Libffmpeg.avcodec_alloc_context3;
import static com.nextbreakpoint.ffmpeg4java.windows.Libffmpeg.avcodec_close;
import static com.nextbreakpoint.ffmpeg4java.windows.Libffmpeg.avcodec_open2;
import static com.nextbreakpoint.ffmpeg4java.windows.Libffmpeg.avcodec_parameters_from_context;
import static com.nextbreakpoint.ffmpeg4java.windows.Libffmpeg.avcodec_parameters_to_context;
import static com.nextbreakpoint.ffmpeg4java.windows.Libffmpeg.avcodec_receive_frame;
import static com.nextbreakpoint.ffmpeg4java.windows.Libffmpeg.avcodec_receive_packet;
import static com.nextbreakpoint.ffmpeg4java.windows.Libffmpeg.avcodec_send_frame;
import static com.nextbreakpoint.ffmpeg4java.windows.Libffmpeg.avcodec_send_packet;
import static com.nextbreakpoint.ffmpeg4java.windows.Libffmpeg.sws_freeContext;
import static com.nextbreakpoint.ffmpeg4java.windows.Libffmpeg.sws_getCachedContext;
import static com.nextbreakpoint.ffmpeg4java.windows.Libffmpeg.sws_scale;
import static com.nextbreakpoint.ffmpeg4java.windows.Libffmpeg_1.AV_CODEC_ID_NONE;
import static com.nextbreakpoint.ffmpeg4java.windows.Libffmpeg_1.AV_PKT_DATA_CPB_PROPERTIES;
import static com.nextbreakpoint.ffmpeg4java.windows.Libffmpeg_1.av_dump_format;
import static com.nextbreakpoint.ffmpeg4java.windows.Libffmpeg_1.av_frame_alloc;
import static com.nextbreakpoint.ffmpeg4java.windows.Libffmpeg_1.av_guess_format;
import static com.nextbreakpoint.ffmpeg4java.windows.Libffmpeg_1.av_packet_alloc;
import static com.nextbreakpoint.ffmpeg4java.windows.Libffmpeg_1.av_packet_rescale_ts;
import static com.nextbreakpoint.ffmpeg4java.windows.Libffmpeg_1.av_packet_unref;
import static com.nextbreakpoint.ffmpeg4java.windows.Libffmpeg_1.av_read_frame;
import static com.nextbreakpoint.ffmpeg4java.windows.Libffmpeg_1.av_stream_new_side_data;
import static com.nextbreakpoint.ffmpeg4java.windows.Libffmpeg_1.av_write_frame;
import static com.nextbreakpoint.ffmpeg4java.windows.Libffmpeg_1.av_write_trailer;
import static com.nextbreakpoint.ffmpeg4java.windows.Libffmpeg_1.avcodec_find_decoder;
import static com.nextbreakpoint.ffmpeg4java.windows.Libffmpeg_1.avcodec_find_encoder;
import static com.nextbreakpoint.ffmpeg4java.windows.Libffmpeg_1.avcodec_parameters_alloc;
import static com.nextbreakpoint.ffmpeg4java.windows.Libffmpeg_1.avformat_alloc_context;
import static com.nextbreakpoint.ffmpeg4java.windows.Libffmpeg_1.avformat_close_input;
import static com.nextbreakpoint.ffmpeg4java.windows.Libffmpeg_1.avformat_find_stream_info;
import static com.nextbreakpoint.ffmpeg4java.windows.Libffmpeg_1.avformat_free_context;
import static com.nextbreakpoint.ffmpeg4java.windows.Libffmpeg_1.avformat_new_stream;
import static com.nextbreakpoint.ffmpeg4java.windows.Libffmpeg_1.avformat_open_input;
import static com.nextbreakpoint.ffmpeg4java.windows.Libffmpeg_1.avformat_write_header;
import static com.nextbreakpoint.ffmpeg4java.windows.Libffmpeg_1.avio_close;
import static com.nextbreakpoint.ffmpeg4java.windows.Libffmpeg_1.avio_open2;
import static com.nextbreakpoint.ffmpeg4java.windows.Libffmpeg_2.AVIO_FLAG_WRITE;
import static com.nextbreakpoint.ffmpeg4java.windows.Libffmpeg_2.AVMEDIA_TYPE_VIDEO;
import static com.nextbreakpoint.ffmpeg4java.windows.Libffmpeg_2.AV_PIX_FMT_RGB24;
import static com.nextbreakpoint.ffmpeg4java.windows.Libffmpeg_2.AV_PIX_FMT_YUV420P;
import static com.nextbreakpoint.ffmpeg4java.windows.Libffmpeg_2.C_POINTER;
import static com.nextbreakpoint.ffmpeg4java.windows.Libffmpeg_2.SWS_BILINEAR;
import static com.nextbreakpoint.ffmpeg4java.windows.Libffmpeg_2.av_free;
import static com.nextbreakpoint.ffmpeg4java.windows.Libffmpeg_2.av_image_alloc;
import static com.nextbreakpoint.ffmpeg4java.windows.Libffmpeg_2.av_image_fill_arrays;
import static com.nextbreakpoint.ffmpeg4java.windows.Libffmpeg_2.av_image_get_buffer_size;
import static java.lang.foreign.MemorySegment.NULL;

class ConvertVideoMain {
    public static void main(String[] args) {
        if (args.length < 2) {
            throw new IllegalArgumentException("Invalid arguments");
        }

        final String sourceFileName = args[0];
        final String outputFileName = args[1];

        System.out.println("Converting video to quicktime...");

        System.out.println(STR."Source file \{sourceFileName}");
        System.out.println(STR."Output file \{outputFileName}");

        try (var arena = Arena.ofConfined()) {
            var pInputFileName = arena.allocateFrom(sourceFileName);
            var pOutputFileName = arena.allocateFrom(outputFileName);

            var ppInputFormatCtx = NULL;
            var pInputCodecCtx = NULL;
            var pInputSwsContext = NULL;
            var pInputRGBFrame = NULL;
            var pInputTMPFrame = NULL;
            var pInputRGBBuffer = NULL;
            var pInputPacket = NULL;
            var pOutputFormatCtx = NULL;
            var pOutputCodecCtx = NULL;
            var pOutputAVIOCtx = NULL;
            var pOutputSwsContext = NULL;
            var pOutputRGBFrame = NULL;
            var pOutputYUVFrame = NULL;
            var pOutputRGBBuffer = NULL;
            var pOutputYUVBuffer = NULL;

            try {
                ppInputFormatCtx = arena.allocate(C_POINTER);

                if (avformat_open_input(ppInputFormatCtx, pInputFileName, NULL, NULL) != 0) {
                    throw new RuntimeException(formatLoadVideoError(sourceFileName, "Can't open file"));
                }

                final var pInputFormatCtx = ppInputFormatCtx.get(C_POINTER, 0);

                if (avformat_find_stream_info(pInputFormatCtx, NULL) != 0) {
                    throw new RuntimeException(formatLoadVideoError(sourceFileName, "Can't find stream info"));
                }

                av_dump_format(pInputFormatCtx, 0, pInputFileName, 0);

                final int inputNbStreams = AVFormatContext.nb_streams(pInputFormatCtx);
                final var pInputStreams = AVFormatContext.streams(pInputFormatCtx);

                var pInputCodecParameters = NULL;
                int videoStreamIndex = -1;
                var pInputCodec = NULL;

                for (int i = 0; i < inputNbStreams; i++) {
                    final var pStream = pInputStreams.getAtIndex(C_POINTER, i);

                    final var pVideoCodecParameters = AVStream.codecpar(pStream);

                    final var codecType = AVCodecParameters.codec_type(pVideoCodecParameters);

                    if (codecType == AVMEDIA_TYPE_VIDEO()) {
                        videoStreamIndex = i;
                        pInputCodecParameters = pVideoCodecParameters;
                        pInputCodec = avcodec_find_decoder(AVCodecParameters.codec_id(pVideoCodecParameters));
                        break;
                    }
                }

                if (videoStreamIndex == -1) {
                    throw new RuntimeException(formatLoadVideoError(sourceFileName, "Can't find video stream"));
                }

                if (pInputCodec.equals(NULL)) {
                    throw new RuntimeException(formatLoadVideoError(sourceFileName, "Can't find decoder"));
                }

                pInputCodecCtx = avcodec_alloc_context3(pInputCodec);

                if (avcodec_parameters_to_context(pInputCodecCtx, pInputCodecParameters) != 0) {
                    throw new RuntimeException(formatLoadVideoError(sourceFileName, "Can't copy codec parameters"));
                }

                if (pInputCodecCtx.equals(NULL)) {
                    throw new RuntimeException(formatLoadVideoError(sourceFileName, "Can't allocate codec context"));
                }

                if (avcodec_open2(pInputCodecCtx, pInputCodec, NULL) < 0) {
                    throw new RuntimeException(formatLoadVideoError(sourceFileName, "Can't open decoder"));
                }

                final int frameWidth = AVCodecContext.width(pInputCodecCtx);
                final int frameHeight = AVCodecContext.height(pInputCodecCtx);
                final int pixelFormat = AVCodecContext.pix_fmt(pInputCodecCtx);

                pInputSwsContext = sws_getCachedContext(NULL, frameWidth, frameHeight, pixelFormat, frameWidth, frameHeight, AV_PIX_FMT_RGB24(), SWS_BILINEAR(), NULL, NULL, NULL);

                if (pInputSwsContext.equals(NULL)) {
                    throw new RuntimeException(formatLoadVideoError(sourceFileName, "Can't allocate scale context"));
                }

                pInputRGBFrame = av_frame_alloc();
                pInputTMPFrame = av_frame_alloc();

                if (pInputRGBFrame.equals(NULL)) {
                    throw new RuntimeException(formatLoadVideoError(sourceFileName, "Can't allocate RGB frame"));
                }

                if (pInputTMPFrame.equals(NULL)) {
                    throw new RuntimeException(formatLoadVideoError(sourceFileName, "Can't allocate TMP frame"));
                }

                final int inputRGBByteSize = av_image_get_buffer_size(AV_PIX_FMT_RGB24(), frameWidth, frameHeight, 1);

                pInputRGBBuffer = arena.allocate(ValueLayout.OfByte.JAVA_BYTE, inputRGBByteSize);

                if (pInputRGBBuffer.equals(NULL)) {
                    throw new RuntimeException(formatLoadVideoError(sourceFileName, "Can't allocate RGB buffer"));
                }

                av_image_fill_arrays(AVFrame.data(pInputRGBFrame), AVFrame.linesize(pInputRGBFrame), pInputRGBBuffer, AV_PIX_FMT_RGB24(), frameWidth, frameHeight, 1);

                pInputPacket = av_packet_alloc();

                if (pInputPacket.equals(NULL)) {
                    throw new RuntimeException(formatLoadVideoError(sourceFileName, "Can't allocate packet"));
                }

                pOutputFormatCtx = avformat_alloc_context();

                if (pOutputFormatCtx.equals(NULL)) {
                    throw new RuntimeException(formatSaveVideoError(outputFileName, "Can't allocate format context"));
                }

                final var pOutputFormat = av_guess_format(NULL, pOutputFileName, NULL);

                if (pOutputFormat.equals(NULL)) {
                    throw new RuntimeException(formatSaveVideoError(outputFileName, "Can't allocate output format"));
                }

                if (AVOutputFormat.video_codec(pOutputFormat) == AV_CODEC_ID_NONE()) {
                    throw new RuntimeException(formatSaveVideoError(outputFileName, "Video codec not found"));
                }

                AVFormatContext.oformat(pOutputFormatCtx, pOutputFormat);

                final var pOutputCodec = avcodec_find_encoder(AVOutputFormat.video_codec(pOutputFormat));

                if (pOutputCodec.equals(NULL)) {
                    throw new RuntimeException(formatSaveVideoError(outputFileName, "Can't find encoder"));
                }

                pOutputCodecCtx = avcodec_alloc_context3(pOutputCodec);

                if (pOutputCodecCtx.equals(NULL)) {
                    throw new RuntimeException(formatSaveVideoError(outputFileName, "Can't allocate codec context"));
                }

                final var pTimeBase = arena.allocate(AVRational.layout());
                AVRational.num(pTimeBase, 1);
                AVRational.den(pTimeBase, 24);

                AVCodecContext.codec_id(pOutputCodecCtx, AVOutputFormat.video_codec(pOutputFormat));
                AVCodecContext.codec_type(pOutputCodecCtx, AVMEDIA_TYPE_VIDEO());
                AVCodecContext.width(pOutputCodecCtx, frameWidth);
                AVCodecContext.height(pOutputCodecCtx, frameHeight);
                AVCodecContext.pix_fmt(pOutputCodecCtx, AV_PIX_FMT_YUV420P());
                AVCodecContext.time_base(pOutputCodecCtx, pTimeBase);
                AVCodecContext.gop_size(pOutputCodecCtx, 4);
                AVCodecContext.bit_rate(pOutputCodecCtx, 4096);
                AVCodecContext.max_b_frames(pOutputCodecCtx, 2);

                final var pOutputStream = avformat_new_stream(pOutputFormatCtx, pOutputCodec);

                if (pOutputStream.equals(NULL)) {
                    throw new RuntimeException(formatSaveVideoError(outputFileName, "Can't create stream"));
                }

                if (AVFormatContext.nb_streams(pOutputFormatCtx) != 1) {
                    throw new RuntimeException(formatSaveVideoError(outputFileName, "Unexpected number of streams"));
                }

                final var pOutputCodecParameters = avcodec_parameters_alloc();

                if (pOutputCodecParameters.equals(NULL)) {
                    throw new RuntimeException(formatSaveVideoError(outputFileName, "Can't allocate codec parameters"));
                }

                if (avcodec_parameters_from_context(pOutputCodecParameters, pOutputCodecCtx) != 0) {
                    throw new RuntimeException(formatLoadVideoError(outputFileName, "Can't copy codec parameters"));
                }

                AVCodecParameters.codec_id(pOutputCodecParameters, AVOutputFormat.video_codec(pOutputFormat));
                AVCodecParameters.codec_type(pOutputCodecParameters, AVMEDIA_TYPE_VIDEO());
                AVCodecParameters.width(pOutputCodecParameters, frameWidth);
                AVCodecParameters.height(pOutputCodecParameters, frameHeight);
                AVStream.codecpar(pOutputStream, pOutputCodecParameters);
                AVStream.time_base(pOutputStream, pTimeBase);

                final var pOutputProperties = av_stream_new_side_data(pOutputStream, AV_PKT_DATA_CPB_PROPERTIES(), AVCPBProperties.sizeof());
                AVCPBProperties.buffer_size(pOutputProperties, frameWidth * frameHeight * 3L * 2L);

                if (avcodec_open2(pOutputCodecCtx, pOutputCodec, NULL) != 0) {
                    throw new RuntimeException(formatSaveVideoError(outputFileName, "Can't open encoder"));
                }

                final var ppOutputAVIOCtx = arena.allocate(C_POINTER);

                if (avio_open2(ppOutputAVIOCtx, pOutputFileName, AVIO_FLAG_WRITE(), NULL, NULL) < 0) {
                    throw new RuntimeException(formatSaveVideoError(outputFileName, "Can't open file"));
                }

                pOutputAVIOCtx = ppOutputAVIOCtx.get(C_POINTER, 0);

                if (pOutputAVIOCtx.equals(NULL)) {
                    throw new RuntimeException(formatLoadVideoError(outputFileName, "Can't allocate IO context"));
                }

                AVFormatContext.pb(pOutputFormatCtx, pOutputAVIOCtx);

                pOutputSwsContext = sws_getCachedContext(NULL, frameWidth, frameHeight, AV_PIX_FMT_RGB24(), frameWidth, frameHeight, AV_PIX_FMT_YUV420P(), SWS_BILINEAR(), NULL, NULL, NULL);

                if (pOutputSwsContext.equals(NULL)) {
                    throw new RuntimeException(formatLoadVideoError(outputFileName, "Can't allocate scale context"));
                }

                pOutputRGBFrame = av_frame_alloc();
                pOutputYUVFrame = av_frame_alloc();

                if (pOutputRGBFrame.equals(NULL)) {
                    throw new RuntimeException(formatLoadVideoError(outputFileName, "Can't allocate RGB frame"));
                }

                if (pOutputYUVFrame.equals(NULL)) {
                    throw new RuntimeException(formatLoadVideoError(outputFileName, "Can't allocate YUV frame"));
                }

                AVFrame.width(pOutputRGBFrame, frameWidth);
                AVFrame.height(pOutputRGBFrame, frameHeight);
                AVFrame.format(pOutputRGBFrame, AV_PIX_FMT_RGB24());
                AVFrame.width(pOutputYUVFrame, frameWidth);
                AVFrame.height(pOutputYUVFrame, frameHeight);
                AVFrame.format(pOutputYUVFrame, AV_PIX_FMT_YUV420P());

                av_image_alloc(AVFrame.data(pOutputRGBFrame), AVFrame.linesize(pOutputRGBFrame), frameWidth, frameHeight, AV_PIX_FMT_RGB24(), 1);
                av_image_alloc(AVFrame.data(pOutputYUVFrame), AVFrame.linesize(pOutputYUVFrame), frameWidth, frameHeight, AV_PIX_FMT_YUV420P(), 1);

                final int outputRGBByteSize = av_image_get_buffer_size(AV_PIX_FMT_RGB24(), frameWidth, frameHeight, 1);
                final int outputYUVByteSize = av_image_get_buffer_size(AV_PIX_FMT_YUV420P(), frameWidth, frameHeight, 1);

                pOutputRGBBuffer = arena.allocate(outputRGBByteSize);
                pOutputYUVBuffer = arena.allocate(outputYUVByteSize);

                if (pOutputRGBBuffer.equals(NULL)) {
                    throw new RuntimeException(formatLoadVideoError(outputFileName, "Can't allocate RGB buffer"));
                }

                if (pOutputYUVBuffer.equals(NULL)) {
                    throw new RuntimeException(formatLoadVideoError(outputFileName, "Can't allocate YUV buffer"));
                }

                av_image_fill_arrays(AVFrame.data(pOutputRGBFrame), AVFrame.linesize(pOutputRGBFrame), pOutputRGBBuffer, AV_PIX_FMT_RGB24(), frameWidth, frameHeight, 1);
                av_image_fill_arrays(AVFrame.data(pOutputYUVFrame), AVFrame.linesize(pOutputYUVFrame), pOutputYUVBuffer, AV_PIX_FMT_YUV420P(), frameWidth, frameHeight, 1);

                final var pOutputPacket = av_packet_alloc();

                if (pOutputPacket.equals(NULL)) {
                    throw new RuntimeException(formatLoadVideoError(outputFileName, "Can't allocate packet"));
                }

                AVPacket.stream_index(pOutputPacket, AVStream.index(pOutputStream));

                if (inputRGBByteSize != 3 * frameWidth * frameHeight) {
                    throw new RuntimeException(formatLoadVideoError(sourceFileName, "Unexpected buffer size"));
                }

                if (outputRGBByteSize != 3 * frameWidth * frameHeight) {
                    throw new RuntimeException(formatLoadVideoError(outputFileName, "Unexpected buffer size"));
                }

                final byte[] buffer = new byte[outputRGBByteSize];

                avformat_write_header(pOutputFormatCtx, NULL);

                while (av_read_frame(pInputFormatCtx, pInputPacket) == 0) {
                    if (AVPacket.stream_index(pInputPacket) == videoStreamIndex) {
                        if (avcodec_send_packet(pInputCodecCtx, pInputPacket) == 0) {
                            while (avcodec_receive_frame(pInputCodecCtx, pInputTMPFrame) == 0) {
                                sws_scale(pInputSwsContext, AVFrame.data(pInputTMPFrame), AVFrame.linesize(pInputTMPFrame), 0, frameHeight, AVFrame.data(pInputRGBFrame), AVFrame.linesize(pInputRGBFrame));

                                final var pInputData = AVFrame.data(pInputRGBFrame);
                                MemorySegment.copy(pInputData.get(C_POINTER, 0), 0, MemorySegment.ofArray(buffer), 0, inputRGBByteSize);

                                final var pOutputData = AVFrame.data(pOutputRGBFrame);
                                MemorySegment.copy(MemorySegment.ofArray(buffer), 0, pOutputData.get(C_POINTER, 0), 0, outputRGBByteSize);

                                sws_scale(pOutputSwsContext, AVFrame.data(pOutputRGBFrame), AVFrame.linesize(pOutputRGBFrame), 0, frameHeight, AVFrame.data(pOutputYUVFrame), AVFrame.linesize(pOutputYUVFrame));

                                if (avcodec_send_frame(pOutputCodecCtx, pOutputYUVFrame) == 0) {
                                    while (avcodec_receive_packet(pOutputCodecCtx, pOutputPacket) == 0) {
                                        av_packet_rescale_ts(pOutputPacket, AVCodecContext.time_base(pOutputCodecCtx), AVStream.time_base(pOutputStream));
                                        av_write_frame(pOutputFormatCtx, pOutputPacket);
                                    }
                                    av_write_frame(pOutputFormatCtx, NULL);
                                }
                            }
                        }
                    }
                }

                if (avcodec_send_packet(pInputCodecCtx, NULL) == 0) {
                    while (avcodec_receive_frame(pInputCodecCtx, pInputTMPFrame) == 0) {
                        sws_scale(pInputSwsContext, AVFrame.data(pInputTMPFrame), AVFrame.linesize(pInputTMPFrame), 0, frameHeight, AVFrame.data(pInputRGBFrame), AVFrame.linesize(pInputRGBFrame));

                        final var pInputData = AVFrame.data(pInputRGBFrame);
                        MemorySegment.copy(pInputData.get(C_POINTER, 0), 0, MemorySegment.ofArray(buffer), 0, inputRGBByteSize);

                        final var pOutputData = AVFrame.data(pOutputRGBFrame);
                        MemorySegment.copy(MemorySegment.ofArray(buffer), 0, pOutputData.get(C_POINTER, 0), 0, outputRGBByteSize);

                        sws_scale(pOutputSwsContext, AVFrame.data(pOutputRGBFrame), AVFrame.linesize(pOutputRGBFrame), 0, frameHeight, AVFrame.data(pOutputYUVFrame), AVFrame.linesize(pOutputYUVFrame));

                        if (avcodec_send_frame(pOutputCodecCtx, pOutputYUVFrame) == 0) {
                            while (avcodec_receive_packet(pOutputCodecCtx, pOutputPacket) == 0) {
                                av_packet_rescale_ts(pOutputPacket, AVCodecContext.time_base(pOutputCodecCtx), AVStream.time_base(pOutputStream));
                                av_write_frame(pOutputFormatCtx, pOutputPacket);
                            }
                            av_write_frame(pOutputFormatCtx, NULL);
                        }
                    }
                }

                if (avcodec_send_frame(pOutputCodecCtx, NULL) == 0) {
                    while (avcodec_receive_packet(pOutputCodecCtx, pOutputPacket) == 0) {
                        av_packet_rescale_ts(pOutputPacket, AVCodecContext.time_base(pOutputCodecCtx), AVStream.time_base(pOutputStream));
                        av_write_frame(pOutputFormatCtx, pOutputPacket);
                    }
                    av_write_frame(pOutputFormatCtx, NULL);
                }

                av_write_trailer(pOutputFormatCtx);
            } finally {
                if (!pInputPacket.equals(NULL)) {
                    av_packet_unref(pInputPacket);
                }

                if (!pInputCodecCtx.equals(NULL)) {
                    avcodec_close(pInputCodecCtx);
                }

                if (!pInputSwsContext.equals(NULL)) {
                    sws_freeContext(pInputSwsContext);
                }

                if (!pInputTMPFrame.equals(NULL)) {
                    av_free(pInputTMPFrame);
                }

                if (!pInputRGBFrame.equals(NULL)) {
                    av_free(pInputRGBFrame);
                }

                if (!ppInputFormatCtx.equals(NULL)) {
                    avformat_close_input(ppInputFormatCtx);
                }

                if (!pOutputAVIOCtx.equals(NULL)) {
                    avio_close(pOutputAVIOCtx);
                }

                if (!pOutputCodecCtx.equals(NULL)) {
                    avcodec_close(pOutputCodecCtx);
                }

                if (!pOutputSwsContext.equals(NULL)) {
                    sws_freeContext(pOutputSwsContext);
                }

                if (!pOutputYUVFrame.equals(NULL)) {
                    av_free(pOutputYUVFrame);
                }

                if (!pOutputRGBFrame.equals(NULL)) {
                    av_free(pOutputRGBFrame);
                }

                if (!pOutputFormatCtx.equals(NULL)) {
                    avformat_free_context(pOutputFormatCtx);
                }
            }

            System.out.println("Video converted");
        }
    }

    private static String formatLoadVideoError(String fileName, String message) {
        return STR."Decode video error (file = \{fileName}). \{message}";
    }

    private static String formatSaveVideoError(String fileName, String message) {
        return STR."Encode video error (file = \{fileName}). \{message}";
    }
}