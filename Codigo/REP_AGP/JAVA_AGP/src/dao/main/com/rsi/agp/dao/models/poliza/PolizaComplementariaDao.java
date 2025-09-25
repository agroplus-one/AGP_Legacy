package com.rsi.agp.dao.models.poliza;

import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.util.CriteriaUtils;
import com.rsi.agp.dao.filters.FiltroUtils;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.tables.cgen.CalculoIndemnizacion;
import com.rsi.agp.dao.tables.cgen.CapitalAseguradoElegible;
import com.rsi.agp.dao.tables.cgen.Garantizado;
import com.rsi.agp.dao.tables.cgen.MinimoIndemnizableElegible;
import com.rsi.agp.dao.tables.cgen.PctFranquiciaElegible;
import com.rsi.agp.dao.tables.cgen.TipoFranquicia;
import com.rsi.agp.dao.tables.cpl.Modulo;
import com.rsi.agp.dao.tables.cpl.ModuloId;
import com.rsi.agp.dao.tables.cpl.RiesgoCubiertoModulo;
import com.rsi.agp.dao.tables.poliza.CapitalAsegurado;
import com.rsi.agp.dao.tables.poliza.Poliza;

public class PolizaComplementariaDao extends BaseDaoHibernate implements IPolizaComplementariaDao{

	private Log logger = LogFactory.getLog(PolizaComplementariaDao.class);
	@Override
	public Modulo getModuloPPalPoliza(ModuloId idModulo) throws DAOException {
		try {
			
			return (Modulo) get(Modulo.class, idModulo);
			
		} catch (Exception ex) {
			logger.error("Se ha producido un error en el acceso a la BBDD",ex);
			throw new DAOException("Se ha producido un error en el acceso a la BBDD",ex);
		}
	}
	
	
	public List<Poliza> getPolizaByTipoRef(Poliza poliza, Character tipoRef) throws DAOException {
		Session session = obtenerSession();
		try {
			Criteria criteria = session.createCriteria(Poliza.class);
			criteria = criteria.createAlias("asegurado", "ase");
			criteria = criteria.createAlias("colectivo", "col");
			criteria = criteria.createAlias("linea", "lin");
			criteria = criteria.createAlias("estadoPoliza", "est");
			
			criteria.add(Restrictions.eq("lin.lineaseguroid", poliza.getLinea().getLineaseguroid()));
			if(FiltroUtils.noEstaVacio(poliza.getAsegurado().getId()))
				criteria.add(Restrictions.eq("ase.id", poliza.getAsegurado().getId()));
			
			if(FiltroUtils.noEstaVacio(poliza.getColectivo().getIdcolectivo()))
				criteria.add(Restrictions.eq("col.id", poliza.getColectivo().getId()));
			if(FiltroUtils.noEstaVacio(poliza.getClase()))
				criteria.add(Restrictions.eq("clase", poliza.getClase()));
			criteria.add(Restrictions.eq("tipoReferencia", tipoRef));
			criteria.add(Restrictions.ne("est.idestado", Constants.ESTADO_POLIZA_BAJA));
			return criteria.list();
			
		} catch (Exception ex) {
			logger.error("Se ha producido un error en el acceso a la BBDD",ex);
			throw new DAOException("Se ha producido un error en el acceso a la BBDD",ex);
		}
	}
	
	/* ESC-13939 ** MODIF TAM (19.05.2021) ** Inicio **/
	/* Se solicita obtener el IdPoliza de las pólizas que estén en estado "Enviada correcta"*/
	@Override
	public Long getIdPolizaByRefSupSbp(String referencia,Character tipo,Long lineaseguroid) throws DAOException {
		Session session = obtenerSession();
		try {
			String sql = "select idpoliza from o02agpe0.tb_polizas po where po.tiporef = '"+tipo+ "'and" +
					" po.lineaseguroid=" + lineaseguroid + " and po.idestado = 8 and po.referencia='"+ referencia +"'";
			logger.debug(sql);		
			BigDecimal id = (BigDecimal) session.createSQLQuery(sql).uniqueResult();
			logger.debug("idPoliza encontrada: "+ id);
			return id.longValue();
			
		} catch (Exception ex) {
			logger.error("Se ha producido un error en el acceso a la BBDD",ex);
			throw new DAOException("Se ha producido un error en el acceso a la BBDD",ex);
		}
	}
	/* ESC-13939 ** MODIF TAM (19.05.2021) ** Fin **/
	
