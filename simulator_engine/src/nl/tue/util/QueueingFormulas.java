package nl.tue.util;

public class QueueingFormulas {

	/**
	 * Returns the given number rounded to the specified number of digits. 
	 * 
	 * @param number	a number
	 * @param nrDigits	number of digits to round to
	 * @return			rounded number
	 */
	public static double round(double number, int nrDigits){
		return Math.round(number * Math.pow(10.0,nrDigits)) / Math.pow(10.0,nrDigits);
	}
	
	/**
	 * Returns n factorial.
	 * 
	 * @param n	the number to compute the factorial for.
	 * @return	n!
	 */
	public static long factorial(long n){
		long result = 1;
		for (int i = 2; i <= n; i++){
			result *= i;
		}
		return result;
	}

	/**
	 * Returns the expected processing time for an exponential distribution.
	 *  
	 * @param mu	the processing rate
	 * @return		the expected processing time
	 */
	public static double EB(double mu){
		return 1.0/mu;
	}
	
	/**
	 * Returns the expected squared processing time for an exponential distribution.
	 * 
	 * @param mu	the processing rate
	 * @return		the expected processing time
	 */
	public static double EB2(double mu){
		return 2.0/Math.pow(mu, 2.0);
	}
	
	/**
	 * Returns the expected residual processing time in an exponential distribution.
	 * 
	 * @param mu	the processing rate
	 * @return		the expected residual processing time
	 */
	public static double ER(double mu){
		return 1.0/mu;
	}
	
	/**
	 * Returns the expected residual processing time in a general distribution. 
	 * 
	 * @param EB	the expected processing time
	 * @param EB2	the expected squared processing time
	 * @return		the expected residual processing time
	 */
	public static double ER(double EB, double EB2){
		return EB2/(2.0*EB);
	}
	
	/**
	 * Returns the server occupancy rate in an M/M/1 system. 
	 * 
	 * @param lambda	the arrival rate
	 * @param mu		the processing rate
	 * @return			the server occupancy rate
	 */
	public static double rho(double lambda, double mu){
		return lambda/mu;
	}
	
	/**
	 * Returns the server occupancy rate in an M/M/c system.
	 * 
	 * @param lambda	the arrival rate
	 * @param mu		the processing rate
	 * @param c			the number of servers
	 * @return			the server occupancy rate
	 */
	public static double rho(double lambda, double mu, double c){
		return lambda/(c*mu);
	}
	
	/**
	 * Returns the expected waiting time in an M/G/c queue, given:
	 * 
	 * @param EB	the expected processing time 
	 * @param EB2	the expected squared processing time
	 * @param rho	the occupancy of the processor as a whole
	 * @param c		the number of servers in the processor
	 * @return		the expected waiting time
	 */
	public static double EWMMc(double EB, double EB2, double rho, Double c){
		return PiW(rho,c)*(1.0/(1.0-rho))*(ER(EB,EB2)/c);
	}

	/**
	 * Returns the expected waiting time in an M/M/c queue, given:
	 * 
	 * @param lambda	the arrival rate
	 * @param mu		the processing rate
	 * @param c			the number of services in the processor
	 * @return			the expected waiting time
	 */
	public static double EWMMc(double lambda, double mu, Double c){
		double rho = rho(lambda, mu, c);
		return PiW(rho,c)*(1.0/(1.0-rho))*(ER(mu)/c);
	}
	
	/**
	 * Returns the probability that an item arriving in an M/M/c queue sees work in front of it. 
	 * 
	 * @param rho	the occupancy rate of the processor
	 * @param c		the number of servers
	 * @return		a probability
	 */
	public static double PiW(double rho, Double c){
		double intermediate = 0;
		for (long n = 0; n <= (c.longValue()-1); n++){
			intermediate += Math.pow(c*rho,n)/(1.0*factorial(n));
		}
		
		return (Math.pow(c*rho,c)/(1.0*factorial(c.longValue()))) * Math.pow((1.0-rho)*intermediate + Math.pow(c*rho,c)/(1.0*factorial(c.longValue())),-1.0);		
	}
	
	/**
	 * Applies Little's law to return the expected number of items in a queue, given the expected waiting time and the arrival rate. 
	 * 
	 * @param W			the expected waiting time
	 * @param lambda	the arrival rate
	 * @return			the expected number of items 
	 */
	public static double littleL(double W, double lambda){
		return W*lambda;
	}
	
	/**
	 * Applies Little's law to return the expected waiting time in a queue, given the 
	 * 
	 * @param L			the expected number of items
	 * @param lambda	the arrival rate
	 * @return			the expected waiting time
	 */
	public static double littleW(double L, double lambda){
		return L/lambda;
	}
	
}
