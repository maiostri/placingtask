package project.mean;

import java.util.List;

import project.entity.Location;
import project.entity.RankedListElement;

public class WeightedArithmeticMean implements IMeanAlgorithm {

    @Override
    public Location calculateAverageLocation(List<RankedListElement> rankedElementList, int numberOfElementsUsed) {
        int count = Math.min(rankedElementList.size(), numberOfElementsUsed);

        double latitude = 0;
        double longitude = 0;
        double sumWeights = 0;
        for (int i = 0; i < count; i++) {
            RankedListElement rankedElement = rankedElementList.get(i);
            Location location = rankedElement.getGroundTruth();
            double weight = rankedElement.getSimilarityFactor();

            latitude += weight * location.getLatitude();
            longitude += weight * location.getLongitude();
            sumWeights += weight;
        }

        return new Location(latitude / sumWeights, longitude / sumWeights);
    }
}
