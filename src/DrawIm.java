/**
 * Class DrawIm. Display an image on the panel.
 *
 * @// TODO: 22/11/2016
 * upload picture from files - DONE
 * limit file selection to image formats - DONE
 * Limit file saving to png - DONE
 * Problem with initial image
 * limit r,k input possibilities, only integers/doubles
 * Swing layout
 * set JFrame size
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

        //frame.setLayout(new GridLayout(3,1));
        frame.add(new DrawIm());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    /**
     * Constructor DrawIm
     * display images
     */
    private DrawIm() {
        //initialize main components
        controlPanel = new JPanel();
        controlPanel.setLayout(new GridLayout(2, 4, 3, 3));
        imagePanel = new JPanel();
        imagePanel.setLayout(new GridLayout(1, 2, 3, 3));

        //add buttons
        addButtons();

        //add images
        String filePath = "files/fish.jpg";
        addImages(filePath);

        //set initialLoad flag to false
        initialLoad = false;
    }

    private void addButtons() {
        JLabel varRLabel = new JLabel("Radius r", JLabel.CENTER);
        JLabel varKLabel = new JLabel("Variable k (0.2 - 0.5)", JLabel.CENTER);
        varRText = new JTextField("5", 4);
        varKText = new JTextField("0.2", 4);
        JButton calculateImg = new JButton("Calculate Threshold Image");
        JButton openImg = new JButton("Open Image");
        JButton saveImg = new JButton("Save Threshold Image");
        statusLabel = new JLabel("OK", JLabel.CENTER);

        ControlPanel control = new ControlPanel();
        calculateImg.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        if (control != null) {
                            control.calculateThresholdImage(e);
                        }
                    }
                }
        );
        openImg.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        if (control != null) {
                            control.selectFile(e);
                        }
                    }
                }
        );
        saveImg.addActionListener(
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
        controlPanel.add(calculateImg);
        controlPanel.add(openImg);
        controlPanel.add(saveImg);
        controlPanel.add(statusLabel);

        add(controlPanel);
    }

    /**
     * inner class Action
     * Action after button "Select file..." is clicked
     * Action after button "calculate" is clicked
     * threshold calculation with inputs of r and k, saving image as jpg
     * Action after button "Save" is clicked
     */
    private class ControlPanel {
        public void calculateThresholdImage(ActionEvent e) {
            //get variables from input in JPanel pControls for calculation
            int r = Integer.parseInt(varRText.getText().trim());
            double k = Double.parseDouble(varKText.getText().trim());

            //apply threshold, update JLabel pImg with threshold image
            LocalThreshold locThres = new LocalThreshold(originalImg, r, k);
            thresImg = locThres.getImage();
            imagePanel.remove(thresImgLabel);
            thresImgLabel.setIcon(new ImageIcon(thresImg));
            imagePanel.add(thresImgLabel);
            imagePanel.revalidate();
            imagePanel.repaint();
        }

        public void selectFile(ActionEvent e) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Open Image");
            fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
            fileChooser.setAcceptAllFileFilterUsed(false);
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Image Files", "jpg", "png", "gif", "jpeg");
            fileChooser.setFileFilter(filter);

            if (fileChooser.showOpenDialog(imagePanel) == JFileChooser.APPROVE_OPTION) {
                String filePath = fileChooser.getSelectedFile().getAbsolutePath();
                addImages(filePath);
            } else {
                statusLabel.setText("No image selection");
            }
        }

        public void saveThresholdImage(ActionEvent e) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Save Threshold Image");
            fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
            FileNameExtensionFilter filter = new FileNameExtensionFilter("png files", "png");
            fileChooser.setFileFilter(filter);
            fileChooser.setAcceptAllFileFilterUsed(false);

            if (fileChooser.showSaveDialog(imagePanel) == JFileChooser.APPROVE_OPTION) {
                String fileName = fileChooser.getSelectedFile().toString();
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
            } else {
                statusLabel.setText("No image selection");
            }
        }
    }

    /**
     * method addImages
     *
     * @param filePath Image file path
     */
    private void addImages(String filePath) {
        try {
            originalImg = ImageIO.read(new File(filePath));
            thresImg = originalImg;
            statusLabel.setText("Opened image " + filePath);

        } catch (IOException e) {
            statusLabel.setText("Problem accessing image " + filePath);
            System.err.println("Problem accessing image " + filePath);
            e.printStackTrace();
        }

        if (initialLoad) {
            originalImgLabel = new JLabel(new ImageIcon(originalImg));
            thresImgLabel = new JLabel(new ImageIcon(thresImg));
        } else {
            imagePanel.remove(originalImgLabel);
            imagePanel.remove(thresImgLabel);
            originalImgLabel.setIcon(new ImageIcon(originalImg));
            thresImgLabel.setIcon(new ImageIcon(thresImg));
        }
        imagePanel.add(originalImgLabel);
        imagePanel.add(thresImgLabel);
        add(imagePanel);
        imagePanel.revalidate();
        imagePanel.repaint();
    }
}