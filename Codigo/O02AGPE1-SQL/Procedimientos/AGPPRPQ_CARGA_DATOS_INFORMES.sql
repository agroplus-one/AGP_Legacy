SET DEFINE OFF;
SET SERVEROUTPUT ON;

create or replace package o02agpe1.PQ_CARGA_DATOS_INFORMES AUTHID CURRENT_USER is

  -- Author  : U028606.
  -- Created : 19/11/2012 14:02:26
  -- Purpose : Carga de datos en las tablas usadas para la generacion de informes
  -- MODIF: GDLD-57628 (23/07/2021)
  -- MODIF: ESC-15280 (28/09/2021)-PROD(04/10/2021)
  -- MODIF: GDLD-78692 (12/01/2022)

  lc VARCHAR2(25) := 'PQ_CARGA_DATOS_INFORMES.'; -- Variable que almacena el nombre del paquete y de la funcion
  TYPE arrayInterno IS VARRAY(4) OF VARCHAR2(50);

  --
  -- Devuelve los registros de 'p_cursor' concatenados y separados por 'p_separador'
  --
  function concat_group(p_cursor in sys_refcursor, p_separador varchar2 default ',') return varchar2;

  --
  -- Procedimiento que lanza la actualizacion de los origenes de datos del modulo de informes
  --
  procedure actualizarOrigenesDatos;

  --
  -- Inserta un registro nuevo en la tabla de historico
  --
  procedure insertarHistorico;

  --
  -- Actualiza el registro nuevo en la tabla de historico
  --
  procedure actualizarHistorico (ejecucionOK in BOOLEAN);

  --
  -- Carga en un array los origenes de datos que se van a actualizar
  --
  procedure cargarOrigenesDatos (v_anios_actualizar IN NUMBER, v_plan_actual IN NUMBER);

  --
  -- Trunca las tablas de backup asociadas a los origenes de datos
  --
  procedure truncarBackups;

  --
  -- Trunca los origenes de datos
  --
  procedure truncarOrigenesDatos;

  --
  -- Actualiza las tablas de backup con los nuevos datos
  --
  procedure actualizarBackups (v_anios_actualizar IN NUMBER, v_plan_actual IN NUMBER);

  --
  -- Actualiza las tablas principales con los datos de las de backup
  --
  procedure actualizarTablasPrincipales;

  --
  -- Actualiza el backup del origen de datos 'TB_INF_POLIZAS'
  --
  procedure act_tb_inf_polizas (v_anios_actualizar IN NUMBER, v_plan_actual IN NUMBER);

  --
  -- Actualiza el backup del origen de datos 'TB_INF_POLIZAS_COBERTURAS'
  --
  procedure act_tb_inf_polizas_coberturas (v_anios_actualizar IN NUMBER, v_plan_actual IN NUMBER);

  --
  -- Actualiza el backup del origen de datos 'TB_INF_ASEGURADOS'
  --
  procedure act_tb_inf_asegurados (v_anios_actualizar IN NUMBER, v_plan_actual IN NUMBER);

  --
  -- Actualiza el backup del origen de datos 'TB_INF_ASEGURADOS_SOCIOS'
  --
  procedure act_tb_inf_asegurados_socios (v_anios_actualizar IN NUMBER, v_plan_actual IN NUMBER);

  --
  -- Actualiza el backup del origen de datos 'TB_INF_COLECTIVOS'
  --
  procedure act_tb_inf_colectivos (v_anios_actualizar IN NUMBER, v_plan_actual IN NUMBER);

  --
  -- Actualiza el backup del origen de datos 'TB_INF_SINIESTROS'
  --
  procedure act_tb_inf_siniestros (v_anios_actualizar IN NUMBER, v_plan_actual IN NUMBER);

  --
  -- Actualiza el backup del origen de datos 'TB_INF_SINIESTROS_PARCELAS'
  --
  procedure ACT_TB_INF_SINIESTROS_PARCELA (v_anios_actualizar IN NUMBER, v_plan_actual IN NUMBER);

  --
  -- Actualiza el backup del origen de datos 'TB_INF_ANEXOS_MODIFICACION'
  --
  procedure act_tb_inf_anexos_modificacion (v_anios_actualizar IN NUMBER, v_plan_actual IN NUMBER);

  --
  -- Actualiza el backup del origen de datos 'TB_INF_ANEXOS_MODIF_PARCEL_BAK'
  --
  procedure act_tb_inf_anexos_modif_parcel (v_anios_actualizar IN NUMBER, v_plan_actual IN NUMBER);

  --
  -- Actualiza el backup del origen de datos 'TB_INF_ANEXOS_MODIF_COBERT_BAK'
  --
  procedure act_tb_inf_anexos_modif_cobert (v_anios_actualizar IN NUMBER, v_plan_actual IN NUMBER);

  --
  -- Actualiza el backup del origen de datos 'TB_INF_ANEXO_RED_BAK'
  --
  procedure act_tb_inf_anexo_red (v_anios_actualizar IN NUMBER, v_plan_actual IN NUMBER);

  --
  -- Actualiza el backup del origen de datos 'TB_INF_ANEXO_RED_PARCELAS_BAK'
  --
  procedure act_tb_inf_anexo_red_parcelas (v_anios_actualizar IN NUMBER, v_plan_actual IN NUMBER);

  --
  -- Actualiza el backup del origen de datos 'TB_INF_DISTRIBUCION_COSTES_BAK'
  --
  procedure act_tb_inf_distribucion_costes (v_anios_actualizar IN NUMBER, v_plan_actual IN NUMBER);

  --
  -- Actualiza el backup del origen de datos 'TB_POLIZAS_BAK'
  --
  procedure act_tb_polizas (v_anios_actualizar IN NUMBER, v_plan_actual IN NUMBER);

  --
  -- Actualiza el backup del origen de datos 'TB_COLECTIVOS_BAK'
  --
  procedure act_tb_colectivos (v_anios_actualizar IN NUMBER, v_plan_actual IN NUMBER);

  --
  -- Actualiza el backup del origen de datos 'TB_PARCELAS_BAK'
  --
  procedure act_tb_parcelas (v_anios_actualizar IN NUMBER, v_plan_actual IN NUMBER);

  --
  -- Actualiza el backup del origen de datos 'TB_CAPITALES_ASEGURADOS_BAK'
  --
  procedure act_tb_capitales_asegurados (v_anios_actualizar IN NUMBER, v_plan_actual IN NUMBER);

  --
  -- Actualiza el backup del origen de datos 'TB_POLIZAS_PARCELAS_BAK'
  --
  procedure act_tb_inf_polizas_parcelas (v_anios_actualizar IN NUMBER, v_plan_actual IN NUMBER);

  --
  -- Actualiza el backup del origen de datos 'TB_CAPASEG_MOD_BAK'
  --
  procedure act_tb_capaseg_mod (v_anios_actualizar IN NUMBER, v_plan_actual IN NUMBER);

  --
  -- Ejecuta un insert de select en las tablas y esquemas indicados en los parametros
  --
  procedure insertDeSelect (esqDest IN VARCHAR2, tbDest IN VARCHAR2, esqOrig IN VARCHAR2, tbOrig IN VARCHAR2, condicion IN VARCHAR2 DEFAULT NULL);

  --
  -- Ejecuta un insert de select por Entidad en las tablas y esquemas indicados en los parametros
  --
  procedure insertDeSelectPorEntidad (esqDest IN VARCHAR2, tbDest IN VARCHAR2, esqOrig IN VARCHAR2, tbOrig IN VARCHAR2, condicion IN VARCHAR2 DEFAULT NULL);

  --
  -- Ejecuta un insert de select en las tablas de datos variables
  --
  procedure insertDeSelectDvp (esqDest IN VARCHAR2, esqOrig IN VARCHAR2, codConcepto IN VARCHAR2);

  --
  -- Anhade al correo el mensaje de numero de registros actualizados
  --
  procedure setMsgCount (origDatos IN VARCHAR2, numTeorico IN VARCHAR2);

  --
  -- Devuelve un boolean indicando si ya se esta ejecutando el proceso
  --
  function ejecutando return boolean;

  --
  -- Actualiza el backup del origen de datos 'TB_INF_CULTIVO_VAR_LINEA'
  --
  procedure act_tb_inf_cultivo_var_linea (v_anios_actualizar IN NUMBER, v_plan_actual IN NUMBER);

  --
  -- Actualiza el backup del origen de datos 'TB_INF_EXPLOTACIONES_COBERTURAS'
  --
  procedure act_tb_inf_explotaciones_coberturas (v_anios_actualizar IN NUMBER, v_plan_actual IN NUMBER);

  --
  -- Actualiza el backup del origen de datos 'TB_INF_PARCELAS_COBERTURAS'
  --
  procedure act_tb_inf_parcelas_coberturas (v_anios_actualizar IN NUMBER, v_plan_actual IN NUMBER);

  --
  -- Actualiza el backup del origen de datos 'TB_DIST_COSTE_PARCELAS'
  --
  procedure act_tb_dist_coste_parcelas (v_anios_actualizar IN NUMBER, v_plan_actual IN NUMBER);

  --
  -- Carga los datos correspondientes en las tablas de historico de estados --
  --
  procedure actualizaHistoricoEstados;

  --
  -- Borra los datos correspondientes en las tablas de historico de estados --
  --
  procedure truncaHistoricoEstados;

  --
  -- Carga los datos correspondientes en las tablas de historico de estados --
  --
  procedure act_tb_historico (nomTabla IN VARCHAR, estado IN NUMBER, orden IN VARCHAR);

  --
  -- Actualiza los datos variable de polizas parcelas con la descripcion para los que correspondan --
  --
  procedure act_datosVariablesDescripcion;

  --
  -- Actualiza los datos variable de polizas parcelas con la descripcion para los que correspondan --
  --
  procedure updateDescDV (reg IN arrayInterno);

  --
  -- Obtiene de la tabla de configuracion la variable que indica si la actualizacion de datos es   --
  -- completa o solo de las polizas que no estan contratadas                                       --
  --
  procedure getActCompleta;

  --
  -- Obtiene el numero de registros que se espera insertar para cada origen de datos               --
  --
  procedure getNumRegistrosEsperados (v_anios_actualizar IN NUMBER, v_plan_actual IN NUMBER);

  --
  -- insertar los datos de los asegurados en la tabla 'TB_INF_DATOS_ASEGURADO_BAK'.
  --
  procedure act_tb_inf_datos_asegurado (v_anios_actualizar IN NUMBER, v_plan_actual IN NUMBER);


  --
  --se encarga de actualizar el campo GRUPO_NEGOCIO para los registros de la tabla
  --TB_POLIZAS_PCT_COMISIONES en los que sea nulo
  --
  procedure actualizarGN_ComisionesPoliza (v_anios_actualizar IN NUMBER, v_plan_actual IN NUMBER);

  --
  --devolvera el caracter asociado a su grupo de negocio; si la linea de seguro asociada a la poliza
  --es agraria (grupo de seguro 'A01') devolvera '1' y si es ganadera (grupo de seguro 'G01') devolvera '2'.
  --
  function getGrupoNegocioPoliza(p_idPoliza number)return varchar2;

  --
  --se encarga de insertar los datos de la tabla TB_INF_COMISIONES_POLIZA_BAK
  --
  procedure act_tb_inf_comisiones_poliza (v_anios_actualizar IN NUMBER, v_plan_actual IN NUMBER);

  --
  --se encargara de actualizar el campo GRUPO_NEGOCIO para los registros
  --de la tabla TB_DISTRIBUCION_COSTES_2015 en los que sea nulo
  --
  procedure actualizarGN_DC2015Poliza (v_anios_actualizar IN NUMBER, v_plan_actual IN NUMBER);

  procedure act_tb_inf_dist_costes_2015 (v_anios_actualizar IN NUMBER, v_plan_actual IN NUMBER);

  procedure act_tb_inf_explotaciones (v_anios_actualizar IN NUMBER, v_plan_actual IN NUMBER);

  procedure act_tb_inf_explotaciones_anexo (v_anios_actualizar IN NUMBER, v_plan_actual IN NUMBER);
  
  procedure act_tb_inf_parc_dist_coste (v_anios_actualizar IN NUMBER, v_plan_actual IN NUMBER);
  
  procedure act_tb_inf_expl_dist_coste (v_anios_actualizar IN NUMBER, v_plan_actual IN NUMBER);
  
  procedure act_tb_inf_parc_dist_coste_gn (v_anios_actualizar IN NUMBER, v_plan_actual IN NUMBER);
  
  procedure act_tb_inf_parc_dist_coste_su (v_anios_actualizar IN NUMBER, v_plan_actual IN NUMBER);
  
  procedure act_tb_inf_parc_dist_coste_br (v_anios_actualizar IN NUMBER, v_plan_actual IN NUMBER);
  
  procedure act_tb_inf_expl_dist_coste_gn (v_anios_actualizar IN NUMBER, v_plan_actual IN NUMBER);
  
  procedure act_tb_inf_expl_dist_coste_su (v_anios_actualizar IN NUMBER, v_plan_actual IN NUMBER);
  
  procedure act_tb_inf_expl_dist_coste_br (v_anios_actualizar IN NUMBER, v_plan_actual IN NUMBER);

  procedure insertDeSelectCampos (esqDest IN VARCHAR2, tbDest IN VARCHAR2, esqOrig IN VARCHAR2, tbOrig IN VARCHAR2, condicion IN VARCHAR2 DEFAULT NULL, campos IN VARCHAR2 DEFAULT NULL);

