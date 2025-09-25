package com.rsi.agp.core.managers;

import java.math.BigDecimal;
import java.sql.Clob;
import java.util.List;
import java.util.Map;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.cpl.ConceptoCubiertoModulo;
import com.rsi.agp.dao.tables.cpl.ModuloView;
import com.rsi.agp.dao.tables.poliza.ModuloPoliza;
import com.rsi.agp.dao.tables.poliza.Poliza;

import es.agroseguro.modulosYCoberturas.Cobertura;
import es.agroseguro.modulosYCoberturas.ModulosYCoberturas;

public interface ISeleccionComparativasSWManager {
	public Map<String, Object> generarListaComparativas (long idpoliza, final boolean vieneDeImportes, String realPath, Usuario usuario);
	public Map<String, Object> guardarComparativas (long idpoliza, long lineaseguroid, String[] infoModulos, String[] infoCoberturas);
	
	public ModulosYCoberturas getModulosYCoberturasBBDD (ModuloPoliza mp,Poliza p, String realPath) throws Exception ;
	public ModulosYCoberturas getModulosYCoberturasBBDD (long idPoliza, String codModulo) throws Exception ;
	public List<String> getCabecerasModulo (Cobertura c);
	public ModulosYCoberturas getModulosYCoberturasSW (ModuloPoliza mp, Poliza p, String realPath, Usuario usuario) throws Exception ;
	public ModuloView getModuloViewFromModulosYCoberturas (final ModulosYCoberturas myc, final ModuloPoliza mp, int numComparativa, String codMod, boolean llenaRenovable); 
	public void actualizaEstadoPoliza(Long idpoliza, BigDecimal idestado) throws DAOException;
	public ModuloView getExplotacionesViewFromModulosYCoberturas (final ModulosYCoberturas myc, int numExplotacion);	
	
	/* Pet. 63485 ** MODIF TAM (15.07.2020) ** Inicio */
	public Map<String, Object> generarListaComparativasAgri(long idpoliza, final boolean vieneDeImportes, String realPath, Usuario usuario);
	public Clob getxmlSWModyCobert(Long idpoliza, String codModulo) throws BusinessException;
	public ModulosYCoberturas getModulosYCoberturasAgriSW (ModuloPoliza mp, Poliza p, String realPath, Usuario usuario) throws Exception;
	public ModuloView getModuloViewFromModulosYCobertAgricolas(final ModulosYCoberturas myc, final ModuloPoliza mp, int numComparativa, String codMod);
	public ModulosYCoberturas getMyCPolizaAgricola(ModuloPoliza mp,Poliza p, String realPath) throws Exception;
	
	/* Pet. 63485-Fase II ** MODIF TAM (25.09.2020) ** Inicio */
	public ModuloView getParcelasViewFromModulosYCoberturas (Poliza poliza, final ModulosYCoberturas myc);
	public boolean existInListConcepto(List<ConceptoCubiertoModulo> lstConcCbrtoMod, String codConceptoStr);	
}