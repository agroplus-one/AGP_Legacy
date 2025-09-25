package com.rsi.agp.dao.models.comisiones;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.tables.admin.SubentidadMediadora;
import com.rsi.agp.dao.tables.admin.SubentidadMediadoraId;
import com.rsi.agp.dao.tables.comisiones.Fase;
import com.rsi.agp.dao.tables.comisiones.InformeMediadores;
import com.rsi.agp.dao.tables.comisiones.InformeMediadoresMeses;
import com.rsi.agp.dao.tables.comisiones.InformeMediadoresSaldos;
import com.rsi.agp.dao.tables.comisiones.RgaComisiones;


public class InformesMediadoresDao  extends BaseDaoHibernate implements IInformesMediadoresDao{

	private static final Log LOGGER = LogFactory.getLog(InformesMediadoresDao.class);
	private BigDecimal codEntidadDesde = new BigDecimal(4000);
	private BigDecimal codEntidadHasta= new BigDecimal(5999);
	@Override
	public void generarDatosInformeMediadores(List<Fase> listFasesCierre)throws DAOException{
	try{
			int tamLista = listFasesCierre.size();
			for (int i=0 ; i< tamLista ; i++){
				
				Fase fase = listFasesCierre.get(i);
			    List<RgaComisiones> rgaList=  new ArrayList<RgaComisiones>();
			 	rgaList = getRgaListByFase(fase);
			 	if (rgaList.size() > 0){
			 		generarDatosMediadores(rgaList);
			 	}
			}
		} catch (Exception ex) {
			LOGGER.error("Se ha producido un error en el acceso a la BBDD: " + ex.getMessage());
			throw new DAOException("Se ha producido un error en el acceso a la BBDD",ex);
		}
		
	}
	
