SET DEFINE OFF;
SET SERVEROUTPUT ON;
create or replace package o02agpe0.PQ_VALIDAR_COMS_UNIF is

PROCEDURE doValidarFichComs2015(IDFICHERO IN NUMBER);

PROCEDURE validaDatosPolizas(v_refColectivoFichero IN VARCHAR2, v_refPolizaFichero IN VARCHAR2, v_dcFichero IN NUMBER, v_tipoRefFichero IN VARCHAR2,
							codEntidadCol IN NUMBER, entMediadoraCol IN NUMBER, subEntMediadoraCol IN NUMBER, codPlan IN NUMBER, v_lineaFichero IN NUMBER, idFichero IN NUMBER,
							v_idReciboComision IN NUMBER, v_planFichero IN NUMBER, v_codEntidadFichero IN VARCHAR2, v_oficinaPoliza IN VARCHAR2,
							v_subEntMedFichero IN VARCHAR2, v_entMedFichero IN VARCHAR2, v_faseFichero IN VARCHAR2,
							v_existeColectivo IN NUMBER, v_isValidColectivo IN NUMBER, v_isColectivoRepetido IN NUMBER, v_coincideRefCol IN boolean,v_idAplicacion IN NUMBER);

PROCEDURE checkIncidencias(v_existeColectivo IN NUMBER, v_isValidColectivo IN NUMBER, v_isColectivoRepetido IN NUMBER,
			v_isValidMediadorGGE IN NUMBER, v_isValidMediadorMtoCultivos IN NUMBER, v_isValidMediadorReglamento IN NUMBER,
			v_isValidReferenciaPoliza IN NUMBER, v_isValidPctGlobalEntidad IN NUMBER, v_isValidESMed IN NUMBER, v_coincideRefCol IN boolean,
			idFichero IN NUMBER, v_lineaseguroId IN NUMBER, v_refColectivoFichero IN VARCHAR2, entMediadoraCol IN NUMBER,
			subEntMediadoraCol IN NUMBER, v_refPolizaFichero IN VARCHAR2, v_idreciboComision IN NUMBER, v_oficinaPoliza IN VARCHAR2,
			v_subEntMedFichero IN VARCHAR2, v_entMedFichero IN VARCHAR2, v_faseFichero IN VARCHAR2, v_polizaSinComisiones IN NUMBER);

end PQ_VALIDAR_COMS_UNIF;
/
create or replace package body o02agpe0.PQ_VALIDAR_COMS_UNIF is

PROCEDURE doValidarFichComs2015(IDFICHERO IN NUMBER) IS

TYPE TpCursor                         IS REF CURSOR;
l_tp_cursor                           TpCursor;
l_sql                                 VARCHAR2(2000);

-- variables principales importación
v_faseFichero            tb_coms_unif_fase.fase%TYPE;
v_planFichero            tb_coms_unif_recibo.plan%TYPE;
v_lineaFichero           tb_coms_unif_recibo.linea%TYPE;
v_fechaEmision           tb_coms_unif_fase.fecha_emision_recibo%TYPE;
v_idreciboComision       tb_coms_unif_recibo.id%TYPE;
v_refPolizaFichero       tb_coms_unif_aplicacion.referencia%TYPE;
v_refColectivoFichero    tb_coms_unif_colectivo.referencia%TYPE;
v_colIdAuxFichero        TB_COMS_UNIF_COLECTIVO.id%TYPE;
v_dcFichero              TB_COMS_UNIF_COLECTIVO.Referencia%TYPE;
v_tipoRefFichero         TB_COMS_UNIF_FICHERO.Tipo_Fichero%TYPE;
v_codEntidadFichero      VARCHAR2(20);
v_entMedFichero          VARCHAR2 (20);
v_subEntMedFichero       VARCHAR2(20);
v_estadoFichero          tb_coms_unif_fichero.estado%TYPE;
v_idAplicacion           tb_coms_unif_aplicacion.id%TYPE;

-- variables validaciones
v_existeColectivo NUMBER := 0;
v_isValidColectivo NUMBER := 1;
v_isColectivoRepetido NUMBER := 1;
v_coincideRefCol boolean;

