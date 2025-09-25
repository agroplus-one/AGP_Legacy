SET DEFINE OFF;
SET SERVEROUTPUT ON;
create or replace package o02agpe0.PQ_COPIAR_SUBVENCIONES_A_ANEXO is

/*******************************************************************************
   NAME:
  PURPOSE:    Clonamos las subvenciones al anexo de modificacion en tipomodificacion null

   REVISIONS:
   Ver        Date        Author           Description
   ---------  ----------  ---------------  --------------------------------------
   1.1        07/01/2011  T-SYSTEMS

   NOTES:

*******************************************************************************/
 -- MAIN (punto de entrada al pl/sql)
   PROCEDURE copiarSubvEnAnexo(P_IDASEG       IN NUMBER);
-- Procedimientos COPY: si hay idcopy

-- Procedimiento para cargar las subvenciones de un anexo de modificación
-- Recibe como parámetro el identificador del anexo.
   PROCEDURE copiarSubvencionesEnAnexo(P_IDANEXO       IN NUMBER);

-- main copy
   PROCEDURE copiarSubvFromCopy(P_IDCOPY             IN NUMBER,
                                P_IDANEXO            IN NUMBER);
   -- Paso 1
   PROCEDURE setSubvFromCopy( p_idsubv             IN NUMBER,
                              P_IDANEXO            IN NUMBER);



    -- Procedimientos POLIZA: si no hay idcopy

    -- main poliza
    PROCEDURE copiarSubvFromPoliza(P_IDPOLIZA          IN NUMBER,
                                   P_IDANEXO           IN NUMBER);



    -- UTILS

    FUNCTION getIdCopyPoliza(P_IDPOLIZA IN NUMBER) RETURN NUMBER;

    FUNCTION getIdAnexoPoliza(P_IDPOLIZA IN NUMBER) RETURN NUMBER;

    FUNCTION getIdCopyAnexo(P_IDANEXO IN NUMBER) RETURN NUMBER;

    FUNCTION getIdPolizaFromAnexo (P_IDANEXO IN NUMBER) RETURN NUMBER;

end PQ_COPIAR_SUBVENCIONES_A_ANEXO;
/
create or replace package body o02agpe0.PQ_COPIAR_SUBVENCIONES_A_ANEXO is

