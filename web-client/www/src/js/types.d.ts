export type ActiveStatus = "ONLINE" | "AWAY" | "OFFLINE"
export type UserType = "ADMIN" | "USER" | "BOT"
export type RoomType = "TEXT" | "WEBRTC" | "VOICE"
export type NotificationActionType = "ADD" | "MODIFY" | "REMOVE"
export type InvitationLinkStatus = "CREATED" | "USED" | "REVOKED"
export type InvitationType = "APPLICATION_JOIN" | "SERVER_JOIN"

export class UserRepresentation {
    id: string;
    displayName: string;
    login: string;
    createdDate: string;
    status: ActiveStatus;
    type: UserType;
}

/** for "USER_STATUS_UPDATE" notifications */
export class UserStatusUpdate {
    userId: string;
    status: ActiveStatus;
}

/** for "STREAM_START" and "STREAM_STOP" notifications */
export class StreamingRepresentation {
    user: string;
    streamName: string;
}

/** for "STREAM_LEAVE" and "STREAM_JOIN" notifications */
export class ViewerRepresentation {
    streamer: string;
    streamName: string;
    viewer: string;
}

export class UserNotificationRepresentation {
    id: string;
    displayName: string;
}

/** for "VOICE_JOINING" notifications */
export class VoiceJoiningNotification {
    user: UserNotificationRepresentation;
    roomId: string;
}

/** for "VOICE_LEAVING" notifications */
export class VoiceLeavingNotification {
    user: string;
    roomId: string;
}

export class StreamRepresentation {
    user: string;
    streamName: string;
    viewers: string[];
}

export class ConnectedUserRepresentation {
    user: UserRepresentation;
    streams: StreamRepresentation[];
}

export class ProfilPictureUpdate {
    id: string;
}

export class MessageRepresentation {
    id: string;
    text: string;
    roomId: string;
    serverId: string;
    answeredTo: MessageAnsweredRepresentation;
    user: UserNotificationRepresentation;
    createdDate: string;
    updatedDate: string;
    medias: MediaDataRepresentation[];
    emotes: EmoteRepresentation[];
    reactions: MessageReaction[];
    messageUrlPreview: boolean;
}

/** for "ROOM_UPDATE" notifications */
export class RoomNotification {
    room: RoomRepresentation;
    action: NotificationActionType;
}

export class MessageReaction {
    emoji: string;
    users: string[];
}

export class MessageAnsweredRepresentation {
    id: string;
    text: string;
    hasMedias: boolean;
    userId: string;
    emotes: EmoteRepresentation[];
}

export type SanctionType = "VOICE_TIME_OUT" | "TEXT_TIME_OUT" | "BAN";

export class SanctionUserRepresentation {
    id: string;
    displayName: string;
}

export class SanctionRepresentation {
    id: string;
    targetedUser: SanctionUserRepresentation;
    server: string|null;
    type: SanctionType;
    startAt: string;
    expiresAt: string;
    issuedBy: SanctionUserRepresentation;
    reason: string;
    revokedBy: SanctionUserRepresentation;
    revokedAt: string;
    active: boolean;
}

export class SanctionRevocationRequestRepresentation {
    id: string;
    sanctionId: string;
    message: string;
    status: string;
    requestAt: string;
    canRequestAgain: boolean;
}

export class RoomPresenceRepresentation {
    id: string;
    name: string;
    allUser: UserRepresentation[];
    connectedUser: ConnectedUserRepresentation[];
}

export class RoomRepresentation {
    id: string;
    name: string;
    type: RoomType;
    serverId: string;
    unreadMessages: UnreadMessage;
}

export class UnreadMessage {
    hasUnreadMessage: boolean;
    mentions: number;
}

export class MessageNotification {
    message: MessageRepresentation;
    action: NotificationActionType;
}

export class RoomPresence {
    id: string;
    name: string;
    allUser: UserRepresentation[];
    connectedUser: ConnectedUserRepresentation[];
}

export class InvitationRepresentation {
    id: string;
    status: InvitationLinkStatus;
    type: InvitationType;
    targetedServer: string;
}

export class ServerRepresentation {
    id: string;
    name: string;
    owner: string;
}

/** for "SERVER_UPDATE" notifications */
export class ServerUpdateNotification {
    server: ServerRepresentation;
    action: NotificationActionType;
}

/** for "NEW_USER_IN_SERVER" notifications */
export class NewUserInServer {
    server: string;
    user: string;
}

export type ServerCategoryType = "CATEGORY" | "ROOM";

export class ServerItem {
    type: ServerCategoryType
}

export class ServerCategory extends ServerItem {
    name: string;
    items: ServerItem[];
}

export class ServerRoom extends ServerItem {
    id: string;
}

export class ServerStructure {
    items: ServerItem[];
}

export type MediaOrigin = "PROFILE_PICTURE" | "ATTACHMENT" | "EMOTE";
export type MediaDataStatus = "DOWNLOADING" | "STORED" | "CORRUPT" | "DELETING" | "DELETED";
export type FileType = "PICTURE" | "SVG" | "VIDEO" | "AUDIO"
    | "PDF" | "TEXT" | "OFFICE" | "ARCHIVE" | "CODE" | "FONT"
    | "MODEL" | "EXECUTABLE" | "DATA" | "OTHER";

export class MediaDataRepresentation {
    id: string;
    name: string;
    url: string;
    origin: MediaOrigin;
    status: MediaDataStatus;
    type: FileType;
}

export class EmoteRepresentation {
    id: string;
    name: string;
    keywords: string[];
}

export class ServerRoleRepresentation {
    id: string;
    name: string;
    color: string;
    priority: number;
    serverId: string;
    risks: RiskRepresentation[];
    members: string[];
}

export interface RiskType {}

export type RiskMode = "ENABLE" | "DISABLE" | "DEFAULT";

export class RiskRepresentation {
    type: RiskType;
    entity: string|null;
    mode: RiskMode;
}

export class MediaSettings {
    maxFileSize: number;
}

export class PageResult<T> {
    content: T[]
    pageNumber: number;
    pageSize: number;
    totalElements: number;
    totalPages: number;
}