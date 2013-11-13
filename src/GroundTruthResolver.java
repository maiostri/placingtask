import java.util.Map;

public class GroundTruthResolver {

    private Map<Integer, Position> groundTruth;

    public GroundTruthResolver(Map<Integer, Position> groundTruth) {
        this.groundTruth = groundTruth;
    }

    public Position get(Integer sampleIdentifier) {
        return groundTruth.get(sampleIdentifier);
    }

}
