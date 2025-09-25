SET DEFINE OFF;
SET SERVEROUTPUT ON;

create or replace package o02agpe0.PQ_CREAXML_SINIESTROS is

  --- Author  :  U029803
  -- Created : 20/12/2010 13:32:56
  --- Purpose : Creacion XML Siniestros

  -- Procedimiento Creacion XML Siniestro
  FUNCTION generaXMLSiniestro(P_IDSINIESTRO IN NUMBER
                               ) RETURN NUMBER;

  FUNCTION getXMLParcelasSiniestradas(P_IDSINIESTRO IN NUMBER) RETURN XMLType;

  FUNCTION getSuperficieParcela(P_IDSINIESTRO IN NUMBER, P_IDPARCELASINIESTRO IN NUMBER) RETURN NUMBER;

end PQ_CREAXML_SINIESTROS;
/
CREATE OR REPLACE PACKAGE BODY O02AGPE0.pq_creaxml_siniestros IS

	log_level VARCHAR2(1) := '2';

	/*
  Funcion: generaXMLSiniestro
  Autor: T-SYSTEMS
  Fecha:
  Descripcion: Funcion que general el XML del siniestro y lo inserta en el CLOB de Siniestros
  */
	FUNCTION generaxmlsiniestro(p_idsiniestro IN NUMBER) RETURN NUMBER AS
		v_xml         CLOB;
		v_xml_temp    xmltype;
		v_rootelement VARCHAR2(50) := '<?xml version="1.0" encoding="UTF-8"?>';
		v_idcopy      NUMBER := NULL;
		idinsercion   NUMBER(15) := p_idsiniestro;
		namespace     VARCHAR2(2000) := 'http://www.agroseguro.es/SeguroAgrario/Siniestros';

		no_validado_exception EXCEPTION;
	BEGIN

		EXECUTE IMMEDIATE 'SELECT S.IDCOPY FROM TB_SINIESTROS S WHERE S.ID = :idInsercion'
			INTO v_idcopy
			USING idinsercion;

		--IF v_idcopy IS NULL THEN
			SELECT xmlelement("ns2:Siniestro",
												 xmlattributes(namespace AS "xmlns:ns2",
																			 s.numsiniestro AS "numeroInterno",
																			 s.fechaocurrencia AS "fechaFirmaSiniestro",
																			 s.observaciones AS "observacion") --opcional
												 ,
												 xmlelement("Poliza",
																		xmlattributes(l.codplan AS "plan",
																									l.codlinea AS "linea",
																									p.referencia AS "referenciaPoliza",
																									col.idcolectivo AS "referenciaColectivo") --opcional
																		),
												 xmlelement("Asegurado",
																		xmlattributes(aseg.nifcif AS "nif",
																									aseg.telefono AS "telefono"),
																		CASE
																			WHEN aseg.nombre IS NOT NULL THEN
																			 xmlelement("NombreApellidos",
																									xmlattributes(aseg.nombre AS "nombre",
																																aseg.apellido1 AS "apellido1",
																																aseg.apellido2 AS "apellido2"))
																			ELSE
																			 xmlelement("RazonSocial",
																									xmlattributes(aseg.razonsocial AS "razonSocial"))
																		END,
																		xmlelement("Direccion" --completar campos opcionales: piso, bloque, escalera
																							 ,
																							 xmlattributes(aseg.direccion AS "via",
																														 aseg.numvia AS "numero",
																														 loc2.nomlocalidad AS "localidad",
																														 lpad(aseg.codpostal,
																																	5,
																																	'0') AS "cp",
																														 aseg.codprovincia AS "provincia",
																														 aseg.codlocalidad AS "termino",
																														 aseg.piso AS "piso",
																														 aseg.bloque AS "bloque",
																														 aseg.escalera AS "escalera"))),
												 xmlelement("PersonaContacto",
																		xmlattributes(s.telefono1 AS "telefono1",
																									s.telefono2 AS "telefono2",
																									s.telefono3 AS "telefono3"),
																		CASE
																			WHEN s.nombre IS NOT NULL THEN
																			 xmlelement("NombreApellidos",
																									xmlattributes(s.nombre AS "nombre",
																																s.apellido1 AS "apellido1",
																																s.apellido2 AS "apellido2"))
																			ELSE
																			 xmlelement("RazonSocial",
																									xmlattributes(s.razonsocial AS "razonSocial"))
																		END,
																		xmlelement("Direccion" --completar campos opcionales: piso, bloque, escalera
																							 ,
																							 xmlattributes(s.direccion AS "via",
																														 s.numvia AS "numero",
																														 loc.nomlocalidad AS "localidad",
																														 lpad(s.codpostal,
																																	5,
																																	'0') AS "cp",
																														 s.codprovincia AS "provincia",
																														 s.codlocalidad AS "termino",
																														 s.piso AS "piso",
																														 s.bloque AS "bloque",
																														 s.escalera AS "escalera"))),
												 xmlelement("Riesgo",
																		xmlattributes(lpad(s.codriesgo,
																											 2,
																											 '0') AS "riesgoSiniestro",
																									s.fechaocurrencia AS "fechaOcurrencia") --opcional si c.codcausa='04'
																		),
												 xmlelement("ObjetosSiniestrados",
																		getxmlparcelassiniestradas(p_idsiniestro))) AS xml
				INTO v_xml_temp
				FROM tb_siniestros s, tb_polizas p, tb_asegurados aseg, tb_localidades loc, tb_lineas l, tb_colectivos col, tb_localidades loc2
			 WHERE s.idpoliza = p.idpoliza
				 AND p.idasegurado = aseg.id
				 AND s.codprovincia = loc.codprovincia
				 AND s.codlocalidad = loc.codlocalidad
				 AND s.sublocalidad = loc.sublocalidad
				 AND p.lineaseguroid = l.lineaseguroid
				 AND p.idcolectivo = col.id
				 AND aseg.codprovincia = loc2.codprovincia
				 AND aseg.codlocalidad = loc2.codlocalidad
				 AND aseg.sublocalidad = loc2.sublocalidad
				 AND s.id = p_idsiniestro;
		/*ELSE
			SELECT xmlelement("ns2:Siniestro",
												 xmlattributes(namespace AS "xmlns:ns2",
																			 s.numsiniestro AS "numeroInterno",
																			 s.fechaocurrencia AS "fechaFirmaSiniestro",
																			 s.observaciones AS "observacion") --opcional
												 ,
												 xmlelement("Poliza",
																		xmlattributes(p.codplan AS "plan",
																									p.codlinea AS "linea",
																									p.refpoliza AS "referenciaPoliza",
																									c.refcolectivo AS "referenciaColectivo") --opcional
																		),
												 xmlelement("Asegurado",
																		xmlattributes(a.nifcif AS "nif",
																									a.telefonofijo AS "telefono"),
																		CASE
																			WHEN a.nombreaseg IS NOT NULL THEN
																			 xmlelement("NombreApellidos",
																									xmlattributes(a.nombreaseg AS "nombre",
																																a.apell1aseg AS "apellido1",
																																a.apell2aseg AS "apellido2"))
																			ELSE
																			 xmlelement("RazonSocial",
																									xmlattributes(a.razonsocialaseg AS "razonSocial"))
																		END,
																		xmlelement("Direccion" --completar campos opcionales: piso, bloque, escalera
																							 ,
																							 xmlattributes(a.viaaseg AS "via",
																														 a.numeroviaaseg AS "numero",
																														 a.localidadaseg AS "localidad",
																														 lpad(a.cpaseg,
																																	5,
																																	'0') AS "cp",
																														 a.provinciaaseg AS "provincia",
																														 aseg.codlocalidad AS "termino",
																														 a.pisoaseg AS "piso",
																														 a.bloqueaseg AS "bloque",
																														 a.escaleraaseg AS "escalera"))),
												 xmlelement("PersonaContacto",
																		xmlattributes(s.telefono1 AS "telefono1",
																									s.telefono2 AS "telefono2",
																									s.telefono3 AS "telefono3"),
																		CASE
																			WHEN s.nombre IS NOT NULL THEN
																			 xmlelement("NombreApellidos",
																									xmlattributes(s.nombre AS "nombre",
																																s.apellido1 AS "apellido1",
																																s.apellido2 AS "apellido2"))
																			ELSE
																			 xmlelement("RazonSocial",
																									xmlattributes(s.razonsocial AS "razonSocial"))
																		END,
																		xmlelement("Direccion" --completar campos opcionales: piso, bloque, escalera
																							 ,
																							 xmlattributes(s.direccion AS "via",
																														 s.numvia AS "numero",
																														 loc.nomlocalidad AS "localidad",
																														 lpad(s.codpostal,
																																	5,
																																	'0') AS "cp",
																														 s.codprovincia AS "provincia",
																														 s.codlocalidad AS "termino",
																														 s.piso AS "piso",
																														 s.bloque AS "bloque",
																														 s.escalera AS "escalera"))),
												 xmlelement("Riesgo",
																		xmlattributes(lpad(s.codriesgo,
																											 2,
																											 '0') AS "riesgoSiniestro",
																									s.fechaocurrencia AS "fechaOcurrencia") --opcional si c.codcausa='04'
																		),
												 xmlelement("ObjetosSiniestrados",
																		getxmlparcelassiniestradas(p_idsiniestro))) AS xml
				INTO v_xml_temp
				FROM tb_siniestros s, tb_copy_polizas p, tb_copy_colectivos c, tb_copy_asegurados a, tb_polizas po, tb_asegurados aseg, tb_localidades loc
			 WHERE s.idcopy = p.id
				 AND p.id = c.idcopy
				 AND a.idcopy = p.id
				 AND s.idpoliza = po.idpoliza
				 AND po.idasegurado = aseg.id
				 AND s.codprovincia = loc.codprovincia
				 AND s.codlocalidad = loc.codlocalidad
				 AND s.sublocalidad = loc.sublocalidad
				 AND s.id = p_idsiniestro;

		END IF;*/


		BEGIN
			pq_utl.log('XML generado. ', log_level);
			--Se incluye la cabecera XML
			v_xml := v_rootelement || v_xml_temp.getclobval;

      v_xml := replace(v_xml, '</CapitalAsegurado>', '</CapitalAsegurado>'||CHR(10));
			--Se inserta en la Tabla de Envios a agroseguro el XML
			UPDATE tb_siniestros s
				 SET s.xml = v_xml
			 WHERE s.id = p_idsiniestro;
			--      RETURNING ID INTO idInsercion;

			COMMIT;
		EXCEPTION
			WHEN no_validado_exception THEN
				pq_utl.log('EL XML no se ha podido validar contra el esquema!!',
									 log_level);
				pq_utl.log('XML=> ' || v_xml_temp.getstringval(),
									 log_level);
				idinsercion := -1;
			WHEN no_data_found THEN
        pq_utl.log('No se encontraron datos para el siniestro ' || p_idsiniestro,
									 log_level);
				idinsercion := -1;
      WHEN others THEN
        pq_utl.log('Error inesperado al crear el xml del siniestro ' ||
                   p_idsiniestro || SQLCODE || ': ' ||SQLERRM||'. ',
									 log_level);
        idinsercion := -1;
		END; -- Exception
		pq_utl.log('XML Insertado en TB_SINIESTROS. idInsercion: ' || idinsercion,
							 log_level);
		RETURN idinsercion;

	END generaxmlsiniestro;

	FUNCTION getxmlparcelassiniestradas(p_idsiniestro IN NUMBER) RETURN xmltype IS
		xmlparcelas xmltype;
	BEGIN
		SELECT xmlagg(xmlelement("Parcela",
															xmlattributes(parc.hoja AS "hoja",
																						parc.numero AS "numero",
																						--sd.superfas AS "superficie",
																						getsuperficieparcela(p_idsiniestro,
																																 parc.id) AS "superficie",
																						parc.nomparcela AS "nombre") --opcional
															,
															xmlelement("Ubicacion",
																				 xmlattributes(parc.codprovincia AS "provincia",
																											 parc.codtermino AS "termino",
																											 nvl(parc.subtermino,
																													 ' ') AS "subtermino",
																											 parc.codcomarca AS "comarca")) --opcional
															,
															xmlelement("Cosecha" --******Cosecha o Especie
																				 ,
																				 xmlattributes(parc.codcultivo AS "cultivo",
																											 parc.codvariedad AS "variedad"),
																				 xmlelement("CapitalesAsegurados",
																										(SELECT xmlagg(xmlelement("CapitalAsegurado",
																																							 xmlattributes(cap.superficie AS "superficie",
																																														 cap.codtipocapital AS "tipo"),
																																							 xmlelement("DatosVariables",
																																													(SELECT xmlagg(CASE
																																																						WHEN (capdv.codconcepto = 112) THEN
																																																						 xmlelement("FecRecol",
																																																												xmlattributes(to_char(to_date(capdv.valor,
																																																																											'DD/MM/YYYY'),
																																																																							'YYYY-MM-DD') AS "valor"))
																																																						WHEN (capdv.codconcepto = 426) THEN
																																																						 xmlelement("FruCaid",
																																																												xmlattributes(capdv.valor AS "valor"))
																																																					END ORDER BY capdv.codconcepto ASC)
																																														 FROM tb_siniestro_cap_aseg_dv capdv
																																														WHERE capdv.idsiniestrocapaseg = cap.id))))
																											 FROM tb_siniestro_cap_aseg cap
																											WHERE cap.idparcelasiniestro = parc.id
																												AND cap.altaensiniestro = 'S'))) --opcional
															,
															CASE
																WHEN parc.poligono IS NOT NULL THEN
																 xmlelement("IdentificacionCatastral" --******IdentificacionCatastral o SIGPAC
																						,
																						xmlattributes(parc.poligono AS "poligono",
																													parc.parcela AS "parcela"))
																WHEN parc.codprovsigpac IS NOT NULL THEN
																 xmlelement("SIGPAC" --******IdentificacionCatastral o SIGPAC
																						,
																						xmlattributes(parc.codprovsigpac AS "provincia",
																													parc.codtermsigpac AS "termino",
																													parc.agrsigpac AS "agregado",
																													parc.zonasigpac AS "zona",
																													parc.poligonosigpac AS "poligono",
																													parc.parcelasigpac AS "parcela",
																													parc.recintosigpac AS "recinto"))
															END))
			INTO xmlparcelas
			FROM tb_siniestro_parcelas parc
		 WHERE parc.idsiniestro = p_idsiniestro
			 AND parc.altaensiniestro = 'S';

		RETURN xmlparcelas;
	END getxmlparcelassiniestradas;

	FUNCTION getsuperficieparcela(p_idsiniestro        IN NUMBER
															 ,p_idparcelasiniestro IN NUMBER) RETURN NUMBER IS
		l_superficie NUMBER(15, 2) := 0;
	BEGIN
		SELECT SUM(superficie)
			INTO l_superficie
			FROM tb_siniestro_parcelas parc, tb_siniestro_cap_aseg t
		 WHERE parc.id = t.idparcelasiniestro
			 AND parc.idsiniestro = p_idsiniestro
			 AND parc.altaensiniestro = 'S'
			 AND t.idparcelasiniestro = p_idparcelasiniestro
		 GROUP BY t.idparcelasiniestro;

		RETURN l_superficie;
	END getsuperficieparcela;

END pq_creaxml_siniestros;
/
SHOW ERRORS;
