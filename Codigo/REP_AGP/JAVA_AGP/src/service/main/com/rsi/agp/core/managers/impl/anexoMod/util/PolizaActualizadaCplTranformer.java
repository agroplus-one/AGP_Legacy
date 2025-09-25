package com.rsi.agp.core.managers.impl.anexoMod.util;

import java.util.ArrayList;
import java.util.List;

import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.anexo.AnexoModificacion;

public class PolizaActualizadaCplTranformer {

	/* Pet. 57626 ** MODIF TAM (18.06.2020) ** Inicio */
	/*
	 * Por el desarrollo de esta petici髇 tanto polizas Agr韈olas como de Ganado van
	 * por formato Unificado se realizan las modificaciones correspondientes para
	 * transformarlo a Formato Unificado.
	 */

	// private static final Log logger =
	// LogFactory.getLog(PolizaActualizadaCplTranformer.class);

	/**
	 * Modifica el objeto Poliza con los datos incluidos en el anexo de modificaci髇
	 * 
	 * @param p
	 * @param am
	 */
	public static void generarPolizaSituacionFinalCompleta(es.agroseguro.contratacion.Poliza p, AnexoModificacion am) {

		// ASEGURADO
		if (!StringUtils.nullToString(am.getNomaseg()).equals("")
				|| !StringUtils.nullToString(am.getRazsocaseg()).equals("")) {
			p.setAsegurado(getAsegurado(am));
		}

		// OBJETOS ASEGURADOS
		if (am.getParcelas() != null && am.getParcelas().size() > 0) {
			p.setObjetosAsegurados(getObjetosAsegurados(am));
		}

		// COBERTURAS
		if (am.getCoberturas() != null && am.getCoberturas().size() > 0) {
			p.setCobertura(getCoberturas(am));
		}

	}

	public static void generarPolizaSituacionFinalCompletaAnt(
			es.agroseguro.seguroAgrario.contratacion.complementario.Poliza p, AnexoModificacion am) {

		// ASEGURADO
		if (!StringUtils.nullToString(am.getNomaseg()).equals("")
				|| !StringUtils.nullToString(am.getRazsocaseg()).equals("")) {
			p.setAsegurado(getAseguradoAnt(am));
		}

		// OBJETOS ASEGURADOS
		if (am.getParcelas() != null && am.getParcelas().size() > 0) {
			p.setObjetosAsegurados(getObjetosAseguradosAnt(am));
		}

		// COBERTURAS
		if (am.getCoberturas() != null && am.getCoberturas().size() > 0) {
			p.setCobertura(getCoberturasAnt(am));
		}

	}

	/**
	 * Devuelve las coberturas del anexo
	 * 
	 * @param am
	 * @return
	 */
	private static es.agroseguro.contratacion.Cobertura getCoberturas(AnexoModificacion am) {

		es.agroseguro.contratacion.Cobertura cobertura = es.agroseguro.contratacion.Cobertura.Factory.newInstance();

		if (!StringUtils.nullToString(am.getCodmodulo().trim()).equals(""))
			cobertura.setModulo(String.format("%-5s", am.getCodmodulo().trim()));
		else
			cobertura.setModulo(String.format("%-5s", am.getPoliza().getCodmodulo().trim()));

		return cobertura;
	}

	private static es.agroseguro.seguroAgrario.contratacion.complementario.Cobertura getCoberturasAnt(
			AnexoModificacion am) {

		es.agroseguro.seguroAgrario.contratacion.complementario.Cobertura cobertura = es.agroseguro.seguroAgrario.contratacion.complementario.Cobertura.Factory
				.newInstance();

		if (!StringUtils.nullToString(am.getCodmodulo().trim()).equals(""))
			cobertura.setModulo(String.format("%-5s", am.getCodmodulo().trim()));
		else
			cobertura.setModulo(String.format("%-5s", am.getPoliza().getCodmodulo().trim()));

		return cobertura;
	}

	/**
	 * Devuelve el objeto Asegurado de la p贸liza a partir del anexo
	 * 
	 * @param am
	 * @return
	 */
	private static es.agroseguro.contratacion.Asegurado getAsegurado(AnexoModificacion am) {

		es.agroseguro.contratacion.Asegurado a = es.agroseguro.contratacion.Asegurado.Factory.newInstance();
		a.setNif(am.getPoliza().getAsegurado().getNifcif());

		return a;
	}