	@Override
	public List<Poliza> getPolizaByRef(String referencia,Long lineaseguroid) throws DAOException {
		Session session = obtenerSession();
		try {
			Criteria criteria = session.createCriteria(Poliza.class);
			
			if(FiltroUtils.noEstaVacio(referencia))
				criteria.add(Restrictions.eq("referencia", referencia));
			
			if(FiltroUtils.noEstaVacio(lineaseguroid))
				criteria.add(Restrictions.eq("linea.lineaseguroid", lineaseguroid));
			
			criteria.add(Restrictions.ne("estadoPoliza.idestado", Constants.ESTADO_POLIZA_ANULADA));
			
			return  criteria.list();
			
		} catch (Exception ex) {
			logger.error("Se ha producido un error en el acceso a la BBDD",ex);
			throw new DAOException("Se ha producido un error en el acceso a la BBDD",ex);
		}
	}
	
	/**
	 * A partir del id de la poliza ppal buscamos si existen cpls
	 * @param Long idPolizaPpal
	 * @return boolean 
	 */
	public Poliza existePolizaCpl(Long idPolizaPpal) throws DAOException {
		Session session = obtenerSession();
		try {
			Criteria criteria = session.createCriteria(Poliza.class);
			
		
			
			criteria = criteria.createAlias("polizaPpal", "polizaPpal");
			criteria = criteria.createAlias("estadoPoliza", "estadoPoliza");
			
			
			criteria.add(Restrictions.eq("polizaPpal.idpoliza", idPolizaPpal));
			criteria.add(Restrictions.gt("estadoPoliza.idestado",BigDecimal.ZERO));
			

			return  (Poliza) criteria.uniqueResult();
			
		} catch (Exception ex) {
			logger.error("Se ha producido un error en el acceso a la BBDD",ex);
			throw new DAOException("Se ha producido un error en el acceso a la BBDD",ex);
		}
		
	}
	
	@Override
	public Modulo getModuloCplPoliza(Long lineaseguroid, String codmodulo)throws DAOException {
		Session session = obtenerSession();
		try {
			
			Criteria criteria = session.createCriteria(Modulo.class);
			
			if(FiltroUtils.noEstaVacio(lineaseguroid))
				criteria.add(Restrictions.eq("id.lineaseguroid", lineaseguroid));
			
			if(FiltroUtils.noEstaVacio(codmodulo))
				criteria.add(Restrictions.eq("codmoduloasoc", codmodulo));
			
			return (Modulo) criteria.uniqueResult();
			
		} catch (Exception ex) {
			logger.error("Se ha producido un error en el acceso a la BBDD",ex);
			throw new DAOException("Se ha producido un error en el acceso a la BBDD",ex);
		}
	}

	@Override
	public Long getIdPolizaByRef(String referencia,Character tipo,Long lineaseguroid) throws DAOException {
		Session session = obtenerSession();
		try {
			String sql = "select idpoliza from o02agpe0.tb_polizas po where po.tiporef = '"+tipo+ "'and" +
					" po.lineaseguroid=" + lineaseguroid + " and po.idestado != 0 and po.referencia='"+ referencia +"'";
			logger.debug(sql);		
			BigDecimal id = (BigDecimal) session.createSQLQuery(sql).uniqueResult();
			logger.debug("idPoliza encontrada: "+ id);
			return id.longValue();
			
		} catch (Exception ex) {
			logger.error("Se ha producido un error en el acceso a la BBDD",ex);
			throw new DAOException("Se ha producido un error en el acceso a la BBDD",ex);
		}
	}
	
	@Override
	public Long getIdPolizaByRef(String referencia,Character moduloPolizaPrincipal) throws DAOException {
		Session session = obtenerSession();
		try {
			
			Query query = session.createQuery("from Poliza po where po.referencia = :ref and po.tipoReferencia = :tipo")
																					.setString("ref", referencia)
																					.setCharacter("tipo", moduloPolizaPrincipal);
																	
			return ((Poliza) query.uniqueResult()).getIdpoliza();
			
		} catch (Exception ex) {
			logger.error("Se ha producido un error en el acceso a la BBDD",ex);
			throw new DAOException("Se ha producido un error en el acceso a la BBDD",ex);
		}
	}

	

