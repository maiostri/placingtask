package project.entity;

public class Run {
    private String identifier;
    private Location groundTruth;
    private double similarityFactor;

    public Run(String identifier, Location groundTruth, double similarityFactor) {
        this.identifier = identifier;
        this.groundTruth = groundTruth;
        this.similarityFactor = similarityFactor;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public double getSimilarityFactor() {
        return similarityFactor;
    }

    public void setSimilarityFactor(double similarityFactor) {
        this.similarityFactor = similarityFactor;
    }

    public Location getGroundTruth() {
        return groundTruth;
    }

    public void setGroundTruth(Location groundTruth) {
        this.groundTruth = groundTruth;
    }
}