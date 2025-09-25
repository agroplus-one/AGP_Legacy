SET DEFINE OFF;
SET SERVEROUTPUT ON;
create or replace package o02agpe0.PQ_INFORMES_COMISIONES is

  -- Author  :T-Systems
  -- Created : 19/08/2014
  -- Purpose :

  PROCEDURE UPDATE_INF_FICHERO_COMISIONES (IDCIERRE IN NUMBER);

end PQ_INFORMES_COMISIONES;
/
create or replace package body o02agpe0.PQ_INFORMES_COMISIONES is

  -- Cada vez que se importe un fichero de comisiones, guarda los datos en la tabla TB_COMS_INFORMES_COMISIONES.
-------------------------------------------------------------------------
  PROCEDURE UPDATE_INF_FICHERO_COMISIONES (IDCIERRE IN NUMBER) IS

    -- lc                       VARCHAR2(100) := 'PQ_INFORMES_COMISIONES.UPDATE_INF_FICHERO_COMISIONES';

      VFECHAEMISION            o02agpe0.tb_coms_fase.fechaemision%TYPE;

      VNUMFAS                  o02agpe0.TB_COMS_INFORMES_COMISIONES.NUMFAS%TYPE;
      VCODENTMED               o02agpe0.TB_COMS_INFORMES_COMISIONES.CODENTMED%TYPE;
      VCODSUBMED               o02agpe0.TB_COMS_INFORMES_COMISIONES.CODSUBMED%TYPE;
      VCODOFI                  o02agpe0.TB_COMS_INFORMES_COMISIONES.CODOFI%TYPE;
      VCODPLN                  o02agpe0.TB_COMS_INFORMES_COMISIONES.CODPLN%TYPE;
      VCODLIN                  o02agpe0.TB_COMS_INFORMES_COMISIONES.CODLIN%TYPE;
      VCODCOL                  o02agpe0.TB_COMS_INFORMES_COMISIONES.CODCOL%TYPE;
      VFECACEP                 o02agpe0.TB_COMS_INFORMES_COMISIONES.FECACEP%TYPE;
      VNOMTOM                  o02agpe0.TB_COMS_INFORMES_COMISIONES.NOMTOM%TYPE;
      VREFPLZ                  o02agpe0.TB_COMS_INFORMES_COMISIONES.Refplz%TYPE;
      VNOMASG                  o02agpe0.Tb_Asegurados.nombre%TYPE;
      VNIFASG                  o02agpe0.TB_COMS_INFORMES_COMISIONES.Nifasg%TYPE;
      VAPE1ASG                 o02agpe0.Tb_Asegurados.apellido1%TYPE;
      VAPE2ASG                 o02agpe0.Tb_Asegurados.apellido2%TYPE;
      VRAZONSOCIAL             o02agpe0.Tb_Asegurados.razonsocial%TYPE;
      VPRINUE                  o02agpe0.TB_COMS_INFORMES_COMISIONES.PRINUE%TYPE;
      VGASENTNUE               o02agpe0.TB_COMS_INFORMES_COMISIONES.GASENTNUE%TYPE;
      VGASSUBNUE               o02agpe0.TB_COMS_INFORMES_COMISIONES.GASSUBNUE%TYPE;
      VCOMMENTNUE              o02agpe0.TB_COMS_INFORMES_COMISIONES.COMMENTNUE%TYPE;
      VCOMSUBNUE               o02agpe0.TB_COMS_INFORMES_COMISIONES.COMSUBNUE%TYPE;
      VGASNUE                  o02agpe0.TB_COMS_INFORMES_COMISIONES.GASNUE%TYPE;
      VGASRGANUE               o02agpe0.TB_COMS_INFORMES_COMISIONES.GASRGANUE%TYPE;
      VTOTNUE                  o02agpe0.TB_COMS_INFORMES_COMISIONES.TOTNUE%TYPE;
      VPRIREG                  o02agpe0.TB_COMS_INFORMES_COMISIONES.PRIREG%TYPE;
      VGASENTREG               o02agpe0.TB_COMS_INFORMES_COMISIONES.GASENTREG%TYPE;
      VGASSUBREG               o02agpe0.TB_COMS_INFORMES_COMISIONES.GASSUBREG%TYPE;
      VCOMENTREG               o02agpe0.TB_COMS_INFORMES_COMISIONES.COMENTREG%TYPE;
      VCOMSUBREG               o02agpe0.TB_COMS_INFORMES_COMISIONES.COMSUBREG%TYPE;
      VGASREG                  o02agpe0.TB_COMS_INFORMES_COMISIONES.GASREG%TYPE;
      VGASRGAREG               o02agpe0.TB_COMS_INFORMES_COMISIONES.GASRGAREG%TYPE;
      VTOTREG                  o02agpe0.TB_COMS_INFORMES_COMISIONES.TOTREG%TYPE;
      VPRISUM                  o02agpe0.TB_COMS_INFORMES_COMISIONES.PRISUM%TYPE;
      VGASENTSUM               o02agpe0.TB_COMS_INFORMES_COMISIONES.GASENTSUM%TYPE;
      VGASSUBSUM               o02agpe0.TB_COMS_INFORMES_COMISIONES.GASSUBSUM%TYPE;
      VCOMENTSUM               o02agpe0.TB_COMS_INFORMES_COMISIONES.COMENTSUM%TYPE;
      VCOMSUBSUM               o02agpe0.TB_COMS_INFORMES_COMISIONES.COMSUBSUM%TYPE;
      VGASSUM                  o02agpe0.TB_COMS_INFORMES_COMISIONES.GASSUM%TYPE;
      VGASRGASUM               o02agpe0.TB_COMS_INFORMES_COMISIONES.GASRGASUM%TYPE;
      VTOTSUM                  o02agpe0.TB_COMS_INFORMES_COMISIONES.TOTSUM%TYPE;
      VGASPEN                  o02agpe0.TB_COMS_INFORMES_COMISIONES.GASPEN%TYPE;
      VGASPAG                  o02agpe0.TB_COMS_INFORMES_COMISIONES.GASPAG%TYPE;
      VTOTLIQ                  o02agpe0.TB_COMS_INFORMES_COMISIONES.TOTLIQ%TYPE;
      VTOTENT                  o02agpe0.TB_COMS_INFORMES_COMISIONES.TOTENT%TYPE;
      VTOTSUB                  o02agpe0.TB_COMS_INFORMES_COMISIONES.TOTSUB%TYPE;
      VCOSTOT                  o02agpe0.TB_COMS_INFORMES_COMISIONES.COSTOT%TYPE;
      VCODMETRA                o02agpe0.TB_COMS_INFORMES_COMISIONES.CODMETRA%TYPE;
      VTIPMDTRA                o02agpe0.TB_COMS_INFORMES_COMISIONES.TIPMDTRA%TYPE;
      VIMPTRA                  o02agpe0.TB_COMS_INFORMES_COMISIONES.IMPTRA%TYPE;
      VIMPSINREDTRA            o02agpe0.TB_COMS_INFORMES_COMISIONES.IMPSINREDTRA%TYPE;
      VCODMEDCAL               o02agpe0.TB_COMS_INFORMES_COMISIONES.CODMEDCAL%TYPE;
      VTIPMEDCAL               o02agpe0.TB_COMS_INFORMES_COMISIONES.TIPMEDCAL%TYPE;
      VIMPCAL                  o02agpe0.TB_COMS_INFORMES_COMISIONES.IMPCAL%TYPE;
      VIMPSINREDCAL            o02agpe0.TB_COMS_INFORMES_COMISIONES.IMPSINREDCAL%TYPE;
      VTOTTRAMCAL              o02agpe0.TB_COMS_INFORMES_COMISIONES.TOTTRAMCAL%TYPE;
      VIDCIERRE                o02agpe0.TB_COMS_INFORMES_COMISIONES.IDCIERRE%TYPE;
      auxCodEnt                o02agpe0.TB_COMS_INFORMES_COMISIONES.CODENTMED%TYPE;
      v_codEntidad             o02agpe0.TB_COMS_INFORMES_COMISIONES.CODENTIDAD%TYPE;

      --Necesarios para planes 2015+
