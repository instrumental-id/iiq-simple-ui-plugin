package com.instrumentalid.iiq.plugin.sup;

import com.instrumentalid.iiq.plugin.sup.dto.XMLResponse;
import sailpoint.api.SailPointContext;
import sailpoint.object.Identity;
import sailpoint.plugin.PluginContext;
import sailpoint.server.Exporter;
import sailpoint.tools.GeneralException;
import sailpoint.tools.ObjectNotFoundException;
import sailpoint.tools.Util;

import java.util.List;

public class XMLService {
    private static final List<String> propertiesToClean = List.of(
            "id", "created", "modified", "lastRefresh", "targetId", "significantModified"
    );

    private final SailPointContext context;
    private final Identity loggedInUser;
    private final PluginContext pluginContext;
    
    public XMLService(SailPointContext context, Identity loggedInUser, PluginContext pluginContext) {
        this.context = context;
        this.loggedInUser = loggedInUser;
        this.pluginContext = pluginContext;
    }

    public XMLResponse getIdentityXml(String targetIdentityId, boolean stripIdentifiers) throws GeneralException {
        Identity targetIdentity = context.getObject(Identity.class, targetIdentityId);
        if (targetIdentity == null) {
            throw new ObjectNotFoundException(Identity.class, targetIdentityId);
        }

        var xml = targetIdentity.toXml(false);
        Exporter.Cleaner cleaner = new Exporter.Cleaner(propertiesToClean);
        var strippedXml = cleaner.clean(xml);

        return new XMLResponse(targetIdentityId, xml, strippedXml);
    }
}
