alter table RVC_MESSAGE
    add column MESSAGE_ID uuid;

alter table RVC_MESSAGE
    add constraint FK_RVC_MESSAGE_ANSWER
        foreign key (MESSAGE_ID) references RVC_MESSAGE DEFERRABLE;