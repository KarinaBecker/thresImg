// local standard deviation s(x; y) of the pixel intensities in a
// window centred around the pixel (x; y) and defined by the radius r

import java.lang.Math;

public class Imstd {

    public Imstd(double[][] P, int r) {
        imstd(P, r);
    }

    public static double[][] imstd(double[][] P, int r) {
        double[][] m = Immean.localMean(P, r);
        double[][] stDev = new double[P.length][P[0].length];
        for (int x = 0; x < P.length; x++) {
            for (int y = 0; y < P[x].length; y++) {
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
                if (right > P.length) {
                    right = P.length;
                }
                int bottom = top + 2 * r;
                if (bottom > P[x].length) {
                    bottom = P[x].length;
                }
                //build sum of differences from center of point in circle
                for (int w = left; w < right; w++) {
                    for (int h = top; h < bottom; h++) {
                        double dist = Math.pow(x - w, 2.0) + Math.pow(y - h, 2.0);
                        if (dist <= squareR) {
                            int pixel = (int) P[w][h];
                            double diff = pixel - m[w][h];
                            sumDiff += Math.pow(diff, 2);
                            count++;
                        }
                    }
                }
                //calculate standard deviation
                double variance = sumDiff / count;
                stDev[x][y] = Math.sqrt(variance);
                //System.out.println("Standard deviation at x,y (" + x + ", " + y + "): "+ stDev[x][y]);
            }
        }
        return stDev;
    }
}