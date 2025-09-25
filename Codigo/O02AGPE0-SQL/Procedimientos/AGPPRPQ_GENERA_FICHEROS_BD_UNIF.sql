SET DEFINE OFF;
SET SERVEROUTPUT ON;

CREATE OR REPLACE PACKAGE o02agpe0.PQ_GENERA_FICHEROS_BD_UNIF AS

       PROCEDURE generarFicheroPolizas;
       PROCEDURE generarFicheroSiniestros;
       PROCEDURE generarRestoAseg ;
	   PROCEDURE generarRestoTom ;
	   PROCEDURE generarCambioTomCol;
       PROCEDURE generarCorreoResumen;
       PROCEDURE generarEstadisticas;
       PROCEDURE generar_ficheros;

	   procedure pintarErrorObligatorios(SISTEMA_ORIGEN varchar2,FECHA_INCREMENTO varchar2,CODIGO_ENTIDAD number,NIF varchar2);
	   procedure insertarTomador(CODIGO_ENTIDAD number,CIF_TOMADOR varchar2,NOMBRE_TOM varchar2,TIPO_VIA_TOM varchar2,NOMBRE_VIA_TOM varchar2,NUMERO_VIA_TOM varchar2,PUERTA_TOM varchar2,
							BLOQUE_TOM varchar2,ESCALERA_TOM varchar2,CP_TOM number,PROV_TOM number,CCAA_TOM number,TERMINO_TOM number,
							SUBTERMINO_TOM varchar2,TLF_FIJO_TOM varchar2,TLF_MOVIL_TOM varchar2,EMAIL_TOM varchar2);

 procedure insertarAsegurado(CODIGO_ENTIDAD number ,NOMBRE_ASG varchar2 ,APELLIDO1_ASG varchar2 ,APELLIDO2_ASG varchar2 ,
                             TIPO_DOC_ASG varchar2 ,NIF_ASEGURADO varchar2,TIPO_IDENT_ASG varchar2,ID_ASEGURADO number,SEG_SOCIAL_ASG varchar2,
							 REGIMEN_SEG_SOCIAL number,TIPO_VIA_ASG varchar2,NOMBRE_VIA_ASG varchar2 ,NUMERO_VIA_ASG varchar2,PUERTA_ASG varchar2,
							 BLOQUE_ASG varchar2,ESCALERA_ASG varchar2, CP_ASG number, PROV_ASG number,CCAA_ASG number,TERMINO_ASG number,
							 SUBTERMINO_ASG varchar2,TLF_FIJO_ASG varchar2,TLF_MOVIL_ASG varchar2,EMAIL_ASG varchar2);
               
     FUNCTION obtener_fechafin_contrato (v_fecha_envio in DATE, v_cod_linea in VARCHAR2, v_codmodulo in VARCHAR2, v_estado in NUMBER, v_idpoliza in NUMBER) RETURN VARCHAR2;  

END PQ_GENERA_FICHEROS_BD_UNIF;
/
CREATE OR REPLACE PACKAGE BODY o02agpe0.PQ_GENERA_FICHEROS_BD_UNIF AS
        lc   VARCHAR2(50) := 'Generar ficheros de siniestros BD Unificada';
        vPolizasTotales number:=0;
        vPolizasEnviadas number:=0;
		    vPolizasMal number:=0;

   	    vSiniestrosTotales number:=0;
        vSiniestrosEnviados number:=0;
		    vSiniestrosMal number:=0;

		    vTomadoresTotales number:=0;
        vTomadoresActualizados number:=0;
		    vTomadoresMal number :=0;

		    vAseguradosTotales number:=0;
        vAseguradosActualizados number:=0;
		    vAseguradosMal number:=0;
		    vPersonasRepetidas number:=0;
		    vPersonasMal number:=0;


        vTelefonosTratados number:=0;
		    vTelefonosActualizados number:=0;
        vDireccionesTratadas number:=0;
		    vDireccionesActualizadas number:=0;
        vEmailsTratados number:=0;
		    vEmailsActualizados number:=0;
        
        l_dir	o02agpe0.TB_CONFIG_AGP.AGP_valor%TYPE; -- Valor del parametro de configuracion
        l_dir_name    		VARCHAR2(1000);
        l_fecha_fichero   varchar2(8) := to_char(sysdate, 'YYYYMMDD');
        l_SISTEMA_ORIGEN        VARCHAR2(1) := '7';
        l_IDEMPRESA             VARCHAR2(1) := '1';
        l_query	      		 VARCHAR2(32000);
		    l_query_siniestros VARCHAR2(32000);
    	  l_query_polizas      VARCHAR2(32000);
        l_query_cambio_colec VARCHAR2(32000);
        l_FECHA_INCREMENTO      VARCHAR2(8):= to_char(sysdate, 'YYYYMMDD');
		    l_FECHA_ANTIGUEDAD       VARCHAR2(8);
        
        l_fechafin_contrato     VARCHAR(8) := to_char(sysdate, 'YYYYMMDD');
		    l_TIPO_DOC_TOM          VARCHAR2(2):='01';
		    l_TIPO_IDENT_TOM        VARCHAR2(1) := 'J';
        l_COBRO_IBAN            VARCHAR2(24);
        l_ESTADO_POLIZA         VARCHAR2(2):='02';
        
        v_num_cuentaAsegurado PQ_ENVIO_POLIZAS_RENOVABLES.array_cuentaAseg;
		    TYPE MapaPersonas IS TABLE OF NUMBER INDEX BY VARCHAR2(15);
		    --Para no incluir duplicados en personas
		    arrPersonasIncluidas   MapaPersonas;
		    aniadir                boolean := true;

        -- Pet. 58869 ** (02.10.2019)
        l_COD_CLIENTE  VARCHAR(7):='CLIENTE';
        l_COD_CONACT   VARCHAR(6):='CONACT';
        l_COD_CONANU   VARCHAR(6):='CONANU';
        l_COD_SINACT   VARCHAR(6):='SINACT';
        l_COD_SINANU   VARCHAR(6):='SINANU';
        l_COD_SINREA   VARCHAR(6):='SINREA';
        l_ConteoPolOk   number :=0;
        l_ConteoPolNoOk number :=0;
        l_ConteoCliente number :=0;
        l_conteoSin     number :=0;
        l_conteoSinanu  number :=0;
        l_conteoSinrea  number :=0;
        
        -- exceptions
        datosNull EXCEPTION;
		    polizaMal EXCEPTION;
		    siniestroMal EXCEPTION;

f_fichero_str		  UTL_FILE.FILE_TYPE; -- Fichero de siniestros
l_nombre_str      varchar2(30) := l_fecha_fichero || '_AGRO_SINIESTROS';
l_line_str     		VARCHAR2(1000);
okSiniestros VARCHAR2(5):='OK';

f_fichero_plz		  UTL_FILE.FILE_TYPE; -- Fichero de pólizas
l_nombre_plz      varchar2(30) := l_fecha_fichero || '_AGRO_CONTRATOS';
l_line_plz     		VARCHAR2(1000);
okPolizas VARCHAR2(5):='OK';


f_fichero_prs     UTL_FILE.FILE_TYPE; -- Fichero de personas
l_nombre_prs      varchar2(30) := l_fecha_fichero || '_AGRO_PERSONAS';
l_line_prs     		VARCHAR2(1000);
okPersonas VARCHAR2(5):='OK';


f_fichero_dir     UTL_FILE.FILE_TYPE; -- Fichero de direcciones
l_nombre_dir      varchar2(30) := l_fecha_fichero || '_AGRO_DOMICILIOS';
l_line_dir     		VARCHAR2(1000);
okDirecciones VARCHAR2(5):='OK';


f_fichero_tlf     UTL_FILE.FILE_TYPE; -- Fichero de teléfonos
l_nombre_tlf      varchar2(30) := l_fecha_fichero || '_AGRO_TELEFONOS';
l_line_tlf     		VARCHAR2(1000);
okTelefonos VARCHAR2(5):='OK';


f_fichero_mail    UTL_FILE.FILE_TYPE; -- Fichero de correos electrónicos
l_nombre_mail     varchar2(30) := l_fecha_fichero || '_AGRO_EMAILS';
l_line_mail    		VARCHAR2(1000);
mesaje varchar2(100);
okEmails VARCHAR2(5):='OK';

/* Pet. 58869 ** Modif TAM (01.10.2019) */
f_fichero_estadistica    UTL_FILE.FILE_TYPE; -- Fichero de correos electrónicos
l_nombre_estadistica     varchar2(40) := l_fecha_fichero || '_AGRO_ESTADISTICA';
l_line_estadistica    	 VARCHAR2(1000);
mesaje varchar2(100);
okEstadistica VARCHAR2(5):='OK';
/* Pet. 58869 ** Modif TAM (01.10.2019) FIN */

nls_characterset nls_database_parameters.value%TYPE;

 PROCEDURE generar_ficheros AS
  BEGIN
  
    /* DIRECTORIO TEST Y PROD */
    l_dir  := o02agpe0.PQ_Utl.getcfg('DIR_EXPORT_ENVIOS');
    /* DIRECTORIO DESA */
    --l_dir := pq_utl.getcfg('DIR_IN_AGP');

    select value into nls_characterset from nls_database_parameters where parameter = 'NLS_CHARACTERSET';


    -- Se guarda el path fisico del directorio
    SELECT DIRECTORY_PATH into l_dir_name FROM ALL_DIRECTORIES WHERE DIRECTORY_NAME=l_dir;

	f_fichero_str := UTL_FILE.FOPEN (LOCATION     => l_dir,
	 			filename     => l_nombre_str || '.csv',
	 			open_mode    => 'w',
	 			max_linesize => o02agpe0.PQ_Typ.MAX_LINEFILESIZEWRITE);

	f_fichero_plz := UTL_FILE.FOPEN (LOCATION     => l_dir,
	 			filename     => l_nombre_plz || '.csv',
	 			open_mode    => 'w',
	 			max_linesize => o02agpe0.PQ_Typ.MAX_LINEFILESIZEWRITE);
				
    f_fichero_prs := UTL_FILE.FOPEN (LOCATION     => l_dir,
	 			filename     => l_nombre_prs || '.csv',
	 			open_mode    => 'w',
	 			max_linesize => o02agpe0.PQ_Typ.MAX_LINEFILESIZEWRITE);

   f_fichero_dir := UTL_FILE.FOPEN (LOCATION     => l_dir,
	 			filename     => l_nombre_dir || '.csv',
	 			open_mode    => 'w',
	 			max_linesize => o02agpe0.PQ_Typ.MAX_LINEFILESIZEWRITE);

   f_fichero_tlf := UTL_FILE.FOPEN (LOCATION     => l_dir,
	 			filename     => l_nombre_tlf || '.csv',
	 			open_mode    => 'w',
	 			max_linesize => o02agpe0.PQ_Typ.MAX_LINEFILESIZEWRITE);

   f_fichero_mail := UTL_FILE.FOPEN (LOCATION     => l_dir,
	 			filename     => l_nombre_mail || '.csv',
	 			open_mode    => 'w',
	 			max_linesize => o02agpe0.PQ_Typ.MAX_LINEFILESIZEWRITE);
        
/* Pet. 58869 ** Modif TAM (01.10.2019) */        
   f_fichero_estadistica := UTL_FILE.FOPEN (LOCATION     => l_dir,
	 			filename     => l_nombre_estadistica || '.csv',
	 			open_mode    => 'w',
	 			max_linesize => o02agpe0.PQ_Typ.MAX_LINEFILESIZEWRITE);