end PQ_CARGA_DATOS_INFORMES;
/
create or replace package body o02agpe1.PQ_CARGA_DATOS_INFORMES is

  -- Tipo de array usado para guardar los nombres de los origenes de datos
  TYPE arrayTablas IS VARRAY(36) OF VARCHAR2(50);
  TYPE arrayTablasDesc IS VARRAY(15) OF arrayInterno;
  -- Array de origenes de datos de backup
  origenesDatosBackups arrayTablas;
  -- Array de origenes de datos
  TYPE arrayTablasEstadoInt IS VARRAY(5) OF VARCHAR2(200);
  TYPE arrayTablasEstado IS VARRAY(30) OF arrayTablasEstadoInt;
  origenesDatos arrayTablasEstado;
  idHistorico o02agpe1.tb_historico_cargas.id%TYPE;
  -- Variables para la generacion del correo
  v_salto CONSTANT VARCHAR2(10) := CHR(10)||CHR(13); -- Salto de linea y retorno de carro
  v_retorno CONSTANT varchar2(10) := CHR(10); -- Retorno de carro
  v_cabecera CONSTANT varchar2(50) := ' ENTORNO DE PRODUCCION '; -- Cadena que se anhade a la cabecera del correo a generar
  v_cuerpo varchar2(2000); -- Almacena el resumen de registros actualizados al finalizar el proceso
  -- Variables para la ejecucion completa o parcial del proceso
  KEY_OD_INFORMES_ACT_COMPLETA CONSTANT VARCHAR2(30) := 'OD_INFORMES_ACT_COMPLET';
  v_act_completa BOOLEAN := TRUE;
  -- Variables para almacenar el numero de registros que se espera insertar en cada origen de datos
  v_numreg_plz             NUMBER;
  v_numreg_coberturas      NUMBER;
  v_numreg_aseg            NUMBER;
  v_numreg_aseg_socios     NUMBER;
  v_numreg_colectivos      NUMBER;
  v_numreg_siniestros      NUMBER;
  v_numreg_siniestros_parc NUMBER;
  v_numreg_am              NUMBER;
  v_numreg_am_par          NUMBER;
  v_numreg_am_cob          NUMBER;
  v_numreg_ar              NUMBER;
  v_numreg_arp             NUMBER;
  v_numreg_dist_coste      NUMBER;
  v_numreg_ca              NUMBER;
  v_numreg_cultvar         NUMBER;
  v_numreg_datos_aseg      NUMBER;
  v_numreg_comis_poliza    NUMBER;
  v_numreg_DC_2015         NUMBER;
  v_numreg_Explotaciones   NUMBER;
  v_numreg_Explo_Anexo     NUMBER;
  v_numreg_expl_cob        NUMBER;
  v_numreg_parc_cob        NUMBER;
  v_anios_actualizar       NUMBER(2);
  v_plan_actual            NUMBER(4);

  ----------------------------------------------------------------------------------------------
  -- Procedimiento que lanza la actualizacion de los origenes de datos del modulo de informes --
  ----------------------------------------------------------------------------------------------
  procedure actualizarOrigenesDatos as
  begin
  
    SELECT agp_valor
      INTO v_anios_actualizar
      FROM o02agpe0.tb_config_agp
     WHERE agp_nemo = 'PLANES_INFORMES';
  
    SELECT MAX(codplan)
      INTO v_plan_actual
      FROM o02agpe0.tb_lineas lin, o02agpe0.tb_sc_c_lineas sc_lin
     WHERE lin.codlinea = sc_lin.codlinea
       AND sc_lin.codgruposeguro = 'G01';
  
    PQ_Utl.LOG(lc, '-- INICIO PQ_CARGA_DATOS_INFORMES--', 2);
    PQ_Utl.LOG(lc,
               '-- Inserta un registro nuevo en la tabla de historico --',
               2);
    insertarHistorico();
    PQ_Utl.LOG(lc,
               '-- Se comprueba el tipo de actualizacion que se va a realizar --',
               2);
    getActCompleta();
    PQ_Utl.LOG(lc,
               '-- Actualizamos el campo Grupo de Negocio en ...PCT_COMISIONES del esquema 0 --',
               2);
    actualizarGN_ComisionesPoliza(v_anios_actualizar, v_plan_actual);
    PQ_Utl.LOG(lc,
               '-- Actualizamos el campo Grupo de Negocio en distribucion de costes 2015+ del esquema 0 --',
               2);
    actualizarGN_DC2015Poliza(v_anios_actualizar, v_plan_actual);
    PQ_Utl.LOG(lc,
               '-- Obtiene el numero de registros que se espera insertar para cada origen de datos --',
               2);
    getNumRegistrosEsperados(v_anios_actualizar, v_plan_actual);
    PQ_Utl.LOG(lc, '-- Carga los origenes de datos a actualizar --', 2);
    cargarOrigenesDatos(v_anios_actualizar, v_plan_actual);
    PQ_Utl.LOG(lc,
               '-- Trunca las tablas de backup asociadas a los origenes de datos --',
               2);
    truncarBackups();
    PQ_Utl.LOG(lc, '-- Actualiza las tablas de historico de estados --', 2);
    actualizaHistoricoEstados();
    PQ_Utl.LOG(lc,
               '-- Actualiza las tablas de backup con los nuevos datos --',
               2);
    actualizarBackups(v_anios_actualizar, v_plan_actual);
    PQ_Utl.LOG(lc,
               '-- Actualiza las descripciones de los datos variables de polizas parcelas --',
               2);
    act_datosVariablesDescripcion();
    PQ_Utl.LOG(lc, '-- Trunca los origenes de datos  --', 2);
    truncarOrigenesDatos();
    PQ_Utl.LOG(lc,
               '-- Actualiza las tablas principales con las de backup --',
               2);
    actualizarTablasPrincipales();
    PQ_Utl.LOG(lc,
               '-- Trunca las tablas de backup asociadas a los origenes de datos --',
               2);
    truncarBackups();
    PQ_Utl.LOG(lc, '-- Trunca las tablas de historico de estados --', 2);
    truncaHistoricoEstados();
    PQ_Utl.LOG(lc, '-- Actualiza el historico --', 2);
    actualizarHistorico(TRUE);
    PQ_Utl.LOG(lc, '-- FIN --', 2);
  
    -- Pintamos en el log de trazas_bd el error y cancelamos cadena
  EXCEPTION
    WHEN OTHERS THEN
      actualizarHistorico(FALSE);
      PQ_Utl.LOG(lc, 'actualizarOrigenesDatos: Cadena cancelada', 2);
      PQ_Utl.LOG(lc,
                 'Error al actualizarOrigenesDatos: ' || SQLCODE || ' - ' ||
                 SQLERRM || '. ',
                 2);
      -- Se vuelve a lanzar la excepcion para que cancele la cadena
      RAISE;
    
  end actualizarOrigenesDatos;

  --------------------------------------------------------
  -- Inserta un registro nuevo en la tabla de historico --
  --------------------------------------------------------
  procedure insertarHistorico as
  
  begin
    select o02agpe1.sq_historico_cargas.nextval into idHistorico from dual;
    INSERT INTO o02agpe1.tb_historico_cargas
    values
      (idHistorico, sysdate, null, null);
    COMMIT;
  
    -- Escribe la fecha de inicio en la salida estandar para el posterior envio de correo
    PQ_Utl.LOG(lc, '-----' || v_cabecera || '-----' || v_retorno, 2);
    PQ_Utl.LOG(lc,
               'Inicio de ejecucion: ' ||
               to_char(sysdate, 'DD/MM/YYYY HH24:MI:SS') || v_retorno,
               2);
  
  end insertarHistorico;

  ----------------------------------------------------------
  -- Actualiza el registro nuevo en la tabla de historico --
  ----------------------------------------------------------
  procedure actualizarHistorico(ejecucionOK in BOOLEAN) as
  
  begin
  
    -- Escribe la fecha de fin de ejecucion en la salida estandar para el posterior envio de correo
    PQ_Utl.LOG(lc,
               'Fin de ejecucion: ' ||
               to_char(sysdate, 'DD/MM/YYYY HH24:MI:SS') || v_retorno,
               2);
  
    IF (ejecucionOK) then
      UPDATE o02agpe1.tb_historico_cargas hc
         set hc.fecha_fin = sysdate, hc.estado = 'OK'
       WHERE hc.id = idHistorico;
      PQ_Utl.LOG(lc, 'Resultado: OK ' || v_retorno, 2);
    ELSE
      UPDATE o02agpe1.tb_historico_cargas hc
         set hc.fecha_fin = sysdate, hc.estado = 'ERROR'
       WHERE hc.id = idHistorico;
      PQ_Utl.LOG(lc, 'Resultado: ERROR ' || v_retorno, 2);
    END IF;
  
    PQ_Utl.LOG(lc, '----------' || v_salto, 2);
  
    -- Escribe el resumen de actualizacion en la salida estandar para el posterior envio de correo
    IF (ejecucionOK) then
      PQ_Utl.LOG(lc, '-- RESULTADO DEL PROCESO DE ACTUALIZACION --', 2);
      PQ_Utl.LOG(lc, v_cuerpo, 2);
    END IF;
  
    COMMIT;
  
  end actualizarHistorico;

  ---------------------------------------------------------------------
  -- Carga en un array los origenes de datos que se van a actualizar --
  ---------------------------------------------------------------------
  procedure cargarOrigenesDatos(v_anios_actualizar IN NUMBER,
                                v_plan_actual      IN NUMBER) as
  
    v_aux          VARCHAR2(10);
    v_anio_inicial NUMBER(4) := v_plan_actual - v_anios_actualizar;
  
  begin
    IF (v_act_completa = TRUE) THEN
      v_aux := NULL;
    ELSE
      v_aux := '8';
    END IF;
  
    origenesDatosBackups := arrayTablas('TB_INF_EXPLOTACIONES',
                                        'TB_INF_EXPLOTACIONES_ANEXO',
                                        'tb_capaseg_mod',
                                        'tb_dist_coste_parcelas',
                                        'tb_inf_polizas',
                                        'tb_inf_polizas_coberturas',
                                        'tb_inf_asegurados',
                                        'tb_inf_asegurados_socios',
                                        'tb_inf_colectivos',
                                        'tb_inf_siniestros',
                                        'tb_inf_siniestros_parcela',
                                        'tb_inf_anexos_modificacion',
                                        'tb_inf_anexos_modif_parcel',
                                        'tb_inf_anexos_modif_cobert',
                                        'tb_inf_anexo_red',
                                        'tb_inf_anexo_red_parcelas',
                                        'tb_inf_distribucion_costes',
                                        'tb_polizas',
                                        'tb_parcelas',
                                        'tb_capitales_asegurados',
                                        'tb_colectivos',
                                        'tb_inf_polizas_parcelas',
                                        'tb_inf_cultivo_var_linea',
                                        'TB_INF_DATOS_ASEGURADO',
                                        'TB_INF_COMISIONES_POLIZA',
                                        'TB_INF_DIST_COSTES_2015',
                                        'TB_INF_EXPLOTACIONES_COBERTURAS',
                                        'TB_INF_PARCELAS_COBERTURAS',
										'TB_INF_PARC_DIST_COSTE',
										'TB_INF_EXPL_DIST_COSTE',
                                        'TB_INF_PARC_DIST_COSTE_GN',
                                        'TB_INF_PARC_DIST_COSTE_SU',
                                        'TB_INF_PARC_DIST_COSTE_BR',
                                        'TB_INF_EXPL_DIST_COSTE_GN',
                                        'TB_INF_EXPL_DIST_COSTE_SU',
                                        'TB_INF_EXPL_DIST_COSTE_BR');
  
    origenesDatos := arrayTablasEstado(arrayTablasEstadoInt('tb_inf_siniestros_parcela t',
                                                            NULL,
                                                            v_numreg_siniestros_parc,
                                                            0,
                                                            ' where t.idsiniestro in (select id from o02agpe1.tb_inf_siniestros t1 where t1.codplan between ' ||
                                                            v_anio_inicial ||
                                                            ' and ' ||
                                                            v_plan_actual || ')'),
                                       arrayTablasEstadoInt('tb_inf_anexos_modif_parcel t',
                                                            NULL,
                                                            v_numreg_am_par,
                                                            0,
                                                            ' where t.idanexo in (select idanexo from o02agpe1.tb_inf_anexos_modificacion t1 where t1.codplan between ' ||
                                                            v_anio_inicial ||
                                                            ' and ' ||
                                                            v_plan_actual || ')'),
                                       arrayTablasEstadoInt('tb_inf_anexos_modif_cobert t',
                                                            NULL,
                                                            v_numreg_am_cob,
                                                            0,
                                                            ' where t.idanexo in (select idanexo from o02agpe1.tb_inf_anexos_modificacion t1 where t1.codplan between ' ||
                                                            v_anio_inicial ||
                                                            ' and ' ||
                                                            v_plan_actual || ')'),
                                       arrayTablasEstadoInt('tb_inf_anexo_red_parcelas t',
                                                            NULL,
                                                            v_numreg_arp,
                                                            0,
                                                            ' where t.idanexo in (select id from o02agpe1.tb_inf_anexo_red t1 where t1.codplan between ' ||
                                                            v_anio_inicial ||
                                                            ' and ' ||
                                                            v_plan_actual || ')'),
                                       arrayTablasEstadoInt('tb_inf_distribucion_costes t',
                                                            NULL,
                                                            v_numreg_dist_coste,
                                                            0,
                                                            ' where t.idpoliza in (select distinct idpoliza from o02agpe1.tb_inf_polizas t1 where t1.codplan between ' ||
                                                            v_anio_inicial ||
                                                            ' and ' ||
                                                            v_plan_actual || ')'),
                                       arrayTablasEstadoInt('tb_inf_polizas_parcelas t',
                                                            v_aux,
                                                            v_numreg_ca,
                                                            1,
                                                            ' where t.idpoliza in (select distinct idpoliza from o02agpe1.tb_inf_polizas t1 where t1.codplan between ' ||
                                                            v_anio_inicial ||
                                                            ' and ' ||
                                                            v_plan_actual || ')'),
                                       arrayTablasEstadoInt('TB_INF_COMISIONES_POLIZA t',
                                                            NULL,
                                                            v_numreg_comis_poliza,
                                                            0,
                                                            ' where t.idpoliza in (select distinct idpoliza from o02agpe1.tb_inf_polizas t1 where t1.codplan between ' ||
                                                            v_anio_inicial ||
                                                            ' and ' ||
                                                            v_plan_actual || ')'),
                                       arrayTablasEstadoInt('tb_inf_dist_costes_2015 t',
                                                            NULL,
                                                            v_numreg_DC_2015,
                                                            0,
                                                            ' where t.idpoliza in (select distinct pol.idpoliza from o02agpe1.tb_inf_polizas pol where pol.codplan between ' ||
                                                            v_anio_inicial ||
                                                            ' and ' ||
                                                            v_plan_actual || ')'),
                                       arrayTablasEstadoInt('TB_INF_EXPLOTACIONES t',
                                                            NULL,
                                                            v_numreg_Explotaciones,
                                                            0,
                                                            ' where t.idpoliza in (select distinct idpoliza from o02agpe1.tb_inf_polizas t1 where t1.codplan between ' ||
                                                            v_anio_inicial ||
                                                            ' and ' ||
                                                            v_plan_actual || ')'),
                                       arrayTablasEstadoInt('TB_INF_EXPLOTACIONES_ANEXO t',
                                                            NULL,
                                                            v_numreg_Explo_Anexo,
                                                            0,
                                                            ' where t.idanexo in (select distinct idanexo from o02agpe1.tb_inf_anexos_modificacion t1 where t1.codplan between ' ||
                                                            v_anio_inicial ||
                                                            ' and ' ||
                                                            v_plan_actual || ')'),
                                       arrayTablasEstadoInt('TB_INF_EXPLOTACIONES_COBERTURAS t',
                                                            NULL,
                                                            v_numreg_expl_cob,
                                                            0,
                                                            ' where t.idpoliza in (select distinct idpoliza from o02agpe1.tb_inf_polizas t1 where t1.codplan between ' ||
                                                            v_anio_inicial ||
                                                            ' and ' ||
                                                            v_plan_actual || ')'),
                                       arrayTablasEstadoInt('TB_INF_PARCELAS_COBERTURAS t',
                                                            NULL,
                                                            v_numreg_parc_cob,
                                                            0,
                                                            ' where t.idpoliza in (select distinct idpoliza from o02agpe1.tb_inf_polizas t1 where t1.codplan between ' ||
                                                            v_anio_inicial ||
                                                            ' and ' ||
                                                            v_plan_actual || ')'),
                                       arrayTablasEstadoInt('tb_inf_polizas t',
                                                            v_aux,
                                                            v_numreg_plz,
                                                            0,
                                                            'where t.codplan between ' ||
                                                            v_anio_inicial ||
                                                            ' and ' ||
                                                            v_plan_actual),
                                       arrayTablasEstadoInt('tb_inf_polizas_coberturas t',
                                                            NULL,
                                                            v_numreg_coberturas,
                                                            0,
                                                            'where t.codplan between ' ||
                                                            v_anio_inicial ||
                                                            ' and ' ||
                                                            v_plan_actual),
                                       arrayTablasEstadoInt('tb_inf_asegurados t',
                                                            NULL,
                                                            v_numreg_aseg,
                                                            0,
                                                            NULL),
                                       arrayTablasEstadoInt('tb_inf_asegurados_socios t',
                                                            NULL,
                                                            v_numreg_aseg_socios,
                                                            0,
                                                            NULL),
                                       arrayTablasEstadoInt('tb_inf_colectivos t',
                                                            NULL,
                                                            v_numreg_colectivos,
                                                            0,
                                                            'where t.codplan between ' ||
                                                            v_anio_inicial ||
                                                            ' and ' ||
                                                            v_plan_actual),
                                       arrayTablasEstadoInt('tb_inf_siniestros t',
                                                            NULL,
                                                            v_numreg_siniestros,
                                                            0,
                                                            'where t.codplan between ' ||
                                                            v_anio_inicial ||
                                                            ' and ' ||
                                                            v_plan_actual),
                                       arrayTablasEstadoInt('tb_inf_anexos_modificacion t',
                                                            NULL,
                                                            v_numreg_am,
                                                            0,
                                                            'where t.codplan between ' ||
                                                            v_anio_inicial ||
                                                            ' and ' ||
                                                            v_plan_actual),
                                       arrayTablasEstadoInt('tb_inf_anexo_red t',
                                                            NULL,
                                                            v_numreg_ar,
                                                            0,
                                                            'where t.codplan between ' ||
                                                            v_anio_inicial ||
                                                            ' and ' ||
                                                            v_plan_actual),
                                       arrayTablasEstadoInt('tb_inf_cultivo_var_linea t',
                                                            NULL,
                                                            v_numreg_cultvar,
                                                            0,
                                                            'where t.codplan between ' ||
                                                            v_anio_inicial ||
                                                            ' and ' ||
                                                            v_plan_actual),
                                       arrayTablasEstadoInt('TB_INF_DATOS_ASEGURADO t',
                                                            NULL,
                                                            v_numreg_datos_aseg,
                                                            0,
                                                            NULL),
                                       -- LA POSICION 3 (NUM REGISTROS) NO SE USA... NO SE CAMBIA EN LAS ANTERIORES
                                       -- NI SE ELIMINA PARA NO AFECTAR A CODIGO EXISTENTE Y FUNCIONAL
                                       arrayTablasEstadoInt('TB_INF_PARC_DIST_COSTE t',
                                                            NULL,
                                                            0,
                                                            0,
                                                            NULL),
										arrayTablasEstadoInt('TB_INF_EXPL_DIST_COSTE t',
                                                            NULL,
                                                            0,
                                                            0,
                                                            NULL),															
									   arrayTablasEstadoInt('TB_INF_PARC_DIST_COSTE_GN t',
                                                            NULL,
                                                            0,
                                                            0,
                                                            NULL),
                                       arrayTablasEstadoInt('TB_INF_PARC_DIST_COSTE_SU t',
                                                            NULL,
                                                            0,
                                                            0,
                                                            NULL),
                                       arrayTablasEstadoInt('TB_INF_PARC_DIST_COSTE_BR t',
                                                            NULL,
                                                            0,
                                                            0,
                                                            NULL),
                                       arrayTablasEstadoInt('TB_INF_EXPL_DIST_COSTE_GN t',
                                                            NULL,
                                                            0,
                                                            0,
                                                            NULL),
                                       arrayTablasEstadoInt('TB_INF_EXPL_DIST_COSTE_SU t',
                                                            NULL,
                                                            0,
                                                            0,
                                                            NULL),
                                       arrayTablasEstadoInt('TB_INF_EXPL_DIST_COSTE_BR t',
                                                            NULL,
                                                            0,
                                                            0,
                                                            NULL));
  
  end cargarOrigenesDatos;

  -------------------------------------------------------------------
  -- Trunca las tablas de backup asociadas a los origenes de datos --
  -------------------------------------------------------------------
  procedure truncarBackups as
  
    contador INTEGER;
  
  begin
  
    -- Recorre el array de origenes de datos y los borra
    contador := origenesDatosBackups.FIRST;
    WHILE contador IS NOT NULL LOOP
      PQ_Utl.LOG(lc,
                 'Trunca la tabla ' || origenesDatosBackups(contador) ||
                 '_bak',
                 2);
      TRUNCATE_TABLE('o02agpe1.' || origenesDatosBackups(contador) ||
                     '_bak');
      contador := origenesDatosBackups.NEXT(contador);
    END LOOP;
  
  end truncarBackups;

  ----------------------------------
  -- Trunca los origenes de datos --
  ----------------------------------
  procedure truncarOrigenesDatos as
  
    contador  INTEGER;
    aux       arrayTablasEstadoInt;
    nom_tabla VARCHAR2(50);
  
  begin
  
    -- Recorre el array de origenes de datos y los borra
    contador := origenesDatos.FIRST;
    WHILE contador IS NOT NULL LOOP
      aux := origenesDatos(contador);
    
      -- Si la quinta posicion del array no es nula hay que filtrar por plan
      IF (aux(5) IS NULL) THEN
        -- Si la segunda posicion del array es nula hay que truncar la tabla
        IF (aux(2) IS NULL) THEN
          nom_tabla := REPLACE(aux(1), ' t', '');
          PQ_Utl.LOG(lc, 'Trunca la tabla ' || nom_tabla, 2);
          TRUNCATE_TABLE('o02agpe1.' || nom_tabla);
        ELSE
          -- Si contiene algun valor, hay que borrar los registros que no tengan el estado
          PQ_Utl.LOG(lc,
                     'Borra los registros con estado diferente a ' ||
                     aux(2),
                     2);
          EXECUTE IMMEDIATE ('DELETE o02agpe1.' || aux(1) ||
                            ' WHERE t.IDESTADO <> ' || aux(2));
          COMMIT;
        END IF;
      ELSE
        -- Si la segunda posicion del array es nula hay que borrar todos los registros de los planes
        IF (aux(2) IS NULL) THEN
          PQ_Utl.LOG(lc,
                     'Borra los registros de los planes afectados en ' ||
                     aux(1),
                     2);
          EXECUTE IMMEDIATE ('DELETE o02agpe1.' || aux(1) || ' ' || aux(5));
        ELSE
          -- Si contiene algun valor, hay que borrar los registros que no tengan el estado
          PQ_Utl.LOG(lc,
                     'Borra los registros con estado diferente a ' ||
                     aux(2),
                     2);
          EXECUTE IMMEDIATE ('DELETE o02agpe1.' || aux(1) || ' ' || aux(5) ||
                            ' AND t.IDESTADO <> ' || aux(2));
        END IF;
        /* ESC-15280 */
        COMMIT;
      END IF;
    
      contador := origenesDatos.NEXT(contador);
    END LOOP;
  
  EXCEPTION
    WHEN OTHERS THEN
      PQ_Utl.LOG(lc,
                 'Error al truncar los origenes de datos: ' || SQLCODE ||
                 ' - ' || SQLERRM || '. ',
                 2);
      -- Se vuelve a lanzar la excepcion para que cancele la cadena
      RAISE;
    
  end truncarOrigenesDatos;

  ----------------------------------------------------------------------
  -- Actualiza las tablas de backup asociadas a los origenes de datos --
  ----------------------------------------------------------------------
  procedure actualizarBackups(v_anios_actualizar IN NUMBER,
                              v_plan_actual      IN NUMBER) as
  
    contador INTEGER;
  
  begin
  
    -- Recorre el array de origenes de datos y ejecuta la funcion de actualizacion asociada
    contador := origenesDatosBackups.FIRST;
    WHILE contador IS NOT NULL LOOP
      PQ_Utl.LOG(lc,
                 'Actualiza la tabla ' || origenesDatosBackups(contador) ||
                 '_bak',
                 2);
      execute immediate ('begin o02agpe1.pq_carga_datos_informes.act_' ||
                        origenesDatosBackups(contador) || '( ' ||
                        v_anios_actualizar || ' , ' || v_plan_actual ||
                        ' ); end;');
      contador := origenesDatosBackups.NEXT(contador);
    END LOOP;
  
  EXCEPTION
    WHEN OTHERS THEN
      PQ_Utl.LOG(lc,
                 'Error al actualizar las tablas de backup: ' || SQLCODE ||
                 ' - ' || SQLERRM || '. ',
                 2);
      -- Se vuelve a lanzar la excepcion para que cancele la cadena
      RAISE;
    
  end actualizarBackups;

  ---------------------------------------------------------------------
  -- Actualiza las tablas principales con los datos de las de backup --
  ---------------------------------------------------------------------
  procedure actualizarTablasPrincipales as
  
    contador  INTEGER;
    aux       arrayTablasEstadoInt;
    nom_tabla VARCHAR2(50);
  
  begin
    contador := origenesDatos.FIRST;
    WHILE contador IS NOT NULL LOOP
      aux       := origenesDatos(contador);
      nom_tabla := REPLACE(aux(1), ' t', '');
    
      PQ_Utl.LOG(lc, 'Actualiza la tabla ' || nom_tabla, 2);
      -- Actualiza la tabla principal
      IF (aux(4) = 1) THEN
        PQ_Utl.LOG(lc, 'Actualiza tabla por entidades ', 2);
        insertDeSelectPorEntidad(esqDest => 'o02agpe1',
                                 tbDest  => nom_tabla,
                                 esqOrig => 'o02agpe1',
                                 tbOrig  => nom_tabla || '_bak t');
      ELSE
        insertDeSelect(esqDest => 'o02agpe1',
                       tbDest  => nom_tabla,
                       esqOrig => 'o02agpe1',
                       tbOrig  => nom_tabla || '_bak t');
      
      END IF;
      -- Genera el mensaje con el numero de registros de la tabla para el correo
      setMsgCount(aux(1), aux(3));
    
      contador := origenesDatos.NEXT(contador);
    END LOOP;
  
  EXCEPTION
    WHEN OTHERS THEN
      PQ_Utl.LOG(lc,
                 'Error al actualizar las tablas principales: ' || SQLCODE ||
                 ' - ' || SQLERRM || '. ',
                 2);
      -- Se vuelve a lanzar la excepcion para que cancele la cadena
      RAISE;
  end actualizarTablasPrincipales;

  --------------------------------------------------------------------
  -- Actualiza los datos de la tabla de backup 'tb_inf_polizas_bak' --
  --------------------------------------------------------------------
  procedure act_tb_inf_polizas(v_anios_actualizar IN NUMBER,
                               v_plan_actual      IN NUMBER) as
  
    /* Pet. 62719 ** MODIF TAM (20.01.2020) ** Inicio */
    /* No se incluye la carga de datos de asegurados que estan bloqueados */
  
    /* Pet. 70105 (Fase III) ** MODIF TAM (01.03.2021) ** Inicio */
    /* Se incluye la cuenta de Siniestros en la tabla de Informes de Polizas (SINIESTROS_IBAN)*/
  
    v_anio_inicial NUMBER(4) := v_plan_actual - v_anios_actualizar;
  
    v_consulta varchar2(32000) := 'select p.IDPOLIZA, c.codentidad, p.OFICINA, p.CODUSUARIO, l.codplan,
          l.codlinea, c.idcolectivo, c.nomcolectivo, em.codentidad, em.nomentidad, sm.codsubentidad,
          sm.nomsubentidad, p.clase, p.referencia, p.importe, p.codmodulo, a.nifcif,
          nvl(a.razonsocial, a.nombre || '' '' || a.apellido1 || '' '' || a.apellido2),
          p.idestado, es.desc_estado, p.pagada, pp.formapago PAGO_FORMAPAGO, pp.fecha PAGO_FECHA,
          CASE
            WHEN pp.iban is not null  and pp.cccbanco is not null THEN
                (pp.IBAN || '' '' || SUBSTR(pp.cccbanco,1,4) || '' '' || SUBSTR(pp.cccbanco,5,4)
                ||'' '' || SUBSTR(pp.cccbanco,9,4) || '' '' || SUBSTR(pp.cccbanco,13,4)
                ||'' '' || SUBSTR(pp.cccbanco,17,4))
            ELSE NUll
          END AS IBAN,
          CASE
            WHEN pp.iban2 is not null  and pp.cccbanco2 is not null THEN
                (pp.IBAN2 || '' '' || SUBSTR(pp.cccbanco2,1,4) || '' '' || SUBSTR(pp.cccbanco2,5,4)
                ||'' '' || SUBSTR(pp.cccbanco2,9,4) || '' '' || SUBSTR(pp.cccbanco2,13,4)
                ||'' '' || SUBSTR(pp.cccbanco2,17,4))
            ELSE NUll
          END AS IBANSINIESTRO,
          pp.pctprimerpago PAGO_PCTPRIMERPAGO, pp.fechasegundopago PAGO_FECHASEGUNDOPAGO,
          pp.pctsegundopago PAGO_PCTSEGUNDOPAGO, p.idasegurado, p.fechaenvio, u.delegacion,
          CASE
            WHEN  mp.renovable = 1 OR pr.referencia IS NOT NULL then ''S''
            ELSE ''N''
          END AS Renovable,
          p.FECHA_VIGOR,
          p.IDESTADO_AGRO,
          p.FECHA_SEGUIMIENTO,
          p.NOTA_PREVIA,
          p.IPID,
          p.RGPD
        from o02agpe0.tb_polizas p
          inner join o02agpe0.tb_lineas l ON l.codplan BETWEEN ' || v_anio_inicial  || ' and ' || v_plan_actual || ' and p.lineaseguroid = l.lineaseguroid
          inner join o02agpe0.tb_asegurados a ON a.id not in (select blq.id_asegurado from o02agpe0.tb_bloqueos_asegurados blq where blq.idestado_aseg =''B'') and p.idasegurado = a.id
          inner join o02agpe0.tb_colectivos c ON p.idcolectivo = c.id
          left outer join o02agpe0.tb_subentidades_mediadoras sm ON c.entmediadora = sm.codentidad and c.subentmediadora = sm.codsubentidad
          left outer join o02agpe0.tb_entidades_mediadoras em ON sm.codentidad = em.codentidad
          left outer join o02agpe0.tb_pagos_poliza pp ON p.idpoliza = pp.idpoliza
          inner join o02agpe0.tb_estados_poliza es ON p.idestado = es.idestado
          left outer join o02agpe0.tb_usuarios u ON p.codusuario = u.codusuario
          left outer join o02agpe0.TB_MODULOS_POLIZA mp ON p.idpoliza= mp.idpoliza
          left outer join o02agpe0.tb_polizas_renovables pr ON p.referencia= pr.REFERENCIA
           where p.idestado != 0';
  
    v_group_by varchar2(10000) := 'group by  p.IDPOLIZA, c.codentidad, p.OFICINA, p.CODUSUARIO, l.codplan, l.codlinea, c.idcolectivo, c.nomcolectivo, em.codentidad, em.nomentidad, sm.codsubentidad,
          sm.nomsubentidad, p.clase, p.referencia, p.importe, p.codmodulo, a.nifcif, a.razonsocial, a.nombre, a.apellido1,a.apellido2,
          p.idestado, es.desc_estado, p.pagada, pp.formapago , pp.fecha,
          pp.iban, pp.cccbanco, pp.iban2, pp.cccbanco2, pp.pctprimerpago, pp.fechasegundopago,
          pp.pctsegundopago, p.idasegurado, p.fechaenvio, u.delegacion,mp.renovable, pr.referencia, p.FECHA_VIGOR, p.IDESTADO_AGRO, p.FECHA_SEGUIMIENTO,
          p.NOTA_PREVIA, p.IPID, p.RGPD';
  
    TYPE cur_typ IS REF CURSOR;
    c_polizas cur_typ;
  
    v_idpoliza         number(15);
    v_codentidad       number(4);
    v_oficina          varchar2(4);
    v_codusuario       varchar2(8);
    v_codplan          number(4);
    v_codlinea         number(3);
    v_idcolectivo      varchar2(7);
    v_nomcolectivo     varchar2(50);
    v_em_codentidad    number(4);
    v_em_nomentidad    varchar2(30);
    v_sm_codsubentidad number(4);
    v_sm_nomsubentidad varchar2(100);
    v_clase            number(3);
    v_referencia       varchar2(7);
    v_importe          number(11, 2);
    v_codmodulo        varchar2(5);
    v_nifcif           varchar2(9);
    v_nomasegurado     varchar2(100);
    v_estado           number(15);
    v_desc_estado      varchar2(30);
    v_pagada           number(1);
    v_formapago        varchar2(1);
    v_fechapago        date;
    v_cccbanco         varchar2(30); -- iban FORMATEADO(cambiado de 20 a 30)
    v_pctprimerpago    number(5, 2);
    v_fechasegundopago date;
    v_pctsegundopago   number(5, 2);
    v_idasegurado      number(15);
    v_delegacion       number(4);
    v_renovable        varchar2(1);
    v_fechavigor       date;
    --estas se obtienen dentro del bucle que recorre las polizas
    v_fecha_alta          date;
    v_fecha_envio         date;
    v_fecha_provisional   date;
    v_fecha_definitiva    date;
    v_usuario_provisional varchar2(8);
    v_usuario_definitiva  varchar2(8);
    v_superficie_tot      number;
    v_produccion_tot      number;
    -- Pet. 70105 (Fase III) ** MODIF TAM (01.03.2021) ** Inicio
    v_iban_siniestros varchar2(30); -- iban Siniestros FORMATEADO(cambiado de 20 a 30)
  
    -- Pet. 50777 ** MODIF (19.03.2019) ** Nuevos campos
    v_estado_agro       number(3);
    v_fecha_seguimiento date;
  
    -- Pet. 49295 **
    v_nota_previa number(1);
    v_ipid        number(1);
    v_rgpd        number(1);
  
  begin
  
    PQ_Utl.LOG(lc, 'procedure act_tb_inf_polizas', 2);
    PQ_Utl.LOG(lc, 'anhos a actualizar = ' || v_anios_actualizar, 2);
    PQ_Utl.LOG(lc, 'anho actual = ' || v_plan_actual, 2);
    PQ_Utl.LOG(lc,
               'anho inical (anho actual - anhos a actualizar)= ' ||
               v_anio_inicial,
               2);
  
    -- Si la actualizacion es parcial se anhade a la consulta la condicion para que solo se carguen
    -- las polizas no contratadas y las contratadas despues de la ultima ejecucion correcta
    -- del proceso
    IF (v_act_completa = FALSE) THEN
      v_consulta := v_consulta ||
                    ' and p.idpoliza not in (select distinct idpoliza from o02agpe1.TB_INF_POLIZAS)';
    END IF;
  
    v_consulta := v_consulta || v_group_by;
  
    OPEN c_polizas FOR v_consulta;
    LOOP
      FETCH c_polizas
        INTO v_idpoliza, v_codentidad, v_oficina, v_codusuario, v_codplan, v_codlinea, v_idcolectivo, v_nomcolectivo, v_em_codentidad, v_em_nomentidad, v_sm_codsubentidad, v_sm_nomsubentidad, v_clase, v_referencia, v_importe, v_codmodulo, v_nifcif, v_nomasegurado, v_estado, v_desc_estado, v_pagada, v_formapago, v_fechapago, v_cccbanco, v_iban_siniestros, v_pctprimerpago, v_fechasegundopago, v_pctsegundopago, v_idasegurado, v_fecha_envio, v_delegacion, v_renovable, v_fechavigor, v_estado_agro, v_fecha_seguimiento, v_nota_previa, v_ipid, v_rgpd;
      EXIT WHEN c_polizas%NOTFOUND;
    
      -- Fecha de alta
      BEGIN
        select p.fecha
          into v_fecha_alta
          from o02agpe1.tb_plz_hist_alta_bak p
         where p.idpoliza = v_idpoliza;
      EXCEPTION
        WHEN OTHERS THEN
          v_fecha_alta := null;
      END;
    
      -- Fecha provisional
      BEGIN
        select p.fecha, p.codusuario
          into v_fecha_provisional, v_usuario_provisional
          from o02agpe1.tb_plz_hist_prov_bak p
         where p.idpoliza = v_idpoliza;
      EXCEPTION
        WHEN OTHERS THEN
          v_fecha_provisional   := null;
          v_usuario_provisional := null;
      END;
    
      -- Fecha de paso a definitiva
      BEGIN
        select p.fecha, p.codusuario
          into v_fecha_definitiva, v_usuario_definitiva
          from o02agpe1.tb_plz_hist_def_bak p
         where p.idpoliza = v_idpoliza;
      EXCEPTION
        WHEN OTHERS THEN
          v_fecha_definitiva   := null;
          v_usuario_definitiva := null;
      END;
    
      --total de superficie y produccion (la produccion es de las complementarias)
      select sum(ca.superficie) superficie_tot,
             sum(ca.produccion) produccion_tot
        into v_superficie_tot, v_produccion_tot
        from o02agpe0.tb_parcelas par, o02agpe0.tb_capitales_asegurados ca
       where ca.idparcela = par.idparcela
         and par.idpoliza = v_idpoliza;
    
      -- MPM 22/02/2017 -
      -- Si la produccion obtenida en el punto anterior es 0, se comprueba si hay dato de produccion
      -- en la tabla tb_cap_aseg_rel_modulo para la poliza
      if (v_produccion_tot is null or v_produccion_tot = 0) then
        --Calculamos la produccion para polizas principales
        -- MPM 22/02/2017 -
        -- Si la produccion obtenida es null devuelve un 0
        select nvl(sum(carm.produccion), 0) produccion_tot_carm
          into v_produccion_tot
          from o02agpe0.tb_cap_aseg_rel_modulo  carm,
               o02agpe0.tb_capitales_asegurados ca,
               o02agpe0.tb_parcelas             par,
               o02agpe0.tb_polizas              p
         where carm.idcapitalasegurado = ca.idcapitalasegurado
           and ca.idparcela = par.idparcela
           and par.idpoliza = p.idpoliza
           and p.idpoliza = v_idpoliza
           and carm.codmodulo = v_codmodulo;
      end if;
    
      --Insertamos el registro en la talba
      insert into o02agpe1.TB_INF_POLIZAS_BAK
      values
        (v_idpoliza,
         v_codentidad,
         v_oficina,
         v_codusuario,
         v_codplan,
         v_codlinea,
         v_idcolectivo,
         v_nomcolectivo,
         v_em_codentidad,
         v_em_nomentidad,
         v_sm_codsubentidad,
         v_sm_nomsubentidad,
         v_clase,
         v_referencia,
         v_importe,
         v_codmodulo,
         v_nifcif,
         v_nomasegurado,
         v_estado,
         v_desc_estado,
         v_pagada,
         v_fecha_alta,
         v_fecha_envio,
         v_fecha_provisional,
         v_fecha_definitiva,
         v_usuario_provisional,
         v_usuario_definitiva,
         v_superficie_tot,
         v_produccion_tot,
         v_formapago,
         v_fechapago,
         v_cccbanco,
         v_pctprimerpago,
         v_fechasegundopago,
         v_pctsegundopago,
         v_idasegurado,
         v_delegacion,
         v_renovable,
         null,
         v_fechavigor,
         null,
         null,
         v_estado_agro,
         v_fecha_seguimiento,
         v_nota_previa,
         v_ipid,
         v_rgpd,
         v_iban_siniestros);
    
    END LOOP;
    close c_polizas;
  
    commit;
  
  EXCEPTION
    WHEN OTHERS THEN
      PQ_Utl.LOG(lc, 'ERROR ' || SQLCODE || ' - ' || SQLERRM || '. ', 2);
      ROLLBACK;
      -- Se vuelve a lanzar la excepcion para que cancele la cadena
      RAISE;
    
  end act_tb_inf_polizas;

  ---------------------------------------------------------------------------
  -- Actualiza los datos de la tabla de backup 'tb_inf_polizas_coberturas' --
  ---------------------------------------------------------------------------
  procedure act_tb_inf_polizas_coberturas(v_anios_actualizar IN NUMBER,
                                          v_plan_actual      IN NUMBER) as
  
    v_anio_inicial NUMBER(4) := v_plan_actual - v_anios_actualizar;
  
  begin
  
    PQ_Utl.LOG(lc, 'procedure act_tb_inf_polizas_coberturas', 2);
    PQ_Utl.LOG(lc, 'anhos a actualizar = ' || v_anios_actualizar, 2);
    PQ_Utl.LOG(lc, 'anho actual = ' || v_plan_actual, 2);
    PQ_Utl.LOG(lc,
               'anho inical (anho actual - anhos a actualizar)= ' ||
               v_anio_inicial,
               2);
  
    execute immediate ('insert into o02agpe1.TB_INF_POLIZAS_COBERTURAS_BAK
                                   select cp.idpoliza,
                                   l.codplan,
                                   l.codlinea,
                                   cp.codmodulo,
                                   mo.desmodulo,
                                   cp.codconceptoppalmod,
                                   cpm.desconceptoppalmod,
                                   cp.codriesgocubierto,
                                   rc.desriesgocubierto,
                                   cp.codconcepto,
                                   dd.nomconcepto,
                                   cp.codvalor,
                                   vw_dv.descvalordv,
                                   c.codentidad,
                                   p.oficina,
                                   p.codusuario,
                                   sm.codentidad,
                                   sm.codsubentidad,
                                   u.delegacion
                              from o02agpe0.tb_comparativas_poliza cp,
                                   o02agpe0.tb_lineas l,
                                   o02agpe0.tb_sc_c_modulos mo,
                                   o02agpe0.tb_sc_c_concepto_ppal_mod cpm,
                                   o02agpe0.tb_sc_c_riesgos_cubiertos rc,
                                   o02agpe0.tb_sc_dd_dic_datos dd,
                                   o02agpe0.tb_polizas p,
                                   o02agpe0.tb_colectivos c,
                                   o02agpe0.tb_subentidades_mediadoras sm,
                                   o02agpe0.tb_usuarios u,
                                   (select 106                     codconceptodv,
                                           ce.codcaractexplotacion codvalordv,
                                           ce.descaractexplotacion descvalordv
                                      from o02agpe0.tb_sc_c_caract_explotacion ce
                                    union
                                    -- 120 - % FRANQUICIA ELEGIBLE
                                    select 120                      codconceptodv,
                                           pfe.codpctfranquiciaeleg codvalordv,
                                           pfe.despctfranquiciaeleg descvalordv
                                      from o02agpe0.tb_sc_c_pct_franquicia_eleg pfe
                                    -- 121 - % MINIMO INDEMNIZABLE
                                    union
                                    select 121             codconceptodv,
                                           pmi.pctminindem codvalordv,
                                           pmi.desminindem descvalordv
                                      from o02agpe0.tb_sc_c_min_indem_eleg pmi
                                    -- 170 - TIPO FRANQUICIA
                                    union
                                    select 170 codconceptodv,
                                           to_number(tf.codtipofranquicia) codvalordv,
                                           tf.destipofranquicia descvalordv
                                      from o02agpe0.tb_sc_c_tipo_franquicia tf
                                    -- 174 - CALCULO INDEMINZACION
                                    union
                                    select 174 codconceptodv,
                                           to_number(ci.codcalculo) codvalordv,
                                           ci.descalculo descvalordv
                                      from o02agpe0.tb_sc_c_calc_indemnizacion ci
                                    -- 175 - GARANTIZADO
                                    union
                                    select 175              codconceptodv,
                                           g.codgarantizado codvalordv,
                                           g.desgarantizado descvalordv
                                      from o02agpe0.tb_sc_c_garantizado g
                                    -- 362 - % CAPITAL ASEGURADO
                                    union
                                    select 362                codconceptodv,
                                           cae.pctcapitalaseg codvalordv,
                                           cae.descapitalaseg descvalordv
                                      from o02agpe0.tb_sc_c_capital_aseg_eleg cae
                                    -- 363 - RIESGO CUBIERTO ELEGIDO
                                    union
                                    select 363 codconceptodv, -1 codvalordv, ''No'' descvalordv
                                      from dual
                                    union
                                    select 363 codconceptodv, -2 codvalordv, ''Si'' descvalordv
                                      from dual) vw_dv
                             where cp.lineaseguroid = l.lineaseguroid
                               and cp.codmodulo = mo.codmodulo
                               and cp.lineaseguroid = mo.lineaseguroid
                               and cp.codconceptoppalmod = cpm.codconceptoppalmod
                               and cp.codriesgocubierto = rc.codriesgocubierto
                               and cp.lineaseguroid = rc.lineaseguroid
                               and cp.codmodulo = rc.codmodulo
                               and cp.codconcepto = dd.codconcepto(+)
                               and cp.codconcepto = vw_dv.codconceptodv(+)
                               and cp.codvalor = vw_dv.codvalordv(+)
                               and cp.idpoliza = p.idpoliza
                               and p.idcolectivo = c.id
                               and c.entmediadora = sm.codentidad (+)
                               and c.subentmediadora = sm.codsubentidad (+)
                               and p.codusuario = u.codusuario (+)
                               and p.idestado != 0
							   AND l.codplan BETWEEN ' || v_anio_inicial || ' and ' || v_plan_actual || '
                             order by cp.codconcepto');
  
    commit;
  
  EXCEPTION
    WHEN OTHERS THEN
      ROLLBACK;
      -- Se vuelve a lanzar la excepcion para que cancele la cadena
      RAISE;
    
  end act_tb_inf_polizas_coberturas;

  -----------------------------------------------------------------------
  -- Actualiza los datos de la tabla de backup 'tb_inf_asegurados_bak' --
  -----------------------------------------------------------------------
  procedure act_tb_inf_asegurados(v_anios_actualizar IN NUMBER,
                                  v_plan_actual      IN NUMBER) as
  
    /* Pet. 62719 ** MODIF TAM (20.01.2020) ** Inicio */
    /* No se incluye la carga de datos de asegurados que estan bloqueados */
  begin
    execute immediate ('insert into o02agpe1.TB_INF_ASEGURADOS_BAK
                          select a.codentidad,
                                 e.nomentidad,
                                 a.nifcif,
                                 nvl(a.razonsocial,
                                     a.nombre || '' '' || a.apellido1 || '' '' || a.apellido2) asegurado,
                                 a.clavevia,
                                 a.direccion,
                                 a.numvia,
                                 a.bloque,
                                 a.escalera,
                                 a.codprovincia,
                                 p.nomprovincia,
                                 a.codlocalidad,
                                 loc.nomlocalidad,
                                 loc.sublocalidad,
                                 a.codpostal,
                                 a.telefono,
                                 a.movil,
                                 a.email,
                                 a.numsegsocial,
                                 a.regimensegsocial,
                                 a.atp,
                                 a.jovenagricultor,
                                 a.codusuario,
                                 a.id,
                                 a.piso,
                                 u.entmediadora,
                                 u.subentmediadora,
                                 u.delegacion
                            from o02agpe0.tb_asegurados  a,
                                 o02agpe0.tb_entidades   e,
                                 o02agpe0.tb_provincias  p,
                                 o02agpe0.tb_localidades loc,
                                 o02agpe0.tb_usuarios u
                           where a.codentidad = e.codentidad(+)
                             and a.codlocalidad = loc.codlocalidad
                             and a.codprovincia = loc.codprovincia
                             and a.sublocalidad = loc.sublocalidad
                             and loc.codprovincia = p.codprovincia
                             and a.codusuario = u.codusuario (+)
                             and a.id not in (select blq.id_asegurado from o02agpe0.tb_bloqueos_asegurados blq
                             where blq.idestado_aseg =''B'')');
  
    commit;
  
  EXCEPTION
    WHEN OTHERS THEN
      ROLLBACK;
      -- Se vuelve a lanzar la excepcion para que cancele la cadena
      RAISE;
    
  end act_tb_inf_asegurados;

  ------------------------------------------------------------------------------
  -- Actualiza los datos de la tabla de backup 'tb_inf_asegurados_socios_bak' --
  ------------------------------------------------------------------------------
  procedure act_tb_inf_asegurados_socios(v_anios_actualizar IN NUMBER,
                                         v_plan_actual      IN NUMBER) as
    /* Pet. 62719 ** MODIF TAM (20.01.2020) ** Inicio */
    /* No se incluye la carga de datos de asegurados que estan bloqueados */
  begin
    execute immediate ('insert into o02agpe1.TB_INF_ASEGURADOS_SOCIOS_BAK
                           select s.nif, nvl (s.razonsocial, s.nombre || '' '' || s.apellido1 || '' '' || s.apellido2) socio,
                           s.numsegsocial, s.regimensegsocial, s.atp, s.jovenagricultor, a.codentidad, a.id,
                           u.entmediadora,
                           u.subentmediadora,
                           u.delegacion
                           from o02agpe0.tb_socios s, o02agpe0.tb_asegurados a, o02agpe0.tb_usuarios u
                           where s.idasegurado = a.id and a.codusuario = u.codusuario (+)
                           and a.id not in (select blq.id_asegurado from o02agpe0.tb_bloqueos_asegurados blq
                             where blq.idestado_aseg =''B'')');
  
    commit;
  
  EXCEPTION
    WHEN OTHERS THEN
      ROLLBACK;
      -- Se vuelve a lanzar la excepcion para que cancele la cadena
      RAISE;
    
  end act_tb_inf_asegurados_socios;

  -----------------------------------------------------------------------
  -- Actualiza los datos de la tabla de backup 'tb_inf_colectivos_bak' --
  -----------------------------------------------------------------------
  procedure act_tb_inf_colectivos(v_anios_actualizar IN NUMBER,
                                  v_plan_actual      IN NUMBER) as
  
    v_anio_inicial NUMBER(4) := v_plan_actual - v_anios_actualizar;
  
  begin
  
    PQ_Utl.LOG(lc, 'procedure act_tb_inf_colectivos', 2);
    PQ_Utl.LOG(lc, 'anhos a actualizar = ' || v_anios_actualizar, 2);
    PQ_Utl.LOG(lc, 'anho actual = ' || v_plan_actual, 2);
    PQ_Utl.LOG(lc,
               'anho inical (anho actual - anhos a actualizar)= ' ||
               v_anio_inicial);
  
    execute immediate ('insert into o02agpe1.TB_INF_COLECTIVOS_BAK
                            select c.id, c.codentidad, e.nomentidad, l.codplan, l.codlinea, c.ciftomador,
                            t.razonsocial nombre_tomador, c.idcolectivo, c.nomcolectivo, c.entmediadora,
                            c.subentmediadora, c.activo, c.pctdescuentocol, c.pctprimerpago, c.fechaprimerpago,
                            c.pctsegundopago, c.fechasegundopago, c.entmediadora, c.subentmediadora,
                            CASE c.TIPO_DESC_RECARG
                                  WHEN 0 THEN ''D''
                                  WHEN 1 THEN ''R''
                                  ELSE ''N'' END as TIPO_DESC_RECARG,
                            c.PCT_DESC_RECARG
                           from o02agpe0.tb_colectivos c, o02agpe0.tb_entidades e, o02agpe0.tb_lineas l,
                           o02agpe0.tb_tomadores t
                           where c.codentidad = e.codentidad (+) and
                           c.lineaseguroid = l.lineaseguroid (+) and
                           c.codentidad = t.codentidad (+) and
                           c.ciftomador = t.ciftomador (+)
						   AND l.codplan BETWEEN ' || v_anio_inicial ||
                      ' and ' || v_plan_actual || '');
  
    commit;
  
  EXCEPTION
    WHEN OTHERS THEN
      ROLLBACK;
      -- Se vuelve a lanzar la excepcion para que cancele la cadena
      RAISE;
    
  end act_tb_inf_colectivos;

  -----------------------------------------------------------------------
  -- Actualiza los datos de la tabla de backup 'tb_inf_siniestros_bak' --
  -----------------------------------------------------------------------
  procedure act_tb_inf_siniestros(v_anios_actualizar IN NUMBER,
                                  v_plan_actual      IN NUMBER) as
    /* Pet. 62719 ** MODIF TAM (20.01.2020) ** Inicio */
    /* No se incluye la carga de datos de asegurados que estan bloqueados */
  
    v_anio_inicial NUMBER(4) := v_plan_actual - v_anios_actualizar;
  
  begin
  
    PQ_Utl.LOG(lc, 'procedure act_tb_inf_siniestros', 2);
    PQ_Utl.LOG(lc, 'anhos a actualizar = ' || v_anios_actualizar, 2);
    PQ_Utl.LOG(lc, 'anho actual = ' || v_plan_actual, 2);
    PQ_Utl.LOG(lc,
               'anho inical (anho actual - anhos a actualizar)= ' ||
               v_anio_inicial,
               2);
  
    execute immediate ('insert into o02agpe1.TB_INF_SINIESTROS_BAK
                          select s.id,
                                 p.idpoliza,
                                 t.codentidad,
                                 p.oficina,
                                 l.codplan,
                                 l.codlinea,
                                 p.referencia,
                                 p.dc,
                                 a.nifcif,
                                 NVL (UPPER (a.razonsocial), UPPER (a.nombre || '' '' || a.apellido1 || '' '' || a.apellido2)) nombre,
                                 com.fecha_envio fenvpol,
                                 s.numsiniestro,
                                 s.fechaocurrencia focurr,
                                 s.fecfirmasiniestro ffirma,
                                 es.idestado,
                                 es.descestado,
                                 (select comusin.fecha_envio
                                           from o02agpe0.tb_comunicaciones comusin
                                          where s.idenvio = comusin.idenvio) fenv,
                                 r.codriesgo,
                                 r.desriesgo,
                                 p.codusuario,
                                 c.entmediadora,
                                 c.subentmediadora,
                                 u.delegacion
                            FROM o02agpe0.tb_siniestros        s,
                                 o02agpe0.tb_polizas           p,
                                 o02agpe0.tb_colectivos        c,
                                 o02agpe0.tb_tomadores         t,
                                 o02agpe0.tb_lineas            l,
                                 o02agpe0.tb_asegurados        a,
                                 o02agpe0.tb_comunicaciones    com,
                                 o02agpe0.tb_estados_siniestro es,
                                 o02agpe0.tb_sc_c_riesgos      r,
                                 o02agpe0.tb_usuarios u
                           WHERE s.idpoliza = p.idpoliza(+)
                             and p.idcolectivo = c.id(+)
                             and c.codentidad = t.codentidad(+)
                             and c.ciftomador = t.ciftomador(+)
                             and p.lineaseguroid = l.lineaseguroid(+)
                             and p.idasegurado = a.id(+)
                             and p.idenvio = com.idenvio(+)
                             and s.estado = es.idestado(+)
                             and s.codriesgo = r.codriesgo(+)
                             and p.codusuario = u.codusuario (+)
                             and p.idestado != 0
                             and p.idasegurado not in (select blq.id_asegurado from o02agpe0.tb_bloqueos_asegurados blq
                                              where blq.idestado_aseg =''B'')
							 AND l.codplan BETWEEN ' || v_anio_inicial || ' and ' || v_plan_actual || '');
  
    commit;
  
  EXCEPTION
    WHEN OTHERS THEN
      ROLLBACK;
      -- Se vuelve a lanzar la excepcion para que cancele la cadena
      RAISE;
    
  end act_tb_inf_siniestros;

  --------------------------------------------------------------------------------
  -- Actualiza los datos de la tabla de backup 'tb_inf_siniestros_parcelas_bak' --
  --------------------------------------------------------------------------------
  procedure act_tb_inf_siniestros_parcela(v_anios_actualizar IN NUMBER,
                                          v_plan_actual      IN NUMBER) as
  
    v_anio_inicial NUMBER(4) := v_plan_actual - v_anios_actualizar;
  
  begin
  
    PQ_Utl.LOG(lc, 'procedure act_tb_inf_siniestros_parcela', 2);
    PQ_Utl.LOG(lc, 'anhos a actualizar = ' || v_anios_actualizar, 2);
    PQ_Utl.LOG(lc, 'anho actual = ' || v_plan_actual, 2);
    PQ_Utl.LOG(lc,
               'anho inical (anho actual - anhos a actualizar)= ' ||
               v_anio_inicial,
               2);
  
    execute immediate ('insert into o02agpe1.TB_INF_SINIESTROS_PARCELA_BAK
                          select p.referencia, s.numsiniestro, sp.hoja, sp.numero, sp.codprovincia, sp.codcomarca, sp.codtermino, sp.subtermino,
                          sp.codcultivo, sp.codvariedad, sp.codprovsigpac || ''-'' || sp.codtermsigpac || ''-'' || sp.agrsigpac || ''-'' || sp.zonasigpac ||
                          ''-'' || sp.poligonosigpac || ''-'' || sp.parcelasigpac || ''-'' || sp.recintosigpac sigpac, sp.nomparcela, sca.superficie,
                          sca.codtipocapital, nvl (dv_frutos.idsca_valor, ''N'') frutos, dv_fechas.idsca_fec_recol_valor fec_recol, s.id, col.codentidad,
                          p.oficina,
                          p.codusuario,
                          col.entmediadora,
                          col.subentmediadora,
                          u.delegacion
                          from o02agpe0.tb_siniestro_parcelas sp, o02agpe0.tb_siniestros s, o02agpe0.tb_polizas p, o02agpe0.tb_siniestro_cap_aseg sca, o02agpe0.tb_usuarios u, o02agpe0.tb_lineas lin,
                          (select scadv.idsiniestrocapaseg idsca_frutos, scadv.valor idsca_valor
                            from o02agpe0.tb_siniestro_cap_aseg_dv scadv
                           where scadv.codconcepto = 426
                             and scadv.valor = ''S'') dv_frutos,
                          (select scadv.idsiniestrocapaseg idsca_fec_recol, scadv.valor idsca_fec_recol_valor
                            from o02agpe0.tb_siniestro_cap_aseg_dv scadv
                           where scadv.codconcepto = 112
                             and scadv.valor is not null) dv_fechas, o02agpe0.tb_colectivos col
                          where sp.idsiniestro = s.id and
                          s.idpoliza = p.idpoliza and
                          sp.id = sca.idparcelasiniestro
                          and sp.altaensiniestro = ''S''
                          and sca.id = dv_frutos.idsca_frutos (+)
                          and sca.id = dv_fechas.idsca_fec_recol (+) and p.idcolectivo = col.id
                          and p.codusuario = u.codusuario (+)
                          and p.idestado != 0
						  AND p.lineaseguroid = lin.lineaseguroid
						  AND lin.codplan BETWEEN ' || v_anio_inicial || ' and ' || v_plan_actual || '');
  
    commit;
  
  EXCEPTION
    WHEN OTHERS THEN
      ROLLBACK;
      -- Se vuelve a lanzar la excepcion para que cancele la cadena
      RAISE;
    
  end act_tb_inf_siniestros_parcela;

  --------------------------------------------------------------------------------
  -- Actualiza los datos de la tabla de backup 'tb_inf_anexos_modificacion_bak' --
  --------------------------------------------------------------------------------
  procedure act_tb_inf_anexos_modificacion(v_anios_actualizar IN NUMBER,
                                           v_plan_actual      IN NUMBER) as
    /* Pet. 62719 ** MODIF TAM (20.01.2020) ** Inicio */
    /* No se incluye la carga de datos de asegurados que estan bloqueados */
  
    v_anio_inicial NUMBER(4) := v_plan_actual - v_anios_actualizar;
  
  begin
  
    PQ_Utl.LOG(lc, 'procedure act_tb_inf_anexos_modificacion', 2);
    PQ_Utl.LOG(lc, 'anhos a actualizar = ' || v_anios_actualizar, 2);
    PQ_Utl.LOG(lc, 'anho actual = ' || v_plan_actual, 2);
    PQ_Utl.LOG(lc,
               'anho inical (anho actual - anhos a actualizar)= ' ||
               v_anio_inicial,
               2);
  
    execute immediate ('insert into o02agpe1.TB_INF_ANEXOS_MODIFICACION_BAK
                            select
                            e.codentidad, p.oficina, l.codplan, l.codlinea, p.referencia, a.nifcif, nvl (a.razonsocial,
                            a.nombre || '' '' || a.apellido1 || '' '' || a.apellido2) asegurado, c.fecha_envio,
                            replace (replace (replace (trim (replace ((modif_par || '','' || modif_sub || '','' || modif_cob || '','' || modif_ase),'','','' '')),'' '','',''), '',,,'', '',''), '',,'', '','') modificacion,
                            am.fechafirmadoc, am.estado, e.descestado,
                            p.fechaenvio fechaenviopoliza,
                            am.id,
                            p.codusuario,
                            p.idpoliza,
                            col.entmediadora,
                            col.subentmediadora,
                            u.delegacion,
                            am.idestado_agro,
                            am.fecha_seguimiento
                            from o02agpe0.tb_anexo_mod am, o02agpe0.tb_polizas p, o02agpe0.tb_asegurados a, o02agpe0.tb_entidades e, o02agpe0.tb_lineas l, o02agpe0.tb_comunicaciones c,
                            o02agpe0.tb_colectivos col, o02agpe0.tb_usuarios u,
                            (select am.id, ''Parcelas'' modif_par from o02agpe0.tb_anexo_mod am where am.asunto like ''%PARCE%'') tab_par,
                            (select am.id, ''Subvenciones'' modif_sub from o02agpe0.tb_anexo_mod am where am.asunto like ''%SUBVE%'') tab_sub,
                            (select am.id, ''Coberturas'' modif_cob from o02agpe0.tb_anexo_mod am where am.asunto like ''%OTROS%'') tab_cob,
                            (select am.id, ''Asegurado'' modif_ase from o02agpe0.tb_anexo_mod am where am.asunto like ''%DOMICM%'') tab_ase,
                            o02agpe0.tb_anexo_mod_estados e
                            where am.idpoliza = p.idpoliza (+) and
                            p.idasegurado = a.id (+) and
                            a.codentidad = e.codentidad (+) and
                            p.lineaseguroid = l.lineaseguroid (+) and
                            am.idenvio = c.idenvio (+) and
                            am.id = tab_par.id (+) and
                            am.id = tab_sub.id (+) and
                            am.id = tab_cob.id (+) and
                            am.id = tab_ase.id (+) and
                            am.estado = e.idestado (+) and
                            p.idcolectivo = col.id (+) and
                            p.codusuario = u.codusuario (+)
                            and p.idestado != 0
                            and p.idasegurado not in (select blq.id_asegurado from o02agpe0.tb_bloqueos_asegurados blq
                                              where blq.idestado_aseg =''B'')
							AND l.codplan BETWEEN ' || v_anio_inicial ||
                      ' and ' || v_plan_actual || '');
  
    commit;
  
  EXCEPTION
    WHEN OTHERS THEN
      ROLLBACK;
      -- Se vuelve a lanzar la excepcion para que cancele la cadena
      RAISE;
    
  end act_tb_inf_anexos_modificacion;

  --------------------------------------------------------------------------------
  -- Actualiza los datos de la tabla de backup 'tb_inf_anexos_modif_parcel_bak' --
  --------------------------------------------------------------------------------
  procedure act_tb_inf_anexos_modif_parcel(v_anios_actualizar IN NUMBER,
                                           v_plan_actual      IN NUMBER) as
  
    v_anio_inicial NUMBER(4) := v_plan_actual - v_anios_actualizar;
  
  begin
  
    PQ_Utl.LOG(lc, 'procedure act_tb_inf_anexos_modif_parcel', 2);
    PQ_Utl.LOG(lc, 'anhos a actualizar = ' || v_anios_actualizar, 2);
    PQ_Utl.LOG(lc, 'anho actual = ' || v_plan_actual, 2);
    PQ_Utl.LOG(lc,
               'anho inical (anho actual - anhos a actualizar)= ' ||
               v_anio_inicial,
               2);
  
    execute immediate ('insert into o02agpe1.TB_INF_ANEXOS_MODIF_PARCEL_BAK
                          select p.referencia,
                           amp.hoja,
                           amp.numero,
                           amp.codprovincia,
                           amp.codcomarca,
                           amp.codtermino,
                           amp.subtermino,
                           amp.codcultivo,
                           amp.codvariedad,
                           amp.codprovsigpac || '' - '' || amp.codtermsigpac || '' - '' ||
                           amp.agrsigpac || '' - '' || amp.zonasigpac || '' - '' ||
                           amp.poligonosigpac || '' - '' || amp.parcelasigpac || '' - '' ||
                           amp.recintosigpac sigpac,
                           amca.superficie,
                           amca.precio,
                           amca.produccion,
                           tc.destipocapital,
                           amp.tipomodificacion estado,
                           amp.idanexo,
                           c.codentidad,
                           p.oficina,
                           p.codusuario,
                           c.entmediadora,
                           c.subentmediadora,
                           u.delegacion
                      from o02agpe0.tb_anexo_mod_parcelas       amp,
                           o02agpe0.tb_polizas                  p,
                           o02agpe0.tb_anexo_mod                am,
                           o02agpe0.tb_anexo_mod_capitales_aseg amca,
                           o02agpe0.tb_sc_c_tipo_capital        tc,
                           o02agpe0.tb_colectivos               c,
                           o02agpe0.tb_usuarios                 u,
					       o02agpe0.tb_lineas 					lin
                     where amp.tipomodificacion is not null
                       and amp.idanexo = am.id
                       and am.idpoliza = p.idpoliza
                       and amp.id = amca.idparcelaanexo(+)
                       and amca.codtipocapital = tc.codtipocapital(+)
                       and p.idcolectivo = c.id
                       and p.codusuario = u.codusuario(+)
                       and p.idestado != 0
					   AND p.lineaseguroid = lin.lineaseguroid
					   AND lin.codplan BETWEEN ' || v_anio_inicial || ' and ' || v_plan_actual || '');
  
    commit;
  
  EXCEPTION
    WHEN OTHERS THEN
      ROLLBACK;
      -- Se vuelve a lanzar la excepcion para que cancele la cadena
      RAISE;
    
  end act_tb_inf_anexos_modif_parcel;

  --------------------------------------------------------------------------------
  -- Actualiza los datos de la tabla de backup 'tb_inf_anexos_modif_cobert_bak' --
  --------------------------------------------------------------------------------
  procedure act_tb_inf_anexos_modif_cobert(v_anios_actualizar IN NUMBER,
                                           v_plan_actual      IN NUMBER) as
  
    v_anio_inicial NUMBER(4) := v_plan_actual - v_anios_actualizar;
  
  begin
  
    PQ_Utl.LOG(lc, 'procedure act_tb_inf_anexos_modif_cobert', 2);
    PQ_Utl.LOG(lc, 'anhos a actualizar = ' || v_anios_actualizar, 2);
    PQ_Utl.LOG(lc, 'anho actual = ' || v_plan_actual, 2);
    PQ_Utl.LOG(lc,
               'anho inical (anho actual - anhos a actualizar)= ' ||
               v_anio_inicial,
               2);
  
    execute immediate ('insert into o02agpe1.TB_INF_ANEXOS_MODIF_COBERT_BAK
                          SELECT
								p.idpoliza,
								am.codmodulo,
								mo.desmodulo,
								amc.codconceptoppalmod,
								cpm.desconceptoppalmod,
								amc.codriesgocubierto,
								rc.desriesgocubierto,
								amc.codconcepto,
								dd.nomconcepto,
								amc.codvalor,
								vw_dv.descvalordv,
								c.codentidad,
								am.id,
								p.oficina,
								p.codusuario,
								c.entmediadora,
								c.subentmediadora,
								u.delegacion
							FROM
								o02agpe0.tb_anexo_mod_coberturas      amc,
								o02agpe0.tb_anexo_mod                 am,
								o02agpe0.tb_polizas                   p,
								o02agpe0.tb_sc_c_modulos              mo,
								o02agpe0.tb_sc_c_concepto_ppal_mod    cpm,
								o02agpe0.tb_sc_c_riesgos_cubiertos    rc,
								o02agpe0.tb_sc_dd_dic_datos           dd,
								(
									SELECT
										106                        codconceptodv,
										ce.codcaractexplotacion    codvalordv,
										ce.descaractexplotacion    descvalordv
									FROM
										o02agpe0.tb_sc_c_caract_explotacion ce
									UNION
							-- 110 - DESTINOS
									SELECT
										110                codconceptodv,
										dest.coddestino    codvalordv,
										dest.desdestino    descvalordv
									FROM
										o02agpe0.tb_sc_c_destinos dest
									UNION
							-- 116 - TIPO MARCO PLANTACION
									SELECT
										116                               codconceptodv,
										marc_plant.codtipomarcoplantac    codvalordv,
										marc_plant.destipomarcoplantac    descvalordv
									FROM
										o02agpe0.tb_sc_c_marco_plantacion marc_plant
									UNION
							-- 120 - % FRANQUICIA ELEGIBLE
									SELECT
										120                         codconceptodv,
										pfe.codpctfranquiciaeleg    codvalordv,
										pfe.despctfranquiciaeleg    descvalordv
									FROM
										o02agpe0.tb_sc_c_pct_franquicia_eleg pfe
							-- 121 - % MINIMO INDEMNIZABLE
									UNION
									SELECT
										121                codconceptodv,
										pmi.pctminindem    codvalordv,
										pmi.desminindem    descvalordv
									FROM
										o02agpe0.tb_sc_c_min_indem_eleg pmi
							-- 123 - SISTEMA DE CULTIVO
									UNION
									SELECT
										123                          codconceptodv,
										sis_cul.codsistemacultivo    codvalordv,
										sis_cul.dessistemacultivo    descvalordv
									FROM
										o02agpe0.tb_sc_c_sistema_cultivo sis_cul
							-- 124 - MEDIDA PREVENTIVA
									UNION
									SELECT
										124                             codconceptodv,
										med_prev.codmedidapreventiva    codvalordv,
										med_prev.desmedidapreventiva    descvalordv
									FROM
										o02agpe0.tb_sc_c_medida_preventiva med_prev
							-- 131 - SISTEMA DE CONDUCCION -
									UNION
									SELECT
										131                               codconceptodv,
										sist_cond.codsistemaconduccion    codvalordv,
										sist_cond.dessistemaconduccion    descvalordv
									FROM
										o02agpe0.tb_sc_c_sistema_conduccion sist_cond
							-- 133 - PRACTICA CULTURAL
									UNION
									SELECT
										133                               codconceptodv,
										pract_cult.codpracticacultural    codvalordv,
										pract_cult.despracticacultural    descvalordv
									FROM
										o02agpe0.tb_sc_c_practica_cultural pract_cult
							-- 170 - TIPO FRANQUICIA
									UNION
									SELECT
										170                                   codconceptodv,
										to_number(tf.codtipofranquicia)      codvalordv,
										tf.destipofranquicia                  descvalordv
									FROM
										o02agpe0.tb_sc_c_tipo_franquicia tf
							-- 173 - TIPO PLANTACION
									UNION
									SELECT
										173                          codconceptodv,
										t_plant.codtipoplantacion    codvalordv,
										t_plant.destipoplantacion    descvalordv
									FROM
										o02agpe0.tb_sc_c_tipo_plantacion t_plant
							-- 174 - CALCULO INDEMINZACION
									UNION
									SELECT
										174                            codconceptodv,
										to_number(ci.codcalculo)      codvalordv,
										ci.descalculo                  descvalordv
									FROM
										o02agpe0.tb_sc_c_calc_indemnizacion ci
							-- 175 - GARANTIZADO
									UNION
									SELECT
										175                 codconceptodv,
										g.codgarantizado    codvalordv,
										g.desgarantizado    descvalordv
									FROM
										o02agpe0.tb_sc_c_garantizado g
							-- 362 - % CAPITAL ASEGURADO
									UNION
									SELECT
										362                   codconceptodv,
										cae.pctcapitalaseg    codvalordv,
										cae.descapitalaseg    descvalordv
									FROM
										o02agpe0.tb_sc_c_capital_aseg_eleg cae
							-- 363 - RIESGO CUBIERTO ELEGIDO
									UNION
									SELECT
										363   codconceptodv,
										- 1    codvalordv,
										''No''  descvalordv
									FROM
										dual
									UNION
									SELECT
										363   codconceptodv,
										- 2    codvalordv,
										''Si''  descvalordv
									FROM
										dual
							-- 616 - SISTEMA DE PRODUCCION
									UNION
									SELECT
										616                               codconceptodv,
										sist_prod.codsistemaproduccion    codvalordv,
										sist_prod.dessistemaproduccion    descvalordv
									FROM
										o02agpe0.tb_sc_c_sistema_produccion sist_prod
							-- 617 - NUM ANIOS PODA
									UNION
									SELECT
										617                           codconceptodv,
										anios_poda.codnumaniospoda    codvalordv,
										anios_poda.desnumaniospoda    descvalordv
									FROM
										o02agpe0.tb_sc_c_num_anios_poda anios_poda
							-- 618 - CICLO DE CULTIVO
									UNION
									SELECT
										618                       codconceptodv,
										c_cult.codciclocultivo    codvalordv,
										c_cult.desciclocultivo    descvalordv
									FROM
										o02agpe0.tb_sc_c_ciclo_cultivo c_cult
							-- 620 - CODIGO REDUCCION RDTOS.
									UNION
									SELECT
										620                    codconceptodv,
										r_rdto.codreducrdto    codvalordv,
										r_rdto.desreducrdto    descvalordv
									FROM
										o02agpe0.tb_sc_c_cod_reduc_rdto r_rdto
							-- 621 - SISTEMA PROTECCION
									UNION
									SELECT
										621                               codconceptodv,
										sist_prot.codsistemaproteccion    codvalordv,
										sist_prot.dessistemaproteccion    descvalordv
									FROM
										o02agpe0.tb_sc_c_sistema_proteccion sist_prot
							-- 765 - CODIGO IGP
									UNION
									SELECT
										765                codconceptodv,
										igp.codigp         codvalordv,
										igp.descripcion    descvalordv
									FROM
										o02agpe0.tb_sc_c_igp igp
							-- 778 - TIPO INSTALACION
									UNION
									SELECT
										778                          codconceptodv,
										t_inst.codtipoinstalacion    codvalordv,
										t_inst.descripcion           descvalordv
									FROM
										o02agpe0.tb_sc_c_tipo_instalacion t_inst
							-- 873 - MATERIAL CUBIERTA
									UNION
									SELECT
										873                            codconceptodv,
										mat_cub.codmaterialcubierta    codvalordv,
										mat_cub.descripcion            descvalordv
									FROM
										o02agpe0.tb_sc_c_material_cubierta mat_cub
							-- 875 - MATERIAL ESTRUCTURA
									UNION
									SELECT
										875                              codconceptodv,
										mat_est.codmaterialestructura    codvalordv,
										mat_est.descripcion              descvalordv
									FROM
										o02agpe0.tb_sc_c_material_estructura mat_est
							-- 879 - CODIGO CERTIFICADO
									UNION
									SELECT
										879                              codconceptodv,
										cod_cert.codcertificadoinstal    codvalordv,
										cod_cert.descripcion             descvalordv
									FROM
										o02agpe0.tb_sc_c_certificado_instal cod_cert
								)                                     vw_dv,
								o02agpe0.tb_colectivos                c,
								o02agpe0.tb_usuarios                  u,
								o02agpe0.tb_lineas                    lin
							WHERE
									amc.idanexo = am.id (+)
								AND am.idpoliza = p.idpoliza (+)
								AND am.codmodulo = mo.codmodulo (+)
								AND p.lineaseguroid = mo.lineaseguroid
								AND amc.codconceptoppalmod = cpm.codconceptoppalmod (+)
								AND amc.codriesgocubierto = rc.codriesgocubierto (+)
								AND p.lineaseguroid = rc.lineaseguroid
								AND am.codmodulo = rc.codmodulo
								AND amc.codconcepto = dd.codconcepto (+)
								AND amc.codconcepto = vw_dv.codconceptodv (+)
								AND amc.codvalor = '''' || vw_dv.codvalordv (+)
								AND p.idcolectivo = c.id
								AND p.codusuario = u.codusuario (+)
								AND p.idestado != 0
								AND p.lineaseguroid = lin.lineaseguroid
								AND lin.codplan BETWEEN ' || v_anio_inicial || ' and ' || v_plan_actual || '');
  
    commit;
  
  EXCEPTION
    WHEN OTHERS THEN
      ROLLBACK;
      -- Se vuelve a lanzar la excepcion para que cancele la cadena
      RAISE;
    
  end act_tb_inf_anexos_modif_cobert;

  ----------------------------------------------------------------------
  -- Actualiza los datos de la tabla de backup 'tb_inf_anexo_red_bak' --
  ----------------------------------------------------------------------
  procedure act_tb_inf_anexo_red(v_anios_actualizar IN NUMBER,
                                 v_plan_actual      IN NUMBER) as
    /* Pet. 62719 ** MODIF TAM (20.01.2020) ** Inicio */
    /* No se incluye la carga de datos de asegurados que estan bloqueados */
  
    v_anio_inicial NUMBER(4) := v_plan_actual - v_anios_actualizar;
  
  begin
  
    PQ_Utl.LOG(lc, 'procedure act_tb_inf_anexo_red', 2);
    PQ_Utl.LOG(lc, 'anhos a actualizar = ' || v_anios_actualizar, 2);
    PQ_Utl.LOG(lc, 'anho actual = ' || v_plan_actual, 2);
    PQ_Utl.LOG(lc,
               'anho inical (anho actual - anhos a actualizar)= ' ||
               v_anio_inicial,
               2);
  
    execute immediate ('insert into o02agpe1.TB_INF_ANEXO_RED_BAK
                          select red.id,
                                 p.idpoliza,
                                 t.codentidad,
                                 p.oficina,
                                 l.codplan,
                                 l.codlinea,
                                 p.referencia,
                                 p.dc,
                                 a.nifcif,
                                 NVL (UPPER (a.razonsocial), UPPER (a.nombre || '' '' || a.apellido1
                                 || '' '' || a.apellido2)) nombre,
                                 com.fecha_envio fenvpol,
                                 red.numanexo,
                                 red.fechadanios,
                                 es.idestado,
                                 es.descestado,
                                 (select comusin.fecha_envio
                                           from o02agpe0.tb_comunicaciones comusin
                                          where red.idenvio = comusin.idenvio) fenv,
                                 r.codriesgo,
                                 r.desriesgo,
                                 p.codusuario,
                                 c.entmediadora,
                                 c.subentmediadora,
                                 u.delegacion
                            FROM o02agpe0.tb_anexo_red         red,
                                 o02agpe0.tb_polizas           p,
                                 o02agpe0.tb_colectivos        c,
                                 o02agpe0.tb_tomadores         t,
                                 o02agpe0.tb_lineas            l,
                                 o02agpe0.tb_asegurados        a,
                                 o02agpe0.tb_comunicaciones    com,
                                 o02agpe0.tb_anexo_red_estados es,
                                 o02agpe0.tb_sc_c_riesgos      r,
                                 o02agpe0.tb_usuarios u
                           WHERE red.idpoliza = p.idpoliza(+)
                             and p.idcolectivo = c.id(+)
                             and c.codentidad = t.codentidad(+)
                             and c.ciftomador = t.ciftomador(+)
                             and p.lineaseguroid = l.lineaseguroid(+)
                             and p.idasegurado = a.id(+)
                             and p.idenvio = com.idenvio(+)
                             and red.idestado = es.idestado(+)
                             and red.codmotivoriesgo = r.codriesgo(+)
                             and p.codusuario = u.codusuario (+)
                             and p.idestado != 0
                             and p.idasegurado not in (select blq.id_asegurado from o02agpe0.tb_bloqueos_asegurados blq
                                              where blq.idestado_aseg =''B'')
							 AND l.codplan BETWEEN ' || v_anio_inicial || ' and ' || v_plan_actual || '');
  
    commit;
  
  EXCEPTION
    WHEN OTHERS THEN
      ROLLBACK;
      -- Se vuelve a lanzar la excepcion para que cancele la cadena
      RAISE;
    
  end act_tb_inf_anexo_red;

  -------------------------------------------------------------------------------
  -- Actualiza los datos de la tabla de backup 'tb_inf_anexo_red_parcelas_bak' --
  -------------------------------------------------------------------------------
  procedure act_tb_inf_anexo_red_parcelas(v_anios_actualizar IN NUMBER,
                                          v_plan_actual      IN NUMBER) as
  
    v_anio_inicial NUMBER(4) := v_plan_actual - v_anios_actualizar;
  
  begin
  
    PQ_Utl.LOG(lc, 'procedure act_tb_inf_anexo_red_parcelas', 2);
    PQ_Utl.LOG(lc, 'anhos a actualizar = ' || v_anios_actualizar, 2);
    PQ_Utl.LOG(lc, 'anho actual = ' || v_plan_actual, 2);
    PQ_Utl.LOG(lc,
               'anho inical (anho actual - anhos a actualizar)= ' ||
               v_anio_inicial,
               2);
  
    execute immediate ('insert into o02agpe1.TB_INF_ANEXO_RED_PARCELAS_BAK
                          SELECT
							p.referencia,
							ar.numanexo,
							arp.hoja,
							arp.numero,
							arp.codprovincia,
							arp.codcomarca           codcomarca_ar,
							arp.codtermino,
							arp.subtermino,
							arp.codcultivo,
							arp.codvariedad,
							arp.codprovsigpac
							|| ''-''
							|| arp.codtermsigpac
							|| ''-''
							|| arp.agrsigpac
							|| ''-''
							|| arp.zonasigpac
							|| ''-''
							|| arp.poligonosigpac
							|| ''-''
							|| arp.parcelasigpac
							|| ''-''
							|| arp.recintosigpac    sigpac,
							arca.codtipocapital,
							arca.superficie,
							arca.precio,
							arca.prodred,
							arp.idanexo,
							c.codentidad,
							p.oficina,
							p.codusuario,
							c.entmediadora,
							c.subentmediadora,
							u.delegacion
						FROM
							o02agpe0.tb_anexo_red_parcelas    arp,
							o02agpe0.tb_anexo_red             ar,
							o02agpe0.tb_polizas               p,
							o02agpe0.tb_anexo_red_cap_aseg    arca,
							o02agpe0.tb_colectivos            c,
							o02agpe0.tb_usuarios              u,
							o02agpe0.tb_lineas                l
						WHERE
							arp.idanexo = ar.id
							AND ar.idpoliza = p.idpoliza
							AND arp.id = arca.idparcelaanexo
							AND p.idcolectivo = c.id
							AND arp.altaenanexo = ''S''
							AND p.codusuario = u.codusuario
							AND p.idestado != 0
							AND l.lineaseguroid = p.lineaseguroid
							AND l.codplan BETWEEN ' || v_anio_inicial || ' and ' || v_plan_actual || '');
  
    commit;
  
  EXCEPTION
    WHEN OTHERS THEN
      ROLLBACK;
      -- Se vuelve a lanzar la excepcion para que cancele la cadena
      RAISE;
    
  end act_tb_inf_anexo_red_parcelas;

  --------------------------------------------------------------------------------
  -- Actualiza los datos de la tabla de backup 'tb_inf_distribucion_costes_bak' --
  --------------------------------------------------------------------------------
  procedure act_tb_inf_distribucion_costes(v_anios_actualizar IN NUMBER,
                                           v_plan_actual      IN NUMBER) as
  
    v_anio_inicial NUMBER(4) := v_plan_actual - v_anios_actualizar;
  
  begin
  
    PQ_Utl.LOG(lc, 'procedure act_tb_inf_distribucion_costes', 2);
    PQ_Utl.LOG(lc, 'anhos a actualizar = ' || v_anios_actualizar, 2);
    PQ_Utl.LOG(lc, 'anho actual = ' || v_plan_actual, 2);
    PQ_Utl.LOG(lc,
               'anho inical (anho actual - anhos a actualizar)= ' ||
               v_anio_inicial,
               2);
  
    execute immediate ('insert into o02agpe1.TB_INF_DISTRIBUCION_COSTES_BAK select
                        dc.primacomercial, dc.primaneta, dc.costeneto, dc.cargotomador, dc.bonifmedpreventivas, dc.bonifasegurado, dc.recargoasegurado,
                        dc.ventanilla, dc.dtocolectivo, dc.reaseguro, dc.recargo, dc.pctbonifasegurado, dc.pctrecargoasegurado, subv_enesa, subv_ccaa, dc.idpoliza, c.codentidad,
                        p.oficina,
                        p.codusuario,
                        dc.id, c.entmediadora, c.subentmediadora, u.delegacion
                        from o02agpe0.tb_distribucion_costes dc, o02agpe0.tb_polizas p, o02agpe0.tb_colectivos c,o02agpe0.tb_usuarios u, o02agpe0.tb_lineas lin,
                        -- total enesa
                        (select dcs.iddistcoste iddistcoste_enesa, sum(dcs.importesubv) subv_enesa
                         from o02agpe0.tb_dist_coste_subvs dcs
                         where dcs.codorganismo = ''0''
                         group by dcs.iddistcoste),
                        -- total ccaa
                        (select dcs.iddistcoste iddistcoste_ccaa, sum (dcs.importesubv) subv_ccaa
                          from o02agpe0.tb_distribucion_costes dc, o02agpe0.tb_dist_coste_subvs dcs
                         where
                           dc.id = dcs.iddistcoste
                           and (dc.idpoliza, dcs.codtiposubv) in
                           (select sac.idpoliza, subccaa.codtiposubvccaa as codtiposubv
                                  from o02agpe0.tb_polizas p, o02agpe0.tb_subvs_aseg_ccaa sac, o02agpe0.tb_sc_c_subvs_ccaa subccaa
                                 where p.idpoliza = sac.idpoliza
                                   and p.idasegurado = sac.idasegurado
                                   and p.lineaseguroid = sac.lineaseguroid
                                   and sac.idsubvencion = subccaa.id
                                   and sac.lineaseguroid = subccaa.lineaseguroid)
                           group by dcs.iddistcoste)
                        where dc.id = iddistcoste_enesa(+)
                        and dc.id = iddistcoste_ccaa (+)
                        and dc.idpoliza = p.idpoliza
                        and p.idcolectivo=c.id
                        and p.codusuario = u.codusuario
                        and p.idestado != 0
						AND p.lineaseguroid = lin.lineaseguroid
						AND lin.codplan BETWEEN ' || v_anio_inicial || ' and ' || v_plan_actual || '');
  
    commit;
  
  EXCEPTION
    WHEN OTHERS THEN
      ROLLBACK;
      -- Se vuelve a lanzar la excepcion para que cancele la cadena
      RAISE;
    
  end act_tb_inf_distribucion_costes;

  --------------------------------------------------------------------
  -- Actualiza los datos de la tabla de backup 'tb_polizas_bak' --
  --------------------------------------------------------------------
  procedure act_tb_polizas(v_anios_actualizar IN NUMBER,
                           v_plan_actual      IN NUMBER) as
  
    v_anio_inicial NUMBER(4) := v_plan_actual - v_anios_actualizar;
  
  begin
    IF (v_act_completa = FALSE) THEN
      insertDeSelect('o02agpe1',
                     'tb_polizas_bak',
                     'o02agpe0',
                     'tb_polizas t',
                     ' inner join o02agpe0.tb_lineas l on l.codplan BETWEEN ' ||
                     v_anio_inicial || ' and ' || v_plan_actual ||
                     ' and l.lineaseguroid = t.lineaseguroid where t.idpoliza not in (select distinct idpoliza from o02agpe1.TB_INF_POLIZAS)');
    ELSE
      insertDeSelect('o02agpe1',
                     'tb_polizas_bak',
                     'o02agpe0',
                     'tb_polizas t',
                     ' inner join o02agpe0.tb_lineas l on l.codplan BETWEEN ' ||
                     v_anio_inicial || ' and ' || v_plan_actual ||
                     ' and l.lineaseguroid = t.lineaseguroid');
    END IF;
  
  EXCEPTION
    WHEN OTHERS THEN
      -- Se vuelve a lanzar la excepcion para que cancele la cadena
      RAISE;
    
  end act_tb_polizas;

  --------------------------------------------------------------------
  -- Actualiza los datos de la tabla de backup 'tb_colectivos_bak' --
  --------------------------------------------------------------------
  procedure act_tb_colectivos(v_anios_actualizar IN NUMBER,
                              v_plan_actual      IN NUMBER) as
  
    v_anio_inicial NUMBER(4) := v_plan_actual - v_anios_actualizar;
  
  begin
  
    PQ_Utl.LOG(lc, 'procedure act_tb_colectivos', 2);
    PQ_Utl.LOG(lc, 'anhos a actualizar = ' || v_anios_actualizar, 2);
    PQ_Utl.LOG(lc, 'anho actual = ' || v_plan_actual, 2);
    PQ_Utl.LOG(lc,
               'anho inical (anho actual - anhos a actualizar)= ' ||
               v_anio_inicial,
               2);
  
    -- Insert de select
    execute immediate ('INSERT INTO o02agpe1.tb_colectivos_bak
							( SELECT
								col.id,
								col.codentidad,
								col.entmediadora,
								col.subentmediadora
							FROM
								o02agpe0.tb_colectivos col
								INNER JOIN o02agpe0.tb_lineas lin ON lin.codplan BETWEEN ' ||
                      v_anio_inicial || ' and ' || v_plan_actual || ' AND col.lineaseguroid = lin.lineaseguroid)');
    commit;
  
  EXCEPTION
    WHEN OTHERS THEN
      ROLLBACK;
      -- Se vuelve a lanzar la excepcion para que cancele la cadena
      RAISE;
    
  end act_tb_colectivos;

  --------------------------------------------------------------------
  -- Actualiza los datos de la tabla de backup 'tb_parcelas_bak' --
  --------------------------------------------------------------------
  procedure act_tb_parcelas(v_anios_actualizar IN NUMBER,
                            v_plan_actual      IN NUMBER) as
  
    v_anio_inicial NUMBER(4) := v_plan_actual - v_anios_actualizar;
  
  begin
    -- Insert de select
    insertDeSelectCampos('o02agpe1',
                         'tb_parcelas_bak pb',
                         'o02agpe0',
                         'tb_parcelas pa',
                         ' inner join o02agpe0.tb_polizas p on pa.idpoliza = p.idpoliza inner join o02agpe0.tb_lineas l on l.codplan BETWEEN ' ||
                         v_anio_inicial || ' and ' || v_plan_actual ||
                         ' and l.lineaseguroid = p.lineaseguroid',
                         'pa.IDPARCELA, pa.IDPOLIZA, pa.CODPROVINCIA, pa.CODTERMINO, pa.SUBTERMINO, pa.POLIGONO, pa.PARCELA, pa.CODPROVSIGPAC, pa.CODTERMSIGPAC, pa.AGRSIGPAC, pa.ZONASIGPAC, pa.POLIGONOSIGPAC, pa.PARCELASIGPAC, pa.RECINTOSIGPAC, pa.NOMPARCELA, pa.CODCULTIVO, pa.CODVARIEDAD, pa.HOJA, pa.NUMERO, pa.CODCOMARCA, pa.LINEASEGUROID, pa.IDPARCELAESTRUCTURA, pa.TIPOPARCELA, pa.ALTAENCOMPLEMENTARIO, pa.IND_RECALCULO_HOJA_NUMERO, pa.parcela_agricola');
  
  EXCEPTION
    WHEN OTHERS THEN
      -- Se vuelve a lanzar la excepcion para que cancele la cadena
      RAISE;
    
  end act_tb_parcelas;

  -----------------------------------------------------------------------------
  -- Actualiza los datos de la tabla de backup 'tb_capitales_asegurados_bak' --
  -----------------------------------------------------------------------------
  procedure act_tb_capitales_asegurados(v_anios_actualizar IN NUMBER,
                                        v_plan_actual      IN NUMBER) as
  
    v_anio_inicial NUMBER(4) := v_plan_actual - v_anios_actualizar;
  
  begin
    -- Insert de select
    insertDeSelectCampos('o02agpe1',
                         'tb_capitales_asegurados_bak cp',
                         'o02agpe0',
                         'tb_capitales_asegurados ca',
                         ' inner join o02agpe0.tb_parcelas pa on pa.idparcela = ca.idparcela inner join o02agpe0.tb_polizas p on pa.idpoliza = p.idpoliza inner join o02agpe0.tb_lineas l on l.codplan BETWEEN ' ||
                         v_anio_inicial || ' and ' || v_plan_actual ||
                         ' and l.lineaseguroid = p.lineaseguroid',
                         'ca.IDPARCELA, ca.IDCAPITALASEGURADO, ca.CODTIPOCAPITAL, ca.SUPERFICIE, ca.PRECIO, ca.PRODUCCION, ca.PRECIOMODIF, ca.PRODUCCIONMODIF, ca.ALTAENCOMPLEMENTARIO, ca.INCREMENTOPRODUCCION');
  
  EXCEPTION
    WHEN OTHERS THEN
      -- Se vuelve a lanzar la excepcion para que cancele la cadena
      RAISE;
    
  end act_tb_capitales_asegurados;

  --------------------------------------------------------------------------------
  -- Actualiza los datos de la tabla de backup 'act_tb_inf_cultivo_var_linea_bak' --TMR
  --------------------------------------------------------------------------------
  procedure act_tb_inf_cultivo_var_linea(v_anios_actualizar IN NUMBER,
                                         v_plan_actual      IN NUMBER) as
  
    v_anio_inicial NUMBER(4) := v_plan_actual - v_anios_actualizar;
  
  begin
  
    PQ_Utl.LOG(lc, 'procedure act_tb_inf_cultivo_var_linea', 2);
    PQ_Utl.LOG(lc, 'anhos a actualizar = ' || v_anios_actualizar, 2);
    PQ_Utl.LOG(lc, 'anho actual = ' || v_plan_actual, 2);
    PQ_Utl.LOG(lc,
               'anho inical (anho actual - anhos a actualizar)= ' ||
               v_anio_inicial,
               2);
  
    execute immediate ('insert into o02agpe1.tb_inf_cultivo_var_linea_bak
                           (select l.codplan,
                                   l.codlinea,
                                   l.nomlinea,
                                   v.codcultivo,
                                   c.descultivo,
                                   v.codvariedad,
                                   v.desvariedad
                              from o02agpe0.tb_sc_c_variedades v, o02agpe0.tb_lineas l, o02agpe0.tb_sc_c_cultivos c
                             where v.lineaseguroid = l.lineaseguroid
                               and v.codcultivo = c.codcultivo
                               and c.lineaseguroid = l.lineaseguroid
							   AND l.codplan BETWEEN ' || v_anio_inicial || ' and ' || v_plan_actual || ')');
  
    commit;
  
  EXCEPTION
    WHEN OTHERS THEN
      ROLLBACK;
      -- Se vuelve a lanzar la excepcion para que cancele la cadena
      RAISE;
    
  end act_tb_inf_cultivo_var_linea;

  ---------------------------------------------------------------------------
  -- Actualiza los datos de la tabla de backup 'tb_inf_explotaciones_coberturas_bak' --
  ---------------------------------------------------------------------------
  procedure act_tb_inf_explotaciones_coberturas(v_anios_actualizar IN NUMBER,
                                                v_plan_actual      IN NUMBER) as
  
    v_anio_inicial NUMBER(4) := v_plan_actual - v_anios_actualizar;
  
  begin
  
    PQ_Utl.LOG(lc, 'procedure act_tb_inf_explotaciones_coberturas', 2);
    PQ_Utl.LOG(lc, 'anhos a actualizar = ' || v_anios_actualizar, 2);
    PQ_Utl.LOG(lc, 'anho actual = ' || v_plan_actual, 2);
    PQ_Utl.LOG(lc,
               'anho inical (anho actual - anhos a actualizar)= ' ||
               v_anio_inicial,
               2);
  
    execute immediate ('insert into o02agpe1.TB_INF_EXPLOTACIONES_COBERTURAS_BAK
                             (SELECT
								pol.idpoliza,
								exp.idexplotacion,
								exp.codmodulo,
								exp.cpm,
								exp.cpm_descripcion,
								exp.riesgo_cubierto,
								exp.rc_descripcion,
								exp.dv_cod_concepto,
								exp.dv_descripcion,
								exp.dv_valor,
								exp.dv_valor_descripcion,
								(
									SELECT
										franq.valor
									FROM
										o02agpe0.tb_sc_c_pct_franquicia_eleg franq
									WHERE
										franq.codpctfranquiciaeleg = exp.dv_valor
										AND exp.dv_cod_concepto = ''120''
								),
								(
									SELECT
										min.valor
									FROM
										o02agpe0.tb_sc_c_min_indem_eleg min
									WHERE
										min.pctminindem = exp.dv_valor
										AND exp.dv_cod_concepto = ''121''
								)
								FROM
									o02agpe0.tb_explotaciones_coberturas exp
									INNER JOIN o02agpe0.tb_explotaciones    ex ON exp.idexplotacion = ex.id
									INNER JOIN o02agpe0.tb_polizas          pol ON ex.idpoliza = pol.idpoliza
									INNER JOIN o02agpe0.tb_lineas           lin ON lin.codplan BETWEEN ' ||
                      v_anio_inicial || ' and ' || v_plan_actual || ' AND pol.lineaseguroid = lin.lineaseguroid
								WHERE
									exp.elegida = ''S''
                             )');
  
    commit;
  
  EXCEPTION
    WHEN OTHERS THEN
      ROLLBACK;
      -- Se vuelve a lanzar la excepcion para que cancele la cadena
      RAISE;
    
  end act_tb_inf_explotaciones_coberturas;

  ---------------------------------------------------------------------------
  -- Actualiza los datos de la tabla de backup 'tb_inf_parcelas_coberturas_bak' --
  ---------------------------------------------------------------------------
  procedure act_tb_inf_parcelas_coberturas(v_anios_actualizar IN NUMBER,
                                           v_plan_actual      IN NUMBER) as
  
    v_anio_inicial NUMBER(4) := v_plan_actual - v_anios_actualizar;
  
  begin
  
    PQ_Utl.LOG(lc, 'procedure act_tb_inf_parcelas_coberturas', 2);
    PQ_Utl.LOG(lc, 'anhos a actualizar = ' || v_anios_actualizar, 2);
    PQ_Utl.LOG(lc, 'anho actual = ' || v_plan_actual, 2);
    PQ_Utl.LOG(lc,
               'anho inical (anho actual - anhos a actualizar)= ' ||
               v_anio_inicial,
               2);
  
    execute immediate ('insert into o02agpe1.TB_INF_PARCELAS_COBERTURAS_BAK
                             (select (select pa.idpoliza from o02agpe0.tb_parcelas pa
                                            where par.idparcela = pa.idparcela),
                                     par.idparcela,
                                     par.lineaseguroid,
                                     par.codmodulo,
                                     par.codconceptoppalmod,
                                     (select cppal.desconceptoppalmod from o02agpe0.TB_SC_C_CONCEPTO_PPAL_MOD cppal
                                             where cppal.codconceptoppalmod = par.codconceptoppalmod),
                                     par.codriesgocubierto,
                                     (select riesg.desriesgocubierto from o02agpe0.TB_SC_C_RIESGOS_CUBIERTOS riesg
                                             where riesg.lineaseguroid = par.lineaseguroid and riesg.codmodulo = par.codmodulo and riesg.codriesgocubierto = par.codriesgocubierto),
                                     par.codconcepto,
                                     (select dd.desconcepto from o02agpe0.TB_SC_DD_DIC_DATOS dd
                                             where par.codconcepto = dd.codconcepto),
                                     par.codvalor,
                                     CASE
                                        WHEN par.codvalor = -1 THEN ''Elegido''
                                        WHEN par.codvalor = -2 THEN ''No elegido''
                                        WHEN par.codvalor = 363 THEN ''Riesgos''
                                     END as descvalor,
                                     (select distinct fr.despctfranquiciaeleg from o02agpe0.tb_datos_var_parcela dvp,
                                                            o02agpe0.tb_capitales_asegurados ca,
                                                            o02agpe0.tb_parcelas_coberturas pa,
                                                            o02agpe0.tb_sc_c_pct_franquicia_eleg fr
                                                          where dvp.idcapitalasegurado = ca.idcapitalasegurado and
                                                             pa.idparcela = ca.idparcela and
                                                             fr.codpctfranquiciaeleg = dvp.valor and
                                                             dvp.codconcepto = 120 and
                                                             pa.idparcela = par.idparcela),
                                     (select distinct min.desminindem from o02agpe0.tb_datos_var_parcela dvp,
                                                            o02agpe0.tb_capitales_asegurados ca,
                                                            o02agpe0.tb_parcelas_coberturas pa,
                                                            o02agpe0.tb_sc_c_min_indem_eleg min
                                                       where dvp.idcapitalasegurado = ca.idcapitalasegurado and
                                                             pa.idparcela = ca.idparcela and
                                                             min.pctminindem = dvp.valor and
                                                             dvp.codconcepto = 121 and
                                                             pa.idparcela = par.idparcela)

                                from o02agpe0.tb_parcelas_coberturas par
								inner JOIN o02agpe0.tb_lineas lin ON lin.codplan BETWEEN ' ||
                      v_anio_inicial || ' and ' || v_plan_actual || ' AND par.lineaseguroid = lin.lineaseguroid)');
  
    commit;
  
  EXCEPTION
    WHEN OTHERS THEN
      ROLLBACK;
      -- Se vuelve a lanzar la excepcion para que cancele la cadena
      RAISE;
    
  end act_tb_inf_parcelas_coberturas;

  --------------------------------------------------------------------------------------
  -- Ejecuta un insert de select en las tablas y esquemas indicados en los parametros --
  --------------------------------------------------------------------------------------
  procedure insertDeSelect(esqDest   IN VARCHAR2,
                           tbDest    IN VARCHAR2,
                           esqOrig   IN VARCHAR2,
                           tbOrig    IN VARCHAR2,
                           condicion IN VARCHAR2 DEFAULT NULL) as
  
  begin
  
    execute immediate ('insert into ' || esqDest || '.' || tbDest ||
                      ' (select t.* from ' || esqOrig || '.' || tbOrig || ' ' ||
                      NVL(condicion, '') || ')');
    commit;
  
  EXCEPTION
    WHEN OTHERS THEN
      ROLLBACK;
      -- Se vuelve a lanzar la excepcion para que cancele la cadena
      RAISE;
    
  end insertDeSelect;

  --------------------------------------------------------------------------------------
  -- Ejecuta un insert de select en las tablas y esquemas indicados en los parametros --
  --------------------------------------------------------------------------------------
  procedure insertDeSelectCampos(esqDest   IN VARCHAR2,
                                 tbDest    IN VARCHAR2,
                                 esqOrig   IN VARCHAR2,
                                 tbOrig    IN VARCHAR2,
                                 condicion IN VARCHAR2 DEFAULT NULL,
                                 campos    IN VARCHAR2 DEFAULT NULL) as
  
  begin
  
    execute immediate ('insert into ' || esqDest || '.' || tbDest ||
                      ' (select ' || NVL(campos, '*') || ' from ' ||
                      esqOrig || '.' || tbOrig || ' ' ||
                      NVL(condicion, '') || ')');
    commit;
  
  EXCEPTION
    WHEN OTHERS THEN
      ROLLBACK;
      -- Se vuelve a lanzar la excepcion para que cancele la cadena
      RAISE;
    
  end insertDeSelectCampos;

  --------------------------------------------------------------------------------------
  -- Ejecuta un insert de select por Entidd en las tablas y esquemas indicados en los parametros --
  --------------------------------------------------------------------------------------
  procedure insertDeSelectPorEntidad(esqDest   IN VARCHAR2,
                                     tbDest    IN VARCHAR2,
                                     esqOrig   IN VARCHAR2,
                                     tbOrig    IN VARCHAR2,
                                     condicion IN VARCHAR2 DEFAULT NULL) as
    codEnt NUMBER(5);
    cont   number default 0;
    CURSOR c_com IS
      select distinct (polb.codentidad)
        from o02agpe1.tb_inf_polizas_parcelas_bak polb;
  
  begin
    FOR c IN c_com LOOP
      codEnt := c.codentidad;
      execute immediate ('insert /*+ append */ into ' || esqDest || '.' ||
                        tbDest || ' (select * from ' || esqOrig || '.' ||
                        tbOrig || ' ' || NVL(condicion, '') ||
                        ' where codentidad = ' || codEnt || ')');
      commit;
      cont := cont + 1;
    END LOOP;
  
  EXCEPTION
    WHEN OTHERS THEN
      ROLLBACK;
      -- Se vuelve a lanzar la excepcion para que cancele la cadena
      RAISE;
    
  end insertDeSelectPorEntidad;

  ----------------------------------------------------------------------------
  -- Ejecuta un insert de select en las tablas de backup de datos variables --
  ----------------------------------------------------------------------------
  procedure insertDeSelectDvp(esqDest     IN VARCHAR2,
                              esqOrig     IN VARCHAR2,
                              codConcepto IN VARCHAR2) as
  
  begin
  
    execute immediate ('insert into ' || esqDest || '.TB_DVP_' ||
                      codConcepto ||
                      ' (select IDCAPITALASEGURADO, VALOR from ' ||
                      esqOrig ||
                      '.TB_DATOS_VAR_PARCELA WHERE CODCONCEPTO=' ||
                      codConcepto || ')');
    commit;
  
  EXCEPTION
    WHEN OTHERS THEN
      ROLLBACK;
      -- Se vuelve a lanzar la excepcion para que cancele la cadena
      RAISE;
    
  end insertDeSelectDvp;

  -------------------------------------------------------------------------------------
  -- Devuelve los registros de 'p_cursor' concatenados y separados por 'p_separador' --
  -------------------------------------------------------------------------------------
  function concat_group(p_cursor    in sys_refcursor,
                        p_separador varchar2 default ',') return varchar2 is
    salida varchar2(32760);
    cadena varchar2(32760);
  
  begin
  
    loop
      fetch p_cursor
        into cadena;
      exit when p_cursor%NOTFOUND;
    
      if (salida is null) then
        salida := cadena;
      else
        salida := salida || p_separador || cadena;
      end if;
    
    end loop;
  
    close p_cursor;
  
    if (salida is null) then
      return '';
    else
      return ltrim(substr(salida, length(p_separador), length(salida)));
    end if;
  
  end concat_group;

  ----------------------------------------------------------------------------
  -- Carga los datos correspondientes en la tabla 'TB_INF_POLIZAS_PARCELAS' --
  ----------------------------------------------------------------------------
  procedure act_tb_inf_polizas_parcelas(v_anios_actualizar IN NUMBER,
                                        v_plan_actual      IN NUMBER) as
  
    -- Variables
    V_ROTACION                  VARCHAR2(30);
    V_CICLO_CULTIVO             VARCHAR2(60);
    V_DENOM_ORIGEN              VARCHAR2(60);
    V_CODIGO_IGP                VARCHAR2(30);
    V_REDUCC_RDTO               VARCHAR2(30);
    V_CODIGO_CERTIFICADO        VARCHAR2(60);
    V_DENSIDAD                  VARCHAR2(30);
    V_DESTINO                   VARCHAR2(60);
    V_EDAD                      VARCHAR2(30);
    V_EDAD_CUBIERTA             VARCHAR2(30);
    V_EDAD_ESTRUCTURA           VARCHAR2(30);
    V_FECHA_RECOLECCION         VARCHAR2(30);
    V_FECHA_SIEMBRA_TRASPLANTE  VARCHAR2(30);
    V_INDIC_GASTOS_SALVAMENTO   VARCHAR2(30);
    V_MATERIAL_CUBIERTA         VARCHAR2(60);
    V_MATERIAL_ESTRUCTURA       VARCHAR2(60);
    V_MEDIDA_PREVENTIVA         VARCHAR2(60);
    V_METROS_LINEALES           VARCHAR2(30);
    V_METROS_CUADRADOS          VARCHAR2(30);
    V_NUM_ANHOS_DESDE_DESCORCHE VARCHAR2(30);
    V_NUM_ANHOS_DESDE_PODA      VARCHAR2(30);
    V_NUMERO_UNIDADES           VARCHAR2(30);
    V_PENDIENTE                 VARCHAR2(30);
    V_SISTEMA_CULTIVO           VARCHAR2(60);
    V_SISTEMA_PRODUCCION        VARCHAR2(60);
    V_SISTEMA_PROTECCION        VARCHAR2(60);
    V_TIPO_INSTALACION          VARCHAR2(60);
    V_TIPO_MASA                 VARCHAR2(60);
    V_TIPO_MARCO_PLANTACION     VARCHAR2(60);
    V_TIPO_PLANTACION           VARCHAR2(60);
    V_SISTEMA_CONDUCCION        VARCHAR2(60);
    V_TIPO_SUB_DECLARADA_PARCEL VARCHAR2(30);
    V_PRACTICA_CULTURAL         VARCHAR2(60);
    V_TIPO_TERRENO              VARCHAR2(60);
    V_VALOR_FIJO                VARCHAR2(30);
    V_EST_FEN_FIN_GARANTIAS     VARCHAR2(100);
    V_FECHA_FIN_GARANTIAS       VARCHAR2(100);
    V_DURACION_MAX_GARANT_DIAS  VARCHAR2(100);
    V_DURACION_MAX_GARANT_MESES VARCHAR2(100);
    V_EST_FEN_INICIO_GARANTIAS  VARCHAR2(100);
    V_FECHA_INICIO_GARANTIAS    VARCHAR2(100);
    V_DIAS_INICIO_GARANTIAS     VARCHAR2(100);
    V_MESES_INICIO_GARANTIAS    VARCHAR2(100);
    V_PERIODO_GARANTIAS         VARCHAR2(100);
    V_TIPO_RENDIMIENTO          VARCHAR2(100);
    V_RIESGO_CUBIERTO_ELEGIDO   VARCHAR2(100);
    V_PRECIO                    NUMBER(10, 4);
    V_PRODUCCION                NUMBER(11, 2);
    V_COSTENETO                 NUMBER(13, 4);
    V_TASACOMERCIAL             NUMBER(5, 2);
    V_CODUSUARIO                VARCHAR2(8);
    V_NIF_SOCIO                 VARCHAR2(20);
    V_SUM_BONIF_REC             NUMBER(13, 2);
    V_SUM_SUBV_ENESA            NUMBER(13, 2);
    V_SUM_SUBV_CCAA             NUMBER(13, 2);
    V_SUM_SUBV_DESG             NUMBER(13, 2);
    -- Auxiliares
    V_DVP_CODCONCEPTO NUMBER(5);
    V_DVP_VALOR       VARCHAR(30);
  
    TYPE cur_typ IS REF CURSOR;
    --cursor_plz_parc cur_typ; -- Cursor para los registros de polizas parcelas
    cursor_dvp cur_typ; -- Cursor para los registros de datos variables
  
    -- Intervalo de inserciones en las que se hara commit
    maxCommit NUMBER := 10000;
    -- Almacena los inserts ejecutados
    cont       NUMBER := 0;
    V_AUX_DESC VARCHAR2(30);
    -- Consulta para obtener los datos variables asociados a un capital asegurado
    v_query_dvp CONSTANT VARCHAR2(1000) := 'select codconcepto, valor from
                                           o02agpe0.tb_datos_var_parcela dvp where dvp.idcapitalasegurado=';
  
    -- Idpoliza que se esta procesando en cada momento
    v_idpoliza_act NUMBER(15);
  
    CURSOR registros_cursor IS
      select p.idpoliza,
             p.referencia,
             p.codmodulo,
             par.codprovincia,
             par.codcomarca,
             par.codtermino,
             par.subtermino,
             par.codcultivo,
             par.codvariedad,
             par.codprovsigpac || '-' || par.codtermsigpac || '-' ||
             par.agrsigpac || '-' || par.zonasigpac || '-' ||
             par.poligonosigpac || '-' || par.parcelasigpac || '-' ||
             par.recintosigpac sigpac,
             par.nomparcela,
             ca.codtipocapital,
             tc.destipocapital,
             ca.idcapitalasegurado,
             col.codentidad,
             p.oficina,
             NULL CODUSUARIO,
             ca.superficie,
             ca.preciomodif,
             ca.produccionmodif,
             ca.altaencomplementario,
             ca.incrementoproduccion,
             dc.id,
             par.hoja,
             par.numero,
             p.idestado,
             (select distinct pc.codvalor
                from o02agpe0.tb_parcelas_coberturas pc
               where pc.idparcela = par.idparcela
                 and rownum = 1) as RIESGO_CUBIERTO_ELEGIDO,
             col.codentidadmediadora,
             col.codsubentidadmediadora,
             u.delegacion,
             par.parcela_agricola
        from o02agpe1.tb_polizas_bak                 p,
             o02agpe1.tb_parcelas_bak                par,
             o02agpe1.tb_capitales_asegurados_bak    ca,
             o02agpe1.tb_sc_c_tipo_capital_bak       tc,
             o02agpe1.tb_colectivos_bak              col,
             o02agpe1.tb_inf_distribucion_costes_bak dc,
             o02agpe0.tb_usuarios                    u
       where p.idpoliza = par.idpoliza(+)
         and par.idparcela = ca.idparcela(+)
         and ca.codtipocapital = tc.codtipocapital(+)
         and p.idcolectivo = col.id
         and p.idpoliza = dc.idpoliza(+)
         and p.codusuario = u.codusuario;
  
    TYPE registros_tipo IS TABLE OF registros_cursor%ROWTYPE INDEX BY PLS_INTEGER;
  
    lista_registros registros_tipo;
  
  BEGIN
  
    OPEN registros_cursor;
  
    LOOP
      FETCH registros_cursor BULK COLLECT
        INTO lista_registros LIMIT maxCommit;
    
      --PQ_Utl.LOG (lc, '** 1 **', 2);
    
      EXIT WHEN lista_registros.COUNT = 0;
    
      FOR idx IN 1 .. lista_registros.COUNT LOOP
      
        v_idpoliza_act := lista_registros(idx).IDPOLIZA;
        --PQ_Utl.LOG (lc, '** 2 **', 2);
      
        -- ----------------------------------------------------- --
        -- Resetea las variables asociadas a los datos variables --
        -- ----------------------------------------------------- --
        V_ROTACION                  := NULL;
        V_CICLO_CULTIVO             := NULL;
        V_DENOM_ORIGEN              := NULL;
        V_CODIGO_IGP                := NULL;
        V_REDUCC_RDTO               := NULL;
        V_CODIGO_CERTIFICADO        := NULL;
        V_DENSIDAD                  := NULL;
        V_DESTINO                   := NULL;
        V_EDAD                      := NULL;
        V_EDAD_CUBIERTA             := NULL;
        V_EDAD_ESTRUCTURA           := NULL;
        V_FECHA_RECOLECCION         := NULL;
        V_FECHA_SIEMBRA_TRASPLANTE  := NULL;
        V_INDIC_GASTOS_SALVAMENTO   := NULL;
        V_MATERIAL_CUBIERTA         := NULL;
        V_MATERIAL_ESTRUCTURA       := NULL;
        V_MEDIDA_PREVENTIVA         := NULL;
        V_METROS_LINEALES           := NULL;
        V_METROS_CUADRADOS          := NULL;
        V_NUM_ANHOS_DESDE_DESCORCHE := NULL;
        V_NUM_ANHOS_DESDE_PODA      := NULL;
        V_NUMERO_UNIDADES           := NULL;
        V_PENDIENTE                 := NULL;
        V_SISTEMA_CULTIVO           := NULL;
        V_SISTEMA_PRODUCCION        := NULL;
        V_SISTEMA_PROTECCION        := NULL;
        V_TIPO_INSTALACION          := NULL;
        V_TIPO_MASA                 := NULL;
        V_TIPO_MARCO_PLANTACION     := NULL;
        V_TIPO_PLANTACION           := NULL;
        V_SISTEMA_CONDUCCION        := NULL;
        V_TIPO_SUB_DECLARADA_PARCEL := NULL;
        V_PRACTICA_CULTURAL         := NULL;
        V_TIPO_TERRENO              := NULL;
        V_VALOR_FIJO                := NULL;
        V_EST_FEN_FIN_GARANTIAS     := NULL;
        V_FECHA_FIN_GARANTIAS       := NULL;
        V_DURACION_MAX_GARANT_DIAS  := NULL;
        V_DURACION_MAX_GARANT_MESES := NULL;
        V_EST_FEN_INICIO_GARANTIAS  := NULL;
        V_FECHA_INICIO_GARANTIAS    := NULL;
        V_DIAS_INICIO_GARANTIAS     := NULL;
        V_MESES_INICIO_GARANTIAS    := NULL;
        V_PERIODO_GARANTIAS         := NULL;
        V_TIPO_RENDIMIENTO          := NULL;
        V_RIESGO_CUBIERTO_ELEGIDO   := NULL;
        V_PRECIO                    := NULL;
        V_PRODUCCION                := NULL;
        V_COSTENETO                 := NULL;
        V_TASACOMERCIAL             := NULL;
        V_CODUSUARIO                := NULL;
        V_NIF_SOCIO                 := NULL;
        V_SUM_BONIF_REC             := NULL;
        V_SUM_SUBV_ENESA            := NULL;
        V_SUM_SUBV_CCAA             := NULL;
        V_SUM_SUBV_DESG             := NULL;
      
        IF (lista_registros(idx).IDCAPITALASEGURADO IS NOT NULL) THEN
        
          -- ----------------------------------------------------------------- --
          -- Obtiene los datos variables asociados al capital asegurado actual --
          -- ----------------------------------------------------------------- --
          OPEN cursor_dvp FOR(v_query_dvp || lista_registros(idx)
                              .IDCAPITALASEGURADO);
          LOOP
            FETCH cursor_dvp
              INTO V_DVP_CODCONCEPTO, V_DVP_VALOR;
            EXIT WHEN cursor_dvp%NOTFOUND;
          
            -- Inserta el valor del dv en la variable correspondiente dependiendo del concepto
            V_AUX_DESC := NULL;
          
            -- ROTACION
            IF (V_DVP_CODCONCEPTO = 144) THEN
              V_ROTACION := V_DVP_VALOR;
            
              -- NIF SOCIO
            ELSIF (V_DVP_CODCONCEPTO = 98) THEN
              V_NIF_SOCIO := V_DVP_VALOR;
            
              -- CICLO CULTIVO
            ELSIF (V_DVP_CODCONCEPTO = 618) THEN
              V_CICLO_CULTIVO := V_DVP_VALOR;
              -- DENOM. ORIGEN
            ELSIF (V_DVP_CODCONCEPTO = 107) THEN
              V_DENOM_ORIGEN := V_DVP_VALOR;
            
              -- CODIGO IGP
            ELSIF (V_DVP_CODCONCEPTO = 765) THEN
              V_CODIGO_IGP := V_DVP_VALOR;
            
              -- REDUCC. RDTO
            ELSIF (V_DVP_CODCONCEPTO = 620) THEN
              V_REDUCC_RDTO := V_DVP_VALOR;
            
              -- CODIGO CERTIFICADO
            ELSIF (V_DVP_CODCONCEPTO = 879) THEN
              V_CODIGO_CERTIFICADO := V_DVP_VALOR;
            
              -- DENSIDAD
            ELSIF (V_DVP_CODCONCEPTO = 109) THEN
              V_DENSIDAD := V_DVP_VALOR;
            
              -- DESTINO
            ELSIF (V_DVP_CODCONCEPTO = 110) THEN
              V_DESTINO := V_DVP_VALOR;
            
              -- EDAD
            ELSIF (V_DVP_CODCONCEPTO = 111) THEN
              V_EDAD := V_DVP_VALOR;
            
              -- EDAD CUBIERTA
            ELSIF (V_DVP_CODCONCEPTO = 874) THEN
              V_EDAD_CUBIERTA := V_DVP_VALOR;
            
              -- EDAD ESTRUCTURA
            ELSIF (V_DVP_CODCONCEPTO = 876) THEN
              V_EDAD_ESTRUCTURA := V_DVP_VALOR;
            
              -- FECHA RECOLECCION
            ELSIF (V_DVP_CODCONCEPTO = 112) THEN
              V_FECHA_RECOLECCION := V_DVP_VALOR;
            
              -- FECHA SIEMBRA/TRASPLANTE
            ELSIF (V_DVP_CODCONCEPTO = 113) THEN
              V_FECHA_SIEMBRA_TRASPLANTE := V_DVP_VALOR;
            
              -- INDIC.GASTOS SALVAMENTO
            ELSIF (V_DVP_CODCONCEPTO = 114) THEN
              V_INDIC_GASTOS_SALVAMENTO := V_DVP_VALOR;
            
              -- MATERIAL CUBIERTA
            ELSIF (V_DVP_CODCONCEPTO = 873) THEN
              V_MATERIAL_CUBIERTA := V_DVP_VALOR;
            
              -- MATERIAL ESTRUCTURA
            ELSIF (V_DVP_CODCONCEPTO = 875) THEN
              V_MATERIAL_ESTRUCTURA := V_DVP_VALOR;
            
              -- MEDIDA PREVENTIVA
            ELSIF (V_DVP_CODCONCEPTO = 124) THEN
              V_MEDIDA_PREVENTIVA := V_DVP_VALOR;
            
              -- METROS LINEALES
            ELSIF (V_DVP_CODCONCEPTO = 766) THEN
              V_METROS_LINEALES := V_DVP_VALOR;
            
              -- METROS CUADRADOS
            ELSIF (V_DVP_CODCONCEPTO = 767) THEN
              V_METROS_CUADRADOS := V_DVP_VALOR;
            
              -- No anhoS DESDE DESCORCHE
            ELSIF (V_DVP_CODCONCEPTO = 944) THEN
              V_NUM_ANHOS_DESDE_DESCORCHE := V_DVP_VALOR;
            
              -- No anhoS DESDE PODA
            ELSIF (V_DVP_CODCONCEPTO = 617) THEN
              V_NUM_ANHOS_DESDE_PODA := V_DVP_VALOR;
            
              -- NUMERO UNIDADES
            ELSIF (V_DVP_CODCONCEPTO = 117) THEN
              V_NUMERO_UNIDADES := V_DVP_VALOR;
            
              -- PENDIENTE
            ELSIF (V_DVP_CODCONCEPTO = 754) THEN
              V_PENDIENTE := V_DVP_VALOR;
            
              -- SISTEMA CULTIVO
            ELSIF (V_DVP_CODCONCEPTO = 123) THEN
              V_SISTEMA_CULTIVO := V_DVP_VALOR;
            
              -- SISTEMA PRODUCCION
            ELSIF (V_DVP_CODCONCEPTO = 616) THEN
              V_SISTEMA_PRODUCCION := V_DVP_VALOR;
            
              -- SISTEMA PROTECCION
            ELSIF (V_DVP_CODCONCEPTO = 621) THEN
              V_SISTEMA_PROTECCION := V_DVP_VALOR;
            
              -- TIPO INSTALACION
            ELSIF (V_DVP_CODCONCEPTO = 778) THEN
              V_TIPO_INSTALACION := V_DVP_VALOR;
            
              -- TIPO MASA
            ELSIF (V_DVP_CODCONCEPTO = 753) THEN
              V_TIPO_MASA := V_DVP_VALOR;
            
              -- TIPO MARCO PLANTACION
            ELSIF (V_DVP_CODCONCEPTO = 116) THEN
              V_TIPO_MARCO_PLANTACION := V_DVP_VALOR;
            
              -- TIPO PLANTACION
            ELSIF (V_DVP_CODCONCEPTO = 173) THEN
              V_TIPO_PLANTACION := V_DVP_VALOR;
            
              -- SISTEMA CONDUCCION
            ELSIF (V_DVP_CODCONCEPTO = 131) THEN
              V_SISTEMA_CONDUCCION := V_DVP_VALOR;
            
              -- TIPO SUB.DECLARADA PARCEL
            ELSIF (V_DVP_CODCONCEPTO = 132) THEN
              V_TIPO_SUB_DECLARADA_PARCEL := V_DVP_VALOR;
            
              -- PRACTICA CULTURAL
            ELSIF (V_DVP_CODCONCEPTO = 133) THEN
              V_PRACTICA_CULTURAL := V_DVP_VALOR;
            
              -- TIPO TERRENO
            ELSIF (V_DVP_CODCONCEPTO = 752) THEN
              V_TIPO_TERRENO := V_DVP_VALOR;
            
              -- VALOR FIJO
            ELSIF (V_DVP_CODCONCEPTO = 768) THEN
              V_VALOR_FIJO := V_DVP_VALOR;
            
              -- EST.FEN.FIN GARANTIAS
            ELSIF (V_DVP_CODCONCEPTO = 135) THEN
              V_EST_FEN_FIN_GARANTIAS := V_DVP_VALOR;
            
              -- FECHA FIN GARANTIAS
            ELSIF (V_DVP_CODCONCEPTO = 134) THEN
              V_FECHA_FIN_GARANTIAS := V_DVP_VALOR;
            
              -- DURACION MAX.GARANT(DIAS)
            ELSIF (V_DVP_CODCONCEPTO = 136) THEN
              V_DURACION_MAX_GARANT_DIAS := V_DVP_VALOR;
            
              -- DURACION MAX.GARANT(MESES)
            ELSIF (V_DVP_CODCONCEPTO = 137) THEN
              V_DURACION_MAX_GARANT_MESES := V_DVP_VALOR;
            
              -- EST.FEN.INICIO GARANTIAS
            ELSIF (V_DVP_CODCONCEPTO = 139) THEN
              V_EST_FEN_INICIO_GARANTIAS := V_DVP_VALOR;
            
              -- FECHA INICIO GARANTIAS
            ELSIF (V_DVP_CODCONCEPTO = 138) THEN
              V_FECHA_INICIO_GARANTIAS := V_DVP_VALOR;
            
              -- DIAS INICIO GARANTIAS
            ELSIF (V_DVP_CODCONCEPTO = 140) THEN
              V_DIAS_INICIO_GARANTIAS := V_DVP_VALOR;
            
              -- MESES INICIO GARANTIAS
            ELSIF (V_DVP_CODCONCEPTO = 141) THEN
              V_MESES_INICIO_GARANTIAS := V_DVP_VALOR;
            
              -- PERIODO GARANTIAS
            ELSIF (V_DVP_CODCONCEPTO = 157) THEN
              V_PERIODO_GARANTIAS := V_DVP_VALOR;
            
              -- TIPO RENDIMIENTO
            ELSIF (V_DVP_CODCONCEPTO = 502) THEN
              V_TIPO_RENDIMIENTO := V_DVP_VALOR;
            
              -- Fin del bloque IF
            END IF;
          
            -- Fin del bucle de datos variables
          END LOOP;
        
          -- PRECIO
          BEGIN
            select nvl(carm.precio, capaseg.precio)
              INTO V_PRECIO
              from o02agpe1.tb_capitales_asegurados_bak capaseg
              left join o02agpe1.tb_capaseg_mod_bak carm on carm.idcapasegprod =
                                                            capaseg.idcapitalasegurado
                                                        and carm.codmoduloprod =
                                                            lista_registros(idx)
            .CODMODULO
             where capaseg.idcapitalasegurado = lista_registros(idx)
            .IDCAPITALASEGURADO
               and rownum = 1;
          EXCEPTION
            WHEN OTHERS THEN
              V_PRECIO := NULL;
          END;
        
          -- PRODUCCION
          BEGIN
            select nvl(carm.prod, capaseg.produccion)
              INTO V_PRODUCCION
              from o02agpe1.tb_capitales_asegurados_bak capaseg
              left join o02agpe1.tb_capaseg_mod_bak carm on carm.idcapasegprod =
                                                            capaseg.idcapitalasegurado
                                                        and carm.codmoduloprod =
                                                            lista_registros(idx)
            .CODMODULO
             where capaseg.idcapitalasegurado = lista_registros(idx)
            .IDCAPITALASEGURADO
               and rownum = 1;
          EXCEPTION
            WHEN OTHERS THEN
              V_PRODUCCION := NULL;
          END;
        
          CLOSE cursor_dvp;
        
          -- Fin del if de la comprobacion de V_IDCAPITALASEGURADO
        END IF;
      
        -- RIESGO CUBIERTO ELEGIDO
        IF (lista_registros(idx).RIESGO_CUBIERTO_ELEGIDO = -1) THEN
          V_RIESGO_CUBIERTO_ELEGIDO := 'Si';
        ELSIF (lista_registros(idx).RIESGO_CUBIERTO_ELEGIDO = -2) THEN
          V_RIESGO_CUBIERTO_ELEGIDO := 'No';
        END IF;
      
        -- CODUSUARIO
        BEGIN
          select p.codusuario
            into V_CODUSUARIO
            from o02agpe1.tb_plz_hist_alta_bak p
           where p.idpoliza = lista_registros(idx).IDPOLIZA;
        EXCEPTION
          WHEN OTHERS THEN
            V_CODUSUARIO := NULL;
        END;
      
        -- COSTE NETO
        BEGIN
          SELECT dcp.costeneto
            INTO V_COSTENETO
            from o02agpe1.tb_dist_coste_parcelas_bak dcp
           where dcp.iddistcoste = lista_registros(idx)
          .ID
             and dcp.hoja = lista_registros(idx)
          .HOJA
             and dcp.numero = lista_registros(idx)
          .NUMERO
             and dcp.tipo = lista_registros(idx).CODTIPOCAPITAL;
        EXCEPTION
          WHEN OTHERS THEN
            V_COSTENETO := NULL;
        END;
      
        -- TASA COMERCIAL
        BEGIN
          select dcp.tasacomercial
            INTO V_TASACOMERCIAL
            from o02agpe1.tb_dist_coste_parcelas_bak dcp
           where dcp.iddistcoste = lista_registros(idx)
          .ID
             and dcp.hoja = lista_registros(idx)
          .HOJA
             and dcp.numero = lista_registros(idx)
          .NUMERO
             and dcp.tipo = lista_registros(idx).CODTIPOCAPITAL;
        EXCEPTION
          WHEN OTHERS THEN
            V_TASACOMERCIAL := NULL;
        END;
      
        BEGIN
          select sum(CASE
                       WHEN rec.tip_bon_rec = 'B' THEN
                        br.importe
                       ELSE
                        -br.importe
                     END)
            INTO V_SUM_BONIF_REC
            from o02agpe0.TB_DIST_COSTE_PARC_BR br
           inner join o02agpe0.TB_SC_C_BONIF_RECARG rec on rec.cod_bon_rec =
                                                           br.codigo
           where br.ID_DIST_COSTE_PARCELAS = lista_registros(idx).ID;
        EXCEPTION
          WHEN OTHERS THEN
            V_SUM_BONIF_REC := NULL;
        END;
      
        BEGIN
          select sum(CASE
                       WHEN ORIGEN_SUBV = 'E' THEN
                        importe
                       ELSE
                        0
                     END),
                 sum(CASE
                       WHEN ORIGEN_SUBV = 'C' THEN
                        importe
                       ELSE
                        0
                     END),
                 sum(CASE
                       WHEN ORIGEN_SUBV = 'D' THEN
                        importe
                       ELSE
                        0
                     END)
            INTO V_SUM_SUBV_ENESA, V_SUM_SUBV_CCAA, V_SUM_SUBV_DESG
            from o02agpe0.TB_DIST_COSTE_PARC_SUBV
           where ID_DIST_COSTE_PARCELAS = lista_registros(idx).ID;
        EXCEPTION
          WHEN OTHERS THEN
            V_SUM_SUBV_ENESA := NULL;
            V_SUM_SUBV_CCAA  := NULL;
            V_SUM_SUBV_DESG  := NULL;
        END;
      
        -- ---------------------------- --
        -- Insert en la tabla de backup --
        -- ---------------------------- --
        INSERT INTO o02agpe1.TB_INF_POLIZAS_PARCELAS_BAK
        VALUES
          (lista_registros(idx).IDPOLIZA,
           lista_registros(idx).REFERENCIA,
           lista_registros(idx).CODMODULO,
           lista_registros(idx).CODPROVINCIA,
           lista_registros(idx).CODCOMARCA,
           lista_registros(idx).CODTERMINO,
           lista_registros(idx).SUBTERMINO,
           lista_registros(idx).CODCULTIVO,
           lista_registros(idx).CODVARIEDAD,
           lista_registros(idx).SIGPAC,
           lista_registros(idx).NOMPARCELA,
           lista_registros(idx).CODTIPOCAPITAL,
           lista_registros(idx).DESTIPOCAPITAL,
           V_ROTACION,
           V_CICLO_CULTIVO,
           V_DENOM_ORIGEN,
           V_CODIGO_IGP,
           V_REDUCC_RDTO,
           V_CODIGO_CERTIFICADO,
           V_DENSIDAD,
           V_DESTINO,
           V_EDAD,
           V_EDAD_CUBIERTA,
           V_EDAD_ESTRUCTURA,
           V_FECHA_RECOLECCION,
           V_FECHA_SIEMBRA_TRASPLANTE,
           V_INDIC_GASTOS_SALVAMENTO,
           V_MATERIAL_CUBIERTA,
           V_MATERIAL_ESTRUCTURA,
           V_MEDIDA_PREVENTIVA,
           V_METROS_LINEALES,
           V_METROS_CUADRADOS,
           V_NUM_ANHOS_DESDE_DESCORCHE,
           V_NUM_ANHOS_DESDE_PODA,
           V_NUMERO_UNIDADES,
           V_PENDIENTE,
           V_SISTEMA_CULTIVO,
           V_SISTEMA_PRODUCCION,
           V_SISTEMA_PROTECCION,
           V_TIPO_INSTALACION,
           V_TIPO_MASA,
           V_TIPO_MARCO_PLANTACION,
           V_TIPO_PLANTACION,
           V_SISTEMA_CONDUCCION,
           V_TIPO_SUB_DECLARADA_PARCEL,
           V_PRACTICA_CULTURAL,
           V_TIPO_TERRENO,
           V_VALOR_FIJO,
           V_EST_FEN_FIN_GARANTIAS,
           V_FECHA_FIN_GARANTIAS,
           V_DURACION_MAX_GARANT_DIAS,
           V_DURACION_MAX_GARANT_MESES,
           V_EST_FEN_INICIO_GARANTIAS,
           V_FECHA_INICIO_GARANTIAS,
           V_DIAS_INICIO_GARANTIAS,
           V_MESES_INICIO_GARANTIAS,
           V_PERIODO_GARANTIAS,
           V_TIPO_RENDIMIENTO,
           V_RIESGO_CUBIERTO_ELEGIDO,
           lista_registros(idx).IDCAPITALASEGURADO,
           lista_registros(idx).CODENTIDAD,
           lista_registros(idx).OFICINA,
           lista_registros(idx).CODUSUARIO,
           lista_registros(idx).SUPERFICIE,
           V_PRECIO,
           V_PRODUCCION,
           lista_registros(idx).PRECIOMODIF,
           lista_registros(idx).PRODUCCIONMODIF,
           lista_registros(idx).ALTAENCOMPLEMENTARIO,
           lista_registros(idx).INCREMENTOPRODUCCION,
           V_COSTENETO,
           V_TASACOMERCIAL,
           lista_registros(idx).IDESTADO,
           lista_registros(idx).codentidadmediadora,
           lista_registros(idx).codsubentidadmediadora,
           lista_registros(idx).delegacion,
           lista_registros(idx).hoja,
           lista_registros(idx).numero,
           lista_registros(idx).parcela_agricola,
           V_NIF_SOCIO,
           V_SUM_BONIF_REC,
           V_SUM_SUBV_ENESA,
           V_SUM_SUBV_CCAA,
           V_SUM_SUBV_DESG);
      
        cont := cont + 1;
      
        IF (MOD(cont, maxCommit) = 0) THEN
          COMMIT;
          PQ_Utl.LOG(lc, '-- Insertados ' || cont || ' registros.', 2);
        END IF;
      
      -- Fin del bucle interno del for
      END LOOP;
    
      -- Fin del bucle de polizas parcelas
    END LOOP;
  
    CLOSE registros_cursor;
  
    COMMIT;
  
  EXCEPTION
    WHEN OTHERS THEN
      PQ_Utl.LOG(lc,
                 'Error al procesar el registro ' || cont || ', idpoliza ' ||
                 v_idpoliza_act,
                 2);
      RAISE;
    
  END act_tb_inf_polizas_parcelas;

  --------------------------------------------------------------------
  -- Anhade al correo el mensaje de numero de registros actualizados --
  --------------------------------------------------------------------
  procedure setMsgCount(origDatos IN VARCHAR2, numTeorico IN VARCHAR2) as
  
    count_aux NUMBER := 0;
  
  begin
  
    execute immediate ('select count(*)  from o02agpe1.' || origDatos)
      into count_aux;
  
    v_cuerpo := v_cuerpo || '  ' || v_retorno || UPPER(origDatos) ||
                '(teo. ' || numTeorico || ', real ' || count_aux || ').  ';
  
  end;

  -------------------------------------------------------------------
  -- Carga los datos correspondientes en la tabla 'TB_CAPASEG_MOD' --
  -------------------------------------------------------------------
  procedure act_tb_capaseg_mod(v_anios_actualizar IN NUMBER,
                               v_plan_actual      IN NUMBER) as
  
    v_anio_inicial NUMBER(4) := v_plan_actual - v_anios_actualizar;
  
  begin
  
    execute immediate ('insert into o02agpe1.TB_CAPASEG_MOD_BAK
                         select carm.idcapitalasegurado idcapasegprod,
                                carm.codmodulo          codmoduloprod,
                                carm.produccion         prod,
                                carm.precio             precio
                            from o02agpe0.tb_cap_aseg_rel_modulo  carm,
                                 o02agpe0.tb_capitales_asegurados ca,
								 o02agpe0.tb_parcelas pa,
								 o02agpe0.tb_polizas p,
								 o02agpe0.tb_lineas l
                            where ca.idcapitalasegurado = carm.idcapitalasegurado(+)
								  and pa.idparcela = ca.idparcela
								  and pa.idpoliza = p.idpoliza
								  and p.lineaseguroid = l.lineaseguroid
								  and l.codplan BETWEEN ' || v_anio_inicial ||
                      ' and ' || v_plan_actual);
  
    commit;
  
  EXCEPTION
    WHEN OTHERS THEN
      ROLLBACK;
      -- Se vuelve a lanzar la excepcion para que cancele la cadena
      RAISE;
    
  end act_tb_capaseg_mod;

  -----------------------------------------------------------------------
  -- Devuelve un boolean indicando si ya se esta ejecutando el proceso --
  -----------------------------------------------------------------------
  function ejecutando return boolean as
  
    aux_count NUMBER := 0;
  
  begin
  
    select count(*)
      into aux_count
      from o02agpe1.tb_historico_cargas hc
     where hc.estado is null;
  
    IF (aux_count = 0) THEN
      RETURN FALSE;
    ELSE
      RETURN TRUE;
    END IF;
  
  end;

  -------------------------------------------------------------------------------
  -- Carga los datos correspondientes en la tabla 'TB_DIST_COSTE_PARCELAS_BAK' --
  -------------------------------------------------------------------------------
  procedure act_tb_dist_coste_parcelas(v_anios_actualizar IN NUMBER,
                                       v_plan_actual      IN NUMBER) as
  
  begin
  
    execute immediate ('insert into o02agpe1.TB_DIST_COSTE_PARCELAS_BAK
                         select iddistcoste, tipo, hoja, numero, costeneto, tasacomercial
                         from o02agpe0.tb_dist_coste_parcelas');
    commit;
  
  EXCEPTION
    WHEN OTHERS THEN
      ROLLBACK;
      -- Se vuelve a lanzar la excepcion para que cancele la cadena
      RAISE;
    
  end act_tb_dist_coste_parcelas;

  ----------------------------------------------------------------------------
  -- Carga los datos correspondientes en las tablas de historico de estados --
  ----------------------------------------------------------------------------
  procedure actualizaHistoricoEstados as
  
  begin
  
    -- HISTORICO ALTA
    act_tb_historico('TB_PLZ_HIST_ALTA_BAK', 1, 'ASC');
    -- HISTORICO ENVIO
    act_tb_historico('TB_PLZ_HIST_ENVIO_BAK', 5, 'DESC');
    -- HISTORICO PROVISIONAL
    act_tb_historico('TB_PLZ_HIST_PROV_BAK', 2, 'DESC');
    -- HISTORICO DEFINITIVA
    act_tb_historico('TB_PLZ_HIST_DEF_BAK', 3, 'DESC');
  
  end actualizaHistoricoEstados;

  ----------------------------------------------------------------------------
  -- Borra los datos correspondientes en las tablas de historico de estados --
  ----------------------------------------------------------------------------
  procedure truncaHistoricoEstados as
  
  begin
  
    TRUNCATE_TABLE('o02agpe1.TB_PLZ_HIST_ALTA_BAK');
    TRUNCATE_TABLE('o02agpe1.TB_PLZ_HIST_ENVIO_BAK');
    TRUNCATE_TABLE('o02agpe1.TB_PLZ_HIST_PROV_BAK');
    TRUNCATE_TABLE('o02agpe1.TB_PLZ_HIST_DEF_BAK');
  
  EXCEPTION
    WHEN OTHERS THEN
      PQ_Utl.LOG(lc,
                 'Error al truncar las tablas de historico de estados: ' ||
                 SQLCODE || ' - ' || SQLERRM || '. ',
                 2);
      -- Se vuelve a lanzar la excepcion para que cancele la cadena
      RAISE;
    
  end truncaHistoricoEstados;

  ----------------------------------------------------------------------------
  -- Carga los datos correspondientes en las tablas de historico de estados --
  ----------------------------------------------------------------------------
  procedure act_tb_historico(nomTabla IN VARCHAR,
                             estado   IN NUMBER,
                             orden    IN VARCHAR) as
  
    TYPE listaIdPlzTipo IS REF CURSOR;
    listaIdPlz listaIdPlzTipo;
    v_idpoliza NUMBER(15);
  
  begin
  
    PQ_Utl.LOG(lc,
               'Insertar historico en la tabla ' || nomTabla ||
               ' para el estado ' || estado || ' y el orden ' || orden,
               2);
  
    -- Selecciona los idpoliza que solo tienen un registro para el estado indicado -> Se insertaran directamente
    execute immediate ('
insert into o02agpe1.' || nomTabla || ' (
select idpoliza, nvl (codusuario, '' ''), fecha
  from o02agpe0.tb_polizas_historico_estados
 where estado = ' || estado || '
   and idpoliza in (select idpoliza
                      from o02agpe0.TB_POLIZAS_HISTORICO_ESTADOS
                     WHERE ESTADO = ' || estado || '
                     group by idpoliza
                     having count(*) = 1))');
  
    commit;
  
    PQ_Utl.LOG(lc,
               'Insertar historico multiple en la tabla ' || nomTabla ||
               ' para el estado ' || estado || ' y el orden ' || orden);
  
    -- Selecciona los idpoliza que tiene varios registros para el estado indicado -> Se inserta el mas antiguo
    OPEN listaIdPlz FOR 'select idpoliza
                            from o02agpe0.TB_POLIZAS_HISTORICO_ESTADOS
                            WHERE ESTADO = ' || estado || '
                            group by idpoliza
                            having count(*) > 1';
  
    LOOP
    
      FETCH listaIdPlz
        INTO v_idpoliza;
      EXIT WHEN listaIdPlz%NOTFOUND;
    
      EXECUTE IMMEDIATE ('insert into o02agpe1.' || nomTabla || ' (
                       select *
                       from (select idpoliza, nvl (codusuario, '' ''), fecha
                              from o02agpe0.tb_polizas_historico_estados
                             where estado = ' ||
                        estado || '
                               and idpoliza = ' ||
                        v_idpoliza || '
                             order by fecha ' ||
                        orden || ')
                       where rownum = 1)');
    
    END LOOP;
  
    CLOSE listaIdPlz;
  
    commit;
  
  EXCEPTION
    WHEN OTHERS THEN
      ROLLBACK;
      -- Se vuelve a lanzar la excepcion para que cancele la cadena
      PQ_Utl.LOG(lc,
                 'Error al cargar las tablas de historico de estados: ' ||
                 SQLCODE || ' - ' || SQLERRM || '. ',
                 2);
      RAISE;
    
  end act_tb_historico;

  ---------------------------------------------------------------------------------------------------
  -- Actualiza los datos variable de polizas parcelas con la descripcion para los que correspondan --
  ---------------------------------------------------------------------------------------------------

  procedure act_datosVariablesDescripcion as
  
    tablasDesc arrayTablasDesc;
  
    contador NUMBER;
  
  begin
  
    tablasDesc := arrayTablasDesc(arrayInterno('tb_sc_c_ciclo_cultivo',
                                               'codciclocultivo',
                                               'desciclocultivo',
                                               'CICLO_CULTIVO'),
                                  arrayInterno('tb_sc_c_certificado_instal',
                                               'CODCERTIFICADOINSTAL',
                                               'DESCRIPCION',
                                               'CODIGO_CERTIFICADO'),
                                  arrayInterno('tb_sc_c_destinos',
                                               'CODDESTINO',
                                               'DESDESTINO',
                                               'DESTINO'),
                                  arrayInterno('tb_sc_c_Material_Cubierta',
                                               'CODMATERIALCUBIERTA',
                                               'DESCRIPCION',
                                               'MATERIAL_CUBIERTA'),
                                  arrayInterno('tb_sc_c_material_estructura',
                                               'CODMATERIALESTRUCTURA',
                                               'DESCRIPCION',
                                               'MATERIAL_ESTRUCTURA'),
                                  arrayInterno('tb_sc_c_sistema_cultivo',
                                               'CODSISTEMACULTIVO',
                                               'DESSISTEMACULTIVO',
                                               'SISTEMA_CULTIVO'),
                                  arrayInterno('tb_sc_c_Sistema_Produccion',
                                               'CODSISTEMAPRODUCCION',
                                               'DESSISTEMAPRODUCCION',
                                               'SISTEMA_PRODUCCION'),
                                  arrayInterno('Tb_Sc_c_Sistema_Proteccion',
                                               'CODSISTEMAPROTECCION',
                                               'DESSISTEMAPROTECCION',
                                               'SISTEMA_PROTECCION'),
                                  arrayInterno('tb_sc_c_Tipo_Instalacion',
                                               'CODTIPOINSTALACION',
                                               'DESCRIPCION',
                                               'TIPO_INSTALACION'),
                                  arrayInterno('tb_sc_c_Tipo_Masa',
                                               'CODTIPOMASA',
                                               'DESTIPOMASA',
                                               'TIPO_MASA'),
                                  arrayInterno('tb_sc_c_Marco_Plantacion',
                                               'CODTIPOMARCOPLANTAC',
                                               'DESTIPOMARCOPLANTAC',
                                               'TIPO_MARCO_PLANTACION'),
                                  arrayInterno('tb_sc_c_Tipo_Plantacion',
                                               'CODTIPOPLANTACION',
                                               'DESTIPOPLANTACION',
                                               'TIPO_PLANTACION'),
                                  arrayInterno('tb_sc_c_Sistema_Conduccion',
                                               'CODSISTEMACONDUCCION',
                                               'DESSISTEMACONDUCCION',
                                               'SISTEMA_CONDUCCION'),
                                  arrayInterno('tb_sc_c_Practica_Cultural',
                                               'CODPRACTICACULTURAL',
                                               'DESPRACTICACULTURAL',
                                               'PRACTICA_CULTURAL'),
                                  arrayInterno('tb_sc_c_Tipo_Terreno',
                                               'CODTIPOTERRENO',
                                               'DESTIPOTERRENO',
                                               'TIPO_TERRENO'));
  
    contador := tablasDesc.FIRST;
  
    WHILE contador IS NOT NULL LOOP
      updateDescDV(tablasDesc(contador));
      contador := tablasDesc.NEXT(contador);
    END LOOP;
  
  end act_datosVariablesDescripcion;

  ---------------------------------------------------------------------------------------------------
  -- Actualiza los datos variable de polizas parcelas con la descripcion para los que correspondan --
  ---------------------------------------------------------------------------------------------------

  procedure updateDescDV(reg IN arrayInterno) as
  
    TYPE cur_typ IS REF CURSOR;
    cursor_cond     cur_typ;
    query           VARCHAR2(100);
    v_query_execute VARCHAR2(1000);
  
    V_CODIGO NUMBER(3);
    V_DESC   VARCHAR2(25);
  
  begin
  
    query := 'SELECT ' || reg(2) || ',' || reg(3) || ' FROM o02agpe0.' ||
             reg(1);
  
    OPEN cursor_cond FOR query;
  
    LOOP
    
      FETCH cursor_cond
        INTO V_CODIGO, V_DESC;
      EXIT WHEN cursor_cond%NOTFOUND;
    
      v_query_execute := 'UPDATE o02agpe1.TB_INF_POLIZAS_PARCELAS_BAK SET ' ||
                         reg(4) || ' = ''' || V_CODIGO || ' - ' || V_DESC ||
                         ''' WHERE ' || reg(4) || '=''' || V_CODIGO || '''';
    
      EXECUTE IMMEDIATE v_query_execute;
    
    END LOOP;
  
    CLOSE cursor_cond;
  
    COMMIT;
  
  end updateDescDV;

  ---------------------------------------------------------------------------------------------------
  -- Obtiene de la tabla de configuracion la variable que indica si la actualizacion de datos es   --
  -- completa o solo de las polizas que no estan contratadas                                       --
  ---------------------------------------------------------------------------------------------------

  procedure getActCompleta as
  
    v_aux_valor VARCHAR2(2000);
  
  begin
    -- Guarda en v_act_completa el tipo de actualizacion que se va a realizar
    begin
      EXECUTE IMMEDIATE 'select agp_valor from o02agpe0.tb_config_agp where agp_nemo = ''' ||
                        KEY_OD_INFORMES_ACT_COMPLETA || ''''
        INTO v_aux_valor;
    
      IF (v_aux_valor = 'NO') THEN
        v_act_completa := false;
      ELSE
        v_act_completa := true;
      END IF;
    
    exception
      when others then
        v_act_completa := true;
    end;
  
    -- Indica en el log el tipo de actualizacion que se va a realizar
    IF (v_act_completa) THEN
      PQ_Utl.LOG(lc,
                 'Se van a actualizar los registros de todas las polizas --',
                 2);
    ELSE
      PQ_Utl.LOG(lc,
                 'Se van a actualizar los registros asociados a polizas no contratadas --',
                 2);
    END IF;
  
  end getActCompleta;

  ---------------------------------------------------------------------------------------------------
  -- Obtiene el numero de registros que se espera insertar para cada origen de datos               --
  ---------------------------------------------------------------------------------------------------

  procedure getNumRegistrosEsperados(v_anios_actualizar IN NUMBER,
                                     v_plan_actual      IN NUMBER) as
  
    v_anio_inicial NUMBER(4) := v_plan_actual - v_anios_actualizar;
  
  begin
  
    PQ_Utl.LOG(lc, 'procedure getNumRegistrosEsperados', 2);
    PQ_Utl.LOG(lc, 'anhos a actualizar = ' || v_anios_actualizar, 2);
    PQ_Utl.LOG(lc, 'anho actual = ' || v_plan_actual, 2);
    PQ_Utl.LOG(lc,
               'anho inical (anho actual - anhos a actualizar)= ' ||
               v_anio_inicial,
               2);
  
    -- Polizas
    begin
      execute immediate ('SELECT
								COUNT(pol.idpoliza)
						    FROM
								o02agpe0.tb_polizas pol
								inner join o02agpe0.tb_lineas lin on lin.codplan BETWEEN ' ||
                        v_anio_inicial || ' and ' || v_plan_actual ||
                        ' and lin.lineaseguroid = pol.lineaseguroid
							')
        into v_numreg_plz;
    exception
      when others then
        v_numreg_plz := 0;
    end;
  
    -- Polizas coberturas
    begin
      execute immediate ('SELECT
								COUNT(*)
							FROM
								o02agpe0.tb_comparativas_poliza cpol
								INNER JOIN o02agpe0.tb_polizas pol ON cpol.idpoliza = pol.idpoliza
								inner join o02agpe0.tb_lineas lin on lin.codplan BETWEEN ' ||
                        v_anio_inicial || ' and ' || v_plan_actual ||
                        ' and lin.lineaseguroid = pol.lineaseguroid
							')
        into v_numreg_coberturas;
    exception
      when others then
        v_numreg_coberturas := 0;
    end;
  
    -- Asegurados
    begin
      execute immediate ('select count(a.id) from o02agpe0.tb_asegurados a
                            where a.id not in (select blq.id_asegurado from o02agpe0.tb_bloqueos_asegurados blq
                                  where blq.idestado_aseg =''B'')')
        into v_numreg_aseg;
    exception
      when others then
        v_numreg_aseg := 0;
    end;
  
    -- Asegurados socios
    begin
      execute immediate ('select count(*) from o02agpe0.tb_socios')
        into v_numreg_aseg_socios;
    exception
      when others then
        v_numreg_aseg_socios := 0;
    end;
  
    -- Colectivos
    begin
      execute immediate ('SELECT
								COUNT(col.id)
							FROM
								o02agpe0.tb_colectivos col
								inner join o02agpe0.tb_lineas lin on lin.codplan BETWEEN ' ||
                        v_anio_inicial || ' and ' || v_plan_actual ||
                        ' and lin.lineaseguroid = col.lineaseguroid
							')
        into v_numreg_colectivos;
    exception
      when others then
        v_numreg_colectivos := 0;
    end;
  
    -- Siniestros
    begin
      execute immediate ('SELECT
								COUNT(sin.id)
							FROM
								o02agpe0.tb_siniestros sin
								INNER JOIN o02agpe0.tb_polizas pol ON sin.idpoliza = pol.idpoliza
								inner join o02agpe0.tb_lineas lin on lin.codplan BETWEEN ' ||
                        v_anio_inicial || ' and ' || v_plan_actual ||
                        ' and lin.lineaseguroid = pol.lineaseguroid
							')
        into v_numreg_siniestros;
    exception
      when others then
        v_numreg_siniestros := 0;
    end;
  
    -- Siniestros parcelas
    begin
      execute immediate ('select count(sp.id) from o02agpe0.tb_siniestro_parcelas sp
								inner join o02agpe0.tb_siniestros s on s.id = sp.idsiniestro
								inner join o02agpe0.tb_polizas p on p.idpoliza = s.idpoliza
								inner join o02agpe0.tb_lineas lin on lin.codplan BETWEEN ' ||
                        v_anio_inicial || ' and ' || v_plan_actual ||
                        ' and lin.lineaseguroid = p.lineaseguroid
							where sp.altaensiniestro = ''S''')
        into v_numreg_siniestros_parc;
    exception
      when others then
        v_numreg_siniestros_parc := 0;
    end;
  
    -- Anexos de modificacion
    begin
      execute immediate ('SELECT
								COUNT(anex.id)
							FROM
								o02agpe0.tb_anexo_mod anex
								INNER JOIN o02agpe0.tb_polizas pol ON anex.idpoliza = pol.idpoliza
								inner join o02agpe0.tb_lineas lin on lin.codplan BETWEEN ' ||
                        v_anio_inicial || ' and ' || v_plan_actual ||
                        ' and lin.lineaseguroid = pol.lineaseguroid
							')
        into v_numreg_am;
    exception
      when others then
        v_numreg_am := 0;
    end;
  
    -- Anexos modificacion - Parcelas
    begin
      execute immediate ('select count(ap.id) from o02agpe0.tb_anexo_mod_parcelas ap
								inner join o02agpe0.tb_anexo_mod a on a.id = ap.idanexo
								inner join o02agpe0.tb_polizas p on p.idpoliza = a.idpoliza
								inner join o02agpe0.tb_lineas lin on lin.codplan BETWEEN ' ||
                        v_anio_inicial || ' and ' || v_plan_actual ||
                        ' and lin.lineaseguroid = p.lineaseguroid
							where ap.tipomodificacion is not null')
        into v_numreg_am_par;
    exception
      when others then
        v_numreg_am_par := 0;
    end;
  
    -- Anexos modificacion - Coberturas
    begin
      execute immediate ('select count(ac.id) from o02agpe0.Tb_Anexo_Mod_Coberturas ac
								inner join o02agpe0.tb_anexo_mod a on a.id = ac.idanexo
								inner join o02agpe0.tb_polizas p on p.idpoliza = a.idpoliza
								inner join o02agpe0.tb_lineas lin on lin.codplan BETWEEN ' ||
                        v_anio_inicial || ' and ' || v_plan_actual ||
                        ' and lin.lineaseguroid = p.lineaseguroid
							')
        into v_numreg_am_cob;
    exception
      when others then
        v_numreg_am_cob := 0;
    end;
  
    -- Anexos reduccion
    begin
      execute immediate ('SELECT
								COUNT(anex.id)
							FROM
								o02agpe0.tb_anexo_red anex
								INNER JOIN o02agpe0.tb_polizas pol ON anex.idpoliza = pol.idpoliza
								inner join o02agpe0.tb_lineas lin on lin.codplan BETWEEN ' ||
                        v_anio_inicial || ' and ' || v_plan_actual ||
                        ' and lin.lineaseguroid = pol.lineaseguroid
							')
        into v_numreg_ar;
    exception
      when others then
        v_numreg_ar := 0;
    end;
  
    -- Anexos reduccion - Parcelas
    begin
      execute immediate ('select count(ap.id) from o02agpe0.Tb_Anexo_Red_Parcelas ap
								inner join o02agpe0.tb_anexo_red ar on ar.id = ap.idanexo
								inner join o02agpe0.tb_polizas p on p.idpoliza = ar.idpoliza
								inner join o02agpe0.tb_lineas lin on lin.codplan BETWEEN ' ||
                        v_anio_inicial || ' and ' || v_plan_actual ||
                        ' and lin.lineaseguroid = p.lineaseguroid
							where ap.altaenanexo = ''S''')
        into v_numreg_arp;
    exception
      when others then
        v_numreg_arp := 0;
    end;
  
    -- Distribuciones de coste
    begin
      execute immediate ('SELECT
								COUNT(dcos.id)
							FROM
								o02agpe0.tb_distribucion_costes dcos
								inner JOIN o02agpe0.tb_polizas pol ON dcos.idpoliza = pol.idpoliza
								inner join o02agpe0.tb_lineas lin on lin.codplan BETWEEN ' ||
                        v_anio_inicial || ' and ' || v_plan_actual ||
                        ' and lin.lineaseguroid = pol.lineaseguroid
							')
        into v_numreg_dist_coste;
    exception
      when others then
        v_numreg_dist_coste := 0;
    end;
  
    -- Polizas parcelas
    begin
      execute immediate ('select count(ca.idcapitalasegurado) from o02agpe0.tb_capitales_asegurados ca
								inner join o02agpe0.tb_parcelas pa on ca.idparcela = pa.idparcela
								inner join o02agpe0.tb_polizas p on p.idpoliza = pa.idpoliza
								inner join o02agpe0.tb_lineas lin on lin.codplan BETWEEN ' ||
                        v_anio_inicial || ' and ' || v_plan_actual ||
                        ' and lin.lineaseguroid = p.lineaseguroid
							')
        into v_numreg_ca;
    exception
      when others then
        v_numreg_ca := 0;
    end;
  
    -- Cultivos/Variedades
    begin
      execute immediate ('SELECT
								COUNT(*)
							FROM
								o02agpe0.tb_sc_c_variedades var
								inner join o02agpe0.tb_lineas lin on lin.codplan BETWEEN ' ||
                        v_anio_inicial || ' and ' || v_plan_actual ||
                        ' and lin.lineaseguroid = var.lineaseguroid
							')
        into v_numreg_cultvar;
    exception
      when others then
        v_numreg_cultvar := 0;
    end;
  
    -- Datos del asegurado
    begin
      execute immediate ('select count(dt.IDASEGURADO) from  o02agpe0.TB_DATOS_ASEGURADOS dt')
        into v_numreg_datos_aseg;
    exception
      when others then
        v_numreg_datos_aseg := 0;
    end;
  
    -- Comisiones poliza
    begin
      execute immediate ('SELECT
								COUNT(com.idpoliza)
							FROM
								o02agpe0.tb_polizas_pct_comisiones com
								INNER JOIN o02agpe0.tb_polizas pol ON com.idpoliza = pol.idpoliza
								inner join o02agpe0.tb_lineas lin on lin.codplan BETWEEN ' ||
                        v_anio_inicial || ' and ' || v_plan_actual ||
                        ' and lin.lineaseguroid = pol.lineaseguroid
							')
        into v_numreg_comis_poliza;
    exception
      when others then
        v_numreg_comis_poliza := 0;
    end;
  
    -- Distribucion de costes 2015+
    begin
      execute immediate ('SELECT
								COUNT(dc.idpoliza)
							FROM
								o02agpe0.tb_distribucion_costes_2015 dc
								INNER JOIN o02agpe0.tb_polizas pol ON dc.idpoliza = pol.idpoliza
								inner join o02agpe0.tb_lineas lin on lin.codplan BETWEEN ' ||
                        v_anio_inicial || ' and ' || v_plan_actual ||
                        ' and lin.lineaseguroid = pol.lineaseguroid
							')
        into v_numreg_DC_2015;
    
    exception
      when others then
        v_numreg_DC_2015 := 0;
    end;
  
    -- tb_inf_Explotaciones
    begin
      execute immediate ('select count(gr.ID) from O02AGPE0.TB_GRUPO_RAZA_EXPLOTACION gr
								inner join o02agpe0.tb_explotaciones e on e.id = gr.idexplotacion
								inner join o02agpe0.tb_polizas p on p.idpoliza = e.idpoliza
								inner join o02agpe0.tb_lineas lin on lin.codplan BETWEEN ' ||
                        v_anio_inicial || ' and ' || v_plan_actual ||
                        ' and lin.lineaseguroid = p.lineaseguroid
							')
        into v_numreg_Explotaciones;
    exception
      when others then
        v_numreg_Explotaciones := 0;
    end;
  
    -- tb_inf_Explotaciones_Anexo
    begin
      execute immediate ('select count(gr.ID) from O02AGPE0.TB_ANEXO_MOD_GRUPO_RAZA gr
								inner join o02agpe0.tb_anexo_mod_explotaciones e on e.id = gr.id_explotacion_anexo
								inner join o02agpe0.tb_anexo_mod a on a.id = e.id_anexo
								inner join o02agpe0.tb_polizas p on p.idpoliza = a.idpoliza
								inner join o02agpe0.tb_lineas lin on lin.codplan BETWEEN ' ||
                        v_anio_inicial || ' and ' || v_plan_actual ||
                        ' and lin.lineaseguroid = p.lineaseguroid
							')
        into v_numreg_Explo_Anexo;
    exception
      when others then
        v_numreg_Explo_Anexo := 0;
    end;
  
    -- tb_inf_explotaciones_coberturas
    begin
      execute immediate ('select count(ec.ID) from O02AGPE0.TB_EXPLOTACIONES_COBERTURAS ec
								inner join o02agpe0.tb_explotaciones e on e.id = ec.idexplotacion
								inner join o02agpe0.tb_polizas p on p.idpoliza = e.idpoliza
								inner join o02agpe0.tb_lineas lin on lin.codplan BETWEEN ' ||
                        v_anio_inicial || ' and ' || v_plan_actual ||
                        ' and lin.lineaseguroid = p.lineaseguroid
							')
        into v_numreg_expl_cob;
    exception
      when others then
        v_numreg_expl_cob := 0;
    end;
  
    -- tb_inf_parcelas_coberturas
    begin
      execute immediate ('SELECT COUNT(pc.id) FROM o02agpe0.tb_parcelas_coberturas pc
								inner join o02agpe0.tb_lineas lin on lin.codplan BETWEEN ' ||
                        v_anio_inicial || ' and ' || v_plan_actual ||
                        ' and lin.lineaseguroid = pc.lineaseguroid
							')
        into v_numreg_parc_cob;
    exception
      when others then
        v_numreg_parc_cob := 0;
    end;
  
  end getNumRegistrosEsperados;

  procedure act_tb_inf_datos_asegurado(v_anios_actualizar IN NUMBER,
                                       v_plan_actual      IN NUMBER) as
  
    /* Pet. 70105 (Fase III) ** MODIF TAM (01.03.2021) ** Inicio */
    /* Se incluye la cuenta de Siniestros asi, como el titular de la cuenta y el destinatario*/
  
  begin
    execute immediate ('INSERT INTO o02agpe1.TB_INF_DATOS_ASEGURADO_BAK
           select dt.IDASEGURADO, dt.CODLINEA, (dt.IBAN || '' ''
          || SUBSTR(dt.CCC,1,4) || '' '' || SUBSTR(dt.CCC,5,4)
          ||'' '' || SUBSTR(dt.CCC,9,4) || '' '' || SUBSTR(dt.CCC,13,4)
          ||'' '' || SUBSTR(dt.CCC,17,4)) as IBAN,
          (dt.IBAN2 || '' ''
          || SUBSTR(dt.CCC2,1,4) || '' '' || SUBSTR(dt.CCC2,5,4)
          ||'' '' || SUBSTR(dt.CCC2,9,4) || '' '' || SUBSTR(dt.CCC2,13,4)
          ||'' '' || SUBSTR(dt.CCC2,17,4)) as IBAN2,
          dt.dest_domiciliacion, dt.titular_cuenta
          from  o02agpe0.TB_DATOS_ASEGURADOS dt');
  
    commit;
  
  EXCEPTION
    WHEN OTHERS THEN
      ROLLBACK;
      -- Se vuelve a lanzar la excepcion para que cancele la cadena
      RAISE;
    
  end act_tb_inf_datos_asegurado;

  function getGrupoNegocioPoliza(p_idPoliza NUMBER) RETURN VARCHAR2 IS
    gn       VARCHAR2(1) := null;
    codGrupo VARCHAR2(3) := '';
    str_sql  varchar2(400) := '';
  BEGIN
     BEGIN
      str_sql:='SELECT sc.CODGRUPOSEGURO FROM o02agpe0.tb_polizas pol
            inner join o02agpe0.tb_lineas lin on pol.LINEASEGUROID = lin.LINEASEGUROID
            inner join o02agpe0.TB_SC_C_LINEAS sc ON sc.CODLINEA = lin.CODLINEA
            group by sc.CODGRUPOSEGURO, pol.idpoliza
            having pol.IDPOLIZA =' || p_idPoliza;
    
      EXECUTE IMMEDIATE str_sql
        INTO codGrupo;
    
      IF codGrupo = 'A01' THEN
        gn := '1';
      ELSIF codGrupo = 'G01' THEN
        gn := '2';
      ELSE
        gn := null;
      END IF;
    
    EXCEPTION
      WHEN NO_DATA_FOUND THEN
        gn := null;
    END;
    RETURN gn;
  END getGrupoNegocioPoliza;

  procedure actualizarGN_ComisionesPoliza(v_anios_actualizar IN NUMBER,
                                          v_plan_actual      IN NUMBER) IS
    v_idPoliza NUMBER(15, 0) := NULL;
    v_gn       VARCHAR2(1) := NULL;
    TYPE cur_typ IS REF CURSOR;
    c_pct_comis    cur_typ;
    v_updateSql    VARCHAR(200) := null;
    v_anio_inicial NUMBER(4) := v_plan_actual - v_anios_actualizar;
  
    consulta VARCHAR2(400) := 'SELECT
								pct.idpoliza
							  FROM
								o02agpe0.tb_polizas_pct_comisiones pct
								INNER JOIN o02agpe0.tb_polizas pol ON pct.idpoliza = pol.idpoliza
								inner join o02agpe0.tb_lineas lin on lin.codplan BETWEEN ' ||
                              v_anio_inicial || ' and ' || v_plan_actual ||
                              ' and lin.lineaseguroid = pol.lineaseguroid
							  WHERE
								pct.grupo_negocio IS NULL';
  
  BEGIN
  
    PQ_Utl.LOG(lc, 'procedure actualizarGN_ComisionesPoliza', 2);
    PQ_Utl.LOG(lc, 'anhos a actualizar = ' || v_anios_actualizar, 2);
    PQ_Utl.LOG(lc, 'anho actual = ' || v_plan_actual, 2);
    PQ_Utl.LOG(lc,
               'anho inical (anho actual - anhos a actualizar)= ' ||
               v_anio_inicial,
               2);
  
    OPEN c_pct_comis FOR consulta;
    LOOP
      FETCH c_pct_comis
        INTO v_idPoliza;
      EXIT WHEN c_pct_comis%NOTFOUND;
      v_gn := getGrupoNegocioPoliza(v_idPoliza);
      IF v_gn is not null THEN
        v_updateSql := 'UPDATE o02agpe0.TB_POLIZAS_PCT_COMISIONES SET GRUPO_NEGOCIO =''' || v_gn ||
                       ''' WHERE IDPOLIZA =' || v_idPoliza;
        execute immediate (v_updateSql);
        COMMIT;
      END IF;
    
    END LOOP;
    CLOSE c_pct_comis;
  end actualizarGN_ComisionesPoliza;

  PROCEDURE act_tb_inf_comisiones_poliza(v_anios_actualizar IN NUMBER,
                                         v_plan_actual      IN NUMBER) as
  
    v_insert       VARCHAR2(500) := null;
    v_anio_inicial NUMBER(4) := v_plan_actual - v_anios_actualizar;
  
  begin
  
    PQ_Utl.LOG(lc, 'procedure act_tb_inf_comisiones_poliza', 2);
    PQ_Utl.LOG(lc, 'anhos a actualizar = ' || v_anios_actualizar, 2);
    PQ_Utl.LOG(lc, 'anho actual = ' || v_plan_actual, 2);
    PQ_Utl.LOG(lc,
               'anho inical (anho actual - anhos a actualizar)= ' ||
               v_anio_inicial,
               2);
  
    v_insert := 'INSERT INTO o02agpe1.tb_inf_comisiones_poliza_bak
				SELECT
					com.idpoliza,
					com.grupo_negocio,
					com.pctcommax,
					com.pctentidad,
					com.pctesmediadora,
					com.pctdescelegido,
					com.pctrecarelegido
				FROM
					o02agpe0.tb_polizas_pct_comisiones com
					INNER JOIN o02agpe0.tb_polizas pol ON com.idpoliza = pol.idpoliza
					inner join o02agpe0.tb_lineas lin on lin.codplan BETWEEN ' ||
                v_anio_inicial || ' and ' || v_plan_actual ||
                ' and lin.lineaseguroid = pol.lineaseguroid';
  
    execute immediate (v_insert);
    commit;
  
  EXCEPTION
    WHEN OTHERS THEN
      ROLLBACK;
      -- Se vuelve a lanzar la excepcion para que cancele la cadena
      RAISE;
    
  END act_tb_inf_comisiones_poliza;

  procedure actualizarGN_DC2015Poliza(v_anios_actualizar IN NUMBER,
                                      v_plan_actual      IN NUMBER) as
    v_idPoliza NUMBER(15, 0) := NULL;
    --v_id NUMBER(15,0):=NULL;
    v_gn VARCHAR2(1) := NULL;
    TYPE cur_typ IS REF CURSOR;
    cur_dc_2015    cur_typ;
    v_update       VARCHAR2(300) := null;
    v_anio_inicial NUMBER(4) := v_plan_actual - v_anios_actualizar;
  
    consulta VARCHAR2(400) := 'SELECT
								dc.IDPOLIZA
							 FROM
								O02AGPE0.TB_DISTRIBUCION_COSTES_2015 dc
							 inner JOIN o02agpe0.TB_POLIZAS pol ON dc.IDPOLIZA = pol.IDPOLIZA
							 inner join o02agpe0.tb_lineas lin on lin.codplan BETWEEN ' ||
                              v_anio_inicial || ' and ' || v_plan_actual ||
                              ' and lin.lineaseguroid = pol.lineaseguroid
							 WHERE dc.GRUPO_NEGOCIO IS NULL GROUP BY dc.IDPOLIZA';
  
  BEGIN
  
    PQ_Utl.LOG(lc, 'procedure actualizarGN_DC2015Poliza', 2);
    PQ_Utl.LOG(lc, 'anhos a actualizar = ' || v_anios_actualizar, 2);
    PQ_Utl.LOG(lc, 'anho actual = ' || v_plan_actual, 2);
    PQ_Utl.LOG(lc,
               'anho inical (anho actual - anhos a actualizar)= ' ||
               v_anio_inicial,
               2);
  
    OPEN cur_dc_2015 FOR consulta;
    LOOP
      FETCH cur_dc_2015
        INTO v_idPoliza;
      EXIT WHEN cur_dc_2015%NOTFOUND;
      v_gn := getGrupoNegocioPoliza(v_idPoliza);
      IF v_gn is not null THEN
        v_update := 'UPDATE o02agpe0.TB_DISTRIBUCION_COSTES_2015 SET GRUPO_NEGOCIO =''' || v_gn ||
                    ''' WHERE IDPOLIZA = ' || v_idPoliza ||
                    ' and GRUPO_NEGOCIO is null';
        execute immediate (v_update);
        COMMIT;
      END IF;
    
    END LOOP;
    CLOSE cur_dc_2015;
  
  end actualizarGN_DC2015Poliza;

  procedure act_tb_inf_dist_costes_2015(v_anios_actualizar IN NUMBER,
                                        v_plan_actual      IN NUMBER) as
  
    v_anio_inicial NUMBER(4) := v_plan_actual - v_anios_actualizar;
  
  begin
  
    PQ_Utl.LOG(lc, 'procedure act_tb_inf_dist_costes_2015', 2);
    PQ_Utl.LOG(lc, 'anhos a actualizar = ' || v_anios_actualizar, 2);
    PQ_Utl.LOG(lc, 'anho actual = ' || v_plan_actual, 2);
    PQ_Utl.LOG(lc,
               'anho inicial (anho actual - anhos a actualizar)= ' ||
               v_anio_inicial,
               2);
  
    execute immediate ('insert into o02agpe1.TB_INF_DIST_COSTES_2015_BAK
						SELECT dc.IDPOLIZA, dc.GRUPO_NEGOCIO, dc.PRIMACOMERCIAL, dc.PRIMACOMERCIALNETA, dc.RECARGOCONSORCIO,
							dc.RECIBOPRIMA, dc.COSTETOMADOR, dc.TOTALCOSTETOMADOR, dc.RECARGOAVAL, dc.RECARGOFRACCIONAMIENTO,
							BONI.importe - RECA.importe as IMPORTE,
							nvl(subEnesa.SUBVENCION_ENESA, 0), nvl(subCCAA.SUBVENCION_CCAA, 0), 
							nvl(dc.IMP_CMSN_ENTIDAD, 0), nvl(dc.IMP_CMSN_ESMED, 0)
						FROM O02AGPE0.TB_DISTRIBUCION_COSTES_2015 dc
						LEFT OUTER JOIN (
							select sum(sub.IMPORTESUBV) as SUBVENCION_ENESA, sub.IDDISTCOSTE from O02AGPE0.TB_DIST_COSTE_SUBVS_2015 sub
							where sub.CODORGANISMO=''0'' group by sub.IDDISTCOSTE) subEnesa on dc.id = subEnesa.IDDISTCOSTE
						LEFT OUTER JOIN (
							select sum(sub.IMPORTESUBV) as SUBVENCION_CCAA, sub.IDDISTCOSTE from O02AGPE0.TB_DIST_COSTE_SUBVS_2015 sub
							where sub.CODORGANISMO<>''0'' group by sub.IDDISTCOSTE) subCCAA on dc.id = subCCAA.IDDISTCOSTE
						LEFT OUTER JOIN (
							select br.IDDISTCOSTE, sum(br.IMPORTE) as Importe from  O02AGPE0.TB_BONIFICACION_RECARGO_2015 br
							INNER JOIN O02AGPE0.TB_SC_C_BONIF_RECARG sc ON br.CODIGO = sc.COD_BON_REC and sc.tip_bon_rec = ''B''
							group by br.IDDISTCOSTE) BONI ON dc.id = BONI.IDDISTCOSTE
						LEFT OUTER JOIN (
							select br.IDDISTCOSTE, sum(br.IMPORTE) as Importe from  O02AGPE0.TB_BONIFICACION_RECARGO_2015 br
							INNER JOIN O02AGPE0.TB_SC_C_BONIF_RECARG sc ON br.CODIGO = sc.COD_BON_REC and sc.tip_bon_rec = ''R''
							group by br.IDDISTCOSTE) RECA ON dc.id = RECA.IDDISTCOSTE
						INNER JOIN o02agpe0.tb_polizas pol ON dc.idpoliza = pol.idpoliza
						INNER join o02agpe0.tb_lineas lin on lin.codplan BETWEEN ' || v_anio_inicial || ' and ' || v_plan_actual || ' and lin.lineaseguroid = pol.lineaseguroid');
    commit;
  
  EXCEPTION
    WHEN OTHERS THEN
      ROLLBACK;
      -- Se vuelve a lanzar la excepcion para que cancele la cadena
      RAISE;
  end act_tb_inf_dist_costes_2015;

  procedure act_tb_inf_explotaciones(v_anios_actualizar IN NUMBER,
                                     v_plan_actual      IN NUMBER) as
  
    v_insert       VARCHAR2(20000) := null;
    v_anio_inicial NUMBER(4) := v_plan_actual - v_anios_actualizar;
  
  begin

  PQ_Utl.LOG (lc, 'procedure act_tb_inf_explotaciones', 2);
  PQ_Utl.LOG (lc, 'anhos a actualizar = ' || v_anios_actualizar, 2);
  PQ_Utl.LOG (lc, 'anho actual = ' || v_plan_actual, 2);
  PQ_Utl.LOG (lc, 'anho inical (anho actual - anhos a actualizar)= ' || v_anio_inicial, 2);

  v_insert := ' INSERT INTO O02AGPE1.TB_INF_EXPLOTACIONES_BAK
      SELECT exp.IDPOLIZA, exp.NUMERO, exp.CODPROVINCIA, exp.CODCOMARCA, exp.CODTERMINO,
      exp.SUBTERMINO, exp.LATITUD, exp.LONGITUD, exp.REGA, exp.SIGLA,
      exp.SUBEXPLOTACION, exp.ESPECIE, exp.REGIMEN, gr.CODGRUPORAZA, gr.CODTIPOCAPITAL,
      gr.CODTIPOANIMAL, gr.NUMANIMALES, pre.PRECIO, dce.COSTETOMADOR, dce.TASACOMERCIAL,
      ( Select (dv.VALOR || '' - '' ||  are.DESCRIPCION)ADAP_RIESGO_EXPLO
        from O02AGPE0.TB_DATOS_VAR_EXPLOTACION dv LEFT OUTER JOIN O02AGPE0.VW_ADAPTACION_RIESGO_EXPLOT are
        on dv.CODCONCEPTO= are.CODCPTO and dv.VALOR = TO_CHAR(are.VALOR_CPTO)
        where dv.IDGRUPORAZA = gr.ID and dv.CODCONCEPTO=1074 and rownum=1)ADAP_RIESGO_EXPLO,
      ( Select  (dv.VALOR || '' - '' || arg.DESCRIPCION)  ADAP_RIESGO_GRAZA
        from O02AGPE0.TB_DATOS_VAR_EXPLOTACION dv inner JOIN  O02AGPE0.VW_ADAPTACION_RIESGO_GRP_RAZA arg
        on dv.CODCONCEPTO = arg.CODCPTO and dv.VALOR = TO_CHAR(arg.VALOR_CPTO)
        where dv.IDGRUPORAZA = gr.ID and dv.CODCONCEPTO=1124 and rownum=1)ADAP_RIESGO_GRAZA,
      ( Select (dv.VALOR || '' - '' || art.DESCRIPCION) ADAP_RIESGO_TIPO_CAP
        from O02AGPE0.TB_DATOS_VAR_EXPLOTACION dv inner JOIN O02AGPE0.VW_ADAPTACION_RIESGO_TIPO_CAP art
        on dv.CODCONCEPTO = art.CODCPTO and dv.VALOR = TO_CHAR(art.VALOR_CPTO)
        where dv.IDGRUPORAZA = gr.ID and dv.CODCONCEPTO=1125 and rownum=1)ADAP_RIESGO_TIPO_CAP,
      ( Select (dv.VALOR || '' - '' || bg.DESCRIPCION) ALOJAMIENTO
        from O02AGPE0.TB_DATOS_VAR_EXPLOTACION dv inner JOIN O02AGPE0.TB_SC_C_DATOS_BUZON_GENERAL bg on
        dv.CODCONCEPTO = bg.CODCPTO and dv.VALOR = TO_CHAR(bg.VALOR_CPTO)
        where dv.IDGRUPORAZA = gr.ID and dv.CODCONCEPTO=1053 and rownum=1)ALOJAMIENTO,
      ( Select (dv.VALOR || '' - '' || bg.DESCRIPCION) AUTORIZACION_ESPECIAL
        from O02AGPE0.TB_DATOS_VAR_EXPLOTACION dv inner JOIN O02AGPE0.TB_SC_C_DATOS_BUZON_GENERAL bg on
        dv.CODCONCEPTO = bg.CODCPTO and dv.VALOR = TO_CHAR(bg.VALOR_CPTO)
        where dv.IDGRUPORAZA = gr.ID and dv.CODCONCEPTO=1064 and rownum=1)AUTORIZACION_ESPECIAL,
      ( Select (dv.VALOR || '' - '' || bg.DESCRIPCION) CALIDAD_PRODUCCION
        from O02AGPE0.TB_DATOS_VAR_EXPLOTACION dv inner JOIN O02AGPE0.TB_SC_C_DATOS_BUZON_GENERAL bg on
        dv.CODCONCEPTO = bg.CODCPTO and dv.VALOR = TO_CHAR(bg.VALOR_CPTO)
        where dv.IDGRUPORAZA = gr.ID and dv.CODCONCEPTO=1047 and rownum=1)CALIDAD_PRODUCCION,
      ( Select (dv.VALOR || '' - '' || bg.DESCRIPCION) CALIF_SANEAMIENTO
        from O02AGPE0.TB_DATOS_VAR_EXPLOTACION dv inner JOIN O02AGPE0.TB_SC_C_DATOS_BUZON_GENERAL bg on
        dv.CODCONCEPTO = bg.CODCPTO and dv.VALOR = TO_CHAR(bg.VALOR_CPTO)
        where dv.IDGRUPORAZA = gr.ID and dv.CODCONCEPTO=1061 and rownum=1)CALIF_SANEAMIENTO,
      ( Select (dv.VALOR || '' - '' || bg.DESCRIPCION) CALIF_SANITARIA
        from O02AGPE0.TB_DATOS_VAR_EXPLOTACION dv inner JOIN O02AGPE0.TB_SC_C_DATOS_BUZON_GENERAL bg on
        dv.CODCONCEPTO = bg.CODCPTO and dv.VALOR = TO_CHAR(bg.VALOR_CPTO)
        where dv.IDGRUPORAZA = gr.ID and dv.CODCONCEPTO=1062 and rownum=1)CALIF_SANITARIA,
      ( Select (dv.VALOR || '' - '' || bg.DESCRIPCION) CONDICIONES_PART
        from O02AGPE0.TB_DATOS_VAR_EXPLOTACION dv inner JOIN O02AGPE0.TB_SC_C_DATOS_BUZON_GENERAL bg on
        dv.CODCONCEPTO = bg.CODCPTO and dv.VALOR = TO_CHAR(bg.VALOR_CPTO)
        where dv.IDGRUPORAZA = gr.ID and dv.CODCONCEPTO=1050 and rownum=1)CONDICIONES_PART,
      ( Select (dv.VALOR || '' - '' || bg.DESCRIPCION) CONTROL_OFICIAL_LECHERO
        from O02AGPE0.TB_DATOS_VAR_EXPLOTACION dv inner JOIN O02AGPE0.TB_SC_C_DATOS_BUZON_GENERAL bg on
        dv.CODCONCEPTO = bg.CODCPTO and dv.VALOR = TO_CHAR(bg.VALOR_CPTO)
        where dv.IDGRUPORAZA = gr.ID and dv.CODCONCEPTO=1045 and rownum=1)CONTROL_OFICIAL_LECHERO,
      ( Select (dv.VALOR || '' - '' || bg.DESCRIPCION) CUENCA_HIDROGRAFICA
        from O02AGPE0.TB_DATOS_VAR_EXPLOTACION dv inner JOIN O02AGPE0.TB_SC_C_DATOS_BUZON_GENERAL bg on
        dv.CODCONCEPTO = bg.CODCPTO and dv.VALOR = TO_CHAR(bg.VALOR_CPTO)
        where dv.IDGRUPORAZA = gr.ID and dv.CODCONCEPTO=1073 and rownum=1)CUENCA_HIDROGRAFICA,
      ( Select (dv.VALOR || '' - '' || bg.DESCRIPCION) DUR_PERIODO_PRODUCTIVO
        from O02AGPE0.TB_DATOS_VAR_EXPLOTACION dv inner JOIN O02AGPE0.TB_SC_C_DATOS_BUZON_GENERAL bg on
        dv.CODCONCEPTO = bg.CODCPTO and dv.VALOR = TO_CHAR(bg.VALOR_CPTO)
        where dv.IDGRUPORAZA = gr.ID and dv.CODCONCEPTO=1066 and rownum=1)DUR_PERIODO_PRODUCTIVO,
      ( Select (dv.VALOR || '' - '' || bg.DESCRIPCION) EXCEPCION_CONTRATA_EXPLOTACION
        from O02AGPE0.TB_DATOS_VAR_EXPLOTACION dv inner JOIN O02AGPE0.TB_SC_C_DATOS_BUZON_GENERAL bg on
        dv.CODCONCEPTO = bg.CODCPTO and dv.VALOR = TO_CHAR(bg.VALOR_CPTO)
        where dv.IDGRUPORAZA = gr.ID and dv.CODCONCEPTO=1063 and rownum=1)EXCEPCION_CONTRATA_EXPLOTACION,
      ( Select (dv.VALOR || '' - '' || bg.DESCRIPCION) EXCEPCION_CONTRATA_POLIZA
        from O02AGPE0.TB_DATOS_VAR_EXPLOTACION dv inner JOIN O02AGPE0.TB_SC_C_DATOS_BUZON_GENERAL bg on
        dv.CODCONCEPTO = bg.CODCPTO and dv.VALOR = TO_CHAR(bg.VALOR_CPTO)
        where dv.IDGRUPORAZA = gr.ID and dv.CODCONCEPTO=1111 and rownum=1)EXCEPCION_CONTRATA_POLIZA,
      ( Select (dv.VALOR || '' - '' || bg.DESCRIPCION) GESTORA
        from O02AGPE0.TB_DATOS_VAR_EXPLOTACION dv inner JOIN O02AGPE0.TB_SC_C_DATOS_BUZON_GENERAL bg on
        dv.CODCONCEPTO = bg.CODCPTO and dv.VALOR = TO_CHAR(bg.VALOR_CPTO)
        where dv.IDGRUPORAZA = gr.ID and dv.CODCONCEPTO=1049 and rownum=1)GESTORA,
      ( Select (dv.VALOR || '' - '' || bg.DESCRIPCION) IGP_GANADO
        from O02AGPE0.TB_DATOS_VAR_EXPLOTACION dv inner JOIN O02AGPE0.TB_SC_C_DATOS_BUZON_GENERAL bg on
        dv.CODCONCEPTO = bg.CODCPTO and dv.VALOR = TO_CHAR(bg.VALOR_CPTO)
        where dv.IDGRUPORAZA = gr.ID and dv.CODCONCEPTO=1051 and rownum=1)IGP_GANADO,
      ( Select (dv.VALOR || '' - '' || bg.DESCRIPCION) PUREZA
        from O02AGPE0.TB_DATOS_VAR_EXPLOTACION dv inner JOIN O02AGPE0.TB_SC_C_DATOS_BUZON_GENERAL bg on
        dv.CODCONCEPTO = bg.CODCPTO and dv.VALOR = TO_CHAR(bg.VALOR_CPTO)
        where dv.IDGRUPORAZA = gr.ID and dv.CODCONCEPTO=1046 and rownum=1)PUREZA,
      ( Select (dv.VALOR || '' - '' || bg.DESCRIPCION) SIST_ALMACENAMIENTO
        from O02AGPE0.TB_DATOS_VAR_EXPLOTACION dv inner JOIN O02AGPE0.TB_SC_C_DATOS_BUZON_GENERAL bg on
        dv.CODCONCEPTO = bg.CODCPTO and dv.VALOR = TO_CHAR(bg.VALOR_CPTO)
        where dv.IDGRUPORAZA = gr.ID and dv.CODCONCEPTO=1048 and rownum=1)SIST_ALMACENAMIENTO,
      ( select (cp.CODVALOR || '' - '' || bg.DESCRIPCION)TIPO_ASEG_GAN
        from O02AGPE0.TB_COMPARATIVAS_POLIZA cp inner JOIN O02AGPE0.TB_SC_C_DATOS_BUZON_GENERAL bg on
        TO_CHAR(cp.CODCONCEPTO) = TO_CHAR(bg.CODCPTO) and cp.CODVALOR = bg.VALOR_CPTO
        where cp.IDPOLIZA = exp.IDPOLIZA and cp.CODCONCEPTO=1079 and rownum=1)TIPO_ASEG_GAN,
      ( Select (dv.VALOR || '' - '' || bg.DESCRIPCION) TIPO_GANADERIA
        from O02AGPE0.TB_DATOS_VAR_EXPLOTACION dv inner JOIN O02AGPE0.TB_SC_C_DATOS_BUZON_GENERAL bg on
        dv.CODCONCEPTO = bg.CODCPTO and dv.VALOR = TO_CHAR(bg.VALOR_CPTO)
        where dv.IDGRUPORAZA = gr.ID and dv.CODCONCEPTO=1052 and rownum=1)TIPO_GANADERIA,
      ( Select (dv.VALOR || '' - '' || bg.DESCRIPCION) SIST_PRODUCCION_GAN
        from O02AGPE0.TB_DATOS_VAR_EXPLOTACION dv inner JOIN O02AGPE0.TB_SC_C_DATOS_BUZON_GENERAL bg on
        dv.CODCONCEPTO = bg.CODCPTO and dv.VALOR = TO_CHAR(bg.VALOR_CPTO)
        where dv.IDGRUPORAZA = gr.ID and dv.CODCONCEPTO=616 and rownum=1)SIST_PRODUCCION_GAN,
     ( Select (dv.VALOR || '' - '' || bg.DESCRIPCION) DESTINO_GAN
        from O02AGPE0.TB_DATOS_VAR_EXPLOTACION dv inner JOIN O02AGPE0.TB_SC_C_DATOS_BUZON_GENERAL bg on
        dv.CODCONCEPTO = bg.CODCPTO and dv.VALOR = TO_CHAR(bg.VALOR_CPTO)
        where dv.IDGRUPORAZA = gr.ID and dv.CODCONCEPTO=110 and rownum=1)DESTINO_GAN,
     ( Select dv.VALOR from O02AGPE0.TB_DATOS_VAR_EXPLOTACION dv
        where dv.IDGRUPORAZA = gr.ID and dv.CODCONCEPTO=1071 and rownum=1)BIOMASA_MEDIA,
     ( Select dv.VALOR from O02AGPE0.TB_DATOS_VAR_EXPLOTACION dv
        where dv.IDGRUPORAZA = gr.ID and dv.CODCONCEPTO=1065 and rownum=1)CAPACIDAD_PRODUCTIVA,
     ( Select dv.VALOR from O02AGPE0.TB_DATOS_VAR_EXPLOTACION dv
        where dv.IDGRUPORAZA = gr.ID and dv.CODCONCEPTO=1076 and rownum=1)NUM_COLUMAS,
     ( Select dv.VALOR from O02AGPE0.TB_DATOS_VAR_EXPLOTACION dv
        where dv.IDGRUPORAZA = gr.ID and dv.CODCONCEPTO=1072 and rownum=1)NUM_MEDIO_ANIMALES,
     ( Select dv.VALOR from O02AGPE0.TB_DATOS_VAR_EXPLOTACION dv
        where dv.IDGRUPORAZA = gr.ID and dv.CODCONCEPTO=1095 and rownum=1)PRECIO_RETIRADA_PAC,
	 ((SELECT SUM(br.importe) FROM O02AGPE0.TB_DIST_COSTE_EXPL_BR br
		INNER JOIN o02agpe0.TB_SC_C_BONIF_RECARG brc on brc.tip_bon_rec = ''B'' AND br.codigo = brc.cod_bon_rec
	    WHERE br.id_dist_coste_explotaciones = dce.id
		GROUP BY br.id_dist_coste_explotaciones) - (SELECT SUM(br.importe) FROM O02AGPE0.TB_DIST_COSTE_EXPL_BR br
		INNER JOIN o02agpe0.TB_SC_C_BONIF_RECARG brc on brc.tip_bon_rec = ''R'' AND br.codigo = brc.cod_bon_rec
	    WHERE br.id_dist_coste_explotaciones = dce.id
		GROUP BY br.id_dist_coste_explotaciones)) as SUM_BONIF_REC,
	 (SELECT SUM(sdce.importe) FROM O02AGPE0.TB_DIST_COSTE_EXPL_SUBV sdce WHERE sdce.id_dist_coste_explotaciones = dce.id AND sdce.origen_subv = ''E'' GROUP BY sdce.id_dist_coste_explotaciones) as SUM_SUBV_ENESA,
	 (SELECT SUM(sdce.importe) FROM O02AGPE0.TB_DIST_COSTE_EXPL_SUBV sdce WHERE sdce.id_dist_coste_explotaciones = dce.id AND sdce.origen_subv = ''C'' GROUP BY sdce.id_dist_coste_explotaciones) as SUM_SUBV_CCAA,
	 (SELECT SUM(sdce.importe) FROM O02AGPE0.TB_DIST_COSTE_EXPL_SUBV sdce WHERE sdce.id_dist_coste_explotaciones = dce.id AND sdce.origen_subv = ''D'' GROUP BY sdce.id_dist_coste_explotaciones) as SUM_SUBV_DESG
      FROM O02AGPE0.TB_EXPLOTACIONES exp
        inner join O02AGPE0.TB_POLIZAS pol ON exp.IDPOLIZA=pol.IDPOLIZA
        left outer join O02AGPE0.TB_GRUPO_RAZA_EXPLOTACION gr ON exp.ID = gr.IDEXPLOTACION
        left outer join O02AGPE0.TB_PRECIOS_ANIMALES_MODULOS pre ON gr.ID =pre.IDGRUPORAZA
        left outer join O02AGPE0.TB_DISTRIBUCION_COSTES_2015 dc ON pol.IDPOLIZA=dc.IDPOLIZA
        left outer join O02AGPE0.TB_DIST_COSTE_EXPLOTACIONES dce ON dce.IDDISTCOSTE = dc.ID and
          exp.NUMERO = dce.NUMEXPLOTACION and dce.GRUPORAZA =gr.CODGRUPORAZA and
          dce.TIPOCAPITAL=gr.CODTIPOCAPITAL and dce.TIPOANIMAL=gr.CODTIPOANIMAL
    	inner join o02agpe0.tb_lineas lin on lin.codplan BETWEEN ' ||
                v_anio_inicial || ' and ' || v_plan_actual ||
                ' and lin.lineaseguroid = pol.lineaseguroid';
  
    execute immediate (v_insert);
    commit;
  EXCEPTION
    WHEN OTHERS THEN
      ROLLBACK;
      PQ_Utl.LOG(lc,
                 'Error al actualizar la tabla TB_INF_EXPLOTACIONES_BAK: ' ||
                 SQLCODE || ' - ' || SQLERRM || '. ',
                 2);
      RAISE;
  end act_tb_inf_explotaciones;

  procedure act_tb_inf_explotaciones_anexo(v_anios_actualizar IN NUMBER,
                                           v_plan_actual      IN NUMBER) as
  
    v_insertAnex   VARCHAR2(20000) := null;
    v_anio_inicial NUMBER(4) := v_plan_actual - v_anios_actualizar;
  
  begin
  
    PQ_Utl.LOG(lc, 'procedure act_tb_inf_explotaciones_anexo', 2);
    PQ_Utl.LOG(lc, 'anhos a actualizar = ' || v_anios_actualizar, 2);
    PQ_Utl.LOG(lc, 'anho actual = ' || v_plan_actual, 2);
    PQ_Utl.LOG(lc,
               'anho inical (anho actual - anhos a actualizar)= ' ||
               v_anio_inicial,
               2);
  
    v_insertAnex := 'INSERT INTO O02AGPE1.TB_INF_EXPLOTACIONES_ANEXO_BAK
           SELECT ax.IDPOLIZA, exp.NUMERO, exp.CODPROVINCIA, exp.CODCOMARCA, exp.CODTERMINO,
            exp.SUBTERMINO, exp.LATITUD, exp.LONGITUD, exp.REGA, exp.SIGLA,
            exp.SUBEXPLOTACION, exp.ESPECIE, exp.REGIMEN, gr.CODGRUPORAZA, gr.CODTIPOCAPITAL,
            gr.CODTIPOANIMAL, gr.NUMANIMALES, pre.PRECIO,
            ( Select (dv.VALOR || '' - '' ||  are.DESCRIPCION)ADAP_RIESGO_EXPLO
              from O02AGPE0.TB_DATOS_VAR_EXPLOT_ANX dv LEFT OUTER JOIN O02AGPE0.VW_ADAPTACION_RIESGO_EXPLOT are
              on dv.CODCONCEPTO= are.CODCPTO and dv.VALOR = TO_CHAR(are.VALOR_CPTO)
              where dv.ID_GRUPORAZA_ANX = gr.ID and dv.CODCONCEPTO=1074 and rownum=1)ADAP_RIESGO_EXPLO,
            ( Select  (dv.VALOR || '' - '' || arg.DESCRIPCION)  ADAP_RIESGO_GRAZA
              from O02AGPE0.TB_DATOS_VAR_EXPLOT_ANX dv inner JOIN  O02AGPE0.VW_ADAPTACION_RIESGO_GRP_RAZA arg
              on dv.CODCONCEPTO = arg.CODCPTO and dv.VALOR = TO_CHAR(arg.VALOR_CPTO)
              where dv.ID_GRUPORAZA_ANX = gr.ID and dv.CODCONCEPTO=1124 and rownum=1)ADAP_RIESGO_GRAZA,
            ( Select (dv.VALOR || '' - '' || art.DESCRIPCION) ADAP_RIESGO_TIPO_CAP
              from O02AGPE0.TB_DATOS_VAR_EXPLOT_ANX dv inner JOIN O02AGPE0.VW_ADAPTACION_RIESGO_TIPO_CAP art
              on dv.CODCONCEPTO = art.CODCPTO and dv.VALOR = TO_CHAR(art.VALOR_CPTO)
              where dv.ID_GRUPORAZA_ANX = gr.ID and dv.CODCONCEPTO=1125 and rownum=1)ADAP_RIESGO_TIPO_CAP,
            ( Select (dv.VALOR || '' - '' || bg.DESCRIPCION) ALOJAMIENTO
              from O02AGPE0.TB_DATOS_VAR_EXPLOT_ANX dv inner JOIN O02AGPE0.TB_SC_C_DATOS_BUZON_GENERAL bg on
              dv.CODCONCEPTO = bg.CODCPTO and dv.VALOR = TO_CHAR(bg.VALOR_CPTO)
              where dv.ID_GRUPORAZA_ANX = gr.ID and dv.CODCONCEPTO=1053 and rownum=1)ALOJAMIENTO,
            ( Select (dv.VALOR || '' - '' || bg.DESCRIPCION) AUTORIZACION_ESPECIAL
              from O02AGPE0.TB_DATOS_VAR_EXPLOT_ANX dv inner JOIN O02AGPE0.TB_SC_C_DATOS_BUZON_GENERAL bg on
              dv.CODCONCEPTO = bg.CODCPTO and dv.VALOR = TO_CHAR(bg.VALOR_CPTO)
              where dv.ID_GRUPORAZA_ANX = gr.ID and dv.CODCONCEPTO=1064 and rownum=1)AUTORIZACION_ESPECIAL,
            ( Select (dv.VALOR || '' - '' || bg.DESCRIPCION) CALIDAD_PRODUCCION
              from O02AGPE0.TB_DATOS_VAR_EXPLOT_ANX dv inner JOIN O02AGPE0.TB_SC_C_DATOS_BUZON_GENERAL bg on
              dv.CODCONCEPTO = bg.CODCPTO and dv.VALOR = TO_CHAR(bg.VALOR_CPTO)
              where dv.ID_GRUPORAZA_ANX = gr.ID and dv.CODCONCEPTO=1047 and rownum=1)CALIDAD_PRODUCCION,
            ( Select (dv.VALOR || '' - '' || bg.DESCRIPCION) CALIF_SANEAMIENTO
              from O02AGPE0.TB_DATOS_VAR_EXPLOT_ANX dv inner JOIN O02AGPE0.TB_SC_C_DATOS_BUZON_GENERAL bg on
              dv.CODCONCEPTO = bg.CODCPTO and dv.VALOR = TO_CHAR(bg.VALOR_CPTO)
              where dv.ID_GRUPORAZA_ANX = gr.ID and dv.CODCONCEPTO=1061 and rownum=1)CALIF_SANEAMIENTO,
            ( Select (dv.VALOR || '' - '' || bg.DESCRIPCION) CALIF_SANITARIA
              from O02AGPE0.TB_DATOS_VAR_EXPLOT_ANX dv inner JOIN O02AGPE0.TB_SC_C_DATOS_BUZON_GENERAL bg on
              dv.CODCONCEPTO = bg.CODCPTO and dv.VALOR = TO_CHAR(bg.VALOR_CPTO)
              where dv.ID_GRUPORAZA_ANX = gr.ID and dv.CODCONCEPTO=1062 and rownum=1)CALIF_SANITARIA,
            ( Select (dv.VALOR || '' - '' || bg.DESCRIPCION) CONDICIONES_PART
              from O02AGPE0.TB_DATOS_VAR_EXPLOT_ANX dv inner JOIN O02AGPE0.TB_SC_C_DATOS_BUZON_GENERAL bg on
              dv.CODCONCEPTO = bg.CODCPTO and dv.VALOR = TO_CHAR(bg.VALOR_CPTO)
              where dv.ID_GRUPORAZA_ANX = gr.ID and dv.CODCONCEPTO=1050 and rownum=1)CONDICIONES_PART,
            ( Select (dv.VALOR || '' - '' || bg.DESCRIPCION) CONTROL_OFICIAL_LECHERO
              from O02AGPE0.TB_DATOS_VAR_EXPLOT_ANX dv inner JOIN O02AGPE0.TB_SC_C_DATOS_BUZON_GENERAL bg on
              dv.CODCONCEPTO = bg.CODCPTO and dv.VALOR = TO_CHAR(bg.VALOR_CPTO)
              where dv.ID_GRUPORAZA_ANX = gr.ID and dv.CODCONCEPTO=1045 and rownum=1)CONTROL_OFICIAL_LECHERO,
            ( Select (dv.VALOR || '' - '' || bg.DESCRIPCION) CUENCA_HIDROGRAFICA
              from O02AGPE0.TB_DATOS_VAR_EXPLOT_ANX dv inner JOIN O02AGPE0.TB_SC_C_DATOS_BUZON_GENERAL bg on
              dv.CODCONCEPTO = bg.CODCPTO and dv.VALOR = TO_CHAR(bg.VALOR_CPTO)
              where dv.ID_GRUPORAZA_ANX = gr.ID and dv.CODCONCEPTO=1073 and rownum=1)CUENCA_HIDROGRAFICA,
            ( Select (dv.VALOR || '' - '' || bg.DESCRIPCION) DUR_PERIODO_PRODUCTIVO
              from O02AGPE0.TB_DATOS_VAR_EXPLOT_ANX dv inner JOIN O02AGPE0.TB_SC_C_DATOS_BUZON_GENERAL bg on
              dv.CODCONCEPTO = bg.CODCPTO and dv.VALOR = TO_CHAR(bg.VALOR_CPTO)
              where dv.ID_GRUPORAZA_ANX = gr.ID and dv.CODCONCEPTO=1066 and rownum=1)DUR_PERIODO_PRODUCTIVO,
            ( Select (dv.VALOR || '' - '' || bg.DESCRIPCION) EXCEPCION_CONTRATA_EXPLOTACION
              from O02AGPE0.TB_DATOS_VAR_EXPLOT_ANX dv inner JOIN O02AGPE0.TB_SC_C_DATOS_BUZON_GENERAL bg on
              dv.CODCONCEPTO = bg.CODCPTO and dv.VALOR = TO_CHAR(bg.VALOR_CPTO)
              where dv.ID_GRUPORAZA_ANX = gr.ID and dv.CODCONCEPTO=1063 and rownum=1)EXCEPCION_CONTRATA_EXPLOTACION,
            ( Select (dv.VALOR || '' - '' || bg.DESCRIPCION) EXCEPCION_CONTRATA_POLIZA
              from O02AGPE0.TB_DATOS_VAR_EXPLOT_ANX dv inner JOIN O02AGPE0.TB_SC_C_DATOS_BUZON_GENERAL bg on
              dv.CODCONCEPTO = bg.CODCPTO and dv.VALOR = TO_CHAR(bg.VALOR_CPTO)
              where dv.ID_GRUPORAZA_ANX = gr.ID and dv.CODCONCEPTO=1111 and rownum=1)EXCEPCION_CONTRATA_POLIZA,
            ( Select (dv.VALOR || '' - '' || bg.DESCRIPCION) GESTORA
              from O02AGPE0.TB_DATOS_VAR_EXPLOT_ANX dv inner JOIN O02AGPE0.TB_SC_C_DATOS_BUZON_GENERAL bg on
              dv.CODCONCEPTO = bg.CODCPTO and dv.VALOR = TO_CHAR(bg.VALOR_CPTO)
              where dv.ID_GRUPORAZA_ANX = gr.ID and dv.CODCONCEPTO=1049 and rownum=1)GESTORA,
            ( Select (dv.VALOR || '' - '' || bg.DESCRIPCION) IGP_GANADO
              from O02AGPE0.TB_DATOS_VAR_EXPLOT_ANX dv inner JOIN O02AGPE0.TB_SC_C_DATOS_BUZON_GENERAL bg on
              dv.CODCONCEPTO = bg.CODCPTO and dv.VALOR = TO_CHAR(bg.VALOR_CPTO)
              where dv.ID_GRUPORAZA_ANX = gr.ID and dv.CODCONCEPTO=1051 and rownum=1)IGP_GANADO,
            ( Select (dv.VALOR || '' - '' || bg.DESCRIPCION) PUREZA
              from O02AGPE0.TB_DATOS_VAR_EXPLOT_ANX dv inner JOIN O02AGPE0.TB_SC_C_DATOS_BUZON_GENERAL bg on
              dv.CODCONCEPTO = bg.CODCPTO and dv.VALOR = TO_CHAR(bg.VALOR_CPTO)
              where dv.ID_GRUPORAZA_ANX = gr.ID and dv.CODCONCEPTO=1046 and rownum=1)PUREZA,
            ( Select (dv.VALOR || '' - '' || bg.DESCRIPCION) SIST_ALMACENAMIENTO
              from O02AGPE0.TB_DATOS_VAR_EXPLOT_ANX dv inner JOIN O02AGPE0.TB_SC_C_DATOS_BUZON_GENERAL bg on
              dv.CODCONCEPTO = bg.CODCPTO and dv.VALOR = TO_CHAR(bg.VALOR_CPTO)
              where dv.ID_GRUPORAZA_ANX = gr.ID and dv.CODCONCEPTO=1048 and rownum=1)SIST_ALMACENAMIENTO,
            ( select (cp.CODVALOR || '' - '' || bg.DESCRIPCION)TIPO_ASEG_GAN
              from O02AGPE0.TB_ANEXO_MOD_COBERTURAS cp inner JOIN O02AGPE0.TB_SC_C_DATOS_BUZON_GENERAL bg on
              TO_CHAR(cp.CODCONCEPTO) = TO_CHAR(bg.CODCPTO) and cp.CODVALOR = bg.VALOR_CPTO
              where cp.IDANEXO = ax.ID and cp.CODCONCEPTO=1079 and rownum=1)TIPO_ASEG_GAN,
            ( Select (dv.VALOR || '' - '' || bg.DESCRIPCION) TIPO_GANADERIA
              from O02AGPE0.TB_DATOS_VAR_EXPLOT_ANX dv inner JOIN O02AGPE0.TB_SC_C_DATOS_BUZON_GENERAL bg on
              dv.CODCONCEPTO = bg.CODCPTO and dv.VALOR = TO_CHAR(bg.VALOR_CPTO)
              where dv.ID_GRUPORAZA_ANX = gr.ID and dv.CODCONCEPTO=1052 and rownum=1)TIPO_GANADERIA,
            ( Select (dv.VALOR || '' - '' || bg.DESCRIPCION) SIST_PRODUCCION_GAN
              from O02AGPE0.TB_DATOS_VAR_EXPLOT_ANX dv inner JOIN O02AGPE0.TB_SC_C_DATOS_BUZON_GENERAL bg on
              dv.CODCONCEPTO = bg.CODCPTO and dv.VALOR = TO_CHAR(bg.VALOR_CPTO)
              where dv.ID_GRUPORAZA_ANX = gr.ID and dv.CODCONCEPTO=616 and rownum=1)SIST_PRODUCCION_GAN,
           ( Select (dv.VALOR || '' - '' || bg.DESCRIPCION) DESTINO_GAN
              from O02AGPE0.TB_DATOS_VAR_EXPLOT_ANX dv inner JOIN O02AGPE0.TB_SC_C_DATOS_BUZON_GENERAL bg on
              dv.CODCONCEPTO = bg.CODCPTO and dv.VALOR = TO_CHAR(bg.VALOR_CPTO)
              where dv.ID_GRUPORAZA_ANX = gr.ID and dv.CODCONCEPTO=110 and rownum=1)DESTINO_GAN,
           ( Select dv.VALOR from O02AGPE0.TB_DATOS_VAR_EXPLOT_ANX dv
              where dv.ID_GRUPORAZA_ANX = gr.ID and dv.CODCONCEPTO=1071 and rownum=1)BIOMASA_MEDIA,
           ( Select dv.VALOR from O02AGPE0.TB_DATOS_VAR_EXPLOT_ANX dv
              where dv.ID_GRUPORAZA_ANX = gr.ID and dv.CODCONCEPTO=1065 and rownum=1)CAPACIDAD_PRODUCTIVA,
           ( Select dv.VALOR from O02AGPE0.TB_DATOS_VAR_EXPLOT_ANX dv
              where dv.ID_GRUPORAZA_ANX = gr.ID and dv.CODCONCEPTO=1076 and rownum=1)NUM_COLUMAS,
           ( Select dv.VALOR from O02AGPE0.TB_DATOS_VAR_EXPLOT_ANX dv
              where dv.ID_GRUPORAZA_ANX = gr.ID and dv.CODCONCEPTO=1072 and rownum=1)NUM_MEDIO_ANIMALES,
           ( Select dv.VALOR from O02AGPE0.TB_DATOS_VAR_EXPLOT_ANX dv
              where dv.ID_GRUPORAZA_ANX = gr.ID and dv.CODCONCEPTO=1095 and rownum=1)PRECIO_RETIRADA_PAC,
            ax.ID IDANEXO
            FROM O02AGPE0.TB_ANEXO_MOD_EXPLOTACIONES exp
              inner join O02AGPE0.TB_ANEXO_MOD ax ON exp.ID_ANEXO = ax.ID
              left outer join O02AGPE0.TB_ANEXO_MOD_GRUPO_RAZA gr ON exp.ID = gr.ID_EXPLOTACION_ANEXO
              left outer join O02AGPE0.TB_PRECIOS_ANIMALES_MOD_ANX pre ON gr.ID =pre.IDGRUPORAZA_ANX
			  inner JOIN o02agpe0.tb_polizas pol ON ax.idpoliza = pol.idpoliza
			  inner join o02agpe0.tb_lineas lin on lin.codplan BETWEEN ' ||
                    v_anio_inicial || ' and ' || v_plan_actual ||
                    ' and lin.lineaseguroid = pol.lineaseguroid';
  
    execute immediate (v_insertAnex);
    commit;
  EXCEPTION
    WHEN OTHERS THEN
      ROLLBACK;
      PQ_Utl.LOG(lc,
                 'Error al actualizar la tabla TB_INF_EXPLOTACIONES_ANEXO_BAK: ' ||
                 SQLCODE || ' - ' || SQLERRM || '. ',
                 2);
      RAISE;
  end act_tb_inf_explotaciones_anexo;
  
  procedure act_tb_inf_parc_dist_coste(v_anios_actualizar IN NUMBER,
                                          v_plan_actual      IN NUMBER) as
    v_anio_inicial NUMBER(4) := v_plan_actual - v_anios_actualizar;
  
  begin
  
    PQ_Utl.LOG(lc, 'procedure act_tb_inf_parc_dist_coste', 2);
    PQ_Utl.LOG(lc, 'anhos a actualizar = ' || v_anios_actualizar, 2);
    PQ_Utl.LOG(lc, 'anho actual = ' || v_plan_actual, 2);
    PQ_Utl.LOG(lc,
               'anho inical (anho actual - anhos a actualizar)= ' ||
               v_anio_inicial,
               2);
    execute immediate ('insert into o02agpe1.TB_INF_PARC_DIST_COSTE_BAK 
select p.idpoliza, dcp.hoja, dcp.numero, dcp.tipo, dcp.precio, dcp.produccion, dcp.capital_asegurado, dcp.tasacom, dcp.tasacombase, dcp.tasacombaseneta, dcp.primacomercial, dcp.primacomercialneta, dcp.recargoconsorcio, dcp.reciboprima, dcp.costetomador, dcp.totalcostetomador, dcp.recargoaval, dcp.recargofraccionamiento
from o02agpe0.tb_dist_coste_parcelas_2015 dcp
inner join o02agpe0.tb_distribucion_costes_2015 dc on dc.id = dcp.iddistcoste
inner join o02agpe0.tb_polizas p on p.idpoliza = dc.idpoliza
inner join o02agpe0.tb_lineas l on l.lineaseguroid = p.lineaseguroid and l.codplan BETWEEN ' ||
                      v_anio_inicial || ' and ' || v_plan_actual);
  
    commit;
  
  EXCEPTION
    WHEN OTHERS THEN
      ROLLBACK;
      -- Se vuelve a lanzar la excepcion para que cancele la cadena
      RAISE;										  
  end act_tb_inf_parc_dist_coste;	

  procedure act_tb_inf_expl_dist_coste(v_anios_actualizar IN NUMBER,
                                          v_plan_actual      IN NUMBER) as
	v_anio_inicial NUMBER(4) := v_plan_actual - v_anios_actualizar;
  
  begin
  
    PQ_Utl.LOG(lc, 'procedure act_tb_inf_expl_dist_coste', 2);
    PQ_Utl.LOG(lc, 'anhos a actualizar = ' || v_anios_actualizar, 2);
    PQ_Utl.LOG(lc, 'anho actual = ' || v_plan_actual, 2);
    PQ_Utl.LOG(lc,
               'anho inical (anho actual - anhos a actualizar)= ' ||
               v_anio_inicial,
               2);
    execute immediate ('insert into o02agpe1.TB_INF_EXPL_DIST_COSTE_BAK select p.idpoliza, dce.numexplotacion, dce.gruporaza, dce.tipocapital, dce.tipoanimal, dce.tasacomercialbase, dce.tasacomercial, dce.gruponegocio, dce.costetomador, dce.primacomercial, dce.primacomercialneta, dce.recargoconsorcio, dce.reciboprima
from o02agpe0.tb_dist_coste_explotaciones dce
inner join o02agpe0.tb_distribucion_costes_2015 dc on dc.id = dce.iddistcoste
inner join o02agpe0.tb_polizas p on p.idpoliza = dc.idpoliza
inner join o02agpe0.tb_lineas l on l.lineaseguroid = p.lineaseguroid and l.codplan BETWEEN ' ||
                      v_anio_inicial || ' and ' || v_plan_actual);
  
    commit;
  
  EXCEPTION
    WHEN OTHERS THEN
      ROLLBACK;
      -- Se vuelve a lanzar la excepcion para que cancele la cadena
      RAISE;									  
  end act_tb_inf_expl_dist_coste;  

  procedure act_tb_inf_parc_dist_coste_gn(v_anios_actualizar IN NUMBER,
                                          v_plan_actual      IN NUMBER) as
  
    v_anio_inicial NUMBER(4) := v_plan_actual - v_anios_actualizar;
  
  begin
  
    PQ_Utl.LOG(lc, 'procedure act_tb_inf_parc_dist_coste_gn', 2);
    PQ_Utl.LOG(lc, 'anhos a actualizar = ' || v_anios_actualizar, 2);
    PQ_Utl.LOG(lc, 'anho actual = ' || v_plan_actual, 2);
    PQ_Utl.LOG(lc,
               'anho inical (anho actual - anhos a actualizar)= ' ||
               v_anio_inicial,
               2);
    execute immediate ('insert into o02agpe1.TB_INF_PARC_DIST_COSTE_GN_BAK 
select p.idpoliza, dcp.hoja, dcp.numero, dceb.grupo_negocio, dceb.prima_comercial, dceb.prima_comercial_neta, dceb.recargo_consorcio, dceb.recibo_prima, dceb.coste_tomador, dcp.tipo
from o02agpe0.TB_DIST_COSTE_PARC_GN dceb
inner join o02agpe0.tb_dist_coste_parcelas_2015 dcp on dcp.id = dceb.id_dist_coste_parcelas
inner join o02agpe0.tb_distribucion_costes_2015 dc on dc.id = dcp.iddistcoste
inner join o02agpe0.tb_polizas p on p.idpoliza = dc.idpoliza
inner join o02agpe0.tb_lineas l on l.lineaseguroid = p.lineaseguroid and l.codplan BETWEEN ' ||
                      v_anio_inicial || ' and ' || v_plan_actual);
  
    commit;
  
  EXCEPTION
    WHEN OTHERS THEN
      ROLLBACK;
      -- Se vuelve a lanzar la excepcion para que cancele la cadena
      RAISE;
    
  end act_tb_inf_parc_dist_coste_gn;

  procedure act_tb_inf_parc_dist_coste_su(v_anios_actualizar IN NUMBER,
                                          v_plan_actual      IN NUMBER) as
  
    v_anio_inicial NUMBER(4) := v_plan_actual - v_anios_actualizar;
  
  begin
  
    PQ_Utl.LOG(lc, 'procedure act_tb_inf_parc_dist_coste_su', 2);
    PQ_Utl.LOG(lc, 'anhos a actualizar = ' || v_anios_actualizar, 2);
    PQ_Utl.LOG(lc, 'anho actual = ' || v_plan_actual, 2);
    PQ_Utl.LOG(lc,
               'anho inical (anho actual - anhos a actualizar)= ' ||
               v_anio_inicial,
               2);
    execute immediate ('insert into o02agpe1.TB_INF_PARC_DIST_COSTE_SU_BAK 
select p.idpoliza, dcp.hoja, dcp.numero, dceb.origen_subv, dceb.codorganismo, dceb.codtiposubv, dceb.importe, dceb.pct_subvencion, dceb.valor_unitario, dcp.tipo
from o02agpe0.TB_DIST_COSTE_PARC_SUBV dceb
inner join o02agpe0.tb_dist_coste_parcelas_2015 dcp on dcp.id = dceb.id_dist_coste_parcelas
inner join o02agpe0.tb_distribucion_costes_2015 dc on dc.id = dcp.iddistcoste
inner join o02agpe0.tb_polizas p on p.idpoliza = dc.idpoliza
inner join o02agpe0.tb_lineas l on l.lineaseguroid = p.lineaseguroid and l.codplan BETWEEN ' ||
                      v_anio_inicial || ' and ' || v_plan_actual);
  
    commit;
  
  EXCEPTION
    WHEN OTHERS THEN
      ROLLBACK;
      -- Se vuelve a lanzar la excepcion para que cancele la cadena
      RAISE;
    
  end act_tb_inf_parc_dist_coste_su;

  procedure act_tb_inf_parc_dist_coste_br(v_anios_actualizar IN NUMBER,
                                          v_plan_actual      IN NUMBER) as
  
    v_anio_inicial NUMBER(4) := v_plan_actual - v_anios_actualizar;
  
  begin
  
    PQ_Utl.LOG(lc, 'procedure act_tb_inf_parc_dist_coste_br', 2);
    PQ_Utl.LOG(lc, 'anhos a actualizar = ' || v_anios_actualizar, 2);
    PQ_Utl.LOG(lc, 'anho actual = ' || v_plan_actual, 2);
    PQ_Utl.LOG(lc,
               'anho inical (anho actual - anhos a actualizar)= ' ||
               v_anio_inicial,
               2);
    execute immediate ('insert into o02agpe1.TB_INF_PARC_DIST_COSTE_BR_BAK 
select p.idpoliza, dcp.hoja, dcp.numero, dceb.codigo, dceb.importe, dcp.tipo
from o02agpe0.TB_DIST_COSTE_PARC_BR dceb
inner join o02agpe0.tb_dist_coste_parcelas_2015 dcp on dcp.id = dceb.id_dist_coste_parcelas
inner join o02agpe0.tb_distribucion_costes_2015 dc on dc.id = dcp.iddistcoste
inner join o02agpe0.tb_polizas p on p.idpoliza = dc.idpoliza
inner join o02agpe0.tb_lineas l on l.lineaseguroid = p.lineaseguroid and l.codplan BETWEEN ' ||
                      v_anio_inicial || ' and ' || v_plan_actual);
  
    commit;
  
  EXCEPTION
    WHEN OTHERS THEN
      ROLLBACK;
      -- Se vuelve a lanzar la excepcion para que cancele la cadena
      RAISE;
    
  end act_tb_inf_parc_dist_coste_br;

  procedure act_tb_inf_expl_dist_coste_gn(v_anios_actualizar IN NUMBER,
                                          v_plan_actual      IN NUMBER) as
  
    v_anio_inicial NUMBER(4) := v_plan_actual - v_anios_actualizar;
  
  begin
  
    PQ_Utl.LOG(lc, 'procedure act_tb_inf_expl_dist_coste_gn', 2);
    PQ_Utl.LOG(lc, 'anhos a actualizar = ' || v_anios_actualizar, 2);
    PQ_Utl.LOG(lc, 'anho actual = ' || v_plan_actual, 2);
    PQ_Utl.LOG(lc,
               'anho inical (anho actual - anhos a actualizar)= ' ||
               v_anio_inicial,
               2);
    execute immediate ('insert into o02agpe1.TB_INF_EXPL_DIST_COSTE_GN_BAK 
select p.idpoliza, dce.numexplotacion, dceb.grupo_negocio, dceb.prima_comercial, dceb.prima_comercial_neta, dceb.recargo_consorcio, dceb.recibo_prima, dceb.coste_tomador, e.especie, e.regimen, dce.gruporaza, dce.tipocapital, dce.tipoanimal
from o02agpe0.TB_DIST_COSTE_EXPL_GN dceb
inner join o02agpe0.tb_dist_coste_explotaciones dce on dce.id = dceb.id_dist_coste_explotaciones
inner join o02agpe0.tb_distribucion_costes_2015 dc on dc.id = dce.iddistcoste
inner join o02agpe0.tb_polizas p on p.idpoliza = dc.idpoliza
inner join o02agpe0.tb_explotaciones e on e.idpoliza = p.idpoliza and e.numero = dce.numexplotacion
inner join o02agpe0.tb_lineas l on l.lineaseguroid = p.lineaseguroid and l.codplan BETWEEN ' ||
                      v_anio_inicial || ' and ' || v_plan_actual);
  
    commit;
  
  EXCEPTION
    WHEN OTHERS THEN
      ROLLBACK;
      -- Se vuelve a lanzar la excepcion para que cancele la cadena
      RAISE;
    
  end act_tb_inf_expl_dist_coste_gn;

  procedure act_tb_inf_expl_dist_coste_su(v_anios_actualizar IN NUMBER,
                                          v_plan_actual      IN NUMBER) as
  
    v_anio_inicial NUMBER(4) := v_plan_actual - v_anios_actualizar;
  
  begin
  
    PQ_Utl.LOG(lc, 'procedure act_tb_inf_expl_dist_coste_su', 2);
    PQ_Utl.LOG(lc, 'anhos a actualizar = ' || v_anios_actualizar, 2);
    PQ_Utl.LOG(lc, 'anho actual = ' || v_plan_actual, 2);
    PQ_Utl.LOG(lc,
               'anho inical (anho actual - anhos a actualizar)= ' ||
               v_anio_inicial,
               2);
    execute immediate ('insert into o02agpe1.TB_INF_EXPL_DIST_COSTE_SU_BAK 
select p.idpoliza, dce.numexplotacion, dceb.origen_subv, dceb.codorganismo, dceb.codtiposubv, dceb.importe, dceb.pct_subvencion, dceb.valor_unitario, e.especie, e.regimen, dce.gruporaza, dce.tipocapital, dce.tipoanimal
from o02agpe0.TB_DIST_COSTE_EXPL_SUBV dceb
inner join o02agpe0.tb_dist_coste_explotaciones dce on dce.id = dceb.id_dist_coste_explotaciones
inner join o02agpe0.tb_distribucion_costes_2015 dc on dc.id = dce.iddistcoste
inner join o02agpe0.tb_polizas p on p.idpoliza = dc.idpoliza
inner join o02agpe0.tb_explotaciones e on e.idpoliza = p.idpoliza and e.numero = dce.numexplotacion
inner join o02agpe0.tb_lineas l on l.lineaseguroid = p.lineaseguroid and l.codplan BETWEEN ' ||
                      v_anio_inicial || ' and ' || v_plan_actual);
  
    commit;
  
  EXCEPTION
    WHEN OTHERS THEN
      ROLLBACK;
      -- Se vuelve a lanzar la excepcion para que cancele la cadena
      RAISE;
    
  end act_tb_inf_expl_dist_coste_su;

  procedure act_tb_inf_expl_dist_coste_br(v_anios_actualizar IN NUMBER,
                                          v_plan_actual      IN NUMBER) as
  
    v_anio_inicial NUMBER(4) := v_plan_actual - v_anios_actualizar;
  
  begin
  
    PQ_Utl.LOG(lc, 'procedure act_tb_inf_expl_dist_coste_br', 2);
    PQ_Utl.LOG(lc, 'anhos a actualizar = ' || v_anios_actualizar, 2);
    PQ_Utl.LOG(lc, 'anho actual = ' || v_plan_actual, 2);
    PQ_Utl.LOG(lc,
               'anho inical (anho actual - anhos a actualizar)= ' ||
               v_anio_inicial,
               2);
    execute immediate ('insert into o02agpe1.TB_INF_EXPL_DIST_COSTE_BR_BAK 
select p.idpoliza, dce.numexplotacion, dceb.codigo, dceb.importe, e.especie, e.regimen, dce.gruporaza, dce.tipocapital, dce.tipoanimal
from o02agpe0.TB_DIST_COSTE_EXPL_BR dceb
inner join o02agpe0.tb_dist_coste_explotaciones dce on dce.id = dceb.id_dist_coste_explotaciones
inner join o02agpe0.tb_distribucion_costes_2015 dc on dc.id = dce.iddistcoste
inner join o02agpe0.tb_polizas p on p.idpoliza = dc.idpoliza
inner join o02agpe0.tb_explotaciones e on e.idpoliza = p.idpoliza and e.numero = dce.numexplotacion
inner join o02agpe0.tb_lineas l on l.lineaseguroid = p.lineaseguroid and l.codplan BETWEEN ' ||
                      v_anio_inicial || ' and ' || v_plan_actual);
  
    commit;
  
  EXCEPTION
    WHEN OTHERS THEN
      ROLLBACK;
      -- Se vuelve a lanzar la excepcion para que cancele la cadena
      RAISE;
    
  end act_tb_inf_expl_dist_coste_br;
  
end PQ_CARGA_DATOS_INFORMES;  
/
SHOW ERRORS;