package project.rlsim;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class RankedList {
    private String id;
    private List<RankedListElement> elements;
    private Map<String, Integer> positionsByElementId;

    public RankedList(String id, List<RankedListElement> elements, Map<String, Integer> positionsByElementId) {
        this.id = id;
        this.elements = elements;
        this.positionsByElementId = positionsByElementId;
    }

    public String getId() {
        return id;
    }

    public int getCount() {
        return elements.size();
    }

    public RankedListElement getElement(int j) {
        return elements.get(j);
    }

    public int getPosition(String id) {
        Integer pos = positionsByElementId.get(id);
        if (pos == null) {
            pos = -1;
        }
        return pos;
    }

    /**
     * Ordena os itens do ranked list, decrescente, por valor de similaridade
     */
    public void orderByDistance() {
        Collections.sort(elements, new Comparator<RankedListElement>() {
            public int compare(RankedListElement o1, RankedListElement o2) {
                return o1.getNewDistance().compareTo(o2.getNewDistance());
            }
        });
    }
}
