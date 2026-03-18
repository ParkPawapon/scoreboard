package com.scoreboard.domain;

import java.util.List;

public interface Scoreboard {

  int capacity();

  int count();

  boolean upsertScore(String playerName, int score);

  boolean remove(String playerName);

  List<PlayerScore> getScores();
}

