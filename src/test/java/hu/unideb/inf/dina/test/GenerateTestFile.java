/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.unideb.inf.dina.test;

import hu.unideb.inf.dina.SampleGraphGenerator;
import java.io.FileNotFoundException;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author admin
 */
public class GenerateTestFile {
    
   // @Test
    public void Generate() throws FileNotFoundException{
        SampleGraphGenerator.generateSample(300000, 1200000, "v300000_e1200000.csv");
    }
    
}
