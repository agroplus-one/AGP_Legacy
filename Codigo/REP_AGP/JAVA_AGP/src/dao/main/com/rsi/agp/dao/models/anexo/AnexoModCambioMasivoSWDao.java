package com.rsi.agp.dao.models.anexo;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.util.ConstantsConceptos;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.tables.anexo.AnexoModSWCambioMasivo;
import com.rsi.agp.dao.tables.anexo.CapitalAsegurado;
import com.rsi.agp.dao.tables.anexo.CapitalDTSVariable;
import com.rsi.agp.dao.tables.anexo.Parcela;
import com.rsi.agp.dao.tables.commons.Termino;

public class AnexoModCambioMasivoSWDao extends BaseDaoHibernate implements IAnexoModCambioMasivoSWDao {
	 
	private static final Log logger = LogFactory.getLog(AnexoModCambioMasivoSWDao.class);

	@Override
	public void updateParcelaAnexo(Parcela parcelaAnexo, BigDecimal codcultivo,
			BigDecimal codVariedad) throws DAOException {
		
		logger.debug("Init: updateParcelaAnexo - AnexoModCambioMasivoSWDao");
		try {
			StringBuffer updatePar = new StringBuffer();
			StringBuffer updateInst = new StringBuffer();
			Session session = obtenerSession();
			String sql = "update tb_anexo_mod_parcelas a set ";
			
			String sqlSet= codcultivo!=null ? "codcultivo = "+codcultivo : " ";
			sqlSet+= codcultivo!=null && codVariedad!= null ? " , ": " ";
			sqlSet+= codVariedad!= null ? "codvariedad = " + codVariedad : " ";
			
			String whereParc= " where id in "+ parcelaAnexo.getId();
			String whereInst= " where idparcelaanxestructura in "+ parcelaAnexo.getId();
			
			updatePar.append(sql).append(sqlSet).append(whereParc).toString();
			updateInst.append(sql).append(sqlSet).append(whereInst).toString();
			
			logger.debug("updateParcelaAnexo - sql parcelas: " + updatePar.toString());
			logger.debug("updateParcelaAnexo - sql instalaciones: " + updateInst.toString());
			
			session.createSQLQuery(updatePar.toString()).executeUpdate();
			session.createSQLQuery(updateInst.toString()).executeUpdate();
			
			logger.debug("Fin: updateParcelaAnexo - AnexoModCambioMasivoSWDao");
		}
		catch (Exception e) {
			logger.error("Se ha producido un error al actualizar las parcelas del anexo: ", e);
			throw new DAOException();
		}	
	}

	@Override
	public void updateSuperficie(BigDecimal superficie, Parcela parcelaAnexo)
			throws DAOException {
		
		try {
			logger.debug("Init: updateSuperficie - AnexoModCambioMasivoSWDao");
			Session session = obtenerSession();	
			
			Set<CapitalAsegurado> listCapitalesAsegurados = parcelaAnexo.getCapitalAsegurados();

			for(CapitalAsegurado capitalAsegurado : listCapitalesAsegurados){
				String sql = "update tb_anexo_mod_capitales_aseg set superficie ="+ superficie +	    			    					
						" where id = "+ capitalAsegurado.getId();
			
				session.createSQLQuery(sql).executeUpdate();
			}
			logger.debug("Fin: updateSuperficie - AnexoModCambioMasivoSWDao");   
		
		}catch (Exception e) {
			logger.error("Se ha producido un error al actualizar la superficie: ", e);
			throw new DAOException();
		}	  
		
	}
	
