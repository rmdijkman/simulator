package nl.tue.util;

import java.util.List;

public class Util {

	public static Double round(Double v, int numDigits){
		Double f = Math.pow(10.0, (double)numDigits);
		return Math.round(v*f)/f;
	}
	
	/**
	 * Calculates the mean and the lower and upper bound of the 95% confidence interval
	 * for a series of values.
	 * 
	 * @param values a series of values
	 * @return an array, consisting of the lower bound, the mean and the upper bound in that order
	 */
	public static Double[] lowerMeanUpper(List<Double> values) {
	    //calculate the mean
	    double sum = 0.0;
	    for (double v: values) {
	        sum += v;
	    }
	    double mean = sum/((double)values.size());

	    //calculate standard deviation
	    double squaredDifferenceSum = 0.0;
	    for (double v: values) {
	        squaredDifferenceSum += (v - mean) * (v - mean);
	    }
	    double variance = squaredDifferenceSum/((double)values.size());
	    double standardDeviation = Math.sqrt(variance);

	    //value for 95% confidence interval, source: https://en.wikipedia.org/wiki/Confidence_interval#Basic_Steps
	    double confidenceLevel = 1.96;
	    double temp = confidenceLevel * standardDeviation / Math.sqrt((double) values.size());
	    return new Double[]{mean - temp, mean, mean + temp};
	}
	
}
