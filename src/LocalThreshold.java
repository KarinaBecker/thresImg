//local adaptive thresholding t(x; y) of the pixel intensities in
// a window centered around the pixel (x; y) and dened by the radius r

import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.awt.image.Raster;

public class LocalThreshold {
    private double[][] t;    // Threshold for each array
    private double[][] m;    // Mean
    private double[][] s;    // Standard deviation
    private int width;       // Image width
    private int height;      // Image height
    private BufferedImage thresImg;

    // Constructor to call calculation and implementation of local threshold
    public LocalThreshold(BufferedImage img, int r, double k) {
        BufferedImage gray = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        ColorConvertOp op = new ColorConvertOp(img.getColorModel().getColorSpace(), gray.getColorModel().getColorSpace(), null);
        op.filter(img, gray);
        double[][] p = convertTo2D(gray);
        width = p.length;
        height = p[0].length;
        t = new double[width][height];
        this.t = imAdaptive(p, r, k);
        imThreshold(p, t);

        thresImg = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY);
        for (int w = 0; w < width; w++) {
            for (int h = 0; h < height; h++) {
                int rgb = (int) p[w][h] << 16 | (int) p[w][h] << 8 | (int) p[w][h];
                thresImg.setRGB(w, h, rgb);
            }
        }
    }

    // Get image
    public BufferedImage getImage() {
        return thresImg;
    }

    // Calculate threshold
    private double[][] imAdaptive(double[][] p, int r, double k) {
        int R = 128; //maximum value of standard deviation, in grayscale = 128
        m = Immean.localMean(p, r);
        s = Imstd.imstd(p, r);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                t[x][y] = m[x][y] * (1 + k * ((s[x][y] / R) - 1));
                //System.out.println("Local adaptive threshold at (" + x + ", " + y + "): " + t[x][y]);
            }
        }
        return t;
    }

    // Set threshold
    private void imThreshold(double[][] p, double[][] t) {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                // Set pixel = 0, black if lower than threshold
                if (p[x][y] <= t[x][y]) {
                    p[x][y] = 0.0;
                    // Set pixel = 255,white if otherwise
                } else {
                    p[x][y] = 255.0;
                }
            }
        }
    }

    // Method to turn bufferdImage to array
    private static double[][] convertTo2D(BufferedImage img) {
        // setting up image array size
        Raster image_raster = img.getData();
        int imgWidth = image_raster.getWidth();
        int imgHeight = image_raster.getHeight();
        double[][] p = new double[imgWidth][imgHeight];

        //get pixel by pixel
        int[] pixel = new int[1];
        int[] buffer = new int[1];

        //get the image in the array
        for (int w = 0; w < imgWidth; w++)
            for (int h = 0; h < imgHeight; h++) {
                pixel = image_raster.getPixel(w, h, buffer);
                p[w][h] = pixel[0];
            }
        return p;
    }
}
