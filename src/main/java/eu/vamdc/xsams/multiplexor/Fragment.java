package eu.vamdc.xsams.multiplexor;

import java.util.ArrayList;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

/**
 * A fragment of an XSAMS document that must be kept intact inside a merged
 * document.
 * 
 * @author Guy Rixon
 */
public class Fragment extends ArrayList<XMLEvent> {

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

}
