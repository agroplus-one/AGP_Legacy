SET DEFINE OFF;
SET SERVEROUTPUT ON;

create or replace package o02agpe0.PQ_VALIDAR_IMPAGADOS_UNIF is

 PROCEDURE doValidarFichImpagados2015(idFichero IN NUMBER);

  PROCEDURE checkIncidenciasRefPoliza(v_isValidReferenciaPoliza IN BOOLEAN,
                                      v_lineaSeguroId           IN NUMBER,
                                      v_isValidLineaSeguroID    IN BOOLEAN,
                                      v_idImpagados             IN NUMBER,
                                      idFichero                 IN NUMBER,
                                      v_refPolizaFichero        IN VARCHAR2,
                                      v_oficinaPoliza           IN VARCHAR2,
                                      v_faseFichero             IN VARCHAR2,
                                      v_subEntMedFichero        IN VARCHAR2,
                                      v_entMedFichero           IN VARCHAR2,
                                      v_polizaSinComisiones     IN NUMBER);

  PROCEDURE checkIncidenciasRefColectivo(v_isValidColectivo IN BOOLEAN,
                                       v_isValidEntidadColectivo IN BOOLEAN,
                                       v_isValidLineaSeguroID IN BOOLEAN,
										                   v_lineaSeguroId IN NUMBER,
                                       v_refColectivoFichero IN VARCHAR,
                                       v_idImpagados IN NUMBER,
                                       idFichero IN NUMBER,
                                       esColectivoFichero IN VARCHAR2,
                                       esColectivobbdd IN VARCHAR2,
                                       v_refPolizaFichero IN VARCHAR2,
                                       v_oficinaPoliza IN VARCHAR2,
                                       v_faseFichero IN VARCHAR2,
                                       v_polizaSinComisiones IN NUMBER);




end PQ_VALIDAR_IMPAGADOS_UNIF;
/
create or replace package body o02agpe0.PQ_VALIDAR_IMPAGADOS_UNIF is

PROCEDURE doValidarFichImpagados2015(idFichero IN NUMBER) IS

TYPE TpCursor               IS REF CURSOR;
l_tp_cursor                 TpCursor;
l_sql                       VARCHAR2(2000);

--- campos tabla
v_faseFichero               tb_coms_unif_fase.fase%TYPE;
v_planFichero               tb_coms_unif_recibo.plan%TYPE;
v_lineaFichero              tb_coms_unif_recibo.linea%TYPE;
v_fechaEmision              tb_coms_unif_fase.fecha_emision_recibo%TYPE;
v_idImpagados               tb_coms_unif_recibo.id%TYPE;
v_codEntidadFichero         VARCHAR2(20);
v_entMedFichero             NUMBER := 0;
v_subEntMedFichero          VARCHAR2(20);
v_refPolizaFichero          tb_coms_unif_aplicacion.referencia%TYPE;
v_dcPolizaFichero           tb_coms_unif_aplicacion.dc%TYPE;
v_tipoRefFichero            tb_coms_unif_aplicacion.tipo_referencia%TYPE;
v_colIdAuxFichero           TB_COMS_UNIF_COLECTIVO.id%TYPE;
v_refColectivoFichero       tb_coms_unif_colectivo.referencia%TYPE;
v_colectivoDCFichero        tb_coms_unif_colectivo.dc%TYPE;
v_colectivoCodInterno       tb_coms_unif_colectivo.codigo_interno%TYPE;
v_idAplicacion              tb_coms_unif_aplicacion.id%TYPE;

--validaciones
v_isValidReferenciaPoliza     BOOLEAN := TRUE;
v_isValidColectivo            BOOLEAN := TRUE;
v_isValidEntidadColectivo     BOOLEAN := TRUE;
v_isValidLineaSeguroID        BOOLEAN := TRUE;
v_polizaSinComisiones         NUMBER := 0;

oficinaPoliza               TB_POLIZAS.OFICINA%TYPE;
esColectivoFichero          VARCHAR2(24);
esColectivobbdd             VARCHAR2(24);
subEntidadColectivoFichero  VARCHAR2(20);
aux_codInterno              VARCHAR2(20);
v_lineaSeguroId             NUMBER := 0;
v_estadoFichero             tb_coms_unif_fichero.estado%TYPE;


contador number:=0;

BEGIN

