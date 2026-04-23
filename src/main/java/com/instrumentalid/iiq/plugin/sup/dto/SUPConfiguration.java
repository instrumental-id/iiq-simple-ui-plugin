package com.instrumentalid.iiq.plugin.sup.dto;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.annotate.JsonProperty;
import sailpoint.object.Configuration;
import sailpoint.tools.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SUPConfiguration {
    private static final Log logger = LogFactory.getLog(SUPConfiguration.class);

    public static SUPConfiguration create(Configuration config, Set<String> loggedInUserRights) {
        var configurations = new SUPConfiguration();

        Object configButtonsObj = config.get("Refresh Buttons");
        if (configButtonsObj instanceof List) {
            List<Map<String, Object>> buttons = (List<Map<String, Object>>) configButtonsObj;
            for (Map<String, Object> buttonEntry : buttons) {
                var buttonConfig = new ButtonConfig(buttonEntry);
                if (loggedInUserRights == null) {
                    configurations.addButton(buttonConfig);
                } else {
                    if (Util.isNotNullOrEmpty(buttonConfig.getRightRequired())) {
                        if (loggedInUserRights.contains(buttonConfig.getRightRequired())) {
                            configurations.addButton(buttonConfig);
                        } else {
                            logger.debug(String.format("User does not have right %s required to see button %s", buttonConfig.getRightRequired(), buttonConfig.getLabel()));
                        }
                    } else {
                        // Anybody can see this button
                        configurations.addButton(buttonConfig);
                    }
                }
            }
        }

        // TODO: handle other settings here if needed

        return configurations;
    }

    @JsonProperty
    private final List<ButtonConfig> buttons;

    public SUPConfiguration() {
        this.buttons = new ArrayList<>();
    }

    public void addButton(ButtonConfig buttonConfig) {
        this.buttons.add(buttonConfig);
    }

    public List<ButtonConfig> getButtons() {
        return buttons;
    }
}