	private void generarDatosMediadores( List<RgaComisiones>  rgaList) throws DAOException{

		logger.info("INICIO generarDatosMediadores");
		 try {
		  Date fechafase;
		  String numFase = rgaList.get(0).getNumfas();
		  fechafase = getFechaByNumFase(numFase);
		  		
		  Calendar cal=Calendar.getInstance();
		  cal.setTime(fechafase);
		  BigDecimal mes = new BigDecimal(cal.get(Calendar.MONTH)+1);
		  int dia = cal.get(Calendar.DAY_OF_MONTH);
		  BigDecimal anyo =  new BigDecimal(cal.get(Calendar.YEAR));
		  int anyoAnterior = Integer.parseInt(anyo.toString()) - 1;
		
		  // guardamos los mediadores
		  logger.info("guardamos registroMediadores");
		  guardarRegistroMediadores(rgaList,anyo,mes,anyoAnterior);
		 
		  // guardamos los saldos si es el ultimo dia del ano
		  if ( Integer.parseInt(mes.toString()) == 12 && dia == 31  ){
				List<InformeMediadores> listinformesMediadores = new ArrayList<InformeMediadores>();
				listinformesMediadores = getDistictInformesMediadoresByDate(anyo);
				for (int i= 0; i< listinformesMediadores.size(); i++ ){
					float saldo = 0;
					InformeMediadores mediadores= new InformeMediadores();
					mediadores = listinformesMediadores.get(i);
					
					if (!isExisteSaldos(mediadores)){
						BigDecimal saldoAnterioAnio= getSumSaldoByMediador(mediadores,anyo.intValue());
						saldo = saldo + saldoAnterioAnio.floatValue();
						
						InformeMediadoresSaldos infSaldos = new InformeMediadoresSaldos();
						infSaldos.setInformeMediadores(mediadores);
						infSaldos.setAnyo(anyo);
						infSaldos.setSaldo(new BigDecimal(saldo));
						this.saveOrUpdate(infSaldos);
						this.evict(infSaldos);
					}
				}
	  		}
		    
		 logger.info(" FIN generarDatosMediadores");
		 } catch (Exception ex) {
			LOGGER.error("Se ha producido un error en el acceso a la BBDD: " + ex);
			throw new DAOException("Se ha producido un error en el acceso a la BBDD",ex);
		}
	}
	// se guardan los datos en las tablas mediadores, meses y saldo (para luego generar informes)
	private void  guardarRegistroMediadores( List<RgaComisiones>  rgaList, BigDecimal anyo, BigDecimal mes,int anyoAnterior)throws DAOException{
		
		List<InformeMediadores> listMediadores = new ArrayList<InformeMediadores>();
		List<InformeMediadoresMeses> listMediadoresMeses = new ArrayList<InformeMediadoresMeses>();
		double Devengo = 0; 
		double Pago = 0;
		double retencion = 0;
		double saldo;
		String nuevo = "1";
		RgaComisiones rga = new RgaComisiones();
		rga = rgaList.get(0);
		BigDecimal subentidadAnterior  = rga.getCodsubmed();
		BigDecimal entidadAnterior = rga.getCodentmed();
	 	InformeMediadores informesMediadores = new InformeMediadores();
		InformeMediadores informesMediadoresanterior = new InformeMediadores();
		informesMediadoresanterior.setNuevo(nuevo.charAt(0));
		SubentidadMediadora subenti = new SubentidadMediadora();
		SubentidadMediadoraId id = new SubentidadMediadoraId();
		id.setCodentidad(entidadAnterior);
		id.setCodsubentidad(subentidadAnterior);
		subenti.setId(id);
		informesMediadoresanterior.setSubentidadMediadora(subenti);
		InformeMediadores informesMediadoresExistentes = existeEntidadSubentidad(entidadAnterior,subentidadAnterior);
		if (informesMediadoresExistentes == null)
			listMediadores.add(informesMediadoresanterior);
		else
			informesMediadoresanterior = informesMediadoresExistentes;
		BigDecimal  ggeMediador = rga.getGassubsum();
		BigDecimal cceMediador = rga.getComsubsum();
		Devengo = redondear((Devengo + ggeMediador.doubleValue() + cceMediador.doubleValue()),2);
		retencion = redondear((retencion + (15 * Devengo / 100)),2);
		try{
			if (rgaList.size() > 1)
			{
				for (int i=1 ; i< rgaList.size() ; i++)
				{
					rga = new RgaComisiones();
					rga = rgaList.get(i);
					BigDecimal entidadSiguiente = rga.getCodentmed();
					BigDecimal subentidadSiguiente  = rga.getCodsubmed();
					ggeMediador = rga.getGassubsum();
					cceMediador = rga.getComsubsum();
				 // se comprueba que existen registros en informesMediadores para la subentidad y entidad	
				   if ((entidadAnterior.intValue() != entidadSiguiente.intValue()) &&  (subentidadAnterior.intValue() != subentidadSiguiente.intValue())) {
					    informesMediadores = new InformeMediadores();		
					    informesMediadores.setNuevo(nuevo.charAt(0));
							subenti = new SubentidadMediadora();
							id = new SubentidadMediadoraId();
							id.setCodentidad(entidadSiguiente);
							id.setCodsubentidad(subentidadSiguiente);
							subenti = getSubEntidadMediadoraById(id);
							subenti.setId(id);
							informesMediadores.setSubentidadMediadora(subenti);
							informesMediadoresExistentes  = new InformeMediadores();
							informesMediadoresExistentes = existeEntidadSubentidad(entidadSiguiente,subentidadSiguiente);
							if (informesMediadoresExistentes == null)
								listMediadores.add(informesMediadores);
							else{
								informesMediadores = informesMediadoresExistentes;
							}
							saldo = calcularSaldoAnterior(new BigDecimal(anyoAnterior),informesMediadores) ;
							if (saldo != 0)
									Pago = Devengo + saldo ;
							else
									Pago = Devengo;	
							listMediadoresMeses.add(guardarRegistroMes(informesMediadoresanterior,Devengo,retencion,Pago,anyo,mes));
							informesMediadoresanterior = informesMediadores;
							Devengo = 0;
							retencion = 0;
							entidadAnterior = entidadSiguiente;
							subentidadAnterior = subentidadSiguiente;
							
				   }		
					else
					{ 
						Devengo = redondear((Devengo + ggeMediador.doubleValue() + cceMediador.doubleValue()),2);
						retencion = redondear((retencion + (15 * Devengo / 100)),2);
					}
				}
				listMediadoresMeses.add(guardarRegistroMes(informesMediadoresanterior,Devengo,retencion,Pago,anyo,mes));

			}
			if (listMediadores.size() > 0){
				this.saveOrUpdateList(listMediadores);
				this.evict(listMediadores);
			}
			
			for ( int j = 0 ; j < listMediadoresMeses.size() ; j++)
			{
				InformeMediadoresMeses infMes = new InformeMediadoresMeses();
				InformeMediadoresMeses infMesExistentes = new InformeMediadoresMeses();
				infMes = listMediadoresMeses.get(j);
				infMesExistentes = devengoExistToMediador(anyo,mes,infMes.getInformeMediadores().getSubentidadMediadora().getId());
				
				if (infMesExistentes.getId() == null){
					saveOrUpdate(infMes);
					this.getSession().flush();
					this.evict(infMes);
				}
			}
		}
		catch (Exception ex) {
			LOGGER.error("Se ha producido un error en el acceso a la BBDD: " + ex.getMessage());
			throw new DAOException("Se ha producido un error en el acceso a la BBDD",ex);
		}
	
	}
	
