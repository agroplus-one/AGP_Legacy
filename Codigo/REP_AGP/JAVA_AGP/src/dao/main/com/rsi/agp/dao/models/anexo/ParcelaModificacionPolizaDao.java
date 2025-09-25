package com.rsi.agp.dao.models.anexo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.filters.poliza.CapitalAseguradoAnexoFiltro;
import com.rsi.agp.dao.filters.poliza.ParcelaAnexoFiltro;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.tables.anexo.CapitalAsegurado;
import com.rsi.agp.dao.tables.anexo.CapitalDTSVariable;
import com.rsi.agp.dao.tables.anexo.Parcela;
import com.rsi.agp.dao.tables.config.PantallaConfigurable;
import com.rsi.agp.dao.tables.cpl.Modulo;
import com.rsi.agp.dao.tables.cpl.RiesgoCubiertoModulo;
import com.rsi.agp.dao.tables.cpl.SubvencionCCAA;
import com.rsi.agp.dao.tables.cpl.SubvencionEnesa;
import com.rsi.agp.dao.tables.poliza.ComparativaPoliza;
import com.rsi.agp.dao.tables.poliza.SWModulosCobParcelaAnexo;

public class ParcelaModificacionPolizaDao extends BaseDaoHibernate implements IParcelaModificacionPolizaDao{
	
	private final Log logger = LogFactory.getLog(getClass());

	/**
	 * Obtiene un listado filtrado de parcelas
	 */
	public List<CapitalAsegurado> list(CapitalAsegurado capitalAsegurado) throws DAOException {	
			Session session = obtenerSession();
			try{		
				
				Criteria criteria = session.createCriteria(com.rsi.agp.dao.tables.anexo.CapitalAsegurado.class);
				
				if(capitalAsegurado.getParcela() != null){
					if(capitalAsegurado.getParcela().getAnexoModificacion().getId() != null)
						criteria.add(Restrictions.eq("par.anexoModificacion.id", capitalAsegurado.getParcela().getAnexoModificacion().getId()));
					
					if(capitalAsegurado.getParcela().getId() != null)
						criteria.add(Restrictions.eq("par.id", capitalAsegurado.getParcela().getId()));
					
					if(capitalAsegurado.getParcela().getHoja() != null)
						criteria.add(Restrictions.eq("par.hoja", capitalAsegurado.getParcela().getHoja()));
					
					if(capitalAsegurado.getParcela().getNumero() != null)
						criteria.add(Restrictions.eq("par.numero", capitalAsegurado.getParcela().getNumero()));
					
					if(capitalAsegurado.getParcela().getPoligono() != null && !capitalAsegurado.getParcela().getPoligono().equals(""))
						criteria.add(Restrictions.eq("par.poligono", capitalAsegurado.getParcela().getPoligono()));
					
					if(capitalAsegurado.getParcela().getParcela_1() != null && !capitalAsegurado.getParcela().getParcela_1().equals(""))
						criteria.add(Restrictions.eq("par.parcela_1", capitalAsegurado.getParcela().getParcela_1()));
					
					if(capitalAsegurado.getParcela().getCodprovsigpac() != null)
						criteria.add(Restrictions.eq("par.codprovsigpac", capitalAsegurado.getParcela().getCodprovsigpac()));
					
					if(capitalAsegurado.getParcela().getCodtermsigpac() != null)
						criteria.add(Restrictions.eq("par.codtermsigpac", capitalAsegurado.getParcela().getCodtermsigpac()));
					
					if(capitalAsegurado.getParcela().getAgrsigpac() != null)
						criteria.add(Restrictions.eq("par.agrsigpac", capitalAsegurado.getParcela().getAgrsigpac()));

					if(capitalAsegurado.getParcela().getZonasigpac() != null)
						criteria.add(Restrictions.eq("par.zonasigpac", capitalAsegurado.getParcela().getZonasigpac()));
					
					if(capitalAsegurado.getParcela().getPoligonosigpac() != null)
						criteria.add(Restrictions.eq("par.poligonosigpac", capitalAsegurado.getParcela().getPoligonosigpac()));
					
					if(capitalAsegurado.getParcela().getParcelasigpac() != null)
						criteria.add(Restrictions.eq("par.parcelasigpac", capitalAsegurado.getParcela().getParcelasigpac()));
					
					if(capitalAsegurado.getParcela().getRecintosigpac() != null)
						criteria.add(Restrictions.eq("par.recintosigpac", capitalAsegurado.getParcela().getRecintosigpac()));
					
					if(capitalAsegurado.getParcela().getNomparcela() != null && !capitalAsegurado.getParcela().getNomparcela().equals(""))
						criteria.add(Restrictions.eq("par.nomparcela", capitalAsegurado.getParcela().getNomparcela()));
					
					if(capitalAsegurado.getParcela().getCodprovincia() != null)
						criteria.add(Restrictions.eq("par.codprovincia", capitalAsegurado.getParcela().getCodprovincia()));
					
					if(capitalAsegurado.getParcela().getCodcomarca() != null)
						criteria.add(Restrictions.eq("par.codcomarca", capitalAsegurado.getParcela().getCodcomarca()));
					
					if(capitalAsegurado.getParcela().getCodtermino() != null)
						criteria.add(Restrictions.eq("par.codtermino", capitalAsegurado.getParcela().getCodtermino()));
					
					if(capitalAsegurado.getParcela().getSubtermino() != null)
						criteria.add(Restrictions.eq("par.subtermino", capitalAsegurado.getParcela().getSubtermino()));
					
					if(capitalAsegurado.getParcela().getCodcultivo() != null)
						criteria.add(Restrictions.eq("par.codcultivo", capitalAsegurado.getParcela().getCodcultivo()));
					
					if(capitalAsegurado.getParcela().getCodvariedad() != null)
						criteria.add(Restrictions.eq("par.codvariedad", capitalAsegurado.getParcela().getCodvariedad()));
					
					if(capitalAsegurado.getParcela().getTipomodificacion() != null && !capitalAsegurado.getParcela().getTipomodificacion().toString().equals("T"))
						criteria.add(Restrictions.eq("par.tipomodificacion", capitalAsegurado.getParcela().getTipomodificacion()));
				}
				
				if(capitalAsegurado.getSuperficie() != null)
					criteria.add(Restrictions.eq("superficie", capitalAsegurado.getSuperficie()));
				
				if(capitalAsegurado.getProduccion() != null)
					criteria.add(Restrictions.eq("produccion", capitalAsegurado.getProduccion()));
				
				if(capitalAsegurado.getTipoCapital()!= null && capitalAsegurado.getTipoCapital().getCodtipocapital()!=null)
					criteria.add(Restrictions.eq("tipoCapital.codtipocapital", capitalAsegurado.getTipoCapital().getCodtipocapital()));
				
				
				criteria.createAlias("parcela","par");
				
				return criteria.list();
				
				
			}catch(Exception ex){
				throw new DAOException("Se ha producido un error durante el acceso a la base de datos", ex);
			}finally {
			}
     }


