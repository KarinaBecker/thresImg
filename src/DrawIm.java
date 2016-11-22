/**
 * Class DrawIm. Display an image on the panel.
 *
 * @// TODO: 22/11/2016
 * upload picture from files - DONE
 * limit file selection to image formats - DONE
 * Limit file saving to images
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
    private JPanel imagePanel;
    private JPanel controlPanel;
    private JTextField varRText;
    private JTextField varKText;
    private BufferedImage originalImg;
    private BufferedImage thresImg;
    private int countSaveImg = 0;
    private boolean initialLoad = true;

    /**
     * main class creates JFrame and ads DrawIm
     *
     * @param args
     */
    public static void main(String[] args) {
        JFrame frame = new JFrame("Local Adaptive Threshold Imaging");

        frame.add(new DrawIm());
        frame.setLayout(new GridLayout(2, 1, 5, 5));
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
        //new JPanel container for images and buttons
        imagePanel = new JPanel();
        controlPanel = new JPanel();

        //add buttons
        addButtons();

        //add images
        String filePath = "files/boats.png";
        addImages(filePath);

        //set initialLoad flag to false
        initialLoad = false;
    }

    private void addButtons() {
        //add Buttons to DrawImage
        JLabel varRLabel = new JLabel("Radius r:");
        JLabel varKLabel = new JLabel("Variable k (0.2 - 0.5):");
        varRText = new JTextField("1", 4);
        varKText = new JTextField("0.2", 4);
        JButton selectFile = new JButton("Select Image");
        JButton calculate = new JButton("Calculate Threshold Image");
        JButton save = new JButton("Save Threshold Image");

        //add variable inputs to DrawImage
        controlPanel.add(varRLabel);
        controlPanel.add(varRText);
        controlPanel.add(varKLabel);
        controlPanel.add(varKText);
        ControlPanel control = new ControlPanel();
        selectFile.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (control != null) {
                    control.selectFile(e);
                }
            }
        });
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
        controlPanel.add(selectFile);
        controlPanel.add(calculate);
        controlPanel.add(save);

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
        public void selectFile(ActionEvent e) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Open Image");
            fileChooser.setCurrentDirectory(new java.io.File("."));
            fileChooser.setAcceptAllFileFilterUsed(false);
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Image Files", "jpg", "png", "gif", "jpeg");
            fileChooser.setFileFilter(filter);

            int result = fileChooser.showOpenDialog(imagePanel);
            if (result == JFileChooser.APPROVE_OPTION) {
                String filePath = fileChooser.getSelectedFile().getAbsolutePath();
                System.out.println("Selected image: " + filePath);
                addImages(filePath);
            } else {
                System.out.println("No image selection.");
            }
        }

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

        public void saveThresholdImage(ActionEvent e) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Save Threshold Image");
            fileChooser.setCurrentDirectory(new java.io.File("."));
            int result = fileChooser.showSaveDialog(imagePanel);
            if (result == JFileChooser.APPROVE_OPTION) {
                String filePath = fileChooser.getSelectedFile().getAbsolutePath();
                System.out.println("Saving image as: " + filePath + ".png.");
                try {
                    ImageIO.write(thresImg, "png", new File(filePath + ".png"));
                } catch (IOException e2) {
                    System.err.println("Problem saving image: " + filePath + ".png.");
                    e2.printStackTrace();
                }
            } else {
                System.out.println("No image selection.");
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
            System.out.println("Accessing image: " + filePath + ".");

        } catch (IOException e) {
            System.err.println("Problem accessing image: " + filePath + ".");
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