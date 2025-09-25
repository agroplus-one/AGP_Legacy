SET DEFINE OFF;
SET SERVEROUTPUT ON;

CREATE OR REPLACE PACKAGE O02AGPE0.PQ_TRATAR_POLIZAS_RECHAZADAS IS
/******************************************************************************
   NAME:       PQ_TRATAR_POLIZAS_RECHAZADAS
   PURPOSE:    Package para tratar las pólizas rechazadas por agroseguro
   	           y enviar e-mails informando a las entidades

   REVISIONS:
   Ver        Date        Author           Description
   ---------  -----------  ---------------  ------------------------------------
   1.0        20/05/2011  T-SYSTEMS        1. Created this package.

   NOTES:

******************************************************************************/

--POLIZAS
PROCEDURE GENERA_CORREOS_RECHAZO
(
  p_idenvio	in NUMBER
);

PROCEDURE GENERA_CORREOS_ACEPTACION
(
  p_idenvio	in NUMBER
);

--SINIESTROS
PROCEDURE GENERA_CORREOS_RECHAZO_STR
(
  p_idenvio	in NUMBER
);

PROCEDURE GENERA_CORREOS_ACEPTACION_STR
(
  p_idenvio	in NUMBER
);

--ANEXOS DE MODIFICACIÓN
PROCEDURE GENERA_CORREOS_RECHAZO_MOD
(
  p_idenvio	in NUMBER
);

PROCEDURE GENERA_CORREOS_ACEPTACION_MOD
(
  p_idenvio	in NUMBER
);

--ANEXOS DE REDUCCION
PROCEDURE GENERA_CORREOS_RECHAZO_RED
(
  p_idenvio	in NUMBER
);

PROCEDURE GENERA_CORREOS_ACEPTACION_RED
(
  p_idenvio	in NUMBER
);

END PQ_TRATAR_POLIZAS_RECHAZADAS;
/
CREATE OR REPLACE PACKAGE BODY O02AGPE0.PQ_TRATAR_POLIZAS_RECHAZADAS AS

PROCEDURE GENERA_CORREOS_RECHAZO
(
  p_idenvio	in NUMBER
)
IS
	--Variables auxiliares
	aux_query	varchar2(2000);
	aux_codent_ant	TB_ENTIDADES.CODENTIDAD%TYPE := null;

	aux_codentidad	TB_ENTIDADES.CODENTIDAD%TYPE;
	aux_nif		TB_ASEGURADOS.NIFCIF%TYPE;
	aux_referencia	TB_POLIZAS.REFERENCIA%TYPE;
	aux_dc		TB_POLIZAS.DC%TYPE;
	--aux_xml		TB_POLIZAS.XMLACUSECONTRATACION%TYPE;

	aux_plan	TB_LINEAS.CODPLAN%TYPE;
	aux_linea	TB_LINEAS.CODLINEA%TYPE;

	TYPE TpCursor	IS REF CURSOR;
	aux_cursor	TpCursor;
	aux_cur_err	TpCursor;

	aux_mensaje	varchar2(2000) := '';
	aux_asunto	varchar2(2000) := 'Alerta Agroplus: Rechazo de pólizas por parte de Agroseguro';
	aux_grp_correo	TB_GRUPO_ENTIDADES.ID%TYPE;

	str_cod		varchar2(2000);
	str_desc	varchar2(2000);

BEGIN
	-- 1. Consultamos las pólizas del envio que han sido rechazadas agrupadas por CODENTIDAD.
	aux_query := 'SELECT C.CODENTIDAD, A.NIFCIF, P.REFERENCIA, P.DC, L.CODPLAN, L.CODLINEA
		FROM TB_POLIZAS P, TB_COLECTIVOS C, TB_ASEGURADOS A, TB_LINEAS L
		WHERE P.IDCOLECTIVO = C.ID AND P.IDASEGURADO = A.ID AND P.LINEASEGUROID = L.LINEASEGUROID
		AND P.IDESTADO = 7 AND P.IDENVIO = ' || p_idenvio
		 || ' ORDER BY C.CODENTIDAD';

	-- 2. Recorremos las pólizas y vamos montando el cuerpo del email (por cada entidad)
	OPEN aux_cursor FOR aux_query;

	FETCH aux_cursor INTO aux_codentidad, aux_nif, aux_referencia, aux_dc, aux_plan, aux_linea;
	LOOP
	EXIT WHEN aux_cursor%NOTFOUND;
		BEGIN
			IF (aux_codent_ant IS NULL OR aux_codent_ant != aux_codentidad) THEN
				IF (aux_codent_ant IS NOT NULL) THEN
					aux_mensaje := aux_mensaje || 'Por favor, no responda a este mensaje.';
					SELECT IDGRUPOCORREO INTO aux_grp_correo FROM TB_ENTIDADES WHERE CODENTIDAD = aux_codent_ant;
					PQ_ENVIO_CORREOS.enviarCorreo(aux_grp_correo, aux_asunto, aux_mensaje);
				END IF;
				aux_codent_ant := aux_codentidad;
				--Limpiamos las variables para los emails.
				aux_mensaje := 'Este es un mensaje generado automáticamente para notificarle los errores ' ||
					'detectados en Agroseguro durante el proceso de envío de pólizas.

