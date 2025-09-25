-- Create table
create table O02AGPE0.TB_DESCONEXION_ENTIDAD
(
  CODENTIDAD        NUMBER(4) not null,
  NOMENTIDAD        VARCHAR2(30),
  CODENTIDAD_MED    NUMBER(4),
  CODSUBENTIDAD_MED NUMBER(4)
)
tablespace AGPTS001
  pctfree 10
  initrans 1
  maxtrans 255
  storage
  (
    initial 64K
    next 8K
    minextents 1
    maxextents unlimited
  );
-- Add comments to the table 
comment on table O02AGPE0.TB_DESCONEXION_ENTIDAD
  is 'Parametrización de Desconexion de Entidades';
-- Add comments to the columns 
comment on column O02AGPE0.TB_DESCONEXION_ENTIDAD.CODENTIDAD
  is 'Codigo de la Entidad a Desconectar';
comment on column O02AGPE0.TB_DESCONEXION_ENTIDAD.NOMENTIDAD
  is 'Nombre de la Entidad a Desconectar';
comment on column O02AGPE0.TB_DESCONEXION_ENTIDAD.CODENTIDAD_MED
  is 'Codigo de la Entidad Mediadora correspondiente a la Entidad a Desconectar';
comment on column O02AGPE0.TB_DESCONEXION_ENTIDAD.CODSUBENTIDAD_MED
  is 'Código de la subentidad mediadora correspondiente a la Entidad a Desconectar';
-- Create/Recreate primary, unique and foreign key constraints 
alter table O02AGPE0.TB_DESCONEXION_ENTIDAD
  add constraint PK_CODENTIDAD primary key (CODENTIDAD)
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
grant select on o02agpe0.TB_DESCONEXION_ENTIDAD to ROLE_O02AGPE0_SEL; 
grant select, insert, update, delete on o02agpe0.TB_DESCONEXION_ENTIDAD to ROLE_O02AGPE0_UPD; 

/  
show errors;