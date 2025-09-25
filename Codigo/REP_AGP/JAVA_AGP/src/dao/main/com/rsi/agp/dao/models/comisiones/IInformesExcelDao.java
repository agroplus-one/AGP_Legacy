package com.rsi.agp.dao.models.comisiones;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.dao.models.GenericDao;
import com.rsi.agp.dao.tables.comisiones.Cierre;
import com.rsi.agp.dao.tables.comisiones.EntidadesOperadoresInforme;
import com.rsi.agp.dao.tables.comisiones.impagados.ReciboImpagado;
import com.rsi.agp.dao.tables.comisiones.unificado.RgaUnifMediadores;
import com.rsi.agp.dao.tables.comisiones.unificado.informes.cierre.InformeComsFamLinEnt;
import com.rsi.agp.dao.tables.comisiones.unificado.informes.cierre.InformeComsFacturacion;
import com.rsi.agp.dao.tables.comisiones.unificado.informes.cierre.InformeColaboradores2015;
import com.rsi.agp.dao.tables.comisiones.unificado.informes.cierre.InformeCorredores2015;
import com.rsi.agp.dao.tables.comisiones.unificado.informes.cierre.InformeComsImpagados2015;
import com.rsi.agp.dao.tables.comisiones.unificado.informes.cierre.InformeComsRGA2015;
import com.rsi.agp.dao.tables.comisiones.unificado.informes.cierre.InformeDetMediador2015;
import com.rsi.agp.dao.tables.comisiones.unificado.informes.cierre.InformeEntidades2015;
import com.rsi.agp.dao.tables.comisiones.unificado.informes.cierre.InformeEntidadesOperadores2015;
import com.rsi.agp.dao.tables.comisiones.unificado.informes.cierre.InformeTotMediador2015;
import com.rsi.agp.dao.tables.comisiones.RgaComisiones;
import com.rsi.agp.dao.tables.comisiones.InformeMediadores;
import com.rsi.agp.dao.tables.commons.Usuario;

@SuppressWarnings("rawtypes")
public interface IInformesExcelDao extends GenericDao {

	public List<RgaComisiones> listComisionesEntidades(final Long idCierre) throws DAOException;

	public List<RgaComisiones> listDetalleMediador(final Long idCierre) throws DAOException;

	public List<ReciboImpagado> listImpagados(final Cierre cierre) throws DAOException;

	public void saveFicheroExcelCierre(final String nombreFicheroExcel, final String rutaFicheroExcel, final Long idCierre,
			final Usuario usuario) throws DAOException, IOException;

	public List<InformeMediadores> listMediadores(final Date fechaCierre) throws DAOException;

	public List<RgaComisiones> listTotalesMediador(final Long idCierre) throws DAOException;

	public BigDecimal getNumeroMaximoMes(final Date fechaCierre) throws DAOException;

	public List<EntidadesOperadoresInforme> listEntidadesOperadores(final Long idCierre) throws DAOException;

	public List<InformeEntidades2015> listComisionesEntidades2015() throws DAOException;

	public List<InformeDetMediador2015> listDetalleMediador2015() throws DAOException;

	public List<InformeTotMediador2015> listTotalesMediador2015() throws DAOException;

	public List<InformeComsImpagados2015> listImpagados2015(final Long idCierre) throws DAOException;

	public List<InformeComsRGA2015> listComsRGA2015() throws DAOException;

	public List<InformeEntidadesOperadores2015> listEntidadesOperadores2015() throws DAOException;

	public List<RgaUnifMediadores> listMediadores2015(final Date fechaCierre, boolean segGen) throws DAOException;

	public BigDecimal getNumeroMaximoMes2015(final Date fechaCierre) throws DAOException;

	public List<InformeColaboradores2015> listColaboradores2015() throws DAOException;

	public List<InformeCorredores2015> listCorredores2015() throws DAOException;

	public List<InformeComsFamLinEnt> listComsFamLinEnt() throws DAOException;

	public List<InformeComsFacturacion> listComsFacturacion() throws DAOException;
}
