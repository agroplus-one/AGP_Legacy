-- TABLA: TB_INF_POLIZAS   ESQUEMA:o02agpe1
----------------------------------------------
-- Add/modify columns 
alter table O02AGPE1.TB_INF_POLIZAS add IDESTADO_AGRO NUMBER(3);
alter table O02AGPE1.TB_INF_POLIZAS add FECHA_SEGUIMIENTO date;
-- Add comments to the columns 
comment on column O02AGPE1.TB_INF_POLIZAS.IDESTADO_AGRO
  is 'Estado de la póliza en Agroseguro';
comment on column O02AGPE1.TB_INF_POLIZAS.FECHA_SEGUIMIENTO
  is 'Fecha en la que se modifica el estado Agroseguro';
  

-- TABLA: TB_INF_POLIZAS_BAK   ESQUEMA:o02agpe1
-------------------------------------------------
-- Add/modify columns 
alter table O02AGPE1.TB_INF_POLIZAS_BAK add IDESTADO_AGRO number(3);
alter table O02AGPE1.TB_INF_POLIZAS_BAK add FECHA_SEGUIMIENTO date;
-- Add comments to the columns 
comment on column O02AGPE1.TB_INF_POLIZAS_BAK.IDESTADO_AGRO
  is 'Estado de la póliza en Agroseguro';
comment on column O02AGPE1.TB_INF_POLIZAS_BAK.FECHA_SEGUIMIENTO
  is 'Fecha en la que se modifica el estado Agroseguro';
  
  
-- TABLA: TB_INF_ANEXOS_MODIFICACION   ESQUEMA:o02agpe1
--------------------------------------------------------
-- Add/modify columns 
alter table O02AGPE1.TB_INF_ANEXOS_MODIFICACION add IDESTADO_AGRO NUMBER(3);
alter table O02AGPE1.TB_INF_ANEXOS_MODIFICACION add FECHA_SEGUIMIENTO date;
-- Add comments to the columns 
comment on column O02AGPE1.TB_INF_ANEXOS_MODIFICACION.IDESTADO_AGRO
  is 'Estado de la póliza en Agroseguro';
comment on column O02AGPE1.TB_INF_ANEXOS_MODIFICACION.FECHA_SEGUIMIENTO
  is 'Fecha en la que se modifica el estado Agroseguro';


-- TABLA: TB_INF_ANEXOS_MODIFICACION_BAK   ESQUEMA:o02agpe1
-----------------------------------------------------------
-- Add/modify columns 
alter table O02AGPE1.TB_INF_ANEXOS_MODIFICACION_BAK add IDESTADO_AGRO NUMBER(3);
alter table O02AGPE1.TB_INF_ANEXOS_MODIFICACION_BAK add FECHA_SEGUIMIENTO date;
-- Add comments to the columns 
comment on column O02AGPE1.TB_INF_ANEXOS_MODIFICACION_BAK.IDESTADO_AGRO
  is 'Estado de la póliza en Agroseguro';
comment on column O02AGPE1.TB_INF_ANEXOS_MODIFICACION_BAK.FECHA_SEGUIMIENTO
  is 'Fecha en la que se modifica el estado Agroseguro';
  
  
alter table O02AGPE1.TB_POLIZAS_BAK add FECHA_ENVIO_IRIS date;
alter table O02AGPE1.TB_POLIZAS_BAK add FECHA_RECEPCION_IRIS date;
alter table O02AGPE1.TB_POLIZAS_BAK add ESTADO_IRIS varchar2(5);  
alter table O02AGPE1.TB_POLIZAS_BAK add IDESTADO_AGRO number(3);
alter table O02AGPE1.TB_POLIZAS_BAK add FECHA_SEGUIMIENTO date;
alter table O02AGPE1.TB_POLIZAS_BAK add COSTE_TOMADOR_AGRO number(11,2);
