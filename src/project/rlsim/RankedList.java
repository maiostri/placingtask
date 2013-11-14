package project.rlsim;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


public class RankedList {

	/*class RankedListElement {
		BigDecimal similarityValue;
		String devSampleI;
	}
	List<RankedListElement> elements;
	List<RankedListElement> newElements;*/


	public RankedList(){
		ranking = new ArrayList<String>();
		currentDistances = new ArrayList<BigDecimal>();
		newDistances = new ArrayList<BigDecimal>();
	}



	private String videoName;

	private List<String> ranking;
	private List<BigDecimal> currentDistances;
	private List<BigDecimal> newDistances;

	public String getVideoName() {
		return videoName;
	}

	public void setVideoName(String videoName) {
		this.videoName = videoName;
	}

	public List<String> getRanking() {
		return ranking;
	}

	public void setRanking(List<String> ranking) {
		this.ranking = ranking;
	}

	public List<BigDecimal> getCurrentDistances() {
		return currentDistances;
	}

	public void setCurrentDistances(List<BigDecimal> currentDistances) {
		this.currentDistances = currentDistances;
	}

	public List<BigDecimal> getNewDistances() {
		return newDistances;
	}

	public void setNewDistances(List<BigDecimal> newDistances) {
		this.newDistances = newDistances;
	}

}
