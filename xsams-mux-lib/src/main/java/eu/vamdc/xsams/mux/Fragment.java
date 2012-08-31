package eu.vamdc.xsams.mux;

import java.util.ArrayList;
import javax.xml.stream.events.XMLEvent;

/**
 * A fragment of XML. The content is a sequence of XMLEvent objects. This
 * sequence is expected to contain all the events representing an XML element
 * and that element's substructure.
 * 
 * @author Guy Rixon
 */
public class Fragment extends ArrayList<XMLEvent> {}
