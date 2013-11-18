package project.rlsim;

import java.math.BigDecimal;

public class RankedListElement {
    private String id;
    private BigDecimal distance;
    private BigDecimal newDistance;

    public RankedListElement(String id, BigDecimal distance) {
        this.id = id;
        this.distance = distance;
    }

    public String getId() {
        return id;
    }

    public BigDecimal getDistance() {
        return distance;
    }

    public BigDecimal getNewDistance() {
        return newDistance;
    }

    public void setNewDistance(BigDecimal newDistance) {
        this.newDistance = newDistance;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof RankedListElement) {
            if (((RankedListElement) obj).getId().equals(getId())) {
                return true;
            }
        }
        return super.equals(obj);
    }
}