	/**
	 * Set del estado de una lista de parcelas del anexo
	 * @param idsParcelasAnexo
	 * @throws DAOException
	 */
	public void setEstadoParcelas(List<Parcela> parcelasAnexo, Character estadoParcela) throws DAOException {

		try{
			Session session = obtenerSession();
			for(Parcela parcela : parcelasAnexo){
			    parcela.setTipomodificacion(estadoParcela);                   // Actualizo el estado
			    
			    Iterator<CapitalAsegurado> capitalesIterator = parcela.getCapitalAsegurados().iterator(); 
			    while(capitalesIterator.hasNext()) {
			    	CapitalAsegurado capitalAsegurado = capitalesIterator.next();
			    	capitalAsegurado.setTipomodificacion(estadoParcela);
			    	Iterator<CapitalDTSVariable> capitalDTSVariableIterator = capitalAsegurado.getCapitalDTSVariables().iterator();
			    	
			    	while(capitalDTSVariableIterator.hasNext()){
			    		capitalDTSVariableIterator.next().setTipomodificacion(estadoParcela);
			    	}
			    }
			    
			    this.evict(parcela);
			    session.saveOrUpdate(parcela); 
			}
		}
		catch(Exception excepcion) {
			logger.error("[Error al deshacer]",excepcion);
			throw new DAOException("Error al deshacer:",excepcion);
		}
	}
	
	
	/**
	 * Deshace una lista de parcelas, PARCELA o INSTALACIÓN
	 * @param idsParcelasAnexo
	 * @throws DAOException
	 */
	public void deshacerParcelas(List<Long> idsParcelasAnexo) throws DAOException {
		
		// Obtengo la lista de objetos "Parcela" ordenados de forma que primero vengan las instalaciones
		// para poder tratarlas primero
		List<Parcela> parcelas = this.getParcelasAnexo(idsParcelasAnexo, "tipoparcela", Constants.ORDEN_ASCENDENTE);

		try{
			for(Parcela parcela : parcelas){
				if(parcela.getTipomodificacion() != null){
					if(parcela.getTipomodificacion().equals(Constants.BAJA) || 
							parcela.getTipomodificacion().equals(Constants.MODIFICACION)){
						boolean isCopy = false;
						if(Constants.TIPO_PARCELA_INSTALACION.equals(parcela.getTipoparcela())){
							// Si es instalacion --> recupero la original
							if(parcela.getAnexoModificacion().getIdcopy() != null)
								isCopy = true;
							
							copiarParcelaToAnexo(parcela.getId(), parcela.getAnexoModificacion().getId(), 
									isCopy ? (parcela.getIdcopyparcela()) : (parcela.getParcela().getIdparcela()), isCopy, parcela.getIdparcelaanxestructura());
						}
						else{
							//DAA 23/04/12
							// Si es parcela --> recupero la parcela original y sus instalaciones originales
							// Si tiene copy recupero la parcela de la copy y si no de la poliza.
							Long idParcelaOrigen = null;
							String propiedad = null;
							Parcela parcelaaux = null;
							List<Parcela> listInstalaciones = this.getObjects(Parcela.class,"idparcelaanxestructura", parcela.getId());
							
							// --- recupero la parcela original ---
							if(parcela.getAnexoModificacion().getIdcopy() != null){
								// Tiene copy, recupero la parcela original (sin modificar) de la copy.
								idParcelaOrigen = parcela.getIdcopyparcela();
								propiedad = "idcopyparcela";
								isCopy = true;
							}
							else {
								// No tiene copy, recupero la parcela original (sin modificar) de la poliza.
								idParcelaOrigen = parcela.getParcela().getIdparcela();
								propiedad = "parcela.idparcela";
							}
							copiarParcelaToAnexo(parcela.getId(), parcela.getAnexoModificacion().getId(), 
									isCopy ? (parcela.getIdcopyparcela()) : (parcela.getParcela().getIdparcela()), isCopy, null);
							
							parcelaaux = (Parcela)this.getObjects(Parcela.class, propiedad, idParcelaOrigen).get(0);

							// --- recupero las instalaciones originales ---
							for(Parcela instalacion : listInstalaciones){
								copiarParcelaToAnexo(parcela.getId(),instalacion.getAnexoModificacion().getId(), 
										isCopy ? (instalacion.getIdcopyparcela()) : (instalacion.getParcela().getIdparcela()), isCopy, parcelaaux.getId());
							}
						}
					}
					else if(parcela.getTipomodificacion().equals(Constants.ALTA)){

						//eliminar sus instalaciones si las tiene
						List<Parcela> listInstalaciones = this.getObjects(Parcela.class,"idparcelaanxestructura", parcela.getId());
						for (Parcela instalacion : listInstalaciones){
							this.evict(instalacion);
							this.delete(instalacion);
						}
						
						this.evict(parcela);
						this.delete(parcela); //eliminar parcela del anexo
						
						//Renumeramos por si hay parcelas de alta que tengan la numeracion mayor que la que estamos eliminando
						renumerarHoja(parcela);
					}
				}
			}
		}
		catch(Exception excepcion) {
			logger.error("Error en deshacerParcelas",excepcion);
			throw new DAOException("Error al deshacer:",excepcion);
		}
	}
	
