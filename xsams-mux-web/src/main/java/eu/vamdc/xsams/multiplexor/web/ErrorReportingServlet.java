package eu.vamdc.xsams.multiplexor.web;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Guy Rixon
 */
public abstract class ErrorReportingServlet extends HttpServlet {
  
  protected static final Logger LOG = LoggerFactory.getLogger(ErrorReportingServlet.class);
  
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) 
    throws IOException {
    try {
      get(request, response);
    }
    catch (RequestException e) {
      LOG.error("Request rejected", e);
      response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.toString());
    }
    catch (DownloadTimeoutException e) {
      LOG.error("Request failed", e);
      response.sendError(HttpServletResponse.SC_GATEWAY_TIMEOUT, e.toString());
    }
    catch (DownloadException e) {
      LOG.error("Request failed", e);
      response.sendError(HttpServletResponse.SC_BAD_GATEWAY, e.toString());
    }
    catch (Exception e) {
      LOG.error("Request failed", e);
      response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.toString());
    }
  }
  
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) 
      throws ServletException, IOException {
    try {
      post(request, response);
    }
    catch (RequestException e) {
      LOG.error("Request rejected", e);
      response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.toString());
    }
    catch (DownloadTimeoutException e) {
      LOG.error("Request failed", e);
      response.sendError(HttpServletResponse.SC_GATEWAY_TIMEOUT, e.toString());
    }
    catch (DownloadException e) {
      LOG.error("Request failed", e);
      response.sendError(HttpServletResponse.SC_BAD_GATEWAY, e.toString());
    }
    catch (Exception e) {
      LOG.error("Request failed", e);
      response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.toString());
    }
  }
  
  public abstract void get(HttpServletRequest request, HttpServletResponse response) throws Exception;
  
  public abstract void post(HttpServletRequest request, HttpServletResponse response) throws Exception;
  
}