	private InformeMediadoresMeses guardarRegistroMes(InformeMediadores informeMediador,double devengo, double retencion,double Pago, BigDecimal anyo, BigDecimal mes)throws DAOException{
		
		InformeMediadoresMeses infMes = new InformeMediadoresMeses();
		
		try{	
			String reten = Double.toString(retencion);
			String deven = Double.toString(devengo);
			String pag = Double.toString(Pago);
			infMes.setInformeMediadores(informeMediador);
			infMes.setAnyo(anyo);
			infMes.setMes(mes);	
			
			infMes.setDevengo(new BigDecimal(deven));
			infMes.setPago(new BigDecimal(pag));
						//se comprueba que el mediadora sea una persona fisica y no una entidad para aplicar retencion
			if (informeMediador.getSubentidadMediadora().getTipoidentificacion() != null ){
					if (informeMediador.getSubentidadMediadora().getTipoidentificacion().equals("NIF"))
							infMes.setRetencion(new BigDecimal(reten));
			}	
			
		}
		catch (Exception ex) {
			LOGGER.error("Se ha producido un error en el acceso a la BBDD: " + ex.getMessage());
			throw new DAOException("Se ha producido un error en el acceso a la BBDD",ex);
		}
		return infMes;
	}
	
	
	private List<InformeMediadores> getDistictInformesMediadoresByDate(BigDecimal anyo)throws DAOException{
		List<InformeMediadores> informesMediadores= new ArrayList<InformeMediadores>();
		Session session = obtenerSession(); 
		try{
			Criteria criteria = session.createCriteria(InformeMediadores.class);
			criteria.createAlias("subentidadMediadora", "subentidadMediadora");
			criteria.createAlias("subentidadMediadora.id", "subentidadMediadora.id");
			criteria.createAlias("subentidadMediadora.id", "subentidadMediadora.id");
			criteria.createAlias("informeMediadoresMeseses","informeMediadoresMeseses");
			criteria.createAlias("informeMediadoresMeseses.anyo","informeMediadoresMeseses.anyo");
			criteria.add(Restrictions.eq("informeMediadoresMeseses.anyo", anyo));
			criteria.addOrder(Order.asc("subentidadMediadora.id.codentidad"));
			criteria.addOrder(Order.asc("subentidadMediadora.id.codsubentidad"));
			
			if ( criteria.list().size() > 0)
			{
				informesMediadores =  criteria.list();
				
			}
		}
		catch (Exception ex) {
			LOGGER.error("Se ha producido un error en el acceso a la BBDD: " + ex.getMessage());
			throw new DAOException("Se ha producido un error en el acceso a la BBDD",ex);
		}
		return informesMediadores;
	}

