package com.rsi.agp.core.webapp.util;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rga.documentacion.srvmaestro.beans.FirmaXmpBaseBean;
import com.rga.documentacion.srvmaestro.beans.xml.HojaCodigoOficinaAlta;
import com.rga.documentacion.srvmaestro.beans.xml.HojaCodigoRelacOrden;
import com.rga.documentacion.srvmaestro.beans.xml.HojaCodigoRelacPersona;
import com.rga.documentacion.srvmaestro.beans.xml.HojaCodigoTipoPersona;
import com.rga.documentacion.srvmaestro.beans.xml.HojaCodigoTp;
import com.rga.documentacion.srvmaestro.beans.xml.HojaFirma;
import com.rga.documentacion.srvmaestro.beans.xml.HojaFirmaAlto;
import com.rga.documentacion.srvmaestro.beans.xml.HojaFirmaAncho;
import com.rga.documentacion.srvmaestro.beans.xml.HojaIdInterno;
import com.rga.documentacion.srvmaestro.beans.xml.HojaNif;
import com.rga.documentacion.srvmaestro.beans.xml.HojaNombre;
import com.rga.documentacion.srvmaestro.beans.xml.HojaNombreDocumento;
import com.rga.documentacion.srvmaestro.beans.xml.HojaPagina;
import com.rga.documentacion.srvmaestro.beans.xml.HojaPosicionX;
import com.rga.documentacion.srvmaestro.beans.xml.HojaPosicionY;
import com.rga.documentacion.srvmaestro.beans.xml.HojaPosicionYV2;
import com.rga.documentacion.srvmaestro.beans.xml.HojaTipoEmision;
import com.rga.documentacion.srvmaestro.beans.xml.NodoDescripcion;
import com.rga.documentacion.srvmaestro.beans.xml.NodoDocumento;
import com.rga.documentacion.srvmaestro.beans.xml.NodoElement;
import com.rga.documentacion.srvmaestro.beans.xml.NodoFirma;
import com.rga.documentacion.srvmaestro.beans.xml.NodoRdfLi;
import com.rga.documentacion.srvmaestro.beans.xml.NodoSeq;
import com.rga.documentacion.srvmaestro.beans.xml.Root;
import com.rsi.agp.core.exception.BusinessException;

public final class FirmaTabletaXmlHelper {

	private static final Log LOGGER = LogFactory.getLog(FirmaTabletaXmlHelper.class);

