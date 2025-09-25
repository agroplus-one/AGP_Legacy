package com.rsi.agp.dao.models.sbp;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.hibernate.Session;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.dao.models.GenericDao;
import com.rsi.agp.dao.tables.anexo.AnexoModificacion;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.sbp.MtoImpuestoSbp;
import com.rsi.agp.dao.tables.sbp.ParcelaSbp;
import com.rsi.agp.dao.tables.sbp.PolizaSbp;
import com.rsi.agp.dao.tables.sbp.PrimaMinimaSbp;
import com.rsi.agp.dao.tables.sbp.Sobreprecio;
import com.rsi.agp.dao.tables.sbp.TasasSbp;

@SuppressWarnings("rawtypes")
public interface ISimulacionSbpDao extends GenericDao {
	
	 boolean isCargadasTasas(PolizaSbp polizaSbp) throws DAOException;
	 boolean isCargadosImpuestos(PolizaSbp polizaSbp) throws DAOException;
	 boolean isCargadoSbpCultivo(PolizaSbp polizaSbp) throws DAOException;
	 boolean isCargadoPeriodoContratacion(PolizaSbp polizaSbp) throws DAOException;
	 boolean isCargadaPrimaMinima(PolizaSbp polizaSbp) throws DAOException;
	 boolean isPolizaConSiniestro(PolizaSbp polizaSbp) throws DAOException;
	 boolean isLineaEnPeriodoContratacion(PolizaSbp polizaSbp) throws DAOException;
	 PolizaSbp existePolizaSbp(PolizaSbp polizaSbp)throws DAOException;
	 PrimaMinimaSbp getPrimaMinima(Long lineaseguroid) throws DAOException;
	 List <MtoImpuestoSbp> getImpuestos(BigDecimal codPlan)throws DAOException;
	 Map<String, Object> getSobreprecios(PolizaSbp polizaSbp, String tipoPoliza, boolean incluirCplEnSbp, boolean filtroxComarca,List<ParcelaSbp> lstParSbp) throws DAOException;
	 List<ParcelaSbp> rellenaPrimas(List<ParcelaSbp> parcelaSbps, HttpServletRequest request, Long lineaseguroId) throws DAOException;
	 TasasSbp getTasa(Long lineaseguroId, ParcelaSbp par) throws DAOException;
	 void deleteParcelas(PolizaSbp polizaSbp2) throws DAOException;
	 void cambiaEstado(String referencia) throws DAOException;
	 List<ParcelaSbp> getParcelasSbpbyIdPolizaPpal(Long idPolizaPpal, boolean filtroXestado) throws DAOException;
	 List<ParcelaSbp> getParcelasParaSbp(PolizaSbp polizaSbp, String tipoPoliza, boolean incluirCplEnSbp,
			boolean filtroxComarca, List cultivos) throws DAOException;
	 List<ParcelaSbp> getParcelasSimulacion(PolizaSbp polizaSbp) throws DAOException;
	 void actualizarSobreprecio(BigDecimal sobreprecio, BigDecimal codCultivo, BigDecimal codProvincia, BigDecimal codComarca) throws DAOException;
	 List getCultivosSbp(Long lineaseguroId) throws DAOException;
	 List<PolizaSbp> existeSuplemento(Long idpolizaPpal) throws DAOException;
	 public List<ParcelaSbp> getParcelasCPLconSbp(Long idPolizaCPL,List cultivos,Long lineaseguroId) throws DAOException ;
	 public BigDecimal getPlanSbp() throws DAOException;
	 public BigDecimal esParcelaConSbp(BigDecimal cultivo, BigDecimal provincia, Long lineaseguroId,boolean comprobarfecha, List<BigDecimal> lstTiposCapital) throws DAOException;
	 public List<Object> getSobreprecioYtasasFromParcelas(BigDecimal cultivo,BigDecimal codProvincia, String referencia,BigDecimal comarca,boolean filtroComarca)
				throws DAOException;
	 public Sobreprecio getMaxSobrepreciofromTabla(BigDecimal cultivo,BigDecimal codProvincia, Long lineaseguroId, List<BigDecimal> lstTiposCapital)
				throws DAOException;
	 public TasasSbp getMaxTasasfromTabla(BigDecimal cultivo,BigDecimal codProvincia, Long lineaseguroId,BigDecimal codcomarca)
				throws DAOException;
	 public boolean isCultivoSbpContratable(Long lineaseguroid,BigDecimal codcultivo);
	 public boolean isCultivosEnPeriodoContratacion(PolizaSbp polizaSbp) throws DAOException;
	 public boolean existeCultivosSbpContratables(PolizaSbp p) throws DAOException;
	 ArrayList<AnexoModificacion> getAnexosCuponParaSbp() throws DAOException;
	 ArrayList<AnexoModificacion> getAnexosCuponParaSbp(Long[] estadosCupon, Character revisarSbp) throws DAOException;
	 BigDecimal esPolizaConSbp(Long idPoliza,BigDecimal codLinea,BigDecimal codPlan) throws DAOException;
	 void actualizaFlagSbp(Long idSbp, Session sesion) throws DAOException;
	 List<PolizaSbp> getPolizasSbpParaSuplementos() throws DAOException;
	 boolean esUltimoDiaEnvSupl() throws DAOException;
	 void updateFlag(Long idSbp,Character flag, Session sesion) throws DAOException;
	void guardaSuplemento(PolizaSbp polizaSbp, Usuario usuario,
			Long idPolizaPpal, Character characterN,Session session, BigDecimal estado) throws Exception ;
	void updateFlagAnexo(ArrayList<Long> listaAnexosActualizar) throws DAOException;
	public void insertarHistoricoSuplemento(PolizaSbp psbp) throws DAOException;
	public void updateFlagbyIdsPolSbp(List<Long> lstPolSbp,Character flag) throws DAOException;
	public void deleteParcela(ParcelaSbp parcela) throws DAOException;
	
	/** PET-63699 DNF 14/05/2020*/ 
	public Long getPolizaSbpId(final Long idPolizaPpal) throws DAOException; 
	public void updateGenSplCpl(final Long idPolizaPpal) throws DAOException; 
	/** FIN PET-63699 DNF 14/05/2020*/ 
	public PolizaSbp getPolizaSbp(final Long idpoliza, final Character tipoRef) throws DAOException;
}
