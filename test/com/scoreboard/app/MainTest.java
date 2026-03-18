package com.scoreboard.app;

import com.scoreboard.test.Assertions;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

public final class MainTest {

  private MainTest() {
  }

  public static void runAll() {
    mainShouldPrintSampleScenario();
  }

  private static void mainShouldPrintSampleScenario() {
    PrintStream originalOut = System.out;
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    PrintStream capture = new PrintStream(output, true, StandardCharsets.UTF_8);

    try {
      System.setOut(capture);
      Main.main(new String[0]);
    } finally {
      System.setOut(originalOut);
      capture.close();
    }

    String consoleOutput = output.toString(StandardCharsets.UTF_8);

    Assertions.assertTrue(consoleOutput.contains("Initial board"), "Console output should show the initial board.");
    Assertions.assertTrue(
        consoleOutput.contains("After Vega got new score = 97"),
        "Console output should show the updated board.");
    Assertions.assertTrue(consoleOutput.contains("Vega       97"), "Console output should include Vega.");
    Assertions.assertTrue(
        consoleOutput.contains("Break-even (singly, 32-bit): 80.00"),
        "Console output should include the singly linked-list break-even value.");
    Assertions.assertTrue(
        consoleOutput.contains("Break-even (doubly, 64-bit): 50.00"),
        "Console output should include the doubly linked-list break-even value.");
  }
}