	public static String getXmlFirma(final List<FirmaXmpBaseBean> listaFirmas, final String nomDoc)
			throws BusinessException {

		try {

			Root xmlBean = new Root();
			NodoElement nodoelem = new NodoElement();
			xmlBean.setNodoElemento(nodoelem);
			NodoSeq nodoSeqGlobal = new NodoSeq();
			nodoelem.setNodeSeq(nodoSeqGlobal);
			List<NodoRdfLi> listaTopRdfLi = new ArrayList<>();
			nodoSeqGlobal.setRdfLi(listaTopRdfLi);
			for (int i = 0; i < listaFirmas.size(); i++) {

				FirmaXmpBaseBean firmaBean = listaFirmas.get(i);

				// Datos Tecnicos
				NodoRdfLi liDatosTecnicos = new NodoRdfLi();
				listaTopRdfLi.add(liDatosTecnicos);
				NodoDescripcion nodoDescIntenMed = new NodoDescripcion();
				liDatosTecnicos.setDescripcion(nodoDescIntenMed);
				NodoFirma nodoFirma = new NodoFirma();
				nodoDescIntenMed.setNodoFirma(nodoFirma);
				NodoSeq seqFirma = new NodoSeq();
				nodoFirma.setNodeSeq(seqFirma);
				List<NodoRdfLi> listaRdfLi = new ArrayList<>();
				seqFirma.setRdfLi(listaRdfLi);

				// Firma
				NodoRdfLi subNodoRdfli = new NodoRdfLi();
				listaRdfLi.add(subNodoRdfli);
				NodoDescripcion nodoDesc = new NodoDescripcion();
				subNodoRdfli.setDescripcion(nodoDesc);
				HojaFirma hojaFirma = new HojaFirma();
				nodoDesc.setHojaFirma(hojaFirma);
				hojaFirma.setValue(String.valueOf(i + 1));

				// Nombre
				subNodoRdfli = new NodoRdfLi();
				listaRdfLi.add(subNodoRdfli);
				nodoDesc = new NodoDescripcion();
				subNodoRdfli.setDescripcion(nodoDesc);
				HojaNombre hojaNombre = new HojaNombre();
				nodoDesc.setNombre(hojaNombre);
				hojaNombre.setValue(firmaBean.getNombre());

				// NIF
				subNodoRdfli = new NodoRdfLi();
				listaRdfLi.add(subNodoRdfli);
				nodoDesc = new NodoDescripcion();
				subNodoRdfli.setDescripcion(nodoDesc);
				HojaNif hojanif = new HojaNif();
				nodoDesc.setNif(hojanif);
				hojanif.setValue(firmaBean.getNif());

				// ID Interno
				subNodoRdfli = new NodoRdfLi();
				listaRdfLi.add(subNodoRdfli);
				nodoDesc = new NodoDescripcion();
				subNodoRdfli.setDescripcion(nodoDesc);
				HojaIdInterno hojaIdinterno = new HojaIdInterno();
				nodoDesc.setIdInterno(hojaIdinterno);
				hojaIdinterno.setValue(firmaBean.getIdPersonaInterno());

				// Codigo tipo persona
				subNodoRdfli = new NodoRdfLi();
				listaRdfLi.add(subNodoRdfli);
				nodoDesc = new NodoDescripcion();
				subNodoRdfli.setDescripcion(nodoDesc);
				HojaCodigoTipoPersona hojaCodigoTipoPersona = new HojaCodigoTipoPersona();
				nodoDesc.setCodTipoPersona(hojaCodigoTipoPersona);
				hojaCodigoTipoPersona.setValue(firmaBean.getCodigoPersona());

				// Codigo oficina alta persona
				subNodoRdfli = new NodoRdfLi();
				listaRdfLi.add(subNodoRdfli);
				nodoDesc = new NodoDescripcion();
				subNodoRdfli.setDescripcion(nodoDesc);
				HojaCodigoOficinaAlta hojaCodigoOficinaAlta = new HojaCodigoOficinaAlta();
				nodoDesc.setCodOfcnaAltaPe(hojaCodigoOficinaAlta);
				hojaCodigoOficinaAlta.setValue(firmaBean.getEntidadAltaPersona());

				// cod_rl_pe_ac
				subNodoRdfli = new NodoRdfLi();
				listaRdfLi.add(subNodoRdfli);
				nodoDesc = new NodoDescripcion();
				subNodoRdfli.setDescripcion(nodoDesc);
				HojaCodigoRelacPersona hojaCodRlPersPe = new HojaCodigoRelacPersona();
				nodoDesc.setCodRlPersPe(hojaCodRlPersPe);
				hojaCodRlPersPe.setValue(firmaBean.getCodRlPersPe());

				// num_orden_rl_ac
				subNodoRdfli = new NodoRdfLi();
				listaRdfLi.add(subNodoRdfli);
				nodoDesc = new NodoDescripcion();
				subNodoRdfli.setDescripcion(nodoDesc);
				HojaCodigoRelacOrden hojaNumRlOrden = new HojaCodigoRelacOrden();
				nodoDesc.setNumRlOrden(hojaNumRlOrden);
				hojaNumRlOrden.setValue(firmaBean.getNumRlOrden());

				// cod_tipo_de
				subNodoRdfli = new NodoRdfLi();
				listaRdfLi.add(subNodoRdfli);
				nodoDesc = new NodoDescripcion();
				subNodoRdfli.setDescripcion(nodoDesc);
				HojaCodigoTp hojaCodTpDe = new HojaCodigoTp();
				nodoDesc.setCodTpDe(hojaCodTpDe);
				hojaCodTpDe.setValue(firmaBean.getCodTpDe());

				// Posicion X
				subNodoRdfli = new NodoRdfLi();
				listaRdfLi.add(subNodoRdfli);
				nodoDesc = new NodoDescripcion();
				subNodoRdfli.setDescripcion(nodoDesc);
				HojaPosicionX hojaPosX = new HojaPosicionX();
				nodoDesc.setPosX(hojaPosX);
				hojaPosX.setValue(firmaBean.getPosicionX().toString());

				// Posicion Y
				subNodoRdfli = new NodoRdfLi();
				listaRdfLi.add(subNodoRdfli);
				nodoDesc = new NodoDescripcion();
				subNodoRdfli.setDescripcion(nodoDesc);
				HojaPosicionY hojaPosY = new HojaPosicionY();
				nodoDesc.setPosY(hojaPosY);
				hojaPosY.setValue(firmaBean.getPosicionY().toString());

				// Posicion YV2
				subNodoRdfli = new NodoRdfLi();
				listaRdfLi.add(subNodoRdfli);
				nodoDesc = new NodoDescripcion();
				subNodoRdfli.setDescripcion(nodoDesc);
				HojaPosicionYV2 hojaPosYV2 = new HojaPosicionYV2();
				nodoDesc.setPosYV2(hojaPosYV2);
				hojaPosYV2.setValue(firmaBean.getPosicionYV2().toString());

				// Pagina
				subNodoRdfli = new NodoRdfLi();
				listaRdfLi.add(subNodoRdfli);
				nodoDesc = new NodoDescripcion();
				subNodoRdfli.setDescripcion(nodoDesc);
				HojaPagina hojaPagina = new HojaPagina();
				nodoDesc.setPagina(hojaPagina);
				int paginaFinal = firmaBean.getPagina();
				hojaPagina.setValue("" + paginaFinal);

				// Alto firma
				subNodoRdfli = new NodoRdfLi();
				listaRdfLi.add(subNodoRdfli);
				nodoDesc = new NodoDescripcion();
				subNodoRdfli.setDescripcion(nodoDesc);
				HojaFirmaAlto hojaFirmaAlto = new HojaFirmaAlto();
				nodoDesc.setFirmaAlto(hojaFirmaAlto);
				hojaFirmaAlto.setValue(firmaBean.getAlto().toString());

				// Ancho firma
				subNodoRdfli = new NodoRdfLi();
				listaRdfLi.add(subNodoRdfli);
				nodoDesc = new NodoDescripcion();
				subNodoRdfli.setDescripcion(nodoDesc);
				HojaFirmaAncho hojaFirmaAncho = new HojaFirmaAncho();
				nodoDesc.setFirmaAncho(hojaFirmaAncho);
				hojaFirmaAncho.setValue(firmaBean.getAncho().toString());
			}

			// Datos generales del documento
			NodoRdfLi liDatosGenDocumento = new NodoRdfLi();
			listaTopRdfLi.add(liDatosGenDocumento);
			NodoDescripcion nodoDescripcion = new NodoDescripcion();
			liDatosGenDocumento.setDescripcion(nodoDescripcion);
			NodoDocumento nodoDocumento = new NodoDocumento();
			nodoDescripcion.setNodoDocumento(nodoDocumento);
			NodoSeq nodoSeqDoc = new NodoSeq();
			nodoDocumento.setNodeSeq(nodoSeqDoc);
			List<NodoRdfLi> listaRdfLi = new ArrayList<>();
			nodoSeqDoc.setRdfLi(listaRdfLi);

			// tipo emision
			NodoRdfLi rdfli = new NodoRdfLi();
			listaRdfLi.add(rdfli);
			NodoDescripcion subnodoDescripcion = new NodoDescripcion();
			rdfli.setDescripcion(subnodoDescripcion);
			HojaTipoEmision tipoEmision = new HojaTipoEmision();
			subnodoDescripcion.setTipoEmision(tipoEmision);
			tipoEmision.setValue("POLIZA");

			// nombe documento
			rdfli = new NodoRdfLi();
			listaRdfLi.add(rdfli);
			subnodoDescripcion = new NodoDescripcion();
			rdfli.setDescripcion(subnodoDescripcion);
			HojaNombreDocumento nombreDocumento = new HojaNombreDocumento();
			subnodoDescripcion.setNombreDocumento(nombreDocumento);
			nombreDocumento.setValue(nomDoc);

			JAXBContext jaxbContext = JAXBContext.newInstance(Root.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			jaxbMarshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);

			StringWriter sw = new StringWriter();
			jaxbMarshaller.marshal(xmlBean, sw);

			return sw.toString();

		} catch (Exception e) {

			LOGGER.error("Error al generar el XML para la firma en tableta.", e);
			throw new BusinessException(e);
		}
	}
}
