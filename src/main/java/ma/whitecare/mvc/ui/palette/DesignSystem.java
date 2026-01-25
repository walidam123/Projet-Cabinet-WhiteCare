package ma.whitecare.mvc.ui.palette;

import javax.swing.*;
import java.awt.*;

public class DesignSystem {
    // Colors
    public static final Color PRIMARY = Color.decode("#A89F91"); // Taupe doux
    public static final Color SECONDARY = Color.decode("#EDE6DC"); // Beige clair
    public static final Color ACCENT = Color.decode("#B2B9A3"); // Vert sauge
    public static final Color TEXT_PRIMARY = Color.decode("#3C3C3C"); // Gris charbon
    public static final Color TEXT_SECONDARY = Color.decode("#7A7A7A"); // Gris moyen
    public static final Color BACKGROUND = Color.decode("#FDF8F3"); // Ivoire pâle
    public static final Color BACKGROUND_ALT = Color.decode("#F5EDE3"); // Beige très clair pour alternance

    // Button States
    public static final Color BTN_NORMAL = Color.decode("#B2B9A3"); // Vert sauge
    public static final Color BTN_HOVER = Color.decode("#8A9A7B"); // Vert olive foncé
    public static final Color BTN_ACTIVE = Color.decode("#7E6E5E"); // Brun doux
    public static final Color BTN_DISABLED = Color.decode("#EAEAEA"); // Gris très clair
    public static final Color BTN_LOGIN = Color.decode("#D32F2F"); // Red for login

    // Typography
    public static final Font TITLE = new Font("Lora", Font.BOLD, 26);
    public static final Font SUBTITLE = new Font("Lora", Font.PLAIN, 20);
    public static final Font BODY = new Font("Open Sans", Font.PLAIN, 16);
    public static final Font BODY_SMALL = new Font("Open Sans", Font.PLAIN, 14);
    public static final Font BUTTON_FONT = new Font("Lora", Font.BOLD, 15);

    // Helper to apply font safely
    public static Font getFont(String name, int style, int size) {
        Font f = new Font(name, style, size);
        if (f.getFamily().equalsIgnoreCase("Dialog") && !name.equalsIgnoreCase("Dialog")) {
            if (name.contains("Lora"))
                return new Font(Font.SERIF, style, size);
            if (name.contains("Open Sans"))
                return new Font(Font.SANS_SERIF, style, size);
        }
        return f;
    }

    public static void stylePanel(Container container) {
        container.setBackground(BACKGROUND);
    }

    public static void styleLabel(JLabel label, Font font) {
        label.setFont(font);
        label.setForeground(TEXT_PRIMARY);
    }

    public static void styleTable(JTable table) {
        table.setFont(BODY_SMALL);
        table.setRowHeight(30);
        table.getTableHeader().setFont(BODY);
        table.getTableHeader().setBackground(SECONDARY);
        table.getTableHeader().setForeground(TEXT_PRIMARY);
        table.setSelectionBackground(ACCENT);
        table.setSelectionForeground(Color.WHITE);
        table.setGridColor(SECONDARY);

        // Hide ID column if it's the first column and named appropriately
        if (table.getColumnCount() > 0) {
            String colName = table.getColumnName(0).toUpperCase();
            if (colName.equals("ID") || colName.equals("#") || colName.startsWith("N°") || colName.contains("ID")) {
                table.getColumnModel().getColumn(0).setMinWidth(0);
                table.getColumnModel().getColumn(0).setMaxWidth(0);
                table.getColumnModel().getColumn(0).setPreferredWidth(0);
                table.getTableHeader().getColumnModel().getColumn(0).setMinWidth(0);
                table.getTableHeader().getColumnModel().getColumn(0).setMaxWidth(0);
                table.getTableHeader().getColumnModel().getColumn(0).setPreferredWidth(0);
            }
        }
    }
}