';
				aux_grp_correo := null;
			END IF;

			OPEN aux_cur_err FOR SELECT extractvalue(value(err), '//@codigo'), extractvalue(value(err), '//@descripcion')
			FROM tb_comunicaciones c,
				TABLE (xmlsequence(extract(xmltype(c.fichero_contenido),'/xml-fragment/acus:Documento', 'xmlns:acus=http://www.agroseguro.es/AcuseRecibo'))) docs,
				TABLE (xmlsequence(extract(value(docs),'/acus:Documento/acus:Error', 'xmlns:acus=http://www.agroseguro.es/AcuseRecibo'))) err
			WHERE extractvalue(value(docs), '//@id') = aux_referencia || aux_plan || aux_linea
				AND C.IDENVIO = p_idenvio;

			-- Montamos el mensaje
			-- En el correo indicaremos REFERENCIA, NIF/CIF
			aux_mensaje := aux_mensaje || aux_referencia || aux_dc || ' - ' || aux_nif || '

';
			--  y LISTA DE ERRORES DEVUELTOS. En el xml viene identificado por REFERENCIA + PLAN + LINEA
			FETCH aux_cur_err INTO str_cod, str_desc;
			LOOP
				EXIT WHEN aux_cur_err%NOTFOUND;
        -- Por cada error, ponemos su código y su descripción
				aux_mensaje := aux_mensaje || '          * ' || str_cod || ' - ' || str_desc || '
';

				FETCH aux_cur_err INTO str_cod, str_desc;
			END LOOP;
			CLOSE aux_cur_err;

      aux_mensaje := aux_mensaje || '


';

      dbms_output.put_line(aux_mensaje);
      dbms_output.put_line(aux_asunto);

		EXCEPTION
			WHEN OTHERS THEN
				dbms_output.put_line(sqlerrm);
		END;

    --Reseteo las variables antes de volver a volcar los datos del cursor
    aux_codentidad := null;

		FETCH aux_cursor INTO aux_codentidad, aux_nif, aux_referencia, aux_dc, aux_plan, aux_linea;
    IF (aux_codentidad IS NULL OR aux_codent_ant != aux_codentidad) THEN
					aux_mensaje := aux_mensaje || 'Por favor, no responda a este mensaje.';
					SELECT IDGRUPOCORREO INTO aux_grp_correo FROM TB_ENTIDADES WHERE CODENTIDAD = aux_codent_ant;
					PQ_ENVIO_CORREOS.enviarCorreo(aux_grp_correo, aux_asunto, aux_mensaje);
		END IF;
	END LOOP;
	CLOSE aux_cursor;

	-- 3. Consultamos el grupo de la entidad y le enviamos el correo al grupo y a RGA
END;

PROCEDURE GENERA_CORREOS_ACEPTACION
(
  p_idenvio	in NUMBER
)
IS
	--Variables auxiliares
	aux_query	varchar2(2000);
	aux_codent_ant	TB_ENTIDADES.CODENTIDAD%TYPE := null;

	aux_codentidad	TB_ENTIDADES.CODENTIDAD%TYPE;
	aux_nif		TB_ASEGURADOS.NIFCIF%TYPE;
	aux_referencia	TB_POLIZAS.REFERENCIA%TYPE;
	aux_dc		TB_POLIZAS.DC%TYPE;
	--aux_xml		TB_POLIZAS.XMLACUSECONTRATACION%TYPE;

	aux_plan	TB_LINEAS.CODPLAN%TYPE;
	aux_linea	TB_LINEAS.CODLINEA%TYPE;

	TYPE TpCursor	IS REF CURSOR;
	aux_cursor	TpCursor;

	aux_mensaje	varchar2(2000) := '';
	aux_asunto	varchar2(2000) := 'Alerta Agroplus: Aceptación de pólizas por parte de Agroseguro';
	aux_grp_correo	TB_GRUPO_ENTIDADES.ID%TYPE;

BEGIN
	-- 1. Consultamos las pólizas del envio que han sido rechazadas agrupadas por CODENTIDAD.
	aux_query := 'SELECT C.CODENTIDAD, A.NIFCIF, P.REFERENCIA, P.DC, L.CODPLAN, L.CODLINEA
		FROM TB_POLIZAS P, TB_COLECTIVOS C, TB_ASEGURADOS A, TB_LINEAS L
		WHERE P.IDCOLECTIVO = C.ID AND P.IDASEGURADO = A.ID AND P.LINEASEGUROID = L.LINEASEGUROID
		AND P.IDESTADO = 8 AND P.IDENVIO = ' || p_idenvio
		 || ' ORDER BY C.CODENTIDAD';

	-- 2. Recorremos las pólizas y vamos montando el cuerpo del email (por cada entidad)
	OPEN aux_cursor FOR aux_query;

	FETCH aux_cursor INTO aux_codentidad, aux_nif, aux_referencia, aux_dc, aux_plan, aux_linea;
	LOOP
	EXIT WHEN aux_cursor%NOTFOUND;
		BEGIN
			IF (aux_codent_ant IS NULL OR aux_codent_ant != aux_codentidad) THEN
				IF (aux_codent_ant IS NOT NULL) THEN
					aux_mensaje := aux_mensaje || 'Por favor, no responda a este mensaje.';
					SELECT IDGRUPOCORREO INTO aux_grp_correo FROM TB_ENTIDADES WHERE CODENTIDAD = aux_codent_ant;
					PQ_ENVIO_CORREOS.enviarCorreo(aux_grp_correo, aux_asunto, aux_mensaje);
				END IF;
				aux_codent_ant := aux_codentidad;
				--Limpiamos las variables para los emails.
				aux_mensaje := 'Este es un mensaje generado automáticamente para notificarle el resultado ' ||
					'de los envios a Agroseguro.

