SET DEFINE OFF;   
SET SERVEROUTPUT ON; 

create or replace view o02agpe0.vw_siniestros_utilidades as
select s.id,
       p.idpoliza,
       t.codentidad,
       p.oficina,
       l.codplan,
       l.codlinea,
       p.referencia,
       p.dc,
       a.nifcif,
       NVL (UPPER (a.razonsocial), UPPER (a.nombre || ' ' || a.apellido1 || ' ' || a.apellido2)) nombre,
       com.fecha_envio fenvpol,
       s.numsiniestro,
       s.fechaocurrencia focurr,
       s.fecfirmasiniestro ffirma,
       es.idestado,
       es.descestado,
       s.NUMEROSINIESTRO,
       s.serie,
       NVL(
         (select comusin.fecha_envio
                 from o02agpe0.tb_comunicaciones comusin
                where s.idenvio = comusin.idenvio),
         (select max(fecha)
                 from o02agpe0.tb_siniestro_sw_validacion a
                 where a.idsiniestro=s.id group by idsiniestro )
          ) fenv,
       r.codriesgo,
       r.desriesgo,
      (select pe.CODUSUARIO
         from o02agpe0.Vw_Polizas_Hist_Estados_Asc pe
          where pe.IDPOLIZA = p.Idpoliza AND ROWNUM = 1) codusuario,
       u.delegacion,
       c.entmediadora,
       c.subentmediadora,
       s.fecha_baja fbaja
  FROM o02agpe0.tb_siniestros        s,
       o02agpe0.tb_polizas           p,
       o02agpe0.tb_colectivos        c,
       o02agpe0.tb_tomadores         t,
       o02agpe0.tb_lineas            l,
       o02agpe0.tb_asegurados        a,
       o02agpe0.tb_comunicaciones    com,
       o02agpe0.tb_estados_siniestro es,
       o02agpe0.tb_sc_c_riesgos      r,
       o02agpe0.tb_usuarios          u
 WHERE s.idpoliza = p.idpoliza(+)
   and p.idcolectivo = c.id(+)
   and c.codentidad = t.codentidad(+)
   and c.ciftomador = t.ciftomador(+)
   and p.lineaseguroid = l.lineaseguroid(+)
   and p.idasegurado = a.id(+)
   and p.idenvio = com.idenvio(+)
   and s.estado = es.idestado(+)
   and s.codriesgo = r.codriesgo(+)
   and p.codusuario = u.codusuario(+)
   and p.idasegurado not in (select blq.id_asegurado from o02agpe0.tb_bloqueos_asegurados blq where blq.idestado_aseg = 'B');
