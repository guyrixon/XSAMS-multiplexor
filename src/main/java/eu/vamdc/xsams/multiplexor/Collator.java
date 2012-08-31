package eu.vamdc.xsams.multiplexor;

import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.XMLEvent;

/**
 * A collator for one set of XSAMS inputs, producing one XSAMS output.
 * Therefore, one "job" in the multiplexor system.
 * 
 * @author Guy Rixon
 */
public class Collator {
  
  public static final String XSAMS_NS_URI = "http://vamdc.org/xml/xsams/0.3";
  
  public static final QName XSAMSDATA    = new QName(XSAMS_NS_URI, "XSAMSData");
  public static final QName SPECIES      = new QName(XSAMS_NS_URI, "Species");
  public static final QName ATOMS        = new QName(XSAMS_NS_URI, "Atoms");
  public static final QName MOLECULES    = new QName(XSAMS_NS_URI, "Molecules");
  public static final QName PARTICLES    = new QName(XSAMS_NS_URI, "Particles");
  public static final QName SOLIDS       = new QName(XSAMS_NS_URI, "Solids");
  public static final QName PROCESSES    = new QName(XSAMS_NS_URI, "Processes");
  public static final QName COLLISIONS   = new QName(XSAMS_NS_URI, "Collisions");
  public static final QName RADIATIVE    = new QName(XSAMS_NS_URI, "Radiative");
  public static final QName NONRADIATIVE = new QName(XSAMS_NS_URI, "NonRadiative");
  public static final QName ENVIRONMENTS = new QName(XSAMS_NS_URI, "Environments");
  public static final QName SOURCES      = new QName(XSAMS_NS_URI, "Sources");
  public static final QName METHODS      = new QName(XSAMS_NS_URI, "Methods");
  public static final QName FUNCTIONS    = new QName(XSAMS_NS_URI, "Functions");
  public static final QName COMMENTS     = new QName(XSAMS_NS_URI, "Comments");
  
  public static final String XSI_NS_URI = "http://www.w3.org/2001/XMLSchema-instance";
  
  public static final QName SCHEMALOCATION = new QName(XSI_NS_URI, "schemaLocation");
  
  private Map<String,FragmentList> queues;
  
  private Set<URL> inputs;
  
  private CountDownLatch contributorCount;
  
  private List<Exception> inputErrors;
  
  
  public Collator(Set<URL> u) {
    inputs = u;
    
    int nContributors = u.size();
    contributorCount = new CountDownLatch(nContributors);
    inputErrors = new CopyOnWriteArrayList();
    
    
    // Set up the queues for the XSAMS fragments.
    // Analyzers will fill these queues and the Multiplexor will drain them.
    
    queues = new HashMap<String,FragmentList>();
    queues.put("Atom",                                   new MemoryFragmentList());
    queues.put("Molecule",                               new MemoryFragmentList());
    queues.put("Particle",                               new MemoryFragmentList());
    queues.put("Solid",                                  new MemoryFragmentList());
    queues.put("CollisionalTransition",                  new MemoryFragmentList());
    queues.put("AbsorbtionCrossSection",                 new MemoryFragmentList());
    queues.put("CollisionInducedAbsorbtionCrossSection", new MemoryFragmentList());
    queues.put("RadiativeTransition",                    new MemoryFragmentList());
    queues.put("NonRadiativeTransition",                 new MemoryFragmentList());
    queues.put("Environment",                            new MemoryFragmentList());
    queues.put("Source",                                 new MemoryFragmentList());
    queues.put("Method",                                 new MemoryFragmentList());
    queues.put("Function",                               new MemoryFragmentList());
    queues.put("Comments",                               new MemoryFragmentList());
  }
  
