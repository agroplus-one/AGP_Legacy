SET DEFINE OFF;
SET SERVEROUTPUT ON;
CREATE OR REPLACE PACKAGE O02AGPE1.pq_crear_inserts_vistas_campos AUTHID CURRENT_USER IS

  -- Inserta en las tablas TB_MTOINF_VISTAS y TB_MTOINF_VISTAS_CAMPOS los registros correspondientes
  -- a la vista pasada como parámetro
	PROCEDURE crear_inserts (vista  IN VARCHAR2, nombre_vista IN VARCHAR2, esquema_orig  VARCHAR2, esquema_dest  VARCHAR2);

  -- Cambia el nombre de los registros de la tabla TB_MTOINF_CAMPOS_PERMITIDOS que tengan el mismo campo ABREVIADO
  PROCEDURE actualizar_nombres_duplicados;

END pq_crear_inserts_vistas_campos;
/
SHOW ERRORS;