package eu.vamdc.xsams.multiplexor.mux;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import static org.junit.Assert.*;
import org.junit.Test;
import org.xml.sax.helpers.DefaultHandler;


/**
 * JUnit-4 tests for {@link Collator}.
 * 
 * @author Guy Rixon
 */
public class CollatorTest {
  
  @Test
  public void testReadFromUrl() throws Exception {
    File sink = new File("target", "collator-test.xml");
    OutputStream out  = new FileOutputStream(sink);
    Collator sut = new Collator(new HashSet<File>(0), getDefaultInputs(), out);
    sut.collate();
    assertEquals(0, sut.getErrors().size());
    validateXsamsOutput(sink);
  }
  
  @Test
  public void testReadFromFileUrl() throws Exception {
    File sink = new File("target", "collator-test.xml");
    OutputStream out  = new FileOutputStream(sink);
    Set<File> files = getDefaultInputsInFiles();
    Set<URL> urls = new HashSet<URL>(files.size());
    for (File f : files) {
      urls.add(new URL("file:" + f.getAbsolutePath()));
    }
    Collator sut = new Collator(new HashSet<File>(0), urls, out);
    sut.collate();
    assertEquals(0, sut.getErrors().size());
    for (File f: files) {
      assertTrue(f.exists()); // Collator must not destroy these inputs.
    }
    validateXsamsOutput(sink);
  }
  
  @Test
  public void testReadFromTempFile() throws Exception {
    File sink = new File("target", "collator-test.xml");
    OutputStream out  = new FileOutputStream(sink);
    Set<File> files = getDefaultInputsInFiles();
    Collator sut = new Collator(files, new HashSet<URL>(0), out);
    sut.collate();
    assertEquals(0, sut.getErrors().size());
    for (File f: files) {
      assertFalse(f.exists()); // Collator must destroy these inputs after use.
    }
    validateXsamsOutput(sink);
  }
  
  
  @Test
  public void testMixedRead() throws Exception {
    File sink = new File("target", "collator-test.xml");
    OutputStream out  = new FileOutputStream(sink);
    Set<File> files = getDefaultInputsInFiles();
    Collator sut = new Collator(getDefaultInputsInFiles(), getDefaultInputs(), out);
    sut.collate();
    validateXsamsOutput(sink);
  }
  
  
  
  private Set<URL> getDefaultInputs() throws Exception {
    Set<URL> inputs = new HashSet<URL>(2);
    inputs.add(this.getClass().getResource("/chianti-ti.xml"));
    inputs.add(this.getClass().getResource("/chianti-fe.xml"));
    return inputs;
  }
  
  private Set<File> getDefaultInputsInFiles() throws Exception {
    Set<File> inputs = new HashSet<File>(2);
    inputs.add(transcribeResourceToFile("/chianti-ti.xml"));
    inputs.add(transcribeResourceToFile("/chianti-fe.xml"));
    return inputs;
  }
   
  private File transcribeResourceToFile(String resource) throws IOException {
    String name = (resource.indexOf("/") == 0)? resource.substring(1) : resource;
    InputStream in = new BufferedInputStream(this.getClass().getResourceAsStream(resource));
    assertNotNull(in);
    File f = File.createTempFile("target", name);
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
  
  
  private void validateXsamsOutput(File xsams) throws Exception {
    SAXParserFactory factory = SAXParserFactory.newInstance();
    factory.setValidating(true);
    SAXParser parser = factory.newSAXParser();
    parser.parse(xsams, new DefaultHandler());
  }

}
