package eu.vamdc.xsams.multiplexor;

import java.io.IOException;
import java.io.Writer;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;

/**
 *
 * @author Guy Rixon
 */
public class Synthesizer {

  /**
   * Transcribes the fragments of a document to a given output.
   * @param out The output.
   * @param disposal The document fragments.
   * @throws XMLStreamException If transcription of any fragment fails.
   */
  public void transcribe(Writer out, FragmentBundle disposal) 
      throws XMLStreamException, IOException {
    XMLEventWriter sink = XMLOutputFactory.newFactory().createXMLEventWriter(out);
    for (Fragment f : disposal) {
      f.transcribe(sink);
    }
    sink.close();
  }

}
