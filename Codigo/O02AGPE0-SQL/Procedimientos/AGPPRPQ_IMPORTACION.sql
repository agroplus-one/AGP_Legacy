SET DEFINE OFF;
SET SERVEROUTPUT ON;

create or replace package o02agpe0.PQ_IMPORTACION is

  -- Author  : U028937
  -- Created : 25/5/2010 11:19:36
  -- Purpose :

  -- Public type declarations
  TYPE t_array IS TABLE OF VARCHAR2(2000) INDEX BY BINARY_INTEGER;

  -- PROCEDIMIENTOS Y FUNCIONES
  -- Procedimiento para lanzar la carga
  PROCEDURE PR_CARGAXMLS (TABLAS IN VARCHAR2, TIPOIMPORTACION IN NUMBER, PLAN IN NUMBER, LINEA IN NUMBER, RUTA IN VARCHAR2, FICHTABLAS IN VARCHAR2);
  -- Procedimiento para realizar la inserción de un fichero xml
  FUNCTION FN_XML2TABLE (v_directorio IN VARCHAR2, v_ficheroCanonico IN VARCHAR2, v_table IN VARCHAR2, v_plan IN NUMBER, v_linea IN NUMBER, idHistorico NUMBER, numTabla NUMBER, idTabla NUMBER, FICHTABLAS VARCHAR2) RETURN BOOLEAN;
  --Procedimiento para hacer una copia temporal de las tablas de subvenciones durante la importación
  PROCEDURE PR_COPIA_TEMP_SUBVENCIONES(v_table IN VARCHAR2, v_lineaseguroid IN NUMBER);
  --Procedimiento para actualizar los identificadores de las subvenciones ENESA
  PROCEDURE PR_ACTUALIZA_IDS_SUBV_ENESA(v_table IN VARCHAR2, v_lineaseguroid IN NUMBER);
  --Procedimiento para actualizar los identificadores de las subvenciones CCAA
  PROCEDURE PR_ACTUALIZA_IDS_SUBV_CCAA(v_table IN VARCHAR2, v_lineaseguroid IN NUMBER);

  FUNCTION FN_COMPRUEBAPL (xml_canonico IN xmltype, v_lineaSeguroId IN NUMBER) RETURN BOOLEAN;

  -- Función para comprobar si el fichero xml de grupo tasa riesgo tiene algún registro para insertarlo
  FUNCTION FN_hayRegGrpTasaRiesgo(xml_canonico IN xmltype) RETURN BOOLEAN;
  -- Funciones para realizar las inserciones para las provincias 99
  PROCEDURE PR_CARGA_MODULOS (lineaSeguro IN NUMBER);

  PROCEDURE PR_CARGAVALORESNUEVE (lineaSeguro IN NUMBER);
  PROCEDURE PR_CARGATBCOMARCAS;
  PROCEDURE PR_CARGAPROVINCIAS;
  PROCEDURE PR_CARGATERMINOS(provincia   IN   tb_provincias.codprovincia%TYPE);
  PROCEDURE PR_CARGASUBTERMINOS(provincia   IN   tb_provincias.codprovincia%TYPE,
                          comarca IN tb_comarcas.codcomarca%TYPE);
  PROCEDURE PR_CARGACOMARCAS9;
  procedure pr_cargasubtermino_no9(
             provincia   IN   tb_provincias.codprovincia%TYPE,
             termino     IN   tb_terminos.codtermino%type,
             comarca     IN   tb_comarcas.codcomarca%TYPE);
  PROCEDURE PR_CARGATBCULTIVOS;
  PROCEDURE PR_CARGATBVARIEDADES (lineaSeguro IN NUMBER);
  PROCEDURE PR_CARGA_LINEAS;

  PROCEDURE updateOrganizador (tabla IN VARCHAR2, lineaSeguroId IN NUMBER);
  PROCEDURE deleteCondGeneral (tabla IN VARCHAR2, v_linea IN NUMBER);
  PROCEDURE deleteCondPL (tabla IN VARCHAR2, planLineaId NUMBER);

  FUNCTION DAMEFICHEROXTABLA (ficheroTabla IN t_array, idTabla NUMBER) RETURN VARCHAR2;
  FUNCTION FN_SPLIT (cadena IN VARCHAR2, separador IN CHAR) RETURN t_array;
  PROCEDURE PR_INSERTAERROR (tipoImportacion IN NUMBER, plan IN NUMBER, linea IN NUMBER, error IN VARCHAR2);
  PROCEDURE PR_ACTUALIZA_SUBTERMINO (tabla IN VARCHAR2);
  PROCEDURE PR_CREAPANTALLACONFIGURABLE (LINEASEGUROPARAM IN NUMBER);
  FUNCTION FN_DAMEORIGENDATOS (NUMTABLA IN NUMBER) RETURN NUMBER;
  --Procedimiento para cargar el sistema de producción por defecto (codigo 0)
  PROCEDURE pr_cargaSistProdDefecto;
  --Procedimiento para cargar la denominación de origen por defecto (codigo 0)
  PROCEDURE pr_cargaDenomOrigenDefecto (lineaseguroid in number);