/* Pet. 58869 ** Modif TAM (01.10.2019) FIN */        

					--Incluimos la cabecera en los CSV:
  /* Pet. 62719 ** MODIF TAM (28.01.2021) ** Inicio */
  /* Se incluye el campo Fecha fin de contrato en el fichero de polizas */        
	l_line_plz := 'id sistema origen;Fecha de incremento (delta);Ind_baja;IdEmpresa;Entidad;Numero identificativo;'||
				  'Identificador/Id_Persona/Id Cliente/IdPersona(RSI);Numero de Contrato/Plan+Referencia;FechaVigor;'||
				  'CodCiaPol;Numero de poliza de la otra cia.;Objeto asegurado (a definir por RAMO);Poliza con Embargos;'||
				  'Rol de la persona;Red comercial;Estado;Cobro - Iban;Numero de certificado;Sec_Domicilio;Sec_email;Sec_telefono; Fecha fin del contrato ';
  UTL_FILE.PUT_LINE (f_fichero_plz, utl_raw.cast_to_varchar2(utl_raw.convert(utl_raw.cast_to_raw(l_line_plz || chr(13)), 'UTF8', nls_characterset)));

	l_line_prs := 'id sistema origen;Fecha de incremento (delta);Fecha Antigüedad del dato;Ind_baja;IdEmpresa;Entidad;'||
				  'Nombre - Razon Social;Apellido uno;Apellido dos;Sexo;Tipo de Documento;Numero Identificativo;DNI_Progenitor;'||
				  'Tratamiento;Tipo de Identificacion;Identificador/Id_Persona/Id Cliente/IdPersona(RSI);Num Seguridad Social;'||
				  'Regimen Seguridad Social;Fecha caducidad NIF;Fecha constitucion;Fecha de autonomo;Fecha de nacimiento;Forma Legal;'||
				  'CNAE;CNO;País de nacimiento;PaisResidec;Países contribución/fiscal;Idioma;Nacionalidad;Situación Laboral;'||
				  'Fallecido y/o Baja;No Hijos;Estado Civil;Identificado con fotocopia DNI en RGA;Fecha VIP;Origen de marca VIP;'||
				  'Tipo VIP;Indicador VIP;Marca exento de fiscalidad;Si tiene contrato proveedor;Deportes y/o actividades de riesgo;'||
				  'Prestacion de jubilado;Profesión principal;Profesión secundaria;Fecha de prestacion del jubilado;'||
				  'Envio de publicidad de la CAJA (actual);Fecha de publicidad de la CAJA (actual);Causa de bloqueo;LOPD;FechBloqCanc';
  UTL_FILE.PUT_LINE (f_fichero_prs, utl_raw.cast_to_varchar2(utl_raw.convert(utl_raw.cast_to_raw(l_line_prs || chr(13)), 'UTF8', nls_characterset)));

	l_line_dir := 'id sistema origen;Fecha de incremento (delta);Ind_baja;IdEmpresa;Entidad;Numero identificativo;'||
				  'Identificador/Id_Persona/Id Cliente/IdPersona(RSI);Sec_Domicilio;Tipo Domicilio;Tipo vía;Nombre vía;Número de vía;'||
				  'Puerta/Portal;Bloque;Escalera;Planta;CP;Provincia;Comunidad autonoma;País;Población;Comarca;Término;Subtérmino;Sublocalidad';
  UTL_FILE.PUT_LINE (f_fichero_dir, utl_raw.cast_to_varchar2(utl_raw.convert(utl_raw.cast_to_raw(l_line_dir || chr(13)), 'UTF8', nls_characterset)));

	l_line_tlf := 'id sistema origen;Fecha de incremento (delta);Ind_baja;IdEmpresa;Entidad;Numero identificativo;'||
				  'Identificador/Id_Persona/Id Cliente/IdPersona(RSI);Sec_telefono;Tipo de Teléfono;Telefono';
  UTL_FILE.PUT_LINE (f_fichero_tlf, utl_raw.cast_to_varchar2(utl_raw.convert(utl_raw.cast_to_raw(l_line_tlf || chr(13)), 'UTF8', nls_characterset)));

	l_line_mail := 'id sistema origen;Fecha de incremento (delta);Ind_baja;IdEmpresa;Entidad;Numero identificativo;'||
				  'Identificador/Id_Persona/Id Cliente/IdPersona(RSI);Sec_email;Tipo Email;Email';
  UTL_FILE.PUT_LINE (f_fichero_mail, utl_raw.cast_to_varchar2(utl_raw.convert(utl_raw.cast_to_raw(l_line_mail || chr(13)), 'UTF8', nls_characterset)));
  
  /* Pet. 58869 ** Modif TAM (01.10.2019) */ 
  l_line_estadistica := 'Cod.operacional;Id_empresa;Operacion;Conteo';
  UTL_FILE.PUT_LINE (f_fichero_estadistica, utl_raw.cast_to_varchar2(utl_raw.convert(utl_raw.cast_to_raw(l_line_estadistica || chr(13)), 'UTF8', nls_characterset)));
  /* Pet. 58869 ** Modif TAM (01.10.2019) FIN */ 

	l_line_str := 'id sistema origen;Fecha de incremento (delta);Ind_baja;IdEmpresa;Entidad;Numero identificativo;'||
				  'Identificador/Id_Persona/Id Cliente/IdPersona(RSI);Numero de Expediente;Año de Expediente apertura;'||
				  'Numero de Contrato/Poliza;Especial Fraude;Numero de expediente de la otra cia.;Rol de la persona;'||
				  'Estado de Siniestro;Numero de certificado;Sec_Domicilio;Sec_email;Sec_telefono;Fecha cierre del siniestro;'||
				  'id_cliente_ampliado;causa_siniestro';
  UTL_FILE.PUT_LINE (f_fichero_str, utl_raw.cast_to_varchar2(utl_raw.convert(utl_raw.cast_to_raw(l_line_str || chr(13)), 'UTF8', nls_characterset)));
  lc:='generarFicheroPolizas BD Unificada';
	generarFicheroPolizas;
    commit;
	lc:='generarRestoAseg BD Unificada';
	generarRestoAseg;
	commit;
	lc:='generarRestoTom BD Unificada';
	generarRestoTom;
	commit;
	lc:='generarFicheroSiniestros BD Unificada';
	generarFicheroSiniestros;
  commit;
	generarCambioTomCol;
   commit;
  /* Pet. 58869 ** Modif TAM (01.10.2019) */ 
  lc:='generarEstadistica BD Unificada';
	generarEstadisticas;
  commit;
  /* Pet. 58869 ** Modif TAM (01.10.2019) * FIN*/  

	UTL_FILE.fflush(f_fichero_plz);
    utl_file.fclose(f_fichero_plz);

	UTL_FILE.fflush(f_fichero_str);
    utl_file.fclose(f_fichero_str);

	UTL_FILE.fflush(f_fichero_prs);
    utl_file.fclose(f_fichero_prs);

	UTL_FILE.fflush(f_fichero_dir);
    utl_file.fclose(f_fichero_dir);

	UTL_FILE.fflush(f_fichero_tlf);
    utl_file.fclose(f_fichero_tlf);

	UTL_FILE.fflush(f_fichero_mail);
    utl_file.fclose(f_fichero_mail);
  
  /* Pet. 58869 ** Modif TAM (01.10.2019) */ 
	UTL_FILE.fflush(f_fichero_estadistica);
    utl_file.fclose(f_fichero_estadistica);
  /* Pet. 58869 ** Modif TAM (01.10.2019) FIN */   
	generarCorreoResumen;
	exception
	when others then
  dbms_output.put_line('error indefinido en generar_ficheros');
		pq_utl.log(sqlcode || 'error indefinido en generar_ficheros' || l_line_plz || ' [' || SQLERRM || ']');

END generar_ficheros;

PROCEDURE generarFicheroPolizas  AS
Cursor cPolizas is select a.codentidad codEntidadAseg, a.nifcif nifAseg, a.id as idAseg,
				'4 ' || l.codlinea || ' ' || l.codplan || ' ' || p.referencia as contrato, to_char(p.fecha_vigor, 'YYYYMMDD') as fechaVigor,
                 case when l.codlinea < 400 then 'Parcelas' else 'Explotaciones' end as objetoAsegurado, lpad(c.codentidad,4,'0') || lpad(p.oficina,4,'0') as redComercial,
                 pp.iban || pp.cccbanco as cobroIban, nvl(a.razonsocial, a.nombre) as nombreAseg,
				 a.apellido1 as apellido1Aseg, a.apellido2 as apellido2Aseg, t.RAZONSOCIAL as nombreTom,
				 p.idestado as idestado,
				 case when p.idestado in (4,16,15) then '02' else '01' end as estadoPoliza,
                 case when a.TIPOIDENTIFICACION = 'NIF' then '51' when a.TIPOIDENTIFICACION = 'CIF' then '01'  else '53' end as tipoDocumentoAseg,
                 case when a.TIPOIDENTIFICACION = 'CIF' then 'J' else 'F' end as tipoIdentAseg, a.NUMSEGSOCIAL, a.REGIMENSEGSOCIAL,
                 a.CLAVEVIA as claveViaAseg, a.DIRECCION as direccionAseg, a.NUMVIA numViaAseg, a.PISO as pisoAseg, a.BLOQUE as bloqueAseg, a.ESCALERA as escaleraAseg,
                 a.CODPOSTAL as codPostalAseg, a.CODPROVINCIA as codProvinciaAseg , prov.CODCOMUNIDAD - 60 as codComunidadAseg,
				 a.CODLOCALIDAD as codLocalidadAseg,
                 a.SUBLOCALIDAD as subLocalidadAseg, a.TELEFONO as telefonoAseg, a.MOVIL as movilAseg, a.EMAIL as emailAseg,
				 t.ciftomador as cifTomador,t.codentidad as codEntidadTom,
                 t.CLAVEVIA as claveViaTom, t.DOMICILIO as domicilioTom, t.NUMVIA as numViaTom, t.PISO as pisoTom, t.BLOQUE as bloqueTom, t.ESCALERA as escaleraTom,
				 t.CODPROVINCIA codProvinciaTom, provT.CODCOMUNIDAD - 60 as codComunidadTom, t.CODLOCALIDAD as codLocalidadTom,
				 t.SUBLOCALIDAD as subLocalidadTom,
                 t.CODPOSTAL as codPostalTom, t.TELEFONO as telefonoTom, t.MOVIL as movilTom, t.EMAIL as emailTom,
				 p.externa as polExterna,c.entmediadora as entMediadora ,c.subentmediadora as subEntMediadora,l.codlinea as codLinea, p.idpoliza, p.referencia, p.fechaenvio, p.codmodulo
                 from o02agpe0.tb_polizas p, 
                      o02agpe0.tb_colectivos c, 
                      o02agpe0.tb_tomadores t, 
                      o02agpe0.tb_asegurados a,
                      o02agpe0.tb_pagos_poliza pp, 
                      o02agpe0.tb_lineas l, 
                      o02agpe0.tb_provincias prov, 
                      o02agpe0.tb_provincias provT
                 where p.idcolectivo = c.id and c.codentidad = t.codentidad and c.ciftomador = t.ciftomador and p.idasegurado = a.id and
                 p.idpoliza = pp.idpoliza(+) and p.lineaseguroid = l.lineaseguroid and a.codprovincia = prov.codprovincia and
                 t.codprovincia = provT.codprovincia and p.idestado in(4,8,14,15,16) 
                 and p.tiporef = 'P' and (p.fecha_envio_bd_unif is null or p.fecha_envio_bd_unif<p.fecha_modificacion)
                 /* Pet. 62719 ** MODIF TAM (15.01.2021) ** Inicio */
                 /* No se podrán enviar en el fichero pólizas cuyo asegurado se encuentre bloqueado */
                 and (p.idasegurado NOT IN (select bloqA.Id_Asegurado from o02agpe0.tb_bloqueos_asegurados bloqA where bloqA.Idestado_Aseg = 'B'));
                 

