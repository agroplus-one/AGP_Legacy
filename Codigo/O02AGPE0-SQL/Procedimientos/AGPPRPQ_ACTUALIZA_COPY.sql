SET DEFINE OFF;
SET SERVEROUTPUT ON;

CREATE OR REPLACE PACKAGE o02agpe0.pq_actualiza_copy IS

PROCEDURE actualiza_datos_variables_copy (cadena IN varchar2, numParcelas IN number, idcopy IN number);

END pq_actualiza_copy;
/
CREATE OR REPLACE PACKAGE BODY o02agpe0.pq_actualiza_copy AS

PROCEDURE actualiza_datos_variables_copy (CADENA IN VARCHAR2, NUMPARCELAS IN NUMBER, IDCOPY IN NUMBER) IS

lc VARCHAR2(50) := 'pq_actualiza_copy.actualiza_datos_variables_copy'; -- Variable que almacena el nombre del paquete y de la función

numRegPorDV NUMBER := 0; -- Número de capitales asegurados de la póliza de copy que tienen asignado el código de concepto y valor indicado

v_codConcepto NUMBER; -- Código de concepto del dato variable
v_valor VARCHAR2(10); -- Valor del dato variable

v_cadena_dv         VARCHAR2(100); -- Almacena la cadena que indica el concepto y el valor del dato variable
v_ind_cadena_dv     NUMBER := 1; -- Indicador utilizado al partir la cadena de datos variables

TYPE tpcursor IS REF CURSOR; -- Tipo cursor
l_tp_cursor       tpcursor; -- Cursor para la consulta de ids de capitales asegurados
l_query_ca        VARCHAR2(2000); -- Almacena la consulta para obtener los ids de capitales asegurados

v_id_cap_aseg NUMBER; -- Almacena el id del capital asegurado en la inserción del dato variable
v_aux_existe_dv NUMBER; -- Almacena el número de datos variables que se ajustan al filtro introducido


