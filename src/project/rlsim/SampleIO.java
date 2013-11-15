package project.rlsim;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class SampleIO {

    public static Sample loadSample(File sampleDirectory, String sampleIdentifier) throws IOException {
        File file = new File (sampleDirectory, sampleIdentifier + "_list.txt");
        return SampleIO.loadSample(file);
    }

    public static Sample loadSample(File file) throws IOException {
        String identifier = processFileName(file.getName());
        Sample rankedList = new Sample(identifier);

        BufferedReader in = null;
        try {
            in = new BufferedReader(new FileReader(file));
            String s = null;
            while ((s = in.readLine()) != null) {
                String[] s_args = s.trim().split("\\s++");

                String id = s_args[1].trim().replace("FlickrVideosTrain/", "");
                BigDecimal distance = new BigDecimal(1 - Double.parseDouble(s_args[0].trim())).setScale(6, RoundingMode.HALF_UP);

                rankedList.getRankedList().add(new RankedListElement(id, distance));
            }
        } finally {
            if (in != null) {
                in.close();
            }
        }

        return rankedList;
    }

    private static String processFileName(String name) {
        return name.replace("_list.txt", "");
    }

    public static void writeSample(File file, List<RankedListElement> rankedList) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(file));
        for (RankedListElement rankedListElement : rankedList) {
            String s = rankedListElement.similarityValue + "\t" + rankedListElement.devSampleId;
            bw.write(s);
            bw.newLine();
        }
        bw.flush();
        bw.close();
    }
}