	/**
	 * Método para obtener un listado de parcelas de anexo ordenadas por "campoOrden" según indique "tipoOrden"
	 * @param idsParcelasAnexo Identificadores de las parcelas
	 * @param campoOrden Campo para ordenar
	 * @param tipoOrden Tipo de ordenación: ascendente o descendente
	 * @return
	 */
	private List<Parcela> getParcelasAnexo(List<Long> idsParcelasAnexo, String campoOrden, String tipoOrden) {
		
		Session session = this.obtenerSession();
		Criteria criteria = session.createCriteria(Parcela.class);
		criteria.add(Restrictions.in("id", idsParcelasAnexo));
		
		if (tipoOrden.equals(Constants.ORDEN_ASCENDENTE)){
			criteria.addOrder(Order.asc(campoOrden));
		}
		else{
			criteria.addOrder(Order.desc(campoOrden));
		}
		
		List<Parcela> parcelas = criteria.list();
		
		return parcelas;
	}


	/**
	 * Renumera las parcelas e instalaciones al dar de baja una parcela o instalacion
	 * de su hoja que previamente habia sido dada de ALTA. Para el caso de modificaciones y bajas no se renumera
	 * porque la hoja y el numero que venga de la parcela o de la copy es correcta.
	 * @param parcela a eliminar, de aqui sacamos la hoja
	 */
	public void renumerarHoja(Parcela parcela) throws DAOException{

		try{
			BigDecimal hoja = parcela.getHoja();        // hoja de donde se renumera
			BigDecimal nume = parcela.getNumero();      // numero que desaparece
			
			//Obtenemos las parcelas de la misma hoja y cuyo número sea mayor que el actual
			Session session = this.obtenerSession();
			Criteria criteria = session.createCriteria(Parcela.class);
			criteria.add(Restrictions.eq("anexoModificacion.id",parcela.getAnexoModificacion().getId()));
			criteria.add(Restrictions.eq("hoja", hoja));
			criteria.add(Restrictions.gt("numero", nume));

			criteria.addOrder(Order.asc("numero"));
        	List<Parcela> listParcelas = criteria.list();
        	
			//Recorremos la lista de parcelas y le asignamos el nuevo número
        	int numNuevo = nume.intValue();
        	for (Parcela par : listParcelas){
        		par.setNumero(new BigDecimal(numNuevo));
        		this.evict(par);
				this.saveOrUpdate(par);
				numNuevo++;
        	}
		}catch(Exception ex){
			logger.error("Errror al renumerar las parcelas del anexo " + parcela.getAnexoModificacion().getId(), ex);
			throw new DAOException("Errror al renumerar hoja", ex);
		}
	}
	
	/**
	 * Copia una parcela de la copy o de la poliza al anexo
	 * @param idParcelaAnexo
	 * @param  idAnexo
	 * @param  isCopy
	 * @param  refParcela
	 * @throws DAOException
	 * (se usa un PL/SQL para la realizacion de esta tarea)
	 */
	private void copiarParcelaToAnexo(Long idParcelaAnexo, Long idAnexo, Long idParcelaOrigen, boolean isCopy, Long refParcela)throws DAOException{
		try{
            String procedure = "";
            
			if(isCopy)
				procedure = "PQ_COPIAR_PARCELAS_A_ANEXO.copyParcelaFromCopyToAnexo(COD_PARCELA_ANEXO IN NUMBER,COD_PARCELA_ORIGEN IN NUMBER,ID_ANEXO IN NUMBER,REFPARCELA IN NUMBER)";
			else
				procedure = "PQ_COPIAR_PARCELAS_A_ANEXO.copyParcelaFromPolizaToAnexo(COD_PARCELA_ANEXO IN NUMBER,COD_PARCELA_ORIGEN IN NUMBER,ID_ANEXO IN NUMBER,REFPARCELA IN NUMBER)";
			
			Map<String, Object> parametros = new HashMap<String, Object>(); // parámetros PL
			parametros.put("COD_PARCELA_ANEXO", idParcelaAnexo);
			parametros.put("COD_PARCELA_ORIGEN", idParcelaOrigen);
			parametros.put("ID_ANEXO", idAnexo);
			parametros.put("REFPARCELA", refParcela);
			databaseManager.executeStoreProc(procedure, parametros);        // ejecutamos PL
		}
		catch(Exception excepcion) {
			logger.error("[Excepcion en copiarParcelaToAnexo]",excepcion);
			throw new DAOException("Error al recuperar la parcela original(sin modificar)",excepcion);
		}
	}
	
