package project.mean;

import java.util.List;
import project.entity.Location;
import project.entity.RankedListElement;

public interface IMeanAlgorithm {

    Location calculateAverageLocation(final List<RankedListElement> rankedElementList);
}
