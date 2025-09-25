-- Create table
create table TB_SW_CONS_CONTRATACION
(
  ID                NUMBER(15) not null,
  CODPLAN           NUMBER(4),  
  REF_POLIZ         VARCHAR2(7),
  RESPUESTA         CLOB,
  USUARIO           VARCHAR2(8) not null,
  FECHA             DATE not null
)


tablespace AGPTS001_ENC
  pctfree 10
  initrans 1
  maxtrans 255
  storage
  (
    initial 64K
    next 1M
    minextents 1
    maxextents unlimited
  );

-- Create/Recreate primary, unique and foreign key constraints 
alter table o02agpe0.TB_SW_CONS_CONTRATACION
  add constraint PK_TB_SW_CONS_CONTRATACION primary key (ID)
  using index 
  tablespace AGPIX001_ENC
  pctfree 10
  initrans 2
  maxtrans 255
  storage
  (
    initial 64K
    next 1M
    minextents 1
    maxextents unlimited
  );
-- Grant/Revoke object privileges 
grant select on o02agpe0.TB_SW_CONS_CONTRATACION to ROLE_O02AGPE0_SEL;
grant select, insert, update, delete on TB_SW_CONS_CONTRATACION to ROLE_O02AGPE0_UPD;
