SET DEFINE OFF;
SET SERVEROUTPUT ON;

create or replace view o02agpe0.vw_inc_incidencias_agro as
select inc.idincidencia,
       inc.anhoincidencia as anho,
       inc.numincidencia as numero,
       ias.codasunto,
       ias.descripcion as asunto,
       imo.codmotivo,
       imo.descripcion as motivo,
       inc.codestado,
       decode(inc.codestado,
              '0',
              'Enviada Errónea',
              '1',
              'Enviada Correcta',
              '9',
              'Borrador') estadodes,
       inc.fechaestado as fecha,
       idoc.coddocafectado,
       idoc.descripcion as docafectado,
       inc.tiporef,
       inc.idenvio,
       q.codentidad,
       q.oficina,
       q.entmediadora,
       q.subentmediadora,
       q.delegacion,
       q.codusuario,
       inc.codplan,
       inc.codlinea,
       inc.codestadoagro,
       inc.nifaseg as nifcif,
       e.descripcion as estadoagrodes,
       inc.fechaestadoagro,
       inc.fecha_seguimiento,
       inc.tipo_inc,
       decode(inc.tipo_inc,
              'I',
              'Incidencia',
              'R',
              'Rescisión',
              'A',
              'Anulación') tipo_inc_des,
       decode(inc.tipoalta, 'c', inc.idenvio, 'a', '', 'p', '', 'i', '') idcupon,
       inc.referencia
  from o02agpe0.tb_inc_incidencias inc
  left outer join o02agpe0.tb_inc_asuntos ias on ias.codasunto = inc.codasunto
  			AND ias.catalogo = inc.catalogo
  left outer join o02agpe0.tb_motivos imo on imo.codmotivo = inc.codmotivo
 inner join o02agpe0.tb_inc_docs_afectados idoc on inc.coddocafectado =
                                                   idoc.coddocafectado
 inner join o02agpe0.tb_inc_estados e on e.codestado = inc.codestadoagro
  left outer join o02agpe0.tb_bloqueos_asegurados blq on blq.nifcif =
                                                         inc.nifaseg
  left outer join (select l.codlinea,
                          l.codplan,
                          p.referencia,
                          p.tiporef,
                          p.dc,
                          c.codentidad,
                          c.entmediadora,
                          c.subentmediadora,
                          p.oficina,
                          a.nifcif,
                          u.delegacion,
                          p.codusuario,
                          p.idpoliza
                     from o02agpe0.tb_polizas    p,
                          o02agpe0.tb_lineas     l,
                          o02agpe0.tb_asegurados a,
                          o02agpe0.tb_colectivos c,
                          o02agpe0.tb_usuarios   u
                    where p.lineaseguroid = l.lineaseguroid
                      and p.idasegurado = a.id
                      and c.id = p.idcolectivo
                      and l.lineaseguroid = c.lineaseguroid
                      and u.codusuario = p.codusuario) q on q.codlinea =
                                                            inc.codlinea
                                                        and q.codplan =
                                                            inc.codplan
                                                        and q.referencia =
                                                            inc.referencia
                                                        and q.tiporef =
                                                            inc.tiporef
                                                        and q.dc = inc.dc
 where nvl(blq.idestado_aseg, 'D') <> 'B';
