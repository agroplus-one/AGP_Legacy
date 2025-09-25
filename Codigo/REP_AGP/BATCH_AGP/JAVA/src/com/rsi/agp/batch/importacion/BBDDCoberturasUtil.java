package com.rsi.agp.batch.importacion;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.util.ConstantsConceptos;
import com.rsi.agp.dao.tables.cgen.CalculoIndemnizacion;
import com.rsi.agp.dao.tables.cgen.CapitalAseguradoElegible;
import com.rsi.agp.dao.tables.cgen.Garantizado;
import com.rsi.agp.dao.tables.cgen.MinimoIndemnizableElegible;
import com.rsi.agp.dao.tables.cgen.PctFranquiciaElegible;
import com.rsi.agp.dao.tables.cgen.TipoFranquicia;
import com.rsi.agp.dao.tables.cpl.RiesgoCubiertoModulo;
import com.rsi.agp.dao.tables.poliza.ComparativaPoliza;
import com.rsi.agp.dao.tables.poliza.ComparativaPolizaId;
import com.rsi.agp.dao.tables.poliza.ModuloPoliza;
import com.rsi.agp.dao.tables.poliza.ModuloPolizaId;
import com.rsi.agp.dao.tables.poliza.Poliza;

public final class BBDDCoberturasUtil {

	
	static Map<String, BigDecimal> colFilaModulo = new HashMap<String, BigDecimal>();
	static Map<String, String> colDescripciones = new HashMap<String, String>();
	
	private BBDDCoberturasUtil() {
	}
	
	private static String getDescValorCodGarantizado(final Session session, int valor){
		String clave="CG" + valor; //CG de codigo garantizaedo
		String res=null;
		if(colDescripciones.containsKey(clave)){
			res=colDescripciones.get(clave);
		}else{
			res= ((Garantizado) session.createCriteria(Garantizado.class)
					.add(Restrictions.eq("codgarantizado", BigDecimal.valueOf(valor))).uniqueResult()).getDesgarantizado();
			colDescripciones.put(clave, res);
		}
		return res;
	}
	
	private static String getDescValorCalculoIndemnizacion(final Session session, int valor){
		String clave="CI" + valor; //CG de codigo de calculo de indeminizacion
		String res=null;
		if(colDescripciones.containsKey(clave)){
			res=colDescripciones.get(clave);
		}else{
			
			res=((CalculoIndemnizacion) session.createCriteria(CalculoIndemnizacion.class)
					.add(Restrictions.eq("codcalculo", BigDecimal.valueOf(valor))).uniqueResult()).getDescalculo();
			colDescripciones.put(clave, res);			
		}
		return res;
	}
	
	private static String getDescValorPctFranquiciaElegible(final Session session, int valor){
		String clave="PFE" + valor; //CG de codigo de Porcentaje de Franquicia Elegible
		String res=null;
		if(colDescripciones.containsKey(clave)){
			res=colDescripciones.get(clave);
		}else{
			res=((PctFranquiciaElegible) session.createCriteria(PctFranquiciaElegible.class)
					.add(Restrictions.eq("codpctfranquiciaeleg", BigDecimal.valueOf(valor))).uniqueResult()).getDespctfranquiciaeleg();
			
			colDescripciones.put(clave, res);			
		}
		return res;
	}
	
	private static String getDescValorMinimoIndemnizableElegible(final Session session, int valor){
		String clave="MIE" + valor; //CG de codigo de Minimo de Indemnizable Elegible
		String res=null;
		if(colDescripciones.containsKey(clave)){
			res=colDescripciones.get(clave);
		}else{
			res=((MinimoIndemnizableElegible) session.createCriteria(MinimoIndemnizableElegible.class)
					.add(Restrictions.eq("pctminindem",BigDecimal.valueOf(valor))).uniqueResult()).getDesminindem();			
			colDescripciones.put(clave, res);			
		}
		return res;		
	}
	
