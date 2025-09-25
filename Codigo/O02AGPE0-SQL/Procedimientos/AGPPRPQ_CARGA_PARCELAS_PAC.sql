SET DEFINE OFF;
SET SERVEROUTPUT ON;
create or replace package o02agpe0.PQ_CARGA_PARCELAS_PAC is

  -- Author  :  U028606
  -- Created : 18/10/2016 9:49:36
  --- Purpose : Copia de parcelas de PAC a parcelas de p??liza

  procedure cargaParcelasPolizaDesdePAC (p_idPoliza IN NUMBER, p_idClase IN NUMBER, p_listaIdPacAseg IN VARCHAR2, p_listaDVDefecto IN VARCHAR2, p_error OUT VARCHAR2);

end PQ_CARGA_PARCELAS_PAC;
/
create or replace package body o02agpe0.PQ_CARGA_PARCELAS_PAC is

  type t_array is TABLE OF VARCHAR2(50)INDEX BY BINARY_INTEGER;

  --
  -- Devuelve el c??digo de tipo de capital que se asignar?!n a todas las parcelas de PAC que se carguen
  -- como parcelas de p??liza
  ---
	FUNCTION getTipoCapital (p_idPoliza IN NUMBER) RETURN NUMBER IS

  lc VARCHAR2(100) := 'PQ_CARGA_PARCELAS_PAC.getTipoCapital - ';
  v_codtipocapital tb_sc_c_factores.codconcepto%TYPE;

  BEGIN

   select min(f.valorconcepto)
   into v_codtipocapital
   from TB_SC_C_FACTORES f, tb_modulos_poliza mp
  where mp.idpoliza = p_idPoliza
    and f.codconcepto = 126
    and f.lineaseguroid = mp.lineaseguroid
    and f.codmodulo = mp.codmodulo;

  RETURN v_codtipocapital;

  EXCEPTION

  WHEN OTHERS THEN
       PQ_UTL.log(lc, 'Error al obtener el tipo de capital que se asignar?! a todas las parcelas a cargar ', 2);
       PQ_UTL.log(lc, 'ERROR -- SQLCODE:' || SQLCODE || ' SQLERRM: ' || SQLERRM ||'    ', 2);
       RETURN -1;

  END getTipoCapital;

  --
  -- Devuelve la lista de c??digos de concepto separados por ',' correspondientes a los datos variables
  -- configurados en el configurador de pantallas para el plan/l?-nea asociado a la p??liza.
  -- Devolver?! NULL si no hay DV configurados para el plan/l?-nea o si ocurre alg??n error en el proceso
  --
	FUNCTION getListaDVPantalla (p_idPoliza IN NUMBER) RETURN VARCHAR2 IS

  lc VARCHAR2(100) := 'PQ_CARGA_PARCELAS_PAC.getListaDVPantalla - ';
  v_listaDV VARCHAR2(32767);
  v_idPantalla_Parcela NUMBER := 7; -- Identificador del tipo de pantalla 'Confecci??n de p??liza'

  BEGIN

  select rtrim(xmlagg(xmlelement(e, codconcepto || ',')).extract('//text()'), ',')
  INTO v_listaDV
  from (select distinct c.codconcepto
          from tb_polizas                 pol,
               tb_pantallas_configurables p,
               tb_configuracion_campos    c
         where pol.idpoliza = p_idPoliza
           and p.idpantalla = v_idPantalla_Parcela
           and p.lineaseguroid = pol.lineaseguroid
           and p.idpantallaconfigurable = c.idpantallaconfigurable
           and c.lineaseguroid = pol.lineaseguroid);

  IF (TRIM (v => v_listaDV) IS NULL) THEN RETURN NULL; END IF;

  RETURN v_listaDV;

  EXCEPTION

  WHEN OTHERS THEN
       PQ_UTL.log(lc, 'Error al obtener los datos variables configurados en el configurador de pantallas para el plan/l?-nea asociado a la p??liza', 2);
       PQ_UTL.log(lc, 'ERROR -- SQLCODE:' || SQLCODE || ' SQLERRM: ' || SQLERRM ||'    ', 2);
       RETURN NULL;

  END getListaDVPantalla;


  --
  --
  --
  PROCEDURE cargaFiltrosPorClase (p_idClase IN NUMBER, v_prov_clase OUT VARCHAR2, v_comarca_clase OUT VARCHAR2, v_term_clase OUT VARCHAR2, v_subt_clase OUT VARCHAR2) IS

  lc VARCHAR2(100) := 'PQ_CARGA_PARCELAS_PAC.cargaFiltrosPorClase - ';

  BEGIN

  -- Provincia
  SELECT rtrim(xmlagg(xmlelement(e, codprovincia || ',')).extract('//text()'), ',')
  INTO v_prov_clase
  FROM (SELECT distinct cd.codprovincia as codprovincia
          FROM tb_clase_detalle cd
         WHERE cd.idclase = p_idClase
           AND NOT EXISTS (SELECT cd2.id
                  FROM tb_clase_detalle cd2
                 WHERE cd2.idclase = p_idClase
                   AND cd2.codprovincia = 99));

  -- Comarca
  SELECT rtrim(xmlagg(xmlelement(e, codcomarca || ',')).extract('//text()'), ',')
  INTO v_comarca_clase
  FROM (SELECT distinct cd.codcomarca as codcomarca
          FROM tb_clase_detalle cd
         WHERE cd.idclase = p_idClase
           AND NOT EXISTS (select cd2.id
                  from tb_clase_detalle cd2
                 where cd2.idclase = p_idClase
                   and cd2.codcomarca = 99));

  -- T??rmino
  SELECT rtrim(xmlagg(xmlelement(e, codtermino || ',')).extract('//text()'), ',')
  INTO v_term_clase
  FROM (SELECT distinct cd.codtermino as codtermino
          FROM tb_clase_detalle cd
         WHERE cd.idclase = p_idClase
           AND NOT EXISTS (select cd2.id
                  FROM tb_clase_detalle cd2
                 WHERE cd2.idclase = p_idClase
                   AND cd2.codtermino = 999));

  -- Subt??rmino
  SELECT replace(rtrim(xmlagg(xmlelement(e, '#' || subtermino || '#,')).extract('//text()'), ','), '#', '''')
  INTO v_subt_clase
  FROM (SELECT distinct cd.subtermino as subtermino
          FROM tb_clase_detalle cd
         WHERE cd.idclase = p_idClase
           AND NOT EXISTS (SELECT cd2.id
                  FROM tb_clase_detalle cd2
                 WHERE cd2.idclase = p_idClase
                   AND cd2.subtermino = '9'));

  IF (TRIM (v => v_prov_clase) IS NULL) THEN
     v_prov_clase := NULL;
  ELSE
     PQ_UTL.log(lc, 'Se cargar?!n las parcelas cuya provincia est?? incluida en (' || LENGTH (TRIM (v => v_prov_clase)) || ')', 2);
  END IF;

  IF (TRIM (v => v_comarca_clase) IS NULL) THEN
     v_comarca_clase := NULL;
  ELSE
     PQ_UTL.log(lc, 'Se cargar?!n las parcelas cuya comarca est?? incluida en (' || v_comarca_clase || ')', 2);
  END IF;

  IF (TRIM (v => v_term_clase) IS NULL) THEN
     v_term_clase := NULL;
  ELSE
     PQ_UTL.log(lc, 'Se cargar?!n las parcelas cuyo t??rmino est?? incluido en (' || v_term_clase || ')', 2);
  END IF;

  IF (TRIM (v => v_subt_clase) IS NULL) THEN
     v_subt_clase := NULL;
  ELSE
     PQ_UTL.log(lc, 'Se cargar?!n las parcelas cuyo subt??rmino est?? incluido en (' || v_subt_clase || ')', 2);
  END IF;


  EXCEPTION

  WHEN OTHERS THEN
       PQ_UTL.log(lc, 'Error al obtener los filtros de ubicaci??n por clase', 2);
       PQ_UTL.log(lc, 'ERROR -- SQLCODE:' || SQLCODE || ' SQLERRM: ' || SQLERRM ||'    ', 2);
       v_prov_clase := null;
       v_comarca_clase := null;
       v_term_clase := null;
       v_subt_clase := null;

  END cargaFiltrosPorClase;

  --
  --
  --
  PROCEDURE cargaParcelas (p_idPoliza IN NUMBER, idClase IN NUMBER, listaIdPacAseg IN VARCHAR2) IS

  lc VARCHAR2(100) := 'PQ_CARGA_PARCELAS_PAC.cargaParcelas - ';
  v_consulta VARCHAR2(32767);
  v_consulta_clase VARCHAR2(32767);

  prov_clase VARCHAR2(32767);
  comarca_clase VARCHAR2(32767);
  term_clase VARCHAR2(32767);
  subt_clase VARCHAR2(32767);

  BEGIN

  PQ_UTL.log(lc, 'Inicio del volcado de parcelas para idPoliza: ' || p_idPoliza || ', idClase:' || idClase || ', listaIdPacAseg: ' || listaIdPacAseg, 2);

  -- Carga los filtros de ubicaci??n que marque la clase asociada a la p??liza
  cargaFiltrosPorClase (p_idClase => idClase, v_prov_clase => prov_clase, v_comarca_clase => comarca_clase, v_term_clase => term_clase, v_subt_clase => subt_clase);

  v_consulta := 'INSERT INTO TB_PARCELAS
				  (IDPARCELA                 ,
				  IDPOLIZA                  ,
				  CODPROVINCIA              ,
				  CODTERMINO                ,
				  SUBTERMINO                ,
				  POLIGONO                  ,
				  PARCELA                   ,
				  CODPROVSIGPAC             ,
				  CODTERMSIGPAC             ,
				  AGRSIGPAC                 ,
				  ZONASIGPAC                ,
				  POLIGONOSIGPAC            ,
				  PARCELASIGPAC             ,
				  RECINTOSIGPAC             ,
				  NOMPARCELA                ,
				  CODCULTIVO                ,
				  CODVARIEDAD               ,
				  HOJA                      ,
				  NUMERO                    ,
				  CODCOMARCA                ,
				  LINEASEGUROID             ,
				  IDPARCELAESTRUCTURA       ,
				  TIPOPARCELA               ,
				  ALTAENCOMPLEMENTARIO      ,
				  IND_RECALCULO_HOJA_NUMERO,
				  IDPARCELA_PAC)
				SELECT SQ_PARCELAS.NEXTVAL,
				  ' || p_idPoliza || ',
				  PROVINCIA,
				  TERMINO,
				  PAR.SUBTERMINO,
				  PAR.POLIGONO,
				  PAR.PARCELA,
				  PROVINCIA_SIGPAC,
				  TERMINO_SIGPAC,
				  AGREGADO_SIGPAC,
				  ZONA_SIGPAC,
				  POLIGONO_SIGPAC,
				  PARCELA_SIGPAC,
				  RECINTO_SIGPAC,
				  PAR.NOMBRE,
				  PAR.CULTIVO,
				  PAR.VARIEDAD,
				  NULL,
				  NULL,
				  COMARCA,
				  NULL,
				  NULL,
				  ''P'',
				  ''N'',
				  NULL,
				  PAR.ID
				FROM TB_PAC_PARCELAS par
					 WHERE par.id_pac_aseg IN (' || listaIdPacAseg || ')';


  -- Filtros por ubicaci??n por clase
  IF (prov_clase IS NOT NULL) THEN v_consulta_clase:= v_consulta_clase || ' AND par.PROVINCIA IN (' || prov_clase || ')'; END IF;
  IF (comarca_clase IS NOT NULL) THEN v_consulta_clase:= v_consulta_clase || ' AND par.COMARCA IN (' || comarca_clase || ')'; END IF;
  IF (term_clase IS NOT NULL) THEN v_consulta_clase:= v_consulta_clase || ' AND par.TERMINO IN (' || term_clase || ')'; END IF;
  IF (subt_clase IS NOT NULL) THEN v_consulta_clase:= v_consulta_clase || ' AND par.SUBTERMINO IN (' || subt_clase || ')'; END IF;

  EXECUTE IMMEDIATE v_consulta || v_consulta_clase ;

  COMMIT;
  PQ_UTL.log(lc, 'Volcado finalizado OK', 2);


  END cargaParcelas;


  --
  --
  --
  PROCEDURE cargaCapitalesAsegurados (listaIdPacAseg IN VARCHAR2, codTipoCapital IN NUMBER) IS

  lc VARCHAR2(100) := 'PQ_CARGA_PARCELAS_PAC.cargaCapitalesAsegurados - ';
  v_consulta VARCHAR2(32767);

  BEGIN

  PQ_UTL.log(lc, 'Inicio del volcado de capitales asegurados para listaIdPacAseg: ' || listaIdPacAseg || ', codTipoCapital: ' || codTipoCapital, 2);

  v_consulta := 'INSERT INTO TB_CAPITALES_ASEGURADOS
                 (IDPARCELA            ,
                  IDCAPITALASEGURADO   ,
                  CODTIPOCAPITAL       ,
                  SUPERFICIE           ,
                  PRECIO               ,
                  PRODUCCION           ,
                  PRECIOMODIF          ,
                  PRODUCCIONMODIF      ,
                  ALTAENCOMPLEMENTARIO ,
                  INCREMENTOPRODUCCION ,
                  IDPARCELA_PAC)
                SELECT
                  (SELECT idparcela FROM tb_parcelas WHERE IDPARCELA_PAC = pac_cap.id_pac_parcela),
                  sq_capitales_asegurados.NEXTVAL,
                  ' || codTipoCapital || ',
                  to_number (pac_cap.superficie)/100,
                  0,
                  to_number (pac_cap.produccion),
                  NULL,
                  NULL,
                  NULL,
                  NULL,
                  pac_cap.id_pac_parcela
                FROM tb_pac_capitales_asegurados pac_cap
				     WHERE pac_cap.id_pac_aseg IN (' || listaIdPacAseg || ')';

  EXECUTE IMMEDIATE v_consulta;

  COMMIT;
  PQ_UTL.log(lc, 'Volcado finalizado OK', 2);


  END cargaCapitalesAsegurados;



  --
  --
  --
  PROCEDURE cargaCapAsegRelModulo (p_idPoliza IN NUMBER) IS

  lc VARCHAR2(100) := 'PQ_CARGA_PARCELAS_PAC.cargaCapAsegRelModulo - ';

  BEGIN

  PQ_UTL.log(lc, 'Inicio de inserci??n de registros de relaci??n entre capital asegurado y m??dulo para el idPoliza: ' || p_idPoliza, 2);

  INSERT INTO TB_CAP_ASEG_REL_MODULO
  (ID,
   IDCAPITALASEGURADO,
   CODMODULO,
   PRECIO,
   PRODUCCION,
   PRECIOMODIF,
   PRODUCCIONMODIF)
  SELECT SQ_TB_CAP_ASEG_REL_MODULO.NEXTVAL,
         ca.idcapitalasegurado,
         mp.codmodulo,
         NULL,
         ca.produccion,
         NULL,
         NULL
    FROM tb_parcelas par, tb_modulos_poliza mp, tb_capitales_asegurados ca
   WHERE mp.idpoliza = p_idPoliza
     AND par.idpoliza = mp.idpoliza
     AND par.idparcela = ca.idparcela;

  COMMIT;
  PQ_UTL.log(lc, 'Inserci??n finalizada OK', 2);


  END cargaCapAsegRelModulo;


  --
  --
  --
  PROCEDURE cargaDatosVariables (listaIdPacAseg IN VARCHAR2, listaDVDefecto IN VARCHAR2) IS

  lc VARCHAR2(100) := 'PQ_CARGA_PARCELAS_PAC.cargaDatosVariables - ';
  v_consulta VARCHAR2(32767);

  BEGIN

  PQ_UTL.log(lc, 'Inicio del volcado de datos variables para listaIdPacAseg: ' || listaIdPacAseg || ', listaDVDefecto: (' || listaDVDefecto || ')', 2);

  v_consulta := 'INSERT INTO TB_DATOS_VAR_PARCELA
                  (IDCAPITALASEGURADO ,
                   IDDATOVARIABLE     ,
                   IDRELACION         ,
                   CODCONCEPTO        ,
                   VALOR              ,
                   CODCONCEPTOPPALMOD ,
                   CODRIESGOCUBIERTO)
                SELECT
                   (SELECT ca.idcapitalasegurado FROM tb_capitales_asegurados ca WHERE ca.IDPARCELA_PAC = pac_dv.ID_PAC_PARCELA),
                   sq_datos_var_parcela.NEXTVAL,
                   NULL,
                   pac_dv.CODCONCEPTO,
                   pac_dv.VALOR,
                   NULL,
                   NULL
                FROM TB_PAC_DATOS_VARIABLES pac_dv
				     WHERE pac_dv.id_pac_aseg IN (' || listaIdPacAseg || ')
				       AND pac_dv.CODCONCEPTO IN (' || listaDVDefecto || ')';

  EXECUTE IMMEDIATE v_consulta;

  COMMIT;
  PQ_UTL.log(lc, 'Volcado finalizado OK', 2);

  END cargaDatosVariables;




  --
  -- Actualiza el indicador de pac cargada de la p??liza al valor correspondiente dependiendo de si la carga de PAC
  -- ha finalizado correctamente o no (indicado por el par?!metro booleano 'p_cargaCorrecta'
  --
  procedure liberarPACPoliza (p_idPoliza IN NUMBER, p_cargaCorrecta IN BOOLEAN, p_error OUT VARCHAR2) IS

  BEGIN

  -- Actualiza el indicador de PAC de la p??liza para desbloquearla
  IF (p_cargaCorrecta) THEN
   update tb_polizas p set p.pac_cargada='S' where p.idpoliza=p_idPoliza;
   -- Borra los indicadores de parcela de pac de la tabla de parcelas y de capitales asegurados
   update tb_parcelas par set par.idparcela_pac = null where par.idpoliza =p_idPoliza;
   update tb_capitales_asegurados ca set ca.idparcela_pac = null where ca.idparcela in (
          select idparcela from tb_parcelas where idpoliza = p_idPoliza
   );
  ELSE
   -- Borra todas las parcelas asociadas a la p??liza
   delete tb_parcelas par where par.idpoliza=p_idPoliza;
   update tb_polizas p set p.pac_cargada='N' where p.idpoliza=p_idPoliza;
   p_error := '. Error al cargar las parcelas de PAC';
  END IF;

  commit;

  EXCEPTION WHEN OTHERS THEN
            p_error := 'Error al cargar las parcelas de PAC';

  END;

  FUNCTION SPLIT(in_string VARCHAR2, delim VARCHAR2) RETURN t_array IS
    i       number := 0;
    pos     number := 0;
    lv_str  varchar2(100) := in_string;
    strings t_array;
  BEGIN

    pos := instr(lv_str, delim, 1, 1);
    WHILE (pos != 0) LOOP
      i := i + 1;
      strings(i) := substr(lv_str, 1, pos-1);
      lv_str := substr(lv_str, pos + 1, length(lv_str));
      pos := instr(lv_str, delim, 1, 1);
      If pos = 0 THEN
        strings(i + 1) := lv_str;
      END IF;
    END LOOP;

    RETURN strings;
  END SPLIT;


  procedure insertDVDefecto (p_idPoliza IN NUMBER, p_listaDVDefecto IN VARCHAR2) IS

  lc VARCHAR2(100) := 'PQ_CARGA_PARCELAS_PAC.insertDVDefecto - ';

  v_array t_array;
  v_array_dv t_array;
  v_dv_codcpt tb_datos_var_parcela.codconcepto%TYPE;
  v_dv_valor tb_datos_var_parcela.valor%TYPE;

  BEGIN

  v_array := SPLIT(p_listaDVDefecto, ',');

      IF (v_array IS NOT NULL AND v_array.count > 0) THEN
         FOR i IN v_array.FIRST..v_array.LAST
         LOOP
             IF v_array.EXISTS(i) THEN
                PQ_UTL.log(lc, v_array(i), 2);

                v_array_dv := SPLIT(v_array(i), '#');
                v_dv_codcpt := TO_NUMBER (v_array_dv(1));
                v_dv_valor := v_array_dv(2);

                INSERT INTO TB_DATOS_VAR_PARCELA
				  (IDCAPITALASEGURADO,
				   IDDATOVARIABLE,
				   IDRELACION,
				   CODCONCEPTO,
				   VALOR,
				   CODCONCEPTOPPALMOD,
				   CODRIESGOCUBIERTO)
				  SELECT ca.idcapitalasegurado,
						 sq_datos_var_parcela.NEXTVAL,
						 NULL,
						 v_dv_codcpt,
						 v_dv_valor,
						 NULL,
						 NULL
					FROM tb_capitales_asegurados ca, tb_parcelas par
				   WHERE ca.idparcela = par.idparcela
					 AND par.idpoliza = p_idPoliza
					 AND NOT EXISTS
				   (SELECT dvp.iddatovariable
							FROM tb_datos_var_parcela dvp
						   WHERE dvp.idcapitalasegurado = ca.idcapitalasegurado
							 AND dvp.codconcepto = v_dv_codcpt);
             END IF;
         END LOOP;
      END IF;

  END;


  --
  --
  --
  procedure cargaParcelasPolizaDesdePAC (p_idPoliza IN NUMBER, p_idClase IN NUMBER, p_listaIdPacAseg IN VARCHAR2, p_listaDVDefecto IN VARCHAR2, p_error OUT VARCHAR2) IS

  lc VARCHAR2(100) := 'PQ_CARGA_PARCELAS_PAC.cargaParcelasPolizaDesdePAC - ';

  v_codtipocapital tb_sc_c_factores.codconcepto%TYPE;
  v_listaDV VARCHAR2(32767);

  timeINI DATE := SYSDATE;
  timeFIN DATE;

  BEGIN

  PQ_UTL.log(lc, '*** INICIO DE CARGA DE PARCELAS PAC ***', 2);
  PQ_UTL.log(lc, 'Id. poliza: ' || p_idPoliza, 2);
  PQ_UTL.log(lc, 'Id. clase: ' || p_idClase, 2);
  PQ_UTL.log(lc, 'Lista Id PAC Aseg: (' || p_listaIdPacAseg || ')', 2);
  PQ_UTL.log(lc, 'Lista DV Defecto: (' || p_listaDVDefecto || ')', 2);
  PQ_UTL.log(lc, '***  ***', 2);


  -- Obtiene el tipo de capital al que se asignar?!n a todas las parcelas
  v_codtipocapital := getTipoCapital (p_idPoliza);
  -- Si ha ocurrido alg??n error en el proceso, se cancela la carga de parcelas y se devuelve el mensaje de error correspondiente
  IF (v_codtipocapital = -1) THEN
     p_error := '. Error obtener el tipo de capital de las parcelas';
     RETURN;
  END IF;
  PQ_UTL.log(lc, 'Las parcelas se asociar?!n el tipo de capital ' || v_codtipocapital, 2);

  -- Vuelca las parcelas de PAC en parcelas de p??liza
  BEGIN
       cargaParcelas (p_idPoliza => p_idPoliza, idClase => p_idClase, listaIdPacAseg => p_listaIdPacAseg);
  EXCEPTION WHEN OTHERS THEN
       ROLLBACK;
       PQ_UTL.log(lc, 'Error al volcar las parcelas de PAC en parcelas de p??liza', 2);
       PQ_UTL.log(lc, 'ERROR -- SQLCODE:' || SQLCODE || ' SQLERRM: ' || SQLERRM ||'    ', 2);

       -- Actualiza el indicador de PAC de la p??liza para desbloquearla
       liberarPACPoliza (p_idPoliza, FALSE, p_error);
       RETURN;
  END;

  -- Vuelca los capitales asegurados de PAC y los asocia a las parcelas cargadas en paso anterior
  BEGIN
       cargaCapitalesAsegurados (listaIdPacAseg => p_listaIdPacAseg, codTipoCapital => v_codtipocapital);
  EXCEPTION WHEN OTHERS THEN
       ROLLBACK;
       PQ_UTL.log(lc, 'Error al volcar los capitales asegurados de PAC', 2);
       PQ_UTL.log(lc, 'ERROR -- SQLCODE:' || SQLCODE || ' SQLERRM: ' || SQLERRM ||'    ', 2);

       -- Actualiza el indicador de PAC de la p??liza para desbloquearla
       liberarPACPoliza (p_idPoliza, FALSE, p_error);
       RETURN;
  END;

  -- Inserci??n de registros de relaci??n entre cada capital asegurado insertado en el apartado anterior
  -- cada m??dulo asociado a la p??liza
  BEGIN
       cargaCapAsegRelModulo (p_idPoliza => p_idPoliza);
  EXCEPTION WHEN OTHERS THEN
       ROLLBACK;
       PQ_UTL.log(lc, 'Error al insertar los registros de relaci??n entre capitales asegurados y m??dulos', 2);
       PQ_UTL.log(lc, 'ERROR -- SQLCODE:' || SQLCODE || ' SQLERRM: ' || SQLERRM ||'    ', 2);

       -- Actualiza el indicador de PAC de la p??liza para desbloquearla
       liberarPACPoliza (p_idPoliza, FALSE, p_error);
       RETURN;
  END;

  --
  -- Obtiene lista de c??digos de concepto separados por ',' correspondientes a los datos variables
  -- configurados en el configurador de pantallas para el plan/l?-nea asociado a la p??liza.
  --
  -- S??lo se volcar?!n los datos variables de PAC incluidos en esta lista de c??digos de concepto, si la lista
  -- obtenida es nula no se volcar?! registro alguno
  v_listaDV := getListaDVPantalla (p_idPoliza => p_idPoliza);

  IF (v_listaDV IS NULL) THEN
     PQ_UTL.log(lc, 'No hay datos variables registrados en el configurador de pantallas para el plan/l?-nea de la p??liza', 2);
     PQ_UTL.log(lc, 'No se volcar?! ning??n registro de datos variables de PAC', 2);
  ELSE
     -- Vuelca los datos variables de PAC y los asocia a los capitales asegurados cargados en el paso anterior
     BEGIN
          cargaDatosVariables (listaIdPacAseg => p_listaIdPacAseg, listaDVDefecto => v_listaDV);
     EXCEPTION WHEN OTHERS THEN
       ROLLBACK;
       PQ_UTL.log(lc, 'Error al volcar los datos variables de PAC', 2);
       PQ_UTL.log(lc, 'ERROR -- SQLCODE:' || SQLCODE || ' SQLERRM: ' || SQLERRM ||'    ', 2);

       -- Actualiza el indicador de PAC de la p??liza para desbloquearla
       liberarPACPoliza (p_idPoliza, FALSE, p_error);
       RETURN;
     END;
  END IF;


  -- Si hay datos variables  configurados en el configurador de pantallas para el plan/l?-nea asociado a la p??liza y
  -- se han insertado valores en la pantalla de 'Visualizaci??n de datos variables PAC', se insertan estos valores
  -- para todos los capitales asegurados que no los tengan ya informados
  BEGIN
   IF (v_listaDV IS NOT NULL AND p_listaDVDefecto IS NOT NULL) THEN
      PQ_UTL.log(lc, 'Establece los valores por defecto para los DV indicados en la pantalla', 2);
      insertDVDefecto (p_idPoliza => p_idPoliza, p_listaDVDefecto => p_listaDVDefecto);
   END IF;

   EXCEPTION WHEN OTHERS THEN
       ROLLBACK;
       PQ_UTL.log(lc, 'Error al insertar los DV por defecto', 2);
       PQ_UTL.log(lc, 'ERROR -- SQLCODE:' || SQLCODE || ' SQLERRM: ' || SQLERRM ||'    ', 2);

       -- Actualiza el indicador de PAC de la p??liza para desbloquearla
       liberarPACPoliza (p_idPoliza, FALSE, p_error);
       RETURN;
     END;

  -- Actualiza el indicador de PAC de la p??liza para desbloquearla
  liberarPACPoliza (p_idPoliza, TRUE, p_error);

  timeFIN := SYSDATE;
  PQ_UTL.log(lc, '*** FIN DE CARGA DE PARCELAS PAC ***', 2);
  PQ_UTL.log(lc, '*** TIEMPO EMPLEADO EN LA CARGA : ' || to_char(trunc(sysdate) + (timeFIN - timeINI),'mi:ss'), 2);

  END cargaParcelasPolizaDesdePAC;

end PQ_CARGA_PARCELAS_PAC;
/
SHOW ERRORS;