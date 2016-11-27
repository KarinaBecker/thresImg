/** class LocalThreshold
 * Implementation of Sauvola’s binarization method as mentioned in "Efficient Implementation of Local Adaptive Thresholding Techniques Using
 * Integral Images" by Shafaita, Keysersa & Breuel (2013)
 *
 * Local adaptive thresholding thres(x; y) after of the pixel intensities in
 * a window centered around the pixel (x; y) and defined by the radius r
 *
 * thres[x][y] = mean[x][y] * (1 + k * ((stddev[x][y] / R) - 1));    with R maximum value of standard deviation, in grayscale = 128
 */

import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.awt.image.Raster;

public class LocalThreshold {
    private BufferedImage thresImg;
    /**
     *  Constructor LocalThreshold
     *  Call calculation and implementation of local threshold
     */
    public LocalThreshold(BufferedImage img, int r, double k) {
        double[][] p = convertToPixelArray(img);

        applyLocThres(p, r, k);

        thresImg = convertToBufferedImg(p);
    }

    /**
     * Method convertToPixelArray
     * Method to convert buffered image to grayscale imge and then convert
     * to double array p
     * @param img BufferedImage input image
     * @return p two dimensional array with pixel values
     */
    private double[][] convertToPixelArray(BufferedImage img) {
        BufferedImage gray = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        ColorConvertOp op = new ColorConvertOp(img.getColorModel().getColorSpace(), gray.getColorModel().getColorSpace(), null);
        op.filter(img, gray);

        Raster imageRaster = gray.getData();
        double[][] p = new double[imageRaster.getWidth()][imageRaster.getHeight()];

        int[] buffer = new int[1];
        for (int x = 0; x < imageRaster.getWidth(); x++) {
            for (int y = 0; y < imageRaster.getHeight(); y++) {
                p[x][y] = imageRaster.getPixel(x, y, buffer)[0];
            }
        }
        return p;
    }

    /**
     * Method applyLocThres
     * Calculate local adaptive threshold with mean and standard deviation
     * Set pixel value to black if pixel value lower than threshold, set to white otherwise
     * @param p two dimensional array with pixel values
     * @param r radius
     * @param k variable
     * @return thres thresholded pixel values
     */
    private double[][] applyLocThres(double[][] p, int r, double k) {
        double[][] thres = new double[p.length][p[0].length];
        double[][] mean = getLocalMean(p, r);
        double[][] stdDev = getLocalStdDev(p, r);
        for (int x = 0; x < p.length; x++) {
            for (int y = 0; y < p[0].length; y++) {
                thres[x][y] = mean[x][y] * (1 + k * ((stdDev[x][y] / 128) - 1));
                if (p[x][y] <= thres[x][y]) {
                    p[x][y] = 0.0;
                } else {
                    p[x][y] = 255.0;
                }
            }
        }
        return thres;
    }

    /**
     * Method localMean
     * Calculate the local mean m(x; y) of the pixel intensities in a window
     * Centered around the pixel (x; y) and defined by the radius r
     *
     * @param p image in pixel
     * @param r radius r
     * @return m local mean
     */
    private static double[][] getLocalMean(double[][] p, int r) {
        double[][] mean = new double[p.length][p[0].length];
        for (int x = 0; x < p.length; x++) {
            for (int y = 0; y < p[x].length; y++) {
                double sum = 0.0;
                int count = 0;
                double squareR = Math.pow(r, 2.0);

                //borders of circle
                int left = x - r;
                if (left < 0) {
                    left = 0;
                }
                int top = y - r;
                if (top < 0) {
                    top = 0;
                }
                int right = left + 2 * r;
                if (right > p.length) {
                    right = p.length;
                }
                int bottom = top + 2 * r;
                if (bottom > p[x].length) {
                    bottom = p[x].length;
                }

                //add values of pixels in circle to sum
                for (int w = left; w < right; w++) {
                    for (int h = top; h < bottom; h++) {
                        double dist = Math.pow(x - w, 2.0) + Math.pow(y - h, 2.0);
                        if (dist <= squareR) {
                            int pixel = (int) p[w][h];
                            sum = sum + pixel;
                            count++;
                        }
                    }
                }
                //calculate mean of pixel intensity of circle centered around (x,y) with radius r
                mean[x][y] = sum / count;
            }
        }
        return mean;
    }

    /**
     * Method getLocalStdDev
     * Local standard deviation s(x; y) of the pixel intensities in a
     * window centred around the pixel (x; y) and defined by the radius r
     *
     * @param p double array which holds pixel for image
     * @param r radius r
     * @return stDev standard deviation
     */
    private static double[][] getLocalStdDev(double[][] p, int r) {
        double[][] mean = getLocalMean(p, r);
        double[][] stDev = new double[p.length][p[0].length];
        for (int x = 0; x < p.length; x++) {
            for (int y = 0; y < p[x].length; y++) {
                double sumDiff = 0.0;
                int count = 0;
                double squareR = Math.pow(r, 2.0);

                //borders of circle
                int left = x - r;
                if (left < 0) {
                    left = 0;
                }
                int top = y - r;
                if (top < 0) {
                    top = 0;
                }
                int right = left + 2 * r;
                if (right > p.length) {
                    right = p.length;
                }
                int bottom = top + 2 * r;
                if (bottom > p[x].length) {
                    bottom = p[x].length;
                }

                //build sum of differences from center of point in circle
                for (int w = left; w < right; w++) {
                    for (int h = top; h < bottom; h++) {
                        double dist = Math.pow(x - w, 2.0) + Math.pow(y - h, 2.0);
                        if (dist <= squareR) {
                            int pixel = (int) p[w][h];
                            double diff = pixel - mean[w][h];
                            sumDiff += Math.pow(diff, 2);
                            count++;
                        }
                    }
                }

                //calculate standard deviation
                double variance = sumDiff / count;
                stDev[x][y] = Math.sqrt(variance);
            }
        }
        return stDev;
    }

    /**
     * Method convertToBufferedImage
     * converts double pixel array to BufferedImage
     * @param p double array which holds pixel for image
     * @return thresImg thresholded image of type BufferedImage
     */
    private BufferedImage convertToBufferedImg(double[][]p){
        BufferedImage thresImg = new BufferedImage(p.length, p[0].length, BufferedImage.TYPE_BYTE_BINARY);
        for (int x = 0; x < p.length; x++) {
            for (int y = 0; y < p[0].length; y++) {
                int rgb = (int) p[x][y] << 16 | (int) p[x][y] << 8 | (int) p[x][y];
                thresImg.setRGB(x, y, rgb);
            }
        }
        return thresImg;
    }

    /**
     * Method getImage
     * @return thresImg
     */
    public BufferedImage getImage() {
        return thresImg;
    }
}
