CREATE OR REPLACE FORCE EDITIONABLE VIEW "O02AGPE0"."VW_RGA_COMISIONES" ("NUMFAS", "CODCOL", "CODLIN", "CODPLN", "CODENT", "CODENTMED", "CODSUBMED", "CODOFI", "REFPLZ", "TIPPOL", "FECCAR", "NOMASG", "NIFASG", "GN", "PRINUE", "GASNUE", "GASENTNUE", "GASSUBNUE", "GASRGANUE", "TOTNUE", "COSTOT", "PRISUM", "GASSUM", "GASENTSUM", "GASSUBSUM", "GASRGASUM", "PTESUM", "PTEENTSUM", "PTESUBSUM", "PTERGASUM", "GASRGANUE_ADM", "GASRGANUE_ADQ", "GASRGASUM_ADM", "GASRGASUM_ADQ", "PTERGASUM_ADM", "PTERGASUM_ADQ") AS 
  select cast(fase.fase as VARCHAR2(4)) as NUMFAS,
       colectivo.idcolectivo || colectivo.dc as CODCOL,
       lin.codlinea as CODLIN,
       lin.codplan as CODPLN,
       colectivo.codentidad as CODENT,
       histCol.entmediadora as CODENTMED,
       histCol.subentmediadora as CODSUBMED,
       CAST(poliza.oficina AS NUMBER(4)) as CODOFI,
       CAST(poliza.referencia || TO_CHAR(NVL(poliza.dc, 0)) AS VARCHAR2(8)) AS REFPLZ,
       poliza.tiporef as TIPPOL,
       TO_CHAR(fichero.fecha_aceptacion, 'DD/MM/YYYY') as FECCAR,
       nvl(aseg.razonsocial, aseg.nombre || ' ' || aseg.apellido1 || ' ' || aseg.apellido2) as NOMASG,
       aseg.nifcif as NIFASG,
       grupo_negocio.grupo_negocio as GN,
       --DATOS NUEVOS: en el nuevo esquema se corresponde con los "gastos abonar"
       grupo_negocio.frac_pri_com_neta as PRINUE,
       grupo_negocio.ga_comision_mediador as GASNUE,
       grupo_negocio.ga_commed_entidad as GASENTNUE,
       grupo_negocio.ga_commed_esmed as GASSUBNUE,
       (grupo_negocio.ga_admin + grupo_negocio.ga_adq) as GASRGANUE,
       (grupo_negocio.ga_admin + grupo_negocio.ga_adq + grupo_negocio.ga_comision_mediador) as TOTNUE,
       --DATOS TOTALES: en el nuevo esquema se corresponde con los "gastos devengados"
       (select a.de1_total_coste_tomador
        from o02agpe0.tb_coms_fase fa, o02agpe0.tb_coms_ficheros fi,
             o02agpe0.tb_coms_recibos_emitidos r, o02agpe0.tb_coms_recs_emitidos_apli a
        where fi.idfase = fa.id and r.idfichero = fi.id and a.idreciboemitido = r.id
              and fa.plan = fase.plan and a.referencia = poliza.referencia
              and a.tiporeferencia = poliza.tiporef and fa.fase = fase.fase) as COSTOT,
       grupo_negocio.prima_comercial_neta as PRISUM,
       grupo_negocio.Gd_Imp_Com_Mediador as GASSUM,
       grupo_negocio.Gd_Imp_Commed_Entidad as GASENTSUM,
       grupo_negocio.Gd_Imp_Commed_Esmed as GASSUBSUM,
       (grupo_negocio.Gd_Imp_Admin + grupo_negocio.Gd_Imp_Adq) as GASRGASUM,
       --GASTOS PENDIENTES
       grupo_negocio.Gp_Comision_Mediador as PTESUM,
       grupo_negocio.GP_Commed_Entidad as PTEENTSUM,
       grupo_negocio.GP_Commed_Esmed as PTESUBSUM,
       (grupo_negocio.GP_Admin + grupo_negocio.Gp_Adq) as PTERGASUM,
	   -- adm y adq de "Gastos abonar"
	   grupo_negocio.ga_admin as GASRGANUE_ADM,
       grupo_negocio.ga_adq as GASRGANUE_ADQ,
	   -- adm y adq de "Gastos devengados"
	   grupo_negocio.Gd_Imp_Admin as GASRGASUM_ADM,
       grupo_negocio.Gd_Imp_Adq as GASRGASUM_ADQ,
	   -- adm y adq de "Gastos pendientes"
	   grupo_negocio.GP_Admin as PTERGASUM_ADM,
       grupo_negocio.Gp_Adq as PTERGASUM_ADQ
  From o02agpe0.tb_coms_unif_fichero fichero
	INNER JOIN o02agpe0.tb_coms_unif_fase fase ON fichero.id = fase.idfichero
	INNER JOIN O02AGPE0.TB_COMS_CIERRE cierre ON fase.idcierre = cierre.id
	INNER JOIN o02agpe0.tb_coms_unif17_recibo recibo ON fase.id = recibo.idfase
	INNER JOIN o02agpe0.tb_coms_unif17_poliza polComis ON recibo.id = polComis.idrecibo
	INNER JOIN o02agpe0.tb_coms_unif17_grupo_negocio grupo_negocio ON polComis.id = grupo_negocio.idpoliza
	INNER JOIN o02agpe0.tb_lineas lin ON lin.codplan = fase.plan and lin.codlinea = recibo.linea
	LEFT OUTER JOIN o02agpe0.tb_polizas poliza ON polComis.referencia = poliza.referencia
                                                  and polComis.tipo_referencia = poliza.tiporef
                                                  and poliza.lineaseguroid = lin.lineaseguroid
	INNER JOIN o02agpe0.tb_colectivos colectivo ON poliza.idcolectivo = colectivo.id
	INNER JOIN o02agpe0.tb_asegurados aseg ON poliza.idasegurado = aseg.id
	INNER JOIN o02agpe0.tb_historico_colectivos histCol on (colectivo.id = histCol.Idcolectivo)
  where histCol.rowid = (select hi.rowid from (select * from o02agpe0.tb_historico_colectivos h
                                                        order by h.fechacambio desc) hi
                                         where idcolectivo = poliza.idcolectivo
                                               and fechaefecto <= poliza.fechaenvio and rownum=1);