	/**
	 * Copia las parcelas de la copy/poliza al anexo 
	 * (se usa un PL/SQL para la realizacion de esta tarea)
	 */
	public void copiarParcelasPolizaCopy(Long idAnexoModificacion) throws DAOException {
		try {
			String procedure = "PQ_COPIAR_PARCELAS_A_ANEXO.copiarParcelasEnAnexo(P_IDANEXO IN NUMBER)";
			Map<String, Object> parametros = new HashMap<String, Object>();// parámetros PL
			parametros.put("P_IDANEXO", idAnexoModificacion);
			databaseManager.executeStoreProc(procedure, parametros); // ejecutamos PL
			
			//Copiamos también las subvenciones
			GregorianCalendar gcIni = new GregorianCalendar();
			procedure = "PQ_COPIAR_SUBVENCIONES_A_ANEXO.copiarSubvencionesEnAnexo(P_IDANEXO IN NUMBER)";
			databaseManager.executeStoreProc(procedure, parametros);
			
			//DAA 24/07/2012 Optimizacion
			GregorianCalendar gcFin = new GregorianCalendar();
			Long tiempo = gcFin.getTimeInMillis() - gcIni.getTimeInMillis();
			logger.debug("Tiempo de Copia de Subvenciones: " + tiempo + " milisegundos");
		}
		catch(Exception excepcion) {
			logger.error("[Excepcion al lanzar el PL]",excepcion);
			throw new DAOException("Error  al copiar parcelas al anexo",excepcion);
		}
	}
	public List getPantallaVarAnexo(Long lineaseguroid, Long idPantalla){
			Session session  = obtenerSession();
			List<BigDecimal> list = null;
			
	        try{
	        	Criteria criteria = session.createCriteria(PantallaConfigurable.class);
	        	Criterion criterion1 = Restrictions.eq("linea.lineaseguroid",lineaseguroid);
	        	criteria.add(criterion1);
	        	Criterion criterion2 = Restrictions.eq("pantalla.idpantalla",idPantalla);
	        	criteria.add(criterion2);
	        	list = criteria.list();
			}
			catch(Exception excepcion){
				logger.error(excepcion);
			}
			return list;
	}
	
	
	/**
	 * Comprueba si existen parcelas en el anexo
	 */
	public boolean existenParcelasEnAnexo(Long idAnexoModificacion) throws DAOException {
		boolean result = false;
		Session session = obtenerSession();
		
		try{
			String sql= "select count(*) from TB_ANEXO_MOD_PARCELAS p where p.idanexo = " + idAnexoModificacion;
			List list = session.createSQLQuery(sql).list();
			
        	if(((BigDecimal)list.get(0) ).intValue() > 0)
        		result = true;
        	else
        		result = false;
		}
		catch(Exception excepcion){
			logger.error("Error al comprobar si existen parcelas en el anexo",excepcion);
			throw new DAOException("Error al  copiar parcelas al anexo",excepcion);
		}

		return result;
	}
	
	/**
	 * Obtiene la lista de parcelas de un anexo, por el id del anexo
	 */
	public List<Parcela> getParcelasAnexo(Long idAnexo) throws DAOException {
		List<Parcela> listParcelasAnexo = null;
		Session session = obtenerSession();
        try {
            Criteria criteria = session.createCriteria(com.rsi.agp.dao.tables.anexo.Parcela.class);
        	Criterion criterion1 = Restrictions.eq("anexoModificacion.id",idAnexo);
        	//DAA 11/04/12 Añadimos criterios de busqueda
        	//Order criterion2 = Order.desc("hoja");
        	//Order criterion3 = Order.desc("numero");

        	criteria.addOrder(Order.asc("codprovsigpac"));
    		criteria.addOrder(Order.asc("codtermsigpac"));
    		criteria.addOrder(Order.asc("agrsigpac"));
    		criteria.addOrder(Order.asc("zonasigpac"));
    		criteria.addOrder(Order.asc("poligonosigpac"));
    		criteria.addOrder(Order.asc("parcelasigpac"));
    		criteria.addOrder(Order.asc("recintosigpac"));
    		
    		criteria.addOrder(Order.asc("codcultivo"));
    		criteria.addOrder(Order.asc("codvariedad"));
    		
    		criteria.addOrder(Order.desc("tipoparcela"));
    		
        	criteria.add(criterion1);
        	//criteria.addOrder(criterion2);
        	//criteria.addOrder(criterion3);
        	listParcelasAnexo = criteria.list();
		}
		catch(Exception excepcion) {
			logger.error("Error  al obtener las parcelas del anexo",excepcion);
			throw new DAOException("Error al copiar  parcelas al anexo",excepcion);
		}

		return listParcelasAnexo;
	}
	/**
	 * Obtiene la lista de parcelas de un anexo, aplicando un filtro
	 */
	public List<Parcela> getParcelasAnexo(Parcela parcela) throws DAOException {
		List<Parcela> listParcelasAnexo = null;
        try {
        	
        	ParcelaAnexoFiltro filter= new ParcelaAnexoFiltro(parcela);
        	listParcelasAnexo =  this.getObjects(filter);	
		}
		catch(Exception excepcion) {
			logger.error("Error al  obtener las parcelas del anexo",excepcion);
			throw new DAOException("Error al copiar parcelas  al anexo",excepcion);
		}
		
		return listParcelasAnexo;
	}
	
