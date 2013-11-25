<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>XSAMS merger</title>
        <link rel="stylesheet" href="xsams-views.css" type="text/css"/>
    </head>
    <body>
        <h1>XSAMS merger</h1>
        This application merges two (or more) XSAMS documents into one.
        <!--
        <form  action="service" method="post" enctype="application/x-www-form-urlencoded">
          <p>
            Read from this URL: 
            <input type="text" size="96" name="url"/>
            <input type="submit"/>
          </p>
        </form>
        <p><strong>or</strong></p>
        -->
        <form  action="service" method="post" enctype="multipart/form-data">
          <p>
            Merge this file:
            <input type="file" name="upload" size="128">
          </p>
          <p>
            ...with this file:
            <input type="file" name="upload" size="128">
          </p>
          <p>
            <input type="submit"/>
          </p>
        </form>
        <p><a href="capabilities">Service capabilities (for the VAMDC registry).</a></p>
    </body>
</html>
