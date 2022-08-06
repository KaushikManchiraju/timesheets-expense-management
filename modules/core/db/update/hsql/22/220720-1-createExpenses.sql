create table TS_EXPENSES (
    ID varchar(36) not null,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    PROJECT_ID varchar(36) not null,
    TASK_ID varchar(36) not null,
    NOTES varchar(255),
    DATE_ timestamp not null,
    AMOUNT double precision not null,
    RECEIPT longvarbinary not null,
    --
    primary key (ID)
);