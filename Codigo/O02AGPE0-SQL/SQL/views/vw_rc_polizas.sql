SET DEFINE OFF;  
SET SERVEROUTPUT ON; 

create or replace view vw_rc_polizas as
select rcp.id               as idPoliza,
         c.codentidad         as entidad,
         p.oficina            as oficina,
         p.codusuario         as usuario,
         l.codplan            as plan,
         l.codlinea           as linea,
         c.idcolectivo        as refColectivo,
         c.dc                 as dcColectivo,
         p.referencia         as refPoliza,
         p.dc                 as dcPoliza,
         a.nifcif             as nifcif,
         p.clase              as clase,
         p.codmodulo          as modulo,
         ep.idestado          as estadoPol,
         ep.desc_estado       as desEstadoPol,
         ep.abreviatura       as abrEstadoPol,
         rcp.fecha_envio      as fecEnvioRC,
         rcp.suma_asegurada   as sumaAsegurada,
         rcp.importe          as importe,
         rces.id              as estadoRC,
         rces.descripcion     as desEstadoRC,
         rce.id               as errorRC,
         rce.descripcion      as detalle,
         rcp.referencia_omega as refOmega,
         rcp.codespecie_rc    as codEspecieRC,
         rces.abreviatura     as abreviatura
    from o02agpe0.tb_rc_polizas     rcp,
         o02agpe0.tb_rc_errores     rce,
         o02agpe0.tb_rc_estados     rces,
         o02agpe0.tb_polizas        p,
         o02agpe0.tb_estados_poliza ep,
         o02agpe0.tb_lineas         l,
         o02agpe0.tb_asegurados     a,
         o02agpe0.tb_colectivos     c
   where rcp.idestado <> 0
     and rces.id <> 0
     and rcp.iderror = rce.id (+)
     and rcp.idestado = rces.id
     and rcp.idpoliza = p.idpoliza
     and p.idestado = ep.idestado
     and p.lineaseguroid = l.lineaseguroid
     and p.idasegurado = a.id
     and p.idcolectivo = c.id
     and p.idasegurado not in (select blq.id_asegurado from o02agpe0.tb_bloqueos_asegurados blq where blq.idestado_aseg = 'B');