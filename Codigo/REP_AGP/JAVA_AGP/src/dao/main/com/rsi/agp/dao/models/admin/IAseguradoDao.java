package com.rsi.agp.dao.models.admin;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.dao.filters.admin.impl.Asegurado2Filtro;
import com.rsi.agp.dao.models.GenericDao;
import com.rsi.agp.dao.tables.admin.Asegurado;
import com.rsi.agp.dao.tables.admin.BloqueosAsegurado;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.cpl.AseguradoAutorizadoSC;
import com.rsi.agp.dao.tables.cpl.SubvencionesGrupo;
import com.rsi.agp.dao.tables.cpl.gan.AseguradoAutorizadoGanado;
import com.rsi.agp.dao.tables.poliza.Poliza;
import com.rsi.agp.pagination.PageProperties;
import com.rsi.agp.pagination.PaginatedListImpl;

@SuppressWarnings("rawtypes")
public interface IAseguradoDao extends GenericDao {
	public int getCountAsegurados(Asegurado2Filtro aseguradoFiltro);

	public List<Poliza> tienePolizasVigentes(Asegurado aseg, BigDecimal[] estados) throws DAOException;

	public Map<String, Object> getMapaAsegurado(Asegurado aseguradoBean) throws DAOException;

	public List<Asegurado> getAseguradosGrupoEntidad(Asegurado aseguradoBusqueda, List<BigDecimal> entidades)
			throws DAOException;

	public Usuario aseguradoCargadoUsuario(Asegurado aseg, Long lineaseguroid, String usuario) throws DAOException;

	public PaginatedListImpl<Asegurado> getPaginatedListAsegurados(PageProperties pageProperties,
			Asegurado2Filtro aseguradoFiltro) throws DAOException;

	public List<SubvencionesGrupo> getGruposSubv(BigDecimal codplan) throws DAOException;

	public Integer getCountAseguradosAutorizados(final Long lineaseguroid, final String nifcif) throws DAOException;

	public Integer getCountAseguradosAutorizadosG(final Long lineaseguroid, final String nifcif) throws DAOException;

	public AseguradoAutorizadoSC[] getAAC(final Long lineaseguroid, final String nifcif) throws DAOException;

	public AseguradoAutorizadoGanado[] getAACGan(final Long lineaseguroid, final String nifcif) throws DAOException;

	public BigDecimal[] obtenerIntervaloCoefReduccionRdtos(String tipo, Long lineaseguroid, String nifcifAsegurado,
			List<String> lstCodModulos, List<BigDecimal> lstCodCultivos, List<BigDecimal> lstCodVariedades)
			throws DAOException;

	public boolean chekAseguradoDisponible(String codusuario, Long idAsegurado);

	public Integer getcountOrigenInfo(Long lineaseguroid, BigDecimal usoAutorizContrat) throws DAOException;

	public List getCodsOrganismos(List<BigDecimal> codsProv) throws DAOException;

	public void desbloqueaAsegurado(String usuarioAsegurado) throws DAOException;

	public void actualizaDatosAseguradoWS(Asegurado aseguradoBean, String idAseg) throws DAOException;

	public Object[] getDatosProvincia(BigDecimal bigDecimal, String localidad) throws DAOException;

	public void actualizaFechaRevision(String idAseg, String revisado) throws DAOException;

	public String validaCargaASegurado() throws DAOException;

	public Asegurado getAseguradoById(String id) throws DAOException;

	public BloqueosAsegurado consultarAsegBloqueado(String nifcif) throws DAOException;

	public List<Asegurado> getAsegurados(String inicio, String fin) throws DAOException;
}