package com.panda;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;
import java.util.regex.*;

public class MultiThreadedDownloader {

    private static final int THREAD_COUNT = 4; // Increase for faster downloads
    private static final int BUFFER_SIZE = 8192;

    public void downloadFile(String fileURL, String outputDir) throws Exception {
        URL url = new URL(fileURL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("HEAD");

        long contentLength = conn.getContentLengthLong();
        String fileName = detectFileName(conn, fileURL);
        conn.disconnect();

        System.out.println("Downloading: " + fileName);
        System.out.println("File size: " + (contentLength / 1024 / 1024) + " MB");

        File outputFile = new File(outputDir, fileName);
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
        CountDownLatch latch = new CountDownLatch(THREAD_COUNT);

        long partSize = contentLength / THREAD_COUNT;
        long[] downloadedPerThread = new long[THREAD_COUNT];
        long startTime = System.currentTimeMillis();

        for (int i = 0; i < THREAD_COUNT; i++) {
            final int threadIndex = i;
            long startByte = partSize * i;
            long endByte = (i == THREAD_COUNT - 1) ? contentLength - 1 : startByte + partSize - 1;

            File partFile = new File(outputDir, fileName + ".part" + threadIndex);
            long existingSize = partFile.exists() ? partFile.length() : 0;
            long finalStart = startByte + existingSize;

            int finalThreadIndex = i;
            executor.submit(() -> {
                try {
                    downloadPart(fileURL, partFile, finalStart, endByte, downloadedPerThread, finalThreadIndex);
                } catch (IOException e) {
                    System.err.println("Thread " + finalThreadIndex + " error: " + e.getMessage());
                }
                latch.countDown();
            });
        }

        // Progress Monitor
        new Thread(() -> {
            long previousDownloaded = 0;
            long previousTime = System.currentTimeMillis();

            while (latch.getCount() > 0) {
                long totalDownloaded = 0;
                for (long l : downloadedPerThread) totalDownloaded += l;

                long now = System.currentTimeMillis();
                long timeDiff = now - previousTime;
                long bytesDiff = totalDownloaded - previousDownloaded;

                if (timeDiff >= 1000) {
                    double speedBps = bytesDiff / (timeDiff / 1000.0); // bytes/sec
                    double speedMBps = speedBps / (1024.0 * 1024.0);
                    double percent = (totalDownloaded * 100.0) / contentLength;

                    long remainingBytes = contentLength - totalDownloaded;
                    long etaSeconds = (long) (speedBps > 0 ? remainingBytes / speedBps : -1);
                    String etaStr = etaSeconds >= 0 ? formatETA(etaSeconds) : "Calculating...";

                    System.out.printf("\rDownloaded: %.2f%% (%.2f MB/s) | ETA: %s", percent, speedMBps, etaStr);

                    previousDownloaded = totalDownloaded;
                    previousTime = now;
                }

                try {
                    Thread.sleep(500);
                } catch (InterruptedException ignored) {}
            }
        }).start();

        latch.await();
        executor.shutdown();

        System.out.println("\nMerging parts...");
        mergeParts(outputDir, fileName, outputFile);
        System.out.println("Download complete: " + outputFile.getAbsolutePath());
    }

    private void downloadPart(String fileURL, File partFile, long start, long end, long[] downloaded, int index) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) new URL(fileURL).openConnection();
        conn.setRequestProperty("Range", "bytes=" + start + "-" + end);
        try (InputStream in = conn.getInputStream();
             RandomAccessFile raf = new RandomAccessFile(partFile, "rw")) {
            raf.seek(partFile.length());

            byte[] buffer = new byte[BUFFER_SIZE];
            int read;
            while ((read = in.read(buffer)) != -1) {
                raf.write(buffer, 0, read);
                downloaded[index] += read;
            }
        }
    }

    private void mergeParts(String outputDir, String fileName, File outputFile) throws IOException {
        try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(outputFile))) {
            for (int i = 0; i < THREAD_COUNT; i++) {
                File part = new File(outputDir, fileName + ".part" + i);
                try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(part))) {
                    byte[] buffer = new byte[BUFFER_SIZE];
                    int read;
                    while ((read = bis.read(buffer)) != -1) {
                        bos.write(buffer, 0, read);
                    }
                }
                part.delete(); // delete part after merging
            }
        }
    }

    private String detectFileName(HttpURLConnection conn, String fileURL) {
        String disposition = conn.getHeaderField("Content-Disposition");
        if (disposition != null) {
            Matcher matcher = Pattern.compile("filename=\"?([^\";]+)\"?").matcher(disposition);
            if (matcher.find()) return matcher.group(1);
        }
        return fileURL.substring(fileURL.lastIndexOf('/') + 1);
    }

    private static String formatETA(long seconds) {
        long mins = seconds / 60;
        long secs = seconds % 60;
        return mins > 0 ? String.format("%dm %ds", mins, secs) : String.format("%ds", secs);
    }
}
