package com.scoreboard.domain;

import com.scoreboard.test.Assertions;
import java.util.List;

public final class TopScoreboardTest {

  private TopScoreboardTest() {
  }

  public static void runAll() {
    upsertScoreShouldKeepScoresSortedDescending();
    upsertScoreWhenBoardIsFullShouldTrimTail();
    upsertScoreWhenFullAndScoreIsTooLowShouldReject();
    upsertScoreWhenPlayerAlreadyExistsShouldRepositionWithoutChangingCount();
    upsertScoreShouldTreatPlayerNamesCaseInsensitively();
    removeShouldDeleteExistingPlayer();
    removeWhenPlayerIsMissingShouldReturnFalse();
    getScoresShouldReturnImmutableSnapshot();
    constructorWithInvalidCapacityShouldThrow();
    removeWithInvalidPlayerNameShouldThrow();
    upsertScoreWithInvalidDataShouldThrow();
    tieScoreShouldUseNameAsDeterministicTiebreaker();
    convertSingly32ToDoubly64ShouldMatchDirectCalculation();
    calculateBreakEvenNodeCountWithInvalidArgumentsShouldThrow();
    convertSingly32ToDoubly64WithInvalidArgumentsShouldThrow();
  }

  private static void upsertScoreShouldKeepScoresSortedDescending() {
    TopScoreboard board = new TopScoreboard(4);

    board.upsertScore("Sagat", 94);
    board.upsertScore("Ryu", 100);
    board.upsertScore("Chunli", 95);
    board.upsertScore("Ken", 98);

    Assertions.assertEquals(
        List.of(
            new PlayerScore("Ryu", 100),
            new PlayerScore("Ken", 98),
            new PlayerScore("Chunli", 95),
            new PlayerScore("Sagat", 94)),
        board.getScores(),
        "Scores should be sorted descending.");
  }

  private static void upsertScoreWhenBoardIsFullShouldTrimTail() {
    TopScoreboard board = new TopScoreboard(4);
    seedSampleBoard(board);

    boolean isOnBoard = board.upsertScore("Vega", 97);

    Assertions.assertTrue(isOnBoard, "New score should be inserted.");
    Assertions.assertEquals(4, board.count(), "Board should keep fixed capacity.");
    Assertions.assertEquals(
        List.of(
            new PlayerScore("Ryu", 100),
            new PlayerScore("Ken", 98),
            new PlayerScore("Vega", 97),
            new PlayerScore("Chunli", 95)),
        board.getScores(),
        "Tail entry should be trimmed after insertion.");
  }

  private static void upsertScoreWhenFullAndScoreIsTooLowShouldReject() {
    TopScoreboard board = new TopScoreboard(4);
    seedSampleBoard(board);

    boolean isOnBoard = board.upsertScore("Guile", 90);

    Assertions.assertFalse(isOnBoard, "Score should be rejected when it does not reach the board.");
    Assertions.assertEquals(4, board.count(), "Count should stay unchanged.");
    Assertions.assertFalse(
        board.getScores().contains(new PlayerScore("Guile", 90)),
        "Rejected player must not appear on the board.");
  }

  private static void upsertScoreWhenPlayerAlreadyExistsShouldRepositionWithoutChangingCount() {
    TopScoreboard board = new TopScoreboard(4);
    seedSampleBoard(board);

    boolean isOnBoard = board.upsertScore("Ken", 93);

    Assertions.assertTrue(isOnBoard, "Existing player should remain on the board.");
    Assertions.assertEquals(4, board.count(), "Count should remain unchanged.");
    Assertions.assertEquals(
        List.of(
            new PlayerScore("Ryu", 100),
            new PlayerScore("Chunli", 95),
            new PlayerScore("Sagat", 94),
            new PlayerScore("Ken", 93)),
        board.getScores(),
        "Existing player should be repositioned.");
  }

  private static void upsertScoreShouldTreatPlayerNamesCaseInsensitively() {
    TopScoreboard board = new TopScoreboard(4);

    board.upsertScore("Ken", 98);
    boolean isOnBoard = board.upsertScore("ken", 99);

    Assertions.assertTrue(isOnBoard, "Case-insensitive update should succeed.");
    Assertions.assertEquals(1, board.count(), "Player should not be duplicated.");
    Assertions.assertEquals(
        List.of(new PlayerScore("ken", 99)),
        board.getScores(),
        "Latest value should replace the previous entry.");
  }

  private static void removeShouldDeleteExistingPlayer() {
    TopScoreboard board = new TopScoreboard(4);
    seedSampleBoard(board);

    boolean removed = board.remove("Sagat");

    Assertions.assertTrue(removed, "Existing player should be removed.");
    Assertions.assertEquals(3, board.count(), "Count should decrease after removal.");
    Assertions.assertFalse(
        board.getScores().contains(new PlayerScore("Sagat", 94)),
        "Removed player must disappear from the board.");
  }

