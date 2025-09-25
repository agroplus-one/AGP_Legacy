package com.rsi.agp.dao.models.copy;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.dao.models.GenericDao;
import com.rsi.agp.dao.tables.config.DatoVariableDefault;
import com.rsi.agp.dao.tables.copy.Poliza;
import com.rsi.agp.dao.tables.poliza.DatoVariableParcela;

public interface IPolizaCopyDao extends GenericDao {

	public Poliza getPolizaCopyById(Long idPoliza) throws DAOException;
	public List<Poliza> getByRefPoliza (String refpoliza) throws DAOException;
	public com.rsi.agp.dao.tables.copy.Poliza existeCopyPolizaByReferenciaAndFecha(String tipoReferencia, String refpoliza, Date fechaEmisionRecibo) throws DAOException;
	public Poliza getPolizaCopyMasRecienteByReferencia(Character tipoReferencia, String refpoliza) throws DAOException;
	public List<Poliza> getListaPolizas(Poliza polizaBean) throws DAOException;
	// MPM - 31-07-2012
	// Llama al PL encargado de actualizar los datos variables de la copy
	public void actualizaDVCopy (String cadena, int numParcelas, Long idCopy);
	public List<BigDecimal>  getCodsConceptoOrganizador(Long lineaseguroid) throws DAOException;
	public List<DatoVariableDefault> getDatosVariablesCopy(com.rsi.agp.dao.tables.poliza.Poliza poliza)throws DAOException;
	public void saveDatoVarParcela(DatoVariableParcela varParcela) throws DAOException;
	
	//ASF - Mejora para crear una póliza a partir de los datos de una copy 
	public Long crearPolizaFromCopy(String referencia, String tipoReferencia, BigDecimal clase, Long idCopy);
}
