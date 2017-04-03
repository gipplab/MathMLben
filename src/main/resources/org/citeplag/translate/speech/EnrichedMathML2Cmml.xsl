<?xml version="1.0" encoding="ISO-8859-1"?>

<!-- BLUB TODO -->
<xsl:stylesheet version="2.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns="http://www.w3.org/1998/Math/MathML"
                xpath-default-namespace="http://www.w3.org/1998/Math/MathML">
    <xsl:output method="xml" indent="yes"/>

    <!-- outer row without Id is only for formatting, just go one element deeper -->
    <xsl:template match="mrow[not(@data-semantic-id)]">
        <xsl:apply-templates />
    </xsl:template>

    <!-- fencing around the children, apply templates on the children only -->
    <xsl:template match="mrow[@data-semantic-type='fenced']">
        <!-- iterate over the children -->
        <xsl:call-template name="iterateList">
            <xsl:with-param name="list" select="@data-semantic-children"/>
            <xsl:with-param name="separator" select="','"/>
        </xsl:call-template>
    </xsl:template>

    <!-- Operation: infixop, relseq, multirel (plus or minus) -->
    <xsl:template match="mrow[@data-semantic-type='infixop' or @data-semantic-type='relseq' or @data-semantic-type='multirel']">
        <apply>
            <!-- set id attributes -->
            <xsl:attribute name="id">c<xsl:value-of select="@data-semantic-id"/></xsl:attribute>
            <xsl:attribute name="xref">
                <xsl:value-of select="@id"/>
            </xsl:attribute>

            <!-- take only the first content node, in case the expression is a+b+c -->
            <xsl:variable name="temp" select="tokenize(@data-semantic-content,',')[1]"/>
            <xsl:apply-templates select="//*[@data-semantic-id=$temp]"/>

            <!-- iterate over all children -->
            <xsl:call-template name="iterateList">
                <xsl:with-param name="list" select="@data-semantic-children"/>
                <xsl:with-param name="separator" select="','"/>
            </xsl:call-template>
        </apply>
    </xsl:template>

    <!-- Operation: appl (simple function) -->
    <xsl:template match="mrow[@data-semantic-type='appl']">
        <apply>
            <!-- set id attributes -->
            <xsl:attribute name="id">c<xsl:value-of select="@data-semantic-id"/></xsl:attribute>
            <xsl:attribute name="xref">
                <xsl:value-of select="@id"/>
            </xsl:attribute>

            <!-- iterate over all children -->
            <xsl:call-template name="iterateList">
                <xsl:with-param name="list" select="@data-semantic-children"/>
                <xsl:with-param name="separator" select="','"/>
            </xsl:call-template>
        </apply>
    </xsl:template>

    <!-- Operation: fraction -->
    <xsl:template match="mfrac">
        <xsl:element name="apply">
            <!-- set id attributes -->
            <xsl:attribute name="id">c<xsl:value-of select="@data-semantic-id"/></xsl:attribute>
            <xsl:attribute name="xref"><xsl:value-of select="@id"/></xsl:attribute>

            <!-- inner element (oeprator): divide -->
            <xsl:element name="divide">
                <!-- set (unique) id attributes -->
                <xsl:attribute name="id">u<xsl:value-of select="@data-semantic-id"/></xsl:attribute>
                <xsl:attribute name="xref"><xsl:value-of select="@id"/></xsl:attribute>
            </xsl:element>

            <!-- iterate over the children -->
            <xsl:call-template name="iterateList">
                <xsl:with-param name="list" select="@data-semantic-children"/>
                <xsl:with-param name="separator" select="','"/>
            </xsl:call-template>
        </xsl:element>
    </xsl:template>

    <!-- Operation: square root -->
    <xsl:template match="msqrt">
        <xsl:element name="apply">
            <!-- set id attributes -->
            <xsl:attribute name="id">c<xsl:value-of select="@data-semantic-id"/></xsl:attribute>
            <xsl:attribute name="xref"><xsl:value-of select="@id"/></xsl:attribute>

            <!-- inner element (operator): root -->
            <xsl:element name="root">
                <!-- set (unique) id attributes -->
                <xsl:attribute name="id">u<xsl:value-of select="@data-semantic-id"/></xsl:attribute>
                <xsl:attribute name="xref"><xsl:value-of select="@id"/></xsl:attribute>
            </xsl:element>

            <!-- iterate over the children -->
            <xsl:call-template name="iterateList">
                <xsl:with-param name="list" select="@data-semantic-children"/>
                <xsl:with-param name="separator" select="','"/>
            </xsl:call-template>
        </xsl:element>
    </xsl:template>

    <!-- Operation: square root -->
    <xsl:template match="mroot">
        <xsl:element name="apply">
            <!-- set id attributes -->
            <xsl:attribute name="id">c<xsl:value-of select="@data-semantic-id"/></xsl:attribute>
            <xsl:attribute name="xref"><xsl:value-of select="@id"/></xsl:attribute>

            <!-- inner element (operator): root -->
            <xsl:element name="root">
                <!-- set (unique) id attributes -->
                <xsl:attribute name="id">u<xsl:value-of select="@data-semantic-id"/></xsl:attribute>
                <xsl:attribute name="xref"><xsl:value-of select="@id"/></xsl:attribute>
            </xsl:element>

            <!-- second child node is the degree -->
            <xsl:variable name="temp1" select="tokenize(@data-semantic-children,',')[1]"/>
            <xsl:element name="degree">
                <xsl:apply-templates select="//*[@data-semantic-id=$temp1]"/>
            </xsl:element>

            <xsl:variable name="temp0" select="tokenize(@data-semantic-children,',')[2]"/>
            <xsl:apply-templates select="//*[@data-semantic-id=$temp0]"/>

        </xsl:element>
    </xsl:template>

    <!-- Recursive method over a list of id nodes. (XSLT 1.0 conform) -->
    <xsl:template name="iterateList">
        <xsl:param name="list"/> <!-- e.g. '1,5,9,15' -->
        <xsl:param name="separator"/> <!-- e.g. ',' -->
        <xsl:variable name="first" select="substring-before($list, $separator)"/>
        <xsl:variable name="remaining" select="substring-after($list, $separator)"/>

        <xsl:choose>
            <xsl:when test="not(contains($list, $separator))">
                <!-- the stopper, if no separator is present anymore -->
                <xsl:apply-templates select="//*[@data-semantic-id=$list]"/>
            </xsl:when>
            <xsl:otherwise>
                <!-- take first element and start recursion with the remaining string -->
                <xsl:apply-templates select="//*[@data-semantic-id=$first]"/>
                <xsl:if test="$remaining">
                    <xsl:call-template name="iterateList">
                        <xsl:with-param name="list" select="$remaining"/>
                        <xsl:with-param name="separator" select="$separator"/>
                    </xsl:call-template>
                </xsl:if>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <!-- idenfitiers and constants (numbers) -->
    <xsl:template match="mi|mn">
        <!--TODO cn-->
        <xsl:element name="ci">
            <!-- new unique id starting with the letter "c" -->
            <xsl:attribute name="id">c<xsl:value-of select="@data-semantic-id"/></xsl:attribute>
            <xsl:attribute name="xref">
                <xsl:value-of select="@id"/>
            </xsl:attribute>
            <xsl:attribute name="cd">
                <xsl:value-of select="@data-semantic-type"/>
            </xsl:attribute>
            <xsl:value-of select="normalize-space(node())"/>
        </xsl:element>
    </xsl:template>

    <!-- operator TODO -->
    <xsl:template match="mo[@data-semantic-type='operator']">
        <xsl:variable name="tagname" select="'plus'"/>

        <xsl:element name="{$tagname}">
            <!-- new unique id starting with the letter "c" -->
            <xsl:attribute name="id">c<xsl:value-of select="@data-semantic-id"/></xsl:attribute>
            <xsl:attribute name="xref">
                <xsl:value-of select="@id"/>
            </xsl:attribute>
            <xsl:attribute name="cd">
                <xsl:value-of select="@data-semantic-type"/>
            </xsl:attribute>
            <xsl:value-of select="text()"/>
        </xsl:element>
    </xsl:template>

    <!-- relation -->
    <xsl:template match="mo[@data-semantic-type='relation']">

        <xsl:element name="eq">
            <!-- new unique id starting with the letter "c" -->
            <xsl:attribute name="id">c<xsl:value-of select="@data-semantic-id"/></xsl:attribute>
            <xsl:attribute name="xref">
                <xsl:value-of select="@id"/>
            </xsl:attribute>
            <xsl:attribute name="cd">
                <xsl:value-of select="@data-semantic-type"/>
            </xsl:attribute>
            <xsl:value-of select="normalize-space(node())"/>
        </xsl:element>
    </xsl:template>

    <!-- default node processing (just a copy) -->
    <!--<xsl:template match="*" mode="#all">-->
        <!--<xsl:copy>-->
            <!--<xsl:copy-of select="@*"/>-->
            <!--<xsl:apply-templates mode="#current"/>-->
        <!--</xsl:copy>-->
    <!--</xsl:template>-->

</xsl:stylesheet>
