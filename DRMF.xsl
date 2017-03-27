<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet exclude-result-prefixes="ltx" version="1.0" xmlns:ltx="http://dlmf.nist.gov/LaTeXML" xmlns:m="http://www.w3.org/1998/Math/MathML" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xpath-default-namespace="http://www.w3.org/1998/Math/MathML">
	<!-- Include all LaTeXML to xhtml modules -->
	<xsl:import href="LaTeXML-common.xsl"/>
	<xsl:import href="LaTeXML-math-xhtml.xsl"/>
	<xsl:template match="m:csymbol[@cd='ambiguous' and text() = 'superscript']">
		<power xmlns="http://www.w3.org/1998/Math/MathML">
			<xsl:copy-of select="@id|@xref"/>
		</power>
	</xsl:template>
</xsl:stylesheet>
