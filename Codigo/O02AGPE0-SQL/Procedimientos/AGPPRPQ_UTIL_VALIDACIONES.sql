SET DEFINE OFF;
SET SERVEROUTPUT ON;

create or replace package o02agpe0.PQ_UTIL_VALIDACIONES is

  FUNCTION existeRefColectivo(v_colectivoReferencia IN VARCHAR,
                              v_colectivoDC         IN NUMBER,
                              v_fechaEmision        IN DATE) RETURN BOOLEAN;
  FUNCTION existeRefPoliza(v_individualReferencia IN VARCHAR2,
                           v_individualdc         IN NUMBER) RETURN BOOLEAN;
  FUNCTION existeEntidadColectivo(v_colectivoReferencia IN VARCHAR2,
                                  v_colectivoCodInterno IN VARCHAR,
                                  v_fechaEmision        IN DATE)
    RETURN BOOLEAN;
  FUNCTION getLineaSeguroId(v_linea IN NUMBER, v_plan IN NUMBER)
    RETURN NUMBER;
  FUNCTION getLineaSeguroIdGenerica(v_plan IN NUMBER) RETURN NUMBER;
  FUNCTION getESMedColectivo(v_colectivoReferencia IN VARCHAR,
                             v_lineaseguroid       IN NUMBER,
                             v_fechaEmision        IN DATE) RETURN VARCHAR;
  FUNCTION obtenerNumeroDePolizas(v_refpoliza IN VARCHAR2,
                                  v_dc        IN NUMBER,
                                  v_tipoRef   IN VARCHAR2) RETURN NUMBER;

  PROCEDURE insertIncidencia(IDFICHERO         IN NUMBER,
                             LINEA             IN NUMBER,
                             COLECTIVO         IN VARCHAR2,
                             SUBENT            IN VARCHAR2,
                             POLIZA            IN VARCHAR2,
                             OFICINA           VARCHAR2,
                             ESTADO            IN VARCHAR2,
                             MENSAJE           IN VARCHAR2,
                             IDREGISTROFICHERO IN NUMBER,
                             TIPOINCIDENCIA    IN NUMBER,
                             ENTSUBCOLECTIVO   IN VARCHAR2);
  PROCEDURE insertIncidenciasMult(IDFICHERO         IN NUMBER,
                                  LINEA             IN NUMBER,
                                  COLECTIVO         IN VARCHAR2,
                                  SUBENT            IN VARCHAR2,
                                  POLIZA            IN VARCHAR2,
                                  OFICINA           VARCHAR2,
                                  ESTADO            IN VARCHAR2,
                                  MENSAJE           IN VARCHAR2,
                                  IDREGISTROFICHERO IN NUMBER,
                                  TIPOINCIDENCIA    IN NUMBER,
                                  entSubColectivo   IN VARCHAR2);
  PROCEDURE insertIncidenciaUnif(idfichero         IN NUMBER,
                                 lineaseguroid     IN NUMBER,
                                 idcolectivo       IN VARCHAR2,
                                 subent            IN VARCHAR2,
                                 refPoliza         IN VARCHAR2,
                                 oficina           VARCHAR2,
                                 estado            IN VARCHAR2,
                                 mensaje           IN VARCHAR2,
                                 idregistrofichero IN NUMBER,
                                 tipoincidencia    IN NUMBER,
                                 entsubcolectivo   IN VARCHAR2,
                                 idfase            IN NUMBER);

  FUNCTION getSubentidadMedColectivo(v_colectivoReferencia IN VARCHAR,
                                     v_lineaseguroid       IN NUMBER,
                                     v_fechaEmision        IN DATE)
    RETURN NUMBER;

  FUNCTION checkPorcentajesSubentidad(v_lineaSeguroId IN NUMBER,
                                      v_codEntidad    IN NUMBER,
                                      v_codSubentidad IN NUMBER)
    RETURN NUMBER;
  FUNCTION checkComisionesAsociadas(v_refpoliza IN VARCHAR2,
                                    v_tipoRef   IN VARCHAR2) RETURN NUMBER;
  FUNCTION checkComisionesAsociadas2015(v_refpoliza IN VARCHAR2,
                                        v_tipoRef   IN VARCHAR2)
    RETURN NUMBER;
  FUNCTION getEntidadByEntMed(v_codEntMed IN NUMBER) RETURN NUMBER;

  FUNCTION getPlanFichero(idFichero IN NUMBER) RETURN NUMBER;
  FUNCTION getFechaEmision(idFichero IN NUMBER) RETURN DATE;
  FUNCTION checkExisteColectivo17(v_refColectivo IN VARCHAR2,
                                  v_codPlan      IN NUMBER) RETURN NUMBER;
  FUNCTION checkExisteColectivo(v_refColectivo IN VARCHAR2) RETURN NUMBER;
  FUNCTION checkMediadorGGE(entMediadoraCol    IN NUMBER,
                            subEntMediadoraCol IN NUMBER,
                            codPlan            IN NUMBER) RETURN NUMBER;
  FUNCTION checkMediadorReglamento(codEntidadCol IN NUMBER) RETURN NUMBER;
  FUNCTION checkMediadorMtoCultivos(entMediadoraCol       IN NUMBER,
                                    subEntMediadoraCol    IN NUMBER,
                                    v_lineaseguroId       IN NUMBER,
                                    lineaseguroIdGenerico IN NUMBER)
    RETURN NUMBER;
  FUNCTION checkMediadorMtoCultivos2015(v_entMediadoraCol    IN NUMBER,
                                        v_subEntMediadoraCol IN NUMBER,
                                        v_codPlan            IN NUMBER,
                                        v_codLinea           IN NUMBER)
    RETURN NUMBER;

  FUNCTION checkRefPoliza(v_refpoliza IN VARCHAR2,
                          v_dc        IN NUMBER,
                          v_tipoRef   IN VARCHAR2) RETURN VARCHAR2;
  FUNCTION checkRefPoliza17(v_refpoliza     IN VARCHAR2,
                            v_dc            IN NUMBER,
                            v_tipoRef       IN VARCHAR2,
                            v_lineaseguroId IN NUMBER) RETURN VARCHAR2;
  FUNCTION checkESMediadora(v_entMed IN NUMBER, v_subEntMed IN NUMBER)
    RETURN NUMBER;
  FUNCTION checkSubentidadByColectivo(v_refColectivo IN VARCHAR2)
    RETURN NUMBER;
  FUNCTION checkPctGlobalEntidad(v_datosTotGtoEntEntidad IN NUMBER,
                                 IDFICHERO               IN NUMBER)
    RETURN NUMBER;
  FUNCTION getEstadoFicheroUnificado(idFichero IN NUMBER) RETURN VARCHAR2;
  FUNCTION isNumeric(p_string_value IN VARCHAR2) RETURN BOOLEAN;
  FUNCTION checkPolizaSinComs(idAplicacion IN NUMBER) RETURN NUMBER;

  -- RQ.01 - P78497
  FUNCTION checkComsPolizaFichero(v_idfichero      IN NUMBER,
                                  polizaFicheroRef IN VARCHAR2)
    RETURN BOOLEAN;

  FUNCTION tieneComsESMed(v_refPoliza IN VARCHAR2, v_plan IN NUMBER, v_idFichero IN NUMBER)
    RETURN BOOLEAN;

  FUNCTION checkImpPolizasFichero(v_idfichero     IN NUMBER,
                                  v_lineaseguroid IN NUMBER) RETURN BOOLEAN;

