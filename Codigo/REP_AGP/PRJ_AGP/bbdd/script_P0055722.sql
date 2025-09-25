-- Add/modify columns 
alter table o02agpe0.TB_POLIZAS add FECHA_ENVIO_IRIS date;
alter table o02agpe0.TB_POLIZAS add FECHA_RECEPCION_IRIS date;
alter table o02agpe0.TB_POLIZAS add ESTADO_IRIS varchar2(5);
-- Add comments to the columns 
comment on column o02agpe0.TB_POLIZAS.FECHA_ENVIO_IRIS
  is 'Fecha en la que se env�a la poliza para integrarla en IRIS';
comment on column o02agpe0.TB_POLIZAS.FECHA_RECEPCION_IRIS
  is 'Fecha en la que realiza la integraci�n de la p�liza en IRIS';
comment on column o02agpe0.TB_POLIZAS.ESTADO_IRIS
  is 'Estado de la p�liza en la integraci�n en IRIS';