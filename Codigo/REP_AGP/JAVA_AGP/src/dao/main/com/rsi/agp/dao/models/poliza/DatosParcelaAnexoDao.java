package com.rsi.agp.dao.models.poliza;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.AliasToBeanResultTransformer;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.filters.FiltroUtils;
import com.rsi.agp.dao.filters.cgen.TipoSubvencionCCAAFiltro;
import com.rsi.agp.dao.filters.cgen.TipoSubvencionEnesaFiltro;
import com.rsi.agp.dao.filters.commons.LineasFiltro;
import com.rsi.agp.dao.filters.config.CampoMascaraFiltro;
import com.rsi.agp.dao.filters.config.ConfiguracionCamposFiltro;
import com.rsi.agp.dao.filters.cpl.LimiteRendimientoFiltro;
import com.rsi.agp.dao.filters.poliza.CapitalAseguradoParcelaFiltro;
import com.rsi.agp.dao.filters.poliza.ComarcaFiltro;
import com.rsi.agp.dao.filters.poliza.CultivoFiltro;
import com.rsi.agp.dao.filters.poliza.DatoVariableFiltro;
import com.rsi.agp.dao.filters.poliza.GetIdPantallaConfigurableFiltro;
import com.rsi.agp.dao.filters.poliza.MascaraFechaContrataAgricolaFiltro;
import com.rsi.agp.dao.filters.poliza.MascaraGrupoTasasFiltro;
import com.rsi.agp.dao.filters.poliza.MascaraLimiteRendimientoFiltro;
import com.rsi.agp.dao.filters.poliza.MascaraPrecioFiltro;
import com.rsi.agp.dao.filters.poliza.PrecioFiltro;
import com.rsi.agp.dao.filters.poliza.SubvencionCCAAFiltro;
import com.rsi.agp.dao.filters.poliza.SubvencionEnesaFiltro;
import com.rsi.agp.dao.filters.poliza.VariedadFiltro;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.tables.anexo.CapitalAsegurado;
import com.rsi.agp.dao.tables.anexo.CapitalDTSVariable;
import com.rsi.agp.dao.tables.anexo.Parcela;
import com.rsi.agp.dao.tables.cgen.TipoCapital;
import com.rsi.agp.dao.tables.cgen.TipoSubvencionCCAA;
import com.rsi.agp.dao.tables.cgen.TipoSubvencionEnesa;
import com.rsi.agp.dao.tables.cgen.ZonificacionGrupoCultivoDetalle;
import com.rsi.agp.dao.tables.cgen.ZonificacionSIGPAC;
import com.rsi.agp.dao.tables.commons.Comarca;
import com.rsi.agp.dao.tables.commons.Termino;
import com.rsi.agp.dao.tables.commons.TerminoId;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.commons.VistaTerminosAsegurable;
import com.rsi.agp.dao.tables.config.ConfiguracionCampo;
import com.rsi.agp.dao.tables.config.PantallaConfigurable;
import com.rsi.agp.dao.tables.cpl.AmbitoAsegurable;
import com.rsi.agp.dao.tables.cpl.Cultivo;
import com.rsi.agp.dao.tables.cpl.CultivoId;
import com.rsi.agp.dao.tables.cpl.Factor;
import com.rsi.agp.dao.tables.cpl.LimiteRendimiento;
import com.rsi.agp.dao.tables.cpl.MascaraLimiteRendimiento;
import com.rsi.agp.dao.tables.cpl.MascaraPrecio;
import com.rsi.agp.dao.tables.cpl.Precio;
import com.rsi.agp.dao.tables.cpl.SubvencionCCAA;
import com.rsi.agp.dao.tables.cpl.SubvencionEnesa;
import com.rsi.agp.dao.tables.cpl.Variedad;
import com.rsi.agp.dao.tables.cpl.VariedadId;
import com.rsi.agp.dao.tables.poliza.ComparativaPoliza;
import com.rsi.agp.dao.tables.poliza.Linea;
import com.rsi.agp.dao.tables.poliza.ModuloPoliza;
import com.rsi.agp.dao.tables.poliza.Poliza;
import com.rsi.agp.vo.DatosPantallaConfigurableVO;
import com.rsi.agp.vo.ItemSubvencionVO;
import com.rsi.agp.vo.LocalCultVarVO;
import com.rsi.agp.vo.ParamsSubvencionesVO;
import com.rsi.agp.vo.ParcelaVO;
import com.rsi.agp.vo.SigpacVO;

@SuppressWarnings({"unchecked", "rawtypes"})
public class DatosParcelaAnexoDao extends BaseDaoHibernate implements IDatosParcelaAnexoDao  {
	
	private static final String SUBTERMINO = "subtermino";
	private static final String CODTERMINO = "codtermino";
	private static final String CODCOMARCA = "codcomarca";
	private static final String CODPROVINCIA = "codprovincia";
	private static final String VARIEDAD_ID_CODVARIEDAD = "variedad.id.codvariedad";
	private static final String VARIEDAD_ID_CODCULTIVO = "variedad.id.codcultivo";
	private static final String ID_LINEASEGUROID = "id.lineaseguroid";
	
	protected final Log logger = LogFactory.getLog(getClass());

	/**
	 * Obtiene las comarcas filtrando por codigo de provincia
	 * @param codProvincia: codigo de provincia
	 */
	public List<Comarca> getComarcas(Long codProvincia){
		ComarcaFiltro filtro = new ComarcaFiltro(new BigDecimal(codProvincia));
		return this.getObjects(filtro);		
	}
	
	/**
	 * Obtiene los subterminos filtrando por provincia, comarca, termino y subtermino
	 * @param codProvincia: codigo de provincia
	 * @param codComarca: codigo de comarca
	 * @param codTermino: codigo termino
	 * @param subtermino: codigo de subtermino
	 * @throws DAOException 
	 */
	public List<VistaTerminosAsegurable> getSubterminos(Long codProvincia,  Long codTermino, String subtermino,Long linea) throws DAOException{
		Session session = obtenerSession();
		try {
			Criteria criteria =	session.createCriteria(VistaTerminosAsegurable.class);
			criteria.addOrder(Order.asc("id.nomtermino"));
			criteria.add(Restrictions.eq(ID_LINEASEGUROID, linea));
			if(FiltroUtils.noEstaVacio(codProvincia)){
				criteria.add(Restrictions.eq("id.codprovincia", new BigDecimal(codProvincia)));
			}
			if(FiltroUtils.noEstaVacio(codTermino)){
				criteria.add(Restrictions.eq("id.codtermino", new BigDecimal(codTermino)));
			}
			if(FiltroUtils.noEstaVacio(subtermino)){
				criteria.add(Restrictions.eq("id.subtermino", subtermino.substring(0)));
			}
			
			return criteria.list();
		} catch (Exception ex) {
			logger.error("Se ha produccido un error duranet el acceso a base de datos", ex);
			throw new DAOException ("Se ha produccido un error duranet el acceso a base de datos",ex);
		}
	}
	

	public List<VistaTerminosAsegurable> getSubterminosBySigpacList(List<LocalCultVarVO> listLocalCultVarVO, Long linea, Long claseId) throws DAOException {
		
		logger.debug("init - [DatosParcelaDao] getSubterminosBySigpacList");
	
		Session session = obtenerSession();
		try {
            
			Criteria criteria =	session.createCriteria(VistaTerminosAsegurable.class);
			criteria.addOrder(Order.asc("id.nomtermino"));
			criteria.add(Restrictions.eq(ID_LINEASEGUROID, linea));
			//Conjunction conjunction = Restrictions.conjunction();
			
			Disjunction disjunction = Restrictions.disjunction();
			for (Iterator<LocalCultVarVO> iterator = listLocalCultVarVO.iterator(); iterator.hasNext();) {
				LocalCultVarVO localCultVarVO = iterator.next();
				Criterion critCodProv = Restrictions.eq("id.codprovincia", new BigDecimal(localCultVarVO.getCodProvincia()));
				Criterion critCodTerm = Restrictions.eq("id.codtermino", new BigDecimal(localCultVarVO.getCodTermino()));
				Criterion temporal = Restrictions.and(critCodProv, critCodTerm);
				disjunction.add(temporal);
			}
			//conjunction.add(criterion)
			criteria.add(disjunction);
			logger.debug("end - [DatosParcelaDao] getSubterminos");

			return criteria.list();
		} catch (Exception e) {
			throw new DAOException ("[DatosParcelaDao][getAmbitosAsegurablesProvincias]error en el acceso a la BD",e);
		}
	}
	
	/**
	 * Obtiene los cultivos
	 * @param codLinea
	 * @param codPlan
	 */
	public List<Cultivo> getCultivos(Long codLinea, Long codPlan){
		CultivoFiltro filtro = new CultivoFiltro(new BigDecimal (codLinea), new BigDecimal (codPlan));
		return this.getObjects(filtro);
	}
	
	/**
	 * Obtiene las variedades
	 * @param codCultivo
	 */
	public List<Variedad> getVariedades(Long codCultivo,Long lineaseguroid){				
		VariedadFiltro filtro = new VariedadFiltro(new BigDecimal(codCultivo),lineaseguroid);
		return this.getObjects(filtro);
	}	
	
