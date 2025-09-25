SET DEFINE OFF;
SET SERVEROUTPUT ON;

CREATE OR REPLACE PACKAGE O02AGPE0.pq_crear_inserts_vistas_campos AUTHID CURRENT_USER IS

  -- Inserta en las tablas TB_MTOINF_VISTAS y TB_MTOINF_VISTAS_CAMPOS los registros correspondientes
  -- a la vista pasada como parámetro
	PROCEDURE crear_inserts (vista  IN VARCHAR2, nombre_vista IN VARCHAR2);

  -- Cambia el nombre de los registros de la tabla TB_MTOINF_CAMPOS_PERMITIDOS que tengan el mismo campo ABREVIADO
  PROCEDURE actualizar_nombres_duplicados;

END pq_crear_inserts_vistas_campos;

 
/
CREATE OR REPLACE PACKAGE BODY O02AGPE0.pq_crear_inserts_vistas_campos AS

	-- Inicio de declaración de función
	PROCEDURE crear_inserts (vista  IN VARCHAR2, nombre_vista IN VARCHAR2) IS

  -- Variables generales
	lc VARCHAR2(50) := 'pq_crear_inserts_vistas_campos.crear_inserts'; -- Variable que almacena el nombre del paquete y de la función
  l_query           VARCHAR2(2000); -- Almacena la consulta para obtener los campos de las vistas a insertar
  l_insert          VARCHAR2(2000); -- Almacena la consulta para insertar un registro en TB_MTOINF_VISTAS_CAMPOS
	TYPE tpcursor IS REF CURSOR; -- Tipo cursor
  l_tp_cursor       tpcursor; -- Cursor para la consulta
  l_tipo_campo      NUMBER(1);

  -- Variables para volcar los datos devueltos de la query
  v_table_name      all_tab_columns.TABLE_NAME%TYPE;
  v_column_name     all_tab_columns.COLUMN_NAME%TYPE;
  v_data_type       all_tab_columns.DATA_TYPE%TYPE;

		-- Inicio del cuerpo de la función
	BEGIN

		-- Comienzo de escritura en log
    pq_utl.log(lc, '##  -------------------------------------------  ##', 1);
		pq_utl.log(lc, '##  -------------- crear_inserts --------------  ##', 1);
    pq_utl.log(lc, '##  -------------------------------------------  ##', 1);

    -- Inserta en la tabla TB_MTOINF_VISTAS la vista indicada
    INSERT INTO o02agpe0.tb_mtoinf_vistas VALUES (o02agpe0.sq_mtoinf_vistas.nextval,nombre_vista,vista,1);

		-- Se compone la consulta para recuperar los campos de la vista indicada
		pq_utl.log(lc, 'Monta la query para recuperar los campos de la vista indicada ', 2);
		l_query := 'SELECT ATC.TABLE_NAME, ATC.COLUMN_NAME, ATC.DATA_TYPE FROM ALL_TAB_COLUMNS ATC WHERE ATC.TABLE_NAME = ''' || vista || '''' ;
    --l_query := 'SELECT ATC.TABLE_NAME, ATC.COLUMN_NAME, ATC.DATA_TYPE FROM ALL_TAB_COLUMNS ATC WHERE ATC.TABLE_NAME = ''TB_INF_POLIZAS_PARCELAS''' ;


		-- Pinta la query en el log
    DBMS_OUTPUT.put_line (l_query);
		pq_utl.log(lc, 'Query -> ' || l_query);

		-- Crea el cursor para la consulta
		pq_utl.log(lc, 'Abre el cursor para la query');
		OPEN l_tp_cursor FOR l_query;

		-- Bucle para recorrer todos los registros del cursor
		LOOP

			-- Vuelca los datos del cursor en las variables indicadas
			pq_utl.log(lc, 'Volcado de datos del cursor en variables');
			FETCH l_tp_cursor	INTO v_table_name, v_column_name, v_data_type;

			-- Si no ha más registros
			IF (l_tp_cursor%NOTFOUND) THEN
				-- Sale de la función
				COMMIT;
        EXIT;
			END IF;

      -- Obtiene el tipo de campo
      -- MPM 09/08/2012 - Modificación por cambio de los códigos de tipo de campo
      -- Numérico = 2, Texto = 0, Fecha = 1
      IF (v_data_type = 'NUMBER') THEN
         l_tipo_campo := 2;
      ELSE
        IF (v_data_type = 'DATE') THEN
           l_tipo_campo := 1;
        ELSE
           l_tipo_campo := 0;
        END IF;
      END IF;



			-- Hace el insert de los datos obtenidos
      l_insert := 'INSERT INTO o02agpe0.TB_MTOINF_VISTAS_CAMPOS VALUES ('
                  || o02agpe0.sq_mtoinf_vistas_campos.nextval || ',' || o02agpe0.sq_mtoinf_vistas.currval || ','''
                  || v_column_name || ''',''' || v_column_name || ''',''' || vista || ''', ' || l_tipo_campo || ' , 1, 0)';


      pq_utl.log(lc, 'Insertar --> ' || l_insert);
      execute immediate (l_insert);

		END LOOP;


    EXCEPTION
		WHEN OTHERS THEN
      -- Rollback de las transacciones
			ROLLBACK;
      pq_utl.log(lc, SQLCODE || ' - ' || SQLERRM, 2);
      -- Se escribe en el log el error
			pq_utl.log(lc, 'ERROR - Se hace rollback de las transacciones ', 2);

	END;
	-- Fin del cuerpo de la función


  -- Inicio de declaración de función
	PROCEDURE actualizar_nombres_duplicados  IS

  l_query           VARCHAR2(2000); -- Almacena la consulta para obtener los campos de las vistas a actualizar
	lc VARCHAR2(100) := 'pq_crear_inserts_vistas_campos.actualizar_nombres_duplicados'; -- Variable que almacena el nombre del paquete y de la función
  TYPE tpcursor IS REF CURSOR; -- Tipo cursor
  l_tp_cursor       tpcursor; -- Cursor para la consulta

  v_id_cp           NUMBER(5); -- Almacena el id del campo permitido obtenido en cada iteración del bucle
  v_abr_cp          VARCHAR2(50); -- Almacena el abreviado del campo permitido obtenido en cada iteración del bucle
  v_cont            NUMBER(2) := 1; -- Contador utilizado para cambiar el campo abreviado
  v_abr_cp_ant      VARCHAR2(50); -- Almacena el abreviado del campo permitido obtenido en la anterior iteración del bucle
  l_update          VARCHAR2(2000); -- Almacena la consulta para actualizar el campo abreviado

  BEGIN

  -- Comienzo de escritura en log
  pq_utl.log(lc, '##  -----------------------------------------------------------  ##', 1);
  pq_utl.log(lc, '##  -------------- actualizar_nombres_duplicados --------------  ##', 1);
  pq_utl.log(lc, '##  -----------------------------------------------------------  ##', 1);

  -- Obtiene los registros con abreviado repetido
  pq_utl.log(lc, 'Monta la query para recuperar los registros con abreviado repetido ', 2);
  l_query := 'select cp.id, cp.abreviado
              from o02agpe0.tb_mtoinf_campos_permitidos cp
              where cp.abreviado in (
              select cp.abreviado from o02agpe0.tb_mtoinf_campos_permitidos cp group by cp.abreviado having count(*)>1)
              order by cp.abreviado';

  -- Crea el cursor para la consulta
  pq_utl.log(lc, 'Abre el cursor para la query');
  OPEN l_tp_cursor FOR l_query;

  -- Bucle para recorrer todos los registros del cursor
	LOOP
      -- Vuelca los datos del cursor en las variables indicadas
			pq_utl.log(lc, 'Volcado de datos del cursor en variables');
			FETCH l_tp_cursor	INTO v_id_cp, v_abr_cp;

      -- Si no ha más registros
			IF (l_tp_cursor%NOTFOUND) THEN
				-- Sale de la función
				COMMIT;
        EXIT;
			END IF;

      -- Si el abreviado de la iteración actual es diferente al de la anterior y éste no está vacío (primera iteración)
      IF (v_abr_cp <> v_abr_cp_ant AND v_abr_cp_ant IS NOT NULL) THEN
         -- Inicialización del contador
         v_cont := 1;
      END IF;

      -- Se modifica el campo abreviado
      l_update := 'UPDATE o02agpe0.TB_MTOINF_CAMPOS_PERMITIDOS CP SET CP.ABREVIADO='''
                   || v_abr_cp || '_' || v_cont || ''' WHERE CP.ID=' || v_id_cp;

      execute immediate (l_update);
      pq_utl.log(lc, 'Actualizar --> ' || l_update);

      v_cont := v_cont + 1;
      v_abr_cp_ant := v_abr_cp;

  END LOOP;


  EXCEPTION
  WHEN OTHERS THEN
    -- Rollback de las transacciones
	  ROLLBACK;
	  -- Se escribe en el log el error
	  pq_utl.log(lc, 'ERROR - Se hace rollback de las transacciones ', 2);

  END;
  -- Fin del cuerpo de la función


END pq_crear_inserts_vistas_campos;
-- Fin de declaración de paquete
/

SHOW ERRORS;