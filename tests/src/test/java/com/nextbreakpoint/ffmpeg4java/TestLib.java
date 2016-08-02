package com.nextbreakpoint.ffmpeg4java;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

public class TestLib {
    private static final int IMAGE_WIDTH = 100;
    private static final int IMAGE_HEIGHT = 100;
    private static final int PKT_BIT_BUFFER_SIZE = 4096;
    private static final boolean GENERATE = true;

    private static byte[] pixels;

    private static final int AVERROR_EOF = -MKBETAG('E','O','F',' ');

    public static final int MKBETAG(char a, char b, char c, char d) {
        int na = Character.getNumericValue(a);
        int nb = Character.getNumericValue(b);
        int nc = Character.getNumericValue(c);
        int nd = Character.getNumericValue(d);
        return ((nd) | ((nc) << 8) | ((nb) << 16) | ((na) << 24));
    }

    @BeforeClass
    public static void setup() {
        pixels = createRandomPixels(IMAGE_WIDTH, IMAGE_HEIGHT);
    }

    @AfterClass
    public static void clean() {
//        try {
//            Files.delete(new File("test.mpg").toPath());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    @Test
    public void shouldLoadVideo() {
        FFmpeg4Java.avcodec_register_all();
        FFmpeg4Java.avdevice_register_all();
        FFmpeg4Java.avfilter_register_all();
        FFmpeg4Java.av_register_all();
        String file_name = "test.mpg";
        int fps = 24;
        int frame_count = 48;
        int frame_width = 640;
        int frame_height = 480;
//        saveVideo(file_name, fps, frame_count, frame_width, frame_height);
        loadVideo(file_name);
    }

    private void loadVideo(String file_name) {
        AVFormatContext format_context = null;
        AVCodecContext codec_context = null;
        AVCodec codec = null;
        AVFrame rgb_frame = null;
        AVFrame yuv_frame = null;
        SWIGTYPE_p_p_AVFormatContext p_p_format_context = FFmpeg4Java.swig_create_format_context_p_p();
        if (FFmpeg4Java.avformat_open_input(p_p_format_context, file_name, null, null) == 0) {
            format_context = FFmpeg4Java.swig_get_format_context(p_p_format_context);
            System.out.println(format_context.getIformat().getLong_name());
            if (FFmpeg4Java.avformat_find_stream_info(format_context, null) == 0) {
                FFmpeg4Java.av_dump_format(format_context, 0, file_name, 0);
            }
            System.out.println("Streams " + format_context.getNb_streams());
            for (int i = 0; i < format_context.getNb_streams(); i++) {
                AVStream stream = FFmpeg4Java.swig_get_stream_p(format_context.getStreams(), 0);
                System.out.println("Stream " + i + " frames = " + stream.getNb_frames());
            }
            System.out.println("codec " + format_context.getVideo_codec_id());
//            codec = FFmpeg4Java.avcodec_find_decoder(format_context.getVideo_codec_id());
            if (codec != null && FFmpeg4Java.avcodec_open2(codec_context, codec, null) == 0) {
                byte[] data = new byte[codec_context.getWidth() * codec_context.getHeight() * 3];
                int[] rgb_data = new int[codec_context.getWidth() * codec_context.getHeight()];
                BufferedImage image = new BufferedImage(codec_context.getWidth(), codec_context.getHeight(), BufferedImage.TYPE_INT_ARGB);
                SWIGTYPE_p_SwsContext sws_context = FFmpeg4Java.sws_getCachedContext(null, codec_context.getWidth(), codec_context.getHeight(), codec_context.getPix_fmt(), codec_context.getWidth(), codec_context.getHeight(), AVPixelFormat.AV_PIX_FMT_RGB24, FFmpeg4Java.SWS_BILINEAR, null, null, null);
                if (sws_context != null) {
                    rgb_frame = FFmpeg4Java.av_frame_alloc();
                    yuv_frame = FFmpeg4Java.av_frame_alloc();
                    if (rgb_frame != null && yuv_frame != null) {
                        int rgb_bit_buffer_size = FFmpeg4Java.av_image_get_buffer_size(AVPixelFormat.AV_PIX_FMT_RGB24, codec_context.getWidth(), codec_context.getHeight(), 1);
                        SWIGTYPE_p_uint8_t rgb_bit_buffer = SWIGTYPE_p_uint8_t.asTypePointer(FFmpeg4Java.av_malloc(rgb_bit_buffer_size));
                        if (rgb_bit_buffer != null) {
                            FFmpeg4Java.av_image_fill_arrays(rgb_frame.getData(), rgb_frame.getLinesize(), rgb_bit_buffer, AVPixelFormat.AV_PIX_FMT_RGB24, codec_context.getWidth(), codec_context.getHeight(), 0);
                            int frame = 0;
                            while (FFmpeg4Java.avcodec_receive_frame(codec_context, yuv_frame) >= 0) {
                                System.out.println("Frame " + frame++);
                                FFmpeg4Java.sws_scale(sws_context, yuv_frame.getData(), yuv_frame.getLinesize(), 0, codec_context.getHeight(), rgb_frame.getData(), rgb_frame.getLinesize());
                                FFmpeg4Java.swig_get_bytes(rgb_bit_buffer, data);
                                copyPixelsIntoBitmap(data, rgb_data, image);
//                              System.out.println(((double) packet.getDts() * (double) stream.getTime_base().getNum()) / (double) stream.getTime_base().getDen());
                                writeBitmap(image, frame);
                            }
                        }
                    }
                    FFmpeg4Java.sws_freeContext(sws_context);
                }
                FFmpeg4Java.avcodec_close(codec_context);
            }
            FFmpeg4Java.avformat_close_input(p_p_format_context);
        }
        if (rgb_frame != null) {
            FFmpeg4Java.av_freep(AVFrame.asVoidPointer(rgb_frame));
            rgb_frame.delete();
            rgb_frame = null;
        }
        if (yuv_frame != null) {
            FFmpeg4Java.av_freep(AVFrame.asVoidPointer(yuv_frame));
            yuv_frame.delete();
            yuv_frame = null;
        }
        if (codec != null) {
            codec.delete();
            codec = null;
        }
        if (codec_context != null) {
            codec_context.delete();
            codec_context = null;
        }
        if (format_context != null) {
            format_context.delete();
            format_context = null;
        }
        if (p_p_format_context != null) {
            FFmpeg4Java.av_freep(SWIGTYPE_p_p_AVFormatContext.asVoidPointer(p_p_format_context));
            p_p_format_context = null;
        }
    }

    private void writeBitmap(BufferedImage image, int frame) {
        try {
            if (frame % 10 == 0) {
                ImageIO.write(image, "png", new FileOutputStream(new File(frame + ".png")));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void copyPixelsIntoBitmap(byte[] data, int[] rgb_data, BufferedImage image) {
        for (int t = 0, k = 0; k < data.length; k += 3) {
            rgb_data[t++] = 0xFF000000 | ((((int) data[k + 0]) << 16) & 0x00FF0000) | ((((int) data[k + 0]) << 8) & 0x0000FF00) | ((((int) data[k + 0]) << 0) & 0x000000FF);
        }
        image.setRGB(0, 0, image.getWidth(), image.getHeight(), rgb_data, 0, image.getWidth());
    }

    private void saveVideo(String file_name, int fps, int frame_count, int frame_width, int frame_height) {
        byte[] data = new byte[frame_width * frame_height * 3];
        AVFormatContext format_context = null;
        AVOutputFormat output_format = null;
        AVCodecContext codec_context = null;
        AVCodec codec = null;
        AVFrame rgb_frame = null;
        AVFrame yuv_frame = null;
        SWIGTYPE_p_uint8_t rgb_bit_buffer = null;
        SWIGTYPE_p_uint8_t yuv_bit_buffer = null;
        format_context = FFmpeg4Java.avformat_alloc_context();
        output_format = FFmpeg4Java.av_guess_format(null, file_name, null);
        if (format_context != null && output_format != null && !output_format.getVideo_codec().equals(AVCodecID.AV_CODEC_ID_NONE)) {
            format_context.setOformat(output_format);
            AVRational time_base = new AVRational();
            time_base.setNum(1);
            time_base.setDen(fps);
            codec = FFmpeg4Java.avcodec_find_encoder(output_format.getVideo_codec());
            codec_context = FFmpeg4Java.avcodec_alloc_context3(codec);
            if (codec != null && codec_context != null) {
                codec_context.setCodec_id(output_format.getVideo_codec());
                codec_context.setCodec_type(AVMediaType.AVMEDIA_TYPE_VIDEO);
                codec_context.setPix_fmt(AVPixelFormat.AV_PIX_FMT_YUV420P);
                codec_context.setWidth(frame_width);
                codec_context.setHeight(frame_height);
                codec_context.setTime_base(time_base);
                codec_context.setGop_size(4);
                codec_context.setBit_rate(4096);
                codec_context.setMax_b_frames(2);
                AVStream stream = FFmpeg4Java.avformat_new_stream(format_context, codec);
                if (stream != null && format_context.getNb_streams() == 1) {
                    AVCodecParameters params = new AVCodecParameters();
                    params.setCodec_id(output_format.getVideo_codec());
                    params.setCodec_type(AVMediaType.AVMEDIA_TYPE_VIDEO);
                    params.setWidth(frame_width);
                    params.setHeight(frame_height);
                    stream.setTime_base(time_base);
                    stream.setCodecpar(params);
//                    params.delete();
//                    AVCPBProperties props = new AVCPBProperties();
//                    SWIGTYPE_p_uint8_t side_data = FFmpeg4Java.av_stream_new_side_data(stream, AVPacketSideDataType.AV_PKT_DATA_CPB_PROPERTIES, props.size_of());
//                    props = props.from_data(side_data);
//                    props.setBuffer_size(codec_context.getWidth() * codec_context.getHeight() * 4);
//                    side_data = FFmpeg4Java.av_stream_get_side_data(stream, AVPacketSideDataType.AV_PKT_DATA_CPB_PROPERTIES, null);
//                    props = props.from_data(side_data);
//                    System.out.println(props.getBuffer_size());
                    if (FFmpeg4Java.avcodec_open2(codec_context, codec, null) == 0) {
                        if (FFmpeg4Java.avio_open2(format_context.get_aviocontext_p_p(), file_name, FFmpeg4Java.AVIO_FLAG_WRITE, null, null) >= 0) {
                            SWIGTYPE_p_SwsContext sws_context = FFmpeg4Java.sws_getCachedContext(null, codec_context.getWidth(), codec_context.getHeight(), AVPixelFormat.AV_PIX_FMT_RGB24, codec_context.getWidth(), codec_context.getHeight(), AVPixelFormat.AV_PIX_FMT_YUV420P, FFmpeg4Java.SWS_BILINEAR, null, null, null);
                            if (sws_context != null) {
                                rgb_frame = FFmpeg4Java.av_frame_alloc();
                                yuv_frame = FFmpeg4Java.av_frame_alloc();
                                if (rgb_frame != null && yuv_frame != null) {
                                    rgb_frame.setWidth(codec_context.getWidth());
                                    rgb_frame.setHeight(codec_context.getHeight());
                                    rgb_frame.setFormat(AVPixelFormat.AV_PIX_FMT_RGB24.swigValue());
                                    yuv_frame.setWidth(codec_context.getWidth());
                                    yuv_frame.setHeight(codec_context.getHeight());
                                    yuv_frame.setFormat(AVPixelFormat.AV_PIX_FMT_YUV420P.swigValue());
                                    FFmpeg4Java.av_image_alloc(rgb_frame.getData(), rgb_frame.getLinesize(), codec_context.getWidth(), codec_context.getHeight(), AVPixelFormat.AV_PIX_FMT_RGB24, 1);
                                    FFmpeg4Java.av_image_alloc(yuv_frame.getData(), yuv_frame.getLinesize(), codec_context.getWidth(), codec_context.getHeight(), AVPixelFormat.AV_PIX_FMT_YUV420P, 1);
                                    int rgb_bit_buffer_size = FFmpeg4Java.av_image_get_buffer_size(AVPixelFormat.AV_PIX_FMT_RGB24, codec_context.getWidth(), codec_context.getHeight(), 1);
                                    int yuv_bit_buffer_size = FFmpeg4Java.av_image_get_buffer_size(AVPixelFormat.AV_PIX_FMT_YUV420P, codec_context.getWidth(), codec_context.getHeight(), 1);
                                    rgb_bit_buffer = SWIGTYPE_p_uint8_t.asTypePointer(FFmpeg4Java.av_mallocz(rgb_bit_buffer_size));
                                    yuv_bit_buffer = SWIGTYPE_p_uint8_t.asTypePointer(FFmpeg4Java.av_mallocz(yuv_bit_buffer_size));
                                    if (rgb_bit_buffer != null && yuv_bit_buffer != null) {
                                        FFmpeg4Java.av_image_fill_arrays(rgb_frame.getData(), rgb_frame.getLinesize(), rgb_bit_buffer, AVPixelFormat.AV_PIX_FMT_RGB24, codec_context.getWidth(), codec_context.getHeight(), 1);
                                        FFmpeg4Java.av_image_fill_arrays(yuv_frame.getData(), yuv_frame.getLinesize(), yuv_bit_buffer, AVPixelFormat.AV_PIX_FMT_YUV420P, codec_context.getWidth(), codec_context.getHeight(), 1);
                                        AVPacket packet = FFmpeg4Java.av_packet_alloc();
                                        if (packet != null) {
                                            packet.setStream_index(stream.getIndex());
                                            FFmpeg4Java.avformat_write_header(format_context, null);
                                            for (int frame = 0; frame < frame_count; frame++) {
                                                System.out.println("Frame " + frame + " [ " + (frame * 100 / (frame_count - 1)) + "% ]");
                                                fillFrameWithRandomPixels(codec_context, data);
                                                FFmpeg4Java.swig_set_bytes(rgb_bit_buffer, data);
                                                FFmpeg4Java.sws_scale(sws_context, rgb_frame.getData(), rgb_frame.getLinesize(), 0, codec_context.getHeight(), yuv_frame.getData(), yuv_frame.getLinesize());
                                                if (FFmpeg4Java.avcodec_send_frame(codec_context, yuv_frame) == 0) {
                                                    while (FFmpeg4Java.avcodec_receive_packet(codec_context, packet) == 0) {
                                                        FFmpeg4Java.av_packet_rescale_ts(packet, codec_context.getTime_base(), stream.getTime_base());
                                                        FFmpeg4Java.av_write_frame(format_context, packet);
                                                    }
                                                }
                                            }
                                            if (FFmpeg4Java.avcodec_send_frame(codec_context, null) == 0) {
                                                while (FFmpeg4Java.avcodec_receive_packet(codec_context, packet) == 0) {
                                                    FFmpeg4Java.av_packet_rescale_ts(packet, codec_context.getTime_base(), stream.getTime_base());
                                                    FFmpeg4Java.av_write_frame(format_context, packet);
                                                }
                                            }
                                            FFmpeg4Java.av_write_trailer(format_context);
                                            FFmpeg4Java.av_freep(AVPacket.asVoidPointer(packet));
                                            packet.delete();
                                        }
                                    }
                                }
                                FFmpeg4Java.sws_freeContext(sws_context);
                            }
                            FFmpeg4Java.avio_close(format_context.getPb());
                        }
                        FFmpeg4Java.avcodec_close(codec_context);
                    }
                    stream.delete();
                    stream = null;
                }
            }
        }
        if (rgb_frame != null) {
            FFmpeg4Java.av_freep(AVFrame.asVoidPointer(rgb_frame));
            rgb_frame.delete();
            rgb_frame = null;
        }
        if (yuv_frame != null) {
            FFmpeg4Java.av_freep(AVFrame.asVoidPointer(yuv_frame));
            yuv_frame.delete();
            yuv_frame = null;
        }
        if (codec != null) {
            codec.delete();
            codec = null;
        }
        if (codec_context != null) {
            codec_context.delete();
            codec_context = null;
        }
        if (output_format != null) {
            output_format.delete();
            output_format = null;
        }
        if (format_context != null) {
            format_context.delete();
            format_context = null;
        }
    }

    private void fillFrameWithRandomPixels(AVCodecContext codec_context, byte[] data) {
        byte x = 0;
        for (int j = 0; j < data.length; j += 3) {
            if (j % (codec_context.getWidth() * 3) == 0) {
                x = (byte) Math.rint(Math.random() * 255);
            }
            data[j + 0] = x;
            data[j + 1] = x;
            data[j + 2] = x;
        }
    }

/*    private int comparePixels(FIBITMAP actualDib, FIBITMAP expectedDib) {
        RGBQUAD expectedValue = new RGBQUAD();
        RGBQUAD actualValue = new RGBQUAD();
        int result = 0;
        for (int y = 0; y < IMAGE_HEIGHT; y++) {
            for (int x = 0; x < IMAGE_WIDTH; x++) {
                FFmpeg4Java.FreeImage_GetPixelColor(expectedDib, x, y, expectedValue);
                FFmpeg4Java.FreeImage_GetPixelColor(actualDib, x, y, actualValue);
                if (expectedValue.getRgbRed() != actualValue.getRgbRed()) {
                    result += 1;
                } else if (expectedValue.getRgbGreen() != actualValue.getRgbGreen()) {
                    result += 1;
                } else if (expectedValue.getRgbBlue() != actualValue.getRgbBlue()) {
                    result += 1;
                } else if (expectedValue.getRgbReserved() != actualValue.getRgbReserved()) {
                    result += 1;
                }
            }
        }
        expectedValue.delete();
        actualValue.delete();
        return result;
    }

    private void setImagePixels(FIBITMAP dib, byte[] pixels) {
        RGBQUAD value = new RGBQUAD();
        for (int y = 0; y < IMAGE_HEIGHT; y++) {
            int j = y * IMAGE_WIDTH;
            for (int x = 0; x < IMAGE_WIDTH; x++) {
                int i = (j + x) * 4;
                value.setRgbRed(pixels[i + 0]);
                value.setRgbGreen(pixels[i + 1]);
                value.setRgbBlue(pixels[i + 2]);
                value.setRgbReserved(pixels[i + 3]);
                FFmpeg4Java.FreeImage_SetPixelColor(dib, x, y, value);
            }
        }
        value.delete();
    }*/

    private static byte[] createRandomPixels(int imageWidth, int imageHeight) {
        byte[] pixels = new byte[imageHeight * imageWidth * 4];
        for (int y = 0; y < imageHeight; y++) {
            int j = y * imageWidth;
            for (int x = 0; x < imageWidth; x++) {
                int i = (j + x) * 4;
                pixels[i + 0] = (byte)(Math.random() * 255);
                pixels[i + 1] = (byte)(Math.random() * 255);
                pixels[i + 2] = (byte)(Math.random() * 255);
                pixels[i + 3] = (byte)0xFF;
            }
        }
        return pixels;
    }
}
