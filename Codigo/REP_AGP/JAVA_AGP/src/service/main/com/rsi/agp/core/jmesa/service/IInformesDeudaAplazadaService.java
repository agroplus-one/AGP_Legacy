package com.rsi.agp.core.jmesa.service;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.jmesa.dao.IGenericoDao;
import com.rsi.agp.core.jmesa.dao.impl.IIncidenciasComisionesUnificadoDao;
import com.rsi.agp.core.jmesa.dao.impl.IInformesDeudaAplazadaDao;
import com.rsi.agp.dao.tables.comisiones.unificado.FicheroIncidenciasUnificado;
import com.rsi.agp.dao.tables.comisiones.unificado.FicheroUnificado;
import com.rsi.agp.dao.tables.comisiones.unificado.informes.InformeDeudaAplazadaUnificado;
import com.rsi.agp.dao.tables.commons.Usuario;

public interface IInformesDeudaAplazadaService extends
		IGetTablaService {
	String getTabla(HttpServletRequest request, HttpServletResponse response,
			Serializable infDeudaBean, String origenLlamada,
			List<BigDecimal> listaGrupoEntidades, String perfil,
			boolean externo, IGenericoDao genericoDao);
	public String getNombreLinea(Integer linea,
			IInformesDeudaAplazadaDao informesDeudaAplazadaDao);
	public String getNombreEntidad(Integer codentidad,
			IInformesDeudaAplazadaDao informesDeudaAplazadaDao);
	List<InformeDeudaAplazadaUnificado> getListado(HttpServletRequest request,
			HttpServletResponse response, Serializable infDeudaBean,
			String origenLlamada, List<BigDecimal> listaGrupoEntidades,
			String perfil, boolean externo, IGenericoDao genericoDao);

}
