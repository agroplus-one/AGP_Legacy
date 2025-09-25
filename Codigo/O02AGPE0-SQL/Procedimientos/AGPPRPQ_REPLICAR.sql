SET DEFINE OFF;
SET SERVEROUTPUT ON;

create or replace package o02agpe0.PQ_REPLICAR is

  procedure replicarClase(LINEASEGUROID_DESTINO IN NUMBER,
                          LINEASEGUROID_ORIGEN  IN NUMBER,
                          CLASE                 IN NUMBER);

  procedure replicarErroresWs(LINEASEGUROID_DESTINO IN NUMBER,
                              LINEASEGUROID_ORIGEN  IN NUMBER,
                              SERVICIO_ORIGEN       IN VARCHAR2,
                              SERVICIO_DESTINO      IN VARCHAR2);

  procedure replicarCPMTipoCapital(LINEASEGUROID_DESTINO IN NUMBER,
                                   LINEASEGUROID_ORIGEN  IN NUMBER,
                                   P_RESULT              OUT NUMBER);

  procedure replicarTasasSbp(LINEASEGUROID_DESTINO IN NUMBER,
                             LINEASEGUROID_ORIGEN  IN NUMBER,
                             P_RESULT              OUT NUMBER);

  procedure replicarSbp(LINEASEGUROID_DESTINO IN NUMBER,
                        LINEASEGUROID_ORIGEN  IN NUMBER,
                        P_RESULT              OUT NUMBER);

  procedure replicarMtoImpuestoSbp(PLAN_DESTINO IN NUMBER,
                                   PLAN_ORIGEN  IN NUMBER,
                                   P_RESULT     OUT NUMBER);
                                   
  procedure replicarComisRenov(PLAN_ORIGEN   IN NUMBER,
                               LINEA_ORIGEN  IN NUMBER,
                               PLAN_DESTINO  IN NUMBER,
                               LINEA_DESTINO IN NUMBER,
                               USUARIO       IN VARCHAR2);    
                               
  procedure replicarDescuentos(LINEASEGUROID_DESTINO IN NUMBER,
                               LINEASEGUROID_ORIGEN  IN NUMBER,
                               CODENT_REPLICA        IN NUMBER);

  procedure replicarDescuentosHistorico(LINEASEGUROID_DESTINO IN NUMBER,
                                        USUARIO               IN VARCHAR2,
                                        CODENT_REPLICA        IN NUMBER);

  procedure replicarLineasRC(CODPLAN_ORIGEN IN NUMBER,
                             CODLINEA_ORIGEN IN NUMBER,
                             CODPLAN_DESTINO IN NUMBER,
                             CODLINEA_DESTINO IN NUMBER);

  procedure replicarDatosRC(CODPLAN_ORIGEN IN NUMBER,
                            CODLINEA_ORIGEN IN NUMBER,
                            CODPLAN_DESTINO IN NUMBER,
                            CODLINEA_DESTINO IN NUMBER);
                            
  procedure replicarDatosCultivos(CODPLAN_ORIGEN IN NUMBER,
                            CODLINEA_ORIGEN IN NUMBER,
                            CODPLAN_DESTINO IN NUMBER,
                            CODLINEA_DESTINO IN NUMBER);

  procedure replicarImpuestosRC(CODPLAN_ORIGEN  IN NUMBER,
                                CODPLAN_DESTINO IN NUMBER);

  FUNCTION getLineaGanado (idLineaSeguro IN NUMBER) RETURN NUMBER;

