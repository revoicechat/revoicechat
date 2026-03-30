package fr.revoicechat.core.service.server;

import static fr.revoicechat.core.nls.ServerErrorCode.*;

import java.util.UUID;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import fr.revoicechat.core.model.InvitationLink;
import fr.revoicechat.core.model.InvitationLinkStatus;
import fr.revoicechat.core.model.InvitationType;
import fr.revoicechat.core.model.Server;
import fr.revoicechat.core.model.ServerType;
import fr.revoicechat.core.model.ServerUser;
import fr.revoicechat.core.model.User;
import fr.revoicechat.core.service.invitation.InvitationLinkEntityRetriever;
import fr.revoicechat.core.service.invitation.InvitationLinkUsage;
import fr.revoicechat.core.service.serveruser.ServerUserService;
import fr.revoicechat.web.error.BadRequestException;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class TestServerJoiner {

  @Test
  void testJoinPublicWithPublicServer() {
    // Given
    var serverUserService = new ServerUserServiceMock();
    var invitationLinkUsage = new InvitationLinkUsageMock();
    Server server = new Server();
    server.setType(ServerType.PUBLIC);
    // When
    var process = new ServerJoiner(
        new ServerEntityRetrieverMock(server),
        new InvitationLinkEntityRetrieverMock(null),
        serverUserService,
        invitationLinkUsage
    );
    var id = UUID.randomUUID();
    // Then
    Assertions.assertThatCode(() -> process.joinPublic(id)).doesNotThrowAnyException();
    Assertions.assertThat(serverUserService.joined).isTrue();
    Assertions.assertThat(invitationLinkUsage.used).isFalse();
  }

  @Test
  void testJoinPublicWithPrivateServer() {
    // Given
    var serverUserService = new ServerUserServiceMock();
    var invitationLinkUsage = new InvitationLinkUsageMock();
    Server server = new Server();
    server.setType(ServerType.PRIVATE);
    // When
    var process = new ServerJoiner(
        new ServerEntityRetrieverMock(server),
        new InvitationLinkEntityRetrieverMock(null),
        serverUserService,
        invitationLinkUsage
    );
    var id = UUID.randomUUID();
    // Then
    Assertions.assertThatThrownBy(() -> process.joinPublic(id))
        .isInstanceOf(BadRequestException.class)
        .hasMessageContaining(SERVER_NOT_PUBLIC.translate());
    Assertions.assertThat(serverUserService.joined).isFalse();
    Assertions.assertThat(invitationLinkUsage.used).isFalse();
  }

  @Test
  void testJoinPrivateWithNoInvitationLink() {
    // Given
    var serverUserService = new ServerUserServiceMock();
    var invitationLinkUsage = new InvitationLinkUsageMock();
    // When
    var process = new ServerJoiner(
        new ServerEntityRetrieverMock(null),
        new InvitationLinkEntityRetrieverMock(null),
        serverUserService,
        invitationLinkUsage
    );
    var id = UUID.randomUUID();
    // Then
    Assertions.assertThatThrownBy(() -> process.joinPrivate(id))
              .isInstanceOf(BadRequestException.class)
              .hasMessageContaining(NO_VALID_INVITATION.translate());
    Assertions.assertThat(serverUserService.joined).isFalse();
    Assertions.assertThat(invitationLinkUsage.used).isFalse();
  }

  @Test
  void testJoinPrivateWithInvitationLinkButWrongType() {
    // Given
    var serverUserService = new ServerUserServiceMock();
    var invitationLinkUsage = new InvitationLinkUsageMock();
    var invitation = new InvitationLink();
    invitation.setStatus(InvitationLinkStatus.CREATED);
    invitation.setType(InvitationType.APPLICATION_JOIN);
    // When
    var process = new ServerJoiner(
        new ServerEntityRetrieverMock(null),
        new InvitationLinkEntityRetrieverMock(invitation),
        serverUserService,
        invitationLinkUsage
    );
    var id = UUID.randomUUID();
    // Then
    Assertions.assertThatThrownBy(() -> process.joinPrivate(id))
              .isInstanceOf(BadRequestException.class)
              .hasMessageContaining(NO_VALID_INVITATION.translate());
    Assertions.assertThat(serverUserService.joined).isFalse();
    Assertions.assertThat(invitationLinkUsage.used).isFalse();
  }

  @Test
  void testJoinPrivateWithInvitationLinkButWrongStatus() {
    // Given
    var serverUserService = new ServerUserServiceMock();
    var invitationLinkUsage = new InvitationLinkUsageMock();
    var invitation = new InvitationLink();
    invitation.setStatus(InvitationLinkStatus.USED);
    invitation.setType(InvitationType.SERVER_JOIN);
    // When
    var process = new ServerJoiner(
        new ServerEntityRetrieverMock(null),
        new InvitationLinkEntityRetrieverMock(invitation),
        serverUserService,
        invitationLinkUsage
    );
    var id = UUID.randomUUID();
    // Then
    Assertions.assertThatThrownBy(() -> process.joinPrivate(id))
              .isInstanceOf(BadRequestException.class)
              .hasMessageContaining(NO_VALID_INVITATION.translate());
    Assertions.assertThat(serverUserService.joined).isFalse();
    Assertions.assertThat(invitationLinkUsage.used).isFalse();
  }

  @Test
  void testJoinPrivateWithInvitationLinkValid() {
    // Given
    var serverUserService = new ServerUserServiceMock();
    var invitationLinkUsage = new InvitationLinkUsageMock();
    var invitation = new InvitationLink();
    invitation.setStatus(InvitationLinkStatus.CREATED);
    invitation.setType(InvitationType.SERVER_JOIN);
    // When
    var process = new ServerJoiner(
        new ServerEntityRetrieverMock(null),
        new InvitationLinkEntityRetrieverMock(invitation),
        serverUserService,
        invitationLinkUsage
    );
    var id = UUID.randomUUID();
    // Then
    Assertions.assertThatCode(() -> process.joinPrivate(id)).doesNotThrowAnyException();
    Assertions.assertThat(serverUserService.joined).isTrue();
    Assertions.assertThat(invitationLinkUsage.used).isTrue();
  }

  private record InvitationLinkEntityRetrieverMock(InvitationLink invitationLink) implements InvitationLinkEntityRetriever {
    @Override
    public InvitationLink getEntity(final UUID id) {return invitationLink;}
  }

  private record ServerEntityRetrieverMock(Server server) implements ServerEntityRetriever {
    @Override
    public Server getEntity(final UUID id) {return server;}
  }

  private static class ServerUserServiceMock implements ServerUserService {
    boolean joined = false;

    @Override
    public ServerUser join(final Server server) {joined = true;
      return null;
    }
  }

  private static class InvitationLinkUsageMock implements InvitationLinkUsage {
    boolean used = false;

    @Override
    public void use(final InvitationLink invitationLink, final User user) {used = true;}
  }
}