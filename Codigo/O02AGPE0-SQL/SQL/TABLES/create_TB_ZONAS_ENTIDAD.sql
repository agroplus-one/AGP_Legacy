-- Create table
create table o02agpe0.TB_ZONAS_ENTIDAD
(
  CODENTIDAD number(4) not null,
  CODZONA    NUMBER(5) not null,
  NOMZONA    VARCHAR2(40)
)
tablespace AGPTS001_ENC
  pctfree 10
  initrans 1
  maxtrans 255
  storage
  (
    initial 64K
    next 1
    minextents 1
    maxextents unlimited
  );
-- Add comments to the columns 
comment on column o02agpe0.TB_ZONAS_ENTIDAD.CODENTIDAD
  is 'Código de Entidad';
comment on column o02agpe0.TB_ZONAS_ENTIDAD.CODZONA
  is 'Código de Zona';
comment on column o02agpe0.TB_ZONAS_ENTIDAD.NOMZONA
  is 'Nombre de la Zona';

-- Create/Recreate primary, unique and foreign key constraints 
alter table o02agpe0.TB_ZONAS_ENTIDAD
  add constraint PK_ZONAS_ENTIDAD primary key (CODENTIDAD, CODZONA);

-- Grant/Revoke object privileges 
grant select on o02agpe0.TB_ZONAS_ENTIDAD to O02AGPE1;
grant select on o02agpe0.TB_ZONAS_ENTIDAD to ROLE_O02AGPE0_SEL;
grant select, insert, update, delete on o02agpe0.TB_ZONAS_ENTIDAD to ROLE_O02AGPE0_UPD;
