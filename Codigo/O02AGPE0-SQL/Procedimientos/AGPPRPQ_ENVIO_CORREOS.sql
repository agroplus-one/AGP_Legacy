SET DEFINE OFF;
SET SERVEROUTPUT ON;

CREATE OR REPLACE PACKAGE O02AGPE0.PQ_ENVIO_CORREOS IS
/*******************************************************************************
   NAME:       PQ_ENVIO_CORREOS
   PURPOSE:    Package para el envio de e-mails

   REVISIONS:
   Ver        Date        Author           Description
   ---------  ----------  ---------------  -------------------------------------
   1.0        05/05/2011  T-SYSTEMS        1. Created this package.
   1.3        23/05/2012  T-SYSTEMS        3. Anadidos procedimientos para sobreprecio
   1.4	      11/01/2016  T-SYSTEMS        4. Anadido procedimiento para enviar correo con mensaje de tipo Clob
   1.5        09/01/2020  T-SYSTEMS        5. Resolucion escalados
   1.6        28/07/2021  T-Systems        6. GDLD-63697 (PROD)
   1.7        12/11/2021  T-Systems        7. ESC-15880.

   NOTES:

******************************************************************************/

  TYPE            TpCursor	IS REF CURSOR;

  --Constante para el salto de linea. Se concatena a variables tipo varchar2
  v_salto         VARCHAR2(10) := CHR(10)||CHR(13);
  v_retorno       varchar2(10) := CHR(10);
  TYPE t_array IS TABLE OF VARCHAR2(200) INDEX BY BINARY_INTEGER;
  /* ESC-15880 ** MODIF TAM (12/11/2021) ** Inicio */ 
  TYPE map_estados IS TABLE OF NUMBER(5) INDEX BY VARCHAR2(30);

PROCEDURE enviarCorreoUnico(p_destinatario in varchar2,
                            p_asunto       in varchar2,
                            p_mensaje      in varchar2);

PROCEDURE enviarCorreo
(
  p_codgrupo	in varchar2,
  p_asunto	in varchar2,
  p_mensaje	in varchar2
);

PROCEDURE enviarCorreoGrande
(
  p_codgrupo	in varchar2,
  p_asunto	in varchar2,
  p_mensaje	in clob
);

PROCEDURE generaCorreoResumenEnvios(p_fecha IN varchar2);

FUNCTION getMensajeResumenEnvios(p_fecha IN varchar2) RETURN VARCHAR2;

FUNCTION getMensajePolizas(p_fecha IN varchar2) RETURN VARCHAR2;

FUNCTION getMensajeAnexoModificacion(p_fecha IN varchar2) RETURN VARCHAR2;

FUNCTION getMensajeAnexoReduccion(p_fecha IN varchar2) RETURN VARCHAR2;

FUNCTION getMensajeSiniestros(p_fecha IN varchar2) RETURN VARCHAR2;

FUNCTION getMensajeCuentasRenovables(p_fecha IN varchar2) RETURN VARCHAR2;
-- Sobreprecio

PROCEDURE generaCorreoResumenEnviosSbp(p_fecha IN varchar2);

FUNCTION getMensajeResumenEnviosSbp(p_fecha IN varchar2) RETURN VARCHAR2;

FUNCTION getMensajePolizasSbp(p_fecha IN varchar2) RETURN VARCHAR2;

------------------------------
-- Polizas de R.C Ganado
------------------------------
PROCEDURE generaCorreoResumenEnviosRc(p_fecha IN varchar2);

FUNCTION getMensajeResumenEnviosRc(p_fecha IN varchar2) RETURN VARCHAR2;

FUNCTION getMensajePolizasRc(p_fecha IN varchar2) RETURN VARCHAR2;

------------------------------------------
-- Polizas envio Integracion Agro en Iris
------------------------------------------
PROCEDURE generaCorreoResumenEnviosIris;

FUNCTION getMensajeResumenEnviosIris RETURN VARCHAR2;

FUNCTION getMensajePolizasIris RETURN VARCHAR2;



-- Actualizacion de origenes de datos de informes
PROCEDURE generaCorreoActODInformes (msg IN VARCHAR2);

-- Pagos
FUNCTION getMensajePagos(p_fecha IN VARCHAR2) RETURN VARCHAR2;

FUNCTION getMensajePagosRenovables(p_fecha IN VARCHAR2) RETURN VARCHAR2;

PROCEDURE generaCorreoBorradoPoliza(num_plz_estado_baja IN number,
                                    num_plz_borradas_ok IN number,
                                    num_plz_borradas_ko IN number,
                                    ids_polizas_ko      IN VARCHAR2);

PROCEDURE generaCorreoResumenSeguimiento(v_num_plz IN number,
                                         v_num_anx IN number,
                                         plzs_nf   IN VARCHAR2,
                                         v_estados_poliza map_estados,
										 v_estados_anexo  map_estados);

PROCEDURE generaCorreoSeguimientoTomador(v_cif     o02agpe0.TB_TOMADORES.ciftomador%TYPE,
										 v_eMail   o02agpe0.TB_TOMADORES.email%TYPE,
                                         v_plzs    VARCHAR2,
                                         v_anexos  VARCHAR2,
                                         v_plzs_sg VARCHAR2);

PROCEDURE generaCorreoResumenCargaCondRec(p_fecha IN varchar2);

FUNCTION SPLIT(in_string VARCHAR2, delim VARCHAR2) RETURN t_array;

PROCEDURE enviarCorreoConfigurable(p_host in varchar2, p_helo in varchar2, p_emisor	in varchar2, p_from	in varchar2, p_destinatario	in varchar2, p_asunto in varchar2, p_mensaje in varchar2);

END PQ_ENVIO_CORREOS;
/
CREATE OR REPLACE PACKAGE BODY O02AGPE0.PQ_ENVIO_CORREOS AS
-- Variables Globales para todas las funciones
SMTP_SERVER	VARCHAR2(13) := 'correointerno';
SMTP_USER	VARCHAR2(50) := 'comunicacionesagroplus@cajarural.com';

-------------------------------------------------------------------------------

PROCEDURE enviarCorreoUnico(p_destinatario in varchar2,
                            p_asunto       in varchar2,
                            p_mensaje      in varchar2) IS
  --Variables para la conexion con el servidor de correo
  c utl_smtp.connection;

  lc VARCHAR2(50) := 'pq_envio_correo.enviarCorreoUnico'; -- Variable que almacena el nombre del paquete y de la funcion

  PROCEDURE send_header(name IN VARCHAR2, header IN VARCHAR2) AS
  BEGIN
    utl_smtp.write_data(c, name || ': ' || header || utl_tcp.CRLF);
  END;

BEGIN

  pq_utl.log(lc, 'Dentro de enviarCorreoUnico.', 2);

  --Creamos la conexion con el servidor de correo
  c := utl_smtp.open_connection(SMTP_SERVER, 25);

  BEGIN
    --Establecemos la direccion que envia el correo
    utl_smtp.helo(c, SMTP_USER);
    utl_smtp.mail(c, SMTP_USER);

    --Establecemos el 'PARA' del correo
    utl_smtp.rcpt(c, p_destinatario);

    pq_utl.log(lc, 'Antes de rellenar Datos Correo.', 2);
    pq_utl.log(lc, 'Valor de email:' || p_destinatario, 2);
    pq_utl.log(lc, 'Valor del asunto:' || p_asunto, 1);

    --Rellenamos el correo
    utl_smtp.open_data(c);
    send_header('From', SMTP_USER);
    send_header('Subject', p_asunto);
    utl_smtp.write_data(c, '' || utl_tcp.CRLF);
    utl_smtp.write_data(c, p_mensaje || utl_tcp.CRLF);

    utl_smtp.close_data(c);
  EXCEPTION
    WHEN utl_smtp.transient_error OR utl_smtp.permanent_error THEN
      utl_smtp.quit(c);
      PQ_UTL.log('PQ_ENVIO_CORREOS.enviarCorreo - ERROR: ' || SQLCODE ||
                 ' - ' || SQLERRM || ' ****',
                 2);
      dbms_output.put_line(sqlerrm);
    WHEN OTHERS THEN
      PQ_UTL.log('PQ_ENVIO_CORREOS.enviarCorreo - ERROR: ' || SQLCODE ||
                 ' - ' || SQLERRM || ' ****',
                 2);
      dbms_output.put_line(sqlerrm);
  END;

  utl_smtp.quit(c);

END;

-------------------------------------------------------------------------------

PROCEDURE enviarCorreoGrande
(
  p_codgrupo	in varchar2,
  p_asunto	in varchar2,
  p_mensaje	in clob
)
IS
	--Variables para la conexion con el servidor de correo
	c utl_smtp.connection;

	--Variables auxiliares
	aux_query	varchar2(2000);
	TYPE TpCursor	IS REF CURSOR;
	aux_cursor	TpCursor;
	aux_email	TB_CORREOS.EMAIL%TYPE;

  -- Variables para escribir el CLOB en el cuerpo del correo
  l_step        PLS_INTEGER  := 900;

	PROCEDURE send_header(name IN VARCHAR2, header IN VARCHAR2) AS
	BEGIN
		utl_smtp.write_data(c, name || ': ' || header || utl_tcp.CRLF);
	END;

BEGIN
	--Creamos la conexion con el servidor de correo
	c := utl_smtp.open_connection(SMTP_SERVER ,25);

	--Consultamos los correos del grupo para enviarles el mensaje
	aux_query := 'SELECT EMAIL FROM TB_CORREOS T WHERE T.IDGRUPO = ' || p_codgrupo;

	OPEN aux_cursor FOR aux_query;

	FETCH aux_cursor INTO aux_email;
	LOOP
	EXIT WHEN aux_cursor%NOTFOUND;
		BEGIN
			--Establecemos la direccion que envia el correo
			utl_smtp.helo(c, SMTP_USER);
			utl_smtp.mail(c, SMTP_USER);

			--Establecemos el 'PARA' del correo
			utl_smtp.rcpt(c, aux_email);

			--Rellenamos el correo
			utl_smtp.open_data(c);
			send_header('From', SMTP_USER);
			send_header('Subject', p_asunto);

      UTL_SMTP.write_data(c, 'Content-Type: text/plain; charset="iso-8859-1"' || UTL_TCP.crlf || UTL_TCP.crlf);

      FOR i IN 0 .. TRUNC((DBMS_LOB.getlength(p_mensaje) - 1 )/l_step) LOOP
        UTL_SMTP.write_data(c, DBMS_LOB.substr(p_mensaje, l_step, i * l_step + 1));
      END LOOP;

      UTL_SMTP.write_data(c, UTL_TCP.crlf || UTL_TCP.crlf);

      send_header('Para cualquier problema ponerse en contacto con: incidenciasagrarios@segurosrga.es. ', ' ');

			utl_smtp.close_data(c);

		EXCEPTION
			WHEN utl_smtp.transient_error OR utl_smtp.permanent_error THEN
				utl_smtp.quit(c);
        PQ_UTL.log('PQ_ENVIO_CORREOS.enviarCorreo - ERROR: ' || SQLCODE || ' - ' || SQLERRM || ' ****', 2);
				dbms_output.put_line(sqlerrm);
			WHEN OTHERS THEN
        PQ_UTL.log('PQ_ENVIO_CORREOS.enviarCorreo - ERROR: ' || SQLCODE || ' - ' || SQLERRM || ' ****', 2);
				dbms_output.put_line(sqlerrm);
		END;

		FETCH aux_cursor INTO aux_email;
	END LOOP;
	utl_smtp.quit(c);




END;


-------------------------------------------------------------------------------

PROCEDURE enviarCorreo
(
  p_codgrupo	in varchar2,
  p_asunto	in varchar2,
  p_mensaje	in varchar2
)
IS
	--Variables para la conexion con el servidor de correo
	c utl_smtp.connection;

	--Variables auxiliares
	aux_query	varchar2(2000);

  lc VARCHAR2(50) := 'pq_envio_correo.enviarCorreo'; -- Variable que almacena el nombre del paquete y de la funcion

	TYPE TpCursor	IS REF CURSOR;
	aux_cursor	TpCursor;
	aux_email	TB_CORREOS.EMAIL%TYPE;

	PROCEDURE send_header(name IN VARCHAR2, header IN VARCHAR2) AS
	BEGIN
		utl_smtp.write_data(c, name || ': ' || header || utl_tcp.CRLF);
	END;

