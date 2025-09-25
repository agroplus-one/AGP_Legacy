SET DEFINE OFF;
SET SERVEROUTPUT ON;

CREATE OR REPLACE PACKAGE O02AGPE0.PQ_CREAXML IS

    FUNCTION generaXMLPoliza(IDPOLIZAPARAM IN NUMBER,
                             LINEASEGUROIDPARAM IN NUMBER,
                             MODULO IN VARCHAR2,
                             FILAMODULO IN NUMBER,
                             FILACOMPARATIVAPARAM IN NUMBER,
                             TIPO IN VARCHAR2) RETURN NUMBER;

    FUNCTION getXMLPago(IDPOLIZAPARAM IN NUMBER) RETURN XMLType;

    FUNCTION getXMLRelacionSocios(IDPOLIZAPARAM IN NUMBER,
                                  lineaSeguroIdParam IN NUMBER,
                                  modulo IN VARCHAR2
                                 ) RETURN XMLType;

    FUNCTION getXMLDistribucionCostes(IDPOLIZAPARAM IN NUMBER) RETURN XMLType;

    FUNCTION getXMLSubvencionesDeclaradas(IDPOLIZAPARAM IN NUMBER) RETURN XMLType;


    FUNCTION getXMLDatosVariables(IDPOLIZAPARAMDV IN NUMBER,
                                  PPALMODDV IN NUMBER,
                                  RIESGOCUBIERTODV IN NUMBER,
                                  IDPARCELAPARAMDV IN NUMBER,
                                  IDCAPITALASEGDV IN NUMBER,
                                  LINEASEGIDPARAMDV IN NUMBER,
                                  MODULOSELEC IN VARCHAR2,
                                  FILACOMPARATIVAPARAM IN NUMBER
                                 ) RETURN XMLType;

    FUNCTION getXMLObjetosAsegurados(IDPOLIZAPARAM IN NUMBER,
                                     MODULOOA IN VARCHAR2,
                                     ppalMod IN NUMBER,
                                     riesgoCubierto IN NUMBER,
                                     FILACOMPARATIVAPARAM IN NUMBER
                                    ) RETURN XMLType;

    FUNCTION getXMLCapitalesAsegurados(IDPOLIZAPARAMCA IN NUMBER,
                                       IDPARCELAPARAMCA IN NUMBER,
                                       PPALMODCA IN NUMBER,
                                       RIESGOCUBCA IN NUMBER,
                                       MODULOCA IN VARCHAR2,
                                       FILACOMPARATIVAPARAM IN NUMBER
                                      ) RETURN XMLType;

    FUNCTION getXMLCobertura(idPolizaParamCB IN NUMBER,
                             lineaSeguroIdCB IN NUMBER,
                             moduloCB IN VARCHAR2,
                             filaComparativaCB In NUMBER
                            ) RETURN XMLType;

    FUNCTION getXMLEntidad(IDPOLIZAPARAM IN NUMBER) RETURN XMLType;

    FUNCTION getXMLColectivo(IDPOLIZAPARAM IN NUMBER) RETURN XMLType;

    FUNCTION getXMLAsegurado(IDPOLIZAPARAM In NUMBER) RETURN XMLType;

    FUNCTION getXMLFechaFinGarantias(
                                     IDPOLIZAPARAMFF IN NUMBER,
                                     IDPARCELAPARAMFF IN NUMBER,
                                     LINEASEGIDPARAMFF IN NUMBER,
                                     VALORFF IN VARCHAR2,
                                     MODULOFF IN VARCHAR2,
                                     FILACOMPARATIVAPARAMFF IN NUMBER
                                    ) RETURN XMLType;

    FUNCTION transformaPoliza(poliza IN xmltype) RETURN xmltype;

    FUNCTION transformaFecFGarant(FechasFGarant IN xmltype) RETURN xmltype;

    FUNCTION formateaModulo(modulo IN VARCHAR2) RETURN VARCHAR2;


    FUNCTION formatNumDec(numero IN NUMBER,
                          longitud IN NUMBER,
                          decimales IN NUMBER
                         ) RETURN VARCHAR2;

    FUNCTION getCodConceptoDV(parcelaOcobertura IN VARCHAR2,
                              lineaSeguroIdParam IN NUMBER
                             ) RETURN conceptos_varray;

    FUNCTION getDVConAtributos(parcelaOcobertura IN VARCHAR2,
                               lineaSeguroIdParam IN NUMBER
                              ) RETURN conceptos_varray;

    FUNCTION XML_EXTRACT_NO_EXCEPTION (p_xml IN XMLTYPE,
                                       p_xpath IN VARCHAR2,
                                       p_namespace IN VARCHAR2 default NULL
                                      ) RETURN VARCHAR2;

END PQ_CREAXML;
/
CREATE OR REPLACE PACKAGE BODY O02AGPE0.PQ_CREAXML IS

-- Variables Globales para todas las funciones
TIPO_NATURALEZA_DATE TB_SC_DD_TIPO_NATURALEZA.CODTIPONATURALEZA%TYPE := 4;
DEFAULT_DATE_FORMAT  VARCHAR2(10) := 'YYYY-MM-DD';
STORED_DATE_FORMAT   VARCHAR2(10) := 'DD/MM/YYYY';
LOG_LEVEL            VARCHAR2(1)  := '2';
USO_PARCELA          VARCHAR2(2)  := '31';
TIPO_DV_PARCELAS     VARCHAR2(2)  := '16';
TIPO_DV_COBERTURAS   VARCHAR2(2)  := '18';


-- *******************************************************************
--- Description: Genera el XML de la Poliza Funcion principal que
--              llama al resto de funciones para generar un XML
--              de Poliza
--
-- Input Parameters: IDPOLIZAPARAM
--                   LINEASEGUROIDPARAM
--                   MODULO
--                   FILAMODULO
--                   FILACOMPARATIVAPARAM
--                   TIPO
--
-- Output Parameters: El XML
--
-- Error Conditions Raised:
--
-- Author:      sergio.castro
--
-- Revision History
-- Date            Author       Reason for Change
-- ----------------------------------------------------------------
-- 03 OCT 2010     S.Castro     Created.
-- *****************************************************************
FUNCTION generaXMLPoliza (IDPOLIZAPARAM IN NUMBER,
                          LINEASEGUROIDPARAM IN NUMBER,
                          MODULO IN VARCHAR2,
                          FILAMODULO IN NUMBER,
                          FILACOMPARATIVAPARAM IN NUMBER,
                          TIPO IN VARCHAR2
                         ) RETURN NUMBER
AS
    v_xml            CLOB;
    riesgoCubierto   NUMBER(3) := 0;
    ppalMod          NUMBER(3) := 0;
    v_xml_temp       XMLTYPE;
    v_rootElement    VARCHAR2(50):= '<?xml version="1.0" encoding="UTF-8"?>';
    namespace        VARCHAR2(2000);
    idInsercion      NUMBER(15)  := -1;
    estadoPol        NUMBER(15);
    -- Tipo para los codigos del Modulo
    Type modulos IS RECORD (
                            ppalMod TB_SC_C_RIESGO_CBRTO_MOD.CODCONCEPTOPPALMOD%TYPE,
                            riesgoCubierto TB_SC_C_RIESGO_CBRTO_MOD.CODRIESGOCUBIERTO%TYPE
                           );

    codigosModulo modulos;

    TYPE cur_typ IS REF CURSOR;
    C_SOCIOS   cur_typ;
    CONSULTA VARCHAR2(2000);
    NIFSOCIO TB_SOCIOS.NIF%TYPE;

    CONTADOR NUMBER(3) := 1;

    no_validado_exception EXCEPTION;
    err_num NUMBER;
    err_msg VARCHAR2(2000);
