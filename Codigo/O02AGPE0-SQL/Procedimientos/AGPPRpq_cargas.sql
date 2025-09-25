CREATE OR REPLACE PACKAGE O02AGPE0.pq_cargas IS

	PROCEDURE pr_carga_pac(p_fichero       VARCHAR2
												,p_cod_usuario  VARCHAR2
												,p_codplan      NUMBER
                        ,p_ent_med      NUMBER
                        ,p_subent_med      NUMBER
                        ,p_error        OUT VARCHAR2);

END pq_cargas;
/
CREATE OR REPLACE PACKAGE BODY O02AGPE0.pq_cargas IS

  log BOOLEAN := FALSE; -- Indica si en la ejecución del proceso se pintarán logs de debug o no

  -- Indica cada cuantas líneas procesadas se escribirá en el log la línea que se está procesando actualmente
  v_num_lineas_prc NUMBER(5) := 10000;

  -- Número mínimo de caracteres que debe contener la línea leida del fichero para que se procese
  v_tam_linea_minmo NUMBER(3) := 124;

  -- Mapa para insertar los asegurados ya procesados y evitar duplicados en la tabla de asegurados
  TYPE MapaAsegurados IS TABLE OF NUMBER INDEX BY o02agpe0.tb_pac_asegurados.nif_asegurado%TYPE;
	mapaAsegProcesados   MapaAsegurados;

  --
  -- Abre en modo lectura el fichero con el nombre indicado en 'p_fichero' y devuelve
  -- la variable para poder acceder a él
  --
  FUNCTION abrirFichero (p_fichero in VARCHAR2, p_file out utl_file.file_type) RETURN BOOLEAN is

  lc VARCHAR2(30) := 'PQ_CARGAS.abrirFichero - ';

  BEGIN

    -- Abre para lectura el fichero a cargar
    -- AGP_INTERFACES (EN DESARROLLO), AGP_BATCH (EN TEST/PROD) !!!!!!!!!!!!!!!!!!!!!!!
    p_file := utl_file.fopen(location     => 'AGP_BATCH', filename     => p_fichero, open_mode    => 'r', max_linesize => 32767);
    RETURN TRUE;

    EXCEPTION

       WHEN OTHERS THEN
           PQ_UTL.log(lc, 'ERROR -- SQLCODE:' || SQLCODE || ' SQLERRM: ' || SQLERRM ||'    ', 2);
           RETURN FALSE;

  END abrirFichero;
  --------------------------------------------------------------------------------------


  --
  -- Comprueba que en la tabla TB_SUBENTIDADES_MEDIADORAS existe algún registro que relacione la entidad
  -- con la entidad y subentidad mediadora indicadas
  --
  FUNCTION compruebaESMedEntidad (entidad IN NUMBER, ent_med IN NUMBER, subent_med IN NUMBER) RETURN BOOLEAN is

  lc VARCHAR2(50) := 'PQ_CARGAS.compruebaESMedEntidad - ';
  v_count NUMBER :=0;

  BEGIN

       SELECT COUNT(*) INTO v_count from TB_SUBENTIDADES_MEDIADORAS sm where sm.codentidad=ent_med and sm.codsubentidad=subent_med and sm.codentidadnomediadora=entidad;

       RETURN v_count >0;

  EXCEPTION

       WHEN OTHERS THEN
           PQ_UTL.log(lc, 'ERROR -- SQLCODE:' || SQLCODE || ' SQLERRM: ' || SQLERRM ||'    ', 2);
           RETURN FALSE;

  END compruebaESMedEntidad;
  --------------------------------------------------------------------------------------

  --
  -- Función para leer datos de tipo varchar de una línea de texto. Recibe la linea, la posicion inicial del número
  -- en la línea y el número de caracteres a leer para componer el número
  --
  -- Posibles valores de retorno:
  --              null : El campo no viene informado ya que todas las posiciones del fichero asignadas al campo contienen espacios en blanco (no es un error)
  -- valor alfanumérico: El campo viene informado y es correcto
  --
  FUNCTION leeCadena (P_LINEA IN VARCHAR2, P_POSICION IN NUMBER, P_CARACTERES IN NUMBER) RETURN VARCHAR2 IS

  lc VARCHAR2(50) := 'PQ_CARGAS.leeCadena - ';
  v_aux VARCHAR2(100) := null;

  BEGIN

      -- Comprueba si la línea contiene las posiciones que se quieren leer
      IF (LENGTH (P_LINEA) + 1 < P_POSICION + P_CARACTERES) THEN
         RETURN NULL;
      END IF;

      v_aux := TRIM(substr(P_LINEA, P_POSICION, P_CARACTERES));

      IF (LENGTH (v_aux) = 0) THEN
         RETURN NULL;
      END IF;

      RETURN v_aux;

  EXCEPTION
       WHEN OTHERS THEN
            IF (log) THEN PQ_UTL.log(lc, 'ERROR -- SQLCODE:' || SQLCODE || ' SQLERRM: ' || SQLERRM ||'    ', 2); END IF;
            RETURN NULL;

  END leeCadena;
  --------------------------------------------------------------------------------------


  --
  -- Función para leer datos numéricos (obligatorios) de una línea de texto. Recibe la linea, la posicion inicial del número
  -- en la línea y el número de caracteres a leer para componer el número
  --
  -- Posibles valores de retorno:
  --              -1 : El campo viene informado pero ha ocurrido un error al obtener el valor numérico (error que cancela la carga de fichero de PAC)
  --            null : El campo no viene informado ya que todas las posiciones del fichero asignadas al campo contienen espacios en blanco (no es un error)
  -- valor numérico: El campo viene informado y es correcto
  --
  FUNCTION leeNumeroObligatorio (P_LINEA IN VARCHAR2, P_POSICION IN NUMBER, P_CARACTERES IN NUMBER) RETURN NUMBER IS

  lc VARCHAR2(50) := 'PQ_CARGAS.leeNumero - ';
  v_aux VARCHAR2(100) := null;

  BEGIN

      v_aux := leeCadena (P_LINEA, P_POSICION, P_CARACTERES);

      IF (v_aux IS NULL) THEN
         RETURN NULL;
      END IF;

      RETURN to_number(v_aux);

  EXCEPTION
       WHEN OTHERS THEN
            IF (log) THEN PQ_UTL.log(lc, 'ERROR -- SQLCODE:' || SQLCODE || ' SQLERRM: ' || SQLERRM ||'    ', 2); END IF;
            RETURN -1;

  END leeNumeroObligatorio;
  --------------------------------------------------------------------------------------

  --
  -- Función para leer datos numéricos (no obligatorios) de una línea de texto. Recibe la linea, la posicion inicial del número
  -- en la línea y el número de caracteres a leer para componer el número
  --
  -- Posibles valores de retorno:
  --            null : El campo no viene informado o el valor no es numérico
  -- valor numérico: El campo viene informado y es correcto
  --
  FUNCTION leeNumero (P_LINEA IN VARCHAR2, P_POSICION IN NUMBER, P_CARACTERES IN NUMBER) RETURN NUMBER IS

  lc VARCHAR2(50) := 'PQ_CARGAS.leeNumero - ';

  BEGIN

      RETURN to_number (leeCadena (P_LINEA, P_POSICION, P_CARACTERES));

  EXCEPTION
       WHEN OTHERS THEN
            IF (log) THEN PQ_UTL.log(lc, 'ERROR -- SQLCODE:' || SQLCODE || ' SQLERRM: ' || SQLERRM ||'    ', 2); END IF;
            RETURN -1;

  END leeNumero;
  --------------------------------------------------------------------------------------



  --
  -- Función para leer datos de tipo DATE (en formato DDMMYYYY) de una línea de texto. Recibe la linea, la posicion inicial del número
  -- en la línea y el número de caracteres a leer para componer el número
  --
  -- Posibles valores de retorno:
  --              FALSE : El campo viene informado pero ha ocurrido un error al convertirlo en fecha
  --              TRUE: El campo viene informado y es una fecha correcta o no viene informado
  --              v_fecha : En esta variable de salida se devuelve el valor de la fecha si es correcta o null si no viene informada o es errónea
  --
  FUNCTION leeFecha (P_LINEA IN VARCHAR2, P_POSICION IN NUMBER, P_CARACTERES IN NUMBER) RETURN DATE IS

  lc VARCHAR2(50) := 'PQ_CARGAS.leeNumero - ';

  BEGIN

      RETURN (TO_DATE (leeCadena (P_LINEA, P_POSICION, P_CARACTERES), 'DDMMYYYY'));

  EXCEPTION
       WHEN OTHERS THEN
            IF (log) THEN PQ_UTL.log(lc, 'ERROR -- SQLCODE:' || SQLCODE || ' SQLERRM: ' || SQLERRM ||'    ', 2); END IF;
            RETURN NULL;

  END leeFecha;
  --------------------------------------------------------------------------------------

  --
  -- Inserta los datos relativos a la carga en BD y devuelve el identificador de la carga
  --
  FUNCTION registrarCarga(p_fichero in VARCHAR2, p_cod_usuario in VARCHAR2, p_codplan in NUMBER, p_ent_med in NUMBER, p_subent_med in NUMBER, v_codlin in tb_pac_cargas.linea%TYPE, v_codent in tb_pac_cargas.entidad%TYPE) RETURN NUMBER IS

    v_id_carga NUMBER := NULL;
    v_clob     CLOB := NULL;
    f_lob      BFILE;

    lc VARCHAR2(50) := 'PQ_CARGAS.registrarCarga - ';

  begin

    -- Obtiene el identificador de la carga, el cual se usará para borrar todos los datos relacionadas con la
    -- carga en el caso de que ésta sea incorrecta
    SELECT sq_pac_cargas.NEXTVAL INTO v_id_carga FROM dual;


    -- Inserta el registro con la información de la carga en TB_PAC_CARGAS
    INSERT INTO
           TB_PAC_CARGAS (ID, PLAN, LINEA, ENTIDAD, ENT_MED, SUBENT_MED, NOMBRE_FICHERO, USUARIO, FECHA_CARGA)
       VALUES
           (v_id_carga, p_codplan, v_codlin, v_codent, p_ent_med, p_subent_med, p_fichero, p_cod_usuario, SYSDATE);

    -- Inserta el registro con el contenido del fichero a cargar en TB_PAC_CARGAS_FICHERO
    INSERT INTO TB_PAC_CARGAS_FICHERO  (ID_CARGA, FICHERO) VALUES (v_id_carga, empty_clob());

    -- Copia el contenido del fichero al campo TB_PAC_CARGAS_FICHERO.FICHERO
    SELECT FICHERO INTO v_clob FROM TB_PAC_CARGAS_FICHERO WHERE ID_CARGA = v_id_carga;
    -- AGP_INTERFACES (EN DESARROLLO), AGP_BATCH (EN TEST/PROD) !!!!!!!!!!!!!!!!!!!!!!!
    f_lob := bfilename('AGP_BATCH', p_fichero);
    dbms_lob.fileopen(f_lob, dbms_lob.file_readonly);
    dbms_lob.loadfromfile(v_clob, f_lob, dbms_lob.getlength(f_lob));
    dbms_lob.fileclose(f_lob);
    COMMIT;
    dbms_lob.filecloseall; --cerrar todos los ficheros

    RETURN v_id_carga;

  EXCEPTION
       WHEN OTHERS THEN
            PQ_UTL.log(lc, 'ERROR -- SQLCODE:' || SQLCODE || ' SQLERRM: ' || SQLERRM ||'    ', 2);
            RETURN NULL;

  end registrarCarga;
  --------------------------------------------------------------------------------------

  --
  -- Lee la siguiente línea del fichero indicado por el parámetro 'v_file'
  -- Devuelve el contador actualizado que contiene el número de línea actual (v_num_linea) y un booleano
  -- indicando si la línea leida tiene el tamaño mínimo
  --
  FUNCTION leeLinea (v_file IN utl_file.file_type, v_num_linea IN OUT NUMBER, v_tam_ok OUT BOOLEAN) RETURN VARCHAR2 IS

  lc VARCHAR2(50) := 'PQ_CARGAS.leeLinea - ';
  v_linea    VARCHAR2(32767) := NULL;

  BEGIN

      utl_file.get_line(v_file, v_linea);
      -- Aumenta el contador de número de línea
      v_num_linea := v_num_linea + 1;
      -- Comprueba si el tamaño de la línea leída supera el mínimo
      v_tam_ok := (LENGTH (v_linea) >= v_tam_linea_minmo);

      return v_linea;

  EXCEPTION
      WHEN NO_DATA_FOUND THEN
          PQ_UTL.log(lc, 'Fin de fichero', 2);
          RETURN NULL;
      WHEN OTHERS THEN
          PQ_UTL.log(lc, 'ERROR -- SQLCODE:' || SQLCODE || ' SQLERRM: ' || SQLERRM ||'    ', 2);
          RETURN NULL;
  END leeLinea;
  --------------------------------------------------------------------------------------

  --
  -- Lee la línea del fichero y obtiene los códigos de línea, entidad y nif/cif del asegurado
  --
  PROCEDURE obtieneDatosCarga(v_file in out utl_file.file_type,
                              v_linea out VARCHAR2,
                              v_num_linea in out NUMBER,
                              v_codlin in out tb_pac_cargas.linea%TYPE,
                              v_codent in out tb_pac_cargas.entidad%TYPE,
                              v_cifnifasegurado in out tb_pac_asegurados.nif_asegurado%TYPE,
                              v_plan in NUMBER,
                              v_lineaseguroid OUT tb_lineas.lineaseguroid%TYPE,
                              v_tam_ok OUT BOOLEAN) is

  lc VARCHAR2(50) := 'PQ_CARGAS.obtieneDatosCarga - ';

  BEGIN
    v_linea := leeLinea (v_file, v_num_linea, v_tam_ok);

    v_codent := leeNumero (v_linea, 1, 4);
    v_codlin := substr(v_linea, 5, 3);
    v_cifnifasegurado := substr(v_linea, 8, 9);

    -- Obtiene el lineaseguroid asociado al plan recibido como parámetro y al código de línea leido del fichero
    SELECT LINEASEGUROID INTO v_lineaseguroid FROM TB_LINEAS L WHERE L.CODPLAN=v_plan AND L.CODLINEA=TO_NUMBER(v_codlin);

  EXCEPTION
      WHEN OTHERS THEN
          PQ_UTL.log(lc, 'ERROR -- SQLCODE:' || SQLCODE || ' SQLERRM: ' || SQLERRM ||'    ', 2);
          RETURN;

  END obtieneDatosCarga;
  --------------------------------------------------------------------------------------

  --
  -- Eliminar de la tabla TB_PAC_CARGAS la carga asociada al identificador pasado como parámetro
  --
  PROCEDURE eliminarCarga (p_id_carga IN tb_pac_cargas.id%TYPE) IS

  lc VARCHAR2(50) := 'PQ_CARGAS.eliminarCarga - ';

  BEGIN

       DELETE TB_PAC_CARGAS WHERE ID = p_id_carga;
       COMMIT;

  EXCEPTION
      WHEN OTHERS THEN
          PQ_UTL.log(lc, 'ERROR -- SQLCODE:' || SQLCODE || ' SQLERRM: ' || SQLERRM ||'    ', 2);
          RETURN;

  END;
  --------------------------------------------------------------------------------------


  --
  -- Inserta en TB_PAC_ASEGURADOS el registro correspondiente a los datos pasados como parámetro
  --
	PROCEDURE insertarAsegurado(v_linea VARCHAR2, v_cifnifasegurado in tb_pac_asegurados.nif_asegurado%TYPE,
                                   p_id_carga IN tb_pac_asegurados.id_carga%TYPE, p_id_pac_aseg  OUT tb_pac_asegurados.id%TYPE) IS

	  lc VARCHAR2(50) := 'PQ_CARGAS.insertarAsegurado - ';
		v_discriminanteasegurad tb_pac_asegurados.discriminante%TYPE := NULL;
    asegYaProcesado BOOLEAN := FALSE;

	BEGIN

  -- --
  -- MPM - SIGPE 8971 -
  -- Antes de insertar el asegurado en la tabla se comprueba si ya se ha procesado anteriormente (si existe en el mapa de ids de
  -- asegurado indexado por nif); en caso de que no exista, se inserta en la tabla y en el mapa
  -- --
  BEGIN
    -- Si el asegurado ya se ha procesado con anterioridad
    IF mapaAsegProcesados.FIRST is not null AND mapaAsegProcesados(v_cifnifasegurado) is not null THEN
       p_id_pac_aseg := mapaAsegProcesados(v_cifnifasegurado);
       asegYaProcesado := TRUE;
    END IF;

    EXCEPTION WHEN NO_DATA_FOUND THEN
        -- El asegurado no se ha procesado anteriormente
        asegYaProcesado := FALSE;
  END;


  -- Si el asegurado no se ha procesado anteriormente
  IF asegYaProcesado = FALSE THEN

      -- Obtiene el valor del campo 'Discriminante' de la línea
      v_discriminanteasegurad := leeCadena (v_linea, 17, 1);

	    -- Obtiene el identificador del registro de PAC Asegurados
      SELECT sq_pac_asegurados.NEXTVAL INTO p_id_pac_aseg	FROM dual;

      -- Inserta el registro en la tabla TB_PAC_ASEGURADOS
      INSERT INTO TB_PAC_ASEGURADOS
      	(ID, ID_CARGA, NIF_ASEGURADO, DISCRIMINANTE)
      VALUES
      	(p_id_pac_aseg, p_id_carga, v_cifnifasegurado, v_discriminanteasegurad);

     -- Inserta el dato en el mapa de asegurados para no volver a procesarlo
     mapaAsegProcesados(v_cifnifasegurado) := p_id_pac_aseg;

  END IF;

	EXCEPTION
      WHEN OTHERS THEN
          PQ_UTL.log(lc, 'ERROR -- SQLCODE:' || SQLCODE || ' SQLERRM: ' || SQLERRM ||'    ', 2);
          p_id_pac_aseg := NULL;
          RETURN;

	END;
  --------------------------------------------------------------------------------------


  FUNCTION getMsgObligatorio (v_nom_campo IN VARCHAR2) RETURN VARCHAR2 IS

  BEGIN
       RETURN 'El campo ''' || v_nom_campo || ''' no está informado';
  END;

  FUNCTION getMsgErrorNumerico (v_nom_campo IN VARCHAR2) RETURN VARCHAR2 IS

  BEGIN
       RETURN 'El valor del campo ''' || v_nom_campo || ''' no es correcto';
  END;

  FUNCTION getMsgErrorFecha (v_nom_campo IN VARCHAR2) RETURN VARCHAR2 IS

  BEGIN
       RETURN 'El valor del campo ''' || v_nom_campo || ''' no es una fecha en formato DDMMYYYY';
  END;


  --
  -- Comprueba que el cultivo y la variedad pasados como parámetros existen en la tabla TB_SC_C_VARIEDADES para el plan/línea indicado
  --
  FUNCTION validaCultivoVariedad (p_codCultivo IN NUMBER, p_codVariedad IN NUMBER, p_lineaseguroid IN tb_lineas.lineaseguroid%TYPE) RETURN BOOLEAN IS

  v_count NUMBER := 0;
  lc VARCHAR2(50) := 'PQ_CARGAS.validaCultivoVariedad - ';

  BEGIN

       SELECT COUNT(*) INTO v_count FROM TB_SC_C_VARIEDADES V WHERE V.LINEASEGUROID=p_lineaseguroid AND V.CODCULTIVO=p_codCultivo AND V.CODVARIEDAD=p_codVariedad;

       IF (v_count > 0) THEN
          RETURN TRUE;
       END IF;

       RETURN FALSE;

  EXCEPTION
      WHEN OTHERS THEN
          PQ_UTL.log(lc, 'ERROR -- SQLCODE:' || SQLCODE || ' SQLERRM: ' || SQLERRM ||'    ', 2);
          RETURN FALSE;

  END;

  --
  -- Inserta en TB_PAC_PARCELAS el registro correspondiente a la línea pasada como parámetro
  --
  PROCEDURE insertarParcela (v_linea VARCHAR2, p_id_pac_aseg IN tb_pac_asegurados.id%TYPE,
                             p_id_pac_parcela OUT tb_pac_parcelas.id%TYPE, p_lineaseguroid IN tb_lineas.lineaseguroid%TYPE,
                             p_error OUT VARCHAR2) IS

  lc VARCHAR2(50) := 'PQ_CARGAS.insertarParcela - ';

  v_provincia  TB_PAC_PARCELAS.PROVINCIA%TYPE := NULL;
  v_comarca  TB_PAC_PARCELAS.COMARCA%TYPE := NULL;
  v_termino  TB_PAC_PARCELAS.TERMINO%TYPE := NULL;
  v_subtermino  TB_PAC_PARCELAS.SUBTERMINO%TYPE := NULL;
  v_poligono  TB_PAC_PARCELAS.POLIGONO%TYPE := NULL;
  v_parcela  TB_PAC_PARCELAS.PARCELA%TYPE := NULL;
  v_nombre  TB_PAC_PARCELAS.NOMBRE%TYPE := NULL;
  v_cultivo  TB_PAC_PARCELAS.CULTIVO%TYPE := NULL;
  v_variedad  TB_PAC_PARCELAS.VARIEDAD%TYPE := NULL;
  v_opcion  TB_PAC_PARCELAS.OPCION%TYPE := NULL;
  v_ajuste  TB_PAC_PARCELAS.AJUSTE%TYPE := NULL;
  v_codorg  TB_PAC_PARCELAS.CODORG%TYPE := NULL;
  v_prov_sigpac  TB_PAC_PARCELAS.PROVINCIA_SIGPAC%TYPE := NULL;
  v_term_sigpac  TB_PAC_PARCELAS.TERMINO_SIGPAC%TYPE := NULL;
  v_agre_sigpac   TB_PAC_PARCELAS.AGREGADO_SIGPAC%TYPE := NULL;
  v_zona_sigpac  TB_PAC_PARCELAS.ZONA_SIGPAC%TYPE := NULL;
  v_poli_sigpac   TB_PAC_PARCELAS.POLIGONO_SIGPAC%TYPE := NULL;
  v_parc_sigpac  TB_PAC_PARCELAS.PARCELA_SIGPAC%TYPE := NULL;
  v_reci_sigpac  TB_PAC_PARCELAS.RECINTO_SIGPAC%TYPE := NULL;
  v_r_cub_eleg  TB_PAC_PARCELAS.R_CUB_ELEG%TYPE := NULL;

  BEGIN

  -- Obtiene los valores de cada campo del fichero
  v_provincia :=  leeNumeroObligatorio (v_linea, 18, 2);
  v_comarca :=    leeNumeroObligatorio (v_linea, 20, 2);
  v_termino :=    leeNumeroObligatorio (v_linea, 22, 3);
  v_subtermino := substr (v_linea, 25, 1); -- Para el subtérmino el espacio en blanco es un valor correcto
  --
  -- Estos campos no se van a tratar por un cambio solicitado por RGA
  -- v_poligono :=   leeNumero (v_linea, 26, 3);
  -- v_parcela :=    leeNumero (v_linea, 29, 5);
  --
  v_nombre :=     leeCadena (v_linea, 34, 15);
  v_cultivo :=    leeNumeroObligatorio (v_linea, 49, 3);
  v_variedad :=   leeNumeroObligatorio (v_linea, 52, 3);
  v_opcion :=     leeCadena (v_linea, 55, 1);
  v_ajuste :=     leeNumero (v_linea, 64, 4);
  v_codorg :=     leeNumero (v_linea, 68, 2);
  v_prov_sigpac:= leeNumeroObligatorio (v_linea, 102, 2);
  v_term_sigpac:= leeNumeroObligatorio (v_linea, 104, 3);
  v_agre_sigpac:= leeNumeroObligatorio (v_linea, 107, 3);
  v_zona_sigpac:= leeNumeroObligatorio (v_linea, 110, 2);
  v_poli_sigpac:= leeNumeroObligatorio (v_linea, 112, 3);
  v_parc_sigpac:= leeNumeroObligatorio (v_linea, 115, 5);
  v_reci_sigpac:= leeNumeroObligatorio (v_linea, 120, 5);
  v_r_cub_eleg := leeNumero (v_linea, 183, 1);


  --
  -- Validaciones previas a la inserción del registro de parcela de PAC
  --
  -- Si un campo numérico o de fecha está identificado como obligatorio, valida que esté informado (NOT NULL) y que tenga
  -- un formato correcto (<> -1)
  -- Si un campo numérico o de fecha no está identificado como obligatorio, se insertará a NULL tanto si no está informado
  -- como si tiene un formato incorrecto
  --
  -- Ubicación (Obligatorios)
  IF (v_provincia IS NULL) THEN p_error := getMsgObligatorio ('Provincia'); RETURN; END IF;
  IF (v_provincia = -1) THEN p_error := getMsgErrorNumerico ('Provincia'); RETURN; END IF;
  IF (v_comarca IS NULL) THEN p_error := getMsgObligatorio ('Comarca'); RETURN; END IF;
  IF (v_comarca = -1) THEN p_error := getMsgErrorNumerico ('Comarca'); RETURN; END IF;
  IF (v_termino IS NULL) THEN p_error := getMsgObligatorio ('Término'); RETURN; END IF;
  IF (v_termino = -1) THEN p_error := getMsgErrorNumerico ('Término'); RETURN; END IF;
  IF (v_subtermino IS NULL) THEN p_error := getMsgObligatorio ('Subtérmino'); RETURN; END IF;
  --
  -- Cultivo/Variedad (Obligatorios)
  --
  IF (v_cultivo IS NULL) THEN p_error := getMsgObligatorio ('Cultivo'); RETURN; END IF;
  IF (v_cultivo = -1) THEN p_error := getMsgErrorNumerico ('Cultivo'); RETURN; END IF;
  IF (v_variedad IS NULL) THEN p_error := getMsgObligatorio ('Variedad'); RETURN; END IF;
  IF (v_variedad = -1) THEN p_error := getMsgErrorNumerico ('Variedad'); RETURN; END IF;
  --
  -- SIGPAC (Obligatorios)
  --
  IF (v_prov_sigpac IS NULL) THEN p_error := getMsgObligatorio ('Provincia SIGPAC'); RETURN; END IF;
  IF (v_prov_sigpac = -1) THEN p_error := getMsgErrorNumerico ('Provincia SIGPAC'); RETURN; END IF;
  IF (v_term_sigpac IS NULL) THEN p_error := getMsgObligatorio ('Término SIGPAC'); RETURN; END IF;
  IF (v_term_sigpac = -1) THEN p_error := getMsgErrorNumerico ('Término SIGPAC'); RETURN; END IF;
  IF (v_agre_sigpac IS NULL) THEN p_error := getMsgObligatorio ('Agregado SIGPAC'); RETURN; END IF;
  IF (v_agre_sigpac = -1) THEN p_error := getMsgErrorNumerico ('Agregado SIGPAC'); RETURN; END IF;
  IF (v_zona_sigpac IS NULL) THEN p_error := getMsgObligatorio ('Zona SIGPAC'); RETURN; END IF;
  IF (v_zona_sigpac = -1) THEN p_error := getMsgErrorNumerico ('Zona SIGPAC'); RETURN; END IF;
  IF (v_poli_sigpac IS NULL) THEN p_error := getMsgObligatorio ('Polígono SIGPAC'); RETURN; END IF;
  IF (v_poli_sigpac = -1) THEN p_error := getMsgErrorNumerico ('Polígono SIGPAC'); RETURN; END IF;
  IF (v_parc_sigpac IS NULL) THEN p_error := getMsgObligatorio ('Parcela SIGPAC'); RETURN; END IF;
  IF (v_parc_sigpac = -1) THEN p_error := getMsgErrorNumerico ('Parcela SIGPAC'); RETURN; END IF;
  IF (v_reci_sigpac IS NULL) THEN p_error := getMsgObligatorio ('Recinto SIGPAC'); RETURN; END IF;
  IF (v_reci_sigpac = -1) THEN p_error := getMsgErrorNumerico ('Recinto SIGPAC'); RETURN; END IF;
  --


  -- Valida que el cultivo y la variedad existe para el plan/línea de la carga en la tabla TB_SC_C_VARIEDADES
  IF (validaCultivoVariedad (v_cultivo, v_variedad, p_lineaseguroid) = FALSE) THEN
     p_error := 'El cultivo ' || v_cultivo || ' y la variedad ' || v_variedad || ' no existen para el plan/línea indicado';
     RETURN;
  END IF;

  -- Obtiene el identificador del registro de parcela de PAC
  SELECT sq_pac_parcelas.NEXTVAL INTO p_id_pac_parcela	FROM dual;

  -- Los datos son correctos, se inserta el registro de parcela de PAC
  INSERT INTO TB_PAC_PARCELAS
         (ID, ID_PAC_ASEG, PROVINCIA, COMARCA, TERMINO, SUBTERMINO, POLIGONO, PARCELA, NOMBRE, CULTIVO, VARIEDAD,
         OPCION, AJUSTE, CODORG, PROVINCIA_SIGPAC, TERMINO_SIGPAC, AGREGADO_SIGPAC, ZONA_SIGPAC, POLIGONO_SIGPAC,
         PARCELA_SIGPAC, RECINTO_SIGPAC, R_CUB_ELEG)
  VALUES
         (p_id_pac_parcela, p_id_pac_aseg, v_provincia, v_comarca, v_termino, v_subtermino, v_poligono,
          v_parcela, v_nombre, v_cultivo, v_variedad, v_opcion, v_ajuste, v_codorg, v_prov_sigpac,
          v_term_sigpac, v_agre_sigpac, v_zona_sigpac, v_poli_sigpac, v_parc_sigpac,
          v_reci_sigpac, v_r_cub_eleg);


  EXCEPTION
      WHEN OTHERS THEN
          PQ_UTL.log(lc, 'ERROR -- SQLCODE:' || SQLCODE || ' SQLERRM: ' || SQLERRM ||'    ', 2);
          p_id_pac_parcela := NULL;
          p_error := 'Ha ocurrido un error al insertar el registro de parcela de PAC';
          RETURN;

  END;
  --------------------------------------------------------------------------------------


  --
  -- Inserta en TB_PAC_CAPITALES_ASEGURADOS el registro correspondiente a la línea pasada como parámetro
  --
  PROCEDURE insertarCapitalAsegurado (v_linea VARCHAR2, p_id_pac_aseg IN tb_pac_asegurados.id%TYPE,
                             p_id_pac_parcela IN tb_pac_parcelas.id%TYPE, p_error OUT VARCHAR2) IS

  lc VARCHAR2(50) := 'PQ_CARGAS.insertarCapitalAsegurado - ';

  v_superficie tb_pac_capitales_asegurados.superficie%TYPE;
  v_produccion tb_pac_capitales_asegurados.produccion%TYPE;

  BEGIN

  -- Obtiene los valores de cada campo del fichero
  v_superficie :=  leeNumero (v_linea, 56, 8);
  v_produccion :=  leeNumero (v_linea, 92, 10);

  -- Los datos son correctos, se inserta el registro de capital asegurado de PAC
  INSERT INTO TB_PAC_CAPITALES_ASEGURADOS
         (ID_PAC_PARCELA, ID_PAC_ASEG, SUPERFICIE, PRODUCCION)
  VALUES
         (p_id_pac_parcela, p_id_pac_aseg, v_superficie, v_produccion);

  EXCEPTION
      WHEN OTHERS THEN
          PQ_UTL.log(lc, 'ERROR -- SQLCODE:' || SQLCODE || ' SQLERRM: ' || SQLERRM ||'    ', 2);
          p_error := 'Ha ocurrido un error al insertar el registro de capital asegurado de PAC';
          RETURN;
  END;

  --------------------------------------------------------------------------------------


  PROCEDURE insertDVSql (p_id_pac_parcela IN tb_pac_parcelas.id%TYPE,
                         p_id_pac_aseg IN tb_pac_asegurados.id%TYPE,
                         p_codconcepto IN tb_pac_datos_variables.codconcepto%TYPE,
                         p_valor IN tb_pac_datos_variables.valor%TYPE) IS

  --lc VARCHAR2(50) := 'PQ_CARGAS.insertDVSql - ';

  BEGIN

  /*PQ_UTL.log(lc, 'Id PAC Aseg: ' || p_id_pac_aseg || ', Id PAC Parc: ' || p_id_pac_parcela
                 || ', Concepto: ' || p_codconcepto || ', Valor: ' || p_valor, 2);*/

  INSERT INTO TB_PAC_DATOS_VARIABLES VALUES (p_id_pac_parcela, p_id_pac_aseg, p_codconcepto, p_valor);

  END;


  --
  -- Inserta en TB_PAC_DATOS_VARIABLES los registros correspondientes a la línea pasada como parámetro
  --
  PROCEDURE insertarDatosVariables (v_linea VARCHAR2, p_id_pac_aseg IN tb_pac_asegurados.id%TYPE,
                             p_id_pac_parcela IN tb_pac_parcelas.id%TYPE, p_error OUT VARCHAR2) IS

  lc VARCHAR2(50) := 'PQ_CARGAS.insertarDatosVariables - ';

  v_num_arboles   NUMBER(6);  -- CODCONCEPTO 117
  v_num_cepas     NUMBER(6);  -- CODCONCEPTO 117
  v_fec_siembra   DATE;       -- CODCONCEPTO 113
  v_sis_cultivo   NUMBER(3);  -- CODCONCEPTO 123
  v_sis_producc   NUMBER(3);  -- CODCONCEPTO 616
  v_edad          NUMBER(3);  -- CODCONCEPTO 111
  v_destino       NUMBER(3);  -- CODCONCEPTO 110
  v_tip_plantac   NUMBER(3);  -- CODCONCEPTO 173
  v_pra_cultura   NUMBER(3);  -- CODCONCEPTO 133
  v_fec_fin_gar   DATE;       -- CODCONCEPTO 134
  v_sis_conducc   NUMBER(3);  -- CODCONCEPTO 131
  v_cic_cultivo   NUMBER(3);  -- CODCONCEPTO 618
  v_fec_recolec   DATE;       -- CODCONCEPTO 112
  v_tip_mar_pla   NUMBER(3);  -- CODCONCEPTO 116
  v_num_ani_pod   NUMBER(3);  -- CODCONCEPTO 617
  v_sis_protecc   NUMBER(3);  -- CODCONCEPTO 621
  v_rotacion      NUMBER(3);  -- CODCONCEPTO 144
  v_den_origen    NUMBER(3);  -- CODCONCEPTO 107
  v_igp           NUMBER(3);  -- CODCONCEPTO 765
  v_met_cuadrad   NUMBER(9);  -- CODCONCEPTO 767
  v_tip_instala   NUMBER(3);  -- CODCONCEPTO 778
  v_mat_cubiert   NUMBER(3);  -- CODCONCEPTO 873
  v_tip_terreno   NUMBER(3);  -- CODCONCEPTO 752
  v_tip_masa      NUMBER(3);  -- CODCONCEPTO 753
  v_pendiente     NUMBER(3);  -- CODCONCEPTO 754

  BEGIN

  -- Obtiene los valores de cada campo del fichero
  v_num_arboles   :=  leeNumero (v_linea, 70, 6);
  v_num_cepas     :=  leeNumero (v_linea, 76, 6);
  v_fec_siembra   :=  leeFecha (v_linea, 82, 8);
  v_sis_cultivo   :=  leeNumero (v_linea, 125, 3);
  v_sis_producc   :=  leeNumero (v_linea, 128, 3);
  v_edad          :=  leeNumero (v_linea, 131, 3);
  v_destino       :=  leeNumero (v_linea, 134, 3);
  v_tip_plantac   :=  leeNumero (v_linea, 137, 3);
  v_pra_cultura   :=  leeNumero (v_linea, 140, 3);
  v_fec_fin_gar   :=  leeFecha (v_linea, 143, 8);
  v_sis_conducc   :=  leeNumero (v_linea, 151, 3);
  v_cic_cultivo   :=  leeNumero (v_linea, 154, 3);
  v_fec_recolec   :=  leeFecha (v_linea, 157, 8);
  v_tip_mar_pla   :=  leeNumero (v_linea, 165, 3);
  v_num_ani_pod   :=  leeNumero (v_linea, 168, 3);
  v_sis_protecc   :=  leeNumero (v_linea, 171, 3);
  v_rotacion      :=  leeNumero (v_linea, 174, 3);
  v_den_origen    :=  leeNumero (v_linea, 177, 3);
  v_igp           :=  leeNumero (v_linea, 180, 3);
  v_met_cuadrad   :=  leeNumero (v_linea, 184, 9);
  v_tip_instala   :=  leeNumero (v_linea, 193, 3);
  v_mat_cubiert   :=  leeNumero (v_linea, 196, 3);
  v_tip_terreno   :=  leeNumero (v_linea, 199, 3);
  v_tip_masa      :=  leeNumero (v_linea, 202, 3);
  v_pendiente     :=  leeNumero (v_linea, 205, 3);


  -- Los datos son correctos, se insertan los registros informados en la tabla de datos variables de PAC
  -- Sólo vendrá informado uno de los DV (Núm. árboles o núm. cepas), por lo que controlamos que si se ha insertado
  -- uno el otro no se trata (ya que provocará un fallo de PK por compartir el mismo código de concepto)
  IF (v_num_arboles IS NOT NULL AND v_num_arboles > 0) THEN insertDVSql (p_id_pac_parcela, p_id_pac_aseg, 117, TO_CHAR (v_num_arboles)); END IF;
  IF (v_num_arboles IS NULL AND v_num_cepas IS NOT NULL AND v_num_cepas > 0) THEN insertDVSql (p_id_pac_parcela, p_id_pac_aseg, 117,  TO_CHAR (v_num_cepas  )); END IF;
  --
  IF (v_sis_cultivo IS NOT NULL) THEN insertDVSql (p_id_pac_parcela, p_id_pac_aseg, 123,  TO_CHAR (v_sis_cultivo)); END IF;
  IF (v_sis_producc IS NOT NULL) THEN insertDVSql (p_id_pac_parcela, p_id_pac_aseg, 616,  TO_CHAR (v_sis_producc)); END IF;
  IF (v_edad        IS NOT NULL) THEN insertDVSql (p_id_pac_parcela, p_id_pac_aseg, 111,  TO_CHAR (v_edad       )); END IF;
  IF (v_destino     IS NOT NULL) THEN insertDVSql (p_id_pac_parcela, p_id_pac_aseg, 110,  TO_CHAR (v_destino    )); END IF;
  IF (v_tip_plantac IS NOT NULL) THEN insertDVSql (p_id_pac_parcela, p_id_pac_aseg, 173,  TO_CHAR (v_tip_plantac)); END IF;
  IF (v_pra_cultura IS NOT NULL) THEN insertDVSql (p_id_pac_parcela, p_id_pac_aseg, 133,  TO_CHAR (v_pra_cultura)); END IF;
  --IF (v_fec_fin_gar IS NOT NULL) THEN insertDVSql (p_id_pac_parcela, p_id_pac_aseg, 134,  TO_CHAR (v_fec_fin_gar)); END IF;
  IF (v_sis_conducc IS NOT NULL) THEN insertDVSql (p_id_pac_parcela, p_id_pac_aseg, 131,  TO_CHAR (v_sis_conducc)); END IF;
  IF (v_cic_cultivo IS NOT NULL) THEN insertDVSql (p_id_pac_parcela, p_id_pac_aseg, 618,  TO_CHAR (v_cic_cultivo)); END IF;
  IF (v_fec_recolec IS NOT NULL) THEN insertDVSql (p_id_pac_parcela, p_id_pac_aseg, 112,  TO_CHAR (v_fec_recolec)); END IF;
  IF (v_tip_mar_pla IS NOT NULL) THEN insertDVSql (p_id_pac_parcela, p_id_pac_aseg, 116,  TO_CHAR (v_tip_mar_pla)); END IF;
  IF (v_num_ani_pod IS NOT NULL) THEN insertDVSql (p_id_pac_parcela, p_id_pac_aseg, 617,  TO_CHAR (v_num_ani_pod)); END IF;
  IF (v_sis_protecc IS NOT NULL) THEN insertDVSql (p_id_pac_parcela, p_id_pac_aseg, 621,  TO_CHAR (v_sis_protecc)); END IF;
  IF (v_rotacion    IS NOT NULL) THEN insertDVSql (p_id_pac_parcela, p_id_pac_aseg, 144,  TO_CHAR (v_rotacion   )); END IF;
  IF (v_den_origen  IS NOT NULL) THEN insertDVSql (p_id_pac_parcela, p_id_pac_aseg, 107,  TO_CHAR (v_den_origen )); END IF;
  IF (v_igp         IS NOT NULL) THEN insertDVSql (p_id_pac_parcela, p_id_pac_aseg, 765,  TO_CHAR (v_igp        )); END IF;
  IF (v_met_cuadrad IS NOT NULL) THEN insertDVSql (p_id_pac_parcela, p_id_pac_aseg, 767,  TO_CHAR (v_met_cuadrad)); END IF;
  IF (v_tip_instala IS NOT NULL) THEN insertDVSql (p_id_pac_parcela, p_id_pac_aseg, 778,  TO_CHAR (v_tip_instala)); END IF;
  IF (v_mat_cubiert IS NOT NULL) THEN insertDVSql (p_id_pac_parcela, p_id_pac_aseg, 873,  TO_CHAR (v_mat_cubiert)); END IF;
  IF (v_tip_terreno IS NOT NULL) THEN insertDVSql (p_id_pac_parcela, p_id_pac_aseg, 752,  TO_CHAR (v_tip_terreno)); END IF;
  IF (v_tip_masa    IS NOT NULL) THEN insertDVSql (p_id_pac_parcela, p_id_pac_aseg, 753,  TO_CHAR (v_tip_masa   )); END IF;
  IF (v_pendiente   IS NOT NULL) THEN insertDVSql (p_id_pac_parcela, p_id_pac_aseg, 754,  TO_CHAR (v_pendiente  )); END IF;
  -- Campos de fecha
  IF (v_fec_siembra   IS NOT NULL) THEN insertDVSql (p_id_pac_parcela, p_id_pac_aseg, 113,  TO_CHAR (v_fec_siembra, 'DD/MM/YYYY')); END IF;
  IF (v_fec_fin_gar   IS NOT NULL) THEN insertDVSql (p_id_pac_parcela, p_id_pac_aseg, 134,  TO_CHAR (v_fec_fin_gar, 'DD/MM/YYYY')); END IF;
  IF (v_fec_recolec   IS NOT NULL) THEN insertDVSql (p_id_pac_parcela, p_id_pac_aseg, 112,  TO_CHAR (v_fec_recolec, 'DD/MM/YYYY')); END IF;


  EXCEPTION
      WHEN OTHERS THEN
          PQ_UTL.log(lc, 'ERROR -- SQLCODE:' || SQLCODE || ' SQLERRM: ' || SQLERRM ||'    ', 2);
          p_error := 'Ha ocurrido un error al insertar los registros de datos variables de PAC';
          RETURN;
  END;




	/*PROCEDURE pr_insertar_parcelas(v_linea   VARCHAR2
																,p_id_aseg VARCHAR2
																,p_error   OUT VARCHAR2) IS


    err_num NUMBER;
    err_msg VARCHAR2(255);
		v_codprovincia        tb_pac_aseg_parcelas.codprovincia%TYPE := NULL;
		v_codcomarca          tb_pac_aseg_parcelas.codcomarca%TYPE := NULL;
		v_codtermino          tb_pac_aseg_parcelas.codtermino%TYPE := NULL;
		v_codsubtermino       tb_pac_aseg_parcelas.subtermino%TYPE := NULL;
		v_codpoligono         tb_pac_aseg_parcelas.poligono%TYPE := NULL;
		v_codparcela          tb_pac_aseg_parcelas.parcela%TYPE := NULL;
		v_nombreparcela       tb_pac_aseg_parcelas.nomparcela%TYPE := NULL;
		v_codcultivo          tb_pac_aseg_parcelas.cultivo%TYPE := NULL;
		v_codvariedad         tb_pac_aseg_parcelas.variedad%TYPE := NULL;
		v_codopcion           tb_pac_aseg_parcelas.opcion%TYPE := NULL;
		v_numhectareas        tb_pac_aseg_parcelas.numhectareas%TYPE := NULL;
		v_codajuste           tb_pac_aseg_parcelas.codajuste%TYPE := NULL;
		v_codorg              tb_pac_aseg_parcelas.codorg%TYPE := NULL;
		v_numarboles          tb_pac_aseg_parcelas.numarboles%TYPE := NULL;
		v_numcepas            tb_pac_aseg_parcelas.numcepas%TYPE := NULL;
		v_fechasiembra        tb_pac_aseg_parcelas.fec_siembra%TYPE := NULL;
		v_messiembra          tb_pac_aseg_parcelas.mes_siembra%TYPE := NULL;
		v_prdnor              tb_pac_aseg_parcelas.prdnor%TYPE := NULL;
		v_codprovsigpac       tb_pac_aseg_parcelas.codprovsigpac%TYPE := NULL;
		v_codtermsigpac       tb_pac_aseg_parcelas.codtermsigpac%TYPE := NULL;
		v_agrsigpac           tb_pac_aseg_parcelas.agrsigpac%TYPE := NULL;
		v_zonasigpac          tb_pac_aseg_parcelas.zonasigpac%TYPE := NULL;
		v_poligonosigpac      tb_pac_aseg_parcelas.poligonosigpac%TYPE := NULL;
		v_parcelasigpac       tb_pac_aseg_parcelas.parcelasigpac%TYPE := NULL;
		v_recintosigpac       tb_pac_aseg_parcelas.recintosigpac%TYPE := NULL;
    v_sistcultivo         tb_pac_aseg_parcelas.sistcultivo%TYPE := NULL;
    v_sistprod            tb_pac_aseg_parcelas.sistprod%TYPE := NULL;
    v_edad                tb_pac_aseg_parcelas.edad%TYPE := NULL;
    v_destino             tb_pac_aseg_parcelas.destino%TYPE := NULL;
    v_tipoplantacion      tb_pac_aseg_parcelas.tipoplantacion%TYPE := NULL;
    v_practcult           tb_pac_aseg_parcelas.practcult%TYPE := NULL;
    v_fec_f_garant        tb_pac_aseg_parcelas.fec_f_garant%TYPE := NULL;
    v_sistcond            tb_pac_aseg_parcelas.sistcond%TYPE := NULL;
    v_ciclocultivo        tb_pac_aseg_parcelas.ciclocultivo%TYPE := NULL;
    v_frecol              tb_pac_aseg_parcelas.frecol%TYPE := NULL;
    v_tipmarcoplant       tb_pac_aseg_parcelas.tipmarcoplant%TYPE := NULL;
    v_numaniospoda        tb_pac_aseg_parcelas.numaniospoda%TYPE := NULL;
    v_sistprot            tb_pac_aseg_parcelas.sistprot%TYPE := NULL;
    v_rotacion            tb_pac_aseg_parcelas.rotacion%TYPE := NULL;
    v_denomorigen         tb_pac_aseg_parcelas.denomorigen%TYPE := NULL;
    v_igp                 tb_pac_aseg_parcelas.igp%TYPE := NULL;
    v_rcubeleg            tb_pac_aseg_parcelas.rcubeleg%TYPE := NULL;
    v_metros2             tb_pac_aseg_parcelas.metros2%TYPE := NULL;
    v_tipoinstal          tb_pac_aseg_parcelas.tipoinstal%TYPE := NULL;
    v_matcubierta         tb_pac_aseg_parcelas.matcubierta%TYPE := NULL;
    v_tipoterreno         tb_pac_aseg_parcelas.tipoterreno%TYPE := NULL;
    v_tipomasa            tb_pac_aseg_parcelas.tipomasa%TYPE := NULL;
    v_pendiente           tb_pac_aseg_parcelas.pendiente%TYPE := NULL;
		v_sq_pac_aseg_parcela NUMBER := 0;

	BEGIN

    v_codprovincia   := substr(v_linea,18,2);
		v_codcomarca     := substr(v_linea,20,2);
		v_codtermino     := substr(v_linea,22,3);
		v_codsubtermino  := substr(v_linea,25,1);
		v_codpoligono    := substr(v_linea,26,3);
		v_codparcela     := substr(v_linea,29,5);
		v_nombreparcela  := substr(v_linea,34,15);
		v_codcultivo     := substr(v_linea,49,3);
		v_codvariedad    := substr(v_linea,52,3);
		v_codopcion      := substr(v_linea,55,1);
		v_numhectareas   := substr(v_linea,56,8);
		v_codajuste      := substr(v_linea,64,4);
		v_codorg         := substr(v_linea,68,2);
		v_numarboles     := substr(v_linea,70,6);
		v_numcepas       := substr(v_linea,76,6);
		v_fechasiembra   := substr(v_linea,82,8);
		v_messiembra     := substr(v_linea,90,2);
		v_prdnor         := substr(v_linea,92,10);
		v_codprovsigpac  := substr(v_linea,102,2);
		v_codtermsigpac  := substr(v_linea,104,3);
		v_agrsigpac      := substr(v_linea,107,3);
		v_zonasigpac     := substr(v_linea,110,2);
		v_poligonosigpac := substr(v_linea,112,3);
		v_parcelasigpac  := substr(v_linea,115,5);
		v_recintosigpac  := substr(v_linea,120,5);
--TMR
    v_sistcultivo       := FN_LEE_NUMERO(v_linea, 125, 3);
    v_sistprod          := FN_LEE_NUMERO(v_linea, 128, 3);
    v_edad              := FN_LEE_NUMERO(v_linea, 131, 3);
    v_destino           := FN_LEE_NUMERO(v_linea, 134, 3);
    v_tipoplantacion    := FN_LEE_NUMERO(v_linea, 137, 3);
    v_practcult         := FN_LEE_NUMERO(v_linea, 140, 3);

    v_fec_f_garant      := FN_LEE_FECHA(v_linea, 143, 8);

    v_sistcond          := FN_LEE_NUMERO(v_linea, 151, 3);
    v_ciclocultivo      := FN_LEE_NUMERO(v_linea, 154, 3);

    v_frecol            := FN_LEE_FECHA(v_linea, 157, 8);

    v_tipmarcoplant     := FN_LEE_NUMERO(v_linea, 165, 3);
    v_numaniospoda      := FN_LEE_NUMERO(v_linea, 168, 3);
    v_sistprot          := FN_LEE_NUMERO(v_linea, 171, 3);
    v_rotacion          := FN_LEE_NUMERO(v_linea, 174, 3);
    v_denomorigen       := FN_LEE_NUMERO(v_linea, 177, 3);
    v_igp               := FN_LEE_NUMERO(v_linea, 180, 3);
    v_rcubeleg          := substr(v_linea, 183, 1);
    v_metros2           := FN_LEE_NUMERO(v_linea, 184, 9);
    v_tipoinstal        := FN_LEE_NUMERO(v_linea, 193, 3);
    v_matcubierta       := FN_LEE_NUMERO(v_linea, 196, 3);
    v_tipoterreno       := FN_LEE_NUMERO(v_linea, 199, 3);
    v_tipomasa          := FN_LEE_NUMERO(v_linea, 202, 3);
    v_pendiente         := FN_LEE_NUMERO(v_linea, 205, 3);

		v_numhectareas := REPLACE(v_numhectareas, ' ', '0');

		SELECT sq_pac_aseg_parcela.NEXTVAL INTO v_sq_pac_aseg_parcela FROM dual;



		INSERT INTO tb_pac_aseg_parcelas
			(id, id_pac_aseg, codprovincia, codcomarca, codtermino, subtermino, poligono, parcela, cultivo, variedad,
			 opcion, numhectareas, codajuste, codorg, medprev, numarboles, numcepas, fec_siembra, mes_siembra,
			 prdnor, nomparcela, codprovsigpac, codtermsigpac, agrsigpac, zonasigpac, poligonosigpac, parcelasigpac,
			 recintosigpac, sistcultivo, sistprod, edad, destino, tipoplantacion, practcult, fec_f_garant, sistcond,
       ciclocultivo, frecol, tipmarcoplant, numaniospoda, sistprot, rotacion, denomorigen, igp, rcubeleg,
       metros2, tipoinstal, matcubierta, tipoterreno, tipomasa, pendiente)
		VALUES
			(v_sq_pac_aseg_parcela, p_id_aseg, v_codprovincia, v_codcomarca, v_codtermino, v_codsubtermino, v_codpoligono,
			 v_codparcela, v_codcultivo, v_codvariedad, v_codopcion, v_numhectareas, v_codajuste, v_codorg, NULL,
       v_numarboles, v_numcepas, v_fechasiembra, v_messiembra, v_prdnor, v_nombreparcela, v_codprovsigpac,
			 v_codtermsigpac, v_agrsigpac, v_zonasigpac, v_poligonosigpac, v_parcelasigpac, v_recintosigpac,
       v_sistcultivo, v_sistprod, v_edad, v_destino, v_tipoplantacion, v_practcult, v_fec_f_garant, v_sistcond,
       v_ciclocultivo, v_frecol, v_tipmarcoplant, v_numaniospoda, v_sistprot, v_rotacion, v_denomorigen, v_igp,
       v_rcubeleg, v_metros2, v_tipoinstal, v_matcubierta, v_tipoterreno, v_tipomasa, v_pendiente);

	EXCEPTION
		WHEN OTHERS THEN
			  p_error := SQLERRM;
        err_num := SQLCODE;
        err_msg := SQLERRM;
        PQ_UTL.log('*** [pr_carga_pac] -- (5)ERROR -- SQLCODE:' || err_num || ' SQLERRM: ' || err_msg ||'    ', 2);
	END;*/

  --Función para leer datos numéricos de una línea de texto. Recibe la linea, la posicion inicial del número
  -- en la línea y el número de caracteres a leer para componer el número
  /*FUNCTION FN_LEE_NUMERO(P_LINEA IN VARCHAR2, P_POSICION IN NUMBER, P_CARACTERES IN NUMBER) RETURN NUMBER IS
     v_numero number;
  BEGIN
     BEGIN
       v_numero := to_number(substr(P_LINEA, P_POSICION, P_CARACTERES));
     EXCEPTION
       WHEN OTHERS THEN
          v_numero := null;
     END;
     return v_numero;
  END FN_LEE_NUMERO;*/

  --Función para leer datos numéricos de una línea de texto. Recibe la linea, la posicion inicial del número
  -- en la línea y el número de caracteres a leer para componer el número
  /*FUNCTION FN_LEE_FECHA(P_LINEA IN VARCHAR2, P_POSICION IN NUMBER, P_CARACTERES IN NUMBER) RETURN DATE IS
     v_fecha date;
  BEGIN
     BEGIN
        v_fecha := to_date(substr(P_LINEA, P_POSICION, P_CARACTERES), 'YYYYMMDD');
     EXCEPTION
        WHEN OTHERS THEN
            v_fecha := null;
    END;
     return v_fecha;
  END FN_LEE_FECHA;*/

  /*FUNCTION fn_extraer_campo(entrada   VARCHAR2
													 ,campo     NUMBER DEFAULT 1
													 ,separador VARCHAR2 DEFAULT '#') RETURN VARCHAR2 IS

     --Este procedimiento obtiene el campo pasado en una cadena de caracteres separado por #

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
	END;*/

--  asegurados.csv
	/*PROCEDURE pr_carga_asegurados(p_fichero VARCHAR2) IS
		v_file     utl_file.file_type;
		v_file_log utl_file.file_type;
		v_linea    VARCHAR2(32000) := NULL;
		v_lin_log  VARCHAR2(32000) := NULL;

		v_leidos     NUMBER := 0;
		v_insertados NUMBER := 0;


		v_id                 tb_asegurados.id%TYPE := NULL;
		v_codentidad         tb_asegurados.codentidad%TYPE := NULL;
		v_nifcif             tb_asegurados.nifcif%TYPE := NULL;
		v_discriminante      tb_asegurados.discriminante%TYPE := NULL;
		v_tipoidentificacion tb_asegurados.tipoidentificacion%TYPE := NULL;
		v_nombre             tb_asegurados.nombre%TYPE := NULL;
		v_apellido1          tb_asegurados.apellido1%TYPE := NULL;
		v_apellido2          tb_asegurados.apellido2%TYPE := NULL;
		v_razonsocial        tb_asegurados.razonsocial%TYPE := NULL;
		v_clavevia           tb_asegurados.clavevia%TYPE := NULL;
		v_direccion          tb_asegurados.direccion%TYPE := NULL;
		v_numvia             tb_asegurados.numvia%TYPE := NULL;
		v_piso               tb_asegurados.piso%TYPE := NULL;
		v_bloque             tb_asegurados.bloque%TYPE := NULL;
		v_escalera           tb_asegurados.escalera%TYPE := NULL;
		v_codprovincia       tb_asegurados.codprovincia%TYPE := NULL;
		v_codlocalidad       tb_asegurados.codlocalidad%TYPE := NULL;
		v_sublocalidad       tb_asegurados.sublocalidad%TYPE := NULL;
		v_codpostal          tb_asegurados.codpostal%TYPE := NULL;
		v_telefono           tb_asegurados.telefono%TYPE := NULL;
		v_movil              tb_asegurados.movil%TYPE := NULL;
		v_email              tb_asegurados.email%TYPE := NULL;
		v_numsegsocial       tb_asegurados.numsegsocial%TYPE := NULL;
		v_regimensegsocial   tb_asegurados.regimensegsocial%TYPE := NULL;
		v_atp                tb_asegurados.atp%TYPE := NULL;
		v_jovenagricultor    tb_asegurados.jovenagricultor%TYPE := NULL;
		v_codusuario         tb_asegurados.codusuario%TYPE := NULL;

		v_revisado tb_asegurados.revisado%TYPE := 'N';
		v_codlinea tb_datos_asegurados.codlinea%TYPE := NULL;
		v_ccc      tb_datos_asegurados.ccc%TYPE := NULL;
		--v_ccc VARCHAR2(32000);
		v_localidades tb_localidades%ROWTYPE;



	BEGIN
		v_file_log := utl_file.fopen(location  => 'AGP_INTERFACES',
																 filename  => 'Asegurados.log',
																 open_mode => 'w');

		v_lin_log := 'Inicio el proceso ' || to_char(SYSDATE,
																								 'dd-mm-yyyy hh24:mi:ss');
		utl_file.put_line(file   => v_file_log,
											buffer => v_lin_log);

		v_file := utl_file.fopen(location  => 'AGP_INTERFACES',
														 filename  => p_fichero,
														 open_mode => 'r');

		LOOP
			BEGIN
				utl_file.get_line(v_file,
													v_linea);

				htp.p(v_linea || '<br>');
				v_leidos := v_leidos + 1;

				SELECT sq_asegurados.NEXTVAL
					INTO v_id
					FROM dual;

				v_codentidad         := fn_extraer_campo(v_linea,
																								 1,
																								 ';');
				v_nifcif             := fn_extraer_campo(v_linea,
																								 2,
																								 ';');
				v_discriminante      := fn_extraer_campo(v_linea,
																								 3,
																								 ';');
				v_tipoidentificacion := fn_extraer_campo(v_linea,
																								 4,
																								 ';');
				v_nombre             := fn_extraer_campo(v_linea,
																								 5,
																								 ';');
				v_apellido1          := fn_extraer_campo(v_linea,
																								 6,
																								 ';');
				v_apellido2          := fn_extraer_campo(v_linea,
																								 7,
																								 ';');
				v_razonsocial        := fn_extraer_campo(v_linea,
																								 8,
																								 ';');
				v_clavevia           := fn_extraer_campo(v_linea,
																								 9,
																								 ';');
				v_direccion          := fn_extraer_campo(v_linea,
																								 10,
																								 ';');
				v_numvia             := fn_extraer_campo(v_linea,
																								 11,
																								 ';');
				v_piso               := fn_extraer_campo(v_linea,
																								 12,
																								 ';');
				v_bloque             := fn_extraer_campo(v_linea,
																								 13,
																								 ';');
				v_escalera           := fn_extraer_campo(v_linea,
																								 14,
																								 ';');
				v_codprovincia       := fn_extraer_campo(v_linea,
																								 15,
																								 ';');
				v_codlocalidad       := fn_extraer_campo(v_linea,
																								 16,
																								 ';');
				v_sublocalidad       := fn_extraer_campo(v_linea,
																								 17,
																								 ';');

				IF v_sublocalidad = 0 THEN
					v_sublocalidad := ' ';
				END IF;


				v_codpostal        := fn_extraer_campo(v_linea,
																							 18,
																							 ';');
				v_telefono         := fn_extraer_campo(v_linea,
																							 19,
																							 ';');
				v_movil            := fn_extraer_campo(v_linea,
																							 20,
																							 ';');
				v_email            := fn_extraer_campo(v_linea,
																							 21,
																							 ';');
				v_numsegsocial     := fn_extraer_campo(v_linea,
																							 22,
																							 ';');
				v_regimensegsocial := fn_extraer_campo(v_linea,
																							 23,
																							 ';');
				v_atp              := fn_extraer_campo(v_linea,
																							 24,
																							 ';');
				v_jovenagricultor  := fn_extraer_campo(v_linea,
																							 25,
																							 ';');
				v_codusuario       := fn_extraer_campo(v_linea,
																							 26,
																							 ';');

				v_revisado := 'N';
				v_codlinea := fn_extraer_campo(v_linea,
																			 27,
																			 ';');
				v_ccc      := substr(fn_extraer_campo(v_linea,
																							28,
																							';'),
														 1,
														 20);

				BEGIN
					SELECT *
						INTO v_localidades
						FROM o02agpe0.tb_localidades l
					 WHERE l.codprovincia = v_codprovincia
						 AND l.codlocalidad = v_codlocalidad
						 AND l.sublocalidad = v_sublocalidad;
				EXCEPTION
					WHEN no_data_found THEN
						BEGIN
							SELECT sublocalidad
								INTO v_sublocalidad
								FROM o02agpe0.tb_localidades l
							 WHERE l.codprovincia = v_codprovincia
								 AND l.codlocalidad = v_codlocalidad
								 AND rownum = 1
							 ORDER BY l.sublocalidad ASC;
						EXCEPTION
							WHEN OTHERS THEN
								NULL;

						END;
				END;


				BEGIN
					INSERT INTO tb_asegurados
						(id,
						 codentidad,
						 nifcif,
						 discriminante,
						 tipoidentificacion,
						 nombre,
						 apellido1,
						 apellido2,
						 razonsocial,
						 clavevia,
						 direccion,
						 numvia,
						 piso,
						 bloque,
						 escalera,
						 codprovincia,
						 codlocalidad,
						 sublocalidad,
						 codpostal,
						 telefono,
						 movil,
						 email,
						 numsegsocial,
						 regimensegsocial,
						 atp,
						 jovenagricultor,
						 codusuario,
						 revisado)
					VALUES
						(v_id,
						 v_codentidad,
						 v_nifcif,
						 v_discriminante,
						 v_tipoidentificacion,
						 v_nombre,
						 v_apellido1,
						 v_apellido2,
						 v_razonsocial,
						 v_clavevia,
						 v_direccion,
						 v_numvia,
						 v_piso,
						 v_bloque,
						 v_escalera,
						 v_codprovincia,
						 v_codlocalidad,
						 v_sublocalidad,
						 v_codpostal,
						 v_telefono,
						 v_movil,
						 v_email,
						 v_numsegsocial,
						 v_regimensegsocial,
						 v_atp,
						 v_jovenagricultor,
						 v_codusuario,
						 v_revisado);

					v_insertados := v_insertados + 1;

					BEGIN
						INSERT INTO tb_datos_asegurados
							(idasegurado, codlinea, ccc)
						VALUES
							(v_id, v_codlinea, v_ccc);
					EXCEPTION
						WHEN OTHERS THEN
							v_lin_log := 'error datos_asegurados' || v_linea;
							utl_file.put_line(file   => v_file_log,
																buffer => v_lin_log);
							v_lin_log := 'error2 datos_asegurados ' || SQLERRM;
							utl_file.put_line(file   => v_file_log,
																buffer => v_lin_log);
					END;

				EXCEPTION
					WHEN OTHERS THEN
						v_lin_log := 'error ' || v_linea;
						utl_file.put_line(file   => v_file_log,
															buffer => v_lin_log);
						v_lin_log := 'error 2 ' || SQLERRM;
						utl_file.put_line(file   => v_file_log,
															buffer => v_lin_log);
				END;



			EXCEPTION
				WHEN no_data_found THEN
					EXIT;
			END;
		END LOOP;

		v_lin_log := 'Leidos ' || v_leidos;
		utl_file.put_line(file   => v_file_log,
											buffer => v_lin_log);


		v_lin_log := 'insertados ' || v_insertados;
		utl_file.put_line(file   => v_file_log,
											buffer => v_lin_log);

		v_lin_log := 'Fin el proceso ' || to_char(SYSDATE,
																							'dd-mm-yyyy hh24:mi:ss');
		utl_file.put_line(file   => v_file_log,
											buffer => v_lin_log);

		utl_file.fclose(v_file);
		utl_file.fclose(v_file_log);

	END;*/


  PROCEDURE pr_carga_pac(p_fichero       VARCHAR2
												,p_cod_usuario  VARCHAR2
												,p_codplan      NUMBER
                        ,p_ent_med      NUMBER
                        ,p_subent_med      NUMBER
                        ,p_error        OUT VARCHAR2) IS


    lc VARCHAR2(30) := 'PQ_CARGAS.pr_carga_pac - ';


    v_file            utl_file.file_type;
    v_arc_abiert      boolean;

		v_codlin          tb_pac_cargas.linea%TYPE := NULL;
		v_codent          tb_pac_cargas.entidad%TYPE := NULL;
		v_nif_ant         tb_pac_asegurados.nif_asegurado%TYPE := ' ';
    v_cifnifasegurado    tb_pac_asegurados.nif_asegurado%TYPE;
    v_lineaseguroid   tb_lineas.lineaseguroid%TYPE;

    v_id_carga tb_pac_cargas.id%TYPE := NULL;
		v_id_aseg  tb_pac_asegurados.id%TYPE := NULL;
    v_id_parcela  tb_pac_parcelas.id%TYPE := NULL;

		v_linea    VARCHAR2(32767) := NULL;
    v_num_linea NUMBER := 0; -- Número de línea que se está procesando en cada momento, para incluir en el mensaje de error
    v_primera_linea BOOLEAN := TRUE;
    v_tam_ok BOOLEAN := TRUE;


	BEGIN
	    BEGIN

          PQ_UTL.log(lc, '*** INICIO CARGA DE FICHERO DE PAC ***', 2);
          PQ_UTL.log(lc, 'Parámetros recibidos: Fichero: ' || p_fichero || ' usuario: ' || p_cod_usuario || ' plan: ' ||
                          p_codplan || ' ent_med: ' || p_ent_med  || ' subent_med: ' || p_subent_med || '. ' , 2);


          -- Abre el fichero de PAC en modo lectura
          PQ_UTL.log(lc, 'Abre  el fichero ' || p_fichero || ' en modo lectura', 2);
          v_arc_abiert := abrirFichero(p_fichero, v_file);

          -- Si ha habido algún error al acceder al archivo no se continúa el proceso y se devuelve el mensaje de error
          IF (v_arc_abiert = FALSE) THEN
             p_error := '. Error al abrir el fichero de carga';
             RETURN;
          END IF;

          -- Lee la primera línea para obtener el código de línea, entidad y nif/cif de asegurado asociados a la carga
          PQ_UTL.log(lc, 'Lee la primera línea para obtener el código de línea, entidad y nif/cif de asegurado asociados a la carga', 2);
          obtieneDatosCarga(v_file, v_linea, v_num_linea, v_codlin, v_codent, v_cifnifasegurado, p_codplan, v_lineaseguroid, v_tam_ok);
          -- Si la primera línea no tiene el tamaño mínimo se elimina la carga y el proceso finaliza
          IF (v_tam_ok = FALSE) THEN
             eliminarCarga (p_id_carga => v_id_carga);
             p_error := '. Error al procesar la línea ' || v_num_linea || ', el tamaño de la línea no es correcto';
             PQ_UTL.log(lc, 'Error al procesar la línea ' || v_num_linea || '. El tamaño de la línea no es correcto', 2);
             RETURN;
          END IF;

          IF ((v_codlin IS NULL) OR (v_codent IS NULL)) THEN
             p_error := '. Error al obtener la línea y entidad asociadas al fichero de carga';
             RETURN;
          END IF;
          -- Comprueba si el plan/línea indicado existe en el sistema
          IF (v_lineaseguroid IS NULL) THEN
             p_error := '. El plan/línea indicado (' || p_codplan || '/' || v_codlin || ') no existe en el sistema';
             RETURN;
          END IF;

          PQ_UTL.log(lc, 'Carga asociada a entidad: ' || v_codent || ', linea: ' || v_codlin || ', nif: ' || v_cifnifasegurado || '. ', 2);

          -- Comprueba que la E-S Mediadora indicada en el formulario de carga está asociada a la entidad que aparece en el fichero de carga
          PQ_UTL.log(lc, 'Comprueba que la E-S Mediadora indicada en el formulario de carga está asociada a la entidad que aparece en el fichero de carga', 2);
          IF (compruebaESMedEntidad (entidad => v_codent, ent_med => p_ent_med, subent_med => p_subent_med) = FALSE) THEN
             p_error := '. La E-S Mediadora indicada (' || p_ent_med || '-' || p_subent_med
                        || ') no pertenece a la Entidad asociada al fichero de carga (' || v_codent || ')';
             RETURN;
          END IF;

          -- Registra los datos de la carga en BBDD y devuelve el identificador
          PQ_UTL.log(lc, 'Registra los datos de la carga en las tablas TB_PAC_CARGAS y TB_PAC_CARGAS_FICHERO' , 2);
          v_id_carga := registrarCarga(p_fichero, p_cod_usuario, p_codplan, p_ent_med, p_subent_med, v_codlin, v_codent);
          IF (v_id_carga IS NULL) THEN
             p_error := 'Error al registrar la carga en el sistema';
             RETURN;
          END IF;

          PQ_UTL.log(lc, 'El identificador de la carga es ' || v_id_carga, 2);

          -- Comienza el procesamiento del fichero y la carga de parcelas
          PQ_UTL.log(lc, 'Comienza el procesamiento del fichero y la carga de parcelas', 2);


          LOOP

            -- Si es la primera iteración del bucle (primera línea del fichero) no hay que leer la línea, ya que se ha leído anteriormente
            IF (v_primera_linea = FALSE) THEN
               -- Lee la siguiente línea
               v_linea := leeLinea (v_file, v_num_linea, v_tam_ok);
               -- Si la línea es nula (porque ha llegado al final del fichero o por un error) sale del bucle
               IF (v_linea IS NULL) THEN
                  EXIT;
               END IF;
               -- Si la línea no tiene el tamaño mínimo se cancela la carga
                IF (v_tam_ok = FALSE) THEN
                   eliminarCarga (p_id_carga => v_id_carga);
                   PQ_UTL.log(lc, 'Error al procesar la línea ' || v_num_linea || '. El tamaño de la línea no es correcto', 2);
                   p_error := '. Error al procesar la línea ' || v_num_linea || ', el tamaño de la línea no es correcto';
                   RETURN;
                END IF;

               -- Lee el NIF/CIF de la línea
               v_cifnifasegurado := substr(v_linea, 8, 9);
            END IF;

            v_primera_linea := FALSE;

            IF (MOD (v_num_linea, v_num_lineas_prc) = 0) THEN PQ_UTL.log(lc, 'Procesando línea ' || v_num_linea, 2); END IF;

            -- Comprueba si el NIF/CIF de la línea leida es diferente del procesado en la línea anterior, ya que en ese caso
            -- hay que insertar un nuevo registro en TB_PAC_ASEGURADOS
            IF v_nif_ant <> v_cifnifasegurado THEN

               v_nif_ant := v_cifnifasegurado;

               -- Inserta el nuevo registro de asegurado de PAC
               insertarAsegurado(v_linea => v_linea,v_cifnifasegurado => v_cifnifasegurado, p_id_carga => v_id_carga, p_id_pac_aseg   => v_id_aseg);

               -- Si ha ocurrido algún error al insertar el registro se elimina la carga y el proceso finaliza
               IF (v_id_aseg IS NULL) THEN
                  eliminarCarga (p_id_carga => v_id_carga);
                  p_error := 'Error al insertar el registro de asegurado correspondiente a la línea ' || v_num_linea;
                  RETURN;
               END IF;
            END IF;

            -- Inserta el registro de parcela de PAC
            insertarParcela (v_linea => v_linea, p_id_pac_aseg => v_id_aseg, p_id_pac_parcela => v_id_parcela, p_lineaseguroid => v_lineaseguroid, p_error => p_error);
            -- Si el identificador de la parcela es nulo ha ocurrido algún error en el proceso, se elimina la carga y el proceso finaliza
            IF (v_id_parcela IS NULL) THEN
                  eliminarCarga (p_id_carga => v_id_carga);
                  p_error := '. Error al procesar la línea ' || v_num_linea || ' - ' || p_error;
                  RETURN;
            END IF;

            -- Inserta el registro de capital asegurado de PAC
            insertarCapitalAsegurado (v_linea => v_linea, p_id_pac_aseg => v_id_aseg, p_id_pac_parcela => v_id_parcela, p_error => p_error);
            -- Si la variable que almacena la descripción del error tiene algún valor
            -- ha ocurrido algún error en el proceso, se elimina la carga y el proceso finaliza
            IF (LENGTH (p_error) > 0) THEN
                  eliminarCarga (p_id_carga => v_id_carga);
                  p_error := '. Error al procesar la línea ' || v_num_linea || ' - ' || p_error;
                  RETURN;
            END IF;

            -- Inserta los registros de datos variables de PAC
            insertarDatosVariables (v_linea => v_linea, p_id_pac_aseg => v_id_aseg, p_id_pac_parcela => v_id_parcela, p_error => p_error);
            -- Si la variable que almacena la descripción del error tiene algún valor
            -- ha ocurrido algún error en el proceso, se elimina la carga y el proceso finaliza
            IF (LENGTH (p_error) > 0) THEN
                  eliminarCarga (p_id_carga => v_id_carga);
                  p_error := '. Error al procesar la línea ' || v_num_linea || ' - ' || p_error;
                  RETURN;
            END IF;

          END LOOP;

          -- Fin del procesamiento del fichero
          PQ_UTL.log(lc, 'Fin del procesamiento del fichero', 2);
          COMMIT;

          RETURN;

      EXCEPTION
          WHEN OTHERS THEN
              PQ_UTL.log(lc, 'ERROR -- SQLCODE:' || SQLCODE || ' SQLERRM: ' || SQLERRM ||'    ', 2);
              p_error := 'Error no controlado en el proceso de carga';
              RETURN;

      END;

      /*
      IF v_nif_ant <> v_cifnifasegurado THEN
          pr_insertar_asegurados(v_linea     => v_linea,
                                 p_id_cargar => v_id_carga,
                                 p_error     => p_error,
                                 p_id_aseg   => v_id_aseg);
      END IF;
      v_nif_ant := v_cifnifasegurado;
      pr_insertar_parcelas(v_linea   => v_linea,
			                     p_id_aseg => v_id_aseg,
												   p_error   => p_error);
      LOOP
          BEGIN
              utl_file.get_line(v_file, v_linea);
          EXCEPTION
              WHEN no_data_found THEN
                  err_num := SQLCODE;
                  err_msg := SQLERRM;
                  PQ_UTL.log('*** [pr_carga_pac] -- (3)ERROR -- SQLCODE:' || err_num || ' SQLERRM: ' || err_msg ||'    ', 2);
                  EXIT;
          END;
          v_cifnifasegurado := substr(v_linea, 8, 9);

          IF v_nif_ant <> v_cifnifasegurado THEN
              pr_insertar_asegurados(v_linea     => v_linea,
							                       p_id_cargar => v_id_carga,
															       p_error     => p_error,
															       p_id_aseg   => v_id_aseg);
          END IF;

          v_nif_ant := v_cifnifasegurado;
          pr_insertar_parcelas(v_linea   => v_linea,
                               p_id_aseg => v_id_aseg,
													     p_error   => p_error);
      END LOOP;
      COMMIT;
      */
  END pr_carga_pac;
  --------------------------------------------------------------------------------------

END pq_cargas;
/