v_2015_dn1PrimaComNeta					TB_COMS_INFORMES_COMS_2015.PRIMA_COMERCIAL_NETA_DN1%TYPE;
v_2015_dn1GastosAdm							TB_COMS_INFORMES_COMS_2015.GASTOS_ADM_DN1%TYPE;
v_2015_dn1GastosAdq							TB_COMS_INFORMES_COMS_2015.GASTOS_ADQ_DN1%TYPE;
v_2015_dn1ComiEntidad						TB_COMS_INFORMES_COMS_2015.COMISION_ENTIDAD_DN1%TYPE;
v_2015_dn1ComiEsMed							TB_COMS_INFORMES_COMS_2015.COMISION_ES_MEDIADORA_DN1%TYPE;
v_2015_dn1TotalGastos						TB_COMS_INFORMES_COMS_2015.TOTAL_GASTOS_DN1%TYPE;
v_2015_dr1PrimaComNeta					TB_COMS_INFORMES_COMS_2015.PRIMA_COMERCIAL_NETA_DR1%TYPE;
v_2015_dr1GastosAdm							TB_COMS_INFORMES_COMS_2015.GASTOS_ADM_DR1%TYPE;
v_2015_dr1GastosAdq							TB_COMS_INFORMES_COMS_2015.GASTOS_ADQ_DR1%TYPE;
v_2015_dr1ComiEnt							  TB_COMS_INFORMES_COMS_2015.COMISION_ENTIDAD_DR1%TYPE;
v_2015_dr1ComiEsMed							TB_COMS_INFORMES_COMS_2015.COMISION_ES_MEDIADORA_DR1%TYPE;
v_2015_dr1TotalGastos						TB_COMS_INFORMES_COMS_2015.TOTAL_GASTOS_DR1%TYPE;
v_2015_dt1PrimaComNeta					TB_COMS_INFORMES_COMS_2015.PRIMA_COMERCIAL_NETA_DT1%TYPE;
v_2015_dt1GastosAdm							TB_COMS_INFORMES_COMS_2015.GASTOS_ADM_DT1%TYPE;
v_2015_dt1GastosAdq							TB_COMS_INFORMES_COMS_2015.GASTOS_ADQ_DT1%TYPE;
v_2015_dt1ComiEntidad						TB_COMS_INFORMES_COMS_2015.COMISION_ENTIDAD_DT1%TYPE;
v_2015_dt1ComiEsMed							TB_COMS_INFORMES_COMS_2015.COMISION_ES_MEDIADORA_DT1%TYPE;
v_2015_dt1TotalGastos						TB_COMS_INFORMES_COMS_2015.TOTAL_GASTOS_DT1%TYPE;--Calculado
v_2015_dt1GastosAdmAbon					TB_COMS_INFORMES_COMS_2015.GASTOS_ADM_ABON_DT1%TYPE;
v_2015_dt1GastosAdqAbon					TB_COMS_INFORMES_COMS_2015.GASTOS_ADQ_ABON_DT1%TYPE;
v_2015_dt1ComiEntidadAbon				TB_COMS_INFORMES_COMS_2015.COMISION_ENT_ABON_DT1%TYPE;
v_2015_dt1ComiEsMedAbon					TB_COMS_INFORMES_COMS_2015.COMISION_ESMED_ABON_DT1%TYPE;
v_2015_dt1GastosAdmPdte					TB_COMS_INFORMES_COMS_2015.GASTOS_ADM_PDTE_DT1%TYPE;
v_2015_dt1GastosAdqPdte					TB_COMS_INFORMES_COMS_2015.GASTOS_ADQ_PDTE_DT1%TYPE;
v_2015_dt1ComiEntidadPdte				TB_COMS_INFORMES_COMS_2015.COMISION_ENT_PDTE_DT1%TYPE;
v_2015_dt1ComiEsMedPdte					TB_COMS_INFORMES_COMS_2015.COMISION_ESMED_PDTE_DT1%TYPE;

