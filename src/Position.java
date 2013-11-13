
class Position {
    private int identifier;
    private double latitude;
    private double longitude;

    public Position(int fileName, double latitude, double longitude) {
        this.identifier = fileName;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public int getIdentifier() {
        return identifier;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    @Override
    public String toString() {
        return new StringBuilder("fileName=").append(identifier).append(", latitude=").append(latitude).append(", longitude=").append(longitude).toString();
    }
}