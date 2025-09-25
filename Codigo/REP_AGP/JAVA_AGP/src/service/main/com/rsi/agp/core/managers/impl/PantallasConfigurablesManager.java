/*
**************************************************************************************************
*
*  CReACION:
*  ------------
*
* REFERENCIA  FECHA       AUTOR             DESCRIPCION
* ----------  ----------  ----------------  ------------------------------------------------------
* P000015034              Miguel Granadino  Manager para pantallasConfigurables.jsp
*
 **************************************************************************************************
*/
package com.rsi.agp.core.managers.impl;

import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rsi.agp.core.managers.IManager;
import com.rsi.agp.dao.models.commons.ICommonDao;
import com.rsi.agp.dao.models.config.IPantallasConfigurablesDao;
import com.rsi.agp.dao.tables.config.ConfiguracionCampo;
import com.rsi.agp.dao.tables.config.ConfiguracionCampoId;
import com.rsi.agp.dao.tables.config.Pantalla;
import com.rsi.agp.dao.tables.config.PantallaConfigurable;
import com.rsi.agp.dao.tables.org.OrganizadorInformacion;
import com.rsi.agp.dao.tables.org.OrganizadorInformacionId;
import com.rsi.agp.dao.tables.poliza.Linea;

public class PantallasConfigurablesManager implements IManager {
	private IPantallasConfigurablesDao pantallasConfigurablesDao;
	private ICommonDao commonDao;
	protected final Log logger = LogFactory.getLog(getClass());
	
	public void setPantallasConfigurablesDao(IPantallasConfigurablesDao pantallasConfigurablesDao) {
		this.pantallasConfigurablesDao = pantallasConfigurablesDao;
	}
	
	public void setCommonDao(ICommonDao commonDao) {
		this.commonDao = commonDao;
	}
	
	@SuppressWarnings("rawtypes")
	public List getPlanes(){
		List listPlanes = null;
		try {
			listPlanes = commonDao.getPlanes();
		}
		catch(Exception excepcion){
			logger.error("Excepcion : PantallasConfigurablesManager - getPlanes", excepcion);
		}
		return listPlanes;
	}
	
	@SuppressWarnings("rawtypes")
	public List getLineas(BigDecimal codPlan){
		List listLineas = null;
		try {
			listLineas = commonDao.getLineas(codPlan);
		}
		catch(Exception excepcion) {
			logger.error("Excepcion : PantallasConfigurablesManager - getLineas", excepcion);
		}
		return listLineas;
	}
	
	@SuppressWarnings("rawtypes")
	public List getPantallas(){
		List listLineas = null;
		try {
			listLineas = pantallasConfigurablesDao.getPantallas();
		}
		catch(Exception excepcion) {
			logger.error("Excepcion : PantallasConfigurablesManager - getPantallas", excepcion);
		}
		return listLineas;
	}
	
	public Pantalla getPantalla(Long idPantalla){
		Pantalla pantalla = null;
		try{
			pantalla = pantallasConfigurablesDao.getPantalla(idPantalla);
		}
		catch(Exception excepcion) {
			logger.error("Excepcion : PantallasConfigurablesManager - getPantalla", excepcion);
		}
		return pantalla;
	}
	
	@SuppressWarnings("rawtypes")
	public List getPantallasConfigurables(){
		List listLineas = null;
		try {
			listLineas = pantallasConfigurablesDao.getPantallasConfigurables();
		}
		catch(Exception excepcion) {
			logger.error("Excepcion : PantallasConfigurablesManager - getPantallasConfigurables", excepcion);
		}
		return listLineas;
	}
	
	public void delete(Long idRow){
		try {
			// Delete de los campos configurados para esa linea
			
			
			// Delete de la linea
			pantallasConfigurablesDao.removeObject(PantallaConfigurable.class, idRow);
		}
		catch(Exception excepcion) {
			logger.error("Excepcion : PantallasConfigurablesManager - delete", excepcion);
		}	
	}
	
	public void savePantallaConfigurable(PantallaConfigurable pantallaConfigurable) throws Exception{
	    try {
	    	pantallasConfigurablesDao.saveOrUpdate(pantallaConfigurable);
		}
		catch(Exception excepcion) {
			logger.error("Error al guardar la pantalla configurable", excepcion);
			throw excepcion;
	    }
	}
	
	public boolean existePantalla(Long lineaseguroid, Long idpantalla, Long idpantallaconfigurable)
	{
		return pantallasConfigurablesDao.existePantalla(lineaseguroid, idpantalla, idpantallaconfigurable);
	}
	
	public PantallaConfigurable getPantallaConfigurable(Long idRow){
	    PantallaConfigurable pantallasConfigurables = (PantallaConfigurable)pantallasConfigurablesDao.getObject(PantallaConfigurable.class, idRow);
		return pantallasConfigurables;
	}
	
	@SuppressWarnings("unchecked")
	public List<PantallaConfigurable> consulta(PantallaConfigurable pantallaConfigurable){
		List<PantallaConfigurable> resultConsulta = null;
		try {
	    	resultConsulta = pantallasConfigurablesDao.getPantallasConfigurablesConsulta(pantallaConfigurable);
		}
		catch(Exception excepcion) {
			logger.error("Excepcion : PantallasConfigurablesManager - consulta", excepcion);
	    }
		return resultConsulta;
	}
	