	/**
	 * Obtiene la lista  de tiposcapital
	 */
	public List<TipoCapital> getTiposCapital(Long lineaSeguroId, Long codProvincia,Long codComarca, Long codTermino,String subtermino,Long codPlan,
			Long codLinea,Long cultivo,Long variedad,String perfilUsuario,Long idPantalla,List<BigDecimal>  lstTipoCapitales){
		List<TipoCapital> tipoCapitales = new ArrayList<TipoCapital>();
		List<BigDecimal> idsCapTablaPrecio = null;
		logger.debug("init - [DatosParcelaAnexoDao] getTiposCapital");
		try{
			idsCapTablaPrecio = this.getPrecios(lineaSeguroId, codProvincia,codComarca,codTermino,subtermino,codPlan,codLinea,cultivo,variedad,perfilUsuario,idPantalla);
			//si existen datos en la tabla Precio,nos quedamos con ellos de lo contrario cogemos los de la tabla Factor
			if (idsCapTablaPrecio.size()>0){
				filtroCapitales(lstTipoCapitales, tipoCapitales,idsCapTablaPrecio);
			}else{
				Linea linea=getLineaseguroId(codLinea,codPlan);
				List<BigDecimal> idsCapTablaFactor = dameListaTotalValoresConceptoFactor(new BigDecimal(linea.getLineaseguroid()),new BigDecimal(126));
				filtroCapitales(lstTipoCapitales, tipoCapitales,idsCapTablaFactor);
			}
			
		}
		catch(Exception e){
			logger.fatal("[Exception][getTiposCapital]Error acceso BD ", e);
		}
		finally{
		}
		logger.debug("end - [DatosParcelaAnexoDao] getTiposCapital");
    	return tipoCapitales;
	}
	
	/**
	 * @param lstTipoCapitales ids CapAsegurados
	 * @param tipoCapitales lista final
	 * @param idsTipoCapitales ids CapAsegurados de la tabla Precio o Factor
	 */
	private void filtroCapitales(List<BigDecimal> lstTipoCapitales,List<TipoCapital> tipoCapitales,List<BigDecimal> idsTipoCapitales) {
		if (lstTipoCapitales != null && lstTipoCapitales.size()>0){
			for(BigDecimal idCapitalAsegurado : idsTipoCapitales){
				if (lstTipoCapitales.contains(idCapitalAsegurado)){
					TipoCapital tipoCapital = (TipoCapital)this.getObject(TipoCapital.class, idCapitalAsegurado);
					tipoCapitales.add(tipoCapital);
				}
			}
		}else{
			for(BigDecimal idCapitalAsegurado : idsTipoCapitales){
				TipoCapital tipoCapital = (TipoCapital)this.getObject(TipoCapital.class, idCapitalAsegurado);
				tipoCapitales.add(tipoCapital);
			}
		}
	}
	
	/**
	 * Borra un capital asegurado de una parcela
	 */
	public void deleteCapitalAsegurado(CapitalAsegurado capitalAsegurado){
		Session session = obtenerSession();
		try {
			if(capitalAsegurado != null)
				session.delete(capitalAsegurado);
		} catch (Exception ex) {
			logger.error(ex);
		}
	}
	
	/**
	 * Guarda un capital asegurado
	 */
	public Long saveCapitalAsegurado(CapitalAsegurado capitalAsegurado)throws DAOException{
		Session session = obtenerSession();
		Long idCapitalAsegurado = new Long(-1);
		try{
			idCapitalAsegurado = (Long)session.save(capitalAsegurado);
		}
		catch(Exception ex){
			logger.error(ex);
			throw new DAOException("Se ha producido un error durante el guardado del capital asegurado", ex);
		}
    	return idCapitalAsegurado;
	}
	
	/**
	 * Actualiza un capital asegurado
	 */
	public void updateCapitalAsegurado(CapitalAsegurado capitalAsegurado)throws DAOException{
		Session session = obtenerSession();
		try{
			session.saveOrUpdate(capitalAsegurado);
		}
		catch(Exception ex){
			logger.error(ex);
			throw new DAOException("Se ha producido un error al actualizar el capital asegurado", ex);
		}
	}
	
	/**
	 * @param codPlan
	 * @param codLinea
	 * @return Lista de objetos precio
	 */
	private List<BigDecimal> getPrecios(Long lineaSeguroId, Long codProvincia,Long codComarca, Long codTermino,String subtermino,
			                           Long codPlan, Long codLinea, Long cultivo, Long variedad,
			                           String perfilUsuario, Long idPantalla){
		
		
		Session session = obtenerSession();
		List<BigDecimal> precios = null;
  
		try{
			
			Criteria c = session.createCriteria(Precio.class);
			Conjunction conj = Restrictions.conjunction();
			c.addOrder(Order.asc("tipoCapital.codtipocapital"));
			c.setProjection(Projections.distinct(Projections.projectionList().add(Projections.property("tipoCapital.codtipocapital"))));

			
			if(idPantalla == 9)
				conj.add(Restrictions.ge("tipoCapital.codtipocapital", new BigDecimal(100)));
			else
				conj.add(Restrictions.lt("tipoCapital.codtipocapital", new BigDecimal(100)));
			
			
			conj.add(Restrictions.eq(ID_LINEASEGUROID, lineaSeguroId));
			
			/* PROPIEDADES DE VARIEDAD */
			if(FiltroUtils.noEstaVacio(cultivo)){
				conj.add(				
						Restrictions.disjunction()
						.add(Restrictions.eq(VARIEDAD_ID_CODCULTIVO, new BigDecimal(cultivo)))
						.add(Restrictions.eq(VARIEDAD_ID_CODCULTIVO, new BigDecimal("999")))

					);
			}
			if(FiltroUtils.noEstaVacio(variedad)){
				conj.add (
						Restrictions.disjunction()
						.add(Restrictions.eq(VARIEDAD_ID_CODVARIEDAD, new BigDecimal(variedad)))
						.add(Restrictions.eq(VARIEDAD_ID_CODVARIEDAD, new BigDecimal("999")))
					
					);
			}
			/* PROPIEDADES DE TERMINO */			
			if(FiltroUtils.noEstaVacio(codProvincia)){
				conj.add(
					Restrictions.disjunction()
						.add(Restrictions.eq(CODPROVINCIA, new BigDecimal(codProvincia)))
						.add(Restrictions.eq(CODPROVINCIA, new BigDecimal(99)))
				);
			}
			if(FiltroUtils.noEstaVacio(codComarca)){
				conj.add(
					Restrictions.disjunction()
						.add(Restrictions.eq(CODCOMARCA, new BigDecimal(codComarca)))
						.add(Restrictions.eq(CODCOMARCA, new BigDecimal(99)))
					);
			}
			if(FiltroUtils.noEstaVacio(codTermino)){
				conj.add(
					Restrictions.disjunction()
						.add(Restrictions.eq(CODTERMINO, new BigDecimal(codTermino)))
						.add(Restrictions.eq(CODTERMINO, new BigDecimal(999)))
					);
			}
			if(FiltroUtils.noEstaVacio(subtermino)){
				conj.add(
					Restrictions.disjunction()
						.add(Restrictions.eq(SUBTERMINO, subtermino.substring(0)))
						.add(Restrictions.eq(SUBTERMINO,  new Character('9')))
					);
			}
			c.add(conj);

			precios = c.list();
		}
		catch(Exception ex){
			logger.error(ex);
		}
		finally{
		}
		
    	return precios;
	}
	
	/**
	 * Obtiene el idpantallaconfigurable
	 * @param codlinea
	 * @param codPlan
	 * @param idPantalla
	 */
	public List<com.rsi.agp.dao.tables.poliza.CapitalAsegurado> getCapitalesAseguradoParcela(Long codParcela){
		CapitalAseguradoParcelaFiltro filtro = new CapitalAseguradoParcelaFiltro(codParcela);
		return this.getObjects(filtro);
	}
	
	/**
	 * Obtiene el idpantallaconfigurable
	 * @param codlinea
	 * @param codPlan
	 * @param idPantalla
	 */
	public Long getIdPantallaConfigurable(Long codLinea,Long codPlan,Long idPantalla) {
		
		GetIdPantallaConfigurableFiltro filtro = new GetIdPantallaConfigurableFiltro(new BigDecimal(codLinea),new BigDecimal(codPlan),new BigDecimal(idPantalla));
		List<PantallaConfigurable> pantallasConfiguradas = this.getObjects(filtro);
		Long idPantallaConfigurable = new Long(0); 
		
		for(PantallaConfigurable pantallaConfigurable : pantallasConfiguradas) 
			idPantallaConfigurable = pantallaConfigurable.getIdpantallaconfigurable();
		
		return idPantallaConfigurable;
	}
	
	/**
	 * Borra una parcela de la BD
	 * @param parcela
	 */
	public void deleteParcela(Parcela parcela)  {
		Session session = obtenerSession();
		try {
			if(parcela != null)
				session.delete(parcela);
		} catch (Exception ex) {
			logger.error(ex);
		}
		
	}

