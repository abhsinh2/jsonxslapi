<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="xml" />
	
	<xsl:param name="parent" /> 
	<xsl:param name="size" />	
	
	<xsl:template match="/">
		<Machines>
			<xsl:for-each select="hosts/element">
				<Machine>
					<name><xsl:value-of select="host_name"/></name>
					<type>host</type>
					<parent><xsl:value-of select="$parent"/></parent>
					<size><xsl:value-of select="$size"/></size>
				</Machine>
			</xsl:for-each>
		</Machines>
	</xsl:template>	
	
</xsl:stylesheet>