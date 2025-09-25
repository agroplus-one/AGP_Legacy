CREATE OR REPLACE VIEW O02AGPE0.VW_ORG_DATOS_POR_FACTORES
(lineaseguroid, codmodulo, codconcepto, codvalor, descripcion)
AS
SELECT DISTINCT
       F.LINEASEGUROID,
       F.CODMODULO,
       F.CODCONCEPTO,
       F.VALORCONCEPTO,
       NAP.DESNUMANIOSPODA
FROM o02agpe0.TB_SC_C_FACTORES F,
     o02agpe0.TB_SC_C_NUM_ANIOS_PODA NAP
WHERE F.CODCONCEPTO = 617 AND
      F.VALORCONCEPTO = NAP.CODNUMANIOSPODA
UNION      
SELECT DISTINCT
       F.LINEASEGUROID,
       F.CODMODULO,
       F.CODCONCEPTO,
       F.VALORCONCEPTO,
       TMP.DESTIPOMARCOPLANTAC
FROM o02agpe0.TB_SC_C_FACTORES F,
     o02agpe0.Tb_SC_C_MARCO_PLANTACION TMP
WHERE F.CODCONCEPTO = 116 AND
      F.VALORCONCEPTO = TMP.CODTIPOMARCOPLANTAC
UNION
SELECT DISTINCT
       F.LINEASEGUROID,
       F.CODMODULO,
       F.CODCONCEPTO,
       F.VALORCONCEPTO,
       ROT.DESALTERNATIVA
FROM o02agpe0.TB_SC_C_FACTORES F,
     o02agpe0.Tb_SC_C_ALTERNATIVAS ROT
WHERE F.CODCONCEPTO = 144 AND
      F.VALORCONCEPTO = ROT.CODALTERNATIVA
UNION
SELECT DISTINCT
       F.LINEASEGUROID,
       F.CODMODULO,
       F.CODCONCEPTO,
       F.VALORCONCEPTO,
       MESC.DESCRIPCION
FROM o02agpe0.TB_SC_C_FACTORES F,
     o02agpe0.TB_SC_C_MATERIAL_ESTRUCTURA MESC
WHERE F.CODCONCEPTO = 875 AND
      F.VALORCONCEPTO = MESC.CODMATERIALESTRUCTURA
UNION
SELECT DISTINCT
       F.LINEASEGUROID,
       F.CODMODULO,
       F.CODCONCEPTO,
       F.VALORCONCEPTO,
       D.DESDESTINO
FROM o02agpe0.TB_SC_C_FACTORES F,
     o02agpe0.TB_SC_C_DESTINOS D
WHERE F.CODCONCEPTO = 110 AND
      F.VALORCONCEPTO = D.CODDESTINO
UNION
SELECT DISTINCT
       F.LINEASEGUROID,
       F.CODMODULO,
       F.CODCONCEPTO,
       F.VALORCONCEPTO,
       SC.DESSISTEMACONDUCCION
FROM o02agpe0.TB_SC_C_FACTORES F,
     o02agpe0.TB_SC_C_SISTEMA_CONDUCCION SC
WHERE F.CODCONCEPTO = 131 AND
      F.VALORCONCEPTO = SC.CODSISTEMACONDUCCION
UNION
SELECT DISTINCT
       F.LINEASEGUROID,
       F.CODMODULO,
       F.CODCONCEPTO,
       F.VALORCONCEPTO,
       SP.DESSISTEMAPRODUCCION
FROM o02agpe0.TB_SC_C_FACTORES F,
     o02agpe0.TB_SC_C_SISTEMA_PRODUCCION SP
WHERE F.CODCONCEPTO = 616 AND
      F.VALORCONCEPTO = SP.CODSISTEMAPRODUCCION
UNION
SELECT DISTINCT
       F.LINEASEGUROID,
       F.CODMODULO,
       F.CODCONCEPTO,
       F.VALORCONCEPTO,
       TP.DESTIPOPLANTACION
FROM o02agpe0.TB_SC_C_FACTORES F,
     o02agpe0.TB_SC_C_TIPO_PLANTACION TP