';
				aux_grp_correo := null;
			END IF;

			-- Montamos el mensaje
			-- En el correo indicaremos REFERENCIA, NIF/CIF
			aux_mensaje := aux_mensaje || aux_referencia || aux_dc || ' - ' || aux_nif || '

';
      aux_mensaje := aux_mensaje || '


';

      dbms_output.put_line(aux_mensaje);
      dbms_output.put_line(aux_asunto);

		EXCEPTION
			WHEN OTHERS THEN
				dbms_output.put_line(sqlerrm);
		END;

    --Reseteo las variables antes de volver a volcar los datos del cursor
    aux_codentidad := null;

		FETCH aux_cursor INTO aux_codentidad, aux_nif, aux_referencia, aux_dc, aux_plan, aux_linea;
    IF (aux_codentidad IS NULL OR aux_codent_ant != aux_codentidad) THEN
					aux_mensaje := aux_mensaje || 'Por favor, no responda a este mensaje.';
					SELECT IDGRUPOCORREO INTO aux_grp_correo FROM TB_ENTIDADES WHERE CODENTIDAD = aux_codent_ant;
					PQ_ENVIO_CORREOS.enviarCorreo(aux_grp_correo, aux_asunto, aux_mensaje);
		END IF;
	END LOOP;
	CLOSE aux_cursor;

	-- 3. Consultamos el grupo de la entidad y le enviamos el correo al grupo y a RGA
END;

PROCEDURE GENERA_CORREOS_RECHAZO_STR
(
  p_idenvio	in NUMBER
)
IS
	--Variables auxiliares
	aux_query	varchar2(2000);
	aux_codent_ant	TB_ENTIDADES.CODENTIDAD%TYPE := null;

	aux_codentidad	TB_ENTIDADES.CODENTIDAD%TYPE;
	aux_nif		      TB_ASEGURADOS.NIFCIF%TYPE;
	aux_referencia	TB_POLIZAS.REFERENCIA%TYPE;
	aux_dc		      TB_POLIZAS.DC%TYPE;
	aux_plan	      TB_LINEAS.CODPLAN%TYPE;
	aux_linea	      TB_LINEAS.CODLINEA%TYPE;
  aux_numinterno  TB_SINIESTROS.NUMINTERNOENVIO%TYPE;

	TYPE TpCursor	IS REF CURSOR;
	aux_cursor	TpCursor;
	aux_cur_err	TpCursor;

	aux_mensaje	varchar2(2000) := '';
	aux_asunto	varchar2(2000) := 'Alerta Agroplus: Rechazo de siniestros por parte de Agroseguro';
	aux_grp_correo	TB_GRUPO_ENTIDADES.ID%TYPE;

	str_cod		varchar2(2000);
	str_desc	varchar2(2000);

BEGIN
	-- 1. Consultamos las pólizas del envio que han sido rechazadas agrupadas por CODENTIDAD.
	aux_query := 'SELECT C.CODENTIDAD, A.NIFCIF, P.REFERENCIA, P.DC, L.CODPLAN, L.CODLINEA, S.Numinternoenvio
		FROM TB_POLIZAS P, TB_SINIESTROS S, TB_COLECTIVOS C, TB_ASEGURADOS A, TB_LINEAS L
		WHERE s.idpoliza = p.idpoliza and P.IDCOLECTIVO = C.ID AND P.IDASEGURADO = A.ID
    AND P.LINEASEGUROID = L.LINEASEGUROID	AND S.IDESTADO = 4 AND S.IDENVIO = ' || p_idenvio
		 || ' ORDER BY C.CODENTIDAD';

	-- 2. Recorremos las pólizas y vamos montando el cuerpo del email (por cada entidad)
	OPEN aux_cursor FOR aux_query;

	FETCH aux_cursor INTO aux_codentidad, aux_nif, aux_referencia, aux_dc, aux_plan, aux_linea, aux_numinterno;
	LOOP
	EXIT WHEN aux_cursor%NOTFOUND;
		BEGIN
			IF (aux_codent_ant IS NULL OR aux_codent_ant != aux_codentidad) THEN
				IF (aux_codent_ant IS NOT NULL) THEN
					aux_mensaje := aux_mensaje || 'Por favor, no responda a este mensaje.';
					SELECT IDGRUPOCORREO INTO aux_grp_correo FROM TB_ENTIDADES WHERE CODENTIDAD = aux_codent_ant;
					PQ_ENVIO_CORREOS.enviarCorreo(aux_grp_correo, aux_asunto, aux_mensaje);
				END IF;
				aux_codent_ant := aux_codentidad;
				--Limpiamos las variables para los emails.
				aux_mensaje := 'Este es un mensaje generado automáticamente para notificarle los errores ' ||
					'detectados en Agroseguro durante el proceso de envío de siniestros.

';
				aux_grp_correo := null;
			END IF;

			OPEN aux_cur_err FOR SELECT extractvalue(value(err), '//@codigo'), extractvalue(value(err), '//@descripcion')
			FROM tb_comunicaciones c,
				TABLE (xmlsequence(extract(xmltype(c.fichero_contenido),'/xml-fragment/acus:Documento', 'xmlns:acus=http://www.agroseguro.es/AcuseRecibo'))) docs,
				TABLE (xmlsequence(extract(value(docs),'/acus:Documento/acus:Error', 'xmlns:acus=http://www.agroseguro.es/AcuseRecibo'))) err
			WHERE extractvalue(value(docs), '//@id') = lpad(aux_numinterno, 6, '0')
				AND C.IDENVIO = p_idenvio;

			-- Montamos el mensaje
			-- En el correo indicaremos REFERENCIA, NIF/CIF
			aux_mensaje := aux_mensaje || aux_referencia || '-' || aux_dc || ' -- ' || aux_nif || '

