package com.rsi.agp.core.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.siniestro.CapAsegSiniestro;
import com.rsi.agp.dao.tables.siniestro.DatVarCapAsegSiniestro;
import com.rsi.agp.dao.tables.siniestro.ParcelaSiniestro;

import es.agroseguro.iTipos.Ambito;
import es.agroseguro.iTipos.DatosContacto;
import es.agroseguro.iTipos.IdentificacionCatastral;
import es.agroseguro.iTipos.NombreApellidos;
import es.agroseguro.iTipos.RazonSocial;
import es.agroseguro.iTipos.SIGPAC;
import es.agroseguro.iTipos.SoN;
import es.agroseguro.seguroAgrario.siniestros.Asegurado;
import es.agroseguro.seguroAgrario.siniestros.CapitalAsegurado;
import es.agroseguro.seguroAgrario.siniestros.CapitalesAsegurados;
import es.agroseguro.seguroAgrario.siniestros.Cosecha;
import es.agroseguro.seguroAgrario.siniestros.Direccion;
import es.agroseguro.seguroAgrario.siniestros.ObjetosSiniestrados;
import es.agroseguro.seguroAgrario.siniestros.Parcela;
import es.agroseguro.seguroAgrario.siniestros.PersonaContacto;
import es.agroseguro.seguroAgrario.siniestros.Poliza;
import es.agroseguro.seguroAgrario.siniestros.Riesgo;
import es.agroseguro.seguroAgrario.siniestros.Siniestro;
import es.agroseguro.seguroAgrario.siniestros.datosVariablesDS.DatosVariablesDS;
import es.agroseguro.seguroAgrario.siniestros.datosVariablesDS.FechaRecoleccion;
import es.agroseguro.seguroAgrario.siniestros.datosVariablesDS.FrutosCaidos;


/**
 * Clase para transformar un siniestro de base de datos en una siniestro para
 * enviar a Agroseguro
 * 
 * @author U029769
 * 
 */
public class SiniestroTransformer {

	private static Log logger = LogFactory.getLog(SiniestroTransformer.class);

	public static Siniestro transformar(
			com.rsi.agp.dao.tables.siniestro.Siniestro siniestro, com.rsi.agp.dao.tables.admin.Asegurado asegurado) {

		es.agroseguro.seguroAgrario.siniestros.Siniestro siniestroAS = Siniestro.Factory.newInstance();

		logger.info("Inicio generacion XML siniestros");
		//@TODO de momento le ponemos un 1 ya que este campo se debe calcular 
		//siniestroAS.setNumeroInterno(siniestro.getNumsiniestro().intValue());
		siniestroAS.setNumeroInterno(1);
		
		// DATOS DE LA PERSONA DE CONTACTO
		if (!StringUtils.nullToString(siniestro.getNombre()).equals("")
				|| !StringUtils.nullToString(siniestro.getRazonsocial()).equals("")) {
			
			PersonaContacto nodoPersonaContacto = nodoPersonaContacto(siniestro);

			// Anadimos la persona de contacto
			siniestroAS.setPersonaContacto(nodoPersonaContacto);
			
			Asegurado nodoAsegurado = nodoAsegurado(siniestro, asegurado);
			// Anadimos el asegurado
			siniestroAS.setAsegurado(nodoAsegurado);
		}
		// FIN ASEGURADO

		// anadimos la fechaFirma del siniestro
		siniestroAS.setFechaFirmaSiniestro(DateUtil.date2Calendar(siniestro.getFechaocurrencia()));

		// POLIZA
		Poliza poliza = nodoPoliza(siniestro);
		// anadimos la poliza
		siniestroAS.setPoliza(poliza);
		// FIN POLIZA

		// anadimos las observaciones
		siniestroAS.setObservacion(siniestro.getObservaciones());

		logger.info("rellenando objetos siniestrados en XML siniestros");
		// OBJETOS SINIESTRADOS
		if (!CollectionsAndMapsUtil.isEmpty(siniestro.getParcelasSiniestros())) {
			ObjetosSiniestrados objectosSiniestrados = ObjetosSiniestrados.Factory.newInstance();
			Set<ParcelaSiniestro> parcelasSiniestros = siniestro.getParcelasSiniestros();
			Parcela[] parcelasArr = getParcelasArray(parcelasSiniestros);
			if (parcelasArr != null) {
				objectosSiniestrados.setParcelaArray(parcelasArr);
				siniestroAS.setObjetosSiniestrados(objectosSiniestrados);
			}
		}
		// FIN OBJETOS SINIESTRADOS

		Riesgo riesgo = Riesgo.Factory.newInstance();

		Calendar cal = Calendar.getInstance();
		
		cal.setTime(siniestro.getFechaocurrencia());
		riesgo.setFechaOcurrencia(cal);
		
		if (siniestro.getCodriesgo().toString().length()==1){
			riesgo.setRiesgoSiniestro('0'+siniestro.getCodriesgo().toString());
		}else{
			riesgo.setRiesgoSiniestro(siniestro.getCodriesgo().toString());
		}
		siniestroAS.setRiesgo(riesgo);

		logger.info("FIN generacion XML siniestros");
		return siniestroAS;
	}