	@Override
	public List<CapitalAsegurado> getCapitalesAsegPolCpl(CapitalAsegurado capAsegurado)	throws DAOException {
		Session session = obtenerSession();
		try {
				Criteria criteria = session.createCriteria(CapitalAsegurado.class);
				criteria.createAlias("parcela", "parcela");
				criteria.addOrder(Order.asc("parcela.codprovsigpac")).addOrder(Order.asc("parcela.codtermsigpac")).addOrder(Order.asc("parcela.agrsigpac")).addOrder(Order.asc("parcela.zonasigpac")).addOrder(Order.asc("parcela.poligonosigpac")).addOrder(Order.asc("parcela.parcelasigpac")).addOrder(Order.asc("parcela.recintosigpac"));
				
								
				if(FiltroUtils.noEstaVacio(capAsegurado.getParcela().getHoja())){
					criteria.add(Restrictions.eq("parcela.hoja", capAsegurado.getParcela().getHoja()));
				}
				if(FiltroUtils.noEstaVacio(capAsegurado.getParcela().getNumero())){
					criteria.add(Restrictions.eq("parcela.numero", capAsegurado.getParcela().getNumero()));
				}
				if(FiltroUtils.noEstaVacio(capAsegurado.getParcela().getPoliza().getIdpoliza())){
			        criteria.add(Restrictions.eq("parcela.poliza.idpoliza", capAsegurado.getParcela().getPoliza().getIdpoliza()));
				}
		        if(FiltroUtils.noEstaVacio(capAsegurado.getParcela().getTermino().getId().getCodprovincia())){
			        criteria.add(Restrictions.eq("parcela.termino.id.codprovincia", capAsegurado.getParcela().getTermino().getId().getCodprovincia()));
		        }
		        if(FiltroUtils.noEstaVacio(capAsegurado.getParcela().getTermino().getId().getCodcomarca())){
			        criteria.add(Restrictions.eq("parcela.termino.id.codcomarca", capAsegurado.getParcela().getTermino().getId().getCodcomarca()));
		        }
		        if(FiltroUtils.noEstaVacio(capAsegurado.getParcela().getTermino().getId().getCodtermino())){
			        criteria.add(Restrictions.eq("parcela.termino.id.codtermino", capAsegurado.getParcela().getTermino().getId().getCodtermino()));
		        }
		        if(FiltroUtils.noEstaVacio(capAsegurado.getParcela().getTermino().getId().getSubtermino())){		        
			        criteria.add(Restrictions.eq("parcela.termino.id.subtermino", capAsegurado.getParcela().getTermino().getId().getSubtermino()));
		        }
		        if(FiltroUtils.noEstaVacio(capAsegurado.getParcela().getCodcultivo())){
				      criteria.add(Restrictions.eq("parcela.codcultivo", capAsegurado.getParcela().getCodcultivo()));
		        }
		        if(FiltroUtils.noEstaVacio(capAsegurado.getParcela().getCodvariedad())){
				      criteria.add(Restrictions.eq("parcela.codvariedad", capAsegurado.getParcela().getCodvariedad()));
		        }
		        if(FiltroUtils.noEstaVacio(capAsegurado.getParcela().getNomparcela())){
		        	criteria.add(Restrictions.like("parcela.nomparcela", "%" + capAsegurado.getParcela().getNomparcela() + "%"));	        	
		        }
		        if(FiltroUtils.noEstaVacio(capAsegurado.getParcela().getPoligono())){
		        	criteria.add(Restrictions.eq("parcela.poligono", capAsegurado.getParcela().getPoligono()));
		        }
		        if(FiltroUtils.noEstaVacio(capAsegurado.getParcela().getParcela())){
		        	criteria.add(Restrictions.eq("parcela.parcela", capAsegurado.getParcela().getParcela()));
		        }
		        if(FiltroUtils.noEstaVacio(capAsegurado.getParcela().getCodprovsigpac())){
			        criteria.add(Restrictions.eq("parcela.codprovsigpac",capAsegurado.getParcela().getCodprovsigpac()));
		        }
		        if(FiltroUtils.noEstaVacio(capAsegurado.getParcela().getCodtermsigpac())){
			        criteria.add(Restrictions.eq("parcela.codtermsigpac",capAsegurado.getParcela().getCodtermsigpac()));
		        }
		        if(FiltroUtils.noEstaVacio(capAsegurado.getParcela().getAgrsigpac())){
			        criteria.add(Restrictions.eq("parcela.agrsigpac",capAsegurado.getParcela().getAgrsigpac()));
		        }
		        if(FiltroUtils.noEstaVacio(capAsegurado.getParcela().getZonasigpac())){
			        criteria.add(Restrictions.eq("parcela.zonasigpac",capAsegurado.getParcela().getZonasigpac()));
		        }
		        if(FiltroUtils.noEstaVacio(capAsegurado.getParcela().getPoligonosigpac())){
			        criteria.add(Restrictions.eq("parcela.poligonosigpac",capAsegurado.getParcela().getPoligonosigpac()));
		        }
		        if(FiltroUtils.noEstaVacio(capAsegurado.getParcela().getParcelasigpac())){
			        criteria.add(Restrictions.eq("parcela.parcelasigpac",capAsegurado.getParcela().getParcelasigpac()));
		        }
		        if(FiltroUtils.noEstaVacio(capAsegurado.getParcela().getRecintosigpac())){
			        criteria.add(Restrictions.eq("parcela.recintosigpac",capAsegurado.getParcela().getRecintosigpac()));
		        }
		        if(FiltroUtils.noEstaVacio(capAsegurado.getSuperficie())){
		        	criteria.add(Restrictions.eq("superficie", capAsegurado.getSuperficie()));
		        }
		        if(FiltroUtils.noEstaVacio(capAsegurado.getProduccion())){
		        	criteria.add(Restrictions.eq("produccion", capAsegurado.getProduccion()));
		        }
		        if(FiltroUtils.noEstaVacio(capAsegurado.getTipoCapital().getCodtipocapital())){
		        	criteria.add(Restrictions.eq("tipoCapital.codtipocapital", capAsegurado.getTipoCapital().getCodtipocapital()));
		        }
		        
		   return criteria.list(); 
			
		} catch (Exception ex) {
			logger.error("Se ha producido un error en el acceso a la BBDD",ex);
			throw new DAOException("Se ha producido un error en el acceso a la BBDD",ex);
		}
	}
	
