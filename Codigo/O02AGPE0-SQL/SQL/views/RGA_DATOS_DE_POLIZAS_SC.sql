CREATE OR REPLACE VIEW RGA_DATOS_DE_POLIZAS_SC AS
SELECT PO.IDPOLIZA AS IDENTIFICADOR_POLIZA,
       c.CODENTIDAD AS CODIGO_ENTIDAD,
       E.NOMENTIDAD AS NOMBRE_ENTIDAD,
       PO.OFICINA AS CODIGO_OFICINA,
       nvl(O.NOMOFICINA, ' ') AS NOMBRE_OFICINA,
       PO.CODUSUARIO AS USUARIO,
       PO.REFERENCIA || '' || TO_CHAR(PO.DC) AS REFERENCIA,
       PO.TIPOREF AS TIPO_POLIZA,
       ASEG.NIFCIF AS NIF_ASEGURADO,
       LIN.CODPLAN AS PLAN,
       LIN.CODLINEA AS CODIGO_LINEA,
       LIN.NOMLINEA AS NOMBRE_LINEA,
       C.IDCOLECTIVO || '' || TO_CHAR(C.DC) AS CODIGO_COLECTIVO,
       to_char(greatest(nvl(case
                              when po.idestado < 10 then
                               nvl((SELECT ph.FECHA
                                     FROM o02agpe0.VW_POLIZAS_HIST_ESTADOS_DESC ph
                                    WHERE ph.IDPOLIZA = po.IDPOLIZA
                                      AND ph.ESTADO = 3
                                      AND ROWNUM = 1),
                                   (SELECT ph.FECHA
                                      FROM o02agpe0.VW_POLIZAS_HIST_ESTADOS_DESC ph
                                     WHERE ph.IDPOLIZA = po.IDPOLIZA
                                       AND ph.ESTADO = 8
                                       AND ROWNUM = 1))
                              else
                               po.fechaenvio
                            end,
                            to_date('01011900', 'DDMMYYYY')),
                        nvl(po.fecha_modificacion,
                            to_date('01011900', 'DDMMYYYY')),
                        nvl(po.fecha_seguimiento,
                            to_date('01011900', 'DDMMYYYY'))),
               'DD/MM/YYYY') AS FECHA_GRABACION,
       to_char(po.fechaenvio, 'DD/MM/YYYY') AS FECHA_ENVIO,
       PAG.PCTPRIMERPAGO AS PORCENTAJE_PRIMER_PAGO,
       to_char(PAG.FECHA, 'DD/MM/YYYY') AS FECHA_PRIMER_PAGO,
       PAG.PCTSEGUNDOPAGO AS PORCENTAJE_SEGUNDO_PAGO,
       to_char(PAG.FECHASEGUNDOPAGO, 'DD/MM/YYYY') AS FECHA_SEGUNDO_PAGO,
       PAG.IBAN || PAG.CCCBANCO AS CUENTA_DE_PAGO,
       NVL(COS.COSTENETO, cos15.costetomador) AS COSTE_TOTAL,
       NVL(COS.BONIFMEDPREVENTIVAS + COS.BONIFASEGURADO,
           (SELECT SUM(bonrec.importe)
              FROM o02agpe0.Tb_Bonificacion_Recargo_2015 bonrec,
                   o02agpe0.tb_sc_c_bonif_recarg         t
             WHERE bonrec.codigo = t.cod_bon_rec
               AND t.tip_bon_rec = 'B'
               AND bonrec.iddistcoste = COS15.ID)) AS BONIFICACIONES,
       NVL(COS.DTOCOLECTIVO, 0) AS DESCUENTOS,
       NVL(COS.COSTENETO, cos15.costetomador) AS COSTE_NETO,
       NVL((SELECT SUM(IMPORTESUBV)
             FROM o02agpe0.Tb_Dist_Coste_Subvs COSSUB1
            WHERE COSSUB1.IDDISTCOSTE = COS.ID
              AND COSSUB1.CODORGANISMO = '0'),
           (SELECT SUM(IMPORTESUBV)
              FROM o02agpe0.Tb_Dist_Coste_Subvs_2015 COSSUB2
             WHERE COSSUB2.IDDISTCOSTE = COS15.ID
               AND COSSUB2.CODORGANISMO = '0')) AS SUBVENCION_ENESA,
       NVL((SELECT SUM(IMPORTESUBV)
             FROM o02agpe0.Tb_Dist_Coste_Subvs COSSUB1
            WHERE COSSUB1.IDDISTCOSTE = COS.ID
              AND COSSUB1.CODORGANISMO <> '0'),
           (SELECT SUM(IMPORTESUBV)
              FROM o02agpe0.Tb_Dist_Coste_Subvs_2015 COSSUB2
             WHERE COSSUB2.IDDISTCOSTE = COS15.ID
               AND COSSUB2.CODORGANISMO <> '0')) AS SUBVENCION_CCAA,
       NVL(COS.CARGOTOMADOR, cos15.costetomador) AS NETO_TOMADOR,
       C.CIFTOMADOR AS NIF_TOMADOR,
       -- E-S Mediadora según el histórico de colectivo...
       histCol.entmediadora as ENTIDAD_MEDIADORA,
       histCol.subentmediadora as SUBENTIDAD_MEDIADORA,
       to_char(PO.FECHA_VIGOR, 'DD/MM/YYYY') as FECHA_VIGOR,
       NVL(COS.primaneta, cos15.primacomercialneta) AS PRIMA_COMERCIAL,
       to_char(PO.FECHA_VTO, 'DD/MM/YYYY') as FECHA_VENCIMIENTO
  FROM o02agpe0.TB_POLIZAS                  PO,
       o02agpe0.TB_ENTIDADES                E,
       o02agpe0.TB_COLECTIVOS               C,
       o02agpe0.tb_historico_colectivos     histCol,
       o02agpe0.TB_OFICINAS                 O,
       o02agpe0.TB_ASEGURADOS               ASEG,
       o02agpe0.TB_LINEAS                   LIN,
       o02agpe0.TB_PAGOS_POLIZA             PAG,
       o02agpe0.TB_DISTRIBUCION_COSTES      COS,
       o02agpe0.Tb_Distribucion_Costes_2015 COS15
 WHERE PO.IDCOLECTIVO = C.ID
   AND E.CODENTIDAD = C.CODENTIDAD
   AND O.CODENTIDAD(+) = C.CODENTIDAD
   AND O.CODOFICINA(+) = PO.OFICINA
   and c.id = histCol.idcolectivo
   and histCol.rowid = (select hi.rowid
                          from (select *
                                  from o02agpe0.tb_historico_colectivos h
                                 order by h.fechacambio desc) hi
                         where idcolectivo = po.idcolectivo
                           and fechaefecto <= po.fechaenvio
                           and rownum = 1)
   AND ASEG.ID = PO.IDASEGURADO
   AND PO.LINEASEGUROID = LIN.LINEASEGUROID
   AND PAG.IDPOLIZA = PO.IDPOLIZA
   AND COS.IDPOLIZA(+) = PO.IDPOLIZA
   AND cos15.idpoliza(+) = po.idpoliza
   and po.idestado in (8, 14, 16);