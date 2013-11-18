package project.mean;

import java.util.List;
import project.entity.Location;
import project.entity.RankedListElement;

public class ArithmeticMean implements IMeanAlgorithm {

    @Override
    public Location calculateAverageLocation(List<RankedListElement> rankedElementList) {
        double latitude = 0.0;
        double longitude = 0.0;
        for (RankedListElement rankedListElement : rankedElementList) {
            Location location = rankedListElement.getGroundTruth();
            latitude += location.getLatitude();
            longitude += location.getLongitude();
        }
        return new Location(latitude / rankedElementList.size(), longitude / rankedElementList.size());
    }

}
