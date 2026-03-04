using Scoreboard.Domain;

var scoreboard = new TopScoreboard(capacity: 4);

scoreboard.UpsertScore("Ryu", 100);
scoreboard.UpsertScore("Ken", 98);
scoreboard.UpsertScore("Chunli", 95);
scoreboard.UpsertScore("Sagat", 94);

PrintBoard("Initial board", scoreboard.GetScores());

scoreboard.UpsertScore("Vega", 97);
PrintBoard("After Vega got new score = 97", scoreboard.GetScores());

double singly32 = BreakEvenCalculator.CalculateBreakEvenNodeCount(
    arrayElementCount: 100,
    elementSizeBytes: 16,
    pointerSizeBytes: 4,
    pointersPerNode: 1);

double doubly64 = BreakEvenCalculator.ConvertSingly32ToDoubly64(
    singlyBreakEvenPoint: singly32,
    elementSizeBytes: 16);

Console.WriteLine();
Console.WriteLine($"Break-even (singly, 32-bit): {singly32:F2}");
Console.WriteLine($"Break-even (doubly, 64-bit): {doubly64:F2}");

static void PrintBoard(string title, IReadOnlyList<PlayerScore> scores)
{
    Console.WriteLine(title);
    foreach (PlayerScore score in scores)
    {
        Console.WriteLine($"{score.PlayerName,-10} {score.Score}");
    }

    Console.WriteLine();
}
