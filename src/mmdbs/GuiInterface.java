package mmdbs;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

/**
 * Created by Fuad on 6/29/2017.
 */
public class GuiInterface {

    public static void main (String[] args) {
        JFrame window = new JFrame("Gui Interface");
        window.setVisible(true);
        window.setSize(800,800);
        window.setResizable(false);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel panel=new JPanel();
        panel.setLayout(null);
        window.add(panel);
        JLabel path=new JLabel();
        path.setText("File name");
        //path.setBorder();
        path.setOpaque(true);
        path.setBounds(10,15,180,20);
        panel.add(path);





        JButton openbtn=new JButton("Choose Image");
        openbtn.setBounds(200,15,120,20);
        panel.add(openbtn);
        JLabel pict1=new JLabel();
        pict1.setBounds(10,200,280,280);
        panel.add(pict1);


        openbtn.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                JFileChooser openfile = new JFileChooser();

                openfile.setCurrentDirectory(new File(System.getProperty("user.home")));
                openfile.setFileSelectionMode(JFileChooser.FILES_ONLY);
                openfile.addChoosableFileFilter(new FileNameExtensionFilter("Images", "jpg", "png", "gif", "bmp"));
                openfile.setAcceptAllFileFilterUsed(false);
                int result = openfile.showOpenDialog(null);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File f=openfile.getSelectedFile() ;
                    String filename=f.getName();

                    path.setText(filename);
                    //ImageIcon image1=new ImageIcon(f.getPath());
                    ImageIcon image1 = new ImageIcon(new ImageIcon(f.getPath()).getImage().getScaledInstance(280, 280, Image.SCALE_DEFAULT));

                    pict1.setIcon(image1);

                }






            }
        });
        JTextField bins=new JTextField("Number of bins" );
        panel.add(bins);
        bins.setForeground(Color.gray);
        bins.setFont(new Font("Arial",Font.BOLD,12));
        bins.setBounds(10,70,100,22);
        bins.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                bins.setText("");
            }
        });
        JTextField cells=new JTextField("Number of cells" );
        panel.add(cells);
        cells.setForeground(Color.gray);
        cells.setFont(new Font("Arial",Font.BOLD,12));
        cells.setBounds(120,70,100,22);
        cells.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                cells.setText("");
            }
        });



        JRadioButton  euclidean=new JRadioButton("Euclidean");
        JRadioButton  quadratic=new JRadioButton("Quadratic Form");
        ButtonGroup bg = new ButtonGroup();
        bg.add(euclidean);
        bg.add(quadratic);
        panel.add(euclidean);
        panel.add(quadratic);
        euclidean.setBounds(370,15,100,20);
        quadratic.setBounds(520,15,150,20);
        euclidean.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            // radio button euclidean
            }
        });

        quadratic.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // radio button quadratic

            }
        });


        JRadioButton  colorhist=new JRadioButton("Color histogram");
        JRadioButton  glbledgehist=new JRadioButton("Global edge histogram");
        ButtonGroup cg = new ButtonGroup();
        cg.add(colorhist);
        cg.add(glbledgehist);
        panel.add(colorhist);
        panel.add(glbledgehist);
        colorhist.setBounds(370,70,150,20);
        glbledgehist.setBounds(520,70,180,20);
        colorhist.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // radio button euclidean
            }
        });

        glbledgehist.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // radio button quadratic

            }
        });
        JLabel pict2=new JLabel();
        pict2.setBounds(300,200,280,280);
        panel.add(pict2);

        JButton generatehst=new JButton("Generate Histogram");
        generatehst.setBounds(250,120,170,23);
        panel.add(generatehst);
        generatehst.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ImageIcon image1 = new ImageIcon(new ImageIcon("C:\\Users\\Fuad\\Desktop\\kot.png").getImage().getScaledInstance(280, 280, Image.SCALE_DEFAULT));

                pict2.setIcon(image1);
            }
        });

    }



}