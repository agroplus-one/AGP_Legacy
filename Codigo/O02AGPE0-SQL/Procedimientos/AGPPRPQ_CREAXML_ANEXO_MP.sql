SET DEFINE OFF;
SET SERVEROUTPUT ON;

create or replace package o02agpe0.PQ_CREAXML_ANEXO_MP is

  -- Author  :  U029803
  -- Created : 29/12/2010 13:43:43
  --- Purpose : Paquete generacion Anexo Modificacion Poliza

  -- Procedimiento Creacion XML Siniestro
  FUNCTION generaXMLAnexoMP(IDANEXO IN NUMBER
                               ) RETURN NUMBER;

  FUNCTION getXMLCoberturasAnexoMP(IDANEXO IN NUMBER, CODMODULO IN VARCHAR2) RETURN XMLType;
  FUNCTION getXMLParcelasAnexoMP(IDANEXO IN NUMBER) RETURN XMLType;
  FUNCTION getXMLSubvDecAnexoMP(IDANEXO IN NUMBER) RETURN XMLType;
end PQ_CREAXML_ANEXO_MP;
/
create or replace package body o02agpe0.PQ_CREAXML_ANEXO_MP is

LOG_LEVEL            VARCHAR2(1)  := '2';

---- Variables Globales para todas las funciones
TIPO_NATURALEZA_DATE TB_SC_DD_TIPO_NATURALEZA.CODTIPONATURALEZA%TYPE := 4;
DEFAULT_DATE_FORMAT  VARCHAR2(10) := 'YYYY-MM-DD';
STORED_DATE_FORMAT   VARCHAR2(10) := 'DD/MM/YYYY';
TIPO_DV_COBERTURAS   VARCHAR2(2)  := '18';

/*
  Funcion: generaXMLAnexoMP
  Autor: T-SYSTEMS
  Fecha:
  Descripcion: Funcion que general el XML del Anexo MP lo inserta en el CLOB de Anexo MP
  */
FUNCTION generaXMLAnexoMP (IDANEXO IN NUMBER
                          ) RETURN NUMBER
AS
     v_xml            CLOB;
     v_xml_temp       XMLTYPE;
     v_rootElement    VARCHAR2(50):= '<?xml version="1.0" encoding="UTF-8"?>';
     v_idcopy         NUMBER := NULL;
     idInsercion      NUMBER(15)  := IDANEXO;
     c_coberturas     NUMBER := NULL;
     c_subv_decl      NUMBER := NULL;
     c_parcelas       NUMBER := NULL;

     no_validado_exception EXCEPTION;

