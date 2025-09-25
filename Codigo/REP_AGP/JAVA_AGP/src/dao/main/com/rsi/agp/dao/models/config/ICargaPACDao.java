package com.rsi.agp.dao.models.config;

import java.io.Reader;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.Session;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.dao.models.GenericDao;
import com.rsi.agp.dao.tables.cpl.RelEspeciesSCEspeciesST;
import com.rsi.agp.dao.tables.pac.FormPacCargasBean;
import com.rsi.agp.dao.tables.pac.PacCargas;
import com.rsi.agp.dao.tables.pac.PacParcelas;
import com.rsi.agp.dao.tables.poliza.DatoVariableParcela;
import com.rsi.agp.dao.tables.poliza.ModuloPoliza;

@SuppressWarnings("rawtypes")
public interface ICargaPACDao extends GenericDao {

	public void saveDatosCargaPAC (List<PacParcelas> listPacAsegParcelas, Reader contenidoFicheroPAC) throws DAOException;
	public Reader getContenidoArchivoCargaPAC(Long idCargaPAC) throws DAOException;
	public List<Long> existeParcelasPACAsegurado(BigDecimal codlinea, BigDecimal codplan, String cifnifAsegurado, BigDecimal codentidad,
											  BigDecimal codentidadMed, BigDecimal codsubentidadMed) throws DAOException;
	public List <PacCargas> listarCargas(PacCargas pacCarga) throws DAOException;
	public List<RelEspeciesSCEspeciesST> buscarST(BigDecimal lineaSeguroId,BigDecimal codLinea,BigDecimal codPlan, BigDecimal  codCultST, BigDecimal  codVarST) throws DAOException;
	public List<PacParcelas> filtrarPacProvComTerSubterm (Map<String,Object> filtro, List<PacParcelas> listParcelasPac)throws DAOException;
	public List getParcelasPac(String nifcif, BigDecimal codlinea, BigDecimal codplan, Long claseId, BigDecimal codentidad) throws DAOException;
	public List getCampoClaseDetalle(Long clase, String campo);
	public List getCampoClaseDetalle(Long clase, Long lineaseguroid, String campo);
	public Criteria getCriteriaParcelasPac(Session session, List provincias, List comarcas, List terminos, List subterminos, 
    		List cultivos, List variedades, String nifcif, BigDecimal codlinea, BigDecimal codplan, BigDecimal codentidad);
	public void saveDatoVarParcela(DatoVariableParcela nDat) throws DAOException;
	public void actualizaCLOBPacCargas(List<PacParcelas> listPacAsegParcelas, final String clob) throws DAOException;
	public List getlstSisCultClaseDetalle(BigDecimal clase, Long lineaseguroid, String campo);
	public String executeStoreProcCargarPAC(String fichero, String codUsuario, FormPacCargasBean formPacCargasBean) throws DAOException;
	public boolean existeArchivoCargado(final String filename);
	public void dropCargaPAC(BigDecimal idCargaPAC) throws DAOException;
	Map<BigDecimal, BigDecimal> getDatosVarPantalla (Long lineaSeguroId) throws DAOException;
	public List<ModuloPoliza> getModulosPoliza(Long idpoliza, Long lineaseguroid)throws DAOException;
	public String cargaParcelasPolizaDesdePAC(Long idPoliza, Long idClase, String listaIdPacAseg, String listaDVDefecto ) throws DAOException;
	public boolean existeESMedEntUsuario (BigDecimal entMed, BigDecimal subentMed, List<BigDecimal> listaEntidades);
}
