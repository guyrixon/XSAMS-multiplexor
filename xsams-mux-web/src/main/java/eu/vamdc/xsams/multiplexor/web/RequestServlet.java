package eu.vamdc.xsams.multiplexor.web;

import eu.vamdc.xsams.multiplexor.mux.Collator;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

/**
 *
 * @author Guy Rixon
 */
public class RequestServlet extends ErrorReportingServlet {
  
  private DataCache cache;

  @Override
  public void get(HttpServletRequest request, HttpServletResponse response) throws Exception {
    post(request, response);
  }

  @Override
  public void post(HttpServletRequest request, HttpServletResponse response) throws Exception {
    if ("application/x-www-form-urlencoded".equals(request.getContentType())) {
      LOG.debug("Handling application/x-www-form-urlencoded");
      String key = processUrls(request);
      redirect(request, key, response);
      LOG.info("New job " + key + " committed.");
    }
    else {
      LOG.debug("Handling multipart");
      String key = processUploadedFiles(request);
      redirect(request, key, response);
      LOG.info("New job " + key + " committed.");
    }
  }
  
  private String processUrls(HttpServletRequest request) throws Exception {
    Set<URL> urls = getUrls(request);
    File out = File.createTempFile("xsams-mux-", ".xsams.xml");
    Collator collator = new Collator(urls, new FileOutputStream(out));
    CachedDataSet data = new CachedDataSet(out, collator);
    String key = cache.put(data);
    new Thread(collator).start();
    return key;
  }
  
  private String processUploadedFiles(HttpServletRequest request) 
      throws FileUploadException, IOException, RequestException {
    try {
      Set<File> files = new HashSet<File>();
      
      // Create a factory for disk-based file items
      DiskFileItemFactory factory = new DiskFileItemFactory();
      factory.setSizeThreshold(0); // Send all files to disc.

      // Create a new file upload handler
      ServletFileUpload upload = new ServletFileUpload(factory);

      // Parse the request
      List<FileItem> items = upload.parseRequest(request);
      for (FileItem i : items) {
        if (!i.isFormField()) {
          DiskFileItem d = (DiskFileItem) i;
          files.add(d.getStoreLocation());
        }
      }
      
      // Form and submit the multiplexing job.
      File out = File.createTempFile("xsams-mux-", ".xsams.xml");
      Collator collator = new Collator(new FileOutputStream(out), files);
      CachedDataSet data = new CachedDataSet(out, collator);
      String key = cache.put(data);
      new Thread(collator).start();
      return key;
    }
    catch (Exception e) {
      throw new RequestException(e);
    }
  }
  
  /**
   * Redirects the client to the URL for the output of the mux.
   * 
   * @param request The HTTP request.
   * @param key The key identifying this job in the cache.
   * @param response The HTTP response.
   */
  private void redirect(HttpServletRequest request, String key, HttpServletResponse response) {
    String location = String.format("http://%s:%d%s/merged/%s",
                                    request.getServerName(),
                                    request.getLocalPort(),
                                    request.getContextPath(),
                                    key);
    response.setHeader("Location", location);
    response.setStatus(HttpServletResponse.SC_SEE_OTHER);
  }
 
  /**
   * Initializes the map of cached data.
   */
  @Override
  public void init() {
    cache = new DataCache();
    getServletContext().setAttribute(DataCache.CACHE_ATTRIBUTE, cache);
  }
  
  /**
   * Destroys the data cache, deleting the data.  
   */
  @Override
  public void destroy() {
    try {
      getServletContext().removeAttribute(DataCache.CACHE_ATTRIBUTE);
      cache.empty();
      cache = null;
      
    }
    catch (Exception e) {
     LOG.error("Failed to delete the data cache", e);
    }
  }
  
  /**
   * Supplies the value of a parameter, applying some checks.
   * 
   * @param request The HTTP request containing the parameter.
   * @param name The name of the parameter
   * @return The value of the parameter, stripped of leading and trailing white space.
   * @throws RequestException If the parameter is not present in the request.
   * @throws RequestException If the parameter's value is an empty string.
   */
  private String getParameter(HttpServletRequest request, String name) 
      throws RequestException {
    String value = request.getParameter(name);
    if (value == null) {
      return null;
    }
    else {
      String trimmedValue = value.trim();
      if (trimmedValue.length() == 0) {
        throw new RequestException("Parameter " + name + " is empty");
      }
      else {
        return trimmedValue;
      }
    }
  }
  
  private Set<URL> getUrls(HttpServletRequest request) throws RequestException {
    String[] values = request.getParameterValues("url");
    if (values == null) {
      throw new RequestException("Please set the url parameter or upload a file"); 
    }
    
    Set<URL> urls = new HashSet<URL>(values.length);
    for (String s : values) {
      String value = s.trim();
      try {
        URL u = new URL(value);
        LOG.debug("Accepted URL " + u + " as a data source");
        urls.add(u);
      }
      catch (MalformedURLException e) {
        throw new RequestException("'" + value + "' is not a valid URL");
      }
    }
    return urls;
  }

  
  
}
