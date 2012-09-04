package eu.vamdc.xsams.multiplexor.web;

/**
 *
 * @author Guy Rixon
 */
public class DownloadException extends Exception {
  
  public DownloadException(String message) {
    super(message);
  }
  
  public DownloadException(String message, Throwable t) {
    super(message, t);
  }
  
}
