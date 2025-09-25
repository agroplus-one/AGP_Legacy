SET DEFINE OFF;
SET SERVEROUTPUT ON;

CREATE OR REPLACE PACKAGE o02agpe0.PQ_DATOS_VARIABLES_RIESGO IS

    FUNCTION getDatosVariablesParcelaRiesgo(IDPOLIZAPARAM IN NUMBER, CODMODULOPARAM IN VARCHAR2, FILACOMPARATIVAPARAM IN NUMBER) RETURN VARCHAR2;
    FUNCTION getDatosVarParcelaRiesgoCPL (IDPOLIZAPARAM IN NUMBER, CODMODULOPARAM IN VARCHAR2) RETURN VARCHAR2;
    FUNCTION getDatVarParcelaModifRiesgo(IDANEXOPARAM IN NUMBER) RETURN VARCHAR2;
    FUNCTION getDatVarParcelaModifCPL(IDANEXOPARAM IN NUMBER,CODMODULOPARAM IN VARCHAR2) RETURN VARCHAR2;
    FUNCTION getDatVarCoberturaParcela (IDPOLIZAPARAM IN NUMBER, CODMODULOPARAM IN VARCHAR2) RETURN VARCHAR2;
    FUNCTION getDatVarCobParcelaReport (IDPOLIZAPARAM IN NUMBER, CODMODULOPARAM IN VARCHAR2) RETURN VARCHAR2;
    -- Pinta la cadena recibida en el log independientemente de su tama?o
    FUNCTION print_log_completo(str IN VARCHAR2) RETURN VARCHAR2;
    

END PQ_DATOS_VARIABLES_RIESGO;
/
CREATE OR REPLACE PACKAGE BODY o02agpe0.PQ_DATOS_VARIABLES_RIESGO IS

-- Variables Globales para todas las funciones
USO_PARCELA          VARCHAR2(2)  := '31';
TIPO_DV_PARCELAS     VARCHAR2(2)  := '16';

-- ******************************************************************
-- Description: Función para obtener los datos variables de parcela que
-- dependen del riesgo cubierto y del concepto principal del modulo
--
-- Input Parameters: IDPOLIZAPARAM, CODMODULOPARAM, FILACOMPARATIVAPARAM
--
-- Output Parameters: Cadena de texto con el siguiente formato:
--   CODCONCEPTO1:CAPITAL_ASEGURADO11#CPPAL11#RIESGO11#CAPITAL_ASEGURADO12#CPPAL12#RIESGO12..|CODCONCEPTO2...
--
-- Author:      T-Systems
--
-- Revision History
-- Date            Author       Reason for Change
-- ----------------------------------------------------------------
-- 21 FEB 2011     A. Serrano   Created.
-- 27/03/202       T-System     Modify (GDLD-63497-PTC-6306 - Adaptación envío coberturas en módulo P)
--                              Se añaden los Datos variables Elegidos y los no elegibles para las complementarias
-- *****************************************************************
FUNCTION getDatosVariablesParcelaRiesgo (IDPOLIZAPARAM IN NUMBER, CODMODULOPARAM IN VARCHAR2, FILACOMPARATIVAPARAM IN NUMBER) RETURN VARCHAR2 is

	relacionDatosVariables	VARCHAR2(32000) := '';
	sqlWhere		varchar2(2000) := '';
	consulta		varchar2(32000);
	aux_lineaseguroid	number(15);

  TYPE cur_typ IS REF CURSOR;
  c_codconcepto   cur_typ;
  c_resultado     cur_typ;
  aux_concepto    number(5);

  aux_idcapital   number(15);
  aux_valor       varchar2(30);
  aux_cpmod       number(3);
  aux_riesgo      number(3);

BEGIN
	--Consulta del 'lineaseguroid' para la póliza indicada como parámetro
	SELECT LINEASEGUROID INTO AUX_LINEASEGUROID FROM TB_POLIZAS WHERE IDPOLIZA = IDPOLIZAPARAM;

	--Consulta sobre el organizador con el uso 'póliza' y ubicación 'Parcela datos variables'
  OPEN c_codconcepto FOR SELECT OI.CODCONCEPTO FROM TB_SC_OI_ORG_INFO OI
                     WHERE OI.LINEASEGUROID = AUX_LINEASEGUROID AND OI.CODUSO = USO_PARCELA
                     AND OI.CODUBICACION = TIPO_DV_PARCELAS
                     AND OI.CODCONCEPTO IN (134, 135, 136, 137, 138, 139, 140);
  LOOP
      FETCH c_codconcepto INTO aux_concepto;
      EXIT WHEN c_codconcepto%NOTFOUND;

      --Recorremos el resultado de la consulta y tratamos de manera particular cada codigo de concepto
      IF (aux_concepto = 140) THEN
    		--DIAS INICIO GARANTIAS
        sqlWhere := 'DV.CODCONCEPTO = 140 AND p.numdiasdesde = DV.VALOR';

    	ELSIF (aux_concepto = 137) THEN
    		--DURACION MAX.GARAN(MESES)
        sqlWhere := 'DV.CODCONCEPTO = 137 AND p.nummeseshasta = DV.VALOR';

    	ELSIF (aux_concepto = 136) THEN
    		--DURACION MAX.GARANT(DIAS)
        sqlWhere := 'DV.CODCONCEPTO = 136 AND p.numdiashasta = DV.VALOR';

    	ELSIF (aux_concepto = 134) THEN
    		--FECHA FIN GARANTIAS
    		sqlWhere := 'DV.CODCONCEPTO = 134 AND p.fgaranthasta = TO_DATE(DV.VALOR, ''dd/mm/yyyy'')';

    	ELSIF (aux_concepto = 138) THEN
    		--FECHA INICIO GARANTIAS
        sqlWhere := 'DV.CODCONCEPTO = 138 AND p.fgarantdesde = TO_DATE(DV.VALOR, ''dd/mm/yyyy'')';

      ELSIF (aux_concepto = 135) THEN
        --EST.FEN.FIN GARANTIAS
        sqlWhere := 'DV.CODCONCEPTO = 135 AND p.codestfenhasta = DV.VALOR';

      ELSIF (aux_concepto = 139) THEN
        --EST.FEN.INICIO GARANTIAS
        sqlWhere := 'DV.CODCONCEPTO = 135 AND p.codestfendesde = DV.VALOR';

    	END IF;

        consulta := 'select ca.CODTIPOCAPITAL, dv.valor, p.codconceptoppalmod, p.codriesgocubierto
      		FROM  tb_parcelas par, tb_capitales_asegurados ca, tb_sc_c_riesgo_cbrto_mod r, Tb_Sc_c_Fecha_Fin_Garantia p, TB_DATOS_VAR_PARCELA DV
      		WHERE ca.idparcela = par.idparcela and p.lineaseguroid = r.lineaseguroid AND r.codmodulo = p.codmodulo
      		AND r.codriesgocubierto = p.codriesgocubierto AND r.codconceptoppalmod = p.codconceptoppalmod
      		AND par.idpoliza = ' || IDPOLIZAPARAM || ' AND r.lineaseguroid = ' || aux_lineaseguroid || '
      		AND r.codmodulo = ''' || CODMODULOPARAM || ''' AND p.codcultivo = par.codcultivo
      		AND (p.codvariedad = par.codvariedad OR p.codvariedad = 999)
      		AND (p.codprovincia = par.codprovincia or P.CODPROVINCIA = 99)
      		AND (p.codcomarca = par.codcomarca or P.CODCOMARCA = 99)
      		AND (p.codtermino = par.codtermino or P.CODTERMINO = 999)
      		AND (p.subtermino = par.subtermino or P.SUBTERMINO = ''9'')
      		AND p.codtipocapital = ca.codtipocapital AND r.elegible = ''N''
      		AND DV.IDCAPITALASEGURADO = CA.IDCAPITALASEGURADO
      		AND ' || sqlWhere || '
      		union
      		select ca.idcapitalasegurado, dv.valor, p.codconceptoppalmod, p.codriesgocubierto
      		FROM  tb_parcelas par, tb_capitales_asegurados ca, tb_comparativas_poliza c, tb_sc_c_riesgo_cbrto_mod r, Tb_Sc_c_Fecha_Fin_Garantia p, TB_DATOS_VAR_PARCELA DV
      		WHERE ca.idparcela = par.idparcela AND p.lineaseguroid = r.lineaseguroid AND p.lineaseguroid = c.lineaseguroid
      		AND r.codmodulo = p.codmodulo AND p.codmodulo = c.codmodulo
      		AND r.codriesgocubierto = p.codriesgocubierto AND p.codriesgocubierto = c.codriesgocubierto
      		AND r.codconceptoppalmod = p.codconceptoppalmod AND p.codconceptoppalmod = c.codconceptoppalmod
      		AND par.idpoliza = ' || IDPOLIZAPARAM || ' AND r.lineaseguroid = ' || aux_lineaseguroid || '
      		AND r.codmodulo = ''' || CODMODULOPARAM || ''' AND c.idpoliza = par.idpoliza AND p.codcultivo = par.codcultivo
      		AND (p.codvariedad = par.codvariedad OR p.codvariedad = 999)
      		AND (p.codprovincia = par.codprovincia or P.CODPROVINCIA = 99)
      		AND (p.codcomarca = par.codcomarca or P.CODCOMARCA = 99)
      		AND (p.codtermino = par.codtermino or P.CODTERMINO = 999)
      		AND (p.subtermino = par.subtermino or P.SUBTERMINO = ''9'')
      		AND p.codtipocapital = ca.codtipocapital AND r.elegible = ''S'' AND c.codconcepto = 363
      		AND c.codvalor = -1 AND c.filacomparativa = ' || FILACOMPARATIVAPARAM || '
      		AND DV.IDCAPITALASEGURADO = CA.IDCAPITALASEGURADO
      		AND ' || sqlWhere || '
          union

          select ca.CODTIPOCAPITAL, dv.valor, p.codconceptoppalmod, p.codriesgocubierto
      		FROM  o02agpe0.tb_polizas po,
                o02agpe0.tb_parcelas par,
                o02agpe0.tb_capitales_asegurados ca,
                o02agpe0.TB_DATOS_VAR_PARCELA DV,
                o02agpe0.tb_parcelas_coberturas c,
                o02agpe0.tb_sc_c_fecha_fin_garantia p
          where  po.idpoliza = par.idpoliza
                and par.idparcela = ca.idparcela
                and ca.idcapitalasegurado = dv.idcapitalasegurado
       	        and c.idparcela = par.idparcela
                and c.codconceptoppalmod = p.codconceptoppalmod
                and c.codriesgocubierto = p.codriesgocubierto
                and c.codmodulo = p.codmodulo
                AND (p.codvariedad = par.codvariedad OR p.codvariedad = 999)
                AND p.codcultivo = par.codcultivo
                and ca.codtipocapital = p.codtipocapital
                and c.codvalor = -1
                and po.idpoliza = ' || IDPOLIZAPARAM || '
                and c.codmodulo = ''' || CODMODULOPARAM || '''
                AND ' || sqlWhere || '
                group by ca.CODTIPOCAPITAL, dv.valor, p.codconceptoppalmod, p.codriesgocubierto';

        pq_utl.log('Consulta construida correctamente ***', 2);

        relacionDatosVariables := relacionDatosVariables || aux_concepto || ':';
        OPEN c_resultado FOR consulta;
        LOOP
            FETCH c_resultado INTO aux_idcapital, aux_valor, aux_cpmod, aux_riesgo;
            EXIT WHEN c_resultado%NOTFOUND;
            relacionDatosVariables := relacionDatosVariables || aux_idcapital || '#' || aux_cpmod  || '#' || aux_riesgo || '#' || aux_valor || ';';
        END LOOP;
        relacionDatosVariables := relacionDatosVariables || '|';

        pq_utl.LOG('Variable relacionDatosVariables rellena correctamente ---'); 

  END LOOP;

  pq_utl.log('Fin del bulce de relacionDatosVariables ***', 2);

	RETURN relacionDatosVariables;