-- datos de póliza sacados del sistema
refColectivo       TB_HISTORICO_COLECTIVOS.REFERENCIA%TYPE;
codEntidadCol      TB_HISTORICO_COLECTIVOS.CODENTIDAD%TYPE;
entMediadoraCol    TB_HISTORICO_COLECTIVOS.ENTMEDIADORA%TYPE;
subEntMediadoraCol TB_HISTORICO_COLECTIVOS.SUBENTMEDIADORA%TYPE;
codPlan            TB_LINEAS.CODPLAN%TYPE;
oficinaPoliza      TB_POLIZAS.OFICINA%TYPE;

contador number:=0;


BEGIN

l_sql :='select fase.fase fase, rec.plan plan, rec.linea linea, fase.fecha_emision_recibo fechaEmision, apli.idrecibo,
SUBSTR(col.codigo_interno,2,3) as codentidad, SUBSTR(col.codigo_interno,1,4)as entMed, nvl(trim(SUBSTR(col.codigo_interno,5)), 0),
apli.referencia refPoliza, apli.dc, apli.tipo_referencia, col.id, col.referencia refColectivo, apli.id as idAplicacion
from TB_COMS_UNIF_FASE fase, TB_COMS_UNIF_RECIBO rec, TB_COMS_UNIF_APLICACION apli, TB_COMS_UNIF_COLECTIVO col
where fase.id = rec.idfase and apli.id = rec.idaplicacion_da and rec.idcolectivo(+)=col.id and fase.idfichero = '||IDFICHERO||' order by fase.fase, rec.plan, rec.linea';

	OPEN l_tp_cursor FOR l_sql;
		LOOP FETCH l_tp_cursor INTO v_faseFichero, v_planFichero, v_lineaFichero, v_fechaEmision, v_idreciboComision,v_codEntidadFichero,
    v_entMedFichero, v_subEntMedFichero, v_refPolizaFichero, v_dcFichero, v_tipoRefFichero, v_colIdAuxFichero, v_refColectivoFichero ,v_idAplicacion;
			EXIT WHEN l_tp_cursor%NOTFOUND;

			---Por si es individual, que no salten
			v_existeColectivo := 1;
			v_isValidColectivo := 1;
			v_isColectivoRepetido := 1;
			v_coincideRefCol := true;
			subEntMediadoraCol := 0;
			codEntidadCol := 0;
			entMediadoraCol := 0;
			codPlan := 0;
      oficinaPoliza:=null;

      contador := contador +1;

      DBMS_OUTPUT.PUT_LINE(contador || ' '  || v_idAplicacion);


			-- si es distinto de null, es que hay colectivo y lo validamos
			IF v_colIdAuxFichero IS NOT null THEN
				v_existeColectivo := PQ_UTIL_VALIDACIONES.checkExisteColectivo(v_refColectivoFichero);

				BEGIN
					select referencia, codentidad, entmediadora, subentmediadora, codplan
					into refColectivo, codEntidadCol,entMediadoraCol, subEntMediadoraCol, codPlan
					from(
					  select c.referencia as referencia, c.codentidad as codentidad,c.entmediadora as entmediadora, c.subentmediadora as subentmediadora, l.codplan as codplan
						from tb_historico_colectivos c, tb_lineas l
						where c.idcolectivo in
							 (select idcolectivo from tb_polizas p where p.referencia = v_refPolizaFichero)
							 and l.lineaseguroid = c.lineaseguroid
							 and c.fechaefecto <= v_fechaEmision
							 --and (c.fechabaja is null or c.fechabaja >= v_fechaEmision)
						order by c.fechacambio desc)
						where rownum = 1;

					EXCEPTION
					  WHEN NO_DATA_FOUND THEN
						   v_isValidColectivo := 0;
						   subEntMediadoraCol :=0;
					  WHEN too_many_rows THEN
						   v_isColectivoRepetido := 0;
				END;

				-- Comprobamos que las referencias de los colectivos son iguales
				IF refColectivo != v_refColectivoFichero THEN
					v_coincideRefCol := false;
				END IF;
			END IF;

      IF PQ_UTIL_VALIDACIONES.obtenerNumeroDePolizas(v_refPolizaFichero, v_dcFichero, v_tipoRefFichero) > 0 THEN
        --Calculamos la oficina
        select oficina into oficinaPoliza
            from tb_polizas p
            where p.referencia = v_refPolizaFichero
            and p.dc = v_dcFichero
            and p.tiporef = v_tipoRefFichero
            and rownum = 1;
      END IF;

      -- Por cada póliza hacemos las validaciones e insertamos incidencias
 validaDatosPolizas(v_refColectivoFichero, v_refPolizaFichero, v_dcFichero, v_tipoRefFichero,
							codEntidadCol, entMediadoraCol, subEntMediadoraCol, codPlan, v_lineaFichero, idFichero,
							v_idReciboComision, v_planFichero, v_codEntidadFichero, oficinaPoliza,
							v_subEntMedFichero, v_entMedFichero, v_faseFichero,
							v_existeColectivo, v_isValidColectivo, v_isColectivoRepetido, v_coincideRefCol, v_idAplicacion);