BEGIN


  pq_utl.log(lc, 'Dentro de enviarCorreo.', 2);

	--Creamos la conexion con el servidor de correo
	c := utl_smtp.open_connection(SMTP_SERVER ,25);

	--Consultamos los correos del grupo para enviarles el mensaje
	aux_query := 'SELECT EMAIL FROM TB_CORREOS T WHERE T.IDGRUPO = ' || p_codgrupo;

	OPEN aux_cursor FOR aux_query;

	FETCH aux_cursor INTO aux_email;
	LOOP
	EXIT WHEN aux_cursor%NOTFOUND;
		BEGIN
			--Establecemos la direccion que envia el correo
			utl_smtp.helo(c, SMTP_USER);
			utl_smtp.mail(c, SMTP_USER);

			--Establecemos el 'PARA' del correo
			utl_smtp.rcpt(c, aux_email);

      pq_utl.log(lc, 'Antes de rellenar Datos Correo.', 2);
      pq_utl.log(lc, 'Valor de email:' ||aux_email, 2);
      pq_utl.log(lc, 'Valor del asunto:' ||p_asunto, 1);

			--Rellenamos el correo
			utl_smtp.open_data(c);
			send_header('From', SMTP_USER);
			send_header('Subject', p_asunto);
			utl_smtp.write_data(c, '' || utl_tcp.CRLF);
			utl_smtp.write_data(c, p_mensaje||utl_tcp.CRLF);
			
      send_header('Para cualquier problema ponerse en contacto con: incidenciasagrarios@segurosrga.es. ', ' ');

			utl_smtp.close_data(c);
		EXCEPTION
			WHEN utl_smtp.transient_error OR utl_smtp.permanent_error THEN
				utl_smtp.quit(c);
        PQ_UTL.log('PQ_ENVIO_CORREOS.enviarCorreo - ERROR: ' || SQLCODE || ' - ' || SQLERRM || ' ****', 2);
				dbms_output.put_line(sqlerrm);
			WHEN OTHERS THEN
        PQ_UTL.log('PQ_ENVIO_CORREOS.enviarCorreo - ERROR: ' || SQLCODE || ' - ' || SQLERRM || ' ****', 2);
				dbms_output.put_line(sqlerrm);
		END;

		FETCH aux_cursor INTO aux_email;
	END LOOP;
	utl_smtp.quit(c);



END;

--Procedimiento para generar y enviar el correo resumen de los procesos batch de envio.
PROCEDURE generaCorreoResumenEnvios(p_fecha IN varchar2) IS

	lc VARCHAR2(50) := 'pq_envio_correo.generaCorreoResumenEnvios'; -- Variable que almacena el nombre del paquete y de la funcion

	aux_asunto	    varchar2(2000) := 'Alerta Agroplus: Resumen de envios a Agroseguro del dia ';
  	aux_fecha       varchar2(20);

	aux_mensaje	    varchar2(32000) := '';
	
	v_fecha_planif DATE;

BEGIN

	
	pq_utl.log(lc, 'Fecha de planificacion recibida por parametro:' || p_fecha);
	
	if p_fecha = '' then
		v_fecha_planif := sysdate;
	else
	-- Convertimos a DATE la fecha de planificacion recibida por parametro
		v_fecha_planif := TO_DATE(p_fecha,'YYYYMMDD');
	end if;
		
  --Recibimos la fecha de planificacion para anhadirla al asunto
  select to_char(v_fecha_planif, 'DD/MM/YYYY') into aux_fecha from dual;
  aux_asunto := aux_asunto || aux_fecha;

  aux_mensaje := PQ_ENVIO_CORREOS.getMensajeResumenEnvios(p_fecha);
  
  pq_utl.log(lc, 'ASUNTO: ' || aux_asunto, 2);
  pq_utl.log(lc, 'MENSAJE: ' || aux_mensaje, 2);

  Pq_Envio_Correos.enviarCorreo(p_codgrupo => 1, p_asunto => aux_asunto, p_mensaje => aux_mensaje);

END generaCorreoResumenEnvios;

FUNCTION getMensajeResumenEnvios(p_fecha IN varchar2) RETURN VARCHAR2 IS

	lc VARCHAR2(50) := 'pq_envio_correo.getMensajeResumenEnvios'; -- Variable que almacena el nombre del paquete y de la funcion

		
  aux_mensaje	    varchar2(32000) := '';

  aux_num_ref     number;
  

BEGIN

  --Obtenemos el mensaje de polizas
  aux_mensaje := aux_mensaje || PQ_ENVIO_CORREOS.getMensajePolizas(p_fecha => p_fecha);
  
  --Concatenamos al mensaje un separador
  aux_mensaje := aux_mensaje || PQ_ENVIO_CORREOS.v_salto || '---------------------------------' || PQ_ENVIO_CORREOS.v_salto;
  
  --Obtenemos el mensaje de anexos de reduccion
  aux_mensaje := aux_mensaje || PQ_ENVIO_CORREOS.getMensajeAnexoReduccion(p_fecha => p_fecha);
  
  --Concatenamos al mensaje un separador
  aux_mensaje := aux_mensaje || PQ_ENVIO_CORREOS.v_salto || '---------------------------------' || PQ_ENVIO_CORREOS.v_salto;

  --Concatenamos el numero de referencias libres
  select count(*) into aux_num_ref from tb_referencias_agricolas r where r.fechaenvio is null;
  aux_mensaje := aux_mensaje || 'Total referencias libres => ' || aux_num_ref || '  '
                 || PQ_ENVIO_CORREOS.v_salto || PQ_ENVIO_CORREOS.v_salto;

  --Concatenamos al mensaje dos separadores
  aux_mensaje := aux_mensaje || PQ_ENVIO_CORREOS.v_salto || '------------------------------------------------------------------' || PQ_ENVIO_CORREOS.v_retorno;
  aux_mensaje := aux_mensaje || '------------------------------------------------------------------' || PQ_ENVIO_CORREOS.v_salto;
	    
  --Obtenemos el mensaje de pagos
  aux_mensaje := aux_mensaje || PQ_ENVIO_CORREOS.getMensajePagos(p_fecha);
  
  --Obtenemos el mensaje de pagos renovables
  aux_mensaje := aux_mensaje || PQ_ENVIO_CORREOS.getMensajePagosRenovables(p_fecha);
	  
  --Obtenemos el mensaje de cuentas renovables
  aux_mensaje := aux_mensaje || PQ_ENVIO_CORREOS.getMensajeCuentasRenovables(p_fecha => p_fecha);
    
  RETURN aux_mensaje;

END getMensajeResumenEnvios;


--Funcion para generar el mensaje de los envios de poliza en una fecha
-- p_fecha: Fecha para la que se quiere obtener el mensaje
FUNCTION getMensajePolizas(p_fecha IN varchar2) RETURN VARCHAR2 IS
	
	lc VARCHAR2(50) := 'pq_envio_correo.getMensajePolizas'; -- Variable que almacena el nombre del paquete y de la funcion

	aux_referencia	TB_POLIZAS.REFERENCIA%TYPE;

  aux_cnt         number(5) := 0;
	aux_mensaje	    varchar2(32000) := '';

  num_plz_tot_env number(5) := 0;
  num_plz_tot_env_ok number(5) := 0;
  num_plz_tot_env_ko number(5) := 0;
  aux_cur_plz_err TpCursor;
  aux_msg_err     varchar2(32000) := '';
  
  v_fecha_planif DATE;

BEGIN
	
	-- Comienzo de escritura en log
	pq_utl.log(lc, '## INI ##', 1);
	pq_utl.log(lc, 'Fecha de planificacion recibida por parametro:' || p_fecha);
		  
	-- Convertimos a DATE la fecha de planificacion recibida por parametro
	v_fecha_planif := TO_DATE(p_fecha,'YYYYMMDD');
	
      /* ESC-7582 ** MODIF TAM (16.12.2019) */
      /* No tenemos en cuenta los estados de poliza > 10, asi no tenemos en cuenta las polizas renovables */
      select count(*) into aux_cnt
        from o02agpe0.tb_polizas p
       where p.fechaenvio >= to_date(to_char(v_fecha_planif - 1, 'dd/mm/yyyy')||' 16:40:00', 'dd/mm/yyyy hh24:mi:ss')
         and p.fechaenvio <= to_date(to_char(v_fecha_planif, 'dd/mm/yyyy')||' 16:40:00', 'dd/mm/yyyy hh24:mi:ss')
         and p.idestado = 8;

      num_plz_tot_env_ok := num_plz_tot_env_ok + aux_cnt;

      /* ESC-7582 ** MODIF TAM (16.12.2019) */
      /* No tenemos en cuenta los estados de poliza > 10, asi no tenemos en cuenta las polizas renovables */
       select count(*) into aux_cnt
        from o02agpe0.tb_polizas p
        where p.fechaenvio >= to_date(to_char(v_fecha_planif - 1, 'dd/mm/yyyy')||' 16:40:00', 'dd/mm/yyyy hh24:mi:ss')
        and p.fechaenvio <= to_date(to_char(v_fecha_planif, 'dd/mm/yyyy')||' 16:40:00', 'dd/mm/yyyy hh24:mi:ss')
        and p.idestado <> 8 and p.idestado < 10;

      num_plz_tot_env_ko := num_plz_tot_env_ko + aux_cnt;

	  --DNF 17 Junio 2019 Anhado en el where de la query la clausula para eliminar las renovables
      IF (aux_cnt > 0) THEN
          --Buscamos referencia, plan y linea de las polizas rechazadas para obtener las descripciones de los errores
         /* ESC-7582 ** MODIF TAM (16.12.2019) */
         /* No tenemos en cuenta los estados de poliza > 10, asi no tenemos en cuenta las polizas renovables */
          OPEN aux_cur_plz_err FOR select p.referencia
                                     from o02agpe0.tb_polizas p
                                    where p.fechaenvio >= to_date(to_char(v_fecha_planif - 1, 'dd/mm/yyyy')||' 16:40:00', 'dd/mm/yyyy hh24:mi:ss')
                                      and p.fechaenvio <= to_date(to_char(v_fecha_planif, 'dd/mm/yyyy')||' 16:40:00', 'dd/mm/yyyy hh24:mi:ss')
                                      and p.idestado <> 8 and p.idestado < 10;
          FETCH aux_cur_plz_err INTO aux_referencia;

          LOOP
              EXIT WHEN aux_cur_plz_err%NOTFOUND;

              IF aux_msg_err IS NULL THEN
                 aux_msg_err := aux_referencia;
              ELSE
                  aux_msg_err := aux_msg_err || ', ' || aux_referencia;
              END IF;

              FETCH aux_cur_plz_err INTO aux_referencia;

          END LOOP;
          CLOSE aux_cur_plz_err;

      END IF;

  /* ESC-7582 ** MODIF TAM (16.12.2019) */
  select count(*) into num_plz_tot_env
    from o02agpe0.tb_polizas p
    where p.fechaenvio >= to_date(to_char(v_fecha_planif - 1, 'dd/mm/yyyy')||' 16:40:00', 'dd/mm/yyyy hh24:mi:ss')
    and p.fechaenvio <= to_date(to_char(v_fecha_planif, 'dd/mm/yyyy')||' 16:40:00', 'dd/mm/yyyy hh24:mi:ss')
    and p.idestado < 10;

  aux_mensaje := 'Polizas totales enviadas a Agroseguro => ' || num_plz_tot_env || '  ' || PQ_ENVIO_CORREOS.v_retorno || '  ' ||
  'Polizas totales enviadas a AGROSEGURO enviadas correctas => ' || num_plz_tot_env_ok || '  '  || PQ_ENVIO_CORREOS.v_retorno || '  '  ||
  'Polizas totales enviadas a AGROSEGURO sin estado enviada correcta => ' || num_plz_tot_env_ko || '  '  || PQ_ENVIO_CORREOS.v_salto ||
  ' Referencias: ' || aux_msg_err || '  ' ;

  RETURN aux_mensaje;

END getMensajePolizas;

--Funcion para generar el mensaje de los envios de anexos de modificacion en una fecha
-- p_fecha: Fecha para la que se quiere obtener el mensaje
FUNCTION getMensajeAnexoModificacion(p_fecha IN varchar2) RETURN VARCHAR2 IS

	aux_referencia	TB_POLIZAS.REFERENCIA%TYPE;
	aux_dc		      TB_POLIZAS.DC%TYPE;

  aux_cnt         number(5) := 0;
  aux_idenvio     TB_COMUNICACIONES.IDENVIO%TYPE;
	aux_mensaje	    varchar2(32000) := '';

  num_mod_def     number(5) := 0;
  num_mod_env     number(5) := 0;
  num_mod_ok      number(5) := 0;
  num_mod_err     number(5) := 0;
  num_mod_no_env  number(5) := 0;
  num_mod_no_env_ref varchar2(2000);

  consulta_plz    varchar2(2000);
  aux_cur_plz	    TpCursor;
  aux_cur_plz_err TpCursor;
  aux_cur_err     TpCursor;
  aux_plan        TB_LINEAS.CODPLAN%TYPE;
  aux_linea       TB_LINEAS.CODLINEA%TYPE;
  aux_msg_err     varchar2(32000) := '';
	str_cod		      varchar2(2000);
	str_desc	      varchar2(2000);
  str_path	      varchar2(2000);

