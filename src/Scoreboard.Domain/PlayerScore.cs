namespace Scoreboard.Domain;

public readonly record struct PlayerScore
{
    public PlayerScore(string playerName, int score)
    {
        ArgumentException.ThrowIfNullOrWhiteSpace(playerName);

        if (score < 0)
        {
            throw new ArgumentOutOfRangeException(nameof(score), "Score must be greater than or equal to zero.");
        }

        PlayerName = playerName.Trim();
        Score = score;
    }

    public string PlayerName { get; }

    public int Score { get; }
}

