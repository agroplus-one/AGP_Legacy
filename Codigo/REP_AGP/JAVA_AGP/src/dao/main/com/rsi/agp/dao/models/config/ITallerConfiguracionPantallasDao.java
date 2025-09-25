package com.rsi.agp.dao.models.config;

import java.math.BigDecimal;
import java.util.List;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.dao.models.GenericDao;
import com.rsi.agp.dao.tables.config.ConfiguracionCampo;
import com.rsi.agp.dao.tables.config.PantallaConfigurable;
import com.rsi.agp.dao.tables.org.OrganizadorInformacion;
import com.rsi.agp.vo.ComboDataVO;

@SuppressWarnings("rawtypes")
public interface ITallerConfiguracionPantallasDao extends GenericDao {

	public List<ComboDataVO> getUsos(final Long lineaseguroid) throws BusinessException;

	public List getEstructuraCampos(final Long idLinea, final BigDecimal codUso) throws BusinessException;

	public PantallaConfigurable getPantallaConfigurada(final Long idPantallaConfigurable) throws BusinessException;

	public void saveCamposPantalla(final Long idPantallaConfigurable, final List<ConfiguracionCampo> campos)
			throws BusinessException;

	public OrganizadorInformacion getOrgInformacion(final Long lineaseguroid, final BigDecimal codConcepto,
			final BigDecimal codUbicacion, final BigDecimal codUso) throws BusinessException;
}