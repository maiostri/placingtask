package project.mean;

import project.Configs;

public class MeanAlgorithmFactory {

    public static IMeanAlgorithm createMeanAlgorithm(
	    final Configs.MeanAlgorithm meanAlgorithmName) {
	IMeanAlgorithm meanAlgorithm = null;
	switch (meanAlgorithmName) {
	case ARITHMETIC_MEAN:
	    meanAlgorithm = new ArithmeticMean();
	    break;
	case WEIGHTED_ARITHMETIC_MEAN:
	    meanAlgorithm = new WeightedArithmeticMean();
	    break;
	case GEOMETRIC_MEAN:
	    meanAlgorithm = new GeometricMean();
	default:
	    break;
	}
	return meanAlgorithm;
    }

}
