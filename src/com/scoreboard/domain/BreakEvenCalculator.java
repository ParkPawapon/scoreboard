package com.scoreboard.domain;

public final class BreakEvenCalculator {

  private BreakEvenCalculator() {
  }

  public static double calculateBreakEvenNodeCount(
      int arrayElementCount,
      int elementSizeBytes,
      int pointerSizeBytes,
      int pointersPerNode) {
    validatePositive(arrayElementCount, "Array element count must be greater than zero.");
    validatePositive(elementSizeBytes, "Element size must be greater than zero.");
    validatePositive(pointerSizeBytes, "Pointer size must be greater than zero.");
    validatePositive(pointersPerNode, "Pointers per node must be greater than zero.");

    double arrayBytes = arrayElementCount * (double) elementSizeBytes;
    double nodeBytes = elementSizeBytes + (pointerSizeBytes * (double) pointersPerNode);
    return arrayBytes / nodeBytes;
  }

  public static double convertSingly32ToDoubly64(double singlyBreakEvenPoint, int elementSizeBytes) {
    if (singlyBreakEvenPoint <= 0) {
      throw new IllegalArgumentException("Break-even point must be greater than zero.");
    }

    validatePositive(elementSizeBytes, "Element size must be greater than zero.");
    return singlyBreakEvenPoint * (elementSizeBytes + 4d) / (elementSizeBytes + 16d);
  }

  private static void validatePositive(int value, String message) {
    if (value <= 0) {
      throw new IllegalArgumentException(message);
    }
  }
}

