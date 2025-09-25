SET DEFINE OFF;
SET SERVEROUTPUT ON;

CREATE OR REPLACE PACKAGE O02AGPE0.PQ_SEGUIMIENTO_POLIZAS IS

  PROCEDURE generaCorreoResumen;

  PROCEDURE generaCorreoTomadores;

end PQ_SEGUIMIENTO_POLIZAS;
/
create or replace package body o02agpe0.PQ_SEGUIMIENTO_POLIZAS is

PROCEDURE generaCorreoResumen AS

    lc VARCHAR2(50) := 'pq_seguimiento_polizas.generaCorreoResumen';

	v_num_plz NUMBER(5);
    v_num_anx NUMBER(5);
    plzs_nf   VARCHAR2(30000) := ' ';
    TYPE TpCursor IS REF CURSOR;
    v_cursor     TpCursor;
    v_referencia VARCHAR2(7);
    v_asegurado  VARCHAR2(10);
    v_detalle    VARCHAR2(255);
    v_plan		  NUMBER(4);
	v_linea		  NUMBER(3);
	v_colectivo	  VARCHAR2(7);
	v_tomador	  VARCHAR2(10);
	v_desc_estado VARCHAR2(30);
	v_estados_poliza PQ_ENVIO_CORREOS.map_estados;
	v_estados_anexo PQ_ENVIO_CORREOS.map_estados;

  BEGIN

    PQ_UTL.LOG(lc, '** INICIO geraCorreoResumen -SeguimientoPolizas', 2);


    PQ_UTL.LOG(lc, 'Antes de consultar polizas Actualizadas' , 2);
    SELECT COUNT(*)
      INTO v_num_plz
      FROM o02agpe0.TB_TMP_BATCH_SEGUIMIENTO t
    WHERE t.TIPO = 1;

    FOR r IN (SELECT
                    COUNT(t.tipo) AS num_poliz,
                    e.desc_estado AS estado
                FROM o02agpe0.TB_TMP_BATCH_SEGUIMIENTO t
                JOIN o02agpe0.TB_ESTADOS_AGROSEGURO e
                    ON t.estado = e.idestado
                WHERE t.tipo = 1
                    AND t.estado IN (
                        SELECT DISTINCT s.estado
                        FROM o02agpe0.TB_TMP_BATCH_SEGUIMIENTO s
                        WHERE s.tipo = 1
                    )
                GROUP BY e.desc_estado)
    LOOP
        v_estados_poliza(r.estado) := r.num_poliz;
        PQ_UTL.LOG(lc, 'Obtenemos para el estado: ' || r.estado || ' el nº de polizas:' || r.num_poliz, 2);
    END LOOP;

    PQ_UTL.LOG(lc, 'Polizas actualizadas: ' || v_num_plz, 2);


    PQ_UTL.LOG(lc, 'Antes de consultar Anexos Actualizados' , 2);

    SELECT COUNT(*)
      INTO v_num_anx
      FROM o02agpe0.TB_TMP_BATCH_SEGUIMIENTO t
    WHERE t.TIPO = 2;

    /* Incidencia para obtener el resumen, ya que el estado de los Anexos es */
    /* alfanumérico y por tanto no se puede buscar en la tabla de estados de Agroseguro.*/
    FOR r IN (SELECT
                    COUNT(t.tipo) AS num_poliz,
                    t.detalle AS estado
                FROM o02agpe0.TB_TMP_BATCH_SEGUIMIENTO t
                WHERE t.tipo = 2
                    AND t.estado IN (
                        SELECT DISTINCT s.estado
                        FROM o02agpe0.TB_TMP_BATCH_SEGUIMIENTO s
                        WHERE s.tipo = 2
                    )
                GROUP BY t.detalle)
    LOOP
        v_estados_anexo(r.estado) := r.num_poliz;
        PQ_UTL.LOG(lc, 'Anexos - Obtenemos para el estado: ' || r.estado || ' el nº de polizas:' || r.num_poliz, 2);
    END LOOP;

    PQ_UTL.LOG(lc, 'Anexos e incidencias actualizados: ' || v_num_anx, 2);

    PQ_UTL.LOG(lc, 'Abrimos cursor para obtener las polizas que están en estado 3' , 2);
    OPEN v_cursor
	  FOR 'SELECT t.REFERENCIA, t.ASEGURADO, t.DETALLE, t.PLAN, t.LINEA, t.COLECTIVO, t.CIF_TOMADOR, e.desc_estado
		 FROM o02agpe0.TB_TMP_BATCH_SEGUIMIENTO t
		 LEFT JOIN TB_ESTADOS_AGROSEGURO e ON t.estado = e.idestado
	 	 WHERE t.TIPO = 3';
    LOOP
      FETCH v_cursor
        INTO v_referencia, v_asegurado, v_detalle, v_plan, v_linea, v_colectivo, v_tomador, v_desc_estado;
      EXIT WHEN v_cursor%NOTFOUND;

	  IF LENGTH(plzs_nf) < 28000 THEN
			plzs_nf := plzs_nf || v_referencia || ' - ' || v_asegurado || ' - ' || v_plan || ' - ' || v_linea || 
				' - ' || v_colectivo || ' - ' || v_tomador || ' - ' || v_desc_estado || '  ' || PQ_ENVIO_CORREOS.v_retorno;
	  ELSE
			plzs_nf := plzs_nf || '[...] ' || o02agpe0.PQ_ENVIO_CORREOS.v_retorno;
	  END IF;
    END LOOP;
	
    PQ_UTL.LOG(lc, 'POLIZAS NOT FOUND -->' || plzs_nf, 2);
    
    PQ_UTL.LOG(lc, 'Antes de llamar a generaCorreoResumenSeguimiento', 2);
    PQ_ENVIO_CORREOS.generaCorreoResumenSeguimiento(v_num_plz,
                                                    v_num_anx,
                                                    plzs_nf,
                                                    v_estados_poliza,
                                                    v_estados_anexo
                                                    );

    PQ_UTL.LOG(lc, '** FIN geraCorreoResumen -SeguimientoPolizas', 2);

  END;

  PROCEDURE generaCorreoTomadores AS

    lc VARCHAR2(50) := 'pq_seguimiento_polizas.generaCorreoTomadores';
    TYPE TpCursor IS REF CURSOR;
    v_cursor     TpCursor;
    v_cursor2    TpCursor;
    v_cursor3    TpCursor;
    v_cursor4    TpCursor;
    v_cifTomador o02agpe0.TB_TMP_BATCH_SEGUIMIENTO.cif_tomador%TYPE;
    v_plan       o02agpe0.TB_TMP_BATCH_SEGUIMIENTO.plan%TYPE;
    v_linea      o02agpe0.TB_TMP_BATCH_SEGUIMIENTO.linea%TYPE;
    v_asegurado  o02agpe0.TB_TMP_BATCH_SEGUIMIENTO.asegurado%TYPE;
    v_entidad    o02agpe0.TB_TMP_BATCH_SEGUIMIENTO.entidad%TYPE;
    v_oficina    o02agpe0.TB_TMP_BATCH_SEGUIMIENTO.oficina%TYPE;
    v_detalle    o02agpe0.TB_TMP_BATCH_SEGUIMIENTO.detalle%TYPE;
	v_eMail      o02agpe0.TB_TOMADORES.email%TYPE;
    v_eMail2     o02agpe0.TB_TOMADORES.email_2%TYPE;
    v_eMail3     o02agpe0.TB_TOMADORES.email_3%TYPE;
    v_plzs       VARCHAR2(10000);
    v_anexos     VARCHAR2(10000);
    v_plzs_sg    VARCHAR2(10000);
	v_est_pol	 o02agpe0.TB_CONFIG_AGP.agp_valor%TYPE;
	v_est_inc	 o02agpe0.TB_CONFIG_AGP.agp_valor%TYPE;

  BEGIN

	SELECT conf.agp_valor
      INTO v_est_pol
      FROM o02agpe0.TB_CONFIG_AGP conf
     WHERE conf.agp_nemo = 'EST_POL_MAIL_SEG_TOM';

    SELECT conf.agp_valor
      INTO v_est_inc
      FROM o02agpe0.TB_CONFIG_AGP conf
     WHERE conf.agp_nemo = 'EST_INC_MAIL_SEG_TOM';

    OPEN v_cursor FOR 'SELECT CIF_TOMADOR, ENTIDAD
						FROM o02agpe0.TB_TMP_BATCH_SEGUIMIENTO
						WHERE TIPO <> 3
						GROUP BY CIF_TOMADOR, ENTIDAD';
	LOOP
      FETCH v_cursor
        INTO v_cifTomador, v_entidad;
      EXIT WHEN v_cursor%NOTFOUND;

	  v_plzs := '';
      v_anexos := '';
      v_plzs_sg := '';

      PQ_UTL.LOG(lc, 'Tomador: ' || v_cifTomador || ' / ' || v_entidad, 2);

      BEGIN

		SELECT TRIM(t.email), TRIM(t.email_2), TRIM(t.email_3)
        INTO v_eMail, v_eMail2, v_eMail3
        FROM o02agpe0.TB_TOMADORES t
       WHERE t.ciftomador = v_cifTomador
         AND t.codentidad = v_entidad;

        IF v_eMail IS NULL AND v_eMail2 IS NULL AND v_eMail3 IS NULL THEN

		  PQ_UTL.LOG(lc, 'Tomador sin correo electrónico registrado.', 2);

		ELSE

          OPEN v_cursor2 FOR 'SELECT t.ASEGURADO, t.PLAN, t.LINEA, t.ENTIDAD, t.OFICINA, t.DETALLE
								FROM o02agpe0.TB_TMP_BATCH_SEGUIMIENTO t
								WHERE t.TIPO = 1
									AND t.CIF_TOMADOR = ''' || v_cifTomador || '''
									AND t.ENTIDAD = ' || v_entidad || '
									AND t.ESTADO IN (' || v_est_pol || ')';
          LOOP
            FETCH v_cursor2
              INTO v_asegurado, v_plan, v_linea, v_entidad, v_oficina, v_detalle;
            EXIT WHEN v_cursor2%NOTFOUND;

            v_plzs := v_plzs || v_plan || '/' || v_linea || ' - ' ||
                      v_asegurado || ' - ' || v_entidad || '-' || v_oficina ||
                      ' - ' || v_detalle || ' ' || PQ_ENVIO_CORREOS.v_retorno;

          END LOOP;

          OPEN v_cursor3 FOR 'SELECT t.ASEGURADO, t.PLAN, t.LINEA, t.ENTIDAD, t.OFICINA, t.DETALLE
								FROM o02agpe0.TB_TMP_BATCH_SEGUIMIENTO t
								WHERE t.TIPO = 2
									AND t.CIF_TOMADOR = ''' || v_cifTomador || '''
									AND t.ENTIDAD = ' || v_entidad || '
									AND t.ESTADO IN (' || v_est_inc || ')';
          LOOP
            FETCH v_cursor3
              INTO v_asegurado, v_plan, v_linea, v_entidad, v_oficina, v_detalle;
            EXIT WHEN v_cursor3%NOTFOUND;

            v_anexos := v_anexos || v_plan || '/' || v_linea || ' - ' ||
                        v_asegurado || ' - ' || v_entidad || '-' ||
                        v_oficina || ' - ' || v_detalle || ' ' ||
                        PQ_ENVIO_CORREOS.v_retorno;

          END LOOP;

          OPEN v_cursor4 FOR 'SELECT t.ASEGURADO, t.PLAN, t.LINEA, t.ENTIDAD, t.OFICINA, t.DETALLE
								FROM o02agpe0.TB_TMP_BATCH_SEGUIMIENTO t
								WHERE t.TIPO = 4
									AND t.CIF_TOMADOR = ''' || v_cifTomador || '''
									AND t.ENTIDAD = ' || v_entidad;
          LOOP
            FETCH v_cursor4
              INTO v_asegurado, v_plan, v_linea, v_entidad, v_oficina, v_detalle;
            EXIT WHEN v_cursor4%NOTFOUND;

            v_plzs_sg := v_plzs_sg || v_plan || '/' || v_linea || ' - ' ||
                         v_asegurado || ' - ' || v_entidad || '-' ||
                         v_oficina || ' - ' || v_detalle || ' ' ||
                         PQ_ENVIO_CORREOS.v_retorno;

          END LOOP;

          IF v_plzs IS NOT NULL OR v_anexos IS NOT NULL OR v_plzs_sg IS NOT NULL THEN
                IF v_eMail IS NOT NULL THEN
                    Pq_Envio_Correos.generaCorreoSeguimientoTomador(v_cifTomador,
                                                                v_eMail,
                                                                v_plzs,
                                                                v_anexos,
                                                                v_plzs_sg);
                END IF;
                
                IF v_eMail2 IS NOT NULL THEN
                    Pq_Envio_Correos.generaCorreoSeguimientoTomador(v_cifTomador,
                                                                v_eMail2,
                                                                v_plzs,
                                                                v_anexos,
                                                                v_plzs_sg);
                END IF;
                
                IF v_eMail3 IS NOT NULL THEN
                    Pq_Envio_Correos.generaCorreoSeguimientoTomador(v_cifTomador,
                                                                v_eMail3,
                                                                v_plzs,
                                                                v_anexos,
                                                                v_plzs_sg);                                          
				END IF;
          END IF;

        END IF;

      EXCEPTION
        WHEN NO_DATA_FOUND THEN
          PQ_UTL.LOG(lc, 'Tomador no encontrado', 2);
      END;
    END LOOP;

  END;

end PQ_SEGUIMIENTO_POLIZAS;
/

SHOW ERRORS;
