SET DEFINE OFF;
SET SERVEROUTPUT ON;
create or replace package o02agpe0.PQ_CHECK_ASEG_AUTORIZADOS is

  -- Author  : U028982
  -- Created : 24/10/2011 12:30:28
  -- Purpose : procedimiento para optimizar la tabla tb_sc_c_aseg_autorizados
  --           Se usará la tabla tb_Aseg_autorizados_dia siempre que sea posible

  -- Public type declarations
 PROCEDURE PR_CHECK_ASEG_AUTORIZADOS (P_LINEASEGUROID IN NUMBER, P_NIFCIF IN VARCHAR2);

 PROCEDURE PR_CARGA_ASEG_AUTORIZADOS_DIA;

end PQ_CHECK_ASEG_AUTORIZADOS;
/
create or replace package body o02agpe0.PQ_CHECK_ASEG_AUTORIZADOS is


PROCEDURE PR_CHECK_ASEG_AUTORIZADOS (P_LINEASEGUROID IN NUMBER, P_NIFCIF IN VARCHAR2) IS
       lst_Aseg                NUMBER := 0;
       lst_Aseg2                NUMBER := 0;
begin
     --SELECT count(*) into lst_Aseg from TB_aseg_autorizados_dia where NIFASEGURADO=NIFCIF;
     delete TB_aseg_autorizados_dia D where D.LINEASEGUROID = P_LINEASEGUROID AND D.NIFASEGURADO=P_NIFCIF;

IF lst_Aseg <=0 THEN

    INSERT INTO TB_ASEG_AUTORIZADOS_DIA d
        (
        d.lineaseguroid,
        d.codmodulo,
        d.nifasegurado,
        d.codcultivo,
        d.codvariedad,
        d.codgarantizado,
        d.codnivelriesgo,
        d.coefsobrerdtos,
        d.rdtopermitido,
        d.codprovincia,
        d.cpmodffg,
        d.codrcubffg,
        d.fecfgarant,
        d.cpmodcg,
        d.codrcubcg,
        d.valorcg,
        d.desccg,
        d.id,
        d.rcubeleg,
        d.cpmodrcub,
        d.codrcubrcub,
        d.CARACTEXPLOT,
        d.REVISIONRDTO
      )
      SELECT a.lineaseguroid,
        a.codmodulo,
        a.nifasegurado,
        a.codcultivo,
        a.codvariedad,
        a.codgarantizado,
        a.codnivelriesgo,
        a.coefsobrerdtos,
        a.rdtopermitido,
        a.codprovincia,
        a.cpmodffg,
        a.codrcubffg,
        a.fecfgarant,
        a.cpmodcg,
        a.codrcubcg,
        a.valorcg,
        a.desccg,
        a.id,
        a.rcubeleg,
        a.cpmodrcub,
        a.codrcubrcub,
        a.CARACTEXPLOT,
        a.REVISIONRDTO
              from TB_SC_C_ASEG_AUTORIZADOS a where a.LINEASEGUROID = P_LINEASEGUROID AND a.NIFASEGURADO=P_NIFCIF;
      commit;

      SELECT count(*) into lst_Aseg from TB_aseg_autorizados_dia D where D.LINEASEGUROID = P_LINEASEGUROID AND D.NIFASEGURADO=P_NIFCIF;
      IF lst_Aseg <=0 THEN
          SELECT count(*) into lst_Aseg2 from TB_aseg_autorizados_dia D where D.LINEASEGUROID = P_LINEASEGUROID AND D.NIFASEGURADO IS NULL;
          IF lst_Aseg2 <=0 THEN
             INSERT into TB_aseg_autorizados_dia d(
               d.lineaseguroid,
              d.codmodulo,
              d.nifasegurado,
              d.codcultivo,
              d.codvariedad,
              d.codgarantizado,
              d.codnivelriesgo,
              d.coefsobrerdtos,
              d.rdtopermitido,
              d.codprovincia,
              d.cpmodffg,
              d.codrcubffg,
              d.fecfgarant,
              d.cpmodcg,
              d.codrcubcg,
              d.valorcg,
              d.desccg,
              d.id,
              d.rcubeleg,
              d.cpmodrcub,
              d.codrcubrcub,
              d.CARACTEXPLOT,
              d.REVISIONRDTO
            )

            SELECT a.lineaseguroid,
              a.codmodulo,
              a.nifasegurado,
              a.codcultivo,
              a.codvariedad,
              a.codgarantizado,
              a.codnivelriesgo,
              a.coefsobrerdtos,
              a.rdtopermitido,
              a.codprovincia,
              a.cpmodffg,
              a.codrcubffg,
              a.fecfgarant,
              a.cpmodcg,
              a.codrcubcg,
              a.valorcg,
              a.desccg,
              a.id,
              a.rcubeleg,
              a.cpmodrcub,
              a.codrcubrcub,
              a.CARACTEXPLOT,
              a.REVISIONRDTO
              from TB_SC_C_ASEG_AUTORIZADOS a where a.LINEASEGUROID = P_LINEASEGUROID AND a.NIFASEGURADO IS NULL;
            commit;
         END IF;
      END IF;