end PQ_REPLICAR;
/
create or replace package body o02agpe0.PQ_REPLICAR is

  PROCEDURE replicarClase (LINEASEGUROID_DESTINO IN NUMBER, LINEASEGUROID_ORIGEN IN NUMBER,CLASE IN NUMBER) IS
      TYPE  TpCursor    IS REF CURSOR;
      consulta          varchar2(2000) := 'select id, clase, descripcion, maxpolizas, comprobar_aac, rdto_historico, comprobar_rce from tb_clase c where c.lineaseguroid = ' || LINEASEGUROID_ORIGEN;
      aux_cur_clase	    TpCursor;
      v_idorigen        TB_CLASE.ID%TYPE;
      v_clase           TB_CLASE.CLASE%TYPE;
      v_descripcion     TB_CLASE.DESCRIPCION%TYPE;
      v_maxpolizas      TB_CLASE.MAXPOLIZAS%TYPE;
      v_comprobar_aac   TB_CLASE.COMPROBAR_AAC%TYPE;
      v_rdto_hist       TB_CLASE.RDTO_HISTORICO%TYPE;
	    v_comprobar_rce	  TB_CLASE.COMPROBAR_RCE%TYPE;
      v_count           NUMBER;
      x_count           NUMBER;

  BEGIN

      IF CLASE IS NOT NULL THEN
         consulta := consulta || ' and c.clase = '|| CLASE;
      END IF;
      --Bucle para recorrer las clases origen
      OPEN aux_cur_clase FOR consulta;
    LOOP
          FETCH aux_cur_clase INTO v_idorigen, v_clase, v_descripcion, v_maxpolizas, v_comprobar_aac, v_rdto_hist, v_comprobar_rce;
          EXIT WHEN aux_cur_clase%NOTFOUND;

          select count(*) INTO x_count from tb_lineas a, tb_sc_c_lineas b where a.lineaseguroid=LINEASEGUROID_ORIGEN and b.codlinea=a.codlinea
          and b.codgruposeguro='G01';

          IF x_count > 0 THEN
                    select count(d.id) INTO v_count from tb_clase_detalle_ganado d where d.idclase = v_idorigen;
          ELSE
                    select count(d.id) INTO v_count from tb_clase_detalle d where d.idclase = v_idorigen;
          END IF;

          IF v_count > 0 THEN
                --Insertar la clase en la linea de destino
                insert into tb_clase values (sq_clases.nextval, LINEASEGUROID_DESTINO, v_clase, v_descripcion, v_maxpolizas, v_comprobar_aac, v_rdto_hist, v_comprobar_rce);
                --Insertar el detalle


               IF x_count > 0 THEN
                   insert into TB_CLASE_DETALLE_GANADO (ID, IDCLASE, CODPROVINCIA, CODCOMARCA, CODTERMINO,
                   SUBTERMINO, CODMODULO, LINEASEGUROID, CODESPECIE, CODREGIMEN, CODGRUPORAZA, CODTIPOANIMAL, CODTIPOCAPITAL) (
                   select sq_clases_detalle_ganado.nextval,sq_clases.currval,CODPROVINCIA, CODCOMARCA, CODTERMINO,
                   SUBTERMINO, CODMODULO,LINEASEGUROID_DESTINO,CODESPECIE, CODREGIMEN, CODGRUPORAZA, CODTIPOANIMAL, CODTIPOCAPITAL
                   from tb_clase_detalle_ganado
                   where idclase=v_idorigen);
               ELSE
                  insert into tb_clase_detalle  (id, idclase, codcultivo, codprovincia, codcomarca, codtermino,
                  subtermino, codmodulo, codciclocultivo, codvariedad, lineaseguroid, codsistemacultivo,
                  codtipocapital, codtipoplantacion) (
                      select sq_clases_detalle.nextval, sq_clases.currval, codcultivo, codprovincia,
                      codcomarca, codtermino, subtermino, codmodulo, codciclocultivo, codvariedad,
                      LINEASEGUROID_DESTINO, codsistemacultivo, codtipocapital, codtipoplantacion
                      from tb_clase_detalle
                      where idclase = v_idorigen and codcultivo in (select distinct codcultivo
                         from tb_sc_c_cultivos where lineaseguroid = LINEASEGUROID_DESTINO));
               END IF;
               commit;
          ELSE
                pq_utl.log('No se ha copiado el detalle para la clase: '|| v_clase,'***', 2);
          END IF;
      END LOOP;
      CLOSE aux_cur_clase;

  EXCEPTION
      when others then
          pq_utl.log('Error al replicar clase. Mensaje: '||SQLERRM||', codigo: '|| SQLCODE || ' ***', 2);
           IF x_count > 0 THEN
                     delete from tb_clase_detalle_ganado where lineaseguroid = LINEASEGUROID_DESTINO;
          ELSE
                    delete from tb_clase_detalle where lineaseguroid = LINEASEGUROID_DESTINO;
          END IF;

          delete from tb_clase where lineaseguroid = LINEASEGUROID_DESTINO;
          commit;
          --Lanzamos la excepción para que de error!!
          raise;
  end replicarClase;

  ----------------------------------------------------------------------------------------------------

  PROCEDURE replicarErroresWs (LINEASEGUROID_DESTINO IN NUMBER, LINEASEGUROID_ORIGEN IN NUMBER, SERVICIO_ORIGEN IN VARCHAR2, SERVICIO_DESTINO IN VARCHAR2) IS
  
  /* P0063481 ** MODIF TAM (21.05.2021) ** Inicio */
  /* Se añaden los cambios correspondientes con el nuevo campo del catálogo */

  lc VARCHAR2(50) := 'pq_replicar.replicarErroresWs'; -- Variable que almacena el nombre del paquete y de la función

  BEGIN
     pq_utl.log(lc, '## REPLICA ERRORES WS [INI] ##', 1);
     pq_utl.log(lc, '## Replicar Errores WS del lineaseguroid ' || LINEASEGUROID_ORIGEN || ' al ' || LINEASEGUROID_DESTINO, 1);

     -- Copia los registros de la tabla 'TB_COD_ERRORES_WS_ACCION' correspondientes al plan/lÃ­nea origen al destino
     IF SERVICIO_ORIGEN IS NOT NULL AND SERVICIO_DESTINO IS NOT NULL THEN

		    pq_utl.log(lc, '## Replicar Errores WS del servicio ' || SERVICIO_ORIGEN || ' al ' || SERVICIO_DESTINO, 1);

        insert into tb_cod_errores_ws_accion (id,coderror,servicio,ocultar,lineaseguroid,codentidad, catalogo)
              (select sq_cod_error_accion.nextval,
                      ew.coderror,
                      SERVICIO_DESTINO,
                      ew.ocultar,
                      LINEASEGUROID_DESTINO,
                      ew.codentidad,
                      ew.catalogo
              from tb_cod_errores_ws_accion ew
              where ew.lineaseguroid = LINEASEGUROID_ORIGEN
              		and ew.servicio = SERVICIO_ORIGEN);
        
             insert into tb_cod_errores_perfiles
			  (id, iderroraccion, idperfil)
			  (select o02agpe0.sq_cod_error_perfil.nextval, ewa1.id, ep.idperfil
			     from o02agpe0.tb_cod_errores_ws_accion ewa1
			    inner join o02agpe0.tb_cod_errores_ws_accion ewa2 on ewa2.lineaseguroid =
			                                                         LINEASEGUROID_ORIGEN
			                                                     and ewa2.servicio =
			                                                         SERVICIO_ORIGEN
			                                                     and ewa1.coderror =
			                                                         ewa2.coderror
			                                                     and ewa1.catalogo =
			                                                         ewa2.catalogo
			                                                     and ewa1.servicio =
			                                                         ewa2.servicio
			    inner join o02agpe0.tb_cod_errores_perfiles ep on ep.iderroraccion =
			                                                      ewa2.id
			    where ewa1.lineaseguroid = LINEASEGUROID_DESTINO
			      and (ewa1.codentidad = ewa2.codentidad OR
			          (ewa2.codentidad IS NULL AND ewa1.codentidad IS NULL)));

      ELSE
         pq_utl.log(lc, '## Replicar Errores WS del servicio else', 1);

         insert into tb_cod_errores_ws_accion (id,coderror,servicio,ocultar,lineaseguroid,codentidad, catalogo)
         (select sq_cod_error_accion.nextval,
                ew.coderror,
                ew.servicio,
                ew.ocultar,
                LINEASEGUROID_DESTINO,
                ew.codentidad, 
                catalogo
         from tb_cod_errores_ws_accion ew
         where ew.lineaseguroid = LINEASEGUROID_ORIGEN);

         insert into tb_cod_errores_perfiles 
        (id, iderroraccion, idperfil) 
        (select o02agpe0.sq_cod_error_perfil.nextval, ewa1.id, ep.idperfil 
        from o02agpe0.tb_cod_errores_ws_accion ewa1 
        inner join o02agpe0.tb_cod_errores_ws_accion ewa2 on ewa2.lineaseguroid = 
                                                         LINEASEGUROID_ORIGEN 
                                                     and ewa1.coderror = 
                                                         ewa2.coderror 
                                                     and ewa1.catalogo = 
                                                         ewa2.catalogo 
                                                     and ewa1.servicio = 
                                                         ewa2.servicio 
        inner join o02agpe0.tb_cod_errores_perfiles ep on ep.iderroraccion = 
                                                      ewa2.id 
        where ewa1.lineaseguroid = LINEASEGUROID_DESTINO 
        and (ewa1.codentidad = ewa2.codentidad OR 
          (ewa2.codentidad IS NULL AND ewa1.codentidad IS NULL)));

      END IF;

     -- Commit de las transacciones
     commit;

     pq_utl.log(lc, '## REPLICA ERRORES WS [Fin]  ##', 1);

  EXCEPTION
      when others then
          pq_utl.log(lc,'Error al replicar los errores ws. Mensaje: '||SQLERRM||', codigo: '|| SQLCODE, 1);
          -- Deshace las transacciones
          rollback;
          -- Lanza una excepción para indicar que ha habido algún problema
          RAISE;

  end replicarErroresWs;