	public BigDecimal getFilaModulo(final Long lineaseguroid,
			final String codmodulo, final BigDecimal codconceptoppalmod,
			final BigDecimal codriesgocubierto) {

		RiesgoCubiertoModulo rcmodHbm;
		Session session = obtenerSession();
		rcmodHbm = (RiesgoCubiertoModulo) session
				.createCriteria(RiesgoCubiertoModulo.class)
				.createAlias("conceptoPpalModulo", "conceptoPpalModulo")
				.createAlias("riesgoCubierto", "riesgoCubierto")
				.add(Restrictions.eq("id.lineaseguroid", lineaseguroid))
				.add(Restrictions.eq("id.codmodulo", codmodulo))
				.add(Restrictions.eq("conceptoPpalModulo.codconceptoppalmod",
						codconceptoppalmod))
				.add(Restrictions.eq("riesgoCubierto.id.codriesgocubierto",
						codriesgocubierto)).uniqueResult();
		if (rcmodHbm == null)
			return null;
		else
			return rcmodHbm.getId().getFilamodulo();
	}
	
	public String getDescGarantizado(BigDecimal cod)throws DAOException {
		Session session = obtenerSession();
		try {
			Garantizado gar = new Garantizado();
			gar = (Garantizado) session
					.createCriteria(Garantizado.class)

					.add(Restrictions.eq("codgarantizado",cod)
					).uniqueResult();
		
			return gar.getDesgarantizado();
		} catch (Exception ex) {
			logger.error("Se ha producido un error en el acceso a la BBDD",ex);
			throw new DAOException("Se ha producido un error en el acceso a la BBDD",ex);
		}	
	}		
	
	public String getDescCalcIndemnizacion(BigDecimal cod)throws DAOException {
		Session session = obtenerSession();
		try {
			CalculoIndemnizacion cal = new CalculoIndemnizacion();
			cal = (CalculoIndemnizacion) session
					.createCriteria(CalculoIndemnizacion.class)

					.add(Restrictions.eq("codcalculo",cod)
					).uniqueResult();
		
			return cal.getDescalculo();
		} catch (Exception ex) {
			logger.error("Se ha producido un error en el acceso a la BBDD",ex);
			throw new DAOException("Se ha producido un error en el acceso a la BBDD",ex);
		}	
	}		
	