END getDatosVariablesParcelaRiesgo;

-- *****************************************************************
-- Description: Función para obtener los datos variables de parcela que
-- dependen del riesgo cubierto y del concepto principal del modulo
--
-- Input Parameters: IDPOLIZAPARAM, CODMODULOPARAM, FILACOMPARATIVAPARAM
--
-- Output Parameters: Cadena de texto con el siguiente formato:
--   CODCONCEPTO1:CAPITAL_ASEGURADO11#CPPAL11#RIESGO11#CAPITAL_ASEGURADO12#CPPAL12#RIESGO12..|CODCONCEPTO2...
--
-- Author:      T-Systems
--
-- Revision History
-- Date            Author       Reason for Change
-- ----------------------------------------------------------------
-- 21 FEB 2011     A. Serrano   Created.
-- MARZO 2020      T-Systems    Modificado (Pet. 63497)
-- *****************************************************************
FUNCTION getDatosVarParcelaRiesgoCPL (IDPOLIZAPARAM IN NUMBER, CODMODULOPARAM IN VARCHAR2) RETURN VARCHAR2 is

	relacionDatosVariables	VARCHAR2(20000) := '';
	sqlWhere		varchar2(2000) := '';
	consulta		varchar2(32000);

	aux_lineaseguroid	number(15);

  lc VARCHAR2(50) := 'pq.datos_variables_riesgo'; -- Variable que almacena el nombre del paquete y de la funcion

  TYPE cur_typ IS REF CURSOR;
  c_codconcepto   cur_typ;
  c_resultado     cur_typ;
  c_ffg           cur_typ;
  aux_concepto    number(5);

  aux_conceptoEsp    number(5);
  c_codconceptoEsp   cur_typ;
  consultaEsp		     varchar2(32000);
  sqlWhereEsp		     varchar2(2000) := '';

  aux_idcapital   number(15);
  aux_valor       varchar2(30);
  aux_cpmod       number(3);
  aux_riesgo      number(3);
  vaux_concepto    TB_SC_DD_DIC_DATOS.CODCONCEPTO%TYPE := null;
  v_fechaFFG       varchar2(30);

