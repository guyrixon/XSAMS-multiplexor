/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.vamdc.xsams.multiplexor;

import java.util.Collection;
import javax.xml.stream.events.XMLEvent;

/**
 *
 * @author Guy Rixon
 */
public interface Fragment extends Iterable<XMLEvent> {
  
  public void add(XMLEvent x);
  
  public void addAll(Fragment f);
  
  public long size();
  
}
