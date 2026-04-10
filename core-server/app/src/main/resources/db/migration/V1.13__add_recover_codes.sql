create table RVC_USER_RECOVER_CODES
(
    USER_ID      uuid         not null,
    RECOVER_CODE text         not null,
    STATUS       varchar(255) not null,
    primary key (USER_ID, RECOVER_CODE),
    constraint FK_USER_RECOVER_CODES_USER foreign key (USER_ID) references RVC_USER DEFERRABLE
);