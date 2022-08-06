alter table TS_EXPENSES add column REJECTION_REASON varchar(255) ;
alter table TS_EXPENSES add column STATUS varchar(50) ^
update TS_EXPENSES set STATUS = 'new' where STATUS is null ;
alter table TS_EXPENSES alter column STATUS set not null ;
