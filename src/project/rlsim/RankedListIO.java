package project.rlsim;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RankedListIO {

    public RankedList readRankedList(File sampleDir, String sampleId, boolean calculateDistance) throws NumberFormatException, IOException {
        File file = new File(sampleDir, sampleId + "_list.txt");
        return readRankedList(file, calculateDistance);
    }

    public RankedList readRankedList(File file, boolean calculateDistance) throws NumberFormatException, IOException {
        String fileId = processFileName(file.getName());
        List<RankedListElement> elements = new ArrayList<RankedListElement>();
        Map<String, Integer> positionsByElementId = new HashMap<String, Integer>();

        BufferedReader in = new BufferedReader(new FileReader(file.getAbsoluteFile()));
        String s = null;
        int cont = 0;
        while ((s = in.readLine()) != null) {
            cont = cont + 1;
            String[] s_args = s.trim().split("\t");

            String id = s_args[1].trim().replace("FlickrVideosTrain/", "");

            double dist = Double.parseDouble(s_args[0].trim());
            if (calculateDistance) {
                dist = 1 - dist;
            }

            positionsByElementId.put(id, cont);

            BigDecimal distance = new BigDecimal(dist).setScale(6, RoundingMode.HALF_UP);

            RankedListElement rle = new RankedListElement(id, distance);

            elements.add(rle);
        }
        in.close();

        return new RankedList(fileId, elements, positionsByElementId);
    }

    private static String processFileName(String name) {
        return name.replace("_list.txt", "");
    }

    public void writeRankedListToFile(RankedList rankedList, File file) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(file));
        int count = rankedList.getCount();
        for (int i = 0; i < count; i++) {
            RankedListElement element = rankedList.getElement(i);
            String s = element.getNewDistance() + "\t" + element.getId();
            bw.write(s);
            bw.newLine();
        }
        bw.close();
    }
}
