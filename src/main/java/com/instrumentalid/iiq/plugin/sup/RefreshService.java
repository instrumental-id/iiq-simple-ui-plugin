package com.instrumentalid.iiq.plugin.sup;

import com.instrumentalid.iiq.plugin.sup.dto.SUPConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import sailpoint.api.SailPointContext;
import sailpoint.object.Identity;
import sailpoint.plugin.PluginContext;
import sailpoint.tools.GeneralException;
import sailpoint.tools.ObjectNotFoundException;

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

    public SUPConfiguration getConfigurations() throws GeneralException {
        // TODO
        return null;
    }

    public void refreshIdentity(String type, String targetIdentityId) throws GeneralException, ObjectNotFoundException {
        // TODO
    }
}
