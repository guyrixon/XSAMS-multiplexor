<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>XSAMS merger</title>
        <link rel="stylesheet" href="xsams-views.css" type="text/css"/>
        <script type="text/javascript">
          function addUpload() {
            var newInput = document.createElement('input');
            newInput.type='file';
            newInput.name='upload';
            var newItem = document.createElement('li');
            newItem.textContent = 'Merge local file:';
            newItem.appendChild(newInput);
            document.getElementById('file-list').appendChild(newItem);
          }
          function addUrl() {
            var newInput = document.createElement('input');
            newInput.type='text';
            newInput.name='url';
            newInput.size='128';
            var newItem = document.createElement('li');
            newItem.textContent = 'Merge file at URL:';
            newItem.appendChild(newInput);
            document.getElementById('file-list').appendChild(newItem);
          }
        </script>
    </head>
    <body>
        <h1>XSAMS merger</h1>
        This application merges two or more XSAMS documents into one.
        <form action="service" method="post" enctype="multipart/form-data">
          <p>Files to be merged 
            (<button type="button" onclick="addUpload();">Add a local file</button>,
            <button type="button" onclick="addUrl();">Add a URL</button>):
          </p>
          <ul id="file-list">
          </ul>
          <p>
            <input type="submit" value="Merge"/>
          </p>
        </form>
        <p><a href="capabilities">Service capabilities (for the VAMDC registry).</a></p>
    </body>
</html>