	/**
	 * Obtiene la lista de parcelas de un anexo, aplicando un filtro
	 */
	public List<Parcela> getParcelasAnexo(CapitalAsegurado capitalAsegurado,String columna,String orden) throws DAOException {
		List<Parcela> listParcelasAnexo = null;
        try {
 
        	CapitalAseguradoAnexoFiltro filter= new CapitalAseguradoAnexoFiltro(capitalAsegurado,columna,orden);
        	listParcelasAnexo =  this.getObjects(filter);
		}
		catch(Exception excepcion) {
			logger.error("Error al obtener  las parcelas del anexo",excepcion);
			throw new DAOException("Error al obtener las  parcelas del anexo",excepcion);
		}
		
		return listParcelasAnexo;
	}
	
	/**
	 * 
	 */
	public List<Parcela> listAux(Parcela parcela) throws DAOException {
		List<Parcela> listParcelasModificadas = null;
		Session session = obtenerSession();
        try{
        	Criteria criteria = session.createCriteria(com.rsi.agp.dao.tables.anexo.Parcela.class);
        	
        	Criterion criterion1 = Restrictions.eq("anexoModificacion.id",parcela.getAnexoModificacion().getId());
        	criteria.add(criterion1);
        	
        	Criterion criterion2 = Restrictions.eq("codprovincia",parcela.getCodprovincia());
        	criteria.add(criterion2);
        	
        	listParcelasModificadas = criteria.list();
 
		}
		catch(Exception excepcion) {
			logger.error("Error al copiar parcelas al  anexo",excepcion);
			throw new DAOException("Error al copiar parcelas al anexo:",excepcion);
		}
		finally {
		}
		return listParcelasModificadas;	
	}
	
	public List getModulosPoliza(Long idPoliza) throws BusinessException {
		List lista = null;
		lista =this.getObjectsBySQLQuery("select codmodulo from tb_modulos_poliza t where idpoliza="+idPoliza);
		return lista;
	}
	
	public List<ComparativaPoliza> getModulosPolizaWithComparativa(Long idPoliza) throws BusinessException {
		List<ComparativaPoliza> listComparativasPoliza = null;
		Session session  = obtenerSession();

		try{
        	Criteria criteria = session.createCriteria(ComparativaPoliza.class);
		    // idpoliza y codvalor = -1(seleccionada)
    		criteria.add(Restrictions.eq("poliza.idpoliza", idPoliza));
    		
        	listComparativasPoliza = criteria.list();
		}
		catch(Exception excepcion){
			logger.error("Se ha producido un error al recuperar los modulos de la poliza",excepcion);
		}
		finally{  }
		
		return listComparativasPoliza;
	}
	
	public List<RiesgoCubiertoModulo> getRiesgosCubiertosModulo(Long lineaseguroid, String codmodulo){
		logger.debug("init - [DatosParcelaFLDao] getRiesgosCubiertosModulo");
		
		Session session = obtenerSession();
		List<RiesgoCubiertoModulo> lista = new ArrayList<RiesgoCubiertoModulo>();
		
		try{
			
			Criteria criteria =	session.createCriteria(RiesgoCubiertoModulo.class);
			criteria.add(Restrictions.eq("id.lineaseguroid", lineaseguroid));
			criteria.add(Restrictions.eq("id.codmodulo", codmodulo));
			lista = criteria.list();
		
		} catch (Exception e) {
			logger.fatal("[DAOException - sin throw][DatosParcelaFLDao][getRiesgosCubiertosModulo]Error lectura BD", e);
		}
		
		logger.debug("end - [DatosParcelaFLDao] getRiesgosCubiertosModulo");
		return lista;
	}
	
	public List<Modulo> getCoberturasNivelParcela(List<String> listCodigosModulos, Long lineaseguroid) throws DAOException {
		Session session = obtenerSession();
		Criteria criteria;
		
		try
		{
			criteria = session.createCriteria(Modulo.class);
			if(listCodigosModulos.size() == 0){
				listCodigosModulos.add("99999");
			}
			
			Criterion criterion = Restrictions.conjunction()//and
        	.add(Restrictions.in("id.codmodulo", listCodigosModulos))
        	.add(Restrictions.eq("linea.lineaseguroid", lineaseguroid));		
    		criteria.add(criterion);

			
		} catch (Exception e) {
			throw new DAOException ("[DatosParcelaFLDao][getSubvencionesCCAANivelParcela]error lectura BD",e);
		}
		
		return criteria.list();
	
	}
	
