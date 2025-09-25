SET DEFINE OFF;
SET SERVEROUTPUT ON;

create or replace package o02agpe0.PQ_INFORMES_RECIBOS is

  --  Author  : DAA
  -- Created : 24/09/2013 9:43:28
  -- Purpose :

 PROCEDURE UPDATE_INF_FICHERO_EMITIDOS (IDFICHERO IN NUMBER);
  PROCEDURE UPDATE_INF_FICH_EMIT_2015 (IDFICHERO IN NUMBER);
  PROCEDURE GET_ENTMEDIADORA (VREF_POLIZA IN VARCHAR2, VFECHA_EMISION IN DATE, VCODENTIDAD_MED OUT NUMBER, VCODSUBENT_MED OUT NUMBER, VCODENTIDAD OUT NUMBER);
  FUNCTION getCodOficinaByRefPoliza(v_refPoliza IN VARCHAR2, v_tipoReferencia IN VARCHAR2) RETURN NUMBER;

  FUNCTION calcularBonificaciones(v_idApli IN NUMBER) RETURN NUMBER;
  FUNCTION calcularRecargos(v_idApli IN NUMBER) RETURN NUMBER;
  FUNCTION calcularSubvCCAA(v_idApli IN NUMBER) RETURN NUMBER;

end PQ_INFORMES_RECIBOS;
/
create or replace package body o02agpe0.PQ_INFORMES_RECIBOS is

  --DAA 24/09/2013
  ---Cada vez que se importe un fichero de comisiones, guarda los datos en la tabla TB_COMS_INFORMES_RECIBOS.
