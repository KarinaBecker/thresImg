/**
 * Class DrawIm. Display an image on the panel.
 * TO DO
 * upload picture from data
 * limit input possibilities, only integers/doubles
 * set JFrame size
 * selectable name for saving
 **/

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class DrawIm extends JPanel {
    private static final long serialVersionUID = 1L;
    private JLabel thres;
    private JPanel p;
    private JTextField varR;
    private JTextField varK;
    private BufferedImage gray;
    private BufferedImage thresImg;
    private int countSaveImg = 0;

    /**
     * Constructor DrawIm
     * display images
     * */
    private DrawIm() {
        try {
            // read an image from the disk
            String file = "files/boats.png"; //CHANGE IMAGE
            BufferedImage image = ImageIO.read(new File(file));
            int width = image.getWidth();
            int height = image.getHeight();

            // create a gray scale image the same size from original colored image
            gray = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
            ColorConvertOp op = new ColorConvertOp(image.getColorModel().getColorSpace(), gray.getColorModel().getColorSpace(), null);
            op.filter(image, gray);

            // new JPanel container for pictures and buttons
            p = new JPanel();
            p.setLayout(new BoxLayout(p, BoxLayout.PAGE_AXIS));
            JPanel p2 = new JPanel();
            p2.setLayout(new BoxLayout(p2, BoxLayout.PAGE_AXIS));

            // convert BufferedImage and threshold image to ImageIcons
            JLabel img = new JLabel(new ImageIcon(image));
            thres = new JLabel(new ImageIcon(gray));

            // add ImageIcons to DrawImage
            p.add(img);
            p.add(thres);
            add(p);

            //add Buttons to DrawImage            
            JLabel varRLabel = new JLabel("Radius r:");
            JLabel varKLabel = new JLabel("Variable k, values 0.2 to 0.5:");
            varR = new JTextField("1", 5);
            varK = new JTextField("0.2", 5);
            JButton calculate = new JButton("Calculate Threshold Picture");
            JButton save = new JButton("Save Threshold Picture");

            //add variable inputs to DrawImage
            p2.add(varRLabel);
            p2.add(varR);
            p2.add(varKLabel);
            p2.add(varK);
            ControlPanel control = new ControlPanel();
            calculate.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (control != null) {
                        control.calculateThresholdImage(e);
                    }
                }
            });
            save.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (control != null) {
                        control.saveThresholdImage(e);
                    }
                }
            });
            p2.add(calculate);
            p2.add(save);
            add(p2);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *  inner class Action
     *  Action after button "calculate" is clicked
     *  threshold calculation with inputs of r and k, saving image as jpg
     */
    class ControlPanel {

        public void calculateThresholdImage(ActionEvent e) {
            //get variables from input in JPanel p for calculation
            int r = Integer.parseInt(varR.getText().trim());
            double k = Double.parseDouble(varK.getText().trim());

            //apply threshold; update picture panel p with threshold image
            LocalThreshold locThres = new LocalThreshold(gray, r, k);
            thresImg = locThres.getImage();
            p.remove(thres);
            thres = new JLabel(new ImageIcon(thresImg));
            p.add(thres);
            p.revalidate();
            p.repaint();
        }

        public void saveThresholdImage(ActionEvent e){
            try {
                ImageIO.write(thresImg, "jpg", new File("bwImage_" + countSaveImg + ".jpg"));
                countSaveImg++;
            } catch (IOException e2) {
                System.out.println("ERROR. Picture could not be saved");
                e2.printStackTrace();
            }
        }
    }

    /**
     * main class creates JFrame and ads DrawIm
     * @param args
     */
    public static void main(String[] args) {
        JFrame frame = new JFrame("Local Adaptive Threshold Image");

        frame.add(new DrawIm());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
    }
}