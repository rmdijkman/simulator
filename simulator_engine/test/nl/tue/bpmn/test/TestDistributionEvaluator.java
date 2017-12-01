package nl.tue.bpmn.test;

import org.junit.Test;

import nl.tue.bpmn.parser.DistributionEvaluator;

public class TestDistributionEvaluator {

	@Test
	public void testDistribution(){
		DistributionEvaluator de = new DistributionEvaluator();
		System.out.println(de.evaluate("N(10,4)"));
		System.out.println(de.evaluate("exp(5)"));
		System.out.println(de.evaluate("[{correct, 0%}, {incorrect, 100%}]"));
		System.out.println(de.evaluate("[{high, 10%}, {medium, 20%}, {low, 70%}]}]"));
		System.out.println(de.evaluate("[{high, 10%}, {medium, 20%}, {low, 70%}]}]"));
	}

	@Test
	public void testDistributionSyntax(){
		DistributionEvaluator de = new DistributionEvaluator();
		System.out.println(de.validate("N(10,4)"));
		System.out.println(de.validate("exp(5)"));
		System.out.println(de.validate("[{correct, 0%}, {incorrect, 100%}]"));
		System.out.println(de.validate("[{high, 10%}, {medium, 20%}, {low, 70%}]}]"));
		System.out.println(de.validate("[high, 10%}, {medium, 20%}, {low, 70%}]}]"));
	}
}