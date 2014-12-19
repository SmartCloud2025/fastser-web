package org.fastser.web;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AppTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp()
    {
    	String pattern = "**abc";
		String value = "abc";
		boolean result = false;
		if(true){
			int index = pattern.indexOf("**");
			if(index == -1){
				result = pattern.equals(value);
			}else{
				result = pattern.contains(value);
			}
		}
		System.out.println(result);
    }
}
