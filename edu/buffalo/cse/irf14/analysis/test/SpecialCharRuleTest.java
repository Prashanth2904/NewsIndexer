/**
 * 
 */
package edu.buffalo.cse.irf14.analysis.test;

import static org.junit.Assert.*;

import org.junit.Test;

import edu.buffalo.cse.irf14.analysis.TokenFilterType;
import edu.buffalo.cse.irf14.analysis.TokenizerException;

/**
 * @author nikhillo
 *
 */
public class SpecialCharRuleTest extends TFRuleBaseTest {
	
	@Test
	public void testRule() {
			try {
					//special symbols one by one
					
					
					
					
					assertArrayEquals(new String[]{"pray", "to"}, 
							runTest(TokenFilterType.SPECIALCHARS, "pray to __/\\__"));
			
			} catch (TokenizerException e) {
				fail("Exception thrown when not expected!");	
			}
	}
}
