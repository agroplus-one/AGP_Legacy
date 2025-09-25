package com.rsi.agp.dao.models.inc;

import java.math.BigDecimal;
import java.util.List;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.dao.models.GenericDao;
import com.rsi.agp.dao.tables.inc.AsuntosInc;
import com.rsi.agp.dao.tables.inc.DocumentosInc;
import com.rsi.agp.dao.tables.inc.TiposDocInc;
import com.rsi.agp.dao.tables.inc.VistaIncidenciasAgro;
import com.rsi.agp.dao.tables.poliza.Poliza;
import com.rsi.agp.dao.tables.inc.Motivos;

@SuppressWarnings("rawtypes")
public interface IAportarDocIncidenciaDao extends GenericDao {

	List<DocumentosInc> getDocumentos(Long idIncidencia) throws DAOException;

	List<TiposDocInc> getExtensionesFicherosValidas() throws DAOException;

	List<AsuntosInc> getAsuntos() throws DAOException;
	List<Motivos> getMotivos() throws DAOException;
	

	TiposDocInc getExtension(String extension) throws DAOException;

	BigDecimal getLineaPoliza(String referencia, BigDecimal plan, Character tipoReferencia);
	
	String getNifCifPoliza(String referencia, Character tipoReferencia, BigDecimal codPlan);
	
	String getReferenciaPoliza(String nifCif, BigDecimal codPlan, BigDecimal codLinea);
	
	BigDecimal getDCPoliza(String referencia, Character tipoRef, BigDecimal codPlan, BigDecimal codLinea);
	
	BigDecimal getIdPoliza(String referencia, Character tipoRef, BigDecimal codPlan, BigDecimal codLinea);

	String getCodEstInc(BigDecimal numInc, BigDecimal codPlan, BigDecimal codLinea, BigDecimal anho); 
		
	String getNombLinea(BigDecimal codLinea) throws DAOException;
	
	Poliza getPolizaById(Long idPoliza) throws DAOException ;
	
	VistaIncidenciasAgro getIncidenciasById(Long idIncidencia) throws DAOException ;
	
	void actualizarIncidenciaPlanRefYTipo(BigDecimal plan, String referencia, Character tipoRef, Long idIncidencia);
	VistaIncidenciasAgro getPlanRefTipoRefFromIncidenciaById(Long idIncidencia) throws DAOException;
}
