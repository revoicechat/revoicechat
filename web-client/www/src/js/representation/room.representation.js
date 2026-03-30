/**
 * @typedef {"TEXT"|"WEBRTC"|"VOICE"} RoomType
 */

class RoomRepresentation {
  /** @type {string} */
  id
  /** @type {string} */
  name
  /** @type {RoomType} */
  type
  /** @type {string} */
  serverId
  /** @type {UnreadMessage} */
  unreadMessages
}

class UnreadMessage {
  /** @type {boolean} */
  hasUnreadMessage
  /** @type {number} */
  mentions
}

/**
 * for "ROOM_UPDATE" notifications
 */
class RoomNotification {
  /** @type {RoomRepresentation} */
  room
  /** @type {NotificationActionType} */
  action
}

export class MessageRepresentation {
  /** @type {string} */
  id
  /** @type {string} */
  text
  /** @type {string} */
  roomId
  /** @type {string} */
  serverId
  /** @type {MessageAnsweredRepresentation} */
  answeredTo
  /** @type {UserNotificationRepresentation} */
  user
  createdDate
  updatedDate
  /** @type {MediaDataRepresentation[]} */
  medias
  /** @type {EmoteRepresentation[]} */
  emotes
  /** @type {MessageReaction[]} */
  reactions
  /** @type {boolean} */
  messageUrlPreview
}

class MessageReaction {
  /** @type {string} */
  emoji;
  /** @type {string[]} */
  users;
}

class MessageAnsweredRepresentation {
  /** @type {string} */
  id
  /** @type {string} */
  text
  /** @type {boolean} */
  hasMedias
  /** @type {string} */
  userId
  /** @type {EmoteRepresentation[]} */
  emotes
}

class MessageNotification {
  /** @type {MessageRepresentation} */
  message
  /** @type {NotificationActionType} */
  action
}

class RoomPresence {
  /** @type {string} */
  id
  /** @type {string} */
  name
  /** @type {UserRepresentation[]} */
  allUser
  /** @type {ConnectedUserRepresentation[]} */
  connectedUser
}