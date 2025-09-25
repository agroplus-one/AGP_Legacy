create or replace view o02agpe0.vw_informe_corredores_2015 as
select rownum as id, aux.*
  from (select idcierre,
               csb,
               nomsubentidad,
               email,
               email2,
               pcn,
               importe,
               importe * (pct_retencion / 100) as retencion,
               importe - decode(pct_retencion,
                                NULL,
                                0,
                                (importe * (pct_retencion / 100))) as liquidar,
               abonadq,
               mes,
               iban
          from (select q.idcierre,
                       LPAD(subent.codentidad, 4, '0') ||
                       LPAD(subent.codsubentidad, 2, '0') as csb,
                       decode(subent.tipoidentificacion,
                              'NIF',
                              subent.nombre || ' ' || subent.apellido1 || ' ' ||
                              subent.apellido2,
                              subent.nomsubentidad) as nomsubentidad,
                       subent.email as email,
                       subent.email_2 as email2,
                       nvl(q.pcn, 0) as pcn,
                       decode(subent.ind_gastos_adq,
                              1,
                              nvl(q.comsmed, 0) + nvl(q.abonadq, 0),
                              0,
                              nvl(q.comsmed, 0),
                              0) as importe,
                       decode(subent.tipoidentificacion,
                              'NIF',
                              (select pct_retencion from o02agpe0.tb_parametros),
                              0) as pct_retencion,
                       nvl(q.abonadq, 0) as abonadq,
                       q.mes,
                       decode(subent.pagodirecto,
                              '1',
                              subent.iban,
                              '0',
                              'Pago a C.R.',
                              NULL) as iban
                  from o02agpe0.tb_subentidades_mediadoras subent
                 inner join (select ci.id as idcierre,
                                   hc.entmediadora,
                                   hc.subentmediadora,
                                   sum(gn.frac_pri_com_neta) as pcn,
                                   sum(gn.ga_commed_esmed) as comsmed,
                                   sum(gn.ga_adq) as abonadq,
                                   to_char(ci.periodo,
                                           'Month',
                                           'nls_date_language=spanish') as mes
                              from o02agpe0.tb_polizas p
                             inner join o02agpe0.tb_lineas l on l.lineaseguroid =
                                                                p.lineaseguroid
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
                             inner join o02agpe0.tb_coms_unif17_poliza po on po.referencia =
                                                                             p.referencia
                                                                         and po.tipo_referencia =
                                                                             p.tiporef
                             inner join o02agpe0.tb_coms_unif17_grupo_negocio gn on gn.idpoliza =
                                                                                    po.id
                             inner join o02agpe0.tb_coms_unif17_recibo re on re.id =
                                                                             po.idrecibo
                                                                         and re.linea =
                                                                             l.codlinea
                             inner join o02agpe0.tb_coms_unif_fase fa on fa.id =
                                                                         re.idfase
                                                                     and fa.plan =
                                                                         l.codplan
                             inner join o02agpe0.tb_coms_cierre ci on fa.idcierre =
                                                                      ci.id
                             group by ci.id,
                                      ci.periodo,
                                      hc.entmediadora,
                                      hc.subentmediadora) q on q.entmediadora =
                                                               subent.codentidad
                                                           and q.subentmediadora =
                                                               subent.codsubentidad
                 where subent.codentidad between 4000 and 4999
                    or subent.codentidad between 6000 and 6999)
         where (importe +
               decode(pct_retencion,
                       NULL,
                       0,
                       (importe * (pct_retencion / 100)))) > 0
         order by csb, idcierre) aux;