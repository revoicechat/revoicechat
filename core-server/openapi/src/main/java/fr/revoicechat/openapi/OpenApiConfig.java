package fr.revoicechat.openapi;

import static fr.revoicechat.openapi.OpenApiConfig.*;
import static org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeIn.QUERY;
import static org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeType.*;

import org.eclipse.microprofile.openapi.annotations.security.SecurityScheme;

@SecurityScheme(securitySchemeName = JWT_HEADER, type = HTTP, scheme = "bearer", bearerFormat = "JWT")
@SecurityScheme(securitySchemeName = JWT_QUERY, type = APIKEY, in = QUERY, apiKeyName = "jwt")
public class OpenApiConfig {

  private OpenApiConfig() {/*not instantiable*/}

  public static final String JWT_HEADER = "jwtHeader";
  public static final String JWT_QUERY = "jwtQuery";
}
