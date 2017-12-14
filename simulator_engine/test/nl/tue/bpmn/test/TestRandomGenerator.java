package nl.tue.bpmn.test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import nl.tue.util.RandomGenerator;
import nl.tue.util.Util;

public class TestRandomGenerator {

	@Test
	public void testExponential() {
		double lambda = 2.0;
		double replications = 1000.0;
		double margin = 0.05;

		List<Double> sample = new ArrayList<Double>(); 
		for (int i = 0; i < replications; i++){
			sample.add(RandomGenerator.generateExponential(lambda));
		}
		double mean = Util.mean(sample);
		double variance = Util.variance(sample);
		double theoreticalMean = 1.0/lambda; 
		double theoreticalVariance = Math.pow(lambda,-2.0); 
		assertTrue("Mean ("+mean+") of generated sample is not conform the theoretical mean ("+theoreticalMean+") (note: this can be a rondom occurrence, execute again to be sure)", mean < theoreticalMean*(1.0 + margin) && mean > theoreticalMean*(1.0 - margin));
		assertTrue("Variance ("+variance+") of generated sample is not conform the theoretical variance ("+theoreticalVariance+") (note: this can be a rondom occurrence, execute again to be sure)", variance < theoreticalVariance*(1.0 + margin) && variance > theoreticalVariance*(1.0 - margin));		
	}

	@Test
	public void testNormal() {
		double mu = 2.0;
		double sigma = 0.5;
		double replications = 1000.0;
		double margin = 0.05;

		List<Double> sample = new ArrayList<Double>(); 
		for (int i = 0; i < replications; i++){
			sample.add(RandomGenerator.generateNormal(mu, sigma));
		}
		double mean = Util.mean(sample);
		double variance = Util.sd(sample);
		double theoreticalMean = mu; 
		double theoreticalSD = sigma; 
		assertTrue("Mean ("+mean+") of generated sample is not conform the theoretical mean ("+theoreticalMean+") (note: this can be a rondom occurrence, execute again to be sure)", mean < theoreticalMean*(1.0 + margin) && mean > theoreticalMean*(1.0 - margin));
		assertTrue("Variance ("+variance+") of generated sample is not conform the theoretical variance ("+theoreticalSD+") (note: this can be a rondom occurrence, execute again to be sure)", variance < theoreticalSD*(1.0 + margin) && variance > theoreticalSD*(1.0 - margin));		
	}
	
	@Test
	public void testUniform() {
		double maximum = 2.0;
		double replications = 1000.0;
		double margin = 0.05;

		List<Double> sample = new ArrayList<Double>(); 
		for (int i = 0; i < replications; i++){
			sample.add(RandomGenerator.generateUniform(maximum));
		}
		double mean = Util.mean(sample);
		double variance = Util.variance(sample);
		double theoreticalMean = maximum/2.0; 
		double theoreticalVariance = Math.pow(maximum,2.0)/12.0; 
		assertTrue("Mean ("+mean+") of generated sample is not conform the theoretical mean ("+theoreticalMean+") (note: this can be a rondom occurrence, execute again to be sure)", mean < theoreticalMean*(1.0 + margin) && mean > theoreticalMean*(1.0 - margin));
		assertTrue("Variance ("+variance+") of generated sample is not conform the theoretical variance ("+theoreticalVariance+") (note: this can be a rondom occurrence, execute again to be sure)", variance < theoreticalVariance*(1.0 + margin) && variance > theoreticalVariance*(1.0 - margin));		
	}
}
