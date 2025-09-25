-- NUEVA TABLA: TB_MESES_VENC
--============================================
-- Create table
create table TB_MESES_VENC
(
  CODLINEA  number(3) not null,
  CODMODULO VARCHAR2(5) not null,
  NUM_MESES NUMBER(5) not null
)
tablespace AGPTS001
  pctfree 10
  initrans 1
  maxtrans 255
  storage
  (
    initial 64K
    minextents 1
    maxextents unlimited
  );
-- Add comments to the table 
comment on table TB_MESES_VENC
  is 'Tabla de Parametrización de meses de vigencia por línea y módulo';
-- Add comments to the columns 
comment on column TB_MESES_VENC.CODLINEA
  is 'Código de Línea';
comment on column TB_MESES_VENC.CODMODULO
  is 'Código del Módulo';
comment on column TB_MESES_VENC.NUM_MESES
  is 'Número de meses del vencimiento asociado a la línea y Módulo';
-- Create/Recreate primary, unique and foreign key constraints 
alter table TB_MESES_VENC
  add constraint PK_MESES_VENC primary key (CODLINEA, CODMODULO);
-- Grant/Revoke object privileges 
grant select on TB_MESES_VENC to O02AGPE1;
grant select on TB_MESES_VENC to ROLE_O02AGPE0_SEL;
grant select, insert, update, delete on TB_MESES_VENC to ROLE_O02AGPE0_UPD;


-- MODIF TABLA: TB_POLIZAS
--============================================
-- Add/modify columns 
alter table TB_POLIZAS add FECHA_ENV_CANC_IRIS date;
alter table TB_POLIZAS add ESTADO_CANC_IRIS VARCHAR2(5);
-- Add comments to the columns 
comment on column TB_POLIZAS.FECHA_ENV_CANC_IRIS
  is 'Fecha en la que se envía la Cancelación para integrarla en IRIS';
comment on column TB_POLIZAS.ESTADO_CANC_IRIS
  is 'Estado de la cancelación en la integración en IRIS';