	@Override
	public void updateProduccion(BigDecimal increHa,BigDecimal increParcela,Parcela parcelaAnexo) 
			throws DAOException {
		
		Set<CapitalAsegurado> listCapitalesAsegurados = parcelaAnexo.getCapitalAsegurados();
		
		BigDecimal newProduccion = new BigDecimal(0);
		try {
		logger.debug("Init: updateProduccion - AnexoModCambioMasivoSWDao");
		Session session = obtenerSession();
	
		    for(CapitalAsegurado capitalAsegurado : listCapitalesAsegurados){
		    	
		    	if(capitalAsegurado.getTipoCapital().getCodconcepto().equals(BigDecimal.valueOf(ConstantsConceptos.CODCPTO_METROS_CUADRADOS))|| 
		    			capitalAsegurado.getTipoCapital().getCodconcepto().equals(BigDecimal.valueOf(ConstantsConceptos.CODCPTO_PRODUCCION))){
		    			
		    		    newProduccion = calculaProduccion (increHa,increParcela,capitalAsegurado);
		    			String sql = "update tb_anexo_mod_capitales_aseg set produccion ="+ newProduccion +	    			    					
		    					" where id = "+ capitalAsegurado.getId();
		    			session.createSQLQuery(sql).executeUpdate();
			    }
		    }
		logger.debug("Fin: updateProduccion - AnexoModCambioMasivoSWDao");   
		}catch (Exception e) {
			logger.error("Se ha producido un error al actualizar la produccion: ", e);
			throw new DAOException();
		}	   
	}
	@Override
	public void updateIncUnidades(String unidades_cm,
			BigDecimal inc_unidades_cm, Parcela parcelaAnexo) throws DAOException {
		
		Session session = obtenerSession();
		BigDecimal incremento = new BigDecimal(0);
    	BigDecimal codConceptoProduccion = new BigDecimal(ResourceBundle.getBundle("agp").getString("codConceptoRendimiento"));
    	Set<CapitalAsegurado> listCapitalesAsegurados = parcelaAnexo.getCapitalAsegurados();
    	
    	try {
    		logger.debug("Init: updateIncUnidades - AnexoModCambioMasivoSWDao");
    		
    		for(CapitalAsegurado capitalAsegurado : listCapitalesAsegurados){
    			if(capitalAsegurado.getTipoCapital().getCodconcepto().equals(codConceptoProduccion)){
		    	
    				
		    		boolean enco = false;
					if (!"".equals(unidades_cm)){
		    			incremento = new BigDecimal(unidades_cm).multiply(inc_unidades_cm);
		    			enco  = true;
		    		}else{
		    			
		    			for (CapitalDTSVariable datos :  capitalAsegurado.getCapitalDTSVariables()){
		    				
		            		if (datos.getCodconcepto().equals(BigDecimal.valueOf(ConstantsConceptos.CODCPTO_NUMARBOLES))){
		            			incremento = new BigDecimal(datos.getValor()).multiply(inc_unidades_cm);
		            			enco  = true;
		            		}
		            	}
		            }
		    		if (!enco){
		    			incremento = new BigDecimal(0);
		    		}
		    		
		    		String sql = "update tb_anexo_mod_capitales_aseg set produccion ="+ incremento +	    			    					
	    					" where id = "+ capitalAsegurado.getId();
		    		session.createSQLQuery(sql).executeUpdate();
		    	}
	    	}
    	logger.debug("Fin: updateIncUnidades - AnexoModCambioMasivoSWDao");   
		}catch (Exception e) {
			logger.error("Se ha producido un error al actualizar la produccion: ", e);
			throw new DAOException();
		}	   
		
	}
	
