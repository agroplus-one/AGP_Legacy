SET DEFINE OFF;  
SET SERVEROUTPUT ON; 

create or replace view vw_informe_impagados_unif as
select rownum as id,                                  colectivo.codentidad,                           ent.nomentidad,
       colectivo.entmediadora,                        colectivo.subentmediadora,                      colectivo.idcolectivo,
       tom.ciftomador,                                tom.razonsocial nom_tomador,                    fase.plan,
       recibo.linea,                                  lin.nomlinea,                                   fase.fase,
       recibo.recibo,                                 aplicacion.referencia,                          fichero.fecha_carga,
       fichero.fecha_aceptacion,                      fichero.fecha_cierre as fecha_cierre,           aplicacion.importe_saldo_pdte,
       aplicacion.importe_cobro_recibido,             grupo_negocio.ga_admin,                         grupo_negocio.ga_adq,
       grupo_negocio.ga_comision_mediador,            grupo_negocio.ga_commed_entidad,                grupo_negocio.ga_commed_esmed,
       fase.fecha_emision_recibo,                     grupo_negocio.GRUPO_NEGOCIO
  From o02agpe0.tb_coms_unif_fichero fichero
       INNER JOIN o02agpe0.tb_coms_unif_fase    fase ON fichero.id = fase.idfichero
       INNER JOIN o02agpe0.tb_coms_unif_recibo  recibo ON fase.id = recibo.idfase
       INNER JOIN o02agpe0.tb_coms_unif_aplicacion aplicacion ON recibo.id = aplicacion.idrecibo
       INNER JOIN o02agpe0.tb_coms_unif_grupo_negocio grupo_negocio ON aplicacion.id = grupo_negocio.idaplicacion
       LEFT OUTER JOIN o02agpe0.tb_polizas poliza ON aplicacion.referencia = poliza.referencia
                                                    and aplicacion.tipo_referencia = poliza.tiporef
       LEFT OUTER JOIN o02agpe0.tb_colectivos colectivo ON poliza.idcolectivo = colectivo.id
       LEFT OUTER JOIN o02agpe0.tb_tomadores tom ON colectivo.ciftomador = tom.ciftomador
                                                     and colectivo.codentidad = tom.codentidad
       LEFT OUTER JOIN o02agpe0.tb_lineas lin ON lin.codplan=fase.plan and lin.codlinea= recibo.linea
       LEFT OUTER JOIN o02agpe0.tb_entidades ent ON colectivo.codentidad= ent.codentidad
 where fichero.tipo_fichero = 'I'
 and poliza.idasegurado not in (select blq.id_asegurado from o02agpe0.tb_bloqueos_asegurados blq where blq.idestado_aseg = 'B');
