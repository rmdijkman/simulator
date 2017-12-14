package nl.tue.bpmn.sandbox;

import java.util.Arrays;

import nl.tue.util.RandomGenerator;
import nl.tue.util.Util;

public class SandboxRandom {

	public static void testRandomOrder(){
		System.out.println(Arrays.toString(Util.randomOrder(7)));		
	}
	
	public static void testExponentialDistribution(){
		for (int i = 0; i < 100; i++){
			System.out.println(RandomGenerator.generateExponential(1));
		}
	}
	
	public static void main(String[] args) {
		testExponentialDistribution();
	}

}