begin

     EXECUTE IMMEDIATE
       'SELECT A.IDCOPY FROM TB_ANEXO_MOD A WHERE A.ID = :idInsercion'
     INTO v_idcopy
     USING idInsercion;

     EXECUTE IMMEDIATE
       'SELECT COUNT(*) FROM TB_ANEXO_MOD_COBERTURAS C WHERE C.IDANEXO = :idInsercion'
     INTO c_coberturas
     USING idInsercion;

      EXECUTE IMMEDIATE
       'SELECT COUNT(*) FROM TB_ANEXO_MOD_PARCELAS P WHERE P.IDANEXO =  :idInsercion'
     INTO c_parcelas
     USING idInsercion;

     EXECUTE IMMEDIATE
       'SELECT COUNT(*) FROM TB_ANEXO_MOD_SUBV_DECL S WHERE S.IDANEXO =  :idInsercion'
     INTO c_subv_decl
     USING idInsercion;

      IF v_idcopy IS NULL THEN
       SELECT
         XMLElement("Poliza"
              , XMLAttributes(LIN.CODPLAN AS "plan",
                              LIN.CODLINEA AS "linea",
                              P.REFERENCIA AS "referencia",
                              P.DC AS "digitoControl",
                              ASEG.NIFCIF AS "nifAsegurado",
                              to_char(A.FECHAFIRMADOC, 'yyyy-mm-dd') AS "fechaFirmaDocumento",
                              A.ASUNTO AS "asunto")
               , XMLElement("Colectivo"
                       , XMLAttributes(COL.IDCOLECTIVO AS "referencia",
                                       COL.DC AS "digitoControl",
                                       COL.CIFTOMADOR AS "nif"))
               , XMLElement("Asegurado"
                  , XMLAttributes(ASEG.NIFCIF AS "nif")
                    , CASE
                       WHEN ASEG.NOMBRE IS NOT NULL THEN
                          XMLElement("NombreApellidos"
                            , XMLAttributes(UPPER(ASEG.NOMBRE) AS "nombre",
                                            UPPER(ASEG.APELLIDO1) AS "apellido1",
                                            UPPER(ASEG.APELLIDO2) AS "apellido2"))
                       ELSE XMLElement("RazonSocial"
                                , XMLAttributes(UPPER(ASEG.RAZONSOCIAL) AS "razonSocial"))
                      END
                  , XMLElement("Direccion"
                      ,XMLAttributes(ASEG.clavevia || ' ' || UPPER(ASEG.direccion) AS "via",
                                     ASEG.numvia AS "numero",
                                     ASEG.piso AS "piso",
                                     UPPER(ASEG.bloque) AS "bloque",
                                     ASEG.escalera AS "escalera",
                                     LOC.nomlocalidad AS "localidad",
                                     LPAD(ASEG.codpostal, 5, '0') AS "cp",
                                     ASEG.codprovincia AS "provincia"))
                  , CASE
                      WHEN (ASEG.telefono IS NOT NULL
                      OR    ASEG.movil    IS NOT NULL
                      OR    ASEG.email    IS NOT NULL) THEN
                       XMLElement ("DatosContacto",
                                  XMLAttributes(NVL(ASEG.telefono, '') AS "telefonoFijo",
                                                NVL(ASEG.movil, '') AS "telefonoMovil",
                                                NVL(UPPER(ASEG.email), '') AS "email"))
                    END
                    )
               ,
                 PQ_CREAXML.getXMLEntidad(P.IDPOLIZA)

               , CASE WHEN c_coberturas > 0 THEN
                    XMLElement("Cobertura"
                         , getXMLCoberturasAnexoMP(idInsercion, A.CODMODULO))
               END
               , CASE WHEN c_parcelas > 0 THEN
                    XMLElement("Objetos Asegurados"
                         , getXMLParcelasAnexoMP(idInsercion))
               END
               , XMLElement("Pago"
                      )
               , CASE WHEN c_subv_decl > 0 THEN
                    XMLElement("Objetos Asegurados"
                         , getXMLSubvDecAnexoMP(idInsercion))
                 END
                   ) as XML
        INTO v_xml_temp
        FROM TB_ANEXO_MOD A,
             TB_POLIZAS P,
             TB_ASEGURADOS ASEG,
             TB_COLECTIVOS COL,
             TB_LINEAS LIN,
             TB_LOCALIDADES LOC
        WHERE A.IDPOLIZA= P.IDPOLIZA
        AND P.IDASEGURADO = ASEG.ID
        AND P.IDCOLECTIVO = COL.ID
        AND P.LINEASEGUROID = LIN.LINEASEGUROID
        AND ASEG.CODPROVINCIA = LOC.CODPROVINCIA
        AND ASEG.CODLOCALIDAD = LOC.CODLOCALIDAD
        AND ASEG.SUBLOCALIDAD = LOC.SUBLOCALIDAD
        AND A.ID = idInsercion;

      ELSE
        SELECT
         XMLElement("Poliza"
              , XMLAttributes(P.CODPLAN AS "plan",
                              P.CODLINEA AS "linea",
                              P.REFPOLIZA AS "referencia",
                              P.DCPOLIZA AS "digitoControl",
                              ASEG.NIFCIF AS "nifAsegurado",
                              to_char(A.FECHAFIRMADOC, 'yyyy-mm-dd') AS "fechaFirmaDocumento",
                              A.ASUNTO AS "asunto")
               , XMLElement("Colectivo"
                       , XMLAttributes(COL.REFCOLECTIVO AS "referencia",
                                       COL.DCCOLECTIVO AS "digitoControl",
                                       COL.CIFNIFTOMADOR AS "nif"))
               , XMLElement("Asegurado"
                  , XMLAttributes(ASEG.NIFCIF AS "nif")
                    , CASE
                       WHEN ASEG.NOMBREASEG IS NOT NULL THEN
                          XMLElement("NombreApellidos"
                            , XMLAttributes(ASEG.NOMBREASEG AS "nombre",
                                            ASEG.APELL1ASEG AS "apellido1",
                                            ASEG.APELL2ASEG AS "apellido2"))
                       ELSE XMLElement("RazonSocial"
                                , XMLAttributes(ASEG.RAZONSOCIALASEG AS "razonSocial"))
                      END
                  , XMLElement("Direccion"
                     , XMLAttributes(ASEG.VIAASEG AS "via",
                                     ASEG.NUMEROVIAASEG AS "numero",
                                     ASEG.PISOASEG AS "piso",
                                     ASEG.BLOQUEASEG AS "bloque",
                                     ASEG.ESCALERAASEG AS "escalera",
                                     ASEG.LOCALIDADASEG AS "localidad",
                                     ASEG.CPASEG AS "cp",
                                     ASEG.PROVINCIAASEG AS "provincia"))
                    , XMLElement ("DatosContacto"
                      , XMLAttributes(ASEG.TELEFONOFIJO AS "telefonoFijo",
                                      ASEG.TELEFONOMOVIL AS "telefonoMovil",
                                      ASEG.EMAIL AS "email")))
               , XMLElement("Entidad"
                  , XMLAttributes(P.CODENTIDADASEG AS "codigo",
                                  P.CODINTERNOENTIDAD AS "codigoInterno")
                  , CASE WHEN P.TIPOMEDIADOR is not null THEN
                        XMLElement("Mediador",
                           XMLAttributes(P.TIPOMEDIADOR AS "tipo",
                                         P.RECIBERETRIBUCION AS "retribucion",
                                         P.IMPORTERETRIBUCION AS "importeRetribucion"))
                    END)

               , CASE WHEN c_coberturas > 0 THEN
                    XMLElement("Cobertura"
                         , getXMLCoberturasAnexoMP(idInsercion, A.CODMODULO))
               END
               , CASE WHEN c_parcelas > 0 THEN
                    XMLElement("Objetos Asegurados"
                         , getXMLParcelasAnexoMP(idInsercion))
               END
               , XMLElement("Pago"
                      )
               , CASE WHEN c_subv_decl > 0 THEN
                    XMLElement("Objetos Asegurados"
                         , getXMLSubvDecAnexoMP(idInsercion))
               END
                   ) as XML
        INTO v_xml_temp
        FROM TB_ANEXO_MOD A,
             TB_COPY_POLIZAS P,
             TB_COPY_ASEGURADOS ASEG,
             TB_COPY_COLECTIVOS COL
        WHERE A.IDCOPY = P.ID
        AND ASEG.IDCOPY = P.ID
        AND P.IDCOLECTIVO = COL.ID
        AND A.ID = idInsercion;

      END IF;

      BEGIN
       PQ_UTL.LOG('XML=> ' || v_xml_temp.getStringVal(), LOG_LEVEL);
       --Se incluye la cabecera XML
       v_xml := v_rootElement || v_xml_temp.getClobVal;
       --Se inserta en la Tabla de Envios a agroseguro el XML
       UPDATE TB_ANEXO_MOD A SET A.XML = v_xml
       where A.ID = IDANEXO;
    --     RETURNING ID INTO idInsercion;

       COMMIT;
     EXCEPTION
       WHEN no_validado_exception THEN
           PQ_UTL.LOG('EL XML no se ha podido validar contra el esquema!!', LOG_LEVEL);
           PQ_UTL.LOG('XML=> ' || v_xml_temp.getStringVal(), LOG_LEVEL);
           idInsercion := -1;
      WHEN NO_DATA_FOUND THEN
         idInsercion := -1;
     END;-- Exception
    PQ_UTL.LOG('XML Insertado en TB_ANEXO_MOD. idInsercion: ' || idInsercion, LOG_LEVEL);
    RETURN idInsercion;