begin
 dbms_output.put_line(lc || ': ## INICIO PROCESO POLIZAS ##');
    -- Procesamos las PÓLIZAS: Se irán completando también los ficheros de personas, direcciones, teléfonos y e-mails
      dbms_output.put_line(lc || ': Recuperando las polizas contratadas no enviadas a BD Unificada ');
      dbms_output.put_line(l_query_polizas);
    for rPoliza in cPolizas loop
		BEGIN
			vPolizasTotales:=vPolizasTotales+1;
			if rPoliza.referencia is null then
			   vPolizasMal:=vPolizasMal+1;
			   Raise polizaMal;
			end if;
			l_COBRO_IBAN:=rPoliza.cobroIban;
			if( rPoliza.polExterna = 1 ) then
				begin
					v_num_cuentaAsegurado := PQ_ENVIO_POLIZAS_RENOVABLES.getCuentaAsegurado(rPoliza.nifAseg,rPoliza.codEntidadAseg,rPoliza.codLinea,rPoliza.entmediadora,rPoliza.subentmediadora);
					IF (v_num_cuentaAsegurado is not null AND v_num_cuentaAsegurado(1) is not null) then
						pq_utl.log(lc, 'CCC encontrada: ' || v_num_cuentaAsegurado(1));
						l_COBRO_IBAN := v_num_cuentaAsegurado(0)|| v_num_cuentaAsegurado(1);
					ELSE
						pq_utl.log(lc, 'CUENTA NO ENCONTRADA');
					END IF;
				EXCEPTION
				WHEn OTHERS THEN
					pq_utl.log(sqlcode || 'error al obtener el numero de cuenta externo [' || SQLERRM || ']');
				END;
				END IF;
		  	begin
			vAseguradosTotales:=vAseguradosTotales+1;
			pintarErrorObligatorios(l_SISTEMA_ORIGEN,l_FECHA_INCREMENTO,rPoliza.codEntidadAseg,rPoliza.nifAseg);

			insertarAsegurado(rPoliza.codEntidadAseg,rPoliza.nombreAseg, rPoliza.apellido1Aseg,rPoliza.apellido2Aseg,rPoliza.tipoDocumentoAseg,
			rPoliza.nifAseg,rPoliza.tipoIdentAseg,rPoliza.idAseg,rPoliza.NUMSEGSOCIAL,rPoliza.REGIMENSEGSOCIAL,rPoliza.claveViaTom,
			rPoliza.direccionAseg,rPoliza.numViaAseg, rPoliza.pisoAseg,rPoliza.bloqueAseg,rPoliza.escaleraAseg, rPoliza.codPostalAseg,
			rPoliza.codProvinciaAseg,rPoliza.codComunidadAseg,rPoliza.codLocalidadAseg,rPoliza.subLocalidadAseg ,rPoliza.telefonoAseg,
			rPoliza.movilAseg ,rPoliza.emailAseg);
      
      /* Pet. 62719 ** MODIF TAM (19.02.2021) ** Inicio */
      /* Obtenemos el nuevo dato de la fecha de vencimiento del contrato */
       l_fechafin_contrato := obtener_fechafin_contrato(rPoliza.Fechaenvio, rPoliza.Codlinea, rPoliza.Codmodulo, rPoliza.idestado, rPoliza.idpoliza);
      /* Pet. 62719 ** MODIF TAM (19.02.2021) ** Fin  */ 
      
      /* Pet. 62719  ** MODIF TAM (23.02.2021) - Cambio de Alcance * INICIO */
      /* Si la fecha de fin de contrato es <> '99991231', se informa el estado con valor '2'*/
      IF (l_fechafin_contrato = '99991231') THEN
      			--Línea para la póliza con el asegurado 
			      l_line_plz := l_SISTEMA_ORIGEN || ';' || l_FECHA_INCREMENTO || ';;' || l_IDEMPRESA || ';' || rPoliza.codEntidadAseg || ';' ||
                       		rPoliza.nifAseg  || ';;' || rPoliza.CONTRATO || ';' || rPoliza.fechaVigor || ';;;' || rPoliza.OBJETOASEGURADO || ';;Aseg;' || rPoliza.redComercial || ';'||
                          rPoliza.estadoPoliza||';' ||l_COBRO_IBAN || ';0;1;1;1;' ||l_fechafin_contrato;
      ELSE
      			--Línea para la póliza con el asegurado 
			      l_line_plz := l_SISTEMA_ORIGEN || ';' || l_FECHA_INCREMENTO || ';;' || l_IDEMPRESA || ';' || rPoliza.codEntidadAseg || ';' ||
                       		rPoliza.nifAseg  || ';;' || rPoliza.CONTRATO || ';' || rPoliza.fechaVigor || ';;;' || rPoliza.OBJETOASEGURADO || ';;Aseg;' || rPoliza.redComercial || ';'||
                          l_ESTADO_POLIZA ||';' ||l_COBRO_IBAN || ';0;1;1;1;' ||l_fechafin_contrato;
      END IF;                    
      /* Pet. 62719  ** MODIF TAM (23.02.2021) - Cambio de Alcance * FIN */

			UTL_FILE.PUT_LINE (f_fichero_plz, utl_raw.cast_to_varchar2(utl_raw.convert(utl_raw.cast_to_raw(l_line_plz || chr(13)), 'UTF8', nls_characterset)));

		--update tb_asegurados (llevado a insertaAsegurado)

		exception
		when datosNull then
			 pq_utl.log(lc||' excepcion de datos nulos al insertar asegurado '|| rPoliza.nifAseg);
			 vAseguradosMal:=vAseguradosMal+1;
		when others then
			pq_utl.log(sqlcode || 'error al insertar asegurado' || l_line_plz || ' [' || SQLERRM || ']');
		end;

		begin
      /* Pet. 62719 ** MODIF TAM (28.01.2021) ** Inicio */
      /* Obtenemos el nuevo dato de la fecha de vencimiento del contrato */
       l_fechafin_contrato := obtener_fechafin_contrato(rPoliza.Fechaenvio, rPoliza.Codlinea, rPoliza.Codmodulo, rPoliza.idestado, rPoliza.idpoliza);
      /* Pet. 62719 ** MODIF TAM (28.01.2021) ** Fin  */ 
		    vTomadoresTotales:=vTomadoresTotales+1;
			pintarErrorObligatorios(l_SISTEMA_ORIGEN,l_FECHA_INCREMENTO,rPoliza.codEntidadAseg,rPoliza.cifTomador);
			insertarTomador(rPoliza.codEntidadTom,rPoliza.cifTomador,rPoliza.nombreTom,rPoliza.claveViaTom,rPoliza.domicilioTom,
			rPoliza.numViaTom, rPoliza.pisoTom,rPoliza.bloqueTom,rPoliza.escaleraTom, rPoliza.codPostalTom,rPoliza.codProvinciaTom,
			rPoliza.codComunidadTom,rPoliza.codLocalidadTom,rPoliza.subLocalidadTom , rPoliza.telefonoTom, rPoliza.movilTom ,rPoliza.emailTom);
      
      /* Pet. 62719  ** MODIF TAM (23.02.2021) - Cambio de Alcance * INICIO */
      /* Si la fecha de fin de contrato es <> '99991231', se informa el estado con valor '2'*/
      IF (l_fechafin_contrato = '99991231') THEN
			    -- Línea para la póliza con el tomador
			    l_line_plz := l_SISTEMA_ORIGEN || ';' || l_FECHA_INCREMENTO || ';;' || l_IDEMPRESA || ';' || rPoliza.codEntidadTom || ';' ||
			                  rPoliza.cifTomador  || ';;' || rPoliza.contrato || ';' || rPoliza.fechaVigor|| ';;;' || rPoliza.objetoAsegurado || ';;Toma;' ||
			                  rPoliza.redComercial || ';'|| rPoliza.estadoPoliza||';' ||
					              l_COBRO_IBAN || ';0;1;1;1;' ||l_fechafin_contrato;
      ELSE
			    l_line_plz := l_SISTEMA_ORIGEN || ';' || l_FECHA_INCREMENTO || ';;' || l_IDEMPRESA || ';' || rPoliza.codEntidadTom || ';' ||
			                  rPoliza.cifTomador  || ';;' || rPoliza.contrato || ';' || rPoliza.fechaVigor|| ';;;' || rPoliza.objetoAsegurado || ';;Toma;' ||
			                  rPoliza.redComercial || ';'|| l_ESTADO_POLIZA ||';' ||
					              l_COBRO_IBAN || ';0;1;1;1;' ||l_fechafin_contrato;
      
      END IF;
      /* Pet. 62719  ** MODIF TAM (23.02.2021) - Cambio de Alcance * FIN */                  
      
      
			UTL_FILE.PUT_LINE (f_fichero_plz, utl_raw.cast_to_varchar2(utl_raw.convert(utl_raw.cast_to_raw(l_line_plz || chr(13)), 'UTF8', nls_characterset)));
			--update tb_tomadores t set t.fecha_envio_bd_unif=sysdate where t.codentidad=rPoliza.codEntidadTom and t.ciftomador=rPoliza.cifTomador;

		exception
		when datosNull then
		 vTomadoresMal:=vTomadoresMal+1;
			 pq_utl.log('excepcion de datos null lanzada y capturada al insertar tomador con NIF !!'||rPoliza.cifTomador);
		when others then
		okPolizas:='ERROR';
			pq_utl.log(sqlcode || 'error al insertar tomador' || l_line_plz || ' [' || SQLERRM || ']');
		end;

		update  o02agpe0.TB_POLIZAS set o02agpe0.TB_POLIZAS.fecha_envio_bd_unif=sysdate where o02agpe0.TB_POLIZAS.idPoliza=rPoliza.idPoliza;
		vPolizasEnviadas:=vPolizasEnviadas+1;
exception
WHEN polizaMal THEN
   pq_utl.log('error al insertar poliza; ref contrato es null '||rPoliza.idpoliza);
when others then
    okPolizas:='ERROR';
	pq_utl.log(sqlcode || 'error indefinido en generarFicheroPolizas' || l_line_plz || ' [' || SQLERRM || ']');
 end;

end loop;
dbms_output.put_line(lc || ': Fin del proceso de pólizas ');

END generarFicheroPolizas;


PROCEDURE generarFicheroSiniestros  AS
 Cursor cSiniestros is 
	select 
        to_char(sysdate, 'YYYYMMDD') as fechaIncremento, 
        a.codentidad, 
        a.nifcif, 
        a.id idAseg,
        '4 ' || l.codlinea || ' ' || l.codplan || ' ' || p.referencia || ' ' || to_number(s.numsiniestro) as num_expte,
        to_char(s.fechaocurrencia, 'YYYYMMDD') as fechaOcurrencia, 
        '4 ' || l.codlinea || ' ' || l.codplan || ' ' || p.referencia as contrato,
        s.numerosiniestro as num_expte_otra_cia,
        t.ciftomador,
        s.ID as idSiniestro,
        r.desriesgo as causa_siniestro
    from 
        o02agpe0.tb_siniestros s, 
        o02agpe0.tb_polizas p, 
        o02agpe0.tb_asegurados a, 
        o02agpe0.tb_colectivos c, 
        o02agpe0.tb_tomadores t,
        o02agpe0.tb_lineas l,
        o02agpe0.tb_sc_c_riesgos r
    where 
        s.idpoliza = p.idpoliza 
        and p.idasegurado = a.id 
        and p.idcolectivo = c.id 
        and c.codentidad = t.codentidad 
        and c.ciftomador = t.ciftomador 
        and p.lineaseguroid = l.lineaseguroid 
        and s.estado = 3 
        and s.FECHA_ENVIO_BD_unif is null
        /* Pet. 62719 ** MODIF TAM (15.01.2021) ** Inicio */
        /* No se podrán enviar en el fichero pólizas cuyo asegurado se encuentre bloqueado */
        and p.idasegurado NOT IN (
            select 
                bloqA.Id_Asegurado 
            from 
                o02agpe0.tb_bloqueos_asegurados bloqA 
            where 
                bloqA.Idestado_Aseg = 'B'
			)
        and r.codriesgo = s.codriesgo;
 BEGIN
   dbms_output.put_line(lc || ': ## INICIO PROCESO SINIESTROS ##');

   dbms_output.put_line('Abriendo fichero');

   -- ********************************************************
   -- Abrimos los ficheros de salida con extension .CSV
   -- ********************************************************

	  -- Procesamos los SINIESTROS: en este caso no añadimos datos a personas ya que todos los datos están relacionados con la póliza
      dbms_output.put_line(lc || ': Recuperando los siniestros no enviados a BD Unificada ');

     dbms_output.put_line(l_query_siniestros);
	for rSiniestros in cSiniestros
      loop

      vSiniestrosTotales:=vSiniestrosTotales+1;
         --Procesamos los siniestros:

		if(rSiniestros.num_expte is null or rSiniestros.contrato is null) then
			vSiniestrosMal:=vSiniestrosMal+1;
			raise siniestroMal;
		end if;
		pintarErrorObligatorios(l_SISTEMA_ORIGEN,l_FECHA_INCREMENTO,rSiniestros.codEntidad,rSiniestros.nifcif);

        begin
		--Línea para el asegurado
        l_line_str :=l_SISTEMA_ORIGEN || ';' || l_FECHA_INCREMENTO || ';;' || l_IDEMPRESA || ';' || rSiniestros.codentidad || ';'
		|| rSiniestros.nifcif || ';;' || rSiniestros.num_expte || ';' || rSiniestros.fechaOcurrencia || ';' || rSiniestros.contrato || ';;' ||
		rSiniestros.num_expte_otra_cia || ';Aseg;VV;0;1;1;1;;' || ';' || rSiniestros.causa_siniestro;
        UTL_FILE.PUT_LINE (f_fichero_str, utl_raw.cast_to_varchar2(utl_raw.convert(utl_raw.cast_to_raw(l_line_str || chr(13)), 'UTF8', nls_characterset)));
        vSiniestrosEnviados:=vSiniestrosEnviados+1;

        --Línea para el tomador
        l_line_str :=l_SISTEMA_ORIGEN || ';' || l_FECHA_INCREMENTO || ';;' || l_IDEMPRESA || ';' || rSiniestros.codentidad || ';'
		|| rSiniestros.ciftomador || ';;' || rSiniestros.num_expte || ';' || rSiniestros.fechaOcurrencia || ';' || rSiniestros.contrato || ';;'
		|| rSiniestros.num_expte_otra_cia || ';Toma;VV;0;1;1;1;;' || ';' || rSiniestros.causa_siniestro;
        UTL_FILE.PUT_LINE (f_fichero_str, utl_raw.cast_to_varchar2(utl_raw.convert(utl_raw.cast_to_raw(l_line_str || chr(13)), 'UTF8', nls_characterset)));
        vSiniestrosEnviados:=vSiniestrosEnviados+1;

        update  o02agpe0.TB_SINIESTROS set o02agpe0.TB_SINIESTROS.fecha_envio_bd_unif=sysdate where o02agpe0.TB_SINIESTROS.id=rSiniestros.idSiniestro;
        exception
		when siniestroMal then
		   -- okSiniestros:=false;
			pq_utl.log(lc||' error de datos nulos al insertar el siniestro'|| rSiniestros.contrato);
        WHEN others then
		    okSiniestros:='ERROR';
			pq_utl.log(lc||sqlcode || 'error de escritura'|| l_line_str || ' [' || SQLERRM || ']');
        end;

      END LOOP;
      dbms_output.put_line(lc || ': Fin del proceso de siniestros ');

   EXCEPTION
       when others then
	     okSiniestros:='ERROR';
         pq_utl.log(lc||sqlcode || ' Error indefinido en generarFicheroSiniestros' || ' [' || SQLERRM || ']');

 END generarFicheroSiniestros;


 PROCEDURE generarRestoTom  AS
 cursor cTomadores is select  t.codentidad, t.ciftomador,t.RAZONSOCIAL as nombreTom ,t.CLAVEVIA, t.DOMICILIO,t.NUMVIA, t.PISO, t.BLOQUE, t.ESCALERA,
							  t.CODPROVINCIA, provT.CODCOMUNIDAD - 60 as codComunidadTom, t.CODLOCALIDAD, t.SUBLOCALIDAD,
                              t.CODPOSTAL, t.TELEFONO, t.MOVIL, t.EMAIL
			        from o02agpe0.tb_tomadores t,  o02agpe0.tb_colectivos c, o02agpe0.tb_polizas p, o02agpe0.tb_provincias provT
			        where t.codprovincia = provT.codprovincia
					and c.ciftomador=t.ciftomador
					and c.codentidad=t.codentidad
                    and c.id=p.idcolectivo
				    and p.idestado in (4, 8, 14, 15, 16)
					and t.fecha_envio_bd_unif is null;