	private static Poliza nodoPoliza(
			com.rsi.agp.dao.tables.siniestro.Siniestro siniestro) {
		Poliza poliza = Poliza.Factory.newInstance();

		poliza.setLinea(siniestro.getPoliza().getLinea().getCodlinea().intValue());
		poliza.setPlan(siniestro.getPoliza().getLinea().getCodplan().intValue());
		poliza.setReferenciaColectivo(siniestro.getPoliza().getColectivo().getIdcolectivo());
		poliza.setReferenciaPoliza(siniestro.getPoliza().getReferencia());
		return poliza;
	}

	private static Asegurado nodoAsegurado(
			com.rsi.agp.dao.tables.siniestro.Siniestro siniestro,
			com.rsi.agp.dao.tables.admin.Asegurado asegurado) {
		Asegurado a = Asegurado.Factory.newInstance();
		//Añadimos DATOS DEL ASEGURADO	
		a.setNif(siniestro.getPoliza().getAsegurado().getNifcif());
		if(null!=asegurado.getRazonsocial()){
			RazonSocial rsAseg = RazonSocial.Factory.newInstance();
			rsAseg.setRazonSocial(asegurado.getRazonsocial());
			a.setRazonSocial(rsAseg);		
		}else{
			NombreApellidos nomAse = NombreApellidos.Factory.newInstance();
			if(null!=asegurado.getNombre()){
				nomAse.setNombre(asegurado.getNombre());
			}
			if(null!=asegurado.getApellido1()){
				nomAse.setApellido1(asegurado.getApellido1());
			}
			if (!"".equals(StringUtils.nullToString(asegurado.getApellido2()).trim())){
				nomAse.setApellido2(asegurado.getApellido2());
			}
			
			a.setNombreApellidos(nomAse);
		}
		if(null!=asegurado.getTelefono() || null!=asegurado.getMovil()){
			if(null!=asegurado.getTelefono()){
				a.setTelefono(Integer.parseInt(asegurado.getTelefono()));
			} else {
				a.setTelefono(Integer.parseInt(asegurado.getMovil()));
			}
		}
		
		Direccion dirAseg = Direccion.Factory.newInstance();
		if (null!=asegurado.getCodpostal()){
			dirAseg.setCp(String.format("%05d", asegurado.getCodpostal().intValue()));
		}
		if (null!=asegurado.getBloque()){
			dirAseg.setBloque(asegurado.getBloque());
		}
		if (null!=asegurado.getEscalera()){
			dirAseg.setEscalera(asegurado.getEscalera());
		}
		if (null!=asegurado.getNumvia()){
			dirAseg.setNumero(asegurado.getNumvia());
		}
		if (null!=asegurado.getPiso()){
			dirAseg.setPiso(asegurado.getPiso());
		}
		if(null!=asegurado.getLocalidad()){
			if(null!=asegurado.getLocalidad().getNomlocalidad()){
				dirAseg.setLocalidad(asegurado.getLocalidad().getNomlocalidad());
			}
			if(null!=asegurado.getLocalidad().getProvincia() && 
					null!=asegurado.getLocalidad().getProvincia().getCodprovincia()){
				dirAseg.setProvincia(asegurado.getLocalidad().getProvincia().getCodprovincia().intValue());
				//asegurado.getLocalidad().getId().getCodprovincia()
			}
			if(null!=asegurado.getLocalidad().getId() && 
					null!= asegurado.getLocalidad().getId().getCodlocalidad()){
				dirAseg.setTermino(asegurado.getLocalidad().getId().getCodlocalidad().intValue());
			}
		}
		
		
		if (null!=asegurado.getVia() && null!=asegurado.getVia().getClave() && null!= asegurado.getDireccion()){
			dirAseg.setVia(asegurado.getVia().getClave() + " " + asegurado.getDireccion().toUpperCase());
		}		
		
		a.setDireccion(dirAseg);
		
		DatosContacto datosCont = DatosContacto.Factory.newInstance();
		
		if (!StringUtils.isNullOrEmpty(siniestro.getTelefonoFijoAsegurado())) {
			//a.getDatosContacto().setTelefonoFijo(Integer.parseInt(siniestro.getTelefonoFijoAsegurado()));
			datosCont.setTelefonoFijo(Integer.parseInt(siniestro.getTelefonoFijoAsegurado()));
		}
		if (!StringUtils.isNullOrEmpty(siniestro.getTelefonoMovilAsegurado())) {
			//a.getDatosContacto().setTelefonoMovil(Integer.parseInt(siniestro.getTelefonoMovilAsegurado()));
			datosCont.setTelefonoMovil(Integer.parseInt(siniestro.getTelefonoMovilAsegurado()));
		}
		
		if (!StringUtils.isNullOrEmpty(siniestro.getEmailAsegurado())) {
			//a.getDatosContacto().setEmail(siniestro.getEmailAsegurado());
			datosCont.setEmail(siniestro.getEmailAsegurado());
		}
		a.setDatosContacto(datosCont);
		
		return a;
	}

