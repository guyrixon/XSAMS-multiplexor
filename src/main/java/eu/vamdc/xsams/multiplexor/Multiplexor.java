package eu.vamdc.xsams.multiplexor;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import javax.xml.stream.XMLStreamException;

/**
 * Top-level object for multiplexing XSAMS document.
 * Applications should construct one instance per output document and then
 * invoke {@link #merge}.
 *
 * @author Guy Rixon
 */
public class Multiplexor {


  /**
   * Merge the XSAMS documents in the given inputs onto the given output.
   * The caller must close all inputs and the output after this method returns.
   *
   * @param in The inputs documents
   * @param out The output document.
   * @return Always null
   * @throws InterruptedException If execution is interrupted.
   * @throws ExecutionException If any input cannot be processed.
   * @throws XMLStreamException If any input cannot be opened.
   * @throws XMLStreamException If the output cannot be written.
   */
  public Object merge(List<Reader> in, Writer out)
      throws InterruptedException, ExecutionException, XMLStreamException, IOException {

    // Define the fragments expected in the XSAMS document.
    FragmentBundle disposal = new FragmentBundle();

    // Set up a task to parse each input document.
    List<Callable<Object>> tasks = new ArrayList<Callable<Object>>(in.size());
    for (Reader r : in) {
      tasks.add(new Analyzer(r, disposal));
    }

    // Run the parsing tasks in parallel. Assume that there will never be so
    // many inputs that they cannot all be done in parallel. All tasks run
    // to completion or failure, and the following loop throws if there is a
    // problem with any task.
    ExecutorService engine = Executors.newFixedThreadPool(in.size());
    try {
      List<Future<Object>> verdict = engine.invokeAll(tasks);
      for (Future<Object> v : verdict) {
        v.get(); // This throws if the task has failed.
      }
    }
    finally {
      engine.shutdown();
    }

    // Transcribe fragments to the output document.
    Synthesizer synthesizer = new Synthesizer();
    synthesizer.transcribe(out, disposal);

    return null;
  }

}
