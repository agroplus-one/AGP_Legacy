SET DEFINE OFF;
SET SERVEROUTPUT ON;

create or replace package o02agpe0.PQ_CREAXML_ANEXO_RC is

  -- Author  : U029803
  --- Created : 20/12/2010 13:32:56
  -- Changed : 17/07/2019
  -- Purpose : Creacion XML Siniestros

  -- Procedimiento Creacion XML Siniestro
  FUNCTION generaXMLAnexoRC(IDANEXO IN NUMBER) RETURN NUMBER;

  FUNCTION getXMLParcelasAnexoRC(P_IDANEXO IN NUMBER) RETURN XMLType;

end PQ_CREAXML_ANEXO_RC;
/

create or replace package body o02agpe0.PQ_CREAXML_ANEXO_RC is

LOG_LEVEL            VARCHAR2(1)  := '2';

/* 
  Funcion: generaXMLAnexoRC
  Autor: T-SYSTEMS
  Fecha:
  Descripcion: Funcion que general el XML del Anexo RC lo inserta en el CLOB de Anexo RC
  */
FUNCTION generaXMLAnexoRC (IDANEXO IN NUMBER
                          ) RETURN NUMBER
AS
     v_xml            CLOB;
     v_xml_temp       XMLTYPE;
     v_rootElement    VARCHAR2(50):= '<?xml version="1.0" encoding="UTF-8"?>';
     v_idcopy         NUMBER := NULL;
     idInsercion      NUMBER(15)  := IDANEXO;

     no_validado_exception EXCEPTION;

     namespace        VARCHAR2(2000) := 'http://www.agroseguro.es/SeguroAgrario/Contratacion/ReduccionCapital';