BEGIN
  --Seleccionamos las polizas para anhadirlas al mensaje
  consulta_plz := 'select c.idenvio from o02agpe0.tb_comunicaciones c where to_char(c.fecha_envio, ''YYYYMMDD'') = '''
                  || p_fecha || ''' and c.fichero_tipo = ''M''';

  OPEN aux_cur_plz FOR consulta_plz;
	FETCH aux_cur_plz INTO aux_idenvio;
	LOOP
	    EXIT WHEN aux_cur_plz%NOTFOUND;

      select count(*) into aux_cnt from o02agpe0.tb_anexo_mod m where m.idenvio = aux_idenvio;
      num_mod_env := num_mod_env + aux_cnt;

      select count(*) into aux_cnt from o02agpe0.tb_anexo_mod m where m.estado = 3 and m.idenvio = aux_idenvio;
      num_mod_ok := num_mod_ok + aux_cnt;

      select count(*) into aux_cnt from o02agpe0.tb_anexo_mod m where m.estado = 4 and m.idenvio = aux_idenvio;
      num_mod_err := num_mod_err + aux_cnt;
      --En caso de que haya errores, anhadimos el texto con el error
      IF (aux_cnt > 0) THEN
          --Buscamos referencia, plan y linea de las polizas rechazadas para obtener las descripciones de los errores
          OPEN aux_cur_plz_err FOR 'select p.referencia, p.dc, l.codplan, l.codlinea
                                   from o02agpe0.tb_anexo_mod m, o02agpe0.tb_polizas p, o02agpe0.tb_lineas l
                                   where m.idpoliza = p.idpoliza and p.lineaseguroid = l.lineaseguroid and m.estado = 4 and m.idenvio = ' || aux_idenvio;
          FETCH aux_cur_plz_err INTO aux_referencia, aux_dc, aux_plan, aux_linea;
          LOOP
              EXIT WHEN aux_cur_plz_err%NOTFOUND;
              OPEN aux_cur_err FOR SELECT extractvalue(value(err), '//@codigo'), extractvalue(value(err), '//@descripcion'),extractvalue(value(loc), '//@xpath')
			                             FROM tb_comunicaciones c,
				                           TABLE (xmlsequence(extract(xmltype(c.fichero_contenido),'/xml-fragment/acus:Documento', 'xmlns:acus=http://www.agroseguro.es/AcuseRecibo'))) docs,
				                           TABLE (xmlsequence(extract(value(docs),'/acus:Documento/acus:Error', 'xmlns:acus=http://www.agroseguro.es/AcuseRecibo'))) err,
                                   TABLE (xmlsequence(extract(value(err),'acus:Error/acus:Localizacion', 'xmlns:acus=http://www.agroseguro.es/AcuseRecibo'))) loc
			                             WHERE extractvalue(value(docs), '//@id') = aux_referencia || aux_plan || aux_linea
				                           AND C.IDENVIO = aux_idenvio;

		          -- Montamos el mensaje
			        -- En el correo indicaremos REFERENCIA y DC
			        aux_msg_err := aux_msg_err || ' - ' || aux_referencia || aux_dc || '  '  || Pq_Envio_Correos.v_retorno;
			        --  y LISTA DE ERRORES DEVUELTOS. En el xml viene identificado por REFERENCIA + PLAN + LINEA
			        FETCH aux_cur_err INTO str_cod, str_desc,str_path;
			        LOOP
				      EXIT WHEN aux_cur_err%NOTFOUND;
                  -- Por cada error, ponemos su codigo y su descripcion
				          aux_msg_err := aux_msg_err || '    * ' || str_cod || ' - ' || str_desc || '-' || str_path ||'  '  || pq_envio_correos.v_retorno;

				          FETCH aux_cur_err INTO str_cod, str_desc,str_path;
			        END LOOP;
			        CLOSE aux_cur_err;

              FETCH aux_cur_plz_err INTO aux_referencia, aux_dc, aux_plan, aux_linea;

          END LOOP;
          CLOSE aux_cur_plz_err;

      END IF;

      FETCH aux_cur_plz INTO aux_idenvio;
  END LOOP;
  CLOSE aux_cur_plz;

  select AGP_VALOR into num_mod_def from o02agpe0.tb_config_agp c where c.agp_nemo = 'ANEXO_MOD_DEF';
  
  select AGP_VALOR into num_mod_no_env from o02agpe0.tb_config_agp c where c.agp_nemo = 'ANEXO_MOD_DEF_NO_ENVIADOS';
  
  select AGP_VALOR into num_mod_no_env_ref from o02agpe0.tb_config_agp c where c.agp_nemo = 'ANEXO_MOD_DEF_NO_ENVIADOS_REF';

  aux_mensaje := 'Anexos de modificacion marcados como definitivos => ' || num_mod_def || '  ' || pq_envio_correos.v_retorno || '  '  ||
              'Anexos de modificacion enviados => ' || num_mod_env || '  '  || pq_envio_correos.v_retorno || '  '  ||
              'Anexos de modificacion correctos => ' || num_mod_ok || '  '  || pq_envio_correos.v_retorno || '  '  ||
              'Anexos de modificacion erroneos => ' || num_mod_err || '  '  || pq_envio_correos.v_retorno || '  '  ||
              'Anexos de modificacion no enviados por validacion rechazada =>' || '  '|| num_mod_no_env;

  IF num_mod_no_env != 0 THEN

     aux_mensaje := aux_mensaje ||'  Referencias:'||num_mod_no_env_ref;

  END IF;


  aux_mensaje := aux_mensaje || '  '  || pq_envio_correos.v_salto || '  '  || aux_msg_err;

  RETURN aux_mensaje;

END getMensajeAnexoModificacion;


--Funcion para generar el mensaje de los envios de anexos de reduccion en una fecha
-- p_fecha: Fecha para la que se quiere obtener el mensaje
FUNCTION getMensajeAnexoReduccion(p_fecha IN varchar2) RETURN VARCHAR2 IS

	aux_referencia	TB_POLIZAS.REFERENCIA%TYPE;
	aux_dc		      TB_POLIZAS.DC%TYPE;

  aux_cnt         number(5) := 0;
  aux_idenvio     TB_COMUNICACIONES.IDENVIO%TYPE;
	aux_mensaje	    varchar2(32000) := '';

  num_red_def     number(5) := 0;
  num_red_env     number(5) := 0;
  num_red_ok      number(5) := 0;
  num_red_err     number(5) := 0;

  consulta_plz    varchar2(2000);
  aux_cur_plz	    TpCursor;
  aux_cur_plz_err TpCursor;
  aux_cur_err     TpCursor;
  aux_plan        TB_LINEAS.CODPLAN%TYPE;
  aux_linea       TB_LINEAS.CODLINEA%TYPE;
  aux_msg_err     varchar2(32000) := '';
	str_cod		      varchar2(2000);
	str_desc	      varchar2(2000);

BEGIN
  --Seleccioamos las polizas para anhadirlas al mensaje
  consulta_plz := 'select c.idenvio from o02agpe0.tb_comunicaciones c where to_char(c.fecha_envio, ''YYYYMMDD'') = '''
                  || p_fecha || ''' and c.fichero_tipo = ''R''';

  OPEN aux_cur_plz FOR consulta_plz;
	FETCH aux_cur_plz INTO aux_idenvio;
	LOOP
	    EXIT WHEN aux_cur_plz%NOTFOUND;

      select count(*) into aux_cnt from o02agpe0.tb_anexo_red r where r.idenvio = aux_idenvio;
      num_red_env := num_red_env + aux_cnt;

      select count(*) into aux_cnt from o02agpe0.tb_anexo_red r where r.idestado = 3 and r.idenvio = aux_idenvio;
      num_red_ok := num_red_ok + aux_cnt;

      select count(*) into aux_cnt from o02agpe0.tb_anexo_red r where r.idestado = 4 and r.idenvio = aux_idenvio;
      num_red_err := num_red_err + aux_cnt;
      --En caso de que haya errores, anhadimos el texto con el error
      IF (aux_cnt > 0) THEN
          --Buscamos referencia, plan y linea de las polizas rechazadas para obtener las descripciones de los errores
          OPEN aux_cur_plz_err FOR 'select p.referencia, p.dc, l.codplan, l.codlinea
                                   from o02agpe0.tb_anexo_red r, o02agpe0.tb_polizas p, o02agpe0.tb_lineas l
                                   where r.idpoliza = p.idpoliza and p.lineaseguroid = l.lineaseguroid and r.idestado = 4 and r.idenvio = ' || aux_idenvio;
          FETCH aux_cur_plz_err INTO aux_referencia, aux_dc, aux_plan, aux_linea;
          LOOP
              EXIT WHEN aux_cur_plz_err%NOTFOUND;
              OPEN aux_cur_err FOR SELECT extractvalue(value(err), '//@codigo'), extractvalue(value(err), '//@descripcion')
			                             FROM tb_comunicaciones c,
				                           TABLE (xmlsequence(extract(xmltype(c.fichero_contenido),'/xml-fragment/acus:Documento', 'xmlns:acus=http://www.agroseguro.es/AcuseRecibo'))) docs,
				                           TABLE (xmlsequence(extract(value(docs),'/acus:Documento/acus:Error', 'xmlns:acus=http://www.agroseguro.es/AcuseRecibo'))) err
			                             WHERE extractvalue(value(docs), '//@id') = aux_referencia || aux_plan || aux_linea
				                           AND C.IDENVIO = aux_idenvio;

		          -- Montamos el mensaje
			        -- En el correo indicaremos REFERENCIA y DC
			        aux_msg_err := aux_msg_err || ' - ' || aux_referencia || aux_dc || '  '  || pq_envio_correos.v_retorno;
			        --  y LISTA DE ERRORES DEVUELTOS. En el xml viene identificado por REFERENCIA + PLAN + LINEA
			        FETCH aux_cur_err INTO str_cod, str_desc;
			        LOOP
				      EXIT WHEN aux_cur_err%NOTFOUND;
                  -- Por cada error, ponemos su codigo y su descripcion
				          aux_msg_err := aux_msg_err || '    * ' || str_cod || ' - ' || str_desc || '  '  || pq_envio_correos.v_retorno;

				          FETCH aux_cur_err INTO str_cod, str_desc;
			        END LOOP;
			        CLOSE aux_cur_err;

              FETCH aux_cur_plz_err INTO aux_referencia, aux_dc, aux_plan, aux_linea;

          END LOOP;
          CLOSE aux_cur_plz_err;

      END IF;

      FETCH aux_cur_plz INTO aux_idenvio;
  END LOOP;
  CLOSE aux_cur_plz;

 num_red_def := PQ_UTL.getcfgAndDelete('ANEXO_RED_DEF' || p_fecha);
 
  aux_mensaje := 'Anexos de reduccion marcados como definitivos => ' || num_red_def || '  ' || pq_envio_correos.v_retorno || '  '  ||
                 'Anexos de reduccion enviados => ' || num_red_env || '  '  || pq_envio_correos.v_retorno || '  '  ||
                 'Anexos de reduccion correctos => ' || num_red_ok || '  '  || pq_envio_correos.v_retorno || '  '  ||
                 'Anexos de reduccion erroneos => ' || num_red_err || '  ' ;

  aux_mensaje := aux_mensaje || '  '  || pq_envio_correos.v_salto || '  '  || aux_msg_err;

  RETURN aux_mensaje;

END getMensajeAnexoReduccion;

--Funcion para generar el mensaje de los envios de siniestros en una fecha
-- p_fecha: Fecha para la que se quiere obtener el mensaje
FUNCTION getMensajeSiniestros(p_fecha IN varchar2) RETURN VARCHAR2 IS

	aux_referencia	TB_POLIZAS.REFERENCIA%TYPE;
	aux_dc		      TB_POLIZAS.DC%TYPE;
  aux_numinterno  TB_SINIESTROS.NUMINTERNOENVIO%TYPE;

  aux_cnt         number(6) := 0;
  aux_idenvio     TB_COMUNICACIONES.IDENVIO%TYPE;
	aux_mensaje	    varchar2(32000) := '';

  num_sin_def     number(6) := 0;
  num_sin_env     number(6) := 0;
  num_sin_ok      number(6) := 0;
  num_sin_err     number(6) := 0;

  consulta_plz    varchar2(2000);
  aux_cur_plz	    TpCursor;
  aux_cur_plz_err TpCursor;
  aux_cur_err     TpCursor;
  aux_plan        TB_LINEAS.CODPLAN%TYPE;
  aux_linea       TB_LINEAS.CODLINEA%TYPE;
  aux_msg_err     varchar2(32000) := '';
	str_cod		      varchar2(2000);
	str_desc	      varchar2(2000);

  aux_ref_tot     varchar2(2000) := '';

