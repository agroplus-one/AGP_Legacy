package com.rsi.agp.dao.models.poliza;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.webapp.util.VistaImportesPorGrupoNegocio;
import com.rsi.agp.dao.models.GenericDao;
import com.rsi.agp.dao.tables.poliza.DistribucionCoste;
import com.rsi.agp.dao.tables.poliza.dc2015.DistribucionCoste2015;

import es.agroseguro.seguroAgrario.financiacion.FinanciacionDocument;

@SuppressWarnings("rawtypes")
public interface IDistribucionCosteDAO extends GenericDao {

	public DistribucionCoste getDistribucionCoste(final Long idPoliza,
			final String codModulo, final BigDecimal filaComparativa)
			throws DAOException;

	public DistribucionCoste getDistribucionCosteById(final Long id)
			throws DAOException;

	public void deleteDistribucionCoste(final Long idPoliza,
			final String codModulo, final BigDecimal filaComparativa)
			throws DAOException;

	public void deleteDistribucionCosteById(final Long id) throws DAOException;

	public void saveDistribucionCoste(final DistribucionCoste distCoste,
			final Long idPoliza) throws DAOException;
	
	public void saveDistribucionCoste2015(final DistribucionCoste2015 distCoste,
			final Long idPoliza) throws DAOException; 

	public DistribucionCoste saveDistribucionCoste(
			final es.agroseguro.seguroAgrario.distribucionCostesSeguroAgrario.Poliza polizaXML,
			final Long idPoliza, final String codModulo,
			final BigDecimal filaComparativa) throws DAOException;

	public DistribucionCoste updateDistribucionCoste(
			final es.agroseguro.seguroAgrario.distribucionCostesSeguroAgrario.Poliza polizaXML,
			final Long idPoliza, final String codModulo,
			final BigDecimal filaComparativa) throws DAOException;

	public DistribucionCoste2015 saveDistribucionCoste2015(
			final es.agroseguro.seguroAgrario.distribucionCostesSeguroAgrario.Poliza polizaXML,
			final Long idpoliza, final String codModulo, final BigDecimal filaComparativa, Long idComparativa)throws DAOException;
	
	public Set<DistribucionCoste2015> saveDistribucionCoste2015Unificado(
			es.agroseguro.distribucionCostesSeguro.Poliza plzUnificada,
			Long idpoliza, String codModulo, BigDecimal filaComparativa,Long idComparativa,
			FinanciacionDocument financiacion, int periodoFracc, int opcionFracc, BigDecimal valorOpcionFracc, Boolean esGanado) throws DAOException;
	

	public DistribucionCoste2015 saveDistribucionCoste2015(
			es.agroseguro.seguroAgrario.distribucionCostesSeguroAgrario.Poliza polizaXML,
			Long idpoliza, String codModulo, BigDecimal filaComparativa, 
			FinanciacionDocument  financiacion, int periodoFracc, int opcionFracc, BigDecimal valorOpcionFracc,Long idComparativa) throws DAOException;

	public void deleteDistribucionCoste2015(Long idpoliza, String codModulo, Long idComparativa)throws DAOException;
	
	public List<DistribucionCoste2015> getDistribucionCoste2015ByIdPoliza(final Long idPoliza)
			throws DAOException;
	
	public BigDecimal getTotalCosteTomadorAFinanciar(Long idPoliza) throws DAOException;
	
	public void updateComsDistCoste2015(Set<DistribucionCoste2015> distCostes,
			List<VistaImportesPorGrupoNegocio> vistasImportes, String codModulo, BigDecimal idComparativa) throws DAOException;
	
	public String getLiteralBonificacion (String codRecargo);
}
