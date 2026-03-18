package com.scoreboard.domain;

public record PlayerScore(String playerName, int score) {

  public PlayerScore {
    if (playerName == null || playerName.isBlank()) {
      throw new IllegalArgumentException("Player name must not be blank.");
    }

    if (score < 0) {
      throw new IllegalArgumentException("Score must be greater than or equal to zero.");
    }

    playerName = playerName.trim();
  }
}