	private static PersonaContacto nodoPersonaContacto(
			com.rsi.agp.dao.tables.siniestro.Siniestro siniestro) {
		PersonaContacto p = PersonaContacto.Factory.newInstance();
		if (siniestro.getRazonsocial() != null) {
			RazonSocial rs = RazonSocial.Factory.newInstance();
			rs.setRazonSocial(siniestro.getRazonsocial());
			p.setRazonSocial(rs);				
		} else {
			NombreApellidos nom = NombreApellidos.Factory.newInstance();
			nom.setNombre(siniestro.getNombre());
			nom.setApellido1(siniestro.getApellido1());
			nom.setApellido2(siniestro.getApellido2());
			p.setNombreApellidos(nom);				
		}
		
		if (!StringUtils.nullToString(siniestro.getTelefono1()).equals("")){
			p.setTelefono1(Integer.parseInt(siniestro.getTelefono1()));
		}
		if (!StringUtils.nullToString(siniestro.getTelefono2()).equals("")){
			p.setTelefono2(Integer.parseInt(siniestro.getTelefono2()));
		}
		if (!StringUtils.nullToString(siniestro.getTelefono3()).equals("")){
			p.setTelefono3(Integer.parseInt(siniestro.getTelefono3()));
		}

		Direccion dirContacto = Direccion.Factory.newInstance();
		dirContacto.setCp(String.format("%05d", siniestro.getCodpostal().intValue()));
		dirContacto.setBloque(siniestro.getBloque());
		dirContacto.setEscalera(siniestro.getEscalera());
		dirContacto.setLocalidad(siniestro.getPoliza().getAsegurado().getLocalidad().getNomlocalidad());
		dirContacto.setNumero(siniestro.getNumvia());
		dirContacto.setPiso(siniestro.getPiso());
		dirContacto.setProvincia(siniestro.getCodprovincia().intValue());
		dirContacto.setVia(siniestro.getClavevia() + " " + siniestro.getDireccion().toUpperCase());
		dirContacto.setTermino(siniestro.getCodlocalidad().intValue());
		
		p.setDireccion(dirContacto);
		return p;
	}