	private static String getDescValoTipoFranquicia(final Session session, String valor){
		String clave=null;
		if(null!=valor){
			clave="TF" + valor; //CG de codigo de Tipo de Franquicia
		}else{
			clave="TFNulo" ;//CG de codigo de Tipo de Franquicia
		}
		
		String res=null;
		if(colDescripciones.containsKey(clave)){
			res=colDescripciones.get(clave);
		}else{
			res=((TipoFranquicia) session.createCriteria(TipoFranquicia.class)
					.add(Restrictions.eq("codtipofranquicia",valor)).uniqueResult()).getDestipofranquicia();			
			colDescripciones.put(clave, res);			
		}
		return res;		
	}
	
	private static String getDescValoCapitalAseguradoElegible(final Session session, int valor){
		
		String clave="CAE" + valor; //CG de codigo de Minimo de Indemnizable Elegible
		String res=null;
		if(colDescripciones.containsKey(clave)){
			res=colDescripciones.get(clave);
		}else{
			res=((CapitalAseguradoElegible) session.createCriteria(CapitalAseguradoElegible.class)
					.add(Restrictions.eq("pctcapitalaseg",BigDecimal.valueOf(valor))).uniqueResult()).getDescapitalaseg();			
			colDescripciones.put(clave, res);			
		}
		return res;
	}
	
