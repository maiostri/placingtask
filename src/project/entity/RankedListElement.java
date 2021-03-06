package project.entity;

import project.MathUtils;

public class RankedListElement {
    private String identifier;
    private Location groundTruth;
    private double similarityFactor;

    public RankedListElement(String identifier, Location groundTruth, double similarityFactor) {
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

    /**
     * converts from similarity to distance and vice-versa
     */
    public void convertSimilarityFactor() {
        similarityFactor = MathUtils.round(1 - similarityFactor, 6);
    }
}