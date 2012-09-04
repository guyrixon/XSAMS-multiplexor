package eu.vamdc.xsams.multiplexor.cl;

import eu.vamdc.xsams.multiplexor.mux.Collator;
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
    try {
      for (String s: args) {
        inputs.add(new URL(s));
      }
    }
    catch (Exception e) {
      System.err.println(e);
      System.exit(1);
    }
    
    Collator c = new Collator(inputs, System.out);
    try {
      c.collate();
    }
    catch (Exception e1) {
      System.err.println(e1);
      for (Exception e2: c.getErrors()) {
        System.err.println(e2);
        System.exit(1);
      }
    }
  }

}