	/**
	 * Obtiene una pantallaconfigurable de la BD
	 * @param idPantallaConfigurable --> primary key 
	 */
    public PantallaConfigurable getPantallaConfigurada(Long idPantallaConfigurable){
		PantallaConfigurable pantallaConfigurable = null;
		pantallaConfigurable = (PantallaConfigurable)this.getObject(PantallaConfigurable.class, idPantallaConfigurable);	
		return pantallaConfigurable;
	}
    
    /**
	 * 
	 * @param paramsSubvencionesVO
	 */
    public ArrayList<ItemSubvencionVO> getSubvencionesParcelaCCAA(ParamsSubvencionesVO paramsSubvencionesVO){
        Poliza poliza = setNewPoliza(paramsSubvencionesVO);

        //Nos traemos los codigos de subvenciones CCAA para las parcelas
		SubvencionCCAAFiltro filtro = new SubvencionCCAAFiltro(poliza);
		
		List<String> listCodigosModulos = new ArrayList<String>();
		StringTokenizer tokens = new StringTokenizer(paramsSubvencionesVO.getListCodigosModulos(),";");
	    while(tokens.hasMoreTokens())
	       listCodigosModulos.add(new String(tokens.nextToken()));
	    
		filtro.setListCodigosModulos(listCodigosModulos);

		
		List<SubvencionCCAA> subvencionesCCAA = this.getObjects(filtro);
		ArrayList<BigDecimal> codigosSubvenciones = new ArrayList<BigDecimal>();
		if (subvencionesCCAA != null && subvencionesCCAA.size() > 0)
			for (SubvencionCCAA en : subvencionesCCAA)				
				codigosSubvenciones.add(en.getTipoSubvencionCCAA().getCodtiposubvccaa());

		TipoSubvencionCCAAFiltro filtroCCAAs = new TipoSubvencionCCAAFiltro();
		filtroCCAAs.setCodigosSubvenciones(codigosSubvenciones);
		filtroCCAAs.setNivelDependencia('N');
		//filtroCCAAs.setTipoidentificacion("N");
		
		//El tipo 2 de nivel de declaracion indica que nos traemos subvenciones a nivel de parcela
		filtroCCAAs.setNivelDeclaracion(new BigDecimal(2));
		
		List<TipoSubvencionCCAA> subvencionesCCAATratamiento = this.getObjects(filtroCCAAs);
		List<TipoSubvencionCCAA> subvencionesCCAAAPantalla = new ArrayList<TipoSubvencionCCAA>();
		
		for (TipoSubvencionCCAA ten: subvencionesCCAATratamiento){		
			List<BigDecimal> valorMax;
			filtro = new SubvencionCCAAFiltro(poliza);
			filtro.setTiposubvencionCCAA(ten.getCodtiposubvccaa());
			filtro.setListCodigosModulos(listCodigosModulos);
			Integer registrosSubv = this.getNumObjects(filtro);
			filtro.setDameMax(true);
			valorMax = this.getObjects(filtro);
			
			if (registrosSubv.intValue() > 1)
				ten.setDestiposubvccaa(ten.getDestiposubvccaa()+" ("+valorMax.get(0)+"%)*");
			else
				ten.setDestiposubvccaa(ten.getDestiposubvccaa()+" ("+valorMax.get(0)+"%)");
			subvencionesCCAAAPantalla.add(ten);
		}
		return getArrayItemSubvencionCCAAVO(subvencionesCCAAAPantalla);
    }
    
    
    /**
	 * Obtiene las subvenciones enesa a nivel de parcela
	 * @param paramsSubvencionesVO
	 */
    public ArrayList<ItemSubvencionVO> getSubvencionesParcelaEnesa(ParamsSubvencionesVO paramsSubvencionesVO){
    	
        ArrayList<ItemSubvencionVO> subvencionesParcelaEnesa = new ArrayList<ItemSubvencionVO>();
        
        Poliza poliza = setNewPoliza(paramsSubvencionesVO);

        // Get all subvenciones Enesa
		SubvencionEnesaFiltro filtro = new SubvencionEnesaFiltro(poliza);
		
		List<String> listCodigosModulos = new ArrayList<String>();
		StringTokenizer tokens = new StringTokenizer(paramsSubvencionesVO.getListCodigosModulos(),";");
	    while(tokens.hasMoreTokens()){ 
	       listCodigosModulos.add(new String(tokens.nextToken()));
	    }
		filtro.setListCodigosModulos(listCodigosModulos);
 		List<SubvencionEnesa> subvencionesEnesa = this.getObjects(filtro);
		
		
		
		
		// Solo subvenciones Parcela
		ArrayList<BigDecimal> codigosSubvenciones = new ArrayList<BigDecimal>();
		if (subvencionesEnesa != null && subvencionesEnesa.size() > 0)
			for (SubvencionEnesa en : subvencionesEnesa)				
				codigosSubvenciones.add(en.getTipoSubvencionEnesa().getCodtiposubvenesa());

		TipoSubvencionEnesaFiltro filtroEnesas = new TipoSubvencionEnesaFiltro();
		filtroEnesas.setCodigosSubvenciones(codigosSubvenciones);
		//filtroEnesas.setTipoidentificacion("N");
		filtroEnesas.setNivelDependencia('N');
		//Set nivel de declaracion parcela
		filtroEnesas.setNivelDeclaracion(new BigDecimal(2));
		
		List<TipoSubvencionEnesa> subvencionesEnesaTratamiento = this.getObjects(filtroEnesas);
		List<TipoSubvencionEnesa> subvencionesEnesaPantalla = new ArrayList<TipoSubvencionEnesa>();
		
		for (TipoSubvencionEnesa ten: subvencionesEnesaTratamiento){		
			List<BigDecimal> valorMax;
			filtro = new SubvencionEnesaFiltro(poliza);
			filtro.setTipoSubvencion(ten.getCodtiposubvenesa());
			filtro.setListCodigosModulos(listCodigosModulos);
			Integer registrosSubv = this.getNumObjects(filtro);
			filtro.setDameMax(true);
			valorMax = this.getObjects(filtro);
			
			if (registrosSubv.intValue() > 1)
				ten.setDestiposubvenesa(ten.getDestiposubvenesa()+" ("+valorMax.get(0)+"%)*");
			else
				ten.setDestiposubvenesa(ten.getDestiposubvenesa()+" ("+valorMax.get(0)+"%)");
			
			subvencionesEnesaPantalla.add(ten);
		}

		subvencionesParcelaEnesa = getArrayItemSubvencionEnesaVO(subvencionesEnesaPantalla);
		
        return subvencionesParcelaEnesa;
    }
    
    /**
	 * Crea y rellena un objeto poliza
	 * @param paramsSubvencionesVO
	 */
    public Poliza setNewPoliza(ParamsSubvencionesVO paramsSubvencionesVO){
    	
        // --- LINEA ---
        Linea linea = new Linea();
        LineasFiltro lineasFiltro = new LineasFiltro();
        lineasFiltro.setCodLinea(new BigDecimal(paramsSubvencionesVO.getCodLinea()));
        lineasFiltro.setCodPlan(new BigDecimal(paramsSubvencionesVO.getCodPlan()));
        linea = (Linea)this.getObjects(lineasFiltro).get(0);
        
        
    	 // --- PARCELA ---
        com.rsi.agp.dao.tables.poliza.Parcela parcela = new com.rsi.agp.dao.tables.poliza.Parcela();
        
        VariedadId id = new VariedadId(linea.getLineaseguroid(), 
        		new BigDecimal(paramsSubvencionesVO.getCodVariedad()), new BigDecimal(paramsSubvencionesVO.getCodCultivo()));
        Variedad var = new Variedad();
        var.setId(id);
        
        parcela.setVariedad(var);  
        
        
        // Termino
        Termino termino = new Termino();
        TerminoId terminoId = new TerminoId();
        terminoId.setCodprovincia(new BigDecimal(paramsSubvencionesVO.getCodProvincia()));
        terminoId.setCodtermino(new BigDecimal(paramsSubvencionesVO.getCodTermino()));
        terminoId.setSubtermino( new Character(paramsSubvencionesVO.getSubtermino()));
        
        termino.setId(terminoId);
        parcela.setTermino(termino);
        
        
        // --- POLIZA ---
        Poliza poliza = new Poliza();
        poliza.setLinea(linea);
        HashSet<com.rsi.agp.dao.tables.poliza.Parcela> parcelas = new HashSet<com.rsi.agp.dao.tables.poliza.Parcela>();
		parcelas.add(parcela);
        poliza.setParcelas(parcelas);

        return poliza;
    }
    
    /**
	 * Obtiene una lista de subvenciones enesa en forma de objetos ItemSubvencionVO 
	 * @param tiposubvencionesEnesa
	 */
    public ArrayList<ItemSubvencionVO> getArrayItemSubvencionEnesaVO(List<TipoSubvencionEnesa> tiposubvencionesEnesa ){
    	ArrayList<ItemSubvencionVO> subvencionesParcelaEnesa = new ArrayList<ItemSubvencionVO>();
    	
    	for(TipoSubvencionEnesa tiposubvencionEnesa : tiposubvencionesEnesa){
    		ItemSubvencionVO itemSubvencionVO = new ItemSubvencionVO();
    		itemSubvencionVO.setCodigo(tiposubvencionEnesa.getCodtiposubvenesa().longValue());
    		itemSubvencionVO.setDescripcion(tiposubvencionEnesa.getDestiposubvenesa());
    		
    		subvencionesParcelaEnesa.add(itemSubvencionVO);
    	}

    	return subvencionesParcelaEnesa;
    }
    
