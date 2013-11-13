
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ValidationPT {

    public static void main(String[] args) throws IOException {
        if(args.length == 0){
            throw new IllegalArgumentException("Wrong use. Please call the program specifying a directory containing the .list files.");
        }

        // Import *.list files
        File[] files = new File(args[0]).listFiles();

        System.out.println("Valitation toolbox, based on its initial version from Pascal Kelm, for the Placing Task at MediaEval.\n");

        System.out.print("Import ground truth... ");
        Map<Integer, Position> groundTruthFromTestSamples = loadGroundTruthFromTestSamples();
        System.out.println(" Done ground truth. ");

        System.out.print("Import training file... ");
        Map<Integer, Position> groundTruthFromDevSamples = loadGroundTruthFromDevSamples();
        System.out.println(" Done training. ");

        GroundTruthResolver groundTruthResolver = new GroundTruthResolver(groundTruthFromDevSamples);

        List<MediaSample> mediaSamples = new ArrayList<MediaSample>();

        for (File file : files) {
            MediaSample mediaSample = readMediaSample(file, groundTruthResolver);
            mediaSamples.add(mediaSample);
        }

        for (MediaSample mediaSample : mediaSamples) {
            mediaSample.determineMediaAnswer();
        }

        System.out.println(" Done.");

        /****************
         * Calc thresholds
         ****************/
        System.out.print("Calculating thresholds... ");
        List<Rang> list_range = new ArrayList<Rang>();
        for (MediaSample mediaSample : mediaSamples) {
            Rang temp_rang = new Rang();

            for (Double threshold : Configs.thresholdsList) {
                if (mediaSample.getAnswerMedia().getHavesineDistance() < threshold) {
                    temp_rang.setThreshold(threshold);
                }
            }

            temp_rang.setMediaFile(mediaSample.getFileName());
            list_range.add(temp_rang);
        }


        /*****************
         * Show Results
         *****************/

        writeFinalList(list_range);

        writeFinalListCompact(mediaSamples, list_range);

        // Save summary in folder
        System.out.println(" Finish.");
    }

    private static Map<Integer, Position> loadGroundTruthFromTestSamples() throws IOException {
        Map<Integer, Position> idPositionTestMap = new HashMap<Integer, Position>();

        String s = null;
        BufferedReader in = new BufferedReader(new FileReader(Configs.FILENAME_GROUND_TRUTH_TEST_SAMPLES));

        while ((s = in.readLine()) != null) {
            String[] arg = s.split(";");
            Position temp_Pos = new Position(Integer.parseInt(arg[0].trim()),
                Double.parseDouble(arg[1].trim()), Double.parseDouble(arg[2].trim()));
            idPositionTestMap.put(temp_Pos.getIdentifier(), temp_Pos);
        }
        in.close();

        return idPositionTestMap;
    }

    private static Map<Integer, Position> loadGroundTruthFromDevSamples() throws IOException {
        Map<Integer, Position> idPositionDVLPMap = new HashMap<Integer, Position>();

        String s = null;
        BufferedReader in = new BufferedReader(new FileReader(Configs.FILENAME_GROUND_TRUTH_DEV_SAMPLES));
        while ((s = in.readLine()) != null) {
            String[] arg = s.split(";");
            Position temp_Pos = new Position(Integer.parseInt(arg[0].trim().substring(0, 5)),
                Double.parseDouble(arg[1].trim()),
                Double.parseDouble(arg[2].trim()));
            idPositionDVLPMap.put(temp_Pos.getIdentifier(), temp_Pos);
        }
        in.close();

        return idPositionDVLPMap;
    }

    private static Integer readSampleIdentifierFromFilename(String name) {
        String fileName = name.replace("p1_", "").replace("p2_", "").replace("_list.txt", "");
        return Integer.parseInt(fileName);
    }

    private static Integer readSampleIdentifierFromDevSampleName(String id) {
        id = id.replace("FlickrVideosTrain/p1_", "").replace("FlickrVideosTrain/p2_", "");
        return Integer.parseInt(id);
    }

    private static MediaSample readMediaSample(File sampleFile, GroundTruthResolver groundTruthResolver) throws IOException {
        Integer sampleIdentifier = readSampleIdentifierFromFilename(sampleFile.getName());

        System.out.println(sampleIdentifier);

        MediaSample mediaSample = new MediaSample();
        mediaSample.setFileName(sampleFile.getName());

        Position sampleGroundTruth = groundTruthResolver.get(sampleIdentifier);

        String s = null;
        BufferedReader in = new BufferedReader(new FileReader(sampleFile.getAbsoluteFile()));
        while ((s = in.readLine()) != null) {
            String[] s_args = s.trim().split("\\s++");
            Run run = new Run();
            run.setSimilarityFactor(Double.parseDouble(s_args[0].replace(",", ".")));
            run.setIdentifier(readSampleIdentifierFromDevSampleName(s_args[1]));

            Position runGroundTruth = groundTruthResolver.get(run.getIdentifier());

            run.setGroundTruth(runGroundTruth);
            run.setHavesineDistance(MathUtils.calcHaversineDistance(runGroundTruth, sampleGroundTruth));
            mediaSample.addSimilarityElement(run);
        }
        in.close();

        return mediaSample;
    }

    private static void writeFinalList(List<Rang> list_range) throws IOException {
        File fileList = new File(Configs.FILENAME_FINAL_LIST);
        BufferedWriter bw = new BufferedWriter(new FileWriter(fileList));
        for (Rang rang : list_range) {
            bw.write("file: " + rang.getMediaFile() + " \t threshold: " + rang.getThreshold());
            bw.newLine();
        }
        bw.close();
    }

    private static void writeFinalListCompact(List<MediaSample> mediaSampleList, List<Rang> list_range) throws IOException {
        File fileListCompact = new File(Configs.FILENAME_FINAL_LIST_COMPACT);
        BufferedWriter bwCompact = new BufferedWriter(new FileWriter(fileListCompact));
        bwCompact.write("Threshold \t Count \t Percentage");
        bwCompact.newLine();
        for (Double threshold : Configs.thresholdsList) {
            int countThreshold = 0;
            for (Rang rang : list_range) {
                if (rang.getThreshold() == threshold) {
                    countThreshold++;
                }
            }
            bwCompact.write(threshold + "\t " + countThreshold + " \t " + (countThreshold * 100) / mediaSampleList.size());
            bwCompact.newLine();
        }
        bwCompact.close();
    }
}