	@SuppressWarnings("rawtypes")
	public List getPantallasConfigurables(Long idLinea){
		List listLineas = null;
		try {
			listLineas = pantallasConfigurablesDao.getPantallasConfigurables(idLinea);
		}
		catch(Exception excepcion) {
			logger.error("Excepcion : PantallasConfigurablesManager - getPantallasConfigurables", excepcion);
		}
		return listLineas;
	}
	
	
	
	@SuppressWarnings("unchecked")
	public void replicar(Long idLineaOrigen, Long idLineaDestino) throws Exception {
		try {
			/* ESC-10686 ** MODIF TAM (17/09/2020) ** Inicio */
			/* Antes de replicar comprobamos si ya existen datos para el Plan/Linea destino
			 * en cuyo caso y antes de replicar borraremos las pantallas configurables existentes
			 */
			
			List<PantallaConfigurable> listPantallasLineaDestino = null;
			boolean borrar = false;
			listPantallasLineaDestino = getPantallasConfigurables(new Long(idLineaDestino));
			if (listPantallasLineaDestino != null) {
				if (listPantallasLineaDestino.size() > 0) {
					borrar = true;
				}
			}
			
			if (borrar == true) {
				for (PantallaConfigurable pantallaConfigurable : listPantallasLineaDestino) {
					delete(pantallaConfigurable.getIdpantallaconfigurable());
				}
				
			}
			/* ESC-10686 ** MODIF TAM (17/09/2020) ** Fin */
			
			// Replica todas las pantallas configurables de un idLinea a otro idLinea
			List<PantallaConfigurable> listPantallasConfigurables = pantallasConfigurablesDao
					.getPantallasConfigurables(idLineaOrigen);

			for (PantallaConfigurable pantallaConfigurable : listPantallasConfigurables) {
				// Replicar pantalla configurada
				PantallaConfigurable newPantalla = new PantallaConfigurable(null, pantallaConfigurable.getPantalla(),
						(Linea) pantallasConfigurablesDao.get(Linea.class, idLineaDestino));
				newPantalla.setGruposeguro(pantallaConfigurable.getGruposeguro());
				newPantalla.setObligatoria(pantallaConfigurable.getObligatoria());
				pantallasConfigurablesDao.saveOrUpdate(newPantalla);

				List<ConfiguracionCampo> lstConfiguracionCampos = pantallasConfigurablesDao.getObjects(
						ConfiguracionCampo.class, "id.idpantallaconfigurable",
						BigDecimal.valueOf(pantallaConfigurable.getIdpantallaconfigurable()));

				for (ConfiguracionCampo cc : lstConfiguracionCampos) {
					/*
					 * BigDecimal idpantallaconfigurable, BigDecimal idseccion, Long lineaseguroid,
					 * BigDecimal codubicacion, BigDecimal codconcepto, BigDecimal coduso
					 */
					ConfiguracionCampoId id = new ConfiguracionCampoId(
							new BigDecimal(newPantalla.getIdpantallaconfigurable()), cc.getId().getIdseccion(),
							idLineaDestino, cc.getId().getCodubicacion(), cc.getId().getCodconcepto(),
							cc.getId().getCoduso());

					/*
					 * ConfiguracionCampoId id, TipoCampo tipoCampo, PantallaConfigurable
					 * pantallaConfigurable, OrganizadorInformacion organizadorInformacion,
					 * OrigenDatos origenDatos, Seccion seccion, String etiqueta, BigDecimal x,
					 * BigDecimal y, BigDecimal ancho, BigDecimal alto, Character disabled,
					 * Character mostrarsiempre
					 */
					OrganizadorInformacion oi = new OrganizadorInformacion();
					OrganizadorInformacionId idOi = new OrganizadorInformacionId(idLineaDestino,
							cc.getId().getCodubicacion(), cc.getId().getCodconcepto(), cc.getId().getCoduso());
					oi.setId(idOi);

					OrganizadorInformacion orgInfo = (OrganizadorInformacion) this.commonDao
							.getObject(OrganizadorInformacion.class, idOi);

					if (orgInfo != null) {
						ConfiguracionCampo newCc = new ConfiguracionCampo(id, cc.getTipoCampo(), newPantalla, oi,
								cc.getOrigenDatos(), cc.getSeccion(), cc.getEtiqueta(), cc.getX(), cc.getY(),
								cc.getAncho(), cc.getAlto(), cc.getDisabled(), cc.getMostrarsiempre(),
								cc.getMostrarcargapac(), cc.getValorcargapac());

						pantallasConfigurablesDao.saveOrUpdate(newCc);
					}
				}
			}
		} catch (Exception excepcion) {
			logger.error("Error al replicar las pantallas configurables", excepcion);
			throw excepcion;
		}
	}
	
	public String obtieneGrupoSeguro(BigDecimal codLinea){
		
		String codGrupoSeguro=pantallasConfigurablesDao.obtieneGrupoSeguro(codLinea);
		return codGrupoSeguro;
	}
}