';
			--  y LISTA DE ERRORES DEVUELTOS. En el xml viene identificado por REFERENCIA + PLAN + LINEA
			FETCH aux_cur_err INTO str_cod, str_desc;
			LOOP
				EXIT WHEN aux_cur_err%NOTFOUND;
        -- Por cada error, ponemos su código y su descripción
				aux_mensaje := aux_mensaje || '          * ' || str_cod || ' - ' || str_desc || '
';

				FETCH aux_cur_err INTO str_cod, str_desc;
			END LOOP;
			CLOSE aux_cur_err;

      aux_mensaje := aux_mensaje || '


';

      dbms_output.put_line(aux_mensaje);
      dbms_output.put_line(aux_asunto);

		EXCEPTION
			WHEN OTHERS THEN
				dbms_output.put_line(sqlerrm);
		END;

    --Reseteo las variables antes de volver a volcar los datos del cursor
    aux_codentidad := null;

		FETCH aux_cursor INTO aux_codentidad, aux_nif, aux_referencia, aux_dc, aux_plan, aux_linea, aux_numinterno;
    IF (aux_codentidad IS NULL OR aux_codent_ant != aux_codentidad) THEN
					aux_mensaje := aux_mensaje || 'Por favor, no responda a este mensaje.';
					SELECT IDGRUPOCORREO INTO aux_grp_correo FROM TB_ENTIDADES WHERE CODENTIDAD = aux_codent_ant;
					PQ_ENVIO_CORREOS.enviarCorreo(aux_grp_correo, aux_asunto, aux_mensaje);
		END IF;
	END LOOP;
	CLOSE aux_cursor;

	-- 3. Consultamos el grupo de la entidad y le enviamos el correo al grupo y a RGA
END;

PROCEDURE GENERA_CORREOS_ACEPTACION_STR
(
  p_idenvio	in NUMBER
)
IS
	--Variables auxiliares
	aux_query	varchar2(2000);
	aux_codent_ant	TB_ENTIDADES.CODENTIDAD%TYPE := null;

	aux_codentidad	TB_ENTIDADES.CODENTIDAD%TYPE;
	aux_nif		      TB_ASEGURADOS.NIFCIF%TYPE;
	aux_referencia	TB_POLIZAS.REFERENCIA%TYPE;
	aux_dc		      TB_POLIZAS.DC%TYPE;
	aux_plan	      TB_LINEAS.CODPLAN%TYPE;
	aux_linea	      TB_LINEAS.CODLINEA%TYPE;
  aux_numinterno  TB_SINIESTROS.NUMINTERNOENVIO%TYPE;

	TYPE TpCursor	IS REF CURSOR;
	aux_cursor	TpCursor;

	aux_mensaje	varchar2(2000) := '';
	aux_asunto	varchar2(2000) := 'Alerta Agroplus: Aceptación de siniestros por parte de Agroseguro';
	aux_grp_correo	TB_GRUPO_ENTIDADES.ID%TYPE;

BEGIN
	-- 1. Consultamos las pólizas del envio que han sido rechazadas agrupadas por CODENTIDAD.
	aux_query := 'SELECT C.CODENTIDAD, A.NIFCIF, P.REFERENCIA, P.DC, L.CODPLAN, L.CODLINEA, S.Numinternoenvio
		FROM TB_POLIZAS P, TB_SINIESTROS S, TB_COLECTIVOS C, TB_ASEGURADOS A, TB_LINEAS L
		WHERE s.idpoliza = p.idpoliza and P.IDCOLECTIVO = C.ID AND P.IDASEGURADO = A.ID
    AND P.LINEASEGUROID = L.LINEASEGUROID	AND S.ESTADO = 3 AND P.IDENVIO = ' || p_idenvio
		 || ' ORDER BY C.CODENTIDAD';

	-- 2. Recorremos las pólizas y vamos montando el cuerpo del email (por cada entidad)
	OPEN aux_cursor FOR aux_query;

	FETCH aux_cursor INTO aux_codentidad, aux_nif, aux_referencia, aux_dc, aux_plan, aux_linea, aux_numinterno;
	LOOP
	EXIT WHEN aux_cursor%NOTFOUND;
		BEGIN
			IF (aux_codent_ant IS NULL OR aux_codent_ant != aux_codentidad) THEN
				IF (aux_codent_ant IS NOT NULL) THEN
					aux_mensaje := aux_mensaje || 'Por favor, no responda a este mensaje.';
					SELECT IDGRUPOCORREO INTO aux_grp_correo FROM TB_ENTIDADES WHERE CODENTIDAD = aux_codent_ant;
					PQ_ENVIO_CORREOS.enviarCorreo(aux_grp_correo, aux_asunto, aux_mensaje);
				END IF;
				aux_codent_ant := aux_codentidad;
				--Limpiamos las variables para los emails.
				aux_mensaje := 'Este es un mensaje generado automáticamente para notificarle los siniestros ' ||
					'aceptados por Agroseguro durante el proceso de envío.

';
				aux_grp_correo := null;
			END IF;

			-- Montamos el mensaje
			-- En el correo indicaremos REFERENCIA, NIF/CIF
			aux_mensaje := aux_mensaje || aux_referencia || '-' || aux_dc || ' -- ' || aux_nif || '



