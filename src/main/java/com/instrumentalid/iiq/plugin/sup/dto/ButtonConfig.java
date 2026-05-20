package com.instrumentalid.iiq.plugin.sup.dto;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import sailpoint.object.Identity;
import sailpoint.tools.Util;

import java.util.Map;

public class ButtonConfig {
    @JsonProperty
    private String icon;

    @JsonProperty
    private String label;

    @JsonIgnore
    private Map<String, Object> options;

    @JsonIgnore
    private String rightRequired;

    @JsonProperty
    private String type;

    public ButtonConfig() {
    }

    public ButtonConfig(Map<String, Object> entry) {
        this.label = Util.otoa(Util.get(entry, "label"));
        this.type = Util.otoa(Util.get(entry, "type"));
        this.icon = Util.otoa(Util.get(entry, "icon"));
        this.rightRequired = Util.otoa(Util.get(entry, "rightRequired"));
        this.options = Util.otom(Util.get(entry, "refreshOptions"));
    }

    public String getIcon() {
        return icon;
    }

    public String getLabel() {
        return label;
    }

    public Map<String, Object> getOptions() {
        return options;
    }

    public String getRightRequired() {
        return rightRequired;
    }

    public String getType() {
        return type;
    }
}