l_sql :='select fase.fase fase,
                rec.plan plan,
                rec.linea linea,
                fase.fecha_emision_recibo fechaEmision,
                apli.idrecibo,
                SUBSTR(col.codigo_interno,2,3) as codentidad,
                SUBSTR(col.codigo_interno,1,4)as entMed,
                nvl(trim(SUBSTR(col.codigo_interno,5)), 0) as subentidad,
                apli.referencia refPoliza,
                apli.dc as dcPoliza,
                apli.tipo_referencia,
                col.id,
                col.referencia refColectivo,
                col.dc dcColectivo,
                col.codigo_interno,
                apli.id as idAplicacion
         from TB_COMS_UNIF_FASE fase,
              TB_COMS_UNIF_RECIBO rec,
              TB_COMS_UNIF_APLICACION apli,
              TB_COMS_UNIF_COLECTIVO col
         where fase.id = rec.idfase
               and apli.id = rec.idaplicacion_da
               and rec.idcolectivo(+)=col.id and fase.idfichero = '||IDFICHERO||'
               order by fase.fase, rec.plan, rec.linea';

	OPEN l_tp_cursor FOR l_sql;
  		LOOP FETCH l_tp_cursor INTO
      v_faseFichero,
      v_planFichero,
      v_lineaFichero,
      v_fechaEmision,
      v_idImpagados,
  		v_codEntidadFichero,
      v_entMedFichero,
      v_subEntMedFichero,
      v_refPolizaFichero,
      v_dcPolizaFichero,
  		v_tipoRefFichero,
      v_colIdAuxFichero,
      v_refColectivoFichero,
      v_colectivoDCFichero,
      v_colectivoCodInterno,
      v_idAplicacion;
			EXIT WHEN l_tp_cursor%NOTFOUND;

          oficinaPoliza:=null;

      contador := contador +1;
      DBMS_OUTPUT.PUT_LINE(contador || ' '  || v_idAplicacion);

          IF PQ_UTIL_VALIDACIONES.obtenerNumeroDePolizas(v_refPolizaFichero, v_dcPolizaFichero, v_tipoRefFichero) > 0 THEN
            --Calculamos la oficina
            select oficina into oficinaPoliza
                from tb_polizas p
                where p.referencia = v_refPolizaFichero
                and p.dc = v_dcPolizaFichero
                and p.tiporef = v_tipoRefFichero
                and rownum = 1;
          END IF;

         v_lineaSeguroId := PQ_UTIL_VALIDACIONES.getLineaSeguroId(v_lineaFichero,v_planFichero);

         IF v_lineaSeguroId = -1 THEN
            v_isValidLineaSeguroID := FALSE;
            v_lineaSeguroId := PQ_UTIL_VALIDACIONES.getLineaSeguroIdGenerica(v_planFichero);
         END IF;

         --Validamos si la póliza tiene distribución de comisiones
         v_polizaSinComisiones := PQ_UTIL_VALIDACIONES.checkPolizaSinComs(v_idAplicacion);


         IF v_colIdAuxFichero IS NULL THEN
             v_isValidReferenciaPoliza :=  PQ_UTIL_VALIDACIONES.existeRefPoliza(v_refPolizaFichero, v_dcPolizaFichero);
             checkIncidenciasRefPoliza(v_isValidReferenciaPoliza,v_lineaSeguroId, v_isValidLineaSeguroID,
                                       v_idImpagados,idFichero, v_refPolizaFichero,
                                        oficinaPoliza, v_faseFichero, v_entMedFichero, v_subEntMedFichero, v_polizaSinComisiones);

         ELSE
              -- validamos el colectivo
              v_isValidColectivo := PQ_UTIL_VALIDACIONES.existeRefColectivo(v_refColectivoFichero, v_colectivoDCFichero, v_fechaEmision);

              -- Si el colectivo no existe mostramos en pantalla 0 - 0
              IF v_isValidColectivo = FALSE THEN
                 v_isValidEntidadColectivo := FALSE;
                 esColectivobbdd := 0 || '-' || 0;
              -- Si el colectivo existe validamos que el colectivo exista con esa entidad
              ELSE
                 esColectivobbdd := PQ_UTIL_VALIDACIONES.getESMedColectivo(v_refColectivoFichero , v_lineaSeguroId, v_fechaEmision);
              END IF;

                      /**  posibles valores de codigoInterno:
                        1.codigoInterno=3059, codigoInterno=305900 ó codigoInterno=30590000 se traducen como 3059-0
                        2.codigoInterno=305902 ó codigoInterno=30590002 se traducen como 3059-2.
                        3.codigoInterno="3059-08" se traduce como 3058-8 En este caso puede venir un guion
                        4.codigoInterno="3059 BANTIERRA" se traduce como 3190-Bantierra */

                  --  Cogemos del colectivoCodInterno desde la posicion 5 hasta el final
                  -- si es numero lo formateamos para los casos 1 y 2
                  -- si tiene caracteres lo guardamos sin espacios para los casos 3 y 4
                  -- Esto se hace para mostrarlo en pantalla en campo E-S Med Fichero
                  aux_codInterno := nvl(SUBSTR(v_colectivoCodInterno,5), 0);
                  IF (PQ_UTIL_VALIDACIONES.isNumeric(aux_codInterno)) then
                        subEntidadColectivoFichero := nvl(to_number(SUBSTR(v_colectivoCodInterno,5)), 0);
                  ELSE
                      subEntidadColectivoFichero := SUBSTR(v_colectivoCodInterno,5);
                  END IF;
                  -- si tiene guion no se lo ponemos
                  IF INSTR(subEntidadColectivoFichero,'-') >0 THEN
                     esColectivoFichero := v_entMedFichero || '' ||subEntidadColectivoFichero;
                  ELSE
                     esColectivoFichero := v_entMedFichero || '-' ||subEntidadColectivoFichero;
                  END IF;

                  IF esColectivobbdd != esColectivoFichero then
                     v_isValidEntidadColectivo := FALSE;
                  END IF;

                  checkIncidenciasRefColectivo(v_isValidColectivo,
                                               v_isValidEntidadColectivo,
                                               v_isValidLineaSeguroID,
                                               v_lineaSeguroId,
                                               v_refColectivoFichero,
                                               v_idImpagados,
                                               idFichero,
                                               esColectivoFichero,
                                               esColectivobbdd,
                                               v_refPolizaFichero,
                                               oficinaPoliza,
                                               v_faseFichero, v_polizaSinComisiones);

                  --Reiniciamos variables
                 esColectivoFichero := 0 || '-' || 0;
                 v_isValidEntidadColectivo := TRUE;

         END IF;

        v_isValidLineaSeguroID := TRUE;
        oficinaPoliza := null;
	END LOOP;

   begin
     v_estadoFichero :=  PQ_UTIL_VALIDACIONES.getEstadoFicheroUnificado(IDFICHERO);
  end;
  begin
    UPDATE TB_COMS_UNIF_FICHERO SET ESTADO = v_estadoFichero WHERE ID = IDFICHERO;

  end;

