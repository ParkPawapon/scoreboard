namespace Scoreboard.Domain;

public static class BreakEvenCalculator
{
    public static double CalculateBreakEvenNodeCount(
        int arrayElementCount,
        int elementSizeBytes,
        int pointerSizeBytes,
        int pointersPerNode)
    {
        if (arrayElementCount <= 0)
        {
            throw new ArgumentOutOfRangeException(
                nameof(arrayElementCount),
                "Array element count must be greater than zero.");
        }

        if (elementSizeBytes <= 0)
        {
            throw new ArgumentOutOfRangeException(
                nameof(elementSizeBytes),
                "Element size must be greater than zero.");
        }

        if (pointerSizeBytes <= 0)
        {
            throw new ArgumentOutOfRangeException(
                nameof(pointerSizeBytes),
                "Pointer size must be greater than zero.");
        }

        if (pointersPerNode <= 0)
        {
            throw new ArgumentOutOfRangeException(
                nameof(pointersPerNode),
                "Pointers per node must be greater than zero.");
        }

        double arrayBytes = arrayElementCount * (double)elementSizeBytes;
        double nodeBytes = elementSizeBytes + (pointerSizeBytes * pointersPerNode);
        return arrayBytes / nodeBytes;
    }

    public static double ConvertSingly32ToDoubly64(double singlyBreakEvenPoint, int elementSizeBytes)
    {
        if (singlyBreakEvenPoint <= 0)
        {
            throw new ArgumentOutOfRangeException(
                nameof(singlyBreakEvenPoint),
                "Break-even point must be greater than zero.");
        }

        if (elementSizeBytes <= 0)
        {
            throw new ArgumentOutOfRangeException(
                nameof(elementSizeBytes),
                "Element size must be greater than zero.");
        }

        return singlyBreakEvenPoint * (elementSizeBytes + 4d) / (elementSizeBytes + 16d);
    }
}