BEGIN
  --Seleccionamos las polizas para anhadirlas al mensaje
  consulta_plz := 'select c.idenvio from o02agpe0.tb_comunicaciones c where to_char(c.fecha_envio, ''DD/MM/YYYY'') = '''
                  || p_fecha || ''' and c.fichero_tipo = ''S''';

  OPEN aux_cur_plz FOR consulta_plz;
	FETCH aux_cur_plz INTO aux_idenvio;
	LOOP
	    EXIT WHEN aux_cur_plz%NOTFOUND;

      select count(*) into aux_cnt from o02agpe0.tb_siniestros s where s.idenvio = aux_idenvio;
      num_sin_env := num_sin_env + aux_cnt;

      select count(*) into aux_cnt from o02agpe0.tb_siniestros s where s.estado = 3 and s.idenvio = aux_idenvio;
      num_sin_ok := num_sin_ok + aux_cnt;

      select count(*) into aux_cnt from o02agpe0.tb_siniestros s where s.estado = 4 and s.idenvio = aux_idenvio;
      num_sin_err := num_sin_err + aux_cnt;
      --En caso de que haya errores, anhadimos el texto con el error
      IF (aux_cnt > 0) THEN
          --Buscamos referencia, plan y linea de las polizas rechazadas para obtener las descripciones de los errores
          OPEN aux_cur_plz_err FOR 'select p.referencia, p.dc, l.codplan, l.codlinea, S.Numinternoenvio
                                   from o02agpe0.tb_siniestros s, o02agpe0.tb_polizas p, o02agpe0.tb_lineas l
                                   where s.idpoliza = p.idpoliza and p.lineaseguroid = l.lineaseguroid and s.estado = 4 and s.idenvio = ' || aux_idenvio;
          FETCH aux_cur_plz_err INTO aux_referencia, aux_dc, aux_plan, aux_linea, aux_numinterno;
          LOOP
              EXIT WHEN aux_cur_plz_err%NOTFOUND;
              OPEN aux_cur_err FOR SELECT extractvalue(value(err), '//@codigo'), extractvalue(value(err), '//@descripcion')
			                             FROM tb_comunicaciones c,
				                           TABLE (xmlsequence(extract(xmltype(c.fichero_contenido),'/xml-fragment/acus:Documento', 'xmlns:acus=http://www.agroseguro.es/AcuseRecibo'))) docs,
				                           TABLE (xmlsequence(extract(value(docs),'/acus:Documento/acus:Error', 'xmlns:acus=http://www.agroseguro.es/AcuseRecibo'))) err
			                             WHERE extractvalue(value(docs), '//@id') = lpad(aux_numinterno, 6, '0')
				                           AND C.IDENVIO = aux_idenvio;

		          -- Montamos el mensaje
			        -- En el correo indicaremos REFERENCIA y DC
			        aux_msg_err := aux_msg_err || ' - ' || aux_referencia || aux_dc || '  ' || pq_envio_correos.v_retorno;
			        --  y LISTA DE ERRORES DEVUELTOS. En el xml viene identificado por REFERENCIA + PLAN + LINEA
			        FETCH aux_cur_err INTO str_cod, str_desc;
			        LOOP
				      EXIT WHEN aux_cur_err%NOTFOUND;
                  -- Por cada error, ponemos su codigo y su descripcion
				          aux_msg_err := aux_msg_err || '    * ' || str_cod || ' - ' || str_desc || '  '  || pq_envio_correos.v_retorno;

				          FETCH aux_cur_err INTO str_cod, str_desc;
			        END LOOP;
			        CLOSE aux_cur_err;

              FETCH aux_cur_plz_err INTO aux_referencia, aux_dc, aux_plan, aux_linea, aux_numinterno;

          END LOOP;
          CLOSE aux_cur_plz_err;

      END IF;

      FETCH aux_cur_plz INTO aux_idenvio;
  END LOOP;
  CLOSE aux_cur_plz;

  select AGP_VALOR into num_sin_def from o02agpe0.tb_config_agp c where c.agp_nemo = 'SINIESTROS_DEF';

  aux_mensaje := 'Siniestros marcados como definitivos => ' || num_sin_def || '  ' || pq_envio_correos.v_retorno || '  '  ||
                 'Siniestros enviados => ' || num_sin_env || '  '  || pq_envio_correos.v_retorno || '  '  ||
                 'Siniestros correctos => ' || num_sin_ok || '  '  || pq_envio_correos.v_retorno || '  '  ||
                 'Siniestros erroneos => ' || num_sin_err || '  ' ;


  aux_mensaje := aux_mensaje || '  '  || pq_envio_correos.v_salto || '  '  || aux_msg_err;

  --Busco si hay siniestros que no se han enviado a Agro pero estan en definitivo
  select AGP_VALOR into aux_ref_tot from o02agpe0.tb_config_agp c where c.agp_nemo = 'SINIESTROS_DEF_NO_ENV';

  IF aux_ref_tot <> ' ' THEN
      aux_mensaje := aux_mensaje || 'Siniestros marcados como definitivos y no enviados: ' || pq_envio_correos.v_salto || aux_ref_tot;
  END IF;

  RETURN aux_mensaje;

END getMensajeSiniestros;


--Funcion para generar el mensaje de los envios de Cuentas de polizas renovables en una fecha
-- p_fecha: Fecha para la que se quiere obtener el mensaje
FUNCTION getMensajeCuentasRenovables(p_fecha IN varchar2) RETURN VARCHAR2 IS

	aux_referencia	TB_POLIZAS_RENOVABLES.REFERENCIA%TYPE;
	aux_dc		      TB_POLIZAS_RENOVABLES.DC%TYPE;

  aux_cnt         number(5) := 0;
  aux_idenvio     TB_COMUNICACIONES.IDENVIO%TYPE;
	aux_mensaje	    varchar2(32000) := '';

  --num_plz_def     number(5) := 0;
  num_plz_env     number(5) := 0;
  num_plz_ok      number(5) := 0;
  num_plz_err     number(5) := 0;
  consulta_plz    varchar2(2000);
  aux_cur_plz	    TpCursor;
  aux_cur_plz_err TpCursor;
  aux_cur_err     TpCursor;
  aux_plan        TB_LINEAS.CODPLAN%TYPE;
  aux_linea       TB_LINEAS.CODLINEA%TYPE;
  aux_msg_err     varchar2(32000) := '';
	str_cod		      varchar2(2000);
	str_desc	      varchar2(2000);

BEGIN
  --Seleccioamos las polizas para anhadirlas al mensaje
  consulta_plz := 'select c.idenvio from o02agpe0.tb_comunicaciones c where to_char(c.fecha_envio, ''YYYYMMDD'') = '''
                  || p_fecha || ''' and c.fichero_tipo = ''G''';

  OPEN aux_cur_plz FOR consulta_plz;
	FETCH aux_cur_plz INTO aux_idenvio;
	LOOP
	    EXIT WHEN aux_cur_plz%NOTFOUND;

      select count(*) into aux_cnt from o02agpe0.tb_polizas_renovables p where p.idenvio = aux_idenvio;
      num_plz_env := num_plz_env + aux_cnt;

      select count(*) into aux_cnt from o02agpe0.tb_polizas_renovables p where p.estado_envio_iban_agro = 4 and p.idenvio = aux_idenvio;
      num_plz_ok := num_plz_ok + aux_cnt;

      select count(*) into aux_cnt from o02agpe0.tb_polizas_renovables p where p.estado_envio_iban_agro = 5 and p.idenvio = aux_idenvio;
      num_plz_err := num_plz_err + aux_cnt;
      --En caso de que haya errores, anhadimos el texto con el error
      IF (aux_cnt > 0) THEN
          --Buscamos referencia, plan y linea de las polizas renovables rechazadas para obtener las descripciones de los errores
          OPEN aux_cur_plz_err FOR 'select p.referencia, p.dc, p.plan, p.linea
                                   from o02agpe0.tb_polizas_renovables p
                                   where p.estado_envio_iban_agro = 5 and p.idenvio = ' || aux_idenvio;
          FETCH aux_cur_plz_err INTO aux_referencia, aux_dc, aux_plan, aux_linea;
          LOOP
              EXIT WHEN aux_cur_plz_err%NOTFOUND;
              OPEN aux_cur_err FOR SELECT extractvalue(value(err), '//@codigo'), extractvalue(value(err), '//@descripcion')
			                             FROM tb_comunicaciones c,
				                           TABLE (xmlsequence(extract(xmltype(c.fichero_contenido),'/xml-fragment/acus:Documento', 'xmlns:acus=http://www.agroseguro.es/AcuseRecibo'))) docs,
				                           TABLE (xmlsequence(extract(value(docs),'/acus:Documento/acus:Error', 'xmlns:acus=http://www.agroseguro.es/AcuseRecibo'))) err
			                             WHERE extractvalue(value(docs), '//@id') = aux_referencia || aux_plan || aux_linea
				                           AND C.IDENVIO = aux_idenvio;




		          -- Montamos el mensaje
			        -- En el correo indicaremos REFERENCIA y DC
			        aux_msg_err := aux_msg_err || ' '||aux_referencia || aux_dc || ': ';
			        --  y LISTA DE ERRORES DEVUELTOS. En el xml viene identificado por REFERENCIA + PLAN + LINEA
			        FETCH aux_cur_err INTO str_cod, str_desc;
			        LOOP
				      EXIT WHEN aux_cur_err%NOTFOUND;
                  -- Por cada error, ponemos su codigo y su descripcion
				          aux_msg_err := aux_msg_err || str_cod || ' - ' || str_desc || '  '  || PQ_ENVIO_CORREOS.v_retorno|| '  ';

				          FETCH aux_cur_err INTO str_cod, str_desc;
			        END LOOP;
			        CLOSE aux_cur_err;

              FETCH aux_cur_plz_err INTO aux_referencia, aux_dc, aux_plan, aux_linea;

          END LOOP;
          CLOSE aux_cur_plz_err;

      END IF;

      FETCH aux_cur_plz INTO aux_idenvio;
  END LOOP;
  CLOSE aux_cur_plz;

  aux_mensaje := 'RESUMEN DE CUENTAS DE POLIZAS RENOVABLES ENVIADAS A AGROSEGURO ' || '  '  || PQ_ENVIO_CORREOS.v_retorno || '  '  ||
				 'Cuentas enviadas a Agroseguro => ' || num_plz_env || '   ' || PQ_ENVIO_CORREOS.v_retorno || '  '  ||
				 'Cuentas enviadas a Agroseguro correctas => ' || num_plz_ok || '   ' || PQ_ENVIO_CORREOS.v_retorno || '  '  ||
				 'Cuentas enviadas a Agroseguro erroneas => ' || num_plz_err || '  ';

  aux_mensaje := aux_mensaje || '  '  ||  PQ_ENVIO_CORREOS.v_retorno || '  '  || aux_msg_err;

  RETURN aux_mensaje;

END getMensajeCuentasRenovables;

-----------------------------
-- ENVIOS PARA SOBREPRECIO --
-----------------------------
--
-- Procedimiento para generar y enviar el correo resumen del proceso batch de envio de polizas de Sobreprecio.
--
PROCEDURE generaCorreoResumenEnviosSbp(p_fecha IN varchar2) IS



	lc VARCHAR2(50) := 'pq_envio_correo.generaCorreoResumenEnviosSbp'; -- Variable que almacena el nombre del paquete y de la funcion
	
	aux_asunto	    varchar2(2000) := 'Alerta Agroplus: Resumen de envios de Sobreprecio a OMEGA del dia ';
  	aux_fecha       varchar2(20);

	aux_mensaje	    varchar2(32000) := '';
	
    v_fecha_planif DATE;	

BEGIN
	
	-- Comienzo de escritura en log
	pq_utl.log(lc, '## INI ##', 1);
	pq_utl.log(lc, 'Fecha de planificacion recibida por parametro:' || p_fecha);
		  
	-- Convertimos a DATE la fecha de planificacion recibida por parametro
	v_fecha_planif := TO_DATE(p_fecha,'YYYYMMDD');

  --Seleccionamos la fecha de hoy para anhadirla al asunto
  select to_char(v_fecha_planif, 'DD/MM/YYYY') into aux_fecha from dual;
  aux_asunto := aux_asunto || aux_fecha;
  pq_utl.log(lc, 'Asunto generado: ', 2);
  pq_utl.log(lc, aux_asunto, 2);
  aux_mensaje := PQ_ENVIO_CORREOS.getMensajeResumenEnviosSbp(p_fecha);
	
  pq_utl.log(lc, 'Mensaje generado: ', 2);
  pq_utl.log(lc, aux_mensaje, 2);

  Pq_Envio_Correos.enviarCorreo(p_codgrupo => 1, p_asunto => aux_asunto, p_mensaje => aux_mensaje);

END generaCorreoResumenEnviosSbp;


--
-- Devuelve el cuerpo del mensaje de envios de polizas de sobreprecio
--
FUNCTION getMensajeResumenEnviosSbp(p_fecha IN varchar2) RETURN VARCHAR2 IS

  aux_mensaje	    varchar2(32000) := '';

BEGIN

  --Obtenemos el mensaje de polizas de sobreprecio
  aux_mensaje := aux_mensaje || PQ_ENVIO_CORREOS.getMensajePolizasSbp(p_fecha);
  --Concatenamos al mensaje un separador
  aux_mensaje := aux_mensaje || PQ_ENVIO_CORREOS.v_salto || '---------------------------------' || PQ_ENVIO_CORREOS.v_salto;

  RETURN aux_mensaje;

END getMensajeResumenEnviosSbp;


--
-- Funcion para generar el mensaje de los envios de poliza en una fecha
-- p_fecha: Fecha para la que se quiere obtener el mensaje
--
FUNCTION getMensajePolizasSbp(p_fecha IN varchar2) RETURN VARCHAR2 IS

  -- Variables que almacenan el numero de polizas que hay en los diferentes estados
  num_plz_sbp_def     number(5) := 0;
  num_plz_sbp_env     number(5) := 0;
  num_plz_sbp_ok      number(5) := 0;
  num_plz_sbp_error   number(5) := 0;
  v_ref_desconocidas VARCHAR2(4000) := '';

  -- Estados de las polizas de sobreprecio
  estado_ok           number(1) := 5;
  estado_error        number(1) := 4;

  -- Nombre de la variable de configuracion que almacena el num de polizas de sobreprecio pasadas a definitiva
  nom_var_config	    varchar2(15) := 'POLIZAS_SBP_DEF';
  nom_var_config_no_tratadas	    varchar2(20) := 'REF_SBP_NO_TRATADAS';

  -- Cursor para las polizas de sobreprecio erroneas
  aux_cur_plz_err     TpCursor;

  -- Variables para anhadir al mensaje los datos de la poliza de sobreprecio erronea
  aux_referencia	    TB_SBP_POLIZAS.REFERENCIA%TYPE;
  aux_id_error	      Tb_Sbp_Errores_Poliza.Iderror%TYPE;
  aux_desc_error	    Tb_Sbp_Errores_Poliza.Desc_Error%TYPE;

  -- Mensaje general
  aux_mensaje	    varchar2(32000) := '';
  -- Mensaje de polizas erroneas
  aux_msg_err     varchar2(32000) := '';

