-- Add/modify columns 
alter table o02agpe0.TB_DATOS_ASEGURADOS add CCC2 VARCHAR2(20);
alter table o02agpe0.TB_DATOS_ASEGURADOS add IBAN2 VARCHAR2(4);
-- Add comments to the columns 
comment on column o02agpe0.TB_DATOS_ASEGURADOS.CCC
  is 'Numero de cuenta pago prima';
comment on column o02agpe0.TB_DATOS_ASEGURADOS.IBAN
  is 'Iban pago prima';
comment on column o02agpe0.TB_DATOS_ASEGURADOS.CCC2
  is 'Numero de cuenta cobro siniestros';
comment on column o02agpe0.TB_DATOS_ASEGURADOS.IBAN2
  is 'Iban cobro siniestros';
update o02agpe0.tb_datos_asegurados set iban2 = iban, ccc2 = ccc where dest_domiciliacion = 'A';