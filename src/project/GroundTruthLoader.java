package project;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import project.entity.Location;

public class GroundTruthLoader {

    public static GroundTruthResolver loadGroundTruth() throws IOException {
        System.out.println("Import ground truth from test samples...");
        Map<String, Location> groundTruthFromTestSamples = loadGroundTruthFromTestSamples();
        int size1 = groundTruthFromTestSamples.size();

        System.out.println("Import ground truth from dev samples...");
        Map<String, Location> groundTruthFromDevSamples = loadGroundTruthFromDevSamples();
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
}
