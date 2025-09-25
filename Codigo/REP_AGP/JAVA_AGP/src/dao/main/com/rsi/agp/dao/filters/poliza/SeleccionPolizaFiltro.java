package com.rsi.agp.dao.filters.poliza;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.dao.filters.Filter;
import com.rsi.agp.dao.filters.FiltroUtils;
import com.rsi.agp.dao.tables.poliza.Poliza;

public class SeleccionPolizaFiltro implements Filter {

	private Poliza poliza;
	
	//no se listaran las polizas que tengan alguno de estos estados
	private BigDecimal estadosPolizaNoIncluir [];
	
	private BigDecimal estadosPolizaIncluir [];
	
	// MPM - 21/05/12
	// No se listaran las polizas que tengan pertenezcan a alguno de estos colectivos
	private String colectivosNoIncluir [];
	
	private final Log logger = LogFactory.getLog(SeleccionPolizaFiltro.class);
	
	public SeleccionPolizaFiltro(final Poliza poliza) {
		this.poliza = poliza;
	}

	@Override
	public Criteria getCriteria(final Session sesion) {
		final Criteria criteria = sesion.createCriteria(Poliza.class);		

		if (poliza.getColectivo() != null || colectivosNoIncluir!=null)
			criteria.createAlias("colectivo", "col");
		
		criteria.createAlias("asegurado", "ase");
		criteria.createAlias("linea", "lin");
		
		String usuario = poliza.getUsuario().getCodusuario();
		if (FiltroUtils.noEstaVacio(usuario)) {
			criteria.createAlias("usuario", "usu");
			criteria.add(Restrictions.eq("usu.codusuario", usuario));
		}
		
		if (FiltroUtils.noEstaVacio(poliza)) {
			criteria.add(Restrictions.allEq(getMapaPoliza()));
		}
		
		if(FiltroUtils.noEstaVacio(poliza.getBloqueadopor())){
			criteria.add(Restrictions.eq("bloqueadopor", poliza.getBloqueadopor()));
		}
		
		final String nombre = poliza.getAsegurado().getNombre();
		if (FiltroUtils.noEstaVacio(nombre)) {
			criteria.add(
					Restrictions.disjunction()
					.add(Restrictions.sqlRestriction("upper(nombre||' '||apellido1||' '||apellido2) like upper('%"+nombre+"%')"))
					.add(Restrictions.sqlRestriction("upper(razonsocial) like upper('%"+nombre+"%')"))
			);
					
					
		}
		if(estadosPolizaIncluir != null) 
			criteria.add(Restrictions.in("estadoPoliza.idestado", estadosPolizaIncluir));
		
		if(estadosPolizaNoIncluir != null) 
			criteria.add(Restrictions.not(Restrictions.in("estadoPoliza.idestado", estadosPolizaNoIncluir)));
		
		// MPM - 21/05/12
		// Si se ha introducido algun colectivo para que se excluyan las polizas que estan asociadas a el
		if (colectivosNoIncluir != null) {
			criteria.add(Restrictions.not(Restrictions.in("col.idcolectivo", colectivosNoIncluir)));
		}
		
		logger.debug("Consulta para obtener el listado de polizas: " + this.toString());
	
		return criteria;
	}

