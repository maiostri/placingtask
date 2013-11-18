package project.mean;

import java.util.List;

import project.entity.Location;
import project.entity.RankedListElement;

public class WeightedArithmeticMean implements IMeanAlgorithm {

    @Override
    public Location calculateAverageLocation(List<RankedListElement> rankedElementList) {
	double latitude = 0.0;
	double longitude = 0.0;
	for (RankedListElement rankedElement : rankedElementList) {
	    Location location = rankedElement.getGroundTruth();
	    latitude += rankedElement.getSimilarityFactor() * location.getLatitude();
	    longitude += rankedElement.getSimilarityFactor() * location.getLongitude();
	}
	return new Location(latitude, longitude);
    }
}