WHERE F.CODCONCEPTO = 173 AND
      F.VALORCONCEPTO = TP.CODTIPOPLANTACION
UNION
SELECT DISTINCT
       F.LINEASEGUROID,
       F.CODMODULO,
       F.CODCONCEPTO,
       F.VALORCONCEPTO,
       PC.DESPRACTICACULTURAL
FROM o02agpe0.TB_SC_C_FACTORES F,
     o02agpe0.TB_SC_C_PRACTICA_CULTURAL PC
WHERE F.CODCONCEPTO = 133 AND
      F.VALORCONCEPTO = PC.CODPRACTICACULTURAL
UNION
SELECT DISTINCT
       F.LINEASEGUROID,
       F.CODMODULO,
       F.CODCONCEPTO,
       F.VALORCONCEPTO,
       IGP.DESCRIPCION
FROM o02agpe0.TB_SC_C_FACTORES F,
     o02agpe0.TB_SC_C_IGP IGP
WHERE F.CODCONCEPTO = 765 AND
      F.VALORCONCEPTO = IGP.CODIGP
UNION
SELECT DISTINCT
       F.LINEASEGUROID,
       F.CODMODULO,
       F.CODCONCEPTO,
       F.VALORCONCEPTO,
       SPROT.DESSISTEMAPROTECCION
FROM o02agpe0.TB_SC_C_FACTORES F,
     o02agpe0.TB_SC_C_SISTEMA_PROTECCION SPROT
WHERE F.CODCONCEPTO = 621 AND
      F.VALORCONCEPTO = SPROT.CODSISTEMAPROTECCION
UNION
SELECT DISTINCT
       F.LINEASEGUROID,
       F.CODMODULO,
       F.CODCONCEPTO,
       F.VALORCONCEPTO,
       CC.DESCICLOCULTIVO
FROM o02agpe0.TB_SC_C_FACTORES F,
     o02agpe0.TB_SC_C_CICLO_CULTIVO CC
WHERE F.CODCONCEPTO = 618 AND
      F.VALORCONCEPTO = CC.CODCICLOCULTIVO
UNION
SELECT DISTINCT
       F.LINEASEGUROID,
       F.CODMODULO,
       F.CODCONCEPTO,
       F.VALORCONCEPTO,
       MC.DESCRIPCION
FROM o02agpe0.TB_SC_C_FACTORES F,
     o02agpe0.TB_SC_C_MATERIAL_CUBIERTA MC
WHERE F.CODCONCEPTO = 873 AND
      F.VALORCONCEPTO = MC.CODMATERIALCUBIERTA
UNION
SELECT DISTINCT
       F.LINEASEGUROID,
       F.CODMODULO,
       F.CODCONCEPTO,
       F.VALORCONCEPTO,
       TI.DESCRIPCION
FROM o02agpe0.TB_SC_C_FACTORES F,
     o02agpe0.TB_SC_C_TIPO_INSTALACION TI
WHERE F.CODCONCEPTO = 778 AND
      F.VALORCONCEPTO = TI.CODTIPOINSTALACION
UNION
SELECT DISTINCT
       F.LINEASEGUROID,
       F.CODMODULO,
       F.CODCONCEPTO,
       F.VALORCONCEPTO,
       SCULT.DESSISTEMACULTIVO
FROM o02agpe0.TB_SC_C_FACTORES F,
     o02agpe0.TB_SC_C_SISTEMA_CULTIVO SCULT
WHERE F.CODCONCEPTO = 123 AND
      F.VALORCONCEPTO = SCULT.CODSISTEMACULTIVO
UNION
SELECT DISTINCT
       F.LINEASEGUROID,
       F.CODMODULO,
       F.CODCONCEPTO,
       F.VALORCONCEPTO,
       CRD.DESREDUCRDTO
FROM o02agpe0.TB_SC_C_FACTORES F,
     o02agpe0.TB_SC_C_COD_REDUC_RDTO CRD
WHERE F.CODCONCEPTO = 620 AND
      F.VALORCONCEPTO = CRD.CODREDUCRDTO
UNION
SELECT DISTINCT
       F.LINEASEGUROID,
       F.CODMODULO,
       F.CODCONCEPTO,
       F.VALORCONCEPTO,
       ALT.DESALTERNATIVA
