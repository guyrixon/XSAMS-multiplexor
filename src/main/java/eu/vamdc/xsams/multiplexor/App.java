/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package eu.vamdc.xsams.multiplexor;

import java.net.URL;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Guy Rixon
 */
public class App {
  
  public static void main(String[] args) throws Exception {
    Set<URL> inputs = new HashSet<URL>(args.length);
    for (String s: args) {
      inputs.add(new URL(s));
    }
    Collator c = new Collator(inputs);
    c.collate(System.out);
  }

}
