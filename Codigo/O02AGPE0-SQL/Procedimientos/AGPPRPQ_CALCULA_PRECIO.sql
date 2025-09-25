SET DEFINE OFF;
SET SERVEROUTPUT ON;
create or replace package o02agpe0.PQ_CALCULA_PRECIO is

  -- Author  : U028783
  -- Created : 2/11/2011 9:58:25
  -- Purpose :

  TYPE t_varchar2 IS TABLE OF VARCHAR2(32767) INDEX BY BINARY_INTEGER;

  TYPE t_masc_precio IS TABLE OF tb_sc_c_masc_precios%ROWTYPE INDEX BY BINARY_INTEGER;

  TYPE T_REL_MASCARAS IS TABLE OF TB_CAMPOS_MASCARA%ROWTYPE INDEX BY BINARY_INTEGER;

  TYPE t_campos_mascaras IS TABLE OF pq_calcula_precio.t_cod_valor INDEX BY BINARY_INTEGER;

  TYPE t_cod_valor IS RECORD(
		 cod   NUMBER
		,valor VARCHAR2(32767));

	TYPE t_dat_variable IS TABLE OF t_cod_valor INDEX BY BINARY_INTEGER;


  FUNCTION fn_getPrecios(p_lineaseguroid       VARCHAR2
													,p_datosvariables    VARCHAR2
													,p_modulo            VARCHAR2
													,p_codcultivo        VARCHAR2
													,p_codvariedad       VARCHAR2
													,p_provincia         VARCHAR2
													,p_comarca           VARCHAR2
													,p_termino           VARCHAR2
													,p_subtermino        VARCHAR2) RETURN VARCHAR2;

  PROCEDURE pr_get_precios(p_lineaseguroid       VARCHAR2
														,p_datosvariables    pq_calcula_precio.t_dat_variable
														,p_modulo            VARCHAR2
														,p_codcultivo        VARCHAR2
														,p_codvariedad       VARCHAR2
														,p_provincia         VARCHAR2
														,p_comarca           VARCHAR2
														,p_termino           VARCHAR2
														,p_subtermino        VARCHAR2
														,p_precio            OUT pq_calcula_precio.t_varchar2);

  FUNCTION fn_getMascaraPrecio(p_lineaseguroid VARCHAR2
																,p_modulo        VARCHAR2
																,p_codcultivo    VARCHAR2
																,p_codvariedad   VARCHAR2
																,p_provincia     VARCHAR2
																,p_comarca       VARCHAR2
																,p_termino       VARCHAR2
																,p_subtermino    VARCHAR2) RETURN t_masc_precio;

  FUNCTION fn_filtroMascaraPrecio(p_lineaseguroid  VARCHAR2
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
																	,p_allsubtermino  VARCHAR2 DEFAULT 'N') RETURN t_masc_precio;

  FUNCTION fn_calcularPrecios(p_lineaseguroid   VARCHAR2
														 ,p_modulo          VARCHAR2
														 ,p_codcultivo      VARCHAR2
														 ,p_codvariedad     VARCHAR2
														 ,p_provincia       VARCHAR2
														 ,p_comarca         VARCHAR2
														 ,p_termino         VARCHAR2
														 ,p_subtermino      VARCHAR2
														 ,p_campos_mascaras t_campos_mascaras) RETURN t_varchar2;

  FUNCTION fn_buscaPrecios(p_lineaseguroid   VARCHAR2
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
																 ,p_campos_mascaras t_campos_mascaras) RETURN t_varchar2;

  FUNCTION fn_extraer_campo(entrada   VARCHAR2
													 ,campo     NUMBER DEFAULT 1
													 ,separador VARCHAR2 DEFAULT '#') RETURN VARCHAR2;

  FUNCTION fn_valorpordefectofitros(p_campos_mascara pq_calcula_precio.t_campos_mascaras) RETURN pq_calcula_precio.t_campos_mascaras;

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

  FUNCTION fn_getRelacionMascaras(P_MASCARA IN PQ_CALCULA_PRECIO.t_masc_precio) RETURN PQ_CALCULA_PRECIO.T_REL_MASCARAS;