begin
  dbms_output.put_line(lc || ': ## INICIO PROCESO RESTO TOMADORES ##');
	 for rTomador in cTomadores
      loop
	     begin
	        vTomadoresTotales:=vTomadoresTotales+1;
			pintarErrorObligatorios(l_SISTEMA_ORIGEN,l_FECHA_INCREMENTO,rTomador.codEntidad,rTomador.cifTomador);
			insertarTomador(rTomador.codEntidad,rTomador.cifTomador,rTomador.nombreTom,rTomador.claveVia,rTomador.domicilio,
			rTomador.numVia, rTomador.piso,rTomador.bloque,rTomador.escalera, rTomador.codPostal,rTomador.codProvincia,
			rTomador.codComunidadTom,rTomador.codLocalidad,rTomador.subLocalidad , rTomador.telefono, rTomador.movil ,rTomador.email);			
		exception
		when datosNull then
		     vPersonasMal:=vPersonasMal+1;
			 pq_utl.log('excepcion de datos null lanzada y capturada al insertar tomador con nif !!'||rTomador.cifTomador);
		when others then
		okPersonas:='ERROR';
			pq_utl.log(lc||sqlcode || 'error al insertar tomador' || l_line_plz || ' [' || SQLERRM || ']');
		end;
	  end loop;

 END generarRestoTom;

 PROCEDURE generarRestoAseg  AS
 cursor cAsegurados is select a.codentidad, a.nifcif, a.id,nvl(a.razonsocial, a.nombre) as nombreAseg, a.apellido1, a.apellido2,
						case when a.TIPOIDENTIFICACION = 'NIF' then '51' when a.TIPOIDENTIFICACION = 'CIF' then '01'  else '53' end as tipoDocumento,
						case when a.TIPOIDENTIFICACION = 'CIF' then 'J' else 'F' end as tipoIdent, a.NUMSEGSOCIAL, a.REGIMENSEGSOCIAL,
						a.CLAVEVIA, a.DIRECCION, a.NUMVIA, a.PISO, a.BLOQUE, a.ESCALERA, a.CODPOSTAL, a.CODPROVINCIA, prov.CODCOMUNIDAD - 60 as comunidadAseg, a.CODLOCALIDAD,
						a.SUBLOCALIDAD, a.TELEFONO, a.MOVIL, a.EMAIL
						from o02agpe0.tb_asegurados a,
						o02agpe0.tb_polizas p,
						o02agpe0.tb_provincias prov
						where  a.codprovincia = prov.codprovincia
						and p.idasegurado=a.id
						and p.idestado in (4, 8, 14, 15, 16)
						and a.fecha_envio_bd_unif is null
            /* Pet. 62719 ** MODIF TAM (15.01.2021) ** Inicio */
            /* No se podrán enviar en el fichero pólizas cuyo asegurado se encuentre bloqueado */
            and (p.idasegurado NOT IN (select bloqA.Id_Asegurado from o02agpe0.tb_bloqueos_asegurados bloqA where bloqA.Idestado_Aseg = 'B'));

begin
  dbms_output.put_line(lc || ': ## INICIO PROCESO RESTO ASEGURADOS ##');
   for rAsegurado in cAsegurados
    loop
	begin
		vAseguradosTotales:=vAseguradosTotales+1;
		pintarErrorObligatorios(l_SISTEMA_ORIGEN,l_FECHA_INCREMENTO,rAsegurado.codEntidad,rAsegurado.nifcif);
		insertarAsegurado(rAsegurado.codEntidad,rAsegurado.nombreAseg, rAsegurado.apellido1,rAsegurado.apellido2,rAsegurado.tipoDocumento,
		rAsegurado.nifcif,rAsegurado.tipoIdent,rAsegurado.id,rAsegurado.NUMSEGSOCIAL,rAsegurado.REGIMENSEGSOCIAL,rAsegurado.claveVia,
		rAsegurado.direccion,rAsegurado.numVia, rAsegurado.piso,rAsegurado.bloque,rAsegurado.escalera, rAsegurado.codPostal,
		rAsegurado.codProvincia,rAsegurado.comunidadAseg,rAsegurado.codLocalidad,rAsegurado.subLocalidad ,rAsegurado.telefono,
		rAsegurado.movil ,rAsegurado.email);
	exception
	when datosNull then
		 vAseguradosMal:=vAseguradosMal+1;
		 pq_utl.log('excepcion de datos nulos lanzada y capturada al insertar asegurado!!'|| rAsegurado.nifcif);
	when others then
		okPersonas:='ERROR';
		pq_utl.log(lc||sqlcode || 'error al insertar asegurado' || l_line_plz || ' [' || SQLERRM || ']');
end;
	end loop;


 END generarRestoAseg;

 PROCEDURE generarCambioTomCol AS
 
 Cursor CambioTomColPol is select a.codentidad codEntidadAseg, a.nifcif nifAseg, a.id as idAseg,
				l.codplan || '/' || p.referencia as contrato, to_char(p.fecha_vigor, 'YYYYMMDD') as fechaVigor,
                 case when l.codlinea < 400 then 'Parcelas' else 'Explotaciones' end as objetoAsegurado, lpad(c.codentidad,4,'0') || lpad(p.oficina,4,'0') as redComercial,
                 pp.iban || pp.cccbanco as cobroIban, nvl(a.razonsocial, a.nombre) as nombreAseg,
				 a.apellido1 as apellido1Aseg, a.apellido2 as apellido2Aseg, t.RAZONSOCIAL as nombreTom,
                 case when a.TIPOIDENTIFICACION = 'NIF' then '51' when a.TIPOIDENTIFICACION = 'CIF' then '01'  else '53' end as tipoDocumentoAseg,
                 case when a.TIPOIDENTIFICACION = 'CIF' then 'J' else 'F' end as tipoIdentAseg, a.NUMSEGSOCIAL, a.REGIMENSEGSOCIAL,
                 a.CLAVEVIA as claveViaAseg, a.DIRECCION as direccionAseg, a.NUMVIA numViaAseg, a.PISO as pisoAseg, a.BLOQUE as bloqueAseg, a.ESCALERA as escaleraAseg,
                 a.CODPOSTAL as codPostalAseg, a.CODPROVINCIA as codProvinciaAseg , prov.CODCOMUNIDAD - 60 as codComunidadAseg,
				 a.CODLOCALIDAD as codLocalidadAseg,
                 a.SUBLOCALIDAD as subLocalidadAseg, a.TELEFONO as telefonoAseg, a.MOVIL as movilAseg, a.EMAIL as emailAseg,
				 t.ciftomador as cifTomador,t.codentidad as codEntidadTom,
                 t.CLAVEVIA as claveViaTom, t.DOMICILIO as domicilioTom, t.NUMVIA as numViaTom, t.PISO as pisoTom, t.BLOQUE as bloqueTom, t.ESCALERA as escaleraTom,
				 t.CODPROVINCIA codProvinciaTom, provT.CODCOMUNIDAD - 60 as codComunidadTom, t.CODLOCALIDAD as codLocalidadTom,
				 t.SUBLOCALIDAD as subLocalidadTom,
                 t.CODPOSTAL as codPostalTom, t.TELEFONO as telefonoTom, t.MOVIL as movilTom, t.EMAIL as emailTom,
				 p.externa as polExterna,c.entmediadora as entMediadora ,c.subentmediadora as subEntMediadora,l.codlinea as codLinea, p.idpoliza, p.fechaenvio, p.codmodulo, p.idestado
                 from o02agpe0.tb_polizas p, o02agpe0.tb_colectivos c, o02agpe0.tb_tomadores t, o02agpe0.tb_asegurados a,
                 o02agpe0.tb_pagos_poliza pp, o02agpe0.tb_lineas l, o02agpe0.tb_provincias prov, o02agpe0.tb_provincias provT
                 where p.idcolectivo = c.id and c.codentidad = t.codentidad and c.ciftomador = t.ciftomador and p.idasegurado = a.id and
                 p.idpoliza = pp.idpoliza(+) and p.lineaseguroid = l.lineaseguroid and a.codprovincia = prov.codprovincia and
                 t.codprovincia = provT.codprovincia and p.idestado in(4,8,14,15,16) and p.tiporef = 'P'
                and
                 (p.fecha_envio_bd_unif is null or p.fecha_envio_bd_unif<sysdate)
		             and p.idcolectivo in (
                                   select colec.id from o02agpe0.tb_colectivos colec join
                                   o02agpe0.tb_historico_colectivos hc  on (hc.idcolectivo=colec.id)
										               where
											             hc.tipooperacion='M'
							                     and
										               to_char(hc.Fechacambio,'dd/mm/yyyy') = to_char(sysdate,'dd/mm/yyyy')
                      						 and
										               colec.ciftomador!=(select hc.ciftomador from o02agpe0.tb_historico_colectivos hc
										                                     where
                                                               hc.tipooperacion='M'
                                                               and hc.idcolectivo=colec.id
                                                               and  hc.fechacambio=
                                                    (select max(hci.fechacambio)
                                                    from o02agpe0.tb_historico_colectivos hci
                                                    where hci.tipooperacion='M'

                                                   and hci.idcolectivo= hc.idcolectivo
                                                    and hci.fechacambio<=
                                                    (select max (c.fechacambio)
                                                    from  o02agpe0.tb_colectivos c
                                                    where
                                                    c.id=hci.idcolectivo))))
            /* Pet. 62719 ** MODIF TAM (15.01.2021) ** Inicio */
            /* No se podrán enviar en el fichero pólizas cuyo asegurado se encuentre bloqueado */
            and (p.idasegurado NOT IN (select bloqA.Id_Asegurado from o02agpe0.tb_bloqueos_asegurados bloqA where bloqA.Idestado_Aseg = 'B'));


