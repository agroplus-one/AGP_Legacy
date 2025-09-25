package com.rsi.agp.dao.models.comisiones;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.tables.admin.Colectivo;
import com.rsi.agp.dao.tables.admin.EntidadMediadora;
import com.rsi.agp.dao.tables.admin.SubentidadMediadora;
import com.rsi.agp.dao.tables.comisiones.Cierre;
import com.rsi.agp.dao.tables.comisiones.Fase;
import com.rsi.agp.dao.tables.comisiones.Fichero;

import com.rsi.agp.dao.tables.comisiones.RgaComisionesPendientes;
import com.rsi.agp.dao.tables.comisiones.comisiones.Comision;



public class RgaComisionesPendientesDao  extends BaseDaoHibernate implements IRgaComisionesPendientesDao {

	private static final Log LOGGER = LogFactory.getLog(RgaComisionesPendientesDao.class);
	
	@Override
	public void generarDatosRgaComisionesPendientes(List<Fase> listFicherosCerrados) throws DAOException {
		int tamLista = listFicherosCerrados.size();
		for (int i=0 ; i< tamLista ; i++)
		{
			Fase fase = listFicherosCerrados.get(i);
			generarDatosRgaComisionesByFase(fase);
		}
		
	}		
	
	//Generar datos para insetrar en tb_rga_comisiones_pendientes a partir de las fases cerradas (de sus ficheros)
	private void generarDatosRgaComisionesByFase(Fase fase) throws DAOException
	{
		Set<Fichero> ficheros = new HashSet<Fichero>(0);
		try {
				//cierre
				Cierre cierre = fase.getCierre();
				if (cierre == null)
					cierre = new Cierre();
				// ficheros
				
				ficheros = fase.getFicheros();
				
				Iterator<Fichero> iteratorFichero = ficheros.iterator();
							
				while (iteratorFichero.hasNext()){
					Fichero fichero = iteratorFichero.next();
					List <RgaComisionesPendientes> rgacomisionesListC  = null;		
					
					// hallamos los datos correspondientes al fichero de comisiones
					if (fichero.getTipofichero().equals('C')){
						rgacomisionesListC = new ArrayList<RgaComisionesPendientes>();	
						rgacomisionesListC = hallarDatosRgaFicheroComisiones(fichero, cierre, fase);
					}
					if (rgacomisionesListC != null){
						this.saveOrUpdateList(rgacomisionesListC);
					}
				}

		} catch (Exception ex) {
			LOGGER.error("Se ha producido un error en el acceso a la BBDD: " + ex.getMessage());
			throw new DAOException("Se ha producido un error en el acceso a la BBDD",ex);
		}
		
		
	}
	
	// Halla la lista a guardar de los ficheros de comisiones
	private List <RgaComisionesPendientes> hallarDatosRgaFicheroComisiones(Fichero fichero, Cierre cierre, Fase fase) throws DAOException{
	
		List<RgaComisionesPendientes> rgacomisionesListC  = new ArrayList<RgaComisionesPendientes>();			
		Map<String, SubentidadMediadora> subEntidadMap = new HashMap<String, SubentidadMediadora>();
		try{
			Set<Comision> reciboComisionesSet = fichero.getReciboComisioneses();
			for (Comision reciboComisiones: reciboComisionesSet){			
				RgaComisionesPendientes rgaComisionesPendientes = new RgaComisionesPendientes();
				
				if (fichero.getFechacarga() != null){
					rgaComisionesPendientes.setFeccar(fichero.getFechacarga());
				}
				
				if (cierre != null){
					rgaComisionesPendientes.setCierre(cierre);
				}
				// TMR lo guardamos en un map para no ir siempre a bbdd
				SubentidadMediadora subEntidad = null;
				subEntidad = subEntidadMap.get(reciboComisiones.getColectivoreferencia());
				if (subEntidad == null) {
					subEntidad = this.getSubentidadByCodCol(reciboComisiones.getColectivoreferencia());
					if (subEntidad != null)
						subEntidadMap.put(reciboComisiones.getColectivoreferencia(), subEntidad);
				}
				if (subEntidad != null){
					if (subEntidad.getEntidadMediadora() != null)
					{
						EntidadMediadora entidad = subEntidad.getEntidadMediadora();
						rgaComisionesPendientes.setCodent(entidad.getCodentidad());
					}			
				}
				
				if (reciboComisiones.getColectivoreferencia() != null){
					String refcolectivo = reciboComisiones.getColectivoreferencia();
					if (reciboComisiones.getColectivodc() != null){
						refcolectivo += reciboComisiones.getColectivodc();
					}
					rgaComisionesPendientes.setCodcol(refcolectivo);
				}
				
				
				if (reciboComisiones.getLinea() != null){
					rgaComisionesPendientes.setCodlin(reciboComisiones.getLinea());
				}
				
				if (fase.getPlan() != null){
					rgaComisionesPendientes.setCodpln(fase.getPlan());
				}
							
				
				if (reciboComisiones.getDtGastospendientes() != null){
					rgaComisionesPendientes.setGaspen(reciboComisiones.getDtGastospendientes());
				}
				if (fase.getFase()!= null){
					rgaComisionesPendientes.setFase(fase.getFase());
				}
				
				rgacomisionesListC.add(rgaComisionesPendientes);
			}			
				
		}
		catch (Exception ex) {
					LOGGER.error("Se ha producido un error en el acceso a la BBDD  : "  + ex.getMessage());
					throw new DAOException("Se ha producido un error en el acceso a la BBDD al Fichero de comisiones"+fichero.getNombrefichero() ,ex);
		}
		return rgacomisionesListC;
		
	}
			
	private  SubentidadMediadora getSubentidadByCodCol(String referenciaColectivo) throws DAOException {
		Session session = obtenerSession(); 
		SubentidadMediadora subEntidad =null;
		Colectivo colectivo = new Colectivo();
		try {
			
			Criteria criteria = session.createCriteria(Colectivo.class);
			criteria.add(Restrictions.eq("idcolectivo", referenciaColectivo));
			if ( criteria.list().size() > 0)
			{
				colectivo =(Colectivo) criteria.list().get(0);
				subEntidad = colectivo.getSubentidadMediadora();
				
			}
			
		}
		catch (Exception ex) {
			LOGGER.error("Se ha producido un error en el acceso a la BBDD: " + ex.getMessage());
			throw new DAOException("Se ha producido un error en el acceso a la BBDD",ex);
		}
		return subEntidad;
	}
	
}