	public List<SubvencionCCAA> getSubvencionesCCAANivelParcela(List<String> listCodigosModulos, Long lineaseguroid) throws DAOException {
		Session session = obtenerSession();
		Criteria criteria;
		
		try
		{
			criteria = session.createCriteria(SubvencionCCAA.class);
			if(listCodigosModulos.size() == 0){
				listCodigosModulos.add("99999");
			}
			
			Criterion criterion = Restrictions.conjunction()//and
        	.add(Restrictions.in("modulo.id.codmodulo", listCodigosModulos))
        	.add(Restrictions.eq("id.lineaseguroid", lineaseguroid));		
    		criteria.add(criterion);

			
		} catch (Exception e) {
			throw new DAOException ("[DatosParcelaFLDao][getSubvencionesCCAANivelParcela]error lectura BD",e);
		}
		
		return criteria.list();
	}
	
	public List<SubvencionEnesa> getSubencionesEnesaNivelParcela(List<String> listCodigosModulos, Long lineaseguroid) throws DAOException {
		Session session = obtenerSession();
		Criteria criteria;
		
        try
        {
        	criteria = session.createCriteria(SubvencionEnesa.class);
        	if(listCodigosModulos.size() == 0){
				listCodigosModulos.add("99999");
			}
        	
            Criterion criterion = Restrictions.conjunction()//and
        	.add(Restrictions.in("modulo.id.codmodulo", listCodigosModulos))
        	.add(Restrictions.eq("id.lineaseguroid", lineaseguroid));		
    		criteria.add(criterion);
        	
			
		} catch (Exception e) {
			throw new DAOException ("[DatosParcelaFLDao][getSubencionesEnesaNivelParcela]error lectura BD",e);
		}
		
		return criteria.list();
	}
	
	/** DAA 04/01/2013
	 * Obtiene una parcelaAnexo de la BD
	 * @param idParcela: PK en la BD
	 */
	public Parcela getParcelaAnexo(Long id) {
		
		Session session = obtenerSession();
		Parcela parcela = null;
		
		try{
			parcela  = (Parcela)session.get(Parcela.class,id);
		}
		catch(Exception excepcion){
			logger.error("Excepcion : ParcelaModificacionPolizaDao - getParcelaAnexo", excepcion);
		}
		finally{
		}
		return parcela;
		
	}
	
	/* Pet. 78691 ** MODIF TAM (22/12/2021) ** Inicio */
	/** DAA 04/01/2013
	 * Obtiene una parcelaAnexo de la BD
	 * @param idParcela: PK en la BD
	 * @throws DAOException 
	 */
	public String getdescSistCultivo(String sistCult) throws DAOException {
		
		Session session = obtenerSession();
		String descSistCultivo = "";
		
		try{
			String sql = "select sistCul.Dessistemacultivo " +
					  " from o02agpe0.tb_sc_c_sistema_cultivo  sistCul " +
					  " where sistCul.Codsistemacultivo = " + sistCult;
			
			List list = session.createSQLQuery(sql).list();
			
			descSistCultivo = (String) list.get(0);
			
		}catch(Exception excepcion){
			logger.error("Error al recuperar la descripci�n del Sistema de cultivo",excepcion);
			throw new DAOException("Error al recuperar la descripci�n del Sistema de cultivo",excepcion);
		}
		
		return descSistCultivo;
	}
	
	/* Pet. 78691 ** MODIF TAM (22/12/2021) ** Fin */


	/** DAA 19/12/2012 recupero todas las parcelas del anexo con el mismo sigpac, que no sea otra instalacion 
	 *  que no este ya de baja y que no sea la que quiero dar de baja
	 * 
	 */
	public List<Parcela> getParcelasAnexoMismoSigpac(Parcela parcelaBaja,Long idAnexo) {
		
		logger.debug("init - [ParcelaModificacionPolizaDao] getParcelasAnexoMismoSigpac");
		
		Session session = obtenerSession();
		List<Parcela> lista = new ArrayList<Parcela>();
		
		try{
			
			Criteria criteria =	session.createCriteria(Parcela.class);

			//criteria.createAlias("anexoModificacion","anexoModificacion");
			
			criteria.add(Restrictions.eq("codprovsigpac", parcelaBaja.getCodprovsigpac()));
			criteria.add(Restrictions.eq("codtermsigpac", parcelaBaja.getCodtermsigpac()));
			criteria.add(Restrictions.eq("agrsigpac", parcelaBaja.getAgrsigpac()));
			criteria.add(Restrictions.eq("zonasigpac", parcelaBaja.getZonasigpac()));
			criteria.add(Restrictions.eq("poligonosigpac", parcelaBaja.getPoligonosigpac()));
			criteria.add(Restrictions.eq("parcelasigpac", parcelaBaja.getParcelasigpac()));
			criteria.add(Restrictions.eq("recintosigpac", parcelaBaja.getRecintosigpac()));
			criteria.add(Restrictions.eq("codcultivo", parcelaBaja.getCodcultivo()));
			criteria.add(Restrictions.eq("codvariedad", parcelaBaja.getCodvariedad()));
			
			criteria.add(Restrictions.eq("anexoModificacion.id", idAnexo));
			criteria.add(Restrictions.eq("tipoparcela",'P'));
			criteria.add(Restrictions.not(Restrictions.eq("id", parcelaBaja.getId())));
			criteria.add(Restrictions.not(Restrictions.eq("tipomodificacion", 'B')));
			
			lista = criteria.list();
		
		} catch (Exception e) {
			logger.fatal("[DAOException - sin throw][SeleccionPolizaDao][validarEliminarParcela]Error lectura BD", e);
		}
		
		logger.debug("end - [ParcelaModificacionPolizaDao] getParcelasAnexoMismoSigpac");
		return lista;
	
	}
	