Cursor CambioTomColSin is select to_char(sysdate, 'YYYYMMDD') as fechaIncremento, a.codentidad, a.nifcif, a.id idAseg,
		to_number(s.numsiniestro) as num_expte,
        to_char(s.fechaocurrencia, 'YYYYMMDD') as fechaOcurrencia, l.codplan || '/' || p.referencia as contrato,
		s.numerosiniestro as num_expte_otra_cia,
        t.ciftomador ,s.ID as idSiniestro
  from o02agpe0.tb_siniestros s,
       o02agpe0.tb_polizas    p,
       o02agpe0.tb_asegurados a,
       o02agpe0.tb_colectivos c,
       o02agpe0.tb_tomadores  t,
       o02agpe0.tb_lineas     l
 where s.idpoliza = p.idpoliza
   and p.idasegurado = a.id
   and p.idcolectivo = c.id
   and c.codentidad = t.codentidad
   and c.ciftomador = t.ciftomador
   and p.lineaseguroid = l.lineaseguroid
   and s.estado = 3
   and (s.FECHA_ENVIO_BD_unif is null or  s.fecha_envio_bd_unif<sysdate)
   and p.idcolectivo in  ( select colec.id from o02agpe0.tb_colectivos colec join
                                   o02agpe0.tb_historico_colectivos hc  on (hc.idcolectivo=colec.id)
										               where
											             hc.tipooperacion='M'
							                     and
										               to_char(hc.Fechacambio,'dd/mm/yyyy') = to_char(sysdate,'dd/mm/yyyy')
                      						 and
										               colec.ciftomador!=(select hc.ciftomador from o02agpe0.tb_historico_colectivos hc
										                                     where
                                                               hc.tipooperacion='M'
                                                               and hc.idcolectivo=colec.id
                                                               and  hc.fechacambio=
                                                    (select max(hci.fechacambio)
                                                    from o02agpe0.tb_historico_colectivos hci
                                                    where hci.tipooperacion='M'

                                                   and hci.idcolectivo= hc.idcolectivo
                                                    and hci.fechacambio<=
                                                    (select max (c.fechacambio)
                                                    from  o02agpe0.tb_colectivos c
                                                    where
                                                    c.id=hci.idcolectivo))))
   /* Pet. 62719 ** MODIF TAM (15.01.2021) ** Inicio */
   /* No se podrán enviar en el fichero pólizas cuyo asegurado se encuentre bloqueado */
   and (p.idasegurado NOT IN (select bloqA.Id_Asegurado from o02agpe0.tb_bloqueos_asegurados bloqA where bloqA.Idestado_Aseg = 'B'));

begin
	dbms_output.put_line(lc || ': ## INICIO PROCESO CAMBIO TOMADOR COLECTIVO ##');
	  -- Procesamos las PÓLIZAS: Se irán completando también los ficheros de personas, direcciones, teléfonos y e-mails
      dbms_output.put_line(lc || ': Recuperando las polizas contratadas no enviadas a BD Unificada ');
      dbms_output.put_line(l_query_polizas);
    for rPoliza in CambioTomColPol loop
		BEGIN
			vPolizasTotales:=vPolizasTotales+1;

			l_COBRO_IBAN:=rPoliza.cobroIban;
			if( rPoliza.polExterna = 1 ) then
				begin
					v_num_cuentaAsegurado := PQ_ENVIO_POLIZAS_RENOVABLES.getCuentaAsegurado(rPoliza.nifAseg,rPoliza.codEntidadAseg,rPoliza.codLinea,rPoliza.entmediadora,rPoliza.subentmediadora);
					IF (v_num_cuentaAsegurado is not null AND v_num_cuentaAsegurado(1) is not null) then
						pq_utl.log(lc, 'CCC encontrada: ' || v_num_cuentaAsegurado(1));
						l_COBRO_IBAN := v_num_cuentaAsegurado(0)|| v_num_cuentaAsegurado(1);
					ELSE
						pq_utl.log(lc, 'CUENTA NO ENCONTRADA');
					END IF;
				EXCEPTION
				WHEn OTHERS THEN
					pq_utl.log(lc|| sqlcode || 'error al obtener el numero de cuenta externo [' || SQLERRM || ']');
				END;
				END IF;
		  	begin
			vAseguradosTotales:=vAseguradosTotales+1;
			pintarErrorObligatorios(l_SISTEMA_ORIGEN,l_FECHA_INCREMENTO,rPoliza.codEntidadAseg,rPoliza.nifAseg);
			insertarAsegurado(rPoliza.codEntidadAseg,rPoliza.nombreAseg, rPoliza.apellido1Aseg,rPoliza.apellido2Aseg,rPoliza.tipoDocumentoAseg,
			rPoliza.nifAseg,rPoliza.tipoIdentAseg,rPoliza.idAseg,rPoliza.NUMSEGSOCIAL,rPoliza.REGIMENSEGSOCIAL,rPoliza.claveViaTom,
			rPoliza.direccionAseg,rPoliza.numViaAseg, rPoliza.pisoAseg,rPoliza.bloqueAseg,rPoliza.escaleraAseg, rPoliza.codPostalAseg,
			rPoliza.codProvinciaAseg,rPoliza.codComunidadAseg,rPoliza.codLocalidadAseg,rPoliza.subLocalidadAseg ,rPoliza.telefonoAseg,
			rPoliza.movilAseg ,rPoliza.emailAseg);
      
      /* Pet. 62719 ** MODIF TAM (19.02.2021) ** Inicio */
      /* Obtenemos el nuevo dato de la fecha de vencimiento del contrato */
       l_fechafin_contrato := obtener_fechafin_contrato(rPoliza.Fechaenvio, rPoliza.Codlinea, rPoliza.Codmodulo, rPoliza.idestado, rPoliza.idpoliza);
      /* Pet. 62719 ** MODIF TAM (19.02.2021) ** Fin  */ 

			--Línea para la póliza con el asegurado
    	l_line_plz := l_SISTEMA_ORIGEN || ';' || l_FECHA_INCREMENTO || ';;' || l_IDEMPRESA || ';' || rPoliza.codEntidadAseg || ';' ||
    	              rPoliza.nifAseg  || ';' ||
                    rPoliza.idAseg || ';' || rPoliza.CONTRATO || ';' || rPoliza.fechaVigor || ';;;' || rPoliza.OBJETOASEGURADO || ';;Aseg;' || rPoliza.redComercial || ';01;' ||
                    l_COBRO_IBAN || ';0;1;1;1;' ||l_fechafin_contrato;
                         
      
      
			UTL_FILE.PUT_LINE (f_fichero_plz, utl_raw.cast_to_varchar2(utl_raw.convert(utl_raw.cast_to_raw(l_line_plz || chr(13)), 'UTF8', nls_characterset)));
		
		exception
		when datosNull then
			 vAseguradosMal:=vAseguradosMal+1;
			 pq_utl.log('excepcion de datos nulos lanzada y capturada al insertar asegurado!!'|| rPoliza.nifAseg);
		when others then
			okPersonas:='ERROR';
			pq_utl.log(lc|| sqlcode || 'error al insertar asegurado' || l_line_plz || ' [' || SQLERRM || ']');
		end;

		begin
		 vTomadoresTotales:=vTomadoresTotales+1;
			pintarErrorObligatorios(l_SISTEMA_ORIGEN,l_FECHA_INCREMENTO,rPoliza.codEntidadAseg,rPoliza.cifTomador);
			insertarTomador(rPoliza.codEntidadTom,rPoliza.cifTomador,rPoliza.nombreTom,rPoliza.claveViaTom,rPoliza.domicilioTom,
			rPoliza.numViaTom, rPoliza.pisoTom,rPoliza.bloqueTom,rPoliza.escaleraTom, rPoliza.codPostalTom,rPoliza.codProvinciaTom,
			rPoliza.codComunidadTom,rPoliza.codLocalidadTom,rPoliza.subLocalidadTom , rPoliza.telefonoTom, rPoliza.movilTom ,rPoliza.emailTom);
      
      /* Pet. 62719 ** MODIF TAM (19.02.2021) ** Inicio */
      /* Obtenemos el nuevo dato de la fecha de vencimiento del contrato */
       l_fechafin_contrato := obtener_fechafin_contrato(rPoliza.Fechaenvio, rPoliza.Codlinea, rPoliza.Codmodulo, rPoliza.idestado, rPoliza.idpoliza);
      /* Pet. 62719 ** MODIF TAM (19.02.2021) ** Fin  */ 
 
      
			 -- Línea para la póliza con el tomador
			 l_line_plz := l_SISTEMA_ORIGEN || ';' || l_FECHA_INCREMENTO || ';;' || l_IDEMPRESA || ';' || rPoliza.codEntidadTom || ';' || rPoliza.cifTomador  || ';' ||
					       ';' || rPoliza.contrato || ';' || rPoliza.fechaVigor|| ';;;' || rPoliza.objetoAsegurado || ';;Toma;' || rPoliza.redComercial || ';01;' ||
					       l_COBRO_IBAN || ';0;1;1;1;' ||l_fechafin_contrato;

			UTL_FILE.PUT_LINE (f_fichero_plz, utl_raw.cast_to_varchar2(utl_raw.convert(utl_raw.cast_to_raw(l_line_plz || chr(13)), 'UTF8', nls_characterset)));
		
		exception
		when datosNull then
			--okPersonas:=false;
			 vTomadoresMal:=vTomadoresMal+1;
			 pq_utl.log('excepcion de datos null lanzada y capturada al insertar tomador con NIF!!'||rPoliza.cifTomador);
		when others then
			okPersonas:='ERROR';
			pq_utl.log(lc|| sqlcode || 'error al insertar tomador' || l_line_plz || ' [' || SQLERRM || ']');
		end;

		update  o02agpe0.TB_POLIZAS set o02agpe0.TB_POLIZAS.fecha_envio_bd_unif=sysdate where o02agpe0.TB_POLIZAS.idPoliza=rPoliza.idPoliza;
		vPolizasEnviadas:=vPolizasEnviadas+1;
exception
when polizaMal then
   pq_utl.log('error al insertar poliza; ref contrato es null '||rPoliza.idpoliza);
when others then
	pq_utl.log(sqlcode || 'error en el proceso de actualizar poliza' || l_line_plz || ' [' || SQLERRM || ']');
 end;

end loop;
dbms_output.put_line(lc || ': Fin del proceso de pólizas cambio Tomador Colectivo ');


