SET DEFINE OFF;
SET SERVEROUTPUT ON;

CREATE OR REPLACE PACKAGE O02AGPE0.pq_calcula_produccion IS

  -- declaracion de tipos
	TYPE tipo_capital IS TABLE OF NUMBER INDEX BY BINARY_INTEGER;
	TYPE t_varchar2 IS TABLE OF VARCHAR2(32767) INDEX BY BINARY_INTEGER;
	TYPE t_limites IS TABLE OF tb_sc_c_limites_rdtos%ROWTYPE INDEX BY BINARY_INTEGER;
	TYPE t_ren_car_expecificas IS TABLE OF tb_sc_c_masc_rdtos_caract_e%ROWTYPE INDEX BY BINARY_INTEGER;

	TYPE t_cod_valor IS RECORD(
		 cod   NUMBER
		,valor VARCHAR2(32767));

	TYPE t_array_cod_valor IS TABLE OF t_cod_valor INDEX BY BINARY_INTEGER;

	TYPE t_dat_variable IS TABLE OF t_cod_valor INDEX BY BINARY_INTEGER;

	TYPE t_capital_asegurado IS TABLE OF t_dat_variable INDEX BY BINARY_INTEGER;

	TYPE t_aseg_autorizados IS TABLE OF tb_sc_c_aseg_autorizados%ROWTYPE INDEX BY BINARY_INTEGER;

	TYPE t_campos_mascaras IS TABLE OF pq_calcula_produccion.t_cod_valor INDEX BY BINARY_INTEGER;

	TYPE t_rdtos_caract_esp IS TABLE OF tb_sc_c_rdtos_caract_esp%ROWTYPE INDEX BY BINARY_INTEGER;

	TYPE t_masc_lim_rendim IS TABLE OF tb_sc_c_masc_limites_rdtos%ROWTYPE INDEX BY BINARY_INTEGER;

  type t_array is TABLE OF VARCHAR2(50)INDEX BY BINARY_INTEGER;
  -- declaracion de funciones
  FUNCTION fn_asignarValoresMascCarEsp(p_mascaras t_ren_car_expecificas
                                          ,p_capitalesasegurados pq_calcula_produccion.t_capital_asegurado) return t_campos_mascaras;

  FUNCTION fn_asignarValoresMascLimRdto(p_mascaras t_masc_lim_rendim
                                          ,p_capitalesasegurados pq_calcula_produccion.t_capital_asegurado) return t_campos_mascaras;

	FUNCTION fn_buscalimredimientos(p_lineaseguroid   VARCHAR2
																 ,p_modulo          VARCHAR2
																 ,p_codcultivo      VARCHAR2
																 ,p_codvariedad     VARCHAR2
																 ,p_provincia       VARCHAR2
																 ,p_comarca         VARCHAR2
																 ,p_termino         VARCHAR2
																 ,p_subtermino      VARCHAR2
																 ,p_allcodcultivo   VARCHAR2 DEFAULT 'N'
																 ,p_allcodvariedad  VARCHAR2 DEFAULT 'N'
																 ,p_allprovincia    VARCHAR2 DEFAULT 'N'
																 ,p_allcomarca      VARCHAR2 DEFAULT 'N'
																 ,p_alltermino      VARCHAR2 DEFAULT 'N'
																 ,p_allsubtermino   VARCHAR2 DEFAULT 'N'
																 ,p_campos_mascaras t_campos_mascaras
																 ,p_tablardto       VARCHAR2) RETURN t_limites;

	FUNCTION fn_calculaproduccion(p_capitalesasegurados pq_calcula_produccion.t_capital_asegurado
															 ,p_apprdto             VARCHAR2
															 ,p_limrendmax          VARCHAR2
															 ,p_limrendmin          VARCHAR2
                               -- MPM --0912
                               -- Nuevo parametro % aplicable al rendimiento
                               ,p_pctaplrdto          tb_sc_c_limites_rdtos.pctaplrdto%TYPE
                               ) RETURN pq_calcula_produccion.t_varchar2;


	FUNCTION fn_cargaAseguradosAutorizados(p_lineaseguroid     VARCHAR2
                                      ,p_codlinea            VARCHAR2
																			,p_nif               VARCHAR2 DEFAULT NULL
																			,p_modulo            VARCHAR2 DEFAULT NULL
																			,p_fechafingarantias VARCHAR2 DEFAULT NULL
																			,p_garantizado       VARCHAR2 DEFAULT NULL
                                      ,p_concPpalMod       VARCHAR2 DEFAULT NULL
                                      ,p_riesgoCbrto       VARCHAR2 DEFAULT NULL
                                      ,p_valor             VARCHAR2 DEFAULT NULL
																			,p_codcultivo        VARCHAR2 DEFAULT NULL
																			,p_convariedad       VARCHAR2 DEFAULT NULL) RETURN pq_calcula_produccion.t_aseg_autorizados;

	FUNCTION fn_checkrendcaracesp(p_lineaseguroid VARCHAR2) RETURN BOOLEAN;

	FUNCTION fn_damelistarencaracesp(p_lineaseguroid VARCHAR2
																	,p_modulo        VARCHAR2
																	,p_codcultivo    VARCHAR2
																	,p_codvariedad   VARCHAR2
																	,p_provincia     VARCHAR2
																	,p_comarca       VARCHAR2
																	,p_termino       VARCHAR2
																	,p_subtermino    VARCHAR2
																	,p_filtromascara t_campos_mascaras) RETURN pq_calcula_produccion.t_rdtos_caract_esp;

	FUNCTION fn_existelimiterendbylinea(p_lineaseguroid VARCHAR2) RETURN BOOLEAN;

	FUNCTION fn_extraer_campo(entrada   VARCHAR2
													 ,campo     NUMBER DEFAULT 1
													 ,separador VARCHAR2 DEFAULT '#') RETURN VARCHAR2;

	FUNCTION fn_getaseguradosautorizados(p_lineaseguroid     VARCHAR2
																			,p_nif               VARCHAR2 DEFAULT NULL
																			,p_modulo            VARCHAR2 DEFAULT NULL
																			,p_fechafingarantias VARCHAR2 DEFAULT NULL
																			,p_garantizado       VARCHAR2 DEFAULT NULL
                                      ,p_concPpalMod       VARCHAR2 DEFAULT NULL
                                      ,p_riesgoCbrto       VARCHAR2 DEFAULT NULL
                                      ,p_valor             VARCHAR2 DEFAULT NULL
																			,p_codcultivo        VARCHAR2 DEFAULT NULL
																			,p_convariedad       VARCHAR2 DEFAULT NULL) RETURN pq_calcula_produccion.t_aseg_autorizados;

	FUNCTION fn_getcoefrdtomaxaseg(p_lineaseguroid VARCHAR2
																,p_nif           VARCHAR2) RETURN tb_sc_c_medidas%ROWTYPE;

	FUNCTION fn_getCriteriaRdtosCaracEsp(p_lineaseguroid         VARCHAR2
																			,p_codmodulo             VARCHAR2
																			,p_codcultivo            VARCHAR2
																			,p_codvariedad           VARCHAR2
																			,p_codprovincia          VARCHAR2
																			,p_codtermino            VARCHAR2
																			,p_subtermino            VARCHAR2
																			,p_codcomarca            VARCHAR2
																			,p_codsistemaconduccion  VARCHAR2
																			,p_codpracticacultural   VARCHAR2
																			,p_coddenominacionorigen VARCHAR2
																			,p_allcultivos           VARCHAR2 DEFAULT 'N'
																			,p_allvariedades         VARCHAR2 DEFAULT 'N'
																			,p_allprovincias         VARCHAR2 DEFAULT 'N'
																			,p_allterminos           VARCHAR2 DEFAULT 'N'
																			,p_allsubterminos        VARCHAR2 DEFAULT 'N'
																			,p_allcomarcas           VARCHAR2 DEFAULT 'N') RETURN pq_calcula_produccion.t_rdtos_caract_esp;

	FUNCTION fn_getCritMascRdtosCaracEsp(p_lineaseguroid   VARCHAR2
																		,p_codcultivoall   VARCHAR2 DEFAULT 'N'
																		,p_codcultivo      VARCHAR2
																		,p_codvariedadall  VARCHAR2 DEFAULT 'N'
																		,p_codvariedad     VARCHAR2
																		,p_codprovinciaall VARCHAR2 DEFAULT 'N'
																		,p_codprovincia    VARCHAR2
																		,p_codterminoall   VARCHAR2 DEFAULT 'N'
																		,p_codtermino      VARCHAR2
																		,p_subterminoall   VARCHAR2 DEFAULT 'N'
																		,p_subtermino      VARCHAR2
																		,p_codcomarcaall   VARCHAR2 DEFAULT 'N'
																		,p_codcomarca      VARCHAR2
																		,p_modulos         VARCHAR2) RETURN t_ren_car_expecificas;

	FUNCTION fn_getFechaFinGarantias(p_capitalesasegurados pq_calcula_produccion.t_capital_asegurado) RETURN VARCHAR2;

	FUNCTION fn_filtromaslimrendimiento(p_lineaseguroid  VARCHAR2
																		 ,p_modulo         VARCHAR2
																		 ,p_codcultivo     VARCHAR2
																		 ,p_codvariedad    VARCHAR2
																		 ,p_provincia      VARCHAR2
																		 ,p_comarca        VARCHAR2
																		 ,p_termino        VARCHAR2
																		 ,p_subtermino     VARCHAR2
																		 ,p_allcodcultivo  VARCHAR2 DEFAULT 'N'
																		 ,p_allcodvariedad VARCHAR2 DEFAULT 'N'
																		 ,p_allprovincia   VARCHAR2 DEFAULT 'N'
																		 ,p_allcomarca     VARCHAR2 DEFAULT 'N'
																		 ,p_alltermino     VARCHAR2 DEFAULT 'N'
																		 ,p_allsubtermino  VARCHAR2 DEFAULT 'N') RETURN t_masc_lim_rendim;

	FUNCTION fn_getgarantizado(p_capitalesasegurados pq_calcula_produccion.t_capital_asegurado) RETURN VARCHAR2;

	FUNCTION fn_getlimitesrendimiento(p_lineaseguroid       VARCHAR2
																	 ,p_capitalesasegurados pq_calcula_produccion.t_capital_asegurado
																	 ,p_modulo              VARCHAR2
																	 ,p_codcultivo          VARCHAR2
																	 ,p_codvariedad         VARCHAR2
																	 ,p_provincia           VARCHAR2
																	 ,p_comarca             VARCHAR2
																	 ,p_termino             VARCHAR2
																	 ,p_subtermino          VARCHAR2
																	 ,p_tablardto           VARCHAR2
                                   ,p_rdtoPermitido        tb_sc_c_aseg_autorizados.rdtopermitido%TYPE
                                   ,p_esAsegBonus         BOOLEAN
                                   ,p_codlinea            VARCHAR2
                                   ,v_312_superficie     IN OUT BOOLEAN) RETURN pq_calcula_produccion.t_varchar2;

	FUNCTION fn_getlimredimientos(p_lineaseguroid   VARCHAR2
															 ,p_modulo          VARCHAR2
															 ,p_codcultivo      VARCHAR2
															 ,p_codvariedad     VARCHAR2
															 ,p_provincia       VARCHAR2
															 ,p_comarca         VARCHAR2
															 ,p_termino         VARCHAR2
															 ,p_subtermino      VARCHAR2
															 ,p_campos_mascaras t_campos_mascaras
															 ,p_tablardto       VARCHAR2
                               ) RETURN t_limites;

	FUNCTION fn_getMascLimitesRendimiento(p_lineaseguroid VARCHAR2
																				 ,p_modulo        VARCHAR2
																				 ,p_codcultivo    VARCHAR2
																				 ,p_codvariedad   VARCHAR2
																				 ,p_provincia     VARCHAR2
																				 ,p_comarca       VARCHAR2
																				 ,p_termino       VARCHAR2
																				 ,p_subtermino    VARCHAR2) RETURN t_masc_lim_rendim;

	FUNCTION fn_getMascaraRdtosCaracEsp(p_lineaseguroid VARCHAR2
																		 ,p_modulo        VARCHAR2
																		 ,p_codcultivo    VARCHAR2
																		 ,p_codvariedad   VARCHAR2
																		 ,p_provincia     VARCHAR2
																		 ,p_comarca       VARCHAR2
																		 ,p_termino       VARCHAR2
																		 ,p_subtermino    VARCHAR2) RETURN t_ren_car_expecificas;

	FUNCTION fn_getmedidasaplicablemodulo(p_lineaseguroid VARCHAR
																			 ,p_codmodulo     VARCHAR2) RETURN NUMBER;

	FUNCTION fn_getorganizadorinformacion(p_lineaseguroid VARCHAR2
																			 --,p_codubicacion  tb_sc_oi_org_info.codubicacion%TYPE
																			 ,p_codconcepto   tb_sc_oi_org_info.codconcepto%TYPE
																			 ,p_coduso        tb_sc_oi_org_info.coduso%TYPE) RETURN NUMBER;

	FUNCTION fn_porcreducrendvariables(p_lineaseguroid       VARCHAR2
																		,p_capitalesasegurados pq_calcula_produccion.t_capital_asegurado
																		,p_modulo              VARCHAR2
																		,p_provincia           VARCHAR2
																		,p_comarca             VARCHAR2
																		,p_termino             VARCHAR2
																		,p_subtermino          VARCHAR2) RETURN NUMBER;

	FUNCTION fn_getproducciones(p_lineaseguroid       VARCHAR2
                             ,p_idpoliza            VARCHAR2
														 ,p_capitalesasegurados VARCHAR2
														 ,p_nif                 VARCHAR2
														 ,p_modulo              VARCHAR2
														 ,p_codcultivo          VARCHAR2
														 ,p_codvariedad         VARCHAR2
														 ,p_provincia           VARCHAR2
														 ,p_comarca             VARCHAR2
														 ,p_termino             VARCHAR2
														 ,p_subtermino          VARCHAR2
                             ,p_sigpac              VARCHAR2) RETURN VARCHAR2;

  FUNCTION fn_getSistemaCultivo(p_capitalesasegurados pq_calcula_produccion.t_capital_asegurado) RETURN VARCHAR2;

	FUNCTION fn_valorpordefectofitros(p_campos_mascara pq_calcula_produccion.t_campos_mascaras) RETURN pq_calcula_produccion.t_campos_mascaras;

	FUNCTION fn_where(p_codcultivoall   VARCHAR2 DEFAULT 'N'
									 ,p_codcultivo      VARCHAR2
									 ,p_codvariedadall  VARCHAR2 DEFAULT 'N'
									 ,p_codvariedad     VARCHAR2
									 ,p_codprovinciaall VARCHAR2 DEFAULT 'N'
									 ,p_codprovincia    VARCHAR2
									 ,p_codterminoall   VARCHAR2 DEFAULT 'N'
									 ,p_codtermino      VARCHAR2
									 ,p_subterminoall   VARCHAR2 DEFAULT 'N'
									 ,p_subtermino      VARCHAR2
									 ,p_codcomarcaall   VARCHAR2 DEFAULT 'N'
									 ,p_codcomarca      VARCHAR2) RETURN VARCHAR2;

	-- declaracion de procedimientos
  PROCEDURE p(p VARCHAR2);

	PROCEDURE pr_get_producciones(p_lineaseguroid       VARCHAR2
                               ,p_idpoliza            VARCHAR2
															 --,p_codconcepto         tb_sc_oi_org_info.codconcepto%TYPE DEFAULT 175 -- Garantizado
															 ,p_capitalesasegurados pq_calcula_produccion.t_capital_asegurado
															 ,p_nif                 VARCHAR2
															 ,p_modulo              VARCHAR2
															 ,p_codcultivo          VARCHAR2
															 ,p_codvariedad         VARCHAR2
															 ,p_codlinea            VARCHAR2
															 ,p_provincia             VARCHAR2
															 ,p_comarca             VARCHAR2
															 ,p_termino             VARCHAR2
															 ,p_subtermino          VARCHAR2
															 ,p_produccion          OUT pq_calcula_produccion.t_varchar2);

	PROCEDURE pr_prueba;

  -- MPM - ENE13
  -- Devuelve un boolean indicando si los parametros indicados existen en la tabla TB_SC_C_ZONIF_APLIC_MEDIDAS
  FUNCTION fn_getAplicaCoefRdtos (p_lineaseguroid     VARCHAR2,
                                  p_provincia         VARCHAR2,
                                  p_comarca           VARCHAR2,
                                  p_termino           VARCHAR2,
															    p_subtermino        VARCHAR2,
                                  p_codcultivo        VARCHAR2,
															    p_codvariedad       VARCHAR2) RETURN BOOLEAN;



  -- MPM - 201311
  -- Devuelve el rendimiento maximo de la parcela indicada por los parametros si existe en la tabla
  -- de rendimientos registros parcela
  FUNCTION fn_getRendimientosRegParcela (p_lineaseguroid IN VARCHAR2,
                                         p_idpoliza IN VARCHAR2,
                                         p_modulo IN VARCHAR2,
                                         p_cultivo IN VARCHAR2,
                                         p_variedad IN VARCHAR2,
                                         p_sigpac IN VARCHAR2,
                                         v_capitales  pq_calcula_produccion.t_capital_asegurado) RETURN VARCHAR2;


  FUNCTION SPLIT(in_string VARCHAR2, delim VARCHAR2) RETURN t_array;

  /*PROCEDURE fn_getVarSIGPAC (p_sigpac IN VARCHAR2,
                            v_prov_SIGPAC OUT o02agpe0.tb_parcelas.codprovsigpac%TYPE,
                            v_term_SIGPAC OUT o02agpe0.tb_parcelas.codtermsigpac%TYPE,
                            v_agre_SIGPAC OUT o02agpe0.tb_parcelas.agrsigpac%TYPE,
                            v_zona_SIGPAC OUT o02agpe0.tb_parcelas.zonasigpac%TYPE,
                            v_polg_SIGPAC OUT o02agpe0.tb_parcelas.poligonosigpac%TYPE,
                            v_parc_SIGPAC OUT o02agpe0.tb_parcelas.parcelasigpac%TYPE);    */


