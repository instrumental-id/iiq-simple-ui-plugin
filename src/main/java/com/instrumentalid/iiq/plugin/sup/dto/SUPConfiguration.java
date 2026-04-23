package com.instrumentalid.iiq.plugin.sup.dto;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.annotate.JsonProperty;
import sailpoint.object.Configuration;
import sailpoint.object.Identity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SUPConfiguration {
    private static final Log logger = LogFactory.getLog(SUPConfiguration.class);

    public static SUPConfiguration create(Configuration config, Identity loggedInUser) {
        var configurations = new SUPConfiguration();

        Object configButtonsObj = config.get("Refresh Buttons");
        if (configButtonsObj instanceof List) {
            List<Map<String, Object>> buttons = (List<Map<String, Object>>) configButtonsObj;
            for (Map<String, Object> buttonEntry : buttons) {
                var buttonConfig = new ButtonConfig(buttonEntry);
                if (loggedInUser == null) {
                    configurations.addButton(buttonConfig);
                } else {
                    if (buttonConfig.isUserAllowed(loggedInUser)) {
                        configurations.addButton(buttonConfig);
                    } else {
                        logger.debug("User " + loggedInUser.getName() + " is not allowed to see button " + buttonConfig.getLabel());
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
