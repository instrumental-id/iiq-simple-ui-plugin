package com.instrumentalid.iiq.plugin.sup;

import com.instrumentalid.iiq.plugin.sup.dto.ErrorResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import sailpoint.api.Meter;

import javax.ws.rs.core.Response;

public class Utils {
    private static final Log log = LogFactory.getLog(Utils.class);

    public static Response handleRequest(String meterString, RestMethodHandler handler) {
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

}