------------------------------------------------------------------------
  PROCEDURE UPDATE_INF_FICHERO_EMITIDOS (IDFICHERO IN NUMBER) IS

      lc                       VARCHAR2(50) := 'PQ_INFORMES_RECIBOS.UPDATE_INF_FICHERO_EMITIDOS';
      VFASE                    TB_COMS_INFORMES_RECIBOS.FASE%TYPE;
      VCODENTIDAD_MED          TB_COMS_INFORMES_RECIBOS.CODENTIDAD_MED%TYPE;
      VCODSUBENT_MED           TB_COMS_INFORMES_RECIBOS.CODSUBENT_MED%TYPE;
      VCODPLAN                 TB_COMS_INFORMES_RECIBOS.CODPLAN%TYPE;
      VCODLINEA                TB_COMS_INFORMES_RECIBOS.CODLINEA%TYPE;
      VREF_COLECTIVO           TB_COMS_INFORMES_RECIBOS.REF_COLECTIVO%TYPE;
      VFECHA_EMISION           TB_COMS_INFORMES_RECIBOS.FECHA_EMISION%TYPE;
      VRS_TOMADOR              TB_COMS_INFORMES_RECIBOS.RS_TOMADOR%TYPE;
      VNUM_RECIBO              TB_COMS_INFORMES_RECIBOS.NUM_RECIBO%TYPE;
      VSALDO_TOMADOR           TB_COMS_INFORMES_RECIBOS.SALDO_TOMADOR%TYPE;
      VCOMPENSACION_TOM        TB_COMS_INFORMES_RECIBOS.COMPENSACION_TOM%TYPE;
      VCOMPENSACION_IMP        TB_COMS_INFORMES_RECIBOS.COMPENSACION_IMP%TYPE;
      VPAGO_RECIBO             TB_COMS_INFORMES_RECIBOS.PAGO_RECIBO%TYPE;
      VLIQUIDO_RECIBO          TB_COMS_INFORMES_RECIBOS.LIQUIDO_RECIBO%TYPE;
      VREF_POLIZA              TB_COMS_INFORMES_RECIBOS.REF_POLIZA%TYPE;
      VTIPO_RECIBO             TB_COMS_INFORMES_RECIBOS.TIPO_RECIBO%TYPE;
      VNIF_ASEGURADO           TB_COMS_INFORMES_RECIBOS.NIF_ASEGURADO%TYPE;
      VAP1_ASEG                TB_COMS_INFORMES_RECIBOS.AP1_ASEG%TYPE;
      VAP2_ASEG                TB_COMS_INFORMES_RECIBOS.AP2_ASEG%TYPE;
      VNOM_ASEG                TB_COMS_INFORMES_RECIBOS.NOM_ASEG%TYPE;
      VRAZONSOCIAL_ASEG        TB_COMS_INFORMES_RECIBOS.RAZONSOCIAL_ASEG%TYPE;
      VPRIMA_COMERCIAL         TB_COMS_INFORMES_RECIBOS.PRIMA_COMERCIAL%TYPE;
      VPRIMA_NETA              TB_COMS_INFORMES_RECIBOS.PRIMA_NETA%TYPE;
      VCOSTE_NETO              TB_COMS_INFORMES_RECIBOS.COSTE_NETO%TYPE;
      VCOSTE_TOMADOR           TB_COMS_INFORMES_RECIBOS.COSTE_TOMADOR%TYPE;
      VPAGO_POLIZA             TB_COMS_INFORMES_RECIBOS.PAGO_POLIZA%TYPE;
      VSALDO_POLIZA            TB_COMS_INFORMES_RECIBOS.SALDO_POLIZA%TYPE;
      VCODENTIDAD              TB_COMS_INFORMES_RECIBOS.CODENTIDAD%TYPE;
      VCODOFICINA              TB_COMS_INFORMES_RECIBOS.CODOFICINA%TYPE;
      v_tipoReferencia         TB_COMS_RECS_EMITIDOS_APLI.TIPOREFERENCIA%TYPE;

      TYPE TpCursor            IS REF CURSOR;
      cur_consulta             TpCursor;
      consulta                 varchar2(32000):=
      'select fa.FASE, fa.PLAN, re.LINEA, re.COLECTIVOREF, fa.FECHAEMISION, NVL(re.RAZONSOCIAL, re.NOMBRE ||'' ''||re.APELLIDO1 ||'' ''||re.APELLIDO2),
              re.RECIBO, re.COSTETOMADOR, re.COMPSALDOTOMADOR, re.COMPRECIBOSIMPAGADOS, re.PAGOS, re.LIQUIDO, rea.REFERENCIA,
              rea.TIPORECIBO, rea.NIFCIF, rea.APELLIDO1, rea.APELLIDO2, rea.NOMBRE, rea.RAZONSOCIAL, rea.PRIMACOMERCIAL, rea.PRIMANETA,
              rea.COSTENETO, rea.COSTETOMADOR, rea.PAGOS, rea.SALDOPOLIZA, rea.tiporeferencia
       from   tb_coms_recibos_emitidos re,        tb_coms_recs_emitidos_apli rea,
              tb_coms_recs_emitidos_ccaa reca,    tb_coms_recs_emitidos_det_comp redc,
              tb_coms_ficheros f,                 tb_coms_fase fa
       where  re.id = rea.idreciboemitido and re.id = reca.idreciboemitido(+) and
              re.id = redc.idreciboemitido(+) and re.idfichero = f.id and f.idfase = fa.id and
              re.idfichero = '|| IDFICHERO;

  BEGIN
      OPEN cur_consulta FOR consulta;
             LOOP
                 FETCH cur_consulta INTO VFASE, VCODPLAN, VCODLINEA, VREF_COLECTIVO, VFECHA_EMISION, VRS_TOMADOR, VNUM_RECIBO,
                       VSALDO_TOMADOR, VCOMPENSACION_TOM, VCOMPENSACION_IMP, VPAGO_RECIBO, VLIQUIDO_RECIBO, VREF_POLIZA, VTIPO_RECIBO,
                       VNIF_ASEGURADO, VAP1_ASEG, VAP2_ASEG, VNOM_ASEG, VRAZONSOCIAL_ASEG, VPRIMA_COMERCIAL, VPRIMA_NETA,
                       VCOSTE_NETO, VCOSTE_TOMADOR, VPAGO_POLIZA, VSALDO_POLIZA, v_tipoReferencia;

                 EXIT WHEN cur_consulta%NOTFOUND;

                 ------------------ VCODENTIDAD_MED, VCODSUBENT_MED ------------------------------------
                 PQ_INFORMES_RECIBOS.GET_ENTMEDIADORA (VREF_POLIZA, VFECHA_EMISION, VCODENTIDAD_MED, VCODSUBENT_MED, VCODENTIDAD);

                 VCODOFICINA:= getCodOficinaByRefPoliza(VREF_POLIZA, v_tipoReferencia);

                 -- Hacemos el insert en TB_COMS_INFORMES_RECIBOS
                 insert into TB_COMS_INFORMES_RECIBOS values (sq_coms_informes_recibos.nextval, VFASE, VCODENTIDAD_MED,
                             VCODSUBENT_MED, VCODPLAN, VCODLINEA, VREF_COLECTIVO, VFECHA_EMISION, VRS_TOMADOR, VNUM_RECIBO,
                             VSALDO_TOMADOR, VCOMPENSACION_TOM, VCOMPENSACION_IMP, VPAGO_RECIBO, VLIQUIDO_RECIBO, VREF_POLIZA,
                             VTIPO_RECIBO, VNIF_ASEGURADO, VAP1_ASEG, VAP2_ASEG, VNOM_ASEG, VRAZONSOCIAL_ASEG,
                             VPRIMA_COMERCIAL, VPRIMA_NETA, VCOSTE_NETO, VCOSTE_TOMADOR, VPAGO_POLIZA, VSALDO_POLIZA, IDFICHERO,
                             VCODENTIDAD, VCODOFICINA);

             END LOOP;
      CLOSE cur_consulta;
  EXCEPTION
      when others then
          pq_utl.log(lc,'Error al actualizar informes de Fichero Emitidos. Mensaje: '||SQLERRM||', codigo: '|| SQLCODE, 1);
          -- Deshace las transacciones
          rollback;
          -- Lanza una excepción para indicar que ha habido algún problema
          RAISE;

  END UPDATE_INF_FICHERO_EMITIDOS;
