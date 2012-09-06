package eu.vamdc.xsams.multiplexor.web;

import eu.vamdc.xsams.multiplexor.mux.Collator;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Guy Rixon
 */
public class CachedDataSet {
  
  /**
   * The file to which the merged XSAMS is written.
   */
  private final File outputFile;
  
  /**
   * The timestamp of the cache entry. Used when purging the cache.
   */
  private final Date entryTime;
  
  /**
   * The object doing the work. This object will be running in its own
   * thread. Status of the job can be got from the object itself.
   */
  private final Collator collator;
  
  
  public CachedDataSet(File out, Collator c) throws IOException {
    this(c, out, new Date());
  }
  
  protected CachedDataSet(Collator c, File out, Date d) throws IOException {
    collator    = c;
    entryTime   = d;
    outputFile  = out;
  }
  
  public File getCacheFile() {
    return outputFile;
  }
  
  public Set<URL> getOriginalUrls() {
    return collator.getInputUrls();
  }
  
  public Collator getCollator() {
    return collator;
  }
  
  public Date getEntryTime() {
    return entryTime;
  }
  
  public boolean isReady() throws DownloadException {
    return collator.isFinished();
  }
  
  public void delete() {
    if (outputFile != null) {
      outputFile.delete();
    }
    for (File f : collator.getInputFiles()) {
      f.delete();
    }
  }
  
}
