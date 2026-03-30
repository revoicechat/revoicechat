package fr.revoicechat.openapi.api;

import static fr.revoicechat.openapi.OpenApiConfig.*;

import jakarta.ws.rs.core.MediaType;

import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;

@APIResponse(
    responseCode = "401",
    description = "Unauthorized – user not logged in",
    content = {
        @Content(
            mediaType = MediaType.APPLICATION_JSON,
            schema = @Schema(implementation = String.class, examples = """
                {
                  "error": "Forbidden",
                  "message": "You do not have permission to access this resource.",
                  "swaggerDoc": "/api/swagger-ui/index.html"
                }""")
        ),
        @Content(
            schema = @Schema(implementation = String.class, examples = """
                <!DOCTYPE>
                <html lang="en">
                <head>
                    <title>403 Forbidden</title>
                </head>
                <body>
                <div class="container">
                    <h1>Access Denied</h1>
                    <p>You do not have permission to access this resource.</p>
                    <p>See the <a href="/api/swagger-ui/index.html">API documentation</a>.</p>
                </div>
                </body>
                </html>""")
        )
    }
)
@SecurityRequirement(name = JWT_HEADER)
@SecurityRequirement(name = JWT_QUERY)
public interface LoggedApi {}
