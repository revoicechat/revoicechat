package fr.revoicechat.risk.web;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import fr.revoicechat.risk.type.RiskCategory;
import fr.revoicechat.risk.type.RoomEntityRiskType;
import fr.revoicechat.risk.type.ServerEntityRiskType;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import jakarta.ws.rs.core.MediaType;

/** @see fr.revoicechat.risk.web.api.RiskController */
@QuarkusTest
class TestRiskController {

  public static final String PATH = "fr.revoicechat.risk.type.RiskTypeMock";

  @Test
  void test() {
    var response = RestAssured.given()
                              .contentType(MediaType.APPLICATION_JSON)
                              .when().get("/risk")
                              .then().statusCode(200)
                              .extract().body().asPrettyString();
    assertThat(response).contains(
                            "\"type\": \"SERVER_RISK_TYPE_MOCK\"",
                            "\"title\": \"server risk type mock\",",
                            "\"type\": \"SERVER_MOCK_RISK_1\"",
                            "\"title\": \"server risk 1\"",
                            "\"type\": \"SERVER_MOCK_RISK_2\"",
                            "\"title\": \"server risk 2\"",

                            "\"type\": \"ROOM_RISK_TYPE_MOCK\"",
                            "\"title\": \"room risk type mock\",",
                            "\"type\": \"ROOM_MOCK_RISK_1\"",
                            "\"title\": \"room risk 1\"",
                            "\"type\": \"ROOM_MOCK_RISK_2\"",
                            "\"title\": \"room risk 2\""
                        )
                        .doesNotContain(
                            "\"type\": \"SERVER_NO_RISK_TYPE_MOCK\"",
                            "\"type\": \"ROOM_NO_RISK_TYPE_MOCK\""
                        );
  }

  @Test
  void serverRisks() {
    var response = RestAssured.given()
                              .contentType(MediaType.APPLICATION_JSON)
                              .when().get("/risk/server")
                              .then().statusCode(200)
                              .extract().body().asPrettyString();
    assertThat(response).contains(
                            "\"type\": \"SERVER_RISK_TYPE_MOCK\"",
                            "\"title\": \"server risk type mock\",",
                            "\"type\": \"SERVER_MOCK_RISK_1\"",
                            "\"title\": \"server risk 1\"",
                            "\"type\": \"SERVER_MOCK_RISK_2\"",
                            "\"title\": \"server risk 2\""
                        )
                        .doesNotContain(
                            "\"type\": \"SERVER_NO_RISK_TYPE_MOCK\"",
                            "\"type\": \"ROOM_NO_RISK_TYPE_MOCK\"",
                            "\"type\": \"ROOM_RISK_TYPE_MOCK\""
                        );
  }

  @Test
  void roomRisks() {
    var response = RestAssured.given()
                              .contentType(MediaType.APPLICATION_JSON)
                              .when().get("/risk/room")
                              .then().statusCode(200)
                              .extract().body().asPrettyString();
    assertThat(response).contains(
                            "\"type\": \"ROOM_RISK_TYPE_MOCK\"",
                            "\"title\": \"room risk type mock\",",
                            "\"type\": \"ROOM_MOCK_RISK_1\"",
                            "\"title\": \"room risk 1\"",
                            "\"type\": \"ROOM_MOCK_RISK_2\"",
                            "\"title\": \"room risk 2\""
                        )
                        .doesNotContain(
                            "\"type\": \"SERVER_NO_RISK_TYPE_MOCK\"",
                            "\"type\": \"ROOM_NO_RISK_TYPE_MOCK\"",
                            "\"type\": \"SERVER_RISK_TYPE_MOCK\""
                        );
  }

  @RiskCategory("SERVER_NO_RISK_TYPE_MOCK")
  @SuppressWarnings("unused") // here for reflection test purpose
  public enum ServerEntityNoRiskTypeMock implements ServerEntityRiskType {
    ;

    @Override
    public String fileName() {
      return PATH;
    }
  }

  @RiskCategory("SERVER_RISK_TYPE_MOCK")
  @SuppressWarnings("unused") // here for reflection test purpose
  public enum ServerEntityRiskTypeMock implements ServerEntityRiskType {
    SERVER_MOCK_RISK_1,
    SERVER_MOCK_RISK_2,
    ;

    @Override
    public String fileName() {
      return PATH;
    }
  }

  @RiskCategory("ROOM_NO_RISK_TYPE_MOCK")
  @SuppressWarnings("unused") // here for reflection test purpose
  public enum RoomEntityNoRiskTypeMock implements ServerEntityRiskType {
    ;

    @Override
    public String fileName() {
      return PATH;
    }
  }

  @RiskCategory("ROOM_RISK_TYPE_MOCK")
  @SuppressWarnings("unused") // here for reflection test purpose
  public enum RoomEntityRiskTypeMock implements RoomEntityRiskType {
    ROOM_MOCK_RISK_1,
    ROOM_MOCK_RISK_2,
    ;

    @Override
    public String fileName() {
      return PATH;
    }
  }
}