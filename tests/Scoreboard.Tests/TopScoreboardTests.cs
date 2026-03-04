using Scoreboard.Domain;

namespace Scoreboard.Tests;

public sealed class TopScoreboardTests
{
    [Fact]
    public void UpsertScore_ShouldKeepScoresSortedDescending()
    {
        var board = new TopScoreboard(capacity: 4);

        board.UpsertScore("Sagat", 94);
        board.UpsertScore("Ryu", 100);
        board.UpsertScore("Chunli", 95);
        board.UpsertScore("Ken", 98);

        IReadOnlyList<PlayerScore> scores = board.GetScores();

        Assert.Collection(
            scores,
            score => Assert.Equal(new PlayerScore("Ryu", 100), score),
            score => Assert.Equal(new PlayerScore("Ken", 98), score),
            score => Assert.Equal(new PlayerScore("Chunli", 95), score),
            score => Assert.Equal(new PlayerScore("Sagat", 94), score));
    }

    [Fact]
    public void UpsertScore_WhenBoardIsFull_ShouldTrimTail()
    {
        var board = new TopScoreboard(capacity: 4);
        SeedSampleBoard(board);

        bool isOnBoard = board.UpsertScore("Vega", 97);

        Assert.True(isOnBoard);
        Assert.Equal(4, board.Count);

        IReadOnlyList<PlayerScore> scores = board.GetScores();
        Assert.Collection(
            scores,
            score => Assert.Equal(new PlayerScore("Ryu", 100), score),
            score => Assert.Equal(new PlayerScore("Ken", 98), score),
            score => Assert.Equal(new PlayerScore("Vega", 97), score),
            score => Assert.Equal(new PlayerScore("Chunli", 95), score));
    }

    [Fact]
    public void UpsertScore_WhenFullAndScoreIsTooLow_ShouldReject()
    {
        var board = new TopScoreboard(capacity: 4);
        SeedSampleBoard(board);

        bool isOnBoard = board.UpsertScore("Guile", 90);

        Assert.False(isOnBoard);
        Assert.Equal(4, board.Count);
        Assert.DoesNotContain(board.GetScores(), score => score.PlayerName == "Guile");
    }

    [Fact]
    public void UpsertScore_WhenPlayerAlreadyExists_ShouldRepositionWithoutChangingCount()
    {
        var board = new TopScoreboard(capacity: 4);
        SeedSampleBoard(board);

        bool isOnBoard = board.UpsertScore("Ken", 93);

        Assert.True(isOnBoard);
        Assert.Equal(4, board.Count);

        IReadOnlyList<PlayerScore> scores = board.GetScores();
        Assert.Collection(
            scores,
            score => Assert.Equal(new PlayerScore("Ryu", 100), score),
            score => Assert.Equal(new PlayerScore("Chunli", 95), score),
            score => Assert.Equal(new PlayerScore("Sagat", 94), score),
            score => Assert.Equal(new PlayerScore("Ken", 93), score));
    }

    [Fact]
    public void Remove_ShouldDeleteExistingPlayer()
    {
        var board = new TopScoreboard(capacity: 4);
        SeedSampleBoard(board);

        bool removed = board.Remove("Sagat");

        Assert.True(removed);
        Assert.Equal(3, board.Count);
        Assert.DoesNotContain(board.GetScores(), score => score.PlayerName == "Sagat");
    }

    [Fact]
    public void Constructor_WithInvalidCapacity_ShouldThrow()
    {
        Assert.Throws<ArgumentOutOfRangeException>(() => new TopScoreboard(0));
        Assert.Throws<ArgumentOutOfRangeException>(() => new TopScoreboard(11));
    }

    [Fact]
    public void Remove_WhenPlayerIsMissing_ShouldReturnFalse()
    {
        var board = new TopScoreboard(capacity: 4);
        SeedSampleBoard(board);

        bool removed = board.Remove("Dhalsim");

        Assert.False(removed);
        Assert.Equal(4, board.Count);
    }

    [Fact]
    public void Remove_WithInvalidPlayerName_ShouldThrow()
    {
        var board = new TopScoreboard(capacity: 4);

        Assert.Throws<ArgumentException>(() => board.Remove(" "));
    }

    [Fact]
    public void UpsertScore_WithInvalidData_ShouldThrow()
    {
        var board = new TopScoreboard(capacity: 4);

        Assert.Throws<ArgumentException>(() => board.UpsertScore(" ", 10));
        Assert.Throws<ArgumentOutOfRangeException>(() => board.UpsertScore("Ryu", -1));
    }

    [Fact]
    public void TieScore_ShouldUseNameAsDeterministicTiebreaker()
    {
        var board = new TopScoreboard(capacity: 4);

        board.UpsertScore("Vega", 97);
        board.UpsertScore("Bison", 97);

        IReadOnlyList<PlayerScore> scores = board.GetScores();
        Assert.Collection(
            scores,
            score => Assert.Equal(new PlayerScore("Bison", 97), score),
            score => Assert.Equal(new PlayerScore("Vega", 97), score));
    }

    [Fact]
    public void ConvertSingly32ToDoubly64_ShouldMatchDirectCalculation()
    {
        const int elementSize = 16;
        const int arrayElementCount = 100;

        double singly32 = BreakEvenCalculator.CalculateBreakEvenNodeCount(
            arrayElementCount,
            elementSize,
            pointerSizeBytes: 4,
            pointersPerNode: 1);

        double doubly64FromRelation = BreakEvenCalculator.ConvertSingly32ToDoubly64(singly32, elementSize);

        double doubly64Direct = BreakEvenCalculator.CalculateBreakEvenNodeCount(
            arrayElementCount,
            elementSize,
            pointerSizeBytes: 8,
            pointersPerNode: 2);

        Assert.Equal(doubly64Direct, doubly64FromRelation, precision: 10);
    }

    [Fact]
    public void CalculateBreakEvenNodeCount_WithInvalidArguments_ShouldThrow()
    {
        Assert.Throws<ArgumentOutOfRangeException>(() =>
            BreakEvenCalculator.CalculateBreakEvenNodeCount(0, 16, 4, 1));

        Assert.Throws<ArgumentOutOfRangeException>(() =>
            BreakEvenCalculator.CalculateBreakEvenNodeCount(100, 0, 4, 1));

        Assert.Throws<ArgumentOutOfRangeException>(() =>
            BreakEvenCalculator.CalculateBreakEvenNodeCount(100, 16, 0, 1));

        Assert.Throws<ArgumentOutOfRangeException>(() =>
            BreakEvenCalculator.CalculateBreakEvenNodeCount(100, 16, 4, 0));
    }

    [Fact]
    public void ConvertSingly32ToDoubly64_WithInvalidArguments_ShouldThrow()
    {
        Assert.Throws<ArgumentOutOfRangeException>(() =>
            BreakEvenCalculator.ConvertSingly32ToDoubly64(0, 16));

        Assert.Throws<ArgumentOutOfRangeException>(() =>
            BreakEvenCalculator.ConvertSingly32ToDoubly64(100, 0));
    }

    private static void SeedSampleBoard(TopScoreboard board)
    {
        board.UpsertScore("Ryu", 100);
        board.UpsertScore("Ken", 98);
        board.UpsertScore("Chunli", 95);
        board.UpsertScore("Sagat", 94);
    }
}