for rSiniestros in CambioTomColSin
      loop

      vSiniestrosTotales:=vSiniestrosTotales+1;
         --Procesamos los siniestros:

		if(rSiniestros.num_expte is null or rSiniestros.contrato is null) then
			vsiniestrosMal:=vsiniestrosMal+1;
			raise siniestroMal;
		end if;
		pintarErrorObligatorios(l_SISTEMA_ORIGEN,l_FECHA_INCREMENTO,rSiniestros.codEntidad,rSiniestros.nifcif);

        begin
		--Línea para el asegurado
        l_line_str :=l_SISTEMA_ORIGEN || ';' || l_FECHA_INCREMENTO || ';;' || l_IDEMPRESA || ';' || rSiniestros.codentidad || ';'
		|| rSiniestros.nifcif || ';' ||rSiniestros.idaseg|| ';' || rSiniestros.num_expte || ';' || rSiniestros.fechaOcurrencia || ';' || rSiniestros.contrato || ';;' ||
		rSiniestros.num_expte_otra_cia || ';Aseg;VV;0;1;1;1';
        UTL_FILE.PUT_LINE (f_fichero_str, utl_raw.cast_to_varchar2(utl_raw.convert(utl_raw.cast_to_raw(l_line_str || chr(13)), 'UTF8', nls_characterset)));
        vSiniestrosEnviados:=vSiniestrosEnviados+1;

         --Línea para el tomador
         l_line_str :=l_SISTEMA_ORIGEN || ';' || l_FECHA_INCREMENTO || ';;' || l_IDEMPRESA || ';' || rSiniestros.codentidad || ';'
		 || rSiniestros.nifcif || ';;' || rSiniestros.num_expte || ';' || rSiniestros.fechaOcurrencia || ';' || rSiniestros.contrato || ';;'
		 || rSiniestros.num_expte_otra_cia || ';Toma;VV;0;1;1;1';
         UTL_FILE.PUT_LINE (f_fichero_str,utl_raw.cast_to_varchar2(utl_raw.convert(utl_raw.cast_to_raw(l_line_str || chr(13)), 'UTF8', nls_characterset)));
        vSiniestrosEnviados:=vSiniestrosEnviados+1;

        update  o02agpe0.TB_SINIESTROS set o02agpe0.TB_SINIESTROS.fecha_envio_bd_unif=sysdate where o02agpe0.TB_SINIESTROS.id=rSiniestros.idSiniestro;
        exception
		when siniestroMal then
			pq_utl.log('error de datos nulos al insertar el siniestro'|| rSiniestros.contrato);
        WHEN others then
			pq_utl.log(sqlcode || 'error de escritura'|| l_line_str || ' [' || SQLERRM || ']');
        end;

      END LOOP;
      dbms_output.put_line(lc || ': Fin del proceso de siniestros ');

   EXCEPTION
   	  WHEN NO_DATA_FOUND THEN

         dbms_output.put_line(sqlcode || ' No se han encontrado SINIESTROS para enviar a la base de datos unificada' || ' [' || SQLERRM || ']');

      when others then
	  okSiniestros:='ERROR';
          pq_utl.log(sqlcode || ' Error indefinido en generarCambioTomCol' || ' [' || SQLERRM || ']');
 END generarCambioTomCol;
 
 
/* Pet. 58896 ** Modif Tam (01.10.2019) */
 procedure generarEstadisticas AS
 

       -- Conteo de polizas Emitidas enviadas a BD Unificada 
       Cursor cConteoPolOK is SELECT count(*) as conteopolEnv
              from o02agpe0.TB_POLIZAS pol 
              where to_date(pol.fecha_envio_bd_unif) <= sysdate 
			    and (pol.idestado = 14 OR pol.idestado = 8)
				and (pol.fecha_vto is null OR pol.fecha_vto >= sysdate);

       -- Conteo de polizas Rechazadas enviadas a BD Unificada
       Cursor cConteoPolNoOK is SELECT count(*) as conteopolAnul
              from o02agpe0.TB_POLIZAS pol 
              where to_date(pol.fecha_envio_bd_unif) <= sysdate
				       and ((pol.idestado = 15 OR pol.idestado = 16)
				or ((pol.idestado = 14 OR pol.idestado = 8)
				       and (pol.fecha_vto is not null and pol.fecha_vto < sysdate)));

       -- Conteo de Siniestros enviados a BD Unificada              
       Cursor cConteoSin is Select count(*) as conteoSin 
              from o02agpe0.Tb_Siniestros sin 
              where to_date(sin.fecha_envio_bd_unif) <= sysdate and SIN.ESTADO = 3;      

       -- Conteo de Asegurados y Tomadores enviados a BD Unificada       
       Cursor cConteoCliente is SELECT SUM(T1.sumascli) as conteocliente FROM
              (SELECT COUNT(*) sumascli  
                 FROM o02agpe0.TB_ASEGURADOS ASEG
                WHERE TO_DATE(ASEG.FECHA_ENVIO_BD_UNIF) <= sysdate
              union all
               SELECT count(*) sumascli
                 FROM o02agpe0.TB_TOMADORES TOM
                WHERE TO_DATE(TOM.FECHA_ENVIO_BD_UNIF) <= sysdate) T1;
              
       BEGIN
       
   dbms_output.put_line(lc || ': ## INICIO PROCESO ESTADÍSTICAS ##');

   dbms_output.put_line('Abriendo fichero');

   -- ********************************************************
   -- Abrimos los ficheros de salida con extension .CSV
   -- ********************************************************

    /* Conteo CLIENTES => Conteo de Asegurados y Tomadores Enviados a BD Unificada */
	 for rConteoCliente in cConteoCliente
      loop
      
         l_ConteoCliente := rConteoCliente.Conteocliente;
      Begin 
         l_line_estadistica :=l_SISTEMA_ORIGEN || ';' || l_IDEMPRESA || ';' || l_COD_CLIENTE || ';' || l_ConteoCliente;
   			 UTL_FILE.PUT_LINE (f_fichero_estadistica, utl_raw.cast_to_varchar2(utl_raw.convert(utl_raw.cast_to_raw(l_line_estadistica || chr(13)), 'UTF8', nls_characterset)));
          
         dbms_output.put_line(lc || 'Linea de Clientes enviados :' || l_line_estadistica);
      exception
		
        WHEN others then
           okEstadistica:='ERROR';
			     pq_utl.log(lc||sqlcode || 'error de escritura'|| l_line_estadistica || ' [' || SQLERRM || ']');
        end;

      END LOOP;
      dbms_output.put_line(lc || ': Fin del proceso de clientes ');

   /* Conteo Contratatos ACTIVOS => Conteo de pólizas Enviadas a BD Unificadas en Estado 'Enviadas Correctas' y 'Emitidas*/
	 for rConteoPolOK in cConteoPolOK
      loop
      
         l_ConteoPolOk := rConteoPolOK.Conteopolenv;
      Begin 
         l_line_estadistica :=l_SISTEMA_ORIGEN || ';' || l_IDEMPRESA || ';' || l_COD_CONACT || ';' || l_ConteoPolOK;
   			 UTL_FILE.PUT_LINE (f_fichero_estadistica, utl_raw.cast_to_varchar2(utl_raw.convert(utl_raw.cast_to_raw(l_line_estadistica || chr(13)), 'UTF8', nls_characterset)));         

         dbms_output.put_line(lc || 'Linea de Contratos Activos :' || l_line_estadistica);
      exception
		
        WHEN others then
           okEstadistica:='ERROR';        
			     pq_utl.log(lc||sqlcode || 'error de escritura'|| l_line_estadistica || ' [' || SQLERRM || ']');
        end;

      END LOOP;
      dbms_output.put_line(lc || ': Fin del proceso de Polizas Activas ');
      
   /* Conteo Contratatos ANULADOS => Conteo de pólizas Enviadas a BD Unificadas en Estado 'Anuladas' y 'Rescindidas*/
	 for rConteoPolNoOK in cConteoPolNoOK
      loop
      
         l_ConteoPolNoOk := rConteoPolNoOK.Conteopolanul;
      Begin 
         l_line_estadistica :=l_SISTEMA_ORIGEN || ';' || l_IDEMPRESA || ';' || l_COD_CONANU|| ';' || l_ConteoPolNoOK;
   			 UTL_FILE.PUT_LINE (f_fichero_estadistica, utl_raw.cast_to_varchar2(utl_raw.convert(utl_raw.cast_to_raw(l_line_estadistica || chr(13)), 'UTF8', nls_characterset)));
         
         dbms_output.put_line(lc || 'Linea de Contratos Anulados :' || l_line_estadistica);
      exception
		
        WHEN others then
           okEstadistica:='ERROR';
 			     pq_utl.log(lc||sqlcode || 'error de escritura'|| l_line_estadistica || ' [' || SQLERRM || ']');
        end;

      END LOOP;
      dbms_output.put_line(lc || ': Fin del proceso dePolizas Anuladas ');
      
      /* Conteo Siniestros ACTIVOS => Conteo de pólizas Enviadas a BD Unificadas en Estado 'Anuladas' y 'Rescindidas*/
	 for rConteoSin in cConteoSin
      loop
      
         l_conteoSin := rConteoSin.Conteosin;
      Begin 
         l_line_estadistica :=l_SISTEMA_ORIGEN || ';' || l_IDEMPRESA || ';' || l_COD_SINACT|| ';' || l_ConteoSin;
   			 UTL_FILE.PUT_LINE (f_fichero_estadistica, utl_raw.cast_to_varchar2(utl_raw.convert(utl_raw.cast_to_raw(l_line_estadistica || chr(13)), 'UTF8', nls_characterset)));
                  
         dbms_output.put_line(lc || 'Linea de Siniestros Activos :' || l_line_estadistica);
      exception
		
        WHEN others then
           okEstadistica:='ERROR';
      		 pq_utl.log(lc||sqlcode || 'error de escritura'|| l_line_estadistica || ' [' || SQLERRM || ']');
        end;

      END LOOP;
      
      dbms_output.put_line(lc || ': Fin del proceso de Siniestros Activos ');
      
      /* Insertamos las líneas de las operactiones que no aplican a Agroseguro */
   
      l_line_estadistica :=l_SISTEMA_ORIGEN || ';' || l_IDEMPRESA || ';' || l_COD_SINANU|| ';' || l_conteoSinanu;
 		  UTL_FILE.PUT_LINE (f_fichero_estadistica, utl_raw.cast_to_varchar2(utl_raw.convert(utl_raw.cast_to_raw(l_line_estadistica || chr(13)), 'UTF8', nls_characterset)));
     
      l_line_estadistica :=l_SISTEMA_ORIGEN || ';' || l_IDEMPRESA || ';' || l_COD_SINREA|| ';' ||  l_conteoSinrea;
 		  UTL_FILE.PUT_LINE (f_fichero_estadistica, utl_raw.cast_to_varchar2(utl_raw.convert(utl_raw.cast_to_raw(l_line_estadistica || chr(13)), 'UTF8', nls_characterset)));
     
      
      
   EXCEPTION
       when others then
	     okEstadistica:='ERROR';
         pq_utl.log(lc||sqlcode || ' Error indefinido en generarEstadisticas' || ' [' || SQLERRM || ']');
 end generarEstadisticas;
