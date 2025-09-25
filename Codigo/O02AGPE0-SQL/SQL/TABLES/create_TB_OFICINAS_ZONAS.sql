-- Create table
create table o02agpe0.TB_OFICINAS_ZONAS
(
  CODENTIDAD NUMBER(4),
  CODOFICINA NUMBER(4),
  CODZONA    NUMBER(5)
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
-- Add comments to the table 
comment on table o02agpe0.TB_OFICINAS_ZONAS
  is 'Nueva tabla de Relación de Zonas por Oficina y Entidad';
-- Add comments to the columns 
comment on column o02agpe0.TB_OFICINAS_ZONAS.CODENTIDAD
  is 'Codigo de Entidad';
comment on column o02agpe0.TB_OFICINAS_ZONAS.CODOFICINA
  is 'Codigo de la Oficina';
comment on column o02agpe0.TB_OFICINAS_ZONAS.CODZONA
  is 'Codigo de Zona';

-- Create/Recreate primary, unique and foreign key constraints 
alter table o02agpe0.TB_OFICINAS_ZONAS
  add constraint PK_TB_OFICINAS_ZONAS primary key (CODENTIDAD, CODOFICINA, CODZONA);

-- Grant/Revoke object privileges 
grant select on o02agpe0.TB_OFICINAS_ZONAS to O02AGPE1;
grant select on o02agpe0.TB_OFICINAS_ZONAS to ROLE_O02AGPE0_SEL;
grant select, insert, update, delete on o02agpe0.TB_OFICINAS_ZONAS to ROLE_O02AGPE0_UPD;
