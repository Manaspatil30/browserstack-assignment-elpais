package com.manas.utils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class ImageDownloader {

    public static void downloadTo(String imageUrl, Path folder, String fileName) throws IOException {

        // Some articles don't have a clear cover image
        if (imageUrl == null || imageUrl.isBlank()) return;


        Files.createDirectories(folder);
        Path target = folder.resolve(fileName);

        // Stream directly to disk (no nees to hold full image in memory).
        try(InputStream in = new BufferedInputStream(new URL(imageUrl).openStream())) {
            Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
        }
    }
}
