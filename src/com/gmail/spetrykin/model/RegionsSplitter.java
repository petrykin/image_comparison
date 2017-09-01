package com.gmail.spetrykin.model;

import java.awt.*;
import java.util.*;
import java.util.List;

public class RegionsSplitter {
    private static int minRegionSpacing = 15;

    static class HComparator implements Comparator<Integer> {
        private int imgWidth;

        public HComparator(int imgWidth) {
            this.imgWidth = imgWidth;
        }

        @Override
        public int compare(Integer o1, Integer o2) {
            return (o1 % imgWidth) - (o2 % imgWidth);
        }
    }

    public static void setMinRegionSpacing(int minRegionSpacing) {
        RegionsSplitter.minRegionSpacing = minRegionSpacing;
    }

    public static List<Rectangle> getRectangles(List<List<Integer>> regions, int imgWidth, int imgHeight) {
        List<Rectangle> rectangles = new ArrayList<>();
        for (List<Integer> region : regions) {
            rectangles.add(makeBorder(region, imgWidth, imgHeight));
        }
        return rectangles;
    }

    public static List<List<Integer>> divideIntoVerticalRegions(List<Integer> pixels, int imgWidth) {
        List<List<Integer>> verticalRegions = new ArrayList<>();
        Collections.sort(pixels);
        Queue<Integer> indexes = new ArrayDeque<>();
        indexes.offer(0);
        for (int i = 1; i < pixels.size(); i++) {
            if (pixels.get(i) / imgWidth - pixels.get(i - 1) / imgWidth > minRegionSpacing) {
                indexes.offer(i);
            }
        }
        indexes.offer(pixels.size());
        fillRegionsList(pixels, verticalRegions, indexes);
        return verticalRegions;
    }

    public static List<List<Integer>> divideIntoHorizontalRegions(List<Integer> pixels, int imgWidth) {
        List<List<Integer>> horizontalRegions = new ArrayList<>();
        Collections.sort(pixels, new HComparator(imgWidth));
        Queue<Integer> indexes = new ArrayDeque<>();
        indexes.offer(0);
        for (int i = 1; i < pixels.size(); i++) {
            if (pixels.get(i) % imgWidth - pixels.get(i - 1) % imgWidth > minRegionSpacing) {
                indexes.offer(i);
            }
        }
        indexes.offer(pixels.size());
        fillRegionsList(pixels, horizontalRegions, indexes);
        return horizontalRegions;
    }

    public static List<List<Integer>> divideIntoRegions(List<Integer> pixels, int imgWidth) {
        List<List<Integer>> areas = new ArrayList<>();
        for (List<Integer> verticalSortedPixels : divideIntoVerticalRegions(pixels, imgWidth)) {
            areas.addAll(divideIntoHorizontalRegions(verticalSortedPixels, imgWidth));
        }
        return areas;
    }

    private static void fillRegionsList(List<Integer> pixels, List<List<Integer>> list, Queue<Integer> indexes) {
        while (!indexes.isEmpty() && indexes.size() > 1) {
            int beginIndex = indexes.poll();
            int endIndex = indexes.peek();
            list.add(pixels.subList(beginIndex, endIndex));
        }
    }

    public static Rectangle makeBorder(List<Integer> pixels, int imgWidth, int imgHeight) {
        int left = imgWidth;
        int top = imgHeight;
        int right = 0;
        int bottom = 0;
        for (Integer pixel : pixels) {
            left = Math.min(left, pixel % imgWidth);
            top = Math.min(top, pixel / imgWidth);
            right = Math.max(right, pixel % imgWidth);
            bottom = Math.max(bottom, pixel / imgWidth);
        }
        return new Rectangle(left, top, right - left, bottom - top);
    }
}