------------------------------------------------------------------------

PROCEDURE UPDATE_INF_FICH_EMIT_2015 (IDFICHERO IN NUMBER) IS

	lc VARCHAR2(50) := 'PQ_INFORMES_RECIBOS.UPDATE_INF_FICH_EMIT_2015';

	v_fase				TB_COMS_INFORMES_RECIBOS_2015.FASE%TYPE;

	v_codEnt			TB_COMS_INFORMES_RECIBOS_2015.CODENTIDAD%TYPE;
	v_codOfi			TB_COMS_INFORMES_RECIBOS_2015.CODOFICINA%TYPE;
	v_codEntMed			TB_COMS_INFORMES_RECIBOS_2015.CODENTMED%TYPE;
	v_codSubent			TB_COMS_INFORMES_RECIBOS_2015.CODSUBENTMED%TYPE;

	v_codPlan			TB_COMS_INFORMES_RECIBOS_2015.CODPLAN%TYPE;
	v_codLinea			TB_COMS_INFORMES_RECIBOS_2015.CODLINEA%TYPE;
	v_fecEmision		TB_COMS_INFORMES_RECIBOS_2015.FECHA_EMISION%TYPE;
	v_refCol			TB_COMS_INFORMES_RECIBOS_2015.REF_COLECTIVO%TYPE;
	v_rsTomador			TB_COMS_INFORMES_RECIBOS_2015.RS_TOMADOR%TYPE;
	v_numRecibo			TB_COMS_INFORMES_RECIBOS_2015.NUM_RECIBO%TYPE;

	v_refPol			TB_COMS_INFORMES_RECIBOS_2015.REF_POLIZA%TYPE;
	v_tipoRecibo		TB_COMS_INFORMES_RECIBOS_2015.TIPO_RECIBO%TYPE;
	v_nifAseg			TB_COMS_INFORMES_RECIBOS_2015.NIF_ASEGURADO%TYPE;
	v_ap1Aseg			TB_COMS_INFORMES_RECIBOS_2015.AP1_ASEG%TYPE;
	v_ap2Aseg			TB_COMS_INFORMES_RECIBOS_2015.AP2_ASEG%TYPE;
	v_nomAseg			TB_COMS_INFORMES_RECIBOS_2015.NOM_ASEG%TYPE;
	v_rsAseg			TB_COMS_INFORMES_RECIBOS_2015.RAZONSOCIAL_ASEG%TYPE;
	v_primaCom			TB_COMS_INFORMES_RECIBOS_2015.PRIMA_COMERCIAL%TYPE;
	v_primaNeta			TB_COMS_INFORMES_RECIBOS_2015.PRIMA_NETA%TYPE;

	v_recargoCons		TB_COMS_INFORMES_RECIBOS_2015.RECARGO_CONSORCIO%TYPE;
	v_reciboPrima		TB_COMS_INFORMES_RECIBOS_2015.RECIBO_PRIMA%TYPE;
	v_subvEnesa			TB_COMS_INFORMES_RECIBOS_2015.SUBV_ENESA%TYPE;
	v_costeTom			TB_COMS_INFORMES_RECIBOS_2015.COSTE_TOMADOR%TYPE;
	v_totalCosteTom		TB_COMS_INFORMES_RECIBOS_2015.TOTAL_COSTE_TOMADOR%TYPE;
	v_pagos				TB_COMS_INFORMES_RECIBOS_2015.PAGOS%TYPE;
	v_diferencia		TB_COMS_INFORMES_RECIBOS_2015.DIFERENCIA%TYPE;
	v_impRecargoAval	TB_COMS_INFORMES_RECIBOS_2015.IMP_RECARGO_AVAL%TYPE;
	v_impRecargoFrac	TB_COMS_INFORMES_RECIBOS_2015.IMP_RECARGO_FRACC%TYPE;
  
  v_domiciliado       TB_COMS_INFORMES_RECIBOS_2015.DOMICILIADO%TYPE;
  v_destDomiciliacion TB_COMS_INFORMES_RECIBOS_2015.DEST_DOMICILIACION%TYPE;
  v_impDomiciliado    TB_COMS_INFORMES_RECIBOS_2015.IMP_DOMICILIADO%TYPE;

	--Campos calculados
	v_bonif				TB_COMS_INFORMES_RECIBOS_2015.BONIFICACIONES%TYPE;
	v_subvCcaa			TB_COMS_INFORMES_RECIBOS_2015.SUBV_CCAA%TYPE;
	v_recargos			TB_COMS_INFORMES_RECIBOS_2015.RECARGOS%TYPE;

	--Extra
	v_tipoReferencia	TB_COMS_RECS_EMITIDOS_APLI.TIPOREFERENCIA%TYPE;
  v_idApli          TB_COMS_RECS_EMITIDOS_APLI.ID%TYPE;

	TYPE TpCursor IS REF CURSOR;
	cur_consulta TpCursor;
	consulta varchar2(32000):=
	'select fa.FASE, fa.PLAN, re.LINEA, fa.FECHAEMISION, re.COLECTIVOREF,  NVL(re.RAZONSOCIAL, re.NOMBRE ||'' ''||re.APELLIDO1 ||'' ''||re.APELLIDO2),
		  re.RECIBO, rea.REFERENCIA, rea.TIPORECIBO, rea.NIFCIF, rea.APELLIDO1, rea.APELLIDO2, rea.NOMBRE, rea.RAZONSOCIAL,
		  rea.DE1_PRIMA_COMERCIAL, rea.DE1_PRIMA_COMERCIAL_NETA, rea.DE1_RECARGO_CONSORCIO, rea.DE1_RECIBO_PRIMA, rea.DE1_SUBV_ENESA,
		  rea.DE1_COSTE_TOMADOR, rea.DE1_TOTAL_COSTE_TOMADOR, rea.DE1_PAGOS, rea.DE1_DIFERENCIA, rea.DE1_RECARGO_AVAL, rea.DE1_RECARGO_FRACC,
		  rea.tiporeferencia, rea.id, rea.domiciliado, rea.dest_domiciliacion, rea.de1_imp_domiciliado