END doValidarFichImpagados2015;

PROCEDURE checkIncidenciasRefPoliza(v_isValidReferenciaPoliza IN BOOLEAN,
                                    v_lineaSeguroId           IN NUMBER,
                                    v_isValidLineaSeguroID    IN BOOLEAN,
                                    v_idImpagados             IN NUMBER,
                                    idFichero                 IN NUMBER,
                                    v_refPolizaFichero        IN VARCHAR2,
                                    v_oficinaPoliza           IN VARCHAR2,
                                    v_faseFichero             IN VARCHAR2,
                                    v_subEntMedFichero        IN VARCHAR2,
                                    v_entMedFichero           IN VARCHAR2,
                                    v_polizaSinComisiones     IN NUMBER) IS

  v_mjsError        VARCHAR2(2000) := '';
  v_estado          VARCHAR2(20);
  v_tipoIncidencia  NUMBER;
  entidadSubentidad varchar2(8);
  v_idIncidR        NUMBER(15);
  v_insertaIncid    boolean := TRUE;

BEGIN

  -- E-S Fichero
  entidadSubentidad := v_entMedFichero || '-' || v_subEntMedFichero;



  IF v_isValidReferenciaPoliza = FALSE OR v_isValidLineaSeguroID = FALSE or v_polizaSinComisiones=1 THEN
     IF v_polizaSinComisiones=1 OR v_oficinaPoliza is  null THEN
        v_mjsError := v_mjsError || 'Póliza sin distribución de comisiones.|';
    END IF;

    IF v_oficinaPoliza is null THEN
      	v_mjsError :=v_mjsError || 'Error, La póliza no tiene oficina.|';
    END IF;
    IF v_isValidReferenciaPoliza = FALSE THEN
      v_mjsError := v_mjsError || 'La Póliza no existe en la BBDD.|';
    END IF;

    IF v_isValidLineaSeguroID = FALSE THEN
      v_mjsError := v_mjsError || 'No existe plan/línea.|';
    END IF;

    IF v_polizaSinComisiones=1 or  v_isValidReferenciaPoliza = FALSE THEN
      v_estado         := 'E';
      v_tipoIncidencia := 1;
    ELSE
      v_estado         := 'A';
      v_tipoIncidencia := 2;
    END IF;


  ELSE
    v_estado         := 'C';
    v_tipoIncidencia := 0;
  END IF;

	IF v_estado = 'E' OR v_estado = 'A' THEN

		--VERIFICAR SI EXISTE REGISTRO EN TB_COMS_UNIF_FICH_INCIDENCIAS
		BEGIN
			SELECT ID
			INTO v_idIncidR
			FROM TB_COMS_UNIF_FICH_INCIDENCIAS
			WHERE ESTADO = 'R'
				AND ID_FICHERO_UNIF = idFichero
				AND LINEASEGUROID = v_lineaseguroId
				AND FASE = v_faseFichero
				AND TIPO_INCIDENCIA = v_tipoIncidencia
				AND ROWNUM = 1;
			v_insertaIncid := FALSE;

		EXCEPTION
			WHEN NO_DATA_FOUND THEN
				v_insertaIncid := TRUE;
		END;

	END IF;

	IF v_insertaIncid = TRUE THEN
		-- Insertamos la incidencia
		PQ_UTIL_VALIDACIONES.insertIncidenciaUnif(idFichero,
                                            v_lineaseguroId,
                                            null,
                                            entidadSubentidad,
                                            v_refPolizaFichero,
                                            v_oficinaPoliza,
                                            v_estado,
                                            v_mjsError,
                                            v_idImpagados,
                                            v_tipoIncidencia,
                                            null,
                                            v_faseFichero);
	END IF;

