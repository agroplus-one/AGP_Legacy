package com.rsi.agp.core.managers.impl.anexoMod;

import java.util.Map;

import org.apache.xmlbeans.XmlObject;

import com.rsi.agp.dao.tables.anexo.AnexoModSWComparativas;
import com.rsi.agp.dao.tables.anexo.AnexoModificacion;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.cpl.ModuloView;
import com.rsi.agp.dao.tables.poliza.ModuloPoliza;
import com.rsi.agp.dao.tables.poliza.Poliza;

import es.agroseguro.modulosYCoberturas.ModulosYCoberturas;

public interface ISeleccionComparativasAnexoSWManager {

	public Map<String, Object> generarListaComparativas(long idAnexo, String realPath, Usuario usuario,
			boolean cargaBBDD);

	public Map<String, Object> guardarComparativas(long idanexo, String[] infoCoberturas, String tipologia);

	public boolean isAnexoTCRetirada(long idAnexo);

	public Map<String, Object> generarListaComparativasAgri(long idAnexo, String realPath, Usuario usuario,
			boolean cargaBBDD);

	public ModuloView getModuloViewFromModulosYCobertAgricolas(final ModulosYCoberturas myc, final ModuloPoliza mp,
			int numComparativa, String codMod);

	public ModulosYCoberturas getModulosYCoberturasAgriSW(ModuloPoliza mp, Poliza p, String realPath, Usuario usuario,
			String codmodulo, AnexoModificacion anexMod) throws Exception;

	public ModulosYCoberturas getMyCPolizaAgricola(XmlObject plzAct, String realPath, AnexoModSWComparativas amc)
			throws Exception;

	public AnexoModSWComparativas getAnexoModSWComparativas(Usuario usuario, AnexoModificacion am);
}