v_2015_totalLiq 							  TB_COMS_INFORMES_COMS_2015.TOTAL_LIQUIDACION%TYPE;--Calculado
v_2015_totalLiqRga							TB_COMS_INFORMES_COMS_2015.TOTAL_LIQUIDACION_RGA%TYPE;--Calculado
v_2015_totalTramCal             TB_COMS_INFORMES_COMS_2015.Total_Tram_Calidad%TYPE;--Calculado

      contador                 number default 0;
      TYPE TpCursor            IS REF CURSOR;
      cur_consulta             TpCursor;
      consulta                 varchar2(32000):=
      'select fa.FECHAEMISION,
       fa.fase,
       com.codentmed,
       com.codsubmed,
       com.codofi,
       com.codpln,
       com.codlin,
       com.codcol,
       f.fechaaceptacion,
       NVL(ccom.razonsocial, ccom.nombre ||''||ccom.apellido1 ||''||ccom.apellido2) ,
       com.refplz,
       com.nifasg,
       com.prinue,
       com.gasentnue,
       com.gassubnue,
       com.COMMENTNUE,
       com.commendnue,
       com.GASNUE,
       com.GASRGANUE,
       com.TOTNUE,
       com.PRIREG,
       com.GASENTREG,
       com.gassubreg,
       com.comentreg,
       com.commendreg,
       com.GASREG,
       com.GASRGAREG,
       com.TOTREG,
       com.PRISUM,
       com.Gasentsum,
       com.gassubsum,
       com.comentsum,
       com.comsubsum,
       com.gassum,
       com.gasrgasum,
       ccom.dt_TOTAL,
       ccom.dt_gastospendientes,
       ccom.dt_gastospagados,
       ccom.DT_COMISIONES,
       (ccom.DT_COMISIONES * nvl(sub.pctentidad,100))/100 TOTAL_ENTIDAD,
       (ccom.DT_COMISIONES * nvl(sub.pctmediador,0))/100 TOTAL_SUBENTIDAD,
       com.costot,
       com.codmetra1,
       com.tipmdtra,
       nvl(com.imptra,0),
       com.CODMEDCAL,
       com.TIPMEDCAL,
       nvl(com.IMPCAL,0),
       (nvl(com.imptra,0)+nvl(com.impcal,0)) Tottracal,
       fa.idcierre,
       com.codent,
        apli.dn1_prima_comercial_neta,
        apli.dn1_gastos_admin_entidad,
        apli.dn1_gastos_adq_entidad,
        apli.dn1_comisiones_mediador_ent,
        apli.dn1_comisiones_mediador_esmed,
        nvl(apli.dn1_total,0),

        apli.dr1_prima_comercial_neta,
        apli.dr1_gastos_admin_entidad,
        apli.dr1_gastos_adq_entidad,
        apli.dr1_comisiones_mediador_ent,
        apli.dr1_comisiones_mediador_esmed,
        nvl(apli.dr1_total,0),

        apli.dt1_prima_comercial_neta,
        apli.dt1_gastos_admin_entidad,
        apli.dt1_gastos_adq_entidad,
        apli.dt1_comisiones_mediador_ent,
        apli.dt1_comisiones_mediador_esmed,
        apli.dt1_gastos_admin_ent_abon,
        apli.dt1_gastos_adq_ent_abon,
        nvl(apli.dt1_comis_mediador_abon_ent,0),
        nvl(apli.dt1_comis_mediador_abon_esmed,0),
        apli.dt1_gastos_admin_ent_pdte,
        apli.dt1_gastos_adq_ent_pdte,
        nvl(apli.dt1_comis_mediador_pdte_ent,0),
        nvl(apli.dt1_comis_mediador_pdte_esmed,0)

  from o02agpe0.tb_rga_comisiones  com,
       o02agpe0.tb_coms_comisiones ccom,
       o02agpe0.tb_coms_fase       fa,
       o02agpe0.tb_coms_cierre     cr,
       o02agpe0.tb_coms_ficheros   f,
       o02agpe0.tb_coms_gge_subentidades sub,
       o02agpe0.tb_coms_comis_aplicaciones apli

 where com.idcierre = fa.idcierre
   and com.codcol = ccom.colectivoreferencia
   and fa.idcierre = cr.id
   and ccom.idfichero = f.id
   and f.idfase = fa.id
   and com.idcierre = cr.id
   and com.numfas = fa.fase
   and com.codpln = fa.plan
   and sub.codentidad(+) = com.codent
   and sub.codsubentidad(+) = com.codsubmed
   and sub.plan(+) = com.codpln
   and ccom.id = apli.idcomisiones
   and com.refplz = apli.referencia(+)
   and fa.idcierre ='|| IDCIERRE;

  BEGIN
         OPEN cur_consulta FOR consulta;

             LOOP

                 FETCH cur_consulta INTO VFECHAEMISION, VNUMFAS, VCODENTMED, VCODSUBMED, VCODOFI, VCODPLN, VCODLIN, VCODCOL,
                       VFECACEP, VNOMTOM, VREFPLZ,  VNIFASG,
                       VPRINUE, VGASENTNUE, VGASSUBNUE, VCOMMENTNUE, VCOMSUBNUE, VGASNUE, VGASRGANUE, VTOTNUE,
                       VPRIREG, VGASENTREG, VGASSUBREG, VCOMENTREG, VCOMSUBREG, VGASREG, VGASRGAREG, VTOTREG,
                       VPRISUM, VGASENTSUM, VGASSUBSUM, VCOMENTSUM, VCOMSUBSUM, VGASSUM, VGASRGASUM, VTOTSUM,
                       VGASPEN, VGASPAG, VTOTLIQ, VTOTENT, VTOTSUB, VCOSTOT, VCODMETRA, VTIPMDTRA, VIMPTRA,
                       VCODMEDCAL, VTIPMEDCAL, VIMPCAL, VTOTTRAMCAL, VIDCIERRE, v_codEntidad,

                       v_2015_dn1PrimaComNeta, v_2015_dn1GastosAdm, v_2015_dn1GastosAdq, v_2015_dn1ComiEntidad,
                       v_2015_dn1ComiEsMed, v_2015_dn1TotalGastos,

                       v_2015_dr1PrimaComNeta, v_2015_dr1GastosAdm, v_2015_dr1GastosAdq,
                       v_2015_dr1ComiEnt, v_2015_dr1ComiEsMed, v_2015_dr1TotalGastos,

                       v_2015_dt1PrimaComNeta, v_2015_dt1GastosAdm, v_2015_dt1GastosAdq, v_2015_dt1ComiEntidad,
                       v_2015_dt1ComiEsMed, v_2015_dt1GastosAdmAbon, v_2015_dt1GastosAdqAbon, v_2015_dt1ComiEntidadAbon,
                       v_2015_dt1ComiEsMedAbon, v_2015_dt1GastosAdmPdte, v_2015_dt1GastosAdqPdte,
                       v_2015_dt1ComiEntidadPdte, v_2015_dt1ComiEsMedPdte;


                 EXIT WHEN cur_consulta%NOTFOUND;
                 contador := contador +1;
                 ------------------ VCODENTIDAD_MED, VCODSUBENT_MED ------------------------------------
                -- o02agpe0.PQ_INFORMES_RECIBOS.GET_ENTMEDIADORA (VREFPLZ, VFECHAEMISION, VCODENTMED, VCODSUBMED);
                 BEGIN
                   SELECT codentidad, entmediadora, subentmediadora into v_codEntidad, VCODENTMED, VCODSUBMED
                      FROM (
                         select codentidad, entmediadora, subentmediadora, FECHACAMBIO
                         from (
                             select c.*
                             from o02agpe0.tb_historico_colectivos c
                             where c.idcolectivo in (select p.idcolectivo from o02agpe0.tb_polizas p where p.referencia = VREFPLZ )
                             and c.fechaefecto <= VFECHAEMISION
                         )
                       ORDER BY FECHACAMBIO DESC
                   )
                   WHERE ROWNUM = 1;

                 EXCEPTION
                     WHEN NO_DATA_FOUND THEN
                        v_codEntidad := 0;
                        VCODENTMED := 0;
                        VCODSUBMED := 0;
                     WHEN OTHERS THEN
                        dbms_output.put_line('Error al recuperar los datos de la entidad mediadora. Mensaje: '||SQLERRM||' codigo: '|| SQLCODE);

                 END;

                 -- Hacemos el insert en TB_COMS_INFORMES_COMISIONES
                 BEGIN
                 IF VCODPLN < 2015 THEN

                 insert into o02agpe0.TB_COMS_INFORMES_COMISIONES values (o02agpe0.sq_coms_informes_comisiones.nextval,
                       VNUMFAS, VCODENTMED, VCODSUBMED, VCODOFI, VCODPLN, VCODLIN, VCODCOL,
                       VFECACEP, VNOMTOM, VREFPLZ, VNIFASG, null, null, null, null,
                       VPRINUE, VGASENTNUE, VGASSUBNUE, VCOMMENTNUE, VCOMSUBNUE, VGASNUE, VGASRGANUE, VTOTNUE,
                       VPRIREG, VGASENTREG, VGASSUBREG, VCOMENTREG, VCOMSUBREG, VGASREG, VGASRGAREG, VTOTREG,
                       VPRISUM, VGASENTSUM, VGASSUBSUM, VCOMENTSUM, VCOMSUBSUM, VGASSUM, VGASRGASUM, VTOTSUM,
                       VGASPEN, VGASPAG, VTOTLIQ, VTOTENT, VTOTSUB, VCOSTOT, VCODMETRA, VTIPMDTRA, VIMPTRA,
                       null, VCODMEDCAL, VTIPMEDCAL, VIMPCAL, null, VTOTTRAMCAL, VIDCIERRE, v_codEntidad);

                 ELSE
                 --Para planes 2015+

                 v_2015_totalTramCal:= VIMPTRA + VIMPCAL;
                 v_2015_dt1TotalGastos:=v_2015_dn1TotalGastos + v_2015_dr1TotalGastos;
                 v_2015_totalLiq:=v_2015_dt1ComiEntidadAbon + v_2015_dt1ComiEsMedAbon - v_2015_dt1ComiEntidadPdte - v_2015_dt1ComiEsMedPdte;
                 v_2015_totalLiqRga:=v_2015_dt1GastosAdmAbon + v_2015_dt1GastosAdqAbon - v_2015_dt1GastosAdmPdte - v_2015_dt1GastosAdqPdte;