----------------------------------------------------------------------------------------------------

  PROCEDURE replicarCPMTipoCapital (LINEASEGUROID_DESTINO IN NUMBER, LINEASEGUROID_ORIGEN IN NUMBER, P_RESULT OUT NUMBER) IS

  lc VARCHAR2(50) := 'pq_replicar.replicarCPMTipoCapital'; -- Variable que almacena el nombre del paquete y de la función
  v_result number(10) := 0;

  BEGIN
     pq_utl.log(lc, '## INI ##', 1);
     pq_utl.log(lc, '## Replicar CPMTipoCapital del lineaseguroid ' || LINEASEGUROID_ORIGEN || ' al ' || LINEASEGUROID_DESTINO, 1);

     -- Copia los registros de la tabla 'TB_CPM_TIPO_CAPITAL' correspondientes al plan/línea origen
     -- cuyo codtipocapital,codcultivo y sistema de cultivo exista para el lineaseguroid destino

     insert into TB_CPM_TIPO_CAPITAL (ID,LINEASEGUROID,CODMODULO,CODCONCEPTOPPALMOD,CODTIPOCAPITAL,CODCULTIVO,CODSISTEMACULTIVO,FECHAFINGARANTIA,CODCICLOCULTIVO)
     (select sq_cpm_tipo_capital.nextval,
           LINEASEGUROID_DESTINO,
           t.codmodulo,
           t.codconceptoppalmod,
           t.codtipocapital,
           t.codcultivo,
           t.codsistemacultivo,
           t.fechafingarantia,
           t.codciclocultivo
      from TB_CPM_TIPO_CAPITAL t where
           t.lineaseguroid = LINEASEGUROID_ORIGEN and
           t.codtipocapital in (select distinct codtipocapital from vw_org_datos_tipo_capital t where t.lineaseguroid = LINEASEGUROID_DESTINO) and
           (t.codcultivo in (select codcultivo from tb_sc_c_cultivos c where c.lineaseguroid = LINEASEGUROID_DESTINO) or t.codcultivo = 999) and
           (t.codsistemacultivo in
                 (select distinct codsistemacultivo from vw_org_datos_sist_cult where lineaseguroid = LINEASEGUROID_DESTINO) or t.codsistemacultivo is null)
      );

     -- Commit de las transacciones
     commit;

     -- Comprobamos el numero de registros que se han copiado

     select count(*) into v_result from TB_CPM_TIPO_CAPITAL t where t.lineaseguroid = LINEASEGUROID_DESTINO;

     pq_utl.log(lc, '## FIN ##', 1);
     pq_utl.log(lc, '## Numero de registros copiados = '|| V_RESULT ||' ##', 1);
     p_result := v_result;


  EXCEPTION
      when others then
          pq_utl.log(lc,'Error al replicar los CPMTipoCapital. Mensaje: '||SQLERRM||', codigo: '|| SQLCODE, 1);
          -- Deshace las transacciones
          rollback;
          -- Lanza una excepción para indicar que ha habido algÃºn problema
          RAISE;

  end replicarCPMTipoCapital;

  ----------------------------------------------------------------------------------------------------

  PROCEDURE replicarTasasSbp (LINEASEGUROID_DESTINO IN NUMBER, LINEASEGUROID_ORIGEN IN NUMBER, P_RESULT OUT NUMBER) IS

  lc VARCHAR2(50) := 'pq_replicar.replicarTasasSbp'; -- Variable que almacena el nombre del paquete y de la función
  v_result number(10) := 0;

  BEGIN
     pq_utl.log(lc, '## INI ##', 1);
     pq_utl.log(lc, '## Replicar Tasas de Sbp del lineaseguroid ' || LINEASEGUROID_ORIGEN || ' al ' || LINEASEGUROID_DESTINO, 1);

     -- Copia los registros de la tabla 'TB_SBP_TASAS' correspondientes al plan/línea origen al destino
     -- siempre y cuando el cultivo exista para la linea destino
     insert into TB_SBP_TASAS (id,lineaseguroid,codprovincia,tasa_incendio,tasa_pedrisco,codcomarca,codcultivo)
      (select SQ_SBP_TASAS.nextval,
              LINEASEGUROID_DESTINO,
              t.codprovincia,
              t.tasa_incendio,
              t.tasa_pedrisco,
              t.codcomarca,
              t.codcultivo
         from TB_SBP_TASAS t
        where t.lineaseguroid = LINEASEGUROID_ORIGEN and
              (t.codcultivo in (select codcultivo from tb_sc_c_cultivos c where c.lineaseguroid = LINEASEGUROID_DESTINO) or t.codcultivo = 999)
        );

     -- Commit de las transacciones
     commit;

     -- Comprobamos el numero de registros que se han copiado

     select count(*) into v_result from TB_SBP_TASAS t where t.lineaseguroid = LINEASEGUROID_DESTINO;

     pq_utl.log(lc, '## FIN ##', 1);
     pq_utl.log(lc, '## Numero de registros copiados = '|| V_RESULT ||' ##', 1);
     p_result := v_result;

  EXCEPTION
      when others then
          pq_utl.log(lc,'Error al replicar las Tasas de Sbp. Mensaje: '||SQLERRM||', codigo: '|| SQLCODE, 1);
          -- Deshace las transacciones
          rollback;
          -- Lanza una excepción para indicar que ha habido algún problema
          RAISE;

  end replicarTasasSbp;

