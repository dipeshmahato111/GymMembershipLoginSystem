package view;

import javax.swing.JPanel;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/**
 * A simple logo drawn with {@link Graphics2D} (a gold badge containing a
 * dumbbell silhouette) instead of an external image asset, so the project
 * doesn't depend on shipping/loading an image file. Used on the Login
 * screen for brand identity.
 */
public class GymLogoPanel extends JPanel {

    private final int size;

    public GymLogoPanel(int size) {
        this.size = size;
        setOpaque(false);
        setPreferredSize(new Dimension(size, size));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int d = size;

        // Badge circle
        g2.setColor(UiTheme.ACCENT_GOLD);
        g2.fillOval(0, 0, d, d);
        g2.setColor(UiTheme.PURPLE_DARK);
        g2.setStroke(new BasicStroke(Math.max(2f, d * 0.035f)));
        g2.drawOval(2, 2, d - 4, d - 4);

        // Dumbbell bar
        int cy = d / 2;
        int barThickness = Math.max(3, d / 16);
        int barInset = (int) (d * 0.30);
        g2.setColor(UiTheme.PURPLE_DARK);
        g2.fillRoundRect(barInset, cy - barThickness / 2, d - 2 * barInset, barThickness, barThickness, barThickness);

        // Dumbbell weight plates (outer + inner on each side)
        int plateW = Math.max(6, (int) (d * 0.10));
        int outerH = (int) (d * 0.46);
        int innerH = (int) (d * 0.30);
        int outerX1 = (int) (d * 0.16);
        int innerX1 = outerX1 + plateW + 2;
        int outerX2 = (int) (d * 0.84) - plateW;
        int innerX2 = outerX2 - plateW - 2;

        g2.fillRoundRect(outerX1, cy - outerH / 2, plateW, outerH, 4, 4);
        g2.fillRoundRect(innerX1, cy - innerH / 2, plateW, innerH, 4, 4);
        g2.fillRoundRect(innerX2, cy - innerH / 2, plateW, innerH, 4, 4);
        g2.fillRoundRect(outerX2, cy - outerH / 2, plateW, outerH, 4, 4);

        g2.dispose();
    }
}
