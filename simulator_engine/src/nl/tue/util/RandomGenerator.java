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
	public static long generateExponential(long lambda){
		if (r == null){
			r = new Random();
		}
		return (long) (Math.log(1.0 - Math.random())*-((double)lambda));
	}
	
	/**
	 * Generates a positive normally distributed random number for the normal distribution with parameters mu and sigma. 
	 * Negative values become 0.
	 * 
	 * @param mu the mu of the normal distribution
	 * @param sigma the sigma of the normal distribution
	 * @return a positive normally distributed random number
	 */
	public static long generateNormal(long mu, long sigma){
		if (r == null){
			r = new Random();
		}		
		long result = (long) (((double)sigma) * r.nextGaussian() + ((double)mu));		
		return (result >= 0)?result:0;
	}
	
	/**
	 * Returns a uniformly distributed random number between 0 (inclusive) and max (exclusive).
	 * 
	 * @param max the max value of the returned number (exclusive)
	 * @return a uniformly distributed random number
	 */
	public static long generateUniform(long max){
		if (r == null){
			r = new Random();
		}		
		return (long) (r.nextDouble() * ((double) max));
	}
}