';

      dbms_output.put_line(aux_mensaje);
      dbms_output.put_line(aux_asunto);

		EXCEPTION
			WHEN OTHERS THEN
				dbms_output.put_line(sqlerrm);
		END;

    --Reseteo las variables antes de volver a volcar los datos del cursor
    aux_codentidad := null;

		FETCH aux_cursor INTO aux_codentidad, aux_nif, aux_referencia, aux_dc, aux_plan, aux_linea, aux_numinterno;
    IF (aux_codentidad IS NULL OR aux_codent_ant != aux_codentidad) THEN
					aux_mensaje := aux_mensaje || 'Por favor, no responda a este mensaje.';
					SELECT IDGRUPOCORREO INTO aux_grp_correo FROM TB_ENTIDADES WHERE CODENTIDAD = aux_codent_ant;
					PQ_ENVIO_CORREOS.enviarCorreo(aux_grp_correo, aux_asunto, aux_mensaje);
		END IF;
	END LOOP;
	CLOSE aux_cursor;

	-- 3. Consultamos el grupo de la entidad y le enviamos el correo al grupo y a RGA
END;

PROCEDURE GENERA_CORREOS_RECHAZO_MOD
(
  p_idenvio	in NUMBER
)
IS
	--Variables auxiliares
	aux_query	varchar2(2000);
	aux_codent_ant	TB_ENTIDADES.CODENTIDAD%TYPE := null;

	aux_codentidad	TB_ENTIDADES.CODENTIDAD%TYPE;
	aux_nif		TB_ASEGURADOS.NIFCIF%TYPE;
	aux_referencia	TB_POLIZAS.REFERENCIA%TYPE;
	aux_dc		TB_POLIZAS.DC%TYPE;
	aux_plan	TB_LINEAS.CODPLAN%TYPE;
	aux_linea	TB_LINEAS.CODLINEA%TYPE;

	TYPE TpCursor	IS REF CURSOR;
	aux_cursor	TpCursor;
	aux_cur_err	TpCursor;

	aux_mensaje	varchar2(2000) := '';
	aux_asunto	varchar2(2000) := 'Alerta Agroplus: Rechazo de anexos de modificación por parte de Agroseguro';
	aux_grp_correo	TB_GRUPO_ENTIDADES.ID%TYPE;

	str_cod		varchar2(2000);
	str_desc	varchar2(2000);

BEGIN
	-- 1. Consultamos las pólizas del envio que han sido rechazadas agrupadas por CODENTIDAD.
	aux_query := 'SELECT C.CODENTIDAD, A.NIFCIF, P.REFERENCIA, P.DC, L.CODPLAN, L.CODLINEA
		FROM TB_POLIZAS P, TB_ANEXO_MOD M, TB_COLECTIVOS C, TB_ASEGURADOS A, TB_LINEAS L
		WHERE M.IDPOLIZA = P.IDPOLIZA AND P.IDCOLECTIVO = C.ID AND P.IDASEGURADO = A.ID
    AND P.LINEASEGUROID = L.LINEASEGUROID	AND M.ESTADO = 4 AND M.IDENVIO = ' || p_idenvio
		 || ' ORDER BY C.CODENTIDAD';

	-- 2. Recorremos las pólizas y vamos montando el cuerpo del email (por cada entidad)
	OPEN aux_cursor FOR aux_query;

	FETCH aux_cursor INTO aux_codentidad, aux_nif, aux_referencia, aux_dc, aux_plan, aux_linea;
	LOOP
	EXIT WHEN aux_cursor%NOTFOUND;
		BEGIN
			IF (aux_codent_ant IS NULL OR aux_codent_ant != aux_codentidad) THEN
				IF (aux_codent_ant IS NOT NULL) THEN
					aux_mensaje := aux_mensaje || 'Por favor, no responda a este mensaje.';
					SELECT IDGRUPOCORREO INTO aux_grp_correo FROM TB_ENTIDADES WHERE CODENTIDAD = aux_codent_ant;
					PQ_ENVIO_CORREOS.enviarCorreo(aux_grp_correo, aux_asunto, aux_mensaje);
				END IF;
				aux_codent_ant := aux_codentidad;
				--Limpiamos las variables para los emails.
				aux_mensaje := 'Este es un mensaje generado automáticamente para notificarle los errores ' ||
					'detectados en Agroseguro durante el proceso de envío de anexos de modificación.

';
				aux_grp_correo := null;
			END IF;

			OPEN aux_cur_err FOR SELECT extractvalue(value(err), '//@codigo'), extractvalue(value(err), '//@descripcion')
			FROM tb_comunicaciones c,
				TABLE (xmlsequence(extract(xmltype(c.fichero_contenido),'/xml-fragment/acus:Documento', 'xmlns:acus=http://www.agroseguro.es/AcuseRecibo'))) docs,
				TABLE (xmlsequence(extract(value(docs),'/acus:Documento/acus:Error', 'xmlns:acus=http://www.agroseguro.es/AcuseRecibo'))) err
			WHERE extractvalue(value(docs), '//@id') = aux_referencia || aux_plan || aux_linea
				AND C.IDENVIO = p_idenvio;

			-- Montamos el mensaje
			-- En el correo indicaremos REFERENCIA, NIF/CIF
			aux_mensaje := aux_mensaje || aux_referencia || aux_dc || ' - ' || aux_nif || '

';
			--  y LISTA DE ERRORES DEVUELTOS. En el xml viene identificado por REFERENCIA + PLAN + LINEA
			FETCH aux_cur_err INTO str_cod, str_desc;
			LOOP
				EXIT WHEN aux_cur_err%NOTFOUND;
        -- Por cada error, ponemos su código y su descripción
				aux_mensaje := aux_mensaje || '          * ' || str_cod || ' - ' || str_desc || '
