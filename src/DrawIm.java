/**
 * Class DrawIm
 * Display an image and the thresholded version on the panel.
 *
 * @// TODO: 22/11/2016
 * upload picture from files - DONE
 * limit file selection to image formats - DONE
 * Limit file saving to png - DONE
 * limit r,k input possibilities, only integers/doubles
 * Swing layout - DONE
 * set JFrame size, replace initial image
 * selectable name for saving - DONE
 * Look into action events
 * restructure, less global variables
 **/

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

public class DrawIm extends JPanel {
    private static final long serialVersionUID = 1L;
    private JLabel originalImgLabel;
    private JLabel thresImgLabel;
    private JLabel statusLabel;
    private JPanel imagePanel;
    private JPanel controlPanel;
    private JTextField varRText;
    private JTextField varKText;
    private BufferedImage originalImg;
    private BufferedImage thresImg;
    private boolean initialLoad = true;

    /**
     * main class creates JFrame and ads DrawIm
     *
     * @param args
     */
    public static void main(String[] args) {
        JFrame frame = new JFrame("Local Adaptive Threshold Imaging");

        frame.setLayout(new GridLayout(1, 2, 3, 3));
        frame.add(new DrawIm());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    /**
     * Constructor DrawIm
     * Display image and threshold image as well as controls.
     */
    private DrawIm() {
        //initialize main components
        controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 3, 3));
        imagePanel = new JPanel();
        imagePanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 3, 3));

        //add buttons
        addControls();

        //add images
        String filePath = "files/fish.jpg";
        addImages(filePath);

        //set initialLoad flag to false
        initialLoad = false;
    }

    /**
     * Method addControls
     * Adds input fields and buttons to control image thresholding.
     */
    private void addControls() {
        JLabel varRLabel = new JLabel("Radius r");
        JLabel varKLabel = new JLabel("Variable k (0.2 - 0.5)");
        varRText = new JTextField("5", 4);
        varKText = new JTextField("0.2", 4);
        JButton calculateImgBtn = new JButton("Calculate Threshold Image");
        JButton openImgBtn = new JButton("Open Image");
        JButton saveImgBtn = new JButton("Save Threshold Image");
        statusLabel = new JLabel("OK");

        ControlPanel control = new ControlPanel();
        calculateImgBtn.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        if (control != null) {
                            control.calculateThresholdImage(e);
                        }
                    }
                }
        );
        openImgBtn.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        if (control != null) {
                            control.openImage(e);
                        }
                    }
                }
        );
        saveImgBtn.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        if (control != null) {
                            control.saveThresholdImage(e);
                        }
                    }
                }
        );

        statusLabel.setBackground(Color.CYAN);
        statusLabel.setOpaque(true);

        controlPanel.add(varRLabel);
        controlPanel.add(varRText);
        controlPanel.add(varKLabel);
        controlPanel.add(varKText);
        controlPanel.add(calculateImgBtn);
        controlPanel.add(openImgBtn);
        controlPanel.add(saveImgBtn);
        controlPanel.add(statusLabel);

        add(controlPanel);
    }

    /**
     * Inner class ControlPanel
     */
    private class ControlPanel {

        /**
         * Method calculateThresholdImage
         * Get variables r and k from input in JPanel controlsPanel for threshold
         * calculation, update JLabel thresImgLabel with threshold image.
         *
         * @param e
         */
        public void calculateThresholdImage(ActionEvent e) {
            int r = Integer.parseInt(varRText.getText().trim());
            double k = Double.parseDouble(varKText.getText().trim());

            LocalThreshold locThres = new LocalThreshold(originalImg, r, k);
            thresImg = locThres.getImage();
            imagePanel.remove(thresImgLabel);
            thresImgLabel.setIcon(new ImageIcon(thresImg.getScaledInstance(500, -1, Image.SCALE_SMOOTH)));
            imagePanel.add(thresImgLabel);
            imagePanel.revalidate();
            imagePanel.repaint();
        }

        /**
         * Method openImage
         * Initialise FileChooser open dialog, call addImages method if open option
         * is approved.
         *
         * @param e
         */
        public void openImage(ActionEvent e) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Open Image");
            fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
            fileChooser.setAcceptAllFileFilterUsed(false);
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Image Files", "jpg", "png", "gif", "jpeg");
            fileChooser.setFileFilter(filter);

            if (fileChooser.showOpenDialog(imagePanel) == JFileChooser.APPROVE_OPTION) {
                addImages(fileChooser.getSelectedFile().getAbsolutePath());
            } else {
                statusLabel.setText("No image selection");
            }
        }

        /**
         * Method saveThresholdImage
         * Initialise FileChooser save dialog, call saveImage method if save option
         * is approved.
         *
         * @param e
         */
        public void saveThresholdImage(ActionEvent e) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Save Threshold Image");
            fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
            FileNameExtensionFilter filter = new FileNameExtensionFilter("png files", "png");
            fileChooser.setFileFilter(filter);
            fileChooser.setAcceptAllFileFilterUsed(false);

            if (fileChooser.showSaveDialog(imagePanel) == JFileChooser.APPROVE_OPTION) {
                saveImage(fileChooser.getSelectedFile().getAbsolutePath());
            } else {
                statusLabel.setText("No image selection");
            }
        }
    }

    /**
     * Method addImages
     * Open an image from file path and display the image twice.
     *
     * @param filePath file path of image to be displayed
     */
    private void addImages(String filePath) {
        try {
            originalImg = ImageIO.read(new File(filePath));
            statusLabel.setText("Opened image " + filePath);
        } catch (IOException e) {
            statusLabel.setText("Problem accessing image " + filePath);
            System.err.println("Problem accessing image " + filePath);
            e.printStackTrace();
        }

        ImageIcon originalImgIcon = new ImageIcon(originalImg.getScaledInstance(500, -1, Image.SCALE_SMOOTH));

        if (initialLoad) {
            originalImgLabel = new JLabel(originalImgIcon);
            thresImgLabel = new JLabel(originalImgIcon);
        } else {
            imagePanel.remove(originalImgLabel);
            imagePanel.remove(thresImgLabel);
            originalImgLabel.setIcon(originalImgIcon);
            thresImgLabel.setIcon(originalImgIcon);
        }

        imagePanel.add(originalImgLabel);
        imagePanel.add(thresImgLabel);
        add(imagePanel);
        imagePanel.revalidate();
        imagePanel.repaint();
    }

    /**
     * Method saveImage
     * Add png to filename if not already there, save an image as png.
     *
     * @param fileName file name of image to be saved
     */
    private void saveImage(String fileName) {
        if (!fileName.endsWith(".png"))
            fileName += ".png";

        try {
            ImageIO.write(thresImg, "png", new File(fileName));
            statusLabel.setText("Image saved as " + fileName);
        } catch (IOException e2) {
            statusLabel.setText("Problem saving image " + fileName);
            System.err.println("Problem saving image " + fileName);
            e2.printStackTrace();
        }
    }
}