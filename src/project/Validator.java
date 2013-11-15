package project;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;
import project.entity.Location;
import project.entity.RankedListElement;
import project.entity.Sample;

public class Validator {

    public static void main(String[] args) throws IOException {
        if(args.length == 0){
            throw new IllegalArgumentException("Wrong use. Please call the program specifying a directory containing the .list files.");
        }

        // Import *.list files
        File[] sampleFiles = new File(args[0]).listFiles();

        //linha abaixo apenas para facilitar testes. rewmover depois!
        //sampleFiles = Arrays.copyOfRange(sampleFiles, 0, 100);

        System.out.println("Valitation toolbox, based on its initial version from Pascal Kelm, for the Placing Task at MediaEval.\n");

        GroundTruthResolver groundTruthResolver = loadGroundTruth();

        Map<Double, AtomicInteger> countsByThreshold = new TreeMap<Double, AtomicInteger>();

        for (File file : sampleFiles) {
            Sample mediaSample = readMediaSample(file, groundTruthResolver);

            mediaSample.estimateLocation();
            double havesineDistance = mediaSample.calcHavesineDistance();

            Double thresholdMatch = findThresholdMatch(havesineDistance);

            AtomicInteger thresholdCount = countsByThreshold.get(thresholdMatch);
            if (thresholdCount != null) {
                thresholdCount.incrementAndGet();
            }else{
                countsByThreshold.put(thresholdMatch, new AtomicInteger(1));
            }
        }

        System.out.println(" Done.");

        /*****************
         * Save Results
         *****************/
        writeFinalListCompact(sampleFiles.length, countsByThreshold);

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

    private static GroundTruthResolver loadGroundTruth() throws IOException {
        System.out.print("Import ground truth... ");
        Map<String, Location> groundTruthFromTestSamples = loadGroundTruthFromTestSamples();
        System.out.println("Done ground truth.");
        int size1 = groundTruthFromTestSamples.size();

        System.out.print("Import training file... ");
        Map<String, Location> groundTruthFromDevSamples = loadGroundTruthFromDevSamples();
        System.out.println("Done training.");
        int size2 = groundTruthFromDevSamples.size();

        groundTruthFromTestSamples.putAll(groundTruthFromDevSamples);
        if (groundTruthFromTestSamples.size() < size1 + size2) {
            throw new IllegalStateException("the ground truth files seemed to have repeated entries");
        }

        return new GroundTruthResolver(groundTruthFromTestSamples);
    }

    private static Map<String, Location> loadGroundTruthFromTestSamples() throws IOException {
        Map<String, Location> idPositionTestMap = new LinkedHashMap<String, Location>();

        String s = null;
        BufferedReader in = new BufferedReader(new FileReader(Configs.FILENAME_GROUND_TRUTH_TEST_SAMPLES));

        while ((s = in.readLine()) != null) {
            String[] arg = s.split(";");
            idPositionTestMap.put(arg[0], new Location(Double.parseDouble(arg[1]), Double.parseDouble(arg[2])));
        }
        in.close();

        return idPositionTestMap;
    }

    private static Map<String, Location> loadGroundTruthFromDevSamples() throws IOException {
        Map<String, Location> idPositionDVLPMap = new LinkedHashMap<String, Location>();

        String s = null;
        BufferedReader in = new BufferedReader(new FileReader(Configs.FILENAME_GROUND_TRUTH_DEV_SAMPLES));
        while ((s = in.readLine()) != null) {
            String[] arg = s.split(";");
            idPositionDVLPMap.put(arg[0], new Location(Double.parseDouble(arg[1]), Double.parseDouble(arg[2])));
        }
        in.close();

        return idPositionDVLPMap;
    }

    private static Sample readMediaSample(File sampleFile, GroundTruthResolver groundTruthResolver) throws IOException {
        String sampleIdentifier = readSampleIdentifierFromFilename(sampleFile.getName());

        System.out.println(sampleIdentifier);

        Location sampleGroundTruth = groundTruthResolver.get(sampleIdentifier);

        Sample mediaSample = new Sample(sampleIdentifier, sampleGroundTruth);

        String s = null;
        BufferedReader in = new BufferedReader(new FileReader(sampleFile));
        while ((s = in.readLine()) != null) {
            String[] s_args = s.trim().split("\\s++");

            String runIdentifier = readSampleIdentifierFromDevSampleName(s_args[1]);
            Location runGroundTruth = groundTruthResolver.get(runIdentifier);

            RankedListElement run = new RankedListElement(runIdentifier, runGroundTruth, Double.parseDouble(s_args[0].replace(",", ".")));

            mediaSample.addSimilarityElement(run);
        }
        in.close();

        return mediaSample;
    }

    private static String readSampleIdentifierFromFilename(String name) {
        return name.replace("_list.txt", "");
    }

    private static String readSampleIdentifierFromDevSampleName(String devSampleName) {
        return devSampleName.replace("FlickrVideosTrain/", "");
    }

    private static void writeFinalListCompact(int samplesCount, Map<Double, AtomicInteger> countsByThreshold) throws IOException {
        File fileListCompact = new File(Configs.FILENAME_THRESHOLD_COUNT);
        BufferedWriter bwCompact = new BufferedWriter(new FileWriter(fileListCompact));
        bwCompact.write("Threshold \t Count \t Percentage");
        bwCompact.newLine();
        for (Double threshold : Configs.thresholdsList) {
            AtomicInteger count = countsByThreshold.get(threshold);
            int countThreshold = count == null ? 0 : count.get();
            bwCompact.write(threshold + "\t " + countThreshold + " \t " + (countThreshold * 100.0) / samplesCount);
            bwCompact.newLine();
        }
        bwCompact.close();
    }
}