	private List<RgaComisiones> getRgaListByFase(Fase fase)throws DAOException
	{
		
		Session session = obtenerSession(); 
		 List<RgaComisiones>  rgaList= new  ArrayList<RgaComisiones>();
		try {
			Criteria criteria = session.createCriteria(RgaComisiones.class);
			criteria.add(Restrictions.disjunction().add(Restrictions.between("codentmed",codEntidadDesde,codEntidadHasta)));
			criteria.add(Restrictions.eq("numfas", fase.getFase()));
			criteria.addOrder(Order.asc("numfas"));
			criteria.addOrder(Order.asc("codentmed"));
			criteria.addOrder(Order.asc("codsubmed"));
			if ( criteria.list().size() > 0)
			{
				rgaList =  criteria.list();
				
			}
		}
		catch (Exception ex) {
			LOGGER.error("Se ha producido un error en el acceso a la BBDD: " + ex.getMessage());
			throw new DAOException("Se ha producido un error en el acceso a la BBDD",ex);
		}
		return rgaList ;
	}
	
	private double calcularSaldoAnterior(BigDecimal anyo,InformeMediadores informesMediadores )throws DAOException
	{
		Session session = obtenerSession(); 
		BigDecimal saldo = new BigDecimal(0);
		try {
			Criteria criteria = session.createCriteria(InformeMediadoresSaldos.class);
			criteria.createAlias("informeMediadores", "informeMediadores");
			criteria.add(Restrictions.eq("anyo", anyo));
			criteria.add(Restrictions.eq("informeMediadores.id", informesMediadores.getId()));
			if ( criteria.list().size() > 0)
			{
				saldo =  (BigDecimal)criteria.list().get(0);
	
			}
		}
		catch (Exception ex) {
			LOGGER.error("Se ha producido un error en el acceso a la BBDD: " + ex.getMessage());
			throw new DAOException("Se ha producido un error en el acceso a la BBDD",ex);
		}
		return Double.parseDouble(saldo.toString()) ;
	}
	// comprueba si existe ya el registro para insertarlo o modificarlo
	private InformeMediadoresMeses devengoExistToMediador(BigDecimal anyo, BigDecimal mes, SubentidadMediadoraId idMediador)throws DAOException
	{
		
		Session session = obtenerSession(); 
		InformeMediadoresMeses informe = new InformeMediadoresMeses();
		try {
			Criteria criteria = session.createCriteria(InformeMediadoresMeses.class);
			criteria.add(Restrictions.eq("mes",(mes)));
			criteria.add(Restrictions.eq("anyo",(anyo)));
			criteria.createAlias("informeMediadores", "informeMediadores");
			criteria.createAlias("informeMediadores.subentidadMediadora", "subentidadMediadora");
			criteria.createAlias("subentidadMediadora.id", "subentidadMediadora.id");
			criteria.add(Restrictions.eq("subentidadMediadora.id.codentidad",idMediador.getCodentidad()));
			criteria.add(Restrictions.eq("subentidadMediadora.id.codsubentidad",idMediador.getCodsubentidad() ));
			criteria.addOrder(Order.asc("informeMediadores.id"));
			if ( criteria.list().size() > 0)
			{
				informe =  (InformeMediadoresMeses) criteria.list().get(0);
			}
		}
		catch (Exception ex) {
			LOGGER.error("Se ha producido un error en el acceso a la BBDD: " + ex.getMessage());
			throw new DAOException("Se ha producido un error en el acceso a la BBDD",ex);
		}
		return informe;
	}
	