END IF;


END;

    PROCEDURE PR_CARGA_ASEG_AUTORIZADOS_DIA IS
        v_nif            TB_SC_C_ASEG_AUTORIZADOS.NIFASEGURADO%TYPE;
        v_lineaseguroid  TB_SC_C_ASEG_AUTORIZADOS.LINEASEGUROID%TYPE;
        v_consulta       varchar2(2000) := 'select DISTINCT P.LINEASEGUROID, A.NIFCIF ' ||
                                           'from TB_POLIZAS P, TB_ASEGURADOS A ' ||
                                           'WHERE P.IDASEGURADO = A.ID';
        TYPE cur_typ IS REF CURSOR;
        C_ASEGURADOS cur_typ;

    BEGIN
        --VACIAMOS LA TABLA
        TRUNCATE_TABLE('TB_ASEG_AUTORIZADOS_DIA');

        --PRIMERO INSERTAMOS LOS NIF NULOS DE TODAS LAS LÍNEAS
        INSERT INTO TB_ASEG_AUTORIZADOS_DIA(
            SELECT * FROM TB_SC_C_ASEG_AUTORIZADOS WHERE NIFASEGURADO IS NULL
        );

        --LUEGO INSERTAMOS TODOS LOS REGISTROS PARA LOS ASEGURADOS QUE TIENEN PÓLIZA
        OPEN C_ASEGURADOS FOR v_consulta;
        LOOP
            FETCH C_ASEGURADOS INTO v_lineaseguroid, v_nif;
            EXIT WHEN C_ASEGURADOS%NOTFOUND;

            INSERT into TB_aseg_autorizados_dia d(
               d.lineaseguroid,
               d.codmodulo,
               d.nifasegurado,
               d.codcultivo,
               d.codvariedad,
               d.codgarantizado,
               d.codnivelriesgo,
               d.coefsobrerdtos,
               d.rdtopermitido,
               d.codprovincia,
               d.cpmodffg,
               d.codrcubffg,
               d.fecfgarant,
               d.cpmodcg,
               d.codrcubcg,
               d.valorcg,
               d.desccg,
               d.id,
               d.rcubeleg,
               d.cpmodrcub,
               d.codrcubrcub,
               d.CARACTEXPLOT,
               d.REVISIONRDTO
            )
                SELECT lineaseguroid, codmodulo, nifasegurado, codcultivo, codvariedad, codgarantizado, codnivelriesgo,
                coefsobrerdtos, rdtopermitido, codprovincia, cpmodffg, codrcubffg, fecfgarant, cpmodcg, codrcubcg,
                valorcg, desccg, id, rcubeleg, cpmodrcub, codrcubrcub, CARACTEXPLOT, REVISIONRDTO
                FROM TB_SC_C_ASEG_AUTORIZADOS WHERE NIFASEGURADO = v_nif AND LINEASEGUROID = v_lineaseguroid;

        END LOOP;

        commit;

    EXCEPTION
         when others then
            pq_utl.log('ERRO EN PR_CARGA_ASEG_AUTORIZADOS_DIA ' || SQLERRM || '*********');
            rollback;
    END;

end PQ_CHECK_ASEG_AUTORIZADOS;
/
SHOW ERRORS;