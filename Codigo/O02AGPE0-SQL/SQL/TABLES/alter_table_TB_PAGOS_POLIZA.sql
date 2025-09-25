-- Add/modify columns 
alter table o02agpe0.TB_PAGOS_POLIZA add IBAN2 VARCHAR2(4);
alter table o02agpe0.TB_PAGOS_POLIZA add CCCBANCO2 VARCHAR2(20);
-- Add comments to the columns 
comment on column o02agpe0.TB_PAGOS_POLIZA.CCCBANCO
  is 'CCC  del pago prima';
comment on column o02agpe0.TB_PAGOS_POLIZA.IBAN
  is 'Iban pago prima';
comment on column o02agpe0.TB_PAGOS_POLIZA.IBAN2
  is 'Iban cobro siniestros';
comment on column o02agpe0.TB_PAGOS_POLIZA.CCCBANCO2
  is 'CCC  del cobro siniestros';  