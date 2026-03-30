class ServerRepresentation {
  /** @type {string} */
  id
  /** @type {string} */
  name
  /** @type {string} */
  owner
}

/**
 * for "SERVER_UPDATE" notifications
 */
class ServerUpdateNotification {
  /** @type {ServerRepresentation} */
  server
  /** @type {NotificationActionType} */
  action
}

/**
 * for "NEW_USER_IN_SERVER" notifications
 */
class NewUserInServer {
  /** @type {string} */
  server
  /** @type {string} */
  user
}

class ServerItem {
  /** @type {"CATEGORY"|"ROOM"} */
  type
}

class ServerCategory extends ServerItem{
  /** @type {string} */
  name
  /** @type {ServerItem[]} */
  items
}

class ServerRoom extends ServerItem {
  /** @type {string} */
  id
}

class ServerStructure {
  /** @type {ServerItem[]} */
  items
}