
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ValidationPT {

    public static void main(String[] args) throws IOException {
        if(args.length == 0){
            throw new IllegalArgumentException("Wrong use. Please call the program specifying a directory containing the .list files.");
        }

        // Import *.list files
        File[] files = new File(args[0]).listFiles();

        //linha abaixo apenas para facilitar testes. rewmover depois!
        //files = Arrays.copyOfRange(files, 0, 500);

        System.out.println("Valitation toolbox, based on its initial version from Pascal Kelm, for the Placing Task at MediaEval.\n");

        GroundTruthResolver groundTruthResolver = loadGroundTruth();

        List<MediaSample> mediaSamples = new ArrayList<MediaSample>();

        for (File file : files) {
            MediaSample mediaSample = readMediaSample(file, groundTruthResolver);
            mediaSample.determineMediaAnswer();

            mediaSamples.add(mediaSample);
        }

        System.out.println(" Done.");

        /****************
         * Calc thresholds
         ****************/
        System.out.print("Calculating thresholds... ");
        Map<Double, Rang> list_range = new TreeMap<Double, Rang>();
        for (MediaSample mediaSample : mediaSamples) {

            Double thresholdMatch = null;

            for (Double threshold : Configs.thresholdsList) {
                if (mediaSample.getAnswerMedia().getHavesineDistance() < threshold) {
                    thresholdMatch = threshold;
                    break;
                }
            }

            Rang rang = list_range.get(thresholdMatch);
            if (rang == null) {
                rang = new Rang();
                rang.setThreshold(thresholdMatch);
                list_range.put(thresholdMatch, rang);
            }
            rang.addMediaSample(mediaSample);
        }

        /*****************
         * Save Results
         *****************/
        writeFinalList(list_range);
        writeFinalListCompact(mediaSamples, list_range);

        System.out.println(" Finish.");
    }

    private static GroundTruthResolver loadGroundTruth() throws IOException {
        System.out.print("Import ground truth... ");
        Map<String, Position> groundTruthFromTestSamples = loadGroundTruthFromTestSamples();
        System.out.println("Done ground truth.");
        int size1 = groundTruthFromTestSamples.size();

        System.out.print("Import training file... ");
        Map<String, Position> groundTruthFromDevSamples = loadGroundTruthFromDevSamples();
        System.out.println("Done training.");
        int size2 = groundTruthFromDevSamples.size();

        groundTruthFromTestSamples.putAll(groundTruthFromDevSamples);
        if (groundTruthFromTestSamples.size() < size1 + size2) {
            throw new IllegalStateException("the ground truth files seemed to have repeated entries");
        }

        return new GroundTruthResolver(groundTruthFromTestSamples);
    }

    private static Map<String, Position> loadGroundTruthFromTestSamples() throws IOException {
        Map<String, Position> idPositionTestMap = new LinkedHashMap<String, Position>();

        String s = null;
        BufferedReader in = new BufferedReader(new FileReader(Configs.FILENAME_GROUND_TRUTH_TEST_SAMPLES));

        while ((s = in.readLine()) != null) {
            String[] arg = s.split(";");
            Position temp_Pos = new Position(arg[0], Double.parseDouble(arg[1]), Double.parseDouble(arg[2]));
            idPositionTestMap.put(temp_Pos.getIdentifier(), temp_Pos);
        }
        in.close();

        return idPositionTestMap;
    }

    private static Map<String, Position> loadGroundTruthFromDevSamples() throws IOException {
        Map<String, Position> idPositionDVLPMap = new LinkedHashMap<String, Position>();

        String s = null;
        BufferedReader in = new BufferedReader(new FileReader(Configs.FILENAME_GROUND_TRUTH_DEV_SAMPLES));
        while ((s = in.readLine()) != null) {
            String[] arg = s.split(";");
            Position temp_Pos = new Position(arg[0], Double.parseDouble(arg[1]), Double.parseDouble(arg[2]));
            idPositionDVLPMap.put(temp_Pos.getIdentifier(), temp_Pos);
        }
        in.close();

        return idPositionDVLPMap;
    }

    private static MediaSample readMediaSample(File sampleFile, GroundTruthResolver groundTruthResolver) throws IOException {
        String sampleIdentifier = readSampleIdentifierFromFilename(sampleFile.getName());

        System.out.println(sampleIdentifier);

        MediaSample mediaSample = new MediaSample();
        mediaSample.setFileName(sampleFile.getName());

        Position sampleGroundTruth = groundTruthResolver.get(sampleIdentifier);

        String s = null;
        BufferedReader in = new BufferedReader(new FileReader(sampleFile));
        while ((s = in.readLine()) != null) {
            Run run = new Run();
            {
                String[] s_args = s.trim().split("\\s++");
                run.setSimilarityFactor(Double.parseDouble(s_args[0].replace(",", ".")));
                run.setIdentifier(readSampleIdentifierFromDevSampleName(s_args[1]));
            }

            Position runGroundTruth = groundTruthResolver.get(run.getIdentifier());

            run.setGroundTruth(runGroundTruth);
            run.setHavesineDistance(MathUtils.calcHaversineDistance(runGroundTruth, sampleGroundTruth));
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

    private static void writeFinalList(Map<Double, Rang> rangs) throws IOException {
        File fileList = new File(Configs.FILENAME_FINAL_LIST);
        BufferedWriter bw = new BufferedWriter(new FileWriter(fileList));
        for (Rang rang : rangs.values()) {
            for(MediaSample mediaSample : rang.getMediaSamples()){
                bw.write("file: " + mediaSample.getFileName() + " \t threshold: " + rang.getThreshold());
                bw.newLine();
            }
        }
        bw.close();
    }

    private static void writeFinalListCompact(List<MediaSample> mediaSampleList, Map<Double, Rang> rangs) throws IOException {
        File fileListCompact = new File(Configs.FILENAME_FINAL_LIST_COMPACT);
        BufferedWriter bwCompact = new BufferedWriter(new FileWriter(fileListCompact));
        bwCompact.write("Threshold \t Count \t Percentage");
        bwCompact.newLine();
        for (Double threshold : Configs.thresholdsList) {
            Rang rang = rangs.get(threshold);
            int countThreshold = rang == null ? 0 : rang.getCount();
            bwCompact.write(threshold + "\t " + countThreshold + " \t " + (countThreshold * 100.0) / mediaSampleList.size());
            bwCompact.newLine();
        }
        bwCompact.close();
    }
}
