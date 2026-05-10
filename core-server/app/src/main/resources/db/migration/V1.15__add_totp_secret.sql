alter table RVC_USER add column TOTP_SECRET varchar(255);
alter table RVC_USER add column TOTP_STATUS varchar(32) NOT NULL DEFAULT 'INACTIVE';