package ma.whitecare.mvc.ui.palette;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class RoundedButton extends JButton {
    private int radius = 15;

    public RoundedButton(String text) {
        super(text);
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setFont(DesignSystem.BUTTON_FONT);
        setForeground(Color.WHITE);
        setBackground(DesignSystem.BTN_NORMAL);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Color bg = getBackground();
        if (!isEnabled()) {
            bg = DesignSystem.BTN_DISABLED;
        } else if (getModel().isPressed()) {
            // If it's the login button, we might want a different active color,
            // but for now let's use BTN_ACTIVE or a darker version of current BG
            bg = bg.equals(DesignSystem.BTN_LOGIN) ? bg.darker() : DesignSystem.BTN_ACTIVE;
        } else if (getModel().isRollover()) {
            bg = bg.equals(DesignSystem.BTN_LOGIN) ? bg.brighter() : DesignSystem.BTN_HOVER;
        }

        g2.setColor(bg);
        g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), radius, radius));

        FontMetrics fm = g2.getFontMetrics();
        int x = (getWidth() - fm.stringWidth(getText())) / 2;
        int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();

        g2.setColor(getForeground());
        g2.drawString(getText(), x, y);
        g2.dispose();
    }
}
