package com.scoreboard.test;

import java.util.Objects;

public final class Assertions {

  private Assertions() {
  }

  public static void assertTrue(boolean condition, String message) {
    if (!condition) {
      throw new AssertionError(message);
    }
  }

  public static void assertFalse(boolean condition, String message) {
    if (condition) {
      throw new AssertionError(message);
    }
  }

  public static void assertEquals(Object expected, Object actual, String message) {
    if (!Objects.equals(expected, actual)) {
      throw new AssertionError(message + " Expected: " + expected + ", actual: " + actual);
    }
  }

  public static void assertDoubleEquals(double expected, double actual, double tolerance, String message) {
    if (Math.abs(expected - actual) > tolerance) {
      throw new AssertionError(message + " Expected: " + expected + ", actual: " + actual);
    }
  }

  public static void assertThrows(Class<? extends Throwable> expectedType, Runnable action, String message) {
    try {
      action.run();
    } catch (Throwable throwable) {
      if (expectedType.isInstance(throwable)) {
        return;
      }

      throw new AssertionError(message + " Threw unexpected exception type: " + throwable.getClass().getName());
    }

    throw new AssertionError(message + " Expected exception type: " + expectedType.getName());
  }
}