	private static es.agroseguro.seguroAgrario.contratacion.complementario.Asegurado getAseguradoAnt(
			AnexoModificacion am) {

		es.agroseguro.seguroAgrario.contratacion.complementario.Asegurado a = es.agroseguro.seguroAgrario.contratacion.complementario.Asegurado.Factory
				.newInstance();
		a.setNif(am.getPoliza().getAsegurado().getNifcif());

		return a;
	}

	/**
	 * Devuelve los objetos asegurados de la p贸liza a partir del anexo
	 * 
	 * @param am
	 * @return
	 */
	private static es.agroseguro.contratacion.ObjetosAsegurados getObjetosAsegurados(AnexoModificacion am) {

		// IPolizaDao polizaDao
		// Objetos asegurados de la situaci髇 actualizada de la p髄iza
		es.agroseguro.contratacion.ObjetosAsegurados objAseg = es.agroseguro.contratacion.ObjetosAsegurados.Factory
				.newInstance();

		// Objetos asegurados
		List<es.agroseguro.contratacion.parcela.ParcelaDocument> listaParcelas = new ArrayList<es.agroseguro.contratacion.parcela.ParcelaDocument>(
				am.getParcelas().size());

		for (com.rsi.agp.dao.tables.anexo.Parcela parcelaAnexo : am.getParcelas()) {
			// Si la parcela no se ha dado de baja se copia
			if (!Constants.BAJA.equals(parcelaAnexo.getTipomodificacion())) {
				boolean enviarAmCpl = false;
				for (com.rsi.agp.dao.tables.anexo.CapitalAsegurado ca : parcelaAnexo.getCapitalAsegurados()) {
					// Ha que enviarlo si est谩 dado de alta anexo de complementario o si ten铆a
					// incremento en la complementaria original
					if (ca.getIncrementoproduccion() != null || ca.getIncrementoproduccionanterior() != null) {
						enviarAmCpl = true;
					}
				}

				if (enviarAmCpl) {
					listaParcelas.add(parcelaAnexoToParcelaAgr(parcelaAnexo));
				}

			}
		}

		org.w3c.dom.Node importedNode;

		for (es.agroseguro.contratacion.parcela.ParcelaDocument parcelaDoc : listaParcelas) {
			importedNode = objAseg.getDomNode().getOwnerDocument().importNode(parcelaDoc.getParcela().getDomNode(),
					true);
			objAseg.getDomNode().appendChild(importedNode);
		}

		return objAseg;

	}

	
	
	private static es.agroseguro.seguroAgrario.contratacion.complementario.ObjetosAsegurados getObjetosAseguradosAnt(
			AnexoModificacion am) {
	// Objetos asegurados de la situaci贸n actualizada de la p贸liza		
		es.agroseguro.seguroAgrario.contratacion.complementario.ObjetosAsegurados newOa = es.agroseguro.seguroAgrario.contratacion.complementario.ObjetosAsegurados.Factory.newInstance();
			
			// Objetos asegurados
			List<es.agroseguro.seguroAgrario.contratacion.complementario.Parcela> listaParcelas = new ArrayList<es.agroseguro.seguroAgrario.contratacion.complementario.Parcela>();
			for (com.rsi.agp.dao.tables.anexo.Parcela parcelaAnexo : am.getParcelas()) {

				// Si la parcela no se ha dado de baja
				if (!Constants.BAJA.equals(parcelaAnexo.getTipomodificacion())) {
					boolean enviarAmCpl = false;
					for (com.rsi.agp.dao.tables.anexo.CapitalAsegurado ca : parcelaAnexo.getCapitalAsegurados()) {
						// Ha que enviarlo si est谩 dado de alta anexo de complementario o si ten铆a incremento en la complementaria original
						if (ca.getIncrementoproduccion() != null || ca.getIncrementoproduccionanterior() != null) {
							enviarAmCpl = true;
						}
					}
					
					if (enviarAmCpl) {
						listaParcelas.add(parcelaAnexoToParcelaAgrAnt(parcelaAnexo));
					}
				}
			}
			
			// Establece la lista de parcelas en 'ObjetosAsegurados'
			newOa.setParcelaArray(listaParcelas.toArray(new es.agroseguro.seguroAgrario.contratacion.complementario.Parcela[listaParcelas.size()]));
			
			return newOa;
	}
	
	/**** TAM (Fin) ****/

