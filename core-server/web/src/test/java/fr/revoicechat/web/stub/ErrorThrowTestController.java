package fr.revoicechat.web.stub;

import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Path("/tests/error/throw")
public class ErrorThrowTestController {

  @GET
  @PermitAll
  public String error() {
    throw new UnsupportedOperationException("this method is here to test error log generation");
  }
}