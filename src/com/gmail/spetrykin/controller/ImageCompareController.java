package com.gmail.spetrykin.controller;

import com.gmail.spetrykin.model.Image;

import java.awt.*;
import java.io.File;
import java.util.List;

public class ImageCompareController {
    private double comparingTiming;
    private String resultFileName;

    public ImageCompareController() {
        resultFileName = "image_comparison_result.png";
    }

    public String getResultFileName() {
        return resultFileName;
    }

    public double getComparingTiming() {
        return comparingTiming;
    }

    public void compare(final File file1, final File file2, List<Rectangle> excludedRegions) {
        long start = System.currentTimeMillis();
        Image img1 = new Image(file1);
        Image img2 = new Image(file2);
        Image resultImage = img1.compare(img2, excludedRegions);
        Image.saveToFile(resultImage, "PNG", new File(resultFileName));
        comparingTiming = (System.currentTimeMillis() - start) / 1000.0;
    }
}
