package eu.vamdc.xsams.multiplexor;

import java.io.Reader;
import java.util.concurrent.Callable;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

/**
 *
 * @author Guy Rixon
 */
public class Analyzer implements Callable {
  
  private XMLEventReader in;

  private FragmentBundle disposal;

  public Analyzer(Reader r, FragmentBundle b) throws XMLStreamException {
    in = XMLInputFactory.newFactory().createXMLEventReader(r);
    disposal = b;
  }

  /**
   * Reads the XSAMS fragments from the event source and adds them to the
   * appropriate queues.
   *
   * @return Always null.
   * @throws XMLStreamException If XML parsing fails.
   */
  @Override
  public Object call() throws XMLStreamException {

    boolean transcribing = false;
    QName kind = null;
    Fragment current = null;
    while (in.hasNext()) {
      XMLEvent event = in.nextEvent();

      if (!transcribing && event.isStartElement()) {
        QName q = event.asStartElement().getName();
        //System.out.println("**** " + q.getNamespaceURI().length() + ":" + q.getLocalPart());
        if (disposal.known(q)) {
          kind = q;
          transcribing = true;
          current = new Fragment();
        }
      }

      if (transcribing) {
        current.add(event);
      }

      if (transcribing && event.isEndElement()) {
        if (kind.equals(event.asEndElement().getName())) {
          transcribing = false;
          disposal.getFragmentForWriting(kind).addAll(current);
          current = null;
        }
      }
    }

    // Don't want to return anything, but Callable requires a return.
    return null;
  }

}
