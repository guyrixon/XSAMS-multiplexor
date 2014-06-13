<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xmlns:avail="http://www.ivoa.net/xml/VOSIAvailability/v1.0">

  <xsl:template match="/avail:availability">
    <html>
      <head>
        <title>Service availbility</title>
      </head>
      <body>
        <h1>Service availability</h1>
        <p>
          <xsl:text>Service is currently available: </xsl:text>
          <xsl:value-of select="avail:available"/>
        </p>
      </body>
    </html>
  </xsl:template>

</xsl:stylesheet>