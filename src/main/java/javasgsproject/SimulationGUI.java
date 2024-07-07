package javasgsproject;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

// GUI for the simulation
public class SimulationGUI extends JFrame {
    private SimulationPanel simulationPanel;
    private JPanel graphPanel;
    private JPanel controlPanel;
    private JSlider iterationSlider;
    private JSlider playerSlider;
    private JSlider intelligentSlider;
    private JSlider charismaticSlider;
    private JSlider empathicSlider;
    public JButton startButton;
    public JButton stopButton;
    public JButton pauseButton;
    private JProgressBar progressBar;
    private Simulation simulation;
    private int fontSize = 20;

    public SimulationGUI() {
        simulationPanel = new SimulationPanel(new ArrayList<Player>());

        setLayout(new BorderLayout());
        add(new JScrollPane(simulationPanel), BorderLayout.CENTER);
        initializeGraphPanel();
        initializeControlPanel();

        configureFrame();
    }

    private void initializeGraphPanel() {
        graphPanel = new JPanel();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = (int) (screenSize.getWidth() * 0.20);
        int height = (int) screenSize.getHeight();
        graphPanel.setLayout(new GridBagLayout());
        graphPanel.setPreferredSize(new Dimension(width, height));
        add(graphPanel, BorderLayout.WEST);
    }

