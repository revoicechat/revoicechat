package fr.revoicechat.core.technicaldata.message;

import java.util.UUID;

import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.QueryParam;

public class MessageFilterParams {
  @QueryParam("page")
  @DefaultValue("0")
  private int page;

  @QueryParam("size")
  @DefaultValue("50")
  private int size;

  @QueryParam("keyword")
  private String keyword;

  @QueryParam("lastMessage")
  private UUID lastMessage;

  @QueryParam("user")
  private UUID userId;

  @QueryParam("room")
  private UUID roomId;

  public int getPage() {
    return page;
  }

  public void setPage(final int page) {
    this.page = page;
  }

  public int getSize() {
    return size;
  }

  public void setSize(final int size) {
    this.size = size;
  }

  public String getKeyword() {
    return keyword;
  }

  public void setKeyword(final String keyword) {
    this.keyword = keyword;
  }

  public UUID getLastMessage() {
    return lastMessage;
  }

  public void setLastMessage(final UUID lastMessage) {
    this.lastMessage = lastMessage;
  }

  public UUID getUserId() {
    return userId;
  }

  public void setUserId(final UUID userId) {
    this.userId = userId;
  }

  public UUID getRoomId() {
    return roomId;
  }

  public void setRoomId(final UUID roomId) {
    this.roomId = roomId;
  }
}
