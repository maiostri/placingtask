package project;
import java.util.Map;
import project.entity.Location;

public class GroundTruthResolver {

    private Map<String, Location> groundTruth;

    public GroundTruthResolver(Map<String, Location> groundTruth) {
        this.groundTruth = groundTruth;
    }

    public Location get(String sampleIdentifier) {
        return groundTruth.get(sampleIdentifier);
    }
}
