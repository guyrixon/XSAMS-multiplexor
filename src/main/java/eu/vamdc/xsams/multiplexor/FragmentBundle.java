package eu.vamdc.xsams.multiplexor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.events.XMLEvent;

/**
 * A set of fragments covering the required types of elements. Each fragment
 * contains body elements of a single type (e.g. Source) contained in an
 * enclosing element (e.g. Sources). The body elements are added by the callers.
 * The tags for the enclosing element are added by this class.
 * <p>
 * Callers wishing to write body elements to a fragment call
 * {@link #getFragmentForWriting} to get access. Callers wishing to transcribe
 * the fragments into an output document use the iterator API to get the 
 * fragments in the correct order.
 * 
 * @author Guy Rixon
 */
public class FragmentBundle implements Iterable<BasicFragment> {

  public static final String XSAMS_NS_URI = "";

  /**
   * A device for making start and end tags of elements.
   */
  private final XMLEventFactory factory;

  /**
   * The fragments for shared content. Keys are names of body elements.
   */
  private final Map<QName,SharedFragment> fragments;

  private final List<BasicFragment> queue;
  
  /**
   * Indicates whether the shared fragments should be large (i.e.
   * file-backed, the normal case) or small (memory-backed, the special
   * case for quick processing of small inputs).
   */
  private boolean largeFragments;
  
  public FragmentBundle() throws IOException {
    this(true);
  }

  public FragmentBundle(boolean large) throws IOException {

    largeFragments = large;
    factory = XMLEventFactory.newFactory();
    fragments = new HashMap<QName,SharedFragment>();
    queue = new ArrayList<BasicFragment>();

    // Each call in this section adds one or more fragments to the queue.
    // Calls to backetedSequence and plainSequence add shared fragments
    // that collect content from the input documents. Other calls
    // just add literal elements.
    documentStart();
    bracketedSequence("Source", "Sources");
    startTag("States");
    bracketedSequence("Atom", "Atoms");
    bracketedSequence("Molecule", "Molecules");
    bracketedSequence("Solid", "Solids");
    bracketedSequence("Particle", "Particles");
    endTag("States");
    startTag("Processes");
    bracketedSequence("RadiativeTransition", "Radiative");
    bracketedSequence("NonRadiativeTransition", "NonRadiative");
    bracketedSequence("CollisionalTransition", "Collisions");
    endTag("Processes");
    bracketedSequence("Method", "Methods");
    bracketedSequence("Function", "Functions");
    plainSequence("Comment");
    documentEnd();
  }

  /**
   * Indicates whether there is a fragment for a given type of body element.
   *
   * @param q The name of the element.
   * @return True if there is a suitable fragment.
   */
  public boolean known(QName q) {
    return fragments.containsKey(q);
  }

  /**
   * Supplies the fragment for a given kind of body-element.
   *
   * @param kind The kind of body element.
   * @return The fragment (null if no known fragment for this kind of body element).
   * @throws IllegalStateException If the fragments are not open for writing.
   */
  public SharedFragment getFragmentForWriting(QName kind) {
    return fragments.get(kind);
  }

  /**
   * Makes the bundle iterable.
   *
   * @return The iterator for the fragments.
   * @throws IllegalStateException If the fragments are not closed for transcription.
   */
  @Override
  public Iterator<BasicFragment> iterator() {
    return queue.iterator();
  }

  private void documentStart() {
    BasicFragment f = new BasicFragment();
    f.add(factory.createStartDocument());
    f.add(factory.createStartElement(new QName(null,"XSAMSData"), null, null));
    f.add(factory.createCharacters("\n"));
    queue.add(f);
  }

  private void documentEnd() {
    BasicFragment f = new BasicFragment();
    f.add(factory.createEndElement(new QName(null,"XSAMSData"), null));
    f.add(factory.createCharacters("\n"));
    f.add(factory.createEndDocument());
    queue.add(f);
  }

  /**
   * Adds to the queue a shared fragment backed by two conditional
   * fragments, the latter representing the start and end tags of a
   * containing element.
   *
   * @param body Local name of body elements to go in the shared fragment.
   * @param container Local name of the container element.
   */
  private void bracketedSequence(String body, String container) throws IOException {
    SharedFragment f2 = (largeFragments)? new LargeSharedFragment() : new SharedFragment();
    fragments.put(qName(body), f2);

    ConditionalFragment f1 = new ConditionalFragment(f2);
    f1.add(startElement(container));
    f1.add(factory.createCharacters("\n"));

    ConditionalFragment f3 = new ConditionalFragment(f2);
    f3.add(endElement(container));
    f3.add(factory.createCharacters("\n"));

    queue.add(f1);
    queue.add(f2);
    queue.add(f3);
  }

  private void plainSequence(String body) throws IOException {
    SharedFragment f2 = (largeFragments)? new LargeSharedFragment() : new SharedFragment();
    fragments.put(qName(body), f2);
    queue.add(f2);
  }

  private QName qName(String local) {
    return new QName(XSAMS_NS_URI, local);
  }

  private void startTag(String local) {
    BasicFragment f = new BasicFragment();
    f.add(startElement(local));
    f.add(factory.createCharacters("\n"));
    queue.add(f);
  }

  
  private void endTag(String local) {
    BasicFragment f = new BasicFragment();
    f.add(endElement(local));
    f.add(factory.createCharacters("\n"));
    queue.add(f);
  }

  

  private XMLEvent startElement(String local) {
    return factory.createStartElement(qName(local), null, null);
  }

  private XMLEvent endElement(String local) {
    return factory.createEndElement(qName(local), null);
  }

}
