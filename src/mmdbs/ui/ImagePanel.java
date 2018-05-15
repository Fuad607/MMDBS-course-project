package mmdbs.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class ImagePanel extends JPanel
{
    private static final BufferedImage defaultImage = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);

    private BufferedImage image;

    public void setImage(BufferedImage image)
    {
        this.image = image;
    }

    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        if (image == null)
        {
            image = defaultImage;
        }
        if (image != null)
        {
            double delta = Math.min(getSize().getWidth() / image.getWidth(), getSize().getHeight() / image.getHeight());
            int w = (int)(image.getWidth() * delta);
            int h = (int)(image.getHeight() * delta);
            g.drawImage(image, (int)(getSize().getWidth() - w) / 2, (int)(getSize().getHeight() - h) / 2, w, h, null);
        }
    }
}