PROCEDURE copiarSubvEnAnexo(P_IDASEG     IN NUMBER)IS
     TYPE TpCursor       IS REF CURSOR;
     l_tp_cursor         TpCursor;

     v_idPoliza                  NUMBER(10);
     v_idCopy                    NUMBER(10);
     v_idAnexo                   NUMBER(10):=0;
     v_count                     NUMBER(5) :=0;
     l_sql                       VARCHAR2(300);
     planActual                        varchar2(4);
      plan                      varchar2(4);
 BEGIN
     BEGIN -- Exception

         select to_char(sysdate, 'YYYY') into planActual from dual;
         select to_char(sysdate, 'YYYY')-1 into plan from dual;
          l_sql := 'select po.idpoliza ';
          l_sql := l_sql || 'from tb_polizas po, tb_lineas li ';
          l_sql    := l_sql || 'where po.idasegurado = ' || P_IDASEG;
          l_sql := l_sql || ' and po.lineaseguroid = li.lineaseguroid ';
          l_sql := l_sql || ' and po.IDESTADO = 8 ';
          l_sql := l_sql || 'AND Li.CODPLAN BETWEEN ' || plan || ' AND ' || planActual ;

       --Cursor con todas las polizas vigentes del Asegurado
       --Por cada poliza buscamos sus idcopy,anexos y los modificamos
       --si no existe el anexo para la poliza, lo creamos.
           OPEN l_tp_cursor FOR l_sql;
           LOOP FETCH l_tp_cursor INTO v_idPoliza;
              EXIT WHEN l_tp_cursor%NOTFOUND;

                         v_idCopy := getIdCopyPoliza(v_idPoliza);
                         v_idAnexo := getIdAnexoPoliza(v_idPoliza);
                         --Comprobamos que no hemos copiado las subv antes
                         select count(*) into v_count
                         from tb_anexo_mod_subv_decl
                         where idanexo = v_idAnexo;

                         IF v_count = 0 THEN
                           IF v_idAnexo != 0 THEN

                                 IF v_idCopy = 0 THEN  -- no hay copy

                                     copiarSubvFromPoliza(v_idPoliza,v_idAnexo);

                                 ELSE                -- si hay copy

                                     copiarSubvFromCopy(v_idCopy,v_idAnexo);

                                 END IF;
                           ELSE --> CREO ANEXO CON DATOS DE ASEGURADO, ESTADO 1 Y LAS SUBVS
                               --EXISTE COPY -> CREO ANEXO CON COPY Y POLIZA. COPIO SUBV DE COPY
                               -- NO EXISTE COPY -> CREO ANEXO CON POLIZA. COPIO SUBV DE POLIZA
                                 INSERT INTO TB_ANEXO_MOD  AM
                                 (AM.ID,
                                 AM.IDPOLIZA,
                                 AM.IDCOPY,
                                 AM.ESTADO,
                                 AM.IDENVIO,
                                 AM.FECHAFIRMADOC,
                                 AM.ASUNTO,
                                 AM.NOMASEG,
                                 AM.APEL1ASEG,
                                 AM.APEL2ASEG,
                                 AM.RAZSOCASEG,
                                 AM.CALLEASEG,
                                 AM.NUMASEG,
                                 AM.CODPROVINCIA,
                                 AM.CODLOCALIDAD,
                                 AM.CODPOSASEG,
                                 AM.PISOASEG,
                                 AM.BLOQUEASEG,
                                 AM.ESCASEG,
                                 AM.TELFFIJOASEG,
                                 AM.TELFMOVILASEG,
                                 AM.EMAIL,
                                 AM.NUMSEGSOCIAL,
                                 AM.REGIMENSEGSOCIAL,
                                 AM.CODMODULO)
                                   SELECT
                                           SQ_TB_ANEXO_MOD.Nextval as id,
                                           v_idPoliza,
                                           v_idCopy,
                                           1,
                                           NULL,
                                           NULL,
                                           NULL,
                                           ASEG.NOMBRE,
                                           ASEG.APELLIDO1,
                                           ASEG.APELLIDO2,
                                           ASEG.RAZONSOCIAL,
                                           ASEG.DIRECCION,
                                           ASEG.NUMVIA,
                                           ASEG.CODPROVINCIA,
                                           ASEG.CODLOCALIDAD,
                                           ASEG.CODPOSTAL,
                                           ASEG.PISO,
                                           ASEG.BLOQUE,
                                           ASEG.ESCALERA,
                                           ASEG.TELEFONO,
                                           ASEG.MOVIL,
                                           ASEG.EMAIL,
                                           ASEG.NUMSEGSOCIAL,
                                           ASEG.REGIMENSEGSOCIAL,
                                           NULL
                                   FROM TB_ASEGURADOS ASEG
                                   WHERE ID=P_IDASEG;

                                 v_idAnexo := getIdAnexoPoliza(v_idPoliza);
                                 IF v_idCopy = 0 THEN  -- no hay copy

                                      copiarSubvFromPoliza(v_idPoliza,v_idAnexo);

                                 ELSE                -- si hay copy

                                      copiarSubvFromCopy(v_idCopy,v_idAnexo);

                                 END IF;

                           END IF;
                        END IF;

           END LOOP;

      EXCEPTION

            WHEN NO_DATA_FOUND THEN
               ROLLBACK;

      END;-- Exception

    COMMIT;
END copiarSubvEnAnexo;


PROCEDURE copiarSubvencionesEnAnexo(P_IDANEXO     IN NUMBER)IS
     v_idPoliza                  NUMBER(10);
     v_idCopy                    NUMBER(10);
     v_count                     NUMBER(5) :=0;

 BEGIN
     BEGIN -- Exception

     --Comprobamos que no hemos copiado las subv antes
     select count(*) into v_count
     from tb_anexo_mod_subv_decl
     where idanexo = p_idAnexo;

     v_idPoliza := getIdPolizaFromAnexo(P_IDANEXO);
     v_idCopy := getIdCopyAnexo(P_IDANEXO);

     IF v_count = 0 THEN
        IF v_idCopy = 0 OR v_idCopy is null THEN  -- no hay copy
             copiarSubvFromPoliza(v_idPoliza,P_IDANEXO);
         ELSE                -- si hay copy
             copiarSubvFromCopy(v_idCopy,P_IDANEXO);
         END IF;
     END IF;

     EXCEPTION
         WHEN NO_DATA_FOUND THEN
               ROLLBACK;
      END;-- Exception

    COMMIT;
END copiarSubvencionesEnAnexo;


PROCEDURE copiarSubvFromCopy(P_IDCOPY  IN NUMBER,P_IDANEXO   IN NUMBER)  IS

      TYPE TpCursor IS REF CURSOR;
      l_tp_cursor     TpCursor;
      l_sql           VARCHAR2(60);
      l_idsubv     NUMBER;

  BEGIN
      BEGIN -- Exception

      -- Copio las subv con idcpy = P_IDCOPY
      IF (P_IDCOPY IS NOT NULL) THEN
          l_sql := 'select id from tb_copy_subv_declarada where idcopy = ' || P_IDCOPY;

          OPEN l_tp_cursor FOR l_sql;
          LOOP FETCH l_tp_cursor INTO l_idsubv;
              EXIT WHEN l_tp_cursor%NOTFOUND;
              setSubvFromCopy(l_idsubv, P_IDANEXO); -- Copio la subv
          END LOOP;
      END IF;



      EXCEPTION
            WHEN NO_DATA_FOUND THEN
               ROLLBACK;
      END;-- Exception

      COMMIT;

