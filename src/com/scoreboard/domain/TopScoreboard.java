package com.scoreboard.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class TopScoreboard implements Scoreboard {

  private static final int MAX_SUPPORTED_CAPACITY = 10;

  private final int capacity;
  private final Map<String, ScoreNode> nodesByPlayer = new HashMap<>();

  private ScoreNode head;
  private ScoreNode tail;
  private int count;

  public TopScoreboard() {
    this(MAX_SUPPORTED_CAPACITY);
  }

  public TopScoreboard(int capacity) {
    if (capacity <= 0 || capacity > MAX_SUPPORTED_CAPACITY) {
      throw new IllegalArgumentException(
          "Capacity must be between 1 and " + MAX_SUPPORTED_CAPACITY + ".");
    }

    this.capacity = capacity;
  }

  @Override
  public int capacity() {
    return capacity;
  }

  @Override
  public int count() {
    return count;
  }

  @Override
  public boolean upsertScore(String playerName, int score) {
    PlayerScore candidate = new PlayerScore(playerName, score);
    String normalizedKey = normalizeKey(candidate.playerName());

    ScoreNode existingNode = nodesByPlayer.get(normalizedKey);
    if (existingNode != null) {
      removeNode(existingNode);
      nodesByPlayer.remove(normalizedKey);
      count--;
    }

    if (count == capacity && tail != null && compareByRank(candidate, tail.value) > 0) {
      return false;
    }

    ScoreNode newNode = new ScoreNode(candidate);
    insertNodeByRank(newNode);
    nodesByPlayer.put(normalizedKey, newNode);
    count++;

    if (count > capacity) {
      trimTail();
    }

    return nodesByPlayer.containsKey(normalizedKey);
  }

  @Override
  public boolean remove(String playerName) {
    if (playerName == null || playerName.isBlank()) {
      throw new IllegalArgumentException("Player name must not be blank.");
    }

    String normalizedKey = normalizeKey(playerName);
    ScoreNode node = nodesByPlayer.get(normalizedKey);
    if (node == null) {
      return false;
    }

    removeNode(node);
    nodesByPlayer.remove(normalizedKey);
    count--;
    return true;
  }

  @Override
  public List<PlayerScore> getScores() {
    List<PlayerScore> scores = new ArrayList<>(count);
    ScoreNode current = head;

    while (current != null) {
      scores.add(current.value);
      current = current.next;
    }

    return List.copyOf(scores);
  }

  private static String normalizeKey(String playerName) {
    return playerName.trim().toLowerCase(Locale.ROOT);
  }

  private static int compareByRank(PlayerScore left, PlayerScore right) {
    int scoreComparison = Integer.compare(right.score(), left.score());
    if (scoreComparison != 0) {
      return scoreComparison;
    }

    int caseInsensitiveNameComparison = left.playerName().compareToIgnoreCase(right.playerName());
    if (caseInsensitiveNameComparison != 0) {
      return caseInsensitiveNameComparison;
    }

    return left.playerName().compareTo(right.playerName());
  }

  private void insertNodeByRank(ScoreNode node) {
    if (head == null) {
      head = node;
      tail = node;
      return;
    }

    ScoreNode current = head;
    while (current != null && compareByRank(node.value, current.value) >= 0) {
      current = current.next;
    }

    if (current == null) {
      appendToTail(node);
      return;
    }

    insertBefore(current, node);
  }

  private void appendToTail(ScoreNode node) {
    if (tail == null) {
      head = node;
      tail = node;
      return;
    }

    node.previous = tail;
    tail.next = node;
    tail = node;
  }

  private void insertBefore(ScoreNode target, ScoreNode node) {
    node.next = target;
    node.previous = target.previous;

    if (target.previous == null) {
      head = node;
    } else {
      target.previous.next = node;
    }

    target.previous = node;
  }

  private void removeNode(ScoreNode node) {
    if (node.previous == null) {
      head = node.next;
    } else {
      node.previous.next = node.next;
    }

    if (node.next == null) {
      tail = node.previous;
    } else {
      node.next.previous = node.previous;
    }

    node.next = null;
    node.previous = null;
  }

  private void trimTail() {
    if (tail == null) {
      return;
    }

    String normalizedKey = normalizeKey(tail.value.playerName());
    removeNode(tail);
    nodesByPlayer.remove(normalizedKey);
    count--;
  }

  private static final class ScoreNode {

    private final PlayerScore value;
    private ScoreNode previous;
    private ScoreNode next;

    private ScoreNode(PlayerScore value) {
      this.value = value;
    }
  }
}