    /**
	 * Obtiene una lista de subvenciones CCAA en forma de objetos ItemSubvencionVO 
	 * @param tiposubvencionesCCAA
	 */
    public ArrayList<ItemSubvencionVO> getArrayItemSubvencionCCAAVO(List<TipoSubvencionCCAA> tiposubvencionesCCAA){
    	ArrayList<ItemSubvencionVO> subvencionesParcelaCCAA = new ArrayList<ItemSubvencionVO>();
    	
    	for(TipoSubvencionCCAA tiposubvencionCCAA : tiposubvencionesCCAA){
    		ItemSubvencionVO itemSubvencionVO = new ItemSubvencionVO();
    		itemSubvencionVO.setCodigo(tiposubvencionCCAA.getCodtiposubvccaa().longValue());
    		itemSubvencionVO.setDescripcion(tiposubvencionCCAA.getDestiposubvccaa());
    		
    		subvencionesParcelaCCAA.add(itemSubvencionVO);
    	}

    	return subvencionesParcelaCCAA;
    }
    
    /**
     * Guardan en BD un objeto parcela
     * @param el objeto parcela a guardar
     * @return la pk del nuevo registro
     */
    public Long saveObjectParcela(Parcela parcela)throws DAOException{
    	Session session = obtenerSession();
		Long idParcela = new Long(-1);
		try{
			idParcela = (Long)session.save(parcela);
		}
		catch(Exception ex){
			logger.error(ex);
			throw new DAOException("Se ha producido un error durante el acceso a la base de datos", ex);
		}
		finally{
		}
    	return idParcela;
    }
    
    /**
     * Guarda o modifica en BD un objeto parcela
     */
    public void saveOrUpdateParcela(Parcela parcela)throws DAOException{
    	Session session = obtenerSession();
		try{
			session.saveOrUpdate(parcela);
		}
		catch(Exception ex){
			logger.error(ex);
			throw new DAOException("Se ha producido un error durante el acceso a la base de datos", ex);
		}
		finally{
		}
    }
    
    /**
     * Obtiene el listado de subvenciones Enesa filtrando por codigo
     * @param
     * @return
     */
    public List<SubvencionCCAA> getSubvencionesCCAA(Long codigo){
    	Session session = obtenerSession();
		List<SubvencionCCAA> listaSubvencionesCCAA = null;

		try{
			Criteria c = session.createCriteria(SubvencionCCAA.class);
			Criterion crit = Restrictions.eq("id.codtiposubvccaa",new BigDecimal(codigo));
			c.add(crit);
			listaSubvencionesCCAA = c.list();
		}
		catch(Exception ex){
			logger.error(ex);
		}
		finally{
		}
    	return listaSubvencionesCCAA;
    }
    
    /**
     * Obtiene el listado de subvenciones Enesa filtrando por codigo
     * @param
     * @return
     */
    public List<SubvencionEnesa> getSubvencionesEnesa(Long codigo){
    	Session session = obtenerSession();
		List<SubvencionEnesa> listaSubvencionesEnesa = null;

		try{
			Criteria c = session.createCriteria(SubvencionEnesa.class);
			Criterion crit = Restrictions.eq("id.codtiposubvenesa",new BigDecimal(codigo));
			c.add(crit);
			listaSubvencionesEnesa = c.list();
		}
		catch(Exception ex){
			logger.error(ex);
		}
		finally{
		}
		
    	return listaSubvencionesEnesa;
    }

    public List<MascaraPrecio> getMascaraPrecio(Long lineaseguroid, String codmodulo,
			ParcelaVO parcela) {
    	List<MascaraPrecio> mascaras = new ArrayList<MascaraPrecio>();
    	
    	BigDecimal codcultivo = (parcela.getCultivo() != null && !parcela
				.getCultivo().equals("")) ? new BigDecimal(parcela.getCultivo())
				: null;
		BigDecimal codvariedad = (parcela.getVariedad() != null && !parcela
				.getVariedad().equals("")) ? new BigDecimal(parcela
				.getVariedad()) : null;
		BigDecimal codprovincia = (parcela.getCodProvincia() != null && !parcela
				.getCodProvincia().equals("")) ? new BigDecimal(parcela
				.getCodProvincia()) : null;
		BigDecimal codtermino = (parcela.getCodTermino() != null && !parcela
				.getCodTermino().equals("")) ? new BigDecimal(parcela
				.getCodTermino()) : null;
		Character subtermino = (parcela.getCodSubTermino() != null && !parcela
				.getCodSubTermino().equals("")) ? new Character(parcela
				.getCodSubTermino().charAt(0)) : null;
		BigDecimal codcomarca = (parcela.getCodComarca() != null && !parcela
				.getCodComarca().equals("")) ? new BigDecimal(parcela
				.getCodComarca()) : null;
				
		MascaraPrecioFiltro mascaraFiltro = new MascaraPrecioFiltro(lineaseguroid, codcultivo, codvariedad,
				codprovincia, codcomarca, codtermino, subtermino, codmodulo);
		
    	mascaras = getObjects(mascaraFiltro);
    	
    	if (mascaras.isEmpty()) {
    		// Si no recupera datos filtramos por los datos genericos. Segun el siguiente orden:
			//	1. Subtermino = 9; 2. Termino = 999; 3. Comarca = 99
			//	4. Provincia = 99; 5. Variedad = 999; 6. Cultivo = 999
    		mascaras = filtroMascPrecio(mascaraFiltro);
		}
    	
    	return mascaras;
    }
    
    public List<MascaraLimiteRendimiento> getMascaraLimiteRendimiento(Long lineaseguroid, String codmodulo,
			ParcelaVO parcela) {
    	List<MascaraLimiteRendimiento> mascaras = new ArrayList<MascaraLimiteRendimiento>();
    	
    	BigDecimal codcultivo = (parcela.getCultivo() != null && !parcela
				.getCultivo().equals("")) ? new BigDecimal(parcela.getCultivo())
				: null;
		BigDecimal codvariedad = (parcela.getVariedad() != null && !parcela
				.getVariedad().equals("")) ? new BigDecimal(parcela
				.getVariedad()) : null;
		BigDecimal codprovincia = (parcela.getCodProvincia() != null && !parcela
				.getCodProvincia().equals("")) ? new BigDecimal(parcela
				.getCodProvincia()) : null;
		BigDecimal codtermino = (parcela.getCodTermino() != null && !parcela
				.getCodTermino().equals("")) ? new BigDecimal(parcela
				.getCodTermino()) : null;
		Character subtermino = (parcela.getCodSubTermino() != null && !parcela
				.getCodSubTermino().equals("")) ? new Character(parcela
				.getCodSubTermino().charAt(0)) : null;
		BigDecimal codcomarca = (parcela.getCodComarca() != null && !parcela
				.getCodComarca().equals("")) ? new BigDecimal(parcela
				.getCodComarca()) : null;
		
		MascaraLimiteRendimientoFiltro mascaraFiltro = new MascaraLimiteRendimientoFiltro(lineaseguroid, codcultivo,
				codvariedad, codprovincia, codcomarca, codtermino, subtermino, codmodulo);
		
    	mascaras = getObjects(mascaraFiltro);
    	
    	if (mascaras.isEmpty()) {
    		// Si no recupera datos filtramos por los datos genericos. Segun el siguiente orden:
			//	1. Subtermino = 9; 2. Termino = 999; 3. Comarca = 99
			//	4. Provincia = 99; 5. Variedad = 999; 6. Cultivo = 999
    		mascaras = filtroMascLimRendimiento(mascaraFiltro);
    	}
    	
    	return mascaras;
    }
    
    public List<Precio> getPrecio(PrecioFiltro precioFiltro) {
		List<Precio> precios = new ArrayList<Precio>();
		
		precios = getObjects(precioFiltro);
		
		if(precios.isEmpty()){
			precioFiltro.setAllsubterminos(true);
			precios = getObjects(precioFiltro);
			
			if(precios.isEmpty()){
				precioFiltro.setAllterminos(true);
				precios = getObjects(precioFiltro);
				
				if(precios.isEmpty()){
					precioFiltro.setAllcomarcas(true);
					precios = getObjects(precioFiltro);
					
					if(precios.isEmpty()){
						precioFiltro.setAllprovincias(true);
						precios = getObjects(precioFiltro);
					
						if(precios.isEmpty()){
							precioFiltro.setAllvariedades(true);
							precios = getObjects(precioFiltro);
							
							if(precios.isEmpty()){
								precioFiltro.setAllcultivos(true);
								precios = getObjects(precioFiltro);
							}
						}
					}
				}
			}
		}
		
		return precios;
	}
    