from  tb_coms_recibos_emitidos re,        tb_coms_recs_emitidos_apli rea,
		  tb_coms_recs_emitidos_ccaa reca,    tb_coms_recs_emitidos_det_comp redc,
		  tb_coms_ficheros f,                 tb_coms_fase fa
where re.id = rea.idreciboemitido and re.id = reca.idreciboemitido(+) and
		  re.id = redc.idreciboemitido(+) and re.idfichero = f.id and f.idfase = fa.id and
		  re.idfichero = '|| IDFICHERO;

	BEGIN
		OPEN cur_consulta FOR consulta;
			LOOP
				FETCH cur_consulta INTO v_fase, v_codPlan, v_codLinea, v_fecEmision, v_refCol,
										v_rsTomador, v_numRecibo, v_refPol, v_tipoRecibo, v_nifAseg, v_ap1Aseg, v_ap2Aseg, v_nomAseg, v_rsAseg,
										v_primaCom, v_primaNeta, v_recargoCons, v_reciboPrima, v_subvEnesa,
										v_costeTom, v_totalCosteTom, v_pagos, v_diferencia, v_impRecargoAval, v_impRecargoFrac,
										v_tipoReferencia, v_idApli, v_domiciliado, v_destDomiciliacion, v_impDomiciliado;

				EXIT WHEN cur_consulta%NOTFOUND;

				--Rellenamos entidad, entidad mediadora y subentidad mediadora
				PQ_INFORMES_RECIBOS.GET_ENTMEDIADORA (v_refPol, v_fecEmision, v_codEntMed, v_codSubent, v_codEnt);

				--Rellenamos el resto de variables
				v_codOfi	   := getCodOficinaByRefPoliza(v_refPol, v_tipoReferencia);
				v_bonif		   := calcularBonificaciones(v_idApli);

				v_subvCcaa	 := calcularSubvCCAA(v_idApli);
				v_recargos	 := calcularRecargos(v_idApli);

				 -- Hacemos el insert en TB_COMS_INFORMES_RECIBOS_2015
				 insert into TB_COMS_INFORMES_RECIBOS_2015 values (sq_coms_informes_recibos_2015.nextval,
																	v_fase,
																	v_codEnt,
																	v_codOfi,
																	v_codEntMed,
																	v_codSubent,
																	v_codPlan,
																	v_codLinea,
																	v_fecEmision,
																	v_refCol,
																	v_rsTomador,
																	v_numRecibo,
																	v_refPol,
																	v_tipoRecibo,
																	v_nifAseg,
																	v_ap1Aseg,
																	v_ap2Aseg,
																	v_nomAseg,
																	v_rsAseg,
																	v_primaCom,
																	v_primaNeta,
																	v_recargoCons,
																	v_reciboPrima,
																	v_subvEnesa,
																	v_costeTom,
																	v_totalCosteTom,
																	v_pagos,
																	v_diferencia,
																	v_impRecargoAval,
																	v_impRecargoFrac,
																	v_bonif,
																	v_subvCcaa,
																	v_recargos,
                                  IDFICHERO,
                                  v_domiciliado, 
                                  v_destDomiciliacion, 
                                  v_impDomiciliado
																	);

			END LOOP;
		CLOSE cur_consulta;
	EXCEPTION
		when others then
			pq_utl.log(lc,'Error al actualizar informes de Fichero Emitidos 2015. Mensaje: '||SQLERRM||', codigo: '|| SQLCODE, 1);
		  -- Deshace las transacciones
		  rollback;
		  -- Lanza una excepción para indicar que ha habido algún problema
		  RAISE;

