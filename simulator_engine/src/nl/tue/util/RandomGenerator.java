package nl.tue.util;

import java.util.Random;

public class RandomGenerator {
	
	private static Random r = null;
	
	/**
	 * Generates an exponentially distributed random number for the exponential distribution with parameter lambda. 
	 * 
	 * @param lambda the parameter of the exponential distribution
	 * @return an exponentially distributed random number
	 */
	public static double generateExponential(double lambda){
		if (r == null){
			r = new Random(System.currentTimeMillis());
		}
		return Math.log(1.0 - Math.random())*(-1.0/lambda);
	}
	
	/**
	 * Generates a positive normally distributed random number for the normal distribution with parameters mu and sigma. 
	 * Negative values become 0.
	 * 
	 * @param mu the mu of the normal distribution
	 * @param sigma the sigma of the normal distribution
	 * @return a positive normally distributed random number
	 */
	public static double generateNormal(double mu, double sigma){
		if (r == null){
			r = new Random(System.currentTimeMillis());
		}		
		double result = sigma * r.nextGaussian() + mu;		
		return (result >= 0)?result:0;
	}
	
	/**
	 * Returns a uniformly distributed random number between 0 (inclusive) and max (exclusive).
	 * 
	 * @param max the max value of the returned number (exclusive)
	 * @return a uniformly distributed random number
	 */
	public static double generateUniform(double max){
		if (r == null){
			r = new Random(System.currentTimeMillis());
		}		
		return r.nextDouble() * max;
	}
}