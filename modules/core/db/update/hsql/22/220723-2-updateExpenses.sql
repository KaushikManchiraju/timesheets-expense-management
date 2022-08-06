alter table TS_EXPENSES alter column PROJECT_ID rename to PROJECT_ID__U76896 ^
alter table TS_EXPENSES alter column PROJECT_ID__U76896 set null ;
alter table TS_EXPENSES drop constraint FK_TS_EXPENSES_PROJECT ;
drop index IDX_TS_EXPENSES_PROJECT ;
