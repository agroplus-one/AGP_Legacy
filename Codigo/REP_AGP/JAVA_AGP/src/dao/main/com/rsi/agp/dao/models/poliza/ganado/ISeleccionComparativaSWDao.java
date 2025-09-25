package com.rsi.agp.dao.models.poliza.ganado;

import java.math.BigDecimal;
import java.sql.Clob;
import java.util.List;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.dao.models.GenericDao;
import com.rsi.agp.dao.tables.cgen.DatosBuzonGeneral;
import com.rsi.agp.dao.tables.poliza.ComparativaPolizaSimple;

@SuppressWarnings("rawtypes")
public interface ISeleccionComparativaSWDao extends GenericDao {
	
	final long CODCPTO_TIPO_ASEG_GANADO = 1079;
	final long CODUSO_POLIZA = 31;
	final long CODUBICACION_CABECERA_DV = 18;
	
	Long actualizaModuloRenovable(long idpoliza, long lineaseguroid, String codModulo, int numComparativa, int renovable, Integer tipoAsegGanado) throws DAOException;
	void guardaListaComparativasPoliza(long idpoliza, List<ComparativaPolizaSimple> listCp) throws DAOException;
	void guardaListaComparativasAnexo(long idanexo, List<ComparativaPolizaSimple> listCp) throws DAOException;
	boolean aplicaTipoAseguradoGanado(long lineaseguroid) throws DAOException;
	public boolean aplicaValidacionCapitalRetirada(long idAnexo) throws DAOException;
	List<DatosBuzonGeneral> obtenerListaTipoAseguradoGanado() throws DAOException;
	public BigDecimal getMaxNumComparativas() throws DAOException;
	public Long  getSecuenciaComparativa() throws DAOException;
	public Clob getRespuestaModulosPolizaCoberturaSW(Long idPoliza, String codModulo, Integer operacion) throws DAOException ;
	void borrarComparativasNoElegidas(long idpoliza, long lineaseguroid, String[] infoModulos) throws DAOException;
}