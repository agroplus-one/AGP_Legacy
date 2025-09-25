CREATE OR REPLACE PACKAGE pq_seguridad_agp IS
	FUNCTION fn_usuario_aplicacion_valid(p_usuario VARCHAR2
																			,p_perfil  VARCHAR2 DEFAULT 'AGP') RETURN VARCHAR2;
END pq_seguridad_agp;
/
CREATE OR REPLACE PACKAGE BODY pq_seguridad_agp IS

	FUNCTION fn_usuario_aplicacion_valid(p_usuario VARCHAR2
																			,p_perfil  VARCHAR2 DEFAULT 'AGP') RETURN VARCHAR2 IS
		v_rc VARCHAR2(5);
	
	BEGIN
	
		IF (pk_mod_seguridad.ft_usuario_aplicacion(p_usuario, p_perfil)) THEN
			v_rc := 'true';
		ELSE
			v_rc := 'false';
		END IF;
	
		BEGIN
			IF pk_mod_seguridad.ft_entidad(p_usuario => p_usuario) IN ('3102', '3110', '3177') THEN
				IF p_usuario = '39018091' THEN
					v_rc :=  v_rc;
				ELSE
					v_rc := 'false';
				END IF;
			END IF;
		EXCEPTION
			WHEN OTHERS THEN
				NULL;
		END;
	
	
		RETURN v_rc;
	END;

END pq_seguridad_agp;
/
