create or replace view o02agpe0.vw_informe_entope_3 as
select fase.idcierre,
       col.entmediadora,
	   col.subentmediadora,
       sum(nvl(gn.ga_commed_esmed, 0) - nvl(gn.gp_commed_esmed, 0)) as coop
  from o02agpe0.tb_coms_unif_fichero       fichero,
       o02agpe0.tb_coms_unif_fase          fase,
       o02agpe0.tb_coms_unif_recibo        recibo,
       o02agpe0.tb_coms_unif_aplicacion    aplicacion,
       o02agpe0.tb_polizas                 pol,
       o02agpe0.tb_colectivos              col,
       o02agpe0.tb_coms_unif_grupo_negocio gn,
       o02agpe0.tb_subentidades_mediadoras sub
 where fichero.tipo_fichero = 'C'
   and fichero.id = fase.idfichero
   and fase.id = recibo.idfase
   and recibo.id = aplicacion.idrecibo
   and aplicacion.id = gn.idaplicacion
   and aplicacion.referencia = pol.referencia
   and aplicacion.tipo_referencia = pol.tiporef
   and pol.idcolectivo = col.id
   and col.entmediadora = sub.codentidad
   and col.subentmediadora = sub.codsubentidad
   and sub.pagodirecto <> '1'
 group by fase.idcierre,
          col.entmediadora,
		  col.subentmediadora
union
select coms17.idcierre,
       coms17.entmediadora,
       subentmediadora,
       sum(nvl(gn.ga_commed_esmed, 0)) as coop
       from
            (select
                    fase.idcierre,
                   (select entmediadora from (select h.entmediadora, h.fechaefecto, h.idcolectivo
                             from o02agpe0.tb_historico_colectivos h
                             order by h.fechacambio desc) where idcolectivo = p.idcolectivo and fechaefecto <= p.fechaenvio and rownum = 1) entmediadora,
                   (select subentmediadora from (select h.subentmediadora, h.fechaefecto, h.idcolectivo
                             from o02agpe0.tb_historico_colectivos h
                             order by h.fechacambio desc) where idcolectivo = p.idcolectivo and fechaefecto <= p.fechaenvio and rownum = 1) subentmediadora,
                   pol.id as idpoliza
             from o02agpe0.tb_coms_unif_fichero   fichero,
                  o02agpe0.tb_coms_unif_fase      fase,
                  o02agpe0.tb_coms_unif17_recibo  recibo,
                  o02agpe0.tb_coms_unif17_poliza  pol,
	                o02agpe0.tb_lineas              lin,
                  o02agpe0.tb_coms_unif_colectivo unifCol,
                  o02agpe0.tb_polizas             p
             where fichero.tipo_fichero = 'U'
                   and fichero.id = fase.idfichero
                   and fase.id = recibo.idfase
                   and lin.codplan = fase.plan
                   and lin.codlinea = recibo.linea
                   and recibo.id = pol.idrecibo
                   and pol.idcolectivo = unifCol.Id
                   and pol.referencia = p.referencia and pol.tipo_referencia = p.tiporef and p.lineaseguroid = lin.lineaseguroid
            ) coms17
              ,o02agpe0.tb_coms_unif17_grupo_negocio gn
              ,o02agpe0.tb_subentidades_mediadoras sub
       where coms17.idpoliza = gn.idpoliza and coms17.entmediadora = sub.codentidad and coms17.subentmediadora = sub.codsubentidad
             and sub.pagodirecto <> '1'
       group by coms17.idcierre,
                coms17.entmediadora,
                subentmediadora

