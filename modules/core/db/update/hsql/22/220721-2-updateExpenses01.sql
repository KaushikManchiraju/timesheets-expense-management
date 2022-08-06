alter table TS_EXPENSES add constraint FK_TS_EXPENSES_USER foreign key (USER_ID) references SEC_USER(ID);
create index IDX_TS_EXPENSES_USER on TS_EXPENSES (USER_ID);
