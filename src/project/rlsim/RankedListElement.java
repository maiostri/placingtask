package project.rlsim;

import java.math.BigDecimal;

public class RankedListElement {
    String devSampleId;
	BigDecimal similarityValue;

	public RankedListElement(String devSampleId, BigDecimal similarityValue) {
	    this.devSampleId = devSampleId;
	    this.similarityValue = similarityValue;
    }

	@Override
	public String toString() {
	    return new StringBuilder("devSampleId=").append(devSampleId).append(", similarityValue=").append(similarityValue).toString();
	}
}