END copiarSubvFromCopy;

PROCEDURE setSubvFromCopy(p_idsubv  IN NUMBER,P_IDANEXO    IN NUMBER) IS

  BEGIN
    -- Copiamos la subv de la copy
    INSERT INTO tb_anexo_mod_subv_decl
      (id,
       idanexo,
       codsubvencion,
       tipomodificacion)

      SELECT
             SQ_TB_ANEXO_MOD_SUBV_DECL.Nextval as id,
             P_IDANEXO,
             codsubvencion,
             null

       FROM tb_copy_subv_declarada
       WHERE id = p_idsubv;


END setSubvFromCopy;


PROCEDURE copiarSubvFromPoliza(P_IDPOLIZA  IN NUMBER,P_IDANEXO   IN NUMBER)
 IS

  BEGIN

          -- Copiamos la subv de la copy
            INSERT INTO tb_anexo_mod_subv_decl
                  (id,
                   idanexo,
                   codsubvencion,
                   tipomodificacion)

             select SQ_TB_ANEXO_MOD_SUBV_DECL.Nextval as id,
                    P_IDANEXO,
                    s.codtiposubvccaa,
                    null
             from tb_subvs_aseg_ccaa c, tb_sc_c_subvs_ccaa s
             where c.lineaseguroid = s.lineaseguroid and
             c.idsubvencion = s.id and c.idpoliza = P_IDPOLIZA;

             INSERT INTO tb_anexo_mod_subv_decl
                  (id,
                   idanexo,
                   codsubvencion,
                   tipomodificacion)
             select
                    SQ_TB_ANEXO_MOD_SUBV_DECL.Nextval as id,
                    P_IDANEXO,
                    s.codtiposubvenesa,
                    null
             from tb_subvs_aseg_enesa e, tb_sc_c_subvs_enesa s
             where e.lineaseguroid = s.lineaseguroid and
             e.idsubvencion = s.id and e.idpoliza=  P_IDPOLIZA;


END copiarSubvFromPoliza;


/* ------------------------------------------------------------------------------
  *                              UTILS
  * ------------------------------------------------------------------------------
  */


  --GETIDCOPY RECUPERA EL IDCOPY DE UNA POLIZA
FUNCTION getIdCopyPoliza(P_IDPOLIZA IN NUMBER) RETURN NUMBER
  IS
    v_idCopy NUMBER := 0;
    referencia varchar2(100);
  BEGIN
       BEGIN

            select referencia into referencia from tb_polizas where idpoliza = P_IDPOLIZA;
            SELECT id into v_idCopy FROM  (SELECT id FROM tb_copy_polizas WHERE refpoliza = referencia ORDER BY fecemisionrecibo DESC) WHERE ROWNUM <= 1;

       EXCEPTION
                WHEN NO_DATA_FOUND THEN
                     v_idCopy := 0;
       END;

        RETURN v_idCopy;

END getIdCopyPoliza;

--Funcion que devuelve el anexo asociado a lapoliza en estado 1
FUNCTION getIdAnexoPoliza(P_IDPOLIZA IN NUMBER) RETURN NUMBER
IS
  v_idAnexo NUMBER :=0;
BEGIN
     BEGIN
            SELECT id into v_idAnexo
            FROM tb_anexo_mod
            WHERE idpoliza = P_IDPOLIZA and estado=1;

       EXCEPTION
                WHEN NO_DATA_FOUND THEN
                     v_idAnexo := 0;
     END;

     RETURN v_idAnexo;


END getIdAnexoPoliza;

  --GETIDCOPY RECUPERA EL IDCOPY DE UNA POLIZA
  FUNCTION getIdCopyAnexo(P_IDANEXO IN NUMBER) RETURN NUMBER
  IS
    v_idCopy NUMBER := 0;
  BEGIN
       BEGIN

            SELECT idcopy INTO v_idCopy
            FROM tb_anexo_mod
            WHERE id = P_IDANEXO;

       EXCEPTION
                WHEN NO_DATA_FOUND THEN
                     v_idCopy := 0;
       END;

        RETURN v_idCopy;

  END getIdCopyAnexo;

  --idPoliza del anexo
  FUNCTION getIdPolizaFromAnexo (P_IDANEXO IN NUMBER) RETURN NUMBER
  IS
      v_idPoliza NUMBER :=0;

  BEGIN
       BEGIN

             SELECT idPoliza into v_idPoliza FROM tb_anexo_mod WHERE id=P_IDANEXO;

       EXCEPTION
                WHEN NO_DATA_FOUND THEN
                     v_idPoliza := 0;
       END;

      RETURN v_idPoliza;

  END getIdPolizaFromAnexo;

end PQ_COPIAR_SUBVENCIONES_A_ANEXO;
/
SHOW ERRORS;