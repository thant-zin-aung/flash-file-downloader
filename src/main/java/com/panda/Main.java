package com.panda;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        System.out.println("Hello world!");
        ResumableDownloader.downloadFile("https://drive.usercontent.google.com/download?id=19L6ApMRr3LfZjvc9NuG5EDblU92GvunR&export=download&authuser=0&confirm=t&uuid=96943570-b19b-4df1-9496-1f191ed44dc0&at=AN8xHoqo8-CtU7M53Imw_Hr9C_GV:1753016074545", "D:\\project_test_files");
    }
}