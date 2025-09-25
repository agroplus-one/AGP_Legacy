package com.rsi.agp.core.manager.impl.anexoRC;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.apache.xmlbeans.XmlObject;

//cambiar a estructura pkg RC?
import com.rsi.agp.core.report.anexoMod.BeanExplotacion;
import com.rsi.agp.core.report.anexoMod.BeanParcelaCapitalAsegurado;
import com.rsi.agp.core.report.anexoMod.ResumenCosechaAsegurada;
import com.rsi.agp.core.report.anexoMod.ResumenValorAsegurable;
//cambiar a estructura pkg RC?
import com.rsi.agp.dao.tables.admin.Tomador;
import com.rsi.agp.dao.tables.cgen.CaracteristicaExplotacion;
import com.rsi.agp.dao.tables.cgen.SubvencionDeclarada;
import com.rsi.agp.dao.tables.poliza.Linea;

import es.agroseguro.seguroAgrario.estadoContratacion.EstadoContratacionDocument;

public class PolizaActualizadaRCResponse {

	private es.agroseguro.seguroAgrario.contratacion.PolizaDocument polizaPrincipal;
	private es.agroseguro.seguroAgrario.contratacion.complementario.PolizaDocument polizaComplementaria;
	private es.agroseguro.contratacion.PolizaDocument polizaComplementariaUnif;
	private es.agroseguro.contratacion.PolizaDocument polizaPrincipalUnif;
	private es.agroseguro.contratacion.PolizaDocument polizaGanado;
	private EstadoContratacionDocument estadoContratacion;
	private XmlObject polizaPrincipalRC;
	private XmlObject polizaComplementariaRC;

	// Datos del tomador
	private Tomador tomador;

	// Datos de la línea
	private Linea linea;

	// Datos de la característica de la explotación.
	private CaracteristicaExplotacion caracteristicaExplotacion;

	// Listado con información de cada combinación parcela-capital asegurado.
	// Debe ir ordenado por hoja y número para que
	// el informe muestre la información correctamente.
	private List<BeanParcelaCapitalAsegurado> parcelas;

	// Listado con información de cada explotacion.
	private List<BeanExplotacion> explotaciones;

	// Mapa con el resumen del valor asegurable de PARCELAS por hoja (K: hoja,
	// V: listado con los elementos a mostrar en el resumen
	private Map<Integer, List<ResumenValorAsegurable>> mapaValorAsegurableParcelas;

	// Mapa con el resumen del valor asegurable de INSTALACIONES por hoja (K:
	// hoja, V: listado con los elementos a mostrar en el resumen
	private Map<Integer, List<ResumenValorAsegurable>> mapaValorAsegurableInstalaciones;

	// Lista para el resumen de cosechas asegurables
	private List<ResumenCosechaAsegurada> listaCosechasAseguradas;

	// Mapa para las descripciones de los módulos (principal y complementario
	// si lo tiene)
	private Map<String, String> mapaModulos;

	// Mapa para las descipciones de las subvenciones
	private Map<BigDecimal, SubvencionDeclarada> subvenciones;

	public PolizaActualizadaRCResponse() {
	}

	public PolizaActualizadaRCResponse(final es.agroseguro.seguroAgrario.contratacion.PolizaDocument polizaPrincipal,
			final es.agroseguro.seguroAgrario.contratacion.complementario.PolizaDocument polizaComplementaria,
			final EstadoContratacionDocument estadoContratacion,
			final es.agroseguro.contratacion.PolizaDocument polizaGanado) {

		this.polizaPrincipal = polizaPrincipal;
		this.polizaComplementaria = polizaComplementaria;
		this.estadoContratacion = estadoContratacion;
		this.polizaGanado = polizaGanado;
	}
	
	public es.agroseguro.seguroAgrario.contratacion.PolizaDocument getPolizaPrincipal() {
		return polizaPrincipal;
	}

	public void setPolizaPrincipal(final es.agroseguro.seguroAgrario.contratacion.PolizaDocument polizaPrincipal) {
		this.polizaPrincipal = polizaPrincipal;
	}

	public es.agroseguro.seguroAgrario.contratacion.complementario.PolizaDocument getPolizaComplementaria() {
		return polizaComplementaria;
	}

	public void setPolizaComplementaria(
			final es.agroseguro.seguroAgrario.contratacion.complementario.PolizaDocument polizaComplementaria) {
		this.polizaComplementaria = polizaComplementaria;
	}

