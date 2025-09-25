create or replace view vw_red_capital_utilidades as
select rc.id,
       p.idpoliza,
       t.codentidad,
       p.oficina,
       l.codplan,
       l.codlinea,
       p.referencia,
       p.dc,
       a.nifcif,
       NVL(UPPER(a.razonsocial),
           UPPER(a.nombre || ' ' || a.apellido1 || ' ' || a.apellido2)) nombre,
       com.fecha_envio fenvpol,
       rc.numanexo,
       rc.fechadanios fdanios,
       es.idestado,
       es.descestado,
       (select comured.fecha_envio
          from o02agpe0.tb_comunicaciones comured
         where rc.idenvio = comured.idenvio) fenv,
       r.codriesgo,
       r.desriesgo,
       (select pe.CODUSUARIO
          from o02agpe0.Vw_Polizas_Hist_Estados_Asc pe
         where pe.IDPOLIZA = p.Idpoliza
           AND ROWNUM = 1) codusuario,
       u.delegacion,
       c.entmediadora,
       c.subentmediadora
  FROM o02agpe0.tb_anexo_red         rc,
       o02agpe0.tb_polizas           p,
       o02agpe0.tb_colectivos        c,
       o02agpe0.tb_tomadores         t,
       o02agpe0.tb_lineas            l,
       o02agpe0.tb_asegurados        a,
       o02agpe0.tb_comunicaciones    com,
       o02agpe0.tb_estados_siniestro es,
       o02agpe0.tb_sc_c_riesgos      r,
       o02agpe0.tb_usuarios          u,
       o02agpe0.tb_sc_c_lineas       cl
 WHERE rc.idpoliza = p.idpoliza(+)
   and p.idcolectivo = c.id(+)
   and c.codentidad = t.codentidad(+)
   and c.ciftomador = t.ciftomador(+)
   and p.lineaseguroid = l.lineaseguroid(+)
   and p.idasegurado = a.id(+)
   and p.idenvio = com.idenvio(+)
   and rc.idestado = es.idestado(+)
   and rc.codmotivoriesgo = r.codriesgo
   and l.codlinea = cl.codlinea
   and cl.codgruposeguro = r.codgruposeguro
   and p.codusuario = u.codusuario(+)
   and p.idasegurado not in
       (select blq.id_asegurado
          from o02agpe0.tb_bloqueos_asegurados blq
         where blq.idestado_aseg = 'B');
