package project.rlsim;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RankedList {
    private String id;
    private List<RankedListElement> elements;
    private Map<String, Integer> positionsByElementId;

    public RankedList(String id) {
        this.id = id;
        elements = new ArrayList<RankedListElement>();
        positionsByElementId = new HashMap<String, Integer>();
    }

    public String getId() {
        return id;
    }

    public List<RankedListElement> getElements() {
        return elements;
    }

    public int getPosition(String id) {
        Integer pos = positionsByElementId.get(id);
        if (pos == null) {
            pos = -1;
        }
        return pos;
    }

    public void setPosition(String id, int pos) {
        positionsByElementId.put(id, pos);
    }

    public void writeToFile(File file) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(file));
        for (RankedListElement element : getElements()) {
            String s = element.getNewDistance() + "\t" + element.getId();
            bw.write(s);
            bw.newLine();
        }
        bw.close();
    }
}
