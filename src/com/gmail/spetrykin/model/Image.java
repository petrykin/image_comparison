package com.gmail.spetrykin.model;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

import static java.lang.Math.sqrt;

public class Image {
    private static final int GRAYSCALE = 256;
    private static final double TOLERANCE = 0.1;

    private BufferedImage image;
    private Dimension imgDimension;

    public Image() {
        image = null;
        imgDimension = null;
    }

    public Image(final File file) {
        try {
            image = ImageIO.read(file);
            imgDimension = new Dimension(image.getWidth(), image.getHeight());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Image(int width, int height, int type) {
        image = new BufferedImage(width, height, type);
        imgDimension = new Dimension(image.getWidth(), image.getHeight());
    }

    public BufferedImage getImage() {
        return image;
    }

    public int width() {
        return imgDimension.width;
    }

    public int height() {
        return imgDimension.height;
    }

    public int size() {
        return imgDimension.width * imgDimension.height;
    }

    public int[] rgbArray() {
        int[] rgbArr = new int[this.size()];
        image.getRGB(0, 0, this.width(), this.height(), rgbArr, 0, this.width());
        return rgbArr;
    }

    public void drawRect(Image image, Rectangle rect, Color color) {
        if (rect.x + rect.width <= image.width() && rect.y + rect.height <= image.height()) {
            Graphics2D g = image.getImage().createGraphics();
            g.setColor(color);
            g.drawRect(rect.x, rect.y, rect.width, rect.height);
            g.dispose();
        }
    }

    public Image copy(final Image srcImg) {
        Image copyImg = new Image(srcImg.width(), srcImg.height(), srcImg.getImage().getType());
        Graphics2D g = copyImg.getImage().createGraphics();
        g.drawImage(srcImg.getImage(), null, 0, 0);
        g.dispose();
        return copyImg;
    }

    public static void saveToFile(final Image image, final String formatName, final File destination) {
        try {
            ImageIO.write(image.getImage(), formatName, destination);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Image compare(final Image image, final List<Rectangle> excludedRectangles) {
        Image imgResult = new Image().copy(image);
        List<Integer> differentPixels = this.getDifferentPixels(image, excludedRectangles);
        List<List<Integer>> diffRegions = RegionsSplitter.divideIntoRegions(differentPixels, width());
        List<Rectangle> diffBorders = RegionsSplitter.getRectangles(diffRegions, width(), height());
        for (Rectangle diffBorder : diffBorders) {
            drawRect(imgResult, diffBorder, Color.RED);
        }
        return imgResult;
    }

    public List<Integer> getDifferentPixels(final Image image, List<Rectangle> excludedRegions) {
        List<Integer> differentPixels = new ArrayList<>();
        Set<Integer> excludedPixels = excludedRegions == null ? null : getExcludedPixels(excludedRegions, width());
        int[] srcRGB = this.rgbArray();
        int[] extRGB = image.rgbArray();
        for (int i = 0; i < this.size(); i++) {
            if (excludedPixels != null && !excludedPixels.contains(i)) {
                Color clr = new Color(srcRGB[i] - extRGB[i]);
                int r = clr.getRed();
                int g = clr.getGreen();
                int b = clr.getBlue();
                double difference = (r * r + g * g + b * b) / sqrt(GRAYSCALE * GRAYSCALE * 3);
                if (difference - TOLERANCE > 1e-16) {
                    differentPixels.add(i);
                }
            }
        }
        return differentPixels;
    }

    public Set<Integer> getExcludedPixels(final List<Rectangle> excludedRegions, int imgWidth) {
        Set<Integer> excludedPixels = new HashSet<>();
        for (Rectangle rectangle : excludedRegions) {
            int left = rectangle.x;
            int top = rectangle.y;
            int right = left + rectangle.width;
            int bottom = top + rectangle.height;
            for (int i = left; i <= right; i++) {
                for (int j = top; j <= bottom; j++) {
                    excludedPixels.add(i + j * imgWidth);
                }
            }
        }
        return excludedPixels;
    }
}