';

				FETCH aux_cur_err INTO str_cod, str_desc;
			END LOOP;
			CLOSE aux_cur_err;

      aux_mensaje := aux_mensaje || '


';

      dbms_output.put_line(aux_mensaje);
      dbms_output.put_line(aux_asunto);

		EXCEPTION
			WHEN OTHERS THEN
				dbms_output.put_line(sqlerrm);
		END;

    --Reseteo las variables antes de volver a volcar los datos del cursor
    aux_codentidad := null;

		FETCH aux_cursor INTO aux_codentidad, aux_nif, aux_referencia, aux_dc, aux_plan, aux_linea;
    IF (aux_codentidad IS NULL OR aux_codent_ant != aux_codentidad) THEN
					aux_mensaje := aux_mensaje || 'Por favor, no responda a este mensaje.';
					SELECT IDGRUPOCORREO INTO aux_grp_correo FROM TB_ENTIDADES WHERE CODENTIDAD = aux_codent_ant;
					PQ_ENVIO_CORREOS.enviarCorreo(aux_grp_correo, aux_asunto, aux_mensaje);
		END IF;
	END LOOP;
	CLOSE aux_cursor;

	-- 3. Consultamos el grupo de la entidad y le enviamos el correo al grupo y a RGA
END;

PROCEDURE GENERA_CORREOS_ACEPTACION_MOD
(
  p_idenvio	in NUMBER
)
IS
	--Variables auxiliares
	aux_query	varchar2(2000);
	aux_codent_ant	TB_ENTIDADES.CODENTIDAD%TYPE := null;

	aux_codentidad	TB_ENTIDADES.CODENTIDAD%TYPE;
	aux_nif		TB_ASEGURADOS.NIFCIF%TYPE;
	aux_referencia	TB_POLIZAS.REFERENCIA%TYPE;
	aux_dc		TB_POLIZAS.DC%TYPE;
	aux_plan	TB_LINEAS.CODPLAN%TYPE;
	aux_linea	TB_LINEAS.CODLINEA%TYPE;

	TYPE TpCursor	IS REF CURSOR;
	aux_cursor	TpCursor;

	aux_mensaje	varchar2(2000) := '';
	aux_asunto	varchar2(2000) := 'Alerta Agroplus: Aceptación de anexos de modificación por parte de Agroseguro';
	aux_grp_correo	TB_GRUPO_ENTIDADES.ID%TYPE;

BEGIN
	-- 1. Consultamos las pólizas del envio que han sido rechazadas agrupadas por CODENTIDAD.
	aux_query := 'SELECT C.CODENTIDAD, A.NIFCIF, P.REFERENCIA, P.DC, L.CODPLAN, L.CODLINEA
		FROM TB_POLIZAS P, TB_ANEXO_MOD M, TB_, TB_COLECTIVOS C, TB_ASEGURADOS A, TB_LINEAS L
		WHERE M.IDPOLIZA = P.IDPOLIZA AND P.IDCOLECTIVO = C.ID AND P.IDASEGURADO = A.ID
    AND P.LINEASEGUROID = L.LINEASEGUROID	AND M.ESTADO = 4 AND M.IDENVIO = ' || p_idenvio
		 || ' ORDER BY C.CODENTIDAD';

	-- 2. Recorremos las pólizas y vamos montando el cuerpo del email (por cada entidad)
	OPEN aux_cursor FOR aux_query;

	FETCH aux_cursor INTO aux_codentidad, aux_nif, aux_referencia, aux_dc, aux_plan, aux_linea;
	LOOP
	EXIT WHEN aux_cursor%NOTFOUND;
		BEGIN
			IF (aux_codent_ant IS NULL OR aux_codent_ant != aux_codentidad) THEN
				IF (aux_codent_ant IS NOT NULL) THEN
					aux_mensaje := aux_mensaje || 'Por favor, no responda a este mensaje.';
					SELECT IDGRUPOCORREO INTO aux_grp_correo FROM TB_ENTIDADES WHERE CODENTIDAD = aux_codent_ant;
					PQ_ENVIO_CORREOS.enviarCorreo(aux_grp_correo, aux_asunto, aux_mensaje);
				END IF;
				aux_codent_ant := aux_codentidad;
				--Limpiamos las variables para los emails.
				aux_mensaje := 'Este es un mensaje generado automáticamente para notificarle los anexos de ' ||
					'modificación aceptados por Agroseguro durante el proceso de envío.

';
				aux_grp_correo := null;
			END IF;

			-- Montamos el mensaje
			-- En el correo indicaremos REFERENCIA, NIF/CIF
			aux_mensaje := aux_mensaje || aux_referencia || aux_dc || ' - ' || aux_nif || '


';

      dbms_output.put_line(aux_mensaje);
      dbms_output.put_line(aux_asunto);

		EXCEPTION
			WHEN OTHERS THEN
				dbms_output.put_line(sqlerrm);
		END;

    --Reseteo las variables antes de volver a volcar los datos del cursor
    aux_codentidad := null;

		FETCH aux_cursor INTO aux_codentidad, aux_nif, aux_referencia, aux_dc, aux_plan, aux_linea;
    IF (aux_codentidad IS NULL OR aux_codent_ant != aux_codentidad) THEN
					aux_mensaje := aux_mensaje || 'Por favor, no responda a este mensaje.';
					SELECT IDGRUPOCORREO INTO aux_grp_correo FROM TB_ENTIDADES WHERE CODENTIDAD = aux_codent_ant;
					PQ_ENVIO_CORREOS.enviarCorreo(aux_grp_correo, aux_asunto, aux_mensaje);
		END IF;
	END LOOP;
	CLOSE aux_cursor;

	-- 3. Consultamos el grupo de la entidad y le enviamos el correo al grupo y a RGA
