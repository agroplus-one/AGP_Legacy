SET DEFINE OFF;
SET SERVEROUTPUT ON;

create or replace package o02agpe0.PQ_IMPORTACION_CSV AUTHID CURRENT_USER is

  -- Author  : U028783
  -- Created : 22/10/2012
  -- Purpose : Cargar los datos enviados por Agroseguro en el modelo de la aplicación Agroplus.

  -- Constantes
  HABILITAR     VARCHAR2(10) := 'ENABLE';
  DESHABILITAR  VARCHAR2(10) := 'DISABLE';

  STATUS_HABILITAR     VARCHAR2(10) := 'ENABLED';
  STATUS_DESHABILITAR  VARCHAR2(10) := 'DISABLED';

  -- Public type declarations
  TYPE t_array IS TABLE OF VARCHAR2(2000) INDEX BY BINARY_INTEGER;

  -- Procedimiento para lanzar la carga
  PROCEDURE PR_CARGAXMLS (P_TABLAS IN VARCHAR2, P_TIPOIMPORTACION IN NUMBER, P_PLAN IN NUMBER, P_LINEA IN NUMBER,
                          P_RUTA IN VARCHAR2, P_FICHEROS IN VARCHAR2, P_TABLAS_ERROR IN VARCHAR2);

  PROCEDURE PR_XML2TABLE (P_TABLA_ORIGEN IN VARCHAR2, P_TABLA_DESTINO IN VARCHAR2, P_IDHISTORICO IN NUMBER,
                          P_NUMTABLA IN NUMBER, P_IDTABLA IN NUMBER, P_FICHERO IN VARCHAR2);

  --Procedimiento para recuperar el backup de las tablas a importar.
  PROCEDURE PR_RESTORE_BACKUP (TABLAS IN VARCHAR2);

  PROCEDURE PR_ACTUALIZA_SUBTERMINO (tabla IN VARCHAR2);

  --Procedimiento para cargar la denominación de origen por defecto (codigo 0)
  PROCEDURE PR_CARGA_DENOM_ORIGEN_DEFECTO (P_LINEASEGUROID in number);

  PROCEDURE PR_CARGA_CULTIVOS (P_LINEASEGUROID IN NUMBER);

  PROCEDURE PR_CARGA_VARIEDADES (P_LINEASEGUROID IN NUMBER);

  -- Funciones para realizar las inserciones para las provincias 99
  PROCEDURE PR_CARGA_MODULOS (P_LINEASEGUROID IN NUMBER);

  PROCEDURE PR_CARGA_LINEAS;

  --Procedimiento para cargar el sistema de producción por defecto (codigo 0)
  PROCEDURE PR_CARGA_SIST_PROD_DEFECTO;

  PROCEDURE PR_COPIA_TEMP_SUBVENCIONES(P_TABLA_DESTINO IN VARCHAR2, P_LINEASEGUROID IN NUMBER);

  --Procedimiento para actualizar los identificadores de las subvenciones ENESA
  PROCEDURE PR_ACTUALIZA_IDS_SUBV_ENESA(P_TABLA_DESTINO IN VARCHAR2, P_LINEASEGUROID IN NUMBER);

  --Procedimiento para actualizar los identificadores de las subvenciones CCAA
  PROCEDURE PR_ACTUALIZA_IDS_SUBV_CCAA(P_TABLA_DESTINO IN VARCHAR2, P_LINEASEGUROID IN NUMBER);

  --Procedimiento para actualizar la tabla utilizada para la generación de los cuadros de coberturas
  PROCEDURE PR_CARGA_TABLA_COBERTURAS;

  /*
  P_DIRECTORIO IN VARCHAR2, P_TABLA_ORIGEN IN VARCHAR2, P_TABLA_DESTINO IN VARCHAR2,
      P_PLAN IN NUMBER, P_LINEA IN NUMBER, P_LINEASEGUROID IN NUMBER, P_IDHISTORICO IN NUMBER, P_NUMTABLA IN NUMBER,
      P_IDTABLA IN NUMBER, P_TIPO_SC IN VARCHAR2, P_FICHERO IN VARCHAR2
  */
  --Procedimiento para ejecutar las sentencias previas a la importación de una tabla
  PROCEDURE PR_EXECUTE_PRE_SQL (ID_TABLA IN VARCHAR2, P_PLAN IN NUMBER, P_LINEA IN NUMBER,
                                P_LINEASEGUROID IN NUMBER, P_TABLA_DESTINO IN VARCHAR2);

  --Procedimiento para ejecutar las sentencias posteriores a la importación de una tabla
  PROCEDURE PR_EXECUTE_POST_SQL (ID_TABLA IN VARCHAR2, P_PLAN IN NUMBER, P_LINEA IN NUMBER,
                                P_LINEASEGUROID IN NUMBER, P_TABLA_DESTINO IN VARCHAR2);

  --Función para obtener un array a partir de una cadena de texto y un separador
  FUNCTION FN_SPLIT (cadena IN VARCHAR2, separador IN CHAR) RETURN t_array;

  --Procedimiento para deshabilitar las constraints
  PROCEDURE PR_DESHABILITA_FKS(TABLAS IN VARCHAR2);

  --Procedimiento para habilitar las constraints
  PROCEDURE PR_HABILITA_FKS;

  --Procedimiento para crear las pantallas configurables para una línea
  PROCEDURE PR_CREAPANTALLACONFIGURABLE (LINEASEGUROPARAM IN NUMBER);

  FUNCTION FN_DAMEORIGENDATOS (NUMTABLA IN NUMBER) RETURN NUMBER;

  --Procedimiento para insertar errores producidos durante la carga de condicionados
  PROCEDURE PR_INSERTAERROR (tipoImportacion IN NUMBER, plan IN NUMBER, linea IN NUMBER, error IN VARCHAR2);

  -- Procedimiento para realizar las inserciones para el gruporaza  999
  PROCEDURE PR_CARGA_GRUPOS_RAZAS (P_LINEASEGUROID IN NUMBER);

  -- Procedimiento para realizar las inserciones para la especie  999
  PROCEDURE PR_CARGA_ESPECIE (P_LINEASEGUROID IN NUMBER);

  -- Procedimiento para realizar las inserciones para el tipo animal  999
  PROCEDURE PR_CARGA_TIPOS_ANIMAL (P_LINEASEGUROID IN NUMBER);

  -- Procedimiento para realizar las inserciones para el regimen Ganado  999
  PROCEDURE PR_CARGA_REGIMEN_GANADO (P_LINEASEGUROID IN NUMBER);

  -- Procedimiento para realizar las inserciones para el grupo negocio 9
  PROCEDURE PR_CARGA_GRUPOS_NEGOCIO;

  PROCEDURE PR_CARGA_CAPITALES_GENERICOS;

  -- Inserta en la tabla de líneas un registro con el plan indicado y la línea genérica si no existe ya
  PROCEDURE PR_CARGA_LINEA_GENERICA (P_PLAN IN NUMBER);

  PROCEDURE PR_CARGA_LINEA_GENERICA;

  PROCEDURE PR_CARGA_LINEAS_GRUPO_NEGOCIO(P_LINEASEGUROID IN NUMBER);

  PROCEDURE PR_CARGA_TIPO_CAPITAL_GN;

  PROCEDURE PR_CARGA_GARANTIZADO;
  
  PROCEDURE PR_XML2TABLE_SIGPAC (
        P_TABLA_ORIGEN IN VARCHAR2 DEFAULT 'TBX_SC_C_AMBITO_SIGPAC', -- Nombre de la tabla origen por defecto
        P_TABLA_DESTINO IN VARCHAR2 DEFAULT 'TB_TERMINOS', -- Nombre de la tabla destino por defecto
        P_IDHISTORICO IN NUMBER, -- Identificador historico
        P_NUMTABLA IN NUMBER, -- Numero de la tabla
        P_FICHERO IN VARCHAR2 -- Nombre del fichero XML
    );

  PROCEDURE PR_XML2TABLE_REGA (
        P_TABLA_ORIGEN IN VARCHAR2 DEFAULT 'TBX_SC_C_AMBITO_REGA', -- Nombre de la tabla origen por defecto
        P_TABLA_DESTINO IN VARCHAR2 DEFAULT 'TB_TERMINOS', -- Nombre de la tabla destino por defecto
        P_IDHISTORICO IN NUMBER, -- Identificador historico
        P_NUMTABLA IN NUMBER, -- Numero de la tabla
        P_FICHERO IN VARCHAR2 -- Nombre del fichero XML
    );
    
    PROCEDURE PR_XML2TABLE_CONDICIONADOS (
        P_TABLA_ORIGEN IN VARCHAR2 DEFAULT 'TBX_SC_C_TABLAS_CONDICIONADO', -- Nombre de la tabla origen por defecto
        P_TABLA_DESTINO IN VARCHAR2 DEFAULT 'TB_SC_C_TABLAS_CONDICIONADO', -- Nombre de la tabla destino por defecto
        P_IDHISTORICO IN NUMBER, -- Identificador historico
        P_NUMTABLA IN NUMBER, -- Numero de la tabla
        P_FICHERO IN VARCHAR2 -- Nombre del fichero XML
    );
    
    PROCEDURE PR_XML2TABLE_SUBCCAA (
        P_TABLA_ORIGEN IN VARCHAR2 DEFAULT 'TBX_SC_C_TIPO_SUBV_CCAA', -- Nombre de la tabla origen por defecto
        P_TABLA_DESTINO IN VARCHAR2 DEFAULT 'TB_SC_C_TIPO_SUBV_CCAA', -- Nombre de la tabla destino por defecto
        P_IDHISTORICO IN NUMBER, -- Identificador historico
        P_NUMTABLA IN NUMBER, -- Numero de la tabla
        P_FICHERO IN VARCHAR2 -- Nombre del fichero XML
    );
    
    

