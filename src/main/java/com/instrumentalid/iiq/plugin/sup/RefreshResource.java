package com.instrumentalid.iiq.plugin.sup;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import sailpoint.rest.plugin.BasePluginResource;
import sailpoint.rest.plugin.RequiredRight;
import sailpoint.tools.ObjectNotFoundException;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * REST resource for handling refresh operations initiated by the user clicking buttons
 * in their browser. This class will delegate to the RefreshService to perform the actual work,
 * but is responsible for handling the HTTP request/response and any exceptions that may occur.
 *
 * @author Devin Rosenbauer (Instrumental ID)
 */
@Path("/simple-ui-plugin")
@Produces(MediaType.APPLICATION_JSON)
public class RefreshResource extends BasePluginResource {
    private static final Log log = LogFactory.getLog(RefreshResource.class);

    @GET
    @Path("/configuration")
    @RequiredRight("IID_SUP_RefreshButtons")
    public Response fetchConfigurations() {
        try {
            // TODO: timing / metering
            RefreshService service = new RefreshService(getContext(), getLoggedInUser(), this);
            return Response.ok(service.getConfigurations()).build();
        } catch(Exception e) {
            log.error("Could not fetch configurations", e);
            // TODO: Do we want to return more error details here?
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @POST
    @Path("/refresh/{type}/{targetIdentityId}")
    @RequiredRight("IID_SUP_RefreshButtons")
    public Response handleRefreshButton(@PathParam("type") String type, @PathParam("targetIdentityId") String targetIdentityId) {
        try {
            // TODO: audit
            // TODO: timing / metering
            // TODO: testing
            log.info("Logged in user " + getLoggedInUser().getName() + " is refreshing identity " + targetIdentityId + " using button type " + type);
            RefreshService service = new RefreshService(getContext(), getLoggedInUser(), this);
            service.refreshIdentity(type, targetIdentityId);
            return Response.ok().build();
        } catch(ObjectNotFoundException e) {
            log.warn("Could not find Identity " + e.getObjectIdentifier() + " for refresh operation", e);
            return Response.status(Response.Status.NOT_FOUND).entity(e.getObjectClass().getName() + ": " + e.getObjectIdentifier()).build();
        } catch(Exception e) {
            log.error("Could not handle refresh button", e);
            // TODO: Do we want to return more error details here?
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Override
    public String getPluginName() {
        return PluginConstants.PLUGIN_NAME;
    }
}