	public String getDescPctFranquicia(BigDecimal cod)throws DAOException {
		Session session = obtenerSession();
		try {
			PctFranquiciaElegible fran = new PctFranquiciaElegible();
			fran = (PctFranquiciaElegible) session
					.createCriteria(PctFranquiciaElegible.class)

					.add(Restrictions.eq("codpctfranquiciaeleg",cod)
					).uniqueResult();
		
			return fran.getDespctfranquiciaeleg();
		} catch (Exception ex) {
			logger.error("Se ha producido un error en el acceso a la BBDD",ex);
			throw new DAOException("Se ha producido un error en el acceso a la BBDD",ex);
		}	
	}
	
	public String getDescMinimoIndemnizable(BigDecimal cod)throws DAOException {
		Session session = obtenerSession();
		try {
			MinimoIndemnizableElegible min = new MinimoIndemnizableElegible();
			min = (MinimoIndemnizableElegible) session
					.createCriteria(MinimoIndemnizableElegible.class)

					.add(Restrictions.eq("pctminindem",cod)
					).uniqueResult();
		
			return min.getDesminindem();
		} catch (Exception ex) {
			logger.error("Se ha producido un error en el acceso a la BBDD",ex);
			throw new DAOException("Se ha producido un error en el acceso a la BBDD",ex);
		}	
	}
	
	public String getDescTipoFranquicia(BigDecimal cod)throws DAOException {
		Session session = obtenerSession();
		try {
			TipoFranquicia tip = new TipoFranquicia();
			tip = (TipoFranquicia) session
					.createCriteria(TipoFranquicia.class)

					.add(Restrictions.eq("codtipofranquicia",cod)
					).uniqueResult();
		
			return tip.getDestipofranquicia();
		} catch (Exception ex) {
			logger.error("Se ha producido un error en el acceso a la BBDD",ex);
			throw new DAOException("Se ha producido un error en el acceso a la BBDD",ex);
		}	
	}
	
	public String getDescCapitalAseguradoEleg(BigDecimal cod)throws DAOException {
		Session session = obtenerSession();
		try {
			CapitalAseguradoElegible cap = new CapitalAseguradoElegible();
			cap = (CapitalAseguradoElegible) session
					.createCriteria(CapitalAseguradoElegible.class)

					.add(Restrictions.eq("pctcapitalaseg",cod)
					).uniqueResult();
		
			return cap.getDescapitalaseg();
		} catch (Exception ex) {
			logger.error("Se ha producido un error en el acceso a la BBDD",ex);
			throw new DAOException("Se ha producido un error en el acceso a la BBDD",ex);
		}	
	}
	
	public List<RiesgoCubiertoModulo> getListRiesgoCubiertoMod(Long lineaSegId,String codmod,Character chart)throws DAOException {
		List<RiesgoCubiertoModulo> rCubMobList;
		Session session = obtenerSession();
		try {
			rCubMobList = (List<RiesgoCubiertoModulo>) session
					.createCriteria(RiesgoCubiertoModulo.class)
					.add(Restrictions.eq("id.lineaseguroid", lineaSegId))
					.add(Restrictions.eq("id.codmodulo", codmod))
					.add(Restrictions.eq("elegible", chart))
					.add(Restrictions.eq("niveleccion", 'C')).list();
			return rCubMobList;
		} catch (Exception ex) {
			logger.error("Se ha producido un error en el acceso a la BBDD",ex);
			throw new DAOException("Se ha producido un error en el acceso a la BBDD",ex);
		}
	}