END;


PROCEDURE GENERA_CORREOS_RECHAZO_RED
(
  p_idenvio	in NUMBER
)
IS
	--Variables auxiliares
	aux_query	varchar2(2000);
	aux_codent_ant	TB_ENTIDADES.CODENTIDAD%TYPE := null;

	aux_codentidad	TB_ENTIDADES.CODENTIDAD%TYPE;
	aux_nif		TB_ASEGURADOS.NIFCIF%TYPE;
	aux_referencia	TB_POLIZAS.REFERENCIA%TYPE;
	aux_dc		TB_POLIZAS.DC%TYPE;
	--aux_xml		TB_POLIZAS.XMLACUSECONTRATACION%TYPE;

	aux_plan	TB_LINEAS.CODPLAN%TYPE;
	aux_linea	TB_LINEAS.CODLINEA%TYPE;

	TYPE TpCursor	IS REF CURSOR;
	aux_cursor	TpCursor;
	aux_cur_err	TpCursor;

	aux_mensaje	varchar2(2000) := '';
	aux_asunto	varchar2(2000) := 'Alerta Agroplus: Rechazo de anexos de reducción por parte de Agroseguro';
	aux_grp_correo	TB_GRUPO_ENTIDADES.ID%TYPE;

	str_cod		varchar2(2000);
	str_desc	varchar2(2000);

  aux_consulta varchar2(2000);

BEGIN
	-- 1. Consultamos las pólizas del envio que han sido rechazadas agrupadas por CODENTIDAD.
	aux_query := 'SELECT C.CODENTIDAD, A.NIFCIF, P.REFERENCIA, P.DC, L.CODPLAN, L.CODLINEA
		FROM TB_POLIZAS P, TB_ANEXO_RED R, TB_COLECTIVOS C, TB_ASEGURADOS A, TB_LINEAS L
		WHERE R.IDPOLIZA = P.IDPOLIZA AND P.IDCOLECTIVO = C.ID AND P.IDASEGURADO = A.ID
    AND P.LINEASEGUROID = L.LINEASEGUROID	AND R.IDESTADO = 4 AND R.IDENVIO = ' || p_idenvio
		 || ' ORDER BY C.CODENTIDAD';

	-- 2. Recorremos las pólizas y vamos montando el cuerpo del email (por cada entidad)
	OPEN aux_cursor FOR aux_query;

	FETCH aux_cursor INTO aux_codentidad, aux_nif, aux_referencia, aux_dc, aux_plan, aux_linea;
	LOOP
	EXIT WHEN aux_cursor%NOTFOUND;
		BEGIN
			IF (aux_codent_ant IS NULL OR aux_codent_ant != aux_codentidad) THEN
				IF (aux_codent_ant IS NOT NULL) THEN
					aux_mensaje := aux_mensaje || 'Por favor, no responda a este mensaje.';
					SELECT IDGRUPOCORREO INTO aux_grp_correo FROM TB_ENTIDADES WHERE CODENTIDAD = aux_codent_ant;
					PQ_ENVIO_CORREOS.enviarCorreo(aux_grp_correo, aux_asunto, aux_mensaje);
				END IF;
				aux_codent_ant := aux_codentidad;
				--Limpiamos las variables para los emails.
				aux_mensaje := 'Este es un mensaje generado automáticamente para notificarle los errores ' ||
					'detectados en Agroseguro durante el proceso de envío de anexos de reducción.