	/* Pet. 57626 ** MODIF TAM (18.06.2020) ** Inicio */
	public es.agroseguro.contratacion.PolizaDocument getPolizaComplementariaUnif() {
		return polizaComplementariaUnif;
	}

	public void setPolizaComplementariaUnif(final es.agroseguro.contratacion.PolizaDocument polizaComplementariaUnif) {
		this.polizaComplementariaUnif = polizaComplementariaUnif;
	}

	public es.agroseguro.contratacion.PolizaDocument getPolizaPrincipalUnif() {
		return polizaPrincipalUnif;
	}

	public void setPolizaPrincipalUnif(final es.agroseguro.contratacion.PolizaDocument polizaPrincipal) {
		this.polizaPrincipalUnif = polizaPrincipal;
	}
	/* Pet. 57626 ** MODIF TAM (18.06.2020) ** Fin */

	public EstadoContratacionDocument getEstadoContratacion() {
		return estadoContratacion;
	}

	public void setEstadoContratacion(final EstadoContratacionDocument estadoContratacion) {
		this.estadoContratacion = estadoContratacion;
	}

	public List<BeanParcelaCapitalAsegurado> getParcelas() {
		return parcelas;
	}

	public void setParcelas(final List<BeanParcelaCapitalAsegurado> parcelas) {
		this.parcelas = parcelas;
	}

	public Map<Integer, List<ResumenValorAsegurable>> getMapaValorAsegurableParcelas() {
		return mapaValorAsegurableParcelas;
	}

	public void setMapaValorAsegurableParcelas(final Map<Integer, List<ResumenValorAsegurable>> mapaValorAsegurable) {
		this.mapaValorAsegurableParcelas = mapaValorAsegurable;
	}

	public Map<Integer, List<ResumenValorAsegurable>> getMapaValorAsegurableInstalaciones() {
		return mapaValorAsegurableInstalaciones;
	}

	public void setMapaValorAsegurableInstalaciones(
			final Map<Integer, List<ResumenValorAsegurable>> mapaValorAsegurableInstalaciones) {
		this.mapaValorAsegurableInstalaciones = mapaValorAsegurableInstalaciones;
	}

	public List<ResumenCosechaAsegurada> getListaCosechasAseguradas() {
		return listaCosechasAseguradas;
	}

	public void setListaCosechasAseguradas(final List<ResumenCosechaAsegurada> listaCosechasAseguradas) {
		this.listaCosechasAseguradas = listaCosechasAseguradas;
	}

	public Map<String, String> getMapaModulos() {
		return mapaModulos;
	}

	public void setMapaModulos(final Map<String, String> mapaModulos) {
		this.mapaModulos = mapaModulos;
	}

	public Map<BigDecimal, SubvencionDeclarada> getSubvenciones() {
		return subvenciones;
	}

	public void setSubvenciones(final Map<BigDecimal, SubvencionDeclarada> subvenciones) {
		this.subvenciones = subvenciones;
	}

	public Tomador getTomador() {
		return tomador;
	}

	public void setTomador(final Tomador tomador) {
		this.tomador = tomador;
	}

	public Linea getLinea() {
		return linea;
	}

	public void setLinea(final Linea linea) {
		this.linea = linea;
	}

	public CaracteristicaExplotacion getCaracteristicaExplotacion() {
		return caracteristicaExplotacion;
	}

	public void setCaracteristicaExplotacion(final CaracteristicaExplotacion caracteristicaExplotacion) {
		this.caracteristicaExplotacion = caracteristicaExplotacion;
	}

	public es.agroseguro.contratacion.PolizaDocument getPolizaGanado() {
		return polizaGanado;
	}

	public void setPolizaGanado(final es.agroseguro.contratacion.PolizaDocument polizaGanado) {
		this.polizaGanado = polizaGanado;
	}

	public List<BeanExplotacion> getExplotaciones() {
		return this.explotaciones;
	}

	public void setExplotaciones(final List<BeanExplotacion> explotaciones) {
		this.explotaciones = explotaciones;
	}

	public XmlObject getPolizaPrincipalRC() {
		return polizaPrincipalRC;
	}

	public void setPolizaPrincipalRC(XmlObject polizaPrincipalRC) {
		this.polizaPrincipalRC = polizaPrincipalRC;
	}

	public XmlObject getPolizaComplementariaRC() {
		return polizaComplementariaRC;
	}

	public void setPolizaComplementariaRC(XmlObject polizaComplementariaRC) {
		this.polizaComplementariaRC = polizaComplementariaRC;
	}
}
