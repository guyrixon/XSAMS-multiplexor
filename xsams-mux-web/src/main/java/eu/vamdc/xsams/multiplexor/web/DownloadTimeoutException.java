package eu.vamdc.xsams.multiplexor.web;

/**
 *
 * @author Guy Rixon
 */
public class DownloadTimeoutException extends DownloadException {
  
  public DownloadTimeoutException(String message) {
    super(message);
  }
  
  public DownloadTimeoutException(String message, Throwable t) {
    super(message, t);
  }
  
}
