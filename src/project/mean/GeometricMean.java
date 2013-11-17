package project.mean;

import java.util.List;

import project.entity.Location;
import project.entity.RankedListElement;

public class GeometricMean implements IMeanAlgorithm {

    @Override
    public Location calculateAverageLocation(
	    List<RankedListElement> rankedElementList) {
	Double latitude = 1.0;
	Double longitude = 1.0;
	for (RankedListElement rankedListElement : rankedElementList) {
	    Location location = rankedListElement.getGroundTruth();
	    latitude *= location.getLatitude();
	    longitude *= location.getLongitude();
	}

	latitude = Math.pow(latitude, rankedElementList.size());
	longitude = Math.pow(longitude, rankedElementList.size());
	return new Location(latitude, longitude);

    }

}
