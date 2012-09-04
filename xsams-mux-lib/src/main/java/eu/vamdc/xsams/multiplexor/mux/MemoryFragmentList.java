/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package eu.vamdc.xsams.multiplexor.mux;

import java.util.ArrayList;

/**
 *
 * @author Guy Rixon
 */
public class MemoryFragmentList extends ArrayList<Fragment> implements FragmentList {
  
  @Override
  public synchronized boolean add(Fragment f) {
    return super.add(f);
  }

}
