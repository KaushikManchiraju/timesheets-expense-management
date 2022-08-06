create table TS_EXPENSES_TAG_LINK (
    TAG_ID varchar(36) not null,
    EXPENSES_ID varchar(36) not null,
    primary key (TAG_ID, EXPENSES_ID)
);
