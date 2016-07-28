package com.nextbreakpoint.ffmpeg4java;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

public class TestLib {
    private static final int IMAGE_WIDTH = 100;
    private static final int IMAGE_HEIGHT = 100;

    private static byte[] pixels;

/*    static {
        FreeImage4Java.FreeImage_Initialise(FreeImage4JavaConstants.TRUE);
    }

    @BeforeClass
    public static void setup() {
        pixels = createRandomPixels(IMAGE_WIDTH, IMAGE_HEIGHT);
    }

    @AfterClass
    public static void clean() {
        try {
            Files.delete(new File("test.png").toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void shouldLoadImage() {
        FIBITMAP dib = null;
        try {
            dib = FreeImage4Java.FreeImage_Load(FREE_IMAGE_FORMAT.FIF_PNG, "sample.png", 0);
            assertThat(dib, is(notNullValue()));
        }
        finally {
            if (dib != null) {
                FreeImage4Java.FreeImage_Unload(dib);
                dib.delete();
            }
        }

    }

    @Test
    public void shouldSaveImage() {
        FIBITMAP dib = null;
        try {
            dib = FreeImage4Java.FreeImage_Allocate(IMAGE_WIDTH, IMAGE_HEIGHT, 4, 0x00FF0000, 0x0000FF00, 0x000000FF);
            setImagePixels(dib, pixels);
            int result = FreeImage4Java.FreeImage_Save(FREE_IMAGE_FORMAT.FIF_PNG, dib, "test.png", 0);
            assertThat(result, is(equalTo(1)));
        }
        finally {
            if (dib != null) {
                FreeImage4Java.FreeImage_Unload(dib);
                dib.delete();
            }
        }
    }

    @Test
    public void shouldSaveAndLoadSameImage() {
        FIBITMAP expectedDib = null;
        FIBITMAP actualDib = null;
        try {
            expectedDib = FreeImage4Java.FreeImage_Allocate(IMAGE_WIDTH, IMAGE_HEIGHT, 4, 0x00FF0000, 0x0000FF00, 0x000000FF);
            setImagePixels(expectedDib, pixels);
            FreeImage4Java.FreeImage_Save(FREE_IMAGE_FORMAT.FIF_PNG, expectedDib, "test.png", 0);
            actualDib = FreeImage4Java.FreeImage_Load(FREE_IMAGE_FORMAT.FIF_PNG, "test.png", 0);
            assertThat(comparePixels(actualDib, expectedDib), is(equalTo(0)));
        }
        finally {
            if (expectedDib != null) {
                FreeImage4Java.FreeImage_Unload(expectedDib);
                expectedDib.delete();
            }
            if (actualDib != null) {
                FreeImage4Java.FreeImage_Unload(actualDib);
                actualDib.delete();
            }
        }
    }

    private int comparePixels(FIBITMAP actualDib, FIBITMAP expectedDib) {
        RGBQUAD expectedValue = new RGBQUAD();
        RGBQUAD actualValue = new RGBQUAD();
        int result = 0;
        for (int y = 0; y < IMAGE_HEIGHT; y++) {
            for (int x = 0; x < IMAGE_WIDTH; x++) {
                FreeImage4Java.FreeImage_GetPixelColor(expectedDib, x, y, expectedValue);
                FreeImage4Java.FreeImage_GetPixelColor(actualDib, x, y, actualValue);
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
                FreeImage4Java.FreeImage_SetPixelColor(dib, x, y, value);
            }
        }
        value.delete();
    }

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
    }*/
}
