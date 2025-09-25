-- Create table
create table o02agpe0.TB_BLOQUEOS_ASEGURADOS
(
  ID                   NUMBER(15) not null,
  ID_ASEGURADO         NUMBER(15) not null,
  NIFCIF               VARCHAR2(9) not null,
  NOMBRE               VARCHAR2(20),
  APELLIDO1            VARCHAR2(40),
  APELLIDO2            VARCHAR2(40),
  FECHA_BLOQUEO        DATE,
  USUARIO_BLOQ         VARCHAR2(8),
  FECHA_DESBLOQUE      DATE,
  USUARIO_DESBLOQ      VARCHAR2(8),
  IDESTADO_ASEG        VARCHAR2(1) not null,     
  FECHA_AUDIT          DATE   
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
-- Add comments to the columns 
comment on column o02agpe0.TB_BLOQUEOS_ASEGURADOS.ID
  is 'Identificador';
comment on column o02agpe0.TB_BLOQUEOS_ASEGURADOS.ID_ASEGURADO
  is 'Identificador del Asegurado ';
comment on column o02agpe0.TB_BLOQUEOS_ASEGURADOS.IDESTADO_ASEG
  is '(B- Bloqueado, D-Desbloqueado, A-Alta)';
comment on column o02agpe0.TB_BLOQUEOS_ASEGURADOS.FECHA_AUDIT
  is 'Fecha de Auditoria';
-- Create/Recreate primary, unique and foreign key constraints 
alter table o02agpe0.TB_BLOQUEOS_ASEGURADOS
  add constraint PK_BLOQUEOS_ASEGURADOS primary key (ID)
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
alter table o02agpe0.TB_BLOQUEOS_ASEGURADOS
  add constraint FK_BLOQUES_ASEG foreign key (ID_ASEGURADO)
  references o02agpe0.TB_ASEGURADOS (ID) on delete cascade;
-- Grant/Revoke object privileges 
grant select on o02agpe0.TB_BLOQUEOS_ASEGURADOS to ROLE_O02AGPE0_SEL;
grant select, insert, update, delete on o02agpe0.TB_BLOQUEOS_ASEGURADOS to ROLE_O02AGPE0_UPD;