';
				aux_grp_correo := null;
			END IF;

      aux_consulta := 'SELECT extractvalue(value(err), ''//@codigo''), extractvalue(value(err), ''//@descripcion'')
			FROM tb_comunicaciones c,
				TABLE (xmlsequence(extract(xmltype(c.fichero_contenido),''/xml-fragment/acus:Documento'', ''xmlns:acus=http://www.agroseguro.es/AcuseRecibo''))) docs,
				TABLE (xmlsequence(extract(value(docs),''/acus:Documento/acus:Error'', ''xmlns:acus=http://www.agroseguro.es/AcuseRecibo''))) err
			WHERE extractvalue(value(docs), ''//@id'') = ''' ||aux_referencia || aux_plan || aux_linea ||
				''' AND C.IDENVIO = ' || p_idenvio;

			OPEN aux_cur_err FOR aux_consulta;

			-- Montamos el mensaje
			-- En el correo indicaremos REFERENCIA, NIF/CIF
			aux_mensaje := aux_mensaje || aux_referencia || aux_dc || ' - ' || aux_nif || '

';
			--  y LISTA DE ERRORES DEVUELTOS. En el xml viene identificado por REFERENCIA + PLAN + LINEA
			FETCH aux_cur_err INTO str_cod, str_desc;
			LOOP
				EXIT WHEN aux_cur_err%NOTFOUND;
        -- Por cada error, ponemos su código y su descripción
				aux_mensaje := aux_mensaje || '          * ' || str_cod || ' - ' || str_desc || '
';

				FETCH aux_cur_err INTO str_cod, str_desc;
			END LOOP;
			CLOSE aux_cur_err;

      aux_mensaje := aux_mensaje || '


';

      dbms_output.put_line(aux_mensaje);
      dbms_output.put_line(aux_asunto);

		EXCEPTION
			WHEN OTHERS THEN
				dbms_output.put_line(sqlerrm);
		END;

    --Reseteo las variables antes de volver a volcar los datos del cursor
    aux_codentidad := null;

		FETCH aux_cursor INTO aux_codentidad, aux_nif, aux_referencia, aux_dc, aux_plan, aux_linea;
    IF (aux_codentidad IS NULL OR aux_codent_ant != aux_codentidad) THEN
					aux_mensaje := aux_mensaje || 'Por favor, no responda a este mensaje.';
					SELECT IDGRUPOCORREO INTO aux_grp_correo FROM TB_ENTIDADES WHERE CODENTIDAD = aux_codent_ant;
					PQ_ENVIO_CORREOS.enviarCorreo(aux_grp_correo, aux_asunto, aux_mensaje);
		END IF;
	END LOOP;
	CLOSE aux_cursor;

	-- 3. Consultamos el grupo de la entidad y le enviamos el correo al grupo y a RGA
END;

PROCEDURE GENERA_CORREOS_ACEPTACION_RED
(
  p_idenvio	in NUMBER
)
IS
	--Variables auxiliares
	aux_query	varchar2(2000);
	aux_codent_ant	TB_ENTIDADES.CODENTIDAD%TYPE := null;

	aux_codentidad	TB_ENTIDADES.CODENTIDAD%TYPE;
	aux_nif		TB_ASEGURADOS.NIFCIF%TYPE;
	aux_referencia	TB_POLIZAS.REFERENCIA%TYPE;
	aux_dc		TB_POLIZAS.DC%TYPE;
	aux_plan	TB_LINEAS.CODPLAN%TYPE;
	aux_linea	TB_LINEAS.CODLINEA%TYPE;

	TYPE TpCursor	IS REF CURSOR;
	aux_cursor	TpCursor;

	aux_mensaje	varchar2(2000) := '';
	aux_asunto	varchar2(2000) := 'Alerta Agroplus: Aceptación de anexos de reducción por parte de Agroseguro';
	aux_grp_correo	TB_GRUPO_ENTIDADES.ID%TYPE;

BEGIN
	-- 1. Consultamos las pólizas del envio que han sido rechazadas agrupadas por CODENTIDAD.
	aux_query := 'SELECT C.CODENTIDAD, A.NIFCIF, P.REFERENCIA, P.DC, L.CODPLAN, L.CODLINEA
		FROM TB_POLIZAS P, TB_ANEXO_RED R, TB_COLECTIVOS C, TB_ASEGURADOS A, TB_LINEAS L
		WHERE R.IDPOLIZA = P.IDPOLIZA AND P.IDCOLECTIVO = C.ID AND P.IDASEGURADO = A.ID
    AND P.LINEASEGUROID = L.LINEASEGUROID	AND R.ESTADO = 4 AND R.IDENVIO = ' || p_idenvio
		 || ' ORDER BY C.CODENTIDAD';

	-- 2. Recorremos las pólizas y vamos montando el cuerpo del email (por cada entidad)
	OPEN aux_cursor FOR aux_query;

	FETCH aux_cursor INTO aux_codentidad, aux_nif, aux_referencia, aux_dc, aux_plan, aux_linea;
	LOOP
	EXIT WHEN aux_cursor%NOTFOUND;
		BEGIN
			IF (aux_codent_ant IS NULL OR aux_codent_ant != aux_codentidad) THEN
				IF (aux_codent_ant IS NOT NULL) THEN
					aux_mensaje := aux_mensaje || 'Por favor, no responda a este mensaje.';
					SELECT IDGRUPOCORREO INTO aux_grp_correo FROM TB_ENTIDADES WHERE CODENTIDAD = aux_codent_ant;
					PQ_ENVIO_CORREOS.enviarCorreo(aux_grp_correo, aux_asunto, aux_mensaje);
				END IF;
				aux_codent_ant := aux_codentidad;
				--Limpiamos las variables para los emails.
				aux_mensaje := 'Este es un mensaje generado automáticamente para notificarle los anexos de ' ||
					'reducción aceptados por Agroseguro durante el proceso de envío.

';
				aux_grp_correo := null;
			END IF;

			-- Montamos el mensaje
			-- En el correo indicaremos REFERENCIA, NIF/CIF
			aux_mensaje := aux_mensaje || aux_referencia || aux_dc || ' - ' || aux_nif || '


';

      dbms_output.put_line(aux_mensaje);
      dbms_output.put_line(aux_asunto);

		EXCEPTION
			WHEN OTHERS THEN
				dbms_output.put_line(sqlerrm);
		END;

    --Reseteo las variables antes de volver a volcar los datos del cursor
    aux_codentidad := null;

		FETCH aux_cursor INTO aux_codentidad, aux_nif, aux_referencia, aux_dc, aux_plan, aux_linea;
    IF (aux_codentidad IS NULL OR aux_codent_ant != aux_codentidad) THEN
					aux_mensaje := aux_mensaje || 'Por favor, no responda a este mensaje.';
					SELECT IDGRUPOCORREO INTO aux_grp_correo FROM TB_ENTIDADES WHERE CODENTIDAD = aux_codent_ant;
					PQ_ENVIO_CORREOS.enviarCorreo(aux_grp_correo, aux_asunto, aux_mensaje);
		END IF;
	END LOOP;
	CLOSE aux_cursor;

	-- 3. Consultamos el grupo de la entidad y le enviamos el correo al grupo y a RGA
END;

END PQ_TRATAR_POLIZAS_RECHAZADAS;
/
SHOW ERRORS;