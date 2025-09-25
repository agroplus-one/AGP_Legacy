-- Add/modify columns 
alter table o02agpe1.TB_INF_DATOS_ASEGURADO add IBAN2 VARCHAR2(30);
alter table o02agpe1.TB_INF_DATOS_ASEGURADO add DEST_DOMICILIACION VARCHAR2(1) default 'A';
alter table o02agpe1.TB_INF_DATOS_ASEGURADO add TITULAR_CUENTA VARCHAR2(100);
-- Add comments to the columns 
comment on column o02agpe1.TB_INF_DATOS_ASEGURADO.IBAN2
  is 'Se corresponde con la concatenación y separación en grupos de 4 de los campos IBAN2 y CCC2 de TB_DATOS_ASEGURADOS (Cuenta Cobro Siniestro)';
comment on column o02agpe1.TB_INF_DATOS_ASEGURADO.DEST_DOMICILIACION
  is 'Destinatario de la domiciliación (A - Asegurado, T - Tomador, O - Otros)';
comment on column o02agpe1.TB_INF_DATOS_ASEGURADO.TITULAR_CUENTA
  is 'Titular de la cuenta';