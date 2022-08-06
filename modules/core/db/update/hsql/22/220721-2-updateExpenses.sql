alter table TS_EXPENSES alter column DATE_ rename to DATE___U82454 ^
alter table TS_EXPENSES alter column DATE___U82454 set null ;
-- alter table TS_EXPENSES add column USER_ID varchar(36) ^
-- update TS_EXPENSES set USER_ID = <default_value> ;
-- alter table TS_EXPENSES alter column USER_ID set not null ;
alter table TS_EXPENSES add column USER_ID varchar(36) not null ;
alter table TS_EXPENSES add column ACTIVITY_TYPE_ID varchar(36) ;
alter table TS_EXPENSES add column DATE_ date ^
update TS_EXPENSES set DATE_ = current_date where DATE_ is null ;
alter table TS_EXPENSES alter column DATE_ set not null ;
