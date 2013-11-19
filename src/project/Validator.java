package project;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;
import project.Configs.MeanAlgorithm;
import project.entity.Sample;
import project.mean.IMeanAlgorithm;
import project.mean.MeanAlgorithmFactory;

public class Validator {

    public static void main(String[] args) throws IOException {
        File[] sampleFiles; // the *.list files
        int numberOfElementsUsed = 1;
        MeanAlgorithm meanAlgorithmType = MeanAlgorithm.ARITHMETIC_MEAN;

        if (args.length < 1) {
            throw new IllegalArgumentException("Wrong use. Please call the program specifying: a directory containing the .list files"
                + ", [optional default 1] the K number of KNN elements to compute the estimated locations"
                + ", [optional default ARITHMETIC_MEAN] and the name of the mean algorithm to use (ARITHMETIC_MEAN, WEIGHTED_ARITHMETIC_MEAN, or GEOMETRIC_MEAN)");
        }

        File folder = new File(args[0]);
        sampleFiles = folder.listFiles();
        if (args.length >= 2) {
            numberOfElementsUsed = Integer.valueOf(args[1]);
            if (args.length >= 3) {
                meanAlgorithmType = Configs.MeanAlgorithm.valueOf(args[2]);
            }
        }
        IMeanAlgorithm meanAlgorithm = MeanAlgorithmFactory.createMeanAlgorithm(meanAlgorithmType);

        // linha abaixo apenas para facilitar testes. rewmover depois!
        //sampleFiles = Arrays.copyOfRange(sampleFiles, 0, 10);

        System.out.println("Valitation toolbox, based on its initial version from Pascal Kelm, for the Placing Task at MediaEval.\n");
        System.out.println("Executing to folder=" + folder + ", K="+numberOfElementsUsed + ", mean type=" + meanAlgorithmType + "...\n");

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

        System.out.println(" Done.");

        /*****************
         * Save Results
         *****************/
        writeReportCountsByThreshold(sampleFiles.length, countsByThreshold);

        System.out.println(" Finish.");
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

    private static void writeReportCountsByThreshold(int samplesCount, Map<Double, AtomicInteger> countsByThreshold) throws IOException {
        File fileListCompact = new File(Configs.FILENAME_THRESHOLD_COUNT);
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
}
