package KNN;

public class SampleData

{
    private final double []data;
    private final String identifier;

    public SampleData(String identifier, double[] data)
    {
        this.data = data;
        this.identifier = identifier;
    }

    public double[] getData() {
        return data;
    }

    public String getIdentifier() {
        return identifier;
    }
}