END pq_calcula_produccion;
/
CREATE OR REPLACE PACKAGE BODY O02AGPE0.pq_calcula_produccion IS

  pck CONSTANT VARCHAR2(30) := 'PQ_CALCULA_PRODUCCION';

  --Funcion para rellenar los valores para los campos de mascaras
  -- de rendimientos de caracteristicas especificas
  FUNCTION fn_asignarValoresMascCarEsp(p_mascaras t_ren_car_expecificas
                                      ,p_capitalesasegurados pq_calcula_produccion.t_capital_asegurado)
                                      return t_campos_mascaras IS

      v_campos_mascaras t_campos_mascaras;
      v_campo_asociado  tb_campos_mascara.codconceptoasoc%TYPE;
      v_ind             NUMBER := 1;

  BEGIN
      --Recorremos los campos de mascara obtenidos y buscamos los valores en los datos variables.
			FOR a IN 1 .. p_mascaras.COUNT LOOP
				IF p_mascaras(a).codconcepto IS NOT NULL THEN
          --consultamos la tabla de campos de mascara por si un codigo de concepto esta asociado
          --con algun otro (le paso la tabla de mascara y el codigo de concepto)
          begin
              select cm.codconceptoasoc into v_campo_asociado from tb_campos_mascara cm
              where cm.codtablacondicionado = 9137
              and cm.codconceptomasc = p_mascaras(a).codconcepto;
              dbms_output.put_line('Codigo de concepto asociado ' || v_campo_asociado);
          exception
              when others then
                  v_campo_asociado := null;
          end;

					FOR i IN 1 .. p_capitalesasegurados(1).COUNT LOOP
            if (v_campo_asociado is null) then
    						IF p_capitalesasegurados(1) (i).cod = p_mascaras(a).codconcepto THEN
    							v_campos_mascaras(v_ind).cod := p_capitalesasegurados(1) (i).cod;
    							v_campos_mascaras(v_ind).valor := p_capitalesasegurados(1) (i).valor;
                  v_ind := v_ind + 1;
                  exit;
    						END IF;
            else
                IF p_capitalesasegurados(1) (i).cod = v_campo_asociado THEN
                  /*dbms_output.put_line('Codigo de concepto origen ' || v_mascaras_limit_ren(a).codconcepto);
                  dbms_output.put_line('Valor de concepto asociado ' || p_capitalesasegurados(1) (i).valor);
                  dbms_output.put_line('Indice asociado ' || v_ind);*/
    							v_campos_mascaras(v_ind).cod := p_mascaras(a).codconcepto;
    							v_campos_mascaras(v_ind).valor := p_capitalesasegurados(1) (i).valor;
                  v_ind := v_ind + 1;
                  exit;
    						END IF;
            end if;
					END LOOP;
				END IF;
			END LOOP;

      return v_campos_mascaras;

  END fn_asignarValoresMascCarEsp;

  --Funcion para rellenar los valores para los campos de mascaras
  -- de rendimientos de limites de rendimientos
  FUNCTION fn_asignarValoresMascLimRdto(p_mascaras t_masc_lim_rendim
                                       ,p_capitalesasegurados pq_calcula_produccion.t_capital_asegurado)
                                       return t_campos_mascaras IS

      v_campos_mascaras t_campos_mascaras;
      v_campo_asociado  tb_campos_mascara.codconceptoasoc%TYPE;
      v_ind             NUMBER := 1;

  BEGIN
      --Recorremos los campos de mascara obtenidos y buscamos los valores en los datos variables.
			FOR a IN 1 .. p_mascaras.COUNT LOOP
				IF p_mascaras(a).codconcepto IS NOT NULL THEN
          --consultamos la tabla de campos de mascara por si un codigo de concepto esta asociado
          --con algun otro (le paso la tabla de mascara y el codigo de concepto)
          begin
              select cm.codconceptoasoc into v_campo_asociado from tb_campos_mascara cm
              where cm.codtablacondicionado = 9009
              and cm.codconceptomasc = p_mascaras(a).codconcepto;
              dbms_output.put_line('Codigo de concepto asociado ' || v_campo_asociado);
          exception
              when others then
                  v_campo_asociado := null;
          end;

					FOR i IN 1 .. p_capitalesasegurados(1).COUNT LOOP
            if (v_campo_asociado is null) then
    						IF p_capitalesasegurados(1) (i).cod = p_mascaras(a).codconcepto THEN
    							v_campos_mascaras(v_ind).cod := p_capitalesasegurados(1) (i).cod;
    							v_campos_mascaras(v_ind).valor := p_capitalesasegurados(1) (i).valor;
                  v_ind := v_ind + 1;
                  exit;
    						END IF;
            else
                IF p_capitalesasegurados(1) (i).cod = v_campo_asociado THEN
                  /*dbms_output.put_line('Codigo de concepto origen ' || v_mascaras_limit_ren(a).codconcepto);
                  dbms_output.put_line('Valor de concepto asociado ' || p_capitalesasegurados(1) (i).valor);
                  dbms_output.put_line('Indice asociado ' || v_ind);*/
    							v_campos_mascaras(v_ind).cod := p_mascaras(a).codconcepto;
    							v_campos_mascaras(v_ind).valor := p_capitalesasegurados(1) (i).valor;
                  v_ind := v_ind + 1;
                  exit;
    						END IF;
            end if;
					END LOOP;
				END IF;
			END LOOP;

      return v_campos_mascaras;

  END fn_asignarValoresMascLimRdto;

	PROCEDURE p(p VARCHAR2) IS
	BEGIN
		dbms_output.put_line(p);
	END p;



	FUNCTION fn_getproducciones(p_lineaseguroid       VARCHAR2
                             ,p_idpoliza            VARCHAR2
														 ,p_capitalesasegurados VARCHAR2
														 ,p_nif                 VARCHAR2
														 ,p_modulo              VARCHAR2
														 ,p_codcultivo          VARCHAR2
														 ,p_codvariedad         VARCHAR2
														 ,p_provincia           VARCHAR2
														 ,p_comarca             VARCHAR2
														 ,p_termino             VARCHAR2
														 ,p_subtermino          VARCHAR2
                             -- MPM - 201310
                             ,p_sigpac              VARCHAR2) RETURN VARCHAR2 IS

		v_produccion pq_calcula_produccion.t_varchar2;
		v_capitales  pq_calcula_produccion.t_capital_asegurado;
		v_ind        NUMBER := 1;
		v_valor      VARCHAR2(32767) := NULL;
    v_SistCult   VARCHAR2(32767) := NULL;
    --
    v_plan       o02agpe0.tb_lineas.codplan%TYPE;
	v_linea      o02agpe0.tb_lineas.codlinea%TYPE;
    v_ret        VARCHAR2(100);


	BEGIN

    execute immediate 'ALTER SESSION SET NLS_NUMERIC_CHARACTERS = ".,"';

		dbms_output.put_line(p_capitalesasegurados);

		WHILE pq_calcula_produccion.fn_extraer_campo(p_capitalesasegurados,
																								 v_ind,
																								 '#') IS NOT NULL LOOP
			v_valor := pq_calcula_produccion.fn_extraer_campo(p_capitalesasegurados,
																												v_ind,
																												'#');

			v_capitales(1)(v_ind).cod := substr(v_valor,
																					1,
																					instr(v_valor,
																								'|') - 1);
			v_capitales(1)(v_ind).valor := substr(v_valor,
																						instr(v_valor,
																									'|') + 1);
			v_ind := v_ind + 1;
		END LOOP;

    v_SistCult := pq_calcula_produccion.fn_getSistemaCultivo(v_capitales);
	
	BEGIN
		SELECT l.codplan, l.codlinea INTO v_plan, v_linea FROM o02agpe0.tb_lineas l where l.lineaseguroid=p_lineaseguroid;
    EXCEPTION WHEN OTHERS THEN
		v_plan := 0;
		v_linea := 0;
    END;

    -- Si la linea es la 309 y el sistema de cultivo es regadio (2)
    -- el rendimiento es libre y no hay que consultar las tablas.
    IF (v_SistCult = '2' AND v_linea = 309) THEN
        RETURN '0##0#';
    END IF;

    -- MPM - 201310
    -- Para la línea 300 a partir del plan 2013
    IF (v_linea = 300 AND v_plan >= 2013) THEN
          -- Se comprueba si la parcela tiene asignado un rendimiento máximo
          v_ret := fn_getRendimientosRegParcela (p_lineaseguroid => p_lineaseguroid,
                                                p_idpoliza => p_idpoliza,
                                                p_modulo => p_modulo,
                                                p_cultivo => p_codcultivo,
                                                p_variedad => p_codvariedad,
                                                p_sigpac => p_sigpac,
                                                v_capitales => v_capitales);

          -- Si lo tiene asignado se devuelve este valor, si no se continua con el calculo
          IF (v_ret IS NOT NULL) THEN
             RETURN v_ret;
          END IF;
    END IF;


    pq_calcula_produccion.pr_get_producciones(p_lineaseguroid   => p_lineaseguroid,
                                            p_idpoliza            => p_idpoliza,
																					--p_codconcepto         => 175,
																					p_capitalesasegurados => v_capitales,
																					p_nif                 => p_nif,
																					p_modulo              => p_modulo,
																					p_codcultivo          => p_codcultivo,
																					p_codvariedad         => p_codvariedad,
																					p_codlinea            => v_linea,
																					p_provincia           => p_provincia,
																					p_comarca             => p_comarca,
																					p_termino             => p_termino,
																					p_subtermino          => p_subtermino,
																					p_produccion          => v_produccion);



    dbms_output.put_line('Producción ' || v_produccion(1) || ' ' || v_produccion(2));
    dbms_output.put_line('Rendimiento ' || v_produccion(3) || ' ' || v_produccion(4));

    RETURN v_produccion(1) || '#' || v_produccion(2) || '#' || v_produccion(3) || '#' || nvl(v_produccion(4), ' ');


	END fn_getproducciones;




	PROCEDURE pr_get_producciones(p_lineaseguroid       VARCHAR2
                               ,p_idpoliza            VARCHAR2
															 --,p_codconcepto         tb_sc_oi_org_info.codconcepto%TYPE DEFAULT 175 -- Garantizado
															 ,p_capitalesasegurados pq_calcula_produccion.t_capital_asegurado
															 ,p_nif                 VARCHAR2
															 ,p_modulo              VARCHAR2
															 ,p_codcultivo          VARCHAR2
															 ,p_codvariedad         VARCHAR2
															 ,p_codlinea            VARCHAR2
															 ,p_provincia             VARCHAR2
															 ,p_comarca             VARCHAR2
															 ,p_termino             VARCHAR2
															 ,p_subtermino          VARCHAR2
															 ,p_produccion          OUT pq_calcula_produccion.t_varchar2) IS


		v_countorganizador             NUMBER := 0;
		v_garantizado                  VARCHAR2(32767) := NULL;
		v_fechafingarantias            VARCHAR2(32767) := NULL;
    v_concPpalMod                  VARCHAR2(32767) := NULL;
    v_riesgoCbrto                  VARCHAR2(32767) := NULL;
    v_valor                        VARCHAR2(32767) := NULL;

    v_comparativa363   tb_comparativas_poliza%ROWTYPE;

		v_coduso                       tb_sc_oi_org_info.coduso%TYPE := 31; -- Uso poliza
		--v_codubicacion                 tb_sc_oi_org_info.codubicacion%TYPE := 16; -- Datos variables.
    --v_codconcepto                  tb_sc_oi_org_info.codconcepto%TYPE := 175;

		v_aseg_autorizados             pq_calcula_produccion.t_aseg_autorizados;
		v_medida                       tb_sc_c_medidas%ROWTYPE;
		v_tablardto                    tb_sc_c_medidas.tablardto%TYPE := 0;
		v_countmedidasaplicablesmodulo NUMBER := 0;
		v_produccion                   pq_calcula_produccion.t_varchar2;

		v_prodxsuperficie    NUMBER := 0;
		v_prodxunidades      NUMBER := 0;
		v_prodxsuperficiemax NUMBER := 0;
		v_prodxunidadesmax   NUMBER := 0;

		v_sprod                  VARCHAR2(32767) := '';
		v_sprodmax               VARCHAR2(32767) := '';
		v_unidades               NUMBER := 0;
		v_superficie             NUMBER := 0;
		v_coefrdtoasegp          NUMBER := 1;
		v_porcreducrendvariables NUMBER := 0;

    -- MPM -0912
    v_es314              BOOLEAN;
    v_produccion_aux                   pq_calcula_produccion.t_varchar2;
    v_esAsegBonus              BOOLEAN := FALSE;

    -- MPM - ENE13
    v_linsegid_2012300     NUMBER(15) := 0;
    v_aplic_coef_300       BOOLEAN := FALSE;
    -- Si el rdto permitido no cambia de valor -1, indica que no hay aac o que este no tiene informado el campo correspondiente
    v_rdtopermitido_aac    NUMBER := -1;
    v_limsup_limrdtos      NUMBER := -1;
    -- Indica si la linea es 312 y la aplicacion del rendimiento es por superficie
    v_312_superficie       BOOLEAN := FALSE;


	BEGIN

      v_countorganizador := pq_calcula_produccion.fn_getorganizadorinformacion(p_lineaseguroid,
																																						   --v_codubicacion,
																																						   175,
																																						   v_coduso);



      --1.- Comprobamos si hay que filtrar asegurados Autorizados por Garantizado, F.F.Garantias o RCubElegido
		  IF v_countorganizador > 0 THEN
			    --Buscamos el valor del garantizado dentro de los datos variables de las pacelas por cada
			    -- capital asegurado.
			    v_garantizado := pq_calcula_produccion.fn_getgarantizado(p_capitalesasegurados => p_capitalesasegurados);
		  ELSE
          v_countorganizador := pq_calcula_produccion.fn_getorganizadorinformacion(p_lineaseguroid,
																																						       --v_codubicacion,
																																						       134,
																																						       v_coduso);
          IF v_countorganizador > 0 THEN
              --Buscamos el valor para fecha de fin de garantias dentro de los datos variables de las pacelas
			        v_fechafingarantias := pq_calcula_produccion.fn_getFechaFinGarantias(p_capitalesasegurados => p_capitalesasegurados);
          ELSE
              v_countorganizador := pq_calcula_produccion.fn_getorganizadorinformacion(p_lineaseguroid,
																																						           --v_codubicacion,
																																						           363,
																																						           v_coduso);
              IF v_countorganizador > 0 THEN
                  --Buscamos el valor para riesgo cubierto elegido en la comparativa
                  begin
                      SELECT * INTO v_comparativa363 FROM tb_comparativas_poliza cp
                      WHERE cp.codconcepto = 363 AND cp.idpoliza = p_idpoliza and cp.codmodulo = p_modulo;

                      IF (v_comparativa363.codconceptoppalmod is not null) THEN
                          v_concPpalMod := v_comparativa363.codconceptoppalmod;
                          v_riesgoCbrto := v_comparativa363.codriesgocubierto;
                          v_valor       := v_comparativa363.codvalor;
                      END IF;
                  exception
                      when others then
                          v_concPpalMod := null;
                          v_riesgoCbrto := null;
                          v_valor := null;
                  end;

              END IF;
		      END IF;
      END IF;

    -- Sacamos los asegurados autorizados.
    -- MPM - ENE13
    -- Para todas las lineas
    -- IF (p_codlinea <> 300) THEN
    --
    -- -------------------------------------------------- --
    -- PUNTO 2 - ASEGURADOS AUTORIZADOS A LA CONTRATACION --
    -- -------------------------------------------------- --
    v_aseg_autorizados := pq_calcula_produccion.fn_cargaAseguradosAutorizados(p_lineaseguroid     => p_lineaseguroid,
                                                                          p_codlinea          => p_codlinea,
																																			p_nif               => p_nif,
																																			p_modulo            => p_modulo,
																																			p_fechafingarantias => v_fechafingarantias,
																																			p_garantizado       => v_garantizado,
                                                                          p_concPpalMod       => v_concPpalMod,
                                                                          p_riesgoCbrto       => v_riesgoCbrto,
                                                                          p_valor             => v_valor,
																																			p_codcultivo        => p_codcultivo,
																																			p_convariedad       => NULL);
    -- END IF;

    --Buscamos en medidas aplicables modulo
		v_countmedidasaplicablesmodulo := pq_calcula_produccion.fn_getmedidasaplicablemodulo(p_lineaseguroid => p_lineaseguroid,
																																												 p_codmodulo     => p_modulo);

    -- ----------------- --
    -- PUNTO 2 - MEDIDAS --
    -- ----------------- --
    IF v_countmedidasaplicablesmodulo <> 0 THEN
       --Si aplica la medida para este modulo, obtenemos la medida
		    v_medida := pq_calcula_produccion.fn_getcoefrdtomaxaseg(p_lineaseguroid => p_lineaseguroid,
																													 	    p_nif           => p_nif);
    end if;

		IF (v_medida.tablardto IS NOT NULL) THEN
			v_tablardto := v_medida.tablardto;
		END IF;

    -- MPM - 0912
    -- Se comprueba si el asegurado es bonus
    IF (v_medida.tipomedidaclub IS NOT NULL AND v_medida.tipomedidaclub = 1) THEN
			v_esAsegBonus := true;
		END IF;
    -- fin MPM --

    -- MPM - ENE13
    -- Si es la linea 300, aunque exista aac se calcula los limites de rendimiento para la posterior comprobacion
    -- con el rendimiento permitido del aac
		IF (v_aseg_autorizados.EXISTS(1) = FALSE OR p_codlinea=300) THEN
			IF pq_calcula_produccion.fn_existelimiterendbylinea(p_lineaseguroid => p_lineaseguroid) THEN
				v_produccion := fn_getlimitesrendimiento(p_lineaseguroid       => p_lineaseguroid,
																								 p_capitalesasegurados => p_capitalesasegurados,
																								 p_modulo              => p_modulo,
																								 p_codcultivo          => p_codcultivo,
																								 p_codvariedad         => p_codvariedad,
																								 p_provincia           => p_provincia,
																								 p_comarca             => p_comarca,
																								 p_termino             => p_termino,
																								 p_subtermino          => p_subtermino,
																								 p_tablardto           => v_tablardto,
                                                 -- MPM - 0912
                                                 -- Se pasa nulo ya que al no existir AAC no tiene rendimiento permitido
                                                 p_rdtoPermitido        => NULL,
                                                 -- Se indica si el asegurado es bonus o no
                                                 p_esAsegBonus          => v_esAsegBonus,
                                                 -- MPM - ENE13
                                                 p_codlinea             => p_codlinea,
                                                 v_312_superficie       => v_312_superficie);
        -- MPM - ENE13
        -- Si en la primera posicion del array viene un -3, el n? de unidades es obligatorio y no se ha informado,
        -- por lo que no se sigue calculando y se muestra un aviso
        IF (v_produccion(1) IS NOT NULL AND v_produccion(1) = -3) THEN
           p_produccion := v_produccion;
           RETURN;
        END IF;
        --

        IF v_countmedidasaplicablesmodulo <> 0 THEN
					v_coefrdtoasegp := v_medida.coefrdtomaxaseg;
				END IF;
			ELSE
				   v_produccion(1) := '1';
				   v_produccion(2) := '';
           v_produccion(3) := '1';
				   v_produccion(4) := '';
			END IF;

      -- MPM - ENE13
      -- Se guarda el limite superior de limites de rendimiento
      v_limsup_limrdtos:= v_produccion(2);

    END IF;


    -- MPM - ENE13
    -- Sacamos los asegurados autorizados de la linea 300 solo si no hemos obtenido limite superior
    /*IF (p_codlinea = 300 AND (v_produccion(2) is null OR v_produccion(2) = '0')) THEN
        v_aseg_autorizados := pq_calcula_produccion.fn_cargaAseguradosAutorizados(
                                                           p_lineaseguroid     => p_lineaseguroid,
                                                           p_codlinea          => p_codlinea,
																													 p_nif               => p_nif,
																													 p_modulo            => p_modulo,
																													 p_fechafingarantias => v_fechafingarantias,
																													 p_garantizado       => v_garantizado,
                                                           p_concPpalMod       => v_concPpalMod,
                                                           p_riesgoCbrto       => v_riesgoCbrto,
                                                           p_valor             => v_valor,
																													 p_codcultivo        => p_codcultivo,
																													 p_convariedad       => NULL);
    END IF;*/

    -- MPM
    v_es314 := FALSE;

		IF (v_aseg_autorizados.EXISTS(1) = TRUE) THEN
			--      v_aseg_autorizados
			-- el asegurado tiene rendimiento fijo, se mide en Kg/Ha.
			--String sProd = calcularProduccionFija(aseg.getRdtopermitido().longValue(), datVarPar);

			--calculo prod x superficie/unidades
			IF v_aseg_autorizados(1).rdtopermitido IS NOT NULL OR
				 v_aseg_autorizados(1).rdtopermitido <> 0 OR
				 v_aseg_autorizados(1).rdtopermitido <> '' THEN

				FOR ind IN 1 .. p_capitalesasegurados.COUNT LOOP
					FOR ind2 IN 1 .. p_capitalesasegurados(ind).COUNT LOOP
						IF (p_codlinea = 314) AND
							 p_capitalesasegurados(ind) (ind2).cod = 117 THEN

							v_unidades         := p_capitalesasegurados(ind) (ind2).valor;
							v_prodxunidades    := v_unidades * v_aseg_autorizados(1).rdtopermitido;
							v_prodxunidadesmax := v_prodxunidades;
							v_prodxunidades    := v_prodxunidades * 0.5;
							v_sprod            := v_prodxunidades;
							v_sprodmax         := v_prodxunidadesmax;

              -- MPM - 0912
              -- Si hay medidas aplicables para el modulo, se carga el coeficiente a aplicar sobre el rendimiento permitido
              IF v_countmedidasaplicablesmodulo <> 0 THEN
					       v_coefrdtoasegp := v_medida.coefrdtomaxaseg;
				      END IF;

              -- Indica que la linea tratada es la 314  y ya se ha hecho el calculo de la produccion
              v_es314 := TRUE;
              -- fin MPM --

							EXIT;
						ELSIF p_codlinea != 314 AND	p_capitalesasegurados(ind) (ind2).cod = 258 THEN
							v_superficie         := p_capitalesasegurados(ind) (ind2).valor;
							v_prodxsuperficie    := v_superficie * v_aseg_autorizados(1).rdtopermitido;
							v_prodxsuperficiemax := v_prodxsuperficie;
							v_prodxsuperficie    := v_prodxsuperficie * 0.5;
							v_sprod              := v_prodxsuperficie;
							v_sprodmax           := v_prodxsuperficiemax;
						END IF;
					END LOOP;
				END LOOP;

        -- Obtiene la produccion
				v_produccion(1) := v_sprod;
				v_produccion(2) := v_sprodmax;
				v_produccion(3) := v_aseg_autorizados(1).rdtopermitido * 0.5;
				v_produccion(4) := v_aseg_autorizados(1).rdtopermitido;

        -- MPM - ENE13
        -- Se guarda el rendimiento permitido asociado al asegurado para usarlo posteriormente si la linea es la 300
        v_rdtopermitido_aac := v_produccion(2);

        -- MPM
        IF (v_es314) THEN
           -- Se va al paso 3, a obtener los limites de rendimiento
           v_produccion_aux := fn_getlimitesrendimiento(p_lineaseguroid       => p_lineaseguroid,
																								 p_capitalesasegurados => p_capitalesasegurados,
																								 p_modulo              => p_modulo,
																								 p_codcultivo          => p_codcultivo,
																								 p_codvariedad         => p_codvariedad,
																								 p_provincia           => p_provincia,
																								 p_comarca             => p_comarca,
																								 p_termino             => p_termino,
																								 p_subtermino          => p_subtermino,
																								 p_tablardto           => v_tablardto,
                                                 p_rdtoPermitido       => v_aseg_autorizados(1).rdtopermitido,
                                                 -- Se indica si el asegurado es bonus o no
                                                 p_esAsegBonus         => v_esAsegBonus,
                                                 -- MPM - ENE13
                                                 p_codlinea             => p_codlinea,
                                                 v_312_superficie       => v_312_superficie);
           -- MPM - ENE13
           -- Aqui no hay que controlar el -3 del n? de unidades porque solo es para la linea 300

           -- Si la produccion minina devuelta es nula, indica que hay que mantener la produccion calculada en el paso anterior
           IF (v_produccion_aux(1) IS NOT NULL) THEN
              -- Si no es nula y viene como -2, indica que hay que aplicar el % aplicable al rendimiento a lo
              -- calculado anteriormente
              IF (v_produccion_aux(1) = -2) THEN
                 -- El % viene en la segunda posicion del array de produccion
                 v_produccion(2) := v_produccion(2) * v_produccion_aux(2);
                 v_produccion(4) := v_produccion(4) * v_produccion_aux(2);
                 -- El limite inferior es el 50% del superior
                 v_produccion(1) := v_produccion(2) * 0.5;
                 v_produccion(3) := v_produccion(4) * 0.5;
              ELSE
                  -- En cualquier otro caso, la produccion es la que se acaba de calcular
                  v_produccion := v_produccion_aux;
              END IF;

           END IF;
        END IF;
      -- fin MPM --

      ELSIF v_aseg_autorizados(1).coefsobrerdtos IS NOT NULL THEN
				v_produccion := fn_getlimitesrendimiento(p_lineaseguroid       => p_lineaseguroid,
																								 p_capitalesasegurados => p_capitalesasegurados,
																								 p_modulo              => p_modulo,
																								 p_codcultivo          => p_codcultivo,
																								 p_codvariedad         => p_codvariedad,
																								 p_provincia           => p_provincia,
																								 p_comarca             => p_comarca,
																								 p_termino             => p_termino,
																								 p_subtermino          => p_subtermino,
																								 p_tablardto           => v_tablardto,
                                                 -- MPM - 0912
                                                 -- Se pasa el rendimiento permitido del AAC
                                                 p_rdtoPermitido        => v_aseg_autorizados(1).rdtopermitido,
                                                 -- Se indica si el asegurado es bonus o no
                                                 p_esAsegBonus          => v_esAsegBonus,
                                                 -- MPM - ENE13
                                                 p_codlinea             => p_codlinea,
                                                 v_312_superficie       => v_312_superficie);


				-- MPM - ENE13
        -- Si en la primera posicion del array viene un -3, el n? de unidades es obligatorio y no se ha informado,
        -- por lo que no se sigue calculando y se muestra un aviso
        IF (v_produccion(1) IS NOT NULL AND v_produccion(1) = -3) THEN
           p_produccion := v_produccion;
           RETURN;
        END IF;
        --

        IF v_countmedidasaplicablesmodulo != 0 THEN
					p('Entor ' || v_countmedidasaplicablesmodulo);
				ELSE
					v_coefrdtoasegp := v_aseg_autorizados(1).coefsobrerdtos;

				END IF;

			ELSIF pq_calcula_produccion.fn_existelimiterendbylinea(p_lineaseguroid => p_lineaseguroid) THEN

				v_produccion := fn_getlimitesrendimiento(p_lineaseguroid       => p_lineaseguroid,
																								 p_capitalesasegurados => p_capitalesasegurados,
																								 p_modulo              => p_modulo,
																								 p_codcultivo          => p_codcultivo,
																								 p_codvariedad         => p_codvariedad,
																								 p_provincia           => p_provincia,
																								 p_comarca             => p_comarca,
																								 p_termino             => p_termino,
																								 p_subtermino          => p_subtermino,
																								 p_tablardto           => v_tablardto,
                                                 -- MPM - 0912
                                                 -- Se pasa el rendimiento permitido del AAC
                                                 p_rdtoPermitido        => v_aseg_autorizados(1).rdtopermitido,
                                                 -- Se indica si el asegurado es bonus o no
                                                 p_esAsegBonus          => v_esAsegBonus,
                                                 -- MPM - ENE13
                                                 p_codlinea             => p_codlinea,
                                                 v_312_superficie       => v_312_superficie);


				-- MPM - ENE13
        -- Si en la primera posicion del array viene un -3, el n? de unidades es obligatorio y no se ha informado,
        -- por lo que no se sigue calculando y se muestra un aviso
        IF (v_produccion(1) IS NOT NULL AND v_produccion(1) = -3) THEN
           p_produccion := v_produccion;
           RETURN;
        END IF;
        --

        IF v_countmedidasaplicablesmodulo <> 0 THEN
					v_coefrdtoasegp := v_medida.coefrdtomaxaseg;
				END IF;
			ELSE
				v_produccion(1) := 1;
				v_produccion(2) := '';
				v_produccion(3) := 1;
				v_produccion(4) := '';
			END IF;
		END IF;

    -- MPM - 0912
    -- Si el array de v_produccion tiene todas las posiciones a -1, la parcela no es asegurable con los datos indicados
    -- Se devuelve la produccion y finaliza la ejecucion
    IF (v_produccion(1) = '-1' AND v_produccion(2) = '-1' AND v_produccion(3) = '-1' AND v_produccion(4) = '-1') THEN
        v_produccion(1) := -1;
				v_produccion(2) := -1;
				v_produccion(3) := -1;
				v_produccion(4) := -1;
       	p('La parcela no es asegurable con los datos indicados');
		    p_produccion := v_produccion;
        RETURN;
    END IF;
    -- Si los maximos de produccion y rendimiento estan vacios, el rendimiento es libre
    -- Se devuelve la produccion y finaliza la ejecucion
    IF ((v_produccion(2) is null OR v_produccion(2) = '')  AND (v_produccion(4) is null OR v_produccion(4) = '')) THEN
       	p('Rendimiento libre');
		    p_produccion := v_produccion;
        RETURN;
    END IF;
    -- fin MPM --


		v_porcreducrendvariables := fn_porcreducrendvariables(p_lineaseguroid       => p_lineaseguroid,
																													p_capitalesasegurados => p_capitalesasegurados,
																													p_modulo              => p_modulo,
																													p_provincia           => p_provincia,
																													p_comarca             => p_comarca,
																													p_termino             => p_termino,
																													p_subtermino          => p_subtermino);


		-- MPM - ENE13
    -- Para la linea 2012/300 solo se aplicara el coeficiente obtenido si la ubicacion y la especie cultivada de la parcela
    -- aparecen en la tabla TB_SC_C_ZONIF_APLIC_MEDIDAS
    --
    -- Se busca en base de datos si el plan es mayor o igual que 2012 y la linea es la 300
    BEGIN
        SELECT LINEASEGUROID INTO v_linsegid_2012300
            FROM TB_LINEAS WHERE CODPLAN >= 2012 AND CODLINEA = 300 AND LINEASEGUROID = p_lineaseguroid;
    EXCEPTION
        WHEN OTHERS THEN
            --No se encuentra => lo pongo a 0
            v_linsegid_2012300 := 0;
    END;
    -- Si se corresponde con la 2012/300 y se ha obtenido el coeficiente
    IF (p_lineaseguroid = v_linsegid_2012300 AND v_coefrdtoasegp IS NOT NULL) THEN
       -- Llama al metodo que comprueba si hay que aplicar el coeficiente o no
       v_aplic_coef_300 := fn_getAplicaCoefRdtos (p_lineaseguroid     => p_lineaseguroid,
                                                  p_provincia         => p_provincia,
                                                  p_comarca           => p_comarca,
                                                  p_termino           => p_termino,
															                    p_subtermino        => p_subtermino,
                                                  p_codcultivo        => p_codcultivo,
															                    p_codvariedad       => p_codvariedad);

       -- Si no hay que aplicar el coeficiente se resetea su valor
       IF (v_aplic_coef_300 = FALSE) THEN
          v_coefrdtoasegp := NULL;
       END IF;
    END IF;



    IF v_coefrdtoasegp IS NULL THEN
			v_coefrdtoasegp := 1;
		END IF;

		IF v_porcreducrendvariables != 0 THEN
			IF v_porcreducrendvariables < v_coefrdtoasegp THEN
				v_coefrdtoasegp := v_porcreducrendvariables;
			END IF;
		END IF;



		v_produccion(1) := v_produccion(1) * v_coefrdtoasegp;
		BEGIN
			v_produccion(2) := v_produccion(2) * v_coefrdtoasegp;
		EXCEPTION
			WHEN OTHERS THEN
				IF SQLCODE = -6502 THEN
					v_produccion(2) := '';
				END IF;

		END;

		BEGIN
			v_produccion(3) := v_produccion(3) * v_coefrdtoasegp;
		EXCEPTION
			WHEN OTHERS THEN
				IF SQLCODE = -6502 THEN
					v_produccion(3) := '';
				END IF;

		END;


		BEGIN
			v_produccion(4) := v_produccion(4) * v_coefrdtoasegp;
		EXCEPTION
			WHEN OTHERS THEN
				IF SQLCODE = -6502 THEN
					v_produccion(4) := '';
				END IF;

		END;

    -- MPM ENE13
    -- Solo para la linea 300, se compara el limite maximo obtenido con el limite de rendimiento asociado al usuario
    -- y se elige el menor
    IF (p_codlinea = 300) THEN
       -- Si vienen informados los dos valores se elige el menor
       IF (v_limsup_limrdtos <> -1 AND v_limsup_limrdtos is not null AND v_rdtopermitido_aac <> -1) THEN
          IF (v_rdtopermitido_aac < (v_limsup_limrdtos*v_coefrdtoasegp)) THEN
             v_produccion(2) := v_rdtopermitido_aac;
          ELSE
             v_produccion(2) := (v_limsup_limrdtos*v_coefrdtoasegp);
          END IF;
       -- Si solo esta informado el rendimiento asociado al usuario, se utiliza este valor
       ELSIF ((v_limsup_limrdtos = -1 or v_limsup_limrdtos is null) AND v_rdtopermitido_aac <> -1) THEN
            v_produccion(2) := v_rdtopermitido_aac;
       -- Si solo esta informado el limite maximo obtenido, se utiliza este valor
       ELSIF (v_limsup_limrdtos <> -1 AND v_limsup_limrdtos is not null AND v_rdtopermitido_aac = -1) THEN
            v_produccion(2) := (v_limsup_limrdtos*v_coefrdtoasegp);
       END IF;

    END IF;

    -- Si la linea es la 312 y la aplicacion del rendimiento es por superficie, predomina el rendimiento permitido del aac
    -- Si esta informado el rendimiento asociado al usuario, se utiliza este valor
    IF (v_312_superficie AND v_rdtopermitido_aac <> -1) THEN
       v_produccion(2) := v_rdtopermitido_aac;
    END IF;

    --
    --


		IF v_produccion(1) = 0 THEN
			v_produccion(1) := 1;
		ELSE
			v_produccion(1) := trunc(v_produccion(1));
		END IF;

		IF (v_produccion(2) = 0) THEN
			v_produccion(2) := '';
		ELSE
			v_produccion(2) := trunc(nvl(v_produccion(2),
																	 '0'));
		END IF;


		p('Produccion ' || v_produccion(1) || ' ' || v_produccion(2));
		p_produccion := v_produccion;

	END pr_get_producciones;

	FUNCTION fn_porcreducrendvariables(p_lineaseguroid       VARCHAR2
																		,p_capitalesasegurados pq_calcula_produccion.t_capital_asegurado
																		,p_modulo              VARCHAR2
																		,p_provincia           VARCHAR2
																		,p_comarca             VARCHAR2
																		,p_termino             VARCHAR2
																		,p_subtermino          VARCHAR2) RETURN NUMBER IS
		v_result       NUMBER := 1;
		v_codreducrdto VARCHAR2(32767) := NULL;
		v_consulta     VARCHAR2(32767) := NULL;
	BEGIN

		FOR ind IN 1 .. p_capitalesasegurados.COUNT LOOP
			FOR ind2 IN 1 .. p_capitalesasegurados(ind).COUNT LOOP
				IF p_capitalesasegurados(ind) (ind2).cod = 620 THEN
					v_codreducrdto := p_capitalesasegurados(ind) (ind2).valor;
				END IF;
			END LOOP;
		END LOOP;

		v_codreducrdto := REPLACE(v_codreducrdto,
															';',
															',');

		IF v_codreducrdto IS NULL THEN
			RETURN 0;
		END IF;


		v_consulta := 'SELECT MIN(a.pctreduccion)
			FROM tb_sc_c_reduc_rdtos_ambitos a
		 WHERE a.lineaseguroid = ' || p_lineaseguroid || '
			 AND a.codmodulo = ''' || p_modulo || '''
			 AND a.codreducrdto IN (' || v_codreducrdto || ')
			 AND a.codprovincia = ' || p_provincia || '
			 AND a.codcomarca = ' || p_comarca || '
			 AND a.codtermino = ' || p_termino || '
			 AND a.subtermino = ''' || p_subtermino || ''' ';

		dbms_output.put_line(v_consulta);

		EXECUTE IMMEDIATE 'SELECT MIN(a.pctreduccion)
			FROM tb_sc_c_reduc_rdtos_ambitos a
		 WHERE a.lineaseguroid = ' || p_lineaseguroid || '
			 AND a.codmodulo = ''' || p_modulo || '''
			 AND a.codreducrdto IN (' || v_codreducrdto || ')
			 AND a.codprovincia = ' || p_provincia || '
			 AND a.codcomarca = ' || p_comarca || '
			 AND a.codtermino = ' || p_termino || '
			 AND a.subtermino = ''' || p_subtermino || ''' '
			INTO v_result;
		IF v_result IS NULL THEN
			EXECUTE IMMEDIATE 'SELECT MIN(a.pctreduccion)
				FROM tb_sc_c_reduc_rdtos_ambitos a
			 WHERE a.lineaseguroid = ' || p_lineaseguroid || '
				 AND a.codmodulo = ''' || p_modulo || '''
				 AND a.codreducrdto IN (' || v_codreducrdto || ')
				 AND a.codprovincia = ' || p_provincia || '
				 AND a.codcomarca = ' || p_comarca || '
				 AND a.codtermino = ' || p_termino || '
				 AND a.subtermino IN (''' || p_subtermino || ''', ''9'')'
				INTO v_result;
			IF v_result IS NULL THEN
				EXECUTE IMMEDIATE 'SELECT MIN(a.pctreduccion)
					FROM tb_sc_c_reduc_rdtos_ambitos a
				 WHERE a.lineaseguroid = ' || p_lineaseguroid || '
					 AND a.codmodulo = ''' || p_modulo || '''
					 AND a.codreducrdto IN (' || v_codreducrdto || ')
					 AND a.codprovincia = ' || p_provincia || '
					 AND a.codcomarca = ' || p_comarca || '
					 AND a.codtermino IN (' || p_termino || ', 999)
					 AND a.subtermino IN (''' || p_subtermino || ''', ''9'')'
					INTO v_result;

				IF v_result IS NULL THEN
					EXECUTE IMMEDIATE 'SELECT MIN(a.pctreduccion)
						FROM tb_sc_c_reduc_rdtos_ambitos a
					 WHERE a.lineaseguroid = ' || p_lineaseguroid || '
						 AND a.codmodulo = ''' || p_modulo || '''
						 AND a.codreducrdto IN (' || v_codreducrdto || ')
						 AND a.codprovincia = ' || p_provincia || '
						 AND a.codcomarca IN (' || p_comarca || ', 99)
						 AND a.codtermino IN (' || p_termino || ', 999)
						 AND a.subtermino IN (''' || p_subtermino || ''', ''9'')'
						INTO v_result;
					IF v_result IS NULL THEN
						EXECUTE IMMEDIATE 'SELECT MIN(a.pctreduccion)
							FROM tb_sc_c_reduc_rdtos_ambitos a
						 WHERE a.lineaseguroid = ' || p_lineaseguroid || '
							 AND a.codmodulo = ''' || p_modulo || '''
							 AND a.codreducrdto IN (' || v_codreducrdto || ')
							 AND a.codprovincia IN (' || p_provincia || ', 99)
							 AND a.codcomarca IN (' || p_comarca || ', 99)
							 AND a.codtermino IN (' || p_termino || ', 999)
							 AND a.subtermino IN (''' || p_subtermino || ''', ''9'')'
							INTO v_result;
					END IF;
				END IF;
			END IF;
		END IF;

		IF v_result IS NULL THEN
			v_result := 0;
		END IF;

		RETURN v_result / 100;
	END fn_porcreducrendvariables;

	FUNCTION fn_getorganizadorinformacion(p_lineaseguroid VARCHAR2
																			 --,p_codubicacion  tb_sc_oi_org_info.codubicacion%TYPE
																			 ,p_codconcepto   tb_sc_oi_org_info.codconcepto%TYPE
																			 ,p_coduso        tb_sc_oi_org_info.coduso%TYPE) RETURN NUMBER IS
		v_resul NUMBER := 0;
	BEGIN
		SELECT COUNT(*)
			INTO v_resul
			FROM tb_sc_oi_org_info o
		 WHERE o.lineaseguroid = p_lineaseguroid
			 --AND o.codubicacion = p_codubicacion
			 AND o.codconcepto = p_codconcepto
			 AND o.coduso = p_coduso;
		RETURN v_resul;
	END fn_getorganizadorinformacion;


	FUNCTION fn_getgarantizado(p_capitalesasegurados pq_calcula_produccion.t_capital_asegurado) RETURN VARCHAR2 IS
		v_resul VARCHAR2(32767) := '';
	BEGIN

		FOR ind IN 1 .. p_capitalesasegurados.COUNT LOOP
			FOR ind2 IN 1 .. p_capitalesasegurados(ind).COUNT LOOP
				IF p_capitalesasegurados(ind) (ind2).cod = 175 THEN
					RETURN p_capitalesasegurados(ind)(ind2) .valor;
				END IF;
			END LOOP;
		END LOOP;

		RETURN v_resul;

	END fn_getgarantizado;


	FUNCTION fn_getFechaFinGarantias(p_capitalesasegurados pq_calcula_produccion.t_capital_asegurado) RETURN VARCHAR2 IS
		v_resul VARCHAR2(32767) := '';
	BEGIN

		FOR ind IN 1 .. p_capitalesasegurados.COUNT LOOP
			FOR ind2 IN 1 .. p_capitalesasegurados(ind).COUNT LOOP
				IF p_capitalesasegurados(ind) (ind2).cod = 134 THEN
					RETURN p_capitalesasegurados(ind)(ind2) .valor;
				END IF;
			END LOOP;
		END LOOP;

		RETURN v_resul;

	END fn_getFechaFinGarantias;

  FUNCTION fn_getSistemaCultivo(p_capitalesasegurados pq_calcula_produccion.t_capital_asegurado) RETURN VARCHAR2 IS
		v_resul VARCHAR2(32767) := '';
	BEGIN

		FOR ind IN 1 .. p_capitalesasegurados.COUNT LOOP
			FOR ind2 IN 1 .. p_capitalesasegurados(ind).COUNT LOOP
				IF p_capitalesasegurados(ind) (ind2).cod = 123 THEN
					RETURN p_capitalesasegurados(ind)(ind2) .valor;
				END IF;
			END LOOP;
		END LOOP;

		RETURN v_resul;

	END fn_getSistemaCultivo;

	FUNCTION fn_cargaAseguradosAutorizados(p_lineaseguroid     VARCHAR2
                                      ,p_codlinea            VARCHAR2
																			,p_nif               VARCHAR2 DEFAULT NULL
																			,p_modulo            VARCHAR2 DEFAULT NULL
																			,p_fechafingarantias VARCHAR2 DEFAULT NULL
																			,p_garantizado       VARCHAR2 DEFAULT NULL
                                      ,p_concPpalMod       VARCHAR2 DEFAULT NULL
                                      ,p_riesgoCbrto       VARCHAR2 DEFAULT NULL
                                      ,p_valor             VARCHAR2 DEFAULT NULL
																			,p_codcultivo        VARCHAR2 DEFAULT NULL
																			,p_convariedad       VARCHAR2 DEFAULT NULL) RETURN pq_calcula_produccion.t_aseg_autorizados IS

  v_aseg_autorizados pq_calcula_produccion.t_aseg_autorizados;

  BEGIN

      IF (p_codlinea = 310) OR (p_codlinea = 312) OR (p_codlinea = 313) THEN
			    v_aseg_autorizados := pq_calcula_produccion.fn_getaseguradosautorizados(p_lineaseguroid     => p_lineaseguroid,
																																							p_nif               => p_nif,
																																							p_modulo            => p_modulo,
																																							p_fechafingarantias => p_fechafingarantias,
																																							p_garantizado       => p_garantizado,
                                                                              p_concPpalMod       => p_concPpalMod,
                                                                              p_riesgoCbrto       => p_riesgoCbrto,
                                                                              p_valor             => p_valor,
																																							p_codcultivo        => p_codcultivo,
																																							p_convariedad       => NULL);

      END IF;


      IF v_aseg_autorizados.EXISTS(1) = FALSE THEN
			      v_aseg_autorizados := pq_calcula_produccion.fn_getaseguradosautorizados(p_lineaseguroid     => p_lineaseguroid,
																																							p_nif               => p_nif,
																																							p_modulo            => p_modulo,
																																							p_fechafingarantias => p_fechafingarantias,
																																							p_garantizado       => p_garantizado,
                                                                              p_concPpalMod       => p_concPpalMod,
                                                                              p_riesgoCbrto       => p_riesgoCbrto,
                                                                              p_valor             => p_valor,
																																							p_codcultivo        => 999,
																																							p_convariedad       => NULL);
		    END IF;

		    IF (v_aseg_autorizados.EXISTS(1) = FALSE) AND
			    ((p_codlinea = 310) OR (p_codlinea = 312) OR (p_codlinea = 313)) THEN
            v_aseg_autorizados := pq_calcula_produccion.fn_getaseguradosautorizados(p_lineaseguroid     => p_lineaseguroid,
																																							p_nif               => NULL,
																																							p_modulo            => p_modulo,
																																							p_fechafingarantias => p_fechafingarantias,
																																							p_garantizado       => p_garantizado,
                                                                              p_concPpalMod       => p_concPpalMod,
                                                                              p_riesgoCbrto       => p_riesgoCbrto,
                                                                              p_valor             => p_valor,
																																							p_codcultivo        => p_codcultivo,
																																							p_convariedad       => NULL);
		    END IF;

		    IF v_aseg_autorizados.EXISTS(1) = FALSE THEN
			      v_aseg_autorizados := pq_calcula_produccion.fn_getaseguradosautorizados(p_lineaseguroid     => p_lineaseguroid,
																																							p_nif               => NULL,
																																							p_modulo            => p_modulo,
																																							p_fechafingarantias => p_fechafingarantias,
																																							p_garantizado       => p_garantizado,
                                                                              p_concPpalMod       => p_concPpalMod,
                                                                              p_riesgoCbrto       => p_riesgoCbrto,
                                                                              p_valor             => p_valor,
																																							p_codcultivo        => 999,
																																							p_convariedad       => NULL);
		    END IF;

        return v_aseg_autorizados;

  END fn_cargaAseguradosAutorizados;


	FUNCTION fn_getaseguradosautorizados(p_lineaseguroid     VARCHAR2
																			,p_nif               VARCHAR2 DEFAULT NULL
																			,p_modulo            VARCHAR2 DEFAULT NULL
																			,p_fechafingarantias VARCHAR2 DEFAULT NULL
																			,p_garantizado       VARCHAR2 DEFAULT NULL
                                      ,p_concPpalMod       VARCHAR2 DEFAULT NULL
                                      ,p_riesgoCbrto       VARCHAR2 DEFAULT NULL
                                      ,p_valor             VARCHAR2 DEFAULT NULL
																			,p_codcultivo        VARCHAR2 DEFAULT NULL
																			,p_convariedad       VARCHAR2 DEFAULT NULL) RETURN pq_calcula_produccion.t_aseg_autorizados IS


		v_consulta VARCHAR2(32767) := NULL;
		v_result   pq_calcula_produccion.t_aseg_autorizados;
		v_res      tb_aseg_autorizados_dia%ROWTYPE;
		TYPE cur_typ IS REF CURSOR;
		v_cursor    cur_typ;
		v_ind       NUMBER := 1;


	BEGIN
		v_consulta := 'SELECT * ';
		v_consulta := v_consulta || 'FROM tb_aseg_autorizados_dia a ';
		v_consulta := v_consulta || 'WHERE a.lineaseguroid = ' || p_lineaseguroid || ' ';

		IF p_modulo IS NOT NULL THEN
			v_consulta := v_consulta || 'AND a.codmodulo = ''' || p_modulo || ''' ';
		END IF;

		IF p_nif IS NOT NULL THEN
			v_consulta := v_consulta || 'AND a.nifasegurado = ''' || p_nif || ''' ';
		ELSE
			v_consulta := v_consulta || 'AND a.nifasegurado is null ';
		END IF;

		IF p_codcultivo IS NOT NULL THEN
			v_consulta := v_consulta || ' AND a.codcultivo = ' || p_codcultivo || ' ';
		END IF;

		IF p_convariedad IS NOT NULL THEN
			v_consulta := v_consulta || ' AND a.codvariedad = ' || p_convariedad || ' ';
		END IF;

		IF p_fechafingarantias IS NOT NULL THEN
			v_consulta := v_consulta || ' AND a.fecfgarant = to_date(''' || p_fechafingarantias || ''',''dd/MM/yyyy'')';
		END IF;

		IF p_garantizado IS NOT NULL THEN
			v_consulta := v_consulta || ' AND a.codgarantizado = ' || p_garantizado || ' ';
		END IF;

    IF (p_concPpalMod IS NOT NULL) THEN
        v_consulta := v_consulta || ' AND a.cpmodrcub = ' || p_concPpalMod;
    END IF;

    IF (p_riesgoCbrto IS NOT NULL) THEN
        v_consulta := v_consulta || ' AND a.codrcubrcub = ' || p_riesgoCbrto;
    END IF;

    IF (p_valor IS NOT NULL) THEN
        IF (p_valor = '-1') THEN
            --'S'
            v_consulta := v_consulta || ' AND a.rcubeleg = ''S''';
        ELSE
            --'N'
            v_consulta := v_consulta || ' AND a.rcubeleg = ''N''';
        END IF;

    END IF;

    p(v_consulta);

		OPEN v_cursor FOR v_consulta;
		LOOP
			FETCH v_cursor
				INTO v_res;
			EXIT WHEN v_cursor%NOTFOUND;
			v_result(v_ind).lineaseguroid := v_res.lineaseguroid;
			v_result(v_ind).codmodulo := v_res.codmodulo;
			v_result(v_ind).nifasegurado := v_res.nifasegurado;
			v_result(v_ind).codcultivo := v_res.codcultivo;
			v_result(v_ind).codvariedad := v_res.codvariedad;
			v_result(v_ind).codgarantizado := v_res.codgarantizado;
			v_result(v_ind).codnivelriesgo := v_res.codnivelriesgo;
			v_result(v_ind).coefsobrerdtos := v_res.coefsobrerdtos;
			v_result(v_ind).rdtopermitido := v_res.rdtopermitido;
			v_result(v_ind).codprovincia := v_res.codprovincia;
			v_result(v_ind).cpmodffg := v_res.cpmodffg;
			v_result(v_ind).codrcubffg := v_res.codrcubffg;
			v_result(v_ind).fecfgarant := v_res.fecfgarant;
			v_result(v_ind).cpmodcg := v_res.cpmodcg;
			v_result(v_ind).codrcubcg := v_res.codrcubcg;
			v_result(v_ind).valorcg := v_res.valorcg;
			v_result(v_ind).desccg := v_res.desccg;
			v_result(v_ind).id := v_res.id;
			v_ind := v_ind + 1;
		END LOOP;
		CLOSE v_cursor;

		RETURN v_result;

	END fn_getaseguradosautorizados;



	FUNCTION fn_getcoefrdtomaxaseg(p_lineaseguroid VARCHAR2
																,p_nif           VARCHAR2) RETURN tb_sc_c_medidas%ROWTYPE IS

		v_medidas tb_sc_c_medidas%ROWTYPE DEFAULT NULL;
	BEGIN
		BEGIN
			SELECT *
				INTO v_medidas
				FROM tb_sc_c_medidas m
			 WHERE m.lineaseguroid = p_lineaseguroid
				 AND m.nifasegurado = p_nif;
		EXCEPTION
			WHEN no_data_found THEN
				BEGIN
					SELECT *
						INTO v_medidas
						FROM tb_sc_c_medidas m
					 WHERE m.lineaseguroid = p_lineaseguroid
						 AND m.nifasegurado = '-';
				EXCEPTION
					WHEN no_data_found THEN
						NULL;
				END;

		END;

		RETURN v_medidas;
	END fn_getcoefrdtomaxaseg;



	FUNCTION fn_getmedidasaplicablemodulo(p_lineaseguroid VARCHAR
																			 ,p_codmodulo     VARCHAR2) RETURN NUMBER IS
		v_result NUMBER := 0;
	BEGIN
		SELECT COUNT(*)
			INTO v_result
			FROM tb_sc_c_modulos_apl_medidas
		 WHERE lineaseguroid = p_lineaseguroid
			 AND codmodulo = p_codmodulo;

		RETURN v_result;

	END fn_getmedidasaplicablemodulo;





	FUNCTION fn_existelimiterendbylinea(p_lineaseguroid VARCHAR2) RETURN BOOLEAN IS
		--existeLimiteRendimientoByLineaseguroid
		v_result BOOLEAN := FALSE;
		v_hay    NUMBER := 0;
	BEGIN

		SELECT COUNT(*)
			INTO v_hay
			FROM tb_sc_c_limites_rdtos lr where lr.lineaseguroid = p_lineaseguroid;

		IF v_hay = 0 THEN
			v_result := FALSE;
		ELSE
			v_result := TRUE;
		END IF;

		RETURN v_result;
	END fn_existelimiterendbylinea;



	FUNCTION fn_getlimitesrendimiento(p_lineaseguroid       VARCHAR2
																	 ,p_capitalesasegurados pq_calcula_produccion.t_capital_asegurado
																	 ,p_modulo              VARCHAR2
																	 ,p_codcultivo          VARCHAR2
																	 ,p_codvariedad         VARCHAR2
																	 ,p_provincia           VARCHAR2
																	 ,p_comarca             VARCHAR2
																	 ,p_termino             VARCHAR2
																	 ,p_subtermino          VARCHAR2
																	 ,p_tablardto           VARCHAR2
                                   -- MPM - 0912
                                   -- Se a?ade este parametro para poder hacer la comprobacion posterior
                                   ,p_rdtoPermitido        tb_sc_c_aseg_autorizados.rdtopermitido%TYPE
                                   -- Indica si el asegurado es tipo bonus
                                   ,p_esAsegBonus        BOOLEAN
                                   -- MPM - ENE13
                                   -- Parametros para comprobar si la linea es 312 y la aplicacion de rendimientos
                                   -- es por superficie
                                   ,p_codlinea           VARCHAR2
                                   ,v_312_superficie     IN OUT BOOLEAN) RETURN pq_calcula_produccion.t_varchar2 IS


		v_produccion pq_calcula_produccion.t_varchar2;

		v_limrendmin                   NUMBER := 1;
		v_limrendmax                   NUMBER := 0;
		v_tablacalcrdtos               VARCHAR2(150) := 'tb_sc_c_limites_rdtos';
		v_checkrendcaracesp            BOOLEAN := FALSE;
		v_mascaras                     t_ren_car_expecificas;


		v_campos_mascaras t_campos_mascaras;

		v_rendimientos    pq_calcula_produccion.t_rdtos_caract_esp;

		v_rendimientos_lim pq_calcula_produccion.t_limites;

		v_mascaras_limit_ren   pq_calcula_produccion.t_masc_lim_rendim;

		v_apprdto VARCHAR2(32767) := NULL;

    v_pctaplrdto tb_sc_c_limites_rdtos.pctaplrdto%TYPE;


	BEGIN
		-- comprobacion inicial de la tabla a usar para calcular los rendimientos
		-- (LimiteRendimiento o RDTOS_CARACT_ESP)
		v_checkrendcaracesp := pq_calcula_produccion.fn_checkrendcaracesp(p_lineaseguroid => p_lineaseguroid);
		IF v_checkrendcaracesp THEN
			--calculamos primero los rendimientos con la tabla RDTOS_CARACT_ESP

			--1. Obtenemos los campos de mascara segun plan, linea, cultivo
			-- variedad, provincia, comarca, termino, subtermino y modulo
			v_mascaras := pq_calcula_produccion.fn_getMascaraRdtosCaracEsp(p_lineaseguroid => p_lineaseguroid,
																																		 p_modulo        => p_modulo,
																																		 p_codcultivo    => p_codcultivo,
																																		 p_codvariedad   => p_codvariedad,
																																		 p_provincia     => p_provincia,
																																		 p_comarca       => p_comarca,
																																		 p_termino       => p_termino,
																																		 p_subtermino    => p_subtermino);

			--Asignamos los valores de los datos variables a los campos de mascaras
      v_campos_mascaras := pq_calcula_produccion.fn_asignarValoresMascCarEsp(v_mascaras, p_capitalesasegurados);

      -- 2. Ponemos a cero los campos del filtro que vayan vacios y tengan
			-- un valor por defecto
			v_campos_mascaras := pq_calcula_produccion.fn_valorpordefectofitros(p_campos_mascara => v_campos_mascaras);

			-- 3. Obtenemos los Limites de Rendimiento
			v_rendimientos := fn_damelistarencaracesp(p_lineaseguroid => p_lineaseguroid,
																								p_modulo        => p_modulo,
																								p_codcultivo    => p_codcultivo,
																								p_codvariedad   => p_codvariedad,
																								p_provincia     => p_provincia,
																								p_comarca       => p_comarca,
																								p_termino       => p_termino,
																								p_subtermino    => p_subtermino,
																								p_filtromascara => v_campos_mascaras);

			FOR a IN 1 .. v_rendimientos.COUNT LOOP
				IF a = 1 THEN
					v_limrendmin := v_rendimientos(a).limiteinfrdto;
					v_limrendmax := v_rendimientos(a).limitesuprdto;
				ELSE
					IF v_limrendmin > v_rendimientos(a).limiteinfrdto THEN
						v_limrendmin := v_rendimientos(a).limiteinfrdto;
					END IF;
					IF v_limrendmax < v_rendimientos(a).limitesuprdto THEN
						v_limrendmax := v_rendimientos(a).limitesuprdto;
					END IF;
				END IF;
			END LOOP;
			IF v_limrendmax > 0 THEN
				v_apprdto        := 'S';
				v_tablacalcrdtos := '';
			END IF;



		END IF;
		IF v_tablacalcrdtos = 'tb_sc_c_limites_rdtos' THEN
			v_mascaras_limit_ren := fn_getMascLimitesRendimiento(p_lineaseguroid => p_lineaseguroid,
																														 p_modulo        => p_modulo,
																														 p_codcultivo    => p_codcultivo,
																														 p_codvariedad   => p_codvariedad,
																														 p_provincia     => p_provincia,
																														 p_comarca       => p_comarca,
																														 p_termino       => p_termino,
																														 p_subtermino    => p_subtermino);

			v_campos_mascaras.DELETE;

      --Asignamos los valores de los datos variables a los campos de mascaras
      v_campos_mascaras := pq_calcula_produccion.fn_asignarValoresMascLimRdto(v_mascaras_limit_ren, p_capitalesasegurados);

			-- 2. Ponemos a cero los campos del filtro que vayan vacios y tengan
			-- un valor por defecto
			v_campos_mascaras := pq_calcula_produccion.fn_valorpordefectofitros(p_campos_mascara => v_campos_mascaras);

			-- 3. Obtenemos los Limites de Rendimiento
			v_rendimientos_lim := fn_getlimredimientos(p_lineaseguroid   => p_lineaseguroid,
																								 p_modulo          => p_modulo,
																								 p_codcultivo      => p_codcultivo,
																								 p_codvariedad     => p_codvariedad,
																								 p_provincia       => p_provincia,
																								 p_comarca         => p_comarca,
																								 p_termino         => p_termino,
																								 p_subtermino      => p_subtermino,
																								 p_campos_mascaras => v_campos_mascaras,
																								 p_tablardto       => p_tablardto);

      -- MPM - 0912
      -- Si no se han encontrado registros para los datos indicados, el asegurado es bonus y
      -- la tabla de rendimientos donde se ha buscado es la 1, se repite la busqueda en la tabla 0
      IF (v_rendimientos_lim.COUNT = 0 AND p_esAsegBonus = TRUE AND p_tablardto = 1) THEN
         	v_rendimientos_lim := fn_getlimredimientos(p_lineaseguroid   => p_lineaseguroid,
																								 p_modulo          => p_modulo,
																								 p_codcultivo      => p_codcultivo,
																								 p_codvariedad     => p_codvariedad,
																								 p_provincia       => p_provincia,
																								 p_comarca         => p_comarca,
																								 p_termino         => p_termino,
																								 p_subtermino      => p_subtermino,
																								 p_campos_mascaras => v_campos_mascaras,
																								 p_tablardto       => 0);
      END IF;


      -- Si solo viene un registro en los limites de rendimiento, se comprueba que los campos limite inferior,
      -- superior y % de aplicacion de rendimiento no sean 0
      IF v_rendimientos_lim.COUNT = 1 THEN
         -- Si los limites inferior y superior son 0 y el % de aplicacion del rendimiento es nulo,
         -- se establece la produccion a 0 y se ocntinua con el calculo
         IF (v_rendimientos_lim(1).pctaplrdto IS NULL AND
             v_rendimientos_lim(1).limiteinfrdto = 0 AND v_rendimientos_lim(1).limitesuprdto = 0) THEN
            v_produccion(1) := '0';	v_produccion(2) := '0'; v_produccion(3) := '0'; v_produccion(4) := '0';
         END IF;
         -- Si los tres campos son 0, la parcela no es asegurable con esos datos
         -- Se devuelve el array de produccion a -1 para poder mostrar el error en la pantalla de parcela
         IF (v_rendimientos_lim(1).pctaplrdto = 0 AND
             v_rendimientos_lim(1).limiteinfrdto = 0 AND v_rendimientos_lim(1).limitesuprdto = 0) THEN
             v_produccion(1) := '-1';	v_produccion(2) := '-1'; v_produccion(3) := '-1'; v_produccion(4) := '-1';
             return v_produccion;
         END IF;
      END IF;

      -- Si no se ha obtenido ningun registro de limites de rendimiento
      IF v_rendimientos_lim.COUNT = 0 THEN
         -- Si no tiene rendimiento permitido asignado, los rendimientos son libres
         IF (p_rdtoPermitido IS NULL OR p_rdtoPermitido = 0) THEN
            v_produccion(1) := '1'; v_produccion(2) := ''; v_produccion(3) := '1'; v_produccion(4) := '';
            RETURN v_produccion;
         -- Si tiene rendimiento permitido asignado, se mantendra el calculo realizado en el paso 1 (AAC)
         ELSE
             -- Se devuelve nulo para indicar que se debe mantener el calculo hecho anteriormente
             v_produccion(1) := NULL;
             RETURN v_produccion;
         END IF;
      END IF;
      -- fin MPM --

      -- Recorre los registros de rendimientos y obtiene los limites inferior y superior
			FOR a IN 1 .. v_rendimientos_lim.COUNT LOOP
				IF a = 1 THEN
					v_limrendmin := v_rendimientos_lim(a).limiteinfrdto;
					v_limrendmax := v_rendimientos_lim(a).limitesuprdto;
				ELSE
					IF (v_limrendmin is null OR v_limrendmin > v_rendimientos_lim(a).limiteinfrdto) THEN
						v_limrendmin := v_rendimientos_lim(a).limiteinfrdto;
					END IF;
					IF (v_limrendmax is null OR v_limrendmax < v_rendimientos_lim(a).limitesuprdto) THEN
						v_limrendmax := v_rendimientos_lim(a).limitesuprdto;
					END IF;

				END IF;
				v_apprdto := v_rendimientos_lim(a).apprdto;

        -- MPM - 0912
        -- Obtiene el % de aplicacion del rendimiento
        v_pctaplrdto := v_rendimientos_lim(a).pctaplrdto;
        -- fin MPM --

			END LOOP;


		END IF;


		--4. Se aplica el coeficiente de Rendimiento Maximo Asegurable a los
		--limites obtenidos
		IF v_limrendmax IS NULL THEN
			v_limrendmax := -1;
		END IF;


		-- 5. Obtenemos la produccion.
		-- MPM - 0912
    /*IF v_limrendmax = 0 THEN
			v_produccion(1) := '0';
			v_produccion(2) := '0';
			v_produccion(3) := '0';
			v_produccion(4) := '0';
		ELSIF v_limrendmax = -1 THEN*/
    IF v_limrendmax = -1 THEN
			v_produccion(1) := v_limrendmin;
			v_produccion(2) := '';
			v_produccion(3) := v_limrendmin;
			v_produccion(4) := '';
		ELSE
			v_produccion := fn_calculaproduccion(p_capitalesasegurados => p_capitalesasegurados,
																					 p_apprdto             => v_apprdto,
																					 p_limrendmax          => v_limrendmax,
																					 p_limrendmin          => v_limrendmin,
                                           p_pctaplrdto          => v_pctaplrdto);

      IF (p_codlinea = 312 AND v_apprdto = 'S') THEN
         v_312_superficie := TRUE;
      END IF;
		END IF;

		RETURN v_produccion;
	END fn_getlimitesrendimiento;


	FUNCTION fn_calculaproduccion(p_capitalesasegurados pq_calcula_produccion.t_capital_asegurado
															 ,p_apprdto             VARCHAR2
															 ,p_limrendmax          VARCHAR2
															 ,p_limrendmin          VARCHAR2
                               -- Nuevo parametro % aplicable al rendimiento
                               ,p_pctaplrdto          tb_sc_c_limites_rdtos.pctaplrdto%TYPE
                               ) RETURN pq_calcula_produccion.t_varchar2 IS

    v_produccion pq_calcula_produccion.t_varchar2;
		v_superficie NUMBER := 0;
		v_unidades   NUMBER := 0;

		v_flimrendmin NUMBER := 0;
		v_flimrendmax NUMBER := 0;

	BEGIN
		FOR ind IN 1 .. p_capitalesasegurados.COUNT LOOP
			FOR ind2 IN 1 .. p_capitalesasegurados(ind).COUNT LOOP
				IF p_capitalesasegurados(ind) (ind2).cod = 258 THEN
					BEGIN
						v_superficie := p_capitalesasegurados(ind) (ind2).valor;
					EXCEPTION
						WHEN OTHERS THEN
							IF SQLCODE = -6502 THEN
								v_superficie := REPLACE(p_capitalesasegurados(ind) (ind2).valor,
																				'.',
																				',');
							END IF;

					END;
				ELSIF p_capitalesasegurados(ind) (ind2).cod = 117 THEN
					v_unidades := p_capitalesasegurados(ind) (ind2).valor;
				END IF;
			END LOOP;
		END LOOP;


		IF p_apprdto = 'S' THEN
			-- Si el apprdto es "S" el rendimiento se mide en Kg/Ha
			-- los rendimineto se multiplican por la superficie

			v_flimrendmin := trunc(p_limrendmin * v_superficie);
			v_flimrendmax := trunc(p_limrendmax * v_superficie);


		ELSIF p_apprdto = 'U' THEN
			-- Si el apprdto es "U" el rendimiento se mide en Kg/?!rbol
			-- los rendimineto se multiplican por las unidades

      -- MPM - ENE13
      -- En este caso el numero de unidades es obligatorio, en caso de no haberse informado no se calculara el rendimiento
      -- y se mostrara un mensaje informativo
      IF (v_unidades = 0) THEN
         v_produccion(1) := -3;
    		 v_produccion(2) := NULL;
    		 v_produccion(3) := NULL;
    		 v_produccion(4) := NULL;
         RETURN v_produccion;
      END IF;


			v_flimrendmin := trunc(p_limrendmin * v_unidades);
			v_flimrendmax := trunc(p_limrendmax * v_unidades);

    -- MPM - 0912
    -- Si el apprdto es "P" el rendimiento se calcula por porcentaje
   ELSIF p_apprdto = 'P' THEN
      -- Si el % de aplicacion del rendimiento es distinto de 0
      IF (p_pctaplrdto != 0) THEN
         -- Hay que aplicar el % aplicable al rendimiento al calculo realizado en el paso 1
         -- Se devuelve el array de produccion con la primera posicion a -2 y en la segunda posicion
         -- el % aplicable al rendimiento, para indicar que hay que aplicar este campo al calculo realizado
         -- anteriormente
         v_produccion(1) := -2;
    		 v_produccion(2) := p_pctaplrdto;
    		 v_produccion(3) := NULL;
    		 v_produccion(4) := NULL;

    		 RETURN v_produccion;
      END IF;
		END IF;
    -- fin MPM --

    v_produccion(1) := v_flimrendmin;
		v_produccion(2) := v_flimrendmax;
		v_produccion(3) := p_limrendmin;
		v_produccion(4) := p_limrendmax;


		RETURN v_produccion;
	END;


	FUNCTION fn_getlimredimientos(p_lineaseguroid   VARCHAR2
															 ,p_modulo          VARCHAR2
															 ,p_codcultivo      VARCHAR2
															 ,p_codvariedad     VARCHAR2
															 ,p_provincia       VARCHAR2
															 ,p_comarca         VARCHAR2
															 ,p_termino         VARCHAR2
															 ,p_subtermino      VARCHAR2
															 ,p_campos_mascaras t_campos_mascaras
															 ,p_tablardto       VARCHAR2) RETURN t_limites IS
		v_limites t_limites;

	BEGIN



		v_limites := fn_buscalimredimientos(p_lineaseguroid   => p_lineaseguroid,
																				p_modulo          => p_modulo,
																				p_codcultivo      => p_codcultivo,
																				p_codvariedad     => p_codvariedad,
																				p_provincia       => p_provincia,
																				p_comarca         => p_comarca,
																				p_termino         => p_termino,
																				p_subtermino      => p_subtermino,
																				p_allcodcultivo   => 'N',
																				p_allcodvariedad  => 'N',
																				p_allprovincia    => 'N',
																				p_allcomarca      => 'N',
																				p_alltermino      => 'N',
																				p_allsubtermino   => 'N',
																				p_campos_mascaras => p_campos_mascaras,
																				p_tablardto       => p_tablardto);

		IF v_limites.COUNT = 0 THEN
			--Si no recupera datos filtramos por los datos genericos. Segun el siguiente orden:
			--1. Subtermino = 9; 2. Termino = 999; 3. Comarca = 99
			--4. Provincia = 99; 5. Variedad = 999; 6. Cultivo = 999
			v_limites.DELETE;
			v_limites := fn_buscalimredimientos(p_lineaseguroid   => p_lineaseguroid,
																					p_modulo          => p_modulo,
																					p_codcultivo      => p_codcultivo,
																					p_codvariedad     => p_codvariedad,
																					p_provincia       => p_provincia,
																					p_comarca         => p_comarca,
																					p_termino         => p_termino,
																					p_subtermino      => p_subtermino,
																					p_allcodcultivo   => 'N',
																					p_allcodvariedad  => 'N',
																					p_allprovincia    => 'N',
																					p_allcomarca      => 'N',
																					p_alltermino      => 'N',
																					p_allsubtermino   => 'S',
																					p_campos_mascaras => p_campos_mascaras,
																					p_tablardto       => p_tablardto);
			IF v_limites.COUNT = 0 THEN
				--Si no recupera datos filtramos por los datos genericos. Segun el siguiente orden:
				--1. Subtermino = 9; 2. Termino = 999; 3. Comarca = 99
				--4. Provincia = 99; 5. Variedad = 999; 6. Cultivo = 999
				v_limites.DELETE;
				v_limites := fn_buscalimredimientos(p_lineaseguroid   => p_lineaseguroid,
																						p_modulo          => p_modulo,
																						p_codcultivo      => p_codcultivo,
																						p_codvariedad     => p_codvariedad,
																						p_provincia       => p_provincia,
																						p_comarca         => p_comarca,
																						p_termino         => p_termino,
																						p_subtermino      => p_subtermino,
																						p_allcodcultivo   => 'N',
																						p_allcodvariedad  => 'N',
																						p_allprovincia    => 'N',
																						p_allcomarca      => 'N',
																						p_alltermino      => 'S',
																						p_allsubtermino   => 'S',
																						p_campos_mascaras => p_campos_mascaras,
																						p_tablardto       => p_tablardto);
				IF v_limites.COUNT = 0 THEN
					--Si no recupera datos filtramos por los datos genericos. Segun el siguiente orden:
					--1. Subtermino = 9; 2. Termino = 999; 3. Comarca = 99
					--4. Provincia = 99; 5. Variedad = 999; 6. Cultivo = 999
					v_limites.DELETE;
					v_limites := fn_buscalimredimientos(p_lineaseguroid   => p_lineaseguroid,
																							p_modulo          => p_modulo,
																							p_codcultivo      => p_codcultivo,
																							p_codvariedad     => p_codvariedad,
																							p_provincia       => p_provincia,
																							p_comarca         => p_comarca,
																							p_termino         => p_termino,
																							p_subtermino      => p_subtermino,
																							p_allcodcultivo   => 'N',
																							p_allcodvariedad  => 'N',
																							p_allprovincia    => 'N',
																							p_allcomarca      => 'S',
																							p_alltermino      => 'S',
																							p_allsubtermino   => 'S',
																							p_campos_mascaras => p_campos_mascaras,
																							p_tablardto       => p_tablardto);
					IF v_limites.COUNT = 0 THEN
						--Si no recupera datos filtramos por los datos genericos. Segun el siguiente orden:
						--1. Subtermino = 9; 2. Termino = 999; 3. Comarca = 99
						--4. Provincia = 99; 5. Variedad = 999; 6. Cultivo = 999
						v_limites.DELETE;
						v_limites := fn_buscalimredimientos(p_lineaseguroid   => p_lineaseguroid,
																								p_modulo          => p_modulo,
																								p_codcultivo      => p_codcultivo,
																								p_codvariedad     => p_codvariedad,
																								p_provincia       => p_provincia,
																								p_comarca         => p_comarca,
																								p_termino         => p_termino,
																								p_subtermino      => p_subtermino,
																								p_allcodcultivo   => 'N',
																								p_allcodvariedad  => 'N',
																								p_allprovincia    => 'S',
																								p_allcomarca      => 'S',
																								p_alltermino      => 'S',
																								p_allsubtermino   => 'S',
																								p_campos_mascaras => p_campos_mascaras,
																								p_tablardto       => p_tablardto);
						IF v_limites.COUNT = 0 THEN
							--Si no recupera datos filtramos por los datos genericos. Segun el siguiente orden:
							--1. Subtermino = 9; 2. Termino = 999; 3. Comarca = 99
							--4. Provincia = 99; 5. Variedad = 999; 6. Cultivo = 999
							v_limites.DELETE;
							v_limites := fn_buscalimredimientos(p_lineaseguroid   => p_lineaseguroid,
																									p_modulo          => p_modulo,
																									p_codcultivo      => p_codcultivo,
																									p_codvariedad     => p_codvariedad,
																									p_provincia       => p_provincia,
																									p_comarca         => p_comarca,
																									p_termino         => p_termino,
																									p_subtermino      => p_subtermino,
																									p_allcodcultivo   => 'N',
																									p_allcodvariedad  => 'S',
																									p_allprovincia    => 'S',
																									p_allcomarca      => 'S',
																									p_alltermino      => 'S',
																									p_allsubtermino   => 'S',
																									p_campos_mascaras => p_campos_mascaras,
																									p_tablardto       => p_tablardto);
							IF v_limites.COUNT = 0 THEN
								--Si no recupera datos filtramos por los datos genericos. Segun el siguiente orden:
								--1. Subtermino = 9; 2. Termino = 999; 3. Comarca = 99
								--4. Provincia = 99; 5. Variedad = 999; 6. Cultivo = 999
								v_limites.DELETE;
								v_limites := fn_buscalimredimientos(p_lineaseguroid   => p_lineaseguroid,
																										p_modulo          => p_modulo,
																										p_codcultivo      => p_codcultivo,
																										p_codvariedad     => p_codvariedad,
																										p_provincia       => p_provincia,
																										p_comarca         => p_comarca,
																										p_termino         => p_termino,
																										p_subtermino      => p_subtermino,
																										p_allcodcultivo   => 'S',
																										p_allcodvariedad  => 'S',
																										p_allprovincia    => 'S',
																										p_allcomarca      => 'S',
																										p_alltermino      => 'S',
																										p_allsubtermino   => 'S',
																										p_campos_mascaras => p_campos_mascaras,
																										p_tablardto       => p_tablardto);
							END IF;
						END IF;
					END IF;
				END IF;
			END IF;
		END IF;




		RETURN v_limites;
	END;

	FUNCTION fn_buscalimredimientos(p_lineaseguroid   VARCHAR2
																 ,p_modulo          VARCHAR2
																 ,p_codcultivo      VARCHAR2
																 ,p_codvariedad     VARCHAR2
																 ,p_provincia       VARCHAR2
																 ,p_comarca         VARCHAR2
																 ,p_termino         VARCHAR2
																 ,p_subtermino      VARCHAR2
																 ,p_allcodcultivo   VARCHAR2 DEFAULT 'N'
																 ,p_allcodvariedad  VARCHAR2 DEFAULT 'N'
																 ,p_allprovincia    VARCHAR2 DEFAULT 'N'
																 ,p_allcomarca      VARCHAR2 DEFAULT 'N'
																 ,p_alltermino      VARCHAR2 DEFAULT 'N'
																 ,p_allsubtermino   VARCHAR2 DEFAULT 'N'
																 ,p_campos_mascaras t_campos_mascaras
																 ,p_tablardto       VARCHAR2) RETURN t_limites IS
		v_limites t_limites;

		v_consulta VARCHAR2(32767) := NULL;
		TYPE cur_typ IS REF CURSOR;
		v_cursor cur_typ;
		v_ind    NUMBER := 1;
		v_res    tb_sc_c_limites_rdtos%ROWTYPE;

	BEGIN



		v_consulta := 'select * from tb_sc_c_limites_rdtos where 1 = 1 ';
		v_consulta := v_consulta || ' and LINEASEGUROID = ' || p_lineaseguroid || ' ';
		v_consulta := v_consulta || ' and CODMODULO = ''' || p_modulo || ''' ';
		v_consulta := v_consulta || fn_where(p_codcultivoall   => p_allcodcultivo,
																				 p_codcultivo      => p_codcultivo,
																				 p_codvariedadall  => p_allcodvariedad,
																				 p_codvariedad     => p_codvariedad,
																				 p_codprovinciaall => p_allprovincia,
																				 p_codprovincia    => p_provincia,
																				 p_codterminoall   => p_alltermino,
																				 p_codtermino      => p_termino,
																				 p_subterminoall   => p_allsubtermino,
																				 p_subtermino      => p_subtermino,
																				 p_codcomarcaall   => p_allcomarca,
																				 p_codcomarca      => p_comarca);


		FOR a IN 1 .. p_campos_mascaras.COUNT LOOP

			IF p_campos_mascaras(a).cod = 116 THEN
				v_consulta := v_consulta || ' and CODTIPOMARCOPLANTAC = ' || p_campos_mascaras(a).valor || '';
			ELSIF p_campos_mascaras(a).cod = 231 THEN
        --EDAD
				v_consulta := v_consulta || ' and EDADDESDE <= ' || p_campos_mascaras(a).valor;
      ELSIF p_campos_mascaras(a).cod = 232 THEN
        --EDAD
				v_consulta := v_consulta || ' and EDADHASTA >= ' || p_campos_mascaras(a).valor;
			ELSIF p_campos_mascaras(a).cod = 106 THEN
				v_consulta := v_consulta || ' and CODCARACTEXPLOTACION = ' || p_campos_mascaras(a).valor || '';
			ELSIF p_campos_mascaras(a).cod = 226 THEN
        --DENSIDAD
				v_consulta := v_consulta || ' and DENSIDADDESDE <= ' || p_campos_mascaras(a).valor || '';
			ELSIF p_campos_mascaras(a).cod = 227 THEN
        --DENSIDAD
				v_consulta := v_consulta || ' and DENSIDADHASTA >= ' || p_campos_mascaras(a).valor || '';
			ELSIF p_campos_mascaras(a).cod = 235 THEN
        --Fecha de recoleccion
				v_consulta := v_consulta || ' and FRECOLDESDE >= ' || to_date(p_campos_mascaras(a).valor,
																																			'dd/mm/yyyy') || '';
			ELSIF p_campos_mascaras(a).cod = 236 THEN
        --Fecha de recoleccion
				v_consulta := v_consulta || ' and FRECOLHASTA <= ' || to_date(p_campos_mascaras(a).valor,
																																			'dd/mm/yyyy') || '';
				/*ELSIF p_campos_mascaras(a).cod = 235 THEN
          v_consulta := v_consulta || ' and FRECOLDESDE = ' || to_date(p_campos_mascaras(a).valor,
                                                                       'dd/mm/yyyy') || '';
        ELSIF p_campos_mascaras(a).cod = 236 THEN
          v_consulta := v_consulta || ' and FRECOLHASTA = ' || to_date(p_campos_mascaras(a).valor,
                                                                       'dd/mm/yyyy') || '';*/
			ELSIF p_campos_mascaras(a).cod = 244 THEN
        --NUM. UNIDADES
				v_consulta := v_consulta || ' and NUMUDSDESDE >= ' || p_campos_mascaras(a).valor || '';
			ELSIF p_campos_mascaras(a).cod = 245 THEN
        --NUM. UNIDADES
				v_consulta := v_consulta || ' and NUMUDSHASTA <= ' || p_campos_mascaras(a).valor || '';
			ELSIF p_campos_mascaras(a).cod = 617 THEN
				v_consulta := v_consulta || ' and NUMANIOSPODA = ' || p_campos_mascaras(a).valor || '';
			ELSIF p_campos_mascaras(a).cod = 123 THEN
				v_consulta := v_consulta || ' and CODSISTEMACULTIVO = ' || p_campos_mascaras(a).valor || '';
			ELSIF p_campos_mascaras(a).cod = 616 THEN
				v_consulta := v_consulta || ' and CODSISTEMAPRODUCCION = ' || p_campos_mascaras(a).valor || '';
			ELSIF p_campos_mascaras(a).cod = 131 THEN
				v_consulta := v_consulta || ' and CODSISTEMACONDUCCION = ' || p_campos_mascaras(a).valor || '';
			ELSIF p_campos_mascaras(a).cod = 173 THEN
				v_consulta := v_consulta || ' and CODTIPOPLANTACION = ' || p_campos_mascaras(a).valor || '';
			ELSIF p_campos_mascaras(a).cod = 133 THEN
				v_consulta := v_consulta || ' and CODPRACTICACULTURAL = ' || p_campos_mascaras(a).valor || '';
			END IF;


		END LOOP;

		IF p_tablardto IS NOT NULL THEN
			v_consulta := v_consulta || ' and TABLARDTOS = ' || p_tablardto || '';
		END IF;

    -- MPM 0912
    -- Se ordena el resultado ascendentemento por cultivo, variedad, provincia, comarca y termino
    -- y descendentemente por subtermino
    v_consulta := v_consulta || ' order by codcultivo asc, codvariedad asc, codprovincia asc, codtermino asc, subtermino desc';

		p(v_consulta);

		OPEN v_cursor FOR v_consulta;
    -- Independientemente del numero de registros resultantes, se cogen los datos del primero
    FETCH v_cursor INTO v_res;
    IF (v_cursor%NOTFOUND = FALSE) THEN
        v_limites(1).limiteinfrdto := v_res.limiteinfrdto;
		    v_limites(1).limitesuprdto := v_res.limitesuprdto;
		    v_limites(1).apprdto := v_res.apprdto;
        v_limites(1).pctaplrdto := v_res.pctaplrdto/100;
    END IF;
    CLOSE v_cursor;


		/*LOOP
			FETCH v_cursor
				INTO v_res;
			EXIT WHEN v_cursor%NOTFOUND;
			v_limites(v_ind).limiteinfrdto := v_res.limiteinfrdto;
			v_limites(v_ind).limitesuprdto := v_res.limitesuprdto;
			v_limites(v_ind).apprdto := v_res.apprdto;
      v_limites(v_ind).pctaplrdto := v_res.pctaplrdto/100;
			v_ind := v_ind + 1;
		END LOOP;
		CLOSE v_cursor;*/
    --

		RETURN v_limites;
	END;






	FUNCTION fn_getMascLimitesRendimiento(p_lineaseguroid VARCHAR2
																				 ,p_modulo        VARCHAR2
																				 ,p_codcultivo    VARCHAR2
																				 ,p_codvariedad   VARCHAR2
																				 ,p_provincia     VARCHAR2
																				 ,p_comarca       VARCHAR2
																				 ,p_termino       VARCHAR2
																				 ,p_subtermino    VARCHAR2) RETURN t_masc_lim_rendim IS
		v_mascara t_masc_lim_rendim;



	BEGIN

		v_mascara := fn_filtromaslimrendimiento(p_lineaseguroid  => p_lineaseguroid,
																						p_modulo         => p_modulo,
																						p_codcultivo     => p_codcultivo,
																						p_codvariedad    => p_codvariedad,
																						p_provincia      => p_provincia,
																						p_comarca        => p_comarca,
																						p_termino        => p_termino,
																						p_subtermino     => p_subtermino,
																						p_allcodcultivo  => 'N',
																						p_allcodvariedad => 'N',
																						p_allprovincia   => 'N',
																						p_allcomarca     => 'N',
																						p_alltermino     => 'N',
																						p_allsubtermino  => 'N');

		IF v_mascara.COUNT = 0 THEN
			--Si no recupera datos filtramos por los datos genericos. Segun el siguiente orden:
			--1. Subtermino = 9; 2. Termino = 999; 3. Comarca = 99
			--4. Provincia = 99; 5. Variedad = 999; 6. Cultivo = 999
			v_mascara.DELETE;
			v_mascara := fn_filtromaslimrendimiento(p_lineaseguroid  => p_lineaseguroid,
																							p_modulo         => p_modulo,
																							p_codcultivo     => p_codcultivo,
																							p_codvariedad    => p_codvariedad,
																							p_provincia      => p_provincia,
																							p_comarca        => p_comarca,
																							p_termino        => p_termino,
																							p_subtermino     => p_subtermino,
																							p_allcodcultivo  => 'N',
																							p_allcodvariedad => 'N',
																							p_allprovincia   => 'N',
																							p_allcomarca     => 'N',
																							p_alltermino     => 'N',
																							p_allsubtermino  => 'S');
			IF v_mascara.COUNT = 0 THEN
				--Si no recupera datos filtramos por los datos genericos. Segun el siguiente orden:
				--1. Subtermino = 9; 2. Termino = 999; 3. Comarca = 99
				--4. Provincia = 99; 5. Variedad = 999; 6. Cultivo = 999
				v_mascara.DELETE;
				v_mascara := fn_filtromaslimrendimiento(p_lineaseguroid  => p_lineaseguroid,
																								p_modulo         => p_modulo,
																								p_codcultivo     => p_codcultivo,
																								p_codvariedad    => p_codvariedad,
																								p_provincia      => p_provincia,
																								p_comarca        => p_comarca,
																								p_termino        => p_termino,
																								p_subtermino     => p_subtermino,
																								p_allcodcultivo  => 'N',
																								p_allcodvariedad => 'N',
																								p_allprovincia   => 'N',
																								p_allcomarca     => 'N',
																								p_alltermino     => 'S',
																								p_allsubtermino  => 'S');
				IF v_mascara.COUNT = 0 THEN
					--Si no recupera datos filtramos por los datos genericos. Segun el siguiente orden:
					--1. Subtermino = 9; 2. Termino = 999; 3. Comarca = 99
					--4. Provincia = 99; 5. Variedad = 999; 6. Cultivo = 999
					v_mascara.DELETE;
					v_mascara := fn_filtromaslimrendimiento(p_lineaseguroid  => p_lineaseguroid,
																									p_modulo         => p_modulo,
																									p_codcultivo     => p_codcultivo,
																									p_codvariedad    => p_codvariedad,
																									p_provincia      => p_provincia,
																									p_comarca        => p_comarca,
																									p_termino        => p_termino,
																									p_subtermino     => p_subtermino,
																									p_allcodcultivo  => 'N',
																									p_allcodvariedad => 'N',
																									p_allprovincia   => 'N',
																									p_allcomarca     => 'S',
																									p_alltermino     => 'S',
																									p_allsubtermino  => 'S');
					IF v_mascara.COUNT = 0 THEN
						--Si no recupera datos filtramos por los datos genericos. Segun el siguiente orden:
						--1. Subtermino = 9; 2. Termino = 999; 3. Comarca = 99
						--4. Provincia = 99; 5. Variedad = 999; 6. Cultivo = 999
						v_mascara.DELETE;
						v_mascara := fn_filtromaslimrendimiento(p_lineaseguroid  => p_lineaseguroid,
																										p_modulo         => p_modulo,
																										p_codcultivo     => p_codcultivo,
																										p_codvariedad    => p_codvariedad,
																										p_provincia      => p_provincia,
																										p_comarca        => p_comarca,
																										p_termino        => p_termino,
																										p_subtermino     => p_subtermino,
																										p_allcodcultivo  => 'N',
																										p_allcodvariedad => 'N',
																										p_allprovincia   => 'S',
																										p_allcomarca     => 'S',
																										p_alltermino     => 'S',
																										p_allsubtermino  => 'S');
						IF v_mascara.COUNT = 0 THEN
							--Si no recupera datos filtramos por los datos genericos. Segun el siguiente orden:
							--1. Subtermino = 9; 2. Termino = 999; 3. Comarca = 99
							--4. Provincia = 99; 5. Variedad = 999; 6. Cultivo = 999
							v_mascara.DELETE;
							v_mascara := fn_filtromaslimrendimiento(p_lineaseguroid  => p_lineaseguroid,
																											p_modulo         => p_modulo,
																											p_codcultivo     => p_codcultivo,
																											p_codvariedad    => p_codvariedad,
																											p_provincia      => p_provincia,
																											p_comarca        => p_comarca,
																											p_termino        => p_termino,
																											p_subtermino     => p_subtermino,
																											p_allcodcultivo  => 'N',
																											p_allcodvariedad => 'S',
																											p_allprovincia   => 'S',
																											p_allcomarca     => 'S',
																											p_alltermino     => 'S',
																											p_allsubtermino  => 'S');
							IF v_mascara.COUNT = 0 THEN
								--Si no recupera datos filtramos por los datos genericos. Segun el siguiente orden:
								--1. Subtermino = 9; 2. Termino = 999; 3. Comarca = 99
								--4. Provincia = 99; 5. Variedad = 999; 6. Cultivo = 999
								v_mascara.DELETE;
								v_mascara := fn_filtromaslimrendimiento(p_lineaseguroid  => p_lineaseguroid,
																												p_modulo         => p_modulo,
																												p_codcultivo     => p_codcultivo,
																												p_codvariedad    => p_codvariedad,
																												p_provincia      => p_provincia,
																												p_comarca        => p_comarca,
																												p_termino        => p_termino,
																												p_subtermino     => p_subtermino,
																												p_allcodcultivo  => 'S',
																												p_allcodvariedad => 'S',
																												p_allprovincia   => 'S',
																												p_allcomarca     => 'S',
																												p_alltermino     => 'S',
																												p_allsubtermino  => 'S');
							END IF;
						END IF;
					END IF;
				END IF;
			END IF;
		END IF;

		RETURN v_mascara;
	END fn_getMascLimitesRendimiento;

	FUNCTION fn_filtromaslimrendimiento(p_lineaseguroid  VARCHAR2
																		 ,p_modulo         VARCHAR2
																		 ,p_codcultivo     VARCHAR2
																		 ,p_codvariedad    VARCHAR2
																		 ,p_provincia      VARCHAR2
																		 ,p_comarca        VARCHAR2
																		 ,p_termino        VARCHAR2
																		 ,p_subtermino     VARCHAR2
																		 ,p_allcodcultivo  VARCHAR2 DEFAULT 'N'
																		 ,p_allcodvariedad VARCHAR2 DEFAULT 'N'
																		 ,p_allprovincia   VARCHAR2 DEFAULT 'N'
																		 ,p_allcomarca     VARCHAR2 DEFAULT 'N'
																		 ,p_alltermino     VARCHAR2 DEFAULT 'N'
																		 ,p_allsubtermino  VARCHAR2 DEFAULT 'N') RETURN t_masc_lim_rendim IS

		v_mascara t_masc_lim_rendim;

		v_consulta VARCHAR2(32767) := NULL;
		TYPE cur_typ IS REF CURSOR;
		v_cursor cur_typ;
		v_ind    NUMBER := 1;
		v_res    tb_sc_c_masc_limites_rdtos%ROWTYPE;

	BEGIN

		v_consulta := 'select * ';
    v_consulta := v_consulta || 'from tb_sc_c_masc_limites_rdtos lr ';
    v_consulta := v_consulta || 'where LINEASEGUROID = ' || p_lineaseguroid || ' ';
		v_consulta := v_consulta || ' and CODMODULO = ''' || p_modulo || ''' ';
		v_consulta := v_consulta || fn_where(p_codcultivoall   => p_allcodcultivo,
																				 p_codcultivo      => p_codcultivo,
																				 p_codvariedadall  => p_allcodvariedad,
																				 p_codvariedad     => p_codvariedad,
																				 p_codprovinciaall => p_allprovincia,
																				 p_codprovincia    => p_provincia,
																				 p_codterminoall   => p_alltermino,
																				 p_codtermino      => p_termino,
																				 p_subterminoall   => p_allsubtermino,
																				 p_subtermino      => p_subtermino,
																				 p_codcomarcaall   => p_allcomarca,
																				 p_codcomarca      => p_comarca);

		p(v_consulta);


		OPEN v_cursor FOR v_consulta;
		LOOP
			FETCH v_cursor
				INTO v_res;
			EXIT WHEN v_cursor%NOTFOUND;
			v_mascara(v_ind).lineaseguroid := v_res.lineaseguroid;
			v_mascara(v_ind).codmodulo := v_res.codmodulo;
			v_mascara(v_ind).codcultivo := v_res.codcultivo;
			v_mascara(v_ind).codvariedad := v_res.codvariedad;
			v_mascara(v_ind).codprovincia := v_res.codprovincia;
			v_mascara(v_ind).codtermino := v_res.codtermino;
			v_mascara(v_ind).subtermino := v_res.subtermino;
			v_mascara(v_ind).codconcepto := v_res.codconcepto;
			v_mascara(v_ind).codcomarca := v_res.codcomarca;
			v_ind := v_ind + 1;
		END LOOP;
		CLOSE v_cursor;
		RETURN v_mascara;
	END;


	FUNCTION fn_checkrendcaracesp(p_lineaseguroid VARCHAR2) RETURN BOOLEAN IS
		v_result BOOLEAN := FALSE;
		v_hay    NUMBER := 0;
	BEGIN

		SELECT COUNT(*)
			INTO v_hay
			FROM tb_sc_c_masc_rdtos_caract_e e
		 WHERE e.lineaseguroid = p_lineaseguroid;

		IF v_hay > 0 THEN
			v_result := TRUE;
		ELSE
			v_result := FALSE;
		END IF;

		RETURN v_result;
	END fn_checkrendcaracesp;



	FUNCTION fn_getMascaraRdtosCaracEsp(p_lineaseguroid VARCHAR2
																		 ,p_modulo        VARCHAR2
																		 ,p_codcultivo    VARCHAR2
																		 ,p_codvariedad   VARCHAR2
																		 ,p_provincia     VARCHAR2
																		 ,p_comarca       VARCHAR2
																		 ,p_termino       VARCHAR2
																		 ,p_subtermino    VARCHAR2) RETURN t_ren_car_expecificas IS

		v_mascaras t_ren_car_expecificas;

		v_codcultivo  tb_sc_c_masc_rdtos_caract_e.codcultivo%TYPE := NULL;
		v_codvariedad tb_sc_c_masc_rdtos_caract_e.codvariedad%TYPE := NULL;
		v_provincia   tb_sc_c_masc_rdtos_caract_e.codprovincia%TYPE := NULL;
		v_comarca     tb_sc_c_masc_rdtos_caract_e.codcomarca%TYPE := NULL;
		v_termino     tb_sc_c_masc_rdtos_caract_e.codtermino%TYPE := NULL;
		v_subtermino  tb_sc_c_masc_rdtos_caract_e.subtermino%TYPE := NULL;

	BEGIN

		IF p_codcultivo IS NULL OR
			 p_codcultivo IS NULL THEN
			v_codcultivo := NULL;
		ELSE
			v_codcultivo := p_codcultivo;
		END IF;

		IF p_codvariedad IS NULL OR
			 p_codvariedad IS NULL THEN
			v_codvariedad := NULL;
		ELSE
			v_codvariedad := p_codvariedad;
		END IF;

		IF p_provincia IS NULL OR
			 p_provincia IS NULL THEN
			v_provincia := NULL;
		ELSE
			v_provincia := p_provincia;
		END IF;

		IF p_comarca IS NULL OR
			 p_comarca IS NULL THEN
			v_comarca := NULL;
		ELSE
			v_comarca := p_comarca;
		END IF;

		IF p_termino IS NULL OR
			 p_termino IS NULL THEN
			v_termino := NULL;
		ELSE
			v_termino := p_termino;
		END IF;

		IF p_subtermino IS NULL OR
			 p_subtermino IS NULL THEN
			v_subtermino := NULL;
		ELSE
			v_subtermino := p_subtermino;
		END IF;

		v_mascaras := pq_calcula_produccion.fn_getCritMascRdtosCaracEsp(p_lineaseguroid   => p_lineaseguroid,
																						p_codcultivoall   => 'N',
																						p_codcultivo      => v_codcultivo,
																						p_codvariedadall  => 'N',
																						p_codvariedad     => v_codvariedad,
																						p_codprovinciaall => 'N',
																						p_codprovincia    => v_provincia,
																						p_codterminoall   => 'N',
																						p_codtermino      => v_termino,
																						p_subterminoall   => 'N',
																						p_subtermino      => v_subtermino,
																						p_codcomarcaall   => 'N',
																						p_codcomarca      => v_comarca,
																						p_modulos         => p_modulo);

		IF v_mascaras.COUNT = 0 THEN
			--Si no recupera datos filtramos por los datos genericos. Segun el siguiente orden:
			--1. Subtermino = 9; 2. Termino = 999; 3. Comarca = 99
			--4. Provincia = 99; 5. Variedad = 999; 6. Cultivo = 999
			v_mascaras.DELETE;
			v_mascaras := pq_calcula_produccion.fn_getCritMascRdtosCaracEsp(p_lineaseguroid   => p_lineaseguroid,
																							p_codcultivoall   => 'N',
																							p_codcultivo      => v_codcultivo,
																							p_codvariedadall  => 'N',
																							p_codvariedad     => v_codvariedad,
																							p_codprovinciaall => 'N',
																							p_codprovincia    => v_provincia,
																							p_codterminoall   => 'N',
																							p_codtermino      => v_termino,
																							p_subterminoall   => 'S',
																							p_subtermino      => v_subtermino,
																							p_codcomarcaall   => 'N',
																							p_codcomarca      => v_comarca,
																							p_modulos         => p_modulo);


			IF v_mascaras.COUNT = 0 THEN
				v_mascaras.DELETE;
				v_mascaras := pq_calcula_produccion.fn_getCritMascRdtosCaracEsp(p_lineaseguroid   => p_lineaseguroid,
																								p_codcultivoall   => 'N',
																								p_codcultivo      => v_codcultivo,
																								p_codvariedadall  => 'N',
																								p_codvariedad     => v_codvariedad,
																								p_codprovinciaall => 'N',
																								p_codprovincia    => v_provincia,
																								p_codterminoall   => 'S',
																								p_codtermino      => v_termino,
																								p_subterminoall   => 'S',
																								p_subtermino      => v_subtermino,
																								p_codcomarcaall   => 'N',
																								p_codcomarca      => v_comarca,
																								p_modulos         => p_modulo);
				IF v_mascaras.COUNT = 0 THEN
					v_mascaras.DELETE;
					v_mascaras := pq_calcula_produccion.fn_getCritMascRdtosCaracEsp(p_lineaseguroid   => p_lineaseguroid,
																									p_codcultivoall   => 'N',
																									p_codcultivo      => v_codcultivo,
																									p_codvariedadall  => 'N',
																									p_codvariedad     => v_codvariedad,
																									p_codprovinciaall => 'N',
																									p_codprovincia    => v_provincia,
																									p_codterminoall   => 'S',
																									p_codtermino      => v_termino,
																									p_subterminoall   => 'S',
																									p_subtermino      => v_subtermino,
																									p_codcomarcaall   => 'S',
																									p_codcomarca      => v_comarca,
																									p_modulos         => p_modulo);
					IF v_mascaras.COUNT = 0 THEN
						v_mascaras.DELETE;
						v_mascaras := pq_calcula_produccion.fn_getCritMascRdtosCaracEsp(p_lineaseguroid   => p_lineaseguroid,
																										p_codcultivoall   => 'N',
																										p_codcultivo      => v_codcultivo,
																										p_codvariedadall  => 'N',
																										p_codvariedad     => v_codvariedad,
																										p_codprovinciaall => 'S',
																										p_codprovincia    => v_provincia,
																										p_codterminoall   => 'S',
																										p_codtermino      => v_termino,
																										p_subterminoall   => 'S',
																										p_subtermino      => v_subtermino,
																										p_codcomarcaall   => 'S',
																										p_codcomarca      => v_comarca,
																										p_modulos         => p_modulo);
						IF v_mascaras.COUNT = 0 THEN
							v_mascaras.DELETE;
							v_mascaras := pq_calcula_produccion.fn_getCritMascRdtosCaracEsp(p_lineaseguroid   => p_lineaseguroid,
																											p_codcultivoall   => 'N',
																											p_codcultivo      => v_codcultivo,
																											p_codvariedadall  => 'S',
																											p_codvariedad     => v_codvariedad,
																											p_codprovinciaall => 'S',
																											p_codprovincia    => v_provincia,
																											p_codterminoall   => 'S',
																											p_codtermino      => v_termino,
																											p_subterminoall   => 'S',
																											p_subtermino      => v_subtermino,
																											p_codcomarcaall   => 'S',
																											p_codcomarca      => v_comarca,
																											p_modulos         => p_modulo);
							IF v_mascaras.COUNT = 0 THEN
								v_mascaras.DELETE;
								v_mascaras := pq_calcula_produccion.fn_getCritMascRdtosCaracEsp(p_lineaseguroid   => p_lineaseguroid,
																												p_codcultivoall   => 'S',
																												p_codcultivo      => v_codcultivo,
																												p_codvariedadall  => 'S',
																												p_codvariedad     => v_codvariedad,
																												p_codprovinciaall => 'S',
																												p_codprovincia    => v_provincia,
																												p_codterminoall   => 'S',
																												p_codtermino      => v_termino,
																												p_subterminoall   => 'S',
																												p_subtermino      => v_subtermino,
																												p_codcomarcaall   => 'S',
																												p_codcomarca      => v_comarca,
																												p_modulos         => p_modulo);
							END IF;
						END IF;
					END IF;
				END IF;
			END IF;
		END IF;

		RETURN v_mascaras;
	END fn_getMascaraRdtosCaracEsp;


	FUNCTION fn_getCritMascRdtosCaracEsp(p_lineaseguroid   VARCHAR2
																		,p_codcultivoall   VARCHAR2 DEFAULT 'N'
																		,p_codcultivo      VARCHAR2
																		,p_codvariedadall  VARCHAR2 DEFAULT 'N'
																		,p_codvariedad     VARCHAR2
																		,p_codprovinciaall VARCHAR2 DEFAULT 'N'
																		,p_codprovincia    VARCHAR2
																		,p_codterminoall   VARCHAR2 DEFAULT 'N'
																		,p_codtermino      VARCHAR2
																		,p_subterminoall   VARCHAR2 DEFAULT 'N'
																		,p_subtermino      VARCHAR2
																		,p_codcomarcaall   VARCHAR2 DEFAULT 'N'
																		,p_codcomarca      VARCHAR2
																		,p_modulos         VARCHAR2) RETURN t_ren_car_expecificas IS

		v_mascaras t_ren_car_expecificas;
		v_consulta VARCHAR2(32767) := NULL;
		TYPE cur_typ IS REF CURSOR;
		v_cursor cur_typ;
		v_ind    NUMBER := 1;
		v_res    tb_sc_c_masc_rdtos_caract_e%ROWTYPE;

	BEGIN

		v_consulta := 'select * from tb_sc_c_masc_rdtos_caract_e where LINEASEGUROID = ' || p_lineaseguroid || ' ';
		v_consulta := v_consulta || ' and CODMODULO = ''' || p_modulos || ''' ';
		v_consulta := v_consulta || fn_where(p_codcultivoall   => p_codcultivoall,
																				 p_codcultivo      => p_codcultivo,
																				 p_codvariedadall  => p_codvariedadall,
																				 p_codvariedad     => p_codvariedad,
																				 p_codprovinciaall => p_codprovinciaall,
																				 p_codprovincia    => p_codprovincia,
																				 p_codterminoall   => p_codterminoall,
																				 p_codtermino      => p_codtermino,
																				 p_subterminoall   => p_subterminoall,
																				 p_subtermino      => p_subtermino,
																				 p_codcomarcaall   => p_codcomarcaall,
																				 p_codcomarca      => p_codcomarca);

		p(v_consulta);


		OPEN v_cursor FOR v_consulta;
		LOOP
			FETCH v_cursor
				INTO v_res;
			EXIT WHEN v_cursor%NOTFOUND;
			v_mascaras(v_ind).lineaseguroid := v_res.lineaseguroid;
			v_mascaras(v_ind).codmodulo := v_res.codmodulo;
			v_mascaras(v_ind).codcultivo := v_res.codcultivo;
			v_mascaras(v_ind).codvariedad := v_res.codvariedad;
			v_mascaras(v_ind).codprovincia := v_res.codprovincia;
			v_mascaras(v_ind).codtermino := v_res.codtermino;
			v_mascaras(v_ind).subtermino := v_res.subtermino;
			v_mascaras(v_ind).codconcepto := v_res.codconcepto;
			v_mascaras(v_ind).codcomarca := v_res.codcomarca;
			v_ind := v_ind + 1;
		END LOOP;
		CLOSE v_cursor;

		RETURN v_mascaras;

	END fn_getCritMascRdtosCaracEsp;

	FUNCTION fn_where(p_codcultivoall   VARCHAR2 DEFAULT 'N'
									 ,p_codcultivo      VARCHAR2
									 ,p_codvariedadall  VARCHAR2 DEFAULT 'N'
									 ,p_codvariedad     VARCHAR2
									 ,p_codprovinciaall VARCHAR2 DEFAULT 'N'
									 ,p_codprovincia    VARCHAR2
									 ,p_codterminoall   VARCHAR2 DEFAULT 'N'
									 ,p_codtermino      VARCHAR2
									 ,p_subterminoall   VARCHAR2 DEFAULT 'N'
									 ,p_subtermino      VARCHAR2
									 ,p_codcomarcaall   VARCHAR2 DEFAULT 'N'
									 ,p_codcomarca      VARCHAR2) RETURN VARCHAR2 IS
		v_consulta VARCHAR2(32767) := NULL;

	BEGIN

		IF p_codcultivoall = 'N' THEN
			v_consulta := ' and codcultivo = ' || p_codcultivo || ' ';
		ELSE
			v_consulta := ' and codcultivo in (' || p_codcultivo || ', 999 )';
		END IF;

		IF p_codvariedadall = 'N' THEN
			v_consulta := v_consulta || ' and codvariedad = ' || p_codvariedad || ' ';
		ELSE
			v_consulta := v_consulta || ' and codvariedad in (' || p_codvariedad || ', 999 )';
		END IF;

		IF p_codprovinciaall = 'N' THEN
			v_consulta := v_consulta || ' and CODPROVINCIA = ' || p_codprovincia || ' ';
		ELSE
			v_consulta := v_consulta || ' and CODPROVINCIA in (' || p_codprovincia || ', 99 )';
		END IF;

		IF p_codterminoall = 'N' THEN
			v_consulta := v_consulta || ' and CODTERMINO = ' || p_codtermino || ' ';
		ELSE
			v_consulta := v_consulta || ' and CODTERMINO in (' || p_codtermino || ', 999 )';
		END IF;

		IF p_subterminoall = 'N' THEN
			v_consulta := v_consulta || ' and SUBTERMINO = ''' || p_subtermino || ''' ';
		ELSE
			v_consulta := v_consulta || ' and SUBTERMINO in (''' || p_subtermino || ''', ''9'' )';
		END IF;

		IF p_codcomarcaall = 'N' THEN
			v_consulta := v_consulta || ' and CODCOMARCA = ' || p_codcomarca || ' ';
		ELSE
			v_consulta := v_consulta || ' and CODCOMARCA in (' || p_codcomarca || ', 99 )';
		END IF;


		RETURN v_consulta;
	END;


	FUNCTION fn_valorpordefectofitros(p_campos_mascara pq_calcula_produccion.t_campos_mascaras) RETURN pq_calcula_produccion.t_campos_mascaras IS

		v_result pq_calcula_produccion.t_campos_mascaras;

	BEGIN


		FOR a IN 1 .. p_campos_mascara.COUNT LOOP
			IF p_campos_mascara(a).cod IN (116,
								 616,
								 173,
								 133,
								 126,
								 110) THEN
				IF p_campos_mascara(a).valor IS NULL OR
					 p_campos_mascara(a).valor = '' OR
					 p_campos_mascara(a).valor = -1 THEN
					v_result(a).cod := p_campos_mascara(a).cod;
					v_result(a).valor := '0';
				ELSE
					v_result(a).cod := p_campos_mascara(a).cod;
					v_result(a).valor := p_campos_mascara(a).valor;
				END IF;
			ELSE
				v_result(a).cod := p_campos_mascara(a).cod;
				v_result(a).valor := p_campos_mascara(a).valor;
			END IF;
		END LOOP;

		RETURN v_result;
	END fn_valorpordefectofitros;





	FUNCTION fn_damelistarencaracesp(p_lineaseguroid VARCHAR2
																	,p_modulo        VARCHAR2
																	,p_codcultivo    VARCHAR2
																	,p_codvariedad   VARCHAR2
																	,p_provincia     VARCHAR2
																	,p_comarca       VARCHAR2
																	,p_termino       VARCHAR2
																	,p_subtermino    VARCHAR2
																	,p_filtromascara t_campos_mascaras) RETURN pq_calcula_produccion.t_rdtos_caract_esp IS

		v_result      pq_calcula_produccion.t_rdtos_caract_esp;
		--v_codcultivo  tb_sc_c_masc_rdtos_caract_e.codcultivo%TYPE := NULL;
		--v_codvariedad tb_sc_c_masc_rdtos_caract_e.codvariedad%TYPE := NULL;
		--v_provincia   tb_sc_c_masc_rdtos_caract_e.codprovincia%TYPE := NULL;
		--v_comarca     tb_sc_c_masc_rdtos_caract_e.codcomarca%TYPE := NULL;
		--v_termino     tb_sc_c_masc_rdtos_caract_e.codtermino%TYPE := NULL;
		--v_subtermino  tb_sc_c_masc_rdtos_caract_e.subtermino%TYPE := NULL;

		v_codsistemaconduccion  tb_sc_c_rdtos_caract_esp.codsistemaconduccion%TYPE := NULL;
		v_codpracticacultural   tb_sc_c_rdtos_caract_esp.codpracticacultural%TYPE := NULL;
		v_coddenominacionorigen tb_sc_c_rdtos_caract_esp.coddenomorigen%TYPE := NULL;

	BEGIN


		/*IF p_codcultivo IS NULL OR
			 p_codcultivo IS NULL THEN
			v_codcultivo := NULL;
		ELSE
			v_codcultivo := p_codcultivo;
		END IF;

		IF p_codvariedad IS NULL OR
			 p_codvariedad IS NULL THEN
			v_codvariedad := NULL;
		ELSE
			v_codvariedad := p_codvariedad;
		END IF;

		IF p_provincia IS NULL OR
			 p_provincia IS NULL THEN
			v_provincia := NULL;
		ELSE
			v_provincia := p_provincia;
		END IF;

		IF p_comarca IS NULL OR
			 p_comarca IS NULL THEN
			v_comarca := NULL;
		ELSE
			v_comarca := p_comarca;
		END IF;

		IF p_termino IS NULL OR
			 p_termino IS NULL THEN
			v_termino := NULL;
		ELSE
			v_termino := p_termino;
		END IF;

		IF p_subtermino IS NULL OR
			 p_subtermino IS NULL THEN
			v_subtermino := NULL;
		ELSE
			v_subtermino := p_subtermino;
		END IF;*/


		FOR a IN 1 .. p_filtromascara.COUNT LOOP
			IF p_filtromascara(a).cod = 131 THEN
				v_codsistemaconduccion := p_filtromascara(a).valor;
			ELSIF p_filtromascara(a).cod = 133 THEN
				v_codpracticacultural := p_filtromascara(a).valor;
			ELSIF p_filtromascara(a).cod = 107 THEN
				v_coddenominacionorigen := p_filtromascara(a).valor;
			ELSE
				p('el condigo concepto ' || p_filtromascara(a).cod || ' no esta en el filtro para el calculo de los limites de rendimiento');
			END IF;
		END LOOP;

		-- 3.2. Obtenemos los limites

		v_result := fn_getcriteriardtoscaracesp(p_lineaseguroid         => p_lineaseguroid,
																						p_codmodulo             => p_modulo,
																						p_codcultivo            => p_codcultivo,
																						p_codvariedad           => p_codvariedad,
																						p_codprovincia          => p_provincia,
																						p_codtermino            => p_termino,
																						p_subtermino            => p_subtermino,
																						p_codcomarca            => p_comarca,
																						p_codsistemaconduccion  => v_codsistemaconduccion,
																						p_codpracticacultural   => v_codpracticacultural,
																						p_coddenominacionorigen => v_coddenominacionorigen,
																						p_allcultivos           => 'N',
																						p_allvariedades         => 'N',
																						p_allprovincias         => 'N',
																						p_allterminos           => 'N',
																						p_allsubterminos        => 'N',
																						p_allcomarcas           => 'N');

		IF v_result.COUNT = 0 THEN
			--Si no recupera datos filtramos por los datos genericos. Segun el siguiente orden:
			--1. Subtermino = 9; 2. Termino = 999; 3. Comarca = 99
			--4. Provincia = 99; 5. Variedad = 999; 6. Cultivo = 999
			v_result.DELETE;
			v_result := fn_getcriteriardtoscaracesp(p_lineaseguroid         => p_lineaseguroid,
																							p_codmodulo             => p_modulo,
																							p_codcultivo            => p_codcultivo,
																							p_codvariedad           => p_codvariedad,
																							p_codprovincia          => p_provincia,
																							p_codtermino            => p_termino,
																							p_subtermino            => p_subtermino,
																							p_codcomarca            => p_comarca,
																							p_codsistemaconduccion  => v_codsistemaconduccion,
																							p_codpracticacultural   => v_codpracticacultural,
																							p_coddenominacionorigen => v_coddenominacionorigen,
																							p_allcultivos           => 'N',
																							p_allvariedades         => 'N',
																							p_allprovincias         => 'N',
																							p_allterminos           => 'N',
																							p_allsubterminos        => 'S',
																							p_allcomarcas           => 'N');
			IF v_result.COUNT = 0 THEN
				--Si no recupera datos filtramos por los datos genericos. Segun el siguiente orden:
				--1. Subtermino = 9; 2. Termino = 999; 3. Comarca = 99
				--4. Provincia = 99; 5. Variedad = 999; 6. Cultivo = 999
				v_result.DELETE;
				v_result := fn_getcriteriardtoscaracesp(p_lineaseguroid         => p_lineaseguroid,
																								p_codmodulo             => p_modulo,
																								p_codcultivo            => p_codcultivo,
																								p_codvariedad           => p_codvariedad,
																								p_codprovincia          => p_provincia,
																								p_codtermino            => p_termino,
																								p_subtermino            => p_subtermino,
																								p_codcomarca            => p_comarca,
																								p_codsistemaconduccion  => v_codsistemaconduccion,
																								p_codpracticacultural   => v_codpracticacultural,
																								p_coddenominacionorigen => v_coddenominacionorigen,
																								p_allcultivos           => 'N',
																								p_allvariedades         => 'N',
																								p_allprovincias         => 'N',
																								p_allterminos           => 'S',
																								p_allsubterminos        => 'S',
																								p_allcomarcas           => 'N');
				IF v_result.COUNT = 0 THEN
					--Si no recupera datos filtramos por los datos genericos. Segun el siguiente orden:
					--1. Subtermino = 9; 2. Termino = 999; 3. Comarca = 99
					--4. Provincia = 99; 5. Variedad = 999; 6. Cultivo = 999
					v_result.DELETE;
					v_result := fn_getcriteriardtoscaracesp(p_lineaseguroid         => p_lineaseguroid,
																									p_codmodulo             => p_modulo,
																									p_codcultivo            => p_codcultivo,
																									p_codvariedad           => p_codvariedad,
																									p_codprovincia          => p_provincia,
																									p_codtermino            => p_termino,
																									p_subtermino            => p_subtermino,
																									p_codcomarca            => p_comarca,
																									p_codsistemaconduccion  => v_codsistemaconduccion,
																									p_codpracticacultural   => v_codpracticacultural,
																									p_coddenominacionorigen => v_coddenominacionorigen,
																									p_allcultivos           => 'N',
																									p_allvariedades         => 'N',
																									p_allprovincias         => 'N',
																									p_allterminos           => 'S',
																									p_allsubterminos        => 'S',
																									p_allcomarcas           => 'S');
					IF v_result.COUNT = 0 THEN
						--Si no recupera datos filtramos por los datos genericos. Segun el siguiente orden:
						--1. Subtermino = 9; 2. Termino = 999; 3. Comarca = 99
						--4. Provincia = 99; 5. Variedad = 999; 6. Cultivo = 999
						v_result.DELETE;
						v_result := fn_getcriteriardtoscaracesp(p_lineaseguroid         => p_lineaseguroid,
																										p_codmodulo             => p_modulo,
																										p_codcultivo            => p_codcultivo,
																										p_codvariedad           => p_codvariedad,
																										p_codprovincia          => p_provincia,
																										p_codtermino            => p_termino,
																										p_subtermino            => p_subtermino,
																										p_codcomarca            => p_comarca,
																										p_codsistemaconduccion  => v_codsistemaconduccion,
																										p_codpracticacultural   => v_codpracticacultural,
																										p_coddenominacionorigen => v_coddenominacionorigen,
																										p_allcultivos           => 'N',
																										p_allvariedades         => 'S',
																										p_allprovincias         => 'N',
																										p_allterminos           => 'S',
																										p_allsubterminos        => 'S',
																										p_allcomarcas           => 'S');
						IF v_result.COUNT = 0 THEN
							--Si no recupera datos filtramos por los datos genericos. Segun el siguiente orden:
							--1. Subtermino = 9; 2. Termino = 999; 3. Comarca = 99
							--4. Provincia = 99; 5. Variedad = 999; 6. Cultivo = 999
							v_result.DELETE;
							v_result := fn_getcriteriardtoscaracesp(p_lineaseguroid         => p_lineaseguroid,
																											p_codmodulo             => p_modulo,
																											p_codcultivo            => p_codcultivo,
																											p_codvariedad           => p_codvariedad,
																											p_codprovincia          => p_provincia,
																											p_codtermino            => p_termino,
																											p_subtermino            => p_subtermino,
																											p_codcomarca            => p_comarca,
																											p_codsistemaconduccion  => v_codsistemaconduccion,
																											p_codpracticacultural   => v_codpracticacultural,
																											p_coddenominacionorigen => v_coddenominacionorigen,
																											p_allcultivos           => 'N',
																											p_allvariedades         => 'S',
																											p_allprovincias         => 'S',
																											p_allterminos           => 'S',
																											p_allsubterminos        => 'S',
																											p_allcomarcas           => 'S');
							IF v_result.COUNT = 0 THEN
								--Si no recupera datos filtramos por los datos genericos. Segun el siguiente orden:
								--1. Subtermino = 9; 2. Termino = 999; 3. Comarca = 99
								--4. Provincia = 99; 5. Variedad = 999; 6. Cultivo = 999
								v_result.DELETE;
								v_result := fn_getcriteriardtoscaracesp(p_lineaseguroid         => p_lineaseguroid,
																												p_codmodulo             => p_modulo,
																												p_codcultivo            => p_codcultivo,
																												p_codvariedad           => p_codvariedad,
																												p_codprovincia          => p_provincia,
																												p_codtermino            => p_termino,
																												p_subtermino            => p_subtermino,
																												p_codcomarca            => p_comarca,
																												p_codsistemaconduccion  => v_codsistemaconduccion,
																												p_codpracticacultural   => v_codpracticacultural,
																												p_coddenominacionorigen => v_coddenominacionorigen,
																												p_allcultivos           => 'S',
																												p_allvariedades         => 'S',
																												p_allprovincias         => 'S',
																												p_allterminos           => 'S',
																												p_allsubterminos        => 'S',
																												p_allcomarcas           => 'S');
							END IF;
						END IF;
					END IF;
				END IF;
			END IF;
		END IF;



		RETURN v_result;
	END;

	FUNCTION fn_getCriteriaRdtosCaracEsp(p_lineaseguroid         VARCHAR2
																			,p_codmodulo             VARCHAR2
																			,p_codcultivo            VARCHAR2
																			,p_codvariedad           VARCHAR2
																			,p_codprovincia          VARCHAR2
																			,p_codtermino            VARCHAR2
																			,p_subtermino            VARCHAR2
																			,p_codcomarca            VARCHAR2
																			,p_codsistemaconduccion  VARCHAR2
																			,p_codpracticacultural   VARCHAR2
																			,p_coddenominacionorigen VARCHAR2
																			,p_allcultivos           VARCHAR2 DEFAULT 'N'
																			,p_allvariedades         VARCHAR2 DEFAULT 'N'
																			,p_allprovincias         VARCHAR2 DEFAULT 'N'
																			,p_allterminos           VARCHAR2 DEFAULT 'N'
																			,p_allsubterminos        VARCHAR2 DEFAULT 'N'
																			,p_allcomarcas           VARCHAR2 DEFAULT 'N') RETURN pq_calcula_produccion.t_rdtos_caract_esp IS
		v_result pq_calcula_produccion.t_rdtos_caract_esp;

		v_consulta VARCHAR2(32767) := NULL;
		TYPE cur_typ IS REF CURSOR;
		v_cursor cur_typ;
		v_ind    NUMBER := 1;
		v_res    tb_sc_c_rdtos_caract_esp%ROWTYPE;

	BEGIN

		v_consulta := 'select * from tb_sc_c_rdtos_caract_esp where LINEASEGUROID = ' || p_lineaseguroid || ' ';
		v_consulta := v_consulta || ' and CODMODULO = ''' || p_codmodulo || ''' ';
		v_consulta := v_consulta || fn_where(p_codcultivoall   => p_allcultivos,
																				 p_codcultivo      => p_codcultivo,
																				 p_codvariedadall  => p_allvariedades,
																				 p_codvariedad     => p_codvariedad,
																				 p_codprovinciaall => p_allprovincias,
																				 p_codprovincia    => p_codprovincia,
																				 p_codterminoall   => p_allterminos,
																				 p_codtermino      => p_codtermino,
																				 p_subterminoall   => p_allsubterminos,
																				 p_subtermino      => p_subtermino,
																				 p_codcomarcaall   => p_allcomarcas,
																				 p_codcomarca      => p_codcomarca);
		IF p_coddenominacionorigen IS NOT NULL THEN
			v_consulta := v_consulta || ' and CODDENOMORIGEN = ' || p_coddenominacionorigen || ' ';
		END IF;
		IF p_codpracticacultural IS NOT NULL THEN
			v_consulta := v_consulta || ' and CODPRACTICACULTURAL = ' || p_codpracticacultural || ' ';
		END IF;
		IF p_codsistemaconduccion IS NOT NULL THEN
			v_consulta := v_consulta || ' and CODSISTEMACONDUCCION = ' || p_codsistemaconduccion || ' ';
		END IF;


		p(v_consulta);

		OPEN v_cursor FOR v_consulta;
		LOOP
			FETCH v_cursor
				INTO v_res;
			EXIT WHEN v_cursor%NOTFOUND;
			v_result(v_ind).id := v_res.id;
			v_result(v_ind).lineaseguroid := v_res.lineaseguroid;
			v_result(v_ind).codmodulo := v_res.codmodulo;
			v_result(v_ind).codcultivo := v_res.codcultivo;
			v_result(v_ind).codvariedad := v_res.codvariedad;
			v_result(v_ind).coddenomorigen := v_res.coddenomorigen;
			v_result(v_ind).codpracticacultural := v_res.codpracticacultural;
			v_result(v_ind).codsistemaconduccion := v_res.codsistemaconduccion;
			v_result(v_ind).codprovincia := v_res.codprovincia;
			v_result(v_ind).codcomarca := v_res.codcomarca;
			v_result(v_ind).codtermino := v_res.codtermino;
			v_result(v_ind).subtermino := v_res.subtermino;
			v_result(v_ind).limiteinfrdto := v_res.limiteinfrdto;
			v_result(v_ind).limitesuprdto := v_res.limitesuprdto;

			v_ind := v_ind + 1;
		END LOOP;
		CLOSE v_cursor;

		RETURN v_result;
	END fn_getCriteriaRdtosCaracEsp;



	PROCEDURE pr_prueba IS

		v_valor pq_calcula_produccion.t_capital_asegurado;
		v       pq_calcula_produccion.tipo_capital;
		va      pq_calcula_produccion.t_cod_valor;

		v1 pq_calcula_produccion.t_dat_variable;

		v2 pq_calcula_produccion.t_aseg_autorizados;

		v_medida tb_sc_c_medidas%ROWTYPE;
	BEGIN

		v_medida := pq_calcula_produccion.fn_getcoefrdtomaxaseg('181',
																														'11641419S');
		IF (v_medida.tablardto IS NULL) THEN
			p('entro en null');
			p(v_medida.tablardto);
		ELSE
			p(v_medida.tablardto);
			p('No entro en null');
		END IF;



		v_valor(1)(1).cod := 12;

		v2(1).lineaseguroid := NULL;

		v2 := pq_calcula_produccion.fn_getaseguradosautorizados(p_lineaseguroid     => 181,
																														p_nif               => 'B58057977',
																														p_modulo            => '1',
																														p_fechafingarantias => NULL,
																														p_garantizado       => NULL,
																														p_codcultivo        => NULL,
																														p_convariedad       => NULL);


		IF v2.EXISTS(1) = FALSE THEN
			p('esta vacio');
		ELSE
			p('trae valor');
		END IF;

		FOR a IN 1 .. v2.COUNT LOOP
			p(v2(a).lineaseguroid || ' ' || v2(a).codmodulo || ' ' || v2(a).codprovincia || ' ' || v2(a).codnivelriesgo);
		END LOOP;

	END;


	FUNCTION fn_extraer_campo(entrada   VARCHAR2
													 ,campo     NUMBER DEFAULT 1
													 ,separador VARCHAR2 DEFAULT '#') RETURN VARCHAR2 IS
		/*
      Este procedimiento obtiene el campo pasado en una cadena de caracteres separado por #
    */
		campo_ini NUMBER;
		salida    VARCHAR2(1300);

	BEGIN
		salida    := NULL;
		campo_ini := 1;
		FOR i IN 1 .. length(entrada) LOOP
			IF campo_ini = campo THEN
				FOR j IN i .. length(entrada) LOOP
					IF substr(entrada,
										j,
										1) = separador THEN
						RETURN salida;
					ELSE
						salida := salida || substr(entrada,
																			 j,
																			 1);
					END IF;
				END LOOP;
				EXIT;
			END IF;
			IF substr(entrada,
								i,
								1) = separador THEN
				campo_ini := campo_ini + 1;
			END IF;
		END LOOP;
		RETURN salida;
	EXCEPTION
		WHEN OTHERS THEN
			RETURN NULL;
	END;

  -------------
  FUNCTION fn_getAplicaCoefRdtos (p_lineaseguroid     VARCHAR2,
                                  p_provincia         VARCHAR2,
                                  p_comarca           VARCHAR2,
                                  p_termino           VARCHAR2,
															    p_subtermino        VARCHAR2,
                                  p_codcultivo        VARCHAR2,
															    p_codvariedad       VARCHAR2) RETURN BOOLEAN IS

  v_count NUMBER := 0;

  BEGIN

  SELECT COUNT(*)
  INTO v_count
  FROM TB_SC_C_ZONIF_APLIC_MEDIDAS z
  WHERE z.lineaseguroid = p_lineaseguroid
     AND (z.codprovincia = p_provincia OR z.codprovincia = 99)
     AND (z.codcomarca = p_comarca OR z.codcomarca= 99)
     AND (z.codtermino = p_termino OR z.codtermino = 999)
     AND (z.subtermino = p_subtermino OR z.subtermino = '9')
     AND (z.codcultivo = p_codcultivo OR z.codcultivo = 999)
     AND (z.codvariedad = p_codvariedad OR z.codvariedad = 999);

  IF v_count > 0 THEN RETURN TRUE;
  ELSE RETURN FALSE;
  END IF;

  EXCEPTION
		WHEN OTHERS THEN
			RETURN FALSE;

  END;

  --------------------
  --  MPM - 201311  --
  --------------------


  ---
  ---
  ---

  FUNCTION SPLIT(in_string VARCHAR2, delim VARCHAR2) RETURN t_array IS
    i       number := 0;
    pos     number := 0;
    lv_str  varchar2(100) := in_string;
    strings t_array;
  BEGIN
    pos := instr(lv_str, delim, 1, 1);
    WHILE (pos != 0) LOOP
      i := i + 1;
      strings(i) := substr(lv_str, 1, pos-1);
      lv_str := substr(lv_str, pos + 1, length(lv_str));
      pos := instr(lv_str, delim, 1, 1);
      If pos = 0 THEN
        strings(i + 1) := lv_str;
      END IF;
    END LOOP;
    RETURN strings;
  END SPLIT;


  --
  -- Establece los valores del SIGPAC indicados en p_sigpac en los parametros
  --
  PROCEDURE fn_getVarSIGPAC (p_sigpac IN VARCHAR2,
                            v_prov_SIGPAC OUT o02agpe0.tb_parcelas.codprovsigpac%TYPE,
                            v_term_SIGPAC OUT o02agpe0.tb_parcelas.codtermsigpac%TYPE,
                            v_agre_SIGPAC OUT o02agpe0.tb_parcelas.agrsigpac%TYPE,
                            v_zona_SIGPAC OUT o02agpe0.tb_parcelas.zonasigpac%TYPE,
                            v_polg_SIGPAC OUT o02agpe0.tb_parcelas.poligonosigpac%TYPE,
                            v_parc_SIGPAC OUT o02agpe0.tb_parcelas.parcelasigpac%TYPE) AS

  v_array_sigpac t_array;

  BEGIN
  -- Parte la cadena del SIGPAC por el caracter # y lo devuelve en un array
  Pq_Utl.log (pck || '.fn_getVarSIGPAC - ', p_sigpac, 2);
  v_array_sigpac := SPLIT(p_sigpac, '-');
  -- Rellena las variables numericas del SIGPAC
  v_prov_SIGPAC := TO_NUMBER (v_array_sigpac(1));
  Pq_Utl.log (pck || '.fn_getVarSIGPAC - v_prov_SIGPAC=', v_prov_SIGPAC, 2);
  v_term_SIGPAC := TO_NUMBER (v_array_sigpac(2));
  Pq_Utl.log (pck || '.fn_getVarSIGPAC - v_term_SIGPAC=', v_term_SIGPAC, 2);
  v_agre_SIGPAC := TO_NUMBER (v_array_sigpac(3));
  Pq_Utl.log (pck || '.fn_getVarSIGPAC - v_agre_SIGPAC=', v_agre_SIGPAC, 2);
  v_zona_SIGPAC := TO_NUMBER (v_array_sigpac(4));
  Pq_Utl.log (pck || '.fn_getVarSIGPAC - v_zona_SIGPAC=', v_zona_SIGPAC, 2);
  v_polg_SIGPAC := TO_NUMBER (v_array_sigpac(5));
  Pq_Utl.log (pck || '.fn_getVarSIGPAC - v_polg_SIGPAC=', v_polg_SIGPAC, 2);
  v_parc_SIGPAC := TO_NUMBER (v_array_sigpac(6));
  Pq_Utl.log (pck || '.fn_getVarSIGPAC - v_parc_SIGPAC=', v_parc_SIGPAC, 2);

  EXCEPTION
  WHEN OTHERS THEN
        Pq_Utl.log (pck || '.fn_getVarSIGPAC - ', SQLERRM, 2);
        v_prov_SIGPAC := NULL;
        v_term_SIGPAC := NULL;
        v_agre_SIGPAC := NULL;
        v_zona_SIGPAC := NULL;
        v_polg_SIGPAC := NULL;
        v_parc_SIGPAC := NULL;

  END fn_getVarSIGPAC;





  --
  -- Devuelve el registro de la tabla RRP asociado a la parcela indicada por los parametros
  --
  PROCEDURE fn_getRegistroRRP (p_lineaseguroid IN VARCHAR2,
                              p_modulo IN VARCHAR2,
                              p_cultivo IN VARCHAR2,
                              p_variedad IN VARCHAR2,
                              p_prov_SIGPAC IN VARCHAR2,
                              p_term_SIGPAC IN VARCHAR2,
                              p_agre_SIGPAC IN VARCHAR2,
                              p_zona_SIGPAC IN VARCHAR2,
                              p_polg_SIGPAC IN VARCHAR2,
                              p_parc_SIGPAC IN VARCHAR2,
                              p_reg_rrp OUT o02agpe0.tb_sc_c_rdto_regs_parcela%ROWTYPE) AS

  BEGIN

    SELECT * INTO p_reg_rrp
    FROM o02agpe0.tb_sc_c_rdto_regs_parcela r
    WHERE r.lineaseguroid = p_lineaseguroid AND
          r.codmodulo = p_modulo AND
          r.codcultivo = p_cultivo AND
          r.codvariedad = p_variedad AND
          r.codprovsigpac = p_prov_SIGPAC AND
          r.codtermsigpac = p_term_SIGPAC AND
          r.agrsigpac = p_agre_SIGPAC AND
          r.zonasigpac = p_zona_SIGPAC AND
          r.poligonosigpac = p_polg_SIGPAC AND
          r.parcelasigpac = p_parc_SIGPAC;

    EXCEPTION
      -- No hay informacion para la parcela indicada
      WHEN NO_DATA_FOUND THEN
           p_reg_rrp := NULL;
           Pq_Utl.log (pck || '.fn_getRegistroRRP - ', 'No se han encontrado datos para la parcela indicada en la tabla RRP', 2);
      -- Existen varios registros de RRP para los datos indicados
      WHEN TOO_MANY_ROWS THEN
           p_reg_rrp := NULL;
           Pq_Utl.log (pck || '.fn_getRegistroRRP - ', 'Se han encontrado varios registros para la parcela indicada en la tabla RRP', 2);
      -- Error inesperado
      WHEN OTHERS THEN
           p_reg_rrp := NULL;
           Pq_Utl.log (pck || '.fn_getRegistroRRP - ', 'Ocurrio un error inesperado al buscar registros para la parcela indicada en la tabla RRP', 2);

  END;


  --
  -- Devuelve el resultado de multiplicar la superficie o el numero de unidades (dependiendo de la
  -- aplicacion del rendimiento) por el rendimiento permitido para la parcela
  --
  FUNCTION fn_calculaRendimiento (v_capitales  pq_calcula_produccion.t_capital_asegurado,
                               aplic_rend   o02agpe0.tb_sc_c_rdto_regs_parcela.aplic_rendimiento%TYPE,
                               rendimiento_perm o02agpe0.tb_sc_c_rdto_regs_parcela.rendimiento_perm%TYPE) RETURN NUMBER IS

  v_codconcepto NUMBER(5);
  v_valor NUMBER;

  BEGIN
      -- Obtiene el codigo de concepto de la superficie o del numero de unidades
       IF (aplic_rend = 'S') THEN
            v_codconcepto := 258;
       ELSE
            IF (aplic_rend = 'U') THEN
               v_codconcepto := 117;
            END IF;
       END IF;

       -- Obtiene el valor de la superficio o n? de unidades de la parcela
       FOR ind IN 1 .. v_capitales.COUNT LOOP
					FOR ind2 IN 1 .. v_capitales(ind).COUNT LOOP
              IF v_capitales(ind) (ind2).cod = v_codconcepto THEN
                 v_valor := TO_NUMBER (v_capitales(ind) (ind2).valor);
                 EXIT;
              END IF;
          END LOOP;
       END LOOP;

       -- Devuelve la multiplicacion entre el rendimiento permitido y el valor de unidades o superficie
       RETURN v_valor * rendimiento_perm;

  END fn_calculaRendimiento;


  --
  -- Devuelve un boolean indicando si en las comparativas de la poliza se ha elegido la combinacion
  -- indicada por los parametros
  --
  FUNCTION fn_isElegidaComparativa (p_idpoliza IN VARCHAR2,
                                    p_modulo IN VARCHAR2,
                                    p_cpm o02agpe0.tb_sc_c_rdto_regs_parcela.codconceptoppalmod%TYPE,
                                    p_crc o02agpe0.tb_sc_c_rdto_regs_parcela.codriesgocubierto%TYPE,
                                    p_rce o02agpe0.tb_sc_c_rdto_regs_parcela.riesgo_cubierto_elegido%TYPE) RETURN BOOLEAN IS

  -- Constante que indica el codigo de concepto del riesgo cubierto elegido
  c_conc_ries_cub_eleg CONSTANT o02agpe0.tb_comparativas_poliza.codconcepto%TYPE := 363;
  v_comparativa NUMBER := 0;
  v_rce NUMBER := 0;

  BEGIN
      IF (p_rce = 'S') THEN
         v_rce := -1;
      ELSE
         v_rce := -2;
      END IF;

      SELECT COUNT(*) INTO v_comparativa
      FROM TB_COMPARATIVAS_POLIZA CP
      WHERE CP.IDPOLIZA= P_IDPOLIZA
      AND CP.CODMODULO= P_MODULO
      AND CP.CODCONCEPTO= c_conc_ries_cub_eleg
      AND CP.CODCONCEPTOPPALMOD = p_cpm
      AND CP.CODRIESGOCUBIERTO = p_crc
      AND CP.CODVALOR = v_rce;

      -- Si se encuentra alguna comparativa se devuelve true
      IF (v_comparativa > 0) THEN
         RETURN TRUE;
      ELSE
         RETURN FALSE;
      END IF;

  EXCEPTION
    -- Error inesperado, no se tendra en cuenta el riesgo cubierto
    WHEN OTHERS THEN
         RETURN FALSE;

  END fn_isElegidaComparativa;


  --
  -- Devuelve el rendimiento maximo para la parcela indicada por los parametros si
  -- existe en la tabla de rendimientos registros parcela
  --
  FUNCTION fn_getRendimientosRegParcela (p_lineaseguroid IN VARCHAR2,
                                         p_idpoliza IN VARCHAR2,
                                         p_modulo IN VARCHAR2,
                                         p_cultivo IN VARCHAR2,
                                         p_variedad IN VARCHAR2,
                                         p_sigpac IN VARCHAR2,
                                         v_capitales  pq_calcula_produccion.t_capital_asegurado) RETURN VARCHAR2 IS

  v_ries_cub_eleg o02agpe0.tb_comparativas_poliza.codvalor%TYPE;

  -- Variables para almacenar los valores del SIGPAC
  v_prov_SIGPAC o02agpe0.tb_parcelas.codprovsigpac%TYPE;
  v_term_SIGPAC o02agpe0.tb_parcelas.codtermsigpac%TYPE;
  v_agre_SIGPAC o02agpe0.tb_parcelas.agrsigpac%TYPE;
  v_zona_SIGPAC o02agpe0.tb_parcelas.zonasigpac%TYPE;
  v_polg_SIGPAC o02agpe0.tb_parcelas.poligonosigpac%TYPE;
  v_parc_SIGPAC o02agpe0.tb_parcelas.parcelasigpac%TYPE;
  -- Variable para almacenar el registro de RRP
  v_rrp_parcela o02agpe0.tb_sc_c_rdto_regs_parcela%ROWTYPE;
  -- Variable para almacenar el rendimiento obtenido
  v_rendimiento NUMBER;

  BEGIN
    Pq_Utl.log (pck || '.fn_getRendimientosRegParcela - ', 'Obtener rendimientos registros parcela', 2);

    -- Obtiene las variables del SIGPAC
    fn_getVarSIGPAC (p_sigpac, v_prov_SIGPAC, v_term_SIGPAC, v_agre_SIGPAC, v_zona_SIGPAC, v_polg_SIGPAC, v_parc_SIGPAC);

    -- Comprueba si en la tabla de rendimientos registros parcela hay datos para la actual
    fn_getRegistroRRP ( p_lineaseguroid, p_modulo, p_cultivo, p_variedad, v_prov_SIGPAC,
                        v_term_SIGPAC, v_agre_SIGPAC, v_zona_SIGPAC, v_polg_SIGPAC, v_parc_SIGPAC,
                        v_rrp_parcela);

    -- Si no hay rendimiento establecido para esta parcela sale del metodo
    IF (v_rrp_parcela.id IS NULL) THEN
       Pq_Utl.log (pck || '.fn_getRendimientosRegParcela - ', 'No hay rendimiento establecido para esta parcela', 2);
       Pq_Utl.log (pck || '.fn_getRendimientosRegParcela - ', 'Se continua con el calculo de la produccion', 2);
       RETURN NULL;
    END IF;

    -- Si hay rendimiento establecido para la parcela
    -- Si en el registro devuelto no estan informados ni el cpm ni el codigo de riesgo cubierto
    -- ni el riesgo cubierto elegido se aplica el rendimiento directamente
    IF (v_rrp_parcela.codconceptoppalmod IS NULL AND v_rrp_parcela.codriesgocubierto IS NULL
       AND v_rrp_parcela.riesgo_cubierto_elegido IS NULL) THEN
           v_rendimiento := fn_calculaRendimiento (v_capitales, v_rrp_parcela.aplic_rendimiento, v_rrp_parcela.rendimiento_perm);
           Pq_Utl.log (pck || '.fn_getRendimientosRegParcela - ', 'En el rendimiento establecido para esta parcela no estan informados ni el cpm ni el codigo de riesgo cubierto', 2);
           Pq_Utl.log (pck || '.fn_getRendimientosRegParcela - ', 'Se obtiene el rendimiento = ' || v_rendimiento, 2);
           RETURN '0#' || v_rendimiento || '#0#' || v_rrp_parcela.rendimiento_perm;
    ELSE
       Pq_Utl.log (pck || '.fn_getRendimientosRegParcela - ', 'En el rendimiento establecido para esta parcela el cpm y el codigo de riesgo cubierto si estan informados', 2);
       -- Si estan informados, se busca en las comparativas de la poliza el si se ha elegido el
       -- cpm-riesgo cubierto indicado por la tabla RRP
       IF (fn_isElegidaComparativa (p_idpoliza, p_modulo,
                                    v_rrp_parcela.codconceptoppalmod,
                                    v_rrp_parcela.codriesgocubierto,
                                    v_rrp_parcela.riesgo_cubierto_elegido)) THEN
          -- Si se ha elegido la comparativa se devuelve el rendimiento
          v_rendimiento := fn_calculaRendimiento (v_capitales, v_rrp_parcela.aplic_rendimiento, v_rrp_parcela.rendimiento_perm);
          Pq_Utl.log (pck || '.fn_getRendimientosRegParcela - ', 'En alguna comparativa se ha elegido la combinacion que aparece en la tabla RRP', 2);
          Pq_Utl.log (pck || '.fn_getRendimientosRegParcela - ', 'Se obtiene el rendimiento = ' || v_rendimiento, 2);
          RETURN '0#' || v_rendimiento || '#0#' || v_rrp_parcela.rendimiento_perm;
       ELSE
           Pq_Utl.log (pck || '.fn_getRendimientosRegParcela - ', 'En ninguna comparativa se ha elegido la combinacion que aparece en la tabla RRP', 2);
           Pq_Utl.log (pck || '.fn_getRendimientosRegParcela - ', 'Se continua con el calculo de la produccion', 2);
           RETURN NULL;
       END IF;
    END IF;

  END fn_getRendimientosRegParcela;




END pq_calcula_produccion;
/
SHOW ERRORS;
