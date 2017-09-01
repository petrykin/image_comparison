package com.gmail.spetrykin.view;

import com.gmail.spetrykin.controller.ImageCompareController;
import com.gmail.spetrykin.model.RegionsSplitter;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Formatter;

public class MainWnd extends JFrame implements ActionListener, ChangeListener {
    static final int SPACING_MIN = 0;
    static final int SPACING_MAX = 30;
    static final int SPACING_INIT = 15;

    private ImageCompareController controller;
    private File file1;
    private File file2;

    private JPanel mainPanel;
    private JPanel topPanel;
    private JButton leftChooseBtn;
    private JButton rightChooseBtn;
    private JButton compareButton;

    private JSplitPane bodyPanel;
    private JPanel leftBodyPanel;
    private JScrollPane leftScrollPane;

    private JPanel rightBodyPanel;
    private JScrollPane rightScrollPane;

    private JPanel bottomPanel;
    private JLabel infoLabel;

    public MainWnd() {
        super("Image comparison");
        setSize(Toolkit.getDefaultToolkit().getScreenSize());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        mainPanel = new JPanel(new BorderLayout());

        topPanel = new JPanel(new GridLayout(1, 3));

        leftChooseBtn = new JButton("Choose first image");
        leftChooseBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                file1 = chooseImage(leftBodyPanel, leftChooseBtn);
                compareButton.setEnabled(file1 != null && file2 != null ? true : false);
            }
        });

        rightChooseBtn = new JButton("Choose second image");
        rightChooseBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                file2 = chooseImage(rightBodyPanel, rightChooseBtn);
                compareButton.setEnabled(file1 != null && file2 != null ? true : false);
            }
        });

        compareButton = new JButton("Compare");
        compareButton.addActionListener(this);
        compareButton.setEnabled(false);

        topPanel.add(leftChooseBtn);
        topPanel.add(compareButton);
        topPanel.add(rightChooseBtn);

        bodyPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        bodyPanel.setDividerLocation(getSize().width / 2);

        leftBodyPanel = new JPanel(new BorderLayout());
        JLabel leftLabel = new JLabel("Choose first image, please");
        leftLabel.setHorizontalAlignment(SwingConstants.CENTER);
        leftBodyPanel.add(leftLabel, BorderLayout.CENTER);
        leftScrollPane = new JScrollPane(leftBodyPanel);
        bodyPanel.add(leftScrollPane);

        rightBodyPanel = new JPanel(new BorderLayout());
        JLabel rightLabel = new JLabel("Choose second image, please");
        rightLabel.setHorizontalAlignment(SwingConstants.CENTER);
        rightBodyPanel.add(rightLabel, BorderLayout.CENTER);
        rightScrollPane = new JScrollPane(rightBodyPanel);
        bodyPanel.add(rightScrollPane);

        bottomPanel = new JPanel();
        infoLabel = new JLabel(" ");

        JSlider minRegionSpacing = new JSlider(JSlider.HORIZONTAL,
                SPACING_MIN, SPACING_MAX, SPACING_INIT);
        minRegionSpacing.addChangeListener(this);
        minRegionSpacing.setMajorTickSpacing(10);
        minRegionSpacing.setMinorTickSpacing(2);
        minRegionSpacing.setPaintTicks(true);
        minRegionSpacing.setPaintLabels(true);
        minRegionSpacing.setFont(new Font("Serif", Font.BOLD, 10));

        bottomPanel.add(new JLabel("MIN REGION SPACING, px:"));
        bottomPanel.add(minRegionSpacing);
        bottomPanel.add(infoLabel);

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(bodyPanel, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        add(mainPanel);
        controller = new ImageCompareController();
        setVisible(true);
    }

    public File chooseImage(JPanel panel, JButton btn) {
        File file = null;
        JFileChooser openFile = new JFileChooser();
        int ret = openFile.showDialog(null, "Choose file");
        if (ret == JFileChooser.APPROVE_OPTION) {
            file = openFile.getSelectedFile();
            panel.removeAll();
            panel.add(new JLabel(createImageIcon(file)));
            mainPanel.repaint();
            mainPanel.revalidate();
            btn.setText(file.getPath());
        }
        return file;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        controller.compare(file1, file2, new ArrayList<>());
        File resultFile = new File(controller.getResultFileName());
        rightBodyPanel.removeAll();
        rightBodyPanel.add(new JLabel(createImageIcon(resultFile)));
        String infoText = new Formatter().format(
                "COMPARISON TIME: %.2f SEC",
                controller.getComparingTiming())
                .toString();
        rightChooseBtn.setText(resultFile.getPath());
        infoLabel.setText(infoText);
        mainPanel.repaint();
        mainPanel.revalidate();
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        JSlider source = (JSlider) e.getSource();
        if (!source.getValueIsAdjusting()) {
            RegionsSplitter.setMinRegionSpacing(source.getValue());
        }
    }

    public ImageIcon createImageIcon(File file) {
        try {
            return new ImageIcon(ImageIO.read(file).getScaledInstance(
                    -1,
                    (int) (0.95 * bodyPanel.getSize().height),
                    Image.SCALE_SMOOTH));
        } catch (IOException e) {
            System.err.println("Couldn't find file: " + e);
            return null;
        }
    }

    public static void main(String[] args) {
        new MainWnd();
    }
}