    public List<LimiteRendimiento> getRendimientos(
			LimiteRendimientoFiltro limRendFiltro) {
		List<LimiteRendimiento> rendimientos = new ArrayList<LimiteRendimiento>();

		// 3.2. Obtenemos los limites
		rendimientos = getObjects(limRendFiltro);

		if (rendimientos.isEmpty()) {
			limRendFiltro.setAllsubterminos(true);
			rendimientos = getObjects(limRendFiltro);

			if (rendimientos.isEmpty()) {
				limRendFiltro.setAllterminos(true);
				rendimientos = getObjects(limRendFiltro);

				if (rendimientos.isEmpty()) {
					limRendFiltro.setAllcomarcas(true);
					rendimientos = getObjects(limRendFiltro);

					if (rendimientos.isEmpty()) {
						limRendFiltro.setAllprovincias(true);
						rendimientos = getObjects(limRendFiltro);

						if (rendimientos.isEmpty()) {
							limRendFiltro.setAllvariedades(true);
							rendimientos = getObjects(limRendFiltro);

							if (rendimientos.isEmpty()) {
								limRendFiltro.setAllcultivos(true);
								rendimientos = getObjects(limRendFiltro);
							}
						}
					}
				}
			}
		}
		return rendimientos;
    }

	@Override
	public List getMascaraFCA(DatosPantallaConfigurableVO datosPantalla,List modulos) {
		/*************************************************/
		/******* MASCARA FECHA CONTRATACION AGRARIA ******/
		/*************************************************/
		List listaMFCAF = null;
		
		Character subter = null;
		if(datosPantalla.getSubtermino().length() > 0){
			subter = datosPantalla.getSubtermino().charAt(0);
		}

		MascaraFechaContrataAgricolaFiltro filtroMFCAF = new MascaraFechaContrataAgricolaFiltro(new Long(datosPantalla.getLineaSeguroId()),
				new BigDecimal(datosPantalla.getCodCultivo()),new BigDecimal(datosPantalla.getCodVariedad()),new BigDecimal(datosPantalla.getCodProvincia()),
				new BigDecimal(datosPantalla.getCodComarca()),new BigDecimal(datosPantalla.getCodTermino()),subter,modulos);
		
		listaMFCAF = this.getObjects(filtroMFCAF);
		
		// Si no recupera datos filtramos por los datos genericos. Segun el siguiente orden:
		//	1. Subtermino = 9; 2. Termino = 999; 3. Comarca = 99
		//	4. Provincia = 99; 5. Variedad = 999; 6. Cultivo = 999
		if (listaMFCAF.isEmpty()) {
			filtroMFCAF.setAllsubterminos(true);
			listaMFCAF = this.getObjects(filtroMFCAF);

			if (listaMFCAF.isEmpty()) {
				filtroMFCAF.setAllterminos(true);
				listaMFCAF = this.getObjects(filtroMFCAF);

				if (listaMFCAF.isEmpty()) {
					filtroMFCAF.setAllcomarcas(true);
					listaMFCAF = this.getObjects(filtroMFCAF);

					if (listaMFCAF.isEmpty()) {
						filtroMFCAF.setAllprovincias(true);
						listaMFCAF = this.getObjects(filtroMFCAF);

						if (listaMFCAF.isEmpty()) {
							filtroMFCAF.setAllvariedades(true);
							listaMFCAF = this.getObjects(filtroMFCAF);

							if (listaMFCAF.isEmpty()) {
								filtroMFCAF.setAllcultivos(true);
								listaMFCAF = this.getObjects(filtroMFCAF);
							}
						}
					}
				}
			}
		}
		return listaMFCAF;
	}


	@Override
	public List getMascaraGT(DatosPantallaConfigurableVO datosPantalla,List modulos) {
		/*************************************************/
		/******* MASCARA GRUPO TASAS *********************/
		/*************************************************/
		List listaMGT = null;
		
		Character subter = null;
		if(datosPantalla.getSubtermino().length() > 0){
			subter = datosPantalla.getSubtermino().charAt(0);
		}
		
		MascaraGrupoTasasFiltro filtroMGT = new MascaraGrupoTasasFiltro(new Long(datosPantalla.getLineaSeguroId()),
				new BigDecimal(datosPantalla.getCodCultivo()),new BigDecimal(datosPantalla.getCodVariedad()),new BigDecimal(datosPantalla.getCodProvincia()),
				new BigDecimal(datosPantalla.getCodComarca()),new BigDecimal(datosPantalla.getCodTermino()),subter,modulos);
		
		listaMGT = this.getObjects(filtroMGT);
		
		// Si no recupera datos filtramos por los datos genericos. Segun el siguiente orden:
		//	1. Subtermino = 9; 2. Termino = 999; 3. Comarca = 99
		//	4. Provincia = 99; 5. Variedad = 999; 6. Cultivo = 999
		if (listaMGT.isEmpty()) {
			filtroMGT.setAllsubterminos(true);
			listaMGT = this.getObjects(filtroMGT);

			if (listaMGT.isEmpty()) {
				filtroMGT.setAllterminos(true);
				listaMGT = this.getObjects(filtroMGT);

				if (listaMGT.isEmpty()) {
					filtroMGT.setAllcomarcas(true);
					listaMGT = this.getObjects(filtroMGT);

					if (listaMGT.isEmpty()) {
						filtroMGT.setAllprovincias(true);
						listaMGT = this.getObjects(filtroMGT);

						if (listaMGT.isEmpty()) {
							filtroMGT.setAllvariedades(true);
							listaMGT = this.getObjects(filtroMGT);

							if (listaMGT.isEmpty()) {
								filtroMGT.setAllcultivos(true);
								listaMGT = this.getObjects(filtroMGT);
							}
						}
					}
				}
			}
		}
		return listaMGT;
	}


	@Override
	public List getMascaraLRDTO(DatosPantallaConfigurableVO datosPantalla,List modulos) {
		/*************************************************/
		/********MASCARA LIMITES RENDIMIENTO***************/
		/*************************************************/
		List listaMLRDTO = null;
		
		Character subter = null;
		if(datosPantalla.getSubtermino().length() > 0){
			subter = datosPantalla.getSubtermino().charAt(0);
		}
		
		MascaraLimiteRendimientoFiltro filtroMLRDTO = new MascaraLimiteRendimientoFiltro(
				new Long(datosPantalla.getLineaSeguroId()), new BigDecimal(datosPantalla.getCodCultivo()),
				new BigDecimal(datosPantalla.getCodVariedad()), new BigDecimal(datosPantalla.getCodProvincia()),
				new BigDecimal(datosPantalla.getCodComarca()), new BigDecimal(datosPantalla.getCodTermino()), subter,
				modulos);
		
		listaMLRDTO = this.getObjects(filtroMLRDTO);
		
		if(listaMLRDTO.isEmpty()){
			// Si no recupera datos filtramos por los datos genericos. Segun el siguiente orden:
			//	1. Subtermino = 9; 2. Termino = 999; 3. Comarca = 99
			//	4. Provincia = 99; 5. Variedad = 999; 6. Cultivo = 999
			listaMLRDTO = filtroMascLimRendimiento(filtroMLRDTO);
		}
		return listaMLRDTO;
	}


	@Override
	public List getMascaraP(DatosPantallaConfigurableVO datosPantalla,List modulos) {
		/*************************************************/
		/***********MASCARA PRECIOS**********************/
		/*************************************************/
		List listaMPR = null;
		
		Character subter = null;
		if(datosPantalla.getSubtermino().length() > 0){
			subter = datosPantalla.getSubtermino().charAt(0);
		}
		
		MascaraPrecioFiltro filtroMPR = new MascaraPrecioFiltro(
				                  new Long(datosPantalla.getLineaSeguroId()),
				                  new BigDecimal(datosPantalla.getCodCultivo()),
				                  new BigDecimal(datosPantalla.getCodVariedad()),
				                  new BigDecimal(datosPantalla.getCodProvincia()),
				                  new BigDecimal(datosPantalla.getCodComarca()),
				                  new BigDecimal(datosPantalla.getCodTermino()),				                 
				                  subter,
				                  modulos);
		
		listaMPR = this.getObjects(filtroMPR);
		
		if(listaMPR.isEmpty()){
			// Si no recupera datos filtramos por los datos genericos. Segun el siguiente orden:
			//	1. Subtermino = 9; 2. Termino = 999; 3. Comarca = 99
			//	4. Provincia = 99; 5. Variedad = 999; 6. Cultivo = 999
			listaMPR = filtroMascPrecio(filtroMPR);
		}	
		return listaMPR;
	}

	@Override
	public List getConceptosObligatorios(Long idPantallaConfigurable) {
		List listaObligatorios = null;
		ConfiguracionCamposFiltro filter = new ConfiguracionCamposFiltro(true,idPantallaConfigurable);
		listaObligatorios = this.getObjects(filter);
		return listaObligatorios;
	}

	@Override
	public List getConceptosRelacionados(List listaMascaras) {
		List lista = null;
		CampoMascaraFiltro filter = new CampoMascaraFiltro(listaMascaras);
		lista = this.getObjects(filter);
		return lista;
	}

	@Override
	public List getModulosPoliza(Long idPoliza) throws BusinessException {
		List lista = null;
//		lista = this.getObjects(ModuloPoliza.class, "poliza.idpoliza", idPoliza);
		lista =this.getObjectsBySQLQuery("select codmodulo from tb_modulos_poliza t where idpoliza="+idPoliza);
		return lista;
	}
	