	private InformeMediadores existeEntidadSubentidad(BigDecimal entidad,BigDecimal subentidad )throws DAOException{
		InformeMediadores informeMediadores = null ;
		Session session = obtenerSession(); 
		try {
			Criteria criteria = session.createCriteria(InformeMediadores.class);
			criteria.createAlias("subentidadMediadora", "subentidadMediadora");
			criteria.createAlias("subentidadMediadora.id", "subentidadMediadora.id");
			criteria.add(Restrictions.eq("subentidadMediadora.id.codentidad",entidad));
			criteria.add(Restrictions.eq("subentidadMediadora.id.codsubentidad",subentidad ));
			criteria.addOrder(Order.asc("id"));
			if ( criteria.list().size() > 0)
			{
				informeMediadores = (InformeMediadores) criteria.list().get(0);
			}
		}
		catch (Exception ex) {
			LOGGER.error("Se ha producido un error en el acceso a la BBDD: " + ex.getMessage());
			throw new DAOException("Se ha producido un error en el acceso a la BBDD",ex);
		}
		return informeMediadores;
	}
	
	// devuelve la suma de los pagos de un determinado anyo 
	private BigDecimal getSumSaldoByMediador(InformeMediadores informeMediadores,  int anyo)throws DAOException{
		BigDecimal saldo = new BigDecimal(0) ;
		Session session = obtenerSession(); 
		try {

			String sql= "select sum(pago) from o02agpe0.tb_inf_mediadores_meses where anyo ="+anyo+"  and idinfmediador="+informeMediadores.getId();
			List list = session.createSQLQuery(sql).list();
			saldo = (BigDecimal)list.get(0);
			
		}catch (Exception ex) {
			LOGGER.error("Se ha producido un error en el acceso a la BBDD: " + ex.getMessage());
			throw new DAOException("Se ha producido un error en el acceso a la BBDD",ex);
		}
		return saldo;
	}
	//comprubea si existe saldos para el año anterior  
	private boolean isExisteSaldos(InformeMediadores mediadores)throws DAOException{
		Session session = obtenerSession(); 
		boolean existe = true;
		try {
			Criteria criteria = session.createCriteria(InformeMediadoresSaldos.class);
			criteria.createAlias("informeMediadores", "informeMediadores");
			criteria.add(Restrictions.eq("informeMediadores.id",mediadores.getId()));
			if ( criteria.list().size() > 0)
			{
				existe = true;
			}
			else
			{
				existe = false;
			}
			
		}catch (Exception ex) {
			LOGGER.error("Se ha producido un error en el acceso a la BBDD: " + ex.getMessage());
			throw new DAOException("Se ha producido un error en el acceso a la BBDD",ex);
		}
		return existe;
	
	}
	// calcula la fecha de emision de la fase
	private Date getFechaByNumFase(String numFase)throws DAOException{
	
		Fase fase = new Fase();
		Date fecha =null;
		Session session = obtenerSession(); 
		try {
			Criteria criteria = session.createCriteria(Fase.class);
			
			criteria.add(Restrictions.eq("fase",numFase));
			if ( criteria.list().size() > 0)
			{
				fase = (Fase) criteria.list().get(0);
				fecha = fase.getFechaemision();
			}
		}
		catch (Exception ex) {
			LOGGER.error("Se ha producido un error en el acceso a la BBDD: " + ex.getMessage());
			throw new DAOException("Se ha producido un error en el acceso a la BBDD",ex);
		}
		return fecha;
		
	}
	
	
	
	private SubentidadMediadora getSubEntidadMediadoraById(SubentidadMediadoraId id)throws DAOException{
		Session session = obtenerSession(); 
		String tipoIdentificacion ;
		SubentidadMediadora sub = new SubentidadMediadora();
		String Sql = "select tipoidentificacion from o02agpe0.tb_subentidades_mediadoras where codentidad = "+id.getCodentidad()+" and  codsubentidad= "+id.getCodsubentidad();
		List list = session.createSQLQuery(Sql).list();
		tipoIdentificacion = (String)list.get(0);
		sub.setId(id);
		sub.setTipoidentificacion(tipoIdentificacion);
	 
		return sub;
		
	}
	
	public double redondear( double numero, int decimales ) 
	{
	    return Math.round(numero*Math.pow(10,decimales))/Math.pow(10,decimales);
	}  

	

	


}
