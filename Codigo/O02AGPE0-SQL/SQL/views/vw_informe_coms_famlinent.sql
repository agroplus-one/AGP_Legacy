create or replace view o02agpe0.vw_informe_coms_famlinent as
select rownum as id, aux.*
  from (select q.idcierre as idcierre,
               q.recibo as fecEmisionRecibo,
               gf.cod_grupo_familia_seg as codgrupo,
               gf.detalle as grupo,
               fam.cod_familia_seg as codfamilia,
               fam.detalle as familia,
               gn.grupo_negocio as codGrupoNeg,
               gn.descripcion as grupoNeg,
               l.codlinea as linea,
               l.nomlinea as nomlinea,
               l.codplan as plan,
               case
                 when subent.codentidad between 8000 and 8999 then
                  subent.codentidad
                 else
                  subent.codentidadnomediadora
               end as csb,
               subent.codentidad as codentmed,
               subent.codsubentidad as codsubentmed,
               q.total as total,
               q.reglamento as reglamento,
               (q.total + q.reglamento) as comision
          from o02agpe0.tb_subentidades_mediadoras subent
         inner join (select ci.id as idcierre,
                           lin.lineaseguroid as lineaseguroid,
                           hc.entmediadora,
                           hc.subentmediadora,
                           gn.grupo_negocio,
                           fa.fecha_emision_recibo as recibo,
                           sum(gn.ga_commed_entidad) as total,
                           0 as reglamento
                      from o02agpe0.tb_coms_cierre ci
                     inner join o02agpe0.tb_coms_unif_fase fa on fa.idcierre =
                                                                 ci.id
                     inner join o02agpe0.tb_coms_unif17_recibo re on re.idfase =
                                                                     fa.id
                     inner join o02agpe0.tb_coms_unif17_poliza po on po.idrecibo =
                                                                     re.id
                     inner join o02agpe0.tb_coms_unif17_grupo_negocio gn on gn.idpoliza =
                                                                            po.id
                     inner join o02agpe0.tb_lineas lin on fa.plan =
                                                          lin.codplan
                                                      and re.linea =
                                                          lin.codlinea
                     inner join o02agpe0.tb_polizas p on po.referencia =
                                                         p.referencia
                                                     and po.tipo_referencia =
                                                         p.tiporef
                     inner join o02agpe0.tb_lineas l on l.lineaseguroid =
                                                        p.lineaseguroid
                                                    and l.codplan = fa.plan
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
                              lin.lineaseguroid,
                              fa.fecha_emision_recibo,
                              hc.entmediadora,
                              hc.subentmediadora,
                              gn.grupo_negocio) q on subent.codentidad =
                                                     q.entmediadora
                                                 and subent.codsubentidad =
                                                     q.subentmediadora
         inner join o02agpe0.tb_lineas l on q.lineaseguroid =
                                            l.lineaseguroid
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
                                                  and lfs.grupo_negocio =
                                                      q.grupo_negocio
         inner join o02agpe0.tp_grupo_familia_seg gf on gf.cod_grupo_familia_seg =
                                                        lfs.cod_grupo_familia_seg
         where subent.codentidad not between 6000 and 6999
         order by idcierre, csb, fecEmisionRecibo, linea, plan) aux;
         