package com.panda;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ResumableDownloader {

    public static void downloadFile(String fileURL, String outputDir) throws IOException {
        URL url = new URL(fileURL);
        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
        httpConn.setRequestMethod("GET");

        // Detect filename from headers
        String rawContentDisposition = httpConn.getHeaderField("Content-Disposition");
        String fileName = null;

        if (rawContentDisposition != null && rawContentDisposition.contains("filename=")) {
            Matcher matcher = Pattern.compile("filename=\"?([^\"]+)\"?").matcher(rawContentDisposition);
            if (matcher.find()) {
                fileName = matcher.group(1);
            }
        }

        if (fileName == null) {
            fileName = fileURL.substring(fileURL.lastIndexOf('/') + 1);
        }

        File outputFile = new File(outputDir, fileName);
        long downloadedBytes = outputFile.exists() ? outputFile.length() : 0;

        // Re-open connection with Range support
        httpConn = (HttpURLConnection) url.openConnection();
        if (downloadedBytes > 0) {
            httpConn.setRequestProperty("Range", "bytes=" + downloadedBytes + "-");
            System.out.println("Resuming from byte: " + downloadedBytes);
        }

        int responseCode = httpConn.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_PARTIAL || responseCode == HttpURLConnection.HTTP_OK) {

            long totalSize = downloadedBytes + httpConn.getContentLengthLong();
            System.out.println("Downloading to: " + outputFile.getAbsolutePath());

            try (InputStream inputStream = httpConn.getInputStream();
                 RandomAccessFile raf = new RandomAccessFile(outputFile, "rw")) {

                raf.seek(downloadedBytes);
                byte[] buffer = new byte[8192];
                int bytesRead;
                long downloaded = downloadedBytes;

                long lastTime = System.currentTimeMillis();
                long bytesInInterval = 0;

                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    raf.write(buffer, 0, bytesRead);
                    downloaded += bytesRead;
                    bytesInInterval += bytesRead;

                    long now = System.currentTimeMillis();
                    if (now - lastTime >= 1000) {
                        double speedMBps = bytesInInterval / 1024.0 / 1024.0;
                        int percent = (int) ((downloaded * 100) / totalSize);
                        System.out.printf("\rDownloaded: %d%% (%.2f MB/s)", percent, speedMBps);
                        lastTime = now;
                        bytesInInterval = 0;
                    }
                }

                System.out.println("\nDownload complete.");
            }
        } else {
            System.out.println("Server returned response code: " + responseCode);
        }
        httpConn.disconnect();
    }
}
