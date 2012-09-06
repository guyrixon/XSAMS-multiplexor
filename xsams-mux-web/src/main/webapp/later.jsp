<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="refresh" content="5"/>
    <title>Download in progress</title>
  </head>
  <body>
    <h1>Download in progress</h1>
    <p>The data are being downloaded and parsed. Reload this page to see the results (reloading
       is done automatically in most browsers).</p>
    <p>Inputs still being parsed: <%=request.getAttribute("eu.vamdc.xsams.multiplexor.contributors")%></p>
  </body>
</html>
