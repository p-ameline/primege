package com.primege.shared ;

import java.util.ArrayList;

import junit.framework.TestCase ;

public class GlobalParametersTest extends TestCase
{
  public void testAreStringsEqual() 
  {
  	// Test default constructor
  	//
  	String null1 = null ;
  	String null2 = null ;
  	String t1 = "test" ;
  	String t2 = "test2" ;
  	String t3 = "test" ;
  	String t4 = "test4" ;
  	
  	// Check nullity
  	//
  	assertTrue(GlobalParameters.areStringsEqual(null1, null1)) ;
  	assertTrue(GlobalParameters.areStringsEqual(null1, null2)) ;
  	assertFalse(GlobalParameters.areStringsEqual(null1, t1)) ;
  	assertFalse(GlobalParameters.areStringsEqual(t1, null2)) ;
  	
  	assertTrue(GlobalParameters.areStringsEqual(t1, t1)) ;
  	assertTrue(GlobalParameters.areStringsEqual(t1, t3)) ;
  	
  	assertFalse(GlobalParameters.areStringsEqual(t1, t2)) ;
  	assertFalse(GlobalParameters.areStringsEqual(t2, t4)) ;
  }
  
  public void testParseString() 
  {
  	String sToParse = "abc|defg|hijkl" ;
  	ArrayList<String> result = GlobalParameters.ParseString(sToParse, "|") ;
  	assertTrue(result.size() == 3) ;
  	assertTrue("abc".equals(result.get(0))) ;
  	assertTrue("defg".equals(result.get(1))) ;
  	assertTrue("hijkl".equals(result.get(2))) ;
  	
  	sToParse = "abcABCDdefgABCDhijkl" ;
  	result = GlobalParameters.ParseString(sToParse, "ABCD") ;
  	assertTrue(result.size() == 3) ;
  	assertTrue("abc".equals(result.get(0))) ;
  	assertTrue("defg".equals(result.get(1))) ;
  	assertTrue("hijkl".equals(result.get(2))) ;
  	
  	sToParse = "abcABCDdefgABCDhijklABCD" ;
  	result = GlobalParameters.ParseString(sToParse, "ABCD") ;
  	assertTrue(result.size() == 3) ;
  	assertTrue("abc".equals(result.get(0))) ;
  	assertTrue("defg".equals(result.get(1))) ;
  	assertTrue("hijkl".equals(result.get(2))) ;
  }
}