end PQ_CALCULA_PRECIO;
/
create or replace package body o02agpe0.PQ_CALCULA_PRECIO is

  FUNCTION fn_getPrecios(p_lineaseguroid       VARCHAR2
													,p_datosvariables    VARCHAR2
													,p_modulo            VARCHAR2
													,p_codcultivo        VARCHAR2
													,p_codvariedad       VARCHAR2
													,p_provincia         VARCHAR2
													,p_comarca           VARCHAR2
													,p_termino           VARCHAR2
													,p_subtermino        VARCHAR2) RETURN VARCHAR2 IS

     v_datos_var  pq_calcula_precio.t_dat_variable;
     v_ind        NUMBER := 1;
		 v_valor      VARCHAR2(32767) := NULL;

     v_precio pq_calcula_precio.t_varchar2;

  BEGIN

     WHILE pq_calcula_precio.fn_extraer_campo(p_datosvariables, v_ind, '#') IS NOT NULL LOOP

         v_valor := pq_calcula_precio.fn_extraer_campo(p_datosvariables, v_ind, '#');

         v_datos_var(v_ind).cod := substr(v_valor, 1, instr(v_valor, '|') - 1);

         v_datos_var(v_ind).valor := substr(v_valor, instr(v_valor, '|') + 1);

         v_ind := v_ind + 1;

     END LOOP;

     pq_calcula_precio.pr_get_precios(p_lineaseguroid       => p_lineaseguroid,
																			p_datosvariables      => v_datos_var,
																			p_modulo              => p_modulo,
																			p_codcultivo          => p_codcultivo,
																			p_codvariedad         => p_codvariedad,
																			p_provincia           => p_provincia,
																			p_comarca             => p_comarca,
																			p_termino             => p_termino,
																			p_subtermino          => p_subtermino,
																			p_precio              => v_precio);



	--	dbms_output.put_line('La funcion retorna ' || v_precio(1));

		RETURN v_precio(1);

  END fn_getprecios;

  PROCEDURE pr_get_precios(p_lineaseguroid       VARCHAR2
														,p_datosvariables    pq_calcula_precio.t_dat_variable
														,p_modulo            VARCHAR2
														,p_codcultivo        VARCHAR2
														,p_codvariedad       VARCHAR2
														,p_provincia         VARCHAR2
														,p_comarca           VARCHAR2
														,p_termino           VARCHAR2
														,p_subtermino        VARCHAR2
														,p_precio            OUT pq_calcula_precio.t_varchar2) IS

     v_mascara         PQ_CALCULA_PRECIO.t_masc_precio;
     v_rel_mascara     PQ_CALCULA_PRECIO.T_REL_MASCARAS;

     v_campos_mascaras t_campos_mascaras;
		 v_ind             NUMBER := 1;

     --v_precios         pq_calcula_precio.t_varchar2;

  BEGIN

      -- 1. Obtenemos las mascaras de precio según plan, línea, modulo,
      --    cultivo, variedad, provincia, comarca, término y subtérmino
      v_mascara := PQ_CALCULA_PRECIO.fn_getMascaraPrecio(p_lineaseguroid => p_lineaseguroid
                        																,p_modulo        => p_modulo
                        																,p_codcultivo    => p_codcultivo
                        																,p_codvariedad   => p_codvariedad
                        																,p_provincia     => p_provincia
                        																,p_comarca       => p_comarca
                        																,p_termino       => p_termino
                        																,p_subtermino    => p_subtermino);

      -- Según los campos de máscara obtenemos las relaciones entre campos para
      -- saber qué valores van a qué campos.
      v_rel_mascara := PQ_CALCULA_PRECIO.fn_getRelacionMascaras(v_mascara);

      --Bucle para recorrer las máscaras y ver qué datos variables hacen falta en el filtro para obtener el precio
      FOR a IN 1 .. v_mascara.COUNT LOOP
          IF v_mascara(a).codconcepto IS NOT NULL THEN
              FOR m IN 1 .. v_rel_mascara.COUNT LOOP
                  IF (v_rel_mascara(m).CODCONCEPTOMASC = v_mascara(a).codconcepto) THEN
                      FOR i IN 1 .. p_datosvariables.COUNT LOOP
                          IF p_datosvariables(i).cod = v_rel_mascara(m).CODCONCEPTOASOC THEN
                              v_campos_mascaras(v_ind).cod := v_mascara(a).codconcepto;
                              v_campos_mascaras(v_ind).valor := p_datosvariables(i).valor;
                              v_ind := v_ind + 1;
                          END IF;
                      END LOOP;
                  END IF;
              END LOOP;

          END IF;
      END LOOP;

      -- 2. Ponemos a cero los campos del filtro que vayan vacios y tengan
      -- un valor por defecto
      v_campos_mascaras := pq_calcula_precio.fn_valorpordefectofitros(p_campos_mascara => v_campos_mascaras);

      -- 3. Obtenemos los precios según el filtro
      p_precio := fn_calcularPrecios(p_lineaseguroid   => p_lineaseguroid,
																			p_modulo          => p_modulo,
																			p_codcultivo      => p_codcultivo,
																			p_codvariedad     => p_codvariedad,
																			p_provincia       => p_provincia,
																			p_comarca         => p_comarca,
																			p_termino         => p_termino,
																			p_subtermino      => p_subtermino,
																			p_campos_mascaras => v_campos_mascaras);

  END pr_get_precios;


  FUNCTION fn_getMascaraPrecio(p_lineaseguroid VARCHAR2
																,p_modulo        VARCHAR2
																,p_codcultivo    VARCHAR2
																,p_codvariedad   VARCHAR2
																,p_provincia     VARCHAR2
																,p_comarca       VARCHAR2
																,p_termino       VARCHAR2
																,p_subtermino    VARCHAR2) RETURN t_masc_precio IS

		v_mascara t_masc_precio;



	BEGIN

		v_mascara := fn_filtroMascaraPrecio(p_lineaseguroid  => p_lineaseguroid,
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
			v_mascara := fn_filtroMascaraPrecio(p_lineaseguroid  => p_lineaseguroid,
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
				v_mascara := fn_filtroMascaraPrecio(p_lineaseguroid  => p_lineaseguroid,
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
					v_mascara := fn_filtroMascaraPrecio(p_lineaseguroid  => p_lineaseguroid,
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
						v_mascara := fn_filtroMascaraPrecio(p_lineaseguroid  => p_lineaseguroid,
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
							v_mascara := fn_filtroMascaraPrecio(p_lineaseguroid  => p_lineaseguroid,
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
								v_mascara := fn_filtroMascaraPrecio(p_lineaseguroid  => p_lineaseguroid,
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
	END fn_getMascaraPrecio;

  FUNCTION fn_filtroMascaraPrecio(p_lineaseguroid  VARCHAR2
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
																	,p_allsubtermino  VARCHAR2 DEFAULT 'N') RETURN t_masc_precio IS

		v_mascara t_masc_precio;

		v_consulta VARCHAR2(32767) := NULL;
		TYPE cur_typ IS REF CURSOR;
		v_cursor cur_typ;
		v_ind    NUMBER := 1;
	
    V_LINEASEGUROID	 tb_sc_c_masc_limites_rdtos.lineaseguroid%TYPE;
    V_CODMODULO	     tb_sc_c_masc_limites_rdtos.codmodulo%TYPE;
    V_CODCULTIVO	   tb_sc_c_masc_limites_rdtos.codcultivo%TYPE;
    V_CODVARIEDAD	   tb_sc_c_masc_limites_rdtos.codvariedad%TYPE;
    V_CODPROVINCIA	 tb_sc_c_masc_limites_rdtos.codprovincia%TYPE;
    V_CODTERMINO	   tb_sc_c_masc_limites_rdtos.codtermino%TYPE;
    V_SUBTERMINO	   tb_sc_c_masc_limites_rdtos.subtermino%TYPE;
    V_CODCONCEPTO	   tb_sc_c_masc_limites_rdtos.codconcepto%TYPE;
    V_CODCOMARCA	   tb_sc_c_masc_limites_rdtos.codcomarca%TYPE;

	BEGIN

		v_consulta := 'select * from tb_sc_c_masc_precios where 1 = 1 ';
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

		OPEN v_cursor FOR v_consulta;
		
			
      LOOP FETCH v_cursor INTO  V_LINEASEGUROID,V_CODMODULO,V_CODCULTIVO,
                                V_CODVARIEDAD,V_CODPROVINCIA,V_CODTERMINO,
                                V_SUBTERMINO,V_CODCONCEPTO,V_CODCOMARCA;
		
    	EXIT WHEN v_cursor%NOTFOUND;
			v_mascara(v_ind).lineaseguroid := V_LINEASEGUROID;
			v_mascara(v_ind).codmodulo := V_CODMODULO;
			v_mascara(v_ind).codcultivo := V_CODCULTIVO;
			v_mascara(v_ind).codvariedad := V_CODVARIEDAD;
			v_mascara(v_ind).codprovincia := V_CODPROVINCIA;
			v_mascara(v_ind).codtermino := V_CODTERMINO;
			v_mascara(v_ind).subtermino := V_SUBTERMINO;
			v_mascara(v_ind).codconcepto := V_CODCONCEPTO;
			v_mascara(v_ind).codcomarca := V_CODCOMARCA;
			v_ind := v_ind + 1;
		END LOOP;
		CLOSE v_cursor;
		RETURN v_mascara;

	END fn_filtroMascaraPrecio;

  -- Función para realizar el cálculo de precios
  FUNCTION fn_calcularPrecios(p_lineaseguroid   VARCHAR2
														 ,p_modulo          VARCHAR2
														 ,p_codcultivo      VARCHAR2
														 ,p_codvariedad     VARCHAR2
														 ,p_provincia       VARCHAR2
														 ,p_comarca         VARCHAR2
														 ,p_termino         VARCHAR2
														 ,p_subtermino      VARCHAR2
														 ,p_campos_mascaras t_campos_mascaras) RETURN t_varchar2 IS
		v_precios t_varchar2;

	BEGIN



		v_precios := fn_buscaPrecios(p_lineaseguroid   => p_lineaseguroid,
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
																				p_campos_mascaras => p_campos_mascaras);

		IF v_precios.COUNT = 0 THEN
			--Si no recupera datos filtramos por los datos genericos. Segun el siguiente orden:
			--1. Subtermino = 9; 2. Termino = 999; 3. Comarca = 99
			--4. Provincia = 99; 5. Variedad = 999; 6. Cultivo = 999
			v_precios.DELETE;
			v_precios := fn_buscaPrecios(p_lineaseguroid   => p_lineaseguroid,
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
																					p_campos_mascaras => p_campos_mascaras);
			IF v_precios.COUNT = 0 THEN
				--Si no recupera datos filtramos por los datos genericos. Segun el siguiente orden:
				--1. Subtermino = 9; 2. Termino = 999; 3. Comarca = 99
				--4. Provincia = 99; 5. Variedad = 999; 6. Cultivo = 999
				v_precios.DELETE;
				v_precios := fn_buscaPrecios(p_lineaseguroid   => p_lineaseguroid,
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
																						p_campos_mascaras => p_campos_mascaras);
				IF v_precios.COUNT = 0 THEN
					--Si no recupera datos filtramos por los datos genericos. Segun el siguiente orden:
					--1. Subtermino = 9; 2. Termino = 999; 3. Comarca = 99
					--4. Provincia = 99; 5. Variedad = 999; 6. Cultivo = 999
					v_precios.DELETE;
					v_precios := fn_buscaPrecios(p_lineaseguroid   => p_lineaseguroid,
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
																							p_campos_mascaras => p_campos_mascaras);
					IF v_precios.COUNT = 0 THEN
						--Si no recupera datos filtramos por los datos genericos. Segun el siguiente orden:
						--1. Subtermino = 9; 2. Termino = 999; 3. Comarca = 99
						--4. Provincia = 99; 5. Variedad = 999; 6. Cultivo = 999
						v_precios.DELETE;
						v_precios := fn_buscaPrecios(p_lineaseguroid   => p_lineaseguroid,
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
																								p_campos_mascaras => p_campos_mascaras);
						IF v_precios.COUNT = 0 THEN
							--Si no recupera datos filtramos por los datos genericos. Segun el siguiente orden:
							--1. Subtermino = 9; 2. Termino = 999; 3. Comarca = 99
							--4. Provincia = 99; 5. Variedad = 999; 6. Cultivo = 999
							v_precios.DELETE;
							v_precios := fn_buscaPrecios(p_lineaseguroid   => p_lineaseguroid,
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
																									p_campos_mascaras => p_campos_mascaras);
							IF v_precios.COUNT = 0 THEN
								--Si no recupera datos filtramos por los datos genericos. Segun el siguiente orden:
								--1. Subtermino = 9; 2. Termino = 999; 3. Comarca = 99
								--4. Provincia = 99; 5. Variedad = 999; 6. Cultivo = 999
								v_precios.DELETE;
								v_precios := fn_buscaPrecios(p_lineaseguroid   => p_lineaseguroid,
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
																										p_campos_mascaras => p_campos_mascaras);
                IF v_precios.COUNT = 0 THEN
                   v_precios(1) := '0#0#0';
                end if;
							END IF;
						END IF;
					END IF;
				END IF;
			END IF;
		END IF;

		RETURN v_precios;

	END fn_calcularPrecios;

  FUNCTION fn_buscaPrecios(p_lineaseguroid   VARCHAR2
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
																 ,p_campos_mascaras t_campos_mascaras) RETURN t_varchar2 IS

    v_precios    t_varchar2;

		v_consulta  VARCHAR2(32767) := NULL;

    TYPE cur_typ IS REF CURSOR;
		v_cursor cur_typ;

    v_ind    NUMBER := 1;
		v_res    tb_sc_c_precios%ROWTYPE;

	BEGIN



		v_consulta := 'select * from tb_sc_c_precios where 1 = 1 ';
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

      --DESCOMENTAR PARA PONER TRAZAS DE CODIGOS DE CONCEPTO Y VALORES.
      --PQ_UTL.log('fn_buscaPrecios: COD='||p_campos_mascaras(a).cod||', VALOR='||p_campos_mascaras(a).valor||' --- ', 2);

			IF p_campos_mascaras(a).cod = 133 THEN
				v_consulta := v_consulta || ' and CODPRACTICACULTURAL = ' || p_campos_mascaras(a).valor || '';
			ELSIF p_campos_mascaras(a).cod = 126 THEN
				v_consulta := v_consulta || ' and CODTIPOCAPITAL = ' || p_campos_mascaras(a).valor || '';
      ELSIF p_campos_mascaras(a).cod = 616 THEN
				v_consulta := v_consulta || ' and CODSISTEMAPRODUCCION = ' || p_campos_mascaras(a).valor || '';
      ELSIF p_campos_mascaras(a).cod = 107 THEN
				v_consulta := v_consulta || ' and CODDENOMORIGEN = ' || p_campos_mascaras(a).valor || '';
      ELSIF p_campos_mascaras(a).cod = 110 THEN
				v_consulta := v_consulta || ' and CODDESTINO = ' || p_campos_mascaras(a).valor || '';
      --densidad
			ELSIF p_campos_mascaras(a).cod = 226 THEN
				v_consulta := v_consulta || ' and DENSIDADDESDE = ' || p_campos_mascaras(a).valor || '';
			ELSIF p_campos_mascaras(a).cod = 227 THEN
				v_consulta := v_consulta || ' and DENSIDADHASTA = ' || p_campos_mascaras(a).valor || '';
      ELSIF p_campos_mascaras(a).cod = 109 THEN
				v_consulta := v_consulta || ' and DENSIDAD = ' || p_campos_mascaras(a).valor || '';
      --edad
			ELSIF p_campos_mascaras(a).cod = 231 THEN
				v_consulta := v_consulta || ' and EDADDESDE <= ' || p_campos_mascaras(a).valor || '';
			ELSIF p_campos_mascaras(a).cod = 232 THEN
				v_consulta := v_consulta || ' and EDADHASTA >= ' || p_campos_mascaras(a).valor || '';
      ELSIF p_campos_mascaras(a).cod = 111 THEN
				v_consulta := v_consulta || ' and EDAD = ' || to_date(p_campos_mascaras(a).valor,
																																	 'dd/mm/yyyy') || '';

			--fecha recoleccion
      ELSIF p_campos_mascaras(a).cod = 235 THEN
				v_consulta := v_consulta || ' and FRECOLDESDE = ' || to_date(p_campos_mascaras(a).valor,
																																		 'dd/mm/yyyy') || '';
			ELSIF p_campos_mascaras(a).cod = 236 THEN
				v_consulta := v_consulta || ' and FRECOLHASTA = ' || to_date(p_campos_mascaras(a).valor,
																																		 'dd/mm/yyyy') || '';
			ELSIF p_campos_mascaras(a).cod = 112 THEN
				v_consulta := v_consulta || ' and FRECOL = ' || to_date(p_campos_mascaras(a).valor,
																																		 'dd/mm/yyyy') || '';
			ELSIF p_campos_mascaras(a).cod = 123 THEN
				v_consulta := v_consulta || ' and CODSISTEMACULTIVO = ' || p_campos_mascaras(a).valor || '';
			ELSIF p_campos_mascaras(a).cod = 621 THEN
				v_consulta := v_consulta || ' and CODSISTEMAPROTECCION = ' || p_campos_mascaras(a).valor || '';
			ELSIF p_campos_mascaras(a).cod = 173 THEN
				v_consulta := v_consulta || ' and CODTIPOPLANTACION = ' || p_campos_mascaras(a).valor || '';
			ELSIF p_campos_mascaras(a).cod = 618 THEN
				v_consulta := v_consulta || ' and CODCICLOCULTIVO = ' || p_campos_mascaras(a).valor || '';
			ELSIF p_campos_mascaras(a).cod = 778 THEN
				v_consulta := v_consulta || ' and CODTIPOINSTALACION = ' || p_campos_mascaras(a).valor || '';
			ELSIF p_campos_mascaras(a).cod = 875 THEN
				v_consulta := v_consulta || ' and CODMATERIALESTRUCTURA = ' || p_campos_mascaras(a).valor || '';
			ELSIF p_campos_mascaras(a).cod = 873 THEN
				v_consulta := v_consulta || ' and CODMATERIALCUBIERTA = ' || p_campos_mascaras(a).valor || '';
      ELSIF p_campos_mascaras(a).cod = 752 THEN
        -- Tipo terreno
				v_consulta := v_consulta || ' and CODTIPOTERRENO = ' || p_campos_mascaras(a).valor || '';
      ELSIF p_campos_mascaras(a).cod = 753 THEN
        -- Tipo masa
				v_consulta := v_consulta || ' and CODTIPOMASA = ' || p_campos_mascaras(a).valor || '';
      ELSIF p_campos_mascaras(a).cod = 754 THEN
        -- Pendiente
				v_consulta := v_consulta || ' and CODPENDIENTE = ' || p_campos_mascaras(a).valor || '';
			END IF;

		END LOOP;

		OPEN v_cursor FOR v_consulta;
		LOOP
			FETCH v_cursor
				INTO v_res;
			EXIT WHEN v_cursor%NOTFOUND;
			v_precios(v_ind) := nvl(trim(to_char(v_res.PRECIODESDE, '99990.9999')), '0')
                       || '#' || nvl(trim(to_char(v_res.PRECIOHASTA, '99990.9999')), '0')
                       || '#' || nvl(trim(to_char(v_res.PRECIOFIJO, '99990.9999')), '0');
			v_ind := v_ind + 1;
		END LOOP;
		CLOSE v_cursor;

		RETURN v_precios;

  exception
     when others then
        pq_utl.log('Error al buscar precios: '||SQLCODE||': '|| SQLERRM || ' --- ', 2);
        RAISE;
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
	END fn_extraer_campo;

  FUNCTION fn_valorpordefectofitros(p_campos_mascara pq_calcula_precio.t_campos_mascaras) RETURN pq_calcula_precio.t_campos_mascaras IS

		v_result pq_calcula_precio.t_campos_mascaras;

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
	END fn_where;

  -- FUNCIÓN PARA OBTENER LA RELACIÓN ENTRE LOS CAMPOS DE MÁSCARA Y LOS DATOS VARIABLES DE PARCELAS
  -- CON ESTE RESULTADO SE PODRÁ MONTAR LA CONSULTA PARA ASIGNAR LOS VALORES A LOS FILTROS PARA OBTENER EL PRECIO
  FUNCTION fn_getRelacionMascaras(P_MASCARA IN PQ_CALCULA_PRECIO.t_masc_precio) RETURN PQ_CALCULA_PRECIO.T_REL_MASCARAS IS

     v_rel_mascaras  PQ_CALCULA_PRECIO.T_REL_MASCARAS;
     TYPE cur_typ    IS REF CURSOR;
		 v_cursor        cur_typ;
		 v_ind           NUMBER := 1;
     V_CONSULTA      VARCHAR2(2000) := 'select CODTABLACONDICIONADO, CODCONCEPTOMASC, CODCONCEPTOASOC from tb_campos_mascara ';

     V_CODTABLACONDICIONADO  TB_CAMPOS_MASCARA.CODTABLACONDICIONADO%TYPE;
     V_CODCONCEPTOMASC       TB_CAMPOS_MASCARA.CODCONCEPTOMASC%TYPE;
     V_CODCONCEPTOASOC       TB_CAMPOS_MASCARA.CODCONCEPTOASOC%TYPE;

  BEGIN
      --Recorremos las máscaras a ver qué conceptos hay que obtener para el filtro
      IF (P_MASCARA.COUNT > 0) THEN
          V_CONSULTA := V_CONSULTA || ' WHERE CODCONCEPTOMASC IN (';
          FOR a IN 1 .. P_MASCARA.COUNT LOOP
              V_CONSULTA := V_CONSULTA || P_MASCARA(a).CODCONCEPTO;
              IF (a < P_MASCARA.COUNT) THEN
                  V_CONSULTA := V_CONSULTA || ', ';
              END IF;
          END LOOP;
          V_CONSULTA := V_CONSULTA || ')';
      END IF;

      PQ_UTL.log('fn_getRelacionMascaras - consulta: ' || V_CONSULTA || ' --- ', 2);

      --Ejecutamos la consulta y rellenamos un array con los resultados.
      OPEN v_cursor FOR v_consulta;
		  LOOP FETCH v_cursor INTO V_CODTABLACONDICIONADO, V_CODCONCEPTOMASC, V_CODCONCEPTOASOC;
			    EXIT WHEN v_cursor%NOTFOUND;

          v_rel_mascaras(v_ind).CODTABLACONDICIONADO := V_CODTABLACONDICIONADO;
			    v_rel_mascaras(v_ind).CODCONCEPTOMASC := V_CODCONCEPTOMASC;
			    v_rel_mascaras(v_ind).CODCONCEPTOASOC := V_CODCONCEPTOASOC;

			    v_ind := v_ind + 1;
		  END LOOP;
		  CLOSE v_cursor;

      RETURN v_rel_mascaras;

  END fn_getRelacionMascaras;

end PQ_CALCULA_PRECIO;
/
SHOW ERRORS;