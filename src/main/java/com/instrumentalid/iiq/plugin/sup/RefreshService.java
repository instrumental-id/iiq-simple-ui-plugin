package com.instrumentalid.iiq.plugin.sup;

import com.instrumentalid.iiq.plugin.sup.dto.SUPConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import sailpoint.api.Identitizer;
import sailpoint.api.SailPointContext;
import sailpoint.object.Attributes;
import sailpoint.object.Configuration;
import sailpoint.object.Identity;
import sailpoint.plugin.PluginContext;
import sailpoint.tools.GeneralException;
import sailpoint.tools.ObjectNotFoundException;
import sailpoint.tools.Util;

import java.util.HashSet;

/**
 * The backend for the refresh operations initiated by the user clicking buttons in their
 * browser. This class is responsible for implementing the business logic in a web-independent
 * way so that it can be tested more easily.
 */
public class RefreshService {
    private static final Log log = LogFactory.getLog(RefreshService.class);

    private final SailPointContext context;
    private final Identity loggedInUser;
    private final PluginContext plugin;

    public RefreshService(SailPointContext context, Identity loggedInUser, PluginContext plugin) {
        this.context = context;
        this.loggedInUser = loggedInUser;
        this.plugin = plugin;
    }

    /**
     * Fetches the SUPConfiguration for the logged in user. This will be used by the frontend
     * to determine which buttons to show.
     * @return the SUPConfiguration for the logged in user
     * @throws GeneralException if there is any error fetching or building the configuration
     */
    public SUPConfiguration getConfigurations() throws GeneralException {
        var configuration = getIIQConfigurationObject();
        var rights = new HashSet<>(loggedInUser.getCapabilityManager().getEffectiveFlattenedRights());
        return SUPConfiguration.create(configuration, rights);
    }

    /**
     * Fetches the configuration object specified in the plugin settings, or "SUP Configuration"
     * if not specified.
     *
     * @return the Configuration object to use for this plugin
     * @throws GeneralException if the configuration object cannot be found or there is an error fetching it
     */
    protected Configuration getIIQConfigurationObject() throws GeneralException{
        var configurationName = plugin.getSettingString("configurationObject");
        if (Util.isNullOrEmpty(configurationName)) {
            configurationName = "SUP Configuration";
        }
        var configuration = context.getObject(Configuration.class, configurationName);
        if (configuration == null) {
            throw new GeneralException("Configuration object not found: " + configurationName);
        }
        return configuration;
    }

    /**
     * Refreshes the Identity using the IIQ API Identitizer class. The type parameter is used to
     * determine which button was clicked, and the targetIdentityId is the id of the Identity to refresh.
     * The options for the Identitizer are built based on the button configuration for the specified
     * type, and some additional options are added to indicate that the refresh was initiated from
     * this plugin and which user initiated it.
     *
     * @param type the type of refresh button that was clicked, which will be used to look up the button configuration
     * @param targetIdentityId the id of the Identity to refresh
     * @throws GeneralException if there is any error during the refresh operation, such as if the target Identity cannot be found or if there is an error with the Identitizer
     */
    public void refreshIdentity(String type, String targetIdentityId) throws GeneralException {
        var targetIdentity = context.getObject(Identity.class, targetIdentityId);
        if (targetIdentity == null) {
            throw new ObjectNotFoundException(Identity.class, targetIdentityId);
        }

        var configForUser = getConfigurations();
        var buttons = configForUser.getButtons();
        var buttonConfig = buttons.stream()
                .filter(b -> b.getType().equals(type))
                .findFirst()
                // This will happen if the user tries to call the refresh endpoint directly with a type that
                // doesn't exist or that they don't have access to.
                .orElseThrow(() -> new GeneralException("User cannot access refresh button of: " + type));

        var options = buttonConfig.getOptions();
        Attributes<String, Object> refreshOptions = new Attributes<>();
        refreshOptions.putAll(options);
        refreshOptions.put(Identitizer.ARG_REFRESH_SOURCE, "webservice");
        refreshOptions.put(Identitizer.ARG_REFRESH_SOURCE_WHO, loggedInUser.getName());

        refreshIdentityInternal(targetIdentity, refreshOptions);
    }

    /**
     * Performs the refresh. This is separated out so that we can override it for testing.
     *
     * @param target the Identity to refresh
     * @param options the options to use for the refresh, which will be passed to the Identitizer
     * @throws GeneralException if there is an error during the refresh operation
     */
    protected void refreshIdentityInternal(Identity target, Attributes<String, Object> options) throws GeneralException {
        Identitizer identitizer = new Identitizer(context, options);
        identitizer.refresh(target);
    }
}
