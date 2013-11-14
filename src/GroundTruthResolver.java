import java.util.Map;

public class GroundTruthResolver {

    private Map<String, Position> groundTruth;

    public GroundTruthResolver(Map<String, Position> groundTruth) {
        this.groundTruth = groundTruth;
    }

    public Position get(String sampleIdentifier) {
        return groundTruth.get(sampleIdentifier);
    }

}