	private static Parcela[] getParcelasArray(Set<ParcelaSiniestro> parcelasSiniestros) {
		List<Parcela> lstParcelas = new ArrayList<Parcela>();
		for (ParcelaSiniestro parcela : parcelasSiniestros) {
			if(parcelaConSiniestro(parcela)){
				
				Parcela p = Parcela.Factory.newInstance();
				p.setHoja(parcela.getHoja().intValue());
				p.setNumero(parcela.getNumero().intValue());
				p.setNombre(parcela.getNomparcela());
				
				/* P00077429 ** MODIF TAM (28/01/2021) ** Inicio */
				/* Incluir el nuevo atributo 'parcelaAgricola' en la etiqueta 'Parcela' */
				/* en los esquemas es.agroseguro.seguroAgrario.*.Parcela no se encuentra la parcelaagricola*/
				/*p.setParcelaAgricola(StringUtils.nullToString(parcela.getParcAgricola()));*/
				/* P00077429 ** MODIF TAM (28/01/2021) ** Fin */

				if (parcela.getCodprovsigpac() != null) {
					// rellenamos el sigpac
					SIGPAC sigpac = SIGPAC.Factory.newInstance();
					sigpac.setAgregado(parcela.getAgrsigpac().intValue());
					sigpac.setParcela(parcela.getParcelasigpac().intValue());
					sigpac.setPoligono(parcela.getPoligonosigpac().intValue());
					sigpac.setProvincia(parcela.getCodprovsigpac().intValue());
					sigpac.setRecinto(parcela.getRecintosigpac().intValue());
					sigpac.setTermino(parcela.getCodtermsigpac().intValue());
					sigpac.setZona(parcela.getZonasigpac().intValue());
					p.setSIGPAC(sigpac);
				} else if (!StringUtils.nullToString(parcela.getParcela_1()).equals("")) {
					// rellenamos la identificacion catastral
					IdentificacionCatastral idCat = IdentificacionCatastral.Factory.newInstance();
					idCat.setParcela(parcela.getParcela_1());
					idCat.setPoligono(parcela.getPoligono());
					p.setIdentificacionCatastral(idCat);
				}

				Ambito ambito = Ambito.Factory.newInstance();
				ambito.setComarca(parcela.getCodcomarca().intValue());
				ambito.setProvincia(parcela.getCodprovincia().intValue());
				ambito.setSubtermino(parcela.getSubtermino() + "");
				ambito.setTermino(parcela.getCodtermino().intValue());
				// rellenamos ubicacion
				/* P00077429 ** MODIF TAM (28/01/2021) ** Inicio */
				/* Incluir solo la etiqueta "Ubicacion"  de la parcela cuando se tengan informados los correspondientes datos de Provincia/comarca/Termino/Subtermino */
				if (ambito.getProvincia() != 0 && ambito.getComarca() != 0 && ambito.getTermino() != 0 && ambito.getSubtermino() != null) {		
					p.setUbicacion(ambito);
				}
				/* P00077429 ** MODIF TAM (28/01/2021) ** Fin */
				

				// Cosecha
				Cosecha cosecha = Cosecha.Factory.newInstance();
				cosecha.setCultivo(parcela.getCodcultivo().intValue());
				cosecha.setVariedad(parcela.getCodvariedad().intValue());

				CapitalesAsegurados capAseg = CapitalesAsegurados.Factory.newInstance();
				List<CapitalAsegurado> capitalesLista = new ArrayList<CapitalAsegurado>();

				BigDecimal superficieTotal = new BigDecimal(0);
				for (CapAsegSiniestro ca : parcela.getCapAsegSiniestros()) {
					if (ca.getAltaensiniestro().equals(Constants.CHARACTER_S)) {
						CapitalAsegurado c = CapitalAsegurado.Factory.newInstance();
						c.setSuperficie(ca.getSuperficie());
						superficieTotal = superficieTotal.add(ca.getSuperficie());
						c.setTipo(ca.getCodtipocapital().intValue());
						DatosVariablesDS dv = DatosVariablesDS.Factory.newInstance();
						for (DatVarCapAsegSiniestro dvs : ca.getDatVarCapAsegSiniestros()) {
							if (dvs.getCodconcepto().equals(BigDecimal.valueOf(ConstantsConceptos.CODCPTO_FECHA_RECOLEC))) {
								FechaRecoleccion fechar = FechaRecoleccion.Factory.newInstance();
								try {
									Date fecha = DateUtil.string2Calendar(dvs.getValor(), "dd/MM/yyyy");
									Calendar cal = Calendar.getInstance();
									cal.setTime(fecha);
									fechar.setValor(cal);
									dv.setFecRecol(fechar);
								} catch (Exception e) {
									logger.error("ERROR al parsear la fecha", e);
								}
							} else if (dvs.getCodconcepto().equals(BigDecimal.valueOf(ConstantsConceptos.CODCPTO_FRUTOS_CAIDOS))) {
								FrutosCaidos frutos = FrutosCaidos.Factory.newInstance();
								if (dvs.getValor().equals("S")) {
									frutos.setValor(SoN.S);
								} else {
									frutos.setValor(SoN.N);
								}
								dv.setFruCaid(frutos);
							}
						}
						c.setDatosVariables(dv);
						capitalesLista.add(c);
					}
				}
				
				CapitalAsegurado[] capitales = capitalesLista.toArray(new CapitalAsegurado[capitalesLista.size()]);
				
				// guardamos la superficie total de la parcela
				p.setSuperficie(superficieTotal);
				capAseg.setCapitalAseguradoArray(capitales);
				cosecha.setCapitalesAsegurados(capAseg);
				// rellenamos la cosecha
				p.setCosecha(cosecha);
				lstParcelas.add(p);
			}
		}
		
		Parcela[] parc = new Parcela[lstParcelas.size()];
		if (lstParcelas.size() > 0){
			int cntParcelas = 0;
			for (Parcela par : lstParcelas){
				parc[cntParcelas] = par;
				cntParcelas++;
			}
		} else {
			parc = null;
		}
		
		return parc;
	}

	private static boolean parcelaConSiniestro(ParcelaSiniestro parcela) {
		return  parcela.getAltaensiniestro() != null && parcela.getAltaensiniestro().equals(Constants.CHARACTER_S);
	}
}