/* Pet. 58896 ** Modif Tam (01.10.2019) Fin */ 

 

 PROCEDURE generarCorreoResumen AS
 aux_mensaje varchar2(2000);
 aux_asunto varchar2(100):='Alerta Agroplus: Resumen de envios a BD Unificada del dia '|| l_fecha_fichero;
 totalesAct number:=0;
 totalesPer number:=0;
  BEGIN


    dbms_output.put_line(lc || ': ## INICIO PROCESO GENERAR CORREO RESUMEN ##'|| chr(13));
	dbms_output.put_line(vSiniestrosTotales|| ': siniestro tratados en total');
    dbms_output.put_line(vSiniestrosEnviados|| ': lineas insertadas en fichero de siniestros ');
	dbms_output.put_line(vPolizasTotales|| ': pólizas tratadas en total');
	dbms_output.put_line(vPolizasEnviadas || ': lineas insertadas en fichero de pólizas');
    dbms_output.put_line(vAseguradosActualizados+vTomadoresActualizados|| ': lineas insertadas en el fichero de personas ');
    dbms_output.put_line(vTomadoresTotales || ': tomadores tratados en total');

		dbms_output.put_line(vTomadoresMal|| ': tomadores mal');
			dbms_output.put_line(vAseguradosMal|| ': ssegurados mal');

	dbms_output.put_line(vAseguradosTotales || ': asegurados tratados en total');
	dbms_output.put_line(vaseguradosActualizados|| ': asegurados actualizados');
	dbms_output.put_line(vDireccionesTratadas|| ': domicilios tratados en total');
	dbms_output.put_line(vDireccionesActualizadas || ': lineas insertadas en fichero de domicilios');
	dbms_output.put_line(vTelefonosTratados || ': telefonos tratados en total');
	dbms_output.put_line(vTelefonosActualizados || ': lineas insertadas en fichero de telefonos');
	dbms_output.put_line(vEmailsTratados|| ': emails tratados en total');
    dbms_output.put_line(vEmailsActualizados || ': lineas insertadas en fichero de emails');


	totalesAct:= vAseguradosActualizados+vTomadoresActualizados;
	totalesPer:=vTomadoresTotales+vAseguradosTotales;
		vPersonasMal:=vAseguradosMal+vTomadoresMal;
    aux_mensaje:=l_fecha_fichero|| '_AGRO_SINIESTROS.cvs-EJECUCION '||okSiniestros|| '-('||vSiniestrosTotales||' registros a tratar/'||vSiniestrosEnviados||' registros incluídos ';
	if vSiniestrosMal>0 then
	aux_mensaje:=aux_mensaje||'/'||vSiniestrosMal || ' registros no insertados por errores';
    end if;
	aux_mensaje:=aux_mensaje||')'||chr(13);
	aux_mensaje:=aux_mensaje ||l_fecha_fichero|| '_AGRO_CONTRATOS.cvs-EJECUCION '||okPolizas|| '-('||vPolizasTotales||' registros a tratar/'||vPolizasEnviadas||' registros incluídos ';
	if vPolizasMal>0 then
	aux_mensaje:=aux_mensaje||'/'||vPolizasMal || ' registros no insertados por errores';
	end if;
	aux_mensaje:=aux_mensaje||')'||chr(13);
	aux_mensaje:=aux_mensaje ||l_fecha_fichero|| '_AGRO_PERSONAS.cvs-EJECUCION '||okPersonas|| '-('||totalesPer||' registros a tratar/'||totalesAct||' registros incluídos';
	if vPersonasRepetidas>0 then
	aux_mensaje:=aux_mensaje||'/'||vPersonasRepetidas||' registros repetidos ' ;
	end if;

	if vPersonasMal>0 then
	aux_mensaje:=aux_mensaje||'/'|| vPersonasMal || ' registros no insertados por errores';
	end if;
	aux_mensaje:=aux_mensaje||')'||chr(13);

    aux_mensaje:=aux_mensaje ||l_fecha_fichero|| '_AGRO_DOMICILIOS.cvs-EJECUCION '||okDirecciones|| '-('||vDireccionesTratadas||' registros a tratar/'||vDireccionesActualizadas||' registros incluídos)'|| chr(13);
    aux_mensaje:=aux_mensaje ||l_fecha_fichero|| '_AGRO_TELEFONOS.cvs-EJECUCION '||okTelefonos|| '-('||vTelefonosTratados||' registros a tratar/'||vTelefonosActualizados||' registros incluídos)'|| chr(13);
    aux_mensaje:=aux_mensaje ||l_fecha_fichero|| '_AGRO_EMAILS.cvs-EJECUCION '||okEmails|| '-('||vEmailsTratados||' registros a tratar/'||vEmailsActualizados||' registros incluídos)'|| chr(13);
    /* Pet. 58869 ** Modif TAM (01.10.2019) */
    aux_mensaje:=aux_mensaje ||l_fecha_fichero|| '_AGRO_ESTADISTICA.cvs-EJECUCION '||okEstadistica|| chr(13);    
    /* Pet. 58869 ** Modif TAM (01.10.2019) * Fin*/

	PQ_ENVIO_CORREOS.enviarCorreo(3, aux_asunto, aux_mensaje);

	exception
    when others then
      pq_utl.log(sqlcode || ' Error indefinido en generarCorreoResumen' || ' [' || SQLERRM || ']');
 END generarCorreoResumen;


/* Pet. 62719 ** MODIF TAM (15.01.2021) **/
/* Este procedimiento no se toca por que no deberían llegar a este punto polizas cuyo ASegurado este bloqueado, ya que sí que se ha insertado
/* la validación correspondiente en la consulta de pólizas */