RETURN NULL;

END generaXMLAnexoMP;

FUNCTION getXMLCoberturasAnexoMP(IDANEXO IN NUMBER, CODMODULO IN VARCHAR2) RETURN XMLType is
      xmlCobertura XMLType;
      xmlDVCobertura XMLType := null;
      lineaSeguroIdCB NUMBER;
  BEGIN
       -- Primero obtenemos el lineaseguroid a partir del identificador del anexo y a trav?del IDPOLIZA
       EXECUTE IMMEDIATE 'SELECT LINEASEGUROID FROM TB_ANEXO_MOD M, TB_POLIZAS P WHERE M.ID = ' || IDANEXO || ' AND M.IDPOLIZA = P.IDPOLIZA' into lineaSeguroIdCB;
       -- Segundo montamos el xml de datos variables
      SELECT
           XMLElement("DatosVariables",(select XMLAgg (
               CASE
               WHEN cob.CODCONCEPTO IN
                        (SELECT column_value FROM THE(SELECT PQ_CREAXML.getCodConceptoDV(TIPO_DV_COBERTURAS, lineaSeguroIdCB) FROM DUAL)) THEN
                    CASE
                    WHEN dic.CODCONCEPTO IN
                          (SELECT column_value FROM THE(SELECT PQ_CREAXML.getDVConAtributos(TIPO_DV_COBERTURAS, lineaSeguroIdCB) FROM DUAL)) THEN
                               CASE
                               -- Tipo de Datos Fecha
                               WHEN dic.CODTIPONATURALEZA = TIPO_NATURALEZA_DATE THEN
                                   XMLElement(EVALNAME dic.etiquetaxml,
                                              XMLAttributes(cob.CODCONCEPTOPPALMOD as "cPMod",
                                                            cob.CODRIESGOCUBIERTO as "codRCub",
                                                            TO_CHAR(TO_DATE(cob.CODVALOR , STORED_DATE_FORMAT), DEFAULT_DATE_FORMAT) as "valor"
                                                           )
                                             )
                               ELSE
                                   CASE
                                   WHEN cob.CODVALOR = -1 THEN
                                       XMLElement(EVALNAME dic.etiquetaxml,
                                                  XMLAttributes(cob.CODCONCEPTOPPALMOD as "cPMod",
                                                                cob.CODRIESGOCUBIERTO as "codRCub",
                                                                'S' as "valor"
                                                               )
                                                 )
                                   WHEN cob.CODVALOR = -2 THEN
                                       XMLElement(EVALNAME dic.etiquetaxml,
                                                  XMLAttributes(cob.CODCONCEPTOPPALMOD as "cPMod",
                                                                cob.CODRIESGOCUBIERTO as "codRCub",
                                                                'N' as "valor"
                                                               )
                                                 )
                                   ELSE
                                       XMLElement(EVALNAME dic.etiquetaxml,
                                                  XMLAttributes(cob.CODCONCEPTOPPALMOD as "cPMod",
                                                                cob.CODRIESGOCUBIERTO as "codRCub",
                                                                cob.CODVALOR as "valor"
                                                               )
                                                 )
                                   END
                               END
                    ELSE
                               CASE
                               -- Tipo de Datos Fecha
                               WHEN dic.CODTIPONATURALEZA = TIPO_NATURALEZA_DATE THEN
                                 XMLElement(EVALNAME dic.etiquetaxml,
                                            XMLAttributes(TO_CHAR(TO_DATE(cob.CODVALOR , STORED_DATE_FORMAT), DEFAULT_DATE_FORMAT) as "valor"))
                               ELSE
                                 XMLElement(EVALNAME dic.etiquetaxml,
                                            XMLAttributes(cob.CODVALOR  as "valor"))
                               END
                    END
                ELSE
                         CASE
                         -- Tipo de Datos Fecha
                         WHEN dic.CODTIPONATURALEZA = TIPO_NATURALEZA_DATE THEN
                           XMLElement(EVALNAME dic.etiquetaxml,
                                      XMLAttributes(TO_CHAR(TO_DATE(cob.CODVALOR , STORED_DATE_FORMAT), DEFAULT_DATE_FORMAT) as "valor"))
                         ELSE
                           XMLElement(EVALNAME dic.etiquetaxml,
                                      XMLAttributes(cob.CODVALOR  as "valor"))
                         END
                END
              ) -- XMLAgg
                   FROM  TB_ANEXO_MOD_COBERTURAS cob,
                         TB_SC_DD_DIC_DATOS     dic
                   WHERE dic.codconcepto      = cob.codconcepto
                   AND   cob.IDANEXO   = IDANEXO
               )
             )
       INTO xmlDVCobertura
       FROM DUAL;
       --Se comprueba si se recogieron datos variables,
       --para devolver el tag vacio en caso de que no
       IF xmlDVCobertura IS NOT NULL AND
          PQ_CREAXML.XML_EXTRACT_NO_EXCEPTION(xmlDVCobertura, 'DatosVariables/node()') IS NULL THEN
          xmlDVCobertura := null;
       END IF;

       -- Por ?o lo unimos con el xml de coberturas
       SELECT
          XMLElement("Cobertura", XMLAttributes(PQ_CREAXML.formateaModulo(CODMODULO) AS "modulo"),
                     xmlDVCobertura
                    )
       INTO  xmlCobertura
       FROM  DUAL;

      RETURN xmlCobertura;
