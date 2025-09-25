SET DEFINE OFF;
SET SERVEROUTPUT ON;

create or replace package o02agpe0.PQ_ASIGNAR_COMIS_RENOV is

  /******************************************************************************
     NAME:        PQ_ASIGNAR_COMIS_RENOV
     PURPOSE:
  
     REVISIONS:
     Ver        Date        Author           Description
     ---------  ----------  ---------------  ------------------------------------
     1.0        29-01-2019  Ruben Lopez    1. Created this package.
                            T-Systems
  ******************************************************************************/

  -------------------------------------------------------------------------------
  -- PROCEDIMIENTOS PUBLICOS
  -------------------------------------------------------------------------------

  -- Funcion que asigna comision a polizas renovables
  FUNCTION asigna_comision_renovables RETURN VARCHAR2;

  -- Devuelve la consulta que obtiene las polizas renovables 'Pendiente de asignar gastos' y 'Enviada erronea'
  FUNCTION crearQuery RETURN VARCHAR2;

end PQ_ASIGNAR_COMIS_RENOV;
/
create or replace package body o02agpe0.PQ_ASIGNAR_COMIS_RENOV is

  	---------------------------------------------------------------------------------------------------------------------------
	---------------------------------------------------------------------------------------------------------------------------
	-- FUNCTION asigna_comision_renovables
	--
	-- Proceso que asigna la comision a las polizas renovables
	--
	-- Recoge las polizas renovables con estado agroplus 'Pendiente de asignar gastos' y 'Enviada erronea',
  --  las procesa y da su valor de comision correspondiente
	-----------------------------------------------------------------------------------------------------------------------
	-- |
	---------------------------------------------------------------------------------------------------------------------------
	---------------------------------------------------------------------------------------------------------------------------

	-- Inicio de declaracion de funcion
  FUNCTION asigna_comision_renovables RETURN VARCHAR2 IS

		-- Variables generales
		lc VARCHAR2(60) := 'pq_asignar_comis_renov.asigna_comision_renovables'; -- Variable que almacena el nombre del paquete y de la funcion
		TYPE tpcursor IS REF CURSOR; -- Tipo cursor
		l_tp_cursor       tpcursor; -- Cursor para la consulta de polizas
    l_query          VARCHAR2(12500);
    l_num_polizas     NUMBER := 0; -- Contador para el numero de polizas procesadas
    l_num_plzs_comision NUMBER := 0; -- Contador para el numero de polizas renovables que han sido asignadas
    l_num_plzs_sin_comision NUMBER := 0; --  Contador para el numero de polizas renovables que NO han sido asignadas
    l_num_polizas_no_coste NUMBER := 0; -- Contador para las polizas que no tienen coste_total_tomador ni prima_comercial_neta
    no_polizas_found EXCEPTION; -- Excepcion indicadora de que no hay polizas
    v_comision       VARCHAR2(6); --Porcentaje de comision resultante de la consulta
    v_flag_no_com    BOOLEAN;
    
    --Variables para el cursor
    
    v_id_poliza               VARCHAR2(8);
    v_plan                    VARCHAR2(4);
    v_linea                   VARCHAR2(4);
    v_codentidad              VARCHAR2(8);
    v_codentidadmed           VARCHAR2(8);
    v_codsubentmed            VARCHAR2(8);
    v_coste_total_tomador     VARCHAR2(11);
    v_prima_comercial_neta    VARCHAR2(11);
    v_idgrupo                 VARCHAR2(2);
	v_codmodulo               VARCHAR2(5);
    v_estado_agroseguro       VARCHAR2(2);
    v_comision_entidad        VARCHAR2(2);

    -- Inicio del cuerpo de la funcion
  BEGIN
       
    -- Se compone la consulta para recuperar las polizas renovables requeridas
    pq_utl.log(lc, 'Componemos la query para recorrer todas las polizas renovables en estado ''Pendiente de asignar gastos'' y ''Enviada erronea''');
	l_query:= crearQuery;

	-- Crea el cursor para la consulta
	pq_utl.log(lc, 'Abre el cursor para la query');

	OPEN l_tp_cursor FOR l_query;

	-- Bucle para recorrer todos los registros del cursor
	LOOP
      
		v_flag_no_com := false;
		-- Vuelca los datos del cursor en las variables indicadas
		pq_utl.log(lc, 'Volcado de datos del cursor en variables');

		FETCH l_tp_cursor
			INTO
				v_id_poliza,
				v_plan,
				v_linea,
				v_codentidad,
				v_codentidadmed,
				v_codsubentmed,
				v_coste_total_tomador,
				v_idgrupo,
				v_codmodulo,
				v_estado_agroseguro,
				v_comision_entidad;
            
		IF (l_tp_cursor%NOTFOUND) THEN

			-- Si el numero de polizas tratadas es cero se lanza la excepcion correspondiente
			IF (l_num_polizas = 0) THEN
				pq_utl.log(lc, 'No se han encontrado polizas');
				RAISE no_polizas_found;			
			END IF;

			pq_utl.log(lc, 'No hay mas registros que enviar.');

			-- Sale del bucle
			EXIT;
		END IF;

		-- Actualiza el contador de polizas, cada vez que entramos al bucle es que tenemos una poliza mas
		l_num_polizas := l_num_polizas + 1;
      
		pq_utl.log(lc, 'Cursor en poliza numero ' || l_num_polizas || ', con IdPolizaRen: ' || v_id_poliza);
      
		--Obtenemos la prima comercial neta de la poliza renovable (si existe)
		BEGIN
			SELECT CGNR.PRIMA_COMERCIAL_NETA 
				INTO v_prima_comercial_neta
				FROM TB_COSTE_GRUPO_NEGOCIO_REN CGNR 
				WHERE CGNR.IDPOLIZARENOVABLE = v_id_poliza
				AND CGNR.GRUPO_NEGOCIO = v_idgrupo;
		EXCEPTION
			WHEN NO_DATA_FOUND THEN
				pq_utl.log(lc, 'No se ha encontrado registro con prima comercial: ' || v_id_poliza);
                v_prima_comercial_neta := NULL;
                 
		END;
      
		-- Comprueba si la poliza tiene coste_total_tomador o prima_comercial_neta
		IF v_coste_total_tomador is null and v_prima_comercial_neta is null THEN
			l_num_polizas_no_coste := l_num_polizas_no_coste + 1;
			l_num_plzs_sin_comision := l_num_plzs_sin_comision + 1;
			pq_utl.log(lc, 'Poliza renovable (' || v_id_poliza || ') sin coste total tomador ni prima comercial neta ');
       
		-- Si tiene coste total tomador o prima comercial neta
		ELSE
      
			pq_utl.log(lc, 'Busqueda de comision con todos los filtros');
			BEGIN
				-- Lanzamos la consulta para filtrar el registro con la comision correcta
				SELECT CRE.COMISION into v_comision
					FROM TB_COMS_RENOV_ESMED CRE
					WHERE
						CRE.CODPLAN = v_plan
						AND
						CRE.CODLINEA = v_linea
						AND
						CRE.IDGRUPO = v_idgrupo
						AND
						CRE.CODMODULO = v_codmodulo
						AND
						CRE.CODENTIDAD = v_codentidad
						AND
						CRE.CODENTMED = v_codentidadmed
						AND
						CRE.CODSUBMED = v_codsubentmed
						AND
						((CRE.IMP_DESDE <= v_coste_total_tomador AND CRE.IMP_HASTA > v_coste_total_tomador AND CRE.REFIMPORTE = 'C')
						OR
						(CRE.IMP_DESDE <= v_prima_comercial_neta AND CRE.IMP_HASTA > v_prima_comercial_neta AND CRE.REFIMPORTE = 'P'));
        
			EXCEPTION
			-- Si no devuelve resultados, entonces filtramos solo por plan, linea, idgrupo y modulo generico
			WHEN NO_DATA_FOUND THEN
				BEGIN
					pq_utl.log(lc, 'No se ha encontrado comision, se pasa a busqueda con modulo generico');
					SELECT CRE.COMISION into v_comision
						FROM TB_COMS_RENOV_ESMED CRE
						WHERE
							CRE.CODPLAN = v_plan
							AND
							CRE.CODLINEA = v_linea
							AND
							CRE.IDGRUPO = v_idgrupo
							AND
							CRE.CODMODULO = '99999'
							AND
							CRE.CODENTIDAD = v_codentidad
							AND
							CRE.CODENTMED = v_codentidadmed
							AND
							CRE.CODSUBMED = v_codsubentmed
							AND
							((CRE.IMP_DESDE <= v_coste_total_tomador AND CRE.IMP_HASTA > v_coste_total_tomador AND CRE.REFIMPORTE = 'C')
							OR
							(CRE.IMP_DESDE <= v_prima_comercial_neta AND CRE.IMP_HASTA > v_prima_comercial_neta AND CRE.REFIMPORTE = 'P'));
			
				EXCEPTION
                WHEN NO_DATA_FOUND THEN
                    -- Si no devuelve resultados, entonces filtramos solo por plan, linea, idgrupo y modulo
					BEGIN
						pq_utl.log(lc, 'No se ha encontrado comision, se pasa a busqueda con plan, linea, grupo negocio y modulo');
						SELECT CRE.COMISION into v_comision
							FROM o02agpe0.TB_COMS_RENOV_ESMED CRE
							WHERE
								CRE.CODPLAN = v_plan
								AND
								CRE.CODLINEA = v_linea
								AND
								CRE.IDGRUPO = v_idgrupo
								AND
								CRE.CODMODULO = v_codmodulo
								AND
								CRE.CODENTIDAD IS NULL
								AND
								CRE.CODENTMED IS NULL
								AND
								CRE.CODSUBMED IS NULL
								AND
								((CRE.IMP_DESDE <= v_coste_total_tomador AND CRE.IMP_HASTA > v_coste_total_tomador AND CRE.REFIMPORTE = 'C')
								OR
								(CRE.IMP_DESDE <= v_prima_comercial_neta AND CRE.IMP_HASTA > v_prima_comercial_neta AND CRE.REFIMPORTE = 'P'));
					EXCEPTION
					WHEN NO_DATA_FOUND THEN
						BEGIN
							pq_utl.log(lc, 'No se ha encontrado comision, se pasa a busqueda con plan, linea, grupo negocio y modulo generico');
							SELECT CRE.COMISION into v_comision
								FROM o02agpe0.TB_COMS_RENOV_ESMED CRE
								WHERE
									CRE.CODPLAN = v_plan
									AND
									CRE.CODLINEA = v_linea
									AND
									CRE.IDGRUPO = v_idgrupo
									AND
									CRE.CODMODULO = '99999'
									AND
									CRE.CODENTIDAD IS NULL
									AND
									CRE.CODENTMED IS NULL
									AND
									CRE.CODSUBMED IS NULL
									AND
									((CRE.IMP_DESDE <= v_coste_total_tomador AND CRE.IMP_HASTA > v_coste_total_tomador AND CRE.REFIMPORTE = 'C')
									OR
									(CRE.IMP_DESDE <= v_prima_comercial_neta AND CRE.IMP_HASTA > v_prima_comercial_neta AND CRE.REFIMPORTE = 'P'));
						EXCEPTION
						WHEN NO_DATA_FOUND THEN
							pq_utl.log(lc, 'No se ha encontrado ninguna comision para la poliza: ' || v_id_poliza);
							v_comision := NULL;
							v_flag_no_com := true;
						-- Si devuelve mas de una fila lanzamos error en el log
						WHEN TOO_MANY_ROWS THEN
							pq_utl.log(lc, 'Error al asignar una comision (filtro SIN entidad y modulo generico), se han dado varios valores para el mismo filtro en la poliza: ' || v_id_poliza);
							v_flag_no_com := true;
						WHEN OTHERS THEN
							ROLLBACK;
							DBMS_OUTPUT.PUT_LINE(SQLERRM);
							pq_utl.log(lc,SQLERRM);
							-- Se escribe en el log el error
							pq_utl.log(lc, 'El proceso ha finalizado CON ERRORES a las ' || to_char(SYSDATE, 'HH24:MI:SS'), 2);
							pq_utl.log(lc, '## FIN ##', 1);
							pq_err.raiser(SQLCODE, 'Error al asignar la comision de las polizas renovables ' || ' [' || SQLERRM || ']');
							-- Se vuelve a lanzar la excepcion para parar la cadena
							RAISE;
						END;
					-- Si devuelve mas de una fila lanzamos error en el log
					WHEN TOO_MANY_ROWS THEN
						pq_utl.log(lc, 'Error al asignar una comision (filtro SIN entidad), se han dado varios valores para el mismo filtro en la poliza: ' || v_id_poliza);
						v_flag_no_com := true;
					WHEN OTHERS THEN
						ROLLBACK;
						DBMS_OUTPUT.PUT_LINE(SQLERRM);
						pq_utl.log(lc,SQLERRM);
						-- Se escribe en el log el error
						pq_utl.log(lc, 'El proceso ha finalizado CON ERRORES a las ' || to_char(SYSDATE, 'HH24:MI:SS'), 2);
						pq_utl.log(lc, '## FIN ##', 1);
						pq_err.raiser(SQLCODE, 'Error al asignar la comision de las polizas renovables ' || ' [' || SQLERRM || ']');
						-- Se vuelve a lanzar la excepcion para parar la cadena
						RAISE;
					END;
                -- Si devuelve mas de una fila lanzamos error en el log
                WHEN TOO_MANY_ROWS THEN
                    pq_utl.log(lc, 'Error al asignar una comision (filtro CON entidad y modulo generico), se han dado varios valores para el mismo filtro en la poliza: ' || v_id_poliza);
                    v_flag_no_com := true;
                WHEN OTHERS THEN
                    ROLLBACK;
                    DBMS_OUTPUT.PUT_LINE(SQLERRM);
                    pq_utl.log(lc,SQLERRM);
                    -- Se escribe en el log el error
                    pq_utl.log(lc, 'El proceso ha finalizado CON ERRORES a las ' || to_char(SYSDATE, 'HH24:MI:SS'), 2);
                    pq_utl.log(lc, '## FIN ##', 1);
                    pq_err.raiser(SQLCODE, 'Error al asignar la comision de las polizas renovables ' || ' [' || SQLERRM || ']');
                    -- Se vuelve a lanzar la excepcion para parar la cadena
                    RAISE;
				END;
			-- Si devuelve mas de una fila lanzamos error en el log
			WHEN TOO_MANY_ROWS THEN
				pq_utl.log(lc, 'Error al asignar una comision (filtro CON entidad), se han dado varios valores para el mismo filtro en la poliza: ' || v_id_poliza);
				v_flag_no_com := true;
			WHEN OTHERS THEN
				ROLLBACK;
				DBMS_OUTPUT.PUT_LINE(SQLERRM);
				pq_utl.log(lc,SQLERRM);
				-- Se escribe en el log el error
				pq_utl.log(lc, 'El proceso ha finalizado CON ERRORES a las ' || to_char(SYSDATE, 'HH24:MI:SS'), 2);
				pq_utl.log(lc, '## FIN ##', 1);
				pq_err.raiser(SQLCODE, 'Error al asignar la comision de las polizas renovables ' || ' [' || SQLERRM || ']');
				-- Se vuelve a lanzar la excepcion para parar la cadena
				RAISE;
			END;
        
			-- Si se ha encontrado un resultado exactamente actualizamos los registros
			IF NOT v_flag_no_com THEN
        
				pq_utl.log(lc, 'Se ha encontrado comision para la poliza ' || v_id_poliza || '. Se procede a actualizar informacion');
           
				l_num_plzs_comision := l_num_plzs_comision + 1;
                   
				-- Grabamos los datos de las comisiones y el estado
				pq_utl.log(lc, 'Asignamos la comision y actualizamos el estado en gastos'); 
				UPDATE o02agpe0.TB_GASTOS_RENOVACION GR
				SET GR.COMISION_MEDIADOR = v_comision,
					GR.COMISION_ESMED = v_comision,
					GR.ESTADO_AGROPLUS = '2' -- Gastos asignados
				WHERE GR.IDPOLIZARENOVABLE = v_id_poliza 
				AND GR.GRUPO_NEGOCIO = v_idgrupo;
            
				-- Insertamos el nuevo registro en tb_plz_renov_hist_estados
				pq_utl.log(lc, 'Insertamos el nuevo registro en el historico de estados de polizas renovables'); 
				INSERT INTO TB_PLZ_RENOV_HIST_ESTADOS 
					(ID, IDPOLIZARENOVABLE, ESTADO_AGROSEGURO, ESTADO_AGROPLUS, FECHA, USUARIO, PCT_COMISION_MEDIADOR, PCT_COMISION_ENTIDAD, PCT_COMISION_ESMED, COSTE_TOTAL_TOMADOR, IMPORTE_DOMICILIAR, GRUPO_NEGOCIO)
				VALUES (SQ_PLZ_RENOV_HIST_ESTADOS.NEXTVAL, v_id_poliza, v_estado_agroseguro, '2', sysdate, 'BATCH_C', v_comision, v_comision_entidad, v_comision, v_coste_total_tomador, v_coste_total_tomador, v_idgrupo);
        
				--Fin de la actualizacion de registros
				pq_utl.log(lc, 'Fin de la actualizacion de registros'); 
			ELSE
				-- Si no se ha encontrado un resultado aumentamos el contador de polizas sin asignar
				l_num_plzs_sin_comision := l_num_plzs_sin_comision + 1;
            END IF;    
		END IF;
        
    END LOOP;
      
    --Fin del bucle
    pq_utl.log(lc, 'Fin del bucle de las polizas renovables'); 
      
    COMMIT;
      
    pq_utl.log(lc, '');
    pq_utl.log(lc, '*********************************************************************************', 2);
    pq_utl.log(lc, 'ESTADISTICAS DE ASIGNACI�N DE COMISIONES EN RENOVABLES,  FECHA ' || to_char(SYSDATE, 'DD/MM/YY HH24:MI:SS'), 2);
    pq_utl.log(lc, '*********************************************************************************', 2);
    pq_utl.log(lc, 'Polizas Renov. tratadas:= ' || l_num_polizas, 2);
    pq_utl.log(lc, 'P�lizas Renov. actualizadas (GASTOS):= ' || l_num_plzs_comision, 2);
    pq_utl.log(lc, 'P�lizas Renov. sin actualizar (GASTOS):= ' || l_num_plzs_sin_comision, 2);
    pq_utl.log(lc, 'P�lizas Renov. sin coste ni prima:= ' || l_num_polizas_no_coste, 2);
    pq_utl.log(lc, 'Hist�ricos de cambio de Estado en Renov. insertados ' || l_num_plzs_comision, 2);
    pq_utl.log(lc, '*********************************************************************************', 2);
    pq_utl.log(lc, '', 2);
      
  
	RETURN 'Se han encontrado ' || l_num_polizas || ' polizas renovables y se han actualizado ' || l_num_plzs_comision || ' polizas renovables. ';
  
  
	-- Control de excepciones
	EXCEPTION
		WHEN no_polizas_found THEN
			-- Rollback por si se ha hecho algun update
			ROLLBACK;
			-- Se cierra el cursor
			CLOSE l_tp_cursor;
			-- Se escribe en el log el error
			pq_utl.log(lc, 'El proceso ha finalizado correctamente a las ' || to_char(SYSDATE, 'HH24:MI:SS'), 2);
			pq_utl.log(lc, '## FIN ##', 1);

			RETURN 'Se han encontrado ' || l_num_polizas || ' polizas definitivas.';

		WHEN OTHERS THEN
			ROLLBACK;
      DBMS_OUTPUT.PUT_LINE(SQLERRM);
      pq_utl.log(lc,SQLERRM);
			-- Se escribe en el log el error
			pq_utl.log(lc, 'El proceso ha finalizado CON ERRORES a las ' || to_char(SYSDATE, 'HH24:MI:SS'), 2);
			pq_utl.log(lc, '## FIN ##', 1);
			pq_err.raiser(SQLCODE, 'Error al asignar la comision de las polizas renovables ' || ' [' || SQLERRM || ']');
			-- Se vuelve a lanzar la excepcion para parar la cadena
			RAISE;
  
  END;
	-- Fin del cuerpo de la funcion
  
  
 -- Inicio de declaracion de funcion
 FUNCTION crearQuery RETURN VARCHAR2 IS
 
    lc VARCHAR2(50) := 'pq_asignar_comis_renov.crearQuery'; -- Variable que almacena el nombre del paquete y de la funcion

		-- Variables para componer la consulta para obtener las polizas renovables con estado agroplos '1' y '5'
		l_query           VARCHAR2(12500);
		l_query_select    VARCHAR2(4000);
		l_query_from      VARCHAR2(4000);
		l_query_order     VARCHAR2(3000);

		BEGIN

			pq_utl.log(lc, 'Monta la query para recuperar las polizas renovables ', 2);



			----------------------------
			l_query_select := 	'SELECT 
                                  PR.ID,
                                  PR.PLAN,
                                  PR.LINEA,
                                  CR.CODENTIDAD,
                                  CR.CODENTIDADMED,
                                  CR.CODSUBENTMED,
                                  PR.COSTE_TOTAL_TOMADOR,
                                  GR.GRUPO_NEGOCIO,
								  ''1'', --CODMODULO FIJO PARA GANADO
                                  PR.ESTADO_AGROSEGURO,
                                  GR.COMISION_ENTIDAD';

			l_query_from := ' 	FROM 	o02agpe0.TB_POLIZAS_RENOVABLES PR,
										            o02agpe0.TB_COLECTIVOS_RENOVACION CR,
                                o02agpe0.TB_GASTOS_RENOVACION GR

          								WHERE PR.IDCOLECTIVO = CR.ID
                            AND PR.ID = GR.IDPOLIZARENOVABLE
                            AND GR.ESTADO_AGROPLUS = ''1'' 
                            AND PR.PLAN >= (TO_CHAR(sysdate, ''YYYY'') - 1)'; -- PLAN MAYOR O IGUAL QUE ANTERIOR
                    
      l_query_order := ' order by pr.id ';

		

			l_query:= 
				l_query_select ||
				l_query_from ||
        l_query_order;

				RETURN l_query;
 
 
 END;
 -- Fin del cuerpo de la funcion 
  
  
end PQ_ASIGNAR_COMIS_RENOV;
/
SHOW ERRORS;