procedure insertarAsegurado(CODIGO_ENTIDAD number ,NOMBRE_ASG varchar2 ,APELLIDO1_ASG varchar2 ,APELLIDO2_ASG varchar2 ,
                             TIPO_DOC_ASG varchar2 ,NIF_ASEGURADO varchar2,TIPO_IDENT_ASG varchar2,ID_ASEGURADO number,SEG_SOCIAL_ASG varchar2,
							 REGIMEN_SEG_SOCIAL number,TIPO_VIA_ASG varchar2,NOMBRE_VIA_ASG varchar2 ,NUMERO_VIA_ASG varchar2,PUERTA_ASG varchar2,
							 BLOQUE_ASG varchar2,ESCALERA_ASG varchar2, CP_ASG number, PROV_ASG number,CCAA_ASG number,TERMINO_ASG number,
							 SUBTERMINO_ASG varchar2,TLF_FIJO_ASG varchar2,TLF_MOVIL_ASG varchar2,EMAIL_ASG varchar2) as
		asegModif boolean:=false;
		fModif date;
        fEnvio date;

	 begin
		begin
			select a.fecha_modificacion, a.fecha_envio_bd_unif into fModif, fEnvio
			from o02agpe0.tb_asegurados a
            where id=ID_ASEGURADO;
			if(fEnvio is null or (fModif is not null and fEnvio<=fModif)) then
		       asegModif:=true;
            end if;
		end;
		 if (asegModif) then

		 begin
		   if arrPersonasIncluidas.FIRST is not null AND arrPersonasIncluidas(CODIGO_ENTIDAD || '-' || NIF_ASEGURADO) is not null THEN
               aniadir := false;
			   vPersonasRepetidas:=vPersonasRepetidas+1;
            END IF;
			exception
				when NO_DATA_FOUND then
				aniadir := true;
         end;
        if (aniadir) then
			if(NOMBRE_ASG is null) then
			  pq_utl.log(lc||' error al insertar ASEGURADO ; NOMBRE es null'|| chr(13));
				raise datosNull;
			end if;


			 -- Dirección
			 BEGIN
			 vDireccionesTratadas:=vDireccionesTratadas+1;
			 l_line_dir := l_SISTEMA_ORIGEN || ';' || l_FECHA_INCREMENTO || ';;' || l_IDEMPRESA || ';' || CODIGO_ENTIDAD || ';' || NIF_ASEGURADO || ';;1;00;' || TIPO_VIA_ASG || ';' || NOMBRE_VIA_ASG || ';' || NUMERO_VIA_ASG || ';' || PUERTA_ASG || ';' ||
				BLOQUE_ASG || ';' || ESCALERA_ASG || ';;' || CP_ASG || ';' || PROV_ASG || ';' || CCAA_ASG || ';11;;;' || TERMINO_ASG || ';' ||
				SUBTERMINO_ASG || ';';
			 UTL_FILE.PUT_LINE (f_fichero_dir, utl_raw.cast_to_varchar2(utl_raw.convert(utl_raw.cast_to_raw(l_line_dir  || chr(13)), 'UTF8', nls_characterset)));
			 vDireccionesActualizadas:=vDireccionesActualizadas+1;
			 EXCEPTION
			 WHEN OTHERS THEN
			    okDirecciones:='ERROR';

			 END;
			 BEGIN
			 -- Teléfono fijo (si lo tiene)
			 if (TLF_FIJO_ASG is not null) THEN
			  vTelefonosTratados:=vTelefonosTratados+1;
			 l_line_tlf := l_SISTEMA_ORIGEN || ';' || l_FECHA_INCREMENTO || ';;' || l_IDEMPRESA || ';' || CODIGO_ENTIDAD || ';' || NIF_ASEGURADO || ';;1;00;' || TLF_FIJO_ASG;
			 UTL_FILE.PUT_LINE (f_fichero_tlf, utl_raw.cast_to_varchar2(utl_raw.convert(utl_raw.cast_to_raw(l_line_tlf || chr(13)), 'UTF8', nls_characterset)));
			vTelefonosActualizados:=vTelefonosActualizados+1;
			end if;
			-- Teléfono móvil (si lo tiene)
			 if (TLF_MOVIL_ASG is not null) then
			 vTelefonosTratados:=vTelefonosTratados+1;
				l_line_tlf := l_SISTEMA_ORIGEN || ';' || l_FECHA_INCREMENTO || ';;' || l_IDEMPRESA || ';' || CODIGO_ENTIDAD || ';' || NIF_ASEGURADO || ';;2;00;' || TLF_MOVIL_ASG;
				UTL_FILE.PUT_LINE (f_fichero_tlf, utl_raw.cast_to_varchar2(utl_raw.convert(utl_raw.cast_to_raw(l_line_tlf || chr(13)), 'UTF8', nls_characterset)));
				vTelefonosActualizados:=vTelefonosActualizados+1;

			 end if;

			EXCEPTION
			WHEN OTHERS THEN
			   okTelefonos:='ERROR';
			END;
			BEGIN
			 if (EMAIL_ASG is not null) then
			 vEmailsTratados:=vEmailsTratados+1;
				l_line_mail := l_SISTEMA_ORIGEN || ';' || l_FECHA_INCREMENTO || ';;' || l_IDEMPRESA || ';' || CODIGO_ENTIDAD || ';' || NIF_ASEGURADO || ';;1;00;' || EMAIL_ASG;
				UTL_FILE.PUT_LINE (f_fichero_mail, utl_raw.cast_to_varchar2(utl_raw.convert(utl_raw.cast_to_raw(l_line_mail || chr(13)), 'UTF8', nls_characterset)));
				   vEmailsActualizados:=vEmailsActualizados+1;
			 end if;
			 EXCEPTION
			 WHEN OTHERS THEN
			    okEmails:='ERROR';
			 END;

			   -- Obtenemos la fecha de antiguedad
			 select TO_CHAR(min(p.fechaenvio), 'YYYYMMDD') into l_FECHA_ANTIGUEDAD
			 from o02agpe0.tb_polizas p
		     join o02agpe0.tb_asegurados a on (p.idasegurado=a.id)
			where p.idestado in (4, 8, 14, 15, 16)
			 and p.tiporef = 'P'
			 and a.nifcif=NIF_ASEGURADO;

			-- Línea para la persona asegurado (requiere controlar datos añadidos de otras pólizas)
			l_line_prs := l_SISTEMA_ORIGEN || ';' || l_FECHA_INCREMENTO || ';' || l_FECHA_ANTIGUEDAD || ';;' || l_IDEMPRESA || ';' || CODIGO_ENTIDAD || ';' ||
			replace(NOMBRE_ASG,';',':') || ';' || replace(APELLIDO1_ASG,';',':') || ';' || replace(APELLIDO2_ASG,';',':') || ';;' || TIPO_DOC_ASG || ';' || NIF_ASEGURADO || ';;;' ||
			TIPO_IDENT_ASG || ';;' || SEG_SOCIAL_ASG || ';' || REGIMEN_SEG_SOCIAL || ';;;;;;;;;;;01;;;;;;;;;;;;;;;;;;;;;;;';
			 UTL_FILE.PUT_LINE (f_fichero_prs, utl_raw.cast_to_varchar2(utl_raw.convert(utl_raw.cast_to_raw(l_line_prs || chr(13)), 'UTF8', nls_characterset)));

			  vAseguradosActualizados:=vAseguradosActualizados+1;
			  arrPersonasIncluidas(CODIGO_ENTIDAD || '-' || NIF_ASEGURADO ) := 1;
     		  update tb_asegurados a set a.fecha_envio_bd_unif=sysdate where a.id=ID_ASEGURADO;
		end if;
			else
			  vAseguradosTotales:= vAseguradosTotales-1;
      end if;
 end insertarAsegurado;

 procedure insertarTomador(CODIGO_ENTIDAD number,CIF_TOMADOR varchar2,NOMBRE_TOM varchar2,TIPO_VIA_TOM varchar2,NOMBRE_VIA_TOM varchar2,
                           NUMERO_VIA_TOM varchar2,PUERTA_TOM varchar2,
							BLOQUE_TOM varchar2,ESCALERA_TOM varchar2,CP_TOM number,PROV_TOM number,CCAA_TOM number,TERMINO_TOM number,
							SUBTERMINO_TOM varchar2,TLF_FIJO_TOM varchar2,TLF_MOVIL_TOM varchar2,EMAIL_TOM varchar2) as
		tomaModif boolean:=false;
		fModif date;
        fEnvio date;
	begin
		begin
			 select t.fecha_modificacion, t.fecha_envio_bd_unif into fModif, fEnvio
			 from o02agpe0.tb_tomadores t
             where t.ciftomador=CIF_TOMADOR and t.codentidad =CODIGO_ENTIDAD;

			if(fEnvio is null or (fModif is not null and fEnvio<=fModif)) then
              tomaModif:=true;
            end if;
		end;

		 if(tomaModif) then
		begin
			if arrPersonasIncluidas.FIRST is not null AND arrPersonasIncluidas(CODIGO_ENTIDAD || '-' || CIF_TOMADOR) is not null THEN
               aniadir := false;
			   vPersonasRepetidas:=vPersonasRepetidas+1;
            END IF;
			exception
				when NO_DATA_FOUND then
				aniadir := true;
         end;

        if (aniadir) then
		if(NOMBRE_TOM=null) then
			pq_utl.log(lc|| ' error al insertar tomador; NOMBRE es null'|| chr(13));
				raise datosNull;
			end if;

		   --Direccion
		   vDireccionesTratadas:=vDireccionesTratadas+1;
			 l_line_dir := l_SISTEMA_ORIGEN || ';' || l_FECHA_INCREMENTO || ';;' || l_IDEMPRESA || ';' || CODIGO_ENTIDAD || ';' || CIF_TOMADOR || ';' ||
				';1;00;' || TIPO_VIA_TOM || ';' || replace(NOMBRE_VIA_TOM,';',':') || ';' || NUMERO_VIA_TOM || ';' || PUERTA_TOM || ';' ||
				BLOQUE_TOM || ';' || ESCALERA_TOM || ';;' || CP_TOM || ';' || PROV_TOM || ';' || CCAA_TOM || ';11;;;' || TERMINO_TOM || ';' ||
				SUBTERMINO_TOM || ';';
			 UTL_FILE.PUT_LINE (f_fichero_dir, utl_raw.cast_to_varchar2(utl_raw.convert(utl_raw.cast_to_raw(l_line_dir || chr(13)), 'UTF8', nls_characterset)));
			vDireccionesActualizadas:=vDireccionesActualizadas+1;
			 -- Teléfono fijo (si lo tiene)
			 if(TLF_FIJO_TOM is not null) then
			 vTelefonosTratados:=vTelefonosTratados+1;
			 l_line_tlf := l_SISTEMA_ORIGEN || ';' || l_FECHA_INCREMENTO || ';;' || l_IDEMPRESA || ';' || CODIGO_ENTIDAD || ';' || CIF_TOMADOR || ';' ||
				';1;00;' || TLF_FIJO_TOM;
			 UTL_FILE.PUT_LINE (f_fichero_tlf, utl_raw.cast_to_varchar2(utl_raw.convert(utl_raw.cast_to_raw(l_line_tlf || chr(13)), 'UTF8', nls_characterset)));
			 vTelefonosActualizados:=vTelefonosActualizados+1;
			 end if;
			 -- Teléfono móvil (si lo tiene)
			 if (TLF_MOVIL_TOM is not null) then
			 vTelefonosTratados:=vTelefonosTratados+1;
				l_line_tlf := l_SISTEMA_ORIGEN || ';' || l_FECHA_INCREMENTO || ';;' || l_IDEMPRESA || ';' || CODIGO_ENTIDAD || ';' || CIF_TOMADOR || ';' ||
				   ';2;00;' || TLF_MOVIL_TOM;
				UTL_FILE.PUT_LINE (f_fichero_tlf, utl_raw.cast_to_varchar2(utl_raw.convert(utl_raw.cast_to_raw(l_line_tlf || chr(13)), 'UTF8', nls_characterset)));
			vTelefonosActualizados:=vTelefonosActualizados+1;
			 end if;
			 -- email
			 if (EMAIL_TOM is not null) then
			 vEmailsTratados:=vEmailsTratados+1;

				l_line_mail := l_SISTEMA_ORIGEN || ';' || l_FECHA_INCREMENTO || ';;' || l_IDEMPRESA || ';' || CODIGO_ENTIDAD || ';' || CIF_TOMADOR || ';' ||
				   ';1;00;' || EMAIL_TOM;
				UTL_FILE.PUT_LINE (f_fichero_mail, utl_raw.cast_to_varchar2(utl_raw.convert(utl_raw.cast_to_raw(l_line_mail || chr(13)), 'UTF8', nls_characterset)));
				vEmailsActualizados:=vEmailsActualizados+1;
			 end if;

			-- Obtenemos la fecha de antiguedad
			 select TO_CHAR(min(p.fechaenvio), 'YYYYMMDD') into l_FECHA_ANTIGUEDAD from o02agpe0.tb_polizas p, o02agpe0.tb_colectivos c, o02agpe0.tb_tomadores t
			 where p.idcolectivo = c.id and c.codentidad = t.codentidad and c.ciftomador = t.ciftomador and t.codentidad = CODIGO_ENTIDAD
			 and t.ciftomador = CIF_TOMADOR and p.idestado IN (4, 8, 14, 15, 16)  and p.tiporef = 'P';

		     l_line_prs := l_SISTEMA_ORIGEN || ';' || l_FECHA_INCREMENTO || ';' || l_FECHA_ANTIGUEDAD || ';;' || l_IDEMPRESA || ';' || CODIGO_ENTIDAD || ';' ||
			   replace(NOMBRE_TOM,';',':') || ';;;;' || l_TIPO_DOC_TOM || ';' || CIF_TOMADOR || ';;;' ||
			   l_TIPO_IDENT_TOM || ';;;;;;;;;;;;;;01;;;;;;;;;;;;;;;;;;;;;;;';

		UTL_FILE.PUT_LINE (f_fichero_prs, utl_raw.cast_to_varchar2(utl_raw.convert(utl_raw.cast_to_raw(l_line_prs || chr(13)), 'UTF8', nls_characterset)));
				  arrPersonasIncluidas(CODIGO_ENTIDAD || '-' || CIF_TOMADOR ) := 1;
				  vTomadoresActualizados:=vTomadoresActualizados+1;
				update tb_tomadores t set t.fecha_envio_bd_unif=sysdate where t.codentidad=CODIGO_ENTIDAD and t.ciftomador=CIF_TOMADOR;
		end if;
    else
        vTomadoresTotales:=vTomadoresTotales-1;
	end if;

 end insertarTomador;

 procedure pintarErrorObligatorios(SISTEMA_ORIGEN varchar2,FECHA_INCREMENTO varchar2,CODIGO_ENTIDAD number,NIF varchar2) as
	begin
        if(SISTEMA_ORIGEN=null) then
         pq_utl.log(lc||' error SISTEMA_ORIGEN es null'|| chr(13));
            RAISE datosNull;
        end if;
        if(FECHA_INCREMENTO=null) then
         pq_utl.log(lc|| ' error FECHA_INCREMENTO es null'|| chr(13));
            RAISE datosNull;
        end if;
        if(CODIGO_ENTIDAD=null) then
         pq_utl.log(lc||' error CODIGO_ENTIDAD es null'|| chr(13));
            RAISE datosNull;
        end if;
        if(NIF=null) then
          pq_utl.log(lc|| ' error NIF es null '|| chr(13));
         RAISE datosNull;
        end if;
 end pintarErrorObligatorios;
 
 ---------------------------------------------------------------------------------------------------------------------------
 ---------------------------------------------------------------------------------------------------------------------------
 -- FUNCTION devolver fecha vencimiento 
 -----------------------------------------------------------------------------------------------------------------------------
 FUNCTION obtener_fechafin_contrato (v_fecha_envio in DATE, v_cod_linea in VARCHAR2, v_codmodulo in VARCHAR2, v_estado in NUMBER, v_idpoliza in NUMBER) RETURN VARCHAR2 IS
  
      v_vencimiento          BOOLEAN := false;
      v_meses                NUMBER(5);
      v_fecha_vencida        VARCHAR2(10);
      l_fecha_prueba         VARCHAR2(20);
      l_FECHAFIN_CONTRATO    VARCHAR2(8):= '99991231';
      lc VARCHAR2(60) := 'pq_genera_ficheros_bd_unif.obtener_fechafin_contrato'; -- Variable que almacena el nombre del paquete y de la funcion
      
   BEGIN
      
	-- EN ANULADAS Y RESCINDIDAS SE ENVIA LA FECHA DEL CAMBIO DE ESTADO 
    IF v_estado = 15 OR v_estado = 16 THEN
	  BEGIN
		select nvl(to_char(max(fecha), 'YYYYMMDD'), l_FECHAFIN_CONTRATO)
		into l_FECHAFIN_CONTRATO
		from o02agpe0.tb_polizas_historico_estados 
		where idpoliza = v_idpoliza 
		  and estado = v_estado;
	  EXCEPTION
		WHEN OTHERS THEN
            DBMS_OUTPUT.PUT_LINE(SQLERRM);
            pq_utl.log(lc,SQLERRM);
			-- Se escribe en el log el error
			pq_utl.log(lc, 'Se ha producido un error al recuperar la fecha de estado de la poliza ' || to_char(SYSDATE, 'HH24:MI:SS'), 2);
			pq_err.raiser(SQLCODE, 'Error al recuperar la fecha de estado de la poliza' || ' [' || SQLERRM || ']');
            return l_FECHAFIN_CONTRATO;
      END;
	ELSE
	  -- 1º buscamos el valor del mes venicmiento de forma mas especifica (por codlinea y codmodulo)
      BEGIN 
         select ME.NUM_MESES
           into v_meses
           from o02agpe0.tb_meses_venc ME
          where ME.CODLINEA = v_cod_linea
            and ME.CODMODULO = v_codmodulo;
            
      EXCEPTION
         WHEN NO_DATA_FOUND THEN
           v_meses := 0;
         WHEN OTHERS THEN
           DBMS_OUTPUT.PUT_LINE(SQLERRM);
           pq_utl.log(lc,SQLERRM);
			     -- Se escribe en el log el error
			     pq_utl.log(lc, 'Se ha producido un error al recuperar los meses de vencimiento ' || to_char(SYSDATE, 'HH24:MI:SS'), 2);
			     pq_err.raiser(SQLCODE, 'Error al buscar los meses de vencimiento(1)' || ' [' || SQLERRM || ']');
           return l_FECHAFIN_CONTRATO;
      END;   
         
      
      IF (v_meses = 0) THEN
         BEGIN 
           -- 2º buscamos el valor del mes venicmiento de forma mas genérica (por codlinea y codmodulo genérico)         
           select ME.NUM_MESES
             into v_meses
             from o02agpe0.tb_meses_venc ME
            where ME.CODLINEA = v_cod_linea
              and ME.CODMODULO = '99999';
         EXCEPTION
            WHEN NO_DATA_FOUND THEN
              v_meses := 0;
            WHEN OTHERS THEN
              DBMS_OUTPUT.PUT_LINE(SQLERRM);
              pq_utl.log(lc,SQLERRM);
			        -- Se escribe en el log el error
			        pq_utl.log(lc, 'Se ha producido un error al recuperar los meses de vencimiento ' || to_char(SYSDATE, 'HH24:MI:SS'), 2);
			        pq_err.raiser(SQLCODE, 'Error al buscar los meses de vencimiento(2)' || ' [' || SQLERRM || ']');
              return l_FECHAFIN_CONTRATO;
         END;      
      END IF;     

      -- 3º buscamos el valor del mes venicmiento de forma genérica (por codlinea genérica y codmodulo genérico)            
      IF (v_meses = 0) THEN
         BEGIN 
           -- 2º buscamos el valor del mes venicmiento de forma mas genérica (por codlinea y codmodulo genérico)         
           select ME.NUM_MESES
             into v_meses
             from o02agpe0.tb_meses_venc ME
            where ME.CODLINEA = 999
              and ME.CODMODULO = '99999';
         EXCEPTION
            WHEN NO_DATA_FOUND THEN
              v_meses := 0;
            WHEN OTHERS THEN
              DBMS_OUTPUT.PUT_LINE(SQLERRM);
              pq_utl.log(lc,SQLERRM);
			        -- Se escribe en el log el error
			        pq_utl.log(lc, 'Se ha producido un error al recuperar los meses de vencimiento ' || to_char(SYSDATE, 'HH24:MI:SS'), 2);
			        pq_err.raiser(SQLCODE, 'Error al buscar los meses de vencimiento(3)' || ' [' || SQLERRM || ']');
              return l_FECHAFIN_CONTRATO;
         END; 
      END IF;
      
      IF (v_meses > 0) THEN              
         
         -- Una vez obtenido los meses para la línea/codmodulo, calculamos la fehca de vencimiento   
         v_fecha_vencida := TO_CHAR(add_months(v_fecha_envio, +v_meses), 'YYYY/MM/DD');
        
         IF TO_CHAR(sysdate,'YYYY/MM/DD') > v_fecha_vencida THEN
            l_FECHAFIN_CONTRATO := TO_CHAR(add_months(v_fecha_envio, +v_meses), 'YYYYMMDD');
            return l_FECHAFIN_CONTRATO;
         END IF;  
      END IF;
	END IF; 
   return l_FECHAFIN_CONTRATO; 
      
 END obtener_fechafin_contrato;

END PQ_GENERA_FICHEROS_BD_UNIF;
/
SHOW ERRORS;