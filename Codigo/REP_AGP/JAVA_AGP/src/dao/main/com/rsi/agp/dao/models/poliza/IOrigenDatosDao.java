package com.rsi.agp.dao.models.poliza;

import java.math.BigDecimal;
import java.util.List;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.dao.models.GenericDao;
import com.rsi.agp.dao.tables.cgen.NumAniosDesdePoda;
import com.rsi.agp.dao.tables.cgen.PracticaCultural;
import com.rsi.agp.dao.tables.cgen.SistemaConduccion;
import com.rsi.agp.dao.tables.orgDat.VistaIGPFactorAmbito;
import com.rsi.agp.dao.tables.orgDat.VistaPorFactores;
import com.rsi.agp.dao.tables.orgDat.VistaSistemaCultivo312;
import com.rsi.agp.dao.tables.poliza.ComparativaPoliza;

@SuppressWarnings("rawtypes")
public interface IOrigenDatosDao extends GenericDao {
	public List<ComparativaPoliza> getRiesgosCubiertos(String codPoliza, BigDecimal codRiesgo, String valor)
			throws DAOException;

	public String getClaseQuery(String campo);

	public List getFieldFromClase(Long lineaseguroid, Long clase, String query);

	public List<BigDecimal> dameListaValoresConceptoFactor(BigDecimal lineaseguroid, String lstModulos,
			BigDecimal codConcepto);

	public List getCampoClaseDetalle(String campo, BigDecimal clase);

	public List<BigDecimal> dameListaTotalValoresConceptoFactor(BigDecimal lineaseguroid, BigDecimal codConcepto);

	public List<SistemaConduccion> getListSistemaConduccion(BigDecimal lineaseguroid, BigDecimal codConcepto)
			throws DAOException;

	public List<PracticaCultural> getListPracticaCultural314(BigDecimal lineaSeguroId, BigDecimal codConcepto)
			throws DAOException;

	public List<VistaSistemaCultivo312> getVistaSistemaCultivo312(List<BigDecimal> provincias,
			List<BigDecimal> comarcas, List<BigDecimal> terminos, List<BigDecimal> cultivo, List<BigDecimal> variedad,
			List<Character> subterminos, List<BigDecimal> sistemaCultivos, Long lineaSeguroId, String codmodulo,
			BigDecimal codcultivo, BigDecimal codvariedad, BigDecimal codprovincia, BigDecimal codcomarca,
			BigDecimal codtermino, Character subtermino, BigDecimal codtipocapital);

	public List<VistaIGPFactorAmbito> getVistaIGP307FacAmb(BigDecimal lineaSeguroId, String codmodulo,
			BigDecimal codcultivo, BigDecimal codvariedad, BigDecimal codprovincia, BigDecimal codcomarca,
			BigDecimal codtermino, Character subtermino);

	public List<VistaPorFactores> getVistaPorFactores(BigDecimal lineaSeguroId, String codmodulo,
			BigDecimal codConcepto, BigDecimal idClase);

	public List<NumAniosDesdePoda> getListNumAniosPoda() throws DAOException;
}