end PQ_IMPORTACION_CSV;
/
CREATE OR REPLACE PACKAGE BODY o02agpe0.PQ_IMPORTACION_CSV IS
   -- Procedimiento para lanzar la carga de cada uno de los xml de agroseguro incluidos en el parámetro
   PROCEDURE PR_CARGAXMLS (P_TABLAS IN VARCHAR2, P_TIPOIMPORTACION IN NUMBER, P_PLAN IN NUMBER, P_LINEA IN NUMBER,
                          P_RUTA IN VARCHAR2, P_FICHEROS IN VARCHAR2, P_TABLAS_ERROR IN VARCHAR2) IS

      V_LC                VARCHAR2(50) := 'PQ_IMPORTACION_CSV.PR_CARGAXMLS';

      V_IDHISTORICO       NUMBER;
      V_LINEASEGUROID     NUMBER (15);
      V_DESC_LINEA        VARCHAR2 (25);

      V_TABLA_DESTINO     o02agpe0.TB_TABLAS_XMLS.NOMBRE%TYPE;
      V_NUM_TABLA         o02agpe0.TB_TABLAS_XMLS.NUMTABLA%TYPE;
      V_IDTABLA           o02agpe0.TB_TABLAS_XMLS.ID%TYPE;
      V_TABLA_EXTERNA     o02agpe0.TB_TABLAS_XMLS.TABLA_EXTERNA%TYPE;
      V_TABLA_BAK         o02agpe0.TB_TABLAS_XMLS.TABLA_BAK%TYPE;
      V_TABLA_TEMP        o02agpe0.TB_TABLAS_XMLS.TABLA_TEMP%TYPE;
      V_TIPO_SC           o02agpe0.TB_TABLAS_XMLS.TIPOSC%TYPE;

      V_CONSULTA          VARCHAR2 (2000)
         :=    'SELECT t.NOMBRE, t.NUMTABLA, t.ID, t.TABLA_EXTERNA, t.TABLA_BAK, t.TABLA_TEMP, t.TIPOSC ' ||
            'FROM o02agpe0.tb_tablas_xmls t WHERE t.NUMTABLA IN (' || P_TABLAS || ') ORDER BY orden';

      TYPE CUR_TYP IS REF CURSOR;

      C_TABLAS_IMPORTAR   CUR_TYP;
      V_FICHERO_TABLA     t_array;
      V_NUMS_TABLAS       t_array;
      V_NUMS_TABLAS_ERROR t_array;
      V_TXT_FICHERO       VARCHAR2 (50);
      V_ERROR_IMPORTACION BOOLEAN := FALSE;
      V_ERR_NUM           NUMBER;
      V_ERR_MSG           VARCHAR2(2000);
      V_ERR_ANTERIOR      VARCHAR2(2000);
      V_ERR_CNT           NUMBER;

      V_RESTORE_BACKUP    BOOLEAN := TRUE;

      --Excepciones
      error_importacion_exception EXCEPTION;

   BEGIN
      V_FICHERO_TABLA := fn_split (P_FICHEROS, ',');
      V_NUMS_TABLAS := fn_split (P_TABLAS, ',');

      execute immediate 'ALTER SESSION SET NLS_LANGUAGE = AMERICAN';
      execute immediate 'ALTER SESSION SET NLS_TERRITORY = AMERICA';
      execute immediate 'ALTER SESSION SET NLS_NUMERIC_CHARACTERS = ".,"';

      --Obtener lineaseguroid en caso de que sea necesario
      IF (P_PLAN is not null AND P_LINEA is not null) THEN

         SELECT lineaseguroid INTO V_LINEASEGUROID FROM o02agpe0.tb_lineas WHERE codplan = P_PLAN AND codlinea = P_LINEA;
         SELECT deslinea INTO V_DESC_LINEA FROM o02agpe0.tb_sc_c_lineas WHERE codlinea = P_LINEA;

         IF (V_DESC_LINEA IS NULL) THEN
           V_DESC_LINEA := '';
         END IF;

         IF (V_LINEASEGUROID IS NULL) THEN
            -- Si no existe la linea => la creamos y obtenemos su identificador
            SELECT o02agpe0.sq_lineaseguro.NEXTVAL INTO V_LINEASEGUROID FROM DUAL;
            INSERT INTO o02agpe0.tb_lineas (lineaseguroid, codlinea, codplan, nomlinea, activo)
                   VALUES (V_LINEASEGUROID, P_LINEA, P_PLAN, V_DESC_LINEA, 'NO');
         ELSE
             -- Actualizamos la descripción de la línea
             UPDATE o02agpe0.TB_LINEAS SET NOMLINEA = V_DESC_LINEA WHERE LINEASEGUROID = V_LINEASEGUROID;
         END IF;

         commit;
       END IF;

       --Obtenemos el identificador para el registro del histórico:
       SELECT o02agpe0.sq_hist_importaciones.NEXTVAL INTO V_IDHISTORICO FROM DUAL;

       IF (P_TABLAS_ERROR IS NOT NULL OR P_TABLAS_ERROR <> '') THEN

          -- Si hay errores en la canonización, insertamos el historico y los errores y salimos
          V_ERROR_IMPORTACION := TRUE;
          --En este caso no hara falta recuperar el backup
          V_RESTORE_BACKUP := FALSE;
          -- Creamos el registro del historico
          INSERT INTO o02agpe0.tb_hist_importaciones (idhistorico, idtipoimportacion, ubicacionficheros, fechaimport, estado, lineaseguroid, descerror)
             VALUES (V_IDHISTORICO, P_TIPOIMPORTACION, P_RUTA, SYSDATE, 'Error', V_LINEASEGUROID, 'Error durante la transformación de los ficheros XML');
          commit;

          -- Insertamos los valores en el detalle
          V_NUMS_TABLAS_ERROR := fn_split (P_TABLAS_ERROR, ',');
          FOR CNT_ERR IN V_NUMS_TABLAS_ERROR.FIRST .. V_NUMS_TABLAS_ERROR.LAST
          LOOP
             INSERT INTO o02agpe0.TB_IMPORTACION_TABLAS VALUES (V_IDHISTORICO, V_NUMS_TABLAS_ERROR(CNT_ERR), 'Error', 'Error al transformar el fichero', null);
          END LOOP;

       ELSE

          pq_utl.LOG (V_LC, '******** Inicio de la importación. ********', 2);

          -- Si no hay errores => comenzamos la importación
          -- Creamos el registro del historico
          INSERT INTO o02agpe0.tb_hist_importaciones (idhistorico, idtipoimportacion, ubicacionficheros, fechaimport, estado, lineaseguroid)
             VALUES (V_IDHISTORICO, P_TIPOIMPORTACION, P_RUTA, SYSDATE, 'Importado', V_LINEASEGUROID);
          commit;

          -- 1. Realizar backup de los datos de las tablas a importar
          --pq_utl.LOG ('******** CREANDO BACKUP.... ********', 2);
          --o02agpe1.PQ_IMPORTACION_CSV.PR_CREATE_BACKUP (P_TABLAS);

          -- 2. Recorrer las tablas a importar e ir ejecutando por cada una de ellas:
          OPEN c_tablas_importar FOR V_CONSULTA;
          LOOP
             FETCH c_tablas_importar INTO V_TABLA_DESTINO, V_NUM_TABLA, V_IDTABLA, V_TABLA_EXTERNA, V_TABLA_BAK, V_TABLA_TEMP, V_TIPO_SC;
             EXIT WHEN c_tablas_importar%NOTFOUND OR V_ERROR_IMPORTACION = TRUE;
             --Incluyo un bloque de excepciones para que no finalice la importación
             BEGIN
                -- 2.1. Sentencias previas.
                pq_utl.LOG (V_LC, '******** EJECUTANDO SENTENCIAS PREVIAS.... ********', 2);
                PR_EXECUTE_PRE_SQL(V_IDTABLA, P_PLAN, P_LINEA, V_LINEASEGUROID, V_TABLA_DESTINO);
                -- 2.2. Inserts desde tabla externa
                FOR cnt IN V_NUMS_TABLAS.FIRST .. V_NUMS_TABLAS.LAST
                LOOP
                   IF (V_NUMS_TABLAS (cnt) = V_NUM_TABLA) THEN
                      V_TXT_FICHERO := V_FICHERO_TABLA(cnt);
                   END IF;
                END LOOP;

                IF (V_IDTABLA = 9) THEN
                   V_NUM_TABLA := 8;
                END IF;

                pq_utl.LOG (V_LC, 'IMPORTANDO LA TABLA NUMERO ' || V_NUM_TABLA || ', ID ' || V_IDTABLA || ' ****', 2);

                IF (V_NUM_TABLA = 176) THEN -- importar y actualizar datos desde la tabla externa de Ambitos SIGPAC
                    PR_XML2TABLE_SIGPAC (V_TABLA_EXTERNA, V_TABLA_DESTINO, V_IDHISTORICO,
                                                             V_NUM_TABLA, V_TXT_FICHERO);
                ELSIF (V_NUM_TABLA = 451) THEN -- importar y actualizar datos desde la tabla externa de Ambitos REGA
                    PR_XML2TABLE_REGA (V_TABLA_EXTERNA, V_TABLA_DESTINO, V_IDHISTORICO,
                                                             V_NUM_TABLA, V_TXT_FICHERO);
                ELSIF (V_NUM_TABLA = 9000) THEN -- importar y actualizar datos desde la tabla externa de Condicionados
                    PR_XML2TABLE_CONDICIONADOS (V_TABLA_EXTERNA, V_TABLA_DESTINO, V_IDHISTORICO,
                                                             V_NUM_TABLA, V_TXT_FICHERO);  
                ELSIF (V_NUM_TABLA = 61) THEN -- importar y actualizar datos desde la tabla externa de Condicionados
                    PR_XML2TABLE_SUBCCAA (V_TABLA_EXTERNA, V_TABLA_DESTINO, V_IDHISTORICO,
                                                             V_NUM_TABLA, V_TXT_FICHERO);                                               
                ELSE
                    PR_XML2TABLE (V_TABLA_EXTERNA, V_TABLA_DESTINO, V_IDHISTORICO,
                                                             V_NUM_TABLA, V_IDTABLA, V_TXT_FICHERO);
                END IF;

                -- 2.3. Sentencias posteriores
                pq_utl.LOG (V_LC, '******** EJECUTANDO SENTENCIAS POSTERIORES.... ********', 2);
                PR_EXECUTE_POST_SQL(V_IDTABLA, P_PLAN, P_LINEA, V_LINEASEGUROID, V_TABLA_DESTINO);

                -- Consolidamos la ejecución
                commit;

             EXCEPTION
                WHEN OTHERS THEN
                   ROLLBACK;
                   V_ERROR_IMPORTACION := TRUE;
                   V_ERR_NUM := SQLCODE;
                   V_ERR_MSG := SQLERRM;
                   PQ_UTL.log(V_LC, 'PR_CARGAXMLS - ERROR: ' || V_ERR_NUM || ' - ' || V_ERR_MSG || ' ****', 2);
                   --Insertamos el error de la importación
                   BEGIN
                   IF (V_IDTABLA = 9 AND V_NUM_TABLA = 8) THEN
                      execute immediate 'select count(*) from o02agpe0.TB_IMPORTACION_TABLAS where IDHISTORICO = ' || V_IDHISTORICO || ' AND CODTABLACONDICIONADO = 8' into V_ERR_CNT;
                      IF (V_ERR_CNT > 0) then
                         execute immediate 'select descestado from o02agpe0.TB_IMPORTACION_TABLAS where IDHISTORICO = ' || V_IDHISTORICO || ' AND CODTABLACONDICIONADO = 8' into V_ERR_ANTERIOR;
                         UPDATE o02agpe0.TB_IMPORTACION_TABLAS SET estado = 'Error', descestado = V_ERR_ANTERIOR || '**' || SUBSTR(V_ERR_MSG, 0, 998)  WHERE IDHISTORICO = V_IDHISTORICO AND CODTABLACONDICIONADO = 8;
                      ELSE
                         INSERT INTO o02agpe0.TB_IMPORTACION_TABLAS VALUES (V_IDHISTORICO, V_NUM_TABLA, 'Error', SUBSTR(V_ERR_MSG, 0, 1000), V_TXT_FICHERO);
                      END IF;
                   elsif (V_IDTABLA = 8 AND V_NUM_TABLA = 8) THEN
                      execute immediate 'select count(*) from o02agpe0.TB_IMPORTACION_TABLAS where IDHISTORICO = ' || V_IDHISTORICO || ' AND CODTABLACONDICIONADO = 8' into V_ERR_CNT;
                      IF (V_ERR_CNT > 0) then
                         execute immediate 'select descestado from o02agpe0.TB_IMPORTACION_TABLAS where IDHISTORICO = ' || V_IDHISTORICO || ' AND CODTABLACONDICIONADO = 8' into V_ERR_ANTERIOR;
                         UPDATE o02agpe0.TB_IMPORTACION_TABLAS SET estado = 'Error', descestado = V_ERR_ANTERIOR || '**' || SUBSTR(V_ERR_MSG, 0, 998)  WHERE IDHISTORICO = V_IDHISTORICO AND CODTABLACONDICIONADO = 8;
                      else
                          INSERT INTO o02agpe0.TB_IMPORTACION_TABLAS VALUES (V_IDHISTORICO, V_NUM_TABLA, 'Error', SUBSTR(V_ERR_MSG, 0, 1000), V_TXT_FICHERO);
                      end if;
                   ELSE
                      INSERT INTO o02agpe0.TB_IMPORTACION_TABLAS VALUES (V_IDHISTORICO, V_NUM_TABLA, 'Error', SUBSTR(V_ERR_MSG, 0, 2000), V_TXT_FICHERO);
                   END IF;
                   COMMIT;
                   EXCEPTION
                      WHEN OTHERS THEN
                         V_ERR_NUM := SQLCODE;
                         V_ERR_MSG := SQLERRM;
                         PQ_UTL.log(V_LC, 'PR_CARGAXMLS - ERROR Importacion Tablas: ' || V_ERR_NUM || ' - ' || V_ERR_MSG || ' ****', 2);
                   END;
             END;
          END LOOP;

          CLOSE c_tablas_importar;

          pq_utl.LOG (V_LC, '******** Fin de la importación. ********', 2);

       END IF;
       COMMIT;

       IF (V_ERROR_IMPORTACION = TRUE) THEN
          V_CONSULTA := 'UPDATE o02agpe0.TB_HIST_IMPORTACIONES SET ESTADO = ''Error'' WHERE IDHISTORICO = ' || V_IDHISTORICO;
          EXECUTE IMMEDIATE V_CONSULTA;

          -- 2.4. Si ha habido errores => parar la carga, insertar el error y recuperar el backup de las tablas importadas
          IF (V_RESTORE_BACKUP = TRUE) THEN
             pq_utl.LOG (V_LC, '******** RECUPERANDO BACKUP.... ********', 2);
             PR_RESTORE_BACKUP (P_TABLAS);
             pq_utl.LOG (V_LC, '******** BACKUP RECUPERADO ********', 2);
          END IF;

          if (V_LINEASEGUROID is not null) then
              --Actualizo el estado de la activación
              V_CONSULTA := 'UPDATE o02agpe0.TB_LINEAS SET ACTIVO = ''SI'' WHERE LINEASEGUROID = ' || V_LINEASEGUROID;
              EXECUTE IMMEDIATE V_CONSULTA;
          end if;
       ELSE
          -- Si la importación no es del condicionado general => no compruebo la activación
          IF (V_LINEASEGUROID is not null) THEN
              IF (PQ_ACTIVACION.FN_PERMITE_ACTIVACION(V_LINEASEGUROID) = 1) THEN
                  V_CONSULTA := 'UPDATE o02agpe0.TB_LINEAS SET ESTADO = ''IMPORTADO'', ACTIVO = ''NO'' WHERE LINEASEGUROID = ' || V_LINEASEGUROID;
              ELSE
                  V_CONSULTA := 'UPDATE o02agpe0.TB_LINEAS SET ESTADO = ''INCOMPLETO'', ACTIVO = ''NO'' WHERE LINEASEGUROID = ' || V_LINEASEGUROID;
              END IF;
          END IF;
          EXECUTE IMMEDIATE V_CONSULTA;
       END IF;
       COMMIT;

       -- 3. Enviar correo resumen con los resultados de la importación


   END PR_CARGAXMLS;

   PROCEDURE PR_XML2TABLE (P_TABLA_ORIGEN IN VARCHAR2, P_TABLA_DESTINO IN VARCHAR2, P_IDHISTORICO IN NUMBER,
                          P_NUMTABLA IN NUMBER, P_IDTABLA IN NUMBER, P_FICHERO IN VARCHAR2) IS
      V_LC                VARCHAR2(50) := 'PQ_IMPORTACION_CSV.PR_XML2TABLE';
      v_sql               VARCHAR2(32000) := NULL;

   BEGIN
      v_sql := 'INSERT INTO o02agpe0.' || P_TABLA_DESTINO || ' (SELECT * FROM o02agpe1.' || P_TABLA_ORIGEN || ')';
      -- ejecutamos la sentencia
      pq_utl.log(V_LC, 'Inserción con tabla externa: ' || v_sql || ' --', 2);
      execute immediate v_sql;
      pq_utl.log(V_LC, 'Fin de la inserción en ' || P_TABLA_DESTINO || ' --', 2);

      --Insertamos la importacion correcta
      IF (P_IDTABLA != 9) THEN
         INSERT INTO o02agpe0.tb_importacion_tablas VALUES (P_IDHISTORICO, P_NUMTABLA, 'Importado', NULL, P_FICHERO);
      END IF;

      --En este punto devolvemos el control al procedimiento principal para que ejecute las acciones POST
   END PR_XML2TABLE;

   -- Procedimiento para copiar los datos de las tablas indicadas como parametro a las tablas originales del condicionado
   PROCEDURE PR_RESTORE_BACKUP (TABLAS IN VARCHAR2) IS
      consulta VARCHAR2 (2000) := 'SELECT NOMBRE, TABLA_BAK FROM o02agpe0.tb_tablas_xmls t WHERE t.NUMTABLA IN ('
            || tablas
            || ')';

      V_LC                VARCHAR2(50) := 'PQ_IMPORTACION_CSV.PR_RESTORE_BACKUP';

      TYPE cur_typ IS REF CURSOR;
      c_tablas cur_typ;

      sql_backup varchar2(2000);
      tabla_ori o02agpe0.TB_TABLAS_XMLS.NOMBRE%TYPE;
      tabla_bak o02agpe0.TB_TABLAS_XMLS.TABLA_TEMP%TYPE;
   BEGIN
      pq_utl.LOG (V_LC, '******** RECUPERANDO BACKUP.... ********', 2);
      /*pq_utl.LOG (V_LC, '******** RECUPERANDO BACKUP.... ********', 2);
      -- DESHABILITAR LAS CONSTRAINTS
      PQ_UTL.log(V_LC, 'PR_RESTORE_BACKUP - DESHABILITAR CONSTRAINTS  ', 2);
      PQ_IMPORTACION_CSV.PR_DESHABILITA_FKS(TABLAS);
      -- RECUPERAR LA COPIA DE SEGURIDAD
      BEGIN
         OPEN c_tablas FOR consulta;
         LOOP
            FETCH c_tablas INTO tabla_ori, tabla_bak;
            EXIT WHEN c_tablas%NOTFOUND;
            sql_backup := 'TRUNCATE TABLE o02agpe0.' || tabla_ori;
            PQ_UTL.log(V_LC, 'BORRADO - PR_RESTORE_BACKUP: ' || sql_backup || '  ', 2);
            --execute immediate sql_backup;
            truncate_table('o02agpe0.' || tabla_ori);
            COMMIT;

            sql_backup := 'INSERT INTO o02agpe0.' || tabla_ori || ' (SELECT * FROM o02agpe1.' || tabla_bak || ')';
            PQ_UTL.log(V_LC, 'INSERCION - PR_RESTORE_BACKUP: ' || sql_backup || '  ', 2);
            execute immediate sql_backup;
            COMMIT;

         END LOOP;
         close c_tablas;
      EXCEPTION
         WHEN OTHERS THEN
            PQ_UTL.log(V_LC, 'PR_RESTORE_BACKUP - ERROR AL RECUPERAR EL BACKUP ' || SQLCODE || ' - ' || SQLERRM || ' ***', 2);
      END;

      -- HABILITAR LAS CONSTRAINTS
      PQ_UTL.log(V_LC, 'PR_RESTORE_BACKUP - HABILITAR CONSTRAINTS  ', 2);
      PQ_IMPORTACION_CSV.PR_HABILITA_FKS;
      pq_utl.LOG (V_LC, '******** BACKUP RECUPERADO CORRECTAMENTE ********', 2);*/
   END PR_RESTORE_BACKUP;

   PROCEDURE PR_ACTUALIZA_SUBTERMINO (tabla IN VARCHAR2)
   IS
      consulta   VARCHAR2 (2000)
         :=    'UPDATE '
            || tabla
            || ' SET SUBTERMINO = '' '' WHERE SUBTERMINO = ''-''';
   BEGIN
      BEGIN
         EXECUTE IMMEDIATE consulta;
      EXCEPTION
         WHEN OTHERS
         THEN
            --Si da error es que no existe el campo SUBTERMINO en tabla.
            RETURN;
      END;
   END PR_ACTUALIZA_SUBTERMINO;

   PROCEDURE PR_CARGA_DENOM_ORIGEN_DEFECTO (P_LINEASEGUROID in number)
   IS
     cnt NUMBER;
   BEGIN
        execute immediate 'select count(*) from o02agpe0.TB_SC_C_COD_DENOM_ORIGEN where CODDENOMORIGEN = 0 AND LINEASEGUROID = ' || p_lineaseguroid || ' ' into cnt;
        IF (cnt <= 0) then
           INSERT INTO o02agpe0.TB_SC_C_COD_DENOM_ORIGEN VALUES (P_LINEASEGUROID, 0, 'SIN VALOR');
        END IF;
   END PR_CARGA_DENOM_ORIGEN_DEFECTO;

   PROCEDURE PR_CARGA_CULTIVOS (P_LINEASEGUROID IN NUMBER)
   IS
      contador      NUMBER (2)  := 0;
   BEGIN
      SELECT COUNT (*) INTO contador FROM o02agpe0.tb_sc_c_cultivos WHERE lineaseguroid = P_LINEASEGUROID AND codcultivo = 999;

      IF (contador = 0) THEN
         INSERT INTO o02agpe0.tb_sc_c_cultivos VALUES (P_LINEASEGUROID, 999, 'TODOS');
      END IF;
   END PR_CARGA_CULTIVOS;

   PROCEDURE PR_CARGA_VARIEDADES (P_LINEASEGUROID IN NUMBER)
   IS
      CURSOR c2 (vlinea NUMBER) IS
         SELECT DISTINCT (codcultivo) FROM o02agpe0.tb_sc_c_cultivos WHERE lineaseguroid = P_LINEASEGUROID;

      codcultivo   NUMBER (3);
      contador     NUMBER (2) := 0;
   BEGIN
      OPEN c2 (P_LINEASEGUROID);
      FETCH c2 INTO codcultivo;

      WHILE c2%FOUND
      LOOP
         INSERT INTO o02agpe0.tb_sc_c_variedades VALUES (P_LINEASEGUROID, codcultivo, 999, 'TODAS');

         FETCH c2 INTO codcultivo;
      END LOOP;

      CLOSE c2;

      SELECT COUNT (*) INTO contador FROM o02agpe0.tb_sc_c_variedades
       WHERE lineaseguroid = P_LINEASEGUROID AND codcultivo = 999 AND codvariedad = 999;

      IF (contador = 0) THEN
         INSERT INTO o02agpe0.tb_sc_c_variedades VALUES (P_LINEASEGUROID, 999, 999, 'TODAS');
      END IF;
   END PR_CARGA_VARIEDADES;

   PROCEDURE PR_CARGA_MODULOS (P_LINEASEGUROID IN NUMBER)
   IS
      existemodulo   NUMBER;
   BEGIN
      --Cargamos el valor 99999 'Todos los modulos' para la linea seguro importada
      SELECT COUNT (*) INTO existemodulo FROM o02agpe0.tb_sc_c_modulos
         WHERE lineaseguroid = P_LINEASEGUROID AND codmodulo = '99999';

      IF (existemodulo = 0) THEN
         INSERT INTO o02agpe0.tb_sc_c_modulos VALUES (P_LINEASEGUROID, '99999', 'TODOS LOS MODULOS', 'P', null, null);
      --COMMIT;
      END IF;
   END PR_CARGA_MODULOS;

   --Procedimiento para cargar la línea genérica y actualizar la descripción
   PROCEDURE PR_CARGA_LINEAS IS
      CURSOR c1  IS SELECT codgruposeguro FROM o02agpe0.tb_sc_c_grupo_seguro;

      CURSOR c2 IS SELECT CODLINEA FROM o02agpe0.TB_SC_C_LINEAS;

      gruposeguro   VARCHAR2 (3);
      contador      NUMBER;
      descLinea     o02agpe0.tb_sc_c_lineas.deslinea%TYPE;
      linea         o02agpe0.tb_sc_c_lineas.Codlinea%TYPE;
   BEGIN
      OPEN c1;
      FETCH c1 INTO gruposeguro;

      WHILE c1%FOUND
      LOOP
         contador := 0;

         SELECT COUNT (*) INTO contador FROM o02agpe0.tb_sc_c_lineas WHERE codlinea = 999 AND codgruposeguro = gruposeguro;
         PQ_UTL.log('PK_IMPORT_CSV ', 'contador: '|| contador || ' ****', 2);
          PQ_UTL.log('PK_IMPORT_CSV ', 'gruposeguro: '|| gruposeguro || ' ****', 2);
         IF (contador = 0 and gruposeguro = 'A01') THEN
            INSERT INTO o02agpe0.tb_sc_c_lineas VALUES (999, 'TODAS LAS LÍNEAS', gruposeguro);
            PQ_UTL.log('PK_IMPORT_CSV ', 'gruposeguro: '|| gruposeguro || ' ****', 2);
            COMMIT;
            PQ_UTL.log('PK_IMPORT_CSV ', 'LINEA 999gruposeguro INSERTADO: '|| gruposeguro || ' ****', 2);
         END IF;

         FETCH c1 INTO gruposeguro;
      END LOOP;
      CLOSE c1;

      OPEN c2;
      FETCH c2 INTO linea;
      WHILE c2%FOUND
      LOOP
          PQ_UTL.log('PK_IMPORT_CSV ', 'linea: '|| linea || ' ****', 2);
          --Actualizamos la descripción del plan/línea en TB_LINEAS
          SELECT DESLINEA INTO descLinea FROM o02agpe0.TB_SC_C_LINEAS WHERE CODLINEA = linea;
          UPDATE o02agpe0.TB_LINEAS SET NOMLINEA = descLinea WHERE CODLINEA = linea;

          FETCH c2 INTO linea;
      END LOOP;
      CLOSE c2;

   END PR_CARGA_LINEAS;

   PROCEDURE PR_CARGA_SIST_PROD_DEFECTO IS
     cnt NUMBER;
   BEGIN
        execute immediate 'select count(*) from o02agpe0.TB_SC_C_SISTEMA_PRODUCCION where CODSISTEMAPRODUCCION = 0' into cnt;
        IF (cnt <= 0) then
           INSERT INTO o02agpe0.TB_SC_C_SISTEMA_PRODUCCION VALUES (0, 'SIN VALOR');
        END IF;
   END PR_CARGA_SIST_PROD_DEFECTO;

   PROCEDURE PR_COPIA_TEMP_SUBVENCIONES(P_TABLA_DESTINO IN VARCHAR2, P_LINEASEGUROID IN NUMBER) IS
        v_consulta_subv      varchar2(2000);
   BEGIN
        -- 1. Vaciar la tabla temporal
        truncate_table(P_TABLA_DESTINO || '_TEMP');
        -- 2. Insertar los registros del plan/linea
        v_consulta_subv := 'INSERT INTO ' || P_TABLA_DESTINO || '_TEMP SELECT * FROM ' || P_TABLA_DESTINO || ' WHERE LINEASEGUROID = ' || P_LINEASEGUROID;
        execute immediate v_consulta_subv;
   END PR_COPIA_TEMP_SUBVENCIONES;

   PROCEDURE PR_ACTUALIZA_IDS_SUBV_ENESA(P_TABLA_DESTINO IN VARCHAR2, P_LINEASEGUROID IN NUMBER) IS

       V_LC                VARCHAR2(50) := 'PQ_IMPORTACION_CSV.PR_ACTUALIZA_IDS_SUBV_ENESA';

       err_num             NUMBER;
       err_msg             VARCHAR2 (2000);
       --Variables auxiliares para la carga de subvenciones
       v_consulta_subv      varchar2(2000);
       v_consulta_idsubv    varchar2(2000);

       TYPE cur_typ IS REF CURSOR;
       c_import_subv   cur_typ;

       SUBV_ID_ANTERIOR  	    NUMBER(15);
       SUBV_ID_ACTUAL    	    NUMBER(15);
       SUBV_LINEASEGUROID      NUMBER(15);
       SUBV_CODMODULO          VARCHAR2(5);
       SUBV_CODTIPORDTO        NUMBER(3);
       SUBV_CODCULTIVO         NUMBER(3);
       SUBV_CODVARIEDAD        NUMBER(3);
       SUBV_CODPROVINCIA       NUMBER(2);
       SUBV_CODCOMARCA         NUMBER(2);
       SUBV_CODTERMINO         NUMBER(3);
       SUBV_SUBTERMINO         VARCHAR2(1);
       SUBV_CODTIPOSUBV        NUMBER(2);
       SUBV_CODGARANTIZADO     NUMBER(3);
       SUBV_RATIO              NUMBER(5,2);
       SUBV_CODBASECALCULOSUBV VARCHAR2(1);
       SUBV_DATOASOCBASE       NUMBER(8,2);
       SUBV_PCTSUBVINDIVIDUAL  NUMBER(5,2);
       SUBV_PCTSUBVCOLECTIVO   NUMBER(5,2);
       SUBV_CODCONCEPTO        NUMBER(5);
       SUBV_VALORCONCEPTO      NUMBER(3);
       SUBV_FECHAENTRADAVIGOR  DATE;
       SUBV_FECHADESDE  DATE;
       SUBV_FECHAHASTA  DATE;
      --Fin de variables auxiliares para las subvenciones
   BEGIN
        PQ_UTL.log(V_LC, 'Entramos en el método que actualiza los ids de las subvenciones enesa de las tablas relacionadas  ', 2);
        -- 1. bucle para recoger el id nuevo y el antiguo y actualizar
        v_consulta_subv := 'SELECT * FROM ' || P_TABLA_DESTINO || '_TEMP';
        OPEN c_import_subv for v_consulta_subv;
        LOOP
            FETCH c_import_subv INTO SUBV_ID_ANTERIOR, SUBV_LINEASEGUROID, SUBV_CODMODULO,
                SUBV_CODTIPORDTO, SUBV_CODCULTIVO, SUBV_CODVARIEDAD, SUBV_CODPROVINCIA, SUBV_CODCOMARCA,
                SUBV_CODTERMINO, SUBV_SUBTERMINO, SUBV_CODTIPOSUBV, SUBV_CODGARANTIZADO, SUBV_RATIO,
                SUBV_CODBASECALCULOSUBV, SUBV_DATOASOCBASE, SUBV_PCTSUBVINDIVIDUAL,
                SUBV_PCTSUBVCOLECTIVO, SUBV_CODCONCEPTO, SUBV_VALORCONCEPTO, SUBV_FECHAENTRADAVIGOR,
                SUBV_FECHADESDE, SUBV_FECHAHASTA ;
            EXIT WHEN c_import_subv%NOTFOUND;
            BEGIN
                v_consulta_idsubv := 'SELECT ID FROM ' || P_TABLA_DESTINO || ' WHERE lineaseguroid = ' ||
                    SUBV_LINEASEGUROID || ' AND codmodulo = ''' || SUBV_CODMODULO || ''' AND CODTIPORDTO = ' ||
                    SUBV_CODTIPORDTO || ' AND CODCULTIVO = ' || SUBV_CODCULTIVO || ' AND CODVARIEDAD = ' ||
                    SUBV_CODVARIEDAD || ' AND CODPROVINCIA = ' || SUBV_CODPROVINCIA || ' AND CODCOMARCA = ' ||
                    SUBV_CODCOMARCA || ' AND CODTERMINO = ' || SUBV_CODTERMINO || ' AND SUBTERMINO = ''' ||
                    SUBV_SUBTERMINO || ''' AND CODTIPOSUBVENESA = ' || SUBV_CODTIPOSUBV ||
                    ' AND PCTSUBVINDIVIDUAL = ' || SUBV_PCTSUBVINDIVIDUAL || ' AND PCTSUBVCOLECTIVO = ' ||
                    SUBV_PCTSUBVCOLECTIVO;

                    --Tratamos aparte los datos que permiten nulos:
                    --Garantizado
                    if (SUBV_CODGARANTIZADO IS NULL) THEN
                        v_consulta_idsubv := v_consulta_idsubv || ' AND CODGARANTIZADO IS NULL';
                    ELSE
                        v_consulta_idsubv := v_consulta_idsubv || ' AND CODGARANTIZADO = ' || SUBV_CODGARANTIZADO;
                    END IF;
                    --Ratio
                    IF (SUBV_RATIO IS NULL) THEN
                        v_consulta_idsubv := v_consulta_idsubv || ' AND RATIO IS NULL';
                    ELSE
                        v_consulta_idsubv := v_consulta_idsubv || ' AND RATIO = ' || SUBV_RATIO;
                    END IF;
                    --DatoAsocBase
                    IF (SUBV_DATOASOCBASE IS NULL) THEN
                        v_consulta_idsubv := v_consulta_idsubv || ' AND DATOASOCBASE IS NULL';
                    ELSE
                        v_consulta_idsubv := v_consulta_idsubv || ' AND DATOASOCBASE = ' || SUBV_DATOASOCBASE;
                    END IF;
                    --Cod concepto
                    IF (SUBV_CODCONCEPTO IS NULL) THEN
                        v_consulta_idsubv := v_consulta_idsubv || ' AND CODCONCEPTO IS NULL';
                    ELSE
                        v_consulta_idsubv := v_consulta_idsubv || ' AND CODCONCEPTO = ' || SUBV_CODCONCEPTO;
                    END IF;
                    --Valor concepto
                    IF (SUBV_VALORCONCEPTO IS NULL) THEN
                        v_consulta_idsubv := v_consulta_idsubv || ' AND VALORCONCEPTO IS NULL';
                    ELSE
                        v_consulta_idsubv := v_consulta_idsubv || ' AND VALORCONCEPTO = ' || SUBV_VALORCONCEPTO;
                    END IF;
                   --Fecha entrada vigor
                    IF (SUBV_FECHAENTRADAVIGOR IS NULL) THEN
                        v_consulta_idsubv := v_consulta_idsubv || ' AND FECHAENTRADAVIGORHASTA IS NULL';
                    ELSE
                        --v_consulta_idsubv := v_consulta_idsubv || ' AND FECHAENTRADAVIGORHASTA = ' || SUBV_FECHAENTRADAVIGOR;
                        v_consulta_idsubv := v_consulta_idsubv || ' AND FECHAENTRADAVIGORHASTA = TO_DATE(''' || SUBV_FECHAENTRADAVIGOR || ''',''DD/MM/YYYY'')';
                    END IF;
                    --Base calculo subvencion
                    IF (SUBV_CODBASECALCULOSUBV IS NULL) THEN
                        v_consulta_idsubv := v_consulta_idsubv || ' AND CODBASECALCULOSUBV IS NULL';
                    ELSE
                        v_consulta_idsubv := v_consulta_idsubv || ' AND CODBASECALCULOSUBV = ''' || SUBV_CODBASECALCULOSUBV || '''';
                    END IF;

                    --PARA QUEDARNOS SÓLO CON EL PRIMER REGISTRO
                    v_consulta_idsubv := v_consulta_idsubv || ' AND ROWNUM = 1';

                    PQ_UTL.log(V_LC, 'FN_XML2TABLE - SQL Actualizar las subvenciones: ' || v_consulta_idsubv || ' ****', 2);

                    execute immediate v_consulta_idsubv into SUBV_ID_ACTUAL;
                    PQ_UTL.log(V_LC, 'ID_ACTUAL de la subvencion ' || SUBV_ID_ANTERIOR || ' = ' || SUBV_ID_ACTUAL || '   ', 2);
                    if (SUBV_ID_ACTUAL is not null) then
                        --PQ_UTL.log('Actualizando los ids de las subvenciones enesa de socios  ', 2);
                        UPDATE o02agpe0.TB_SUBV_ENESA_SOCIOS SET IDSUBVENESA = SUBV_ID_ACTUAL WHERE LINEASEGUROID = P_LINEASEGUROID AND IDSUBVENESA = SUBV_ID_ANTERIOR;
                        --PQ_UTL.log('Actualizando los ids de las subvenciones enesa de parcelas  ', 2);
                        UPDATE o02agpe0.TB_SUBV_PARCELA_ENESA SET IDSUBVENESA = SUBV_ID_ACTUAL WHERE LINEASEGUROID = P_LINEASEGUROID AND IDSUBVENESA = SUBV_ID_ANTERIOR;
                        --PQ_UTL.log('Actualizando los ids de las subvenciones enesa de asegurados  ', 2);
                        UPDATE o02agpe0.TB_SUBVS_ASEG_ENESA SET IDSUBVENCION = SUBV_ID_ACTUAL WHERE LINEASEGUROID = P_LINEASEGUROID AND IDSUBVENCION = SUBV_ID_ANTERIOR;
                        --PQ_UTL.log('Ids actualizados con exito  ', 2);
                    end if;

            EXCEPTION
                WHEN OTHERS THEN
                    err_num := SQLCODE;
                    err_msg := SQLERRM;
                    PQ_UTL.log(V_LC, 'FN_XML2TABLE - ERROR Actualizando las subvenciones: ' || err_num || ' - ' || err_msg || ' ****', 2);
            END;
        END LOOP;

        PQ_UTL.log(V_LC, 'Fin del método que actualiza los ids de las subvenciones enesa de las tablas relacionadas  ', 2);
   END PR_ACTUALIZA_IDS_SUBV_ENESA;

   PROCEDURE PR_ACTUALIZA_IDS_SUBV_CCAA(P_TABLA_DESTINO IN VARCHAR2, P_LINEASEGUROID IN NUMBER) IS

       V_LC                VARCHAR2(50) := 'PQ_IMPORTACION_CSV.PR_ACTUALIZA_IDS_SUBV_CCAA';

       err_num             NUMBER;
       err_msg             VARCHAR2 (2000);
       --Variables auxiliares para la carga de subvenciones
       v_consulta_subv      varchar2(2000);
       v_consulta_idsubv    varchar2(2000);

       TYPE cur_typ IS REF CURSOR;
       c_import_subv   cur_typ;
       SUBV_ID_ANTERIOR  	     NUMBER(15);
       SUBV_ID_ACTUAL    	     NUMBER(15);
       SUBV_LINEASEGUROID      NUMBER(15);
       SUBV_CODORGANISMO       VARCHAR2(1);
       SUBV_CODMODULO          VARCHAR2(5);
       SUBV_CODTIPORDTO        NUMBER(3);
       SUBV_CODCULTIVO         NUMBER(3);
       SUBV_CODVARIEDAD        NUMBER(3);
       SUBV_CODPROVINCIA       NUMBER(2);
       SUBV_CODCOMARCA         NUMBER(2);
       SUBV_CODTERMINO         NUMBER(3);
       SUBV_SUBTERMINO         VARCHAR2(1);
       SUBV_CODTIPOSUBV        NUMBER(2);
       SUBV_CODGARANTIZADO     NUMBER(3);
       SUBV_TASACOSTE          NUMBER(5,2);
       SUBV_CODBASECALCULOSUBV VARCHAR2(1);
       SUBV_DATOASOCBASE       NUMBER(8,2);
       SUBV_PCTSUBVINDIVIDUAL  NUMBER(5,2);
       SUBV_PCTSUBVCOLECTIVO   NUMBER(5,2);
       SUBV_CODCONCEPTO        NUMBER(5);
       SUBV_VALORCONCEPTO      NUMBER(3);
       SUBV_FECHAENTRADAVIGOR  DATE;
       SUBV_FECHADESDE         DATE;
       SUBV_FECHAHASTA         DATE;       
      --Fin de variables auxiliares para las subvenciones
   BEGIN
        PQ_UTL.log(V_LC, 'Entramos en el método que actualiza los ids de las subvenciones CCAA de las tablas relacionadas  ', 2);
        -- 1. bucle para recoger el id nuevo y el antiguo y actualizar
        v_consulta_subv := 'SELECT * FROM ' || P_TABLA_DESTINO || '_TEMP';
        OPEN c_import_subv for v_consulta_subv;
        LOOP
            FETCH c_import_subv INTO SUBV_ID_ANTERIOR, SUBV_LINEASEGUROID, SUBV_CODORGANISMO, SUBV_CODMODULO,
                SUBV_CODTIPORDTO, SUBV_CODCULTIVO, SUBV_CODVARIEDAD, SUBV_CODPROVINCIA, SUBV_CODCOMARCA,
                SUBV_CODTERMINO, SUBV_SUBTERMINO, SUBV_CODTIPOSUBV, SUBV_CODGARANTIZADO, SUBV_TASACOSTE,
                SUBV_CODBASECALCULOSUBV, SUBV_DATOASOCBASE, SUBV_PCTSUBVINDIVIDUAL,
                SUBV_PCTSUBVCOLECTIVO, SUBV_CODCONCEPTO, SUBV_VALORCONCEPTO, SUBV_FECHAENTRADAVIGOR,
                SUBV_FECHADESDE, SUBV_FECHAHASTA;
            EXIT WHEN c_import_subv%NOTFOUND;
            BEGIN
                v_consulta_idsubv := 'SELECT ID FROM ' || P_TABLA_DESTINO || ' WHERE lineaseguroid = ' ||
                    SUBV_LINEASEGUROID || ' AND codmodulo = ''' || SUBV_CODMODULO || ''' AND CODTIPORDTO = ' ||
                    SUBV_CODTIPORDTO || ' AND CODCULTIVO = ' || SUBV_CODCULTIVO || ' AND CODVARIEDAD = ' ||
                    SUBV_CODVARIEDAD || ' AND CODPROVINCIA = ' || SUBV_CODPROVINCIA || ' AND CODCOMARCA = ' ||
                    SUBV_CODCOMARCA || ' AND CODTERMINO = ' || SUBV_CODTERMINO || ' AND SUBTERMINO = ''' ||
                    SUBV_SUBTERMINO || ''' AND CODTIPOSUBVCCAA = ' || SUBV_CODTIPOSUBV ||
                    ' AND PCTSUBVINDIVIDUAL = ' || SUBV_PCTSUBVINDIVIDUAL || ' AND PCTSUBVCOLECTIVO = ' ||
                    SUBV_PCTSUBVCOLECTIVO;

                    --Tratamos aparte los datos que permiten nulos:
                    --Garantizado
                    if (SUBV_CODGARANTIZADO IS NULL) THEN
                        v_consulta_idsubv := v_consulta_idsubv || ' AND CODGARANTIZADO IS NULL';
                    ELSE
                        v_consulta_idsubv := v_consulta_idsubv || ' AND CODGARANTIZADO = ' || SUBV_CODGARANTIZADO;
                    END IF;
                    --DatoAsocBase
                    IF (SUBV_DATOASOCBASE IS NULL) THEN
                        v_consulta_idsubv := v_consulta_idsubv || ' AND DATOASOCBASE IS NULL';
                    ELSE
                        v_consulta_idsubv := v_consulta_idsubv || ' AND DATOASOCBASE = ' || SUBV_DATOASOCBASE;
                    END IF;
                    --Cod concepto
                    IF (SUBV_CODCONCEPTO IS NULL) THEN
                        v_consulta_idsubv := v_consulta_idsubv || ' AND CODCONCEPTO IS NULL';
                    ELSE
                        v_consulta_idsubv := v_consulta_idsubv || ' AND CODCONCEPTO = ' || SUBV_CODCONCEPTO;
                    END IF;
                    --Valor concepto
                    IF (SUBV_VALORCONCEPTO IS NULL) THEN
                        v_consulta_idsubv := v_consulta_idsubv || ' AND VALORCONCEPTO IS NULL';
                    ELSE
                        v_consulta_idsubv := v_consulta_idsubv || ' AND VALORCONCEPTO = ' || SUBV_VALORCONCEPTO;
                    END IF;
                    --Fecha entrada vigor
                    IF (SUBV_FECHAENTRADAVIGOR IS NULL) THEN
                        v_consulta_idsubv := v_consulta_idsubv || ' AND FECHAENTRADAVIGORHASTA IS NULL';
                    ELSE
                        v_consulta_idsubv := v_consulta_idsubv || ' AND FECHAENTRADAVIGORHASTA = TO_DATE(''' || SUBV_FECHAENTRADAVIGOR || ''',''DD/MM/YYYY'')';
                    END IF;
                    --Base calculo subvencion
                    IF (SUBV_CODBASECALCULOSUBV IS NULL) THEN
                        v_consulta_idsubv := v_consulta_idsubv || ' AND CODBASECALCULOSUBV IS NULL';
                    ELSE
                        v_consulta_idsubv := v_consulta_idsubv || ' AND CODBASECALCULOSUBV = ''' || SUBV_CODBASECALCULOSUBV || '''';
                    END IF;
                    --Codigo de organismo
                    IF (SUBV_CODORGANISMO IS NULL) THEN
                        v_consulta_idsubv := v_consulta_idsubv || ' AND CODORGANISMO IS NULL';
                    ELSE
                        v_consulta_idsubv := v_consulta_idsubv || ' AND CODORGANISMO = ''' || SUBV_CODORGANISMO || '''';
                    END IF;
                    --Tasa coste
                    if (SUBV_TASACOSTE IS NULL) THEN
                        v_consulta_idsubv := v_consulta_idsubv || ' AND TASACOSTE IS NULL';
                    ELSE
                        v_consulta_idsubv := v_consulta_idsubv || ' AND TASACOSTE = ' || SUBV_TASACOSTE;
                    END IF;

                    --PARA QUEDARNOS SÓLO CON EL PRIMER REGISTRO
                    v_consulta_idsubv := v_consulta_idsubv || ' AND ROWNUM = 1';

                    execute immediate v_consulta_idsubv into SUBV_ID_ACTUAL;
                    PQ_UTL.log(V_LC, 'ID_ACTUAL de la subvencion ' || SUBV_ID_ANTERIOR || ' = ' || SUBV_ID_ACTUAL || '   ', 2);
                    if (SUBV_ID_ACTUAL is not null) then
                        PQ_UTL.log(V_LC, 'Actualizando los ids de las subvenciones CCAA de las tablas relacionadas  ', 2);
                        UPDATE o02agpe0.TB_SUBV_PARCELA_CCAA SET IDSUBVCCAA = SUBV_ID_ACTUAL WHERE LINEASEGUROID = P_LINEASEGUROID AND IDSUBVCCAA = SUBV_ID_ANTERIOR;
                        UPDATE o02agpe0.TB_SUBVS_ASEG_CCAA SET IDSUBVENCION = SUBV_ID_ACTUAL WHERE LINEASEGUROID = P_LINEASEGUROID AND IDSUBVENCION = SUBV_ID_ANTERIOR;
                    end if;

            EXCEPTION
                WHEN OTHERS THEN
                    err_num := SQLCODE;
                    err_msg := SQLERRM;
                    PQ_UTL.log(V_LC, 'FN_XML2TABLE - ERROR Actualizando las subvenciones: ' || err_num || ' - ' || err_msg || ' ****', 2);
            END;
        END LOOP;

        PQ_UTL.log(V_LC, 'Fin del método que actualiza los ids de las subvenciones CCAA de las tablas relacionadas  ', 2);
   END PR_ACTUALIZA_IDS_SUBV_CCAA;

   PROCEDURE PR_CARGA_TABLA_COBERTURAS IS
   BEGIN
       truncate_table ('o02agpe0.TB_CUADRO_COBERTURAS');

       -- INSERCIÓN DE REGISTROS DE LÍNEAS AGRARIAS
       insert into o02agpe0.tb_cuadro_coberturas (
         select o02agpe0.SQ_CUADRO_COBERTURAS.nextval, t.* from (
           -- % FRANQUICIA
           select lineaseguroid, codmodulo, filamodulo, columnamodulo, filamodulovinc, columnamodulovinc,
               to_char(pf.codpctfranquiciaeleg) as codigo, pf.despctfranquiciaeleg as descripcion,
               nvl(PCTCAPITALASEGVINC, nvl(CALCINDEMNVINC, nvl(PCTMININDEMNVINC, nvl(TIPOFRANQUICIAVINC,
               nvl(PCTFRANQUICIAVINC, nvl(CODTIPORDTOVINC, nvl(GARANTIZADOVINC, 0))))))) as codigo_vinc,
               120 as codconcepto, v.fichvinculacionexterna
           from o02agpe0.tb_sc_c_vinc_valores_mod v inner join o02agpe0.tb_sc_c_pct_franquicia_eleg pf on (v.codpctfranquiciaeleg = pf.codpctfranquiciaeleg)
           UNION
           -- % MINIMO INDEMNIZABLE
           select lineaseguroid, codmodulo, filamodulo, columnamodulo, filamodulovinc, columnamodulovinc,
               to_char(mi.pctminindem) as codigo, mi.desminindem as descripcion,
               nvl(PCTCAPITALASEGVINC, nvl(CALCINDEMNVINC, nvl(PCTMININDEMNVINC, nvl(TIPOFRANQUICIAVINC,
               nvl(PCTFRANQUICIAVINC, nvl(CODTIPORDTOVINC, nvl(GARANTIZADOVINC, 0))))))) as codigo_vinc,
               121 as codconcepto, v.fichvinculacionexterna
           from o02agpe0.tb_sc_c_vinc_valores_mod v inner join o02agpe0.tb_sc_c_min_indem_eleg mi on (v.pctminindemneleg = mi.pctminindem)
           UNION
           -- TIPO FRANQUICIA
           select lineaseguroid, codmodulo, filamodulo, columnamodulo, filamodulovinc, columnamodulovinc,
               f.codtipofranquicia as codigo, f.destipofranquicia as descripcion,
               nvl(PCTCAPITALASEGVINC, nvl(CALCINDEMNVINC, nvl(PCTMININDEMNVINC, nvl(TIPOFRANQUICIAVINC,
               nvl(PCTFRANQUICIAVINC, nvl(CODTIPORDTOVINC, nvl(GARANTIZADOVINC, 0))))))) as codigo_vinc,
               170 as codconcepto, v.fichvinculacionexterna
           from o02agpe0.tb_sc_c_vinc_valores_mod v inner join o02agpe0.tb_sc_c_tipo_franquicia f on (v.tipofranquiciaeleg = f.codtipofranquicia)
           UNION
           -- CALCULO INDEMNIZACION
           select lineaseguroid, codmodulo, filamodulo, columnamodulo, filamodulovinc, columnamodulovinc,
               to_char(ci.codcalculo) as codigo, ci.descalculo as descripcion,
               nvl(PCTCAPITALASEGVINC, nvl(CALCINDEMNVINC, nvl(PCTMININDEMNVINC, nvl(TIPOFRANQUICIAVINC,
               nvl(PCTFRANQUICIAVINC, nvl(CODTIPORDTOVINC, nvl(GARANTIZADOVINC, 0))))))) as codigo_vinc,
               174 as codconcepto, v.fichvinculacionexterna
           from o02agpe0.tb_sc_c_vinc_valores_mod v inner join o02agpe0.tb_sc_c_calc_indemnizacion ci on (v.calcindemneleg = ci.codcalculo)
           UNION
           -- GARANTIZADO
           select lineaseguroid, codmodulo, filamodulo, columnamodulo, filamodulovinc, columnamodulovinc,
               to_char(c.codgarantizado) as codigo, c.desgarantizado as descripcion,
               nvl(PCTCAPITALASEGVINC, nvl(CALCINDEMNVINC, nvl(PCTMININDEMNVINC, nvl(TIPOFRANQUICIAVINC,
               nvl(PCTFRANQUICIAVINC, nvl(CODTIPORDTOVINC, nvl(GARANTIZADOVINC, 0))))))) as codigo_vinc,
               175 as codconcepto, v.fichvinculacionexterna
           from o02agpe0.tb_sc_c_vinc_valores_mod v inner join o02agpe0.tb_sc_c_garantizado c on (v.garantizadoeleg = c.codgarantizado)
           UNION
           -- % CAPITAL ASEGURADO
           select lineaseguroid, codmodulo, filamodulo, columnamodulo, filamodulovinc, columnamodulovinc,
               to_char(c.pctcapitalaseg) as codigo, c.descapitalaseg as descripcion,
               nvl(PCTCAPITALASEGVINC, nvl(CALCINDEMNVINC, nvl(PCTMININDEMNVINC, nvl(TIPOFRANQUICIAVINC,
               nvl(PCTFRANQUICIAVINC, nvl(CODTIPORDTOVINC, nvl(GARANTIZADOVINC, 0))))))) as codigo_vinc,
               362 as codconcepto, v.fichvinculacionexterna
           from o02agpe0.tb_sc_c_vinc_valores_mod v inner join o02agpe0.tb_sc_c_capital_aseg_eleg c on (v.pctcapitalasegeleg = c.pctcapitalaseg)
         ) t
       );
       commit;

       -- INSERCIÓN DE REGISTROS DE LÍNEAS GANADERAS
       insert into o02agpe0.tb_cuadro_coberturas (
       select o02agpe0.SQ_CUADRO_COBERTURAS.nextval, t.* from (
           -- % FRANQUICIA
           select lineaseguroid, codmodulo, filamodulo, columnamodulo, filamodulovinc, columnamodulovinc,
               to_char(pf.codpctfranquiciaeleg) as codigo, pf.despctfranquiciaeleg as descripcion,
               nvl(PCTCAPITALASEGVINC, nvl(CALCINDEMNVINC, nvl(PCTMININDEMNVINC, nvl(TIPOFRANQUICIAVINC,
               nvl(CODPCTFRANQUICIAVINC, nvl(GARANTIZADOVINC, 0)))))) as codigo_vinc,
               120 as codconcepto, v.fichvinculacionexterna
           from o02agpe0.tb_sc_c_vinc_valores_mod_g v inner join o02agpe0.tb_sc_c_pct_franquicia_eleg pf on (v.codpctfranquiciaeleg = pf.codpctfranquiciaeleg)
           UNION
           -- % MINIMO INDEMNIZABLE
           select lineaseguroid, codmodulo, filamodulo, columnamodulo, filamodulovinc, columnamodulovinc,
               to_char(mi.pctminindem) as codigo, mi.desminindem as descripcion,
               nvl(PCTCAPITALASEGVINC, nvl(CALCINDEMNVINC, nvl(PCTMININDEMNVINC, nvl(TIPOFRANQUICIAVINC,
               nvl(CODPCTFRANQUICIAVINC, nvl(GARANTIZADOVINC, 0)))))) as codigo_vinc,
               121 as codconcepto, v.fichvinculacionexterna
           from o02agpe0.tb_sc_c_vinc_valores_mod_g v inner join o02agpe0.tb_sc_c_min_indem_eleg mi on (v.pctminindemneleg = mi.pctminindem)
           UNION
           -- TIPO FRANQUICIA
            select lineaseguroid, codmodulo, filamodulo, columnamodulo, filamodulovinc, columnamodulovinc,
                f.codtipofranquicia as codigo, f.destipofranquicia as descripcion,
                nvl(PCTCAPITALASEGVINC, nvl(CALCINDEMNVINC, nvl(PCTMININDEMNVINC, nvl(TIPOFRANQUICIAVINC,
                nvl(CODPCTFRANQUICIAVINC, nvl(GARANTIZADOVINC, 0)))))) as codigo_vinc,
                170 as codconcepto, v.fichvinculacionexterna
            from o02agpe0.tb_sc_c_vinc_valores_mod_g v inner join o02agpe0.tb_sc_c_tipo_franquicia f on (v.tipofranquiciaeleg = f.codtipofranquicia)
            UNION
           -- CALCULO INDEMNIZACION
           select lineaseguroid, codmodulo, filamodulo, columnamodulo, filamodulovinc, columnamodulovinc,
               to_char(ci.codcalculo) as codigo, ci.descalculo as descripcion,
               nvl(PCTCAPITALASEGVINC, nvl(CALCINDEMNVINC, nvl(PCTMININDEMNVINC, nvl(TIPOFRANQUICIAVINC,
               nvl(CODPCTFRANQUICIAVINC, nvl(GARANTIZADOVINC, 0)))))) as codigo_vinc,
               174 as codconcepto, v.fichvinculacionexterna
           from o02agpe0.tb_sc_c_vinc_valores_mod_g v inner join o02agpe0.tb_sc_c_calc_indemnizacion ci on (v.calcindemneleg = ci.codcalculo)
           UNION
           -- GARANTIZADO
            select lineaseguroid, codmodulo, filamodulo, columnamodulo, filamodulovinc, columnamodulovinc,
                to_char(c.codgarantizado) as codigo, c.desgarantizado as descripcion,
                nvl(PCTCAPITALASEGVINC, nvl(CALCINDEMNVINC, nvl(PCTMININDEMNVINC, nvl(TIPOFRANQUICIAVINC,
                nvl(CODPCTFRANQUICIAVINC, nvl(GARANTIZADOVINC, 0)))))) as codigo_vinc,
                175 as codconcepto, v.fichvinculacionexterna
            from o02agpe0.tb_sc_c_vinc_valores_mod_g v inner join o02agpe0.tb_sc_c_garantizado c on (v.garantizadoeleg = c.codgarantizado)
            UNION
           -- % CAPITAL ASEGURADO
           select lineaseguroid, codmodulo, filamodulo, columnamodulo, filamodulovinc, columnamodulovinc,
               to_char(c.pctcapitalaseg) as codigo, c.descapitalaseg as descripcion,
               nvl(PCTCAPITALASEGVINC, nvl(CALCINDEMNVINC, nvl(PCTMININDEMNVINC, nvl(TIPOFRANQUICIAVINC,
               nvl(CODPCTFRANQUICIAVINC, nvl(GARANTIZADOVINC, 0)))))) as codigo_vinc,
               362 as codconcepto, v.fichvinculacionexterna
           from o02agpe0.tb_sc_c_vinc_valores_mod_g v inner join o02agpe0.tb_sc_c_capital_aseg_eleg c on (v.pctcapitalasegeleg = c.pctcapitalaseg)
           ) t);
       commit;

   END PR_CARGA_TABLA_COBERTURAS;

   PROCEDURE PR_EXECUTE_PRE_SQL (ID_TABLA IN VARCHAR2, P_PLAN IN NUMBER, P_LINEA IN NUMBER,
                                P_LINEASEGUROID IN NUMBER, P_TABLA_DESTINO IN VARCHAR2) IS
      V_LC    VARCHAR2(50) := 'PQ_IMPORTACION_CSV.PR_EXECUTE_PRE_SQL';

      consultas VARCHAR2(2000);
      arr_sentencias t_array;
      v_sql varchar2(2000);
   BEGIN
      -- Leer las sentencias previas, hacer split y ejecutar de una en una
      SELECT T.PRE_SQL INTO consultas FROM o02agpe0.TB_TABLAS_XMLS T WHERE T.ID = ID_TABLA;
      IF (consultas is not null) THEN
         arr_sentencias := fn_split(consultas, '#');

         FOR cnt IN arr_sentencias.FIRST .. arr_sentencias.LAST
         LOOP
             v_sql := arr_sentencias(cnt);
             v_sql := replace (v_sql, 'P_LINEASEGUROID', P_LINEASEGUROID);
             v_sql := replace (v_sql, 'P_PLAN', P_PLAN);
             v_sql := replace (v_sql, 'P_LINEA', P_LINEA);
             v_sql := replace (v_sql, 'P_TABLA_DESTINO', '''o02agpe0.' || P_TABLA_DESTINO || '''');
             PQ_UTL.log(V_LC, 'PR_EXECUTE_PRE_SQL: ' || V_SQL || '  ', 2);
             execute immediate v_sql;
             --commit;
         END LOOP;
      END IF;
   END PR_EXECUTE_PRE_SQL;

   PROCEDURE PR_EXECUTE_POST_SQL (ID_TABLA IN VARCHAR2, P_PLAN IN NUMBER, P_LINEA IN NUMBER,
                                P_LINEASEGUROID IN NUMBER, P_TABLA_DESTINO IN VARCHAR2) IS

      V_LC    VARCHAR2(50) := 'PQ_IMPORTACION_CSV.PR_EXECUTE_POST_SQL';

      consultas VARCHAR2(2000);
      arr_sentencias t_array;
      v_sql varchar2(2000);
   BEGIN
      -- Leer las sentencias posteriores, hacer split y ejecutar de una en una
      SELECT T.Post_Sql INTO consultas FROM o02agpe0.TB_TABLAS_XMLS T WHERE T.ID = ID_TABLA;
      IF (consultas is not null) THEN
         arr_sentencias := fn_split(consultas, '#');

         FOR cnt IN arr_sentencias.FIRST .. arr_sentencias.LAST
         LOOP
             PQ_UTL.log(V_LC, 'PR_EXECUTE_POST_SQL: ' || V_SQL || '  ', 2);
             PQ_UTL.log(V_LC, 'P_LINEASEGUROID: ' || P_LINEASEGUROID || '  ', 2);
             PQ_UTL.log(V_LC, 'P_PLAN: ' || P_PLAN || '  ', 2);
             PQ_UTL.log(V_LC, 'P_LINEA: ' || P_LINEA || '  ', 2);
             PQ_UTL.log(V_LC, 'P_TABLA_DESTINO: ' || P_TABLA_DESTINO || '  ', 2);
             v_sql := arr_sentencias(cnt);
             v_sql := replace (v_sql, 'P_LINEASEGUROID', P_LINEASEGUROID);
             v_sql := replace (v_sql, 'P_PLAN', P_PLAN);
             v_sql := replace (v_sql, 'P_LINEA', P_LINEA);
             v_sql := replace (v_sql, 'P_TABLA_DESTINO', '''o02agpe0.' || P_TABLA_DESTINO || '''');
             PQ_UTL.log(V_LC, 'PR_EXECUTE_POST_SQL: ' || V_SQL || '  ', 2);
             execute immediate v_sql;
             --commit;
         END LOOP;
      END IF;
   END PR_EXECUTE_POST_SQL;


   FUNCTION FN_SPLIT (cadena IN VARCHAR2, separador IN CHAR) RETURN t_array
   IS
      i         NUMBER          := 0;
      pos       NUMBER          := 0;
      lv_str    VARCHAR2 (2000) := cadena;
      strings   t_array;
   BEGIN
      -- determine first chuck of string
      pos := INSTR (lv_str, separador, 1, 1);

      -- while there are chunks left, loop
      IF (pos = 0)
      THEN
         strings (1) := cadena;
      ELSE
         WHILE (pos != 0)
         LOOP
            -- increment counter
            i := i + 1;
            -- create array element for chuck of string
            strings (i) := SUBSTR (lv_str, 1, pos - 1);
            -- remove chunk from string
            lv_str := SUBSTR (lv_str, pos + 1, LENGTH (lv_str));
            -- determine next chunk
            pos := INSTR (lv_str, separador, 1, 1);

            -- no last chunk, add to array
            IF pos = 0
            THEN
               strings (i + 1) := lv_str;
               --DBMS_OUTPUT.put_line ('Linea ' || (i + 1) || ', valor: ' || strings (i + 1));
            END IF;
         END LOOP;
      END IF;

      -- return array
      RETURN strings;
   END FN_SPLIT;

   --Procedimiento para habilitar/deshabilitar las constraints
  PROCEDURE PR_DESHABILITA_FKS(TABLAS IN VARCHAR2) IS

     V_LC    VARCHAR2(50) := 'PQ_IMPORTACION_CSV.PR_DESHABILITA_FKS';

     consulta_tablas VARCHAR2(2000) := 'SELECT NOMBRE FROM o02agpe0.tb_tablas_xmls t WHERE t.NUMTABLA IN ('
            || tablas
            || ')';

     consulta_constraints_tabla    VARCHAR2 (2000);
     consulta_pk_constraints       VARCHAR2 (2000);
     consulta_constraints_ref      VARCHAR2 (2000);

      TYPE cur_typ IS REF CURSOR;
      c_tablas_origen             cur_typ;
      c_constraints_tablas        cur_typ;
      c_pk_constraints            cur_typ;
      c_constraints_referenciadas cur_typ;

      CONSTRAINT_NAME      ALL_CONSTRAINTS.CONSTRAINT_NAME%TYPE;
      TABLE_NAME           o02agpe0.TB_TABLAS_XMLS.NOMBRE%TYPE;
      TABLE_NAME_PK        ALL_CONSTRAINTS.TABLE_NAME%TYPE;
      PK_CONSTRAINT_NAME   ALL_CONSTRAINTS.R_CONSTRAINT_NAME%TYPE;

      sql_alter varchar2(2000);

  BEGIN
     --obtengo las tablas de las que tenemos que habilitar/deshabilitar las constraints
     PQ_UTL.log(V_LC, 'PR_DESHABILITA_FKS - PRIMERA CONSULTA: '|| consulta_tablas || ' ****', 2);
     OPEN c_tablas_origen FOR consulta_tablas;
     LOOP
         FETCH c_tablas_origen INTO TABLE_NAME;
         EXIT WHEN c_tablas_origen%NOTFOUND;
         BEGIN
            consulta_constraints_tabla := 'select distinct u.constraint_name from all_constraints u ' ||
                        'where u.owner = ''O02AGPE0'' and u.constraint_type = ''R'' and u.table_name = ''' || TABLE_NAME || '''';
            PQ_UTL.log(V_LC, 'PR_DESHABILITA_FKS - CONSULTA_CONSTRAINTS_TABLA: '|| consulta_constraints_tabla || ' ****', 2);
            -- Tratamos primero las constraints de la propia tabla.
            OPEN c_constraints_tablas FOR consulta_constraints_tabla;
            LOOP
               FETCH c_constraints_tablas INTO CONSTRAINT_NAME;
               EXIT WHEN c_constraints_tablas%NOTFOUND;
               BEGIN
                  sql_alter := 'ALTER TABLE o02agpe0.' || TABLE_NAME || ' DISABLE CONSTRAINT ' || CONSTRAINT_NAME;
                  PQ_UTL.log(V_LC, 'PR_DESHABILITA_FKS - alter: '|| sql_alter || ' ****', 2);
                  execute immediate sql_alter;
                  -- Tratamos las constraints de las tablas referenciadas
                  consulta_pk_constraints := 'select distinct u.constraint_name from all_constraints u where u.table_name = ''' ||
                                       TABLE_NAME || ''' and u.constraint_type = ''P''';
                  PQ_UTL.log(V_LC, 'PR_DESHABILITA_FKS - BUSQUEDA DE TABLAS RELACIONADAS: '|| consulta_pk_constraints || ' ****', 2);
                  OPEN c_pk_constraints FOR consulta_pk_constraints;
                  LOOP
                     FETCH c_pk_constraints INTO PK_CONSTRAINT_NAME;
                     EXIT WHEN c_pk_constraints%NOTFOUND;
                     -- Obtengo las FK de la tabla que estén habilitadas (según el caso)
                     consulta_constraints_ref := 'select distinct u.constraint_name, u.table_name from all_constraints u ' ||
                               'where u.constraint_type = ''R'' and u.r_constraint_name = ''' || PK_CONSTRAINT_NAME ||
                               ''' and u.status = ''ENABLED''';
                     PQ_UTL.log(V_LC, 'PR_DESHABILITA_FKS - BUSQUEDA DE TABLAS RELACIONADAS: '|| consulta_constraints_ref || ' ****', 2);
                     OPEN c_constraints_referenciadas FOR consulta_constraints_ref;
                     LOOP
                        FETCH c_constraints_referenciadas INTO CONSTRAINT_NAME, TABLE_NAME_PK;
                        EXIT WHEN c_constraints_referenciadas%NOTFOUND;
                        -- Tratamos cada una de las constraints
                        sql_alter := 'ALTER TABLE o02agpe0.' || TABLE_NAME_PK || ' DISABLE CONSTRAINT ' || CONSTRAINT_NAME;
                        PQ_UTL.log(V_LC, 'PR_DESHABILITA_FKS - alter: '|| sql_alter || ' ****', 2);
                        execute immediate sql_alter;
                     END LOOP;
                     CLOSE c_constraints_referenciadas;

                  END LOOP;
                  CLOSE c_pk_constraints;

               EXCEPTION
               WHEN OTHERS THEN
                  PQ_UTL.log(V_LC, 'PR_HABILITA_FKS - ERROR AL DESHABILITAR CONSTRAINTS DE LA TABLA ' || TABLE_NAME || ' - ' || sqlcode || ' - ' || sqlerrm || ' ****', 2);
               END;
            END LOOP;
            close c_constraints_tablas;
         EXCEPTION
            WHEN OTHERS THEN
               PQ_UTL.log(V_LC, 'PR_HABILITA_FKS - ERROR AL DESHABILITAR CONSTRAINTS DE LA TABLA ' || TABLE_NAME || ' - ' || sqlcode || ' - ' || sqlerrm || ' ****', 2);
         END;
     END LOOP;
     close c_tablas_origen;

  END PR_DESHABILITA_FKS;

   --Procedimiento para habilitar/deshabilitar las constraints
  PROCEDURE PR_HABILITA_FKS IS
     V_LC    VARCHAR2(50) := 'PQ_IMPORTACION_CSV.PR_HABILITA_FKS';
     consulta VARCHAR2 (2000) := 'select u.constraint_name, u.table_name from all_constraints u ' ||
                                 'where u.owner = ''O02AGPE0'' and u.constraint_type = ''R'' and u.status = ''DISABLED''';

      TYPE cur_typ IS REF CURSOR;
      c_tablas cur_typ;

      CONSTRAINT_NAME ALL_CONSTRAINTS.CONSTRAINT_NAME%TYPE;
      TABLE_NAME      ALL_CONSTRAINTS.TABLE_NAME%TYPE;

      sql_alter varchar2(2000);

  BEGIN
      OPEN c_tablas FOR consulta;
      LOOP
         FETCH c_tablas INTO CONSTRAINT_NAME, TABLE_NAME;
         EXIT WHEN c_tablas%NOTFOUND;
         BEGIN
            sql_alter := 'ALTER TABLE o02agpe0.' || TABLE_NAME || ' ENABLE CONSTRAINT ' || CONSTRAINT_NAME;
            execute immediate sql_alter;
         EXCEPTION
            WHEN OTHERS THEN
               PQ_UTL.log(V_LC, 'PR_HABILITA_FKS - ERROR AL HABILITAR LA CONSTRAINT ' || CONSTRAINT_NAME || '  DE LA TABLA ' || TABLE_NAME || ' - ' || sqlcode || ' - ' || sqlerrm || ' ****', 2);
         END;
      END LOOP;
      close c_tablas;
  END PR_HABILITA_FKS;

  PROCEDURE PR_CREAPANTALLACONFIGURABLE (LINEASEGUROPARAM IN NUMBER)
   IS
     idPantallaVar NUMBER;
     idPantallaConfigurableVar NUMBER;
     consultaVariables VARCHAR2(2000) := 'select dd.codconcepto, dd.nomconcepto, DD.ETIQUETAXML,
                                          DD.CODTIPONATURALEZA, dd.numtabla, dd.multiple, dd.longitud
                                          from o02agpe0.tb_sc_oi_ubicaciones ub, o02agpe0.tb_sc_oi_org_info oi,
                                          o02agpe0.tb_sc_dd_dic_datos dd, o02agpe0.tb_sc_oi_usos usos
                                          where ub.codubicacion = 16
                                          and oi.coduso = 31
                                          and oi.coduso = usos.coduso
                                          and ub.codubicacion = oi.codubicacion
                                          and oi.codconcepto = dd.codconcepto
                                          and oi.lineaseguroid = '||LINEASEGUROPARAM;
     TYPE cur_typ IS REF CURSOR;
     c_cursor cur_typ;
     codConceptoVar NUMBER;
     nomConceptoVar VARCHAR2(25);
     etiquetaXmlVar VARCHAR2(30);
     tipoNaturalezaVar NUMBER;
     numTablaVar NUMBER;
     multipleVar VARCHAR2(2);
     longitudVar NUMBER;
     x NUMBER := 20;
     y NUMBER := 20;
   BEGIN
        SELECT IDPANTALLA INTO idPantallaVar FROM o02agpe0.TB_PANTALLAS WHERE OBJETOPANTALLA = 'POLIZA';
        SELECT o02agpe0.SQ_PANTALLAS_CONFIGURABLES.NEXTVAL INTO idPantallaConfigurableVar FROM DUAL;
        INSERT INTO o02agpe0.TB_PANTALLAS_CONFIGURABLES VALUES (idPantallaConfigurableVar, LINEASEGUROPARAM, idPantallaVar, 'A01', 1);
     
        OPEN c_cursor FOR consultaVariables;
        FETCH c_cursor INTO codConceptoVar, nomConceptoVar, etiquetaXmlVar, tipoNaturalezaVar, numTablaVar, multipleVar, longitudVar;
        WHILE c_cursor%FOUND
        LOOP
          IF (multipleVar = 'N' AND numTablaVar = 0) THEN
           --Texto
           IF (codConceptoVar = 134) THEN
              numTablaVar := 3;
              INSERT INTO o02agpe0.TB_CONFIGURACION_CAMPOS
              VALUES (idPantallaConfigurableVar, 1, LINEASEGUROPARAM, 16, codConceptoVar, 31, nomConceptoVar, 6, x, y, longitudVar, 20, 'N', 'N', numTablaVar, 'N', null);
              y := y+ 30;
           ELSE
             INSERT INTO o02agpe0.TB_CONFIGURACION_CAMPOS
             VALUES (idPantallaConfigurableVar, 1, LINEASEGUROPARAM, 16, codConceptoVar, 31, nomConceptoVar, 1, x, y, longitudVar, 20, 'N', 'N', NULL, 'N', null);
             y := y+ 30;
           END IF;
          ELSE
               IF (multipleVar = 'N' AND numTablaVar != 0) THEN
                  --Combo
                  numTablaVar := o02agpe0.Pq_Importacion_Csv.FN_DAMEORIGENDATOS(numTablaVar);
                  INSERT INTO o02agpe0.TB_CONFIGURACION_CAMPOS
                  VALUES (idPantallaConfigurableVar, 1, LINEASEGUROPARAM, 16, codConceptoVar, 31, nomConceptoVar, 6, x, y, longitudVar, 20, 'N', 'N', numTablaVar, 'N', null);
                  y := y+ 30;
               ELSE
                   IF (multipleVar = 'S' AND numTablaVar != 0) THEN
                      numTablaVar := o02agpe0.pq_importacion_csv.FN_DAMEORIGENDATOS(numTablaVar);
                      --Select multiple
                      INSERT INTO o02agpe0.TB_CONFIGURACION_CAMPOS
                      VALUES (idPantallaConfigurableVar, 1, LINEASEGUROPARAM, 16, codConceptoVar, 31, nomConceptoVar, 5, x, y, longitudVar, 20, 'N', 'N', numTablaVar, 'N', null);
                      y := y+ 30;
                   END IF;
               END IF;
          END IF;
          FETCH c_cursor INTO codConceptoVar, nomConceptoVar, etiquetaXmlVar, tipoNaturalezaVar, numTablaVar, multipleVar, longitudVar;
        END LOOP;
        COMMIT;
   END PR_CREAPANTALLACONFIGURABLE;

   FUNCTION FN_DAMEORIGENDATOS (NUMTABLA IN NUMBER) RETURN NUMBER
   IS
     retorno NUMBER := 0;
   BEGIN
        CASE numTabla
             WHEN 5 THEN retorno := 1;
             WHEN 56 THEN retorno := 2;
             WHEN 51 THEN retorno := 4;
             WHEN 54 THEN retorno := 5;
             WHEN 105 THEN retorno := 6;
             WHEN 83 THEN retorno := 7;
             ELSE retorno := NULL;
        END CASE;
        RETURN retorno;
   END FN_DAMEORIGENDATOS;

   PROCEDURE PR_INSERTAERROR (
      tipoimportacion   IN   NUMBER,
      PLAN              IN   NUMBER,
      linea             IN   NUMBER,
      error             IN   VARCHAR2
   )
   IS
      linsegid   NUMBER (15);
      mensaje    VARCHAR2 (2000);
   BEGIN
      SELECT lineaseguroid
        INTO linsegid
        FROM o02agpe0.tb_lineas
       WHERE codlinea = linea AND codplan = PLAN;

      IF (LENGTH (error) > 2000)
      THEN
         mensaje := SUBSTR (error, 0, 2000);
      ELSE
         mensaje := error;
      END IF;

      INSERT INTO o02agpe0.tb_hist_importaciones
                  (idhistorico, idtipoimportacion, fechaimport,
                   estado, lineaseguroid, descerror)
           VALUES (o02agpe0.sq_hist_importaciones.NEXTVAL, tipoimportacion, SYSDATE,
                   'Error', linsegid, mensaje);

      COMMIT;
   END PR_INSERTAERROR;

   PROCEDURE PR_CARGA_GRUPOS_RAZAS (P_LINEASEGUROID IN NUMBER)
   IS
      contador      NUMBER (2)  := 0;
   BEGIN
      SELECT COUNT (*) INTO contador FROM o02agpe0.Tb_Sc_c_Grupos_Razas WHERE lineaseguroid = P_LINEASEGUROID AND codgruporaza = 999;

      IF (contador = 0) THEN
         INSERT INTO o02agpe0.Tb_Sc_c_Grupos_Razas VALUES (P_LINEASEGUROID, 999, 'TODAS');
      END IF;
   END PR_CARGA_GRUPOS_RAZAS;

   PROCEDURE PR_CARGA_ESPECIE (P_LINEASEGUROID IN NUMBER)
   IS
      contador      NUMBER (2)  := 0;
   BEGIN
      dbms_output.put_line('PK_IMPORT_CSV epecie linId: '|| P_LINEASEGUROID);
      PQ_UTL.log('PK_IMPORT_CSV epecie linId', P_LINEASEGUROID);
      SELECT COUNT (*) INTO contador FROM o02agpe0.tb_sc_c_especie WHERE lineaseguroid = P_LINEASEGUROID AND codespecie = 999;

      IF (contador = 0) THEN
        INSERT INTO o02agpe0.tb_sc_c_especie VALUES (P_LINEASEGUROID, 999, 'TODAS');
      END IF;
   exception
       when others then
            PQ_UTL.log('PK_IMPORT_CSV epecie', sqlerrm);
            dbms_output.put_line('PK_IMPORT_CSV epecie: '|| sqlerrm);
   END PR_CARGA_ESPECIE;

   PROCEDURE PR_CARGA_TIPOS_ANIMAL (P_LINEASEGUROID IN NUMBER)
   IS
      contador      NUMBER (2)  := 0;
   BEGIN
      SELECT COUNT (*) INTO contador FROM o02agpe0.TB_SC_C_TIPOS_ANIMAL_GANADO WHERE lineaseguroid = P_LINEASEGUROID AND codtipoanimal = 999;

      IF (contador = 0) THEN
         INSERT INTO o02agpe0.TB_SC_C_TIPOS_ANIMAL_GANADO VALUES (P_LINEASEGUROID, 999, 'TODOS');
      END IF;
   END PR_CARGA_TIPOS_ANIMAL;

   PROCEDURE PR_CARGA_REGIMEN_GANADO (P_LINEASEGUROID IN NUMBER)
   IS
      contador      NUMBER (2)  := 0;
   BEGIN
      SELECT COUNT (*) INTO contador FROM o02agpe0.TB_SC_C_REGIMEN_MANEJO WHERE lineaseguroid = P_LINEASEGUROID AND codregimen = 999;

      IF (contador = 0) THEN
         INSERT INTO o02agpe0.TB_SC_C_REGIMEN_MANEJO VALUES (P_LINEASEGUROID, 999, 'TODOS');
      END IF;
   END PR_CARGA_REGIMEN_GANADO;

   PROCEDURE PR_CARGA_GRUPOS_NEGOCIO
   IS
      contador      NUMBER (2)  := 0;
   BEGIN
      SELECT COUNT (*) INTO contador FROM o02agpe0.TB_SC_C_GRUPOS_NEGOCIO WHERE grupo_negocio = 9;

      IF (contador = 0) THEN
         INSERT INTO o02agpe0.TB_SC_C_GRUPOS_NEGOCIO VALUES (9, 'TODOS');
      END IF;
   END PR_CARGA_GRUPOS_NEGOCIO;

   PROCEDURE PR_CARGA_CAPITALES_GENERICOS
   IS
      contador      NUMBER (2)  := 0;
   BEGIN
      SELECT COUNT (*) INTO contador FROM o02agpe0.TB_SC_C_TIPO_CAPITAL tip WHERE tip.codtipocapital = 999;

      IF (contador = 0) THEN
         INSERT INTO o02agpe0.TB_SC_C_TIPO_CAPITAL VALUES (999, 'TODOS','');
      END IF;
   END PR_CARGA_CAPITALES_GENERICOS;

   -- Inserta en la tabla de líneas un registro con el plan indicado y la línea genérica si no existe ya
   PROCEDURE PR_CARGA_LINEA_GENERICA (P_PLAN IN NUMBER)
   IS
      contador      NUMBER (2)  := 0;
   BEGIN

        PQ_UTL.log('PK_IMPORTACION_CSV.PR_CARGA_LINEA_GENERICA. Comprueba si existe linea generica para el plan ' || P_PLAN);
        SELECT COUNT (*) INTO contador FROM o02agpe0.tb_lineas l WHERE l.codplan = P_PLAN and l.codlinea = 999;

        IF (contador = 0) THEN
           PQ_UTL.log('PK_IMPORTACION_CSV.PR_CARGA_LINEA_GENERICA. No existe linea generica para el plan ' || P_PLAN || '. Se inserta');
           INSERT INTO o02agpe0.tb_lineas VALUES (o02agpe0.sq_lineaseguro.nextval, 999, P_PLAN, 'TODAS LAS LÍNEAS', 'INVÁLIDO', 'NO', NULL, NULL, NULL);
        ELSE
           PQ_UTL.log('PK_IMPORTACION_CSV.PR_CARGA_LINEA_GENERICA. Ya existe linea generica para el plan ' || P_PLAN);
        END IF;

    EXCEPTION
        WHEN OTHERS THEN
             PQ_UTL.log('PK_IMPORTACION_CSV.PR_CARGA_LINEA_GENERICA. Error ', sqlerrm);
    END;

    -- Inserta en la tabla de líneas un registro con el plan indicado y la línea genérica si no existe ya
   PROCEDURE PR_CARGA_LINEA_GENERICA
   IS
      CURSOR cur IS select lin.CODPLAN from o02agpe0.tb_lineas lin group by lin.CODPLAN order by lin.CODPLAN desc;
      codplan  o02agpe0.TB_LINEAS.CODPLAN%TYPE;
   BEGIN
        PQ_UTL.log('PK_IMPORTACION_CSV.PR_CARGA_LINEA_GENERICA. Llamar a PR_CARGA_LINEA_GENERICA para todos los planes registrados ');
        OPEN cur;
        FETCH cur INTO codplan;

       WHILE cur%FOUND
       LOOP
          PR_CARGA_LINEA_GENERICA(codplan);
          FETCH cur INTO codplan;
       END LOOP;
       CLOSE cur;

   EXCEPTION
        WHEN OTHERS THEN
             PQ_UTL.log('PK_IMPORTACION_CSV.PR_CARGA_LINEA_GENERICA. Error ', sqlerrm);

   END;

   PROCEDURE PR_CARGA_LINEAS_GRUPO_NEGOCIO(P_LINEASEGUROID IN NUMBER)
   IS
     v_esLineaGanado NUMBER;
   BEGIN
      IF P_LINEASEGUROID is not NULL THEN
          PQ_UTL.log('PK_IMPORTACION_CSV.PR_CARGA_LINEAS_GRUPO_NEGOCIO. Elimina todos los registros de la línea '
                          || P_LINEASEGUROID || ' en  la tabla TB_LINEAS_GRUPO_NEGOCIO ');
            DELETE FROM o02agpe0.TB_LINEAS_GRUPO_NEGOCIO WHERE LINEASEGUROID = P_LINEASEGUROID;
           PQ_UTL.log('PK_IMPORTACION_CSV.PR_CARGA_LINEAS_GRUPO_NEGOCIO. Registros eliminados');
            /* ***************************  */
            v_esLineaGanado:=PQ_ACTIVACION.FN_ESLINEA_GANADO(P_LINEASEGUROID);
            IF v_esLineaGanado = 0 THEN /*LÍNEA DE AGRO*/
                PQ_UTL.log('PK_IMPORTACION_CSV.PR_CARGA_LINEAS_GRUPO_NEGOCIO. Línea de AGRO. Insertamos los registros en TB_LINEAS_GRUPO_NEGOCIO');
                INSERT INTO o02agpe0.TB_LINEAS_GRUPO_NEGOCIO
                  SELECT pre.LINEASEGUROID, cgn.GRUPO_NEGOCIO FROM o02agpe0.TB_SC_C_TIPO_CAPITAL_GRUPO_NEG cgn
                    inner join o02agpe0.TB_SC_C_PRECIOS pre ON cgn.CODTIPOCAPITAL = pre.CODTIPOCAPITAL
                    where pre.LINEASEGUROID= P_LINEASEGUROID
                    group by cgn.GRUPO_NEGOCIO, pre.LINEASEGUROID;
                PQ_UTL.log('PK_IMPORTACION_CSV.PR_CARGA_LINEAS_GRUPO_NEGOCIO. Registros insertados');
            ELSIF v_esLineaGanado = 1 THEN/*LÍNEA DE GANADO*/
                PQ_UTL.log('PK_IMPORTACION_CSV.PR_CARGA_LINEAS_GRUPO_NEGOCIO. Línea de GANADO. Insertamos los registros en TB_LINEAS_GRUPO_NEGOCIO');
                INSERT INTO o02agpe0.TB_LINEAS_GRUPO_NEGOCIO
                  SELECT pre.LINEASEGUROID, cgn.GRUPO_NEGOCIO FROM o02agpe0.TB_SC_C_TIPO_CAPITAL_GRUPO_NEG cgn
                    inner join o02agpe0.TB_SC_C_PRECIOS_GANADO pre ON cgn.CODTIPOCAPITAL = pre.CODTIPOCAPITAL
                    where pre.LINEASEGUROID= P_LINEASEGUROID
                    group by cgn.GRUPO_NEGOCIO, pre.LINEASEGUROID;
            --    PQ_UTL.log('PK_IMPORTACION_CSV.PR_CARGA_LINEAS_GRUPO_NEGOCIO. Registros insertados');
            END IF;
      ELSE
            /*Carga inicial*/
            PQ_UTL.log('PK_IMPORTACION_CSV.PR_CARGA_LINEAS_GRUPO_NEGOCIO. Carga inicial - Elimina todos los registros de la tabla TB_LINEAS_GRUPO_NEGOCIO');
            DELETE FROM o02agpe0.TB_LINEAS_GRUPO_NEGOCIO;
            PQ_UTL.log('PK_IMPORTACION_CSV.PR_CARGA_LINEAS_GRUPO_NEGOCIO. Todos los registros eliminados');
            /*Insertamos los registros de las líneas de AGRO*/
            INSERT INTO o02agpe0.TB_LINEAS_GRUPO_NEGOCIO
                SELECT pre.LINEASEGUROID, cgn.GRUPO_NEGOCIO FROM o02agpe0.TB_SC_C_TIPO_CAPITAL_GRUPO_NEG cgn
                  inner join o02agpe0.TB_SC_C_PRECIOS pre ON cgn.CODTIPOCAPITAL = pre.CODTIPOCAPITAL
                  group by cgn.GRUPO_NEGOCIO, pre.LINEASEGUROID;
            PQ_UTL.log('PK_IMPORTACION_CSV.PR_CARGA_LINEAS_GRUPO_NEGOCIO. Insertados todos los registros de las líneas de AGRO');
            /*Insertamos los registros de las líneas de GANADO*/
            INSERT INTO o02agpe0.TB_LINEAS_GRUPO_NEGOCIO
                SELECT pre.LINEASEGUROID, cgn.GRUPO_NEGOCIO FROM o02agpe0.TB_SC_C_TIPO_CAPITAL_GRUPO_NEG cgn
                  inner join o02agpe0.TB_SC_C_PRECIOS_GANADO pre ON cgn.CODTIPOCAPITAL = pre.CODTIPOCAPITAL
                  group by cgn.GRUPO_NEGOCIO, pre.LINEASEGUROID;
            PQ_UTL.log('PK_IMPORTACION_CSV.PR_CARGA_LINEAS_GRUPO_NEGOCIO. Insertados todos los registros de las líneas de GANADO');
      END IF;

      COMMIT;
   EXCEPTION
     WHEN OTHERS THEN
             PQ_UTL.log('PK_IMPORTACION_CSV.PR_CARGA_LINEAS_GRUPO_NEGOCIO. Error ', sqlerrm);
   END;

   PROCEDURE PR_CARGA_TIPO_CAPITAL_GN
   IS
      contador      NUMBER (2)  := 0;
   BEGIN
      SELECT COUNT (*) INTO contador FROM o02agpe0.tb_sc_c_tipo_capital_grupo_neg WHERE CODTIPOCAPITAL = 999;

      IF (contador = 0) THEN
         INSERT INTO o02agpe0.tb_sc_c_tipo_capital_grupo_neg VALUES ( 999, 'TODOS', null, null);
      END IF;
   END PR_CARGA_TIPO_CAPITAL_GN;

   PROCEDURE PR_CARGA_GARANTIZADO IS
     BEGIN
             -- Borramos los datos de la tabla tb_sc_c_garantizado
             delete o02agpe0.tb_sc_c_garantizado;
             -- Buscamos en la tabla tb_sc_c_datos_buzon_general por codigo concepto 175
             -- e insertamos en tb_sc_c_garantizado
             insert into o02agpe0.tb_sc_c_garantizado (CODGARANTIZADO, DESGARANTIZADO)
             select  VALOR_CPTO,DESCRIPCION  from o02agpe0.tb_sc_c_datos_buzon_general g where g.codcpto=175;

   END PR_CARGA_GARANTIZADO;



