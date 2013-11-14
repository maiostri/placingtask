
public class Run {

    private String identifier;
    private double similarityFactor;
    private Position groundTruth;
    private double havesineDistance;

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String fileName) {
        this.identifier = fileName;
    }

    public double getHavesineDistance() {
        return havesineDistance;
    }

    public void setHavesineDistance(double havesineDistance) {
        this.havesineDistance = havesineDistance;
    }

    public double getSimilarityFactor() {
        return similarityFactor;
    }

    public void setSimilarityFactor(double similarityFactor) {
        this.similarityFactor = similarityFactor;
    }

    public Position getGroundTruth() {
        return groundTruth;
    }

    public void setGroundTruth(Position groundTruth) {
        this.groundTruth = groundTruth;
    }
}