END checkIncidenciasRefPoliza;


PROCEDURE checkIncidenciasRefColectivo(v_isValidColectivo IN BOOLEAN,
                                       v_isValidEntidadColectivo IN BOOLEAN,
                                       v_isValidLineaSeguroID IN BOOLEAN,
										                   v_lineaSeguroId IN NUMBER,
                                       v_refColectivoFichero IN VARCHAR,
                                       v_idImpagados IN NUMBER,
                                       idFichero IN NUMBER,
                                       esColectivoFichero IN VARCHAR2,
                                       esColectivobbdd IN VARCHAR2,
                                       v_refPolizaFichero IN VARCHAR2,
                                       v_oficinaPoliza IN VARCHAR2,
                                       v_faseFichero IN VARCHAR2,
                                       v_polizaSinComisiones IN NUMBER)
IS

v_mjsError          VARCHAR2(2000) := '';
v_estado            VARCHAR2(20);
v_tipoIncidencia    NUMBER;


v_isValidSubentidad BOOLEAN := TRUE;
v_idIncidR          NUMBER(15);
v_insertaIncid      boolean := TRUE;

 BEGIN

    IF v_isValidColectivo = FALSE OR  v_isValidEntidadColectivo = FALSE OR
    v_isValidLineaSeguroID = FALSE OR v_isValidSubentidad = FALSE or v_polizaSinComisiones=1
    OR v_oficinaPoliza is null THEN

        IF v_polizaSinComisiones=1 THEN
            v_mjsError := v_mjsError || 'Póliza sin distribución de comisiones.|';
        END IF;
        IF v_oficinaPoliza is null THEN
        	v_mjsError :=v_mjsError || 'Error, La póliza no tiene oficina.|';
         END IF;
        IF v_isValidColectivo = FALSE THEN
           v_mjsError := v_mjsError || 'El Colectivo no existe en la BBDD.|';
        END IF;

        IF v_isValidEntidadColectivo = FALSE THEN
           v_mjsError := v_mjsError || 'No corresponden las E-S Med. mostradas con las del colectivo.|';
        END IF;

        IF v_isValidLineaSeguroID = FALSE THEN
           v_mjsError := v_mjsError || 'No existe plan/línea.|';
        END IF;

        IF v_isValidColectivo = FALSE OR v_polizaSinComisiones=1 THEN
           v_estado := 'E';
           v_tipoIncidencia := 1;
        ELSE
           v_estado := 'A';
           v_tipoIncidencia := 2;
        END IF;
     ELSE
         v_estado := 'C';
         v_tipoIncidencia := 0;
     END IF;

	 IF v_estado = 'E' OR v_estado = 'A' THEN

		--VERIFICAR SI EXISTE REGISTRO EN TB_COMS_UNIF_FICH_INCIDENCIAS
		BEGIN
			SELECT ID
			INTO v_idIncidR
			FROM TB_COMS_UNIF_FICH_INCIDENCIAS
			WHERE ESTADO = 'R'
				AND ID_FICHERO_UNIF = idFichero
				AND LINEASEGUROID = v_lineaseguroId
				AND FASE = v_faseFichero
				AND TIPO_INCIDENCIA = v_tipoIncidencia
				AND ROWNUM = 1;
			v_insertaIncid := FALSE;

		EXCEPTION
			WHEN NO_DATA_FOUND THEN
				v_insertaIncid := TRUE;
		END;

	END IF;

	IF v_insertaIncid = TRUE THEN
		-- Insertamos la incidencia
		PQ_UTIL_VALIDACIONES.insertIncidenciaUnif(idFichero, v_lineaseguroId, v_refColectivoFichero,
                                               esColectivoFichero, v_refPolizaFichero, v_oficinaPoliza,
                                                v_estado, v_mjsError, v_idImpagados, v_tipoIncidencia,
                                                esColectivobbdd, v_faseFichero);
	END IF;

END checkIncidenciasRefColectivo;

end PQ_VALIDAR_IMPAGADOS_UNIF;
/

SHOW ERRORS;