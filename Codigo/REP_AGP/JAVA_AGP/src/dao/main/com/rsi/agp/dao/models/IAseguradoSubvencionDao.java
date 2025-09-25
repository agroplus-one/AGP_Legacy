package com.rsi.agp.dao.models;

import java.math.BigDecimal;
import java.util.List;

import com.rsi.agp.core.exception.DAOException;

@SuppressWarnings("rawtypes")
public interface IAseguradoSubvencionDao extends GenericDao {

	public boolean existeAuditAsegDatMed(Long idpoliza);
	public void guardaAuditAsegDatosMedida(Long idPoliza, String codUsuario, String xml) throws DAOException; 
	public void guardaAuditCambioSubvs(Long idPoliza, String codUsuario, String detalle) throws DAOException;
	public String getXmlAsegDatosMedida(Long idPoliza)	throws DAOException;
	public List<String> getControlSubvenciones (final List<String> nifsCifs, final BigDecimal codPlan, final BigDecimal codLinea) throws DAOException;
}