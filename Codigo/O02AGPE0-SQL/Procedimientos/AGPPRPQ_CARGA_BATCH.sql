create or replace package O02AGPE0.PQ_CARGA_BATCH is

  -- Author  : U028783
  -- Created : 23/12/2010
  -- Purpose : Insertar los recibos en la base de datos

--Procedimiento para cargar los ficheros XML de recibos en la base de datos
--Busca los ficheros canónicos en 'RUTA' y los inserta en la base de datos en las tablas de 'ENTIDAD'
PROCEDURE PR_CARGAXMLS (RUTA IN VARCHAR2, ENTIDAD IN VARCHAR2);

--Función para realizar la inserción de ficheros canónicos en la base de datos
FUNCTION FN_XML2TABLE (FICHERO_CANONICO IN VARCHAR2, RUTA IN VARCHAR2, TABLA IN VARCHAR2) RETURN NUMBER;

--Procedimiento para consultar el ultimo valor utilizado de una secuencia
FUNCTION FN_GET_VALOR_SQ (NOMBRE_SQ IN VARCHAR2) RETURN NUMBER;

--Procedimiento para actualizar el valor de una secuencia en función del 'CAMPO' de la 'TABLA' indicados como parámetros
PROCEDURE PR_SET_VALOR_SQ (NOMBRE_SQ IN VARCHAR2, TABLA IN VARCHAR2, CAMPO IN VARCHAR2);

