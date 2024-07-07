package javasgsproject;

import java.util.*;

import javax.swing.SwingWorker;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.awt.Color;
import java.awt.RenderingHints;
import java.lang.Thread;

// Simulation class that represents the simulation
public class Simulation {
  private ArrayList<Player> players;
  private ArrayList<Player> shuffledPlayers;
  private Random random;
  private final SimulationGUI gui;
  private int current_iteration;
  public static int NUM_ITERATIONS;
  private volatile boolean running = true;
  private ArrayList<Double> averageMoods = new ArrayList<>();
  private XYSeries moodSeries;
  private XYSeries playerCountSeries;
  private XYSeries malePopulationSeries;
  private XYSeries femalePopulationSeries;
  private DefaultCategoryDataset traitDataset;
  public boolean paused = false;

  public void pause() {
    paused = !paused;
  }

  public Simulation(SimulationGUI gui) {
    this.gui = gui;
    moodSeries = new XYSeries("Average Mood");
    playerCountSeries = new XYSeries("Player Count");
    malePopulationSeries = new XYSeries("Male Population");
    femalePopulationSeries = new XYSeries("Female Population");
    XYSeriesCollection moodDataset = new XYSeriesCollection(moodSeries);
    XYSeriesCollection playerCountDataset = new XYSeriesCollection(playerCountSeries);
    XYSeriesCollection populationDataset = new XYSeriesCollection();
    populationDataset.addSeries(malePopulationSeries);
    populationDataset.addSeries(femalePopulationSeries);
    traitDataset = new DefaultCategoryDataset();

    traitDataset.addValue(0, "Intelligent", "INT");
    traitDataset.addValue(0, "Charismatic", "CHA");
    traitDataset.addValue(0, "Empathic", "EMP");
    traitDataset.addValue(0, "All Traits", "ALL");
    traitDataset.addValue(0, "No Traits", "NONE");

    JFreeChart moodDataChart = ChartFactory.createXYLineChart(
        "Average Mood Over Time",
        "Iteration",
        "Mood",
        moodDataset);

    XYPlot moodPlot = moodDataChart.getXYPlot();
    ValueAxis moodAxis = moodPlot.getRangeAxis();
    moodAxis.setRange(-1.0, 1.0);
    moodDataChart.getLegend().setVisible(false);

    JFreeChart playerCountChart = ChartFactory.createXYLineChart(
        "Player Count Over Time",
        "Iteration",
        "Player Count",
        playerCountDataset);
    playerCountChart.getLegend().setVisible(false);

    JFreeChart populationChart = ChartFactory.createXYLineChart(
        "Population Over Time",
        "Iteration",
        "Population",
        populationDataset);
    XYPlot plot = populationChart.getXYPlot();

    XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
    renderer.setSeriesPaint(0, Color.BLUE);
    renderer.setSeriesPaint(1, Color.RED);
    renderer.setSeriesShapesVisible(0, false);
    renderer.setSeriesShapesVisible(1, false);
    plot.setRenderer(renderer);

    JFreeChart traitChart = ChartFactory.createBarChart(
        "Trait Count",
        "Traits",
        "Player Count",
        traitDataset);

    CategoryPlot plotBar = traitChart.getCategoryPlot();

    BarRenderer rendererBar = (BarRenderer) plotBar.getRenderer();
    rendererBar.setBarPainter(new StandardBarPainter());
    rendererBar.setSeriesPaint(0, Player.BLUE);
    rendererBar.setSeriesPaint(1, Player.PURPLE);
    rendererBar.setSeriesPaint(2, Player.PINK);
    rendererBar.setSeriesPaint(3, Color.GREEN);
    rendererBar.setSeriesPaint(4, Color.BLACK);

    traitChart.getLegend().setVisible(false);

    setRenderingHints(playerCountChart);
    setRenderingHints(moodDataChart);
    setRenderingHints(populationChart);
    setRenderingHints(traitChart);

    gui.addDataChartToControlPanel(moodDataChart);
    gui.addPlayerCountChartToControlPanel(playerCountChart);
    gui.addPopulationChartToControlPanel(populationChart);
    gui.addTraitChartToControlPanel(traitChart);

  }

