package project.entity;
import java.util.ArrayList;
import java.util.List;

public class ThresholdCounter {
    private double threshold;
    private List<Sample> samples = new ArrayList<Sample>();

    public double getThreshold() {
        return threshold;
    }

    public void setThreshold(double threshold) {
        this.threshold = threshold;
    }

    public List<Sample> getSamples() {
        return samples;
    }

    public void addMediaSample(Sample mediaSample) {
        samples.add(mediaSample);
    }

    public int getCount() {
        return samples.size();
    }
}