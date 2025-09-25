SET DEFINE OFF;  
SET SERVEROUTPUT ON; 

create or replace view vw_informe_deuda_aplazada_unif as
select rownum as id,
       colectivo.codentidad,
       ent.nomentidad,
       colectivo.entmediadora,
       colectivo.subentmediadora,
       colectivo.idcolectivo,
       tom.ciftomador,
       tom.razonsocial nom_tomador,
       fase.plan,
       recibo.linea,
       lin.nomlinea,
       fase.fase,
       recibo.recibo,
       aplicacion.referencia,
       fichero.fecha_aceptacion,
       fichero.fecha_cierre as fecha_cierre,
       fase.fecha_emision_recibo,
       deuda_aplazada.ga_admin,
       deuda_aplazada.ga_adq,
       deuda_aplazada.ga_commed_entidad,
       deuda_aplazada.ga_commed_esmed,
       (deuda_aplazada.ga_admin + deuda_aplazada.ga_adq + deuda_aplazada.ga_commed_entidad + deuda_aplazada.ga_commed_esmed) as gasto_pagado,
       grupo_negocio.gd_admin,
       grupo_negocio.gd_adq,
       (gd_admin + gd_adq) as total_gd_entidad,
       grupo_negocio.gd_commed_entidad,
       grupo_negocio.gd_commed_esmed,
       (gd_commed_entidad + gd_commed_esmed) as total_gd_mediador,
       grupo_negocio.GRUPO_NEGOCIO
  From o02agpe0.tb_coms_unif_fichero fichero
       INNER JOIN o02agpe0.tb_coms_unif_fase fase ON fichero.id = fase.idfichero
       INNER JOIN o02agpe0.tb_coms_unif_recibo  recibo ON fase.id = recibo.idfase
       INNER JOIN o02agpe0.tb_coms_unif_aplicacion aplicacion ON recibo.idaplicacion_da = aplicacion.id
       INNER JOIN o02agpe0.tb_coms_unif_grupo_negocio grupo_negocio ON recibo.id = grupo_negocio.idrecibo_da
       INNER JOIN o02agpe0.tb_coms_unif_indiv_colectivo ind_col ON ind_col.id = aplicacion.idindivcol_da
       INNER JOIN o02agpe0.tb_coms_unif_gas_abo_deuda_apl deuda_aplazada ON grupo_negocio.id = deuda_aplazada.idgruponegocio
       LEFT OUTER JOIN o02agpe0.tb_polizas poliza ON aplicacion.referencia = poliza.referencia
                       and aplicacion.tipo_referencia = poliza.tiporef
       LEFT OUTER JOIN o02agpe0.tb_colectivos colectivo ON poliza.idcolectivo = colectivo.id
       LEFT OUTER JOIN o02agpe0.tb_tomadores tom ON colectivo.ciftomador = tom.ciftomador AND colectivo.codentidad = tom.codentidad
       LEFT OUTER JOIN o02agpe0.tb_lineas lin ON lin.codlinea = recibo.linea AND fase.plan = lin.codplan
       LEFT OUTER JOIN o02agpe0.tb_entidades ent ON colectivo.codentidad = ent.codentidad
 where fichero.tipo_fichero = 'D'
   and poliza.idasegurado not in (select blq.id_asegurado from o02agpe0.tb_bloqueos_asegurados blq where blq.idestado_aseg = 'B');