----------------------------------------------------------------------------------------------------

PROCEDURE replicarSbp (LINEASEGUROID_DESTINO IN NUMBER, LINEASEGUROID_ORIGEN IN NUMBER, P_RESULT OUT NUMBER) IS

  lc VARCHAR2(50) := 'pq_replicar.replicarSbp'; -- Variable que almacena el nombre del paquete y de la función
  v_result number(10) := 0;

  BEGIN
     pq_utl.log(lc, '## INI ##', 1);
     pq_utl.log(lc, '## Replicar Tasas de Sbp del lineaseguroid ' || LINEASEGUROID_ORIGEN || ' al ' || LINEASEGUROID_DESTINO, 1);

     -- Copia los registros de la tabla 'TB_SBP_SOBREPRECIO' correspondientes al plan/línea origen al destino
     -- siempre y cuando el cultivo exista para la linea destino
     insert into TB_SBP_SOBREPRECIO (id,lineaseguroid,codprovincia,codcultivo,precio_minimo,precio_maximo,codtipocapital)
      (select SQ_SBP_SOBREPRECIO.nextval,
              LINEASEGUROID_DESTINO,
              s.codprovincia,
              s.codcultivo,
              s.precio_minimo,
              s.precio_maximo,
              s.codtipocapital
         from TB_SBP_SOBREPRECIO s
        where s.lineaseguroid = LINEASEGUROID_ORIGEN
              and (s.codcultivo in (select codcultivo from tb_sc_c_cultivos c where c.lineaseguroid = LINEASEGUROID_DESTINO) or s.codcultivo = 999)
              and (s.codtipocapital in (select codvalor from VW_ORG_DATOS_POR_FACTORES  fac where fac.lineaseguroid = LINEASEGUROID_DESTINO and fac.codconcepto = 126))
        );

     -- Commit de las transacciones
     commit;

      -- Comprobamos el numero de registros que se han copiado

     select count(*) into v_result from TB_SBP_SOBREPRECIO s where s.lineaseguroid = LINEASEGUROID_DESTINO;

     pq_utl.log(lc, '## FIN ##', 1);
     pq_utl.log(lc, '## Numero de registros copiados = '|| V_RESULT ||' ##', 1);
     p_result := v_result;

  EXCEPTION
      when others then
          pq_utl.log(lc,'Error al replicar los Sbp. Mensaje: '||SQLERRM||', codigo: '|| SQLCODE, 1);
          dbms_output.put_line('SQLERRM: ' ||SQLERRM);
          -- Deshace las transacciones
          rollback;
          -- Lanza una excepción para indicar que ha habido algún problema
          RAISE;

  end replicarSbp;

----------------------------------------------------------------------------------------------------