end PQ_CARGA_BATCH;
/
CREATE OR REPLACE PACKAGE BODY O02AGPE0.PQ_CARGA_BATCH IS

  --Procedimiento que recibe el directory donde se encuentran los ficheros xml canónicos
  -- y la entidad en la que se insertarán los datos contenidos en los mismos. Los posibles
  -- valores de entidad son: 'RECIBO', 'COPY'
	PROCEDURE PR_CARGAXMLS (RUTA IN VARCHAR2, ENTIDAD IN VARCHAR2) IS
		--Declaración de variables
		v_rows NUMBER;
		consulta VARCHAR2(2000) := 'SELECT * FROM TB_CONFIG_AGP WHERE AGP_NEMO LIKE ''TB_' || ENTIDAD || '%''';

		TYPE cur_typ IS REF CURSOR;
		c_recibos cur_typ;

		v_nemo TB_CONFIG_AGP.AGP_NEMO%TYPE;
		v_valor TB_CONFIG_AGP.AGP_VALOR%TYPE;
		v_desc TB_CONFIG_AGP.AGP_DESCRIPCION%TYPE;

    err_num NUMBER;
    err_msg VARCHAR2(2000);
	BEGIN
		--Cuerpo del porcedimiento
		--IMPORTANTE: TODAS LAS FK DE 'ENTIDAD' DEBEN SER DEFERRED PARA QUE SE COMPRUEBE LA INTEGRIDAD AL FINAL
		BEGIN
			-- Se obtienen los ficheros xml canónicos a insertar y las tablas correspondientes de la tabla TB_CONFIG_AGP.
			-- En AGP_NEMO está el nombre de la tabla y en AGP_VALOR el fichero canónico asociado
			-- Por cada fichero se llama a la función que lo inserta en la base de datos.
			pq_utl.LOG ('******** Inicio de la carga de ' || ENTIDAD || '. ********', 2);
			OPEN c_recibos FOR consulta;

			LOOP
				FETCH c_recibos INTO v_nemo, v_valor, v_desc;
				EXIT WHEN c_recibos%NOTFOUND;
				pq_utl.LOG ('--> Carga de ' || v_desc || '----' , 2);
				v_rows := PQ_CARGA_BATCH.FN_XML2TABLE (v_valor, RUTA, v_nemo);

				--Si se ha producido error, pintamos una traza y paramos la ejecución
			END LOOP;

			COMMIT;
			pq_utl.LOG ('******** Fin de la carga de ' || ENTIDAD || '. ********', 2);
		EXCEPTION
			WHEN OTHERS THEN
				ROLLBACK;
				err_num := SQLCODE;
				err_msg := SQLERRM;
				pq_utl.LOG ('PQ_RECIBOS - Error al insertar ' || ENTIDAD || ' - ERROR: ' || err_num || '-' || err_msg || ' ---', 2);
				RETURN;
		END;
	END;

	-- Función que inserta los datos contenidos en FICHERO_CANONICO, ubicado en RUTA, en TABLA.
	FUNCTION FN_XML2TABLE (FICHERO_CANONICO IN VARCHAR2, RUTA IN VARCHAR2, TABLA IN VARCHAR2) RETURN NUMBER IS
		--Declaración de variables
		v_charsetid	VARCHAR2(15);
		xml_canonico	XMLTYPE;
		v_context	DBMS_XMLSTORE.ctxtype;
		v_rows		NUMBER := 0;
    existe NUMBER;
    CURSOR c_columnas IS
         SELECT column_name FROM all_tab_cols WHERE UPPER(owner) = 'O02AGPE0' AND table_name = TABLA ORDER BY column_id;
	BEGIN
		--Obtener el charset
		SELECT VALUE INTO v_charsetid FROM v$nls_parameters WHERE parameter = 'NLS_CHARACTERSET';

		--Cargar el fichero canonico
		xml_canonico := XMLTYPE (BFILENAME (DIRECTORY => RUTA, filename => FICHERO_CANONICO), NLS_CHARSET_ID (v_charsetid));
    --Compruebo que hay elementos para insertar en el fichero xml
    existe := xml_canonico.existsNode('/ROWSET/ROW');

    IF (existe > 0) THEN
  		--Abrir el contexto
  		v_context := DBMS_XMLSTORE.newcontext (targettable => TABLA);

  		--Marcamos las columnas a insertar
  		DBMS_XMLSTORE.clearupdatecolumnlist (ctxhdl => v_context);

  		FOR columna IN c_columnas LOOP
  			DBMS_XMLSTORE.setupdatecolumn (ctxhdl => v_context, colname => columna.column_name);
  		END LOOP;

  		--Realizamos la inserción del fichero canónico en la base de datos
  		v_rows := DBMS_XMLSTORE.insertxml (ctxhdl => v_context, xdoc => xml_canonico);

  		--Se cierra el contexto
  		DBMS_XMLSTORE.closecontext (ctxhdl => v_context);
    END IF;
  	pq_utl.LOG ('===> ' || v_rows || ' Insertadas en ' || TABLA || '.-----', 2);

		RETURN v_rows;
	END;

  FUNCTION FN_GET_VALOR_SQ (NOMBRE_SQ IN VARCHAR2) RETURN NUMBER IS
      num_seq NUMBER;
  BEGIN
      --Obtenemos el valor actual de la secuencia
      EXECUTE IMMEDIATE 'SELECT ' ||NOMBRE_SQ ||'.CURVAL FROM dual' INTO num_seq;
      RETURN num_seq;
  END;

  PROCEDURE PR_SET_VALOR_SQ (NOMBRE_SQ IN VARCHAR2, TABLA IN VARCHAR2, CAMPO IN VARCHAR2) IS
      num_seq NUMBER;
      max_id  NUMBER;
      incremento NUMBER;
  BEGIN
      --Obtenemos el valor actual de la secuencia
      EXECUTE IMMEDIATE 'SELECT ' || NOMBRE_SQ ||'.CURVAL FROM dual' INTO num_seq;
      --Obtenemos el mayor de 'CAMPO' en 'TABLA'
      EXECUTE IMMEDIATE 'SELECT MAX(' || CAMPO ||') FROM ' || TABLA || '' INTO num_seq;
      --Calculamos el incremento de la secuencia
      incremento := max_id - num_seq;
      --Incrementamos la secuencia
      EXECUTE IMMEDIATE 'ALTER SEQUENCE ' || NOMBRE_SQ || ' INCREMENT BY ' || incremento;
  END;

END PQ_CARGA_BATCH;
/
