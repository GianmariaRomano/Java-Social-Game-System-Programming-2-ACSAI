package javasgsproject;

import java.util.*;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;

// Player class that represents a player in the simulation
public class Player {
    int age;
    boolean isAlive = true;
    String name;
    double mood;
    int intelligence;
    int charisma;
    int empathy;
    char gender;
    double disappointments;
    HashMap<Player, Double> relationships = new HashMap<>();
    HashMap<Player, Integer> acquaintances = new HashMap<>();
    ArrayList<Message> receivedMessages = new ArrayList<>();
    ArrayList<Message> sentMessages = new ArrayList<>();
    int receivedMessagesCounter = 0;
    int sentMessagesCounter = 0;
    int maxAcquaintances = 10;
    Player partner;
    static Set<String> usedNames = new HashSet<>();
    static Random random = new Random();
    int x;
    int y;
    static int i;
    static List<Player> allPlayers = new ArrayList<>();
    Player parent1 = null;
    Player parent2 = null;
    int childrenCount = 0;
    Color dotColor;
    final static Color BLUE = new Color(33, 44, 255);
    final static Color GREEN = new Color(118, 252, 61);
    final static Color PURPLE = new Color(135, 29, 86);
    final static Color PINK = new Color(227, 155, 243);
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    int screen_width = (int) screenSize.getWidth();
    int screen_height = (int) screenSize.getHeight();
    boolean isIntelligent;
    boolean isCharismatic;
    boolean isEmpathic;

    Player(boolean isIntelligent, boolean isCharismatic, boolean isEmpathic) {
        this.age = random.nextInt(100);
        this.name = generateUniqueName();
        this.acquaintances.put(this, 0);
        setMood(random.nextDouble() * 2 - 1);
        this.intelligence = random.nextInt(101);
        if (!isIntelligent) {
            this.intelligence -= 100;
        }
        this.charisma = random.nextInt(101);
        if (!isCharismatic) {
            this.charisma -= 100;
        } else {
            this.maxAcquaintances = 15;
        }
        this.empathy = random.nextInt(101);
        if (!isEmpathic) {
            this.empathy -= 100;
        }
        if (isIntelligent && isCharismatic && isEmpathic) {
            this.dotColor = GREEN;
        } else if (isIntelligent && isCharismatic) {
            this.dotColor = new Color(26, 3, 168);
        } else if (isIntelligent && isEmpathic) {
            this.dotColor = new Color(119, 65, 247);
        } else if (isCharismatic && isEmpathic) {
            this.dotColor = new Color(145, 68, 160);
        } else if (isCharismatic) {
            this.dotColor = PURPLE;
        } else if (isIntelligent) {
            this.dotColor = BLUE;
        } else if (isEmpathic) {
            this.dotColor = PINK;
        } else {
            this.dotColor = Color.BLACK;
        }
        do {
            this.x = 50 + random.nextInt(screen_width - 950);
            this.y = 50 + random.nextInt(screen_height - 200);
        } while (isPositionTaken(this.x, this.y));
        allPlayers.add(this);
        this.gender = random.nextBoolean() ? 'M' : 'F';
        this.partner = null;

        this.isIntelligent = isIntelligent;
        this.isCharismatic = isCharismatic;
        this.isEmpathic = isEmpathic;
    }

    public boolean isIntelligent() {
        return isIntelligent;
    }

    public boolean isCharismatic() {
        return isCharismatic;
    }

    public boolean isEmpathic() {
        return isEmpathic;
    }

    // Generate a unique name for each player (Player + some random number)
    private static String generateUniqueName() {
        String name;
        name = "Player" + i;
        i++;
        return name;
    }

    // Check if the position (In the GUI) is already taken by another player
    private boolean isPositionTaken(int x, int y) {
        for (Player player : allPlayers) {
            if (player.x == x && player.y == y) {
                return true;
            }
        }
        return false;
    }

    // Add an acquaintance to the player
    void addAcquaintance(Player acquaintance) {
        if (this.acquaintances.size() < this.maxAcquaintances &&
                acquaintance.acquaintances.size() < acquaintance.maxAcquaintances) {
            this.acquaintances.put(acquaintance, this.acquaintances.size());
            acquaintance.acquaintances.put(this, acquaintance.acquaintances.size());
        }
    }

    // Add a random acquaintance to the player with a 20% chance
    public void addRandomAcquaintance(Player acquaintance) {
        if ((this.acquaintances.size() < this.maxAcquaintances) && Math.random() < 0.2 &&
                acquaintance.acquaintances.size() < acquaintance.maxAcquaintances && Math.random() < 0.2) {
            this.acquaintances.put(acquaintance, this.acquaintances.size());
            acquaintance.acquaintances.put(this, acquaintance.acquaintances.size());
        }
    }

