package com.rsi.agp.dao.models.poliza;

import java.math.BigDecimal;
import java.util.List;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.dao.models.GenericDao;
import com.rsi.agp.dao.tables.cpl.Modulo;
import com.rsi.agp.dao.tables.cpl.ModuloId;
import com.rsi.agp.dao.tables.cpl.RiesgoCubiertoModulo;
import com.rsi.agp.dao.tables.poliza.CapitalAsegurado;
import com.rsi.agp.dao.tables.poliza.Poliza;

public interface IPolizaComplementariaDao extends GenericDao{

	public Modulo getModuloPPalPoliza(ModuloId idModulo) throws DAOException;
	public List<Poliza> getPolizaByRef(String referencia,Long lineaseguroid)throws DAOException;
	public Modulo getModuloCplPoliza(Long lineaseguroid, String codmodulo)throws DAOException;
	public Long getIdPolizaByRef(String referencia,Character tipo,Long lineaseguroid) throws DAOException;
	public Long getIdPolizaByRef(String referencia,Character moduloPolizaPrincipal) throws DAOException;
	public List<CapitalAsegurado> getCapitalesAsegPolCpl(CapitalAsegurado capitalAsegurado)throws DAOException;
	public  Poliza  existePolizaCpl(Long idPolizaPpal) throws DAOException;
	public List<Poliza> getPolizaByTipoRef(Poliza poliza, Character tipoRef) throws DAOException;
	public BigDecimal getFilaModulo(final Long lineaseguroid,
			final String codmodulo, final BigDecimal codconceptoppalmod,
			final BigDecimal codriesgocubierto);
	public String getDescGarantizado(BigDecimal cod)throws DAOException;
	public String getDescCalcIndemnizacion(BigDecimal cod)throws DAOException;
	public String getDescPctFranquicia(BigDecimal cod)throws DAOException;
	public String getDescMinimoIndemnizable(BigDecimal cod)throws DAOException;
	public String getDescTipoFranquicia(BigDecimal cod)throws DAOException;
	public String getDescCapitalAseguradoEleg(BigDecimal cod)throws DAOException;
	public List<RiesgoCubiertoModulo> getListRiesgoCubiertoMod(Long lineaSegId,String codmod,Character chart)throws DAOException;
	/** Pet. 63497 (REQ.02) ** MODIF TAM (30/03/2020) ** Inicio */
	public void cargarcoberturasParcelasCpl (Poliza PolizaPpal, Poliza polizaCpl) throws DAOException;
	public void cargarCapAsegRelModuloCpl (Poliza PolizaPpal, Poliza polizaCpl) throws DAOException;
	/* ESC-13939 ** MODIF TAM (19.05.2021) ** Inicio **/
	public Long getIdPolizaByRefSupSbp(String referencia,Character tipo,Long lineaseguroid) throws DAOException;

	
}