PROCEDURE replicarMtoImpuestoSbp (PLAN_DESTINO IN NUMBER, PLAN_ORIGEN IN NUMBER, P_RESULT OUT NUMBER) IS

  lc VARCHAR2(50) := 'pq_replicar.replicarMtoImpuestoSbp'; -- Variable que almacena el nombre del paquete y de la función
  v_result number(10) := 0;

  BEGIN
     pq_utl.log(lc, '## INI ##', 1);
     pq_utl.log(lc, '## Replicar MtoImpuesto de Sbp del plan ' || PLAN_ORIGEN || ' al ' || PLAN_DESTINO, 1);

     -- Copia los registros de la tabla 'tb_sbp_mto_impuestos' correspondientes al plan origen al destino

     insert into tb_sbp_mto_impuestos (id,codplan,idimpuesto,idbase,valor)
      (select SQ_SBP_MTO_IMPUESTOS.nextval,
              PLAN_DESTINO,
              s.idimpuesto,
              s.idbase,
              s.valor
        from tb_sbp_mto_impuestos s
        where s.codplan = PLAN_ORIGEN);

     -- Commit de las transacciones
     commit;

      -- Comprobamos el numero de registros que se han copiado

     select count(*) into v_result from tb_sbp_mto_impuestos s where s.codplan = PLAN_DESTINO;

     pq_utl.log(lc, '## FIN ##', 1);
     pq_utl.log(lc, '## Numero de registros copiados = '|| V_RESULT ||' ##', 1);
     p_result := v_result;

  EXCEPTION
      when others then
          pq_utl.log(lc,'Error al replicar los mtoImpuestoSbp. Mensaje: '||SQLERRM||', codigo: '|| SQLCODE, 1);
          -- Deshace las transacciones
          rollback;
          -- Lanza una excepción para indicar que ha habido algún problema
          RAISE;

  end replicarMtoImpuestoSbp;

----------------------------------------------------------------------------------------------------

PROCEDURE replicarComisRenov(PLAN_ORIGEN IN NUMBER,
                               LINEA_ORIGEN IN NUMBER,
                               PLAN_DESTINO IN NUMBER,
                               LINEA_DESTINO IN NUMBER,
                               USUARIO       IN VARCHAR2) IS

    TYPE  TpCursor    IS REF CURSOR;
    lc VARCHAR2(50) := 'pq_replicar.replicarComisRenov'; -- Variable que almacena el nombre del paquete y de la función

    comisRenov  varchar2(2000) := 'select coms.idgrupo, coms.id
                                     from o02agpe0.tb_coms_renov_esmed coms
                                    where coms.codplan = ' || PLAN_ORIGEN ||
                                  ' and coms.codlinea = ' || LINEA_ORIGEN ||
                                  ' order by idgrupo';

    idLineaSeguro        NUMBER;
    v_id                 NUMBER;
    v_idgrupo            VARCHAR2(1);
    esLineaGanado        NUMBER(1);
    aux_cur_ComisRenov	 TpCursor;


  BEGIN
    pq_utl.log(lc, '## INI ##', 1);
    pq_utl.log(lc,
               '## Replicar Comisiones de Renovables del Plan/Linea ' || PLAN_ORIGEN || '/' || LINEA_ORIGEN ||
               ' al Plan/Linea Destino ' || PLAN_DESTINO || '/' || LINEA_DESTINO , 1);

    /* Obtenemos si el Plan/Línea destino es de ganado o no */
    pq_utl.log(lc, '## Obtenemos si el Plan/Línea Destino es de ganado o no (0-NO, 1-SI)', 1);

    select lin.lineaseguroid
      INTO idLineaSeguro
      from o02agpe0.tb_lineas lin
     where lin.codlinea = LINEA_DESTINO
       and lin.codplan = PLAN_DESTINO;

     esLineaGanado:=getLineaGanado(idLineaSeguro);
     pq_utl.log(lc, '## esLineaGanado:' || esLineaGanado, 1);


    -- Validamos que el grupo de Negocio del Plan/Línea Origen sea permitido para el Plan/Linea Destino.
    pq_utl.log(lc, '## Validamos primero el grupo de Negocio para el Plan/Línea destino', 1);
    OPEN aux_cur_ComisRenov
     FOR comisRenov;

    LOOP
          FETCH aux_cur_ComisRenov
           INTO v_idgrupo, v_id;

          pq_utl.log(lc, '## Obtenemos el idgrupo de Origen: ' || v_idgrupo , 1);

          EXIT WHEN aux_cur_ComisRenov%NOTFOUND;

             /* Si no es Ganado y el idGrupo de Origen es <> 1 -> No se graba registro */
             IF  esLineaGanado = 0 AND v_idgrupo <> 1 THEN
                 pq_utl.log(lc, '## No se graba registro por que no es valido el Grupo de Negocio: ' || v_idgrupo ||
                                ' para el Plan/Linea Destino: ' || PLAN_DESTINO || '/' || LINEA_DESTINO , 1);
             ELSE

                 /* Si el Plan/Linea Destino es Línea de Ganado y el Grupo de Negocio es 1, se permite la replica */
               pq_utl.log(lc, '## Se Graba registro por que el Grupo de Negocio cumple la validacion', 1);
                 /* Si no no se hace nada, ni se retorna error */
               insert into TB_COMS_RENOV_ESMED
                    (id,
                     codplan,
                     codlinea,
                     codentidad,
                     codentmed,
                     codsubmed,
                     idgrupo,
                     refimporte,
                     imp_desde,
                     imp_hasta,
                     comision,
                     fecha_modif,
                     usuario_modif,
					 codmodulo)
                    (select sq_coms_renov_esmed.nextval,
                            PLAN_DESTINO,
                            LINEA_DESTINO,
                            d.codentidad,
                            d.codentmed,
                            d.codsubmed,
                            d.idgrupo,
                            d.refimporte,
                            d.imp_desde,
                            d.imp_hasta,
                            d.comision,
                            d.fecha_modif,
                            USUARIO,
							codmodulo
                       from tb_coms_renov_esmed d
                      where d.id = V_ID);
             END IF;

    END LOOP;

    CLOSE aux_cur_ComisRenov;


    -- Commit de las transacciones
    commit;

    pq_utl.log(lc, '## FIN ##', 1);

  EXCEPTION
    when others then
      pq_utl.log(lc,
                 'Error al replicar las Comisiones Renovables. Mensaje: ' || SQLERRM ||
                 ', codigo: ' || SQLCODE,
                 1);
      -- Deshace las transacciones
      rollback;
      -- Lanza una excepción para indicar que ha habido algún problema
      RAISE;

  end replicarComisRenov;

