SET DEFINE OFF;  
  
SET SERVEROUTPUT ON; 

CREATE OR REPLACE VIEW O02AGPE0.RGA_DATOS_DE_PERSONAS_SC AS
SELECT a.CODENTIDAD, a.NIFCIF, a.DISCRIMINANTE, a.NOMBRE, a.APELLIDO1,
       a.APELLIDO2, a.RAZONSOCIAL, a.CLAVEVIA, a.DIRECCION, a.NUMVIA,
       a.PISO, a.BLOQUE, a.ESCALERA, a.CODPROVINCIA, p.nomprovincia,
       a.CODLOCALIDAD, a.SUBLOCALIDAD, l.nomlocalidad, a.CODPOSTAL,d.ccc,
       a.REGIMENSEGSOCIAL, a.numsegsocial, a.TELEFONO, a.MOVIL, d.iban
  FROM o02agpe0.tb_asegurados a
       LEFT OUTER JOIN o02agpe0.tb_datos_asegurados d ON  a.ID = d.idasegurado
       INNER JOIN o02agpe0.tb_provincias p ON a.codprovincia = p.codprovincia
       INNER JOIN o02agpe0.tb_localidades l ON a.codprovincia = l.codprovincia
                                           AND a.codlocalidad = l.codlocalidad
                                           AND a.sublocalidad = l.sublocalidad;