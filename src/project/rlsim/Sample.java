package project.rlsim;

import java.util.ArrayList;
import java.util.List;


public class Sample {

	private String identifier;
	List<RankedListElement> rankedList;

    public Sample(String identifier) {
	    this.identifier = identifier;

		rankedList = new ArrayList<RankedListElement>();
	}

	public String getIdentifier() {
		return identifier;
	}

	public List<RankedListElement> getRankedList() {
        return rankedList;
    }

    public int getIndexOfDevSampleId(String name2) {
        for (int i = 0; i < rankedList.size(); i++) {
            if(rankedList.get(i).devSampleId.equals(name2)){
                return i;
            }
        }
        return -1;
    }

    @Override
    public String toString() {
        return new StringBuilder("identifier=").append(identifier).toString();
    }
}