----------------------------------------------------------------------------------------------------

PROCEDURE replicarDescuentos (LINEASEGUROID_DESTINO IN NUMBER, LINEASEGUROID_ORIGEN IN NUMBER, CODENT_REPLICA IN NUMBER) IS

  lc VARCHAR2(50) := 'pq_replicar.replicarDescuentos'; -- Variable que almacena el nombre del paquete y de la función

  BEGIN
     pq_utl.log(lc, '## INI ##', 1);
     pq_utl.log(lc, '## Replicar Descuentos del lineaseguroid ' || LINEASEGUROID_ORIGEN || ' al ' || LINEASEGUROID_DESTINO || ' de la entidad ' || CODENT_REPLICA, 1);

     insert into tb_coms_descuentos
             (id, codent, codentmed, codsubentmed, codoficina, delegacion,
             pct_desc_max, fecha_baja, permitir_recargo, ver_comisiones, lineaseguroid)
      (select sq_coms_descuentos.nextval,  d.codent, d.codentmed, d.codsubentmed, d.codoficina, d.delegacion,
              d.pct_desc_max, d.fecha_baja, d.permitir_recargo, d.ver_comisiones, LINEASEGUROID_DESTINO
              from tb_coms_descuentos d
              where d.lineaseguroid = LINEASEGUROID_ORIGEN and d.codent = CODENT_REPLICA);

     -- Commit de las transacciones
     commit;

     pq_utl.log(lc, '## FIN ##', 1);

  EXCEPTION
      when others then
          pq_utl.log(lc,'Error al replicar los Descuentos. Mensaje: '||SQLERRM||', codigo: '|| SQLCODE, 1);
          -- Deshace las transacciones
          rollback;
          -- Lanza una excepción para indicar que ha habido algún problema
          RAISE;

  end replicarDescuentos;

  -------------------------------------------------------------------------------------------------------------

PROCEDURE replicarDescuentosHistorico(LINEASEGUROID_DESTINO IN NUMBER, USUARIO IN VARCHAR2, CODENT_REPLICA IN NUMBER) IS

  lc VARCHAR2(50) := 'pq_replicar.replicarDescuentosHistorico'; -- Variable que almacena el nombre del paquete y de la función

  BEGIN
     pq_utl.log(lc, '## INI ##', 1);
     pq_utl.log(lc, '## Alta en Descuentos en histórico de la lineaseguroid ' || LINEASEGUROID_DESTINO ||  ' de la entidad ' || CODENT_REPLICA || ' por réplica ', 1);

     insert into tb_coms_descuentos_hist
             (id, iddesc, codent, codentmed, codsubentmed, codoficina, delegacion,
             pct_desc_max, operacion, fecha, usuario, permitir_recargo, ver_comisiones, lineaseguroid)
     (select sq_coms_descuentos_hist.nextval, d.id,  d.codent, d.codentmed, d.codsubentmed, d.codoficina, d.delegacion,
              d.pct_desc_max, 0, SYSDATE, USUARIO, d.permitir_recargo, d.ver_comisiones, d.lineaseguroid
              from tb_coms_descuentos d
              where d.lineaseguroid = LINEASEGUROID_DESTINO and d.codent = CODENT_REPLICA);


     -- Commit de las transacciones
     commit;

     pq_utl.log(lc, '## FIN ##', 1);

  EXCEPTION
      when others then
          pq_utl.log(lc,'Error al replicar los Descuentos en el histórico. Mensaje: '||SQLERRM||', codigo: '|| SQLCODE, 1);
          -- Deshace las transacciones
          rollback;
          -- Lanza una excepción para indicar que ha habido algún problema
          RAISE;

  end replicarDescuentosHistorico;

-------------------------------------------------------------------------------------------------------------

PROCEDURE replicarLineasRC(CODPLAN_ORIGEN   IN NUMBER,
                           CODLINEA_ORIGEN  IN NUMBER,
                           CODPLAN_DESTINO  IN NUMBER,
                           CODLINEA_DESTINO IN NUMBER) IS

  lc VARCHAR2(50) := 'pq_replicar.replicarLineasRC'; -- Variable que almacena el nombre del paquete y de la función

  CURSOR lineasRC_origen_cur IS
    SELECT LRC.CODESPECIE,
           LRC.CODREGIMEN,
           LRC.CODTIPOCAPITAL,
           LRC.CODESPECIE_RC
      FROM TB_RC_LINEAS LRC, TB_LINEAS L
     WHERE LRC.LINEASEGUROID = L.LINEASEGUROID
       AND L.CODLINEA = CODLINEA_ORIGEN
       AND L.CODPLAN = CODPLAN_ORIGEN;

  codEspecie_aux     TB_RC_LINEAS.CODESPECIE%TYPE;
  codRegimen_aux     TB_RC_LINEAS.CODREGIMEN%TYPE;
  codTipoCapital_aux TB_RC_LINEAS.CODTIPOCAPITAL%TYPE;
  codEspecieRC_aux   TB_RC_LINEAS.CODESPECIE_RC%TYPE;

  codEspecie     TB_SC_C_ESPECIE.CODESPECIE%TYPE;
  codRegimen     TB_SC_C_REGIMEN_MANEJO.CODREGIMEN%TYPE;
  codTipoCapital VW_ORG_DATOS_POR_FACTORES.CODVALOR%TYPE;

  lDestinoId TB_LINEAS.LINEASEGUROID%TYPE;