  public void collate(OutputStream output) throws XMLStreamException, Exception  {
    
    // Parse the inputs in parallel.
    Integer i = 0;
    for (URL u : inputs) {
      i++;
      String suffix = "_" + i.toString();
      Analyzer a = new Analyzer(u, queues, suffix, contributorCount, inputErrors);
      new Thread(a).start();
    }
    
    // Wait for all the analyzers to finish.
    contributorCount.await();
    if (inputErrors.size() > 0) {
      throw new Exception("Errors in inputs", inputErrors.get(0));
    }
    
    
    // Create the StaX apparatus to write the output.
    XMLEventFactory  eFactory = XMLEventFactory.newFactory();
    XMLOutputFactory oFactory = XMLOutputFactory.newFactory();
    oFactory.setProperty("javax.xml.stream.isRepairingNamespaces", true);
    XMLEventWriter out = oFactory.createXMLEventWriter(output, "UTF-8");
    out.setDefaultNamespace(XSAMS_NS_URI);
    
    
    // Create the output, adding the detail from the fragment queues.
    startDocument(eFactory, out);
    
    
    
    
    // @TODO: handle the comments. They have to be munged into one element.
    
    transcribeQueues(SOURCES,      eFactory, out, queues.get("Source"));   
    transcribeQueues(FUNCTIONS,    eFactory, out, queues.get("Function"));
    transcribeQueues(METHODS,      eFactory, out, queues.get("Method"));
    transcribeQueues(ENVIRONMENTS, eFactory, out, queues.get("Environment"));
    startElement(eFactory, SPECIES, out);
    transcribeQueues(ATOMS,     eFactory, out, queues.get("Atom"));
    transcribeQueues(MOLECULES, eFactory, out, queues.get("Molecule"));
    transcribeQueues(PARTICLES, eFactory, out, queues.get("Particle"));
    transcribeQueues(SOLIDS, eFactory, out, queues.get("Solid"));
    endElement(eFactory, SPECIES, out);
    startElement(eFactory, PROCESSES, out);

    transcribeQueues(RADIATIVE,    eFactory, out, queues.get("RadiativeTransition"), 
                                                 queues.get("AbsorbtionCrossSection"),
                                                 queues.get("CollisionInducedAbsorbtionCrossSection"));
    
    transcribeQueues(NONRADIATIVE, eFactory, out, queues.get("NonRadiativeTransition"));
    transcribeQueues(COLLISIONS,   eFactory, out, queues.get("CollisionalTransition"));
    endElement(eFactory, PROCESSES, out);
    
    endDocument(eFactory, out);
    
    out.close();
    output.close();
  }
  
  private void startDocument(XMLEventFactory factory, XMLEventWriter out) throws XMLStreamException {
    out.setPrefix("xsi", XSI_NS_URI);
    List<Attribute> attributes = new ArrayList<Attribute>(1);
    attributes.add(factory.createAttribute(SCHEMALOCATION, XSAMS_NS_URI+" "+XSAMS_NS_URI));
    out.add(factory.createStartElement(XSAMSDATA, attributes.iterator(), null));
    out.add(factory.createCharacters("\n"));
  }
  
  private void endDocument(XMLEventFactory factory, XMLEventWriter out) throws XMLStreamException {
    out.add(factory.createEndElement(XSAMSDATA, null));
    out.add(factory.createEndDocument());
  }
  
  private void startElement(XMLEventFactory factory, QName tag, XMLEventWriter out) throws XMLStreamException {
    out.add(factory.createStartElement(tag, null, null));
    out.add(factory.createCharacters("\n"));
  }
  
  private void endElement(XMLEventFactory factory, QName tag, XMLEventWriter out) throws XMLStreamException {
    out.add(factory.createEndElement(tag, null));
    out.add(factory.createCharacters("\n"));
  }
  
  /**
   * Copies the events from the given queues to the output, bracketing them
   * in events for start and end of a given element.
   * 
   * @param tag The name of the bracketing element.
   * @param factory The event factory.
   * @param out The output.
   * @param l The fragment queues.
   * @throws XMLStreamException
   * @throws Exception 
   */
  private void transcribeQueues(QName tag, XMLEventFactory factory, XMLEventWriter out, FragmentList... l) 
      throws XMLStreamException  {
    int totalEvents = 0;
    for (FragmentList q : l) {
      totalEvents += q.size();
    }
    if (totalEvents > 0) {
      startElement(factory, tag, out);
      for (FragmentList q : l) {
        for (Fragment f : q) {
          for (XMLEvent x : f) {
            out.add(x);
          }
        }
        out.add(factory.createCharacters("\n"));
      }
      endElement(factory, tag, out);
    }
  }

}
