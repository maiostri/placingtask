package project;

import java.util.Arrays;
import java.util.List;

public class Configs {
    public static final String FILENAME_GROUND_TRUTH_DEV_SAMPLES = "MediaEval2012DvlpLatLong.txt";
    public static final String FILENAME_GROUND_TRUTH_TEST_SAMPLES = "pt2012.txt";

    public static final List<Double> thresholdsList = Arrays.asList(
        1.0, 10.0, 20.0, 50.0, 100.0, 200.0, 500.0, 1000.0, 15000.0, 20000.0);

    public static final String FILENAME_THRESHOLD_COUNT = "thresholdCount";

    public enum MeanAlgorithm {
        ARITHMETIC_MEAN, WEIGHTED_ARITHMETIC_MEAN;
    }
}
