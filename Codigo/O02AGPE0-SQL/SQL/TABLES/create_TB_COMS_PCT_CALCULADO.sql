-- Create table
create table o02agpe0.TB_COMS_PCT_CALCULADO
(
  IDCOMPARATIVA number(15) not null,
  IDGRUPO       varchar2(1) not null,
  PCT_CALCULADO number(5,2) not null
)
;
-- Create/Recreate primary, unique and foreign key constraints 
alter table o02agpe0.TB_COMS_PCT_CALCULADO
  add constraint PK_TB_COMS_PCT_CALCULADO primary key (IDCOMPARATIVA, IDGRUPO);
  
grant select on o02agpe0.TB_COMS_PCT_CALCULADO to ROLE_O02AGPE0_SEL;
grant select, insert, update, delete on o02agpe0.TB_COMS_PCT_CALCULADO to ROLE_O02AGPE0_UPD;  