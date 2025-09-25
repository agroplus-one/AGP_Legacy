create or replace view o02agpe0.vw_informe_entidades_2015 as
select rownum as id,
       idcierre,
       entidad,
       subentidad,
       nvl(nomsubentidad,
           trim(trim(nombre) || ' ' || trim(apellido1) || ' ' ||
                trim(apellido2))) as nomentidad,
       fase,
       plan,
       linea,
       nomlinea,
       referencia,
       prima,
       comision_ent,
       comision_es_med,
       pdte_ent,
       pdt_mediador,
       tt_ent,
       tt_es_med,
       liquidacion
  from (select coms17.idcierre,
               coms17.entmediadora as entidad,
               coms17.subentmediadora as subentidad,
               sub.nomsubentidad as nomsubentidad,
               sub.nombre as nombre,
               sub.apellido1 as apellido1,
               sub.apellido2 as apellido2,
               coms17.fase,
               coms17.plan,
               coms17.linea,
               coms17.nomlinea,
               coms17.referencia,
               sum(nvl(gn.frac_pri_com_neta, 0)) as prima,
               sum(nvl(gn.gd_imp_commed_entidad, 0)) as comision_ent,
               sum(nvl(gn.gd_imp_commed_esmed, 0)) as comision_es_med,
               sum(gn.gp_commed_esmed) as pdt_mediador,
               sum(gn.gp_commed_entidad) as pdte_ent,
               sum(gn.ga_commed_entidad) as tt_ent,
               sum(gn.ga_commed_esmed) as tt_es_med,
               sum(nvl(gn.ga_commed_entidad, 0)) +
               sum(nvl(gn.ga_commed_esmed, 0)) as liquidacion
          from (Select fase.idcierre as idcierre,
                       hc.entmediadora,
                       hc.subentmediadora,
                       fase.fase as fase,
                       fase.plan as plan,
                       recibo.linea as linea,
                       lin.nomlinea as nomlinea,
                       unifCol.referencia as referencia,
                       pol.id as idpoliza
                  From o02agpe0.tb_coms_unif_fase fase
                 inner join o02agpe0.tb_coms_unif17_recibo recibo on fase.id =
                                                                     recibo.idfase
                 inner join o02agpe0.tb_lineas lin on lin.codplan = fase.plan
                                                  and lin.codlinea =
                                                      recibo.linea
                 inner join o02agpe0.tb_coms_unif17_poliza pol on recibo.id =
                                                                  pol.idrecibo
                 inner join o02agpe0.tb_coms_unif_colectivo unifCol on pol.idcolectivo =
                                                                       unifCol.id
                 inner join o02agpe0.tb_polizas p on pol.referencia =
                                                     p.referencia
                                                 and pol.tipo_referencia =
                                                     p.tiporef
                                                 and p.lineaseguroid =
                                                     lin.lineaseguroid
                 inner join o02agpe0.tb_historico_colectivos hc on hc.rowid =
                                                                   (select hi.rowid
                                                                      from (select *
                                                                              from o02agpe0.tb_historico_colectivos h
                                                                             order by h.fechacambio desc) hi
                                                                     where hi.idcolectivo =
                                                                           p.idcolectivo
                                                                       and hi.fechaefecto <=
                                                                           p.fechaenvio
                                                                       and rownum = 1)) coms17,
               o02agpe0.tb_coms_unif17_grupo_negocio gn,
               o02agpe0.tb_subentidades_mediadoras sub
         where coms17.idpoliza = gn.idpoliza
           and coms17.entmediadora = sub.codentidad
           and coms17.subentmediadora = sub.codsubentidad
         group by coms17.idcierre,
                  coms17.entmediadora,
                  coms17.subentmediadora,
                  sub.nomsubentidad,
                  sub.nombre,
                  sub.apellido1,
                  sub.apellido2,
                  coms17.fase,
                  coms17.plan,
                  coms17.linea,
                  coms17.nomlinea,
                  coms17.referencia)