	/** DAA 19/12/2012
	 * 
	 */
	public void actualizaInstalaciones(List<Parcela> listInstalaciones, Long idNuevaParcela) {
		
		String idsInstalaciones = "";
		int cont = 0;
		for(Parcela instalacion : listInstalaciones){	
			idsInstalaciones =  instalacion.getId().toString();
			cont++;
			if(cont<listInstalaciones.size()){
				idsInstalaciones = idsInstalaciones+",";
			}
		}
		
		Session session = obtenerSession();
		try {
			String sql = "UPDATE TB_ANEXO_MOD_PARCELAS A SET A.IDPARCELAANXESTRUCTURA = " +idNuevaParcela+ " WHERE A.IDPARCELA IN ("+ idsInstalaciones +")";
			logger.debug("actualizaInstalaciones - sql: " + sql);
			session.createSQLQuery(sql).executeUpdate();
		}
		catch (Exception e) {
			logger.error("Se ha producido un error al actualizar la Instalaciones con id: ", e);
		}	
		return;
		
	}


	/** DAA 13/03/2013
	 *  Obteniene el maximo de numeros por cada hoja de las parcelas de un anexo
	 * @return 
	 * @throws DAOException 
	 */
	public Map<Object, Object> getMaxNumPorHoja(Long idAnexo, String codParcelaVO) throws DAOException {
		Session session = obtenerSession();
		Map<Object, Object> maxNumPorHoja = new HashMap<Object, Object>();
		try{
			String sql = "select hoja, max(numero) from tb_anexo_mod_parcelas a where a.idanexo=" + idAnexo;
			if(codParcelaVO != null && !("").equals(codParcelaVO)){
				sql += " and a.id <> " + codParcelaVO ; 
			}
			sql +=" group by hoja" ;
			List list = session.createSQLQuery(sql).list();
			
			for(int i=0;i<list.size();i++){
				Object[] registro = (Object[]) list.get(i);
				maxNumPorHoja.put(registro[0], registro[1]);
			}
			
		}catch(Exception excepcion){
			logger.error("Error al recuperar el maximo de numero por hoja",excepcion);
			throw new DAOException("Error al recuperar el maximo de numero por hoja",excepcion);
		}
		
		return maxNumPorHoja;
		
	}
	/** DAA 13/03/2013
	 *  Obteniene las claves de termino para cada hoja de las parcelas de un anexo
	 * @return 
	 * @throws DAOException 
	 */
	public Map<Object, Object> getClaveTerminoPorHoja(Long idAnexo, String codParcelaVO) throws DAOException {
		Session session = obtenerSession();
		Map<Object, Object> claveTerminoPorHoja = new HashMap<Object, Object>();
		
		try{
			String sql = "select distinct a.codprovincia || '|' || a.codtermino || '|' || a.subtermino as clave_modif, a.hoja,"+  
						"par.codprovincia || '|' || par.codtermino || '|' || par.subtermino as clave_parcela," + 
						"cp.codprovincia || '|' || cp.codtermino || '|' || nvl(cp.subtermino, ' ') as clave_copy" +
						" from tb_anexo_mod_parcelas a left join tb_parcelas par on (a.idparcela = par.idparcela)" +
						" left join tb_copy_parcelas cp on (a.idcopyparcela = cp.id) where a.idanexo=" + idAnexo ;
			/*if(!("").equals(codParcelaVO)){
				sql += " and a.id <> " + codParcelaVO ; 
			}*/
			
			List list = session.createSQLQuery(sql).list();
			
			for(int i=0;i<list.size();i++){
				Object[] registro = (Object[]) list.get(i);
				
				//buscamos en los registros que clave añadir, primero añadiremos las de copy
				//si no existen, las de las parcelas originales,
				//y si tampoco existen, he modificado las parcelas, y añado las claves nuevas
				
				if(!registro[3].equals("||") && !registro[3].equals("|| ")){
					if(!registro[3].equals(registro[0])){ // reviso si hay modificaci�n
						claveTerminoPorHoja.put(registro[0], registro[1]); // la hoja y num se ha modificado
					}else{
						claveTerminoPorHoja.put(registro[3], registro[1]); // no hay modificacion
					}
				}else{
					if(!registro[2].equals("||")){
						if(!registro[2].equals(registro[0])){ // reviso si hay modificaci�n
							claveTerminoPorHoja.put(registro[0], registro[1]); // la hoja y num se ha modificado
						}else{
							claveTerminoPorHoja.put(registro[2], registro[1]); // no hay modificacion
						}
					}else{
						if(!registro[0].equals("||")){
							claveTerminoPorHoja.put(registro[0], registro[1]);
						}	
					}
				}
			}
		}catch(Exception excepcion){
			logger.error("Error al recuperar el mapa de claves Termino",excepcion);
			throw new DAOException("Error al recuperar el mapa de claves Termino",excepcion);
		}
		
		return claveTerminoPorHoja;
	}

