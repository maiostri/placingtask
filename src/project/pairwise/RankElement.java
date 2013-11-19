package project.pairwise;

/**
 *
 * @author Jonas Henrique
 */
public class RankElement
{
    private float  similarity;
    private String trainElement;

    public RankElement(float similarity, String name)
    {
        this.similarity = similarity;
        trainElement = name;
    }

    public float getSimilarity()
    {
        return similarity;
    }

    public String getTrainElementName()
    {
        return trainElement;
    }
}
