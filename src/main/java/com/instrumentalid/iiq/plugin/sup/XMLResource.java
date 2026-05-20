package com.instrumentalid.iiq.plugin.sup;

import sailpoint.rest.plugin.BasePluginResource;
import sailpoint.rest.plugin.RequiredRight;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

@Path("/simple-ui-plugin/xml")
public class XMLResource extends BasePluginResource {
    @Override
    public String getPluginName() {
        return PluginConstants.PLUGIN_NAME;
    }

    @GET
    @RequiredRight("IID_SUP_XML")
    @Path("{identityId}")
    public Response getXml(@PathParam("identityId") String targetIdentityId, @QueryParam("stripIdentifiers") boolean stripIdentifiers) {
        return Utils.handleRequest("SUP.getXml", () -> {
            XMLService service = new XMLService(getContext(), getLoggedInUser(), this);
            return service.getIdentityXml(targetIdentityId, stripIdentifiers);
        });
    }

}
