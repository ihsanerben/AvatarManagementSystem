package com.ihsan.util;

import com.ihsan.service.LoggerService;
import net.coobird.thumbnailator.Thumbnails;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;


public class ImageUtils {

    private LoggerService loggerService;

    public static byte[] generateThumbnail(byte[] imageData) throws IOException {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(imageData);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        Thumbnails.of(inputStream)
                .scale(0.1f)  // %10 küçültme
                .outputFormat("jpeg")
                .toOutputStream(outputStream);

        return outputStream.toByteArray();
    }
}
