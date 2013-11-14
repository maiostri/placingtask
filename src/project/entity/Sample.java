package project.entity;
import java.util.ArrayList;
import java.util.List;
import project.MathUtils;

public class Sample {
    private String identifier;
    private Location location;

    private List<RankedListElement> similarityList;
    private Location estimatedLocation;

    public Sample(String identifier, Location location) {
        this.identifier = identifier;
        this.location = location;
        this.similarityList = new ArrayList<RankedListElement>();
    }

    public String getIdentifier() {
        return identifier;
    }

    public Location getLocation() {
        return location;
    }

    public List<RankedListElement> getSimilarityList() {
        return similarityList;
    }

    public void addSimilarityElement(RankedListElement run) {
        similarityList.add(run);
    }

    public void estimateLocation() {
        // Simple KNN with N = 1.
        estimatedLocation = similarityList.get(0).getGroundTruth();
    }

    public Location getEstimatedLocation() {
        return estimatedLocation;
    }

    public double calcHavesineDistance() {
        return MathUtils.calcHaversineDistance(estimatedLocation, location);
    }

    @Override
    public String toString() {
        return new StringBuilder("fileName=").append(identifier).append(", ").append(location).toString();
    }
}