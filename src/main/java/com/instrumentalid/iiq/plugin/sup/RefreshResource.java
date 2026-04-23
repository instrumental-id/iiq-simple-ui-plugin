package com.instrumentalid.iiq.plugin.sup;

import com.instrumentalid.iiq.plugin.sup.dto.ErrorResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import sailpoint.api.Meter;
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

    private static Response handleRequest(String meterString, RestMethodHandler handler) {
        Meter.enterByName(meterString);
        try {
            var output = handler.handle();
            if (output == null) {
                return Response.ok().build();
            } else {
                return Response.ok(output).build();
            }
        } catch (Exception e) {
            log.error("Error handling request", e);
            return Response.serverError().entity(new ErrorResponse(e)).build();
        } finally {
            Meter.exitByName(meterString);
        }
    }

    @GET
    @Path("/configuration")
    @RequiredRight("IID_SUP_RefreshButtons")
    public Response fetchConfigurations() {
        return handleRequest("SUP.fetchConfiguration", () -> {
            RefreshService service = new RefreshService(getContext(), getLoggedInUser(), this);
            return service.getConfigurations();
        });
    }

    @POST
    @Path("/refresh/{type}/{targetIdentityId}")
    @RequiredRight("IID_SUP_RefreshButtons")
    public Response handleRefreshButton(@PathParam("type") String type, @PathParam("targetIdentityId") String targetIdentityId) {
        var meterString = "SUP.refresh." + type;
        return handleRequest(meterString, () -> {
            log.info("Logged in user " + getLoggedInUser().getName() + " is refreshing identity " + targetIdentityId + " using button type " + type);
            RefreshService service = new RefreshService(getContext(), getLoggedInUser(), this);
            service.refreshIdentity(type, targetIdentityId);
            return null;
        });
    }

    @Override
    public String getPluginName() {
        return PluginConstants.PLUGIN_NAME;
    }
}
