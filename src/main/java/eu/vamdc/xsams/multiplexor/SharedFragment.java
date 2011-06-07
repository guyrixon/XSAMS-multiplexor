package eu.vamdc.xsams.multiplexor;

import javax.xml.stream.events.XMLEvent;

/**
 * A {@link eu.vamdc.xsams.multiplexor.BasicFragment} that can be safely shared
 * between writing threads.
 * 
 * @author Guy Rixon
 */
public class SharedFragment extends BasicFragment {
  
  @Override
  public synchronized void add(XMLEvent e) {
    super.add(e);
  }
  
}
