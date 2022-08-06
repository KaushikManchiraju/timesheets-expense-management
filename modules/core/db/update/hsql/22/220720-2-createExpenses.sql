alter table TS_EXPENSES add constraint FK_TS_EXPENSES_PROJECT foreign key (PROJECT_ID) references TS_PROJECT(ID);
alter table TS_EXPENSES add constraint FK_TS_EXPENSES_TASK foreign key (TASK_ID) references TS_TASK(ID);
create index IDX_TS_EXPENSES_PROJECT on TS_EXPENSES (PROJECT_ID);
create index IDX_TS_EXPENSES_TASK on TS_EXPENSES (TASK_ID);