	/**
	 * Filtra por los datos genericos. Segun el siguiente orden: 
	 * 	1. Subtermino = 9; 2. Termino = 999; 3. Comarca = 99
	 *  4. Provincia = 99; 5. Variedad = 999; 6. Cultivo = 999
	 *  
	 * @param filtroMascPrecio
	 * @return
	 */
	private List<MascaraPrecio> filtroMascPrecio(MascaraPrecioFiltro filtroMascPrecio){
		List<MascaraPrecio> listMascPrecio = new ArrayList<MascaraPrecio>();

		filtroMascPrecio.setAllsubterminos(true);
		listMascPrecio = this.getObjects(filtroMascPrecio);
		
		if(listMascPrecio.isEmpty()){
			filtroMascPrecio.setAllterminos(true);
			listMascPrecio = this.getObjects(filtroMascPrecio);
			
			if(listMascPrecio.isEmpty()){
				filtroMascPrecio.setAllcomarcas(true);
				listMascPrecio = this.getObjects(filtroMascPrecio);
			
				if(listMascPrecio.isEmpty()){
					filtroMascPrecio.setAllprovincias(true);
					listMascPrecio = this.getObjects(filtroMascPrecio);		
					
					if(listMascPrecio.isEmpty()){
						filtroMascPrecio.setAllvariedades(true);
						listMascPrecio = this.getObjects(filtroMascPrecio);
						
						if(listMascPrecio.isEmpty()){
							filtroMascPrecio.setAllcultivos(true);
							listMascPrecio = this.getObjects(filtroMascPrecio);
						}
					}
				}
			}
		}
	
		return listMascPrecio;
	}
	
	/**
	 * Filtra por los datos genericos. Segun el siguiente orden: 
	 * 	1. Subtermino = 9; 2. Termino = 999; 3. Comarca = 99
	 *  4. Provincia = 99; 5. Variedad = 999; 6. Cultivo = 999
	 * 
	 * @param filtroMascLimRend
	 * @return
	 */
	private List<MascaraLimiteRendimiento> filtroMascLimRendimiento(MascaraLimiteRendimientoFiltro filtroMascLimRend){
		List<MascaraLimiteRendimiento> listMascLimRendimiento = new ArrayList<MascaraLimiteRendimiento>();
		
		filtroMascLimRend.setAllsubterminos(true);
		listMascLimRendimiento = this.getObjects(filtroMascLimRend);
		
		if(listMascLimRendimiento.isEmpty()){
			filtroMascLimRend.setAllterminos(true);
			listMascLimRendimiento = this.getObjects(filtroMascLimRend);
			
			if(listMascLimRendimiento.isEmpty()){
				filtroMascLimRend.setAllcomarcas(true);
				listMascLimRendimiento = this.getObjects(filtroMascLimRend);
			
				if(listMascLimRendimiento.isEmpty()){
					filtroMascLimRend.setAllprovincias(true);
					listMascLimRendimiento = this.getObjects(filtroMascLimRend);		
					
					if(listMascLimRendimiento.isEmpty()){
						filtroMascLimRend.setAllvariedades(true);
						listMascLimRendimiento = this.getObjects(filtroMascLimRend);
						
						if(listMascLimRendimiento.isEmpty()){
							filtroMascLimRend.setAllcultivos(true);
							listMascLimRendimiento = this.getObjects(filtroMascLimRend);
						}
					}						
				}					
			}
		}		
	
		return listMascLimRendimiento;
	}
	
	public List<ModuloPoliza> getModulosPoliza(Long idPoliza, Long lineaseguroid)throws DAOException{
		Session session = obtenerSession();
		List<ModuloPoliza> modulosPoliza = null;
	    
	    try{
	    	Criteria criteria = session.createCriteria(ModuloPoliza.class);

	    	if(idPoliza != null) {
			    criteria.add(Restrictions.eq("id.idpoliza", idPoliza));
	    	}
	        if(lineaseguroid != null) {
			    criteria.add(Restrictions.eq(ID_LINEASEGUROID, lineaseguroid));
	        }
			
	        modulosPoliza = criteria.list();
	    }
		catch(Exception ex){
			logger.error(ex);
			throw new DAOException("Se ha producido un error durante el acceso a la base de datos", ex);
		}
		finally{
		}
		
	    return modulosPoliza; 
	}
	
	public LocalCultVarVO getLocalCultVar(SigpacVO sigpacVO) throws DAOException {
		logger.debug("[DatosParcelaDao][Start][getLocalCultVar]");
		
		LocalCultVarVO           localCultVarVO          = new LocalCultVarVO();
		ZonificacionSIGPAC       zonificacionSIGPAC      = new ZonificacionSIGPAC();
		List<ZonificacionSIGPAC> listZonificacionSIGPAC  = null;
        
		try {
			
			listZonificacionSIGPAC = getZonificacionSIGPAC(sigpacVO);
			
			if(listZonificacionSIGPAC.size() > 0){
			    zonificacionSIGPAC = listZonificacionSIGPAC.get(0);
			    
				localCultVarVO = setLocalCultVarVO(zonificacionSIGPAC.getCodprovincia(),
						                           zonificacionSIGPAC.getCodtermino(),
						                           zonificacionSIGPAC.getSubtermino());

				// Si cultivo es unico lo guardo en el objeto para enviarselo al cliente
				if(isUnicCultivo(listZonificacionSIGPAC)){
					if(zonificacionSIGPAC.getZonificacionGrupoCultivo() != null && zonificacionSIGPAC.getZonificacionGrupoCultivo().getZonificacionGrupoCultivoDetalles() != null){
						if(zonificacionSIGPAC.getZonificacionGrupoCultivo().getZonificacionGrupoCultivoDetalles().size() > 0){
							BigDecimal codCultivoAux = zonificacionSIGPAC.getZonificacionGrupoCultivo().getZonificacionGrupoCultivoDetalles().iterator().next().getId().getCodcultivo();
							
							CultivoId cultivoId = new CultivoId();
							cultivoId.setCodcultivo(codCultivoAux);
							cultivoId.setLineaseguroid(Long.parseLong(sigpacVO.getLineaseguroid()));
	
							Cultivo cultivoAux = (Cultivo)this.getObject(Cultivo.class,cultivoId);
							localCultVarVO.setCodCultivo(cultivoAux.getId().getCodcultivo().toString());
							localCultVarVO.setDesCultivo(cultivoAux.getDescultivo());
						}
					}
				}
		    }
		
		} catch (Exception e) {
        	throw new DAOException ("[DatosParcelaDao][getLocalCultVar]error no controlado",e);
		}

		logger.debug("[DatosParcelaDao][End][getLocalCultVar]");
		return localCultVarVO;
	}
	
	private LocalCultVarVO setLocalCultVarVO(BigDecimal codProvincia, BigDecimal codTermino, Character subtermino){
		logger.debug("[DatosParcelaDao][Start][setLocalCultVarVO]");
		
		LocalCultVarVO localCultVarVO = new LocalCultVarVO();
		List<VistaTerminosAsegurable> listVistaTerminosAsegurable = null;
		VistaTerminosAsegurable vistaTerminosAsegurable = null;
		
		try {
			Session session = obtenerSession();
			
			Criteria criteria =	session.createCriteria(VistaTerminosAsegurable.class);
			criteria.add(Restrictions.eq("id.codprovincia",codProvincia));
			criteria.add(Restrictions.eq("id.codtermino",codTermino));
			
			if (Character.isWhitespace(subtermino) || Character.isLetterOrDigit(subtermino)) {
				criteria.add(Restrictions.eq("id.subtermino", subtermino));
			}

			listVistaTerminosAsegurable = criteria.list();
			
			if(listVistaTerminosAsegurable.size() > 0){
			    vistaTerminosAsegurable =  listVistaTerminosAsegurable.get(0);
			    localCultVarVO.setCodProvincia(vistaTerminosAsegurable.getId().getCodprovincia().toString());
				localCultVarVO.setNomProvincia(vistaTerminosAsegurable.getId().getNomprovincia());
				localCultVarVO.setCodComarca(vistaTerminosAsegurable.getId().getCodcomarca().toString());
				localCultVarVO.setNomComarca(vistaTerminosAsegurable.getId().getNomcomarca());
				localCultVarVO.setCodTermino(vistaTerminosAsegurable.getId().getCodtermino().toString());
				localCultVarVO.setNomTermino(vistaTerminosAsegurable.getId().getNomtermino());
				localCultVarVO.setSubTermino(vistaTerminosAsegurable.getId().getSubtermino().toString());
			}

		
		} catch (Exception e) {
			logger.fatal("[DAOException - sin throw][DatosParcelaDao][setLocalCultVarVO]Error lectura BD", e);
		}
		
		logger.debug("[DatosParcelaDao][end][setLocalCultVarVO]");
		return localCultVarVO;
	}
	
