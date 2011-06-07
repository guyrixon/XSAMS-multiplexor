package eu.vamdc.xsams.multiplexor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Iterator;
import javax.xml.stream.events.XMLEvent;

/**
 * A {@link eu.vamdc.xsams.multiplexor.SharedFragment} that may be to large
 * to hold in memory as a Java collection. (This is the general case.) The
 * large shared fragment is backed by a disc file.
 * <p>
 * The intended usage is that all the objects will be written to the file
 * before any are read. Reading back object while more are being written
 * <em>might</em> work but is not guaranteed.
 * 
 * @author Guy Rixon
 */
public class LargeSharedFragment extends SharedFragment {
  
  private File store;
  
  private ObjectOutputStream sink;
  
  private long count = 0;
  
  
  /**
   * Constructs a large shared fragment. The fragment is associated with
   * a disc file that will be deleted on exit of the JVM.
   * 
   * @throws IOException 
   */
  public LargeSharedFragment() throws IOException {
    store = File.createTempFile("xsams-mux-", ".dat");
    store.deleteOnExit();
    sink = new ObjectOutputStream(new FileOutputStream(store));
  }
  
  @Override
  public void add(XMLEvent e) {
    try {
      sink.writeObject(e);
      count++;
    } catch (IOException ex) {
      throw new RuntimeException("Disc buffer failed", ex);
    }
  }
  
  @Override
  public synchronized void addAll(Fragment f) {
    for (XMLEvent e : f) {
      add(e);
    }
  }
  
  @Override
  public Iterator<XMLEvent> iterator() {
    return new XmlEventFileIterator(store);
  }

  @Override
  public long size() {
    return count;
  }
  
}
