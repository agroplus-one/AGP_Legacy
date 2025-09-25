UPDATE (select doc.lineaseguroid,
               doc.codplan plan_doc,
               doc.codlinea linea_doc,
               lin.codlinea linea_lin,
               lin.codplan plan_lin
               FROM o02agpe0.tb_doc_agroseguro doc,
                    o02agpe0.tb_lineas lin
               WHERE doc.lineaseguroid = lin.lineaseguroid)
SET plan_doc = plan_lin,
    linea_doc = linea_lin;

update o02agpe0.TB_DOC_AGROSEGURO D
set d.codentidad = 0
where d.codentidad is null;