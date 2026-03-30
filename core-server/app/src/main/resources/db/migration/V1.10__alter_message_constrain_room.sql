alter table rvc_message drop constraint fk_rvc_message_room;

alter table rvc_message add
    constraint fk_rvc_message_room foreign key (room_id) references RVC_ROOM DEFERRABLE;
