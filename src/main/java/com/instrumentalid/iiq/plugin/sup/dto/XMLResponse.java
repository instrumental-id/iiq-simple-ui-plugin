package com.instrumentalid.iiq.plugin.sup.dto;

import org.codehaus.jackson.annotate.JsonProperty;

public class XMLResponse {
    @JsonProperty
    private final String identityId;

    @JsonProperty
    private final String strippedXml;

    @JsonProperty
    private final String xml;

    public XMLResponse(String identityId, String xml, String strippedXml) {
        this.identityId = identityId;
        this.xml = xml;
        this.strippedXml = strippedXml;
    }

    public String getIdentityId() {
        return identityId;
    }

    public String getStrippedXml() {
        return strippedXml;
    }

    public String getXml() {
        return xml;
    }
}