END UPDATE_INF_FICH_EMIT_2015;
------------------------------------------------------------------------

PROCEDURE GET_ENTMEDIADORA (VREF_POLIZA IN VARCHAR2, VFECHA_EMISION IN DATE, VCODENTIDAD_MED OUT NUMBER, VCODSUBENT_MED OUT NUMBER, VCODENTIDAD OUT NUMBER) IS

   lc     VARCHAR2(50) := 'PQ_INFORMES_RECIBOS.GET_ENTMEDIADORA_INDIVIDUAL';

   BEGIN
       SELECT codentidad, entmediadora, subentmediadora into VCODENTIDAD, VCODENTIDAD_MED, VCODSUBENT_MED
       FROM (
           select codentidad, entmediadora, subentmediadora, FECHACAMBIO
           from (
               select c.*
               from o02agpe0.tb_historico_colectivos c
               where c.idcolectivo in (select p.idcolectivo from o02agpe0.tb_polizas p where p.referencia = VREF_POLIZA )
               and c.fechaefecto <= VFECHA_EMISION
           )
           ORDER BY FECHACAMBIO DESC
       )
       WHERE ROWNUM = 1;

   EXCEPTION
         WHEN NO_DATA_FOUND THEN
            VCODENTIDAD_MED := 0;
            VCODSUBENT_MED := 0;
            VCODENTIDAD := 0;
         WHEN OTHERS THEN
            pq_utl.log(lc,'Error al recuperar los datos de la entidad mediadora. Mensaje: '||SQLERRM||', codigo: '|| SQLCODE, 1);
            RAISE;

