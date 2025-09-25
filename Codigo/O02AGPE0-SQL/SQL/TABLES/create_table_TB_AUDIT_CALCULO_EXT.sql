
-- Create table
create table o02agpe0.TB_AUDIT_CALCULO_EXT
(
  ID             NUMBER(15) not null,
  CODIGO_INTERNO VARCHAR2(12) not null,
  HORA_LLAMADA   DATE not null,
  ENTRADA        CLOB not null,
  ACUSE_RECIBO   CLOB,
  CALCULO        CLOB,
  RESULTADO      NUMBER(1) not null,
  MENSAJE        VARCHAR2(1000)
)
tablespace AGPTS001
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
alter table o02agpe0.TB_AUDIT_CALCULO_EXT
  add constraint PK_TB_AUDIT_CALCULO_EXT primary key (ID)
  using index 
  tablespace AGPTS001
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
grant select on o02agpe0.TB_AUDIT_CALCULO_EXT to ROLE_O02AGPE0_SEL;
grant select, insert, update, delete on TB_AUDIT_CALCULO_EXT to ROLE_O02AGPE0_UPD;
