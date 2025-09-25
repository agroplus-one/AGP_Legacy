SET DEFINE OFF;   
SET SERVEROUTPUT ON; 
 
create or replace view o02agpe0.vw_datos_de_polizas_sc_hist  as
select
        hist.idpoliza              as IDPOLIZA,
        hist.estado                as IDESTADO,
        est.desc_estado            as DESC_ESTADO,
        TO_CHAR(hist.fecha, 'DD/MM/YYYY HH24:MI:SS') as FECHA
   From o02agpe0.tb_polizas_historico_estados hist
    INNER JOIN o02agpe0.tb_estados_poliza est on est.idestado = hist.estado
  where hist.estado in (8, 14, 16);
