package eu.vamdc.xsams.multiplexor;

import java.util.Collection;
import javax.xml.stream.events.XMLEvent;

/**
 * A {@link eu.vamdc.xsams.multiplexor.Fragment} that can be safely shared
 * between writing threads.
 * 
 * @author Guy Rixon
 */
public class SharedFragment extends Fragment {
  
  @Override
  public synchronized boolean add(XMLEvent e) {
    return super.add(e);
  }
  
  @Override
  public void add(int i, XMLEvent e) {
    throw new IllegalStateException("Events can only be added to the back of the queue");
  }
  
  @Override
  public boolean addAll(Collection<? extends XMLEvent> c) {
    return super.addAll(c);
  }
  
  @Override
  public boolean addAll(int i, Collection<? extends XMLEvent> c) {
    throw new IllegalStateException("Events can only be added to the back of the queue");
  }
}