end PQ_IMPORTACION;
/
CREATE OR REPLACE PACKAGE BODY O02AGPE0.pq_importacion
IS
   -- Procedimiento para lanzar la carga de cada uno de los xml de agroseguro incluidos en el parámetro
   PROCEDURE pr_cargaxmls (
      tablas            IN   VARCHAR2,
      tipoimportacion   IN   NUMBER,
      PLAN              IN   NUMBER,
      linea             IN   NUMBER,
      ruta              IN   VARCHAR2,
      fichtablas        IN   VARCHAR2
   )
   IS
      idhistorico         NUMBER;
      idtabla             NUMBER (4);
      v_lineaseguroid     NUMBER (15);
      deslinea            VARCHAR2 (25);
      --ficheroCanonico VARCHAR2(50);
      --tabla VARCHAR2(30);
      reg_tabla           tb_tablas_xmls.nombre%TYPE;
      reg_canonico        tb_tablas_xmls.xml%TYPE;
      consulta            VARCHAR2 (2000)
         :=    'SELECT substr(t.xsl, 0, instr(t.xsl, '
            || '''.xsl'''
            || ')-1)'
            || ' || ''Canonico.xml'''
            || ' AS CANONICO, t.NOMBRE, t.NUMTABLA, t.ID FROM tb_tablas_xmls t WHERE t.NUMTABLA IN ('
            || tablas
            || ') ORDER BY orden';

      TYPE cur_typ IS REF CURSOR;

      c_tablas_importar   cur_typ;
      ficherotabla        t_array;
      nums_tablas         t_array;
      numtabla            NUMBER (15);
      contador            NUMBER := 0;
      txt_fichero         VARCHAR2 (50);
      error_insercion     BOOLEAN := FALSE;
      error_importacion   BOOLEAN := FALSE;
      err_num NUMBER;
      err_msg VARCHAR2(2000);
      err_anterior VARCHAR2(2000);
      err_cnt NUMBER;

   BEGIN
      ficherotabla := fn_split (fichtablas, ',');
      nums_tablas := fn_split (tablas, ',');

      execute immediate 'ALTER SESSION SET NLS_LANGUAGE = AMERICAN';
      execute immediate 'ALTER SESSION SET NLS_TERRITORY = AMERICA';
      execute immediate 'ALTER SESSION SET NLS_NUMERIC_CHARACTERS = ".,"';

      --Obtener lineaseguroid
      IF (PLAN is not null AND linea is not null) THEN
        SELECT lineaseguroid
          INTO v_lineaseguroid
          FROM tb_lineas
         WHERE codplan = PLAN AND codlinea = linea;

         SELECT deslinea
             INTO deslinea
             FROM tb_sc_c_lineas
            WHERE codlinea = linea;

         IF (deslinea IS NULL) THEN
           deslinea := '';
         END IF;

         IF (v_lineaseguroid IS NULL) THEN
            INSERT INTO tb_lineas (lineaseguroid, codlinea, codplan, nomlinea, activo)
                   VALUES (sq_lineaseguro.NEXTVAL, linea, PLAN, deslinea, 'NO');
         ELSE
             UPDATE TB_LINEAS SET NOMLINEA = deslinea WHERE LINEASEGUROID = v_lineaseguroid;
         END IF;
       END IF;

      --Creamos el registro del historico
      SELECT sq_hist_importaciones.NEXTVAL
        INTO idhistorico
        FROM DUAL;

      INSERT INTO tb_hist_importaciones
                  (idhistorico, idtipoimportacion, ubicacionficheros,
                   fechaimport, estado, lineaseguroid)
           VALUES (idhistorico, tipoimportacion, ruta,
                   SYSDATE, 'Importado', v_lineaseguroid);

      COMMIT;
      --Obtener los nombres de los ficheros canonizados
      --select substr(t.xml, 0, instr(t.xml, '.xml')-1) || 'Canonico.xml', t.* from tb_tablas_xmls t where ...;
      --Recorrer los resultados de la consulta y llamar al procedimiento de inserción del canónico por cada una de ellas
      pq_utl.LOG ('******** Inicio de la importación. ********', 2);

      OPEN c_tablas_importar FOR consulta;

      LOOP
         FETCH c_tablas_importar INTO reg_canonico, reg_tabla, numtabla, idtabla;
         EXIT WHEN c_tablas_importar%NOTFOUND;
         --Incluyo un bloque de excepciones para que no finalice la importación
         BEGIN
             contador := contador + 1;
             FOR cnt IN nums_tablas.FIRST .. nums_tablas.LAST
             LOOP
                 IF (nums_tablas (cnt) = numtabla) THEN
                     txt_fichero := ficherotabla (cnt);
                 END IF;
             END LOOP;

             IF (idtabla = 9) THEN
                 numtabla := 8;
             END IF;

             pq_utl.LOG ('Importamos de la tabla numero ' || numtabla || ', id ' || idtabla || ' ****', 2);

             error_insercion := pq_importacion.fn_xml2table (ruta,
                                            reg_canonico,
                                            reg_tabla,
                                            PLAN,
                                            linea,
                                            idhistorico,
                                            numtabla,
                                            idtabla,
                                            txt_fichero
                                           );

             IF (error_insercion = TRUE) THEN
                 error_importacion := TRUE;
             END IF;

             COMMIT;
         EXCEPTION
             WHEN OTHERS THEN
                ROLLBACK;
                error_importacion := TRUE;
                err_num := SQLCODE;
                err_msg := SQLERRM;
                PQ_UTL.log('PR_CARGAXMLS - ERROR: ' || err_num || ' - ' || err_msg || ' ****', 2);
                --Insertamos el error de la importación
                BEGIN
                IF (idTabla = 9 AND numTabla = 8) THEN
                    execute immediate 'select count(*) from TB_IMPORTACION_TABLAS where IDHISTORICO = ' || idHistorico || ' AND CODTABLACONDICIONADO = 8' into err_cnt;
                    IF (err_cnt > 0) then
                       execute immediate 'select descestado from TB_IMPORTACION_TABLAS where IDHISTORICO = ' || idHistorico || ' AND CODTABLACONDICIONADO = 8' into err_anterior;
                       UPDATE TB_IMPORTACION_TABLAS SET estado = 'Error', descestado = err_anterior || '**' || SUBSTR(err_msg, 0, 998)  WHERE IDHISTORICO = idHistorico AND CODTABLACONDICIONADO = 8;
                    ELSE
                       INSERT INTO TB_IMPORTACION_TABLAS VALUES (idHistorico, numTabla, 'Error', SUBSTR(err_msg, 0, 1000), txt_fichero);
                    END IF;
                elsif (idTabla = 8 AND numTabla = 8) THEN
                    execute immediate 'select count(*) from TB_IMPORTACION_TABLAS where IDHISTORICO = ' || idHistorico || ' AND CODTABLACONDICIONADO = 8' into err_cnt;
                    IF (err_cnt > 0) then
                       execute immediate 'select descestado from TB_IMPORTACION_TABLAS where IDHISTORICO = ' || idHistorico || ' AND CODTABLACONDICIONADO = 8' into err_anterior;
                       UPDATE TB_IMPORTACION_TABLAS SET estado = 'Error', descestado = err_anterior || '**' || SUBSTR(err_msg, 0, 998)  WHERE IDHISTORICO = idHistorico AND CODTABLACONDICIONADO = 8;
                    else
                        INSERT INTO TB_IMPORTACION_TABLAS VALUES (idHistorico, numTabla, 'Error', SUBSTR(err_msg, 0, 1000), txt_fichero);
                    end if;
                ELSE
                    INSERT INTO TB_IMPORTACION_TABLAS VALUES (idHistorico, numTabla, 'Error', SUBSTR(err_msg, 0, 2000), txt_fichero);
                END IF;
                COMMIT;
                EXCEPTION
                    WHEN OTHERS THEN
                    err_num := SQLCODE;
                    err_msg := SQLERRM;
                    PQ_UTL.log('PR_CARGAXMLS - ERROR Importacion Tablas: ' || err_num || ' - ' || err_msg || ' ****', 2);
                END;
         END;
      END LOOP;

      pq_utl.LOG ('******** Fin de la importación. ********', 2);

      CLOSE c_tablas_importar;

      IF (error_importacion = TRUE) THEN
          consulta := 'UPDATE TB_HIST_IMPORTACIONES SET ESTADO = ''Error'' WHERE IDHISTORICO = ' || idhistorico;
          EXECUTE IMMEDIATE consulta;
          if (v_lineaseguroid is not null) then
              --Actualizo el estado de la activación
              consulta := 'UPDATE TB_LINEAS SET ESTADO = ''ERROR'', ACTIVO = ''NO'' WHERE LINEASEGUROID = ' || v_lineaseguroid;
              EXECUTE IMMEDIATE consulta;
          end if;
      ELSE
          -- Si la importación no es del condicionado general => no compruebo la activación
          IF (v_lineaseguroid is not null) THEN
              IF (PQ_ACTIVACION.FN_PERMITE_ACTIVACION(v_lineaseguroid) = 1) THEN
                  consulta := 'UPDATE TB_LINEAS SET ESTADO = ''IMPORTADO'', ACTIVO = ''NO'' WHERE LINEASEGUROID = ' || v_lineaseguroid;
              ELSE
                  consulta := 'UPDATE TB_LINEAS SET ESTADO = ''INCOMPLETO'', ACTIVO = ''NO'' WHERE LINEASEGUROID = ' || v_lineaseguroid;
              END IF;
          END IF;
          EXECUTE IMMEDIATE consulta;
      END IF;

      COMMIT;
   END;

   FUNCTION fn_xml2table (
      v_directorio        IN   VARCHAR2,
      v_ficherocanonico   IN   VARCHAR2,
      v_table             IN   VARCHAR2,
      v_plan              IN   NUMBER,
      v_linea             IN   NUMBER,
      idhistorico              NUMBER,
      numtabla                 NUMBER,
      idtabla             IN   NUMBER,
      fichtablas               VARCHAR2
   )
      RETURN BOOLEAN
   IS
      tiposc              VARCHAR2 (3);
      xml_canonico        XMLTYPE;
      v_context           DBMS_XMLSTORE.ctxtype;
      v_rows              NUMBER;
      v_lineaseguroid     NUMBER (15);
      v_charsetid         VARCHAR2 (15);

      errorinsercion      EXCEPTION;

      CURSOR c_columnas IS SELECT column_name FROM all_tab_cols
                           WHERE UPPER (owner) = 'O02AGPE0' AND table_name = v_table ORDER BY column_id;

      err_num             NUMBER;
      err_msg             VARCHAR2 (2000);
      mensaje_error       VARCHAR2 (2000);
      error               BOOLEAN               := FALSE;
      mismopl             BOOLEAN               := TRUE;
      insertar            BOOLEAN               := TRUE;
      err_anterior        VARCHAR2(2000);
      err_cnt             NUMBER;

      v_sql               VARCHAR2(32000) := NULL;

   BEGIN
      BEGIN
         --Obtener lineaseguroid
         IF (v_plan is not null AND v_linea is not null) THEN
            SELECT lineaseguroid INTO v_lineaseguroid FROM tb_lineas WHERE codplan = v_plan AND codlinea = v_linea;
         END IF;

         --Obtener el charset
         SELECT VALUE INTO v_charsetid FROM v$nls_parameters WHERE parameter = 'NLS_CHARACTERSET';

         --Cargar el fichero canonico
         IF (v_ficherocanonico <> 'Canonico.xml') THEN

            xml_canonico := XMLTYPE (BFILENAME (DIRECTORY      => v_directorio,
                                                filename       => v_ficherocanonico),
                                     NLS_CHARSET_ID (v_charsetid));
         end if;
         --Obtenemos el tipo de SC de la tabla para proceder al borrado o actualizacion de sus datos
         SELECT tiposc
           INTO tiposc
           FROM tb_tablas_xmls
          WHERE nombre = v_table;

         --Borrado de los datos antiguos de la tabla
         IF (tiposc = 'ORG')
         THEN
            updateorganizador (TRIM (v_table), v_lineaseguroid);
         ELSE
            IF (tiposc = 'GEN')
            THEN
               deletecondgeneral (TRIM (v_table), v_linea);
            END IF;

            IF (tiposc = 'CPL' AND v_table != 'TB_SC_C_TARIFAS' AND v_table != 'TB_SC_C_PRIMAS_RIESGO'
                AND v_table != 'TB_SC_C_GRUPOS_SUBV' AND v_table != 'TB_SC_C_SUBV_GRUPOS')
            THEN
                IF (numtabla != 19) THEN
                   mismopl := fn_compruebapl (xml_canonico, v_lineaseguroid);
                ELSE
                    mismopl := TRUE;
                END IF;

               IF (mismopl)
               THEN
                   --Las tablas de subvenciones llevan un tratamiento especial por problemas de PK.
                   --Subvenciones: primero se hace una copia temporal
                   if (numtabla = 13 OR numtabla = 14) then
                        -- Subvenciones ENESA (13) o CCAA (14)
                        PR_COPIA_TEMP_SUBVENCIONES(v_table, v_lineaseguroid);
                   end if;

                   -- Se continua con el proceso normalmente para insertar
                   if (idtabla = 8) then
                       deletecondpl ('TB_SC_C_GRUPO_TASAS_RIESGO', v_lineaseguroid);
                       deletecondpl ('TB_SC_C_GRUPO_TASAS', v_lineaseguroid);
                   elsif (idtabla <> 9) then
                       deletecondpl (TRIM (v_table), v_lineaseguroid);
                   end if;
               END IF;
            END IF;

            IF (v_table = 'TB_SC_C_TARIFAS' OR v_table = 'TB_SC_C_PRIMAS_RIESGO' OR v_table = 'TB_SC_C_GRUPOS_SUBV' OR v_table = 'TB_SC_C_SUBV_GRUPOS')
            THEN
               deletecondpl (TRIM (v_table), v_plan);
            END IF;
         END IF;

         IF (v_table = 'TB_SC_C_GRUPO_TASAS_RIESGO') THEN
            insertar := FN_hayRegGrpTasaRiesgo(xml_canonico);
         END IF;

         IF (mismopl AND insertar AND v_ficherocanonico <> 'Canonico.xml')
         THEN
            --Abrir el contexto
            v_context := DBMS_XMLSTORE.newcontext (targettable => v_table);
            --Para General y plan linea, marcamos las columnas a insertar
            DBMS_XMLSTORE.clearupdatecolumnlist (ctxhdl => v_context);

            FOR columna IN c_columnas
            LOOP
               DBMS_XMLSTORE.setupdatecolumn (ctxhdl       => v_context,
                                              colname      => columna.column_name);
            END LOOP;

            v_rows := DBMS_XMLSTORE.insertxml (ctxhdl      => v_context,
                                               xdoc        => xml_canonico);

            IF (v_table = 'TB_SC_C_CULTIVOS') THEN
               pr_cargatbcultivos;
            END IF;

            IF (v_table = 'TB_SC_C_VARIEDADES') THEN
               pr_cargatbvariedades (v_lineaseguroid);
            END IF;

            IF (v_table = 'TB_SC_C_LINEAS') THEN
               pr_carga_lineas ();
            END IF;

            IF (v_table = 'TB_SC_C_MODULOS') THEN
               pr_carga_modulos (v_lineaseguroid);
            END IF;

            IF (v_table = 'TB_SC_C_SISTEMA_PRODUCCION') THEN
               pr_cargaSistProdDefecto;
            END IF;

            IF (v_table = 'TB_SC_C_COD_DENOM_ORIGEN') THEN
               pr_cargaDenomOrigenDefecto (v_lineaseguroid);
            END IF;

            --Se cierra el contexto
            DBMS_XMLSTORE.closecontext (ctxhdl => v_context);

            --Subvenciones: después se actualizan los ids de las tablas relacionadas
            if (numtabla = 13) then
               -- Subvenciones ENESA
               PR_ACTUALIZA_IDS_SUBV_ENESA(v_table, v_lineaseguroid);
            end if;
            if (numtabla = 14) then
               -- Subvenciones CCAA
               PR_ACTUALIZA_IDS_SUBV_CCAA(v_table, v_lineaseguroid);
            end if;

            --Insertamos la importacion correcta
            IF (idtabla != 9) THEN
               INSERT INTO tb_importacion_tablas VALUES (idhistorico, numtabla, 'Importado', NULL, fichtablas);
            END IF;

         ELSIF (mismopl AND insertar AND v_ficherocanonico = 'Canonico.xml') THEN
             -- Insertamos mediante select en la tabla externa correspondiente
             -- Abrimos el fichero
             if (numtabla = 129) then
                 v_sql := 'INSERT INTO ' || v_table || ' SELECT * FROM TBX_SC_C_VALORES_ESPECIE_AMB';
             else
                 v_sql := 'INSERT INTO ' || v_table || ' SELECT * FROM TBX_' || substr(v_table, 4);
             end if;

             -- ejecutamos la sentencia
             pq_utl.log('Inserción con tabla externa: ' || v_sql || ' --', 2);
             execute immediate v_sql;
             pq_utl.log('Fin de la inserción en ' || v_table || ' --', 2);

             -- Si es la tabla de líneas, cargamos las líneas "por defecto"
             IF (v_table = 'TB_SC_C_LINEAS') THEN
                  pr_carga_lineas ();
             END IF;

             --borramos los asegurados autorizados dia
             IF (v_table = 'TB_SC_C_ASEG_AUTORIZADOS') THEN
                 pq_utl.log('Actualizando TB_ASEGURADOS_AUTORIZADOS_DIA  --', 2);
                 PQ_CHECK_ASEG_AUTORIZADOS.PR_CARGA_ASEG_AUTORIZADOS_DIA;
             end if;

             --Insertamos la importación correcta
             INSERT INTO tb_importacion_tablas VALUES (idhistorico, numtabla, 'Importado', NULL, fichtablas);
         ELSIF (not insertar) THEN
               --No se insertan registros
               pq_utl.LOG ('FN_XML2TABLE - ' || v_table || ' --- NO HAY REGISTROS PARA INSERTAR ---', 2);
         ELSE
            --Aqui s¿entraria si estamos con un fichero del CPL
            --y no coincide el PL del fichero con el introducido para la importacion
            IF (idtabla != 9)
            THEN
               INSERT INTO tb_importacion_tablas VALUES (idhistorico, numtabla, 'No coincide el Plan Linea', NULL, fichtablas);
            --COMMIT;
            END IF;
         END IF;

         pr_actualiza_subtermino (v_table);

      EXCEPTION
         WHEN OTHERS
         THEN
            ROLLBACK;
            err_num := SQLCODE;
            err_msg := SQLERRM;

            err_cnt := 0;

            IF (err_num = -21560)
            THEN
               mensaje_error := SQLCODE || ' - Fichero original corrupto';
            ELSE
               mensaje_error := SQLCODE || ' ' || SQLERRM;
            END IF;

            pq_utl.LOG ('FN_XML2TABLE - ' || v_table || ' - ERROR: ' || mensaje_error || ' -', 2);

            BEGIN
               --Insertamos el error de la importación
                IF (idTabla = 9 AND numTabla = 8) THEN
                    execute immediate 'select count(*) from TB_IMPORTACION_TABLAS where IDHISTORICO = ' || idHistorico || ' AND CODTABLACONDICIONADO = 8' into err_cnt;
                    pq_utl.log('Número de registros en importacion_tablas con idhistorico=' || idHistorico || ' and codtablacondicionado = 8 -' || err_cnt || ' ---', 2);
                    IF (err_cnt > 0) then
                       pq_utl.log('es update -', 2);
                       execute immediate 'select descestado from TB_IMPORTACION_TABLAS where IDHISTORICO = ' || idHistorico || ' AND CODTABLACONDICIONADO = 8' into err_anterior;
                       UPDATE TB_IMPORTACION_TABLAS SET estado = 'Error', descestado = err_anterior || '**' || SUBSTR(mensaje_error, 0, 998)  WHERE IDHISTORICO = idHistorico AND CODTABLACONDICIONADO = 8;
                    ELSE
                       pq_utl.log('es insert -', 2);
                       INSERT INTO TB_IMPORTACION_TABLAS VALUES (idHistorico, numTabla, 'Error', SUBSTR(mensaje_error, 0, 1000), FICHTABLAS);
                    END IF;
                elsif (idTabla = 8 AND numTabla = 8) THEN
                    pq_utl.log('es insert con id 8 y numero 8 -', 2);
                    execute immediate 'select count(*) from TB_IMPORTACION_TABLAS where IDHISTORICO = ' || idHistorico || ' AND CODTABLACONDICIONADO = 8' into err_cnt;
                    pq_utl.log('Número de registros en importacion_tablas con idhistorico=' || idHistorico || 'and codtablacondicionado=8 -' || err_cnt || ' -', 2);
                    IF (err_cnt > 0) then
                       execute immediate 'select descestado from TB_IMPORTACION_TABLAS where IDHISTORICO = ' || idHistorico || ' AND CODTABLACONDICIONADO = 8' into err_anterior;
                       UPDATE TB_IMPORTACION_TABLAS SET estado = 'Error', descestado = err_anterior || '**' || SUBSTR(mensaje_error, 0, 998)  WHERE IDHISTORICO = idHistorico AND CODTABLACONDICIONADO = 8;
                    else
                        INSERT INTO TB_IMPORTACION_TABLAS VALUES (idHistorico, numTabla, 'Error', SUBSTR(mensaje_error, 0, 1000), FICHTABLAS);
                    end if;
                ELSE
                    INSERT INTO TB_IMPORTACION_TABLAS VALUES (idHistorico, numTabla, 'Error', SUBSTR(mensaje_error, 0, 2000), FICHTABLAS);
                END IF;
            --COMMIT;
            EXCEPTION
               WHEN OTHERS
               THEN
                   err_num := SQLCODE;
                   err_msg := SQLERRM;
                   pq_utl.LOG ('FN_XML2TABLE - Error al actualizar el estado - ' || v_table || ' - ERROR: ' ||
                               err_num || ' - ' || err_msg || ' ---', 2);
                  RETURN TRUE;
            END;

            RETURN TRUE;
      END;

      RETURN error;
   END;

   PROCEDURE PR_COPIA_TEMP_SUBVENCIONES(v_table IN VARCHAR2, v_lineaseguroid IN NUMBER) IS
        v_consulta_subv      varchar2(2000);
   BEGIN
        -- 1. Vaciar la tabla temporal
        truncate_table(v_table || '_TEMP');
        -- 2. Insertar los registros del plan/linea
        v_consulta_subv := 'INSERT INTO ' || v_table || '_TEMP SELECT * FROM ' || v_table || ' WHERE LINEASEGUROID = ' || v_lineaseguroid;
        execute immediate v_consulta_subv;
   END;

   PROCEDURE PR_ACTUALIZA_IDS_SUBV_ENESA(v_table IN VARCHAR2, v_lineaseguroid IN NUMBER) IS
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
      --Fin de variables auxiliares para las subvenciones
   BEGIN
        PQ_UTL.log('Entramos en el método que actualiza los ids de las subvenciones enesa de las tablas relacionadas  ', 2);
        -- 1. bucle para recoger el id nuevo y el antiguo y actualizar
        v_consulta_subv := 'SELECT * FROM ' || v_table || '_TEMP';
        OPEN c_import_subv for v_consulta_subv;
        LOOP
            FETCH c_import_subv INTO SUBV_ID_ANTERIOR, SUBV_LINEASEGUROID, SUBV_CODMODULO,
                SUBV_CODTIPORDTO, SUBV_CODCULTIVO, SUBV_CODVARIEDAD, SUBV_CODPROVINCIA, SUBV_CODCOMARCA,
                SUBV_CODTERMINO, SUBV_SUBTERMINO, SUBV_CODTIPOSUBV, SUBV_CODGARANTIZADO, SUBV_RATIO,
                SUBV_CODBASECALCULOSUBV, SUBV_DATOASOCBASE, SUBV_PCTSUBVINDIVIDUAL,
                SUBV_PCTSUBVCOLECTIVO, SUBV_CODCONCEPTO, SUBV_VALORCONCEPTO;
            EXIT WHEN c_import_subv%NOTFOUND;
            BEGIN
                v_consulta_idsubv := 'SELECT ID FROM ' || v_table || ' WHERE lineaseguroid = ' ||
                    SUBV_LINEASEGUROID || ' AND codmodulo = ''' || SUBV_CODMODULO || ''' AND CODTIPORDTO = ' ||
                    SUBV_CODTIPORDTO || /*' AND CODCULTIVO = ' || SUBV_CODCULTIVO ||*/ ' AND CODVARIEDAD = ' ||
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
                    --Base calculo subvencion
                    IF (SUBV_CODBASECALCULOSUBV IS NULL) THEN
                        v_consulta_idsubv := v_consulta_idsubv || ' AND CODBASECALCULOSUBV IS NULL';
                    ELSE
                        v_consulta_idsubv := v_consulta_idsubv || ' AND CODBASECALCULOSUBV = ''' || SUBV_CODBASECALCULOSUBV || '''';
                    END IF;

                    --PARA QUEDARNOS SÓLO CON EL PRIMER REGISTRO
                    v_consulta_idsubv := v_consulta_idsubv || ' AND ROWNUM = 1';

                    execute immediate v_consulta_idsubv into SUBV_ID_ACTUAL;
                    PQ_UTL.log('ID_ACTUAL de la subvencion ' || SUBV_ID_ANTERIOR || ' = ' || SUBV_ID_ACTUAL || '   ', 2);
                    if (SUBV_ID_ACTUAL is not null) then
                        --PQ_UTL.log('Actualizando los ids de las subvenciones enesa de socios  ', 2);
                        UPDATE TB_SUBV_ENESA_SOCIOS SET IDSUBVENESA = SUBV_ID_ACTUAL WHERE LINEASEGUROID = v_lineaseguroid AND IDSUBVENESA = SUBV_ID_ANTERIOR;
                        --PQ_UTL.log('Actualizando los ids de las subvenciones enesa de parcelas  ', 2);
                        UPDATE TB_SUBV_PARCELA_ENESA SET IDSUBVENESA = SUBV_ID_ACTUAL WHERE LINEASEGUROID = v_lineaseguroid AND IDSUBVENESA = SUBV_ID_ANTERIOR;
                        --PQ_UTL.log('Actualizando los ids de las subvenciones enesa de asegurados  ', 2);
                        UPDATE TB_SUBVS_ASEG_ENESA SET IDSUBVENCION = SUBV_ID_ACTUAL WHERE LINEASEGUROID = v_lineaseguroid AND IDSUBVENCION = SUBV_ID_ANTERIOR;
                        --PQ_UTL.log('Ids actualizados con exito  ', 2);
                    end if;

            EXCEPTION
                WHEN OTHERS THEN
                    err_num := SQLCODE;
                    err_msg := SQLERRM;
                    PQ_UTL.log('FN_XML2TABLE - ERROR Actualizando las subvenciones: ' || err_num || ' - ' || err_msg || ' ****', 2);
            END;
        END LOOP;

        PQ_UTL.log('Fin del método que actualiza los ids de las subvenciones enesa de las tablas relacionadas  ', 2);
   END;

   PROCEDURE PR_ACTUALIZA_IDS_SUBV_CCAA(v_table IN VARCHAR2, v_lineaseguroid IN NUMBER) IS
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
      --Fin de variables auxiliares para las subvenciones
   BEGIN
        PQ_UTL.log('Entramos en el método que actualiza los ids de las subvenciones CCAA de las tablas relacionadas  ', 2);
        -- 1. bucle para recoger el id nuevo y el antiguo y actualizar
        v_consulta_subv := 'SELECT * FROM ' || v_table || '_TEMP';
        OPEN c_import_subv for v_consulta_subv;
        LOOP
            FETCH c_import_subv INTO SUBV_ID_ANTERIOR, SUBV_LINEASEGUROID, SUBV_CODORGANISMO, SUBV_CODMODULO,
                SUBV_CODTIPORDTO, SUBV_CODCULTIVO, SUBV_CODVARIEDAD, SUBV_CODPROVINCIA, SUBV_CODCOMARCA,
                SUBV_CODTERMINO, SUBV_SUBTERMINO, SUBV_CODTIPOSUBV, SUBV_CODGARANTIZADO, SUBV_TASACOSTE,
                SUBV_CODBASECALCULOSUBV, SUBV_DATOASOCBASE, SUBV_PCTSUBVINDIVIDUAL,
                SUBV_PCTSUBVCOLECTIVO, SUBV_CODCONCEPTO, SUBV_VALORCONCEPTO;
            EXIT WHEN c_import_subv%NOTFOUND;
            BEGIN
                v_consulta_idsubv := 'SELECT ID FROM ' || v_table || ' WHERE lineaseguroid = ' ||
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
                    PQ_UTL.log('ID_ACTUAL de la subvencion ' || SUBV_ID_ANTERIOR || ' = ' || SUBV_ID_ACTUAL || '   ', 2);
                    if (SUBV_ID_ACTUAL is not null) then
                        PQ_UTL.log('Actualizando los ids de las subvenciones CCAA de las tablas relacionadas  ', 2);
                        UPDATE TB_SUBV_PARCELA_CCAA SET IDSUBVCCAA = SUBV_ID_ACTUAL WHERE LINEASEGUROID = v_lineaseguroid AND IDSUBVCCAA = SUBV_ID_ANTERIOR;
                        UPDATE TB_SUBVS_ASEG_CCAA SET IDSUBVENCION = SUBV_ID_ACTUAL WHERE LINEASEGUROID = v_lineaseguroid AND IDSUBVENCION = SUBV_ID_ANTERIOR;
                    end if;

            EXCEPTION
                WHEN OTHERS THEN
                    err_num := SQLCODE;
                    err_msg := SQLERRM;
                    PQ_UTL.log('FN_XML2TABLE - ERROR Actualizando las subvenciones: ' || err_num || ' - ' || err_msg || ' ****', 2);
            END;
        END LOOP;

        PQ_UTL.log('Fin del método que actualiza los ids de las subvenciones CCAA de las tablas relacionadas  ', 2);
   END;


   FUNCTION fn_compruebapl (xml_canonico IN XMLTYPE, v_lineaseguroid IN NUMBER)
      RETURN BOOLEAN
   IS
      retorno         BOOLEAN                 := TRUE;
      lineaseguro     NUMBER (15);
      docdefinitivo   DBMS_XMLDOM.domdocument;
      docelem         DBMS_XMLDOM.domelement;
      nodelist        DBMS_XMLDOM.domnodelist;
      node            DBMS_XMLDOM.domnode;
      childnode       DBMS_XMLDOM.domnode;

   BEGIN
      --Recogemos el valor de LINEASEGUROID del XML transformado
      docdefinitivo := DBMS_XMLDOM.newdomdocument (xml_canonico);
      --Buscamos el nodo...
      docelem := DBMS_XMLDOM.getdocumentelement (docdefinitivo);
      -- Access element
      nodelist := DBMS_XMLDOM.getelementsbytagname (docelem, 'LINEASEGUROID');
      node := DBMS_XMLDOM.item (nodelist, 0);
      childnode := DBMS_XMLDOM.getfirstchild (node);
      -- Manipulate element
      lineaseguro := DBMS_XMLDOM.getnodevalue (childnode);

      IF (v_lineaseguroid != lineaseguro)
      THEN
         retorno := FALSE;
      END IF;

      RETURN retorno;
   END fn_compruebapl;

   FUNCTION FN_hayRegGrpTasaRiesgo (xml_canonico IN XMLTYPE)
      RETURN BOOLEAN
   IS
      docdefinitivo   DBMS_XMLDOM.domdocument;
      docelem         DBMS_XMLDOM.domelement;
      nodelist        DBMS_XMLDOM.domnodelist;
      nodos           NUMBER;
   BEGIN
      --Recogemos el valor de LINEASEGUROID del XML transformado
      docdefinitivo := DBMS_XMLDOM.newdomdocument (xml_canonico);
      --Buscamos el nodo...
      docelem := DBMS_XMLDOM.getdocumentelement (docdefinitivo);
      -- Access element
      nodelist := DBMS_XMLDOM.getelementsbytagname (docelem, 'ROW');
      nodos := DBMS_XMLDOM.getlength (nodelist);
      -- Manipulate element
      IF (nodos > 0) THEN
         RETURN TRUE;
      ELSE
          RETURN FALSE;
      END IF;
   END FN_hayRegGrpTasaRiesgo;

   PROCEDURE updateorganizador (tabla IN VARCHAR2, lineaSeguroId IN NUMBER)
   IS
      sentenciaupdate   VARCHAR2 (1000);
   BEGIN
      --sentenciaUpdate :='UPDATE '||tabla||' SET FECHA_BAJA = SYSDATE WHERE FECHA_BAJA IS NULL';
      sentenciaUpdate :='DELETE '||tabla;
      IF (tabla = 'TB_SC_OI_REL_USOS_TABLAS' OR tabla = 'TB_SC_OI_ORG_INFO') THEN
         sentenciaupdate := sentenciaupdate || ' WHERE LINEASEGUROID = ' || lineaSeguroId;
      END IF;
      EXECUTE IMMEDIATE sentenciaUpdate;
      --COMMIT;
   END updateorganizador;

   PROCEDURE deletecondgeneral (tabla IN VARCHAR2, v_linea IN NUMBER)
   IS
      sentenciadelete   VARCHAR2 (1000);
      tablaupdate       VARCHAR2 (30)   := tabla;
      v_codfamilia        TB_SC_C_ZONIF_SIGPAC.codfamiliazonif%TYPE;

   BEGIN
      if (tablaupdate = 'TB_SC_C_ZONIF_SIGPAC') then
          select CODFAMILIA INTO v_codfamilia from tb_sc_c_zonif_familias_det t where t.codlinea = v_linea;
          sentenciadelete := 'DELETE ' || tablaupdate || ' WHERE CODFAMILIAZONIF = ''' || v_codfamilia || '''';
      else
          sentenciadelete := 'DELETE ' || tablaupdate;
      end if;

      EXECUTE IMMEDIATE sentenciadelete;

      IF (tablaupdate = 'TB_SC_C_TIPOS_RDTO')
      THEN
         INSERT INTO tb_sc_c_tipos_rdto
              VALUES (-9, 'Generico');
      END IF;

      IF (tablaupdate = 'TB_SC_C_MARCO_PLANTACION') THEN
           INSERT INTO TB_SC_C_MARCO_PLANTACION VALUES (-9, 'Genérico');
       END IF;

       IF (tablaupdate = 'TB_SC_C_SISTEMA_CULTIVO') THEN
          INSERT INTO TB_SC_C_SISTEMA_CULTIVO VALUES (0, 'SIN VALOR EN FACTOR');
       END IF;
   END deletecondgeneral;

   PROCEDURE deletecondpl (tabla IN VARCHAR2, planlineaid NUMBER)
   IS
      sentenciadelete   VARCHAR2 (1000);
      tablaupdate       VARCHAR2 (30)   := tabla;

   BEGIN
      IF (tablaupdate = 'TB_TERMINOS')
      THEN
         sentenciadelete := 'DELETE ' || tablaupdate;
      ELSIF (tablaupdate = 'TB_SC_C_TARIFAS' OR tablaupdate = 'TB_SC_C_PRIMAS_RIESGO')
      THEN
         sentenciadelete :=
               'DELETE ' || tablaupdate || ' WHERE CODPLAN = ' || planlineaid;
      ELSIF (tablaupdate = 'TB_SC_C_GRUPOS_SUBV' OR tablaupdate = 'TB_SC_C_SUBV_GRUPOS')
      THEN
         sentenciadelete :=
               'DELETE ' || tablaupdate || ' WHERE PLAN = ' || planlineaid;
      ELSE
         sentenciadelete :=
            'DELETE ' || tablaupdate || ' WHERE LINEASEGUROID = '
            || planlineaid;
      END IF;

      PQ_UTL.log('deletecondpl: ' || sentenciadelete || '***', 2);

      EXECUTE IMMEDIATE sentenciadelete;

   END deletecondpl;

   PROCEDURE pr_cargavaloresnueve (lineaseguro IN NUMBER)
   IS
      existemodulo   NUMBER;
   BEGIN
      pr_cargaprovincias;
      pr_cargatbcomarcas;
      pr_cargatbcultivos;
      pr_cargatbvariedades (lineaseguro);

      --Cargamos el valor 99999 'Todos los modulos' para la linea seguro importada
      SELECT COUNT (*)
        INTO existemodulo
        FROM tb_sc_c_modulos
       WHERE lineaseguroid = lineaseguro AND codmodulo = '99999';

      IF (existemodulo = 0)
      THEN
         INSERT INTO tb_sc_c_modulos
              VALUES (lineaseguro, '99999', 'Todos los modulos', 'P', null, null);
      --COMMIT;
      END IF;

   END pr_cargavaloresnueve;

   PROCEDURE pr_carga_modulos (lineaseguro IN NUMBER)
   IS
      existemodulo   NUMBER;
   BEGIN
      --Cargamos el valor 99999 'Todos los modulos' para la linea seguro importada
      SELECT COUNT (*)
        INTO existemodulo
        FROM tb_sc_c_modulos
       WHERE lineaseguroid = lineaseguro AND codmodulo = '99999';

      IF (existemodulo = 0)
      THEN
         INSERT INTO tb_sc_c_modulos
              VALUES (lineaseguro, '99999', 'Todos los modulos', 'P', null, null);
      --COMMIT;
      END IF;
   END pr_carga_modulos;

   PROCEDURE pr_cargaprovincias
   IS
      contador   NUMBER (2) := 0;
   BEGIN
      --Insertamos la comunidad 99
      SELECT COUNT (*)
        INTO contador
        FROM tb_comunidades
       WHERE codcomunidad = 99;

      IF (contador = 0)
      THEN
         INSERT INTO tb_comunidades
              VALUES (99, 'Todas');
      END IF;

      --Insertamos la provincia 99
      SELECT COUNT (*)
        INTO contador
        FROM tb_provincias
       WHERE codprovincia = 99 AND codcomunidad = 99;

      IF (contador = 0)
      THEN
         INSERT INTO tb_provincias
                     (codprovincia, codcomunidad, nomprovincia
                     )
              VALUES (99, 99, 'Todas'
                     );
      END IF;

      --Llamamos al metodo para insertar los terminos 999 para cada provincia
      pr_cargatbcomarcas;
      --pr_cargaterminos;
   --COMMIT;
   END pr_cargaprovincias;

   PROCEDURE pr_cargatbcomarcas
   IS
      CURSOR c1
      IS
         SELECT DISTINCT (codprovincia)
                    FROM TB_PROVINCIAS;

      provincia   tb_provincias.codprovincia%TYPE;
      contador   NUMBER (2) := 0;
   BEGIN


     OPEN c1;

     FETCH c1
      INTO provincia;

     WHILE c1%FOUND
     LOOP
        SELECT COUNT (*) INTO contador FROM tb_comarcas WHERE codprovincia = provincia AND codcomarca = 99;
        IF (contador = 0) THEN
           INSERT INTO tb_comarcas
                    (codcomarca, codprovincia, nomcomarca
                    )
             VALUES (99, provincia, 'Todas'
                    );
        END IF;

        pr_cargaterminos(provincia);

        FETCH c1
         INTO provincia;
     END LOOP;
      CLOSE c1;

   END pr_cargatbcomarcas;

   --Método para cargar los terminos en función de la provincia y la comarca
   PROCEDURE pr_cargaterminos(
             provincia   IN   tb_provincias.codprovincia%TYPE)
   IS
      --cursor para recorrer las provincias
      CURSOR c1
      IS
         SELECT DISTINCT (codcomarca)
                    FROM tb_comarcas where codprovincia = provincia;

      comarca   tb_comarcas.codcomarca%TYPE;
      contador   NUMBER (2) := 0;
   BEGIN
      OPEN c1;

      FETCH c1
       INTO comarca;

      WHILE c1%FOUND
      LOOP
         SELECT COUNT (*) INTO contador FROM tb_terminos
                WHERE codprovincia = provincia AND codtermino = 999 and subtermino = '9' and codcomarca = comarca;
         IF (contador = 0) THEN

         INSERT INTO tb_terminos
                     (codprovincia, codtermino, subtermino, codcomarca, nomtermino)
                  VALUES (provincia, 999, '9', comarca, 'Todos');

                  --Ahora queda insertar un subtérmino 9 por cada término de la comarca y la provincia provincia actual
                  pr_cargasubterminos (provincia, comarca);
          end if;

         FETCH c1
          INTO comarca;
      END LOOP;

      CLOSE c1;
   END pr_cargaterminos;

--Método para insertar los subtérminos genéricos para una provincia y un término
   PROCEDURE pr_cargasubterminos (
      provincia   IN   tb_provincias.codprovincia%TYPE,
      comarca     IN   tb_comarcas.codcomarca%TYPE
   )
   IS
      CURSOR c2
      IS
         SELECT DISTINCT (codtermino)
                    FROM tb_terminos
                   WHERE codprovincia = provincia and codcomarca = comarca;

      termino    tb_terminos.codtermino%TYPE;
      contador   NUMBER (2) := 0;
   BEGIN
      OPEN c2;

      FETCH c2
       INTO termino;

      WHILE c2%FOUND
      LOOP
         SELECT COUNT (*)
           INTO contador
           FROM tb_terminos
          WHERE codprovincia = provincia
            AND codtermino = termino
            AND codcomarca = comarca
            AND subtermino = '9';

         IF (contador = 0)
         THEN
            INSERT INTO tb_terminos
                        (codprovincia, codtermino, subtermino, codcomarca, nomtermino
                        )
                 VALUES (provincia, termino, '9', comarca, 'Todos'
                        );
         END IF;
         PQ_UTL.log('Llamada a pr_cargasubtermino_no9('||provincia||', '||termino||', '||comarca||')-----');
         pr_cargasubtermino_no9(provincia, termino, comarca);
         FETCH c2
          INTO termino;
      END LOOP;
      pr_cargacomarcas9;
      CLOSE c2;
   END pr_cargasubterminos;

   PROCEDURE pr_cargacomarcas9 IS
   TYPE cur_type IS REF CURSOR;
   cTerms cur_type;
   provincia   TB_TERMINOS.CODPROVINCIA%TYPE;
   termino     TB_TERMINOS.CODTERMINO%TYPE;
   subtermino  TB_TERMINOS.SUBTERMINO%TYPE;
      contador   NUMBER (2) := 0;
   BEGIN
        OPEN cTerms for SELECT codprovincia, codtermino, subtermino FROM TB_TERMINOS WHERE codcomarca <> 99;
        LOOP
            FETCH cTerms INTO provincia, termino, subtermino;
            EXIT WHEN cTerms%NOTFOUND;
            SELECT COUNT (*) INTO contador FROM tb_terminos
                   WHERE codprovincia = provincia AND codtermino = termino AND codcomarca = 99
                   AND subtermino = subtermino;

             IF (contador = 0)
             THEN
                 INSERT INTO TB_TERMINOS (CODPROVINCIA, CODTERMINO, SUBTERMINO, CODCOMARCA, NOMTERMINO) VALUES
                   (provincia, termino, subtermino, 99, 'Todos');
             end if;
        END LOOP;
   END pr_cargacomarcas9;

   procedure pr_cargasubtermino_no9(
             provincia   IN   tb_provincias.codprovincia%TYPE,
             termino     IN   tb_terminos.codtermino%type,
             comarca     IN   tb_comarcas.codcomarca%TYPE) IS
      CURSOR c2
      IS
         SELECT DISTINCT (subtermino)
                    FROM tb_terminos
                   WHERE codprovincia = provincia and codcomarca = comarca and codtermino = termino;

      sub_termino    tb_terminos.subtermino%TYPE;
      contador   NUMBER (2) := 0;
   begin
        OPEN c2;

      FETCH c2
       INTO sub_termino;

      WHILE c2%FOUND
      LOOP
         SELECT COUNT (*)
           INTO contador
           FROM tb_terminos
          WHERE codprovincia = provincia
            AND codtermino = termino
            AND codcomarca = comarca
            AND subtermino = sub_termino;

         IF (contador = 0)
         THEN
            INSERT INTO tb_terminos
                        (codprovincia, codtermino, subtermino, codcomarca, nomtermino
                        )
                 VALUES (provincia, termino, sub_termino, comarca, 'Todos'
                        );
         END IF;

         FETCH c2
          INTO sub_termino;
      END LOOP;
   end;

   PROCEDURE pr_cargatbcultivos
   IS
      CURSOR c1
      IS
         SELECT DISTINCT lineaseguroid
                    FROM tb_sc_c_cultivos;

      lineaseguro   NUMBER (15);
      contador      NUMBER (2)  := 0;
   BEGIN
      OPEN c1;

      FETCH c1
       INTO lineaseguro;

      WHILE c1%FOUND
      LOOP
         SELECT COUNT (*)
           INTO contador
           FROM tb_sc_c_cultivos
          WHERE lineaseguroid = lineaseguro AND codcultivo = 999;

         IF (contador = 0)
         THEN
            INSERT INTO tb_sc_c_cultivos
                 VALUES (lineaseguro, 999, 'Todos');
         END IF;

         FETCH c1
          INTO lineaseguro;
      END LOOP;

      CLOSE c1;
   --COMMIT;
   END pr_cargatbcultivos;

   PROCEDURE pr_cargatbvariedades (lineaseguro IN NUMBER)
   IS
      CURSOR c2 (vlinea NUMBER)
      IS
         SELECT DISTINCT (codcultivo)
                    FROM tb_sc_c_cultivos
                   WHERE lineaseguroid = vlinea;

      codcultivo   NUMBER (3);
      contador     NUMBER (2) := 0;
   BEGIN
      OPEN c2 (lineaseguro);

      FETCH c2
       INTO codcultivo;

      WHILE c2%FOUND
      LOOP
         INSERT INTO tb_sc_c_variedades
              VALUES (lineaseguro, codcultivo, 999, 'Todas');

         FETCH c2
          INTO codcultivo;
      END LOOP;

      CLOSE c2;

      SELECT COUNT (*)
        INTO contador
        FROM tb_sc_c_variedades
       WHERE lineaseguroid = lineaseguro
         AND codcultivo = 999
         AND codvariedad = 999;

      IF (contador = 0)
      THEN
         INSERT INTO tb_sc_c_variedades
              VALUES (lineaseguro, 999, 999, 'Todos');
      END IF;
   END pr_cargatbvariedades;

   --Procedimiento para cargar la línea genérica y actualizar la descripción
   PROCEDURE pr_carga_lineas
   IS
      CURSOR c1
      IS
         SELECT codgruposeguro
           FROM tb_sc_c_grupo_seguro;

      CURSOR c2 IS SELECT CODLINEA FROM TB_SC_C_LINEAS;

      gruposeguro   VARCHAR2 (3);
      contador      NUMBER;
      descLinea     tb_sc_c_lineas.deslinea%TYPE;
      linea         tb_sc_c_lineas.Codlinea%TYPE;
   BEGIN
      OPEN c1;

      FETCH c1
       INTO gruposeguro;

      WHILE c1%FOUND
      LOOP
         contador := 0;

         SELECT COUNT (*)
           INTO contador
           FROM tb_sc_c_lineas
          WHERE codlinea = 999 AND codgruposeguro = gruposeguro;

         IF (contador = 0)
         THEN
            INSERT INTO tb_sc_c_lineas
                 VALUES (999, 'Todas las líneas', gruposeguro);

            COMMIT;
         END IF;

         FETCH c1
          INTO gruposeguro;
      END LOOP;
      CLOSE c1;

      OPEN c2;
      FETCH c2 INTO linea;
      WHILE c2%FOUND
      LOOP
          --Actualizamos la descripción del plan/línea en TB_LINEAS
          SELECT DESLINEA INTO descLinea FROM TB_SC_C_LINEAS WHERE CODLINEA = linea;
          UPDATE TB_LINEAS SET NOMLINEA = descLinea WHERE CODLINEA = linea;

          FETCH c2 INTO linea;
      END LOOP;
      CLOSE c2;

   END pr_carga_lineas;

   FUNCTION dameficheroxtabla (ficherotabla IN t_array, idtabla NUMBER)
      RETURN VARCHAR2
   IS
      nombrefichero   VARCHAR2 (30);
      relfich         t_array;
      tablas          t_array;
   --tablaCadena VARCHAR2(3);
   BEGIN
      --tablaCadena := TO_CHAR(idTabla);
      FOR i IN ficherotabla.FIRST .. ficherotabla.LAST
      LOOP
         relfich := fn_split (ficherotabla (i), ';');
         tablas := fn_split (relfich (2), ',');

         FOR j IN tablas.FIRST .. tablas.LAST
         LOOP
            IF (idtabla = TO_NUMBER (tablas (j)))
            THEN
               nombrefichero := relfich (1);
            END IF;
         END LOOP;
      END LOOP;

      RETURN nombrefichero;
   END dameficheroxtabla;

   FUNCTION fn_split (cadena IN VARCHAR2, separador IN CHAR)
      RETURN t_array
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
               DBMS_OUTPUT.put_line (   'Linea '
                                     || (i + 1)
                                     || ', valor: '
                                     || strings (i + 1)
                                    );
            END IF;
         END LOOP;
      END IF;

      -- return array
      RETURN strings;
   END fn_split;

   PROCEDURE pr_insertaerror (
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
        FROM tb_lineas
       WHERE codlinea = linea AND codplan = PLAN;

      IF (LENGTH (error) > 2000)
      THEN
         mensaje := SUBSTR (error, 0, 2000);
      ELSE
         mensaje := error;
      END IF;

      INSERT INTO tb_hist_importaciones
                  (idhistorico, idtipoimportacion, fechaimport,
                   estado, lineaseguroid, descerror)
           VALUES (sq_hist_importaciones.NEXTVAL, tipoimportacion, SYSDATE,
                   'Error', linsegid, mensaje);

      COMMIT;
   END pr_insertaerror;

   PROCEDURE pr_actualiza_subtermino (tabla IN VARCHAR2)
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
   END pr_actualiza_subtermino;

   PROCEDURE PR_CREAPANTALLACONFIGURABLE (LINEASEGUROPARAM IN NUMBER)
   IS
     idPantallaVar NUMBER;
     idPantallaConfigurableVar NUMBER;
     consultaVariables VARCHAR2(2000) := 'select dd.codconcepto, dd.nomconcepto, DD.ETIQUETAXML,
                                          DD.CODTIPONATURALEZA, dd.numtabla, dd.multiple, dd.longitud
                                          from tb_sc_oi_ubicaciones ub, tb_sc_oi_org_info oi, tb_sc_dd_dic_datos dd, tb_sc_oi_usos usos
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
	 v_gruposeguro o02agpe0.TB_PANTALLAS_CONFIGURABLES.gruposeguro%TYPE;
	 v_obligatoria o02agpe0.TB_PANTALLAS_CONFIGURABLES.obligatoria%TYPE;
   BEGIN
        SELECT IDPANTALLA INTO idPantallaVar FROM TB_PANTALLAS WHERE OBJETOPANTALLA = 'POLIZA';
        SELECT SQ_PANTALLAS_CONFIGURABLES.NEXTVAL INTO idPantallaConfigurableVar FROM DUAL;
        BEGIN
			-- BUSCAMOS LA PANTALLA DEL PLAN ANTERIOR
			SELECT p.GRUPOSEGURO, p.OBLIGATORIA
			INTO v_gruposeguro, v_obligatoria
			FROM o02agpe0.TB_PANTALLAS_CONFIGURABLES p
			INNER JOIN o02agpe0.TB_LINEAS l2
				ON l2.LINEASEGUROID = LINEASEGUROPARAM
			INNER JOIN o02agpe0.TB_LINEAS l1
				ON l1.CODLINEA = l2.CODLINEA
				AND l1.CODPLAN = (l2.CODPLAN - 1)
				AND l1.LINEASEGUROID = p.LINEASEGUROID
			WHERE p.IDPANTALLA = idPantallaVar;
		EXCEPTION
			WHEN NO_DATA_FOUND THEN
				-- SI NO EXISTE PONEMOS VALORES POR DEFECTO: A01 - Agrícola / G01 - Ganado
				SELECT lc.codgruposeguro				
				INTO v_gruposeguro
				FROM o02agpe0.TB_SC_C_LINEAS lc
				INNER JOIN o02agpe0.TB_LINEAS l
					ON l.LINEASEGUROID = LINEASEGUROPARAM 
					AND lc.codlinea = l.codlinea;
				v_obligatoria := 1;
		END;
        INSERT INTO TB_PANTALLAS_CONFIGURABLES VALUES (idPantallaConfigurableVar, LINEASEGUROPARAM, idPantallaVar, v_gruposeguro, v_obligatoria);
        OPEN c_cursor FOR consultaVariables;
        FETCH c_cursor INTO codConceptoVar, nomConceptoVar, etiquetaXmlVar, tipoNaturalezaVar, numTablaVar, multipleVar, longitudVar;
        WHILE c_cursor%FOUND
        LOOP
          IF (multipleVar = 'N' AND numTablaVar = 0) THEN
           --Texto
           IF (codConceptoVar = 134) THEN
              numTablaVar := 3;
              INSERT INTO TB_CONFIGURACION_CAMPOS
              VALUES (idPantallaConfigurableVar, 1, LINEASEGUROPARAM, 16, codConceptoVar, 31, nomConceptoVar, 6, x, y, longitudVar, 20, 'N', 'N', numTablaVar, NULL, NULL);
              y := y+ 30;
           ELSE
             INSERT INTO TB_CONFIGURACION_CAMPOS
             VALUES (idPantallaConfigurableVar, 1, LINEASEGUROPARAM, 16, codConceptoVar, 31, nomConceptoVar, 1, x, y, longitudVar, 20, 'N', 'N', NULL, NULL, NULL);
             y := y+ 30;
           END IF;
          ELSE
               IF (multipleVar = 'N' AND numTablaVar != 0) THEN
                  --Combo
                  numTablaVar := FN_DAMEORIGENDATOS(numTablaVar);
                  INSERT INTO TB_CONFIGURACION_CAMPOS
                  VALUES (idPantallaConfigurableVar, 1, LINEASEGUROPARAM, 16, codConceptoVar, 31, nomConceptoVar, 6, x, y, longitudVar, 20, 'N', 'N', numTablaVar, NULL, NULL);
                  y := y+ 30;
               ELSE
                   IF (multipleVar = 'S' AND numTablaVar != 0) THEN
                      numTablaVar := FN_DAMEORIGENDATOS(numTablaVar);
                      --Select multiple
                      INSERT INTO TB_CONFIGURACION_CAMPOS
                      VALUES (idPantallaConfigurableVar, 1, LINEASEGUROPARAM, 16, codConceptoVar, 31, nomConceptoVar, 5, x, y, longitudVar, 20, 'N', 'N', numTablaVar, NULL, NULL);
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

   PROCEDURE pr_cargaSistProdDefecto
   IS
     cnt NUMBER;
   BEGIN
        execute immediate 'select count(*) from TB_SC_C_SISTEMA_PRODUCCION where CODSISTEMAPRODUCCION = 0' into cnt;
        IF (cnt <= 0) then
           INSERT INTO TB_SC_C_SISTEMA_PRODUCCION VALUES (0, 'SIN VALOR');
        END IF;
   END pr_cargaSistProdDefecto;

   PROCEDURE pr_cargaDenomOrigenDefecto (lineaseguroid in number)
   IS
     cnt NUMBER;
   BEGIN
        execute immediate 'select count(*) from TB_SC_C_COD_DENOM_ORIGEN where CODDENOMORIGEN = 0 AND LINEASEGUROID = ' || lineaseguroid || ' ' into cnt;
        IF (cnt <= 0) then
           INSERT INTO TB_SC_C_COD_DENOM_ORIGEN VALUES (lineaseguroid, 0, 'SIN VALOR');
        END IF;
   END pr_cargaDenomOrigenDefecto;

END pq_importacion;
/
SHOW ERRORS;