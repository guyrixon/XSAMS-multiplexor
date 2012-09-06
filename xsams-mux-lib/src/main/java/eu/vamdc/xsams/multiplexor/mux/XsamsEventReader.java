package eu.vamdc.xsams.multiplexor.mux;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.util.EventReaderDelegate;

/**
 * An event reader for one XML input. The XML is parsed normally, and instances
 * of this class generate the same events as would XMLEventReader. The differences
 * in this class lie in the constructors and the resource management.
 * <p>
 * The reader may be constructed on either a URL or a File as source of the
 * XML. When a file is used, that file may be marked as a cache file in
 * which case the file is deleted when the reader is closed.
 * 
 * @author Guy Rixon
 */
public class XsamsEventReader extends EventReaderDelegate {
  private final File cacheFile;
  
  private final boolean deleteFileOnClose;
  
  /**
   * Constructs a reader for data on a URL.
   * 
   * @param u The URL.
   * @throws XMLStreamException If the URL cannot be read.
   * @throws IOException If the URL cannot be read.
   */
  public XsamsEventReader(URL u) throws XMLStreamException, IOException {
    super(XMLInputFactory.newFactory().createXMLEventReader(u.openStream()));
    cacheFile = null;
    deleteFileOnClose = false;
  }
  
  /**
   * Constructs a reader for data in a file.
   * 
   * @param file The file.
   * @param isCache If true, the file is deleted when the reader is closed.
   * @throws FileNotFoundException If the file does not exist.
   * @throws XMLStreamException If the file cannot be read.
   */
  public XsamsEventReader(File file, boolean isCache) throws FileNotFoundException, XMLStreamException {
    super(XMLInputFactory.newFactory().createXMLEventReader(new FileInputStream(file)));
    cacheFile = file;
    deleteFileOnClose = isCache;
  }
  
  /**
   * Frees the resources associated with the reader. If the data source
   * was marked at construction as a cache file, deletes that file.
   * 
   * @throws XMLStreamException If the reader cannot close cleanly.
   */
  @Override
  public void close() throws XMLStreamException {
    super.close();
    if (deleteFileOnClose) {
      cacheFile.delete();
    }
  }

}