BEGIN
  pq_utl.log(lc, '## INI ##', 1);
  pq_utl.log(lc,
             '## Alta en Líneas para RC para la línea ' || CODPLAN_DESTINO || '/' ||
             CODLINEA_DESTINO || ' por réplica ',
             1);

  SELECT l.lineaseguroid
    INTO lDestinoId
    FROM TB_LINEAS l
   WHERE l.codplan = CODPLAN_DESTINO
     AND l.codlinea = CODLINEA_DESTINO;

  OPEN lineasRC_origen_cur;
  LOOP
    FETCH lineasRC_origen_cur
      INTO codEspecie_aux, codRegimen_aux, codTipoCapital_aux, codEspecieRC_aux;
    EXIT WHEN lineasRC_origen_cur%NOTFOUND;

    BEGIN

      -- COMPROBACIÓN VALIDEZ DE DATOS EN DESTINO (ESPECIE)
      SELECT E.CODESPECIE
        INTO codEspecie
        FROM TB_SC_C_ESPECIE E
       WHERE E.CODESPECIE = codEspecie_aux
         AND E.LINEASEGUROID = lDestinoId;
      -- COMPROBACIÓN VALIDEZ DE DATOS EN DESTINO (REGIMEN)
      SELECT R.CODREGIMEN
        INTO codRegimen
        FROM TB_SC_C_REGIMEN_MANEJO R
       WHERE R.CODREGIMEN = codRegimen_aux
         AND R.LINEASEGUROID = lDestinoId;
      -- COMPROBACIÓN VALIDEZ DE DATOS EN DESTINO (TIPO CAPITAL)
      IF codTipoCapital_aux <> 999 THEN
        SELECT DISTINCT V.CODVALOR
          INTO codTipoCapital
          FROM VW_ORG_DATOS_POR_FACTORES V
         WHERE V.CODVALOR = codTipoCapital_aux
           AND V.CODCONCEPTO = 126
           AND V.LINEASEGUROID = lDestinoId;
      END IF;

      INSERT INTO TB_RC_LINEAS
        (ID,
         LINEASEGUROID,
         CODESPECIE,
         CODREGIMEN,
         CODTIPOCAPITAL,
         CODESPECIE_RC)
      VALUES
        (SQ_RC_LINEAS.NEXTVAL,
         lDestinoId,
         codEspecie_aux,
         codRegimen_aux,
         codTipoCapital_aux,
         codEspecieRC_aux);

    EXCEPTION
      WHEN NO_DATA_FOUND THEN

        pq_utl.log(lc,
                   'Inserción ignorada al no aplicar los datos a la línea destino.',
                   1);
    END;
  END LOOP;

  -- Commit de las transacciones
  commit;

  pq_utl.log(lc, '## FIN ##', 1);

EXCEPTION
  when others then
    pq_utl.log(lc,
               'Error al replicar las líneas para RC. Mensaje: ' || SQLERRM ||
               ', codigo: ' || SQLCODE,
               1);
    -- Deshace las transacciones
    rollback;
    -- Lanza una excepción para indicar que ha habido algún problema
    RAISE;

END replicarLineasRC;

-------------------------------------------------------------------------------------------------------------

PROCEDURE replicarDatosRC(CODPLAN_ORIGEN   IN NUMBER,
                          CODLINEA_ORIGEN  IN NUMBER,
                          CODPLAN_DESTINO  IN NUMBER,
                          CODLINEA_DESTINO IN NUMBER) IS

  lc VARCHAR2(50) := 'pq_replicar.replicarDatosRC'; -- Variable que almacena el nombre del paquete y de la función

  lDestinoId TB_LINEAS.LINEASEGUROID%TYPE;

BEGIN
  pq_utl.log(lc, '## INI ##', 1);
  pq_utl.log(lc,
             '## Alta en Datos para RC para la línea ' || CODPLAN_DESTINO || '/' ||
             CODLINEA_DESTINO || ' por réplica ',
             1);

  SELECT l.lineaseguroid
    INTO lDestinoId
    FROM TB_LINEAS l
   WHERE l.codplan = CODPLAN_DESTINO
     AND l.codlinea = CODLINEA_DESTINO;

  INSERT INTO TB_RC_DATOS
    (ID,
     LINEASEGUROID,
     CODENT_MED,
     CODSUBENT_MED,
     CODESPECIE_RC,
     CODREGIMEN_RC,
     CODSUMA_RC,
     TASA,
     FRANQUICIA,
     PRIMA_MINIMA)
    SELECT SQ_RC_DATOS.NEXTVAL,
           lDestinoId,
           DRC.CODENT_MED,
           DRC.CODSUBENT_MED,
           DRC.CODESPECIE_RC,
           DRC.CODREGIMEN_RC,
           DRC.CODSUMA_RC,
           DRC.TASA,
           DRC.FRANQUICIA,
           DRC.PRIMA_MINIMA
      FROM TB_RC_DATOS DRC, TB_LINEAS L
     WHERE DRC.LINEASEGUROID = L.LINEASEGUROID
       AND L.CODLINEA = CODLINEA_ORIGEN
       AND L.CODPLAN = CODPLAN_ORIGEN;

  -- Commit de las transacciones
  commit;

  pq_utl.log(lc, '## FIN ##', 1);