	/**
	 * Comprueba si en hay un solo cultivo
	 * @param listZonificacionSIGPAC
	 * @return boolean true si es unico
	 */
	private boolean isUnicCultivo(List<ZonificacionSIGPAC> listZonificacionSIGPAC){
		logger.debug("[DatosParcelaDao][Start][isUnicCultivo]");
		boolean result = false;
		List<BigDecimal> listCodCultivos = new ArrayList<BigDecimal>();
		
		// Recorro la lista obteniendo los cultivos
		for(ZonificacionSIGPAC zonificacionSIGPAC : listZonificacionSIGPAC){
			if(zonificacionSIGPAC.getZonificacionGrupoCultivo() != null && zonificacionSIGPAC.getZonificacionGrupoCultivo().getZonificacionGrupoCultivoDetalles()!= null){
				Iterator it = zonificacionSIGPAC.getZonificacionGrupoCultivo().getZonificacionGrupoCultivoDetalles().iterator();
				for (Iterator i = it; i.hasNext(); ) {
					ZonificacionGrupoCultivoDetalle detalle = (ZonificacionGrupoCultivoDetalle)i.next();
					// Si no esta lo añado
					if(!estaInListBigDecimal(detalle.getId().getCodcultivo(), listCodCultivos))
						listCodCultivos.add(detalle.getId().getCodcultivo());
				}
			}
		}
		
		if(listCodCultivos.size() > 1)
			result = false;
		else
			result = true;

		logger.debug("[DatosParcelaDao][End][isUnicCultivo]");
		return result;
	}
	
	/**
	 * Busco si esta el elemento en la lista
	 * @param codCultivo
	 * @param listCodCultivos
	 * @return si existe o no en la lista 
	 */
	private boolean estaInListBigDecimal(BigDecimal elem, List<BigDecimal> listElems){
		logger.debug("[DatosParcelaDao][Start][estaInListBigDecimal]");
		
		boolean result = false;
		
		 for (int i = 0; i < listElems.size(); i++)
			 if(listElems.get(i).equals(elem))
				 result = true;

		 logger.debug("[DatosParcelaDao][End][estaInListBigDecimal]"); 
		return result;
	}
	
	private List<ZonificacionSIGPAC> getZonificacionSIGPAC(SigpacVO sigpacVO) throws DAOException {
		logger.debug("[Inicio][getZonificacionSIGPAC]");
		
		Session session = obtenerSession();
		
		try {

			Criteria criteria =	session.createCriteria(ZonificacionSIGPAC.class);

			criteria.add(Restrictions.eq("codprovsigpac",new BigDecimal(sigpacVO.getProv())));
			criteria.add(Restrictions.eq("codtermsigpac",new BigDecimal(sigpacVO.getTerm())));
			criteria.add(Restrictions.eq("agrsigpac",new BigDecimal(sigpacVO.getAgr())));
			criteria.add(Restrictions.eq("zonasigpac",new BigDecimal(sigpacVO.getZona())));
			criteria.add(Restrictions.eq("poligonosigpac",new BigDecimal(sigpacVO.getPol())));
			criteria.add(Restrictions.eq("parcelasigpac",new BigDecimal(sigpacVO.getParc())));

			logger.debug("[Fin][getZonificacionSIGPAC]"); 
			
			return criteria.list();
			
        } catch (Exception e) {
        	throw new DAOException ("[DatosParcelaDao][getZonificacionSIGPAC]error lectura BD",e);
		}
	}
	
	public Comarca getComarca(BigDecimal codProvincia, BigDecimal codComarca)throws DAOException{
	    Session session = obtenerSession();
	    Comarca comarca = null;
	    
	    try{
			Criteria criteria = session.createCriteria(Comarca.class);
			criteria.add(Restrictions.eq("id.codprovincia", codProvincia));
			criteria.add(Restrictions.eq("id.codcomarca", codComarca));
			
			comarca = (Comarca)criteria.list().get(0);
		}
		catch(Exception ex){
			logger.error(ex);
			throw new DAOException("Se ha producido un error durante el acceso a la base de datos", ex);
		}
		finally{
		}
		
	    return comarca; 
	}

	public Termino getTermino(BigDecimal codProvincia,BigDecimal codComarca, BigDecimal codTermino)throws DAOException{
		Session session = obtenerSession();	
		Termino termino = null;
		
		try{
			Criteria criteria = session.createCriteria(Termino.class);
			criteria.add(Restrictions.eq("id.codprovincia", codProvincia));
			criteria.add(Restrictions.eq("id.codcomarca", codComarca));
			criteria.add(Restrictions.eq("id.codtermino", codTermino));
			
			termino = (Termino)criteria.list().get(0);
			
		}
		catch(Exception ex){
			logger.error(ex);
			throw new DAOException("Se ha producido un error durante el acceso a la base de datos", ex);
		}
		finally{
		}

		return termino; 
	}
	
	public Cultivo getCultivo(BigDecimal codCultivo)throws DAOException{

		Session session = obtenerSession();	
		Cultivo cultivo = null;
		
		try{
			Criteria criteria = session.createCriteria(Cultivo.class);
			criteria.add(Restrictions.eq("id.codcultivo", codCultivo));
			
			cultivo = (Cultivo)criteria.list().get(0);
		}
		catch(Exception ex){
			logger.error(ex);
			throw new DAOException("Se ha producido un error durante el acceso a la base de datos", ex);
		}
		finally{
		}
	
        return cultivo;
	}
	
	public Variedad getVariedad(BigDecimal codCultivo,BigDecimal codVariedad)throws DAOException{
		Variedad variedad = new Variedad();
		
		Session session = obtenerSession();
		
		try{
		    Criteria criteria = session.createCriteria(Variedad.class);
		    criteria.add(Restrictions.eq("id.codcultivo", codCultivo));
		    criteria.add(Restrictions.eq("id.codvariedad", codVariedad));
		    
		    variedad = (Variedad)criteria.list().get(0);
		}
		catch(Exception ex){
			logger.error(ex);
			throw new DAOException("Se ha producido un error durante el acceso a la base de datos", ex);
		}
		finally{
		}

		return variedad;
	}
	
	public boolean existeLimiteRendimientoByLineaseguroid(Long lineaseguroid) {
		
		Session session = obtenerSession();
		
		try{
			
			Criteria criteria =	session.createCriteria(LimiteRendimiento.class);
			criteria.add(Restrictions.eq(ID_LINEASEGUROID, lineaseguroid));
			criteria.setProjection(Projections.rowCount()); 
			return ((Integer)criteria.uniqueResult()).intValue() != 0;			
		
		} catch (Exception ex) {
			
			logger.error("Se ha produccido un error duran te el acceso a base de datos", ex);
			return false;
			
		}finally{
		}		
	}
	
	@Override
	public List<ComparativaPoliza> getRiesgosCubiertos(String codPoliza, BigDecimal codRiesgo,String valor) throws DAOException {
		
		Session session = obtenerSession();	
		
		try {
			
			Criteria criteria = session.createCriteria(ComparativaPoliza.class);
			criteria.add(Restrictions.eq("poliza.idpoliza", new Long(codPoliza)));
			criteria.add(Restrictions.eq("id.codconcepto", codRiesgo));
			criteria.add(Restrictions.eq("id.codvalor", new BigDecimal(valor)));
			
			return criteria.list();
			
		} catch (Exception ex) {
			
			throw new DAOException("Se ha producido un error durante el acceso a la base de datos", ex);
			
		}finally{
		}	
		
	}
	
	public Long getNumMayorParcelaToAnexo(Long idAnexo) throws DAOException{
		try {
		} catch (Exception ex) {
			throw new DAOException("Se ha producido un error durante el acceso a la base de datos", ex);
		}finally{
		}
		return null;
		
	}
	
	@Override
	public List<ConfiguracionCampo> getListConfigCampos(BigDecimal idpantalla)throws DAOException {
		Session session = obtenerSession();
		List<ConfiguracionCampo> lista = new ArrayList<ConfiguracionCampo>();
		try {
			Criteria criteria =	session.createCriteria(ConfiguracionCampo.class);
			criteria.add(Restrictions.eq("id.idpantallaconfigurable", idpantalla));
			criteria.addOrder(Order.asc("x"));
			criteria.addOrder(Order.asc("y"));
			
			lista = criteria.list();
			
		} catch (Exception ex) {
			logger.error("Se ha produccido un error duranet el acceso a base de datos", ex);
		}
		return lista;
	}
	
	@Override
	public List<AmbitoAsegurable> getAmbitosAsegurablesProvincias(Long lineaseguroid) throws DAOException {
		Session session = obtenerSession();
		try {
			Criteria criteria =	session.createCriteria(AmbitoAsegurable.class);
			criteria.add(Restrictions.eq(ID_LINEASEGUROID, lineaseguroid));
			criteria.setProjection(Projections.distinct(Projections.projectionList()
					.add(Projections.property("provincia"), "provincia")))
					.setResultTransformer(new AliasToBeanResultTransformer(AmbitoAsegurable.class));

			return criteria.list();
			
		} catch (Exception ex) {
			logger.error("Se ha produccido un error duranet el acceso a base de datos", ex);
			throw new DAOException ("Se ha produccido un error duranet el acceso a base de datos",ex);
		}
	}

	@Override
	public Linea getLineaseguroId(Long codlinea, Long codplan) {
		Session session = obtenerSession();
		try {
			Criteria criteria =	session.createCriteria(Linea.class);
			criteria.add(Restrictions.eq("codlinea", new BigDecimal(codlinea)));
			criteria.add(Restrictions.eq("codplan", new BigDecimal(codplan)));
			
			return (Linea)criteria.uniqueResult();
			
		} catch (Exception ex) {
			logger.error("Se ha produccido un error duranet el acceso a base de datos", ex);
		}
		return null;
	}
	
