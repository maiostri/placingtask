import java.util.ArrayList;
import java.util.List;

class Rang {
    private double threshold;
    private List<MediaSample> mediaSamples = new ArrayList<MediaSample>();

    public double getThreshold() {
        return threshold;
    }

    public void setThreshold(double threshold) {
        this.threshold = threshold;
    }

    public List<MediaSample> getMediaSamples() {
        return mediaSamples;
    }

    public void addMediaSample(MediaSample mediaSample) {
        mediaSamples.add(mediaSample);
    }

    public int getCount() {
        return mediaSamples.size();
    }
}