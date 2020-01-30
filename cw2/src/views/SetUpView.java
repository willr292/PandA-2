package views;

import draughts.*;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

/**
 * A view to allow users to load or start a new game.
 */

public class SetUpView extends JPanel {

    private static final long serialVersionUID = -3633674241692834588L;

    /**
     * Constructs a new SetUpView.
     *
     * @param listener the ActionListener to recieve
     * events from the buttons in this view.
     */
    public SetUpView(ActionListener listener) {
        setLayout(new GridBagLayout());
        JPanel panel = new JPanel(new GridLayout(2, 1, 0, 10));
        panel.setOpaque(false);
        JButton newButton = getStyledButton("New Game");
        newButton.setActionCommand("new_game");
        newButton.addActionListener(listener);
        JButton loadButton = getStyledButton("Load Game");
        loadButton.setActionCommand("load_game");
        loadButton.addActionListener(listener);
        panel.add(newButton);
        panel.add(loadButton);
        add(panel);
    }

    /**
     * Draws the translucent white background.
     *
     * @param g0 the Graphics object to draw to.
     */
    public void paintComponent(Graphics g0) {
        Graphics2D g = (Graphics2D) g0;
        Dimension size = getSize();
        g.setColor(new Color(255, 255, 255, 200));
        g.fillRect(0, 0, size.width, size.height);
    }

    // Returns a styled JButton.
    // @param title the text to be displayed in the button.
    // @return a styled JButton.
    private JButton getStyledButton(String title) {
        JButton button = new JButton(title);
        button.setPreferredSize(new Dimension(150, 50));
        button.setContentAreaFilled(false);
        button.setOpaque(true);
        button.setBackground(new Color(0, 124, 0));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Helvetica Neue", Font.BOLD, 18));
        Border outerBorder = BorderFactory.createLineBorder(new Color(0, 0, 0, 0), 4);
        Border innerBorder = BorderFactory.createLineBorder(Color.WHITE, 1);
        button.setBorder(BorderFactory.createCompoundBorder(outerBorder, innerBorder));
        return button;
    }

}
