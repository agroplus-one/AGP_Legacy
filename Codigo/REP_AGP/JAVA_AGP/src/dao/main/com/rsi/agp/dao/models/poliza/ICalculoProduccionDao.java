package com.rsi.agp.dao.models.poliza;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.dao.filters.cpl.LimiteRendimientoFiltro;
import com.rsi.agp.dao.models.GenericDao;
import com.rsi.agp.dao.tables.cpl.LimiteRendimiento;
import com.rsi.agp.dao.tables.cpl.MascaraLimiteRendimiento;
import com.rsi.agp.dao.tables.cpl.MascaraRendimientoCaracteristicaEspecifica;
import com.rsi.agp.dao.tables.cpl.RendimientoCaracteristicaEspecifica;
import com.rsi.agp.vo.ItemVO;
import com.rsi.agp.vo.ParcelaVO;

@SuppressWarnings("rawtypes")
public interface ICalculoProduccionDao extends GenericDao {

	/**
	 * Metodo para consultar en la base de datos si el calculo de precio se hace
	 * mediante pl/sql o mediante codigo java.
	 * 
	 * @return
	 */
	public boolean calcularConPlSql();

	/**
	 * Metodo para consultar en la base de datos si el calculo de rendimiento de
	 * producci�n se hace mediante SW o no
	 * 
	 * @return
	 */
	public boolean calcularRendimientoProdConSW();

	/**
	 * Metodo para realizar la llamada al pl/sql de calculo de produccion.
	 * 
	 * @param lineaseguroid
	 *            Identificador de plan/linea
	 * @param datosVariablesCapAseg
	 *            Cadena de texto con los datos variables y sus valores
	 * @param nifasegurado
	 *            Nif del Asegurado de la poliza
	 * @param codmodulo
	 *            Modulo de la poliza
	 * @param codcultivo
	 *            Cultivo de la parcela
	 * @param codvariedad
	 *            Variedad de la parcela
	 * @param codprovincia
	 *            Provincia
	 * @param codcomarca
	 *            Comarca
	 * @param codtermino
	 *            Termino
	 * @param subtermino
	 *            Subtermino
	 * @return Array de dos posiciones con el maximo y el minimo de precio para
	 *         los datos de la parcela introducidos
	 * @throws BusinessException
	 */
	public String[] getProduccionPlSql(Long lineaseguroid, Long idpoliza, List<ItemVO> datosVariablesCapAseg,
			String nifasegurado, String codmodulo, String codcultivo, String codvariedad, String codprovincia,
			String codcomarca, String codtermino, String subtermino, String sigpac) throws BusinessException;

	/**
	 * Metodo para saber si afecta el garantizado o la fecha de fin de garantias
	 * en el calculo de produccion
	 * 
	 * @param lineaseguroid
	 *            Identificador de plan/linea
	 * @return Número de registros que cumplen el criterio. Si es 0 => afecta el
	 *         garantizado y en otro caso, afecta la fecha de fin de garantias
	 * @throws DAOException
	 */
	public Integer getOrganizadorInformacion(Long lineaseguroid) throws DAOException;

	/**
	 * Para saber si el coeficiente que aplica en el calculo de produccion es el
	 * de aseguradoAutorizado o el del coeficiente de medidas
	 * 
	 * @param lineaseguroid
	 *            Identificador de plan/linea
	 * @param codmodulo
	 *            Modulo
	 * @return Número de registros en MedidasAplicablesModulo para los criterios
	 *         indicados. Si es distinto de 0 aplicara el de medias.
	 * @throws DAOException
	 */
	public Integer getMedidasAplicableModulo(Long lineaseguroid, String codmodulo) throws DAOException;

	/**
	 * Metodo para saber si existen registros en la tabla de limites de
	 * rendimiento para un determindado plan/linea
	 * 
	 * @param lineaseguroid
	 *            Identificador de plan/linea.
	 * @return Booleano indicando si existen registros.
	 */
	public boolean existeLimiteRendimientoByLineaseguroid(Long lineaseguroid);

	/**
	 * Metodo para saber si calculamos los rendimientos con la tabla
	 * RDTOS_CARACT_ESP
	 * 
	 * @param lineaseguroid
	 *            Identificador de plan/linea
	 * @return Booleano indicando si calculamos los rendimientos con la tabla
	 *         RDTOS_CARACT_ESP
	 */
	public boolean checkRendCaracEsp(Long lineaseguroid);

	/**
	 * Metodo para obtener los registros de mascara de rendimiento de
	 * caracteristicas especificas que aplican en el calculo de produccion
	 * 
	 * @param lineaseguroid
	 * @param codmodulo
	 * @param parcela
	 * @return
	 */
	public List<MascaraRendimientoCaracteristicaEspecifica> getMascaraRdtosCaracEsp(Long lineaseguroid,
			String codmodulo, ParcelaVO parcela);

	/**
	 * Metodo para obtener la lista de registros de rendimientos de
	 * caracteristicas especificas que aplican en el calculo de produccion.
	 * 
	 * @param tablaRdto
	 * @param lineaseguroid
	 * @param codmodulo
	 * @param parcela
	 * @param filtroMascara
	 * @return
	 */
	public List<RendimientoCaracteristicaEspecifica> dameListaRenCaracEsp(BigDecimal tablaRdto, Long lineaseguroid,
			String codmodulo, ParcelaVO parcela, HashMap<String, String> filtroMascara);

	/**
	 * Metodo para obtener los registros de mascara de limites de rendimiento que
	 * aplican en el calculo de la produccion.
	 * 
	 * @param lineaseguroid
	 * @param codmodulo
	 * @param parcela
	 * @return
	 */
	public List<MascaraLimiteRendimiento> getMascaraLimiteRendimiento(Long lineaseguroid, String codmodulo,
			ParcelaVO parcela);

	/**
	 * Metodo para obtener la produccion de una parcela
	 * 
	 * @param limRendFiltro
	 *            Filtro para el calculo de la produccion
	 * @return
	 */
	public List<LimiteRendimiento> getRendimientos(LimiteRendimientoFiltro limRendFiltro);

	boolean getRiesgoCubElegCalculoRendi(Long lineaseguroid, String codmodulo, List<BigDecimal> lstCodCultivos) throws DAOException;
}