END getXMLCoberturasAnexoMP;


FUNCTION getXMLParcelasAnexoMP(IDANEXO IN NUMBER) RETURN XMLType is
      xmlParcelas XMLType;
  BEGIN
      SELECT XMLAgg(XMLElement("Parcela"
                    , XMLAttributes(PARC.HOJA AS "hoja",
                                    PARC.NUMERO AS "numero")
                    , XMLElement("Ubicacion"
                          , XMLAttributes(PARC.CODPROVINCIA AS "provincia",
                                    PARC.CODTERMINO AS "termino",
                                    PARC.SUBTERMINO AS "subtermino",
                                    PARC.CODCOMARCA AS "comarca"))  --opcional
                    , XMLElement("Cosecha"  --******Cosecha o Especie
                         , XMLAttributes(PARC.CODCULTIVO AS "cultivo",
                                         PARC.CODVARIEDAD AS "variedad")
                         , XMLElement("Capitales Asegurados"
                             , (SELECT XMLAgg(XMLElement ("Capital Asegurado"
                                              , XMLAttributes( CAP.CODTIPOCAPITAL AS "tipo",
                                                               CAP.SUPERFICIE AS "superficie",
                                                               CAP.PRECIO AS "precio",
                                                               CAP.PRODUCCION AS "produccionDeclarada")))
                                FROM TB_ANEXO_MOD_CAPITALES_ASEG CAP
                                WHERE CAP.IDPARCELAANEXO = PARC.ID)))
                    , CASE
                         WHEN PARC.POLIGONO IS NOT NULL THEN
                           XMLElement("IdentificacionCatastral"  --******IdentificacionCatastral o SIGPAC
                                , XMLAttributes(PARC.POLIGONO AS "poligono",
                                                PARC.PARCELA AS "parcela"))
                         WHEN PARC.CODPROVSIGPAC IS NOT NULL THEN
                           XMLElement("SIGPAC"  --******IdentificacionCatastral o SIGPAC
                                , XMLAttributes(PARC.CODPROVSIGPAC AS "provincia",
                                                PARC.CODTERMSIGPAC AS "termino",
                                                PARC.AGRSIGPAC AS "agregado",
                                                PARC.ZONASIGPAC AS "zona",
                                                PARC.POLIGONOSIGPAC AS "poligono",
                                                PARC.PARCELASIGPAC AS "parcela",
                                                PARC.RECINTOSIGPAC AS "recinto"))
                      END))
    INTO xmlParcelas
    FROM TB_ANEXO_MOD_PARCELAS PARC
    WHERE PARC.IDANEXO = IDANEXO;

      RETURN xmlParcelas;
