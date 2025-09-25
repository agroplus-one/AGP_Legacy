-- Create table
create table o02agpe0.TB_GED_CONFIG
(
  GED_PARAM       varchar2(30) not null,
  GED_VALOR       varchar2(200) not null,
  GED_DESCRIPCION varchar2(2000)
);
-- Create/Recreate primary, unique and foreign key constraints 
alter table o02agpe0.TB_GED_CONFIG
  add constraint PK_TB_GED_CONFIG primary key (GED_PARAM);
  
-- Create table
create table o02agpe0.TB_GED_DOC_POLIZA
(
  IDPOLIZA     NUMBER(15) not null,
  IDDOCUMENTUM VARCHAR2(20) not null
);
-- Create/Recreate primary, unique and foreign key constraints 
alter table o02agpe0.TB_GED_DOC_POLIZA
  add constraint PK_TB_GED_ODC_POLIZA primary key (IDPOLIZA, IDDOCUMENTUM) using index;

-- Create table
create table o02agpe0.TB_GED_AUDIT
(
  ID		  NUMBER(15) not null,
  IDPOLIZA    NUMBER(15) not null,
  NOMBRE      varchar2(260) not null,
  ESTADO      number(1) not null,
  DESCRIPCION varchar2(2000),
  CODUSUARIO  varchar2(8) not null,
  FECHA       date not null
);
-- Add comments to the columns 
comment on column o02agpe0.TB_GED_AUDIT.ESTADO
  is '0 - PDTE, 1 - OK, 2 - KO';
comment on column o02agpe0.TB_GED_AUDIT.CODUSUARIO
  is 'USUARIO ONLINE O LITERAL "BATCH" PARA CADENAS';
-- Create/Recreate primary, unique and foreign key constraints 
alter table o02agpe0.TB_GED_AUDIT
  add constraint PK_TB_GED_AUDIT primary key (ID);  
-- Create/Recreate indexes 
create index o02agpe0.IDX_TB_GED_AUDIT on o02agpe0.TB_GED_AUDIT (idpoliza, estado);  

create sequence o02agpe0.SQ_GED_AUDIT
minvalue 1
maxvalue 999999999999999
start with 1
increment by 1;