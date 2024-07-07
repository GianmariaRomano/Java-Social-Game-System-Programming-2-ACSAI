package javasgsproject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.Set;

// Panel that displays the simulation
public class SimulationPanel extends JPanel {
    private ArrayList<Player> players;
    private Player selectedPlayer;

    // Constructor
    public SimulationPanel(ArrayList<Player> players) {
        this.players = players;
        setupMouseMotionListener();
    }

    // Update the players in the simulation panel
    public void updatePlayers(ArrayList<Player> updatedPlayers) {
        this.players = updatedPlayers;
    }

    // Set up the mouse motion listener which selects a player when the mouse is
    // moved
    private void setupMouseMotionListener() {
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                selectPlayerAt(e.getX(), e.getY());
                repaint();
            }
        });
    }

    // Select a player at the given coordinates (x, y)
    private void selectPlayerAt(int x, int y) {
        try {
            for (Player player : players) {
                if (player != null && player.isAlive) {
                    if (x >= player.x && x <= player.x + 10 && y >= player.y && y <= player.y + 10) {
                        selectedPlayer = player;
                        return;
                    }
                }
            }
            selectedPlayer = null;
        } catch (ConcurrentModificationException e) {
            return;
        }
    }

    // Paint the simulation panel
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        setupGraphics(g2);
        drawPlayers(g2);
        drawSelectedPlayerInfo(g2);
    }

    // Set up the graphics for the simulation panel
    private void setupGraphics(Graphics2D g2) {
        Font currentFont = g2.getFont();
        Font boldFont = currentFont.deriveFont(Font.BOLD);
        g2.setFont(boldFont);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    }

    // Draw all the players in the simulation panel
    private void drawPlayers(Graphics2D g2) {
        ArrayList<Player> playersCopy;
        try {
            playersCopy = new ArrayList<>(players);
        } catch (ConcurrentModificationException e) {
            return;
        }
        for (Player player : playersCopy) {
            if (player != null && player.isAlive) {
                drawPlayer(g2, player);
            }
        }
    }

    // Draw the connections of a player in the simulation panel
    private void drawPlayerConnections(Graphics2D g2, Player player) {
        Set<Player> acquaintancesCopy;
        try {
            acquaintancesCopy = new HashSet<>(player.acquaintances.keySet());
        } catch (ConcurrentModificationException e) {
            return;
        }
        for (Player acquaintance : acquaintancesCopy) {
            if (acquaintance != null) {
                g2.setColor(Color.BLACK);
                g2.drawLine(player.x + 5, player.y + 5, acquaintance.x + 5, acquaintance.y + 5);
            }
        }
    }

    // Draw a single player in the simulation panel along with its connections
    private void drawPlayer(Graphics2D g2, Player player) {
        g2.setColor(player.dotColor);
        g2.fillOval(player.x, player.y, 10, 10);
        drawPlayerConnections(g2, player);
    }

    // Draw the information of the selected player in the simulation panel
    private void drawSelectedPlayerInfo(Graphics2D g2) {
        if (selectedPlayer != null) {
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            int baseX = screenSize.width - 1000;
            int baseY = screenSize.height - 900;
            int lineSpacing = 20;

            ArrayList<String> playerInfo = new ArrayList<>();
            playerInfo.add(selectedPlayer.name);
            playerInfo.add("Mood: " + String.format("%.2f", selectedPlayer.mood));
            playerInfo.add("Intelligence: " + selectedPlayer.intelligence);
            playerInfo.add("Charisma: " + selectedPlayer.charisma);
            playerInfo.add("Empathy: " + selectedPlayer.empathy);
            playerInfo.add("Age: " + selectedPlayer.age);
            playerInfo.add("Acquaintances: " + selectedPlayer.acquaintances.size());
            playerInfo
                    .add("Relationship score: " + String.format("%.2f", selectedPlayer.getAverageRelationshipScore()));
            playerInfo.add("Gender: " + selectedPlayer.gender);

            if (selectedPlayer.parent1 != null && selectedPlayer.parent2 != null) {
                playerInfo.add("Parent1: " + selectedPlayer.parent1.name);
                playerInfo.add("Parent2: " + selectedPlayer.parent2.name);
                playerInfo.add("Parent1 Int: " + selectedPlayer.parent1.intelligence);
                playerInfo.add("Parent2 Int: " + selectedPlayer.parent2.intelligence);
                playerInfo.add("Parent1 Cha: " + selectedPlayer.parent1.charisma);
                playerInfo.add("Parent2 Cha: " + selectedPlayer.parent2.charisma);
                playerInfo.add("Parent1 Emp: " + selectedPlayer.parent1.empathy);
                playerInfo.add("Parent2 Emp: " + selectedPlayer.parent2.empathy);
            }
            // Draw background rectangle
            g2.setColor(Color.WHITE);
            g2.fillRect(baseX - 20, baseY - 20, 230, playerInfo.size() * lineSpacing + 20);
            g2.setColor(Color.BLACK);
            for (int i = 0; i < playerInfo.size(); i++) {
                g2.drawString(playerInfo.get(i), baseX, baseY + i * lineSpacing);
            }
        }
    }
}
