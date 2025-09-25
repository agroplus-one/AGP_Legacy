SET DEFINE OFF;  
SET SERVEROUTPUT ON; 

create or replace view vw_informe_comisiones_unif as
select grupo_negocio.id as id,
       colectivo.codentidad,
       ent.nomentidad,
       poliza.oficina,
       oficina.nomoficina,
       colectivo.entmediadora,
       colectivo.subentmediadora,
       fase.plan,
       recibo.linea,
       lin.nomlinea,
       aplicacion.referencia,
       colectivo.idcolectivo,
       colectivo.ciftomador,
       aseg.nifcif,
       CASE
         when aseg.tipoidentificacion = 'CIF' then
          LTRIM(aseg.razonsocial)
         when (aseg.tipoidentificacion = 'NIF' OR
              aseg.tipoidentificacion = 'NIE') then
          LTRIM(aseg.nombre || ' ' || aseg.apellido1 || ' ' ||
                aseg.apellido2)
         else
          ''
       END as nombre_asegurado,
       recibo.recibo,
       fase.fase,
       fichero.fecha_carga,
       fase.fecha_emision_recibo,
       fichero.fecha_aceptacion,
       fichero.fecha_cierre as fecha_cierre,
       poliza.fecha_vigor,
       grupo_negocio.grupo_negocio,
       grupo_negocio.prima_comercial_neta,
       grupo_negocio.gd_admin,
       grupo_negocio.gd_adq,
       grupo_negocio.gd_comision_mediador,  
       grupo_negocio.gd_commed_entidad,
       grupo_negocio.gd_commed_esmed,
       grupo_negocio.ga_admin,
       grupo_negocio.ga_adq,
       grupo_negocio.ga_comision_mediador,
       grupo_negocio.ga_commed_entidad,
       grupo_negocio.ga_commed_esmed,
       grupo_negocio.gp_admin,
       grupo_negocio.gp_adq,
       grupo_negocio.gp_comision_mediador,
       grupo_negocio.gp_commed_entidad,
       grupo_negocio.gp_commed_esmed
  From o02agpe0.tb_coms_unif_fichero fichero
 INNER JOIN o02agpe0.tb_coms_unif_fase fase ON fichero.id = fase.idfichero
 INNER JOIN o02agpe0.tb_coms_unif_recibo recibo ON fase.id = recibo.idfase
 INNER JOIN o02agpe0.tb_coms_unif_aplicacion aplicacion ON recibo.id =
                                                           aplicacion.idrecibo
 INNER JOIN o02agpe0.tb_coms_unif_grupo_negocio grupo_negocio ON aplicacion.id =
                                                                 grupo_negocio.idaplicacion
  LEFT OUTER JOIN o02agpe0.tb_polizas poliza ON poliza.idestado >= 8
                                            and aplicacion.referencia =
                                                poliza.referencia
                                            and aplicacion.tipo_referencia =
                                                poliza.tiporef
  LEFT OUTER JOIN o02agpe0.tb_colectivos colectivo ON poliza.idcolectivo =
                                                      colectivo.id
  LEFT OUTER JOIN o02agpe0.tb_asegurados aseg ON poliza.idasegurado =
                                                 aseg.id
  LEFT OUTER JOIN o02agpe0.tb_lineas lin ON lin.codplan = fase.plan
                                        and lin.codlinea = recibo.linea
  LEFT OUTER JOIN o02agpe0.tb_entidades ent ON colectivo.codentidad =
                                               ent.codentidad
  LEFT OUTER JOIN o02agpe0.tb_oficinas oficina ON oficina.codentidad =
                                                  colectivo.codentidad
                                              and oficina.codoficina =
                                                  poliza.oficina
 where fichero.tipo_fichero = 'C'
   and poliza.idasegurado not in (select blq.id_asegurado from o02agpe0.tb_bloqueos_asegurados blq where blq.idestado_aseg = 'B')
