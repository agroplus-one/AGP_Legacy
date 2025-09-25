package com.rsi.agp.dao.models.config;

import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.Session;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.dao.models.GenericDao;
import com.rsi.agp.dao.tables.cpl.RelEspeciesSCEspeciesST;
import com.rsi.agp.dao.tables.cvs.CvsCarga;
import com.rsi.agp.dao.tables.cvs.CvsParcela;
import com.rsi.agp.dao.tables.cvs.FormCsvCargasBean;
import com.rsi.agp.dao.tables.poliza.DatoVariableParcela;
import com.rsi.agp.dao.tables.poliza.ModuloPoliza;

@SuppressWarnings("rawtypes")
public interface ICargaCSVDao extends GenericDao {

	public void saveDatosCargaCSV(List<CvsParcela> listCsvAsegParcelas, Reader contenidoFicheroCSV) throws DAOException;

	public Reader getContenidoArchivoCargaCSV(Long idCargaCSV) throws DAOException;

	public List<Long> existeParcelasCSVAsegurado(BigDecimal codlinea, BigDecimal codplan, String cifnifAsegurado,
			BigDecimal codentidad, BigDecimal codentidadMed, BigDecimal codsubentidadMed) throws DAOException;

	public List<CvsCarga> listarCargas(CvsCarga csvCarga) throws DAOException;

	public List<RelEspeciesSCEspeciesST> buscarST(BigDecimal lineaSeguroId, BigDecimal codLinea, BigDecimal codPlan,
			BigDecimal codCultST, BigDecimal codVarST) throws DAOException;

	public List getParcelasCsv(String nifcif, BigDecimal codlinea, BigDecimal codplan, BigDecimal codentidad)
			throws DAOException;

	public List getCampoClaseDetalle(Long clase, String campo);

	public List getCampoClaseDetalle(Long clase, Long lineaseguroid, String campo);

	public Criteria getCriteriaParcelasCsv(Session session, List provincias, List comarcas, List terminos,
			List subterminos, List cultivos, List variedades, String nifcif, BigDecimal codlinea, BigDecimal codplan,
			BigDecimal codentidad);

	public void saveDatoVarParcela(DatoVariableParcela nDat) throws DAOException;

	public List getlstSisCultClaseDetalle(BigDecimal clase, Long lineaseguroid, String campo);

	public boolean existeArchivoCargado(final String filename);

	public void dropCargaCSV(BigDecimal idCargaCSV) throws DAOException;

	Map<BigDecimal, BigDecimal> getDatosVarPantalla(Long lineaSeguroId) throws DAOException;

	public List<ModuloPoliza> getModulosPoliza(Long idpoliza, Long lineaseguroid) throws DAOException;

	public String cargaParcelasPolizaDesdeCSV(Long idPoliza, Long idClase, String listaIdCsvAseg, String listaDVDefecto)
			throws DAOException;

	public boolean existeESMedEnt(BigDecimal entMed, BigDecimal subentMed);

	public String executeStoreProcCargarCSV(String fichero, String codUsuario, FormCsvCargasBean formCsvCargasBean)
			throws DAOException, IOException;

	public boolean existePlanLinea(BigDecimal plan, BigDecimal linea);

	public List<CvsParcela> filtrarCsvProvComTerSubterm(Map<String, Object> filtro, List<CvsParcela> listParcelasCsv)
			throws DAOException;
}