	@Override
	public void updateDatosVariables(String valor, Parcela parcelaAnexo, int codConcepto) throws DAOException {
		
		Set<CapitalAsegurado> listCapitalesAsegurados = parcelaAnexo.getCapitalAsegurados();
		boolean actualizamos = false;
		Long idcapitalasegurado = null;
		try {
			logger.debug("Init: updateDatosVariables - AnexoModCambioMasivoSWDao");
			
			for(CapitalAsegurado capitalAsegurado : listCapitalesAsegurados){
			   	for (CapitalDTSVariable capitalDTSVariables: capitalAsegurado.getCapitalDTSVariables()) {
			   		
			   		if (capitalDTSVariables.getCodconcepto().equals(BigDecimal.valueOf(codConcepto))) { //
			   		    idcapitalasegurado = capitalDTSVariables.getId();
			   			actualizamos = true;
			   			break; //salimos del bucle de datos variables
			    	}
			    }
			   	if (actualizamos) {
			   		updateDatoVariable(idcapitalasegurado ,valor);
			   	}else {
			   		insertDatoVariable(capitalAsegurado, codConcepto, valor);
			   		
			   	}
			}
			logger.debug("Fin: updateDatosVariables - AnexoModCambioMasivoSWDao");
			
		}catch (Exception e) {
			logger.error("Se ha producido un error al actualizar/insertar los datos variables: ", e);
			throw new DAOException();
		}	  
	}
	
	
	private void insertDatoVariable(CapitalAsegurado capitalasegurado, int codConcepto, String valor) 
			throws DAOException {
		try {
			
			Session session = obtenerSession();
			CapitalDTSVariable capitalDTSVariable = new CapitalDTSVariable();
			capitalDTSVariable.setCapitalAsegurado(capitalasegurado);
			capitalDTSVariable.setCodconcepto(BigDecimal.valueOf(codConcepto));
			capitalDTSVariable.setValor(valor);
			
			session.save(capitalDTSVariable);
			
		}catch (Exception e) {
			logger.error("Se ha producido un error al insertar los datos variables: ", e);
			throw new DAOException();
		}	  
		
	}

	private void updateDatoVariable(Long idcapitalasegurado, String valor) throws DAOException {
		try {
			
			Session session = obtenerSession();
			String sql = "update tb_anexo_mod_capitales_dts_vbl set valor = '" + valor+
					"' where id = " + idcapitalasegurado;
			session.createSQLQuery(sql).executeUpdate();
			
			
		}catch (Exception e) {
			logger.error("Se ha producido un error al actualizar los datos variables: ", e);
			throw new DAOException();
		}	  
		
	}