oficinaPoliza := null;
	END LOOP;

  begin
     v_estadoFichero :=  PQ_UTIL_VALIDACIONES.getEstadoFicheroUnificado(IDFICHERO);
  end;
  begin
    UPDATE TB_COMS_UNIF_FICHERO SET ESTADO = v_estadoFichero WHERE ID = IDFICHERO;

  end;



END doValidarFichComs2015;


PROCEDURE validaDatosPolizas(v_refColectivoFichero IN VARCHAR2, v_refPolizaFichero IN VARCHAR2, v_dcFichero IN NUMBER, v_tipoRefFichero IN VARCHAR2,
							codEntidadCol IN NUMBER, entMediadoraCol IN NUMBER, subEntMediadoraCol IN NUMBER, codPlan IN NUMBER, v_lineaFichero IN NUMBER, idFichero IN NUMBER,
							v_idReciboComision IN NUMBER, v_planFichero IN NUMBER, v_codEntidadFichero IN VARCHAR2, v_oficinaPoliza IN VARCHAR2,
							v_subEntMedFichero IN VARCHAR2, v_entMedFichero IN VARCHAR2, v_faseFichero IN VARCHAR2,
							v_existeColectivo IN NUMBER, v_isValidColectivo IN NUMBER, v_isColectivoRepetido IN NUMBER, v_coincideRefCol IN boolean,v_idAplicacion IN NUMBER) IS

v_lineaseguroId                       NUMBER := 0;
v_isValidMediadorGGE                  NUMBER := 1;--Por defecto a 1 para no dar falsos positivos
v_isValidMediadorMtoCultivos          NUMBER := 1;--Por defecto a 1 para no dar falsos positivos
v_isValidMediadorReglamento           NUMBER := 1;-- Por defecto a 1 para no dar falsos positivos
v_isValidReferenciaPoliza             NUMBER := 0;
v_isvalidESMed                        NUMBER := 1;--Por defecto a 1 para no dar falsos positivos
v_isValidPctGlobalEntidad             NUMBER := 1;--Por defecto a 1 para no dar falsos positivos
v_polizaSinComisiones                 NUMBER := 0;--
entidad                               NUMBER := 0;

