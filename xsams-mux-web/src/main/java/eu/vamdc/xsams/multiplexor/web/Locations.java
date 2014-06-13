package eu.vamdc.xsams.multiplexor.web;

import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author Guy Rixon
 */
public class Locations {
  
  public static String getRootLocation(HttpServletRequest request) {
    return String.format("http://%s:%d%s",
                         request.getServerName(),
                         request.getLocalPort(),
                         request.getContextPath());
  }
  
  public static String getServiceLocation(HttpServletRequest request) {
    return getRootLocation(request) + "/service";
  }
  
  public static String getCapabilitiesLocation(HttpServletRequest request) {
    return getRootLocation(request) + "/capabilities";
  }
  
  public static String getCapabilitiesCssLocation(HttpServletRequest request) {
    return getRootLocation(request) + "/Capabilities.xsl";
  }
  
  public static String getAvailabilityLocation(HttpServletRequest request) {
    return getRootLocation(request) + "/availability";
  }
  
  public static String getResultsCssLocation(HttpServletRequest request) {
    return getRootLocation(request) + "/xsams-views.css";
  }
  
}
