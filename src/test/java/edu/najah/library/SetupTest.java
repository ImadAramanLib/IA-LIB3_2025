package edu.najah.library;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Initial test to verify Maven, JUnit, and project setup.
 * 
 * @author Imad Araman
 * @version 1.0
 */
public class SetupTest {
    
    /**
     * Test to verify that JUnit 5 is properly configured.
     */
    @Test
    void testSetup() {
        assertTrue(true, "If this test passes, Maven and JUnit are properly configured!");
    }
    
    /**
     * Test basic arithmetic to ensure Java environment works.
     */
    @Test
    void testBasicJavaFunctionality() {
        int result = 2 + 2;
        assertEquals(4, result, "Basic Java operations should work correctly");
    }
}
