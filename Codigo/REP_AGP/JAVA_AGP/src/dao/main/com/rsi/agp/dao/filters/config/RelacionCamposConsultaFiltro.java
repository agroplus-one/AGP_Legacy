package com.rsi.agp.dao.filters.config;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.dao.filters.Filter;
import com.rsi.agp.dao.filters.FiltroUtils;
import com.rsi.agp.dao.tables.cesp.PeriodoGarantiaCe;
import com.rsi.agp.dao.tables.config.ConfiguracionCampo;
import com.rsi.agp.dao.tables.config.GrupoFactores;
import com.rsi.agp.dao.tables.config.RelacionCampo;
import com.rsi.agp.dao.tables.org.DiccionarioDatos;
import com.rsi.agp.dao.tables.org.Ubicacion;
import com.rsi.agp.dao.tables.org.Uso;
import com.rsi.agp.dao.tables.poliza.DatoVariableParcela;

public class RelacionCamposConsultaFiltro implements Filter {
	
    private RelacionCampo relacionCampo;
	
	public RelacionCamposConsultaFiltro(final RelacionCampo relacionCampo){
		this.relacionCampo = relacionCampo;
	}

	@Override
	public final Criteria getCriteria(final Session sesion) {
		final Criteria criteria = sesion.createCriteria(RelacionCampo.class);
		
		criteria.add(Restrictions.allEq(getMapaCriterios()));
		
		return criteria;
	}
	
	
	private final Map<String, Object> getMapaCriterios() {
		final Map<String, Object> mapa = new HashMap<String, Object>();
			
		// tipoCampo
		if (FiltroUtils.noEstaVacio(relacionCampo.getTipocampo())) {
			mapa.put("tipocampo",relacionCampo.getTipocampo());
		}
		
		// plan  -->  ???????????????????????????		
		if (FiltroUtils.noEstaVacio(relacionCampo.getLinea()))
		{
			if (FiltroUtils.noEstaVacio(relacionCampo.getLinea().getLineaseguroid()))
			{
				mapa.put("linea.lineaseguroid", relacionCampo.getLinea().getLineaseguroid());
			}
		}
	
		// uso
		if(FiltroUtils.noEstaVacio(relacionCampo.getUso())){
			if(FiltroUtils.noEstaVacio(relacionCampo.getUso().getCoduso()))
			    mapa.put("uso.coduso",relacionCampo.getUso().getCoduso());
		}
		
		// ubicacion
		if (FiltroUtils.noEstaVacio(relacionCampo.getUbicacion())){
			if(FiltroUtils.noEstaVacio(relacionCampo.getUbicacion().getCodubicacion()))
			    mapa.put("ubicacion.codubicacion", relacionCampo.getUbicacion().getCodubicacion());
		}
		
		// campoSC
		if (FiltroUtils.noEstaVacio(relacionCampo.getDiccionarioDatos())){
			if(FiltroUtils.noEstaVacio(relacionCampo.getDiccionarioDatos().getCodconcepto()))
			    mapa.put("diccionarioDatos.codconcepto", relacionCampo.getDiccionarioDatos().getCodconcepto());
		}
		
		// grupoFactores
		if (FiltroUtils.noEstaVacio(relacionCampo.getGrupoFactores())){
			if(FiltroUtils.noEstaVacio(relacionCampo.getGrupoFactores().getIdgrupofactores()))
			    mapa.put("grupoFactores.idgrupofactores", relacionCampo.getGrupoFactores().getIdgrupofactores());
		}
	    
		// procesoCalculo
		if (FiltroUtils.noEstaVacio(relacionCampo.getProcesocalculo())){
			mapa.put("procesocalculo", relacionCampo.getProcesocalculo());
		}
		return mapa;	
	}
}
