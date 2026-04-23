package com.instrumentalid.iiq.plugin.sup;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import sailpoint.rest.plugin.BasePluginResource;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/simple-ui-plugin")
@Produces(MediaType.APPLICATION_JSON)
public class RefreshResource extends BasePluginResource {
    private static final Log log = LogFactory.getLog(RefreshResource.class);

    @GET
    @Path("/configuration")
    //@RequiredRight("IID_SUP_RefreshButtons")
    public Response fetchConfigurations() {
        try {
            RefreshService service = new RefreshService(getContext(), getLoggedInUser(), this);
            return Response.ok(service.getConfigurations()).build();
        } catch(Exception e) {
            log.error("Could not fetch configurations", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @POST
    @Path("/refresh/{type}/{targetIdentityId}")
    public Response handleRefreshButton(@PathParam("type") String type, @PathParam("targetIdentityId") String targetIdentityId) {
        try {
            RefreshService service = new RefreshService(getContext(), getLoggedInUser(), this);
            service.refreshIdentity(type, targetIdentityId);
            return Response.ok().build();
        } catch(Exception e) {
            log.error("Could not handle refresh button", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Override
    public String getPluginName() {
        return PluginConstants.PLUGIN_NAME;
    }
}