BEGIN

  pq_utl.log(lc, 'Llamada al PL de actualiza_datos_variables_copy con los siguientes parametros', 2);
  pq_utl.log(lc, 'Cadena:' || cadena, 2);
  pq_utl.log(lc, 'numParcelas:' || numParcelas, 2);
  pq_utl.log(lc, 'idcopy:' || idcopy, 2);

  -- Parte la cadena que contiene los datos variables por el caracter ';'
  WHILE pq_calcula_precio.fn_extraer_campo(cadena, v_ind_cadena_dv, ';') IS NOT NULL LOOP
         -- Obtiene la cadena correspondiente a 'codconcepto=valor'
         v_cadena_dv := pq_calcula_precio.fn_extraer_campo(cadena, v_ind_cadena_dv, ';');      
         v_ind_cadena_dv := v_ind_cadena_dv + 1;
         
         pq_utl.log(lc, 'Se trata el dato variable -> ' || v_cadena_dv, 2);
         
         -- Parte la cadena para obtener el codconcepto y el valor
         v_codConcepto := pq_calcula_precio.fn_extraer_campo(v_cadena_dv, 1, '=');
         v_valor := pq_calcula_precio.fn_extraer_campo(v_cadena_dv, 2, '='); 
         
         pq_utl.log(lc, 'Codigo de concepto = ' || v_codConcepto, 2);    
         pq_utl.log(lc, 'Valor = ' || v_valor, 2);    
         
         -- Consulta para obtener el número de capitales asegurados de la póliza de copy que tiene asignado
         -- el código de concepto indicado
        EXECUTE IMMEDIATE 'SELECT COUNT(*)        
          FROM TB_COPY_DATOS_VAR_PARC DV
          WHERE DV.IDCAPITALASEGURADO IN
               (SELECT CA.ID
                  FROM TB_COPY_CAPITALES_ASEG CA
                 WHERE CA.IDPARCELA IN (SELECT PAR.ID
                                          FROM TB_COPY_PARCELAS PAR
                                         WHERE PAR.IDCOPY = ' || idcopy || '))
           AND DV.CODCONCEPTO = ' || v_codConcepto INTO numRegPorDV;    
           
         pq_utl.log(lc, 'Numero de CA de la copy que tienen asignado el codigo de concepto indicado = ' || numRegPorDV, 2);    
         
         
         -- Si el número de capitales asegurados que tienen un dato variable con el código de concepto y valor
         -- actuales es diferente al número total de capitales asegurados de la póliza hay que actualizar los datos
         -- Si son iguales, no se realiza ninguna acción
         IF (numRegPorDV != numParcelas) THEN      
         
            pq_utl.log(lc, 'Numero de CA de la copy con dicho DV es diferente que el que indica la copy descargada', 2);              
            pq_utl.log(lc, 'Se actualizaran los DV de la copy con los datos descargados', 2);              
         
            -- Se obtiene el listado de ids de capitales asegurados
              l_query_ca := 'SELECT CA.ID
                             FROM TB_COPY_CAPITALES_ASEG CA
                             WHERE CA.IDPARCELA IN (SELECT PAR.ID
                                          FROM TB_COPY_PARCELAS PAR
                                          WHERE PAR.IDCOPY = ' || idcopy || ')';
                                          
              OPEN l_tp_cursor FOR l_query_ca;  
                                                       
              -- Recorre los ids de capital asegurado
              LOOP
                  -- Vuelca el valor del curso en la variable
                  FETCH l_tp_cursor	INTO v_id_cap_aseg;
                  
                  -- Si se ha llegado al final se sale del bucle
                  IF (l_tp_cursor%NOTFOUND) THEN
                     pq_utl.log(lc, 'Commit de las transacciones y finalizacion del proceso.', 2);              
                     COMMIT;
                     EXIT;
                  END IF;
                  
                  -- Si no hay ningún capital asegurado que contenga como dato variable ese concepto y valor se inserta 
                  -- el dato variable correspondiente para cada capital asegurado
                  IF (numRegPorDV = 0) THEN 
                  
                    -- Inserta el registro del DV
                    INSERT INTO o02agpe0.tb_copy_datos_var_parc 
                    VALUES (o02agpe0.SQ_TB_COPY_DATOS_VAR_PARC.nextval, v_id_cap_aseg, v_codConcepto, v_valor,
                    NULL, NULL, NULL);
                  
                  -- Si llega hasta aquí significa que hay que comprobar si hay dato variable asociado al capital 
                  -- asegurado. Si lo hay se actualizará los valores y si no lo hay se insertará el registro
                  ELSE                
                   
                    -- Lanza la consulta para comprobar si existe dato variable para el capital
                    EXECUTE IMMEDIATE
                    'SELECT COUNT(*) FROM TB_COPY_DATOS_VAR_PARC DV WHERE DV.CODCONCEPTO = ' || v_codConcepto ||
                    ' AND DV.VALOR = ''' || v_valor || ''' AND DV.IDCAPITALASEGURADO=' || v_id_cap_aseg
                    INTO v_aux_existe_dv;
                    
                    -- Si no existe, se inserta el registro
                    IF (v_aux_existe_dv = 0) THEN
                       -- Inserta el registro del DV
                        INSERT INTO o02agpe0.tb_copy_datos_var_parc 
                        VALUES (o02agpe0.SQ_TB_COPY_DATOS_VAR_PARC.nextval, v_id_cap_aseg, v_codConcepto, v_valor,
                        NULL, NULL, NULL);
                    END IF;
                                                                              
                  END IF; -- Cierre de if que diferencia si hay que insertar todos los registros o comprobar antes
              
              END LOOP; -- Fin de bucle que recorre los capitales asegurados
                                          
         
         END IF; -- Cierre de if que indica que el número de CA que tienen el DV indicado es diferente al total de parcelas que indica la copy descargada
         
               
   END LOOP; -- Fin de bucle que parte los DV


END actualiza_datos_variables_copy;


END pq_actualiza_copy;
/
SHOW ERRORS;