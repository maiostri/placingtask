package project.mean;

import java.util.List;
import project.entity.Location;
import project.entity.RankedListElement;

public class ArithmeticMean implements IMeanAlgorithm {

    @Override
    public Location calculateAverageLocation(List<RankedListElement> rankedElementList, int numberOfElementsUsed) {
        int count = Math.min(rankedElementList.size(), numberOfElementsUsed);

        double latitude = 0;
        double longitude = 0;
        for (int i = 0; i < count; i++) {
            Location location = rankedElementList.get(i).getGroundTruth();
            latitude += location.getLatitude();
            longitude += location.getLongitude();
        }

        return new Location(latitude / count, longitude / count);
    }
}
