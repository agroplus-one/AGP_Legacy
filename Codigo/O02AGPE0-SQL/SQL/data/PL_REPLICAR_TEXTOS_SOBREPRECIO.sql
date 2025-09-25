declare
   v_plan_origen   NUMBER(4) := 2019;
   v_plan_destino  NUMBER(4) := 2020;
   
begin
  /*
  PL para copiar los textos que se muestran en los borradores de sobreprecio de un plan a otro
  */
  --borro si hubiera algo de antes.
  delete o02agpe0.tb_sbp_txt_desc_riesgo where codplan = v_plan_destino;
  delete o02agpe0.tb_sbp_txt_franquicia where codplan = v_plan_destino;
  delete o02agpe0.tb_sbp_txt_periodo_carencia where codplan = v_plan_destino;
  delete o02agpe0.tb_sbp_txt_prima_total where codplan = v_plan_destino;
  delete o02agpe0.tb_sbp_txt_riesgo_garantizado where codplan = v_plan_destino;
  
  --o02agpe0.tb_sbp_txt_desc_riesgo
  insert into o02agpe0.tb_sbp_txt_desc_riesgo values (o02agpe0.sq_sbp_txt_desc_riesgo.nextval, v_plan_destino, (select texto from o02agpe0.tb_sbp_txt_desc_riesgo where codplan = v_plan_origen));
  --o02agpe0.tb_sbp_txt_franquicia
  insert into o02agpe0.tb_sbp_txt_franquicia values (o02agpe0.sq_sbp_txt_franquicia.nextval, v_plan_destino, (select texto from o02agpe0.tb_sbp_txt_franquicia where codplan = v_plan_origen));
  --o02agpe0.tb_sbp_txt_periodo_carencia
  insert into o02agpe0.tb_sbp_txt_periodo_carencia values (o02agpe0.sq_sbp_txt_periodo_carencia.nextval, v_plan_destino, (select texto from o02agpe0.tb_sbp_txt_periodo_carencia where codplan = v_plan_origen));
  --o02agpe0.tb_sbp_txt_prima_total
  insert into o02agpe0.tb_sbp_txt_prima_total values (o02agpe0.sq_sbp_txt_prima_total.nextval, v_plan_destino, (select texto from o02agpe0.tb_sbp_txt_prima_total where codplan = v_plan_origen));
  --o02agpe0.tb_sbp_txt_riesgo_garantizado
  insert into o02agpe0.tb_sbp_txt_riesgo_garantizado values (o02agpe0.sq_sbp_txt_riesgo_garantizado.nextval, v_plan_destino, (select texto from o02agpe0.tb_sbp_txt_riesgo_garantizado where codplan = v_plan_origen));
  
  commit;

end;
