<?xml version="1.0" encoding="UTF-8" ?>
<!--
Copyright (C) 2024  macmarrum (at) outlook (dot) ie
SPDX-License-Identifier: GPL-3.0-or-later

Transforms the mind-map format used by 1.11.x to 1.9.13 (used also by Freeplane 1.10.x),
removing or translating newly-added attributes and/or values to those understood by earlier Freeplane versions

Not usable with Freeplane Export, because this script works on the original xml,
whereas the xml passed by Export to the processor has style formatting converted into node attributes
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xsl:output method="xml" indent="no" encoding="us-ascii" omit-xml-declaration="yes"/>

    <xsl:template match="/map">
        <map version="freeplane 1.9.13">
            <xsl:apply-templates/>
        </map>
    </xsl:template>

    <xsl:template match="@*|node()">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()"/>
        </xsl:copy>
    </xsl:template>

    <!-- translate left, right -->
    <xsl:template match="@POSITION">
        <xsl:attribute name="POSITION">
            <xsl:choose>
                <xsl:when test=". = 'top_or_left'">left</xsl:when>
                <xsl:when test=". = 'bottom_or_right'">right</xsl:when>
                <xsl:otherwise>
                    <xsl:message>Unrecognized @POSITION <xsl:value-of select="."/></xsl:message>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:attribute>
    </xsl:template>

    <!-- translate new layouts to one of their three predecessors (alignment values) -->
    <xsl:template match="@CHILD_NODES_LAYOUT">
        <xsl:attribute name="CHILD_NODES_ALIGNMENT">
            <xsl:choose>
                <xsl:when test="substring(., string-length() - string-length('AUTO') + 1) = 'AUTO'">AS_PARENT</xsl:when>
                <xsl:when test="substring(., string-length() - string-length('CENTERED') + 1) = 'CENTERED' or substring(., string-length() - string-length('FLOW') + 1) = 'FLOW'">BY_CENTER</xsl:when>
                <xsl:when test=". = 'AUTO_AFTERPARENT' or substring(., string-length() - string-length('FIRST') + 1) = 'FIRST' or substring(., string-length() - string-length('RIGHT') + 1) = 'RIGHT' or substring(., string-length() - string-length('BOTTOM') + 1) = 'BOTTOM'">BY_FIRST_NODE</xsl:when>
                <xsl:when test=". = 'AUTO_BEFOREPARENT' or substring(., string-length() - string-length('LAST') + 1) = 'LAST' or substring(., string-length() - string-length('LEFT') + 1) = 'LEFT' or substring(., string-length() - string-length('TOP') + 1) = 'TOP'">BY_LAST_NODE</xsl:when>
                <xsl:otherwise>
                    <xsl:message>Unrecognized @CHILD_NODES_LAYOUT <xsl:value-of select="."/></xsl:message>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:attribute>
    </xsl:template>

    <!-- Freeplane 1.10.7 can work without it, but for the sake of completeness, add CONTENT-TYPE if missing -->
    <xsl:template match="richcontent[@TYPE='DETAILS' and not(@CONTENT-TYPE)]">
        <xsl:copy>
            <!-- suggest to the processor to output CONTENT-TYPE first, then TYPE -->
            <xsl:attribute name="CONTENT-TYPE">xml/</xsl:attribute>
            <xsl:apply-templates select="@*|node()"/>
        </xsl:copy>
    </xsl:template>
    <xsl:template match="richcontent[@TYPE='NOTE' and not(@CONTENT-TYPE)]">
        <xsl:copy>
            <!-- suggest to the processor to output TYPE first, then CONTENT-TYPE -->
            <xsl:apply-templates select="@*"/>
            <xsl:attribute name="CONTENT-TYPE">xml/</xsl:attribute>
            <xsl:apply-templates/>
        </xsl:copy>
    </xsl:template>

    <!-- remove -->
    <xsl:template match="@COMMON_HGAP_QUANTITY"/>
    <xsl:template match="@TEXT_WRITING_DIRECTION"/>
</xsl:stylesheet>
