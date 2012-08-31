/**
 * Classes for collating XSAMS documents into a single document.
 * <p>
 * XSAMS contains certain elements with special characteristics. These elements
 * </p>
 * <ol>
 * <li>occur in repeating sequences;
 * <li>have sub-structure to carry the scientific detail;
 * <li>have super-structure that is the same in every XSAMS instance.
 * </ol>
 * <p>
 * E.g. an <i>Atom</i> element has a deep sub-tree of elements describing the
 * atom and its states. It always appears inside the element at the XPath 
 * <i>/XSAMSData/Species/Atoms</i>.
 * <p>
 * The strategy of the multiplexor is accumulate sequences of the special 
 * elements in files, one file per kind of special element. These sequences
 * can then be combined in a new, entirely-predictable XSAMS-superstructure to
 * form the output.
 * <p>
 * An Analyzer object reads XML from one URL and detects the special elements.
 * It adds each such element, with the element's sub-structure, to a queue for 
 * that kind of element. Each Analyzer writes to multiple queues.
 * <p>
 * A multiplexor object reads the elements back from the queues and writes them
 * to the output. The Multiplexor creates the XSAMS superstructure as it goes.
 * <p>
 * Each job on the application creates one Analyzer per data source and exactly
 * one multiplexor. These objects are discarded at the end of the job.
 * <p>
 * The Analyzers and the Multiplexor can be run in parallel, each in its own 
 * thread. Analyzers block if a queue becomes full, and the Multiplexor blocks
 * when reading a queue that is currently empty.
 * <p>
 * Each FragmentQueue maintains the queue for one type of special element. It
 * provides standard, Java queuing-semantics and adds a counter indicating
 * how many threads are still providing input. Each analyzer decrements the
 * counter as it finishes its input data. The Multiplexor checks the counter
 * before asking for data from the queue. If the counter is positive, the
 * Multiplexor reads from the queue and may block in doing so. If the
 * counter is zero, the Multiplexor reads the queue speculatively: if there
 * are data the Multiplexor reads them; if not, the Multiplexor does not block
 * and is finished with that queue.
 * <p>
 * A FragmentQueue may be implemented to store the queued data in memory, or
 * in a file. The file implementation is cumbersome but allows a longer queue.
 */
package eu.vamdc.xsams.multiplexor;