	 /** Pet. 63497 (REQ.02) ** MODIF TAM (30/03/2020) ** Inicio */
	@Override
	public void cargarcoberturasParcelasCpl(Poliza polizaPpal, Poliza polizaCpl) throws DAOException {
		 Session session = obtenerSession();
		 
		 BigDecimal codCultivo;
		 BigDecimal codVariedad;
		 BigDecimal hoja; 
		 BigDecimal numero;
		 Long idPolizaCpl;
		 
		 logger.debug("**@@**PolizaComplementariaDao.cargarCoberturasParcelas");
		 logger.debug("**@@**CargarCoberturasParcelas, valor de idPolizaPPal: "+polizaPpal.getIdpoliza());
		
		 try {
			 /* Obtenemos las parcelas de la Pol. Complementaria */
			// Obtenemos las incidencias afectadas
			 logger.debug("**@@** cargarCoberturasParcelas, lanzamos la consulta de parcelas de Cpl");
			 
				String sql_par_cpl = "SELECT IDPARCELA, CODCULTIVO, CODVARIEDAD, HOJA, NUMERO "
						+ " FROM o02agpe0.TB_PARCELAS"
						+ " WHERE IDPOLIZA = " +polizaCpl.getIdpoliza();
				
				@SuppressWarnings("unchecked")
				
				
				List<Object[]> listParcelas = (List<Object[]>) session.createSQLQuery(sql_par_cpl).list();
				if (listParcelas != null && listParcelas.size() > 0){
					for (Object[] parcela : listParcelas) {
						
						idPolizaCpl = ((BigDecimal)parcela[0]).longValue();
						codCultivo = ((BigDecimal) parcela[1]);
						codVariedad = ((BigDecimal) parcela[2]);
						hoja = ((BigDecimal)parcela[3]);
						numero = ((BigDecimal)parcela[4]);
						
						logger.debug("**@@** cargarCoberturasParcelas, lanzamos la consulta de parcelas de Ppal");
						
						
						/* Obtenemos el correspondiente idParcela de la poliza principal */
						String sql_par_ppal = "SELECT IDPARCELA "
								+ " FROM o02agpe0.TB_PARCELAS"
								+ " WHERE IDPOLIZA = " +polizaPpal.getIdpoliza()
								+ " AND CODCULTIVO = " +codCultivo
								+ " AND CODVARIEDAD = " +codVariedad
								+ " AND HOJA = " +hoja
								+ " AND NUMERO = " +numero; 
						
						@SuppressWarnings("unchecked")
						
						BigDecimal idParcelaPpal = (BigDecimal) session.createSQLQuery(sql_par_ppal).uniqueResult();
						logger.debug("idParcelaPpal encontrada: "+ idParcelaPpal);
						
						if (idParcelaPpal != null ){
							
							String codmoduloCpl ="CP";
							
							logger.debug("**@@** cargarCoberturasParcelas, Entramos a realizar el InsertSelect");
							
							/* Insertamos para la parcela de la complementaria, lo recuperado de la parcela de la principal */
							StringBuilder sql = new StringBuilder();
							sql.append("INSERT INTO o02agpe0.TB_PARCELAS_COBERTURAS ");
							sql.append("(SELECT sq_parcela_cobertura.nextval, ");
							sql.append(	idPolizaCpl);	
							sql.append(" ,lineaseguroid, ");
							sql.append(" '" + codmoduloCpl + "'" );
							sql.append(" , codconceptoppalmod, codriesgocubierto, codconcepto, codvalor");     
							sql.append(" from o02agpe0.TB_PARCELAS_COBERTURAS ");   
							sql.append("WHERE idparcela =" +idParcelaPpal + " )");
							session.createSQLQuery(sql.toString()).executeUpdate();
						}			
								
					}
				}
			
			 
		 } catch (Exception ex) {
				logger.error("Se ha producido un error en el acceso a la BBDD al cargar las coberturasParcela",ex);
				throw new DAOException("Se ha producido un error en el acceso a la BBDD al cargar las coberturasParcela",ex);
			}	
		
	}
	
