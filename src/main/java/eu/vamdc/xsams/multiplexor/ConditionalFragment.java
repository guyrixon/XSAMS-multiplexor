package eu.vamdc.xsams.multiplexor;

import java.util.Iterator;
import java.util.NoSuchElementException;
import javax.xml.stream.events.XMLEvent;

/**
 * A fragment that only yields content if another, given fragment has content.
 * <p>
 * Consider the construct where one or more XML elements of name Q are nested in
 * an element named Qs. The sequence of Q is modeled as a
 * {@link eu.vamdc.xsams.multiplexor.SharedFragment}, while the Qs is modeled
 * as a pair of conditional fragments, conditional on the shared fragment,
 * one representing the start tag and one the end tag. If the shared fragment
 * has content, then the conditional fragments yield their content too. If the
 * shared fragment has no content (because no Q elements were found in the inputs)
 * then the content of the conditional fragments is supressed: the Qs element
 * simply disappears from the output.
 * <p>
 * The conditional presentation of the content is done by the iterator: if the
 * master fragment is empty, the iterator yields no events. The other access
 * methods to a conditional fragment show the content whether or not the
 * master fragment has content.
 * 
 * @author Guy Rixon
 */
public class ConditionalFragment extends Fragment {

  private Fragment master;

  public ConditionalFragment(Fragment f) {
    super();
    master = f;
  }

  @Override
  public Iterator<XMLEvent> iterator() {
    if (master != null && master.size() > 0) {
      return super.iterator();
    }
    else {
      return new Iterator<XMLEvent>() {

        @Override
        public boolean hasNext() {
          return false;
        }

        @Override
        public XMLEvent next() {
          throw new NoSuchElementException();
        }

        @Override
        public void remove() {
          throw new UnsupportedOperationException("Not supported yet.");
        }

      };
    }
  }

}