BEGIN

     -- obtenemos la lineaseguroid
     v_lineaseguroId := PQ_UTIL_VALIDACIONES.getLineaSeguroId(v_lineaFichero,v_planFichero);

     IF v_lineaseguroId = -1 THEN
       v_lineaseguroId := PQ_UTIL_VALIDACIONES.getLineaSeguroIdGenerica(v_planFichero);
     END IF;

     entidad := '3' || v_codEntidadFichero;

     IF  v_isValidColectivo = 1 AND v_existeColectivo > 0 THEN --correcto

          IF entidad BETWEEN 3000 AND 3999 AND subEntMediadoraCol = 0  THEN
             v_isValidMediadorGGE:=1;
             v_isValidMediadorReglamento:=1;
          ELSE
             v_isValidMediadorGGE := PQ_UTIL_VALIDACIONES.checkMediadorGGE(entMediadoraCol, subEntMediadoraCol, codPlan);
			       v_isValidMediadorMtoCultivos:= PQ_UTIL_VALIDACIONES.checkMediadorMtoCultivos2015(entMediadoraCol, subEntMediadoraCol, codPlan, v_lineaFichero);
             v_isValidMediadorReglamento := PQ_UTIL_VALIDACIONES.checkMediadorReglamento(codEntidadCol);
          END IF;

          v_isvalidESMed := PQ_UTIL_VALIDACIONES.checkESMediadora(entMediadoraCol,subEntMediadoraCol);
     ELSE
            v_isValidMediadorGGE:=1;
			      v_isValidMediadorMtoCultivos:= PQ_UTIL_VALIDACIONES.checkMediadorMtoCultivos2015(entMediadoraCol, subEntMediadoraCol, codPlan, v_lineaFichero);
            v_isValidMediadorReglamento:=1;
     END IF;

     v_isValidReferenciaPoliza :=  PQ_UTIL_VALIDACIONES.checkRefPoliza(v_refPolizaFichero, v_dcFichero, v_tipoRefFichero);
     v_polizaSinComisiones := PQ_UTIL_VALIDACIONES.checkPolizaSinComs(v_idAplicacion);
     -- Chequeamos las validaciones
     checkIncidencias (v_existeColectivo, v_isValidColectivo, v_isColectivoRepetido,
			v_isValidMediadorGGE, v_isValidMediadorMtoCultivos, v_isValidMediadorReglamento,
			v_isValidReferenciaPoliza, v_isValidPctGlobalEntidad, v_isValidESMed, v_coincideRefCol,
			idFichero, v_lineaseguroId, v_refColectivoFichero, entMediadoraCol,
			subEntMediadoraCol, v_refPolizaFichero, v_idreciboComision, v_oficinaPoliza,
			v_subEntMedFichero, v_entMedFichero, v_faseFichero,v_polizaSinComisiones);

END validaDatosPolizas;

PROCEDURE checkIncidencias(v_existeColectivo IN NUMBER, v_isValidColectivo IN NUMBER, v_isColectivoRepetido IN NUMBER,
			v_isValidMediadorGGE IN NUMBER, v_isValidMediadorMtoCultivos IN NUMBER, v_isValidMediadorReglamento IN NUMBER,
			v_isValidReferenciaPoliza IN NUMBER, v_isValidPctGlobalEntidad IN NUMBER, v_isValidESMed IN NUMBER, v_coincideRefCol IN boolean,
			idFichero IN NUMBER, v_lineaseguroId IN NUMBER, v_refColectivoFichero IN VARCHAR2, entMediadoraCol IN NUMBER,
			subEntMediadoraCol IN NUMBER, v_refPolizaFichero IN VARCHAR2, v_idreciboComision IN NUMBER, v_oficinaPoliza IN VARCHAR2,
			v_subEntMedFichero IN VARCHAR2, v_entMedFichero IN VARCHAR2, v_faseFichero IN VARCHAR2, v_polizaSinComisiones IN NUMBER) IS