begin

     EXECUTE IMMEDIATE
       'SELECT A.IDCOPY FROM TB_ANEXO_RED A WHERE A.ID = :idInsercion'
     INTO v_idcopy
     USING idInsercion;

     dbms_output.put_line('v_idcopy='||v_idcopy);

     IF v_idcopy IS NULL THEN
      SELECT
       XMLElement("ns2:Poliza"
              , XMLAttributes(namespace AS "xmlns:ns2",
                              LIN.CODPLAN AS "plan",
                              LIN.CODLINEA AS "linea",
                              P.REFERENCIA AS "referencia",
                              P.DC AS "digitoControl",
                              to_char(A.FECHADANIOS, 'yyyy-mm-dd') AS "fechaFirmaSolicitud",
                              RPAD(NVL(A.CODMODULO, P.CODMODULO), 5, ' ') AS "modulo"
                              )
               , XMLElement("Colectivo"
                       , XMLAttributes(COL.IDCOLECTIVO AS "referencia",
                                       COL.DC AS "digitoControl",
                                       COL.CIFTOMADOR AS "nif"))
               , XMLElement("Asegurado"
                      , XMLAttributes(ASEG.NIFCIF AS "nif")
                      , CASE
                          WHEN ASEG.NOMBRE IS NOT NULL THEN
                              XMLElement("NombreApellidos"
                                 , XMLAttributes(ASEG.NOMBRE AS "nombre",
                                                 ASEG.APELLIDO1 AS "apellido1",
                                                 ASEG.APELLIDO2 AS "apellido2"))
                          ELSE XMLElement("RazonSocial"
                                 , XMLAttributes(ASEG.RAZONSOCIAL AS "razonSocial"))
                        END)
               , XMLElement("Motivo"
                     , XMLAttributes(to_char(A.FECHADANIOS, 'yyyy-mm-dd') AS "fecha",
                                             lpad(A.CODMOTIVORIESGO, 2, '0') AS "codigo",
                                             A.MOTIVO AS "descripcion"))
               , XMLElement("ObjetosAsegurados"
                                    , getXMLParcelasAnexoRC(idInsercion)
                                   )
                   ) as XML
      INTO v_xml_temp
      FROM TB_ANEXO_RED A,
           TB_POLIZAS P,
           TB_ASEGURADOS ASEG,
           TB_COLECTIVOS COL,
           TB_LINEAS LIN
      WHERE A.IDPOLIZA= P.IDPOLIZA
      AND P.IDASEGURADO = ASEG.ID
      AND P.IDCOLECTIVO = COL.ID
      AND P.LINEASEGUROID = LIN.LINEASEGUROID
      AND A.ID = idInsercion;

     ELSE

      SELECT
       XMLElement("ns2:Poliza"
              , XMLAttributes(namespace AS "xmlns:ns2",
                              P.CODPLAN AS "plan",
                              P.CODLINEA AS "linea",
                              P.REFPOLIZA AS "referencia",
                              P.DCPOLIZA AS "digitoControl",
                              to_char(A.FECHADANIOS, 'yyyy-mm-dd') AS "fechaFirmaSolicitud",
                              RPAD(NVL(A.CODMODULO, P.CODMODULO), 5, ' ') AS "modulo"
                              )
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
                        END)
               , XMLElement("Motivo"
                     , XMLAttributes(to_char(A.FECHADANIOS, 'yyyy-mm-dd') AS "fecha",
                                             lpad(A.CODMOTIVORIESGO, 2, '0') AS "codigo",
                                             A.MOTIVO AS "descripcion"))
               , XMLElement("ObjetosAsegurados"
                                    , getXMLParcelasAnexoRC(idInsercion)
                                   )
                   ) as XML
      INTO v_xml_temp
      FROM TB_ANEXO_RED A,
           TB_COPY_POLIZAS P,
           TB_COPY_ASEGURADOS ASEG,
           TB_COPY_COLECTIVOS COL
      WHERE A.IDCOPY = P.ID
      AND ASEG.IDCOPY = P.ID
      AND P.ID = COL.IDCOPY
      AND A.ID = idInsercion;
     END IF;

     BEGIN
       --Se incluye la cabecera XML
       v_xml := v_rootElement || v_xml_temp.getClobVal;
       --Se inserta en la Tabla de Envios a agroseguro el XML
       UPDATE TB_ANEXO_RED A SET A.XML = v_xml
       where A.ID = IDANEXO;
    --     RETURNING ID INTO idInsercion;
       COMMIT;
       --PQ_UTL.LOG('XML=> ' || v_xml_temp.getStringVal(), LOG_LEVEL);
     EXCEPTION
       WHEN no_validado_exception THEN
           PQ_UTL.LOG('EL XML no se ha podido validar contra el esquema!!', LOG_LEVEL);
           PQ_UTL.LOG('XML=> ' || v_xml_temp.getStringVal(), LOG_LEVEL);
           idInsercion := -1;
      WHEN NO_DATA_FOUND THEN
         idInsercion := -1;
      WHEN OTHERS THEN
         idInsercion := -1;
         PQ_UTL.log('generaXMLAnexoRC - ERROR: ' || SQLCODE || ' - ' || SQLERRM || ' ***', 2);
     END;-- Exception
    PQ_UTL.LOG('XML Insertado en TB_ANEXO_RED. idInsercion: ' || idInsercion, LOG_LEVEL);
    RETURN idInsercion;
END generaXMLAnexoRC;

FUNCTION getXMLParcelasAnexoRC(P_IDANEXO IN NUMBER) RETURN XMLType is
      xmlParcelas XMLType;
  BEGIN

  dbms_output.put_line('getXMLParcelasAnexoRC idanexo='||P_IDANEXO);

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
                         , XMLElement("CapitalesAsegurados"
                             , (SELECT XMLAgg(XMLElement ("CapitalAsegurado"
                                              , XMLAttributes( CAP.CODTIPOCAPITAL AS "tipo",
                                                               trim(to_char(CAP.SUPERFICIE, '9999990.00')) AS "superficie",
                                                               trim(to_char(CAP.PRECIO, '9999990.00')) AS "precio",
                                                               CAP.PROD AS "produccionDeclarada",
                                                               CAP.PRODRED AS "produccionTrasDanos")))
                                FROM TB_ANEXO_RED_CAP_ASEG CAP
                                WHERE CAP.IDPARCELAANEXO = PARC.ID
                                AND CAP.ALTAENANEXO = 'S')))  --opcional
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
    FROM TB_ANEXO_RED_PARCELAS PARC
    WHERE PARC.IDANEXO = P_IDANEXO
    AND PARC.ALTAENANEXO = 'S';

      RETURN xmlParcelas;
  END getXMLParcelasAnexoRC;


end PQ_CREAXML_ANEXO_RC;
/

SHOW ERRORS;