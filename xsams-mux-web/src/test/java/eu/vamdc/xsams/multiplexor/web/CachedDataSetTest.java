package eu.vamdc.xsams.multiplexor.web;

import eu.vamdc.xsams.multiplexor.mux.Collator;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 * JUnit-4 tests for {@link CachedDataSet}.
 * 
 * @author Guy Rixon
 */
public class CachedDataSetTest {
  
  @Test
  public void testDeletionOfCacheFiles() throws Exception {
    File output = File.createTempFile("xsams-mux-", ".xsams.xml");
    assertTrue(output.exists());
    
    Set<File> inputs = getDefaultInputsInTempFiles();
    Collator collator = new Collator(new FileOutputStream(output), inputs);
    
    CachedDataSet sut = new CachedDataSet(output, collator);
    assertTrue(output.exists());
    for (File f : inputs) {
      assertTrue(f.exists());
    }
    
    sut.delete();
    assertFalse(output.exists());
    for (File f : inputs) {
      assertFalse(f.exists());
    }
    
  }
  
  private Set<URL> getDefaultInputs() throws Exception {
    Set<URL> inputs = new HashSet<URL>(2);
    inputs.add(this.getClass().getResource("/chianti-ti.xml"));
    inputs.add(this.getClass().getResource("/chianti-fe.xml"));
    return inputs;
  }
  
  private Set<File> getDefaultInputsInTempFiles() throws Exception {
    Set<File> inputs = new HashSet<File>(2);
    inputs.add(transcribeResourceToTempFile("/chianti-ti.xml"));
    inputs.add(transcribeResourceToTempFile("/chianti-fe.xml"));
    return inputs;
  }
   
  private File transcribeResourceToTempFile(String resource) throws IOException {
    InputStream in = new BufferedInputStream(this.getClass().getResourceAsStream(resource));
    assertNotNull(in);
    File f = File.createTempFile("xsams-mux-web-testing", null);
    OutputStream out = new BufferedOutputStream(new FileOutputStream(f));
    try {
      while (true) {
        int c = in.read();
        if (c == -1) {
          break;
        }
        else {
          out.write(c);
        }
      }
      return f;
    } 
    finally {
      in.close();
      out.close();
    }
  }

}