  private static void removeWhenPlayerIsMissingShouldReturnFalse() {
    TopScoreboard board = new TopScoreboard(4);
    seedSampleBoard(board);

    boolean removed = board.remove("Dhalsim");

    Assertions.assertFalse(removed, "Removing a missing player should return false.");
    Assertions.assertEquals(4, board.count(), "Count should stay unchanged.");
  }

  private static void getScoresShouldReturnImmutableSnapshot() {
    TopScoreboard board = new TopScoreboard(4);
    seedSampleBoard(board);

    List<PlayerScore> scores = board.getScores();

    Assertions.assertThrows(
        UnsupportedOperationException.class,
        () -> scores.add(new PlayerScore("Vega", 97)),
        "Returned score list must be immutable.");
  }

  private static void constructorWithInvalidCapacityShouldThrow() {
    Assertions.assertThrows(
        IllegalArgumentException.class,
        () -> new TopScoreboard(0),
        "Capacity below minimum should fail.");
    Assertions.assertThrows(
        IllegalArgumentException.class,
        () -> new TopScoreboard(11),
        "Capacity above maximum should fail.");
  }

  private static void removeWithInvalidPlayerNameShouldThrow() {
    TopScoreboard board = new TopScoreboard(4);

    Assertions.assertThrows(
        IllegalArgumentException.class,
        () -> board.remove(" "),
        "Blank player name should fail.");
  }

  private static void upsertScoreWithInvalidDataShouldThrow() {
    TopScoreboard board = new TopScoreboard(4);

    Assertions.assertThrows(
        IllegalArgumentException.class,
        () -> board.upsertScore(" ", 10),
        "Blank player name should fail.");
    Assertions.assertThrows(
        IllegalArgumentException.class,
        () -> board.upsertScore("Ryu", -1),
        "Negative score should fail.");
  }

  private static void tieScoreShouldUseNameAsDeterministicTiebreaker() {
    TopScoreboard board = new TopScoreboard(4);

    board.upsertScore("Vega", 97);
    board.upsertScore("Bison", 97);

    Assertions.assertEquals(
        List.of(new PlayerScore("Bison", 97), new PlayerScore("Vega", 97)),
        board.getScores(),
        "Equal scores should use deterministic name ordering.");
  }

  private static void convertSingly32ToDoubly64ShouldMatchDirectCalculation() {
    double singly32 = BreakEvenCalculator.calculateBreakEvenNodeCount(100, 16, 4, 1);
    double doubly64FromRelation = BreakEvenCalculator.convertSingly32ToDoubly64(singly32, 16);
    double doubly64Direct = BreakEvenCalculator.calculateBreakEvenNodeCount(100, 16, 8, 2);

    Assertions.assertDoubleEquals(
        doubly64Direct,
        doubly64FromRelation,
        1e-10,
        "Converted break-even value should match direct calculation.");
  }

  private static void calculateBreakEvenNodeCountWithInvalidArgumentsShouldThrow() {
    Assertions.assertThrows(
        IllegalArgumentException.class,
        () -> BreakEvenCalculator.calculateBreakEvenNodeCount(0, 16, 4, 1),
        "Array element count must be positive.");
    Assertions.assertThrows(
        IllegalArgumentException.class,
        () -> BreakEvenCalculator.calculateBreakEvenNodeCount(100, 0, 4, 1),
        "Element size must be positive.");
    Assertions.assertThrows(
        IllegalArgumentException.class,
        () -> BreakEvenCalculator.calculateBreakEvenNodeCount(100, 16, 0, 1),
        "Pointer size must be positive.");
    Assertions.assertThrows(
        IllegalArgumentException.class,
        () -> BreakEvenCalculator.calculateBreakEvenNodeCount(100, 16, 4, 0),
        "Pointers per node must be positive.");
  }

  private static void convertSingly32ToDoubly64WithInvalidArgumentsShouldThrow() {
    Assertions.assertThrows(
        IllegalArgumentException.class,
        () -> BreakEvenCalculator.convertSingly32ToDoubly64(0, 16),
        "Break-even point must be positive.");
    Assertions.assertThrows(
        IllegalArgumentException.class,
        () -> BreakEvenCalculator.convertSingly32ToDoubly64(100, 0),
        "Element size must be positive.");
  }

  private static void seedSampleBoard(TopScoreboard board) {
    board.upsertScore("Ryu", 100);
    board.upsertScore("Ken", 98);
    board.upsertScore("Chunli", 95);
    board.upsertScore("Sagat", 94);
  }
}

