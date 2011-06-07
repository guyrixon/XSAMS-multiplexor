package eu.vamdc.xsams.multiplexor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

/**
 * A fragment of an XSAMS document that must be kept intact inside a merged
 * document.
 * 
 * @author Guy Rixon
 */
public class BasicFragment implements Fragment {
  
  protected ArrayList<XMLEvent> events;
  
  public BasicFragment() {
    events = new ArrayList();
  }

  /**
   * Transcribes the content of the fragment to a given output.
   *
   * @param sink The output for the XML.
   * @throws XMLStreamException If transcription fails.
   */
  public void transcribe(XMLEventWriter sink) throws XMLStreamException {
    for (XMLEvent e: this) {
      sink.add(e);
    }
  }

  @Override
  public void add(XMLEvent x) {
    events.add(x);
  }

  @Override
  public void addAll(Fragment f) {
    for (XMLEvent e : f) {
      add(e);
    }
  }
  
  @Override
  public Iterator<XMLEvent> iterator() {
    return events.iterator();
  }
  
  @Override
  public long size() {
    return (long) events.size();
  }

}