end PQ_UTIL_VALIDACIONES;
/
CREATE OR REPLACE PACKAGE body o02agpe0.PQ_UTIL_VALIDACIONES IS
  ---------------------checkRefColectivo--------------------------
  --METODO QUE COMPRUEBA QUE EL IDCOLECTIVO/DC EXISTE EN LA BBDD--
  -----------------------------------------------------------------
  FUNCTION existeRefColectivo(v_colectivoReferencia IN VARCHAR,
                              v_colectivoDC         IN NUMBER,
                              v_fechaEmision        IN DATE) RETURN BOOLEAN IS
    v_col             tb_historico_colectivos.idcolectivo%TYPE;
    v_existeColectivo BOOLEAN := TRUE;
  BEGIN
    BEGIN
      SELECT col.idcolectivo
        INTO v_col
        FROM tb_historico_colectivos col
       WHERE col.referencia = v_colectivoReferencia
         AND col.dc = v_colectivoDC
         AND col.fechaefecto <= v_fechaEmision
         AND rownum = 1;
    EXCEPTION
      WHEN NO_DATA_FOUND THEN
        v_existeColectivo := FALSE;
    END;
    RETURN v_existeColectivo;
  END existeRefColectivo;
  ---------------------getRefPoliza-------------------
  --METODO QUE COMPRUEBA SI EXISTE LA REFERENCIA DE UNA POLIZA  ----
  ------------------------------------------------------------------
  FUNCTION existeRefPoliza(v_individualReferencia IN VARCHAR2,
                           v_individualdc         IN NUMBER) RETURN BOOLEAN IS
    v_refPol       VARCHAR2(7);
    v_existePoliza BOOLEAN := TRUE;
  BEGIN
    BEGIN
      SELECT p.referencia
        INTO v_refPol
        FROM tb_polizas p
       WHERE p.referencia = v_individualReferencia
         AND p.dc = v_individualdc
         AND rownum = 1;
    EXCEPTION
      WHEN NO_DATA_FOUND THEN
        v_existePoliza := FALSE;
    END;
    RETURN v_existePoliza;
  END existeRefPoliza;
  ---------------------obtenerNumeroDePolizas-------------------
  --METODO
  ------------------------------------------------------------------
  FUNCTION obtenerNumeroDePolizas(v_refpoliza IN VARCHAR2,
                                  v_dc        IN NUMBER,
                                  v_tipoRef   IN VARCHAR2) RETURN NUMBER IS
    v_numPolizas NUMBER := 0;
  BEGIN
    BEGIN
      SELECT COUNT(*)
        INTO v_numPolizas
        FROM tb_polizas p
       WHERE p.referencia = v_refpoliza
         AND p.dc = v_dc
         AND p.tiporef = v_tipoRef;
    EXCEPTION
      WHEN NO_DATA_FOUND THEN
        v_numPolizas := 0;
    END;
    RETURN v_numPolizas;
  END obtenerNumeroDePolizas;
  ---------------------existeEntidadColectivo-------------------------
  --Validamos que el colectivo con esa entidad exista
  -------------------------------------------------------------------
  FUNCTION existeEntidadColectivo(v_colectivoReferencia IN VARCHAR2,
                                  v_colectivoCodInterno IN VARCHAR,
                                  v_fechaEmision        IN DATE)
    RETURN BOOLEAN IS
    v_col                    VARCHAR2(7);
    v_entidadColectivo       VARCHAR2(4);
    v_existeEntidadColectivo BOOLEAN := TRUE;
  BEGIN
    BEGIN
      v_entidadColectivo := SUBSTR(v_colectivoCodInterno, 0, 4);
      SELECT c.idcolectivo
        INTO v_col
        FROM tb_historico_colectivos c
       WHERE c.referencia = v_colectivoReferencia
         AND c.entmediadora = TO_NUMBER(v_entidadColectivo)
         AND c.fechaefecto <= v_fechaEmision
         AND rownum = 1;
    EXCEPTION
      WHEN NO_DATA_FOUND THEN
        v_existeEntidadColectivo := FALSE;
    END;
    RETURN v_existeEntidadColectivo;
  END existeEntidadColectivo;
  ---------------------getLineaSeguroId---------------------------------------------
  -- A partir de la linea y el plan obtenemos la lineaSeguroid de la tabla tb_lineas
  -- Si no existe devolvemos menos uno y buscamos la generica
  ----------------------------------------------------------------------------------
  FUNCTION getLineaSeguroId(v_linea IN NUMBER, v_plan IN NUMBER)
    RETURN NUMBER IS
    v_lineaseguroid NUMBER := 0;
  BEGIN
    BEGIN
      SELECT lineaseguroid
        INTO v_lineaseguroid
        FROM TB_LINEAS l
       WHERE l.codlinea = v_linea
         AND l.codplan = v_plan;
    EXCEPTION
      WHEN NO_DATA_FOUND THEN
        v_lineaseguroid := -1;
    END;
    RETURN v_lineaseguroid;
  END getLineaSeguroId;
  ---------------------getLineaSeguroIdGenerica---------------------------------------------
  -- Obtenemos la lineaseguroid a partir de la linea 999 y el plan que venga en el fichero
  ----------------------------------------------------------------------------------
  FUNCTION getLineaSeguroIdGenerica(v_plan IN NUMBER) RETURN NUMBER IS
    v_lineaseguroid NUMBER := 0;
  BEGIN
    BEGIN
      SELECT lineaseguroid
        INTO v_lineaseguroid
        FROM TB_LINEAS l
       WHERE l.codlinea = 999
         AND l.codplan = v_plan;
    EXCEPTION
      WHEN NO_DATA_FOUND THEN
        v_lineaseguroid := -1;
    END;
    RETURN v_lineaseguroid;
  END getLineaSeguroIdGenerica;
  --------------------getESMedColectivo--------------------------------------------------------
  --- Obtiene la E-S mediadora de un colectivo pasandole la referencia del colectivo, el lineaseguroid y la fecha de emision
  --------------------------------------------------------------------------------------------------
  FUNCTION getESMedColectivo(v_colectivoReferencia IN VARCHAR,
                             v_lineaseguroid       IN NUMBER,
                             v_fechaEmision        IN DATE) RETURN VARCHAR IS
    v_entidad       NUMBER := 0;
    v_subentidad    NUMBER := 0;
    entSubColectivo VARCHAR2(10);
  BEGIN
    BEGIN
      SELECT c.entmediadora, c.subentmediadora
        INTO v_entidad, v_subentidad
        FROM tb_historico_colectivos c
       WHERE c.referencia = v_colectivoReferencia
         AND c.lineaseguroid = v_lineaseguroid
         AND c.fechaefecto <= v_fechaEmision
         AND rownum = 1;
      entSubColectivo := v_entidad || '-' || v_subentidad;
    EXCEPTION
      WHEN NO_DATA_FOUND THEN
        entSubColectivo := 0 || '-' || 0;
    END;
    RETURN entSubColectivo;
  END getESMedColectivo;
  ----------------------------------------------------------------
  --------------------insertIncidencias---------------------------
  --METODO QUE REALIZA EL INSERT EN LA BBDD DE UNA INCIDENCIA-----
  ----------------------------------------------------------------
  PROCEDURE insertIncidencia(IDFICHERO         IN NUMBER,
                             LINEA             IN NUMBER,
                             COLECTIVO         IN VARCHAR2,
                             SUBENT            IN VARCHAR2,
                             POLIZA            IN VARCHAR2,
                             OFICINA           VARCHAR2,
                             ESTADO            IN VARCHAR2,
                             MENSAJE           IN VARCHAR2,
                             IDREGISTROFICHERO IN NUMBER,
                             TIPOINCIDENCIA    IN NUMBER,
                             entSubColectivo   IN VARCHAR2) IS
  BEGIN
    INSERT INTO tb_coms_ficheros_incidencias
      (id,
       idfichero,
       lineaseguroid,
       idcolectivo,
       subentidad,
       refpoliza,
       oficina,
       estado,
       mensaje,
       idregistrofichero,
       tipoincidencia,
       es_med_colectivo)
    VALUES
      (sq_coms_ficheros_incidencias.nextval,
       IDFICHERO,
       LINEA,
       COLECTIVO,
       SUBENT,
       POLIZA,
       OFICINA,
       ESTADO,
       MENSAJE,
       IDREGISTROFICHERO,
       TIPOINCIDENCIA,
       ENTSUBCOLECTIVO);
  END insertIncidencia;
  --------------------insertIncidenciasMult---------------------------
  --METODO QUE REALIZA EL INSERT EN LA BBDD DE UNA INCIDENCIA MULT--
  ----------------------------------------------------------------
  PROCEDURE insertIncidenciasMult(IDFICHERO         IN NUMBER,
                                  LINEA             IN NUMBER,
                                  COLECTIVO         IN VARCHAR2,
                                  SUBENT            IN VARCHAR2,
                                  POLIZA            IN VARCHAR2,
                                  OFICINA           VARCHAR2,
                                  ESTADO            IN VARCHAR2,
                                  MENSAJE           IN VARCHAR2,
                                  IDREGISTROFICHERO IN NUMBER,
                                  TIPOINCIDENCIA    IN NUMBER,
                                  entSubColectivo   IN VARCHAR2) IS
  BEGIN
    INSERT INTO TB_COMS_FICHERO_MULT_INCIDENC
      (id,
       idficheromult,
       lineaseguroid,
       idcolectivo,
       subentidad,
       refpoliza,
       oficina,
       estado,
       mensaje,
       idregistrofichero,
       tipoincidencia,
       es_med_colectivo)
    VALUES
      (SQ_COMS_FICHERO_MULT_INCIDENC.nextval,
       IDFICHERO,
       LINEA,
       COLECTIVO,
       SUBENT,
       POLIZA,
       OFICINA,
       ESTADO,
       MENSAJE,
       IDREGISTROFICHERO,
       TIPOINCIDENCIA,
       entSubColectivo);
    COMMIT;
  END insertIncidenciasMult;
  --------------------insertIncidenciaUnif--------------------------------------------
  --METODO QUE REALIZA EL INSERT EN LA BBDD DE UNA INCIDENCIA (FORMATO UNIFICADO)-----
  ------------------------------------------------------------------------------------
  PROCEDURE insertIncidenciaUnif(idfichero         IN NUMBER,
                                 lineaseguroid     IN NUMBER,
                                 idcolectivo       IN VARCHAR2,
                                 subent            IN VARCHAR2,
                                 refPoliza         IN VARCHAR2,
                                 oficina           VARCHAR2,
                                 estado            IN VARCHAR2,
                                 mensaje           IN VARCHAR2,
                                 idregistrofichero IN NUMBER,
                                 tipoincidencia    IN NUMBER,
                                 entsubcolectivo   IN VARCHAR2,
                                 idfase            IN NUMBER) IS
  BEGIN
	o02agpe0.PQ_UTL.LOG('[BEGIN] insertincidenciaunif' || '            ', 2);
    INSERT INTO o02agpe0.TB_COMS_UNIF_FICH_INCIDENCIAS
      (ID,
       ID_FICHERO_UNIF,
       LINEASEGUROID,
       IDCOLECTIVO,
       SUBENTIDAD,
       REFPOLIZA,
       OFICINA,
       ESTADO,
       MENSAJE,
       IDREGISTROFICHERO,
       TIPO_INCIDENCIA,
       ES_MED_COLECTIVO,
       FASE)
    VALUES
      (SQ_COMS_UNIF_FICH_INCIDENCIAS.NEXTVAL,
       idfichero,
       lineaseguroid,
       idcolectivo,
       subent,
       refPoliza,
       oficina,
       estado,
       mensaje,
       idregistrofichero,
       tipoincidencia,
       entsubcolectivo,
       idfase);
	o02agpe0.PQ_UTL.LOG('[END] insertincidenciaunif' || '            ', 2);
  END insertIncidenciaUnif;
  --------------------getSubentidadMediadoraColectivo--------------------------------------------------------
  --- Obtiene la subentidad de un colectivo pasandole el idcolectivo, el lineaseguroid y la fecha de emision
  --------------------------------------------------------------------------------------------------
  FUNCTION getSubentidadMedColectivo(v_colectivoReferencia IN VARCHAR,
                                     v_lineaseguroid       IN NUMBER,
                                     v_fechaEmision        IN DATE)
    RETURN NUMBER IS
    v_subentidad NUMBER(4);
  BEGIN
    BEGIN
      SELECT c.subentmediadora
        INTO v_subentidad
        FROM tb_historico_colectivos c
       WHERE c.referencia = v_colectivoReferencia
         AND c.lineaseguroid = v_lineaseguroid
         AND c.fechaefecto <= v_fechaEmision
         AND rownum = 1;
    EXCEPTION
      WHEN NO_DATA_FOUND THEN
        v_subentidad := 0;
    END;
    RETURN v_subentidad;
  END getSubentidadMedColectivo;
  --------------------comprobarPorcentajeAsignadoSubentidad--------------------------------------------------------
  --- Devuelve mayor que cero si estan dados de alta los porcentajes de subentidad
  --------------------------------------------------------------------------------------------------
  FUNCTION checkPorcentajesSubentidad(v_lineaSeguroId IN NUMBER,
                                      v_codEntidad    IN NUMBER,
                                      v_codSubentidad IN NUMBER)
    RETURN NUMBER IS
    v_contador NUMBER;
  BEGIN
    BEGIN
      v_contador := 0;
      SELECT COUNT(*)
        INTO v_contador
        FROM tb_coms_cultivos_entidades    e,
             tb_coms_cultivos_ents_hist    h,
             tb_coms_cultivos_subentidades sub
       WHERE e.id = h.idcomisionesent
         AND e.lineaseguroid = sub.lineaseguroid
         AND ((e.fec_baja IS NULL OR to_date(CURRENT_DATE, 'dd/mm/yyy') <
             to_date(e.fec_baja, 'dd/mm/yyy')) AND
             to_date(CURRENT_DATE, 'dd/mm/yyy') >= h.fecha_efecto)
         AND to_date(CURRENT_DATE, 'dd/mm/yyy') >= h.fecha_efecto
         AND e.lineaseguroid = v_lineaSeguroId
         AND sub.codentidad = v_codEntidad
         AND sub.codsubentidad = v_codSubentidad;
      IF v_contador = 0 THEN
        SELECT COUNT(*)
          INTO v_contador
          FROM tb_coms_cultivos_subentidades sub
         WHERE sub.lineaseguroid =
               (SELECT lineaseguroid
                  FROM tb_lineas lin
                 WHERE lin.codlinea = 999
                   AND lin.codplan =
                       (SELECT codplan
                          FROM tb_lineas lin
                         WHERE lin.lineaseguroid = v_lineaSeguroId))
           AND sub.codentidad = v_codEntidad
           AND sub.codsubentidad = v_codSubentidad
           AND ((sub.fec_baja IS NULL OR to_date(CURRENT_DATE, 'dd/mm/yyy') <
               to_date(sub.fec_baja, 'dd/mm/yyy')) AND
               to_date(CURRENT_DATE, 'dd/mm/yyy') >= sub.fec_efecto);
      END IF;
    EXCEPTION
      WHEN OTHERS THEN
        v_contador := 0;
    END;
    RETURN v_contador;
  END checkPorcentajesSubentidad;
  --------------------checkComisionesAsociadas----------------------------------------------------
  --- Devuelve mayor que cero si existen comisiones asociadas
  ------------------------------------------------------------------------------------------------
  FUNCTION checkComisionesAsociadas(v_refpoliza IN VARCHAR2,
                                    v_tipoRef   IN VARCHAR2) RETURN NUMBER IS
    v_contador NUMBER;
  BEGIN
    BEGIN
      SELECT COUNT(*)
        INTO v_contador
        FROM tb_coms_comis_aplicaciones comapli
       WHERE comapli.referencia = v_refpoliza
         AND comapli.tiporeferencia = v_tipoRef;
    EXCEPTION
      WHEN OTHERS THEN
        v_contador := 0;
    END;
    RETURN v_contador;
  END;
  --------------------checkComisionesAsociadas2015----------------------------------------------------
  --- Devuelve mayor que cero si existen comisiones asociadas 2015
  ------------------------------------------------------------------------------------------------
  FUNCTION checkComisionesAsociadas2015(v_refpoliza IN VARCHAR2,
                                        v_tipoRef   IN VARCHAR2)
    RETURN NUMBER IS
    v_contador NUMBER;
  BEGIN
    BEGIN
      SELECT COUNT(*)
        INTO v_contador
        FROM tb_coms_unif_aplicacion comapli
       WHERE comapli.referencia = v_refpoliza
         AND comapli.tipo_referencia = v_tipoRef;
    EXCEPTION
      WHEN OTHERS THEN
        v_contador := 0;
    END;
    RETURN v_contador;
  END;
  --------------------getEntidadByEntMed----------------------------------------------------
  --- Devuelve la entidad a la que pertenece una entidad mediadora
  ------------------------------------------------------------------------------------------------
  FUNCTION getEntidadByEntMed(v_codEntMed IN NUMBER) RETURN NUMBER IS
    v_codEntidad NUMBER;
  BEGIN
    BEGIN
      SELECT codentidadnomediadora
        INTO v_codEntidad
        FROM o02agpe0.tb_subentidades_mediadoras sub
       WHERE sub.codentidad = v_codEntMed
         AND rownum = 1;
    EXCEPTION
      WHEN NO_DATA_FOUND THEN
        v_codEntidad := 0;
    END;
    RETURN v_codEntidad;
  END;
  ---------------------getPlanFichero----------
  -- Obtenemos el plan del fichero ------------
  FUNCTION getPlanFichero(idFichero IN NUMBER) RETURN NUMBER IS
    v_planFichero NUMBER := 0;
  BEGIN
    BEGIN
      SELECT plan
        INTO v_planFichero
        FROM o02agpe0.tb_coms_fase f, o02agpe0.tb_coms_ficheros fi
       WHERE f.id = fi.idfase
         AND fi.id = idFichero;
    EXCEPTION
      WHEN NO_DATA_FOUND THEN
        v_planFichero := -1;
    END;
    RETURN v_planFichero;
  END getPlanFichero;
  ---------------------getFechaEmision----------
  -- Obtenemos la fecha de emision del fichero ------------
  FUNCTION getFechaEmision(idFichero IN NUMBER) RETURN DATE IS
    v_fechaEmision DATE;
  BEGIN
    BEGIN
      SELECT fechaemision
        INTO v_fechaEmision
        FROM o02agpe0.tb_coms_fase f
       WHERE f.id = (SELECT idfase
                       FROM o02agpe0.tb_coms_ficheros cf
                      WHERE cf.id = idfichero);
    EXCEPTION
      WHEN NO_DATA_FOUND THEN
        v_fechaEmision := '';
    END;
    RETURN v_fechaEmision;
  END getFechaEmision;
  ---------------------checkExisteColectivo-----------------------
  -- Comprueba si el colectivo del fichero existe en tb_colectivos
  FUNCTION checkExisteColectivo17(v_refColectivo IN VARCHAR2,
                                  v_codPlan      IN NUMBER) RETURN NUMBER IS
    v_existeColectivo NUMBER := 0;
	timestart NUMBER;
  BEGIN
	timestart := dbms_utility.get_time();
    BEGIN
      IF v_codPlan IS NULL THEN
        SELECT COUNT(*)
          INTO v_existeColectivo
          FROM tb_colectivos c
         WHERE c.idcolectivo = v_refColectivo;
      ELSE
        SELECT COUNT(*)
          INTO v_existeColectivo
          FROM tb_colectivos c, tb_lineas l
         WHERE c.idcolectivo = v_refColectivo
           AND c.lineaseguroid = l.lineaseguroid
           AND l.codplan = v_codPlan;
      END IF;
    EXCEPTION
      WHEN NO_DATA_FOUND THEN
        v_existeColectivo := 0;
    END;
	o02agpe0.PQ_UTL.LOG('[RUNTIME] checkExisteColectivo17(' || v_refColectivo || '/' || v_codPlan || '): ' || (dbms_utility.get_time() - timestart) || '            ', 2);
    RETURN v_existeColectivo;
  END checkExisteColectivo17;

  FUNCTION checkExisteColectivo(v_refColectivo IN VARCHAR2) RETURN NUMBER IS
    v_existeColectivo NUMBER := 0;
  BEGIN
    v_existeColectivo := checkExisteColectivo17(v_refColectivo, NULL);
    RETURN v_existeColectivo;
  END checkExisteColectivo;

  ---------------------checkMediadorGGE-------------------
  --METODO QUE COMPRUEBA SI EXISTE LA ENTIDAD Y SUBENTIDAD MEDIADORAS
  --EN LA TABLA TB_COMS_GGE_SUBENTIDADES
  FUNCTION checkMediadorGGE(entMediadoraCol    IN NUMBER,
                            subEntMediadoraCol IN NUMBER,
                            codPlan            IN NUMBER) RETURN NUMBER IS
    v_isValid NUMBER := 0;
	timestart NUMBER;
  BEGIN
	timestart := dbms_utility.get_time();
    BEGIN
      --Se elimina la validacion que comprueba que el mediador esta dado de alta en el mantenimiento del GGE a partir del plan 2015, pero se mantiene para planes anteriores
      IF codPlan < 2015 THEN
        IF entMediadoraCol IS NOT NULL AND subEntMediadoraCol IS NOT NULL THEN
          IF entMediadoraCol BETWEEN 3000 AND 3999 AND
             subEntMediadoraCol = 0 THEN
            v_isValid := 1;
          ELSE
            BEGIN
              SELECT COUNT(*)
                INTO v_isValid
                FROM tb_coms_gge_subentidades
               WHERE codentidad = entMediadoraCol
                 AND codsubentidad = subEntMediadoraCol
                 AND plan = codPlan;
            EXCEPTION
              WHEN NO_DATA_FOUND THEN
                v_isValid := 0;
            END;
          END IF;
        END IF;
      ELSE
        v_isValid := 1;
      END IF;
    END;
	o02agpe0.PQ_UTL.LOG('[RUNTIME] checkMediadorGGE: ' || (dbms_utility.get_time() - timestart) || '            ', 2);
    RETURN v_isValid;
  END checkMediadorGGE;
  ---------------------checkMediadorReglamento-------------------
  --METODO QUE COMPRUEBA SI LA ENTIDAD A LA QUE PERTENECE EL MEDIADOR
  -- ESTA DADA DE ALTA EN LA TABLA  tb_entidades_mediadoras
  FUNCTION checkMediadorReglamento(codEntidadCol IN NUMBER) RETURN NUMBER IS
    v_isValid NUMBER := 0;
	timestart NUMBER;
  BEGIN
	timestart := dbms_utility.get_time();
    BEGIN
      IF codEntidadCol IS NOT NULL THEN
        BEGIN
          SELECT COUNT(*)
            INTO v_isValid
            FROM tb_entidades_mediadoras
           WHERE codentidad = codEntidadCol;
        EXCEPTION
          WHEN NO_DATA_FOUND THEN
            v_isValid := 0;
        END;
      END IF;
    END;
	o02agpe0.PQ_UTL.LOG('[RUNTIME] checkMediadorReglamento: ' || (dbms_utility.get_time() - timestart) || '            ', 2);
    RETURN v_isValid;
  END checkMediadorReglamento;
  ---------------------checkMediadorMtoCultivos-------------------
  --METODO QUE COMPRUEBA SI EXISTE LA ENTIDAD Y SUBENTIDAD MEDIADORAS
  --EN LA TABLA TB_COMS_CULTIVOS_SUBENTIDADES
  FUNCTION checkMediadorMtoCultivos(entMediadoraCol       IN NUMBER,
                                    subEntMediadoraCol    IN NUMBER,
                                    v_lineaseguroId       IN NUMBER,
                                    lineaseguroIdGenerico IN NUMBER)
    RETURN NUMBER IS
    v_isValid NUMBER := 0;
  BEGIN
    IF entMediadoraCol IS NOT NULL AND subEntMediadoraCol IS NOT NULL THEN
      IF entMediadoraCol BETWEEN 3000 AND 3999 AND subEntMediadoraCol = 0 THEN
        v_isValid := 1;
      ELSE
        BEGIN
          SELECT COUNT(*)
            INTO v_isValid
            FROM tb_coms_cultivos_subentidades
           WHERE codentidad = entMediadoraCol
             AND codsubentidad = subEntMediadoraCol
             AND lineaseguroid = v_lineaseguroId;
        EXCEPTION
          WHEN NO_DATA_FOUND THEN
            v_isValid := 0;
        END;
        IF v_isValid = 0 THEN
          BEGIN
            SELECT COUNT(*)
              INTO v_isValid
              FROM tb_coms_cultivos_subentidades
             WHERE codentidad = entMediadoraCol
               AND codsubentidad = subEntMediadoraCol
               AND lineaseguroid = lineaseguroIdGenerico;
          EXCEPTION
            WHEN NO_DATA_FOUND THEN
              v_isValid := 0;
          END;
        END IF;
      END IF;
    END IF;
    RETURN v_isValid;
  END checkMediadorMtoCultivos;
  ---------------------checkMediadorCultivo-------------------
  --METODO QUE COMPRUEBA SI EXISTE LA ENTIDAD Y SUBENTIDAD MEDIADORAS
  --EN LA TABLA TB_COMS_CULTIVOS_SUBENTIDADES
  FUNCTION checkMediadorMtoCultivos2015(v_entMediadoraCol    IN NUMBER,
                                        v_subEntMediadoraCol IN NUMBER,
                                        v_codPlan            IN NUMBER,
                                        v_codLinea           IN NUMBER)
    RETURN NUMBER IS
    v_isValid NUMBER := 0;
	timestart NUMBER;
  BEGIN
	timestart := dbms_utility.get_time();
    BEGIN
      IF v_entMediadoraCol IS NOT NULL AND v_subEntMediadoraCol IS NOT NULL THEN
        IF v_entMediadoraCol BETWEEN 3000 AND 3999 AND
           v_subEntMediadoraCol = 0 THEN
          v_isValid := 1;
        ELSE
          BEGIN
            SELECT COUNT(*)
              INTO v_isValid
              FROM o02agpe0.TB_COMS_CULTIVOS_SUBENTIDADES sub,
                   o02agpe0.tb_lineas                     lin
             WHERE sub.lineaseguroid = lin.lineaseguroid
               AND lin.codplan = v_codPlan
               AND (lin.codlinea = v_codLinea OR lin.codlinea = 999)
               AND sub.codentidad = v_entMediadoraCol
               AND codsubentidad = v_subEntMediadoraCol;
          EXCEPTION
            WHEN NO_DATA_FOUND THEN
              v_isValid := 0;
          END;
        END IF;
      END IF;
    END;
	o02agpe0.PQ_UTL.LOG('[RUNTIME] checkMediadorGGE: ' || (dbms_utility.get_time() - timestart) || '            ', 2);
    RETURN v_isValid;
  END checkMediadorMtoCultivos2015;
  ------------------------------------------------------------------
  ---------------------checkRefPoliza-------------------
  --METODO QUE RECUPERA LA REFERENCIA DE UNA POLIZA ----
  ------------------------------------------------------------------
  FUNCTION checkRefPoliza(v_refpoliza IN VARCHAR2,
                          v_dc        IN NUMBER,
                          v_tipoRef   IN VARCHAR2) RETURN VARCHAR2 IS
    v_refPol NUMBER := 0;
  BEGIN
    BEGIN
      SELECT COUNT(*)
        INTO v_refPol
        FROM tb_polizas p
       WHERE p.referencia = v_refpoliza
         AND p.dc = v_dc
         AND p.tiporef = v_tipoRef;
    EXCEPTION
      WHEN NO_DATA_FOUND THEN
        v_refPol := '';
    END;
    RETURN v_refPol;
  END checkRefPoliza;

  FUNCTION checkRefPoliza17(v_refpoliza     IN VARCHAR2,
                            v_dc            IN NUMBER,
                            v_tipoRef       IN VARCHAR2,
                            v_lineaseguroId IN NUMBER) RETURN VARCHAR2 IS
    v_refPol NUMBER := 0;
	timestart NUMBER;
  BEGIN
	timestart := dbms_utility.get_time();
    BEGIN
      SELECT COUNT(*)
        INTO v_refPol
        FROM o02agpe0.tb_polizas p
       WHERE p.lineaseguroid = v_lineaseguroId
         AND p.referencia = v_refpoliza
         AND p.dc = v_dc
         AND p.tiporef = v_tipoRef;
    EXCEPTION
      WHEN NO_DATA_FOUND THEN
        v_refPol := 0;
    END;
	o02agpe0.PQ_UTL.LOG('[RUNTIME] checkRefPoliza17(' || v_refpoliza || '): ' || (dbms_utility.get_time() - timestart) || '            ', 2);
    RETURN v_refPol;
  END checkRefPoliza17;

  --Funcion que valida si la E-S existe
  FUNCTION checkESMediadora(v_entMed IN NUMBER, v_subEntMed IN NUMBER)
    RETURN NUMBER IS
    v_isValid NUMBER := 0;
	timestart NUMBER;
  BEGIN
	timestart := dbms_utility.get_time();
    BEGIN
      IF v_entMed IS NOT NULL AND v_subEntMed IS NOT NULL THEN
        BEGIN
          SELECT COUNT(*)
            INTO v_isValid
            FROM tb_subentidades_mediadoras s
           WHERE s.codentidad = v_entMed
             AND s.codsubentidad = v_subEntMed;
        EXCEPTION
          WHEN NO_DATA_FOUND THEN
            v_isValid := 0;
        END;
      END IF;
    END;
	o02agpe0.PQ_UTL.LOG('[RUNTIME] checkESMediadora: ' || (dbms_utility.get_time() - timestart) || '            ', 2);
    RETURN v_isValid;
  END checkESMediadora;

  ---------------------checkSubentidadByColectivo-------------------
  --METODO QUE VALIDA SI LA ENTIDAD ES 3XXX-0 DE UN COLECTIVO
  ----------------------------------------------------------------
  FUNCTION checkSubentidadByColectivo(v_refColectivo IN VARCHAR2)
    RETURN NUMBER IS
    v_isValid NUMBER := 0;
  BEGIN
    BEGIN
      SELECT COUNT(*)
        INTO v_isValid
        FROM tb_colectivos c
       WHERE entMediadora BETWEEN 3000 AND 3999
         AND c.subentmediadora = 0
         AND c.idcolectivo = v_refColectivo;
    EXCEPTION
      WHEN NO_DATA_FOUND THEN
        v_isValid := 0;
    END;
    RETURN v_isValid;
  END checkSubentidadByColectivo;
  ---------------------checkPctGlobalEntidad-------------------
  --METODO QUE COMPRUEBA QUE SEA MAYOR EL PORCENTAJE DE LA ENTIDAD
  -- QUE LOS GASTOS TOTALES EXTERNOS DE LA ENTIDAD
  ----------------------------------------------------------------
  FUNCTION checkPctGlobalEntidad(v_datosTotGtoEntEntidad IN NUMBER,
                                 IDFICHERO               IN NUMBER)
    RETURN NUMBER IS
    v_isValid    NUMBER := 0;
    v_pctEntidad NUMBER := 0;
  BEGIN
    BEGIN
      SELECT gp.pctentidades
        INTO v_pctEntidad
        FROM o02agpe0.tb_coms_ficheros       f,
             o02agpe0.tb_coms_fase           fa,
             o02agpe0.tb_coms_generales_plan gp
       WHERE f.id = IDFICHERO
         AND fa.id = f.idfase
         AND gp.plan = fa.plan;
    EXCEPTION
      WHEN NO_DATA_FOUND THEN
        v_isValid := 0;
    END;
    IF (v_datosTotGtoEntEntidad <> 0 AND
       v_pctEntidad > v_datosTotGtoEntEntidad) THEN
      v_isValid := 0;
    ELSE
      v_isValid := 1;
    END IF;
    RETURN v_isValid;
  END checkPctGlobalEntidad;
  FUNCTION getEstadoFicheroUnificado(idFichero IN NUMBER) RETURN VARCHAR2 IS
    v_res          VARCHAR2(1) := 'C';
    v_regsErroneos NUMBER := 0;
    v_regsAvisos   NUMBER := 0;
  BEGIN
    BEGIN
      SELECT COUNT(*)
        INTO v_regsErroneos
        FROM TB_COMS_UNIF_FICH_INCIDENCIAS
       WHERE ID_FICHERO_UNIF = idFichero
         AND ESTADO = 'E';
    END;
    BEGIN
      SELECT COUNT(*)
        INTO v_regsAvisos
        FROM TB_COMS_UNIF_FICH_INCIDENCIAS
       WHERE ID_FICHERO_UNIF = idFichero
         AND ESTADO = 'A';
    END;
    IF (v_regsErroneos > 0) THEN
      v_res := 'E';
    ELSIF (v_regsAvisos > 0) THEN
      v_res := 'A';
    END IF;
    RETURN v_res;
  END getEstadoFicheroUnificado;
  FUNCTION isNumeric(p_string_value IN VARCHAR2) RETURN BOOLEAN AS
    test_value NUMERIC;
  BEGIN
    BEGIN
      test_value := TO_NUMBER(p_string_value);
      RETURN(TRUE);
    EXCEPTION
      WHEN OTHERS THEN
        RETURN(FALSE);
    END;
  END isNumeric;

  FUNCTION checkPolizaSinComs(idAplicacion IN NUMBER) RETURN NUMBER IS
    v_sinComision NUMBER := 0;
  BEGIN
    BEGIN
      SELECT NVL(apl.ERR_SINCOMISIONES, 0)
        INTO v_sinComision
        FROM TB_COMS_UNIF_APLICACION apl
       WHERE apl.id = idAplicacion;
    EXCEPTION
      WHEN NO_DATA_FOUND THEN
        v_sinComision := 1;
    END;
    RETURN v_sinComision;
  
  END checkPolizaSinComs;

  -- RQ.01 - P78497 -- TODO
  FUNCTION checkComsPolizaFichero(v_idfichero      IN NUMBER,
                                  polizaFicheroRef IN VARCHAR2)
    RETURN BOOLEAN IS
    v_x_gd_comsMed NUMBER(13, 2);
    v_gd_comsMed   NUMBER(13, 2);
    v_x_ga_comsMed NUMBER(13, 2);
    v_ga_comsMed   NUMBER(13, 2);
    v_x_gp_comsMed NUMBER(13, 2);
    v_gp_comsMed   NUMBER(13, 2);
    timestart NUMBER;
  BEGIN
	  timestart := dbms_utility.get_time();
        
       select sum(gn.gd_imp_com_mediador) as x_gd_comsMed,
		sum(nvl(gn.GD_IMP_COMMED_ENTIDAD, 0) +
		nvl(gn.GD_IMP_COMMED_ESMED, 0)) as gd_comsMed,
		sum(gn.ga_comision_mediador) as x_ga_comsMed,
		sum(nvl(gn.GA_COMMED_ENTIDAD, 0) + nvl(gn.GA_COMMED_ESMED, 0)) as ga_comsMed,
		sum(gn.gp_comision_mediador) as x_gp_comsMed,
		sum(nvl(gn.GP_COMMED_ENTIDAD, 0) + nvl(gn.GP_COMMED_ESMED, 0)) as ga_comsMed
	   into v_x_gd_comsMed,
           v_gd_comsMed,
           v_x_ga_comsMed,
           v_ga_comsMed,
           v_x_gp_comsMed,
           v_gp_comsMed
		from o02agpe0.TB_COMS_UNIF_FICH_CONTENIDO f,
		o02agpe0.tb_coms_unif_fase fa,
		o02agpe0.tb_coms_unif17_recibo r,
		o02agpe0.TB_COMS_UNIF17_POLIZA p,
		o02agpe0.tb_coms_unif17_grupo_negocio gn
		where f.idfichero = v_idfichero
		and fa.idfichero = f.idfichero
		and r.idfase = fa.id
		and p.idrecibo = r.id
		and gn.idpoliza = p.id
		and p.referencia = polizaFicheroRef;
		
		o02agpe0.PQ_UTL.LOG('[RUNTIME] checkComsPolizaFichero(' || polizaFicheroRef || '): ' || (dbms_utility.get_time() - timestart) || '            ', 2);
  
    RETURN v_x_gd_comsMed = v_gd_comsMed AND v_x_ga_comsMed = v_ga_comsMed AND v_x_gp_comsMed = v_gp_comsMed;
  
  END checkComsPolizaFichero;

  -- RQ.01 - P78497 -- TODO
  FUNCTION tieneComsESMed(v_refPoliza IN VARCHAR2, v_plan IN NUMBER, v_idFichero IN NUMBER)
    RETURN BOOLEAN IS
  
    v_gd_imp_commed_esmed INT;
    v_ga_commed_esmed     INT;
    v_gp_commed_esmed     INT;
	timestart NUMBER;
  BEGIN
	timestart := dbms_utility.get_time();
    select t.GD_IMP_COMMED_ESMED, t.GA_COMMED_ESMED, t.GP_COMMED_ESMED
      into v_gd_imp_commed_esmed, v_ga_commed_esmed, v_gp_commed_esmed
      from o02agpe0.tb_coms_unif_fase f
		inner join o02agpe0.tb_coms_unif17_recibo r on f.id = r.idfase
		inner join o02agpe0.TB_COMS_UNIF17_POLIZA p on p.referencia = v_refPoliza and r.id = p.idrecibo
		inner join o02agpe0.tb_coms_unif17_grupo_negocio t on t.idpoliza = p.id
	  where f.idfichero = v_idFichero and f.plan = v_plan;
    o02agpe0.PQ_UTL.LOG('[RUNTIME] tieneComsESMed: ' || (dbms_utility.get_time() - timestart) || '            ', 2);
    RETURN (v_gd_imp_commed_esmed IS NOT NULL AND v_gd_imp_commed_esmed <> 0) OR
     (v_ga_commed_esmed IS NOT NULL AND v_ga_commed_esmed <> 0) OR
     (v_gp_commed_esmed IS NOT NULL AND v_gp_commed_esmed <> 0);
  
  EXCEPTION
    when NO_DATA_FOUND then
      return false;
    when TOO_MANY_ROWS then
      return false;
    WHEN OTHERS THEN
      return false;
    
  END tieneComsESMed;

  -- RQ.01 - P78497
  --
  FUNCTION checkImpPolizasFichero(v_idfichero     IN NUMBER,
                                  v_lineaseguroid IN NUMBER) RETURN BOOLEAN IS
    v_prima_a   NUMBER(13, 2);
    v_prima_b   NUMBER(13, 2);
    v_charsetid VARCHAR2(15);
	timestart NUMBER;
  BEGIN
  	timestart := dbms_utility.get_time();
    SELECT VALUE
      INTO v_charsetid
      FROM v$nls_parameters
     WHERE parameter = 'NLS_CHARACTERSET';
  
    SELECT SUM(t.frac_pri_com_neta)
      INTO v_prima_a
      FROM o02agpe0.TB_COMS_UNIF17_GRUPO_NEGOCIO t
     inner join o02agpe0.TB_COMS_UNIF17_POLIZA p on t.idpoliza = p.id
     inner join o02agpe0.TB_COMS_UNIF17_RECIBO r on p.idrecibo = r.id
     inner join o02agpe0.tb_coms_unif_fase f on f.idfichero = v_idfichero
                                            and f.id = r.idfase
     inner join o02agpe0.tb_lineas l on l.lineaseguroid = v_lineaseguroid
                                    and l.codplan = f.plan
                                    and l.codlinea = r.linea;
  
    SELECT SUM(x3."pcn") AS pcn
      INTO v_prima_b
      from o02agpe0.TB_COMS_UNIF_FICH_CONTENIDO f,
           o02agpe0.TB_LINEAS l,
           XMLTable('//*/Fase' passing
                    XMLTYPE(f.contenido, nls_charset_id(v_charsetid))
                    columns "plan" NUMBER(4) path '@plan',
                    "recibo" xmltype path 'Recibo') x1,
           XMLTable('//Recibo' passing x1."recibo" columns "linea"
                    NUMBER(3) path '@linea',
                    "gruponegocio" xmltype path '*/GrupoNegocio') x2,
           XMLTable('//*' passing x2."gruponegocio" columns "pcn"
                    NUMBER(13, 2) path '@fracPriComNeta') x3
     where f.idfichero = v_idfichero
       and l.lineaseguroid = v_lineaseguroid
       and l.codplan = x1."plan"
       and l.codlinea = x2."linea";
    o02agpe0.PQ_UTL.LOG('[RUNTIME] checkImpPolizasFichero: ' || (dbms_utility.get_time() - timestart) || '            ', 2);
    if v_prima_a = v_prima_b THEN
      RETURN TRUE;
    END IF;	
    RETURN FALSE;  
  END checkImpPolizasFichero;

end PQ_UTIL_VALIDACIONES;
/
SHOW ERRORS;
