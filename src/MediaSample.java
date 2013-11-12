

import java.util.ArrayList;
import java.util.List;

class MediaSample {
	private String fileName;
	private List<Run> similarityList;
	private Run answerMedia;

	public MediaSample() {
		this.similarityList = new ArrayList<Run>();
	}

	public String getFileName() {
		return this.fileName;
	}

	public void setFileName(final String fileName) {
		this.fileName = fileName;
	}

	public List<Run> getSimilarityList() {
		return this.similarityList;
	}

	public void addSimilarityElement(final Run element) {
		this.similarityList.add(element);
	}

	public void setAnswerMedia(final Run answerMedia) {
		this.answerMedia = answerMedia;
	}

	public Run getAnswerMedia() {
		return this.answerMedia;
	}

}