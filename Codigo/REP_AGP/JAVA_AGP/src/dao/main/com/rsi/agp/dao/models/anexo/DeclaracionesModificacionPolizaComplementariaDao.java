package com.rsi.agp.dao.models.anexo;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.dao.filters.FiltroUtils;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.tables.anexo.CapitalAsegurado;

public class DeclaracionesModificacionPolizaComplementariaDao extends BaseDaoHibernate implements IDeclaracionesModificacionPolizaComplementariaDao{
	private Log logger = LogFactory.getLog(DeclaracionesModificacionPolizaComplementariaDao.class);

	@Override
	public List<CapitalAsegurado> getCapitalesAsegPolCpl(CapitalAsegurado capAsegurado) throws DAOException {
		Session session = obtenerSession();
		try {
				Criteria criteria = session.createCriteria(CapitalAsegurado.class);		
				criteria.createAlias("parcela", "par");
				
				if(FiltroUtils.noEstaVacio(capAsegurado.getParcela().getAnexoModificacion().getId())){
			        criteria.add(Restrictions.eq("par.anexoModificacion.id", capAsegurado.getParcela().getAnexoModificacion().getId()));
				}
				
				//criteria.addOrder(Order.asc("par.hoja")).addOrder(Order.asc("par.numero"));
				criteria.addOrder(Order.asc("par.codprovsigpac")).addOrder(Order.asc("par.codtermsigpac")).addOrder(Order.asc("par.agrsigpac")).addOrder(Order.asc("par.zonasigpac")).addOrder(Order.asc("par.poligonosigpac")).addOrder(Order.asc("par.parcelasigpac")).addOrder(Order.asc("par.recintosigpac"));
				
				
				if(FiltroUtils.noEstaVacio(capAsegurado.getParcela().getHoja())){
					criteria.add(Restrictions.eq("par.hoja", capAsegurado.getParcela().getHoja()));
				}
				if(FiltroUtils.noEstaVacio(capAsegurado.getParcela().getNumero())){
					criteria.add(Restrictions.eq("par.numero", capAsegurado.getParcela().getNumero()));
				}
			
		        if(FiltroUtils.noEstaVacio(capAsegurado.getParcela().getCodprovincia())){
			        criteria.add(Restrictions.eq("par.codprovincia", capAsegurado.getParcela().getCodprovincia()));
		        }
		        if(FiltroUtils.noEstaVacio(capAsegurado.getParcela().getCodcomarca())){
			        criteria.add(Restrictions.eq("par.codcomarca", capAsegurado.getParcela().getCodcomarca()));
		        }
		        if(FiltroUtils.noEstaVacio(capAsegurado.getParcela().getCodtermino())){
			        criteria.add(Restrictions.eq("par.codtermino", capAsegurado.getParcela().getCodtermino()));
		        }
		        if(FiltroUtils.noEstaVacio(capAsegurado.getParcela().getSubtermino())){		        
			        criteria.add(Restrictions.eq("par.subtermino", capAsegurado.getParcela().getSubtermino()));
		        }
		        if(FiltroUtils.noEstaVacio(capAsegurado.getParcela().getCodcultivo())){
				      criteria.add(Restrictions.eq("par.codcultivo", capAsegurado.getParcela().getCodcultivo()));
		        }
		        if(FiltroUtils.noEstaVacio(capAsegurado.getParcela().getCodvariedad())){
				      criteria.add(Restrictions.eq("par.codvariedad", capAsegurado.getParcela().getCodvariedad()));
		        }
		        if(FiltroUtils.noEstaVacio(capAsegurado.getParcela().getNomparcela())){
		        	criteria.add(Restrictions.like("par.nomparcela", "%" + capAsegurado.getParcela().getNomparcela() + "%"));	        	
		        }
		        if(FiltroUtils.noEstaVacio(capAsegurado.getParcela().getPoligono())){
		        	criteria.add(Restrictions.eq("par.poligono", capAsegurado.getParcela().getPoligono()));
		        }
		        if(FiltroUtils.noEstaVacio(capAsegurado.getParcela().getParcela_1())){
		        	criteria.add(Restrictions.eq("par.parcela_1", capAsegurado.getParcela().getParcela_1()));
		        }
		        if(FiltroUtils.noEstaVacio(capAsegurado.getParcela().getCodprovsigpac())){
			        criteria.add(Restrictions.eq("par.codprovsigpac",capAsegurado.getParcela().getCodprovsigpac()));
		        }
		        if(FiltroUtils.noEstaVacio(capAsegurado.getParcela().getCodtermsigpac())){
			        criteria.add(Restrictions.eq("par.codtermsigpac",capAsegurado.getParcela().getCodtermsigpac()));
		        }
		        if(FiltroUtils.noEstaVacio(capAsegurado.getParcela().getAgrsigpac())){
			        criteria.add(Restrictions.eq("par.agrsigpac",capAsegurado.getParcela().getAgrsigpac()));
		        }
		        if(FiltroUtils.noEstaVacio(capAsegurado.getParcela().getZonasigpac())){
			        criteria.add(Restrictions.eq("par.zonasigpac",capAsegurado.getParcela().getZonasigpac()));
		        }
		        if(FiltroUtils.noEstaVacio(capAsegurado.getParcela().getPoligonosigpac())){
			        criteria.add(Restrictions.eq("par.poligonosigpac",capAsegurado.getParcela().getPoligonosigpac()));
		        }
		        if(FiltroUtils.noEstaVacio(capAsegurado.getParcela().getParcelasigpac())){
			        criteria.add(Restrictions.eq("par.parcelasigpac",capAsegurado.getParcela().getParcelasigpac()));
		        }
		        if(FiltroUtils.noEstaVacio(capAsegurado.getParcela().getRecintosigpac())){
			        criteria.add(Restrictions.eq("par.recintosigpac",capAsegurado.getParcela().getRecintosigpac()));
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
		      //tipo modificacion
				if(capAsegurado.getParcela().getTipomodificacion() != null && Character.isLetter(capAsegurado.getParcela().getTipomodificacion())){
					if("A".equals(capAsegurado.getParcela().getTipomodificacion().toString()) || "M".equals(capAsegurado.getParcela().getTipomodificacion().toString()) || "B".equals(capAsegurado.getParcela().getTipomodificacion().toString())){
					    Criterion crit2 = Restrictions.eq("par.tipomodificacion", capAsegurado.getParcela().getTipomodificacion());
			            criteria.add(crit2);
					}
				}
		        
		   return criteria.list(); 
			
		} catch (Exception ex) {
			logger.error("Se ha producido un error en el acceso a la BBDD",ex);
			throw new DAOException("Se ha producido un error en el acceso a la BBDD",ex);
		}
	}

	/* Pet. 78691 ** MODIF TAM (22/12/2021) ** Inicio */
	@Override
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
			logger.error("Error al recuperar la descripción del Sistema de cultivo",excepcion);
			throw new DAOException("Error al recuperar la descripción del Sistema de cultivo",excepcion);
		}
		
		return descSistCultivo;
	}
	/* Pet. 78691 ** MODIF TAM (22/12/2021) ** Fin */
	
	
	
}