BEGIN

   pq_utl.log(lc, '**@@** PQ_DATOS_VARIABLES_RIESGO.Dentro de getDatosVarParcelaRiesgoCPL');

   pq_utl.log(lc, 'Valor de CODMODULOPARAM:' || CODMODULOPARAM, 2);

	--Consulta del 'lineaseguroid' para la póliza indicada como parámetro
	SELECT LINEASEGUROID INTO AUX_LINEASEGUROID FROM TB_POLIZAS WHERE IDPOLIZA = IDPOLIZAPARAM;

	--Consulta sobre el organizador con el uso 'póliza' y ubicación 'Parcela datos variables'
  OPEN c_codconcepto FOR SELECT OI.CODCONCEPTO FROM TB_SC_OI_ORG_INFO OI
                     WHERE OI.LINEASEGUROID = AUX_LINEASEGUROID AND OI.CODUSO = USO_PARCELA
                     AND OI.CODUBICACION = TIPO_DV_PARCELAS
                     AND OI.CODCONCEPTO IN (134, 135, 136, 137, 138, 139, 140);

  LOOP
      FETCH c_codconcepto INTO aux_concepto;
      EXIT WHEN c_codconcepto%NOTFOUND;

      pq_utl.log(lc, 'Valor de c_codconcepto:' || aux_concepto, 2);

      --Recorremos el resultado de la consulta y tratamos de manera particular cada codigo de concepto
      IF (aux_concepto = 140) THEN
    		--DIAS INICIO GARANTIAS
        sqlWhere := 'DV.CODCONCEPTO = 140 AND p.numdiasdesde = DV.VALOR';

    	ELSIF (aux_concepto = 137) THEN
    		--DURACION MAX.GARAN(MESES)
        sqlWhere := 'DV.CODCONCEPTO = 137 AND p.nummeseshasta = DV.VALOR';

    	ELSIF (aux_concepto = 136) THEN
    		--DURACION MAX.GARANT(DIAS)
        sqlWhere := 'DV.CODCONCEPTO = 136 AND p.numdiashasta = DV.VALOR';

    	ELSIF (aux_concepto = 134) THEN

        pq_utl.log(lc, 'Entramos en 134', 2);

    		--FECHA FIN GARANTIAS
        sqlWhere := 'DV.CODCONCEPTO = 134 AND p.fgaranthasta IN (';
        open c_ffg for 'select DV.VALOR
                          FROM tb_parcelas                par,
                               tb_capitales_asegurados    ca,
                               tb_sc_c_riesgo_cbrto_mod   r,
                               TB_DATOS_VAR_PARCELA       DV
                         WHERE ca.idparcela = par.idparcela
                           AND par.idpoliza = ' || IDPOLIZAPARAM || ' AND r.lineaseguroid = ' || AUX_LINEASEGUROID ||
                           ' AND r.codmodulo = ''' || CODMODULOPARAM || '''  AND r.elegible = ''N''
                           AND par.altaencomplementario = ''S''
                           AND ca.altaencomplementario = ''S''
                           AND DV.IDCAPITALASEGURADO = CA.IDCAPITALASEGURADO
                           AND DV.CODCONCEPTO = 134
                         group by dv.valor';
        LOOP
           fetch c_ffg into v_fechaFFG;
           dbms_output.put_line('v_fechaFFG: '||v_fechaFFG);
           pq_utl.log(lc, 'v_fechaFFG: '||v_fechaFFG);

           exit when c_ffg%NOTFOUND;
--           'DV.CODCONCEPTO = 134 AND p.fgaranthasta = TO_DATE(''' || v_fechaFFG ||''', ''dd/mm/yyyy'')';
              sqlWhere := sqlWhere || 'TO_DATE(''' || v_fechaFFG ||''', ''dd/mm/yyyy''),';
              dbms_output.put_line('sqlWhere FINAL: '||sqlWhere);
              pq_utl.log(lc, 'sqlWhere FINAL: '||sqlWhere);

        END LOOP;
        --Le quitamos la última coma

        pq_utl.log(lc, 'getDatosVarParcelaRiesgoCPL - v_fechaFFG: ' || v_fechaFFG);

      	IF (v_fechaFFG IS NULL) THEN
          pq_utl.log(lc, 'Entramos en el IF');
          sqlWhere := 'DV.CODCONCEPTO = 134 ';
          pq_utl.log(lc, 'Valor de sqlWhere(1): ' || sqlWhere);
        else
          pq_utl.log(lc, 'Entramos en el ELSE');
     	    sqlWhere := substr(sqlWhere, 0, (length(sqlWhere)-1)) || ')';
          pq_utl.log(lc, 'Valor de sqlWhere(2): ' || sqlWhere);
        end if;

        pq_utl.log(lc, 'getDatosVarParcelaRiesgoCPL - sqlWhere: ' || sqlWhere);

		dbms_output.put_line('getDatosVarParcelaRiesgoCPL - sqlWhere: ' || sqlWhere);
    	ELSIF (aux_concepto = 138) THEN
    		--FECHA INICIO GARANTIAS
        execute immediate 'select DV.VALOR
                          FROM tb_parcelas                par,
                               tb_capitales_asegurados    ca,
                               tb_sc_c_riesgo_cbrto_mod   r,
                               TB_DATOS_VAR_PARCELA       DV
                         WHERE ca.idparcela = par.idparcela
                           AND par.idpoliza = ' || IDPOLIZAPARAM || '  AND r.lineaseguroid = '
                           || AUX_LINEASEGUROID || ' AND r.codmodulo = ''' || CODMODULOPARAM || ''' AND r.elegible = ''N''
                           AND par.altaencomplementario = ''S''
                           AND ca.altaencomplementario = ''S''
                           AND DV.IDCAPITALASEGURADO = CA.IDCAPITALASEGURADO
                           AND DV.CODCONCEPTO = 138
                         group by dv.valor'
      into v_fechaFFG;

      sqlWhere := 'DV.CODCONCEPTO = 138 AND p.fgarantdesde = TO_DATE(' || v_fechaFFG ||', ''dd/mm/yyyy'')';

      ELSIF (aux_concepto = 135) THEN
        --EST.FEN.FIN GARANTIAS
        sqlWhere := 'DV.CODCONCEPTO = 135 AND p.codestfenhasta = DV.VALOR';

      ELSIF (aux_concepto = 139) THEN
        --EST.FEN.INICIO GARANTIAS
        sqlWhere := 'DV.CODCONCEPTO = 135 AND p.codestfendesde = DV.VALOR';

    	END IF;

      IF (CODMODULOPARAM = 'P') THEN

         pq_utl.log(lc, 'Entramos por Modulo P');

         consulta := 'select ca.idcapitalasegurado, dv.codconcepto, dv.valor, p.codconceptoppalmod, p.codriesgocubierto
                      		FROM  tb_parcelas par, tb_capitales_asegurados ca, tb_sc_c_riesgo_cbrto_mod r, Tb_Sc_c_Fecha_Fin_Garantia p, TB_DATOS_VAR_PARCELA DV
                      		WHERE ca.idparcela = par.idparcela and p.lineaseguroid = r.lineaseguroid AND r.codmodulo = p.codmodulo
                        		AND r.codriesgocubierto = p.codriesgocubierto AND r.codconceptoppalmod = p.codconceptoppalmod
                        		AND par.idpoliza = ' || IDPOLIZAPARAM || ' AND r.lineaseguroid = ' || aux_lineaseguroid || '
                        		AND r.codmodulo = ''' || CODMODULOPARAM || ''' AND p.codcultivo = par.codcultivo
                        		AND (p.codvariedad = par.codvariedad OR p.codvariedad = 999)
                        		AND (p.codprovincia = par.codprovincia or P.CODPROVINCIA = 99)
                        		AND (p.codcomarca = par.codcomarca or P.CODCOMARCA = 99)
                        		AND (p.codtermino = par.codtermino or P.CODTERMINO = 999)
                        		AND (p.subtermino = par.subtermino or P.SUBTERMINO = ''9'')
                        		AND p.codtipocapital = ca.codtipocapital AND r.elegible = ''N''
                            AND par.altaencomplementario = ''S'' AND ca.altaencomplementario = ''S''
                        		AND DV.IDCAPITALASEGURADO = CA.IDCAPITALASEGURADO
                            AND ' || sqlWhere ||
                       'union
                      select ca.idcapitalasegurado, dv.codconcepto, dv.valor, p.codconceptoppalmod, p.codriesgocubierto
                    		FROM  o02agpe0.tb_polizas po,
                              o02agpe0.tb_parcelas par,
                              o02agpe0.tb_capitales_asegurados ca,
                              o02agpe0.TB_DATOS_VAR_PARCELA DV,
                              o02agpe0.tb_parcelas_coberturas c,
                                                                                o02agpe0.tb_sc_c_fecha_fin_garantia p
                        where  po.idpoliza = par.idpoliza
                              and par.idparcela = ca.idparcela
                              and ca.idcapitalasegurado = dv.idcapitalasegurado
                     	        and c.idparcela = par.idparcela
                              and c.codconceptoppalmod = p.codconceptoppalmod
                              and c.codriesgocubierto = p.codriesgocubierto
                              and c.codmodulo = p.codmodulo
                              AND (p.codvariedad = par.codvariedad OR p.codvariedad = 999)
                              AND p.codcultivo = par.codcultivo
                              and ca.codtipocapital = p.codtipocapital
                              and c.codvalor = -1
                              and po.idpoliza = ' || IDPOLIZAPARAM || '
                              and c.codmodulo = ''' || CODMODULOPARAM || '''
                              AND ' || sqlWhere ||
               'union
                              select ca.idcapitalasegurado, dv.codconcepto, dv.valor, p.codconceptoppalmod, p.codriesgocubierto
		      		 FROM  o02agpe0.tb_polizas po, o02agpe0.tb_parcelas par, o02agpe0.tb_capitales_asegurados ca,
		      		 o02agpe0.TB_DATOS_VAR_PARCELA DV, o02agpe0.tb_parcelas_coberturas c, o02agpe0.tb_sc_c_fecha_fin_garantia p
		      		 where  po.idpoliza = par.idpoliza and par.idparcela = ca.idparcela and ca.idcapitalasegurado = dv.idcapitalasegurado
		      		 and c.idparcela = par.idparcela and c.codconceptoppalmod = p.codconceptoppalmod
		      		 and c.codriesgocubierto = p.codriesgocubierto and c.codmodulo =''CP''
		      		 AND (p.codvariedad = par.codvariedad OR p.codvariedad = 999) AND p.codcultivo = par.codcultivo
               and ca.codtipocapital = p.codtipocapital and c.codvalor = -1 and po.idpoliza =  ' || IDPOLIZAPARAM || ' 
      		     and c.codmodulo = ''CP''  AND DV.CODCONCEPTO = 134 AND p.fgaranthasta = TO_DATE(DV.VALOR, ''dd/mm/yyyy'') '
               || 'group by ca.idcapitalasegurado, dv.codconcepto, dv.valor, p.codconceptoppalmod, p.codriesgocubierto';
      ELSE

         pq_utl.log(lc, 'Entramos por Modulo <> P');

         consulta := 'select ca.idcapitalasegurado, dv.codconcepto, dv.valor, p.codconceptoppalmod, p.codriesgocubierto
                      		FROM  tb_parcelas par, tb_capitales_asegurados ca, tb_sc_c_riesgo_cbrto_mod r, Tb_Sc_c_Fecha_Fin_Garantia p, TB_DATOS_VAR_PARCELA DV
                      		WHERE ca.idparcela = par.idparcela and p.lineaseguroid = r.lineaseguroid AND r.codmodulo = p.codmodulo
                        		AND r.codriesgocubierto = p.codriesgocubierto AND r.codconceptoppalmod = p.codconceptoppalmod
                        		AND par.idpoliza = ' || IDPOLIZAPARAM || ' AND r.lineaseguroid = ' || aux_lineaseguroid || '
                        		AND r.codmodulo = ''' || CODMODULOPARAM || ''' AND p.codcultivo = par.codcultivo
                        		AND (p.codvariedad = par.codvariedad OR p.codvariedad = 999)
                        		AND (p.codprovincia = par.codprovincia or P.CODPROVINCIA = 99)
                        		AND (p.codcomarca = par.codcomarca or P.CODCOMARCA = 99)
                        		AND (p.codtermino = par.codtermino or P.CODTERMINO = 999)
                        		AND (p.subtermino = par.subtermino or P.SUBTERMINO = ''9'')
                        		AND p.codtipocapital = ca.codtipocapital AND r.elegible = ''N''
                            AND par.altaencomplementario = ''S'' AND ca.altaencomplementario = ''S''
                        		AND DV.IDCAPITALASEGURADO = CA.IDCAPITALASEGURADO
                            AND ' || sqlWhere ||
                   ' group by ca.idcapitalasegurado, dv.codconcepto, dv.valor, p.codconceptoppalmod, p.codriesgocubierto';
      END IF;

			-- Si la query es mas larga que lo que permite el metodo de log, se mete en bucle para escribirla completamente
			pq_utl.log(lc, 'Query completa en log -> ' || (print_log_completo(consulta)));


      dbms_output.put_line(consulta);

      relacionDatosVariables := relacionDatosVariables || aux_concepto || ':';
      OPEN c_resultado FOR consulta;
      LOOP
          FETCH c_resultado INTO aux_idcapital, vaux_concepto ,aux_valor, aux_cpmod, aux_riesgo;
          EXIT WHEN c_resultado%NOTFOUND;

          relacionDatosVariables := relacionDatosVariables || aux_idcapital || '#' || aux_cpmod  || '#' || aux_riesgo || '#' || aux_valor || ';';
      END LOOP;
      relacionDatosVariables := relacionDatosVariables || '|';
      pq_utl.log(lc, 'Valor de relacionDatosVariables: '||relacionDatosVariables);

      --END IF;

  END LOOP;


  /* Pet. 63497 (REQ.02) ** MODIF TAM (01.04.2020) ** */

  IF (CODMODULOPARAM = 'P') THEN
     pq_utl.log(lc, 'Entramos a buscar los datos variables Especiales');

     OPEN c_codconceptoEsp FOR SELECT OI.CODCONCEPTO FROM TB_SC_OI_ORG_INFO OI
                        WHERE OI.LINEASEGUROID = AUX_LINEASEGUROID
                          AND OI.CODUSO = USO_PARCELA
                          AND OI.CODUBICACION = TIPO_DV_PARCELAS
                          AND OI.CODCONCEPTO IN (120, 121);

     LOOP
        FETCH c_codconceptoEsp INTO aux_conceptoEsp;
         EXIT WHEN c_codconceptoEsp%NOTFOUND;

         IF (aux_conceptoEsp = 120) THEN
    		    -- % FRANQUICIA
            sqlWhereEsp := ' AND v.CODPCTFRANQUICIAELEG = DV.VALOR';
         ELSIF (aux_conceptoEsp = 121) THEN
            -- % MINIMO INDEMNIZABLE
					  sqlWhereEsp :=  ' AND V.PCTMININDEMNELEG = DV.VALOR';
         END IF;

         pq_utl.log(lc, 'Valor de aux_conceptoEsp: ' ||aux_conceptoEsp);

         consultaEsp := 'SELECT CA.IDCAPITALASEGURADO, DV.CODCONCEPTO ,DV.VALOR, R.CODCONCEPTOPPALMOD, R.CODRIESGOCUBIERTO
						            FROM o02agpe0.TB_PARCELAS              PAR,
						                 o02agpe0.TB_CAPITALES_ASEGURADOS  CA,
						                 o02agpe0.TB_DATOS_VAR_PARCELA     DV,
						                 o02agpe0.TB_SC_C_RIESGO_CBRTO_MOD R,
						                 o02agpe0.TB_SC_C_VINC_VALORES_MOD V,
                             o02agpe0.TB_SC_C_CARACT_MODULO CM
						           WHERE PAR.IDPARCELA = CA.IDPARCELA
						             AND CA.IDCAPITALASEGURADO = DV.IDCAPITALASEGURADO
						             AND R.LINEASEGUROID = V.LINEASEGUROID
						             AND R.CODMODULO = V.CODMODULO
						             AND R.FILAMODULO = V.FILAMODULO
                         AND CM.FILAMODULO = R.FILAMODULO
                         AND CM.LINEASEGUROID = R.LINEASEGUROID
                         AND CM.CODMODULO = R.CODMODULO
                         AND CM.COLUMNAMODULO = V.COLUMNAMODULO
						             AND R.LINEASEGUROID = ' || aux_lineaseguroid || '
						             AND R.CODMODULO  = ''' || CODMODULOPARAM || '''
						             AND R.elegible = ''S''
						             AND R.NIVELECCION = ''D''
						             AND PAR.IDPOLIZA = ' || IDPOLIZAPARAM || '
						             AND DV.CODCONCEPTO = ' || aux_conceptoEsp ||'
                         AND CM.TIPOVALOR = ''E'' ';

         pq_utl.log(lc, 'Query completaEsp en log -> ' || (print_log_completo(consultaEsp)));

         pq_utl.log(lc, 'Query sqlWhereEsp en log -> ' || sqlWhereEsp);

         consultaEsp := consultaEsp || sqlwhereEsp;

				 dbms_output.put_line(consultaEsp);

         relacionDatosVariables := relacionDatosVariables || aux_conceptoEsp || ':';
         pq_utl.log(lc, 'Valor de relacionDatosVariables(Especiales-1): '||relacionDatosVariables);

         OPEN c_resultado FOR consultaEsp;
         LOOP
             FETCH c_resultado INTO aux_idcapital, vaux_concepto ,aux_valor, aux_cpmod, aux_riesgo;
             pq_utl.log(lc, 'Fetch '||aux_idcapital);
             pq_utl.log(lc, 'Fetch-vaux_concepto'||vaux_concepto);
             pq_utl.log(lc, 'Fetch-aux_valor'||aux_valor);
             pq_utl.log(lc, 'Fetch-aux_valor'||aux_cpmod);
             pq_utl.log(lc, 'Fetch-aux_valor'||aux_riesgo);

             EXIT WHEN c_resultado%NOTFOUND;

             relacionDatosVariables := relacionDatosVariables || aux_idcapital || '#' || aux_cpmod  || '#' || aux_riesgo || '#' || aux_valor || ';';
             pq_utl.log(lc, 'Valor de relacionDatosVariables(Especiales): '||relacionDatosVariables);
         END LOOP;

      relacionDatosVariables := relacionDatosVariables || '|';
      --END IF;

     END LOOP;
  END IF;

  pq_utl.log(lc, 'Salimos de recoger los datos especiales: ');


	RETURN relacionDatosVariables;

END getDatosVarParcelaRiesgoCPL;

-- *****************************************************************
-- Description: Función para obtener los datos variables de las parcelas de anexos de modificación que
-- dependen del riesgo cubierto y del concepto principal del modulo
--
-- Input Parameters: IDANEXOPARAM
--
-- Output Parameters: Cadena de texto con el siguiente formato:
--   CODCONCEPTO1:CAPITAL_ASEGURADO11#CPPAL11#RIESGO11#CAPITAL_ASEGURADO12#CPPAL12#RIESGO12..|CODCONCEPTO2...
--
-- Author:      T-Systems
--
-- Revision History
-- Date            Author       Reason for Change
-- ----------------------------------------------------------------
-- 2 MAR 2011      A. Serrano   Created.
-- *****************************************************************
FUNCTION getDatVarParcelaModifRiesgo (IDANEXOPARAM IN NUMBER) RETURN VARCHAR2 is

	relacionDatosVariables	VARCHAR2(32767) := '';
	sqlWhere		varchar2(2000) := '';
	consulta		varchar2(20000);
	aux_lineaseguroid	number(15);
  aux_codmodulo     varchar2(5);

  TYPE cur_typ IS REF CURSOR;
  c_codconcepto   cur_typ;
  c_resultado     cur_typ;
  aux_concepto    number(5);

  aux_idcapital   number(15);
  aux_valor       varchar2(30);
  aux_cpmod       number(3);
  aux_riesgo      number(3);
  
  lc VARCHAR2(50) := 'pq.datos_variables_riesgo'; -- Variable que almacena el nombre del paquete y de la funcion

BEGIN

  pq_utl.log(lc, 'getDatVarParcelaModifRiesgo [INIT]');

	--Consulta del 'lineaseguroid' para la póliza indicada como parámetro
	SELECT P.LINEASEGUROID, nvl(A.CODMODULO, P.CODMODULO) INTO AUX_LINEASEGUROID, aux_codmodulo
     FROM TB_ANEXO_MOD A, TB_POLIZAS P
     WHERE A.IDPOLIZA = P.IDPOLIZA AND A.ID = IDANEXOPARAM;

	--Consulta sobre el organizador con el uso 'póliza' y ubicación 'Parcela datos variables'
  OPEN c_codconcepto FOR SELECT OI.CODCONCEPTO FROM TB_SC_OI_ORG_INFO OI
                     WHERE OI.LINEASEGUROID = AUX_LINEASEGUROID AND OI.CODUSO = USO_PARCELA
                     AND OI.CODUBICACION = TIPO_DV_PARCELAS
                     AND OI.CODCONCEPTO IN (134, 135, 136, 137, 138, 139, 140);
  LOOP
      FETCH c_codconcepto INTO aux_concepto;
      EXIT WHEN c_codconcepto%NOTFOUND;

      --Recorremos el resultado de la consulta y tratamos de manera particular cada codigo de concepto
      IF (aux_concepto = 140) THEN
    		--DIAS INICIO GARANTIAS
        sqlWhere := 'DV.CODCONCEPTO = 140 AND p.numdiasdesde = DV.VALOR';

    	ELSIF (aux_concepto = 137) THEN
    		--DURACION MAX.GARAN(MESES)
        sqlWhere := 'DV.CODCONCEPTO = 137 AND p.nummeseshasta = DV.VALOR';

    	ELSIF (aux_concepto = 136) THEN
    		--DURACION MAX.GARANT(DIAS)
        sqlWhere := 'DV.CODCONCEPTO = 136 AND p.numdiashasta = DV.VALOR';

    	ELSIF (aux_concepto = 134) THEN
    		--FECHA FIN GARANTIAS
    	--sqlWhere := 'DV.CODCONCEPTO = 134 AND p.fgaranthasta = TO_DATE(DV.VALOR, ''dd/mm/yyyy'')';
        sqlWhere := 'DV.CODCONCEPTO = 134 AND TO_CHAR(p.fgaranthasta, ''DD/MM/YYYY'') = dv.valor';

    	ELSIF (aux_concepto = 138) THEN
    		--FECHA INICIO GARANTIAS
        --sqlWhere := 'DV.CODCONCEPTO = 138 AND p.fgarantdesde = TO_DATE(DV.VALOR, ''dd/mm/yyyy'')';
         sqlWhere := 'DV.CODCONCEPTO = 138 AND TO_CHAR(p.fgarantdesde, ''DD/MM/YYYY'') = dv.valor';

      ELSIF (aux_concepto = 135) THEN
        --EST.FEN.FIN GARANTIAS
        sqlWhere := 'DV.CODCONCEPTO = 135 AND p.codestfenhasta = DV.VALOR';

      ELSIF (aux_concepto = 139) THEN
        --EST.FEN.INICIO GARANTIAS
        sqlWhere := 'DV.CODCONCEPTO = 135 AND p.codestfendesde = DV.VALOR';

      END IF;

      --IF (sqlWhere != '') THEN
        consulta := 'select ca.id, dv.valor, p.codconceptoppalmod, p.codriesgocubierto
      		FROM  tb_anexo_mod a, tb_anexo_mod_parcelas par, tb_anexo_mod_capitales_aseg ca, tb_sc_c_riesgo_cbrto_mod r, Tb_Sc_c_Fecha_Fin_Garantia p, Tb_Anexo_Mod_Capitales_Dts_Vbl DV
      		WHERE ca.idparcelaanexo = par.id and p.lineaseguroid = r.lineaseguroid AND r.codmodulo = p.codmodulo
          AND r.codriesgocubierto = p.codriesgocubierto AND r.codconceptoppalmod = p.codconceptoppalmod
          and a.id = ' || IDANEXOPARAM || ' and par.idanexo = a.id
          AND r.lineaseguroid = ' || aux_lineaseguroid || '
      		AND r.codmodulo = ''' || aux_codmodulo || ''' AND p.codcultivo = par.codcultivo
      		AND (p.codvariedad = par.codvariedad OR p.codvariedad = 999)
      		AND (p.codprovincia = par.codprovincia or P.CODPROVINCIA = 99)
      		AND (p.codcomarca = par.codcomarca or P.CODCOMARCA = 99)
      		AND (p.codtermino = par.codtermino or P.CODTERMINO = 999)
      		AND (p.subtermino = par.subtermino or P.SUBTERMINO = ''9'')
      		AND p.codtipocapital = ca.codtipocapital AND r.elegible = ''N''
          AND DV.IDCAPITALASEGURADO = CA.Id

      		AND ' || sqlWhere || '
      		union

          select ca.id, dv.valor, p.codconceptoppalmod, p.codriesgocubierto
          FROM  tb_anexo_mod a, tb_anexo_mod_parcelas par, tb_anexo_mod_coberturas cob, tb_anexo_mod_capitales_aseg ca, tb_sc_c_riesgo_cbrto_mod r, Tb_Sc_c_Fecha_Fin_Garantia p, Tb_Anexo_Mod_Capitales_Dts_Vbl DV
          WHERE ca.idparcelaanexo = par.id and p.lineaseguroid = r.lineaseguroid AND r.codmodulo = p.codmodulo
          AND r.codriesgocubierto = p.codriesgocubierto AND r.codconceptoppalmod = p.codconceptoppalmod
          and a.id = ' || IDANEXOPARAM || ' and par.idanexo = a.id and cob.idanexo = a.id
          AND r.lineaseguroid = ' || aux_lineaseguroid || '
          AND r.codmodulo = ''' || aux_codmodulo || ''' AND p.codcultivo = par.codcultivo
          AND (p.codvariedad = par.codvariedad OR p.codvariedad = 999)
          AND (p.codprovincia = par.codprovincia or P.CODPROVINCIA = 99)
          AND (p.codcomarca = par.codcomarca or P.CODCOMARCA = 99)
          AND (p.codtermino = par.codtermino or P.CODTERMINO = 999)
          AND (p.subtermino = par.subtermino or P.SUBTERMINO = ''9'')
          AND p.codtipocapital = ca.codtipocapital AND r.elegible = ''S'' AND cob.codconcepto = 363
          and (cob.tipomodificacion is null OR cob.tipomodificacion = ''A'')

          AND DV.IDCAPITALASEGURADO = CA.Id AND ' || sqlWhere || '
          group by ca.id, dv.valor, p.codconceptoppalmod, p.codriesgocubierto
          union
             select ca.id, dv.valor, p.codconceptoppalmod, p.codriesgocubierto
             FROM o02agpe0.tb_anexo_mod a,
                  o02agpe0.tb_anexo_mod_parcelas par,
                  o02agpe0.tb_anexo_mod_capitales_aseg ca,
                  o02agpe0.Tb_Anexo_Mod_Capitales_Dts_Vbl dv,
                  o02agpe0.tb_sc_c_fecha_fin_garantia p,
                  o02agpe0.tb_sc_c_riesgo_cbrto_mod r
            where a.id = par.idanexo
                  and par.id = ca.idparcelaanexo
                  and ca.id = dv.idcapitalasegurado
                  AND (p.codvariedad = par.codvariedad OR p.codvariedad = 999)
                  AND p.codcultivo = par.codcultivo
                  and ca.codtipocapital = p.codtipocapital
                  AND r.codriesgocubierto = p.codriesgocubierto
                  AND r.codconceptoppalmod = p.codconceptoppalmod
                  and r.codmodulo = ''' || aux_codmodulo || '''
                  and r.lineaseguroid = ' || aux_lineaseguroid || '
                  and a.id = ' || IDANEXOPARAM || '
                  AND DV.CODCONCEPTO = 134
                  and r.elegible = ''S''
                  and (select count(*)
                          FROM o02agpe0.tb_anexo_mod a1,
                               o02agpe0.tb_anexo_mod_parcelas par1,
                               o02agpe0.tb_anexo_mod_capitales_aseg ca1,
                               o02agpe0.Tb_Anexo_Mod_Capitales_Dts_Vbl dv1
                          where a1.id = par1.idanexo
                            and par1.id = ca1.idparcelaanexo
                            and ca1.id = dv1.idcapitalasegurado
                            and dv1.valor = ''-1''
                            and a1.id = ' || IDANEXOPARAM || '
                            and ca1.id = ca.id
                            AND DV1.CODCONCEPTO = 363) > 0
            group by ca.id, dv.valor, p.codconceptoppalmod, p.codriesgocubierto';

--and par.tipomodificacion is not null
        dbms_output.put_line(consulta);

        relacionDatosVariables := relacionDatosVariables || aux_concepto || ':';
        OPEN c_resultado FOR consulta;
        LOOP
            FETCH c_resultado INTO aux_idcapital, aux_valor, aux_cpmod, aux_riesgo;
            EXIT WHEN c_resultado%NOTFOUND;
            relacionDatosVariables := relacionDatosVariables || aux_idcapital || '#' || aux_cpmod  || '#' || aux_riesgo || '#' || aux_valor || ';';
        END LOOP;
        relacionDatosVariables := relacionDatosVariables || '|';
      --END IF;

  END LOOP;
  
    pq_utl.log(lc, 'getDatVarParcelaModifRiesgo [END]');
    
    pq_utl.log(lc, 'Valor de relacionDatosVariables:' || relacionDatosVariables);

	RETURN relacionDatosVariables;

END getDatVarParcelaModifRiesgo;

-- *****************************************************************
-- Description: Función para obtener los datos variables de las parcelas de anexos de modificación que
-- dependen del riesgo cubierto y del concepto principal del modulo
--
-- Input Parameters: IDANEXOPARAM
--
-- Output Parameters: Cadena de texto con el siguiente formato:
--   CODCONCEPTO1:CAPITAL_ASEGURADO11#CPPAL11#RIESGO11#CAPITAL_ASEGURADO12#CPPAL12#RIESGO12..|CODCONCEPTO2...
--
-- Author:      T-Systems
--
-- Revision History
-- Date            Author       Reason for Change
-- ----------------------------------------------------------------
-- 2 MAR 2011      A. Serrano   Created.
-- *****************************************************************
FUNCTION getDatVarParcelaModifCPL(IDANEXOPARAM IN NUMBER,CODMODULOPARAM IN VARCHAR2) RETURN VARCHAR2 is

	relacionDatosVariables	VARCHAR2(20000) := '';
	sqlWhere		varchar2(2000) := '';
	consulta		varchar2(20000);
	aux_lineaseguroid	number(15);

  TYPE cur_typ IS REF CURSOR;
  c_codconcepto   cur_typ;
  c_resultado     cur_typ;
  aux_concepto    number(5);

  aux_idcapital   number(15);
  aux_valor       varchar2(30);
  aux_cpmod       number(3);
  aux_riesgo      number(3);

BEGIN
	--Consulta del 'lineaseguroid' para la póliza indicada como parámetro
	SELECT P.LINEASEGUROID INTO AUX_LINEASEGUROID FROM TB_ANEXO_MOD A, TB_POLIZAS P
  WHERE A.IDPOLIZA = P.IDPOLIZA AND A.ID = IDANEXOPARAM;

	--Consulta sobre el organizador con el uso 'póliza' y ubicación 'Parcela datos variables'
  OPEN c_codconcepto FOR SELECT OI.CODCONCEPTO FROM TB_SC_OI_ORG_INFO OI
                     WHERE OI.LINEASEGUROID = AUX_LINEASEGUROID AND OI.CODUSO = USO_PARCELA
                     AND OI.CODUBICACION = TIPO_DV_PARCELAS
                     AND OI.CODCONCEPTO IN (134, 135, 136, 137, 138, 139, 140);
  LOOP
      FETCH c_codconcepto INTO aux_concepto;
      EXIT WHEN c_codconcepto%NOTFOUND;

      --Recorremos el resultado de la consulta y tratamos de manera particular cada codigo de concepto
      IF (aux_concepto = 140) THEN
    		--DIAS INICIO GARANTIAS
        sqlWhere := 'DV.CODCONCEPTO = 140 AND p.numdiasdesde = DV.VALOR';

    	ELSIF (aux_concepto = 137) THEN
    		--DURACION MAX.GARAN(MESES)
        sqlWhere := 'DV.CODCONCEPTO = 137 AND p.nummeseshasta = DV.VALOR';

    	ELSIF (aux_concepto = 136) THEN
    		--DURACION MAX.GARANT(DIAS)
        sqlWhere := 'DV.CODCONCEPTO = 136 AND p.numdiashasta = DV.VALOR';

    	ELSIF (aux_concepto = 134) THEN
    		--FECHA FIN GARANTIAS
    	--sqlWhere := 'DV.CODCONCEPTO = 134 AND p.fgaranthasta = TO_DATE(DV.VALOR, ''dd/mm/yyyy'')';
        sqlWhere := 'DV.CODCONCEPTO = 134 AND TO_CHAR(p.fgaranthasta, ''DD/MM/YYYY'') = dv.valor';

    	ELSIF (aux_concepto = 138) THEN
    		--FECHA INICIO GARANTIAS
        --sqlWhere := 'DV.CODCONCEPTO = 138 AND p.fgarantdesde = TO_DATE(DV.VALOR, ''dd/mm/yyyy'')';
         sqlWhere := 'DV.CODCONCEPTO = 138 AND TO_CHAR(p.fgarantdesde, ''DD/MM/YYYY'') = dv.valor';

      ELSIF (aux_concepto = 135) THEN
        --EST.FEN.FIN GARANTIAS
        sqlWhere := 'DV.CODCONCEPTO = 135 AND p.codestfenhasta = DV.VALOR';

      ELSIF (aux_concepto = 139) THEN
        --EST.FEN.INICIO GARANTIAS
        sqlWhere := 'DV.CODCONCEPTO = 135 AND p.codestfendesde = DV.VALOR';

      END IF;


        consulta := 'select ca.id, dv.valor, p.codconceptoppalmod, p.codriesgocubierto
                    FROM tb_anexo_mod_parcelas par, tb_anexo_mod_capitales_aseg ca, tb_sc_c_riesgo_cbrto_mod r, Tb_Sc_c_Fecha_Fin_Garantia p, Tb_Anexo_Mod_Capitales_Dts_Vbl DV
                    WHERE ca.idparcelaanexo = par.id
                    AND p.lineaseguroid = r.lineaseguroid
                    AND r.codmodulo = p.codmodulo
                    AND r.codriesgocubierto = p.codriesgocubierto
                    AND r.codconceptoppalmod = p.codconceptoppalmod
                    AND par.idanexo = ' || IDANEXOPARAM || '
                    AND r.lineaseguroid = ' || aux_lineaseguroid || '
                    AND r.codmodulo = ''' || CODMODULOPARAM || '''
                    AND p.codcultivo = par.codcultivo
                    AND (p.codvariedad = par.codvariedad OR p.codvariedad = 999)
                    AND (p.codprovincia = par.codprovincia or P.CODPROVINCIA = 99)
                    AND (p.codcomarca = par.codcomarca or P.CODCOMARCA = 99)
                    AND (p.codtermino = par.codtermino or P.CODTERMINO = 999)
                    AND (p.subtermino = par.subtermino or P.SUBTERMINO = ''9'')
                    AND p.codtipocapital = ca.codtipocapital AND r.elegible = ''N''
                    AND DV.IDCAPITALASEGURADO = CA.Id
                    AND par.tipomodificacion is not null
                    AND ' || sqlWhere || '
                    group by ca.id, dv.valor, p.codconceptoppalmod, p.codriesgocubierto';

        dbms_output.put_line(consulta);

        relacionDatosVariables := relacionDatosVariables || aux_concepto || ':';
        OPEN c_resultado FOR consulta;
        LOOP
            FETCH c_resultado INTO aux_idcapital, aux_valor, aux_cpmod, aux_riesgo;
            EXIT WHEN c_resultado%NOTFOUND;
            relacionDatosVariables := relacionDatosVariables || aux_idcapital || '#' || aux_cpmod  || '#' || aux_riesgo || '#' || aux_valor || ';';
        END LOOP;
        relacionDatosVariables := relacionDatosVariables || '|';


  END LOOP;

	RETURN relacionDatosVariables;

END getDatVarParcelaModifCPL;


-- *****************************************************************
-- Description: Función para obtener los datos variables de las parcelas de anexos de modificación que
-- dependen del riesgo cubierto y del concepto principal del modulo
--
-- Input Parameters: IDANEXOPARAM
--
-- Output Parameters: Cadena de texto con el siguiente formato:
--   CODCONCEPTO1:CAPITAL_ASEGURADO11#CPPAL11#RIESGO11#CAPITAL_ASEGURADO12#CPPAL12#RIESGO12..|CODCONCEPTO2...
--
-- Author:      T-Systems
--
-- Revision History
-- Date            Author       Reason for Change
-- ----------------------------------------------------------------
-- 2 MAR 2011      A. Serrano   Created.
-- 06.04.2020     T-Systems     Modificado por pet. 63497
-- *****************************************************************
FUNCTION getDatVarCoberturaParcela (IDPOLIZAPARAM IN NUMBER, CODMODULOPARAM IN VARCHAR2) RETURN VARCHAR2 is

	relacionDatosVariables	VARCHAR2(20000) := '';
	consulta		varchar2(20000);

  lc VARCHAR2(50) := 'pq.datos_variables_riesgo';

  TYPE cur_typ IS REF CURSOR;
  c_resultado     cur_typ;

  aux_idcapitalasegurado   tb_capitales_asegurados.idcapitalasegurado%TYPE;
  aux_codmodulo            tb_cap_aseg_rel_modulo.codmodulo%TYPE;
  aux_codconceptoppalmod   tb_parcelas_coberturas.codconceptoppalmod%TYPE;
  aux_codriesgocubierto    tb_parcelas_coberturas.codriesgocubierto%TYPE;
  aux_codvalor             tb_parcelas_coberturas.codvalor%TYPE;

BEGIN

    pq_utl.log(lc, 'Dentro de getDatVarCoberturaParcela');
    pq_utl.log(lc, 'Valor de CODMODULO: '|| CODMODULOPARAM);

    /* Pet. 63497 ** MODIF TAM (06.04.20209 ** Inicio */
    /* Incluimos validación para recuperar los datos variables para las complementarias, ya que en la tabla tb_sc_c_fecha_fin_garantias
    /* no hay registros para el codmodulo 'CP' pero si existen para el 'P' de la principal */
    IF (CODMODULOPARAM = 'CP') THEN
       pq_utl.log(lc, 'Entramos a buscar los Datos Variables Cobertura Parela de las complementarias');

       consulta := 'select caseg.idcapitalasegurado, cmod.codmodulo, pcob.codconceptoppalmod, pcob.codriesgocubierto, pcob.codvalor
             from o02agpe0.tb_parcelas par,
                  o02agpe0.tb_capitales_asegurados caseg,
                  o02agpe0.tb_datos_var_parcela dvar,
                  o02agpe0.tb_cap_aseg_rel_modulo cmod,
                  o02agpe0.tb_polizas pol,
                  o02agpe0.tb_parcelas_coberturas pcob,
                  o02agpe0.tb_sc_c_fecha_fin_garantia fgarant
             where par.idparcela = caseg.idparcela
                   and caseg.idcapitalasegurado = dvar.idcapitalasegurado
                   and dvar.codconcepto = 134
                   and cmod.idcapitalasegurado = caseg.idcapitalasegurado
                   and pol.idpoliza = par.idpoliza
                   and pcob.idparcela = par.idparcela
                   and pcob.lineaseguroid = pol.lineaseguroid
                   and pcob.codmodulo = cmod.codmodulo
                   and fgarant.lineaseguroid = pol.lineaseguroid
                   and fgarant.codmodulo = ''P''
                   and fgarant.codconceptoppalmod = pcob.codconceptoppalmod
                   and fgarant.codriesgocubierto = pcob.codriesgocubierto
                   and fgarant.codcultivo = par.codcultivo
                   and fgarant.codvariedad = par.codvariedad
                   and fgarant.codtipocapital = caseg.codtipocapital
                   and TO_CHAR(fgarant.fgaranthasta, ''DD/MM/YYYY'') = dvar.valor
                   and pol.idpoliza = ' || IDPOLIZAPARAM || '
                   and cmod.codmodulo = ''' || CODMODULOPARAM || '''
             group by caseg.idcapitalasegurado, cmod.codmodulo, pcob.codconceptoppalmod, pcob.codriesgocubierto, pcob.codvalor';
    ELSE
       consulta := 'select caseg.idcapitalasegurado, cmod.codmodulo, pcob.codconceptoppalmod, pcob.codriesgocubierto, pcob.codvalor
             from o02agpe0.tb_parcelas par,
                  o02agpe0.tb_capitales_asegurados caseg,
                  o02agpe0.tb_datos_var_parcela dvar,
                  o02agpe0.tb_cap_aseg_rel_modulo cmod,
                  o02agpe0.tb_polizas pol,
                  o02agpe0.tb_parcelas_coberturas pcob,
                  o02agpe0.tb_sc_c_fecha_fin_garantia fgarant
             where par.idparcela = caseg.idparcela
                   and caseg.idcapitalasegurado = dvar.idcapitalasegurado
                   and dvar.codconcepto = 134
                   and cmod.idcapitalasegurado = caseg.idcapitalasegurado
                   and pol.idpoliza = par.idpoliza
                   and pcob.idparcela = par.idparcela
                   and pcob.lineaseguroid = pol.lineaseguroid
                   and pcob.codmodulo = cmod.codmodulo
                   and fgarant.lineaseguroid = pol.lineaseguroid
                   and fgarant.codmodulo = pcob.codmodulo
                   and fgarant.codconceptoppalmod = pcob.codconceptoppalmod
                   and fgarant.codriesgocubierto = pcob.codriesgocubierto
                   and fgarant.codcultivo = par.codcultivo
                   and fgarant.codvariedad = par.codvariedad
                   and fgarant.codtipocapital = caseg.codtipocapital
                   and TO_CHAR(fgarant.fgaranthasta, ''DD/MM/YYYY'') = dvar.valor
                   and pol.idpoliza = ' || IDPOLIZAPARAM || '
                   and cmod.codmodulo = ''' || CODMODULOPARAM || '''
             group by caseg.idcapitalasegurado, cmod.codmodulo, pcob.codconceptoppalmod, pcob.codriesgocubierto, pcob.codvalor';
    END IF;

    dbms_output.put_line(consulta);

    OPEN c_resultado FOR consulta;
    LOOP
        FETCH c_resultado INTO aux_idcapitalasegurado, aux_codmodulo, aux_codconceptoppalmod, aux_codriesgocubierto, aux_codvalor;
        EXIT WHEN c_resultado%NOTFOUND;
            relacionDatosVariables := relacionDatosVariables || aux_idcapitalasegurado || ':' || aux_codmodulo  || '#' || aux_codconceptoppalmod || '#' || aux_codriesgocubierto || '#' || aux_codvalor ||'|';
    END LOOP;

    relacionDatosVariables := relacionDatosVariables || '|';

    pq_utl.log(lc, 'getDatVarCoberturaParcela-relacionDatosVariables recuperados: ' || relacionDatosVariables);

	RETURN relacionDatosVariables;

END getDatVarCoberturaParcela;


-- *****************************************************************
-- Description: Función para obtener los datos variables de las parcelas de anexos de modificación que
-- dependen del riesgo cubierto y del concepto principal del modulo
--
-- Input Parameters: IDANEXOPARAM
--
-- Output Parameters: Cadena de texto con el siguiente formato:
--   CODCONCEPTO1:CAPITAL_ASEGURADO11#CPPAL11#RIESGO11#CAPITAL_ASEGURADO12#CPPAL12#RIESGO12..|CODCONCEPTO2...
--
-- Author:      T-Systems
--
-- Revision History
-- Date            Author       Reason for Change
-- ----------------------------------------------------------------
-- 2 MAR 2011      A. Serrano   Created.
-- *****************************************************************
FUNCTION getDatVarCobParcelaReport (IDPOLIZAPARAM IN NUMBER, CODMODULOPARAM IN VARCHAR2) RETURN VARCHAR2 is

	relacionDatosVariables	VARCHAR2(20000) := '';
	consulta		varchar2(20000);

  TYPE cur_typ IS REF CURSOR;
  c_resultado     cur_typ;

  aux_idcapitalasegurado   tb_capitales_asegurados.idcapitalasegurado%TYPE;
  aux_desriesgocubierto    tb_sc_c_riesgos_cubiertos.desriesgocubierto%TYPE;
  aux_codvalor             tb_parcelas_coberturas.codvalor%TYPE;

BEGIN

    consulta := 'select caseg.idcapitalasegurado, rcub.desriesgocubierto, pcob.codvalor
             from o02agpe0.tb_parcelas par,
                  o02agpe0.tb_capitales_asegurados caseg,
                  o02agpe0.tb_datos_var_parcela dvar,
                  o02agpe0.tb_cap_aseg_rel_modulo cmod,
                  o02agpe0.tb_polizas pol,
                  o02agpe0.tb_parcelas_coberturas pcob,
                  o02agpe0.tb_sc_c_fecha_fin_garantia fgarant,
                  o02agpe0.tb_sc_c_riesgos_cubiertos rcub
             where par.idparcela = caseg.idparcela
                   and caseg.idcapitalasegurado = dvar.idcapitalasegurado
                   and dvar.codconcepto = 134
                   and cmod.idcapitalasegurado = caseg.idcapitalasegurado
                   and pol.idpoliza = par.idpoliza
                   and pcob.idparcela = par.idparcela
                   and pcob.lineaseguroid = pol.lineaseguroid
                   and pcob.codmodulo = cmod.codmodulo
                   and fgarant.lineaseguroid = pol.lineaseguroid
                   and fgarant.codmodulo = pcob.codmodulo
                   and fgarant.codconceptoppalmod = pcob.codconceptoppalmod
                   and fgarant.codriesgocubierto = pcob.codriesgocubierto
                   and fgarant.codcultivo = par.codcultivo
                   and fgarant.codvariedad = par.codvariedad
                   and fgarant.codtipocapital = caseg.codtipocapital
                   and TO_CHAR(fgarant.fgaranthasta, ''DD/MM/YYYY'') = dvar.valor
                   and pol.idpoliza = ' || IDPOLIZAPARAM || '
                   and cmod.codmodulo = ''' || CODMODULOPARAM || '''
                   and rcub.lineaseguroid = pol.lineaseguroid
                   and rcub.codmodulo = cmod.codmodulo
                   and rcub.codriesgocubierto = pcob.codriesgocubierto
             group by caseg.idcapitalasegurado, rcub.desriesgocubierto, pcob.codvalor order by rcub.desriesgocubierto asc';

        dbms_output.put_line(consulta);

        OPEN c_resultado FOR consulta;
        LOOP
            FETCH c_resultado INTO aux_idcapitalasegurado, aux_desriesgocubierto, aux_codvalor;
            EXIT WHEN c_resultado%NOTFOUND;
            relacionDatosVariables := relacionDatosVariables || aux_idcapitalasegurado || ':' || aux_desriesgocubierto  || '#' || aux_codvalor  ||'|';
        END LOOP;
        relacionDatosVariables := relacionDatosVariables || '|';

	RETURN relacionDatosVariables;

END getDatVarCobParcelaReport;

---------------------------------------------------------------------------------------------------------------------------
	---------------------------------------------------------------------------------------------------------------------------
	-- FUNCTION print_log_completo
	--
	-- Escribe en el log la cadena pasa por parametro independientemente de la longitud de esta
	--
	---------------------------------------------------------------------------------------------------------------------------
	--
	-- Inicio de declaracion de funcion
	FUNCTION print_log_completo(str IN VARCHAR2) RETURN VARCHAR2 IS
		-- Variables
		str_aux VARCHAR2(10000); -- Auxiliar usada para pintar cadena en el log
	BEGIN

		str_aux := str;

		LOOP
			-- Si el tama?o de la cadena es mayor que el que permite el metodo de log, se parte y se escribe por trozos
			IF (str_aux IS NOT NULL AND length(str_aux) > 994) THEN
				pq_utl.log(null, substr(str_aux, 1, 994), 2);
				str_aux := substr(str_aux, 994);
				-- Si el tama?o de la cadena es menor, se pinta y se sale del bucle
			ELSE
				pq_utl.log(null, str_aux, 2);
				EXIT;
			END IF;
		END LOOP;

		RETURN 'OK';

		-- Control de excepciones
	EXCEPTION
		-- Si ocurre cualquier excepcion se pinta la cadena en el log directamente
		WHEN OTHERS THEN
			pq_utl.log(null, str, 2);
			RETURN 'KO';
	END;
	-- Fin del cuerpo de la funcion

END PQ_DATOS_VARIABLES_RIESGO;
/
SHOW ERRORS;