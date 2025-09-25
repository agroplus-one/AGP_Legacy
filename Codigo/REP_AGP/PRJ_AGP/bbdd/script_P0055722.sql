-- Add/modify columns 
alter table o02agpe0.TB_POLIZAS add FECHA_ENVIO_IRIS date;
alter table o02agpe0.TB_POLIZAS add FECHA_RECEPCION_IRIS date;
alter table o02agpe0.TB_POLIZAS add ESTADO_IRIS varchar2(5);
-- Add comments to the columns 
comment on column o02agpe0.TB_POLIZAS.FECHA_ENVIO_IRIS
  is 'Fecha en la que se envía la poliza para integrarla en IRIS';
comment on column o02agpe0.TB_POLIZAS.FECHA_RECEPCION_IRIS
  is 'Fecha en la que realiza la integración de la póliza en IRIS';
comment on column o02agpe0.TB_POLIZAS.ESTADO_IRIS
  is 'Estado de la póliza en la integración en IRIS';