FROM o02agpe0.TB_SC_C_FACTORES F,
     o02agpe0.TB_SC_C_ALTERNATIVAS ALT
WHERE F.CODCONCEPTO = 437 AND
      F.VALORCONCEPTO = ALT.CODALTERNATIVA
UNION
SELECT DISTINCT
       F.LINEASEGUROID,
       F.CODMODULO,
       F.CODCONCEPTO,
       F.VALORCONCEPTO,
       CDO.DESDENOMORIGEN
FROM o02agpe0.TB_SC_C_FACTORES F,
     o02agpe0.TB_SC_C_COD_DENOM_ORIGEN CDO
WHERE F.CODCONCEPTO = 107 AND
      F.VALORCONCEPTO = CDO.CODDENOMORIGEN AND
      F.LINEASEGUROID = CDO.LINEASEGUROID
UNION
SELECT DISTINCT
       F.LINEASEGUROID,
       F.CODMODULO,
       F.CODCONCEPTO,
       F.VALORCONCEPTO,
       TT.DESTIPOTERRENO
FROM o02agpe0.TB_SC_C_FACTORES F,
     o02agpe0.TB_SC_C_TIPO_TERRENO TT
WHERE F.CODCONCEPTO = 752 AND
      F.VALORCONCEPTO = TT.CODTIPOTERRENO
UNION
SELECT DISTINCT
       F.LINEASEGUROID,
       F.CODMODULO,
       F.CODCONCEPTO,
       F.VALORCONCEPTO,
       TM.DESTIPOMASA
FROM o02agpe0.TB_SC_C_FACTORES F,
     o02agpe0.TB_SC_C_TIPO_MASA TM
WHERE F.CODCONCEPTO = 753 AND
      F.VALORCONCEPTO = TM.CODTIPOMASA
UNION
SELECT DISTINCT
       F.LINEASEGUROID,
       F.CODMODULO,
       F.CODCONCEPTO,
       F.VALORCONCEPTO,
       P.DESPENDIENTE
FROM o02agpe0.TB_SC_C_FACTORES F,
     o02agpe0.TB_SC_C_PENDIENTE P
WHERE F.CODCONCEPTO = 754 AND
      F.VALORCONCEPTO = P.CODPENDIENTE
UNION
SELECT DISTINCT
       F.LINEASEGUROID,
       F.CODMODULO,
       F.CODCONCEPTO,
       F.VALORCONCEPTO,
       D.DESNUMANIOSDESCORCHE
FROM o02agpe0.TB_SC_C_FACTORES F,
     o02agpe0.TB_SC_C_NUM_ANIOS_DESCORCHE D
WHERE F.CODCONCEPTO = 944 AND
      F.VALORCONCEPTO = D.CODNUMANIOSDESCORCHE
UNION
SELECT DISTINCT F.LINEASEGUROID, F.CODMODULO, F.CODCONCEPTO, F.VALORCONCEPTO, ff.despctfranquiciaeleg
FROM o02agpe0.TB_SC_C_FACTORES F,
     o02agpe0.tb_sc_c_pct_franquicia_eleg FF
WHERE F.CODCONCEPTO = 120 AND
      F.VALORCONCEPTO = ff.codpctfranquiciaeleg
UNION
SELECT DISTINCT F.LINEASEGUROID, F.CODMODULO, F.CODCONCEPTO, F.VALORCONCEPTO, M.DESMININDEM
FROM o02agpe0.TB_SC_C_FACTORES F,
     o02agpe0.Tb_Sc_c_Min_Indem_Eleg M
WHERE F.CODCONCEPTO = 121 AND
      F.VALORCONCEPTO = M.PCTMININDEM
-- Modificaciones para ganado
--    Alojamiento: 1053
UNION
SELECT DISTINCT F.LINEASEGUROID, F.CODMODULO, F.CODCONCEPTO, F.VALORCONCEPTO, TC.DESTIPOCAPITAL
FROM o02agpe0.TB_SC_C_FACTORES F,
     o02agpe0.TB_SC_C_TIPO_CAPITAL TC