	// Metodo que transforma las coberturas de la situacion actual
	// en el formato esperado por el modelo de datos de Agroplus y puebla el
	// objeto Hibernate encargado de la importacion
	protected static void populateCoberturas(final Poliza polizaHbm,
			final es.agroseguro.contratacion.Cobertura cobertura, final Session session, final boolean isPrincipal)
			throws DAOException {
		Set<ModuloPoliza> modulosPolHbm;

		Set<ComparativaPoliza> comparativasPolHbm;
		ModuloPoliza moduloPolHbm;
		ComparativaPoliza comparativaPolHbm;

		modulosPolHbm = new HashSet<ModuloPoliza>();
		moduloPolHbm = new ModuloPoliza();
		Long secuencia = getSecuenciaComparativa(session);
		moduloPolHbm.setId(new ModuloPolizaId(polizaHbm.getIdpoliza(),
				polizaHbm.getLinea().getLineaseguroid(), polizaHbm
						.getCodmodulo(),secuencia.longValue()));
		moduloPolHbm.setPoliza(polizaHbm);

		session.saveOrUpdate(moduloPolHbm);
		modulosPolHbm.add(moduloPolHbm);
		polizaHbm.setModuloPolizas(modulosPolHbm);

		// Es principal
		if (isPrincipal) {

			es.agroseguro.contratacion.datosVariables.DatosVariables datosVariables = cobertura.getDatosVariables();
			comparativasPolHbm = new HashSet<ComparativaPoliza>();
			// GARANTIZADO 
			if (datosVariables!=null && datosVariables.getGarantArray() != null
					&& datosVariables.getGarantArray().length > 0) {
				for (es.agroseguro.contratacion.datosVariables.Garantizado g : datosVariables.getGarantArray()) {
					String descValor=getDescValorCodGarantizado(session, g.getValor());
					comparativaPolHbm = generarComparativaPoliza(
							polizaHbm,
							BigDecimal.valueOf(g.getCPMod()),
							BigDecimal.valueOf(g.getCodRCub()),
							BigDecimal.valueOf(ConstantsConceptos.CODCPTO_GARANTIZADO),
							getFilaModulo(polizaHbm.getLinea()
									.getLineaseguroid(), polizaHbm
									.getCodmodulo(), BigDecimal.valueOf(g
									.getCPMod()), BigDecimal.valueOf(g
									.getCodRCub()), session),
							BigDecimal.valueOf(g.getValor()),
							descValor,
							BigDecimal.valueOf(1),secuencia);

					session.saveOrUpdate(comparativaPolHbm);
					comparativasPolHbm.add(comparativaPolHbm);
				}
			}

			// CALCULO INDEMNIZACION
			if (datosVariables!=null && datosVariables.getCalcIndemArray() != null
					&& datosVariables.getCalcIndemArray().length > 0) {
				for (es.agroseguro.contratacion.datosVariables.CalculoIndemnizacion c : datosVariables.getCalcIndemArray()) {
					String descValor =getDescValorCalculoIndemnizacion(session,c.getValor());
					comparativaPolHbm = generarComparativaPoliza(
							polizaHbm,
							BigDecimal.valueOf(c.getCPMod()),
							BigDecimal.valueOf(c.getCodRCub()),
							BigDecimal.valueOf(ConstantsConceptos.CODCPTO_CALCULO_INDEMNIZACION),
							getFilaModulo(polizaHbm.getLinea()
									.getLineaseguroid(), polizaHbm
									.getCodmodulo(), BigDecimal.valueOf(c
									.getCPMod()), BigDecimal.valueOf(c
									.getCodRCub()), session),
							BigDecimal.valueOf(c.getValor()),
							descValor,
							BigDecimal.valueOf(1),secuencia);

					session.saveOrUpdate(comparativaPolHbm);
					comparativasPolHbm.add(comparativaPolHbm);
				}
			}

			// % FRANQUICIA
			if (datosVariables!=null && datosVariables.getFranqArray() != null
					&& datosVariables.getFranqArray().length > 0) {
				for (es.agroseguro.contratacion.datosVariables.PorcentajeFranquicia pf : datosVariables.getFranqArray()) {
					String descValor=getDescValorPctFranquiciaElegible(session, pf.getValor() );
					comparativaPolHbm = generarComparativaPoliza(
							polizaHbm,
							BigDecimal.valueOf(pf.getCPMod()),
							BigDecimal.valueOf(pf.getCodRCub()),
							BigDecimal.valueOf(ConstantsConceptos.CODCPTO_PCT_FRANQUICIA),
							getFilaModulo(polizaHbm.getLinea()
									.getLineaseguroid(), polizaHbm
									.getCodmodulo(), BigDecimal.valueOf(pf
									.getCPMod()), BigDecimal.valueOf(pf
									.getCodRCub()), session),
							BigDecimal.valueOf(pf.getValor()),
							descValor,
							BigDecimal.valueOf(1),secuencia);

					session.saveOrUpdate(comparativaPolHbm);
					comparativasPolHbm.add(comparativaPolHbm);
				}
			}

			// MINIMO INDEMNIZABLE
			if (datosVariables!=null && datosVariables.getMinIndemArray() != null
					&& datosVariables.getMinIndemArray().length > 0) {
				for (es.agroseguro.contratacion.datosVariables.PorcentajeMinimoIndemnizable pmi : datosVariables.getMinIndemArray()) {
					String descValor =getDescValorMinimoIndemnizableElegible(session, pmi.getValor() );
					comparativaPolHbm = generarComparativaPoliza(
							polizaHbm,
							BigDecimal.valueOf(pmi.getCPMod()),
							BigDecimal.valueOf(pmi.getCodRCub()),
							BigDecimal.valueOf(ConstantsConceptos.CODCPTO_MINIMO_INDEMNIZABLE),
							getFilaModulo(polizaHbm.getLinea()
									.getLineaseguroid(), polizaHbm
									.getCodmodulo(), BigDecimal.valueOf(pmi
									.getCPMod()), BigDecimal.valueOf(pmi
									.getCodRCub()), session),
							BigDecimal.valueOf(pmi.getValor()),
							descValor,
							BigDecimal.valueOf(1),secuencia);

					session.saveOrUpdate(comparativaPolHbm);
					comparativasPolHbm.add(comparativaPolHbm);
				}
			}

			// TIPO FRANQUICIA
			if (datosVariables!=null && datosVariables.getTipFranqArray() != null
					&& datosVariables.getTipFranqArray().length > 0) {
				for (es.agroseguro.contratacion.datosVariables.TipoFranquicia tf : datosVariables.getTipFranqArray()) {
					String descValor=getDescValoTipoFranquicia(session,tf.getValor());
					comparativaPolHbm = generarComparativaPoliza(
							polizaHbm,
							BigDecimal.valueOf(tf.getCPMod()),
							BigDecimal.valueOf(tf.getCodRCub()),
							BigDecimal.valueOf(ConstantsConceptos.CODCPTO_TIPO_FRANQUICIA),
							getFilaModulo(polizaHbm.getLinea()
									.getLineaseguroid(), polizaHbm
									.getCodmodulo(), BigDecimal.valueOf(tf
									.getCPMod()), BigDecimal.valueOf(tf
									.getCodRCub()), session),
							new BigDecimal(tf.getValor()),
							descValor,
							BigDecimal.valueOf(1),secuencia);

					session.saveOrUpdate(comparativaPolHbm);
					comparativasPolHbm.add(comparativaPolHbm);
				}
			}
			// % CAPITAL ASEGURADO
			if (datosVariables!=null && datosVariables.getCapAsegArray() != null
					&& datosVariables.getCapAsegArray().length > 0) {
				for (es.agroseguro.contratacion.datosVariables.PorcentajeCapitalAsegurado pca : datosVariables.getCapAsegArray()) {
					String descValor=getDescValoCapitalAseguradoElegible(session, pca.getValor());
					comparativaPolHbm = generarComparativaPoliza(
							polizaHbm,
							BigDecimal.valueOf(pca.getCPMod()),
							BigDecimal.valueOf(pca.getCodRCub()),
							BigDecimal.valueOf(ConstantsConceptos.CODCPTO_CAPITAL_ASEGURADO),
							getFilaModulo(polizaHbm.getLinea()
									.getLineaseguroid(), polizaHbm
									.getCodmodulo(), BigDecimal.valueOf(pca
									.getCPMod()), BigDecimal.valueOf(pca
									.getCodRCub()), session),
							BigDecimal.valueOf(pca.getValor()),
							descValor,
							BigDecimal.valueOf(1),secuencia);

					session.saveOrUpdate(comparativaPolHbm);
					comparativasPolHbm.add(comparativaPolHbm);
				}
			}

			if(datosVariables!=null){
				comparativasPolHbm.addAll(getComparativasRiesgCubEleg(polizaHbm,
					datosVariables.getRiesgCbtoElegArray(), session,secuencia));
			}else{
				comparativasPolHbm.addAll(getComparativasRiesgCubEleg(polizaHbm,
						null, session,secuencia));
			}
			polizaHbm.setComparativaPolizas(comparativasPolHbm);
		} else {

			// Las polizas complementarias no traen datos de coberturas. Son los
			// mismos que la poliza principal.
		}
	}

