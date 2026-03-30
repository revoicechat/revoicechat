create table RVC_SANCTION_REVOCATION_REQUEST
(
    ID          uuid         not null,
    SANCTION_ID uuid         not null,
    TYPE        varchar(255) not null,
    MESSAGE     text         not null,
    STATUS      varchar(255) not null,
    REQUEST_AT  timestamp(6),
    primary key (id),
    constraint FK_RVC_SANCTION_REVOCATION_REQUEST_SANCTION foreign key (SANCTION_ID) references RVC_SANCTION DEFERRABLE
);