    // Initialize the control panel with sliders and buttons
    private void initializeControlPanel() {
        controlPanel = new JPanel();
        controlPanel.setLayout(new GridBagLayout());
        controlPanel.setPreferredSize(new Dimension(400, getHeight()));

        GridBagConstraints gbc = new GridBagConstraints();

        // Iteration control slider
        JPanel iterationSliderPanel = new JPanel();
        iterationSliderPanel.setLayout(new BoxLayout(iterationSliderPanel, BoxLayout.Y_AXIS));
        JLabel iterationSliderLabel = new JLabel("Iteration Control Slider");
        iterationSliderLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        iterationSliderLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        iterationSliderLabel.setFont(new Font("Roboto", Font.BOLD, fontSize));

        iterationSliderPanel.add(iterationSliderLabel);
        iterationSlider = createIterationSlider();
        iterationSliderPanel.add(iterationSlider);
        iterationSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                progressBar.setMaximum(iterationSlider.getValue() - 1);
            }
        });
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTH;
        controlPanel.add(iterationSliderPanel, gbc);

        // Player count slider
        JPanel sliderPanel = new JPanel();
        sliderPanel.setLayout(new BoxLayout(sliderPanel, BoxLayout.Y_AXIS));
        JLabel sliderLabel = new JLabel("Player Count Slider");
        sliderLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        sliderLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        sliderLabel.setFont(new Font("Roboto", Font.BOLD, fontSize));

        sliderPanel.add(sliderLabel);
        playerSlider = createPlayerSlider();
        sliderPanel.add(playerSlider);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 3;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTH;
        controlPanel.add(sliderPanel, gbc);

        // Intelligent player percentage slider
        JPanel intelligentSliderPanel = new JPanel();
        intelligentSliderPanel.setLayout(new BoxLayout(intelligentSliderPanel, BoxLayout.Y_AXIS));
        JLabel intelligentSliderLabel = new JLabel("Intelligent Player Percentage Slider");
        intelligentSliderLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        intelligentSliderLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        intelligentSliderLabel.setFont(new Font("Roboto", Font.BOLD, fontSize));
        intelligentSliderPanel.add(intelligentSliderLabel);
        intelligentSlider = createIntelligentPlayerSlider();
        intelligentSliderPanel.add(intelligentSlider);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 3;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTH;
        controlPanel.add(intelligentSliderPanel, gbc);

        // Charismatic player percentage slider
        JPanel charismaticSliderPanel = new JPanel();
        charismaticSliderPanel.setLayout(new BoxLayout(charismaticSliderPanel, BoxLayout.Y_AXIS));
        JLabel charismaticSliderLabel = new JLabel("Charismatic Player Percentage Slider");
        charismaticSliderLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        charismaticSliderLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        charismaticSliderLabel.setFont(new Font("Roboto", Font.BOLD, fontSize));
        charismaticSliderPanel.add(charismaticSliderLabel);
        charismaticSlider = createCharismaticPlayerSlider();
        charismaticSliderPanel.add(charismaticSlider);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 3;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTH;
        controlPanel.add(charismaticSliderPanel, gbc);

        // Empathic player percentage slider
        JPanel empathicSliderPanel = new JPanel();
        empathicSliderPanel.setLayout(new BoxLayout(empathicSliderPanel, BoxLayout.Y_AXIS));
        JLabel empathicSliderLabel = new JLabel("Empathic Player Percentage Slider");
        empathicSliderLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        empathicSliderLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        empathicSliderLabel.setFont(new Font("Roboto", Font.BOLD, fontSize));
        empathicSliderPanel.add(empathicSliderLabel);
        empathicSlider = createEmpathicPlayerSlider();
        empathicSliderPanel.add(empathicSlider);
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 3;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTH;
        controlPanel.add(empathicSliderPanel, gbc);

        startButton = createStartButton();
        gbc.gridx = 0;
        gbc.gridy = 10;
        gbc.gridwidth = 1;
        gbc.weighty = 0.5;
        gbc.weightx = 0.5;
        gbc.anchor = GridBagConstraints.SOUTH;
        controlPanel.add(startButton, gbc);

        pauseButton = createPauseButton();
        gbc.gridx = 1;
        gbc.gridy = 10;
        gbc.weighty = 0.5;
        gbc.weightx = 0.5;
        gbc.anchor = GridBagConstraints.SOUTH;
        controlPanel.add(pauseButton, gbc);

        stopButton = createStopButton();
        gbc.gridx = 2;
        gbc.gridy = 10;
        controlPanel.add(stopButton, gbc);

        progressBar = new JProgressBar(0, iterationSlider.getValue() - 1);
        progressBar.setForeground(Color.DARK_GRAY);
        progressBar.setFont(new Font("Roboto", Font.BOLD, 14));
        progressBar.setValue(0);
        progressBar.setStringPainted(true);

        gbc.gridx = 0;
        gbc.gridy = 11;
        gbc.gridwidth = 3;
        gbc.insets = new Insets(5, 1, 0, 1);
        controlPanel.add(progressBar, gbc);

        add(controlPanel, BorderLayout.EAST);
    }

    // Create the pause button , clickable set
    private JButton createPauseButton() {
        pauseButton = new JButton("Pause");
        pauseButton.setEnabled(false);
        pauseButton.setPreferredSize(new Dimension(150, 30));
        pauseButton.setMargin(new Insets(10, 10, 10, 10));
        pauseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (simulation != null) {
                    simulation.pause();
                    if (simulation.paused == true) {
                        pauseButton.setText("Resume");
                        startButton.setEnabled(false);
                    } else {
                        pauseButton.setText("Pause");
                    }
                }
            }
        });
        return pauseButton;
    }

    // Create the start button
    private JButton createStartButton() {
        startButton = new JButton("Start");
        startButton.setPreferredSize(new Dimension(150, 30));
        startButton.setMargin(new Insets(10, 10, 10, 10));
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                simulation = new Simulation(SimulationGUI.this);
                int numPlayers = playerSlider.getValue();
                int percentIntelligent = intelligentSlider.getValue();
                int percentCharismatic = charismaticSlider.getValue();
                int percentEmpathic = empathicSlider.getValue();
                int numIterations = iterationSlider.getValue();
                simulation.start(numIterations, numPlayers, percentIntelligent, percentCharismatic, percentEmpathic);
                startButton.setEnabled(false);
                stopButton.setEnabled(true);
                pauseButton.setEnabled(true);
                pauseButton.setText("Pause");
            }
        });
        return startButton;
    }

    // Create the stop button
    private JButton createStopButton() {
        stopButton = new JButton("Stop");
        stopButton.setEnabled(false);
        stopButton.setPreferredSize(new Dimension(150, 30));
        stopButton.setMargin(new Insets(10, 10, 10, 10));
        stopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (simulation != null) {
                    simulation.stop();
                    startButton.setEnabled(true);
                    stopButton.setEnabled(false);
                    pauseButton.setEnabled(false);
                    pauseButton.setText("Pause");
                }
            }
        });
        return stopButton;
    }

    // Create the iteration slider with the range 0-5000
    private JSlider createIterationSlider() {
        JSlider iterationSlider = new JSlider(JSlider.HORIZONTAL, 0, 5000, 1000);
        iterationSlider.setMajorTickSpacing(1000);
        iterationSlider.setMinorTickSpacing(500);
        iterationSlider.setPaintTicks(true);
        iterationSlider.setPaintLabels(true);
        iterationSlider.setFont(new Font("Roboto", Font.BOLD, 15));
        ;

        return iterationSlider;
    }

    // Create the player slider with the range 0-200
    private JSlider createPlayerSlider() {
        JSlider playerSlider = new JSlider(JSlider.HORIZONTAL, 50, 400, 100);
        playerSlider.setMajorTickSpacing(50);
        playerSlider.setMinorTickSpacing(25);
        playerSlider.setPaintTicks(true);
        playerSlider.setPaintLabels(true);
        playerSlider.setFont(new Font("Roboto", Font.BOLD, 15));
        ;

        return playerSlider;
    }

    // Create the intelligent player slider with the range 0-100
    private JSlider createIntelligentPlayerSlider() {
        JSlider intelligentSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, 50);
        intelligentSlider.setMajorTickSpacing(25);
        intelligentSlider.setMinorTickSpacing(5);
        intelligentSlider.setPaintTicks(true);
        intelligentSlider.setPaintLabels(true);
        intelligentSlider.setForeground(new Color(20, 30, 240));
        intelligentSlider.setFont(new Font("Roboto", Font.BOLD, 15));
        return intelligentSlider;
    }

    // Create the charismatic player slider with the range 0-100
    private JSlider createCharismaticPlayerSlider() {
        JSlider charismaticSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, 50);
        charismaticSlider.setMajorTickSpacing(25);
        charismaticSlider.setMinorTickSpacing(5);
        charismaticSlider.setPaintTicks(true);
        charismaticSlider.setPaintLabels(true);
        charismaticSlider.setForeground(new Color(135, 29, 86));
        charismaticSlider.setFont(new Font("Roboto", Font.BOLD, 15));
        return charismaticSlider;
    }

    // Create the empathic player slider with the range 0-100
    private JSlider createEmpathicPlayerSlider() {
        JSlider empathicSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, 50);
        empathicSlider.setMajorTickSpacing(25);
        empathicSlider.setMinorTickSpacing(5);
        empathicSlider.setPaintTicks(true);
        empathicSlider.setPaintLabels(true);
        empathicSlider.setForeground(new Color(227, 155, 243));
        empathicSlider.setFont(new Font("Roboto", Font.BOLD, 15));
        return empathicSlider;
    }

    // Add the mood chart to the control panel
    public void addDataChartToControlPanel(JFreeChart chart) {
        ChartPanel chartPanel = new ChartPanel(chart);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        graphPanel.add(chartPanel, gbc);
        graphPanel.revalidate();
    }

    // Add the player count chart to the control panel
    public void addPlayerCountChartToControlPanel(JFreeChart chart) {
        ChartPanel chartPanel = new ChartPanel(chart);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        graphPanel.add(chartPanel, gbc);
        graphPanel.revalidate();
    }

    // Add the population chart to the control panel
    public void addPopulationChartToControlPanel(JFreeChart chart) {
        ChartPanel chartPanel = new ChartPanel(chart);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        graphPanel.add(chartPanel, gbc);
        graphPanel.revalidate();
    }

    // Add the trait chart to the control panel
    public void addTraitChartToControlPanel(JFreeChart chart) {
        ChartPanel chartPanel = new ChartPanel(chart);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        graphPanel.add(chartPanel, gbc);
        graphPanel.revalidate();
    }

    // Configure the frame
    private void configureFrame() {
        setSize(1300, 800);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    // Update the data in the simulation panel with the updated players
    public void updateData(ArrayList<Player> updatedPlayers) {
        SwingUtilities.invokeLater(() -> {
            simulationPanel.updatePlayers(updatedPlayers);
            simulationPanel.revalidate();
            simulationPanel.repaint();
            int currentIteration = simulation.getCurrentIteration();
            progressBar.setValue(currentIteration);
        });
    }

    // Get the player slider from the GUI
    public JSlider getPlayerSlider() {
        return playerSlider;
    }
}
