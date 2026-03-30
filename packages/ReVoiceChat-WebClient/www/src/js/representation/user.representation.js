/**
 * @typedef {"ONLINE"|"AWAY"|"OFFLINE"} ActiveStatus
 * @typedef {"ADMIN"|"USER"|"BOT"} UserType
 */

class UserRepresentation {
  /** @type {string} */
  id
  /** @type {string} */
  displayName
  /** @type {string} */
  login
  createdDate
  /** @type {ActiveStatus} */
  status
  /** @type {UserType} */
  type
}

/**
 * for "USER_STATUS_UPDATE" notifications
 */
class UserStatusUpdate {
  /** @type {string} */
  userId
  /** @type {ActiveStatus} */
  status
}

/**
 * for "STREAM_START" and "STREAM_STOP" notifications
 */
class StreamingRepresentation {
  /** @type {string} */
  user
  /** @type {string} */
  streamName
}

/**
 * for "STREAM_LEAVE" and "STREAM_JOIN" notifications
 */
class ViewerRepresentation {
  /** @type {string} */
  streamer
  /** @type {string} */
  streamName
  /** @type {string} */
  viewer
}

/**
 * for "VOICE_JOINING" notifications
 */
class VoiceJoiningNotification {
  /** @type {UserNotificationRepresentation} */
  user
  /** @type {string} */
  roomId
}

/**
 * for "VOICE_LEAVING" notifications
 */
class VoiceLeavingNotification {
  /** @type {string} */
  user
  /** @type {string} */
  roomId
}


export class UserNotificationRepresentation {
  /** @type {string} */
  id
  /** @type {string} */
  displayName
}

class StreamRepresentation {
  /** @type {string} */
  user
  /** @type {string} */
  streamName
  /** @type {string[]} */
  viewers
}

class ConnectedUserRepresentation {
  /** @type {UserRepresentation} */
  user
  /** @type {StreamRepresentation[]} */
  streams
}