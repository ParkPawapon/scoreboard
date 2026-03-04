namespace Scoreboard.Domain;

public interface IScoreboard
{
    int Capacity { get; }

    int Count { get; }

    bool UpsertScore(string playerName, int score);

    bool Remove(string playerName);

    IReadOnlyList<PlayerScore> GetScores();
}

