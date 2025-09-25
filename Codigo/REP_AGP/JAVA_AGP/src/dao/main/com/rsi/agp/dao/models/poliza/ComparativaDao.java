package com.rsi.agp.dao.models.poliza;


import java.math.BigDecimal;

import org.hibernate.Query;
import org.hibernate.Session;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.util.ConstantsConceptos;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.tables.poliza.ComparativaPoliza;
import com.rsi.agp.dao.tables.poliza.ComparativaPolizaId;
import com.rsi.agp.dao.tables.poliza.Poliza;


public class ComparativaDao extends BaseDaoHibernate implements IComparativaDao {
	
    public ComparativaPoliza  guardarComparatCaracExplot(ComparativaPoliza cp, 
    													 Poliza poliza , 
    													 BigDecimal caractExlp){
    	
    	

    	// Inserto en la base de datos la nueva característica
		ComparativaPoliza compP = new ComparativaPoliza();
		compP.setConceptoPpalModulo(cp.getConceptoPpalModulo());
		compP.setDescvalor(cp.getDescvalor());
		compP.setPoliza(poliza);
		compP.setRiesgoCubierto(cp.getRiesgoCubierto());
		compP.setRiesgoCubiertoModulo(cp.getRiesgoCubiertoModulo());
		compP.setDescvalor("Caracteristica de Explotacion");
		
		ComparativaPolizaId compID = new ComparativaPolizaId();
		
		compID.setCodconcepto(new BigDecimal(ConstantsConceptos.CODCPTO_CARACT_EXPLOTACION));
		compID.setCodconceptoppalmod(cp.getId().getCodconceptoppalmod());
		compID.setCodmodulo(cp.getId().getCodmodulo());
		compID.setCodriesgocubierto(cp.getId().getCodriesgocubierto());
		compID.setCodvalor(caractExlp);
		compID.setFilacomparativa(cp.getId().getFilacomparativa());
		compID.setFilamodulo(cp.getId().getFilamodulo());
		compID.setIdpoliza(poliza.getIdpoliza());
		compID.setLineaseguroid(cp.getId().getLineaseguroid());
		compID.setIdComparativa(cp.getId().getIdComparativa());
		
		// asignamos a la comparativa la caracteristica de la explotacion
		compP.setId(compID);
		
		try {
			this.evict(cp.getId());
			this.evict(cp);
			this.evict(compP.getPoliza());
			this.evict(compID);
			this.evict(compP.getId());
			this.evict(compP);
			this.evict(poliza.getIdpoliza());
			
			saveOrUpdate(compP);
		} catch (DAOException e) {
			try {
			logger.error("Error al guardar Comparativa Caract. Explotacion", e);
			Session session = obtenerSession();
			String insert = " insert into o02agpe0.tb_comparativas_poliza values("+poliza.getIdpoliza()+","+cp.getId().getLineaseguroid()+",'"+cp.getId().getCodmodulo()+"',"+cp.getId().getFilamodulo()+","+cp.getId().getCodconceptoppalmod()+","+cp.getId().getCodriesgocubierto()+","+cp.getId().getFilacomparativa()+",'Caracteristica de Explotacion'," + ConstantsConceptos.CODCPTO_CARACT_EXPLOTACION + ","+caractExlp+","+cp.getId().getIdComparativa()+")";
			Query query = session.createSQLQuery(insert);
			logger.debug("insertamos la comparativa: "+insert);
			query.executeUpdate();
			logger.debug("inserccion OK");
			} catch (Exception ex) {
				logger.error("Error al guardar Comparativa Caract. Explotacion por sql", ex);
			}
			
			
		} catch (Exception e) {
			logger.error("Error al guardar Comparativa Caract. Explotacion", e);
		}

		return compP;	
    }
}
