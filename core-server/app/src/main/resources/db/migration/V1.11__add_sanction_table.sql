create table RVC_SANCTION
(
    ID               uuid         not null,
    TARGETED_USER_ID uuid         not null,
    SERVER_ID        uuid,
    TYPE             varchar(255) not null,
    START_AT         timestamp(6) not null,
    EXPIRES_AT       timestamp(6),
    ISSUED_BY        uuid         not null,
    REASON           text,
    REVOKED_BY       uuid,
    REVOKED_AT       timestamp(6),
    primary key (id),
    constraint FK_RVC_SANCTION_TARGETED_USER foreign key (TARGETED_USER_ID) references RVC_USER DEFERRABLE,
    constraint FK_RVC_SANCTION_SERVER foreign key (SERVER_ID) references RVC_SERVER DEFERRABLE,
    constraint FK_RVC_SANCTION_ISSUED_BY foreign key (ISSUED_BY) references RVC_USER DEFERRABLE,
    constraint FK_RVC_SANCTION_REVOKED_BY foreign key (REVOKED_BY) references RVC_USER DEFERRABLE
);