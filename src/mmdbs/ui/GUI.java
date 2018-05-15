package mmdbs.ui;

import mmdbs.CBIR;
import mmdbs.distance.Euclidean;
import mmdbs.distance.QuadraticForm;
import mmdbs.features.ColorHistogram;
import mmdbs.features.GlobalEdge;
import mmdbs.utils.FileUtils;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class GUI extends JFrame implements ActionListener
{
    private static final String QUERY_FILE_NO_SELECTED = "(Choose a query image.)";

    private JLabel queryFileLabel;
    private JButton queryFileButton;
    private ImagePanel queryImage;
    private JComboBox<String> featureBox;
    private JComboBox<String> distanceBox;
    private JTextField noBinsField;
    private JTextField noCellsField;
    private JTextField cutoffField;
    private JButton queryButton;
    public JScrollPane resultsPane;
    public JPanel resultsPanel;

    public GUI()
    {
        super("MMDBS CBIRsys");
        setSize(800, 600);
        //setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        /* main pane */
        JSplitPane mainPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        mainPane.setOneTouchExpandable(true);
        mainPane.setDividerLocation(0.4);

        /* left pane */
        JSplitPane leftPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        leftPane.setOneTouchExpandable(true);
        leftPane.setDividerLocation(0.5);

        /* query file panel */
        JPanel queryFilePanel = new JPanel(new BorderLayout());
        queryFilePanel.setBackground(Color.white);
        queryFilePanel.setBorder(new EmptyBorder(4, 4, 4, 4));
        queryFileLabel = new JLabel(QUERY_FILE_NO_SELECTED);
        queryFileLabel.setMinimumSize(new Dimension(0, 0));
        queryFilePanel.add(queryFileLabel, BorderLayout.CENTER);
        queryFileButton = new JButton("...");
        queryFileButton.addActionListener(this);
        queryFilePanel.add(queryFileButton, BorderLayout.LINE_END);

        /* query panel */
        JPanel queryPanel = new JPanel(new BorderLayout());
        queryPanel.add(queryFilePanel, BorderLayout.NORTH);
        queryImage = new ImagePanel();
        queryImage.setMinimumSize(new Dimension(100, 100));
        queryImage.setPreferredSize(new Dimension(100, 100));
        queryImage.setMaximumSize(new Dimension(100, 100));
        queryPanel.add(queryImage, BorderLayout.CENTER);
        leftPane.add(queryPanel);

        /* settings panel */
        JPanel settingsPanelWrapper = new JPanel(new BorderLayout());
        JPanel settingsPanel = new JPanel(new GridLayout(100, 1));
        JLabel featureLabel = new JLabel("Choose a feature extraction method:");
        settingsPanel.add(featureLabel);
        featureBox = new JComboBox<>(new String[] {"Color Histogram", "Global Edge"});
        settingsPanel.add(featureBox);
        JLabel distanceLabel = new JLabel("Choose a distance measurement:");
        settingsPanel.add(distanceLabel);
        distanceBox = new JComboBox<>(new String[] {"Euclidean", "Quadratic Form"});
        settingsPanel.add(distanceBox);
        JLabel noBinsLabel = new JLabel("Choose the number of bins:");
        settingsPanel.add(noBinsLabel);
        noBinsField = new JTextField("16");
        settingsPanel.add(noBinsField);
        JLabel noCellsLabel = new JLabel("Choose the number of cells:");
        settingsPanel.add(noCellsLabel);
        noCellsField = new JTextField("4");
        settingsPanel.add(noCellsField);
        JLabel cutoffLabel = new JLabel("Choose the cutoff for the QFD:");
        settingsPanel.add(cutoffLabel);
        cutoffField = new JTextField("16");
        settingsPanel.add(cutoffField);
        queryButton = new JButton("Query Database");
        queryButton.addActionListener(this);
        settingsPanel.add(queryButton);
        settingsPanelWrapper.add(settingsPanel, BorderLayout.NORTH);
        settingsPanelWrapper.setMinimumSize(new Dimension(100, 100));
        leftPane.add(settingsPanelWrapper);

        /* results panel */
        resultsPanel = new JPanel(new FlowLayout()
        {
            /* https://stackoverflow.com/questions/29605734/how-to-set-the-maximum-width-of-a-flow-layouted-jpanel */
            @Override
            public Dimension preferredLayoutSize(Container target)
            {
                Dimension d = super.preferredLayoutSize(target);
                int w = Math.min(resultsPane.getWidth() - 20, d.width);
                if (w > 0)
                {
                    int wa = w / d.height;
                    d.height *= Math.ceil(d.width / d.height) / wa + 1;
                }
                d.width = w;
                return d;
            }
        });
        resultsPanel.setMinimumSize(new Dimension(0, 0));
        resultsPane = new JScrollPane(resultsPanel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER)
        {
            @Override
            public void repaint()
            {
                super.repaint();
                resultsPanel.revalidate();
            }
        };
        resultsPane.getVerticalScrollBar().setUnitIncrement(16);

        mainPane.add(leftPane);
        mainPane.add(resultsPane);
        add(mainPane);

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        if (e.getSource().equals(queryFileButton))
        {
            JFileChooser fileChooser = new JFileChooser(".");
            if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
            {
                if (!CBIR.loadQuery(fileChooser.getSelectedFile()))
                {
                    queryFileLabel.setText(QUERY_FILE_NO_SELECTED);
                    JOptionPane.showMessageDialog(this, "Failed to load the image. Either the file does not exist or it is not a valid image.", "Error", JOptionPane.ERROR_MESSAGE);
                }
                else
                {
                    queryFileLabel.setText(fileChooser.getSelectedFile().getAbsolutePath());
                    queryImage.setImage(CBIR.getQuery());
                    queryImage.revalidate();
                    queryImage.repaint();
                }
            }
            else
            {
                queryFileLabel.setText(QUERY_FILE_NO_SELECTED);
                CBIR.loadQuery(null);
                queryImage.setImage(null);
            }
        }
        else if (e.getSource().equals(queryButton))
        {
            CBIR.clearMap();
            System.out.println("Started query");
            if (CBIR.getQuery() == null)
            {
                JOptionPane.showMessageDialog(this, "No query image is loaded.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String feature = (String) featureBox.getSelectedItem();
            String distance = (String) distanceBox.getSelectedItem();
            System.out.println("feature = " + feature);
            System.out.println("distance = " + distance);
            String binsString = noBinsField.getText();
            int bins;
            try
            {
                bins = Integer.parseInt(binsString);
            }
            catch (NumberFormatException ex)
            {
                JOptionPane.showMessageDialog(this, "The value of the number of bins field must be an integer.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            System.out.println("noBins = " + bins);
            String cellsString = noCellsField.getText();
            int cells;
            try
            {
                cells = Integer.parseInt(cellsString);
            }
            catch (NumberFormatException ex)
            {
                JOptionPane.showMessageDialog(this, "The value of the number of cells field must be an integer.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            System.out.println("noCells = " + cells);
            String cutoffString = cutoffField.getText();
            int cutoff;
            try
            {
                cutoff = Integer.parseInt(cutoffString);
            }
            catch (NumberFormatException ex)
            {
                JOptionPane.showMessageDialog(this, "The value of the cutoff field must be an integer.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            System.out.println("cutoff = " + cutoff);

            query(feature, distance, bins, cells, cutoff);
        }
    }

    public void query(String feature, String distance, int bins, int cells, int cutoff)
    {
        double[][] queryHistogram = new double[3][];
        switch (feature)
        {
            case "Color Histogram":
                queryHistogram = ColorHistogram.getColorHistogram(CBIR.getQuery(), bins, cells);
                break;
            case "Global Edge":
                queryHistogram = GlobalEdge.getGlobalEdgeColorHistogram(CBIR.getQuery(), bins, cells);
                break;
        }

        if (distance.equals("Quadratic Form"))
        {
            QuadraticForm[] quadraticForms = new QuadraticForm[3];
            for (int i = 0; i < quadraticForms.length; i++)
            {
                quadraticForms[i] = new QuadraticForm();
                quadraticForms[i].loadQuery(queryHistogram[i], bins, cells);
            }

            for (File file : FileUtils.getAllFiles())
            {
                BufferedImage test = null;
                try
                {
                    test = ImageIO.read(file);
                }
                catch (IOException e1)
                {
                    e1.printStackTrace();
                }
                double[][] testHistogram = new double[3][];
                switch (feature)
                {
                    case "Color Histogram":
                        testHistogram = ColorHistogram.getColorHistogram(test, bins, cells);
                        break;
                    case "Global Edge":
                        testHistogram = GlobalEdge.getGlobalEdgeColorHistogram(test, bins, cells);
                        break;
                }
                double redDistance = quadraticForms[0].getDistanceToQuery(testHistogram[0], cutoff, bins, cells);
                double greenDistance = quadraticForms[1].getDistanceToQuery(testHistogram[1], cutoff, bins, cells);
                double blueDistance = quadraticForms[2].getDistanceToQuery(testHistogram[2], cutoff, bins, cells);
                double d = (redDistance + greenDistance + blueDistance) / 3;
                CBIR.addResult(file.getParent() + "\\" + file.getName(), test, d);
            }
        }
        else if (distance.equals("Euclidean"))
        {
            for (File file : FileUtils.getAllFiles())
            {
                BufferedImage test = null;
                try
                {
                    test = ImageIO.read(file);
                }
                catch (IOException e1)
                {
                    e1.printStackTrace();
                }
                double[][] testHistogram = new double[3][];
                switch (feature)
                {
                    case "Color Histogram":
                        testHistogram = ColorHistogram.getColorHistogram(test, bins, cells);
                        break;
                    case "Global Edge":
                        testHistogram = GlobalEdge.getGlobalEdgeColorHistogram(test, bins, cells);
                        break;
                }
                double redDistance = Euclidean.calculateEuclideanDistance(queryHistogram[0], testHistogram[0], cells);
                double greenDistance = Euclidean.calculateEuclideanDistance(queryHistogram[1], testHistogram[1], cells);
                double blueDistance = Euclidean.calculateEuclideanDistance(queryHistogram[2], testHistogram[2], cells);
                double d = (redDistance + greenDistance + blueDistance) / 3;
                CBIR.addResult(file.getParent() + "\\" + file.getName(), test, d);
            }
        }
        CBIR.redrawResults();
    }
}