-- Procedimiento para importar y actualizar datos desde la tabla de Ambito SIGPAC a la tabla de terminos
    PROCEDURE PR_XML2TABLE_SIGPAC (
        P_TABLA_ORIGEN IN VARCHAR2 DEFAULT 'TBX_SC_C_AMBITO_SIGPAC',
        P_TABLA_DESTINO IN VARCHAR2 DEFAULT 'TB_TERMINOS',
        P_IDHISTORICO IN NUMBER,
        P_NUMTABLA IN NUMBER,
        P_FICHERO IN VARCHAR2
    ) IS
    V_LC VARCHAR2(50) := 'PQ_IMPORTACION_CSV.PR_XML2TABLE_SIGPAC';
    v_sql VARCHAR2(32000) := NULL;
    v_errm VARCHAR2(32000);
    v_code NUMBER;

    BEGIN
    v_sql := 'MERGE INTO o02agpe0.' || P_TABLA_DESTINO || ' d
              USING (SELECT * FROM o02agpe1.' || P_TABLA_ORIGEN || ') s
              ON (d.CODPROVINCIA = s.CODPROVINCIA AND d.CODCOMARCA = s.CODCOMARCA AND d.CODTERMINO = s.CODTERMINO AND d.SUBTERMINO = s.SUBTERMINO)
              WHEN MATCHED THEN
                UPDATE SET d.NOMBRE_SIGPAC = s.NOMTERMINO
              WHEN NOT MATCHED THEN
                INSERT (CODPROVINCIA, CODCOMARCA, CODTERMINO, SUBTERMINO, NOMTERMINO, NOMBRE_SIGPAC, NOMBRE_REGA)
                VALUES (s.CODPROVINCIA, s.CODCOMARCA, s.CODTERMINO, s.SUBTERMINO, s.NOMTERMINO, s.NOMTERMINO, s.NOMTERMINO)';

        -- Registra el inicio de la operacion
        pq_utl.log(V_LC, 'Iniciando operacion con tabla externa: ' || v_sql || ' --', 2);

    BEGIN
        EXECUTE IMMEDIATE v_sql;

        -- Registra el resultado de la operacion
        pq_utl.log(V_LC, 'Operacion finalizada en ' || P_TABLA_DESTINO || ' --', 2);

        INSERT INTO o02agpe0.tb_importacion_tablas VALUES (P_IDHISTORICO, P_NUMTABLA, 'Importado', NULL, P_FICHERO);
    EXCEPTION
        WHEN OTHERS THEN
            v_code := SQLCODE;
            v_errm := SUBSTR(SQLERRM, 1, 100);
            -- Registra el error
            pq_utl.log(V_LC, 'Error durante la operacion en ' || P_TABLA_DESTINO || '. SQLCODE=' || v_code || '. SQLERRM=' || v_errm, 2);
    END;

    END PR_XML2TABLE_SIGPAC;


    -- Procedimiento para importar y actualizar datos desde la tabla de Ambito REGA a la tabla de terminos
    PROCEDURE PR_XML2TABLE_REGA (
    P_TABLA_ORIGEN IN VARCHAR2 DEFAULT 'TBX_SC_C_AMBITO_REGA',
    P_TABLA_DESTINO IN VARCHAR2 DEFAULT 'TB_TERMINOS',
    P_IDHISTORICO IN NUMBER,
    P_NUMTABLA IN NUMBER,
    P_FICHERO IN VARCHAR2
    ) IS
        V_LC VARCHAR2(50) := 'PQ_IMPORTACION_CSV.PR_XML2TABLE_REGA';
        v_sql VARCHAR2(32000) := NULL;
        v_errm VARCHAR2(32000);
        v_code NUMBER;

    BEGIN
        v_sql := 'MERGE INTO o02agpe0.' || P_TABLA_DESTINO || ' d
                  USING (SELECT * FROM o02agpe1.' || P_TABLA_ORIGEN || ') s
                  ON (d.CODPROVINCIA = s.CODPROVINCIA AND d.CODCOMARCA = s.CODCOMARCA AND d.CODTERMINO = s.CODTERMINO AND d.SUBTERMINO = s.SUBTERMINO)
                  WHEN MATCHED THEN
                    UPDATE SET d.NOMBRE_REGA = s.NOMTERMINO
                  WHEN NOT MATCHED THEN
                    INSERT (CODPROVINCIA, CODCOMARCA, CODTERMINO, SUBTERMINO, NOMTERMINO, NOMBRE_SIGPAC, NOMBRE_REGA)
                    VALUES (s.CODPROVINCIA, s.CODCOMARCA, s.CODTERMINO, s.SUBTERMINO, s.NOMTERMINO, s.NOMTERMINO, s.NOMTERMINO)';
        -- Registra el inicio de la operacion
        pq_utl.log(V_LC, 'Iniciando operacion con tabla externa: ' || v_sql || ' --', 2);

        BEGIN
            EXECUTE IMMEDIATE v_sql;

            -- Registra el resultado de la operacion
            pq_utl.log(V_LC, 'Operacion finalizada en ' || P_TABLA_DESTINO || ' --', 2);

            INSERT INTO o02agpe0.tb_importacion_tablas VALUES (P_IDHISTORICO, P_NUMTABLA, 'Importado', NULL, P_FICHERO);
        EXCEPTION
            WHEN OTHERS THEN
                v_code := SQLCODE;
                v_errm := SUBSTR(SQLERRM, 1, 100);
                -- Registra el error
                pq_utl.log(V_LC, 'Error durante la operacion en ' || P_TABLA_DESTINO || '. SQLCODE=' || v_code || '. SQLERRM=' || v_errm, 2);
        END;

    END PR_XML2TABLE_REGA;
    
    -- Procedimiento para importar y actualizar datos desde la tabla de Ambito REGA a la tabla de terminos
    PROCEDURE PR_XML2TABLE_CONDICIONADOS (
    P_TABLA_ORIGEN IN VARCHAR2 DEFAULT 'TBX_SC_C_TABLAS_CONDICIONADO',
    P_TABLA_DESTINO IN VARCHAR2 DEFAULT 'TB_SC_C_TABLAS_CONDICIONADO',
    P_IDHISTORICO IN NUMBER,
    P_NUMTABLA IN NUMBER,
    P_FICHERO IN VARCHAR2
    ) IS
        V_LC VARCHAR2(50) := 'PQ_IMPORTACION_CSV.PR_XML2TABLE_CONDICIONADOS';
        v_sql VARCHAR2(32000) := NULL;
        v_errm VARCHAR2(32000);
        v_code NUMBER;

    BEGIN
        v_sql := 'MERGE INTO o02agpe0.' || P_TABLA_DESTINO || ' d
                  USING (SELECT * FROM o02agpe1.' || P_TABLA_ORIGEN || ') s
                  ON (d.CODTABLACONDICIONADO=s.CODTABLACONDICIONADO)
                  WHEN MATCHED THEN
                    UPDATE SET d.DESTABLACONDICIONADO = s.DESTABLACONDICIONADO
                  WHEN NOT MATCHED THEN
                    INSERT (CODTABLACONDICIONADO, DESTABLACONDICIONADO)
                    VALUES (s.CODTABLACONDICIONADO, s.DESTABLACONDICIONADO)';
        -- Registra el inicio de la operacion
        pq_utl.log(V_LC, 'Iniciando operacion con tabla externa: ' || v_sql || ' --', 2);

        BEGIN
            EXECUTE IMMEDIATE v_sql;

            -- Registra el resultado de la operacion
            pq_utl.log(V_LC, 'Operacion finalizada en ' || P_TABLA_DESTINO || ' --', 2);

            INSERT INTO o02agpe0.tb_importacion_tablas VALUES (P_IDHISTORICO, P_NUMTABLA, 'Importado', NULL, P_FICHERO);
        EXCEPTION
            WHEN OTHERS THEN
                v_code := SQLCODE;
                v_errm := SUBSTR(SQLERRM, 1, 100);
                -- Registra el error
                pq_utl.log(V_LC, 'Error durante la operacion en ' || P_TABLA_DESTINO || '. SQLCODE=' || v_code || '. SQLERRM=' || v_errm, 2);
        END;

    END PR_XML2TABLE_CONDICIONADOS;
    
    -- Procedimiento para importar y actualizar datos desde la tabla de Ambito REGA a la tabla de terminos
    PROCEDURE PR_XML2TABLE_SUBCCAA (
    P_TABLA_ORIGEN IN VARCHAR2 DEFAULT 'TBX_SC_C_TIPO_SUBV_CCAA',
    P_TABLA_DESTINO IN VARCHAR2 DEFAULT 'TB_SC_C_TIPO_SUBV_CCAA',
    P_IDHISTORICO IN NUMBER,
    P_NUMTABLA IN NUMBER,
    P_FICHERO IN VARCHAR2
    ) IS
        V_LC VARCHAR2(50) := 'PQ_IMPORTACION_CSV.PR_XML2TABLE_SUBCCAA';
        v_sql VARCHAR2(32000) := NULL;
        v_errm VARCHAR2(32000);
        v_code NUMBER;

    BEGIN
        v_sql := 'MERGE INTO o02agpe0.' || P_TABLA_DESTINO || ' d
                  USING (SELECT * FROM o02agpe1.' || P_TABLA_ORIGEN || ') s
                  ON (d.CODTIPOSUBVCCAA=s.CODTIPOSUBVCCAA)
                  WHEN MATCHED THEN
                    UPDATE SET d.DESTIPOSUBVCCAA = s.DESTIPOSUBVCCAA, d.DECLARABLE = s.DECLARABLE, d.NIVELDECLARACION = s.NIVELDECLARACION, d.NIVELDEPENDENCIA = s.NIVELDEPENDENCIA,
					d.APLICABLE = s.APLICABLE, d.CODCONCEPTO = s.CODCONCEPTO
                  WHEN NOT MATCHED THEN
                    INSERT (CODTIPOSUBVCCAA, DESTIPOSUBVCCAA, DECLARABLE, NIVELDECLARACION, NIVELDEPENDENCIA, APLICABLE, CODCONCEPTO)
                    VALUES (s.CODTIPOSUBVCCAA, s.DESTIPOSUBVCCAA, s.DECLARABLE, s.NIVELDECLARACION, s.NIVELDEPENDENCIA, s.APLICABLE, s.CODCONCEPTO)';
        -- Registra el inicio de la operacion
        pq_utl.log(V_LC, 'Iniciando operacion con tabla externa: ' || v_sql || ' --', 2);

        BEGIN
            EXECUTE IMMEDIATE v_sql;

            -- Registra el resultado de la operacion
            pq_utl.log(V_LC, 'Operacion finalizada en ' || P_TABLA_DESTINO || ' --', 2);

            INSERT INTO o02agpe0.tb_importacion_tablas VALUES (P_IDHISTORICO, P_NUMTABLA, 'Importado', NULL, P_FICHERO);
        EXCEPTION
            WHEN OTHERS THEN
                v_code := SQLCODE;
                v_errm := SUBSTR(SQLERRM, 1, 100);
                -- Registra el error
                pq_utl.log(V_LC, 'Error durante la operacion en ' || P_TABLA_DESTINO || '. SQLCODE=' || v_code || '. SQLERRM=' || v_errm, 2);
        END;

    END PR_XML2TABLE_SUBCCAA;

END PQ_IMPORTACION_CSV;    
/
SHOW ERRORS;