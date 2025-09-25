create or replace package o02agpe0.PQ_IMPORTACION_SBP is

  -- DAA 29/04/2013
  -- Procedimiento para insertar en TB_SBP_TASAS a partir de la tabla TBX_SBP_TASAS
   PROCEDURE PR_INSERTAR_TASAS_SBP (P_RESULT OUT NUMBER);

end PQ_IMPORTACION_SBP;
/
create or replace package body o02agpe0.PQ_IMPORTACION_SBP is

--------------------------------------------------------------
 -- DAA 29/04/2013

   PROCEDURE PR_INSERTAR_TASAS_SBP (P_RESULT OUT NUMBER) IS
        LINSEGID             NUMBER (15);
        TYPE  TpCursor       IS REF CURSOR;
        cur_grupoplan        TpCursor;
        consultaGrupoPlan    varchar2(32000):= 'SELECT distinct codplan, codgrupotasa FROM TBX_SBP_TASAS';
        consultaLinsegid     varchar2(32000);
        v_codplan            TBX_SBP_TASAS.CODPLAN%TYPE;
        v_codgrupotasa       TBX_SBP_TASAS.CODGRUPOTASA%TYPE;
        v_result             number(10) := 0;
        lc VARCHAR2(50) := 'PQ_IMPORTACION_CSV.PR_INSERTAR_TASAS_SBP'; -- Variable que almacena el nombre del paquete y de la función

   BEGIN
        -- Obtenemos el lineaseguroid a partir de los grupos de tasas y el plan
        OPEN cur_grupoplan FOR consultaGrupoPlan;
             LOOP
                 FETCH cur_grupoplan INTO v_codplan, v_codgrupotasa;
                 EXIT WHEN cur_grupoplan%NOTFOUND;
                      consultaLinsegid := 'select distinct gt.lineaseguroid from tb_sc_c_grupo_tasas gt, tb_lineas l
                                               where gt.lineaseguroid = l.lineaseguroid and l.codplan = '|| v_codplan || ' and gt.codgrupotasa = '|| v_codgrupotasa;
                      -- Sacamos el lineaseguroid en la variable "linsegid"
                      execute immediate consultaLinsegid INTO LINSEGID;
                      -- Hacemos el insert en tb_sbp_tasas
                      insert into tb_sbp_tasas
                      (select SQ_SBP_TASAS.nextval,
                              LINSEGID,
                              codprovincia,
                              tasa_incendio,
                              tasa_pedrisco,
                              codcomarca,
                              codcultivo
                       from tbx_sbp_tasas
                       where codgrupotasa = v_codgrupotasa and codplan = v_codplan);
             END LOOP;
        CLOSE cur_grupoplan;

        -- Comprobamos el numero de registros que se han copiado
        select count(*) into v_result from TBX_SBP_TASAS;
        p_result := v_result;

   EXCEPTION
      when others then
          pq_utl.log(lc,'Error al copiar las TasasSbp. Mensaje: '||SQLERRM||', codigo: '|| SQLCODE, 1);
          -- Deshace las transacciones
          rollback;
          -- Lanza una excepción para indicar que ha habido algún problema
          RAISE;

   END PR_INSERTAR_TASAS_SBP;
--------------------------------------------------------------

end PQ_IMPORTACION_SBP;
/
