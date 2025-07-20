package com.panda;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws Exception {
        System.out.println("Hello world!");
        MultiThreadedDownloader multiThreadedDownloader = new MultiThreadedDownloader();
//        multiThreadedDownloader.downloadFile("https://drive.usercontent.google.com/download?id=19L6ApMRr3LfZjvc9NuG5EDblU92GvunR&export=download&authuser=0&confirm=t&uuid=96943570-b19b-4df1-9496-1f191ed44dc0&at=AN8xHoqo8-CtU7M53Imw_Hr9C_GV:1753016074545", "D:\\project_test_files");
            multiThreadedDownloader.downloadFile("https://megadl.boats/download/Ata.Thambaycha.Naay.2025.720p.ZEE5.WEB-DL.YK(CM).mp4?download_token=MeF1NvjsqzV41cb3SiqK63LrdDjhjtC09C5eT3RSBjvl7PUz3bOHDOmEGNNCfCT71TBmHczE0873nf8G9txU4AuYkKyHdspqPG5q1gcn1SV56pjwa88dx0uWvYb7mAaNmtVFR56pjwa56pjwaWLvfmw5qqsH7EgiL0A88dx0uBNJKrLf388SnWKWgW2r2YcL7PmK3Q688dx0umpR1tBygp7AeP8pQyazXYUjpWehRrbISGjepGyI88dx0uhVBIfZpMkQyoAxmUwQR8p89hy4", "D:\\project_test_files");
    }
}