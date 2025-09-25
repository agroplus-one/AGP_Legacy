-- Add/modify columns 
alter table o02agpe1.TB_INF_POLIZAS_BAK add SINIESTROS_IBAN VARCHAR2(30);
-- Add comments to the columns 
comment on column o02agpe1.TB_INF_POLIZAS_BAK.SINIESTROS_IBAN
  is 'CCC  del cobro siniestros';
  
 