	private final Map<String, Object> getMapaPoliza() {
		
		final Map<String, Object> mapa = new HashMap<String, Object>();
 
/*
 * DATOS DEL COLECTIVO
 */
		if (poliza.getColectivo() != null){
			final String cifTomador = poliza.getColectivo().getTomador().getId().getCiftomador();
			
			if (FiltroUtils.noEstaVacio(cifTomador)) {
				mapa.put("col.tomador.id.ciftomador", cifTomador);
			}
	
			final BigDecimal codEntidad = poliza.getColectivo().getTomador().getId().getCodentidad();
			if (FiltroUtils.noEstaVacio(codEntidad)) {
				mapa.put("col.tomador.id.codentidad", codEntidad);
			}

			final Long idColectivo = poliza.getColectivo().getId();
			if (FiltroUtils.noEstaVacio(idColectivo)) {
				mapa.put("col.id", idColectivo);
			}
			final String colectivo = poliza.getColectivo().getIdcolectivo();
			if(FiltroUtils.noEstaVacio(colectivo)){
				mapa.put("col.idcolectivo", colectivo);
			}
			
			final String dcColectivo = poliza.getColectivo().getDc();
			if (FiltroUtils.noEstaVacio(dcColectivo)) {
				mapa.put("col.dc", dcColectivo);
			}

		}
		
		final BigDecimal codPlan = poliza.getLinea().getCodplan();
		if(FiltroUtils.noEstaVacio(codPlan)){
			mapa.put("lin.codplan", codPlan);
		}

		final BigDecimal codlinea = poliza.getLinea().getCodlinea();
		if (FiltroUtils.noEstaVacio(codlinea)) {
			mapa.put("lin.codlinea", codlinea);
		}
		
		final Long lineaseguro = poliza.getLinea().getLineaseguroid();
		if(FiltroUtils.noEstaVacio(lineaseguro)){
			mapa.put("lin.lineaseguroid", lineaseguro);
		}
 
/*
 * DATOS DEL ASEGURADO
 */
		final Long idAsegurado = poliza.getAsegurado().getId();
		if (FiltroUtils.noEstaVacio(idAsegurado)) {
			mapa.put("ase.id", idAsegurado);
		}

		final String nifCif = poliza.getAsegurado().getNifcif();
		if (FiltroUtils.noEstaVacio(nifCif)) {
			mapa.put("ase.nifcif", nifCif);
		}

		final BigDecimal codEntidadAseg = poliza.getAsegurado().getEntidad().getCodentidad();
		if (FiltroUtils.noEstaVacio(codEntidadAseg)) {
			mapa.put("ase.entidad.codentidad", codEntidadAseg);
		}
 
		final String discriminante = poliza.getAsegurado().getDiscriminante();
		if (FiltroUtils.noEstaVacio(discriminante)) {
			mapa.put("ase.discriminante", discriminante);
		}

		

		/*
		 * DATOS DE LA POLIZA
		 */
		final String oficina = poliza.getOficina();
		if (FiltroUtils.noEstaVacio(oficina)) {
			mapa.put("oficina", oficina);
		}

		final String modulo = poliza.getCodmodulo();
		if (FiltroUtils.noEstaVacio(modulo)) {
			mapa.put("codmodulo", modulo);
		}

		final Long idPoliza = poliza.getIdpoliza();
		if (FiltroUtils.noEstaVacio(idPoliza)) {
			mapa.put("idpoliza", idPoliza);
		}

		final BigDecimal idestado = poliza.getEstadoPoliza().getIdestado();
		if (FiltroUtils.noEstaVacio(idestado)) {
			mapa.put("estadoPoliza.idestado", idestado);
		}
		
		final String refPol = poliza.getReferencia();
		if(FiltroUtils.noEstaVacio(refPol)){
			mapa.put("referencia", refPol);
		}
		//filtramos por clase
		try {
			final BigDecimal clase = poliza.getClase();
			if (FiltroUtils.noEstaVacio(clase)) {
				mapa.put("clase", clase);
			}
		} catch (NullPointerException e) {
			
		}
		
		//filtramos por tipo de referencia
		try {
			final Character tipoReferencia = poliza.getTipoReferencia();
			if (FiltroUtils.noEstaVacio(tipoReferencia)) {
				mapa.put("tipoReferencia", tipoReferencia);
			}
		} catch (NullPointerException e) {
			
		}
		
		return mapa;
	}

	public BigDecimal[] getEstadosPolizaNoIncluir() {
		return estadosPolizaNoIncluir;
	}

	public void setEstadosPolizaNoIncluir(BigDecimal[] estadosPolizaNoIncluir) {
		this.estadosPolizaNoIncluir = estadosPolizaNoIncluir;
	}

