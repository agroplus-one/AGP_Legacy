package com.rsi.agp.core.jmesa.service.utilidades;

import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jmesa.limit.Limit;
import org.springframework.web.multipart.MultipartFile;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.jmesa.dao.IGenericoDao;
import com.rsi.agp.core.jmesa.service.IGetTablaService;
import com.rsi.agp.dao.tables.admin.ClaseDetalleGanado;

public interface IClaseDetalleGanadoService extends IGetTablaService {
	public Map<String, Object> insertOrUpdate(ClaseDetalleGanado claseDetalleGanado) throws BusinessException;
	public void bajaClaseDetalle(ClaseDetalleGanado claseDetalleGanado) throws BusinessException;
	public Map<String, Object> cambioMasivo(String listaIdsMarcados_cm, ClaseDetalleGanado claseDetalleGanadoBean, String tipoCapitalCheck);
	public ClaseDetalleGanado getBeanFromLimit(Limit consulta_LIMIT);
	public void importaFichero(Map<String, Object> parametros, MultipartFile file)throws Exception, IOException;
	public String getTabla(HttpServletRequest request, HttpServletResponse response, Serializable claseDetalleGanadoBean,
							String origenLlamada, String vieneDeCargaClases, List<BigDecimal> listaGrupoEntidades, IGenericoDao genericoDao); 
}