v_mjsError          VARCHAR2(2000) := '';
v_estado            VARCHAR2(1);
v_tipoIncidencia    NUMBER;
entidadSubentidad   varchar2(20);
entSubColectivo     VARCHAR2(10);
v_validaES          boolean;
subEntFichero       VARCHAR2(20);
v_idIncidR          NUMBER(15);
v_insertaIncid      boolean := TRUE;
BEGIN




     -- INI
     /** posibles valores de codigoInterno:
                        codigoInterno=¿3059¿, codigoInterno=¿305900¿ ó codigoInterno=¿30590000¿ se traducen como 3059-0
                        codigoInterno=¿305902¿ ó codigoInterno=¿30590002¿ se traducen como 3059-2.
                        codigoInterno="3059-08" se traduce como 3058-8 En este caso puede venir un guion
                        codigoInterno="3059 BANTIERRA" se traduce como 3190-Bantierra */

      IF (PQ_UTIL_VALIDACIONES.isNumeric(v_subEntMedFichero)) then
          subEntFichero := abs(nvl(to_number(v_subEntMedFichero),0));
      ELSE
          subEntFichero := v_subEntMedFichero;
      END IF;
     -- FIN

     -- E-S Fichero
     entidadSubentidad :=  v_entMedFichero ||'-'||subEntFichero;
     -- E-S Colectivo
     entSubColectivo := entMediadoraCol ||'-'|| subEntMediadoraCol;

     IF entidadSubentidad != entSubColectivo THEN
        v_validaES := FALSE;
     ELSE
        v_validaES := TRUE;
     END IF;

    IF v_isValidColectivo = 0 OR  v_isValidMediadorMtoCultivos = 0 OR v_isValidMediadorGGE = 0
      OR v_isValidMediadorReglamento = 0 OR v_isValidReferenciaPoliza = 0 OR v_isValidPctGlobalEntidad = 0
      OR v_isColectivoRepetido = 0 OR v_isValidESMed = 0 OR  v_existeColectivo = 0 OR v_validaES = FALSE
      OR  v_coincideRefCol = FALSE OR v_polizaSinComisiones=1 OR v_oficinaPoliza is null THEN

		IF v_polizaSinComisiones=1 THEN
			v_mjsError := v_mjsError || 'Póliza sin distribución de comisiones.|';
		END IF;

		IF v_existeColectivo = 0 THEN
		 v_mjsError := v_mjsError || 'El colectivo no existe en BBDD.|';

		ELSIF v_isValidColectivo = 0 THEN
		 v_mjsError := v_mjsError || 'No corresponden las E-S Med. mostradas con las del colectivo.|';

		ELSIF v_isColectivoRepetido = 0 THEN
		 v_mjsError := v_mjsError || 'El Colectivo esta duplicado en la BBDD.|';

		END IF;

		IF v_isValidReferenciaPoliza = 0 THEN -- si es erroneo
		  v_mjsError :=v_mjsError || 'Error, No existe la referencia de la Poliza.|';
		END IF;

		IF v_oficinaPoliza is null THEN
		  v_mjsError :=v_mjsError || 'Error, La póliza no tiene oficina.|';
		END IF;

		IF v_isValidPctGlobalEntidad = 0 THEN
		  v_mjsError := v_mjsError || 'El porcentaje General Entidades de GGE no debe ser mayor que el porcentaje GGE de la póliza.|';
		END IF;

		IF v_isValidMediadorGGE = 0 THEN
		   v_mjsError := v_mjsError || 'Cada Póliza tiene que tener asignado su mediador GGE.|';
		END IF;

		IF v_isValidMediadorMtoCultivos = 0 THEN
			   v_mjsError := v_mjsError || 'El mediador no existe o está dado de baja.|';
		END IF;

		IF v_isValidMediadorReglamento = 0 THEN
		   v_mjsError := v_mjsError || 'Cada Póliza tiene que tener asignado su mediador Reglamento.|';
		END IF;

		IF v_isValidESMed = 0 THEN
		   v_mjsError := v_mjsError || 'La E-S Mediadora no existe.|';
		END IF;

		IF v_validaES = FALSE THEN
		   v_mjsError := v_mjsError || 'No coincide la E-S Mediadora entre fichero y colectivo.|';
		END IF;

		IF v_coincideRefCol = FALSE THEN
		   v_mjsError := v_mjsError || 'El colectivo del fichero no coincide con el colectivo de la poliza';
		END IF;

		IF v_isValidReferenciaPoliza = 0 OR v_isColectivoRepetido = 0 OR v_existeColectivo =0 or v_polizaSinComisiones=1 THEN
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
		PQ_UTIL_VALIDACIONES.insertIncidenciaUnif(idFichero, v_lineaseguroId, v_refColectivoFichero, entidadSubentidad, v_refPolizaFichero, v_oficinaPoliza,
                       v_estado, v_mjsError, v_idReciboComision, v_tipoIncidencia, entSubColectivo, v_faseFichero);
	END IF;

END checkIncidencias;

end PQ_VALIDAR_COMS_UNIF;
/
SHOW ERRORS;