    // Break acquaintance with a player
    public void breakAcquaintance(Player friend) {
        if (this == friend) {
            return;
        }
        if (this.partner == friend) {
            return;
        }
        if ((this.relationships.get(friend) != null && this.relationships.get(friend) < -200)) {
            // System.out.println(this.name + " broke acquaintance with " + friend.name +
            // "with a score of " + this.relationships.get(friend));
            this.acquaintances.remove(friend);
            this.relationships.remove(friend);
            friend.acquaintances.remove(this);
            friend.relationships.remove(this);
        }
    }

    // Send a message to all acquaintances
    public void sendMessage() {
        double positivityScale;

        for (Player acquaintance : this.acquaintances.keySet()) {
            positivityScale = this.mood + this.intelligence * 0.20;

            if (empathy > 0 && acquaintance.mood < 0) {
                positivityScale += empathy * 2;
            } else if (empathy < 0 && acquaintance.mood < 0) {
                positivityScale += empathy * 2;
            }

            if (!this.receivedMessages.isEmpty()) {
                Message lastMessage = this.receivedMessages.get(this.receivedMessages.size() - 1);
                if (lastMessage.positivityScale > 0) {
                    positivityScale += random.nextInt(1);
                } else if (lastMessage.positivityScale < 0) {
                    positivityScale -= random.nextInt(1);
                }
            }
            Message message = new Message(this, acquaintance, positivityScale);
            sentMessages.add(message);
            sentMessagesCounter++;
            acquaintance.receiveMessage(message);
        }
    }

    // Receive a message from another player
    public void receiveMessage(Message message) {
        receivedMessages.add(message);
        receivedMessagesCounter++;
        if (!this.relationships.containsKey(message.sender)) {
            this.relationships.put(message.sender, 0.0);
        }
        double newScore = this.relationships.get(message.sender) + message.positivityScale + this.intelligence * 0.20
                + this.charisma * 0.01;

        if (this.empathy > 0 && message.sender.mood < 0) {
            newScore += (empathy);
        } else if (this.empathy < 0 && message.sender.mood < 0) {
            newScore += (empathy);
        }

        this.relationships.put(message.sender, newScore);
        message.sender.relationships.put(this, newScore);
        if (message.sender.mood < 0) {
            addDisappointment(10);
        }
    }

    // Calculate the average positivity scale of received messages
    public double calculateAverageReceivedPositivityScale() {
        if (receivedMessages.isEmpty()) {
            return 0.0;
        }

        double totalReceivedPositivityScale = 0;
        for (Message message : receivedMessages) {
            totalReceivedPositivityScale += message.positivityScale;
        }

        return totalReceivedPositivityScale / receivedMessages.size();
    }

    // Calculate the average positivity scale of sent messages
    public double calculateAverageSentPositivityScale() {
        if (sentMessages.isEmpty()) {
            return 0.0;
        }

        double totalSentPositivityScale = 0;
        for (Message message : sentMessages) {
            totalSentPositivityScale += message.positivityScale;
        }

        return totalSentPositivityScale / sentMessages.size();
    }

    // Change the mood of the player based on the average positivity scale of sent
    // and received messages
    public void changeMood() {
        double sentPositivityScale = calculateAverageSentPositivityScale();
        double receivedPositivityScale = calculateAverageReceivedPositivityScale();
        double moodChange;
        moodChange = receivedPositivityScale - sentPositivityScale;
        // this.mood = mood * 0.8 + moodChange * 0.2;
        if (this.isEmpathic) {
            setMood(mood * 0.3 + moodChange * 0.7);
        } else {
            setMood(mood * 0.8 + moodChange * 0.2);
        }
    }

    // Method to set the mood of the player between -1 and 1
    public void setMood(double mood) {
        this.mood = Math.max(-1.0, Math.min(1.0, mood));
    }

    // Get the average relationship score of the player
    public double getAverageRelationshipScore() {
        if (relationships.isEmpty()) {
            return 0;
        }
        double totalScore = 0;
        for (double score : relationships.values()) {
            totalScore += score;
        }
        return (double) totalScore / relationships.size();
    }

    // Check if the player is an acquaintance of another player
    public boolean isAcquaintance(Player otherPlayer) {
        return this.acquaintances.containsKey(otherPlayer);
    }

    // Method to add a disappointment to the player
    public void addDisappointment(double disappointment) {
        this.disappointments += disappointment;
    }

    // Method to get the mood of the player
    public double getMood() {
        return this.mood;
    }
}