BEGIN
    BEGIN -- Exception

      -- Se establece el NameSpace del XML dependiento del servicio al que se
      -- va a llamar (VL - Validacion, CL - Calculo)
      IF (tipo = 'VL') THEN
         namespace  := 'http://www.agroseguro.es/SeguroAgrario/Contratacion';
      ELSE
         namespace  := 'http://www.agroseguro.es/SeguroAgrario/CalculoSeguroAgrario';
      END IF;

      --Si el asegurado es un cif, hay que generar un numero correlativo para cada socio
      --Actualizamos el campo NUM_TEMP de los socios por si lo necesitamos
      --CONSULTAMOS LOS SOCIOS QUE TIENEN SUBVENCIONES
       CONSULTA := 'SELECT DISTINCT(SOCIOS.NIF) FROM TB_SOCIOS socios, TB_SUBVENCIONES_SOCIOS SUBVSOCIOS ' ||
                ' WHERE SUBVSOCIOS.IDPOLIZA = ' || IDPOLIZAPARAM ||
                ' AND SUBVSOCIOS.IDASEGURADO = SOCIOS.IDASEGURADO AND SUBVSOCIOS.NIFSOCIO = SOCIOS.NIF';

       OPEN C_SOCIOS FOR CONSULTA;
       LOOP
           FETCH C_SOCIOS INTO NIFSOCIO;
           EXIT WHEN C_SOCIOS%NOTFOUND;

           update tb_socios set NUM_TEMP = CONTADOR where nif = NIFSOCIO;
           CONTADOR := CONTADOR + 1;
       END LOOP;
       close c_socios;

       commit;

      -- Se recupera el Concepto Principal del Modulo
      -- y el Codigo de Riesgo Cubierto
      SELECT
            CODCONCEPTOPPALMOD,
            CODRIESGOCUBIERTO
      INTO  codigosModulo
      FROM  TB_SC_C_RIESGO_CBRTO_MOD
      WHERE LINEASEGUROID = LINEASEGUROIDPARAM
      AND   CODMODULO = MODULO
      AND   FILAMODULO = FILAMODULO
      AND   ROWNUM < 2;

      SELECT IDESTADO
      INTO estadoPol
      FROM TB_POLIZAS
      WHERE IDPOLIZA           = idPolizaParam;

      PQ_Utl.log('Estado en PQ_CREAXML: ' || estadoPol || '*******', 2);

      -- Esta Query genera el XML de la Poliza,
      -- llamando sucesivamente a las distintas funciones
      IF (estadoPol < 3) THEN

       SELECT
             XMLElement("pks:Poliza", XMLAttributes(namespace AS "xmlns:pks",
                                                    lineas.CODPLAN AS "plan",
                                                    lineas.CODLINEA AS "linea",
                                                    TO_CHAR(SYSDATE, DEFAULT_DATE_FORMAT) AS "fechaFirmaSeguro"),
                        getXMLColectivo             (IDPOLIZAPARAM),
                        getXMLAsegurado             (IDPOLIZAPARAM),
                        getXMLEntidad               (IDPOLIZAPARAM),
                        getXMLCobertura             (IDPOLIZAPARAM, LINEASEGUROIDPARAM, MODULO, FILACOMPARATIVAPARAM),
                        getXMLObjetosAsegurados     (IDPOLIZAPARAM, MODULO, codigosModulo.ppalMod, codigosModulo.riesgoCubierto, FILACOMPARATIVAPARAM),
                        getXMLPago                  (IDPOLIZAPARAM),
                        getXMLDistribucionCostes    (IDPOLIZAPARAM),
                        getXMLSubvencionesDeclaradas(IDPOLIZAPARAM),
                        getXMLRelacionSocios        (IDPOLIZAPARAM, LINEASEGUROIDPARAM, MODULO)
                       )
      AS XML
      INTO  v_xml_temp
      FROM  TB_ASEGURADOS              aseg,
            TB_POLIZAS                 polizas,
            TB_LINEAS                  lineas,
            TB_COLECTIVOS              colectivos,
            TB_ENTIDADES               entidades,
            TB_SUBENTIDADES_MEDIADORAS subEntMed,
            TB_PAGOS_POLIZA            pagosPol,
            TB_DISTRIBUCION_COSTES     distribCos
      WHERE polizas.IDPOLIZA           = idPolizaParam
      AND   polizas.IDPOLIZA           = pagosPol.IDPOLIZA (+)
      AND   polizas.IDPOLIZA           = distribCos.IDPOLIZA (+)
      AND   polizas.IDASEGURADO        = aseg.ID
      AND   polizas.LINEASEGUROID      = lineas.LINEASEGUROID
      AND   aseg.CODENTIDAD            = entidades.CODENTIDAD
      AND   polizas.IDCOLECTIVO        = colectivos.ID
      AND   colectivos.ENTMEDIADORA    = subEntMed.CODENTIDAD
      AND   colectivos.SUBENTMEDIADORA = subEntMed.CODSUBENTIDAD;

      ELSE

             SELECT
             XMLElement("pks:Poliza", XMLAttributes(namespace AS "xmlns:pks",
                                                    lineas.CODPLAN AS "plan",
                                                    lineas.CODLINEA AS "linea",
                                                    polizas.referencia AS "referencia",
                                                    refAgr.Dc  AS "digitoControl",
                                                    TO_CHAR(SYSDATE, DEFAULT_DATE_FORMAT) AS "fechaFirmaSeguro"),
                        getXMLColectivo             (IDPOLIZAPARAM),
                        getXMLAsegurado             (IDPOLIZAPARAM),
                        getXMLEntidad               (IDPOLIZAPARAM),
                        getXMLCobertura             (IDPOLIZAPARAM, LINEASEGUROIDPARAM, MODULO, FILACOMPARATIVAPARAM),
                        getXMLObjetosAsegurados     (IDPOLIZAPARAM, MODULO, codigosModulo.ppalMod, codigosModulo.riesgoCubierto, FILACOMPARATIVAPARAM),
                        getXMLPago                  (IDPOLIZAPARAM),
                        getXMLDistribucionCostes    (IDPOLIZAPARAM),
                        getXMLSubvencionesDeclaradas(IDPOLIZAPARAM),
                        getXMLRelacionSocios        (IDPOLIZAPARAM, LINEASEGUROIDPARAM, MODULO)
                       )
      AS XML
      INTO  v_xml_temp
      FROM  TB_ASEGURADOS              aseg,
            TB_POLIZAS                 polizas,
            TB_LINEAS                  lineas,
            TB_COLECTIVOS              colectivos,
            TB_ENTIDADES               entidades,
            TB_SUBENTIDADES_MEDIADORAS subEntMed,
            TB_PAGOS_POLIZA            pagosPol,
            TB_DISTRIBUCION_COSTES     distribCos,
            TB_REFERENCIAS_AGRICOLAS   refAgr
      WHERE polizas.IDPOLIZA           = idPolizaParam
      AND   polizas.IDPOLIZA           = pagosPol.IDPOLIZA (+)
      AND   polizas.IDPOLIZA           = distribCos.IDPOLIZA (+)
      AND   polizas.IDASEGURADO        = aseg.ID
      AND   polizas.LINEASEGUROID      = lineas.LINEASEGUROID
      AND   aseg.CODENTIDAD            = entidades.CODENTIDAD
      AND   polizas.IDCOLECTIVO        = colectivos.ID
      AND   colectivos.ENTMEDIADORA    = subEntMed.CODENTIDAD
      AND   colectivos.SUBENTMEDIADORA = subEntMed.CODSUBENTIDAD
      AND   refAgr.Referencia = polizas.referencia;



      END IF;




      --Se transforma con XSLT para ordenar los datos variables
      v_xml_temp := transformaPoliza(v_xml_temp);
      --PQ_UTL.LOG('XML=> ' || v_xml_temp.getStringVal(), LOG_LEVEL);
      --Se incluye la cabecera XML
      v_xml := v_rootElement || v_xml_temp.getClobVal;
      --Se inserta en la Tabla de Envios a agroseguro el XML
      INSERT INTO TB_ENVIOS_AGROSEGURO VALUES (SQ_ENVIOS_AGROSEGURO.NEXTVAL,
                                               idPolizaParam,
                                               SYSDATE,
                                               v_xml,
                                               tipo,
                                               MODULO,
                                               FILACOMPARATIVAPARAM,
                                               EMPTY_CLOB()
                                              )
                                              RETURNING ID INTO idInsercion;
      COMMIT;
    EXCEPTION
      WHEN no_validado_exception THEN
           PQ_UTL.LOG('EL XML no se ha podido validar contra el esquema!!', LOG_LEVEL);
           PQ_UTL.LOG('XML=> ' || v_xml_temp.getStringVal(), LOG_LEVEL);
           idInsercion := -1;
      WHEN NO_DATA_FOUND THEN
         idInsercion := -1;
      WHEN OTHERS THEN
           err_num := SQLCODE;
           err_msg := SQLERRM;
           PQ_UTL.LOG('Error inesperado durante la generacion del fichero XML: ' || err_num || ' - ' || err_msg || '****');
    END;-- Exception
    PQ_UTL.LOG('XML Insertado en TB_ENVIOS_AGROSEGURO. idInsercion: ' || idInsercion, LOG_LEVEL);
    RETURN idInsercion;
END generaXMLPoliza;


