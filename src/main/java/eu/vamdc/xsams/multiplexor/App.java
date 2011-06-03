package eu.vamdc.xsams.multiplexor;

import java.io.FileReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

/**
 * Application to merge XSAMS documents from files.
 * <p>
 * The merged document is written to standard output.
 * <p>
 * Each command-line argument is the name of one input file.
 * If there are no input files, an empty, valid  XSAMS document results
 * (i.e. it has the document element and the first-level children but no
 * other content).
 * <p>
 * On error, the program aborts. This may leave the output document incomplete.
 * If there is an error in processing one of the inputs, the program waits
 * for the other inputs to complete or fail before aborting; in this case, no
 * output at all is produced.
 *
 * @author Guy Rixon
 */
public class App {
  
  public static void main(String[] args) throws Exception {
    List<Reader> in = new ArrayList<Reader>(args.length);
    for (String s : args) {
      in.add(new FileReader(s));
    }
    Writer out = new OutputStreamWriter(System.out);
    new Multiplexor().merge(in, out);
  }

}
