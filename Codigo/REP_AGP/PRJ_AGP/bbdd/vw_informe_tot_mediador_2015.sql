create or replace view o02agpe0.vw_informe_tot_mediador_2015 as
select rownum as id,
       idcierre,
       entidad,
       subentidad,
       nomentidad,
       fase,
       plan,
       linea,
       nomlinea,
       referencia,
       refpoliza,
       nifAseg,
       prima,
       comision_agente
  from (Select fase.idcierre as idcierre,
               colectivo.entmediadora as entidad,
               colectivo.subentmediadora as subentidad,
               ent.nomentidad as nomentidad,
               fase.fase as fase,
               fase.plan as plan,
               recibo.linea as linea,
               lin.nomlinea as nomlinea,
               colunif.referencia as referencia,
               aplicacion.referencia as refpoliza,
               aseg.nifcif as nifAseg,
               sum(gn.prima_comercial_neta) as prima,
               sum(nvl(gn.ga_commed_esmed, 0)) +
               sum(nvl(deuda.ga_commed_esmed, 0)) as comision_agente
          From o02agpe0.tb_coms_unif_fase              fase,
               o02agpe0.tb_coms_unif_recibo            recibo,
               o02agpe0.tb_coms_unif_colectivo         colunif,
               o02agpe0.tb_coms_unif_aplicacion        aplicacion,
               o02agpe0.tb_colectivos                  colectivo,
               o02agpe0.tb_coms_unif_grupo_negocio     gn,
               o02agpe0.tb_lineas                      lin,
               o02agpe0.tb_entidades                   ent,
               o02agpe0.tb_coms_unif_gas_abo_deuda_apl deuda,
               o02agpe0.tb_asegurados                  aseg,
               o02agpe0.tb_polizas                     poliza
         where fase.id = recibo.idfase
           and recibo.id = aplicacion.idrecibo
           and colunif.id = recibo.idcolectivo
           and colunif.referencia = colectivo.idcolectivo
           and aplicacion.id = gn.idaplicacion
           and lin.codplan = fase.plan
           and lin.codlinea = recibo.linea
           and colectivo.codentidad = ent.codentidad
           and gn.id = deuda.idgruponegocio(+)
           and poliza.idcolectivo = colectivo.id
           and poliza.idasegurado = aseg.id
           and aplicacion.referencia = poliza.referencia
         group by fase.idcierre,
                  colectivo.entmediadora,
                  colectivo.subentmediadora,
                  ent.nomentidad,
                  fase.fase,
                  fase.plan,
                  recibo.linea,
                  lin.nomlinea,
                  colunif.referencia,
                  aplicacion.referencia,
                  aseg.nifcif)
union
select rownum as id,
       idcierre,
       entidad,
       subentidad,
       nvl (nomsubentidad, trim(trim(nombre) || ' ' || trim(apellido1) || ' ' || trim(apellido2))) as nomentidad,
       fase,
       plan,
       linea,
       nomlinea,
       referencia,
       refpoliza,
       nifAseg,
       prima,
       comision_agente
       from
            (select
                    coms17.idcierre as idcierre,
                    coms17.entmediadora as entidad,
                    coms17.subentmediadora as subentidad,
                    sub.nomsubentidad as nomsubentidad,
                    sub.nombre as nombre,
                    sub.apellido1 as apellido1,
                    sub.apellido2 as apellido2,
                    coms17.fase as fase,
                    coms17.plan as plan,
                    coms17.linea as linea,
                    coms17.nomlinea as nomlinea,
                    coms17.referencia as referencia,
                    coms17.refpoliza as refpoliza,
                    coms17.nifAseg as nifAseg,
                    sum(gn.frac_pri_com_neta) as prima,
                    sum(nvl(gn.ga_commed_esmed, 0)) as comision_agente
             from
                  (Select fase.idcierre as idcierre,
                         (select entmediadora from (select h.entmediadora, h.fechaefecto, h.idcolectivo
                             from o02agpe0.tb_historico_colectivos h
                             order by h.fechacambio desc) where idcolectivo = p.idcolectivo and fechaefecto <= p.fechaenvio and rownum = 1) entmediadora,
						 (select subentmediadora from (select h.subentmediadora, h.fechaefecto, h.idcolectivo
                             from o02agpe0.tb_historico_colectivos h
                             order by h.fechacambio desc) where idcolectivo = p.idcolectivo and fechaefecto <= p.fechaenvio and rownum = 1) subentmediadora,
                          fase.fase as fase,
                          fase.plan as plan,
                          recibo.linea as linea,
                          lin.nomlinea as nomlinea,
                          unifCol.referencia as referencia,
                          poliza.referencia as refpoliza,
                          aseg.nifcif as nifAseg,
                          poliza.id as idpoliza
                   From o02agpe0.tb_coms_unif_fase             fase,
                        o02agpe0.tb_coms_unif17_recibo         recibo,
                        o02agpe0.tb_lineas                     lin,
                        o02agpe0.tb_coms_unif17_asegurado      aseg,
                        o02agpe0.tb_coms_unif17_poliza         poliza,
                        o02agpe0.tb_coms_unif_colectivo        unifCol,
                        o02agpe0.tb_polizas                    p
                   where fase.id = recibo.idfase
                         and recibo.id = poliza.idrecibo
                         and lin.codplan = fase.plan
                         and lin.codlinea = recibo.linea
                         and poliza.idcolectivo = unifCol.Id
                         and poliza.idasegurado = aseg.id
                         and poliza.referencia = p.referencia and poliza.tipo_referencia = p.tiporef and p.lineaseguroid = lin.lineaseguroid
                   ) coms17
                   ,o02agpe0.tb_coms_unif17_grupo_negocio gn
                   ,o02agpe0.tb_subentidades_mediadoras sub
             where coms17.idpoliza = gn.idpoliza and coms17.entmediadora = sub.codentidad and coms17.subentmediadora = sub.codsubentidad
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
                      coms17.referencia,
                      coms17.refpoliza,
                      coms17.nifAseg)
