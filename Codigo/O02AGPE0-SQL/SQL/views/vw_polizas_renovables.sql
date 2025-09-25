SET DEFINE OFF;  
SET SERVEROUTPUT ON; 

create or replace view vw_polizas_renovables as
Select g.ID,
       r.ID as idPol,
       c.codentidad,
       r.nif_tomador as nifTomador,
       r.plan,
       r.linea,
       g.GRUPO_NEGOCIO as grupoNegocio,
       gr.DESCRIPCION as descGrupoNegocio,
       c.referencia as refCol,
       c.DC as dcCol,
       r.REFERENCIA,
       r.dc as dcPol,
       r.NIF_ASEGURADO as nifAsegurado,
       g.ESTADO_AGROPLUS as estAgroplus,
       er.DESCRIPCION as descAgroplus,
       r.ESTADO_AGROSEGURO as estAgroseguro,
       ea.DESCRIPCION as descAgroseguro,
       TO_DATE(r.FECHA_RENOVACION, 'dd/MM/YY') as fechaRenovacion,
       g.COMISION_MEDIADOR,
       g.COMISION_ENTIDAD,
       g.COMISION_ESMED,
       c.CODENTIDADMED,
       c.CODSUBENTMED,
       r.COSTE_TOTAL_TOMADOR,
       r.ESTADO_ENVIO_IBAN_AGRO as estadoIban,
       ib.DESCRIPCION as descEstadoIban,
       TO_DATE(r.FECHA_ENVIO_IBAN_AGRO, 'dd/MM/YY') as fechaEnvioIbanAgro,
       TO_DATE(r.FECHA_CARGA, 'dd/MM/YY') as fechaCarga,
       ga.comision_mediador as COMISION_APL,
       ga.comision_apl_entidad as ENTIDAD_APL,
       ga.comision_apl_es_med as ESMED_APL,
       cgnr.prima_comercial_neta,
       r.coste_total_tomador_ant,
       r.domiciliado,
       r.destino_domiciliacion as DEST_DOMIC,
       r.iban
  from O02AGPE0.TB_POLIZAS_RENOVABLES r
  left outer join O02AGPE0.TB_COLECTIVOS_RENOVACION c on r.IDCOLECTIVO = c.ID 
  join O02AGPE0.TB_GASTOS_RENOVACION g on g.IDPOLIZARENOVABLE = r.ID 
  join O02AGPE0.TB_SC_C_GRUPOS_NEGOCIO gr on g.GRUPO_NEGOCIO = gr.GRUPO_NEGOCIO 
  join O02AGPE0.TB_ESTADO_RENOVACION_AGROSEG ea on r.ESTADO_AGROSEGURO = ea.CODIGO
  join O02AGPE0.TB_ESTADO_RENOVACION_AGROPLUS er on er.CODIGO = g.ESTADO_AGROPLUS
  join O02AGPE0.TB_ESTADO_RENOVACIO_ENVIO_IBAN ib on ib.CODIGO = r.ESTADO_ENVIO_IBAN_AGRO
  left outer join O02AGPE0.TB_GASTOS_RENOVACION_APLICADOS ga on ga.idpolizarenovable = g.idpolizarenovable and ga.grupo_negocio=g.grupo_negocio
  left outer join O02AGPE0.TB_COSTE_GRUPO_NEGOCIO_REN cgnr on cgnr.idpolizarenovable = g.idpolizarenovable and cgnr.grupo_negocio=g.grupo_negocio
  left outer join o02agpe0.tb_bloqueos_asegurados blq on blq.nifcif = r.nif_asegurado                                                           
  where nvl(blq.idestado_aseg, 'D') <> 'B'; 