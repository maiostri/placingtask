package project.mean;

import java.util.List;
import project.entity.Location;
import project.entity.RankedListElement;

public class GeometricMean implements IMeanAlgorithm {

    @Override
    public Location calculateAverageLocation(List<RankedListElement> rankedElementList, int numberOfElementsUsed) {
        int count = Math.min(rankedElementList.size(), numberOfElementsUsed);

        double latitude = 1.0;
        double longitude = 1.0;
        for (int i = 0; i < count; i++) {
            Location location = rankedElementList.get(i).getGroundTruth();
            latitude *= location.getLatitude();
            longitude *= location.getLongitude();
        }

        double rootFactor = 1.0 / count;
        latitude = Math.pow(latitude, rootFactor);
        longitude = Math.pow(longitude, rootFactor);

        return new Location(latitude, longitude);
    }
}
