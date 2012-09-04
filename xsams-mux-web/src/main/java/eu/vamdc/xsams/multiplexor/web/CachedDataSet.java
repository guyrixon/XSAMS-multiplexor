package eu.vamdc.xsams.multiplexor.web;

import java.io.File;
import java.net.URL;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;

/**
 *
 * @author Guy Rixon
 */
public class CachedDataSet {
  
  /**
   * The file to which the merged XSAMS is written.
   */
  private final File cacheFile;
  
  /**
   * The sources of the XSAMS to be merged.
   */
  private final Set<URL> originalUrls;
  
  /**
   * The timestamp of the cache entry. Used when purging the cache.
   */
  private final Date entryTime;
  
  /**
   * A connection to the thread doing the work.
   */
  private final Future<Object> future;
  
  /**
   * A progress counter. The value will be shown to the user when the
   * results are not ready. The counter works in units of XML events, so
   * the absolute value is not very interesting; increases in the counter show
   * that parsing is still in progress.
   */
  private AtomicLong progress;
  
  public CachedDataSet(Set<URL> u, File f, Future<Object> v, AtomicLong p) {
    this(u, f, v, p, new Date());
  }
  
  public CachedDataSet(File f) {
    this(null, f, null, new AtomicLong(), new Date());
  }
  
  protected CachedDataSet(Set<URL> u, File file, Future<Object> f, AtomicLong p, Date d) {
    cacheFile    = file;
    originalUrls = u;
    future       = f;
    entryTime    = d;
    progress     = p;
  }
  
  public AtomicLong getByteCounter() {
    return progress;
  }
  
  public File getCacheFile() {
    return cacheFile;
  }
  
  public Set<URL> getOriginalUrls() {
    return originalUrls;
  }
  
  public Date getEntryTime() {
    return entryTime;
  }
  
  public boolean isReady() throws DownloadException {
    if (future == null) {
      return true;
    }
    else {
      if (future.isDone()) {
        try {
          future.get();
        }
        catch (Exception e) {
          throw new DownloadException("Download failed", e.getCause());
        }
        return true;
      }
      else {
        return false;
      }
    }
  }
  
  public void delete() {
    if (future != null) {
      future.cancel(true);
    }
    if (cacheFile != null) {
      cacheFile.delete();
    }
  }
  
}
