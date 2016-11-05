// Calculate local mean m(x; y) of the pixel intensities in a window
// Centered around the pixel (x; y) and defined by the radius r

import java.lang.Math;

public class Immean {

    // Constructor Immean, calls localMean calculation method
    public Immean(double[][] P, int r) {
        localMean(P, r);
    }

    // Calculate local mean
    public static double[][] localMean(double[][] P, int r) {
        double[][] m = new double[P.length][P[0].length]; //y can be initialized like this because image is rectangle
        for (int x = 0; x < P.length; x++) {
            for (int y = 0; y < P[x].length; y++) {
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
                if (right > P.length) {
                    right = P.length;
                }
                int bottom = top + 2 * r;
                if (bottom > P[x].length) {
                    bottom = P[x].length;
                }
                //add values of pixels in circle to sum
                for (int w = left; w < right; w++) {
                    for (int h = top; h < bottom; h++) {
                        double dist = Math.pow(x - w, 2.0) + Math.pow(y - h, 2.0);
                        if (dist <= squareR) {
                            int pixel = (int) P[w][h];
                            sum = sum + pixel;
                            count++;
                        }
                    }
                }
                // Calculate mean of pixel intensity of circle centered around (x,y) with radius r
                m[x][y] = sum / count;
                //System.out.println("Mean at x,y (" + x + ", " + y + "): "+ m[x][y]);
            }
        }
        return m;
    }
}