-- *****************************************************************
-- Description: Obtiene el Tag XML con el Pago
--
-- Input Parameters: IDPOLIZAPARAM
--
-- Output Parameters: El Tag XML
--
-- Error Conditions Raised:
--
-- Author:      sergio.castro
--
-- Revision History
-- Date            Author       Reason for Change
-- ----------------------------------------------------------------
-- 03 OCT 2010     S.Castro     Created.
-- *****************************************************************
  FUNCTION getXMLPago(IDPOLIZAPARAM IN NUMBER) RETURN XMLType is
      xmlPago XMLType;
  BEGIN
      SELECT
            XMLElement("Pago",
                       --XMLAttributes(NVL(pagosPol.formapago, 'C') as "forma",
                       XMLAttributes('C' as "forma",
                                     TO_CHAR(NVL(pagosPol.fecha, SYSDATE), DEFAULT_DATE_FORMAT) as "fecha",
                                     NVL(pagosPol.importe, '0.00') as "importe",
                                     --NVL(substr(pagosPol.cccbanco, 0, 4), '2000') as "banco"
                                     '2000' as "banco"
                                    )
                      )
      INTO  xmlPago
      FROM  TB_PAGOS_POLIZA pagosPol,
            TB_POLIZAS polizas
      WHERE polizas.idpoliza = idPolizaParam
      AND   polizas.idpoliza = pagosPol.Idpoliza(+);

      RETURN xmlPago;
  EXCEPTION
      WHEN OTHERS THEN
           PQ_UTL.LOG('Error en getXMLPago');
           RAISE;
  END getXMLPago;

-- *****************************************************************
-- Description: Obtiene el TAG XML de la Relacion de Socios
--
-- Input Parameters: IDPOLIZAPARAM
--                   LINEASEGUROIDPARAM
--                   MODULO
--
-- Output Parameters: El Tag XML
--
-- Error Conditions Raised:
--
-- Author:      sergio.castro
--
-- Revision History
-- Date            Author       Reason for Change
-- ----------------------------------------------------------------
-- 03 OCT 2010     S.Castro     Created.
-- *****************************************************************
  FUNCTION getXMLRelacionSocios(idPolizaParam IN NUMBER,
                                lineaSeguroIdParam IN NUMBER,
                                modulo IN VARCHAR2
                               ) RETURN XMLType is
      xmlRelacionSocios XMLType;

      TYPE cur_typ IS REF CURSOR;
      C_SOCIOS   cur_typ;
      CONSULTA VARCHAR2(2000);
      NIFSOCIO TB_SOCIOS.NIF%TYPE;

      STR_NIF_SOCIOS VARCHAR2(2000) := '';

  BEGIN
       --CONSULTAMOS LOS SOCIOS QUE TIENEN SUBVENCIONES
       CONSULTA := 'SELECT DISTINCT(SOCIOS.NIF) FROM TB_SOCIOS socios, TB_SUBVENCIONES_SOCIOS SUBVSOCIOS ' ||
                ' WHERE SUBVSOCIOS.IDPOLIZA = ' || IDPOLIZAPARAM ||
                ' AND SUBVSOCIOS.IDASEGURADO = SOCIOS.IDASEGURADO AND SUBVSOCIOS.NIFSOCIO = SOCIOS.NIF';

       OPEN C_SOCIOS FOR CONSULTA;
       LOOP
           FETCH C_SOCIOS INTO NIFSOCIO;
           EXIT WHEN C_SOCIOS%NOTFOUND;

           STR_NIF_SOCIOS := STR_NIF_SOCIOS || '''' || NIFSOCIO || ''',';

       END LOOP;
       close c_socios;

       IF (LENGTH(STR_NIF_SOCIOS) > 0) THEN
           STR_NIF_SOCIOS := SUBSTR(STR_NIF_SOCIOS, 0, LENGTH(STR_NIF_SOCIOS)-1);

       execute immediate 'SELECT
          XMLElement("RelacionSocios",
                     (SELECT XMLAgg(XMLElement("Socio",
                              XMLAttributes(socios.NIF as "nif",
                                            socios.num_temp AS "numero"
                                           ),
                              CASE
                                WHEN (UPPER(socios.tipoidentificacion) = ''NIF'') THEN
                                     XMLElement("NombreApellidos",
                                                XMLAttributes(socios.nombre as "nombre",
                                                              socios.apellido1 as "apellido1",
                                                              socios.apellido2 as "apellido2")
                                               )
                                ELSE
                                     XMLElement("RazonSocial",
                                                XMLAttributes(socios.razonsocial as "razonSocial")
                                               )
                                END,
                                XMLElement("SubvencionesDeclaradas",
                                    case
                                       when (socios.numsegsocial is not null) then
                                            XMLElement("SeguridadSocial",
                                               XMLAttributes(SUBSTR(socios.numsegsocial,1,2) as "provincia",
                                                             SUBSTR(socios.numsegsocial,3,8) as "numero",
                                                             SUBSTR(socios.numsegsocial,11,2) as "codigo",
                                                             socios.regimensegsocial as "regimen"
                                                            )
                                               )
                                        end,
                                     (SELECT XMLAgg(XMLElement("SubvencionDeclarada",
                                               XMLAttributes(SSOC.codtiposubvenesa as "tipo")))
                                      FROM TB_SUBVENCIONES_SOCIOS SSOC
                                      WHERE SSOC.IDPOLIZA = ' || idPolizaParam ||
                                             'AND SSOC.IDASEGURADO = SOCIOS.IDASEGURADO
                                             AND SSOC.NIFSOCIO = SOCIOS.NIF
                                      )
                                  )
                           )
                       )
                       FROM TB_SOCIOS socios
                       WHERE SOCIOS.NIF IN (' ||STR_NIF_SOCIOS || ') AND SOCIOS.IDASEGURADO = polizas.idasegurado
                      )
            )

        FROM  TB_POLIZAS    POLIZAS,
              TB_SUBV_ASEG_ENESA SUBV
        WHERE POLIZAS.IDPOLIZA = SUBV.IDPOLIZA
              AND SUBV.CODTIPOSUBVENESA = 3
              AND polizas.idpoliza = ' || idPolizaParam || ' ' INTO  xmlRelacionSocios;

       END IF;

        RETURN xmlRelacionSocios;
  EXCEPTION
      WHEN OTHERS THEN
           PQ_UTL.LOG('Error en getXMLRelacionSocios');
           RAISE;
  END getXMLRelacionSocios;


-- *****************************************************************
-- Description: Obtiene el Tag XML de las Subvenciones Declaradas
--
-- Input Parameters: IDPOLIZAPARAM
--
-- Output Parameters: El Tag XML
--
-- Error Conditions Raised:
--
-- Author:      sergio.castro
--
-- Revision History
-- Date            Author       Reason for Change
-- ----------------------------------------------------------------
-- 03 OCT 2010     S.Castro     Created.
-- *****************************************************************
  FUNCTION getXMLSubvencionesDeclaradas(IDPOLIZAPARAM IN NUMBER) RETURN XMLType is
      xmlSubvencionesDeclaradas XMLType;
  BEGIN

      SELECT
        XMLElement("SubvencionesDeclaradas",
                     (SELECT XMLElement("SeguridadSocial",
                                   XMLAttributes(SUBSTR(aseg.numsegsocial,1,2) as "provincia",
                                                 SUBSTR(aseg.numsegsocial,3,8) as "numero",
                                                 SUBSTR(aseg.numsegsocial,11,2) as "codigo",
                                                aseg.regimensegsocial as "regimen"
                                                ))
                      FROM  TB_SUBVS_ASEG_ENESA SENESA,
                            TB_ASEGURADOS asegT,
                            TB_POLIZAS polizasT,
                            TB_SC_C_SUBVS_ENESA se
                      WHERE SENESA.idpoliza = idPolizaParam
                            AND SENESA.IDPOLIZA = POLIZAST.IDPOLIZA
                            AND POLIZAST.IDASEGURADO = ASEGT.ID
                            AND ASEG.NUMSEGSOCIAL IS NOT NULL
                            AND ASEG.REGIMENSEGSOCIAL IS NOT NULL
                            AND se.ID = SENESA.IDSUBVENCION
                            AND se.LINEASEGUROID = SENESA.LINEASEGUROID
                            AND SE.CODTIPOSUBVENESA = 20
                     ),
                     --Este campo va siempre
                     (SELECT XMLAgg(XMLElement("SubvencionDeclarada",
                                               XMLAttributes (sc.CODTIPOSUBVCCAA as "tipo")
                                              )
                                   )
                      FROM  TB_SUBVS_ASEG_CCAA SCCAA, TB_SC_C_SUBVS_CCAA sc
                      WHERE SCCAA.idpoliza = idPolizaParam
                            AND sc.ID = SCCAA.IDSUBVENCION
                            AND sc.LINEASEGUROID = SCCAA.LINEASEGUROID
                     ),
                     (SELECT XMLAgg(XMLElement("SubvencionDeclarada",
                                               XMLAttributes (se.CODTIPOSUBVENESA as "tipo")
                                              )
                                   )
                      FROM  TB_SUBVS_ASEG_ENESA SENESA, TB_SC_C_SUBVS_ENESA se
                      WHERE SENESA.idpoliza = idPolizaParam
                            AND se.ID = SENESA.IDSUBVENCION
                            AND se.LINEASEGUROID = SENESA.LINEASEGUROID
                     )
                  )
       INTO  xmlSubvencionesDeclaradas
       FROM  TB_ASEGURADOS aseg,
             TB_POLIZAS polizas
       WHERE polizas.idpoliza = idPolizaParam
       AND   polizas.idasegurado = aseg.id;

       IF XML_EXTRACT_NO_EXCEPTION(xmlSubvencionesDeclaradas, 'SubvencionesDeclaradas/node()') IS NULL THEN
          RETURN NULL;
       END IF;

       RETURN xmlSubvencionesDeclaradas;
  EXCEPTION
      WHEN OTHERS THEN
           PQ_UTL.LOG('Error en getXMLSubvencionesDeclaradas');
           RAISE;
  END getXMLSubvencionesDeclaradas;


-- *****************************************************************
-- Description: Obtiene el Tag XML de la Distribucion de Costes
--
-- Input Parameters: IDPOLIZAPARAM
--
-- Output Parameters: El Tag XML
--
-- Error Conditions Raised:
--
-- Author:      sergio.castro
--
-- Revision History
-- Date            Author       Reason for Change
-- ----------------------------------------------------------------
-- 03 OCT 2010     S.Castro     Created.
-- *****************************************************************
  FUNCTION getXMLDistribucionCostes(IDPOLIZAPARAM IN NUMBER) RETURN XMLType is
       xmlDistribucionCostes XMLType;
  BEGIN
       SELECT
             XMLElement("DistribucionCoste", XMLAttributes (formatNumDec(distribCostes.primacomercial, 11, 2) as "primaComercial",
                                                            formatNumDec(distribCostes.primaneta, 11, 2) as "primaNeta",
                                                            formatNumDec(distribCostes.costeneto, 11, 2) as "costeNeto",
                                                            formatNumDec(distribCostes.cargotomador, 11, 2) as "cargoTomador"
                                                           ),
                         CASE
                            WHEN distribCostes.bonifmedpreventivas IS NOT NULL THEN
                               XMLElement ("BonificacionMedidasPreventivas",
                                           XMLAttributes(formatNumDec(distribCostes.bonifmedpreventivas, 11, 2) as "importe")
                                          )
                            END,
                         CASE
                            WHEN distribCostes.bonifasegurado IS NOT NULL
                            OR   distribCostes.recargoasegurado IS NOT NULL THEN
                                 CASE
                                     -- BONIFICACION O RECARGO
                                     WHEN distribCostes.bonifasegurado IS NOT NULL THEN
                                       XMLElement("BonificacionAsegurado",
                                                  XMLAttributes(formatNumDec(distribCostes.bonifasegurado, 11, 2) as "importe")
                                                 )
                                     ELSE
                                       XMLElement("RecargoAsegurado",
                                                  XMLAttributes(formatNumDec(distribCostes.recargoasegurado, 11, 2) as "importe")
                                                 )
                                     END
                            END,
                          CASE
                            WHEN distribCostes.ventanilla IS NOT NULL THEN
                                   XMLElement("Descuento",
                                              XMLAttributes(formatNumDec(distribCostes.ventanilla, 11, 2) as "ventanilla",
                                                            formatNumDec(distribCostes.dtocolectivo, 11, 2) as "contratacionColectiva"
                                                           )
                                             )
                            END,
                          /*Consorcio*/
                          XMLElement ("Consorcio",
                                      XMLAttributes(formatNumDec(distribCostes.reaseguro, 11, 2) as "reaseguro",
                                                    formatNumDec(distribCostes.recargo, 11, 2) as "recargo"
                                                   )
                                     ),
                          /*SubvencionENESA*/
                          (SELECT XMLAgg(XMLElement("SubvencionEnesa",
                                                    XMLAttributes(NVL(distCostSub.Codtiposubv, 1) as "tipo",
                                                                  NVL(distCostSub.Importesubv, 0.00) as "importe"
                                                                 )
                                                   )
                                        )
                           FROM  TB_DISTRIBUCION_COSTES distribCostes,
                                 TB_POLIZAS polizas,
                                 TB_DIST_COSTE_SUBVS distCostSub
                           WHERE polizas.idpoliza = IDPOLIZAPARAM
                           AND   polizas.idpoliza = distribCostes.IDPOLIZA
                           AND   distribCostes.Id = distCostSub.Iddistcoste
                          ),
                          /*SubvencionCCAA*/
                          (SELECT XMLAgg(XMLElement("SubvencionCCAA",
                                                    XMLAttributes(NVL(distCostSub.Codorganismo, ' ') AS "codigoOrganismo",
                                                                  NVL(distCostSub.Importesubv, '0.00') as "importe"
                                                                 )
                                                   )
                                        )
                           FROM  TB_DISTRIBUCION_COSTES distribCostes,
                                 TB_POLIZAS polizas,
                                 TB_DIST_COSTE_SUBVS distCostSub
                           WHERE polizas.idpoliza = IDPOLIZAPARAM
                           AND   polizas.idpoliza = distribCostes.IDPOLIZA
                           AND   distribCostes.Id = distCostSub.Iddistcoste
                           AND   distCostSub.Codorganismo IS NOT NULL
                          )
                       )
       INTO  xmlDistribucionCostes
       FROM  TB_DISTRIBUCION_COSTES distribCostes,
             TB_POLIZAS polizas
       WHERE polizas.idpoliza = IDPOLIZAPARAM
       AND   polizas.Idpoliza = distribCostes.idpoliza(+);

       RETURN xmlDistribucionCostes;

  EXCEPTION
      WHEN OTHERS THEN
           PQ_UTL.LOG('Error en getXMLDistribucionCostes');
           RAISE;
  END getXMLDistribucionCostes;

-- *****************************************************************
-- Description: Obtiene el Tag XML de los Datos variables
--
-- Input Parameters: IDPOLIZAPARAM
--                   PPALMOD
--                   RIESGOCUBIERTO
--                   IDPARCELAPARAM
--                   IDCAPITALASEG
--                   LINEASEGUROIDPARAM
--
-- Output Parameters: El Tag XML
--
-- Error Conditions Raised:
--
-- Author:      sergio.castro
--
-- Revision History
-- Date            Author       Reason for Change
-- ----------------------------------------------------------------
-- 03 OCT 2010     S.Castro     Created.
-- *****************************************************************
  FUNCTION getXMLDatosVariables(IDPOLIZAPARAMDV IN NUMBER,
                                PPALMODDV IN NUMBER,
                                RIESGOCUBIERTODV IN NUMBER,
                                IDPARCELAPARAMDV IN NUMBER,
                                IDCAPITALASEGDV IN NUMBER,
                                LINEASEGIDPARAMDV IN NUMBER,
                                MODULOSELEC IN VARCHAR2,
                                FILACOMPARATIVAPARAM IN NUMBER
                               ) RETURN XMLType is
  xmlDatosVariables XMLType;

  BEGIN
       -- Se selecciona el Codigo de Riesgo Cubierto
       -- y el Concepto Principal del modulo del Dato Variable
       -- de Cobertura RiesgoCubiertoElegido, para que
       -- en caso de que hubiera Fecha Fin Garantias
       -- con los mismos códigos, no incluir ese tag de FecFGarant
       -- Estos valores se pasan a la funcion getXMLFechaFinGarantias

       SELECT
         XMLElement("DatosVariables",(select XMLAgg(
           CASE
           WHEN varParcDV.CODCONCEPTO IN
                (SELECT column_value FROM THE(SELECT getCodConceptoDV(TIPO_DV_PARCELAS, LINEASEGIDPARAMDV) FROM DUAL)) THEN

               CASE
               WHEN dicDatDV.CODCONCEPTO IN
                (SELECT column_value FROM THE(SELECT getDVConAtributos(TIPO_DV_PARCELAS, LINEASEGIDPARAMDV) FROM DUAL)) THEN
                     CASE
                     -- Tipo de Datos Fecha
                     WHEN dicDatDV.CODTIPONATURALEZA = TIPO_NATURALEZA_DATE THEN
                         CASE
                         WHEN dicDatDV.CODCONCEPTO = 134 THEN -- FechaFinGarantia
                              case when capAsDV.Codtipocapital != 1 then
                                 getXMLFechaFinGarantias(
                                                     IDPOLIZAPARAMDV,
                                                     IDPARCELAPARAMDV,
                                                     LINEASEGIDPARAMDV,
                                                     varParcDV.VALOR,
                                                     MODULOSELEC,
                                                     FILACOMPARATIVAPARAM
                                                    )
                               end
                         ELSE
                             XMLElement(EVALNAME dicDatDV.etiquetaxml,
                                        XMLAttributes(PPALMODDV as "cPMod",
                                                      RIESGOCUBIERTODV as "codRCub",
                                                      TO_CHAR(TO_DATE(varParcDV.VALOR, STORED_DATE_FORMAT), DEFAULT_DATE_FORMAT) as "valor"
                                                     )
                                       )
                         END
                     ELSE
                         XMLElement(EVALNAME dicDatDV.etiquetaxml,
                                    XMLAttributes(PPALMODDV as "cPMod",
                                                  RIESGOCUBIERTODV as "codRCub",
                                                  varParcDV.VALOR as "valor"
                                                 )
                                   )
                     END
               ELSE
                     CASE
                     -- Tipo de Datos Fecha
                     WHEN dicDatDV.CODTIPONATURALEZA = TIPO_NATURALEZA_DATE THEN
                       XMLElement(EVALNAME dicDatDV.etiquetaxml,
                                  XMLAttributes(TO_CHAR(TO_DATE(varParcDV.VALOR, STORED_DATE_FORMAT), DEFAULT_DATE_FORMAT) as "valor"))
                     ELSE
                       XMLElement(EVALNAME dicDatDV.etiquetaxml,
                                  XMLAttributes(varParcDV.VALOR as "valor"))
                     END
               END
           ELSE
               CASE
               -- Tipo de Datos Fecha
               WHEN dicDatDV.CODTIPONATURALEZA = TIPO_NATURALEZA_DATE THEN
                 XMLElement(EVALNAME dicDatDV.etiquetaxml,
                            XMLAttributes(TO_CHAR(TO_DATE(varParcDV.VALOR, STORED_DATE_FORMAT), DEFAULT_DATE_FORMAT) as "valor"))
               ELSE
                 XMLElement(EVALNAME dicDatDV.etiquetaxml,
                            XMLAttributes(varParcDV.VALOR as "valor"))
               END
           END
           )
           FROM  TB_SC_DD_DIC_DATOS   dicDatDV,
                 TB_DATOS_VAR_PARCELA varParcDV
           WHERE dicDatDV.codconcepto         = varParcDV.codconcepto
           AND   varParcDV.IDCAPITALASEGURADO = capAsDV.IDCAPITALASEGURADO
           )
          )
       INTO  xmlDatosVariables
       FROM  TB_CAPITALES_ASEGURADOS capAsDV,
             TB_PARCELAS parcelDV
       WHERE capAsDV.idparcela = IDPARCELAPARAMDV
       AND   capAsDV.Idcapitalasegurado = IDCAPITALASEGDV
       AND   parcelDV.idparcela = capAsDV.idParcela
       AND   parcelDV.idpoliza = IDPOLIZAPARAMDV;

       RETURN xmlDatosVariables;

  EXCEPTION
      WHEN OTHERS THEN
           PQ_UTL.LOG('Error en getXMLDatosVariables');
           RAISE;
  END;


-- *****************************************************************
-- Description: Obtiene el Tag XML de los Objetos Asegurados
--
-- Input Parameters: IDPOLIZAPARAM
--                   PPALMOD
--                   RIESGOCUBIERTO
--
-- Output Parameters: El Tag XML
--
-- Error Conditions Raised:
--
-- Author:      sergio.castro
--
-- Revision History
-- Date            Author       Reason for Change
-- ----------------------------------------------------------------
-- 03 OCT 2010     S.Castro     Created.
-- *****************************************************************
  FUNCTION getXMLObjetosAsegurados(IDPOLIZAPARAM IN NUMBER,
                                   MODULOOA IN VARCHAR2,
                                   ppalMod IN NUMBER,
                                   riesgoCubierto IN NUMBER,
                                   FILACOMPARATIVAPARAM IN NUMBER
                                  ) RETURN XMLType is
          xmlObjetosAsegurados XMLType;
  BEGIN
          SELECT
            XMLElement("ObjetosAsegurados", (SELECT XMLAgg(XMLElement("Parcela"
                                                                       , XMLAttributes(parcelas.hoja AS "hoja",
                                                                                       parcelas.numero AS "numero",
                                                                                       parcelas.nomparcela AS "nombre")
                                                                       , XMLElement("Ubicacion", XMLAttributes(parcelas.codprovincia AS "provincia",
                                                                                                               parcelas.codtermino AS "termino",
                                                                                                               parcelas.subtermino AS "subtermino",
                                                                                                               term.codcomarca AS "comarca")
                                                                                    )--Fin Ubicacion
                                                                       , XMLElement("Cosecha",
                                                                                    XMLAttributes(parcelas.codcultivo AS "cultivo",
                                                                                                  parcelas.codvariedad AS "variedad"),
                                                                                    getXMLCapitalesAsegurados(idPoliza,
                                                                                                              parcelas.idparcela,
                                                                                                              ppalMod,
                                                                                                              riesgoCubierto,
                                                                                                              MODULOOA,
                                                                                                              FILACOMPARATIVAPARAM
                                                                                                             )

                                                                                   )--Fin Cosecha
                                                                       , CASE
                                                                         WHEN parcelas.poligono IS NOT NULL AND parcelas.PARCELA IS NOT NULL THEN
                                                                              XMLElement("IdentificacionCatastral",
                                                                                         XMLAttributes(parcelas.poligono AS "poligono",
                                                                                                       parcelas.parcela AS "parcela")
                                                                                        )-- Fin IdentificacionCatastral
                                                                         ELSE
                                                                           CASE
                                                                           WHEN parcelas.codprovsigpac IS NOT NULL AND parcelas.codtermsigpac IS NOT NULL THEN
                                                                              XMLElement("SIGPAC",
                                                                                         XMLAttributes(parcelas.codprovsigpac AS "provincia",
                                                                                                       parcelas.codtermsigpac AS "termino",
                                                                                                       parcelas.agrsigpac AS "agregado",
                                                                                                       parcelas.zonasigpac AS "zona",
                                                                                                       parcelas.poligonosigpac AS "poligono",
                                                                                                       parcelas.parcelasigpac AS "parcela",
                                                                                                       parcelas.recintosigpac AS "recinto"
                                                                                                      )
                                                                                        )--Fin SIGPAC
                                                                           END
                                                                         END
                                                                     ) ORDER BY parcelas.hoja ASC, parcelas.numero ASC --Fin parcela
                                                          )
	                                                      FROM  TB_PARCELAS parcelas,
                                                              TB_TERMINOS term
	                                                      WHERE parcelas.idpoliza     = IDPOLIZAPARAM
	                                                      AND   parcelas.codprovincia = term.codprovincia
	                                                      AND   parcelas.codtermino   = term.codtermino
	                                                      AND   parcelas.subtermino   = term.subtermino
                                            )
                       )
            INTO xmlObjetosAsegurados
            FROM DUAL;

            RETURN xmlObjetosAsegurados;
  EXCEPTION
      WHEN OTHERS THEN
           PQ_UTL.LOG('Error en getXMLObjetosAsegurados');
           RAISE;
  END getXMLObjetosAsegurados;

-- *****************************************************************
-- Description: Obtiene el Tag XML de los Capitales Asegurados
--
-- Input Parameters: IDPOLIZAPARAM
--                   IDPARCELAPARAM
--                   PPALMOD
--                   RIESGOCUBIERTO
--
-- Output Parameters: El Tag XML
--
-- Error Conditions Raised:
--
-- Author:      sergio.castro
--
-- Revision History
-- Date            Author       Reason for Change
-- ----------------------------------------------------------------
-- 03 OCT 2010     S.Castro     Created.
-- *****************************************************************
  FUNCTION getXMLCapitalesAsegurados(
                                     IDPOLIZAPARAMCA IN NUMBER,
                                     IDPARCELAPARAMCA IN NUMBER,
                                     PPALMODCA IN NUMBER,
                                     RIESGOCUBCA IN NUMBER,
                                     MODULOCA IN VARCHAR2,
                                     FILACOMPARATIVAPARAM IN NUMBER
                                    ) RETURN XMLType is
       xmlCapitalesAsegurados XMLType;
  BEGIN
       SELECT
          XMLElement("CapitalesAsegurados" , (
                                               SELECT XMLAgg
                                               (      CASE
                                                           WHEN capAsRelMod.Produccionmodif IS NULL THEN
                                                             XMLElement
                                                             ("CapitalAsegurado",
                                                              XMLAttributes(
                                                                            formatNumDec(capAs.superficie, 7, 2) as "superficie",
                                                                            formatNumDec(ABS(capAsRelMod.precio), 7, 4) as "precio",
                                                                            NVL(capAsRelMod.produccion, 0) as "produccion",
                                                                            capAs.codtipocapital as "tipo"
                                                                           ),

                                                             getXMLDatosVariables(IDPOLIZAPARAMCA,
                                                                                   PPALMODCA,
                                                                                   RIESGOCUBCA,
                                                                                   IDPARCELAPARAMCA,
                                                                                   capAs.IDCAPITALASEGURADO,
                                                                                   polizCA.lineaseguroid,
                                                                                   MODULOCA,
                                                                                   FILACOMPARATIVAPARAM
                                                                                  )
                                                             )

                                                           ELSE
                                                             XMLElement
                                                             ("CapitalAsegurado",
                                                              XMLAttributes(
                                                                            formatNumDec(capAs.superficie, 7, 2) as "superficie",
                                                                            formatNumDec(ABS(capAsRelMod.Preciomodif), 7, 4) as "precio",
                                                                            NVL(capAsRelMod.Produccionmodif, 0) as "produccion",
                                                                            capAs.codtipocapital as "tipo"
                                                                           ),

                                                             getXMLDatosVariables(IDPOLIZAPARAMCA,
                                                                                   PPALMODCA,
                                                                                   RIESGOCUBCA,
                                                                                   IDPARCELAPARAMCA,
                                                                                   capAs.IDCAPITALASEGURADO,
                                                                                   polizCA.lineaseguroid,
                                                                                   MODULOCA,
                                                                                   FILACOMPARATIVAPARAM
                                                                                  )
                                                             )
                                                     END
                                               )
                                               FROM  TB_CAPITALES_ASEGURADOS capAs,
                                                     TB_CAP_ASEG_REL_MODULO capAsRelMod
                                               WHERE capAs.idcapitalasegurado = capAsRelMod.idcapitalasegurado
                                                 AND capAs.idParcela = IDPARCELAPARAMCA
                                                 AND capAsRelMod.codmodulo = MODULOCA
                                             )
                     )
        INTO  xmlCapitalesAsegurados
        FROM  TB_POLIZAS  polizCA,
              TB_PARCELAS parcelCA
        WHERE parcelCA.idparcela = IDPARCELAPARAMCA
        AND   parcelCA.idpoliza = polizCA.idpoliza
        AND   polizCA.idpoliza = IDPOLIZAPARAMCA;

        RETURN xmlCapitalesAsegurados;
  EXCEPTION
      WHEN OTHERS THEN
           PQ_UTL.LOG('Error en getXMLCapitalesAsegurados');
           RAISE;

  END getXMLCapitalesAsegurados;

-- *****************************************************************
-- Description: Obtiene el Tag XML de la Cobertura
--
-- Input Parameters: IDPOLIZAPARAMCB
--                   LINEASEGUROIDCB
--                   MODULOCB
--                   FILACOMPARATIVACB
--
-- Output Parameters: El Tag XML
--
-- Error Conditions Raised:
--
-- Author:      sergio.castro
--
-- Revision History
-- Date            Author       Reason for Change
-- ----------------------------------------------------------------
-- 03 OCT 2010     S.Castro     Created.
-- *****************************************************************
  FUNCTION getXMLCobertura(idPolizaParamCB IN NUMBER,
                           lineaSeguroIdCB IN NUMBER,
                           moduloCB IN VARCHAR2,
                           filaComparativaCB In NUMBER
                          ) RETURN XMLType is
       xmlCobertura XMLType;
       xmlDVCobertura XMLType := null;
  BEGIN


       SELECT
           XMLElement("DatosVariables",(select XMLAgg (
               CASE
               WHEN comp.CODCONCEPTO IN
                        (SELECT column_value FROM THE(SELECT getCodConceptoDV(TIPO_DV_COBERTURAS, lineaSeguroIdCB) FROM DUAL)) THEN
                    CASE
                    WHEN dic.CODCONCEPTO IN
                          (SELECT column_value FROM THE(SELECT getDVConAtributos(TIPO_DV_COBERTURAS, lineaSeguroIdCB) FROM DUAL)) THEN
                               CASE
                               -- Tipo de Datos Fecha
                               WHEN dic.CODTIPONATURALEZA = TIPO_NATURALEZA_DATE THEN
                                   XMLElement(EVALNAME dic.etiquetaxml,
                                              XMLAttributes(comp.CODCONCEPTOPPALMOD as "cPMod",
                                                            comp.CODRIESGOCUBIERTO as "codRCub",
                                                            TO_CHAR(TO_DATE(comp.CODVALOR , STORED_DATE_FORMAT), DEFAULT_DATE_FORMAT) as "valor"
                                                           )
                                             )
                               ELSE
                                   CASE
                                   WHEN comp.CODVALOR = -1 THEN
                                       XMLElement(EVALNAME dic.etiquetaxml,
                                                  XMLAttributes(comp.CODCONCEPTOPPALMOD as "cPMod",
                                                                comp.CODRIESGOCUBIERTO as "codRCub",
                                                                'S' as "valor"
                                                               )
                                                 )
                                   WHEN comp.CODVALOR = -2 THEN
                                       XMLElement(EVALNAME dic.etiquetaxml,
                                                  XMLAttributes(comp.CODCONCEPTOPPALMOD as "cPMod",
                                                                comp.CODRIESGOCUBIERTO as "codRCub",
                                                                'N' as "valor"
                                                               )
                                                 )
                                   ELSE
                                       XMLElement(EVALNAME dic.etiquetaxml,
                                                  XMLAttributes(comp.CODCONCEPTOPPALMOD as "cPMod",
                                                                comp.CODRIESGOCUBIERTO as "codRCub",
                                                                comp.CODVALOR as "valor"
                                                               )
                                                 )
                                   END
                               END
                    ELSE
                               CASE
                               -- Tipo de Datos Fecha
                               WHEN dic.CODTIPONATURALEZA = TIPO_NATURALEZA_DATE THEN
                                 XMLElement(EVALNAME dic.etiquetaxml,
                                            XMLAttributes(TO_CHAR(TO_DATE(comp.CODVALOR , STORED_DATE_FORMAT), DEFAULT_DATE_FORMAT) as "valor"))
                               ELSE
                                 XMLElement(EVALNAME dic.etiquetaxml,
                                            XMLAttributes(comp.CODVALOR  as "valor"))
                               END
                    END
                ELSE
                         CASE
                         -- Tipo de Datos Fecha
                         WHEN dic.CODTIPONATURALEZA = TIPO_NATURALEZA_DATE THEN
                           XMLElement(EVALNAME dic.etiquetaxml,
                                      XMLAttributes(TO_CHAR(TO_DATE(comp.CODVALOR , STORED_DATE_FORMAT), DEFAULT_DATE_FORMAT) as "valor"))
                         ELSE
                           XMLElement(EVALNAME dic.etiquetaxml,
                                      XMLAttributes(comp.CODVALOR  as "valor"))
                         END
                END
              ) -- XMLAgg
                   FROM  TB_COMPARATIVAS_POLIZA comp,
                         TB_SC_DD_DIC_DATOS     dic
                   WHERE dic.codconcepto      = comp.codconcepto
                   AND   comp.LINEASEGUROID   = lineaSeguroIdCB
                   AND   comp.FILACOMPARATIVA = filaComparativaCB
                   AND   comp.IDPOLIZA        = idPolizaParamCB
                   AND   comp.CODMODULO       = moduloCB
               )  --NO en la linea de frutales (300) ???
             )
       INTO xmlDVCobertura
       FROM DUAL;
       --Se comprueba si se recogieron datos variables,
       --para devolver el tag vacio en caso de que no
       IF xmlDVCobertura IS NOT NULL AND
          XML_EXTRACT_NO_EXCEPTION(xmlDVCobertura, 'DatosVariables/node()') IS NULL THEN
          xmlDVCobertura := null;
       END IF;

       SELECT
          XMLElement("Cobertura", XMLAttributes(formateaModulo(moduloCB) AS "modulo"),
                     xmlDVCobertura
                    )
       INTO  xmlCobertura
       FROM  DUAL;

       RETURN xmlCobertura;

  EXCEPTION
      WHEN OTHERS THEN
           PQ_UTL.LOG('Error en getXMLCobertura');
           RAISE;
  END getXMLCobertura;

-- *****************************************************************
-- Description: Obtiene el Tag XML de la Entidad
--
-- Input Parameters: IDPOLIZAPARAM
--
-- Output Parameters: El Tag XML
--
-- Error Conditions Raised:
--
-- Author:      sergio.castro
--
-- Revision History
-- Date            Author       Reason for Change
-- ----------------------------------------------------------------
-- 03 OCT 2010     S.Castro     Created.
-- *****************************************************************
  FUNCTION getXMLEntidad(IDPOLIZAPARAM IN NUMBER) RETURN XMLType is
       xmlEntidad XMLType;
  BEGIN
       SELECT
          XMLElement("Entidad", XMLAttributes(PQ_UTL.getcfg('ENTIDAD_POLIZAS') AS "codigo"  -- TB_CONFIG_AGP
                                            , LPAD((ofic.Codentidad || ofic.codoficina), 12, ' ') AS "codigoInterno") -- TB_CONFIG_AGP
                                            , CASE
                                              WHEN entMed.codsubentidad IS NOT NULL THEN
                                                   XMLElement("Mediador", XMLAttributes(entMed.IDTIPOMEDIADOR_AGRO as "tipo",
                                                                                        entMed.retribucion as "retribucionAsegurado",
                                                                                        formatNumDec(entMed.importecomision, 7, 2) as "importeRetribucion"
                                                                          )
                                                             )
                                              END
                    )
       INTO  xmlEntidad

       FROM  TB_POLIZAS polizas,
             TB_ASEGURADOS asegurados,
             TB_ENTIDADES entidades,
             TB_COLECTIVOS colectivos,
             TB_SUBENTIDADES_MEDIADORAS entMed,
             TB_OFICINAS ofic,
             TB_USUARIOS usu
       WHERE polizas.idpoliza = IDPOLIZAPARAM
       AND   polizas.idasegurado = asegurados.id
       AND   polizas.idcolectivo = colectivos.id
       AND   asegurados.codentidad = entidades.codentidad
       AND   ofic.codentidad = usu.Codentidad
       AND   ofic.codoficina = usu.codoficina
       AND   usu.codusuario = polizas.codusuario
       AND   colectivos.ENTMEDIADORA = entMed.CODENTIDAD
       AND   colectivos.subentmediadora= entMed.CODSUBENTIDAD;

       RETURN xmlEntidad;
  EXCEPTION
      WHEN OTHERS THEN
           PQ_UTL.LOG('Error en getXMLEntidad');
           RAISE;
  END getXMLEntidad;

-- *****************************************************************
-- Description: Obtiene el Tag XML del Colectivo
--
-- Input Parameters: IDPOLIZAPARAM
--
-- Output Parameters: El Tag XML
--
-- Error Conditions Raised:
--
-- Author:      sergio.castro
--
-- Revision History
-- Date            Author       Reason for Change
-- ----------------------------------------------------------------
-- 03 OCT 2010     S.Castro     Created.
-- *****************************************************************
  FUNCTION getXMLColectivo(IDPOLIZAPARAM IN NUMBER) RETURN XMLType is
       xmlColectivo XMLType;
  BEGIN
       SELECT
         XMLElement("Colectivo", XMLAttributes(colectivos.idcolectivo AS "referencia",
                                               colectivos.dc AS "digitoControl",
                                               colectivos.ciftomador AS "nif")
                   )
       INTO  xmlColectivo
       FROM  TB_POLIZAS polizas,
             TB_COLECTIVOS colectivos,
             TB_SUBENTIDADES_MEDIADORAS entMed
       WHERE polizas.idpoliza = IDPOLIZAPARAM
       AND   polizas.idcolectivo = colectivos.id
       AND   colectivos.ENTMEDIADORA = entMed.CODENTIDAD
       AND   colectivos.subentmediadora= entMed.CODSUBENTIDAD;

       RETURN xmlColectivo;
  EXCEPTION
      WHEN OTHERS THEN
           PQ_UTL.LOG('Error en getXMLColectivo');
           RAISE;
  END getXMLColectivo;

-- *****************************************************************
-- Description: Obtiene el Tag XML del Asegurado
--
-- Input Parameters: IDPOLIZAPARAM
--
-- Output Parameters: El Tag XML
--
-- Error Conditions Raised:
--
-- Author:      sergio.castro
--
-- Revision History
-- Date            Author       Reason for Change
-- ----------------------------------------------------------------
-- 03 OCT 2010     S.Castro     Created.
-- *****************************************************************
  FUNCTION getXMLAsegurado(IDPOLIZAPARAM In NUMBER) RETURN XMLType is
       xmlAsegurado XMLType;
  BEGIN
       SELECT
           XMLElement("Asegurado"
               , XMLAttributes(asegurados.nifcif AS "nif")
               , CASE
                 WHEN asegurados.TIPOIDENTIFICACION = 'NIF' THEN
                      XMLElement("NombreApellidos"
                                , XMLAttributes(UPPER(asegurados.nombre) AS "nombre",
                                                UPPER(asegurados.apellido1) AS "apellido1",
                                                UPPER(asegurados.apellido2) AS "apellido2"
                                                )
                 )
                 ELSE XMLElement("RazonSocial"
                                , XMLAttributes(asegurados.razonsocial AS "razonSocial")
                                )
                 END
                 , (SELECT XMLCONCAT(XMLElement("Direccion" ,
                                          XMLAttributes(asegurados.clavevia || ' ' || UPPER(asegurados.direccion) AS "via",
                                                        asegurados.numvia AS "numero",
                                                        asegurados.piso AS "piso",
                                                        UPPER(asegurados.bloque) AS "bloque",
                                                        asegurados.escalera AS "escalera",
                                                        localidades.nomlocalidad AS "localidad",
                                                        LPAD(asegurados.codpostal, 5, '0') AS "cp",
                                                        asegurados.codprovincia AS "provincia"
                                                        )
                                                )
                             ) FROM TB_LOCALIDADES localidades
                               WHERE asegurados.CODLOCALIDAD = localidades.CODLOCALIDAD
                               AND asegurados.CODPROVINCIA = localidades.CODPROVINCIA
                               AND asegurados.SUBLOCALIDAD = localidades.SUBLOCALIDAD
                   )
                , CASE
                  WHEN (asegurados.telefono IS NOT NULL
                  OR    asegurados.movil    IS NOT NULL
                  OR    asegurados.email    IS NOT NULL) THEN
                       XMLElement ("DatosContacto",
                                  XMLAttributes(NVL(asegurados.telefono, '') AS "telefonoFijo",
                                                NVL(asegurados.movil, '') AS "telefonoMovil",
                                                NVL(UPPER(asegurados.email), '') AS "email"
                                                )
                       )
                  END
              )
        INTO  xmlAsegurado
        FROM  TB_POLIZAS polizas, TB_ASEGURADOS asegurados
        WHERE polizas.idpoliza = IDPOLIZAPARAM
        AND   polizas.idasegurado = asegurados.id;

        RETURN xmlAsegurado;
  EXCEPTION
      WHEN OTHERS THEN
           PQ_UTL.LOG('Error en getXMLAsegurado');
           RAISE;
  END getXMLAsegurado;


-- *****************************************************************
-- Description: Obtiene los TAG con las Fecha Fin de Garantias
--
-- Input Parameters: IDPOLIZAPARAM
--
-- Output Parameters: El Tag XML
--
-- Error Conditions Raised:
--
-- Author:      sergio.castro
--
-- Revision History
-- Date            Author       Reason for Change
-- ----------------------------------------------------------------
-- 03 OCT 2010     S.Castro     Created.
-- *****************************************************************
   FUNCTION getXMLFechaFinGarantias (IDPOLIZAPARAMFF IN NUMBER,
                                     IDPARCELAPARAMFF IN NUMBER,
                                     LINEASEGIDPARAMFF IN NUMBER,
                                     VALORFF IN VARCHAR2,
                                     MODULOFF IN VARCHAR2,
                                     FILACOMPARATIVAPARAMFF IN NUMBER
                                    ) RETURN XMLType is
           xmlFechaFinGarantias  XMLType;
           l_cultivo NUMBER;
           l_variedad NUMBER;
  BEGIN

    -- RECUPERO CODCULTIVO Y CODVARIEDAD  DE LA PARCELA
    SELECT codcultivo, codvariedad
      INTO l_cultivo, l_variedad
      FROM tb_parcelas
     WHERE idparcela = IDPARCELAPARAMFF;

    -- RECUPERAMOS LAS FECHAS FIN DE GARANTIAS. 1º LAS QUE NO SON ELEGIBLES Y 2º LAS QUE SI SON ELEGIBLES
    SELECT XMLCONCAT(
                     -- ELEGIBLE 'N'
                     (SELECT XMLAGG(
                               XMLElement("FecFGarant", XMLAttributes(p.codconceptoppalmod AS "cPMod",
                                          p.codriesgocubierto AS "codRCub",
                                          TO_CHAR(TO_DATE(VALORFF, STORED_DATE_FORMAT), DEFAULT_DATE_FORMAT) AS "valor")
                                          )
                                  )
                      FROM  tb_parcelas par, tb_capitales_asegurados ca, tb_sc_c_riesgo_cbrto_mod r, Tb_Sc_c_Fecha_Fin_Garantia p
                      WHERE ca.idparcela = par.idparcela and p.lineaseguroid = r.lineaseguroid AND r.codmodulo = p.codmodulo
                        AND r.codriesgocubierto = p.codriesgocubierto AND r.codconceptoppalmod = p.codconceptoppalmod
                        AND par.idpoliza = IDPOLIZAPARAMFF AND r.lineaseguroid = LINEASEGIDPARAMFF AND r.codmodulo = MODULOFF
                        AND p.fgaranthasta = TO_DATE(VALORFF, STORED_DATE_FORMAT)
                        AND p.codcultivo = l_cultivo AND (p.codvariedad = l_variedad OR p.codvariedad = 999)
                        AND (p.codprovincia = par.codprovincia or P.CODPROVINCIA = 99)
                        AND (p.codcomarca = par.codcomarca or P.CODCOMARCA = 99)
                        AND (p.codtermino = par.codtermino or P.CODTERMINO = 999)
                        AND (p.subtermino = par.subtermino or P.SUBTERMINO = '9')
                        AND p.codtipocapital = ca.codtipocapital AND r.elegible = 'N'),

                      -- ELEGIBLE 'S'
                     (SELECT XMLAGG(
                               XMLElement("FecFGarant", XMLAttributes(p.codconceptoppalmod AS "cPMod",
                                          p.codriesgocubierto AS "codRCub",
                                          TO_CHAR(TO_DATE(VALORFF, STORED_DATE_FORMAT), DEFAULT_DATE_FORMAT) AS "valor")
                                          )
                                   )
                      FROM  tb_parcelas par, tb_capitales_asegurados ca, tb_comparativas_poliza c,
                            tb_sc_c_riesgo_cbrto_mod r, Tb_Sc_c_Fecha_Fin_Garantia p

                      WHERE ca.idparcela = par.idparcela AND p.lineaseguroid = r.lineaseguroid AND p.lineaseguroid = c.lineaseguroid
                        AND r.codmodulo = p.codmodulo AND p.codmodulo = c.codmodulo
                        AND r.codriesgocubierto = p.codriesgocubierto AND p.codriesgocubierto = c.codriesgocubierto
                        AND r.codconceptoppalmod = p.codconceptoppalmod AND p.codconceptoppalmod = c.codconceptoppalmod
                        AND par.idpoliza = IDPOLIZAPARAMFF AND r.lineaseguroid = LINEASEGIDPARAMFF AND r.codmodulo = MODULOFF
                        AND p.fgaranthasta = TO_DATE(VALORFF, STORED_DATE_FORMAT)
                        AND c.idpoliza = IDPOLIZAPARAMFF
                        AND p.codcultivo = l_cultivo AND (p.codvariedad = l_variedad OR p.codvariedad = 999)
                        AND (p.codprovincia = par.codprovincia or P.CODPROVINCIA = 99)
                        AND (p.codcomarca = par.codcomarca or P.CODCOMARCA = 99)
                        AND (p.codtermino = par.codtermino or P.CODTERMINO = 999)
                        AND (p.subtermino = par.subtermino or P.SUBTERMINO = '9')
                        AND p.codtipocapital = ca.codtipocapital AND r.elegible = 'S' AND c.codconcepto = 363
                        AND c.codvalor = -1 AND c.filacomparativa = FILACOMPARATIVAPARAMFF)
                     )
    INTO xmlFechaFinGarantias
    FROM DUAL;

    --APLICAR TRANSFORMACIÓN PARA QUITAR LOS ELEMENTOS REPETIDOS

    RETURN transformaFecFGarant(xmlFechaFinGarantias);

  EXCEPTION
      WHEN OTHERS THEN
           PQ_UTL.LOG('Error en getXMLFechaFinGarantias');
           RAISE;
  END getXMLFechaFinGarantias;

-- *****************************************************************
-- Description: Transforma el XMLType de la Poliza segun el XSLT
--              indicado para ordenar correctamente los Datos
--              variables, segun el esquema de Agroseguro
--
-- Input Parameters: POLIZA
--
-- Output Parameters: El Tag XML transformado
--
-- Error Conditions Raised:
--
-- Author:      sergio.castro
--
-- Revision History
-- Date            Author       Reason for Change
-- ----------------------------------------------------------------
-- 03 OCT 2010     S.Castro     Created.
-- *****************************************************************
  FUNCTION transformaPoliza(poliza IN xmltype) RETURN xmltype is
           transformedPoliza XMLType;
  BEGIN
           transformedPoliza := poliza.transform(XMLType('
                            <xsl:stylesheet version="2.0"
                            	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                            	xmlns:pks="http://www.agroseguro.es/SeguroAgrario/Contratacion"
                            	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" exclude-result-prefixes="pks xsl xsi">
                                <xsl:output method="xml" indent="yes" omit-xml-declaration="yes"/>
                            	<xsl:template match="*">
                            		<xsl:apply-templates select="self::*" mode="copy"/>
                            	</xsl:template>
                            	<xsl:template match="//DatosVariables">
                            		<xsl:copy>
                            			<xsl:copy-of select="CalcIndem"/>
                            			<xsl:copy-of select="DnCbtos"/>
                            			<xsl:copy-of select="EFFGarant"/>
                            			<xsl:copy-of select="FecFGarant"/>
                            			<xsl:copy-of select="DurMaxGarantDias"/>
                            			<xsl:copy-of select="DurMaxGarantMeses"/>
                            			<xsl:copy-of select="EFIGarant"/>
                            			<xsl:copy-of select="FecIGarant"/>
                            			<xsl:copy-of select="DIGarant"/>
                            			<xsl:copy-of select="MIGarant"/>
                            			<xsl:copy-of select="Garant"/>
                            			<xsl:copy-of select="PerGarant"/>
                            			<xsl:copy-of select="CapAseg"/>
                            			<xsl:copy-of select="Franq"/>
                            			<xsl:copy-of select="MinIndem"/>
                            			<xsl:copy-of select="TipRdto"/>
                            			<xsl:copy-of select="RiesgCbtoEleg"/>
                            			<xsl:copy-of select="TipFranq"/>
                            			<xsl:copy-of select="Alt"/>
                            			<xsl:copy-of select="CarExpl"/>
                            			<xsl:copy-of select="CiCul"/>
                            			<xsl:copy-of select="CodDO"/>
                            			<xsl:copy-of select="CodRedRdto"/>
                            			<xsl:copy-of select="Dens"/>
                            			<xsl:copy-of select="Dest"/>
                            			<xsl:copy-of select="Edad"/>
                            			<xsl:copy-of select="FecRecol"/>
                            			<xsl:copy-of select="FecSiemTrasp"/>
                            			<xsl:copy-of select="IndGastSalv"/>
                            			<xsl:copy-of select="MedPrev"/>
                            			<xsl:copy-of select="Nadp"/>
                            			<xsl:copy-of select="NumUnid"/>
                            			<xsl:copy-of select="SisCult"/>
                            			<xsl:copy-of select="SisProd"/>
                            			<xsl:copy-of select="SisProt"/>
                            			<xsl:copy-of select="TipMcoPlant"/>
                            			<xsl:copy-of select="TipPlant"/>
                            			<xsl:copy-of select="SisCond"/>
                            			<xsl:copy-of select="TipSubDecPar"/>
                            			<xsl:copy-of select="PraCult"/>
                            		</xsl:copy>
                            	</xsl:template>
                            	<xsl:template match="*" mode="copy">
                            		<xsl:copy>
                            			<xsl:copy-of select="@*"/>
                            			<xsl:apply-templates/>
                            		</xsl:copy>
                            	</xsl:template>
                            </xsl:stylesheet>
                     '));
            RETURN transformedPoliza;
  END transformaPoliza;

-- *****************************************************************
-- Description: Elimina las fechas Fin de Garantia Repetidas
--
-- Input Parameters: FechasFin Garantia
--
-- Output Parameters: El codigo del modulo formateado
--
-- Error Conditions Raised:
--
-- Author:      sergio.castro
--
-- Revision History
-- Date            Author       Reason for Change
-- ----------------------------------------------------------------
-- 03 OCT 2010     S.Castro     Created.
-- *****************************************************************
FUNCTION transformaFecFGarant(FechasFGarant IN xmltype) RETURN xmltype IS
      transformedFechas xmltype;
  BEGIN
           transformedFechas := FechasFGarant.transform(XMLType('
                            <xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
                              <xsl:output method="xml" indent="yes"/>
                              <xsl:strip-space elements="*"/>
                              <xsl:key name="fechas" match="FecFGarant" use="concat(@cPMod, ''-'', @codRCub, ''-'', @valor)"/>
                                  <xsl:template match="node()">
                                          <xsl:copy>
                                                    <xsl:copy-of select="@*"/>
                                                    <xsl:apply-templates select="node()"/>
                                          </xsl:copy>
                                   </xsl:template>
                              <xsl:template match="FecFGarant">
                                <xsl:if test="generate-id() = generate-id(key(''fechas'', concat(@cPMod, ''-'', @codRCub, ''-'', @valor))[1])">
                                        <xsl:copy-of select="."/>
                                </xsl:if>
                              </xsl:template>
                            </xsl:stylesheet>
                     '));
            RETURN transformedFechas;
  END transformaFecFGarant;

-- *****************************************************************
-- Description: Formatea el codigo del modulo
--
-- Input Parameters: MODULO
--
-- Output Parameters: El codigo del modulo formateado
--
-- Error Conditions Raised:
--
-- Author:      sergio.castro
--
-- Revision History
-- Date            Author       Reason for Change
-- ----------------------------------------------------------------
-- 03 OCT 2010     S.Castro     Created.
-- *****************************************************************
  FUNCTION formateaModulo (modulo IN VARCHAR2) RETURN VARCHAR2 is
    longitud number := 0;
    i NUMBER := 0;
    resultante VARCHAR2(5);
  BEGIN
    longitud := 5 - LENGTH(modulo);
    IF (longitud > 0) THEN
       resultante := modulo;
       FOR i IN 0..longitud-1
       loop
           resultante := resultante || ' ';
       end loop;
    END IF;
    RETURN resultante;
  END formateaModulo;

-- *****************************************************************
-- Description: Formatea el numero indicado con la longitud y el
--              numero de decimales indicado
--
-- Input Parameters: NUMERO
--                   LONGITUD
--                   DECIMALES
--
-- Output Parameters: El numero formateado
--
-- Error Conditions Raised:
--
-- Author:      sergio.castro
--
-- Revision History
-- Date            Author       Reason for Change
-- ----------------------------------------------------------------
-- 03 OCT 2010     S.Castro     Created.
-- *****************************************************************
  FUNCTION formatNumDec(numero IN NUMBER,
                        longitud IN NUMBER,
                        decimales IN NUMBER
                       ) RETURN VARCHAR2 is
       formatNumber NUMBER;
  BEGIN
       -- Se cambia el comportamiento por defecto,
       -- el punto para decimales, y la coma para miles
       -- Los decimales en el XML de Agroseguro van con punto
       EXECUTE IMMEDIATE 'ALTER SESSION SET NLS_NUMERIC_CHARACTERS=''.,''';
       -- La longitud es la indicada en el XSD,
       -- es decir, excluye el punto de decimales
       -- Ej.: longitud=6 => 9999.99
       formatNumber := numero;
       IF numero IS NULL THEN
          formatNumber := 0;
       END IF;
       IF decimales > 0 THEN
          RETURN TRIM(TO_CHAR(ABS(formatNumber), LPAD('0', (longitud-decimales), '0') || 'D' || LPAD('9', decimales, '9')));
       END IF;

       RETURN '0.00';
  END formatNumDec;

  --------------------------------------------
  -- Devuelve una cadena separada por       --
  -- comas con los codigos de concepto      --
  -- que corresponden a los Datos Variables --
  -- dependiendo del tipo de dato variable  --
  -- que se indique:                        --
  -- 1 : Para Parcelas (Ubicacion: 16)      --
  -- 2 : Para Coberturas (Ubicacion: 18)    --
  -- El codigo de Uso es 31 Poliza          --
  --------------------------------------------
  FUNCTION getCodConceptoDV(parcelaOcobertura IN VARCHAR2,
                            lineaSeguroIdParam IN NUMBER
                           ) RETURN conceptos_varray is
       TYPE TpCursor 		 IS REF CURSOR;
       l_tp_cursor   		 TpCursor;
       l_sql             VARCHAR2(2000);
       l_codconcepto     NUMBER;
       l_counter         NUMBER := 1;
       cod_conceptos     conceptos_varray;
  BEGIN
       --Se crea un cursor para formar los codigos de concpeto
       --que son datos variables
       --CODUSO 31 para Polizas
       --CODUBICACION 16 para los Datos Variables de Parcelas
       --CODUBICACION 18 para los Datos Variables de Coberturas
       l_sql := 'SELECT D.CODCONCEPTO
                 FROM   TB_SC_DD_DIC_DATOS D,
                        TB_SC_OI_ORG_INFO T,
                        TB_SC_OI_UBICACIONES U,
                        TB_SC_OI_USOS US
                 WHERE T.CODUBICACION = ' || parcelaOcobertura || '
                 AND   T.CODUSO = ' || USO_PARCELA || '
                 AND   T.CODUSO = US.CODUSO
                 AND   T.CODCONCEPTO = D.CODCONCEPTO
                 AND   T.CODUBICACION = U.CODUBICACION
                 AND   T.LINEASEGUROID = ' || lineaSeguroIdParam;
       OPEN l_tp_cursor FOR l_sql;
       FETCH l_tp_cursor INTO l_codconcepto;
       cod_conceptos := conceptos_varray();
       cod_conceptos.EXTEND;
       LOOP
           cod_conceptos(l_counter) := l_codconcepto;
           FETCH l_tp_cursor INTO l_codconcepto;
           l_counter := l_counter + 1;
           EXIT WHEN l_tp_cursor%NOTFOUND; -- No hay mas registros
           cod_conceptos.EXTEND;
       END LOOP;

       RETURN cod_conceptos;
  END getCodConceptoDV;

  ---------------------------------------------
  -- Devuelve los Datos variables de Parcela --
  -- o Cobertura que necesitan los atributos --
  -- de Concepto Principal y Riego Cubierto  --
  -- 1 : Para Parcelas (Ubicacion: 16)       --
  -- 2 : Para Coberturas (Ubicacion: 18)     --
  -- El codigo de Uso es 31 Poliza           --
  ---------------------------------------------
  FUNCTION getDVConAtributos(parcelaOcobertura IN VARCHAR2,
                            lineaSeguroIdParam IN NUMBER
                            ) RETURN conceptos_varray is
       TYPE TpCursor 		 IS REF CURSOR;
       l_tp_cursor   		 TpCursor;
       l_sql             VARCHAR2(2000);
       l_codconcepto     NUMBER;
       l_counter         NUMBER := 1;
       cod_conceptos     conceptos_varray;
  BEGIN
       l_sql := 'select dic.codconcepto from
                        tb_sc_dd_dic_datos dic,
                        tb_sc_oi_org_info orginfo
                 where  dic.codconcepto = orginfo.codconcepto
                 and    dic.DEPRIESGOCBRTO = ''S''
                 and    orginfo.codubicacion = ' || parcelaOcobertura || '
                 and    orginfo.coduso = ' || USO_PARCELA || '
                 and    orginfo.lineaseguroid = ' || lineaSeguroIdParam;

       OPEN l_tp_cursor FOR l_sql;
       FETCH l_tp_cursor INTO l_codconcepto;
       cod_conceptos := conceptos_varray();
       cod_conceptos.EXTEND;
       LOOP
           cod_conceptos(l_counter) := l_codconcepto;
           FETCH l_tp_cursor INTO l_codconcepto;
           l_counter := l_counter + 1;
           EXIT WHEN l_tp_cursor%NOTFOUND; -- No hay mas registros
           cod_conceptos.EXTEND;
       END LOOP;

       RETURN cod_conceptos;

  END getDVConAtributos;

  -----------------------------------
  -- Extrae el valor de un XMLType --
  -----------------------------------
  FUNCTION XML_EXTRACT_NO_EXCEPTION ( p_xml IN XMLTYPE,
                                      p_xpath IN VARCHAR2,
                                      p_namespace IN VARCHAR2 default NULL
                                    ) RETURN VARCHAR2 IS
  BEGIN
       RETURN case when p_xml.extract(p_xpath, p_namespace) is not null
                   then p_xml.extract(p_xpath, p_namespace).getstringval()
              else NULL
              end;
  END XML_EXTRACT_NO_EXCEPTION;

END PQ_CREAXML;
/
SHOW ERRORS;