	/**
	 * Transforma una parcela de anexo en una parcela de contratacion
	 * 
	 * @param parcelaAnexo
	 * @return
	 */
	private static es.agroseguro.contratacion.parcela.ParcelaDocument parcelaAnexoToParcelaAgr(
			com.rsi.agp.dao.tables.anexo.Parcela parcelaAnexo) {

		es.agroseguro.contratacion.parcela.ParcelaDocument newParcela = es.agroseguro.contratacion.parcela.ParcelaDocument.Factory
				.newInstance();

		es.agroseguro.contratacion.parcela.ParcelaDocument.Parcela p = es.agroseguro.contratacion.parcela.ParcelaDocument.Parcela.Factory
				.newInstance();

		p.setHoja(parcelaAnexo.getHoja().intValue());
		p.setNumero(parcelaAnexo.getNumero().intValue());
		p.setNombre(parcelaAnexo.getNomparcela());

		// Si la parcela tiene SIGPAC
		if (parcelaAnexo.getCodprovsigpac() != null)
			p.setSIGPAC(PolizaActualizadaTranformer.getSIGPAC(parcelaAnexo));

		// Establece la ubicaci髇
		p.setUbicacion(PolizaActualizadaTranformer.getAmbito(parcelaAnexo));

		// Establece la cosecha de la parcela
		p.setCosecha(getCosecha(parcelaAnexo));
		newParcela.setParcela(p);

		return newParcela;
	}

	private static es.agroseguro.seguroAgrario.contratacion.complementario.Parcela parcelaAnexoToParcelaAgrAnt(
			com.rsi.agp.dao.tables.anexo.Parcela parcelaAnexo) {

		es.agroseguro.seguroAgrario.contratacion.complementario.Parcela newParcela = es.agroseguro.seguroAgrario.contratacion.complementario.Parcela.Factory
				.newInstance();

		newParcela.setHoja(parcelaAnexo.getHoja().intValue());
		newParcela.setNumero(parcelaAnexo.getNumero().intValue());

		// Si la parcela tiene SIGPAC
		if (parcelaAnexo.getCodprovsigpac() != null)
			newParcela.setSIGPAC(PolizaActualizadaTranformer.getSIGPAC(parcelaAnexo));

		// Establece la ubicaci髇
		newParcela.setUbicacion(PolizaActualizadaTranformer.getAmbito(parcelaAnexo));

		// Establece la cosecha de la parcela
		newParcela.setCosecha(getCosechaAnt(parcelaAnexo));

		return newParcela;
	}

	private static es.agroseguro.contratacion.parcela.Cosecha getCosecha(
			com.rsi.agp.dao.tables.anexo.Parcela parcelaAnexo) {
		es.agroseguro.contratacion.parcela.Cosecha cosecha = es.agroseguro.contratacion.parcela.Cosecha.Factory
				.newInstance();
		cosecha.setVariedad(parcelaAnexo.getCodvariedad().intValue());
		cosecha.setCultivo(parcelaAnexo.getCodcultivo().intValue());
		cosecha.setCapitalesAsegurados(getCapitalesAsegurados(parcelaAnexo));
		return cosecha;
	}

	private static es.agroseguro.seguroAgrario.contratacion.complementario.Cosecha getCosechaAnt(
			com.rsi.agp.dao.tables.anexo.Parcela parcelaAnexo) {
		es.agroseguro.seguroAgrario.contratacion.complementario.Cosecha cosecha = es.agroseguro.seguroAgrario.contratacion.complementario.Cosecha.Factory
				.newInstance();
		cosecha.setVariedad(parcelaAnexo.getCodvariedad().intValue());
		cosecha.setCultivo(parcelaAnexo.getCodcultivo().intValue());
		cosecha.setCapitalesAsegurados(getCapitalesAseguradosAnt(parcelaAnexo));
		return cosecha;
	}

	/**
	 * Devuelve los capitales asegurados de la parcela del anexo
	 * 
	 * @param parcelaAnexo
	 * @return
	 */
	private static es.agroseguro.contratacion.parcela.CapitalesAsegurados getCapitalesAsegurados(
			com.rsi.agp.dao.tables.anexo.Parcela parcelaAnexo) {

		// Listado de capitales asegurados que se establecer谩 en la p贸liza
		List<es.agroseguro.contratacion.parcela.CapitalAsegurado> listaCA = new ArrayList<es.agroseguro.contratacion.parcela.CapitalAsegurado>();
		es.agroseguro.contratacion.parcela.CapitalesAsegurados capitalesAsegurados = es.agroseguro.contratacion.parcela.CapitalesAsegurados.Factory
				.newInstance();

		// Recorre los capitales asegurados de la parcela del anexo
		for (com.rsi.agp.dao.tables.anexo.CapitalAsegurado capitalAsegurado : parcelaAnexo.getCapitalAsegurados()) {
			// Lo transforma a CA de la p髄iza
			listaCA.add(caAnexoToCaPoliza(capitalAsegurado));
		}

		capitalesAsegurados.setCapitalAseguradoArray((es.agroseguro.contratacion.parcela.CapitalAsegurado[]) listaCA
				.toArray(new es.agroseguro.contratacion.parcela.CapitalAsegurado[listaCA.size()]));

		return capitalesAsegurados;

	}