BEGIN

  -- Obtiene el total de polizas de sobreprecio pasadas a definitiva desde la ultima ejecucion del batch de envios
  num_plz_sbp_def := PQ_UTL.getcfgAndDelete(nom_var_config || p_fecha);

  -- Obtiene el total de polizas de sobreprecio enviadas
  select count(*) into num_plz_sbp_env from o02agpe0.tb_sbp_polizas sbp
  inner join o02agpe0.tb_polizas p on sbp.idpoliza = p.idpoliza
  where (select h.fecha
         from o02agpe0.vw_sbp_historico_estados_desc h
         where h.IDPOLIZA_SBP = sbp.id and h.ESTADO = 3 and rownum = 1) = TO_DATE (p_fecha, 'YYYYMMDD')
				and p.lineaseguroid in (select distinct (f.lineaseguroid)
										from o02agpe0.tb_sbp_fechas_contratacion f
										where TO_DATE(p_fecha, 'YYYYMMDD') between f.fechainicio and f.fechafin);

  -- Obtiene el numero de polizas de sobreprecio enviadas y correctas
  select count(*) into num_plz_sbp_ok from o02agpe0.tb_sbp_polizas sbp
  inner join o02agpe0.tb_polizas p on sbp.idpoliza = p.idpoliza
  where (select h.fecha
         from o02agpe0.vw_sbp_historico_estados_desc h
         where h.IDPOLIZA_SBP = sbp.id and h.ESTADO = 3 and rownum = 1) = TO_DATE (p_fecha, 'YYYYMMDD')
			and sbp.idestado = estado_ok
			and p.lineaseguroid in (select distinct (f.lineaseguroid)
									from o02agpe0.tb_sbp_fechas_contratacion f
									where TO_DATE(p_fecha, 'YYYYMMDD') between f.fechainicio and f.fechafin);

  -- Obtiene el numero de polizas de sobreprecio enviadas y erroneas
  select count(*) into num_plz_sbp_error from o02agpe0.tb_sbp_polizas sbp
  inner join o02agpe0.tb_polizas p on sbp.idpoliza = p.idpoliza
  where (select h.fecha
         from o02agpe0.vw_sbp_historico_estados_desc h
         where h.IDPOLIZA_SBP = sbp.id and h.ESTADO = 3 and rownum = 1) = TO_DATE (p_fecha, 'YYYYMMDD')
			and sbp.idestado = estado_error
			and p.lineaseguroid in (select distinct (f.lineaseguroid)
									from o02agpe0.tb_sbp_fechas_contratacion f
									where TO_DATE(p_fecha, 'YYYYMMDD') between f.fechainicio and f.fechafin);

  -- Obtiene el listado de referencias de poliza no encontradas
  v_ref_desconocidas := PQ_UTL.getcfgAndDelete(nom_var_config_no_tratadas || p_fecha);

  --En caso de que haya errores, anhadimos el texto con el error
  IF (num_plz_sbp_error > 0) THEN

      -- Se obtiene la referencia, el codigo de error y la descripcion de cada una de las polizas erroneas
      OPEN aux_cur_plz_err FOR 'select p.referencia, ep.iderror, ep.desc_error
                                from o02agpe0.tb_sbp_polizas         p,
                                     o02agpe0.tb_sbp_errores_poliza  ep,
                                     o02agpe0.tb_sbp_polizas_errores pe
                               where (select h.fecha
                                       from o02agpe0.vw_sbp_historico_estados_desc h
                                       where h.IDPOLIZA_SBP = p.id and h.ESTADO = 3 and rownum = 1) = TO_DATE(''' || p_fecha || ''', ''YYYYMMDD'')
                                 and p.id = pe.id
                                 and pe.iderror = ep.iderror
                                 and p.idestado = ' || estado_error;

      FETCH aux_cur_plz_err INTO aux_referencia, aux_id_error, aux_desc_error;


      -- Montamos el mensaje
      LOOP
          EXIT WHEN aux_cur_plz_err%NOTFOUND;
          -- En el correo indicaremos la referencia, el codigo del error y su descripcion
          aux_msg_err := aux_msg_err || ' - ' || aux_referencia || ' - ' || aux_id_error || ': '
                                     || aux_desc_error || '  '  || PQ_ENVIO_CORREOS.v_retorno;

          FETCH aux_cur_plz_err INTO aux_referencia, aux_id_error, aux_desc_error;

      END LOOP;
      CLOSE aux_cur_plz_err;

  END IF;



  aux_mensaje := 'Polizas marcadas como definitivas => ' || num_plz_sbp_def || '  '  || PQ_ENVIO_CORREOS.v_retorno || '  '  ||
				 'Polizas enviadas => ' || num_plz_sbp_env || '  '  || PQ_ENVIO_CORREOS.v_retorno || '  '  ||
				 'Polizas correctas => ' || num_plz_sbp_ok || '  '  || PQ_ENVIO_CORREOS.v_retorno || '  '  ||
				 'Polizas erroneas => ' || num_plz_sbp_error || '  ' ;

  -- Si hay referencias no encontradas se incluyen en el mensaje
  IF (v_ref_desconocidas IS NOT NULL and v_ref_desconocidas <> ' ') THEN
     aux_mensaje := aux_mensaje || '  '  || PQ_ENVIO_CORREOS.v_retorno || '  '  || 'Polizas no encontradas => ' || v_ref_desconocidas || '  ' ;
  END IF;

  aux_mensaje := aux_mensaje || '  '  ||  PQ_ENVIO_CORREOS.v_salto || '  '  || aux_msg_err;

  RETURN aux_mensaje;

END getMensajePolizasSbp;


-----------------------------
-- ENVIOS PARA R.C GANADO  --
-----------------------------

--
-- Procedimiento para generar y enviar el correo resumen del proceso batch de envio de polizas de Sobreprecio.
--
PROCEDURE generaCorreoResumenEnviosRc(p_fecha IN varchar2) IS

	aux_asunto	    varchar2(2000) := 'Alerta Agroplus: Resumen de envios de R.C Ganado a OMEGA del dia ';
  	aux_fecha       varchar2(20);

	aux_mensaje	    varchar2(32000) := '';

	lc VARCHAR2(50) := 'pq_envio_correo.generaCorreoResumenEnviosRc'; -- Variable que almacena el nombre del paquete y de la funcion
	
	v_fecha_planif DATE;

BEGIN

		-- Comienzo de escritura en log
		pq_utl.log(lc, '## INI ##', 1);

		pq_utl.log(lc, 'Fecha de planificacion recibida por parametro:' || p_fecha);

		-- Convertimos a DATE la fecha de planificacion recibida por parametro
		v_fecha_planif := TO_DATE(p_fecha,'YYYYMMDD');

		-- Se guarda el path fisico del directorio
		pq_utl.log(lc, 'Comenzamos con el envio de correo de R.C Ganado.', 2);



  --Seleccionamos la fecha de planificacion para anhadirla al asunto
  select to_char(v_fecha_planif, 'DD/MM/YYYY') into aux_fecha from dual;
  aux_asunto := aux_asunto || aux_fecha;
  pq_utl.log(lc, 'Asunto generado: ', 2);
  pq_utl.log(lc, aux_asunto, 2);
  aux_mensaje := PQ_ENVIO_CORREOS.getMensajeResumenEnviosRc(p_fecha);

  pq_utl.log(lc, 'Mensaje generado: ', 2);
  pq_utl.log(lc, aux_mensaje, 2);
  pq_utl.log(lc, 'Comenzamos con el envio de correo de R.C Ganado.', 2);


  pq_utl.log(lc, 'Antes de enviar_correo.', 2);
  Pq_Envio_Correos.enviarCorreo(p_codgrupo => 1, p_asunto => aux_asunto, p_mensaje => aux_mensaje);

END generaCorreoResumenEnviosRc;


--
-- Devuelve el cuerpo del mensaje de envios de polizas de sobreprecio
--
FUNCTION getMensajeResumenEnviosRc(p_fecha IN varchar2) RETURN VARCHAR2 IS

  aux_mensaje	    varchar2(32000) := '';
 	lc VARCHAR2(50) := 'pq_envio_correo.getMensajeResumenEnviosRc'; -- Variable que almacena el nombre del paquete y de la funcion

BEGIN

  -- Se guarda el path fisico del directorio
	pq_utl.log(lc, 'Entramos para enviar correo de Rc.', 2);

  --Obtenemos el mensaje de polizas de sobreprecio
  aux_mensaje := aux_mensaje || PQ_ENVIO_CORREOS.getMensajePolizasRc(p_fecha);
  --Concatenamos al mensaje un separador
  aux_mensaje := aux_mensaje || PQ_ENVIO_CORREOS.v_salto || '---------------------------------' || PQ_ENVIO_CORREOS.v_salto;


  RETURN aux_mensaje;

END getMensajeResumenEnviosRc;


--
-- Funcion para generar el mensaje de los envios de poliza en una fecha
-- p_fecha: Fecha para la que se quiere obtener el mensaje
--
FUNCTION getMensajePolizasRc(p_fecha IN varchar2) RETURN VARCHAR2 IS

  -- Variables que almacenan el numero de polizas que hay en los diferentes estados
  num_plz_Rc_def     number(5) := 0;
  num_plz_Rc_env     number(5) := 0;
  num_plz_Rc_ok      number(5) := 0;
  num_plz_Rc_error   number(5) := 0;
  v_ref_desconocidas VARCHAR2(4000) := '';

  -- Estados de las polizas de R.C Ganado
  estado_ok           number(1) := 5;
  estado_error        number(1) := 4;

  -- Nombre de la variable de configuracion que almacena el num de polizas de R.C Ganado pasadas a definitiva
  nom_var_config	    varchar2(15) := 'POLIZAS_RC_DEF';
  nom_var_config_no_tratadas	    varchar2(20) := 'REF_RC_NO_TRATADAS';

  -- Cursor para las polizas de R.C erroneas
  aux_cur_plz_err     TpCursor;

  -- Variables para anhadir al mensaje los datos de la poliza de sobreprecio erronea
  aux_referencia	    TB_POLIZAS.REFERENCIA%TYPE;
  aux_id_error	      Tb_Sbp_Errores_Poliza.Iderror%TYPE;
  aux_desc_error	    Tb_Sbp_Errores_Poliza.Desc_Error%TYPE;

  -- Mensaje general
  aux_mensaje	    varchar2(32000) := '';
  -- Mensaje de polizas erroneas
  aux_msg_err     varchar2(32000) := '';

 	lc VARCHAR2(50) := 'pq_envio_correo.getMensajePolizasRc'; -- Variable que almacena el nombre del paquete y de la funcion

