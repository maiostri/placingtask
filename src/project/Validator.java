package project;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;
import project.Configs.MeanAlgorithm;
import project.entity.Sample;
import project.mean.IMeanAlgorithm;
import project.mean.MeanAlgorithmFactory;

/**
 * Valitation toolbox, based on its initial version from Pascal Kelm, for the Placing Task at MediaEval
 */
public class Validator {

    private final File sampleDir;
    private final int numberOfElementsUsed;
    private final MeanAlgorithm meanAlgorithmType;

    public Validator(File sampleDir, int numberOfElementsUsed, MeanAlgorithm meanAlgorithmType) {
        this.sampleDir = sampleDir;
        this.numberOfElementsUsed = numberOfElementsUsed;
        this.meanAlgorithmType = meanAlgorithmType;
    }

    public void run() throws IOException {
        File[] sampleFiles = sampleDir.listFiles();
        IMeanAlgorithm meanAlgorithm = MeanAlgorithmFactory.createMeanAlgorithm(meanAlgorithmType);

        // linha abaixo apenas para facilitar testes. rewmover depois!
        //sampleFiles = Arrays.copyOfRange(sampleFiles, 0, 10);

        System.out.println("Executing to: folder=" + sampleDir + ", K=" + numberOfElementsUsed + ", mean type=" + meanAlgorithmType + "...\n");

        GroundTruthResolver groundTruthResolver = GroundTruthLoader.loadGroundTruth();

        Map<Double, AtomicInteger> countsByThreshold = new TreeMap<Double, AtomicInteger>();

        for (File file : sampleFiles) {
            Sample mediaSample = SampleLoader.readMediaSample(file, groundTruthResolver, numberOfElementsUsed);

            mediaSample.estimateLocation(meanAlgorithm, numberOfElementsUsed);
            double havesineDistance = mediaSample.calcHavesineDistance();

            Double thresholdMatch = findThresholdMatch(havesineDistance);

            AtomicInteger thresholdCount = countsByThreshold.get(thresholdMatch);
            if (thresholdCount != null) {
                thresholdCount.incrementAndGet();
            } else {
                countsByThreshold.put(thresholdMatch, new AtomicInteger(1));
            }
        }

        // Save Results
        writeReportCountsByThreshold(sampleFiles.length, countsByThreshold);
    }

    private static Double findThresholdMatch(double havesineDistance) {
        Double thresholdMatch = null;
        for (Double threshold : Configs.thresholdsList) {
            if (havesineDistance < threshold) {
                thresholdMatch = threshold;
                break;
            }
        }
        return thresholdMatch;
    }

    private void writeReportCountsByThreshold(int samplesCount, Map<Double, AtomicInteger> countsByThreshold) throws IOException {
        File fileListCompact = new File(Configs.FILENAME_THRESHOLD_COUNT + "_" + sampleDir.getName()
            + "_K" + numberOfElementsUsed + "_" + meanAlgorithmType.name() + ".txt");

        PrintStream bwCompact = new PrintStream(fileListCompact);
        bwCompact.printf("%10s %10s %15s", "Threshold", "Count", "Percentage");
        bwCompact.println();
        for (Double threshold : Configs.thresholdsList) {
            AtomicInteger count = countsByThreshold.get(threshold);
            int countThreshold = count == null ? 0 : count.get();
            bwCompact.printf("%10.0f %10d %15.2f", threshold, countThreshold, (countThreshold * 100.0) / samplesCount);
            bwCompact.println();
        }
        bwCompact.close();
    }

    public static void main(String[] args) throws IOException {
        List<Validator> validators = new ArrayList<Validator>();

        if (args.length >= 1) {
            File sampleDir = new File(args[0]);
            int numberOfElementsUsed = 1;
            MeanAlgorithm meanAlgorithmType = MeanAlgorithm.ARITHMETIC_MEAN;
            if (args.length >= 2) {
                numberOfElementsUsed = Integer.valueOf(args[1]);
                if (args.length >= 3) {
                    meanAlgorithmType = Configs.MeanAlgorithm.valueOf(args[2]);
                }
            }
            validators.add(new Validator(sampleDir, numberOfElementsUsed, meanAlgorithmType));
        } else {
            System.err.println("Invalid Use. Some pre-defined profiles will be used instead."
                + " Please call the program specifying: a directory containing the .list files"
                + ", [optional default 1] the K number of KNN elements to compute the estimated locations"
                + ", [optional default ARITHMETIC_MEAN] and the name of the mean algorithm to use (ARITHMETIC_MEAN or WEIGHTED_ARITHMETIC_MEAN)");


            File devSampleDir = new File("C:/MO633_projeto/data/visual/MediaEval2012_JurandyLists/FlickrVideosTrain");
            validators.add(new Validator(devSampleDir, 1, MeanAlgorithm.ARITHMETIC_MEAN));
            validators.add(new Validator(devSampleDir, 2, MeanAlgorithm.ARITHMETIC_MEAN));
            validators.add(new Validator(devSampleDir, 2, MeanAlgorithm.WEIGHTED_ARITHMETIC_MEAN));
            validators.add(new Validator(devSampleDir, 5, MeanAlgorithm.ARITHMETIC_MEAN));
            validators.add(new Validator(devSampleDir, 5, MeanAlgorithm.WEIGHTED_ARITHMETIC_MEAN));
            validators.add(new Validator(devSampleDir, 10, MeanAlgorithm.ARITHMETIC_MEAN));
            validators.add(new Validator(devSampleDir, 10, MeanAlgorithm.WEIGHTED_ARITHMETIC_MEAN));

            File testSampleDir = new File("C:/MO633_projeto/data/visual/MediaEval2012_JurandyLists/VideosPlacingTask");
            validators.add(new Validator(testSampleDir, 1, MeanAlgorithm.ARITHMETIC_MEAN));
            validators.add(new Validator(testSampleDir, 2, MeanAlgorithm.ARITHMETIC_MEAN));
            validators.add(new Validator(testSampleDir, 2, MeanAlgorithm.WEIGHTED_ARITHMETIC_MEAN));
            validators.add(new Validator(testSampleDir, 5, MeanAlgorithm.ARITHMETIC_MEAN));
            validators.add(new Validator(testSampleDir, 5, MeanAlgorithm.WEIGHTED_ARITHMETIC_MEAN));
            validators.add(new Validator(testSampleDir, 10, MeanAlgorithm.ARITHMETIC_MEAN));
            validators.add(new Validator(testSampleDir, 10, MeanAlgorithm.WEIGHTED_ARITHMETIC_MEAN));
        }

        for (Validator validator : validators) {
            validator.run();
        }
    }
}
