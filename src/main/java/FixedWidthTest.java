import javax.swing.*;
import java.awt.*;

public class FixedWidthTest {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Fixed Width Test");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(2, 6, 2, 6);
            gbc.gridy = 0;
            gbc.fill = GridBagConstraints.NONE;
            gbc.weightx = 0;
            gbc.anchor = GridBagConstraints.WEST;

            // --- First dynamic value ---
            JLabel label1 = new JLabel("123");
            JPanel wrapper1 = createFixedWidthWrapper(label1, 60);
            gbc.gridx = 0;
            frame.add(new JLabel("Value 1:"), gbc);
            gbc.gridx = 1;
            frame.add(wrapper1, gbc);

            // --- Second dynamic value ---
            JLabel label2 = new JLabel("456");
            JPanel wrapper2 = createFixedWidthWrapper(label2, 60);
            gbc.gridx = 2;
            frame.add(new JLabel("Value 2:"), gbc);
            gbc.gridx = 3;
            frame.add(wrapper2, gbc);

            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

            // --- Dynamically update labels ---
            new Timer(1000, e -> {
                label1.setText(String.valueOf((int)(Math.random() * 10000)));
                label2.setText(String.valueOf((int)(Math.random() * 10000)));
            }).start();
        });
    }

    private static JPanel createFixedWidthWrapper(JComponent comp, int width) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(comp, BorderLayout.CENTER);
        Dimension fixed = new Dimension(width, comp.getPreferredSize().height);
        panel.setPreferredSize(fixed);
        panel.setMinimumSize(fixed);
        panel.setMaximumSize(fixed);
        return panel;
    }
}
