create or replace view o02agpe0.vw_informe_coms_facturacion as
select rownum as id, aux2.*
  from (select aux.idCierre,
               aux.fecEmisionRecibo,
               aux.codGrupo,
               aux.grupo,
               aux.codFamilia,
               aux.familia,
               aux.codGrupoNeg,
               aux.grupoNeg,
               aux.linea,
               aux.nomLinea,
               aux.plan,
               aux.csb,
               sum(aux.coste) as coste
          from (select q.idcierre as idCierre,
                       q.recibo as fecEmisionRecibo,
                       gf.cod_grupo_familia_seg as codGrupo,
                       gf.detalle as grupo,
                       fam.cod_familia_seg as codFamilia,
                       fam.detalle as familia,
                       gn.grupo_negocio as codGrupoNeg,
                       gn.descripcion as grupoNeg,
                       l.codlinea as linea,
                       l.nomlinea as nomLinea,
                       l.codplan as plan,
                       case
                         when subent.codentidad between 8000 and 8999 then
                          subent.codentidad
                         else
                          subent.codentidadnomediadora
                       end as csb,
                       q.coste as coste
                  from o02agpe0.tb_subentidades_mediadoras subent
                 cross join o02agpe0.tb_lineas l
                 inner join o02agpe0.tb_lineas_grupo_negocio lgn on lgn.lineaseguroid =
                                                                    l.lineaseguroid
                 inner join o02agpe0.tb_sc_c_grupos_negocio gn on gn.grupo_negocio =
                                                                  lgn.grupo_negocio
                 inner join o02agpe0.lin_familia_seguro lfs on l.codlinea =
                                                               lfs.codlinea
                                                           and lfs.grupo_negocio =
                                                               gn.grupo_negocio
                 inner join o02agpe0.tp_familia_seguro fam on lfs.cod_familia_seg =
                                                              fam.cod_familia_seg
                 inner join o02agpe0.tp_grupo_familia_seg gf on gf.cod_grupo_familia_seg =
                                                                lfs.cod_grupo_familia_seg
                 inner join (select ci.id as idcierre,
                                   fa.fecha_emision_recibo as recibo,
                                   fa.plan,
                                   re.linea,
                                   hc.entmediadora,
                                   hc.subentmediadora,
                                   gn17.grupo_negocio,
                                   sum(gn17.frac_pri_com_neta) as coste
                              from o02agpe0.tb_coms_cierre ci
                             inner join o02agpe0.tb_coms_unif_fase fa on fa.idcierre =
                                                                         ci.id
                             inner join o02agpe0.tb_coms_unif17_recibo re on re.idfase =
                                                                             fa.id
                             inner join o02agpe0.tb_coms_unif17_poliza po on po.idrecibo =
                                                                             re.id
                             inner join o02agpe0.tb_coms_unif17_grupo_negocio gn17 on gn17.idpoliza =
                                                                                      po.id
                             inner join o02agpe0.tb_polizas p on po.referencia =
                                                                 p.referencia
                                                             and po.tipo_referencia =
                                                                 p.tiporef
							 inner join o02agpe0.tb_lineas l on l.lineaseguroid =
                                                                 p.lineaseguroid
                                                             and l.codplan =
                                                                 fa.plan
                                                             and l.codlinea =
                                                                 re.linea
                             inner join o02agpe0.tb_historico_colectivos hc on hc.rowid =
                                                                               (select hi.rowid
                                                                                  from (select *
                                                                                          from o02agpe0.tb_historico_colectivos h
                                                                                         order by h.fechacambio desc) hi
                                                                                 where hi.idcolectivo =
                                                                                       p.idcolectivo
                                                                                   and hi.fechaefecto <=
                                                                                       p.fechaenvio
                                                                                   and rownum = 1)
                             group by ci.id,
                                      fa.fecha_emision_recibo,
                                      fa.plan,
                                      re.linea,
                                      hc.entmediadora,
                                      hc.subentmediadora,
                                      gn17.grupo_negocio) q on q.plan =
                                                               l.codplan
                                                           and q.linea =
                                                               l.codlinea
                                                           and subent.codentidad =
                                                               q.entmediadora
                                                           and subent.codsubentidad =
                                                               q.subentmediadora
                                                           and gn.grupo_negocio =
                                                               q.grupo_negocio) aux
         group by aux.idCierre,
                  aux.fecEmisionRecibo,
                  aux.codGrupo,
                  aux.grupo,
                  aux.codFamilia,
                  aux.familia,
                  aux.codGrupoNeg,
                  aux.grupoNeg,
                  aux.linea,
                  aux.nomLinea,
                  aux.plan,
                  aux.csb
         order by aux.idcierre,
                  aux.csb,
                  aux.fecEmisionRecibo,
                  aux.linea,
                  aux.plan) aux2;
                  