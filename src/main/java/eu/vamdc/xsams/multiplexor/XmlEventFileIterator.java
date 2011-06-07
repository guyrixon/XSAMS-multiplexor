package eu.vamdc.xsams.multiplexor;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Iterator;
import javax.xml.stream.events.XMLEvent;

/**
 * An iterator for XMLEvent objects stored in a file.
 * The iterator reads the file in sequence and a given instance reads one
 * file, once. The iterator closes the file when the last object has been read.
 * 
 * @author Guy Rixon
 */
public class XmlEventFileIterator implements Iterator<XMLEvent> {
  
  /**
   * Stream view of the file.
   */
  private ObjectInputStream in;
  
  /**
   * Buffer for the event read most recently from the file.
   */
  private XMLEvent nextEvent;
  
  public XmlEventFileIterator(File f) {
    try {
      in = new ObjectInputStream(new FileInputStream(f));
    }
    catch (IOException e) {
      throw new RuntimeException("Failed to set up an iterator on the buffer file " + f, e);
    }
    nextEvent = null;
  }

  /**
   * Determines whether there are more objects to return.
   * 
   * @return True if there are more objects, false at EOF. 
   */
  @Override
  public boolean hasNext() {
    return (nextEvent != null);
  }

  /**
   * Returns the next object from the file.
   * 
   * @return The object (null means end of file). 
   */
  @Override
  public XMLEvent next() {
    loadNextEvent();
    return nextEvent;
  }

  /**
   * Removing objects from the sequence is not allowed.
   * 
   * @throws UnsupportedOperationException Always.
   */
  @Override
  public void remove() {
    throw new UnsupportedOperationException("Not supported");
  }
  
  /**
   * Loads the next event from the file to the buffer.
   * Handles EOF by annulling the buffer and closes the stream.
   * Wraps any other exceptions in runtime exceptions.
   * 
   * @throws RuntimeException For unrecoverable errors.
   */
  private void loadNextEvent()  {
    try {
      nextEvent = (XMLEvent) in.readObject();
    } 
    catch (EOFException e1) {
      nextEvent = null;
      try {
        in.close();
      } catch (IOException ex) {
        throw new RuntimeException("Error while closing the iterator's stream", ex);
      }
    }
    catch (Exception e2) {
      throw new RuntimeException(e2);
    }
  }
  
}
