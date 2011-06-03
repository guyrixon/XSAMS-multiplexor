/**
 * An application to combine multiple XSAMS documents into one.
 * <p>
 * {@link eu.vamdc.xsams.multiplexor.Multiplexor} is the top-level API for
 * multiplexing. {@link eu.vamdc.xsams.multiplexor.App} handles the choice of
 * XSAMS inputs on the command line then creates and calls a multiplexor.
 * <p>
 * The multiplexor runs one {@link eu.vamdc.xsams.multiplexor.Analyzer}
 * per input. Each analyzer parses its input XSAMS into a sequence of
 * {@link eu.vamdc.xsams.multiplexor.Fragment} that should be preserved intact
 * in the merged document. These fragments are elements with element descendents
 * such as [TBD]. For each kind of recognized fragment there is a
 * {@link eu.vamdc.xsams.multiplexor.FragmentQueue} to which the analyzer writes
 * the fragments.
 * <p>
 * The program contains one {@link eu.vamdc.xsams.multiplexor.Synthesizer}.
 * This reads the fragment queues and assembles the output document.
 * <p>
 * The multiplexor runs each of the analyzers in a separate thread and waits
 * for all the threads to complete. It then runs the synthesizer. Thus,
 * reading the inputs is done in parallel but the writing of the output does
 * not overlap with the reading. This design is forced by the XSAMS schema:
 * all fragments of the same type have to appear together, but until all the
 * analyzers return there is no way to know if all fragment of a type have
 * been collected.
 */
package eu.vamdc.xsams.multiplexor;