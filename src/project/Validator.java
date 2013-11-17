package project;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;

import project.entity.Sample;
import project.mean.IMeanAlgorithm;
import project.mean.MeanAlgorithmFactory;

public class Validator {

    public static void main(String[] args) throws IOException {
	if (args.length != 3) {
	    throw new IllegalArgumentException(
		    "Wrong use. Please call the program specifying a directory containing the .list files.");
	}

	// Import *.list files
	File[] sampleFiles = new File(args[0]).listFiles();

	// linha abaixo apenas para facilitar testes. rewmover depois!
	// sampleFiles = Arrays.copyOfRange(sampleFiles, 0, 100);

	System.out
		.println("Valitation toolbox, based on its initial version from Pascal Kelm, for the Placing Task at MediaEval.\n");

	GroundTruthResolver groundTruthResolver = GroundTruthLoader
		.loadGroundTruth();

	int numberOfElementsUsed = Integer.valueOf(args[1]);

	IMeanAlgorithm meanAlgorithm = MeanAlgorithmFactory
		.createMeanAlgorithm(Configs.MeanAlgorithm.valueOf(args[2]));

	Map<Double, AtomicInteger> countsByThreshold = new TreeMap<Double, AtomicInteger>();

	for (File file : sampleFiles) {
	    Sample mediaSample = SampleLoader.readMediaSample(file,
		    groundTruthResolver, numberOfElementsUsed);

	    mediaSample.estimateLocation(meanAlgorithm);
	    double havesineDistance = mediaSample.calcHavesineDistance();

	    Double thresholdMatch = findThresholdMatch(havesineDistance);

	    AtomicInteger thresholdCount = countsByThreshold
		    .get(thresholdMatch);
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

    private static void writeReportCountsByThreshold(int samplesCount,
	    Map<Double, AtomicInteger> countsByThreshold) throws IOException {
	File fileListCompact = new File(Configs.FILENAME_THRESHOLD_COUNT);
	BufferedWriter bwCompact = new BufferedWriter(new FileWriter(
		fileListCompact));
	bwCompact.write("Threshold \t Count \t Percentage");
	bwCompact.newLine();
	for (Double threshold : Configs.thresholdsList) {
	    AtomicInteger count = countsByThreshold.get(threshold);
	    int countThreshold = count == null ? 0 : count.get();
	    bwCompact.write(threshold + "\t " + countThreshold + " \t "
		    + (countThreshold * 100.0) / samplesCount);
	    bwCompact.newLine();
	}
	bwCompact.close();
    }
}