WHERE F.CODCONCEPTO = 126 AND
      F.VALORCONCEPTO = TC.CODTIPOCAPITAL
UNION
select DISTINCT f.lineaseguroid, f.codmodulo, dbg.codcpto, dbg.valor_cpto, dbg.descripcion
from o02agpe0.TB_SC_C_FACTORES F,o02agpe0.tb_sc_c_datos_buzon_general dbg
where F.CODCONCEPTO=1053 and f.codconcepto=dbg.codcpto and f.valorconcepto = dbg.valor_cpto
--    Autorizaci??n especial: 1064
UNION
select DISTINCT f.lineaseguroid, f.codmodulo, dbg.codcpto, dbg.valor_cpto, dbg.descripcion
from o02agpe0.TB_SC_C_FACTORES F,o02agpe0.tb_sc_c_datos_buzon_general dbg
where F.CODCONCEPTO=1064 and f.codconcepto=dbg.codcpto and f.valorconcepto = dbg.valor_cpto
--    Calidad producci??n: 1047
UNION
select DISTINCT f.lineaseguroid, f.codmodulo, dbg.codcpto, dbg.valor_cpto, dbg.descripcion
from o02agpe0.TB_SC_C_FACTORES F,o02agpe0.tb_sc_c_datos_buzon_general dbg
where F.CODCONCEPTO=1047 and f.codconcepto=dbg.codcpto and f.valorconcepto = dbg.valor_cpto
--    Calificaci??n de saneamiento: 1061
UNION
select DISTINCT f.lineaseguroid, f.codmodulo, dbg.codcpto, dbg.valor_cpto, dbg.descripcion
from o02agpe0.TB_SC_C_FACTORES F,o02agpe0.tb_sc_c_datos_buzon_general dbg
where F.CODCONCEPTO=1061 and f.codconcepto=dbg.codcpto and f.valorconcepto = dbg.valor_cpto
--    Calificaci??n sanitaria: 1062
UNION
select DISTINCT f.lineaseguroid, f.codmodulo, dbg.codcpto, dbg.valor_cpto, dbg.descripcion
from o02agpe0.TB_SC_C_FACTORES F,o02agpe0.tb_sc_c_datos_buzon_general dbg
where F.CODCONCEPTO=1062 and f.codconcepto=dbg.codcpto and f.valorconcepto = dbg.valor_cpto
--    Condiciones particulares: 1050
UNION
select DISTINCT f.lineaseguroid, f.codmodulo, dbg.codcpto, dbg.valor_cpto, dbg.descripcion
from o02agpe0.TB_SC_C_FACTORES F,o02agpe0.tb_sc_c_datos_buzon_general dbg
where F.CODCONCEPTO=1050 and f.codconcepto=dbg.codcpto and f.valorconcepto = dbg.valor_cpto
--    Control oficial lechero: 1045
UNION
select DISTINCT f.lineaseguroid, f.codmodulo, dbg.codcpto, dbg.valor_cpto, dbg.descripcion
from o02agpe0.TB_SC_C_FACTORES F,o02agpe0.tb_sc_c_datos_buzon_general dbg
where F.CODCONCEPTO=1045 and f.codconcepto=dbg.codcpto and f.valorconcepto = dbg.valor_cpto
--    Cuenca hidrogr?!fica: 1073
UNION
select DISTINCT f.lineaseguroid, f.codmodulo, dbg.codcpto, dbg.valor_cpto, dbg.descripcion
from o02agpe0.TB_SC_C_FACTORES F,o02agpe0.tb_sc_c_datos_buzon_general dbg
where F.CODCONCEPTO=1073 and f.codconcepto=dbg.codcpto and f.valorconcepto = dbg.valor_cpto
--    Duraci??n periodo productivo: 1066
UNION
select DISTINCT f.lineaseguroid, f.codmodulo, dbg.codcpto, dbg.valor_cpto, dbg.descripcion
from o02agpe0.TB_SC_C_FACTORES F,o02agpe0.tb_sc_c_datos_buzon_general dbg
where F.CODCONCEPTO=1066 and f.codconcepto=dbg.codcpto and f.valorconcepto = dbg.valor_cpto
--    Excepci??n contrataci??n a?? Explotaci??n: 1063
UNION
select DISTINCT f.lineaseguroid, f.codmodulo, dbg.codcpto, dbg.valor_cpto, dbg.descripcion
from o02agpe0.TB_SC_C_FACTORES F,o02agpe0.tb_sc_c_datos_buzon_general dbg
where F.CODCONCEPTO=1063 and f.codconcepto=dbg.codcpto and f.valorconcepto = dbg.valor_cpto
--    Excepci??n contrataci??n a?? P??liza: 1111
UNION
select DISTINCT f.lineaseguroid, f.codmodulo, dbg.codcpto, dbg.valor_cpto, dbg.descripcion
from o02agpe0.TB_SC_C_FACTORES F,o02agpe0.tb_sc_c_datos_buzon_general dbg
where F.CODCONCEPTO=1111 and f.codconcepto=dbg.codcpto and f.valorconcepto = dbg.valor_cpto
--    Empresa gestora: 1049
UNION
select DISTINCT f.lineaseguroid, f.codmodulo, dbg.codcpto, dbg.valor_cpto, dbg.descripcion
from o02agpe0.TB_SC_C_FACTORES F,o02agpe0.tb_sc_c_datos_buzon_general dbg
where F.CODCONCEPTO=1049 and f.codconcepto=dbg.codcpto and f.valorconcepto = dbg.valor_cpto
--    IGP/DO Ganado: 1051
UNION
select DISTINCT f.lineaseguroid, f.codmodulo, dbg.codcpto, dbg.valor_cpto, dbg.descripcion
from o02agpe0.TB_SC_C_FACTORES F,o02agpe0.tb_sc_c_datos_buzon_general dbg
where F.CODCONCEPTO=1051 and f.codconcepto=dbg.codcpto and f.valorconcepto = dbg.valor_cpto
--    Pureza: 1046
UNION
select DISTINCT f.lineaseguroid, f.codmodulo, dbg.codcpto, dbg.valor_cpto, dbg.descripcion
from o02agpe0.TB_SC_C_FACTORES F,o02agpe0.tb_sc_c_datos_buzon_general dbg
where F.CODCONCEPTO=1046 and f.codconcepto=dbg.codcpto and f.valorconcepto = dbg.valor_cpto
--    Sistema de almacenamiento: 1048
UNION
select DISTINCT f.lineaseguroid, f.codmodulo, dbg.codcpto, dbg.valor_cpto, dbg.descripcion
from o02agpe0.TB_SC_C_FACTORES F,o02agpe0.tb_sc_c_datos_buzon_general dbg
where F.CODCONCEPTO=1048 and f.codconcepto=dbg.codcpto and f.valorconcepto = dbg.valor_cpto
--    Tipo de ganaderia: 1052
UNION
select DISTINCT f.lineaseguroid, f.codmodulo, dbg.codcpto, dbg.valor_cpto, dbg.descripcion
from o02agpe0.TB_SC_C_FACTORES F,o02agpe0.tb_sc_c_datos_buzon_general dbg
where F.CODCONCEPTO=1052 and f.codconcepto=dbg.codcpto and f.valorconcepto = dbg.valor_cpto
--    Sistema de producci??n de ganado: 616
UNION
select DISTINCT f.lineaseguroid, f.codmodulo, dbg.codcpto, dbg.valor_cpto, dbg.descripcion
from o02agpe0.TB_SC_C_FACTORES F,o02agpe0.tb_sc_c_datos_buzon_general dbg
where F.CODCONCEPTO=616 and f.codconcepto=dbg.codcpto and f.valorconcepto = dbg.valor_cpto
--    Destino de ganado: 110
UNION
select DISTINCT f.lineaseguroid, f.codmodulo, dbg.codcpto, dbg.valor_cpto, dbg.descripcion
from o02agpe0.TB_SC_C_FACTORES F,o02agpe0.tb_sc_c_datos_buzon_general dbg
where F.CODCONCEPTO=110 and f.codconcepto=dbg.codcpto and f.valorconcepto = dbg.valor_cpto;