	private static BigDecimal getFilaModulo(final Long lineaseguroid,
			final String codmodulo, final BigDecimal codconceptoppalmod,
			final BigDecimal codriesgocubierto, final Session session) {

//		RiesgoCubiertoModulo rcmodHbm;
//
//		rcmodHbm = (RiesgoCubiertoModulo) session
//				.createCriteria(RiesgoCubiertoModulo.class)
//				.createAlias("conceptoPpalModulo", "conceptoPpalModulo")
//				.createAlias("riesgoCubierto", "riesgoCubierto")
//				.add(Restrictions.eq("id.lineaseguroid", lineaseguroid))
//				.add(Restrictions.eq("id.codmodulo", codmodulo))
//				.add(Restrictions.eq("conceptoPpalModulo.codconceptoppalmod",
//						codconceptoppalmod))
//				.add(Restrictions.eq("riesgoCubierto.id.codriesgocubierto",
//						codriesgocubierto)).uniqueResult();
//
//		return rcmodHbm.getId().getFilamodulo();
		BigDecimal res=null;
		String clave =null;
		if(null!=lineaseguroid && null!=codmodulo && null!=codconceptoppalmod && null!=codriesgocubierto){
			clave="L" + lineaseguroid.toString() + "M" + codmodulo.toString() + 
					"C" + codconceptoppalmod.toString() + "R" + codriesgocubierto.toString();
		}else{
			clave="Nulo";			
		}
		
		if(colFilaModulo.containsKey(clave)){
			res=colFilaModulo.get(clave);
		}else{
			String sql="SELECT ri.FILAMODULO FROM o02agpe0.TB_SC_C_RIESGO_CBRTO_MOD ri " +
					"WHERE ri.LINEASEGUROID= " + lineaseguroid +  
					" and ri.CODMODULO='" + codmodulo +
					"' and ri.CODCONCEPTOPPALMOD= " + codconceptoppalmod + 
					" and ri.CODRIESGOCUBIERTO= " + codriesgocubierto;
			res= (BigDecimal) session.createSQLQuery(sql).uniqueResult();
			colFilaModulo.put(clave, res);
			
		}		
		
		
		return res;
	}

