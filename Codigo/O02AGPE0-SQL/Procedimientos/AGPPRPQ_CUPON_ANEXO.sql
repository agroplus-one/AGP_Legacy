SET DEFINE OFF;
SET SERVEROUTPUT ON;
CREATE OR REPLACE PACKAGE O02AGPE0.PQ_CUPON_ANEXO IS
/******************************************************************************
   NAME:       	pq_cupon_anexo
   PURPOSE:    	Busca cupones no confirmados y actualiza su estado a "Caducado".
   		Se ejecutará todos los días a partir de las 0:00.

   REVISIONS:
   Ver        Date        Author           Description
   ---------  ----------  ---------------  ------------------------------------
   1.0        17/02/2014  Antonio Serrano  1. Created this package.
   2.0        04/03/2014  Moisés Pérez     2. Finalizar desarrollo

******************************************************************************/

PROCEDURE revisarCuponesCaducados;

END PQ_CUPON_ANEXO;
/
CREATE OR REPLACE PACKAGE BODY O02AGPE0.PQ_CUPON_ANEXO AS

  /*
  Busca todos los cupones en estado 'Abierto' cuya fecha de creación sea menor que la del día actual a las 00:00h
  para cambiar su estado a 'Caducado' e insetar el registro correspondiente en el histórico de estados del cupón
  */
  PROCEDURE revisarCuponesCaducados IS

  lc   VARCHAR2(50) := 'PQ_CUPON_ANEXO.revisarCuponesCaducados';
  TYPE TpCursor IS REF CURSOR;
  l_tp_cursor TpCursor;
  r_cupon_id o02agpe0.tb_anexo_mod_cupon.id%TYPE;

  BEGIN
     PQ_Utl.LOG(lc,'*** Inicio del proceso de actualización de cupones caducados ***', 1);
     dbms_output.put_line ('Inicio del proceso de actualización de cupones caducados');

     --Buscamos cupones en estado distinto de contratado y cuya fecha sea de ayer (contando con que el batch se ejectua a partir de las 12 de la noche)
     OPEN l_tp_cursor FOR 'SELECT ID FROM o02agpe0.tb_anexo_mod_cupon c where c.estado=1 and fecha < (select TO_DATE (TO_CHAR (sysdate, ''YYYYMMDD'') || ''000000'', ''YYYYMMDDHH24MISS'') from dual)';
     LOOP
        FETCH l_tp_cursor INTO r_cupon_id;
        EXIT WHEN l_tp_cursor%NOTFOUND;

        --Por cada cupón, actualizamos el estado a "Caducado"
        PQ_Utl.LOG(lc,'Actualiza el cupon con id = ' || r_cupon_id, 1);
        UPDATE o02agpe0.TB_ANEXO_MOD_CUPON c SET ESTADO = 2 WHERE c.id = r_cupon_id;

        -- Insertar en el histórico los registros actualizados
        PQ_Utl.LOG(lc,'Inserta su registro en el historico ', 1);
        INSERT INTO o02agpe0.tb_anexo_mod_cupon_hist
        values
          (o02agpe0.sq_anexo_mod_cupon_hist.nextval,
           r_cupon_id,
           sysdate,
           null,
           2,
           null,
           null);

        -- Si la actualización del cupón ha sido correcta se hace commit
        COMMIT;

     END LOOP;

     PQ_Utl.LOG(lc,'*** [CORRECTO] Fin del proceso de actualización de cupones caducados ***', 1);
     dbms_output.put_line ('[CORRECTO] - Fin del proceso de actualización de cupones caducados');
     EXCEPTION
        WHEN OTHERS THEN
           PQ_Utl.LOG(lc,'Error durante la revisión de cupones caducados [' || sqlcode || ' - ' || SQLERRM || '] ***', 1);
           dbms_output.put_line ('Error durante la revisión de cupones caducados [' || sqlcode || ' - ' || SQLERRM || '] ***');
           ROLLBACK;
           PQ_Utl.LOG(lc,'*** [ERROR] Fin del proceso de actualización de cupones caducados ***', 1);
           dbms_output.put_line ('[ERROR] - Fin del proceso de actualización de cupones caducados');
           raise;
  END;

END;
/

SHOW ERRORS;