END getXMLParcelasAnexoMP;

FUNCTION getXMLSubvDecAnexoMP(IDANEXO IN NUMBER) RETURN XMLType is
      xmlSubvencionesDeclaradas XMLType;
  BEGIN
      SELECT
        XMLElement("SubvencionesDeclaradas",
                   CASE
                     WHEN (m.numsegsocial is not null) THEN
                      XMLCONCAT( XMLElement("SeguridadSocial",
                                   XMLAttributes(SUBSTR(m.numsegsocial,1,2) as "provincia",
                                                 SUBSTR(m.numsegsocial,3,8) as "numero",
                                                 SUBSTR(m.numsegsocial,11,2) as "codigo",
                                                m.regimensegsocial as "regimen"
                                                )
                                            ),
                                 XMLElement("SubvencionDeclarada",
                                               XMLAttributes ('20' as "tipo")
                                           )
                                )
                     END,
                     --El campo CODSUBVENCION se refiere al tipo de subvenci??eclarada (DES-D-0083)
                     XMLCONCAT( XMLElement("SubvencionDeclarada",
                                               XMLAttributes (subv.CODSUBVENCION as "tipo")
                                              )
                                   )
                  )
       INTO  xmlSubvencionesDeclaradas
       FROM  TB_ANEXO_MOD m,
             TB_ANEXO_MOD_SUBV_DECL subv
       WHERE m.id = IDANEXO
       AND   m.id = subv.idanexo;

       IF PQ_CREAXML.XML_EXTRACT_NO_EXCEPTION(xmlSubvencionesDeclaradas, 'SubvencionesDeclaradas/node()') IS NULL THEN
          RETURN NULL;
       END IF;


      RETURN xmlSubvencionesDeclaradas;
END getXMLSubvDecAnexoMP;


end PQ_CREAXML_ANEXO_MP;
/
SHOW ERRORS;
