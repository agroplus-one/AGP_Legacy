package com.rsi.agp.core.jmesa.service.impl.ganado;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.rsi.agp.core.jmesa.dao.IGenericoDao;
import com.rsi.agp.core.jmesa.service.IGetTablaService;
import com.rsi.agp.dao.tables.commons.Usuario;

public interface IExplotacionesService extends IGetTablaService {
	public String getTabla(HttpServletRequest request,
			HttpServletResponse response,
			Serializable informeComisionesUnificado, String origenLlamada,
			List<BigDecimal> listaGrupoEntidades, IGenericoDao genericoDao,final Boolean esModoLectura);
}