BEGIN

  -- Obtiene el total de polizas de R.C Ganado pasadas a definitiva desde la ultima ejecucion del batch de envios
  num_plz_rc_def := PQ_UTL.getcfgAndDelete(nom_var_config || p_fecha);

  -- Se guarda el path fisico del directorio
	pq_utl.log(lc, 'Obtenermos el num polizas pasadas a definitivas' || num_plz_rc_def, 2);
  	pq_utl.log(lc, 'valor de la fecha recibida por parametro:' || p_fecha);


  -- Obtiene el total de polizas de R.C Ganado enviadas
  select count(*) into num_plz_rc_env
    from o02agpe0.tb_rc_polizas p
  where (select TO_DATE (h.fecha, 'DD/MM/YYYY')
         from o02agpe0.tb_rc_polizas_hist_estados h
         where h.IDPOLIZA_RC = p.id
           and h.idestado = 3 and rownum = 1) = TO_DATE (p_fecha, 'YYYYMMDD');

  -- logs Envio correo R.C Ganado --
	pq_utl.log(lc, 'Obtenermos el num polizas de R.C enviadas:' || num_plz_rc_env, 2);


  -- Obtiene el numero de polizas de R.C Ganado enviadas y correctas
  select count(*)
    into num_plz_rc_ok
    from o02agpe0.tb_rc_polizas p
  where (select TO_DATE (h.fecha, 'DD/MM/YYYY')
           from o02agpe0.tb_rc_polizas_hist_estados h
          where h.IDPOLIZA_RC = p.id
                and h.idestado = 3
                and rownum = 1) = TO_DATE (p_fecha, 'YYYYMMDD')
    and p.idestado = estado_ok;

  -- logs Envio correo R.C Ganado --
	pq_utl.log(lc, 'Obtenermos el num polizas de R.C enviadas y correctas:' || num_plz_rc_ok, 2);


  -- Obtiene el numero de polizas de R.C Ganado enviadas y erroneas
  select count(*) into num_plz_rc_error
    from o02agpe0.tb_rc_polizas p
   where (select TO_DATE (h.fecha, 'DD/MM/YYYY')
           from o02agpe0.tb_rc_polizas_hist_estados h
          where h.IDPOLIZA_RC = p.id
                and h.idestado = 3
                and rownum = 1) = TO_DATE (p_fecha, 'YYYYMMDD')
    and p.idestado = estado_error;

    -- logs Envio correo R.C Ganado --
	pq_utl.log(lc, 'Obtenermos el num polizas de R.C enviadas y erroneas:' || num_plz_rc_error, 2);


  -- Obtiene el listado de referencias de poliza no encontradas
  v_ref_desconocidas := PQ_UTL.getcfgAndDelete(nom_var_config_no_tratadas || p_fecha);

  -- logs Envio correo R.C Ganado --
	pq_utl.log(lc, 'Obtenermos el listado de Referencias no encontradas:' || v_ref_desconocidas, 2);


  --En caso de que haya errores, anhadimos el texto con el error
  IF (num_plz_rc_error > 0) THEN

      -- Se obtiene la referencia, el codigo de error y la descripcion de cada una de las polizas erroneas
      OPEN aux_cur_plz_err FOR 'select p.referencia, pr.iderror, ep.descripcion
                                from o02agpe0.tb_polizas     p,
                                     o02agpe0.tb_rc_polizas  pr,
                                     o02agpe0.tb_rc_errores  ep
                               where (select TO_DATE (h.fecha, ''DD/MM/YYYY'')
                                       from o02agpe0.tb_rc_polizas_hist_estados h
                                       where h.IDPOLIZA_RC = pr.id
                                       and h.idESTADO = 3
                                       and rownum = 1) = TO_DATE(p_fecha, ''YYYYMMDD'')
                                 and p.idpoliza = pr.idpoliza
                                 and pr.iderror = ep.id
                                 and pr.idestado = ' || estado_error;

      FETCH aux_cur_plz_err INTO aux_referencia, aux_id_error, aux_desc_error;


      -- logs Envio correo R.C Ganado --
	  pq_utl.log(lc, 'Despues de buscar las polizas erroneas', 2);
      pq_utl.log(lc, 'Despues de buscar las polizas erroneas. aux_referencia' ||aux_referencia, 2);
      pq_utl.log(lc, 'Despues de buscar las polizas erroneas. aux_id_error' ||aux_id_error, 2);
      pq_utl.log(lc, 'Despues de buscar las polizas erroneas. aux_id_error' ||aux_id_error, 2);
      pq_utl.log(lc, 'Despues de buscar las polizas erroneas. aux_desc_error' ||aux_desc_error, 2);

      -- Montamos el mensaje
      LOOP
          EXIT WHEN aux_cur_plz_err%NOTFOUND;
          -- En el correo indicaremos la referencia, el codigo del error y su descripcion
          aux_msg_err := aux_msg_err || ' - ' || aux_referencia || ' - ' || aux_id_error || ': '
                                     || aux_desc_error || '  '  || PQ_ENVIO_CORREOS.v_retorno;

          FETCH aux_cur_plz_err INTO aux_referencia, aux_id_error, aux_desc_error;

      END LOOP;
      CLOSE aux_cur_plz_err;

  END IF;



  aux_mensaje := 'Polizas marcadas como definitivas => ' || num_plz_rc_def || '  '  || PQ_ENVIO_CORREOS.v_retorno || '  '  ||
				 'Polizas enviadas => ' || num_plz_rc_env || '  '  || PQ_ENVIO_CORREOS.v_retorno || '  '  ||
				 'Polizas correctas => ' || num_plz_rc_ok || '  '  || PQ_ENVIO_CORREOS.v_retorno || '  '  ||
				 'Polizas erroneas => ' || num_plz_rc_error || '  ' ;

  -- Si hay referencias no encontradas se incluyen en el mensaje
  IF (v_ref_desconocidas IS NOT NULL and v_ref_desconocidas <> ' ') THEN
     aux_mensaje := aux_mensaje || '  '  || PQ_ENVIO_CORREOS.v_retorno || '  '  || 'Polizas no encontradas => ' || v_ref_desconocidas || '  ' ;
  END IF;

  aux_mensaje := aux_mensaje || '  '  ||  PQ_ENVIO_CORREOS.v_salto || '  '  || aux_msg_err;

  RETURN aux_mensaje;

END getMensajePolizasRc;