	@Override
	public String toString() {
		String cadena = "SELECT * FROM polizas, colectivos col, asegurados ase, lineas lin WHERE ... ";
		
		if (FiltroUtils.noEstaVacio(poliza)) {
			final String usuario = poliza.getUsuario().getCodusuario();
			if (FiltroUtils.noEstaVacio(usuario)) {
				cadena += " AND usuario.codusuario = ";
			}

			if (poliza.getColectivo() != null){
				final String cifTomador = poliza.getColectivo().getTomador().getId().getCiftomador();
				
				if (FiltroUtils.noEstaVacio(cifTomador)) {
					cadena += " AND col.tomador.id.ciftomador = " + cifTomador;
				}
		
				final BigDecimal codEntidad = poliza.getColectivo().getTomador().getId().getCodentidad();
				if (FiltroUtils.noEstaVacio(codEntidad)) {
					cadena += " AND col.tomador.id.codentidad = " + codEntidad;
				}

				final Long idColectivo = poliza.getColectivo().getId();
				if (FiltroUtils.noEstaVacio(idColectivo)) {
					cadena += " AND col.id = " + idColectivo;
				}
				final String colectivo = poliza.getColectivo().getIdcolectivo();
				if(FiltroUtils.noEstaVacio(colectivo)){
					cadena += " AND col.idcolectivo = " + colectivo;
				}
				
				final String dcColectivo = poliza.getColectivo().getDc();
				if (FiltroUtils.noEstaVacio(dcColectivo)) {
					cadena += " AND col.dc = " + dcColectivo;
				}

			}
			
			final BigDecimal codPlan = poliza.getLinea().getCodplan();
			if(FiltroUtils.noEstaVacio(codPlan)){
				cadena += " AND lin.codplan = " + codPlan;
			}

			final BigDecimal codlinea = poliza.getLinea().getCodlinea();
			if (FiltroUtils.noEstaVacio(codlinea)) {
				cadena += " AND lin.codlinea = " + codlinea;
			}
			
			final Long lineaseguro = poliza.getLinea().getLineaseguroid();
			if(FiltroUtils.noEstaVacio(lineaseguro)){
				cadena += " AND lin.lineaseguroid = " + lineaseguro;
			}
	 
			final Long idAsegurado = poliza.getAsegurado().getId();
			if (FiltroUtils.noEstaVacio(idAsegurado)) {
				cadena += " AND ase.id = " + idAsegurado;
			}

			final String nifCif = poliza.getAsegurado().getNifcif();
			if (FiltroUtils.noEstaVacio(nifCif)) {
				cadena += " AND ase.nifcif = " + nifCif;
			}

			final BigDecimal codEntidadAseg = poliza.getAsegurado().getEntidad().getCodentidad();
			if (FiltroUtils.noEstaVacio(codEntidadAseg)) {
				cadena += " AND ase.entidad.codentidad = " + codEntidadAseg;
			}
	 
			final String discriminante = poliza.getAsegurado().getDiscriminante();
			if (FiltroUtils.noEstaVacio(discriminante)) {
				cadena += " AND ase.discriminante = " + discriminante;
			}

			final String oficina = poliza.getOficina();
			if (FiltroUtils.noEstaVacio(oficina)) {
				cadena += " AND oficina = " + oficina;
			}

			final String modulo = poliza.getCodmodulo();
			if (FiltroUtils.noEstaVacio(modulo)) {
				cadena += " AND codmodulo = " + modulo;
			}

			final Long idPoliza = poliza.getIdpoliza();
			if (FiltroUtils.noEstaVacio(idPoliza)) {
				cadena += " AND idpoliza = " + idPoliza;
			}

			final BigDecimal idestado = poliza.getEstadoPoliza().getIdestado();
			if (FiltroUtils.noEstaVacio(idestado)) {
				cadena += " AND estadoPoliza.idestado = " + idestado;
			}
			
			final String refPol = poliza.getReferencia();
			if(FiltroUtils.noEstaVacio(refPol)){
				cadena += " AND referencia = " + refPol;
			}
			//filtramos por clase
			final BigDecimal clase = poliza.getClase();
			if (FiltroUtils.noEstaVacio(clase)) {
				cadena += " AND clase = " + clase;
			}
			
		}
		
		if(FiltroUtils.noEstaVacio(poliza.getBloqueadopor())){
			cadena += " AND bloqueadopor = " + poliza.getBloqueadopor();
		}
		
		final String nombre = poliza.getAsegurado().getNombre();
		if (FiltroUtils.noEstaVacio(nombre)) {
			cadena += " AND (";
			cadena += "upper(nombre||' '||apellido1||' '||apellido2) like upper('%"+nombre+"%') OR ";
			cadena += "upper(razonsocial) like upper('%"+nombre+"%')";
			cadena += ")";
		}
		
		if(estadosPolizaNoIncluir != null) {
			cadena += " AND estadoPoliza.idestado not in (";
			for (BigDecimal e: estadosPolizaNoIncluir){
				cadena += e + ", ";
			}
			cadena += ")";
		}
		
		// MPM - 21/05/12
		// Si se ha introducido algun colectivo para que se excluyan las polizas que estan asociadas a el
		if(colectivosNoIncluir != null) {
			cadena += " AND col.idcolectivo not in (";
			for (String e: colectivosNoIncluir){
				cadena += e + ", ";
			}
			cadena += ")";
		}
		
		return cadena;
	}

	public void setColectivosNoIncluir(String[] colectivosNoIncluir) {
		this.colectivosNoIncluir = colectivosNoIncluir;
	}

	public BigDecimal[] getEstadosPolizaIncluir() {
		return estadosPolizaIncluir;
	}

	public void setEstadosPolizaIncluir(BigDecimal[] estadosPolizaIncluir) {
		this.estadosPolizaIncluir = estadosPolizaIncluir;
	}
	
	

}