	private static es.agroseguro.seguroAgrario.contratacion.complementario.CapitalesAsegurados getCapitalesAseguradosAnt(
			com.rsi.agp.dao.tables.anexo.Parcela parcelaAnexo) {

		// Listado de capitales asegurados que se establecer谩 en la p贸liza
		List<es.agroseguro.seguroAgrario.contratacion.complementario.CapitalAsegurado> listaCA = new ArrayList<es.agroseguro.seguroAgrario.contratacion.complementario.CapitalAsegurado>();
		es.agroseguro.seguroAgrario.contratacion.complementario.CapitalesAsegurados capitalesAsegurados = es.agroseguro.seguroAgrario.contratacion.complementario.CapitalesAsegurados.Factory
				.newInstance();

		// Recorre los capitales asegurados de la parcela del anexo
		for (com.rsi.agp.dao.tables.anexo.CapitalAsegurado capitalAsegurado : parcelaAnexo.getCapitalAsegurados()) {
			// Lo transforma a CA de la p髄iza
			listaCA.add(caAnexoToCaPolizaAnt(capitalAsegurado));
		}

		capitalesAsegurados.setCapitalAseguradoArray(
				(es.agroseguro.seguroAgrario.contratacion.complementario.CapitalAsegurado[]) listaCA.toArray(
						new es.agroseguro.seguroAgrario.contratacion.complementario.CapitalAsegurado[listaCA.size()]));

		return capitalesAsegurados;

	}

	/**
	 * Transforma el Capital Asegurado del anexo en un Capital Asegurado de la
	 * P贸liza
	 * 
	 * @param caAnexo
	 * @return
	 */
	private static es.agroseguro.contratacion.parcela.CapitalAsegurado caAnexoToCaPoliza(
			com.rsi.agp.dao.tables.anexo.CapitalAsegurado caAnexo) {
		es.agroseguro.contratacion.parcela.CapitalAsegurado capitalAsegurado = es.agroseguro.contratacion.parcela.CapitalAsegurado.Factory
				.newInstance();

		// La producci贸n del ca ser谩 la primera que no sea nula en el siguiente orden
		// 1. Incremento de la producci贸n realizado en el anexo
		// 2. Incremento de la producci贸n realizado en la complementaria
		// 3. Producci贸n de la principal
		capitalAsegurado
				.setProduccion(caAnexo.getIncrementoproduccion() != null ? caAnexo.getIncrementoproduccion().intValue()
						: ((caAnexo.getIncrementoproduccionanterior() != null)
								? caAnexo.getIncrementoproduccionanterior().intValue()
								: caAnexo.getProduccion().intValue()));
		capitalAsegurado.setSuperficie(caAnexo.getSuperficie());
		capitalAsegurado.setTipo(caAnexo.getTipoCapital().getCodtipocapital().intValue());
		return capitalAsegurado;
	}

	private static es.agroseguro.seguroAgrario.contratacion.complementario.CapitalAsegurado caAnexoToCaPolizaAnt(
			com.rsi.agp.dao.tables.anexo.CapitalAsegurado caAnexo) {
		es.agroseguro.seguroAgrario.contratacion.complementario.CapitalAsegurado capitalAsegurado = es.agroseguro.seguroAgrario.contratacion.complementario.CapitalAsegurado.Factory
				.newInstance();

		// La producci贸n del ca ser谩 la primera que no sea nula en el siguiente orden
		// 1. Incremento de la producci贸n realizado en el anexo
		// 2. Incremento de la producci贸n realizado en la complementaria
		// 3. Producci贸n de la principal
		capitalAsegurado
				.setProduccion(caAnexo.getIncrementoproduccion() != null ? caAnexo.getIncrementoproduccion().intValue()
						: ((caAnexo.getIncrementoproduccionanterior() != null)
								? caAnexo.getIncrementoproduccionanterior().intValue()
								: caAnexo.getProduccion().intValue()));
		capitalAsegurado.setSuperficie(caAnexo.getSuperficie());
		capitalAsegurado.setTipo(caAnexo.getTipoCapital().getCodtipocapital().intValue());
		return capitalAsegurado;
	}

}
