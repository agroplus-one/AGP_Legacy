package com.rsi.agp.dao.models.comisiones;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.jmesa.filter.CierreFilter;
import com.rsi.agp.core.jmesa.sort.CierreSort;
import com.rsi.agp.dao.models.GenericDao;
import com.rsi.agp.dao.tables.comisiones.Cierre;
import com.rsi.agp.dao.tables.comisiones.Fase;
import com.rsi.agp.dao.tables.comisiones.Fichero;
import com.rsi.agp.dao.tables.comisiones.ReportCierre;
import com.rsi.agp.dao.tables.comisiones.unificado.FaseUnificado;

@SuppressWarnings("rawtypes")
public interface ICierreComisionesDao extends GenericDao {

	List<Fase> listFasesSinCerrar(Date fechaCierre) throws DAOException;

	List<Fichero> getFasesSinCierre() throws DAOException;

	Integer periodoCerrado(Date fechacierre) throws DAOException;

	List<Fichero> ficherosNoAceptados(Date fecha) throws DAOException;

	List<ReportCierre> getListaInformesGenerados(final Long idCierre) throws DAOException;

	ReportCierre getContenidoInforme(Long idInforme) throws DAOException;

	ReportCierre getInformeById(Long idInforme) throws DAOException;

	List<Fichero> getFicheroByIdCierre(Long idCierre) throws DAOException;

	Cierre getCierreByFecha(Date fechaCierre) throws DAOException;

	int getCierreCountWithFilter(CierreFilter filter);

	Collection<Cierre> getCierreWithFilterAndSort(CierreFilter filter, CierreSort sort, int rowStart, int rowEnd)
			throws BusinessException;

	public void actualizarInformesFicheroComisiones(Long id) throws DAOException;

	public Long obtenerIdCierreMasReciente() throws DAOException;

	public void borrarCierrePorId(Cierre cierre) throws DAOException;

	public boolean borrarInformesComisionesByIdCierre(Long idCierre);

	List<FaseUnificado> listFasesUnifSinCerrar(Date fechaCierre, Long idCierre) throws DAOException;

	List<FaseUnificado> ficherosUnificadosNoAceptados(Date fecha) throws DAOException;

	FaseUnificado existeComisiomesUnif(String fase, BigDecimal codplan, boolean mirarFecha) throws DAOException;

	public void borrarRgaUnifMediadores(final Date fechaCierre) throws Exception;
}
