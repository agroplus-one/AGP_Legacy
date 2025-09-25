CREATE OR REPLACE PACKAGE O02AGPE0.PQ_UTLZIP AS

PROCEDURE abrirZip (p_out_file IN VARCHAR2)
   AS
      LANGUAGE JAVA
         NAME 'com.rsi.agp.UTLZip.abrirZip(java.lang.String)';

PROCEDURE cerrarZip
   AS
      LANGUAGE JAVA
         NAME 'com.rsi.agp.UTLZip.cerrarZip()';

PROCEDURE uncompressFile (p_in_file IN VARCHAR2, ruta IN VARCHAR2)
   AS
      LANGUAGE JAVA
         NAME 'com.rsi.agp.UTLZip.uncompressFile(java.lang.String, java.lang.String)';

PROCEDURE uncompressFile (p_in_file IN VARCHAR2, ruta IN VARCHAR2, fichero IN VARCHAR2)
   AS
      LANGUAGE JAVA
         NAME 'com.rsi.agp.UTLZip.uncompressFile(java.lang.String, java.lang.String, java.lang.String)';

PROCEDURE compressFile (p_in_file IN VARCHAR2, p_out_file IN VARCHAR2)
   AS
      LANGUAGE JAVA
         NAME 'com.rsi.agp.UTLZip.compressFile(java.lang.String, java.lang.String)';

PROCEDURE borraXMLs (ruta IN VARCHAR2)
  AS
     LANGUAGE JAVA
         NAME 'com.rsi.agp.UTLZip.borraXMLs(java.lang.String)';


END PQ_UTLZIP;
/