	@Override
	public List<BigDecimal> dameListaValoresConceptoFactor (BigDecimal lineaseguroid,String lstModulos, BigDecimal codConcepto){
	
	Session session = obtenerSession();
		String cad="";
		cad=lstModulos + ";99999";
		String [] modulos = cad.split(";");
		
		Criteria criteria = session.createCriteria(Factor.class);
		criteria.add(Restrictions.eq(ID_LINEASEGUROID, lineaseguroid.longValue()));
		criteria.add(Restrictions.eq("id.codconcepto", codConcepto));		
		criteria.add(Restrictions.in("modulo.id.codmodulo", modulos));		
		criteria.setProjection(Projections.distinct(Projections.property("id.valorconcepto")));
		List<BigDecimal> lstValoresConcepto = criteria.list();
		
		return lstValoresConcepto;
		
	}

	public String getClaseQuery(String campo){
		return "SELECT distinct " + campo + " FROM ClaseDetalle claseDetalle " +
               "WHERE claseDetalle.clase.id =:clase_ AND claseDetalle.clase.linea.lineaseguroid =:lineaseguroid_";
	}
	
	public List getFieldFromClase(Long lineaseguroid, Long clase, String query){
		Session session = obtenerSession();
		Query hql = session.createQuery(query);
		hql.setParameter("clase_", new BigDecimal(clase.toString()));
		hql.setParameter("lineaseguroid_", lineaseguroid);
		return hql.list();
	}
	
	public List<BigDecimal> dameListaTotalValoresConceptoFactor (BigDecimal lineaseguroid, BigDecimal codConcepto){
		
		Session session = obtenerSession();
		Criteria criteria = session.createCriteria(Factor.class);
		criteria.add(Restrictions.eq(ID_LINEASEGUROID, lineaseguroid.longValue()));
		criteria.add(Restrictions.eq("id.codconcepto", codConcepto));		
		criteria.setProjection(Projections.distinct(Projections.property("id.valorconcepto")));
		List<BigDecimal> lstValoresConcepto = criteria.list();
			
		return lstValoresConcepto;
			
	}
	
	public CapitalDTSVariable getDatoVariable (BigDecimal codConcepto, Long idCapitalAsegurado) {
		DatoVariableFiltro dvf = new DatoVariableFiltro(codConcepto, idCapitalAsegurado);
		List<CapitalDTSVariable> lista = this.getObjects(dvf);
		
		if (lista != null && lista.size()>0) {
			return lista.get(0);
		}
		
		return null;
	}
	
	public CapitalDTSVariable getDatoVariable (CapitalDTSVariable dv) {
		DatoVariableFiltro dvf = new DatoVariableFiltro(dv.getCodconcepto(), dv.getCapitalAsegurado().getId(), 
														dv.getCodconceptoppalmod(), dv.getCodriesgocubierto());
		List<CapitalDTSVariable> lista = this.getObjects(dvf);
		
		if (lista != null && lista.size()>0) {
			return lista.get(0);
		}
		
		return null;
	}
	
	public void borrarDatosVariables (String idCapitalAsegurado, List<Integer> listaConceptos) {
		
		Session session = obtenerSession();
		
		try {
			session.createSQLQuery("DELETE TB_ANEXO_MOD_CAPITALES_DTS_VBL WHERE IDCAPITALASEGURADO=" + idCapitalAsegurado +
								   " AND CODCONCEPTO NOT IN " + StringUtils.toValoresSeparadosXComas(listaConceptos, false, true)).executeUpdate();
		}
		catch (Exception e) {
			logger.error("Error al borrar los datos variables del anexo", e);
		}
		
	}
	
	/**
	 * AMG 20/02/2014
	 * Metodo para clonar los datos de una parcela de Anexo
	 * @param parcela
	 * @return clonParcela
	 * 
	 */
	public Parcela clonarParcelaAnexo(Long idParcela,Usuario usuario,Parcela clonParcela){
		Parcela parcela = null;
	
		
		try 
		{
			Session session = obtenerSession();
		
			parcela = (Parcela)session.get(Parcela.class, idParcela);
			//datos parcela
			clonParcela.setId(null);
			clonParcela.setIdcopyparcela(parcela.getIdcopyparcela());
			clonParcela.setTipomodificacion('A');
			
			//clonParcela.setHoja(parcela.getHoja());
			//clonParcela.setNumero(parcela.getNumero());
			/*Nombre*/
			clonParcela.setNomparcela(parcela.getNomparcela());
			
			clonParcela.setCodcomarca(parcela.getCodcomarca());
			
			/*Cultivo y Variedad*/
			clonParcela.setCodcultivo(parcela.getCodcultivo());
			clonParcela.setCodvariedad(parcela.getCodvariedad());
			clonParcela.setPoligono(parcela.getPoligono());
			clonParcela.setParcela_1(parcela.getParcela_1());
			/*SIGPAC*/
			clonParcela.setCodprovsigpac(parcela.getCodprovsigpac());
			clonParcela.setCodtermsigpac(parcela.getCodtermsigpac());
			clonParcela.setAgrsigpac(parcela.getAgrsigpac());
			clonParcela.setZonasigpac(parcela.getZonasigpac());
			clonParcela.setPoligonosigpac(parcela.getPoligonosigpac());
			clonParcela.setParcelasigpac(parcela.getParcelasigpac());
			clonParcela.setRecintosigpac(parcela.getRecintosigpac());
			clonParcela.setTipoparcela(parcela.getTipoparcela());  // en clonarparcelas de polizas mete una 'P'
			clonParcela.setIdparcelaanxestructura(parcela.getIdparcelaanxestructura());
			clonParcela.setAltaencomplementario(parcela.getAltaencomplementario());
			
			/*Poliza*/
			//Poliza poliza = parcela.getAnexoModificacion().getPoliza();
			//clonParcela.getAnexoModificacion().setPoliza(poliza);
			
			/*Anexo*/
			clonParcela.setAnexoModificacion(parcela.getAnexoModificacion());		

			/*Parcela*/
			clonParcela.setParcela(parcela.getParcela());
			
			/*Hoja y Numero*/
			
			
			
			/*Capitales asegurados*/
			Set<CapitalAsegurado> capitalAsegurados = new HashSet<CapitalAsegurado>();
			CapitalAsegurado objCapAs = null;
			
			for (CapitalAsegurado ca : parcela.getCapitalAsegurados())
			{
				//capitales asegurados/
				objCapAs = new CapitalAsegurado();
				objCapAs.setParcela(clonParcela);
				objCapAs.setTipoCapital(ca.getTipoCapital());
				objCapAs.setSuperficie(ca.getSuperficie());
				objCapAs.setPrecio(ca.getPrecio());
				objCapAs.setProduccion(ca.getProduccion());
				objCapAs.setAltaencomplementario(ca.getAltaencomplementario());
				objCapAs.setIncrementoproduccion(ca.getIncrementoproduccion());
				objCapAs.setTipomodificacion(ca.getTipomodificacion());
				objCapAs.setIncrementoproduccionanterior(ca.getIncrementoproduccionanterior());
				objCapAs.setValorincremento(ca.getValorincremento());
				objCapAs.setTipoincremento(ca.getTipoincremento());			
				
				Set<CapitalDTSVariable> datoVariableParcelas = new HashSet<CapitalDTSVariable>();
				CapitalDTSVariable objDatoVar = null;
				List<CapitalDTSVariable> listaDts = dameListaDatosVariablesAnexo(ca.getId());
				for (CapitalDTSVariable dvp : listaDts) {
				//datos variables	
				objDatoVar = new CapitalDTSVariable();
				objDatoVar.setCapitalAsegurado(objCapAs);
				objDatoVar.setCodconcepto(dvp.getCodconcepto());
				objDatoVar.setValor(dvp.getValor());
				objDatoVar.setTipomodificacion(dvp.getTipomodificacion());
				objDatoVar.setCodconceptoppalmod(dvp.getCodconceptoppalmod());
				objDatoVar.setCodriesgocubierto(dvp.getCodriesgocubierto());
				datoVariableParcelas.add(objDatoVar);
				}
				objCapAs.setCapitalDTSVariables(datoVariableParcelas);
				
				capitalAsegurados.add(objCapAs);
			}
			clonParcela.setCapitalAsegurados(capitalAsegurados);
			
			session.save(clonParcela);
			saveOrUpdateFacturacion(clonParcela, usuario);
			
		} catch (Exception ex) {
			logger.error("Se ha producido un error al clonar la parcela de Anexo.",ex);
		}
		return clonParcela;
	}
	
	public List<CapitalDTSVariable> dameListaDatosVariablesAnexo (Long codTipoCapital){
		List<CapitalDTSVariable> listaDts = new ArrayList<CapitalDTSVariable>();
		Session session = obtenerSession();			
			Criteria criteria = session.createCriteria(CapitalDTSVariable.class);
			criteria.add(Restrictions.eq("capitalAsegurado.id", codTipoCapital));
			listaDts = criteria.list();
			
			return listaDts;
			
		}
	
	
}