	/**
	 * Método para asignar a las instalaciones del anexo el tipo y la parcela asociada
	 * @parm idAnexo Identificador del anexo de modificación
	 */
	public void asignarInstalacionesFromPolizaActualizada(Long idAnexo) {
		Session session = obtenerSession();
		
		// Primero cambio el tipo
		String sql = "UPDATE o02agpe0.TB_ANEXO_MOD_PARCELAS SET TIPOPARCELA = '" + Constants.TIPO_PARCELA_INSTALACION + "'" +
				" WHERE IDANEXO = " + idAnexo + 
				" AND ID IN (SELECT P.ID FROM o02agpe0.TB_ANEXO_MOD_PARCELAS P, o02agpe0.TB_ANEXO_MOD_CAPITALES_ASEG C" +
				" WHERE C.IDPARCELAANEXO = P.ID AND P.IDANEXO = " + idAnexo + " AND C.CODTIPOCAPITAL >= " +
				Constants.TIPOCAPITAL_INSTALACIONES_MINIMO + ")";
		
		int numRegistros = session.createSQLQuery(sql).executeUpdate();
		
		if (numRegistros > 0){
			//Si he actualizado registros => hay que asignar parcelas a las instalaciones
			//1. Obtengo los ids de las instalaciones y su SIGPAC
			sql = "SELECT ID, CODPROVSIGPAC, CODTERMSIGPAC, AGRSIGPAC, ZONASIGPAC, POLIGONOSIGPAC, PARCELASIGPAC, RECINTOSIGPAC" +
					" FROM o02agpe0.TB_ANEXO_MOD_PARCELAS WHERE IDANEXO = " + idAnexo + 
					" AND TIPOPARCELA = '" + Constants.TIPO_PARCELA_INSTALACION + "'";
			List list = session.createSQLQuery(sql).list();
			
			//2. Recorro la lista y a cada uno le asigno la parcela que le corresponda según el SIGPAC
			for(int i=0;i<list.size();i++){
				Object[] registro = (Object[]) list.get(i);
				
				sql = "UPDATE o02agpe0.TB_ANEXO_MOD_PARCELAS SET IDPARCELAANXESTRUCTURA = (SELECT ID FROM" +
						" o02agpe0.TB_ANEXO_MOD_PARCELAS WHERE IDANEXO = " + idAnexo + 
						" AND CODPROVSIGPAC = " + registro[1] + " AND CODTERMSIGPAC = " + registro[2] + " AND AGRSIGPAC = " + 
						registro[3] + " AND ZONASIGPAC = " + registro[4] + " AND POLIGONOSIGPAC = " + registro[5] + 
						" AND PARCELASIGPAC = " + registro[6] + " AND RECINTOSIGPAC = " + registro[7] +
						" AND TIPOPARCELA = '" + Constants.TIPO_PARCELA_PARCELA + "' AND ROWNUM = 1) WHERE ID = " + registro[0];
				session.createSQLQuery(sql).executeUpdate();
			}
		}
	}
	
	/* Pet.50776_63485-Fase II ** MODIF TAM (17.11.2020) ** Inicio */
	
	public SWModulosCobParcelaAnexo saveEnvioCobParcelaAnx(SWModulosCobParcelaAnexo envio)	throws DAOException {
		Session session = obtenerSession();
		try {

			session.saveOrUpdate(envio);
		} catch (Exception ex) {
			logger.error(ex);
			throw new DAOException("Se ha producido un error durante el guardado de la entidad",ex);
		} finally {
		}

		return envio;
	}
	
	public void actualizaXmlCoberturasParcAnx(Long idEnvio, final String xml, final String respuesta) throws DAOException {
		Session session = obtenerSession();
		try {
			if (!StringUtils.nullToString(xml).equals("")) {
			
				Query update = session
						.createSQLQuery("UPDATE o02agpe0.tb_sw_modulos_cob_parc_anexo SET ENVIO=:envio WHERE ID=" + idEnvio)
						.setString("envio", xml);
				update.executeUpdate();	
			}
		
			if (!StringUtils.nullToString(respuesta).equals("")) {
				Query update = session
						.createSQLQuery("UPDATE o02agpe0.tb_sw_modulos_cob_parc_anexo SET RESPUESTA=:respuesta WHERE ID=" + idEnvio)
						.setString("respuesta", respuesta);
				update.executeUpdate();	
			}
		} catch (Exception e) {
			throw new DAOException(
					"Se ha producido un error durante el acceso a la base de datos",
					e);
		} finally {
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<RiesgoCubiertoModulo> getRiesgosCubiertosModuloAnx(final Long lineaseguroid, final String codmodulo,
			final Character nivelEleccion) {
		logger.debug("[INIT] getRiesgosCubiertosModulo");
		Session session = obtenerSession();
		List<RiesgoCubiertoModulo> lista = new ArrayList<RiesgoCubiertoModulo>();
		try {
			Criteria criteria = session.createCriteria(RiesgoCubiertoModulo.class);
			criteria.add(Restrictions.eq("id.lineaseguroid", lineaseguroid));
			criteria.add(Restrictions.eq("id.codmodulo", codmodulo));
			if (nivelEleccion != null) {
				criteria.add(Restrictions.eq("niveleccion", nivelEleccion));
			}
			lista = criteria.list();
		} catch (Exception e) {
			logger.fatal("[DAOException - sin throw][DatosParcelaDao][getRiesgosCubiertosModulo] Error lectura BD", e);
		}
		logger.debug("[END] getRiesgosCubiertosModulo");
		return lista;
	}
	/* Pet.50776_63485-Fase II ** MODIF TAM (17.11.2020) ** Fin */

}