  private void setRenderingHints(JFreeChart chart) {
    chart.getRenderingHints().put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
  }

  // Starts the simulation
  public void start(int numIterations, int numPlayers, int percentIntelligent, int percentCharismatic,
      int percentEmpathic) {
    SwingWorker<Void, Void> worker = new SwingWorker<>() {
      @Override
      protected Void doInBackground() throws Exception {
        players = new ArrayList<>(numPlayers);
        random = new Random();

        NUM_ITERATIONS = numIterations;
        addPlayers(numPlayers, percentIntelligent, percentCharismatic, percentEmpathic);
        addMinimumAcquaintance(numPlayers);
        addAcquaintances(numPlayers);
        simulate(numPlayers, numIterations);

        return null;
      }

      @Override
      protected void done() {
        gui.updateData(players);
      }
    };

    worker.execute();
  }

  // Creates a new player and adds it to the list of players
  private void addPlayers(int numPlayers, int percentIntelligent, int percentCharismatic, int percentEmpathic) {
    for (int i = 0; i < numPlayers; i++) {
      double intelligentThreshold = numPlayers * percentIntelligent / 100.0;
      double charismaticThreshold = numPlayers * percentCharismatic / 100.0;
      double empathicThreshold = numPlayers * percentEmpathic / 100.0;
      boolean isIntelligent = i < intelligentThreshold;
      boolean isCharismatic = i < charismaticThreshold;
      boolean isEmpathic = i < empathicThreshold;
      players.add(new Player(isIntelligent, isCharismatic, isEmpathic));
      pauseAndUpdateGUI();
    }
  }

  // Adds a minimum number of acquaintances(1) to each player apart from
  // themselves
  private void addMinimumAcquaintance(int numPlayers) {
    for (int i = 0; i < numPlayers; i++) {
      int randomIndex;
      do {
        randomIndex = random.nextInt(numPlayers);
      } while (randomIndex == i);
      players.get(i).addRandomAcquaintance(players.get(randomIndex));
    }
  }

  // Adds random acquaintances to each player
  private void addAcquaintances(int numPlayers) {
    for (int i = 0; i < numPlayers; i++) {
      int numAcquaintances = random.nextInt(numPlayers - 1);
      for (int j = 0; j < numAcquaintances; j++) {
        int randomIndex;
        do {
          randomIndex = random.nextInt(numPlayers);
        } while (randomIndex == i);
        players.get(i).addRandomAcquaintance(players.get(randomIndex));
      }
    }
  }

  // Simulates the interactions between players
  private void simulate(int numPlayers, int NUM_ITERATIONS) {

    while (running) {
      shuffledPlayers = new ArrayList<>(players);
      ArrayList<Player> tempPlayers = new ArrayList<>();
      ArrayList<Player> playersToRemove = new ArrayList<>();

      for (int i = 0; i < NUM_ITERATIONS; i++) {
        shuffledPlayers.clear();
        shuffledPlayers.addAll(players);
        while (paused) {
          try {
            Thread.sleep(1);
          } catch (InterruptedException e) {
          }
        }
        if (!running) {
          break;
        }
        Collections.shuffle(shuffledPlayers);
        for (Player player : shuffledPlayers) {
          simulatePlayer(player, tempPlayers, playersToRemove);
        }
        players.removeAll(playersToRemove);
        playersToRemove.clear();
        players.addAll(tempPlayers);
        tempPlayers.clear();
        pauseAndUpdateGUI();
        current_iteration = i;
        double averageMood = calculateAverageMood();
        averageMoods.add(averageMood);
        if (i > 5) {
          moodSeries.add(i, averageMood);
          playerCountSeries.add(i, players.size());
          updatePopulationSeries();
          int[] traitCounts = countPlayerTraits();
          int intelligentCount = traitCounts[0];
          int charismaticCount = traitCounts[1];
          int empathicCount = traitCounts[2];
          int allTraitsCount = traitCounts[3];
          int noTraitsCount = traitCounts[4];
          traitDataset.addValue(intelligentCount, "Intelligent", "INT");
          traitDataset.addValue(charismaticCount, "Charismatic", "CHA");
          traitDataset.addValue(empathicCount, "Empathic", "EMP");
          traitDataset.addValue(allTraitsCount, "All Traits", "ALL");
          traitDataset.addValue(noTraitsCount, "No Traits", "NONE");

        }
        if (players.size() == 0) {
          gui.stopButton.setEnabled(false);
          gui.startButton.setEnabled(true);
          gui.pauseButton.setEnabled(false);
          running = false;
        }
      }
      gui.stopButton.setEnabled(false);
      gui.startButton.setEnabled(true);
      gui.pauseButton.setEnabled(false);
      running = false;
    }
  }

