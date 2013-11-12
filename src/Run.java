
public class Run {

    private Integer fileName;
    private double latitude;
    private double longitude;
    private double havesineDistance;
    private double similarityFactor;
    private Position groundTruth;

    public Integer getFileName() {
        return fileName;
    }

    public void setFileName(Integer fileName) {
        this.fileName = fileName;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
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