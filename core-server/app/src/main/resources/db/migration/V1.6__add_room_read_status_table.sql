CREATE TABLE RVC_ROOM_READ_STATUS
(
    USER_ID              UUID NOT NULL,
    ROOM_ID              UUID NOT NULL,
    LAST_READ_MESSAGE_ID UUID,
    LAST_READ_AT         timestamp(6),
    PRIMARY KEY (user_id, room_id)
);