	private static ComparativaPoliza generarComparativaPoliza(
			final Poliza polizaHbm, final BigDecimal cpm,
			final BigDecimal rCub, final BigDecimal codConcepto,
			final BigDecimal filaModulo, final BigDecimal valor,
			final String descValor, final BigDecimal filaComparativa, final Long idComparativa) {

		ComparativaPoliza comparativaPolHbm = new ComparativaPoliza();
		ComparativaPolizaId id = new ComparativaPolizaId();

		id.setFilamodulo(filaModulo);
		id.setCodconcepto(codConcepto);
		id.setCodconceptoppalmod(cpm);
		id.setCodriesgocubierto(rCub);
		id.setCodmodulo(polizaHbm.getCodmodulo());
		id.setCodvalor(valor);
		id.setIdpoliza(polizaHbm.getIdpoliza());
		id.setLineaseguroid(polizaHbm.getLinea().getLineaseguroid());
		id.setFilacomparativa(filaComparativa);
		id.setIdComparativa(idComparativa);
		comparativaPolHbm.setDescvalor(descValor);
		comparativaPolHbm.setId(id);

		comparativaPolHbm.setPoliza(polizaHbm);

		return comparativaPolHbm;
	}

	@SuppressWarnings("unchecked")
	private static List<ComparativaPoliza> getComparativasRiesgCubEleg(
			final Poliza polizaHbm,
			final es.agroseguro.contratacion.datosVariables.RiesgoCubiertoElegido[] riesgCbtoElegArray,
			final Session session, final Long secuencia) {

		List<ComparativaPoliza> comparativasRiesgCubEleg;
		List<RiesgoCubiertoModulo> rCubMobList;
		ComparativaPoliza comparativaPolHbm;
		BigDecimal codConcepto;
		BigDecimal valor;
		String descValor;
		BigDecimal filaComparativa;

		final BigDecimal[] lineasEspeciales = new BigDecimal[] { BigDecimal
				.valueOf(301) };

		comparativasRiesgCubEleg = new ArrayList<ComparativaPoliza>();

		rCubMobList = (List<RiesgoCubiertoModulo>) session
				.createCriteria(RiesgoCubiertoModulo.class)
				.add(Restrictions.eq("id.lineaseguroid", polizaHbm.getLinea()
						.getLineaseguroid()))
				.add(Restrictions.eq("id.codmodulo", polizaHbm.getCodmodulo()))
				.add(Restrictions.eq("elegible", Constants.CHARACTER_S))
				.add(Restrictions.eq("niveleccion", 'C')).list();

		for (RiesgoCubiertoModulo rcmodHbm : rCubMobList) {

			if (riesgCbtoElegArray != null && riesgCbtoElegArray.length > 0) {

				codConcepto = BigDecimal.valueOf(ConstantsConceptos.CODCPTO_RIESGO_CUBIERTO_ELEGIDO);
				valor = new BigDecimal(Constants.RIESGO_ELEGIDO_NO);
				descValor = "N";
				filaComparativa = BigDecimal.valueOf(0);

				for (es.agroseguro.contratacion.datosVariables.RiesgoCubiertoElegido rce : riesgCbtoElegArray) {

					if (rcmodHbm.getRiesgoCubierto().getId()
							.getCodriesgocubierto()
							.equals(BigDecimal.valueOf(rce.getCodRCub()))
							&& rcmodHbm.getConceptoPpalModulo()
									.getCodconceptoppalmod()
									.equals(BigDecimal.valueOf(rce.getCPMod()))) {
						descValor = rce.getValor();
						valor = new BigDecimal(
								"S".equals(rce.getValor()) ? Constants.RIESGO_ELEGIDO_SI
										: Constants.RIESGO_ELEGIDO_NO);
						if (!Arrays.asList(lineasEspeciales).contains(
								polizaHbm.getLinea().getCodlinea())) {
							filaComparativa = "S".equals(rce.getValor()) ? BigDecimal
									.valueOf(1) : BigDecimal.valueOf(2);
						}
						break;
					}
				}
			} else {

				codConcepto = BigDecimal.valueOf(0);
				valor = new BigDecimal(Constants.RIESGO_ELEGIDO_NO);
				descValor = "";
				filaComparativa = Arrays.asList(lineasEspeciales).contains(
						polizaHbm.getLinea().getCodlinea()) ? BigDecimal
						.valueOf(0) : BigDecimal.valueOf(2);
			}

			comparativaPolHbm = generarComparativaPoliza(polizaHbm, rcmodHbm
					.getConceptoPpalModulo().getCodconceptoppalmod(), rcmodHbm
					.getRiesgoCubierto().getId().getCodriesgocubierto(),
					codConcepto, rcmodHbm.getId().getFilamodulo(), valor,
					descValor, filaComparativa,secuencia);

			session.saveOrUpdate(comparativaPolHbm);
			comparativasRiesgCubEleg.add(comparativaPolHbm);
		}

		// Ajuste de fila comparativa para lineas especiales
		if (!comparativasRiesgCubEleg.isEmpty()
				&& Arrays.asList(lineasEspeciales).contains(
						polizaHbm.getLinea().getCodlinea())) {

			// Ordenamos por fila del modulo
			Collections.sort(comparativasRiesgCubEleg,
					new Comparator<ComparativaPoliza>() {
						@Override
						public int compare(final ComparativaPoliza arg0,
								final ComparativaPoliza arg1) {
							return arg0.getId().getFilamodulo()
									.compareTo(arg1.getId().getFilamodulo());
						}
					});

			if (BigDecimal.valueOf(301).equals(
					polizaHbm.getLinea().getCodlinea())) {
				// LS301 tiene fila 7 y fila 12
				// Filacomp 1: ambos elegidos
				// Filacomp 2: elegida fila 7
				// Filacomp 3: elegida fila 12
				// Filacomp 4: ninguno elegido
				final ComparativaPoliza compFila7 = comparativasRiesgCubEleg
						.get(0);
				final ComparativaPoliza compFila12 = comparativasRiesgCubEleg
						.get(1);
				if (compFila7.getId().getCodvalor()
						.equals(new BigDecimal(Constants.RIESGO_ELEGIDO_NO))
						&& compFila12
								.getId()
								.getCodvalor()
								.equals(new BigDecimal(
										Constants.RIESGO_ELEGIDO_NO))) {
					filaComparativa = BigDecimal.valueOf(4);
					compFila7.getId().setCodconcepto(BigDecimal.valueOf(0));
					compFila12.getId().setCodconcepto(BigDecimal.valueOf(0));
				} else if (compFila7.getId().getCodvalor()
						.equals(new BigDecimal(Constants.RIESGO_ELEGIDO_NO))
						&& compFila12
								.getId()
								.getCodvalor()
								.equals(new BigDecimal(
										Constants.RIESGO_ELEGIDO_SI))) {
					filaComparativa = BigDecimal.valueOf(3);
				} else if (compFila7.getId().getCodvalor()
						.equals(new BigDecimal(Constants.RIESGO_ELEGIDO_SI))
						&& compFila12
								.getId()
								.getCodvalor()
								.equals(new BigDecimal(
										Constants.RIESGO_ELEGIDO_NO))) {
					filaComparativa = BigDecimal.valueOf(2);
				} else if (compFila7.getId().getCodvalor()
						.equals(new BigDecimal(Constants.RIESGO_ELEGIDO_SI))
						&& compFila12
								.getId()
								.getCodvalor()
								.equals(new BigDecimal(
										Constants.RIESGO_ELEGIDO_SI))) {
					filaComparativa = BigDecimal.valueOf(1);
				} else {
					filaComparativa = BigDecimal.valueOf(0);
				}
				compFila7.getId().setFilacomparativa(filaComparativa);
				compFila12.getId().setFilacomparativa(filaComparativa);
			}
		}
		return comparativasRiesgCubEleg;
	}
	
	public static Long  getSecuenciaComparativa(final Session session) throws DAOException {
		try {
		String sql = "select o02agpe0.SQ_MODULOS_POLIZA.nextval from dual";
		BigDecimal secuencia = (BigDecimal)session.createSQLQuery(sql).uniqueResult();
		return secuencia.longValue();
		}catch (Exception e) {
			throw new DAOException("Error al crear la secuencia de la comparativa ", e);
		}
	}
	
}