	private BigDecimal calculaProduccion (BigDecimal increHa,BigDecimal increParcela,
			CapitalAsegurado capitalAsegurado) {
		
		BigDecimal newProduccion = new BigDecimal(0);
		
		if (increHa!= null) {
			newProduccion = capitalAsegurado.getSuperficie().multiply(increHa);
			//newProduccion = incremento.add(capitalAsegurado.getProduccion());
		}else if (increParcela!= null) {
			newProduccion = increParcela;
		}
		
		if ( newProduccion.compareTo(new BigDecimal(0)) < 0)
        	newProduccion = new BigDecimal(0);
			 
		return newProduccion;	
	}

	
	/**
	 * Obtiene el campo "Aplicacion del rendimiento" para un capital asegurado y un modulo
	 * @param ca
	 * @param codModulo
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public String getAplRdto (CapitalAsegurado ca, String codModulo) {
			
		logger.debug("AnexoModCambioMasivoSWDao.getAplRdto - Inicio");
		
		// Carga los datos variables que se usaran para filtrar en la consulta
		Map<BigDecimal, String> mapa = new HashMap<BigDecimal, String>();
		// Sistema de cultivo
		mapa.put(new BigDecimal (123), null);
		// Practica cultural
		mapa.put(new BigDecimal (133), null);
		// Tipo de plantacion
		mapa.put(new BigDecimal (173), null);
		// Nº de anios desde poda
		mapa.put(new BigDecimal (617), null);
		// Edad
		mapa.put(new BigDecimal (111), null);
		// Tipo Marco Plantación
		mapa.put(new BigDecimal (116), null);
		
		for (CapitalDTSVariable dvp: ca.getCapitalDTSVariables()) {
			if (mapa.containsKey(dvp.getCodconcepto())) {
				mapa.put(dvp.getCodconcepto(), dvp.getValor());
			}
		}
		
		String select = " select apprdto from tb_sc_c_limites_rdtos ";
		
		// Monta el where con los datos identificativos de la parcela
		String where = " WHERE lineaseguroid =" + ca.getParcela().getAnexoModificacion().getPoliza().getLinea().getLineaseguroid();
		where += " AND codmodulo = '" + codModulo + "'";
		where += " AND codcultivo in (" + ca.getParcela().getCodcultivo() + ",999)";
		where += " AND codvariedad in (" + ca.getParcela().getCodvariedad() + ",999)";
		where += " AND codprovincia in (" + ca.getParcela().getCodprovincia() + ",99)";
		where += " AND codcomarca in (" + ca.getParcela().getCodcomarca() + ",99)";
		where += " AND codtermino in (" + ca.getParcela().getCodtermino() + ",999)";
		where += " AND subtermino in ('" + ca.getParcela().getSubtermino() + "','9') ";
		
		// Monta el where con los datos variables que vengan informados
		where += (mapa.get(new BigDecimal (123)) != null) ? (" AND codsistemacultivo = " + mapa.get(new BigDecimal (123))) : ("");
		where += (mapa.get(new BigDecimal (133)) != null) ? (" AND codpracticacultural = " + mapa.get(new BigDecimal (133))) : ("");
		where += (mapa.get(new BigDecimal (173)) != null) ? (" AND codtipoplantacion = " + mapa.get(new BigDecimal (173))) : ("");
		where += (mapa.get(new BigDecimal (617)) != null) ? (" AND numaniospoda = " + mapa.get(new BigDecimal (617))) : ("");
		where += (mapa.get(new BigDecimal (111)) != null) ? (" AND edaddesde <= " + mapa.get(new BigDecimal (111)) + " AND edadhasta >= " + mapa.get(new BigDecimal (111))) : ("");
		where += (mapa.get(new BigDecimal (116)) != null) ? (" AND CODTIPOMARCOPLANTAC = " + mapa.get(new BigDecimal (116))) : ("");
		
		String order = "order by codcultivo asc, codvariedad  asc, codprovincia asc, codtermino   asc, subtermino   desc";
		
		Session session = obtenerSession();
		
		String sql= select + where + order;
		
		logger.debug("Consulta: " + sql);
		
		List list = session.createSQLQuery(sql).list();
		
		return ((list.size() > 0) ? ((String)list.get(0)).toString() : (""));
			
	}
	public void cambiaEstadoParcela(List<Long> listParcelas) throws DAOException {
		try {
			if (listParcelas.size()>0) {
				Session session = obtenerSession();
				//parcelas
				String sql = "update tb_anexo_mod_parcelas set tipomodificacion = 'M' " +
						" where id in  " +StringUtils.toValoresSeparadosXComas(listParcelas, false, true)+" and (tipomodificacion != 'A' or tipomodificacion is null) and (tipomodificacion != 'B' or tipomodificacion is null)";
				session.createSQLQuery(sql).executeUpdate();
				//capitales asegurados
				String sql2 ="update tb_anexo_mod_capitales_aseg set tipomodificacion='M' where idparcelaanexo " +
						" in "+StringUtils.toValoresSeparadosXComas(listParcelas, false, true)+" and (tipomodificacion != 'A' or tipomodificacion is null) and (tipomodificacion != 'B' or tipomodificacion is null)";
				session.createSQLQuery(sql2).executeUpdate();
		
				//datos variables
				String sql3 ="update tb_anexo_mod_capitales_dts_vbl set tipomodificacion='M' where idcapitalasegurado in " +
						" ( select id from tb_anexo_mod_capitales_aseg " +
						" where idparcelaanexo in "+StringUtils.toValoresSeparadosXComas(listParcelas, false, true)+" ) and (tipomodificacion != 'A' or tipomodificacion is null) and (tipomodificacion != 'B' or tipomodificacion is null)";
				session.createSQLQuery(sql3).executeUpdate();
			}
		}catch (Exception e) {
			logger.error("Se ha producido un error al actualizar los datos variables: ", e);
			throw new DAOException();
		}	  
		
		
	}

	@Override
	public void setEdad(AnexoModSWCambioMasivo amCm, Parcela parcelaAnexo,
			boolean incrementoEdad) throws DAOException {
		boolean enco = false;
    	
		Set<CapitalAsegurado> listCapitalesAsegurados = parcelaAnexo.getCapitalAsegurados();
    
		try {
			logger.debug("Init: setEdad - AnexoModCambioMasivoSWDao");
			
			for(CapitalAsegurado capitalAsegurado : listCapitalesAsegurados){
			   	for (CapitalDTSVariable capitalDTSVariables: capitalAsegurado.getCapitalDTSVariables()) {
		
			   		if (capitalDTSVariables.getCodconcepto().equals(BigDecimal.valueOf(ConstantsConceptos.CODCPTO_EDAD))) { //
			   			enco = true;
			   			if (incrementoEdad){
			   				Integer sum = Integer.parseInt(capitalDTSVariables.getValor()) + Integer.parseInt(amCm.getIncEdad_cm());
			   				capitalDTSVariables.setValor(sum.toString());
			   			}else{
			   				capitalDTSVariables.setValor(amCm.getEdad_cm());
			   			}
			   		}
			   	}
			
			
				if(!enco){
	        		CapitalDTSVariable capitalDTSVariable = new CapitalDTSVariable();
	        		capitalDTSVariable.setCapitalAsegurado(capitalAsegurado);
	    	    	if (incrementoEdad)
	    	    		capitalDTSVariable.setValor(amCm.getIncEdad_cm());
	    	    	else
	    	    		capitalDTSVariable.setValor(amCm.getEdad_cm());
	    	    	capitalDTSVariable.setCodconcepto(BigDecimal.valueOf(ConstantsConceptos.CODCPTO_EDAD));
	    			
	    	    	saveOrUpdate(capitalDTSVariable);
	        	}
			}
    	}catch (Exception e) {
			logger.error("Se ha producido un error al actualizar/insertar la edad en datos variables: ", e);
			throw new DAOException();
		}	  
	}

	@Override
	public HashMap<String,String> updateUbicacion(AnexoModSWCambioMasivo amCm,
			Parcela parcelaAnexo,HashMap<String,String> mensajesError) 
					throws DAOException {
		
		
		try{
			Session session = obtenerSession();
			String sqlSet="";
			StringBuffer updatePar = new StringBuffer();
			String sql = "update tb_anexo_mod_parcelas a set ";
			
			Criteria c = session.createCriteria(Termino.class);
			
			c.add(Restrictions.eq("id.codprovincia", amCm.getTermino_cm().getId().getCodprovincia()!=null 
													? amCm.getTermino_cm().getId().getCodprovincia()
												    : parcelaAnexo.getCodprovincia()));
			c.add(Restrictions.eq("id.codcomarca", amCm.getTermino_cm().getId().getCodcomarca()!=null
													? amCm.getTermino_cm().getId().getCodcomarca()
													: parcelaAnexo.getCodcomarca()));
			c.add(Restrictions.eq("id.codtermino", amCm.getTermino_cm().getId().getCodtermino()!=null 
													? amCm.getTermino_cm().getId().getCodtermino()
												    : parcelaAnexo.getCodtermino()));
			c.add(Restrictions.eq("id.subtermino", amCm.getSubtermino_cm()!=null 
													? amCm.getSubtermino_cm()
												    : parcelaAnexo.getSubtermino() ));
			
			// comprobamos si existe en la tabla de terminos
			// si no existe lanzamos una excepcion controlada
			if (c.list().size()==0){
				mensajesError .put ("alerta","La ubicaci�n resultante es incorrecta");
			}else{
				if (!StringUtils.nullToString(amCm.getSubtermino_cm()).equals("")){
					sqlSet+= " subtermino = '" + amCm.getSubtermino_cm() +"' ,";
					
				}
				if (!StringUtils.nullToString(amCm.getTermino_cm().getId().getCodtermino()).equals("")){
					sqlSet+= " codtermino = " + amCm.getTermino_cm().getId().getCodtermino()+ ",";
							
				}
				if (!StringUtils.nullToString(amCm.getTermino_cm().getId().getCodcomarca()).equals("")){
					sqlSet+= " codcomarca = " + amCm.getTermino_cm().getId().getCodcomarca() +",";
				
				}
				if (!StringUtils.nullToString(amCm.getTermino_cm().getId().getCodprovincia()).equals("")){
					sqlSet+= " codprovincia = " + amCm.getTermino_cm().getId().getCodprovincia();
				}
				
				
				String whereParc= " where id in "+ parcelaAnexo.getId();
				updatePar.append(sql).append(sqlSet).append(whereParc).toString();
				
				logger.debug("updateParcelaAnexo - sql parcelas: " + updatePar.toString());
				
				session.createSQLQuery(updatePar.toString()).executeUpdate();
			}
			
			logger.debug("Fin: updateUbicacion - AnexoModCambioMasivoSWDao");
		
		}catch (Exception e) {
			logger.error("Se ha producido un error al actualizar la ubicacion de las parcelas del anexo: ", e);
			throw new DAOException();
		}
		return mensajesError;
			
	}

	@Override
	public void updateSigpac(AnexoModSWCambioMasivo amCm, Parcela parcelaAnexo)
			throws DAOException {
		try{	
			String sql = "update tb_anexo_mod_parcelas a set ";
			String sqlwhere =" where id in "+ parcelaAnexo.getId();
			String sqlSet="";
			
			if(!StringUtils.nullToString(amCm.getProvSig_cm()).equals("")){
		    	sqlSet= "CODPROVSIGPAC = " + amCm.getProvSig_cm();
		    	executeupdate(sql + sqlSet + sqlwhere);
		    }
			
			if(!StringUtils.nullToString(amCm.getTermSig_cm()).equals("")){
		    	sqlSet= "CODTERMSIGPAC = " + amCm.getTermSig_cm();
		    	executeupdate(sql + sqlSet + sqlwhere);
		    }
		    
		    if(!StringUtils.nullToString(amCm.getAgrSig_cm()).equals("")){
		    	sqlSet= "AGRSIGPAC = " + amCm.getAgrSig_cm();
		    	executeupdate(sql + sqlSet + sqlwhere);
		    }
		    
		    if(!StringUtils.nullToString(amCm.getZonaSig_cm()).equals("")){
		    	sqlSet= "ZONASIGPAC = " + amCm.getZonaSig_cm();
		    	executeupdate(sql + sqlSet + sqlwhere);
		    }
		    
		    if(!StringUtils.nullToString(amCm.getPolSig_cm()).equals("")){
		    	sqlSet= "POLIGONOSIGPAC = " + amCm.getPolSig_cm();
		    	executeupdate(sql + sqlSet + sqlwhere);
		    }
		   
		    if(!StringUtils.nullToString(amCm.getParcSig_cm()).equals("")){
		    	sqlSet= "PARCELASIGPAC = " + amCm.getParcSig_cm();
		    	executeupdate(sql + sqlSet + sqlwhere);
		    }
		    
		    if(!StringUtils.nullToString(amCm.getRecSig_cm()).equals("")){
		    	sqlSet= "RECINTOSIGPAC = " + amCm.getRecSig_cm();
		    	executeupdate(sql + sqlSet + sqlwhere);
		    }
		    
		}catch (Exception e) {
			logger.error("Se ha producido un error al actualizar el SIGPAC de las parcelas del anexo: ", e);
			throw new DAOException();
		}
	}
	
	private void executeupdate (String updatePar) throws DAOException{
		Session session = obtenerSession();
		
		try{
			session.createSQLQuery(updatePar.toString()).executeUpdate();
		}catch (Exception e) {
			logger.error("Se ha producido un error al actualizar el SIGPAC de las parcelas del anexo: ", e);
			throw new DAOException();
		}
		
	}

	@Override
	public void updatePrecio(BigDecimal precio_cm, Parcela parcelaAnexo)
			throws DAOException {
		try {
			logger.debug("Init: updatePrecio - AnexoModCambioMasivoSWDao");
			Session session = obtenerSession();	
			
			Set<CapitalAsegurado> listCapitalesAsegurados = parcelaAnexo.getCapitalAsegurados();

			for(CapitalAsegurado capitalAsegurado : listCapitalesAsegurados){
				String sql = "update tb_anexo_mod_capitales_aseg set precio ="+ precio_cm +	    			    					
						" where id = "+ capitalAsegurado.getId();
				
			
				session.createSQLQuery(sql).executeUpdate();
			}
			logger.debug("Fin: updatePrecio - AnexoModCambioMasivoSWDao");   
		
		}catch (Exception e) {
			logger.error("Se ha producido un error al actualizar el precio: ", e);
			throw new DAOException();
		}	  
		
		
	}

	
}