END GET_ENTMEDIADORA;
------------------------------------------------------------------------

FUNCTION getCodOficinaByRefPoliza(v_refPoliza IN VARCHAR2, v_tipoReferencia IN VARCHAR2) RETURN NUMBER
IS

  v_codOficina TB_COMS_INFORMES_RECIBOS.CODOFICINA%TYPE;

BEGIN

       BEGIN

          select oficina into v_codOficina
          from tb_polizas p
          where p.referencia = v_refPoliza
          and p.tiporef = v_tipoReferencia
          and rownum = 1;

       EXCEPTION
                WHEN NO_DATA_FOUND THEN
                     v_codOficina := null;
       END;

  RETURN v_codOficina;

END getCodOficinaByRefPoliza;

------------------------------------------------------------------------

--calcularBonificaciones para 2015+
FUNCTION calcularBonificaciones(v_idApli IN NUMBER) RETURN NUMBER
IS

  v_bonif TB_COMS_INFORMES_RECIBOS_2015.BONIFICACIONES%TYPE;
BEGIN
 BEGIN

      select nvl(sum(bonrec.importe), 0) into v_bonif
      from tb_coms_recs_emi_apli_bon_rec bonrec, TB_SC_C_BONIF_RECARG val
      where bonrec.idapli = v_idApli
      and bonrec.codigo = val.cod_bon_rec
      and val.tip_bon_rec='B';

    END;

  RETURN v_bonif;

END calcularBonificaciones;


------------------------------------------------------------------------

--calcularRecargos para 2015+
FUNCTION calcularRecargos(v_idApli IN NUMBER) RETURN NUMBER
IS

  v_recargos TB_COMS_INFORMES_RECIBOS_2015.RECARGOS%TYPE;
BEGIN
 BEGIN

  select nvl(sum(bonrec.importe), 0) into v_recargos
      from tb_coms_recs_emi_apli_bon_rec bonrec, TB_SC_C_BONIF_RECARG val
      where bonrec.idapli = v_idApli
      and bonrec.codigo = val.cod_bon_rec
      and val.tip_bon_rec='R';

    END;

  RETURN v_recargos;

END calcularRecargos;

------------------------------------------------------------------------
--calcularSubvCCAA para 2015+
FUNCTION calcularSubvCCAA(v_idApli IN NUMBER) RETURN NUMBER
IS

  v_subvCcaa TB_COMS_INFORMES_RECIBOS_2015.SUBV_CCAA%TYPE;
BEGIN
 BEGIN

   select nvl(sum(subvcomunidades), 0) into v_subvCcaa
     from tb_coms_recs_emi_apli_ccaa
    where idapli = v_idApli;

    END;

  RETURN v_subvCcaa;

END calcularSubvCCAA;
------------------

end PQ_INFORMES_RECIBOS;
/
SHOW ERRORS;