EXCEPTION
  when others then
    pq_utl.log(lc,
               'Error al replicar los datos para RC. Mensaje: ' || SQLERRM ||
               ', codigo: ' || SQLCODE,
               1);
    -- Deshace las transacciones
    rollback;
    -- Lanza una excepción para indicar que ha habido algún problema
    RAISE;

END replicarDatosRC;

--------------------------------------------------------------------------------------------------------------

PROCEDURE replicarDatosCultivos(CODPLAN_ORIGEN   IN NUMBER,
                          CODLINEA_ORIGEN  IN NUMBER,
                          CODPLAN_DESTINO  IN NUMBER,
                          CODLINEA_DESTINO IN NUMBER) IS

  lc VARCHAR2(50) := 'pq_replicar.replicarDatosCultivos'; -- Variable que almacena el nombre del paquete y de la función

  lDestinoId TB_LINEAS.LINEASEGUROID%TYPE;

BEGIN
  pq_utl.log(lc, '## INI ##', 1);
  pq_utl.log(lc,
             '## Alta en Datos para Cultivos para la línea ' || CODPLAN_DESTINO || '/' ||
             CODLINEA_DESTINO || ' por réplica ',
             1);

  SELECT l.lineaseguroid
    INTO lDestinoId
    FROM TB_LINEAS l
   WHERE l.codplan = CODPLAN_DESTINO
     AND l.codlinea = CODLINEA_DESTINO;

  INSERT INTO TB_COMS_CULTIVOS_ENTIDADES
    (ID,
     LINEASEGUROID,
     PCTGENERALENTIDAD,
     PCTRGA,
     FECHAMODIFICACION,
     CODUSUARIO,
     PCTADQUISICION,
     FECHA_EFECTO,
     FEC_BAJA,
     GRUPO_NEGOCIO,
     ENTMEDIADORA,
     SUBENTMEDIADORA)
    SELECT SQ_RC_DATOS.NEXTVAL,
           lDestinoId,
           C.PCTGENERALENTIDAD,
           C.PCTRGA,
           C.FECHAMODIFICACION,
           C.CODUSUARIO,
           C.PCTADQUISICION,
           C.FECHA_EFECTO,
           C.FEC_BAJA,
           C.GRUPO_NEGOCIO,
           C.ENTMEDIADORA,
           C.SUBENTMEDIADORA
      FROM TB_COMS_CULTIVOS_ENTIDADES C, TB_LINEAS L
     WHERE C.LINEASEGUROID = L.LINEASEGUROID
       AND L.CODLINEA = CODLINEA_ORIGEN
       AND L.CODPLAN = CODPLAN_ORIGEN;

  -- Commit de las transacciones
  commit;

  pq_utl.log(lc, '## FIN ##', 1);

EXCEPTION
  when others then
    pq_utl.log(lc,
               'Error al replicar los datos para Cultivos. Mensaje: ' || SQLERRM ||
               ', codigo: ' || SQLCODE,
               1);
    -- Deshace las transacciones
    rollback;
    -- Lanza una excepción para indicar que ha habido algún problema
    RAISE;

END replicarDatosCultivos;

--------------------------------------------------------------------------------------------------------------

PROCEDURE replicarImpuestosRC(CODPLAN_ORIGEN  IN NUMBER,
                                CODPLAN_DESTINO IN NUMBER) IS

    lc VARCHAR2(50) := 'pq_replicar.replicarImpuestosRC'; -- Variable que almacena el nombre del paquete y de la función

  BEGIN
    pq_utl.log(lc, '## INI ##', 1);
    pq_utl.log(lc,
               '## Alta en Impuestos para RC para el plan ' ||
               CODPLAN_DESTINO || ' por réplica ',
               1);

    INSERT INTO TB_RC_IMPUESTOS
      (ID, CODPLAN, IDIMPUESTO, IDBASE, VALOR)
      SELECT SQ_RC_IMPUESTOS.NEXTVAL,
             CODPLAN_DESTINO,
             RCI.IDIMPUESTO,
             RCI.IDBASE,
             RCI.VALOR
        FROM TB_RC_IMPUESTOS RCI
       WHERE RCI.CODPLAN = CODPLAN_ORIGEN;
    COMMIT;

  EXCEPTION
    WHEN OTHERS THEN
      pq_utl.log(lc,
                 'Error al replicar los impuestos para RC. Mensaje: ' ||
                 SQLERRM || ', codigo: ' || SQLCODE,
                 1);
      -- Deshace las transacciones
      ROLLBACK;
      -- Lanza una excepción para indicar que ha habido algún problema
      RAISE;

END replicarImpuestosRC;

-------------------------------------------------------------------------------------------------------------

FUNCTION getLineaGanado (idLineaSeguro IN NUMBER) return NUMBER IS
   tipoLinea        TB_SC_C_LINEAS.CODGRUPOSEGURO%TYPE;
   BEGIN

  		--Calcular si la línea es de agro o(A01) de ganado (G01)
    select CODGRUPOSEGURO
      into tipoLinea
      from TB_SC_C_LINEAS sclin
     where sclin.codlinea =
           (select codlinea
              from tb_lineas
             where lineaseguroid = idLineaSeguro);

       IF tipoLinea = 'A01' THEN
          RETURN 0;
       ELSIF tipoLinea = 'G01' THEN
          RETURN 1;
       END IF;
END getLineaGanado;


END PQ_REPLICAR;
/
SHOW ERRORS;