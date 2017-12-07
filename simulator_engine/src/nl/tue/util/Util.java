package nl.tue.util;

public class Util {

	public static Double round(Double v, int numDigits){
		double f = Math.pow(10.0, (double)numDigits);
		return Math.round(v*f)/f;
	}
}
