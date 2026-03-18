package com.scoreboard.test;

import com.scoreboard.app.MainTest;
import com.scoreboard.domain.TopScoreboardTest;

public final class TestRunner {

  private TestRunner() {
  }

  public static void main(String[] args) {
    int passed = 0;

    passed += run("TopScoreboardTest", TopScoreboardTest::runAll);
    passed += run("MainTest", MainTest::runAll);

    System.out.println("All tests passed: " + passed);
  }

  private static int run(String suiteName, Runnable suite) {
    suite.run();
    System.out.println("Passed: " + suiteName);
    return 1;
  }
}

