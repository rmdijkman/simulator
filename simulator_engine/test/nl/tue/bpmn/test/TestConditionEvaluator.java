package nl.tue.bpmn.test;

import static org.junit.Assert.*;

import org.junit.Test;

import nl.tue.bpmn.parser.ConditionEvaluator;

public class TestConditionEvaluator {
	@Test
	public void noValue(){
		assertFalse(new ConditionEvaluator().evaluate("Gender IN {male,female}"));
	}
	
	@Test
	public void testNumericCondition(){
		ConditionEvaluator ce = new ConditionEvaluator();
		ce.setVariableValue("Income", "101000");
	
		assertTrue(ce.evaluate("Income = 101000"));		
		assertFalse(ce.evaluate("Income = 100000"));		
		assertTrue(ce.evaluate("Income > 100000"));		
		assertFalse(ce.evaluate("Income > 101000"));		
		assertTrue(ce.evaluate("Income < 102000"));		
		assertFalse(ce.evaluate("Income < 101000"));		
		assertTrue(ce.evaluate("Income >= 101000"));		
		assertFalse(ce.evaluate("Income >= 102000"));		
		assertTrue(ce.evaluate("Income <= 102000"));		
		assertFalse(ce.evaluate("Income <= 100000"));		
	}
	
	@Test
	public void testNominalCondition(){
		ConditionEvaluator ce = new ConditionEvaluator();
		ce.setVariableValue("ABC", "aa");
	
		assertTrue(ce.evaluate("ABC IN {aa}"));		
		assertTrue(ce.evaluate("ABC IN {bb,aa,cc}"));		
		assertFalse(ce.evaluate("ABC IN {bb,cc}"));		
	}

	@Test
	public void testAndOrNotCondition(){
		ConditionEvaluator ce = new ConditionEvaluator();
		ce.setVariableValue("Fa", "1");
		ce.setVariableValue("Tr", "1");
	
		assertTrue(ce.evaluate("Tr=1 OR Tr=1"));		
		assertTrue(ce.evaluate("Tr=1 OR Fa=0"));		
		assertTrue(ce.evaluate("Fa=0 OR Tr=1"));		
		assertFalse(ce.evaluate("Fa=0 OR Fa=0"));
		
		assertTrue(ce.evaluate("Tr=1 AND Tr=1"));		
		assertFalse(ce.evaluate("Tr=1 AND Fa=0"));		
		assertFalse(ce.evaluate("Fa=0 AND Tr=1"));		
		assertFalse(ce.evaluate("Fa=0 AND Fa=0"));
		
		assertFalse(ce.evaluate("Tr=1 AND NOT Tr=1"));		
		assertTrue(ce.evaluate("Tr=1 AND NOT Fa=0"));		
		assertTrue(ce.evaluate("NOT Fa=0 AND Tr=1"));		
		assertTrue(ce.evaluate("NOT Fa=0 AND NOT Fa=0"));
		
		
		assertTrue(ce.evaluate("Tr=1 AND (NOT Fa=0 OR NOT Fa=0)"));		
	}
	
	@Test
	public void testExpressionValidation(){
		ConditionEvaluator ce = new ConditionEvaluator();
		ce.setVariableValue("Fa", "1");
		ce.setVariableValue("Tr", "1");
		
		assertTrue(ce.validate("Tr=1 OR Tr=1").isEmpty());
		assertFalse(ce.validate("Tr OR Tr=1").isEmpty());
	}	
}