-- Pet. 55722 (Integracion de Agroseguro en IRIS - Envio de Correo con Resultado
--------------------------------------------------------------------------------
-----------------------------------
-- ENVIOS A AGROSEGURO POR IRIS  --
-----------------------------------

--
-- Procedimiento para generar y enviar el correo resumen del proceso batch de envio de polizas a agroseguro por Iris
--
PROCEDURE generaCorreoResumenEnviosIris IS

	aux_asunto	    varchar2(2000) := 'Alerta Agroplus: Resumen de Polizas de Agroseguros a Integrar en IRIS - ';
	aux_fecha       varchar2(20);

	aux_mensaje	    varchar2(32000) := '';

	lc VARCHAR2(50) := 'pq_envio_correo.generaCorreoResumenEnviosIris'; -- Variable que almacena el nombre del paquete y de la funcion

BEGIN

	-- Comienzo de escritura en log
	pq_utl.log(lc, '## INICIO ENVIO CORREO POLIZAS AGRO-IRIS ##', 1);
	-- Se guarda el path fisico del directorio
	pq_utl.log(lc, 'Comenzamos con el envio de correo de envios Agro-Iris.', 2);



  --Seleccionamos la fecha de hoy para anhadirla al asunto
  select to_char(sysdate, 'DD/MM/YYYY') into aux_fecha from dual;
  aux_asunto := aux_asunto || aux_fecha;

  aux_mensaje := PQ_ENVIO_CORREOS.getMensajeResumenEnviosIris();

  pq_utl.log(lc, 'Antes de enviar_correo.', 2);
  Pq_Envio_Correos.enviarCorreo(p_codgrupo => 1, p_asunto => aux_asunto, p_mensaje => aux_mensaje);

END generaCorreoResumenEnviosIris;



----------------------------------------------------------------
-- Devuelve el cuerpo del mensaje de envios de polizas a IRIS --
----------------------------------------------------------------
FUNCTION getMensajeResumenEnviosIris RETURN VARCHAR2 IS

  aux_mensaje	    varchar2(32000) := '';
 	lc VARCHAR2(50) := 'pq_envio_correo.getMensajeResumenEnviosiris'; -- Variable que almacena el nombre del paquete y de la funcion

BEGIN

  -- Se guarda el path fisico del directorio
	pq_utl.log(lc, 'Entramos para enviar correo de Iris.', 2);

  --Obtenemos el mensaje de polizas
  aux_mensaje := aux_mensaje || PQ_ENVIO_CORREOS.getMensajePolizasIris();
  --Concatenamos al mensaje un separador
  aux_mensaje := aux_mensaje || PQ_ENVIO_CORREOS.v_salto || '---------------------------------' || PQ_ENVIO_CORREOS.v_salto;


  RETURN aux_mensaje;

END getMensajeResumenEnviosIris;


--------------------------------------------------------------------------
-- Funcion para generar el mensaje de los envios de poliza en una fecha --
-- p_fecha: Fecha para la que se quiere obtener el mensaje              --
--------------------------------------------------------------------------
FUNCTION getMensajePolizasIris RETURN VARCHAR2 IS

  -- Variables que almacenan el numero de polizas que hay en los diferentes estados
  num_plz_Iris_ok         number(5) := 0;
  v_plz_Iris_rechazadas   VARCHAR2(4000) := 0;

  v_cod_agro_iris_OK      VARCHAR2(5);

  -- Nombre de la variable de configuracion que almacena el num de polizas enviadas a Iris y las polizas rechazadas
  nom_var_config	              varchar2(18) := 'POLIZAS_IRIS_AGRO';
  nom_var_config_rechazadas	    varchar2(24) := 'POLIZAS_IRIS_RECHAZADAS';
  nom_var_cod_ok                varchar2(14) := 'ESTADO_IRIS_OK';
  num_plz_Agro_iris_env         number(6);
  num_plz_agro_iris_ok          number(6);
  num_pol_enviadas              number(6);


  -- Pet. 55722 ** MODIF TAM (06.02.2020)
  nom_var_conf_cancel           VARCHAR2(16) := 'CANCEL_IRIS_AGRO';
  nom_var_conf_cancel_rechz     VARCHAR2(22) := 'CANCEL_IRIS_AGRO_RECHZ';
  nom_var_conf_cancel_ok        VARCHAR2(19) := 'CANCEL_IRIS_AGRO_OK';

  v_cancel_iris_rechazadas      VARCHAR2(4000) :=0;

  num_cancel_ok                 VARCHAR(6) := '0';
  num_canc_enviadas             VARCHAR(6) := '0';
  aux_fecha       varchar2(20);



  -- Mensaje general
  aux_mensaje	    varchar2(32000) := '';

 	lc VARCHAR2(50) := 'pq_envio_correo.getMensajePolizasIris'; -- Variable que almacena el nombre del paquete y de la funcion

BEGIN

  select to_char(sysdate, 'DD/MM/YYYY')
    into aux_fecha from dual;

    -- Obtiene el total de polizas enviadas a Iris
  --

  -- Obtenemos el codigo de poliza integrada ok en IRIS
  select AGP_VALOR
    into v_cod_agro_iris_OK
  from o02agpe0.tb_config_agp c
  where c.agp_nemo = nom_var_cod_ok;

  -- Obtenemos el num de polizas enviadas a IRIS
  select count(*)
    into num_plz_Agro_iris_env
  from o02agpe0.tb_polizas pol
  where to_date(pol.fecha_envio_iris) = TO_DATE(sysdate, 'DD/MM/YY');

  -- Obtenemos el num de polizas enviadas grabadas en TB_CONFIG_AGP
  select AGP_VALOR
    into num_pol_enviadas
  from o02agpe0.tb_config_agp c
  where c.agp_nemo = nom_var_config;

  -- Pet. 55722 ** MODIF TAM (06.02.2020) ** Inicio
  -- Incluimos en el correo resumen de las polizas/cancelaciones enviadas a IRIS

  -- Obtenemos el num de Cancelaciones enviadas a IRIS y grabadas en TB_CONFIG_AGP en el proceso de envio.
  select AGP_VALOR
    into num_canc_enviadas
  from o02agpe0.tb_config_agp c
  where c.agp_nemo = nom_var_conf_cancel;

  -- Obtenemos las referencias de aquellas cancelaciones que han sido rechazadas por IRIS
  select AGP_VALOR
    into v_cancel_iris_rechazadas
   from o02agpe0.tb_config_agp c
  where c.agp_nemo = nom_var_conf_cancel_rechz;

  -- Obtenemos el num de Cancelaciones que han sido aceptadas por IRIS
  select AGP_VALOR
    into num_cancel_ok
   from o02agpe0.tb_config_agp c
  where c.agp_nemo = nom_var_conf_cancel_ok;


  -- Pet. 55722 ** MODIF TAM (06.02.2020) ** Fin

  -- Se guarda el path fisico del directorio
	pq_utl.log(lc, 'Obtenemos el num polizas enviadas hoy a Iris: ' || num_plz_Agro_iris_env, 2);
  pq_utl.log(lc, 'Obtenemos el num de polizas enviadas y grabadas en TB_CONFIG: ' || num_pol_enviadas, 2);
  pq_utl.log(lc, 'valor del sysdate: ' || TO_DATE (SYSDATE, 'DD/MM/YYYY'), 2);


  -- Obtiene el total de polizas de Agroseguro enviadas a Iris y que han sido integradas satisfactoriamente.
  select count(*)
    into num_plz_agro_iris_ok
  from o02agpe0.tb_polizas pol
  where to_date(pol.fecha_recepcion_iris) = TO_DATE(sysdate, 'DD/MM/YY')
    and pol.estado_iris = v_cod_agro_iris_OK;

  -- logs Envio correo R.C Ganado --
	pq_utl.log(lc, 'Obtenermos el num polizas de Agroseguros integradas OK en IRIS: ' || num_plz_agro_iris_ok, 2);


  -- Obtiene el listado de referencias de poliza rechazadas
  select AGP_VALOR
    into v_plz_Iris_rechazadas
  from o02agpe0.tb_config_agp c
  where c.agp_nemo = nom_var_config_rechazadas;

	pq_utl.log(lc, 'Obtenemos el listado de Referencias rechazadas: ' || v_plz_Iris_rechazadas, 2);

  -- Pet. 55722 ** MODIF TAM (06.02.2020)
  -- Se anhaden las estadisticas de las cancelaciones enviadas, aceptadas y rechazadas.

  aux_mensaje := 'Alerta Agroplus: Resumen de Polizas de Agroseguros Enviadas a IRIS - ' || aux_fecha || '  '  || PQ_ENVIO_CORREOS.v_retorno || '  '  || PQ_ENVIO_CORREOS.v_retorno ||
				 'Polizas enviadas: ' || num_pol_enviadas || '  '  || PQ_ENVIO_CORREOS.v_retorno ||
				 'Polizas aceptadas: ' || num_plz_agro_iris_ok || '  '  || PQ_ENVIO_CORREOS.v_retorno ||
				 'Polizas rechazadas: ' || v_plz_Iris_rechazadas || '  '  || PQ_ENVIO_CORREOS.v_retorno || '  '  || PQ_ENVIO_CORREOS.v_retorno ||
				 'Cancelaciones enviadas: ' || num_canc_enviadas || '  '  || PQ_ENVIO_CORREOS.v_retorno ||
				 'Cancelaciones aceptadas: ' || num_cancel_ok || '  '  || PQ_ENVIO_CORREOS.v_retorno ||
				 'Cancelaciones rechazadas: ' || v_cancel_iris_rechazadas ;


  RETURN aux_mensaje;

END getMensajePolizasIris;


-- Fin Pet. 55722


PROCEDURE generaCorreoActODInformes (msg IN VARCHAR2) IS

BEGIN

     Pq_Envio_Correos.enviarCorreo(p_codgrupo => 5, p_asunto => 'Actualizacion de origenes de datos del modulo de Informes del dia ' || to_char(sysdate, 'DD/MM/YYYY'), p_mensaje => msg);

END;

---------------------------------------
--- CORREO DE POLIZAS ENVIADAS A PAGOS
---------------------------------------

--
-- Devuelve el cuerpo del mensaje de pagos
--
FUNCTION getMensajePagos(p_fecha IN VARCHAR2) RETURN VARCHAR2 IS
	
 	lc VARCHAR2(50) := 'pq_envio_correo.getMensajePagos'; -- Variable que almacena el nombre del paquete y de la funcion

  aux_mensaje	    varchar2(32000) := '';

  v_polEnviadas   varchar2(2000);
  v_polMarcadasEnviar   varchar2(2000);
  v_polOk   varchar2(2000);
  v_polError   varchar2(2000);
  v_polMsgError   varchar2(2000);
  v_resultEjecucion   varchar2(2000);

  v_polNoEnviadas number;
  v_polRefNoEnviadas varchar2(2000);
  TYPE TpCursor 		IS REF CURSOR;
  v_cursor   		    TpCursor;
  v_referencia      varchar2(7);

  --v_polCrm varchar2(2000);
  v_polCra varchar2(2000);
  v_polManuales varchar2(2000);
  v_polDomiciliadas varchar2(2000);
  

BEGIN
	
	pq_utl.log(lc, '## INI ##', 1);
	pq_utl.log(lc, 'Fecha de planificacion recibida por parametro:' || p_fecha);
		  
	
  --Obtenemos las variables necesarias para montar el mensaje
  v_polEnviadas := PQ_Utl.getcfgAndDelete ('PAGOS_POLIZAS_ENVIADAS'|| p_fecha);
  v_polMarcadasEnviar := PQ_Utl.getcfgAndDelete ('PAGOS_POLIZAS_A_ENVIAR' || p_fecha);
  v_polOk := PQ_Utl.getcfgAndDelete ('PAGOS_POLIZAS_OK' || p_fecha);
  v_polError := PQ_Utl.getcfgAndDelete ('PAGOS_POLIZAS_ERROR' || p_fecha);
  v_polMsgError := PQ_Utl.getcfgAndDelete ('PAGOS_POLIZAS_MSG_ERROR' || p_fecha);
  v_resultEjecucion := PQ_Utl.getcfgAndDelete ('PAGOS_POLIZA_EJECUCION' || p_fecha);
  v_polCra := PQ_Utl.getcfgAndDelete ('PAGOS_POLIZAS_CRA' || p_fecha);
  v_polManuales := PQ_Utl.getcfgAndDelete ('PAGOS_POLIZAS_MANUALES' || p_fecha);
  v_polDomiciliadas := PQ_Utl.getcfgAndDelete ('PAGOS_POLIZAS_DOM' || p_fecha);

  aux_mensaje := 'Proceso de pagos terminado ' || v_resultEjecucion || '   ' || PQ_ENVIO_CORREOS.v_salto;

  aux_mensaje := aux_mensaje || 'Polizas marcadas para enviar a pagos => '|| v_polMarcadasEnviar || '   ' || PQ_ENVIO_CORREOS.v_retorno || '   ' ||
								'Polizas enviadas a pagos => '|| v_polEnviadas || '   ' || PQ_ENVIO_CORREOS.v_retorno || '   ' ||
								'Polizas enviadas a pagos correctas => '|| v_polOk || '   ' || PQ_ENVIO_CORREOS.v_retorno || '   ' ||
								'Polizas marcadas con pago manual => '|| v_polManuales || '   ' || PQ_ENVIO_CORREOS.v_retorno || '   ' ||
								'Polizas enviadas a pagos erroneas => '|| v_polError || '   ' || PQ_ENVIO_CORREOS.v_retorno;

  IF (to_number(v_polError) > 0) THEN
     aux_mensaje := aux_mensaje || v_polMsgError || '   ' || PQ_ENVIO_CORREOS.v_salto;
  END IF;

  v_polNoEnviadas := to_number(v_polMarcadasEnviar) - to_number(v_polEnviadas) - to_number(v_polCra) - to_number(v_polManuales);
  IF (v_polNoEnviadas < 0) THEN
      v_polNoEnviadas := 0;
  END IF;
  
  aux_mensaje := aux_mensaje || 'Polizas definitivas no enviadas a pagos => '|| v_polNoEnviadas || '   ' || PQ_ENVIO_CORREOS.v_retorno;

  IF (v_polNoEnviadas > 0) THEN
      -- No tenemos incluimos las polizas en definitivas porque cuando se procesa este mensaje ya se tienen que haber enviado a Agroseguro.
      OPEN v_cursor FOR 'select referencia from o02agpe0.tb_polizas p where p.IDESTADO in (5, 7, 8) AND p.PAGADA = 0 and p.fechaenvio <= to_date(to_char(sysdate, ''dd/mm/yyyy'')|| ''16:40:00'', ''dd/mm/yyyy hh24:mi:ss'')';
      LOOP FETCH v_cursor INTO v_referencia;
      EXIT WHEN v_cursor%NOTFOUND;
          v_polRefNoEnviadas := v_polRefNoEnviadas || v_referencia || ', ';
      END LOOP;

      aux_mensaje := aux_mensaje || 'Referencias: '|| v_polRefNoEnviadas || '   ' || PQ_ENVIO_CORREOS.v_retorno;
  END IF;

  aux_mensaje := aux_mensaje || 'Polizas domiciliadas con Agroseguro => '|| v_polDomiciliadas || '   ' || PQ_ENVIO_CORREOS.v_salto;

  aux_mensaje := aux_mensaje || 'Polizas de CR Almendralejo enviadas a pagos => '|| v_polCra || '   ';
  aux_mensaje := aux_mensaje || PQ_ENVIO_CORREOS.v_salto || '---------------------------------' || PQ_ENVIO_CORREOS.v_salto;

  RETURN aux_mensaje;

END getMensajePagos;

FUNCTION getMensajePagosRenovables(p_fecha IN VARCHAR2) RETURN VARCHAR2 IS

	lc VARCHAR2(50) := 'pq_envio_correo.getMensajePagosRenovables'; -- Variable que almacena el nombre del paquete y de la funcion

  aux_mensaje	    varchar2(32000) := '';

  v_polEnviadas   varchar2(2000);
  v_polMarcadasEnviar   varchar2(2000);
  v_polOk   varchar2(2000);
  v_polError   varchar2(2000);
  v_polMsgError   varchar2(2000);
  str_plz_def_e_msg1 varchar2(2000);
  str_plz_def_e_msg2 varchar2(2000);

  v_polNoEnviadas number;
  v_polCrm varchar2(2000);
  v_polCra varchar2(2000);

  v_array_no_enviadas t_array;


  pos_dos_pun number;
  v_mensaje varchar2(2000);
  v_refer varchar2(2000);
  v_refer_concat varchar2(2000);
  -- Pet. 55722 ** MODIF TAM (06.02.2020) --
  -- Ampliamos el tamanho por que hay veces que da error
  -- al intentar concatenar 2 cadenas de 2000
  --v_mensaje_nuevo varchar2(2000);
  v_mensaje_nuevo varchar2(4500);

  v_array_referencia t_array;
  v_array_mensaje t_array;

  /* ESC-7877 */
  v_polOkTom varchar2(2000);


  TYPE T_Sampleid IS TABLE OF VARCHAR2 (2000);

  arr_sampleid    t_sampleid := T_Sampleid ();
  arr_sampleid2   t_sampleid := T_Sampleid ();
BEGIN

  --Obtenemos las variables necesarias para montar el mensaje
  v_polEnviadas := PQ_Utl.getcfgAndDelete ('PAGOS_POL_REN_ENV' || p_fecha);
  v_polMarcadasEnviar := PQ_Utl.getcfgAndDelete ('PAGOS_POL_REN_A_ENV' || p_fecha);
  v_polOk := PQ_Utl.getcfgAndDelete ('PAGOS_POL_RENOVA_OK' || p_fecha);
  v_polError := PQ_Utl.getcfgAndDelete ('PAGOS_POL_RENOVA_ERR' || p_fecha);
  v_polMsgError := PQ_Utl.getcfgAndDelete ('PAGOS_POL_REN_MSG_ERR' || p_fecha);

  /* ESC-7877 ** MODIF TAM (11.12.2019) */
  v_polOkTom    := PQ_Utl.getcfgAndDelete ('POL_PAGADAS_TOMADOR' || p_fecha);

  str_plz_def_e_msg1 := PQ_Utl.getcfgAndDelete ('PAGOS_POL_DEF_MSG_ER1' || p_fecha);
  str_plz_def_e_msg2 := PQ_Utl.getcfgAndDelete ('PAGOS_POL_DEF_MSG_ER2' || p_fecha);

  v_polCrm := PQ_Utl.getcfgAndDelete ('PAGOS_POLIZAS_RENOVA_CRM');
  v_polCra := PQ_Utl.getcfgAndDelete ('PAGOS_POL_RENOVA_CRA' || p_fecha);

  aux_mensaje := 'RESUMEN DE PAGOS DE POLIZAS RENOVABLES ' || '  '  || PQ_ENVIO_CORREOS.v_salto;

  aux_mensaje := aux_mensaje || 'Polizas marcadas para enviar a pagos => '|| v_polMarcadasEnviar || '   ' || PQ_ENVIO_CORREOS.v_retorno || '   ' ||
								'Polizas enviadas a pagos => '|| v_polEnviadas || '   ' || PQ_ENVIO_CORREOS.v_retorno || '   ' ||
								'Polizas pagadas no enviadas por Tomador => '|| v_polOkTom || '   ' || PQ_ENVIO_CORREOS.v_retorno || '   ' ||
								'Polizas enviadas a pagos correctas => '|| v_polOk || '   ' || PQ_ENVIO_CORREOS.v_retorno || '   ' ||
								'Polizas enviadas a pagos erroneas => '|| v_polError || '   ' || PQ_ENVIO_CORREOS.v_retorno;

  IF to_number(v_polError) > 0 AND v_polMsgError IS NOT NULL THEN
       v_array_no_enviadas := SPLIT(v_polMsgError,'#');
       IF (v_array_no_enviadas IS NOT NULL AND v_array_no_enviadas.count > 0) THEN
         FOR i IN v_array_no_enviadas.FIRST..v_array_no_enviadas.LAST
         LOOP
             IF v_array_no_enviadas.EXISTS(i) THEN
                 aux_mensaje := aux_mensaje || '   ' || v_array_no_enviadas(i) || '   ' || PQ_ENVIO_CORREOS.v_retorno;
             END IF;
         END LOOP;
      END IF;
  ELSE
      aux_mensaje := aux_mensaje || '  '  || PQ_ENVIO_CORREOS.v_retorno;
  END IF;

     pq_utl.log(lc, 'v_polMarcadasEnviar: ' || v_polMarcadasEnviar, 2);
     pq_utl.log(lc, 'v_polEnviadas: ' || v_polEnviadas, 2);
     pq_utl.log(lc, 'v_polCrm: ' || v_polCrm, 2);
     pq_utl.log(lc, 'v_polCra: ' || v_polCra, 2);

  
  --v_polNoEnviadas := to_number(v_polMarcadasEnviar) - to_number(v_polEnviadas) - to_number(v_polCrm) - to_number(v_polCra);
  v_polNoEnviadas := to_number(v_polMarcadasEnviar) - to_number(v_polEnviadas) - to_number(v_polCra);
  IF (v_polNoEnviadas < 0) THEN
      v_polNoEnviadas := 0;
  END IF;
  aux_mensaje := aux_mensaje || 'Polizas renovables no enviadas a pagos => '|| v_polNoEnviadas || '  '  || PQ_ENVIO_CORREOS.v_retorno;

  v_mensaje_nuevo := str_plz_def_e_msg1 || PQ_ENVIO_CORREOS.v_salto || str_plz_def_e_msg2;
  IF to_number(v_polNoEnviadas) > 0 AND v_mensaje_nuevo IS NOT NULL THEN

       aux_mensaje := aux_mensaje || '  '  || PQ_ENVIO_CORREOS.v_retorno || '  ' || v_mensaje_nuevo || PQ_ENVIO_CORREOS.v_salto;


  ELSE
      aux_mensaje := aux_mensaje || PQ_ENVIO_CORREOS.v_salto || PQ_ENVIO_CORREOS.v_salto;
  END IF;

  aux_mensaje := aux_mensaje || 'Polizas de CR Almendralejo enviadas a pagos => '|| v_polCra || '   ';
  aux_mensaje := aux_mensaje || PQ_ENVIO_CORREOS.v_salto || '---------------------------------' || PQ_ENVIO_CORREOS.v_salto;

  RETURN aux_mensaje;

END getMensajePagosRenovables;

PROCEDURE generaCorreoBorradoPoliza (num_plz_estado_baja IN number,
                                    num_plz_borradas_ok IN number,
                                    num_plz_borradas_ko IN number,
                                    ids_polizas_ko IN VARCHAR2)  IS


  aux_asunto	    varchar2(2000) := 'Alerta Agroplus: Resumen de Borrado de Polizas el dia ';
  aux_fecha       varchar2(20);
  aux_mensaje	    clob;
  lc          VARCHAR2(55) := 'pq_envio_correos.generaCorreoBorradoPoliza';

  BEGIN

  pq_utl.log(lc, '## INI BORRADO POLIZAS ##', 1);
  pq_utl.log(lc, 'Comenzamos con el envio de correo de Borrado de polizas.', 2);

    --Seleccionamos la fecha de hoy para anhadirla al asunto
    select to_char(sysdate, 'DD/MM/YYYY') into aux_fecha from dual;
    aux_asunto := aux_asunto || aux_fecha;

   aux_mensaje := 'Polizas en estado Baja =>     ' || num_plz_estado_baja || '  '  || PQ_ENVIO_CORREOS.v_retorno || '  '  ||
                  'Polizas borradas correctas => ' || num_plz_borradas_ok || '  '  || PQ_ENVIO_CORREOS.v_retorno || '  '  ||
                  'Polizas no borradas =>        ' || num_plz_borradas_ko || '  '  || PQ_ENVIO_CORREOS.v_retorno || '  '  ||
                  'Ids polizas no borradas =>    ' || ids_polizas_ko || '  ' ;

    pq_utl.log(lc, 'Salida correo borrado', 2);
    pq_utl.log(lc, aux_mensaje, 2);
    Pq_Envio_Correos.enviarCorreoGrande(p_codgrupo => 3, p_asunto => aux_asunto, p_mensaje => aux_mensaje);
    pq_utl.log(lc, '## FIN BORRADO POLIZAS ##', 1);


END generaCorreoBorradoPoliza;



FUNCTION SPLIT(in_string VARCHAR2, delim VARCHAR2) RETURN t_array IS
    i       number := 0;
    pos     number := 0;
    lv_str  varchar2(2000) := in_string;
    strings t_array;
  BEGIN
    pos := instr(lv_str, delim, 1, 1);
    IF pos = 0 and lv_str is not null THEN
       strings(1) := lv_str;
    END IF;
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

PROCEDURE generaCorreoResumenSeguimiento(v_num_plz IN number,
                                         v_num_anx IN number,
                                         plzs_nf   IN VARCHAR2,
                                         v_estados_poliza map_estados,
										                     v_estados_anexo map_estados
                                         ) IS
  aux_asunto  varchar2(2000) := 'Alerta Agroplus: Resumen del seguimiento de la contratacion ';
  aux_fecha   varchar2(20);
  aux_mensaje varchar2(32000) := '';
  lc          VARCHAR2(55) := 'pq_envio_correos.generaCorreoResumenSeguimiento';
BEGIN
  pq_utl.log(lc, '## INI ##', 1);
  pq_utl.log(lc,
             'Comenzamos con el envio de correo de seguimiento de la contratacion.',
             2);

  --Seleccionamos la fecha de hoy para anhadirla al asunto
  select to_char(sysdate, 'DD/MM/YYYY') into aux_fecha from dual;
  aux_asunto := aux_asunto || aux_fecha;

  aux_mensaje := 'Polizas actualizadas: ' || v_num_plz ||
                 PQ_ENVIO_CORREOS.v_salto;

  pq_utl.log(lc, 'Polizas actualizadas' || v_num_plz, 2);
  FOR i in (SELECT
			  DISTINCT e.desc_estado AS estado
			FROM o02agpe0.TB_TMP_BATCH_SEGUIMIENTO t
			  JOIN TB_ESTADOS_AGROSEGURO e
				ON t.estado = e.idestado
			  WHERE t.tipo = 1
				AND t.estado IN (
					SELECT DISTINCT s.estado
					FROM o02agpe0.TB_TMP_BATCH_SEGUIMIENTO s
					WHERE s.tipo = 1
				)
			)
  LOOP
	aux_mensaje := aux_mensaje || CHR(9) || i.estado || ': '|| v_estados_poliza(i.estado) || ' ' || PQ_ENVIO_CORREOS.v_salto;

  END LOOP;
  pq_utl.log(lc, '  -: ' || aux_mensaje, 2);

  aux_mensaje := aux_mensaje ||
                 'Anexos de modificacion e Incidencias actualizados: ' ||
                 v_num_anx || ' ' || PQ_ENVIO_CORREOS.v_salto;
  pq_utl.log(lc, 'Anexos de modificacion e Incidencias actualizados: ' || v_num_anx, 2);

  FOR i in (SELECT
			  DISTINCT t.detalle AS estado
			FROM o02agpe0.TB_TMP_BATCH_SEGUIMIENTO t
			WHERE t.tipo = 2
			  AND t.estado IN (
					SELECT DISTINCT s.estado
					FROM o02agpe0.TB_TMP_BATCH_SEGUIMIENTO s
					WHERE s.tipo = 2)
			)
  LOOP
	aux_mensaje := aux_mensaje || CHR(9) || i.estado || ': '|| v_estados_anexo(i.estado) || ' ' || PQ_ENVIO_CORREOS.v_salto;
  pq_utl.log(lc, '  -: ' || i.estado || ':'|| v_estados_anexo(i.estado), 2);
  END LOOP;

  aux_mensaje := aux_mensaje || 'Polizas no encontradas: ' ||
                 PQ_ENVIO_CORREOS.v_salto || plzs_nf;

	PQ_UTL.LOG(lc, 'Polizas no encontradas: ' || plzs_nf, 2);

  --Concatenamos al mensaje un separador
  aux_mensaje := aux_mensaje || ' ' || PQ_ENVIO_CORREOS.v_salto ||
                 '---------------------------------' ||
                 PQ_ENVIO_CORREOS.v_salto;

  pq_utl.log(lc, 'Antes de enviar_correo.', 2);
  Pq_Envio_Correos.enviarCorreo(p_codgrupo => 1,
                                p_asunto   => aux_asunto,
                                p_mensaje  => aux_mensaje);
END;

PROCEDURE generaCorreoSeguimientoTomador(v_cif     o02agpe0.TB_TOMADORES.ciftomador%TYPE,
										 v_eMail   o02agpe0.TB_TOMADORES.email%TYPE,
                                         v_plzs    VARCHAR2,
                                         v_anexos  VARCHAR2,
                                         v_plzs_sg VARCHAR2) IS
  aux_asunto  varchar2(2000) := 'Alerta Agroplus: Resumen del seguimiento de la contratacion ';
  aux_mensaje varchar2(32000) := '';
  lc          VARCHAR2(55) := 'pq_envio_correos.generaCorreoSeguimientoTomador';
BEGIN
  pq_utl.log(lc, '## INI ##', 1);
  pq_utl.log(lc,
             'Comenzamos con el envio de correo de seguimiento de la contratacion para Tomador.',
             2);

  IF v_plzs IS NOT NULL THEN
	aux_mensaje := aux_mensaje || PQ_ENVIO_CORREOS.v_salto ||
				   'Polizas: ' || '  '  || PQ_ENVIO_CORREOS.v_retorno || v_plzs;
  END IF;

  IF v_anexos IS NOT NULL THEN
	aux_mensaje := aux_mensaje || o02agpe0.PQ_ENVIO_CORREOS.v_salto ||
				   'Anexos de modificacion e incidencias: ' || '  '  || o02agpe0.PQ_ENVIO_CORREOS.v_retorno || v_anexos;
  END IF;

  IF v_plzs_sg IS NOT NULL THEN
	aux_mensaje := aux_mensaje || PQ_ENVIO_CORREOS.v_salto ||
				   'Polizas con suspension de garantias: ' || '  '  || PQ_ENVIO_CORREOS.v_retorno || v_plzs_sg;
  END IF;

  --Concatenamos al mensaje un separador
  aux_mensaje := aux_mensaje || '  '  || PQ_ENVIO_CORREOS.v_retorno ||
                 '---------------------------------' ||
                 PQ_ENVIO_CORREOS.v_salto;

  IF LENGTH(aux_mensaje) < 4000 THEN
	INSERT INTO o02agpe0.TB_CORREOS_TOMADORES VALUES (v_cif, SYSDATE, aux_mensaje);
  ELSE
	INSERT INTO o02agpe0.TB_CORREOS_TOMADORES VALUES (v_cif, SYSDATE, SUBSTR(aux_mensaje, 0, 3950) || '[...]');
  END IF;
  
  COMMIT;

  pq_utl.log(lc, 'Antes de enviar_correo.', 2);
  Pq_Envio_Correos.enviarCorreoUnico(v_eMail,
									 p_asunto => aux_asunto,
									 p_mensaje => aux_mensaje);
END;

PROCEDURE generaCorreoResumenCargaCondRec(p_fecha IN VARCHAR2) IS

	lc          VARCHAR2(55) := 'pq_envio_correos.generaCorreoResumenCargaCondRec';

	aux_asunto  varchar2(2000) := 'Alerta Agroplus: Resumen carga de condicionados y recibos del dia ';
	aux_fecha   varchar2(20);
	aux_mensaje varchar2(32000) := '';

	v_cond_proc number(5) := 0;
	v_cond_ok 	number(5) := 0;
	v_cond_ko 	number(5) := 0;
	v_rec_proc  number(5) := 0;
	v_rec_ok 	number(5) := 0;
	v_rec_ko 	number(5) := 0;

	cond_error_msg varchar2(2200);
	rec_error_msg  varchar2(2200);

	v_aux pq_envio_correos.t_array;
	
	v_fecha_planif DATE;
	
BEGIN
	pq_utl.log(lc, '## INI ##', 1);
	pq_utl.log(lc, 'Comenzamos con el envio de correo de resumen de carga de condicionados y recibos.', 2);
	
	pq_utl.log(lc, 'Fecha de planificacion recibida por parametro:' || p_fecha);
	
		if p_fecha = '' then
			v_fecha_planif := sysdate;
		else
			-- Convertimos a DATE la fecha de planificacion recibida por parametro
			v_fecha_planif := TO_DATE(p_fecha,'YYYYMMDD');
		end if;
	
	select to_char(v_fecha_planif, 'DD/MM/YYYY') into aux_fecha from dual;
    aux_asunto := aux_asunto || aux_fecha;

	v_cond_proc := PQ_Utl.getcfg ('CARGA_COND_OK');
	cond_error_msg := PQ_Utl.getcfg ('CARGA_COND_KO_LST');
	v_rec_proc := PQ_Utl.getcfg ('CARGA_REC_OK');
	rec_error_msg := PQ_Utl.getcfg ('CARGA_REC_KO_LST');

	IF TRIM(cond_error_msg) IS NOT NULL THEN
		v_aux := pq_envio_correos.split(cond_error_msg, ', ');
		v_cond_ko := v_aux.count;
	END IF;
	v_cond_ok := v_cond_proc - v_cond_ko;

	IF TRIM(rec_error_msg) IS NOT NULL THEN
		v_aux := pq_envio_correos.split(rec_error_msg, ', ');
		v_rec_ko := v_aux.count;
	END IF;
	v_rec_ok := v_rec_proc - v_rec_ko;

	aux_mensaje := 'Condicionados Tratados: ' || v_cond_proc || '  ' || pq_envio_correos.v_retorno || '  '  ||
                 'Condicionados cargados correctamente: ' || v_cond_ok || '  '  || pq_envio_correos.v_retorno || '  '  ||
                 'Condicionados sin cargar por error: ' || v_cond_ko || '  '  || pq_envio_correos.v_retorno || '    ' ||
				 cond_error_msg || '  ' || pq_envio_correos.v_salto ||
				 'Recibos Tratados: ' || v_rec_proc || '  ' || pq_envio_correos.v_retorno || '  '  ||
                 'Recibos cargados correctamente: ' || v_rec_ok || '  '  || pq_envio_correos.v_retorno || '  '  ||
                 'Recibos sin cargar por error: ' || v_rec_ko || '  '  || pq_envio_correos.v_retorno || '    ' ||
				 rec_error_msg || '  ' || pq_envio_correos.v_salto || '  ';

	pq_utl.log(lc, 'Mensaje generado:', 2);
	pq_utl.log(lc, aux_mensaje, 2);

	pq_utl.log(lc, 'Antes de enviar_correo.', 2);
	Pq_Envio_Correos.enviarCorreo(p_codgrupo => 1,
                                  p_asunto   => aux_asunto,
                                  p_mensaje  => aux_mensaje);
END;

PROCEDURE enviarCorreoConfigurable(p_host in varchar2, p_helo in varchar2, p_emisor	in varchar2, p_from	in varchar2, p_destinatario	in varchar2,
	p_asunto in varchar2, p_mensaje in varchar2)
IS
	--Variables para la conexion con el servidor de correo
	c utl_smtp.connection;

BEGIN
	-- creamos la conexion al host y puerto de conexion
	c := utl_smtp.open_connection(p_host, 25);

	--dominio deberia ser algo como smtp.cajarural.com, o correointerno, en lugar de una direccion de correo.
	utl_smtp.helo(c, p_helo);

	--emisor
	utl_smtp.mail(c, p_emisor);
	--destinatario
	utl_smtp.rcpt(c, p_destinatario);

	--cosas interesantes que podriamos probar:
	--UTL_SMTP.write_data(l_mail_conn, 'Content-Type: text/plain; charset="iso-8859-1"' || UTL_TCP.crlf || UTL_TCP.crlf);

	--Mensaje
	utl_smtp.open_data(c);
	utl_smtp.write_data(c, 'From: ' || p_from || utl_tcp.CRLF); -- tal vez se podria cambiar esto por una direccion de correo a ver que llega
	utl_smtp.write_data(c, 'Subject: ' || p_asunto || utl_tcp.CRLF);
	utl_smtp.write_data(c, '' || utl_tcp.CRLF); -- estno no se para que se utiliza
	utl_smtp.write_data(c, p_mensaje || utl_tcp.CRLF);
	utl_smtp.write_data(c, 'Para cualquier problema ponerse en contacto con: incidenciasagrarios@segurosrga.es.' || utl_tcp.CRLF);
	utl_smtp.close_data(c);

	utl_smtp.quit(c);
END;

END PQ_ENVIO_CORREOS;
/
SHOW ERRORS;