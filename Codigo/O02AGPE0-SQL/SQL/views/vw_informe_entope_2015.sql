create or replace view o02agpe0.vw_informe_entope_2015 as
select rownum as id,
       q.idcierre as idcierre,
       ent.codentidad as entidad,
       ent.nomentidad as nomentidad,
       nvl(q.fases, 0) as fases,
       nvl(q.coop, 0) as coop,
       nvl(q.impagados, 0) as impagados,
       nvl(q.deuda, 0) as deuda,
       (nvl(q.fases, 0) + nvl(q.coop, 0) + nvl(q.impagados, 0) +
       nvl(q.deuda, 0)) as total,
       (select iban
          from o02agpe0.tb_subentidades_mediadoras aux
         where aux.codentidadnomediadora = ent.codentidad
           and aux.codsubentidad = 0
           and aux.codentidad = ent.codentidad) as iban
  from o02agpe0.tb_entidades ent
 inner join (select vista2.idcierre,
                    subent.codentidadnomediadora as codentidadnomediadora,
                    sum(nvl(vista2.FASES, 0)) as fases,
                    sum(nvl(vista3.COOP, 0)) as coop,
                    sum(nvl(vista4.IMPAGADOS, 0)) as impagados,
                    sum(nvl(vista5.DEUDA, 0)) as deuda
               from o02agpe0.tb_subentidades_mediadoras subent
               left outer join o02agpe0.vw_informe_entOpe_2 vista2 ON subent.CODENTIDAD =
                                                                      vista2.ENTMEDIADORA
                                                                  and subent.codsubentidad =
                                                                      vista2.subentmediadora
               left outer join o02agpe0.vw_informe_entope_3 vista3 ON subent.CODENTIDAD =
                                                                      vista3.ENTMEDIADORA
                                                                  and subent.codsubentidad =
                                                                      vista3.subentmediadora
                                                                  and vista2.IDCIERRE =
                                                                      vista3.IDCIERRE
               left outer join o02agpe0.vw_informe_entope_4 vista4 on subent.CODENTIDAD =
                                                                      vista4.ENTMEDIADORA
                                                                  and subent.codsubentidad =
                                                                      vista4.subentmediadora
                                                                  and vista2.IDCIERRE =
                                                                      vista4.IDCIERRE
               left outer join o02agpe0.vw_informe_entope_5 vista5 on subent.CODENTIDAD =
                                                                      vista5.ENTMEDIADORA
                                                                  and subent.codsubentidad =
                                                                      vista5.subentmediadora
                                                                  and vista2.IDCIERRE =
                                                                      vista5.IDCIERRE
              where (subent.codsubentidad = 0 and
                    subent.codentidad between 3000 and 3999)
                 or subent.codentidad between 8000 and 8999
              group by vista2.idcierre, subent.codentidadnomediadora) q on ent.codentidad =
                                                                           q.codentidadnomediadora
 order by entidad asc; 