-- Create table
create table O02AGPE0.TB_AUDIT_SINIESTROS_EXT
(
  ID             NUMBER(15) not null,
  CODIGO_INTERNO VARCHAR2(12) not null,
  HORA_LLAMADA   DATE not null,
  ENTRADA        CLOB not null,
  SALIDA         CLOB,
  RESULTADO      NUMBER(1) not null,
  MENSAJE        VARCHAR2(1000)
);

-- Create/Recreate primary, unique and foreign key constraints 
alter table O02AGPE0.TB_AUDIT_SINIESTROS_EXT
  add constraint PK_TB_AUDIT_SINIESTROS_EXT primary key (ID)
  using index;  
-- Grant/Revoke object privileges 
grant select on O02AGPE0.TB_AUDIT_SINIESTROS_EXT to ROLE_O02AGPE0_SEL;
grant select, insert, update, delete on O02AGPE0.TB_AUDIT_SINIESTROS_EXT to ROLE_O02AGPE0_UPD;

-- Create sequence 
create sequence o02agpe0.SQ_AUDIT_SINIESTROS_EXT
minvalue 1
maxvalue 9999999999999999999999999999
start with 221
increment by 1
cache 20;

-- Create table
create table o02agpe0.TB_AUDIT_ANEXOS_EXT
(
  ID             NUMBER(15) not null,
  CODIGO_INTERNO VARCHAR2(12) not null,
  HORA_LLAMADA   DATE not null,
  REFERENCIA     VARCHAR2(7) null,
  PLAN           NUMBER(4) null,
  IDCUPON        VARCHAR2(14) not null,
  ACUSE_RECIBO   CLOB null,
  POLIZA         CLOB null,
  POLIZACOMP     CLOB null,
  ESTADO_CONT    CLOB null,
  CUPON_MOD      CLOB null,
  RESULTADO      NUMBER(1) not null,
  MENSAJE        VARCHAR2(1000),
  SERVICIO       VARCHAR2(2) not null
);
-- Create/Recreate primary, unique and foreign key constraints 
alter table  o02agpe0.TB_AUDIT_ANEXOS_EXT
  add constraint TB_AUDIT_ANEXOS_EXT primary key (ID)
  using index;
-- Grant/Revoke object privileges 
grant select on  o02agpe0.TB_AUDIT_ANEXOS_EXT to ROLE_O02AGPE0_SEL;
grant select, insert, update, delete on  o02agpe0.TB_AUDIT_ANEXOS_EXT to ROLE_O02AGPE0_UPD;

-- Add comments to the columns 
comment on column o02agpe0.TB_AUDIT_ANEXOS_EXT.SERVICIO
  is 'SC - Solicitud cupon, CA - Confirmacion anexo, AC - Anulacion cupon';

-- Create sequence 
create sequence o02agpe0.SQ_AUDIT_ANEXOS_EXT
minvalue 1
maxvalue 9999999999999999999999999999
start with 221
increment by 1
cache 20;
  
-- Create table
create table o02agpe0.TB_CUPON_EXT
(
  IDPOLIZA number(15) not null,
  IDCUPON  varchar2(14) not null
);
-- Create/Recreate primary, unique and foreign key constraints 
alter table o02agpe0.o02agpe0.TB_CUPON_EXT
  add constraint PK_TB_CUPON_EXT primary key (IDPOLIZA, IDCUPON);
  
grant select on o02agpe0.TB_CUPON_EXT to ROLE_O02AGPE0_SEL;
grant select, insert, update, delete on o02agpe0.TB_CUPON_EXT to ROLE_O02AGPE0_UPD;