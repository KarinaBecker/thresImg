//local adaptive thresholding t(x; y) of the pixel intensities in
// a window centered around the pixel (x; y) and dened by the radius r

import java.awt.image.BufferedImage;
import java.awt.image.Raster;

public class LocalThreshold {
    private double[][] P;    // Image as pixel array
    private double[][] t;    // Threshold for each array
    private double[][] m;    // Mean
    private double[][] s;    // Standard deviation
    private int width;        // Image width
    private int height;        // Image height
    private BufferedImage thresImg;

    // Constructor to call calculation and implementation of local threshold
    public LocalThreshold(BufferedImage bufimg, int r, double k) {
        P = convertTo2D(bufimg);
        width = P.length;
        height = P[0].length;
        t = new double[width][height];
        this.t = imAdaptive(P, r, k);
        imThreshold(P, t);

        thresImg = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY);
        for (int w = 0; w < width; w++) {
            for (int h = 0; h < height; h++) {
                int rgb = (int) P[w][h] << 16 | (int) P[w][h] << 8 | (int) P[w][h];
                thresImg.setRGB(w, h, rgb);
            }
        }
    }

    // Get image
    public BufferedImage getImage() {
        return thresImg;
    }

    // Calculate threshold
    private double[][] imAdaptive(double[][] P, int r, double k) {
        int R = 128; //maximum value of standard deviation, in grayscale = 128
        m = Immean.localMean(P, r);
        s = Imstd.imstd(P, r);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                t[x][y] = m[x][y] * (1 + k * ((s[x][y] / R) - 1));
                //System.out.println("Local adaptive threshold at (" + x + ", " + y + "): " + t[x][y]);
            }
        }
        return t;
    }

    // Set threshold
    private void imThreshold(double[][] P, double[][] t) {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                // Set pixel = 0, black if lower than threshold
                if (P[x][y] <= t[x][y]) {
                    P[x][y] = 0.0;
                    // Set pixel = 255,white if otherwise
                } else {
                    P[x][y] = 255.0;
                }
            }
        }
    }

    // Method to turn bufferdImage to array
    private static double[][] convertTo2D(BufferedImage bufimg) {
        // setting up image array size
        Raster image_raster = bufimg.getData();
        int bufimgWidth = image_raster.getWidth();
        int bufimgHeight = image_raster.getHeight();
        double[][] P = new double[bufimgWidth][bufimgHeight];

        //get pixel by pixel
        int[] pixel = new int[1];
        int[] buffer = new int[1];

        //get the image in the array
        for (int w = 0; w < bufimgWidth; w++)
            for (int h = 0; h < bufimgHeight; h++) {
                pixel = image_raster.getPixel(w, h, buffer);
                P[w][h] = pixel[0];
            }
        return P;
    }
}