  // Simulates the interactions of a single player
  private void simulatePlayer(Player player, List<Player> tempPlayers, List<Player> playersToRemove) {
    player.age++;
    int birthRate;

    if (players.size() < 100)
      birthRate = 60;
    else if (players.size() < 200)
      birthRate = 40;
    else if (players.size() < 300)
      birthRate = 20;
    else if (players.size() < 400)
      birthRate = 10;
    else
      birthRate = 5;

    if (player.isAlive) {
      player.sendMessage();
    } else {
      playersToRemove.add(player);
    }

    for (Player otherPlayer : players) {
      if (player != otherPlayer && player.relationships.get(otherPlayer) != null) {
        Events.becomePartners(player, otherPlayer);
        Events.breakUp(player, otherPlayer);
        Player child = Events.createChild(player, otherPlayer, birthRate);
        if (child != null) {
          tempPlayers.add(child);
        }
        player.breakAcquaintance(otherPlayer);
      }
    }
    Events.ageAndCheckDeath(player);
    Events.checkForDepression(player);
    Events.introduceAcquaintances(player);
    player.changeMood();
  }

  // Adds delay to the simulation and updates the GUI
  private void pauseAndUpdateGUI() {
    try {
      Thread.sleep(1);
    } catch (InterruptedException e) {}
    gui.updateData(players);
  }

  // Returns the current iteration of the simulation
  public int getCurrentIteration() {
    return current_iteration;
  }

  // Stops the simulation
  public void stop() {
    running = false;
  }

  // Returns the average mood of all players
  private double calculateAverageMood() {
    if (players.isEmpty()) {
      return 0.0;
    }
    double totalMood = 0;
    for (Player player : players) {
      totalMood += player.getMood();
    }
    return totalMood / players.size();
  }

  // Counts the number of male and female players
  public void updatePopulationSeries() {
    int maleCount = 0;
    int femaleCount = 0;

    for (Player player : players) {
      if (player.gender == 'M') {
        maleCount++;
      } else {
        femaleCount++;
      }
    }

    malePopulationSeries.add(current_iteration, maleCount);
    femalePopulationSeries.add(current_iteration, femaleCount);
  }

  // Counts the number of players with each trait
  public int[] countPlayerTraits() {
    int intelligentCount = 0;
    int charismaticCount = 0;
    int empathicCount = 0;
    int allTraitsCount = 0;
    int noTraitsCount = 0;

    if (!players.isEmpty()) {
      for (Player player : players) {
        boolean isIntelligent = player.isIntelligent();
        boolean isCharismatic = player.isCharismatic();
        boolean isEmpathic = player.isEmpathic();

        if (isIntelligent) {
          intelligentCount++;
        }
        if (isCharismatic) {
          charismaticCount++;
        }
        if (isEmpathic) {
          empathicCount++;
        }
        if (isIntelligent && isCharismatic && isEmpathic) {
          allTraitsCount++;
        }
        if (!isIntelligent && !isCharismatic && !isEmpathic) {
          noTraitsCount++;
        }
      }
    }
    return new int[] { intelligentCount, charismaticCount, empathicCount, allTraitsCount, noTraitsCount };
  }
}
