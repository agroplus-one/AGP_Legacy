-- Add/modify columns 
alter table o02agpe0.TB_ANEXO_MOD add IBAN2_ASEG_ORIGINAL VARCHAR2(24);
alter table o02agpe0.TB_ANEXO_MOD add IBAN2_ASEG_MODIF NUMBER(1);
alter table o02agpe0.TB_ANEXO_MOD add IBAN2_ASEG_MODIFICADO VARCHAR2(24);
-- Add comments to the columns 
comment on column o02agpe0.TB_ANEXO_MOD.IBAN_ASEG_ORIGINAL
  is 'IBAN ORIGINAL pago prima. Extraido de la situacion actualizada de la poliza';
comment on column o02agpe0.TB_ANEXO_MOD.IBAN_ASEG_MODIF
  is 'Indica si el IBAN pago prima ha cambiado respecto del recibido en la situacion actualizada (0 - NO, 1 - SI)';
comment on column o02agpe0.TB_ANEXO_MOD.IBAN_ASEG_MODIFICADO
  is 'Ultimo IBAN pago prima modificado y actual. Si no ha sido modificado el IBAN actual sera el original';
comment on column o02agpe0.TB_ANEXO_MOD.IBAN2_ASEG_ORIGINAL
  is 'IBAN ORIGINAL cobro siniestros. Extraido de la situacion actualizada de la poliza';
comment on column o02agpe0.TB_ANEXO_MOD.IBAN2_ASEG_MODIF
  is 'Indica si el IBAN cobro siniestros ha cambiado respecto del recibido en la situacion actualizada (0 - NO, 1 - SI)';
comment on column o02agpe0.TB_ANEXO_MOD.IBAN2_ASEG_MODIFICADO
  is 'Ultimo IBAN cobro siniestros modificado y actual. Si no ha sido modificado el IBAN actual sera el original';
  