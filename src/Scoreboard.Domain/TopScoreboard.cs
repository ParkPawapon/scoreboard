namespace Scoreboard.Domain;

public sealed class TopScoreboard : IScoreboard
{
    private const int MaxSupportedCapacity = 10;

    private readonly Dictionary<string, ScoreNode> _nodesByPlayer = new(StringComparer.OrdinalIgnoreCase);

    private ScoreNode? _head;
    private ScoreNode? _tail;
    private int _count;

    public TopScoreboard(int capacity = MaxSupportedCapacity)
    {
        if (capacity <= 0 || capacity > MaxSupportedCapacity)
        {
            throw new ArgumentOutOfRangeException(
                nameof(capacity),
                $"Capacity must be between 1 and {MaxSupportedCapacity}.");
        }

        Capacity = capacity;
    }

    public int Capacity { get; }

    public int Count => _count;

    public bool UpsertScore(string playerName, int score)
    {
        var candidate = new PlayerScore(playerName, score);

        if (_nodesByPlayer.TryGetValue(candidate.PlayerName, out ScoreNode? existingNode))
        {
            RemoveNode(existingNode);
            _nodesByPlayer.Remove(candidate.PlayerName);
            _count--;
        }

        if (_count == Capacity && _tail is not null && CompareByRank(candidate, _tail.Value) > 0)
        {
            return false;
        }

        var newNode = new ScoreNode(candidate);
        InsertNodeByRank(newNode);
        _nodesByPlayer[candidate.PlayerName] = newNode;
        _count++;

        if (_count > Capacity)
        {
            TrimTail();
        }

        return _nodesByPlayer.ContainsKey(candidate.PlayerName);
    }

    public bool Remove(string playerName)
    {
        ArgumentException.ThrowIfNullOrWhiteSpace(playerName);

        var key = playerName.Trim();

        if (!_nodesByPlayer.TryGetValue(key, out ScoreNode? node))
        {
            return false;
        }

        RemoveNode(node);
        _nodesByPlayer.Remove(key);
        _count--;
        return true;
    }

    public IReadOnlyList<PlayerScore> GetScores()
    {
        var results = new List<PlayerScore>(_count);
        ScoreNode? current = _head;

        while (current is not null)
        {
            results.Add(current.Value);
            current = current.Next;
        }

        return results;
    }

    private static int CompareByRank(in PlayerScore left, in PlayerScore right)
    {
        int scoreComparison = right.Score.CompareTo(left.Score);
        if (scoreComparison != 0)
        {
            return scoreComparison;
        }

        return StringComparer.OrdinalIgnoreCase.Compare(left.PlayerName, right.PlayerName);
    }

    private void InsertNodeByRank(ScoreNode node)
    {
        if (_head is null)
        {
            _head = node;
            _tail = node;
            return;
        }

        ScoreNode? current = _head;
        while (current is not null && CompareByRank(node.Value, current.Value) >= 0)
        {
            current = current.Next;
        }

        if (current is null)
        {
            AppendToTail(node);
            return;
        }

        InsertBefore(current, node);
    }

    private void AppendToTail(ScoreNode node)
    {
        if (_tail is null)
        {
            _head = node;
            _tail = node;
            return;
        }

        node.Previous = _tail;
        _tail.Next = node;
        _tail = node;
    }

    private void InsertBefore(ScoreNode target, ScoreNode node)
    {
        node.Next = target;
        node.Previous = target.Previous;

        if (target.Previous is null)
        {
            _head = node;
        }
        else
        {
            target.Previous.Next = node;
        }

        target.Previous = node;
    }

    private void RemoveNode(ScoreNode node)
    {
        if (node.Previous is null)
        {
            _head = node.Next;
        }
        else
        {
            node.Previous.Next = node.Next;
        }

        if (node.Next is null)
        {
            _tail = node.Previous;
        }
        else
        {
            node.Next.Previous = node.Previous;
        }

        node.Next = null;
        node.Previous = null;
    }

    private void TrimTail()
    {
        if (_tail is null)
        {
            return;
        }

        string playerName = _tail.Value.PlayerName;
        RemoveNode(_tail);
        _nodesByPlayer.Remove(playerName);
        _count--;
    }

    private sealed class ScoreNode
    {
        public ScoreNode(PlayerScore value)
        {
            Value = value;
        }

        public PlayerScore Value { get; }

        public ScoreNode? Previous { get; set; }

        public ScoreNode? Next { get; set; }
    }
}

