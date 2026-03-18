package com.scoreboard.app;

import com.scoreboard.domain.BreakEvenCalculator;
import com.scoreboard.domain.PlayerScore;
import com.scoreboard.domain.TopScoreboard;
import java.util.List;
import java.util.Locale;

public final class Main {

  private Main() {
  }

  public static void main(String[] args) {
    TopScoreboard scoreboard = new TopScoreboard(4);

    scoreboard.upsertScore("Ryu", 100);
    scoreboard.upsertScore("Ken", 98);
    scoreboard.upsertScore("Chunli", 95);
    scoreboard.upsertScore("Sagat", 94);

    printBoard("Initial board", scoreboard.getScores());

    scoreboard.upsertScore("Vega", 97);
    printBoard("After Vega got new score = 97", scoreboard.getScores());

    double singly32 = BreakEvenCalculator.calculateBreakEvenNodeCount(100, 16, 4, 1);
    double doubly64 = BreakEvenCalculator.convertSingly32ToDoubly64(singly32, 16);

    System.out.println("Break-even (singly, 32-bit): " + String.format(Locale.ROOT, "%.2f", singly32));
    System.out.println("Break-even (doubly, 64-bit): " + String.format(Locale.ROOT, "%.2f", doubly64));
  }

  private static void printBoard(String title, List<PlayerScore> scores) {
    System.out.println(title);
    for (PlayerScore score : scores) {
      System.out.printf("%-10s %d%n", score.playerName(), score.score());
    }
    System.out.println();
  }
}

