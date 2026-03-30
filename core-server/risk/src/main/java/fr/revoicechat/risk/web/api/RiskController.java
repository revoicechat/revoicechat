package fr.revoicechat.risk.web.api;

import java.util.List;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import fr.revoicechat.risk.representation.RiskCategoryRepresentation;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Path("risk")
@Tag(name = "Risk", description = "Manage risk categories and permissions")
public interface RiskController {

  @Operation(
      summary = "Get all risk categories",
      description = "Retrieve all available risk categories and their associated permissions. Risk categories define what actions users can perform within the application."
  )
  @APIResponse(responseCode = "200", description = "Risk categories retrieved successfully")
  @GET
  List<RiskCategoryRepresentation> getAllRisks();

  @Operation(
      summary = "Get all risk categories for server entity",
      description = "Retrieve all available risk categories and their associated permissions. Risk categories define what actions users can perform within the application."
  )
  @APIResponse(responseCode = "200", description = "Risk categories retrieved successfully")
  @GET
  @Path("server")
  List<RiskCategoryRepresentation> getSpecificServerRisks();

  @Operation(
      summary = "Get all risk categories for room entity",
      description = "Retrieve all available risk categories and their associated permissions. Risk categories define what actions users can perform within the application."
  )
  @APIResponse(responseCode = "200", description = "Risk categories retrieved successfully")
  @GET
  @Path("room")
  List<RiskCategoryRepresentation> getSpecificRoomRisks();
}
