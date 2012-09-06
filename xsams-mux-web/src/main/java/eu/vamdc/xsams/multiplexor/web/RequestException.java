package eu.vamdc.xsams.multiplexor.web;

import javax.servlet.ServletException;


/**
 * Exception indicating an error in the parameters of a request.
 * Typically, such an exception would be caught and turned into an HTTP
 * response with code 400 "bad request".
 * 
 * @author Guy Rixon
 */
public class RequestException extends ServletException {
  
  public RequestException(String message) {
    super(message);
  }
  
  public RequestException(Throwable cause) {
    super(cause);
  }
  
  public RequestException(String message, Throwable cause) {
    super(message, cause);
  }
  
}
