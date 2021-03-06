/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package eu.vamdc.xsams.multiplexor.mux;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

/**
 *
 * @author Guy Rixon
 */
public class Analyzer implements Runnable {
  
  private Map<String,FragmentList> queues;
  
  private URL remoteSource;
  
  private File localSource;
  
  private String suffix;
  
  private CountDownLatch latch;
  
  private List<Exception> errors;
  
  private XMLEventFactory  factory;
  
  private XsamsEventReader reader;
  
  
  private final static List<String> idAttributes = Arrays.asList(new String[] {
      "methodID", "sourceID", "functionID", "speciesID", "stateID", "environmentID", "processID",
      "methodRef", "sourceRef", "functionRef", "speciesRef", "stateRef", "envRef", "processRef",
      "id"
    });
  
  private final static List<String> idElements = Arrays.asList(new String[] {
    "StateRef", "UpperStateRef", "LowerStateRef", "SpeciesRef", "SourceRef"
  });
  
  public Analyzer(URL u, Map<String,FragmentList> q, String s, CountDownLatch l, List<Exception> e) throws XMLStreamException, IOException {
    remoteSource = u;
    localSource  = null;
    queues = q;
    latch  = l;
    errors = e;
    suffix = s;
    
    factory = XMLEventFactory.newFactory();
    
    reader = new XsamsEventReader(u);
  }
  
  public Analyzer(File f, Map<String,FragmentList> q, String s, CountDownLatch l, List<Exception> e) throws FileNotFoundException, XMLStreamException {
    remoteSource = null;
    localSource  = f;
    queues = q;
    latch  = l;
    errors = e;
    suffix = s;
    
    factory = XMLEventFactory.newFactory();
    
    reader = new XsamsEventReader(f, true);
  }

  @Override
  public void run() {
    try {
      parseSource();
    }
    catch (Exception e) {
      errors.add(e);
    }
    finally {
      latch.countDown();
    }
  }
  
  /**
   * Parses the XSAMS document in the given file {@link #source} into {@link Fragment}
   * objects. The fragments are written to the queues set at construction.
   * 
   * @throws XMLStreamException If the data at the source URL cannot be read as XML.
   * @throws IOException If the source URL cannot be read.
   */
  private void parseSource() throws XMLStreamException, IOException {
    try {
      parseDocument();
    }
    finally {
      reader.close();
    }
  }

  private void parseDocument() throws XMLStreamException {
    while (reader.hasNext()) {
      XMLEvent e = reader.nextEvent();
      if (e.isStartElement()) {
        String tag = e.asStartElement().getName().getLocalPart();
        if (queues.containsKey(tag)) {
           parseFragment(e.asStartElement(), tag);
        }
      }
    }
  }

  /**
   * Parses a fragment deemed interesting. The fragment starts with the
   * given StartElement event and ends with the matching EndElement event.
   * These two events, and all events between them are copied to the
   * fragment list. Where ID values appear, they are given a suffix to make
   * them unique in the output.
   * 
   * @param in The event source.
   * @param start The event representing the element constituting the fragment.
   * @param tag The local name of the element.
   * @throws XMLStreamException 
   */
  private void parseFragment(StartElement start, String tag) throws XMLStreamException {
    FragmentList q = queues.get(tag);
    Fragment f = new Fragment();
    f.add(modifyStartElement(start));
    boolean elementClosed = false;
    while (reader.hasNext() && !elementClosed) {
      XMLEvent e = reader.nextEvent();
      if (e.isStartElement()) {
        f.add(modifyStartElement(e.asStartElement()));
      }
      else if (e.isEndElement()) {
        // If this element takes an ID value, add a suffix to the end of that
        // value to make it unique in the output.
        if (idElements.contains(e.asEndElement().getName().getLocalPart())) {
          f.add(factory.createCharacters(suffix));
        }
        f.add(e);
        elementClosed = e.asEndElement().getName().getLocalPart().equals(tag);
      }
      else {
        f.add(e);
      }
      
    }
    q.add(f);
  }
  
  /**
   * Corrects the attribute values in a StartEelement event. Attributes that
   * are ID values are given a suffix to make sure that they are unique in the
   * output.
   * 
   * @param e The event.
   * @return A new, equivalent event with corrected attributes.
   */
  private StartElement modifyStartElement(StartElement e) {
    List<Attribute> attributes = new ArrayList<Attribute>();
    Iterator<Attribute> i = e.getAttributes();
    while (i.hasNext()) {
      attributes.add(modifyAttribute(i.next()));
    }
    return factory.createStartElement(e.getName(), attributes.iterator(), e.getNamespaces());
  }
  
  
  /**
   * Corrects one attribute value. Attributes that are ID values are given a 
   * suffix to make sure that they are unique in the output.
   * 
   * @param e The event.
   * @return A new, equivalent event with corrected attributes.
   */
  private Attribute modifyAttribute(Attribute a) {
    QName name = a.getName();
    if (idAttributes.contains(name.getLocalPart())) {
      return factory.createAttribute(name, a.getValue()+suffix);
    }
    else {
      return a;
    }
  }

}