insert into TB_COMS_INFORMES_COMS_2015
values(SQ_COMS_INFORMES_COMISION_2015.nextval,
VNUMFAS,					--FASE	VARCHAR2(4)	Y
VCODENTMED,					--CODENTMED	NUMBER(4)	Y
VCODSUBMED,					--CODSUBENTMED	NUMBER(4)	Y
v_codEntidad,				--CODENTIDAD	NUMBER(4)	Y
VCODOFI,					--CODOFICINA	NUMBER(4)	Y
VCODPLN,					--CODPLAN	NUMBER(4)	Y
VCODLIN,					--CODLINEA	NUMBER(3)	Y
VCODCOL,					--COLECTIVO	VARCHAR2(7)	Y
VFECACEP,					--FECHA_ACEPTACION	DATE	Y
VNOMTOM,					--TOMADOR	VARCHAR2(100)	Y
VREFPLZ,					--REF_POLIZA	VARCHAR2(7)	Y
VNIFASG,					--NIF_ASEGURADO	VARCHAR2(9)	Y

--Se fijan más tarde
null,						--NOM_ASEG	VARCHAR2(30)	Y
null,						--AP1_ASEG	VARCHAR2(50)	Y
null,						--AP2_ASEG	VARCHAR2(50)	Y
null,						--RAZONSOCIAL_ASEG	VARCHAR2(150)	Y

v_2015_dn1PrimaComNeta,		--PRIMA_COMERCIAL_NETA_DN1	NUMBER(15,2)	Y
v_2015_dn1GastosAdm,		--GASTOS_ADM_DN1	NUMBER(15,2)	Y
v_2015_dn1GastosAdq,		--GASTOS_ADQ_DN1	NUMBER(15,2)	Y
v_2015_dn1ComiEntidad,		--COMISION_ENTIDAD_DN1	NUMBER(15,2)	Y
v_2015_dn1ComiEsMed,		--COMISION_ES_MEDIADORA_DN1	NUMBER(15,2)	Y
v_2015_dn1TotalGastos,		--TOTAL_GASTOS_DN1	NUMBER(15,2)	Y

v_2015_dr1PrimaComNeta,		--PRIMA_COMERCIAL_NETA_DR1	NUMBER(15,2)	Y
v_2015_dr1GastosAdm,		--GASTOS_ADM_DR1	NUMBER(15,2)	Y
v_2015_dr1GastosAdq,		--GASTOS_ADQ_DR1	NUMBER(15,2)	Y
v_2015_dr1ComiEnt,			--COMISION_ENTIDAD_DR1	NUMBER(15,2)	Y
v_2015_dr1ComiEsMed,		--COMISION_ES_MEDIADORA_DR1	NUMBER(15,2)	Y
v_2015_dr1TotalGastos,		--TOTAL_GASTOS_DR1	NUMBER(15,2)	Y

v_2015_dt1PrimaComNeta,		--PRIMA_COMERCIAL_NETA_DT1	NUMBER(15,2)	Y
v_2015_dt1GastosAdm,		--GASTOS_ADM_DT1	NUMBER(15,2)	Y
v_2015_dt1GastosAdq,		--GASTOS_ADQ_DT1	NUMBER(15,2)	Y
v_2015_dt1ComiEntidad,		--COMISION_ENTIDAD_DT1	NUMBER(15,2)	Y
v_2015_dt1ComiEsMed,		--COMISION_ES_MEDIADORA_DT1	NUMBER(15,2)	Y
v_2015_dt1TotalGastos,		--TOTAL_GASTOS_DT1	NUMBER(14,2)	Y
v_2015_dt1GastosAdmAbon,	--GASTOS_ADM_ABON_DT1	NUMBER(15,2)	Y
v_2015_dt1GastosAdqAbon,	--GASTOS_ADQ_ABON_DT1	NUMBER(15,2)	Y
v_2015_dt1ComiEntidadAbon,	--COMISION_ENT_ABON_DT1	NUMBER(15,2)	Y
v_2015_dt1ComiEsMedAbon,	--COMISION_ESMED_ABON_DT1	NUMBER(15,2)	Y
v_2015_dt1GastosAdmPdte,	--GASTOS_ADM_PDTE_DT1	NUMBER(15,2)	Y
v_2015_dt1GastosAdqPdte,	--GASTOS_ADQ_PDTE_DT1	NUMBER(15,2)	Y
v_2015_dt1ComiEntidadPdte,	--COMISION_ENT_PDTE_DT1	NUMBER(15,2)	Y
v_2015_dt1ComiEsMedPdte,	--COMISION_ESMED_PDTE_DT1	NUMBER(15,2)	Y
v_2015_totalLiq,			--TOTAL_LIQUIDACION	NUMBER(14,2)	Y
v_2015_totalLiqRga,			--TOTAL_LIQUIDACION_RGA	NUMBER(14,2)	Y

VCODMETRA,					--COD_TRAM	VARCHAR2(2)	Y
VTIPMDTRA,					--TIPO_TRAM	VARCHAR2(1)	Y
VIMPTRA,					--IMPORTE_APLICADO_TRAM	NUMBER(15,2)	Y

--Se fija más tarde
null,						--IMPORTE_SIN_REDUCC_TRAM	NUMBER(15,2)	Y

VCODMEDCAL,					--COD_CALIDAD	VARCHAR2(2)	Y
VTIPMEDCAL,					--TIPO_CALIDAD	VARCHAR2(1)	Y
VIMPCAL,						--IMPORTE_APLICADO_CALIDAD	NUMBER(15,2)	Y

--Se fija más tarde
null,						      --IMPORTE_SIN_REDUCC_CALIDAD	NUMBER(15,2)	Y

v_2015_totalTramCal,		--TOTAL_TRAM_CALIDAD	NUMBER(14,2)	Y	(VIMPTRA + VIMPCAL)
IDCIERRE
);

                 END IF;
                 END;
                 --  actualizamos los campos   VIMPSINREDTRA y VIMPSINREDCAL que se cogen de reglamento.
                 BEGIN
                   select resi.dt_importe_s_red, -- VIMPSINREDTRA
                        resi.dc_importe_s_red -- VIMPSINREDCAL
                   into VIMPSINREDTRA, VIMPSINREDCAL
                   from o02agpe0.tb_coms_reglamento_prod_emit e,
                         o02agpe0.tb_coms_regl_prod_emit_situac resi,
                         o02agpe0.tb_coms_ficheros f,
                          o02agpe0.tb_coms_fase fa
                    where
                          e.referencia = VREFPLZ
                      and e.linea = VCODLIN
                      and e.id = resi.idreglamento
                      and fa.id= f.idfase
                      and f.id = e.idfichero
                      and fa.fechaemision = (select max (fa.fechaemision) from o02agpe0.tb_coms_reglamento_prod_emit e,
                                                                               o02agpe0.tb_coms_regl_prod_emit_situac resi,
                                                                               o02agpe0.tb_coms_ficheros f,
                                                                               o02agpe0.tb_coms_fase fa
                                                                      where
                                                                       e.referencia = VREFPLZ
                                                                        and e.linea = VCODLIN
                                                                        and e.id = resi.idreglamento
                                                                        and fa.id= f.idfase
                                                                        and f.id = e.idfichero );
                   EXCEPTION
                            when others then
                             VIMPSINREDTRA := null;
                             VIMPSINREDCAL := null;
                   END;

                 IF (VIMPSINREDTRA is not null AND VIMPSINREDCAL is not null) then

                   IF VCODPLN < 2015 THEN

                     update o02agpe0.TB_COMS_INFORMES_COMISIONES c
                            set c.impsinredtra = VIMPSINREDTRA,
                                c.impsinredcal = VIMPSINREDCAL
                            where c.refplz = VREFPLZ
                              and c.codlin = VCODLIN;
                   ELSE

                     update o02agpe0.TB_COMS_INFORMES_COMS_2015 c
                            set c.importe_sin_reducc_tram = VIMPSINREDTRA,
                                c.importe_sin_reducc_calidad = VIMPSINREDCAL
                            where c.ref_poliza = VREFPLZ
                              and c.codlinea = VCODLIN;
                   END IF;


                 END IF;

              -- Actualizamos datos asegurados
                 BEGIN

                   IF SUBSTR( VCODENTMED, 0, 1 ) = '4' THEN
                    auxCodEnt := '3'|| SUBSTR( VCODENTMED, 2,3 );
                   ELSE
                     auxCodEnt  := VCODENTMED;
                   END IF;

                   select ase.nombre,ase.apellido1,ase.apellido2, ase.razonsocial
                     into  VNOMASG,VAPE1ASG, VAPE2ASG, VRAZONSOCIAL
                     from o02agpe0.tb_asegurados ase
                     where ase.nifcif = VNIFASG
                       and ase.codentidad = auxCodEnt
                       and rownum=1;

                   EXCEPTION
                            when others then
                             VNOMASG := null;
                             VAPE1ASG := null;
                             VAPE2ASG := null;
                             VRAZONSOCIAL := null;
                   END;
                    IF (VNOMASG is not null ) then

                      IF VCODPLN < 2015 THEN
                       update o02agpe0.TB_COMS_INFORMES_COMISIONES c
                          set c.nomasg = VNOMASG,
                              c.ape1asg =  VAPE1ASG ,
                              c.ape2asg = VAPE2ASG
                          where c.refplz = VREFPLZ
                            and c.codlin = VCODLIN;
                      ELSE
                      --2015+
                       update o02agpe0.TB_COMS_INFORMES_COMS_2015 c
                          set c.nom_aseg = VNOMASG,
                              c.ap1_aseg =  VAPE1ASG ,
                              c.ap2_aseg = VAPE2ASG
                          where c.ref_poliza = VREFPLZ
                            and c.codlinea = VCODLIN;
                      END IF;
                 END IF;
                 IF (VRAZONSOCIAL is not null) then
                    IF VCODPLN < 2015 THEN
                    update o02agpe0.TB_COMS_INFORMES_COMISIONES c
                          set c.razonsocial = VRAZONSOCIAL
                          where c.refplz = VREFPLZ
                            and c.codlin = VCODLIN;
                      ELSE
                      --2015+
                    update o02agpe0.TB_COMS_INFORMES_COMS_2015 c
                          set c.razonsocial_aseg = VRAZONSOCIAL
                          where c.ref_poliza = VREFPLZ
                            and c.codlinea = VCODLIN;
                      END IF;
                 END IF;


             END LOOP;
             dbms_output.put_line(contador);
             commit;
      CLOSE cur_consulta;

  EXCEPTION
      when others then
         dbms_output.put_line(SQLERRM);
         dbms_output.put_line('Error al actualizar informes de Fichero Comisiones. Mensaje: '||SQLERRM||' codigo: '|| SQLCODE);
          -- Deshace las transacciones
          rollback;
          -- Lanza una excepción para indicar que ha habido algún problema
          RAISE;


  END UPDATE_INF_FICHERO_COMISIONES;
------------------------------------------------------------------------

end PQ_INFORMES_COMISIONES;
/
SHOW ERRORS;