	@Override
	public void cargarCapAsegRelModuloCpl(Poliza polizaPpal, Poliza polizaCpl) throws DAOException {
		
		 Session session = obtenerSession();
		 
		 BigDecimal codCultivo;
		 BigDecimal codVariedad;
		 BigDecimal hoja; 
		 BigDecimal numero;
		 Long idParcelaCpl;
		 
		 logger.debug("**@@**PolizaComplementariaDao.cargarCapAsegRelModuloCpl");
		 logger.debug("**@@**cargarCapAsegRelModuloCpl, valor de idPolizaPPal: "+polizaPpal.getIdpoliza());
		
		 try {
			 
			 /* 
			 /* 1º- Obtenemos las parcelas de la Pol. Complementaria */
			 logger.debug("**@@** cargarCapAsegRelModuloCpl, lanzamos la consulta de parcelas de Cpl");
			 
				String sql_par_cpl = "SELECT IDPARCELA, CODCULTIVO, CODVARIEDAD, HOJA, NUMERO "
						+ " FROM o02agpe0.TB_PARCELAS"
						+ " WHERE IDPOLIZA = " +polizaCpl.getIdpoliza();
				
				@SuppressWarnings("unchecked")
				
				
				List<Object[]> listParcelas = (List<Object[]>) session.createSQLQuery(sql_par_cpl).list();
				if (listParcelas != null && listParcelas.size() > 0){
					for (Object[] parcela : listParcelas) {
						
						idParcelaCpl = ((BigDecimal)parcela[0]).longValue();
						codCultivo = ((BigDecimal) parcela[1]);
						codVariedad = ((BigDecimal) parcela[2]);
						hoja = ((BigDecimal)parcela[3]);
						numero = ((BigDecimal)parcela[4]);
						
						
						/* 2º- Obtenemos el idCapitalAsegurado por cada una de las parcelas de la complementaria. */
						String sql_capAseg_Cpl = "SELECT  CASEG.IDCAPITALASEGURADO "  
								+ " FROM o02agpe0.TB_CAPITALES_ASEGURADOS CASEG " 
								+ " WHERE IDPARCELA = " +idParcelaCpl;
								 
						@SuppressWarnings("unchecked")
						
						BigDecimal idCapAsegCpl = (BigDecimal) session.createSQLQuery(sql_capAseg_Cpl).uniqueResult();
						logger.debug("idCapAseg_Cpl encontrada: "+ idCapAsegCpl);
						
						logger.debug("**@@** cargarCapAsegRelModuloCpl, lanzamos la consulta de parcelas de Ppal");
						
						
						/* 3º- Obtenemos el correspondiente idParcela de la poliza principal */
						String sql_par_ppal = "SELECT IDPARCELA "
								+ " FROM o02agpe0.TB_PARCELAS"
								+ " WHERE IDPOLIZA = " +polizaPpal.getIdpoliza()
								+ " AND CODCULTIVO = " +codCultivo
								+ " AND CODVARIEDAD = " +codVariedad
								+ " AND HOJA = " +hoja
								+ " AND NUMERO = " +numero; 
						
						@SuppressWarnings("unchecked")
						
						BigDecimal idParcelaPpal = (BigDecimal) session.createSQLQuery(sql_par_ppal).uniqueResult();
						logger.debug("idParcelaPpal encontrada: "+ idParcelaPpal);
						
						if (idParcelaPpal != null ){
							
							/* 4º- Obtenemos el idCapitalAsegurado de la parcela de la principal*/
							String sql_capAseg_Ppal = "SELECT  CASEG.IDCAPITALASEGURADO "  
									+ " FROM o02agpe0.TB_CAPITALES_ASEGURADOS CASEG " 
									+ " WHERE IDPARCELA = " +idParcelaPpal;
									 
							@SuppressWarnings("unchecked")
							
							BigDecimal idCapAsegPpal = (BigDecimal) session.createSQLQuery(sql_capAseg_Ppal).uniqueResult();
							logger.debug("idCapAseg_Ppal encontrada: "+ idCapAsegPpal);
							
							/* Insertamos el registro correspondiente en la tabla TB_CAP_ASEG_REL_MODULO */ 
							String codmoduloCpl ="CP";
							
							logger.debug("**@@** cargarCoberturasParcelas, Entramos a realizar el InsertSelect");
							
							/* Insertamos para la parcela de la complementaria, lo recuperado de la parcela de la principal */
							StringBuilder sql = new StringBuilder();
							sql.append("INSERT INTO o02agpe0.TB_CAP_ASEG_REL_MODULO ");
							sql.append("(SELECT SQ_TB_CAP_ASEG_REL_MODULO.nextval, ");
							sql.append(	idCapAsegCpl);	
							sql.append(", '" +codmoduloCpl +"' ");
							sql.append(" , precio, produccion, preciomodif , produccionmodif, tipo_rdto ");     
							sql.append(" from o02agpe0.TB_CAP_ASEG_REL_MODULO ");   
							sql.append("WHERE IDCAPITALASEGURADO =" +idCapAsegPpal + " )");
							session.createSQLQuery(sql.toString()).executeUpdate();
						}			
								
					}
				}
			
			 
		 } catch (Exception ex) {
				logger.error("Se ha producido un error en el acceso a la BBDD al cargar las coberturasParcela",ex);
				throw new DAOException("Se ha producido un error en el acceso a la BBDD al cargar las coberturasParcela",ex);
			}	
		
	}

	/** Pet. 63497 (REQ.02) ** MODIF TAM (30/03/2020) ** Fin **/
}