union
select grupo_negocio.id as id,
       colectivo.codentidad,
       ent.nomentidad,
       poliza.oficina,
       oficina.nomoficina,
       (select entmediadora
          from (select h.entmediadora, h.fechaefecto, h.idcolectivo
                  from o02agpe0.tb_historico_colectivos h
                 order by h.fechacambio desc)
         where idcolectivo = poliza.idcolectivo
           and fechaefecto <= poliza.fechaenvio
           and rownum = 1) entmediadora,
       (select subentmediadora
          from (select h.subentmediadora, h.fechaefecto, h.idcolectivo
                  from o02agpe0.tb_historico_colectivos h
                 order by h.fechacambio desc)
         where idcolectivo = poliza.idcolectivo
           and fechaefecto <= poliza.fechaenvio
           and rownum = 1) subentmediadora,
       fase.plan,
       recibo.linea,
       lin.nomlinea,
       polUnif17.referencia,
       colectivo.idcolectivo,
       colectivo.ciftomador,
       aseg.nifcif,
       DECODE(LTRIM(aseg.razonsocial),
              '',
              LTRIM(aseg.nombre || ' ' || aseg.apellido1 || ' ' ||
                    aseg.apellido2),
              LTRIM(aseg.razonsocial)) as nombre_asegurado,
       recibo.recibo,
       fase.fase,
       fichero.fecha_carga,
       fase.fecha_emision_recibo,
       fichero.fecha_aceptacion,
       fichero.fecha_cierre as fecha_cierre,
       polUnif17.fecha_vigor as fecha_vigor,
       grupo_negocio.grupo_negocio,
       grupo_negocio.frac_pri_com_neta,
       grupo_negocio.gd_imp_admin,
       grupo_negocio.gd_imp_adq,
       grupo_negocio.gd_imp_com_mediador,
       grupo_negocio.gd_imp_commed_entidad,
       grupo_negocio.gd_imp_commed_esmed,
       grupo_negocio.ga_admin,
       grupo_negocio.ga_adq,
       grupo_negocio.ga_comision_mediador,
       grupo_negocio.ga_commed_entidad,
       grupo_negocio.ga_commed_esmed,
       grupo_negocio.gp_admin,
       grupo_negocio.gp_adq,
       grupo_negocio.gp_comision_mediador,
       grupo_negocio.gp_commed_entidad,
       grupo_negocio.gp_commed_esmed
  From o02agpe0.tb_coms_unif_fichero fichero
 INNER JOIN o02agpe0.tb_coms_unif_fase fase ON fichero.id = fase.idfichero
 INNER JOIN o02agpe0.tb_coms_unif17_recibo recibo ON fase.id =
                                                     recibo.idfase
 INNER JOIN o02agpe0.tb_coms_unif17_poliza polUnif17 ON polUnif17.idrecibo =
                                                        recibo.id
  LEFT OUTER JOIN o02agpe0.tb_lineas lin ON lin.codplan = fase.plan
                                        and lin.codlinea = recibo.linea
  LEFT OUTER JOIN o02agpe0.tb_polizas poliza ON poliza.idestado >= 8
                                            and poliza.lineaseguroid =
                                                lin.lineaseguroid
                                            and poliza.referencia =
                                                polUnif17.referencia
                                            and poliza.dc = polUnif17.Dc
                                            and poliza.tiporef =
                                                polUnif17.Tipo_Referencia
 INNER JOIN o02agpe0.tb_coms_unif17_grupo_negocio grupo_negocio ON grupo_negocio.idpoliza =
                                                                   polUnif17.id
 INNER JOIN o02agpe0.tb_coms_unif_colectivo unifCol ON polUnif17.Idcolectivo =
                                                       unifCol.Id
  LEFT OUTER JOIN o02agpe0.tb_colectivos colectivo ON unifCol.Referencia =
                                                      colectivo.Idcolectivo
                                                  and to_char(unifCol.dc) =
                                                      colectivo.dc
                                                  and colectivo.lineaseguroid =
                                                      lin.lineaseguroid
 INNER JOIN o02agpe0.tb_coms_unif17_asegurado aseg ON polUnif17.idasegurado =
                                                      aseg.id
  LEFT OUTER JOIN o02agpe0.tb_entidades ent ON colectivo.codentidad =
                                               ent.codentidad
  LEFT OUTER JOIN o02agpe0.tb_oficinas oficina ON oficina.codentidad =
                                                  colectivo.codentidad
                                              and oficina.codoficina =
                                                  poliza.oficina
 where fichero.tipo_fichero = 'U'
   and poliza.idasegurado not in (select blq.id_asegurado from o02agpe0.tb_bloqueos_asegurados blq where blq.idestado_aseg = 'B');

