package com.rsi.agp.dao.models.comisiones;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
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
import com.rsi.agp.dao.tables.admin.Asegurado;
import com.rsi.agp.dao.tables.admin.Colectivo;
import com.rsi.agp.dao.tables.admin.Entidad;
import com.rsi.agp.dao.tables.admin.EntidadMediadora;
import com.rsi.agp.dao.tables.admin.SubentidadMediadora;
import com.rsi.agp.dao.tables.admin.SubentidadMediadoraId;
import com.rsi.agp.dao.tables.comisiones.Cierre;
import com.rsi.agp.dao.tables.comisiones.CultivosEntidades;
import com.rsi.agp.dao.tables.comisiones.CultivosSubentidades;
import com.rsi.agp.dao.tables.comisiones.Fase;
import com.rsi.agp.dao.tables.comisiones.Fichero;
import com.rsi.agp.dao.tables.comisiones.GGEEntidades;
import com.rsi.agp.dao.tables.comisiones.GGESubentidades;
import com.rsi.agp.dao.tables.comisiones.Reglamento;
import com.rsi.agp.dao.tables.comisiones.RgaComisiones;
import com.rsi.agp.dao.tables.comisiones.comisiones.Comision;
import com.rsi.agp.dao.tables.comisiones.comisiones.ComisionAplicacion;
import com.rsi.agp.dao.tables.comisiones.impagados.ReciboImpagado;
import com.rsi.agp.dao.tables.comisiones.reglamento.ReglamentoProduccionEmitida;
import com.rsi.agp.dao.tables.comisiones.reglamento.ReglamentoProduccionEmitidaSituacion;
import com.rsi.agp.dao.tables.poliza.Linea;
import com.rsi.agp.dao.tables.poliza.Poliza;



public class RgaComisionesDao  extends BaseDaoHibernate implements IRgaComisionesDao {

	private static final Log LOGGER = LogFactory.getLog(RgaComisionesDao.class);
	
	
	public void generarDatosRgaComisiones(	List<Fase> listFasesCierre)throws DAOException{
		
		int tamLista = listFasesCierre.size();
		for (int i=0 ; i< tamLista ; i++)
		{
			Fase fase = listFasesCierre.get(i);
			generarDatosRgaComisionesByFase(fase);
		}
	}
	/**
	 * generar ddatos para insetrar en tb_rga_comisiones a partir de las fases cerradas (de sus ficheros)
	 * @param fase
	 * @return
	 * @throws DAOException
	 */
	public void generarDatosRgaComisionesByFase(Fase fase) throws DAOException
	{
		List <RgaComisiones> rgacomisionesListC  =  new ArrayList<RgaComisiones>();	
		List <RgaComisiones> rgacomisionesListR  =  new ArrayList<RgaComisiones>();	
		List <RgaComisiones> rgacomisionesListI  =  new ArrayList<RgaComisiones>();
		List <RgaComisiones> rgacomisionesList =  new ArrayList<RgaComisiones>();	
		Set<Fichero> ficheros = new HashSet<Fichero>(0);
		Linea lin999 = new Linea();
		String fecha ="";
		try {
				//cierre
				Cierre cierre = fase.getCierre();
				if (cierre == null)
					cierre = new Cierre();
				// ficheros
				
				ficheros = fase.getFicheros();
				
				Iterator<Fichero> iteratorFichero = ficheros.iterator();
				/* por cada fase existe un fichero de comisiones uno de reglamento y 
				uno de emitidos(del que no necesitamos datos) y puede o no existir uno de impagados	*/		
				while (iteratorFichero.hasNext()){
					Fichero fichero = iteratorFichero.next();
					if ( fichero.getFechacarga() != null)
					{
						GregorianCalendar gc = new GregorianCalendar();
						gc.setTime(fichero.getFechacarga() );
						gc.setGregorianChange(fichero.getFechacarga());
						int dia = gc.get(Calendar.DAY_OF_MONTH);
						int mes = gc.get(Calendar.MONTH);
						int anio= gc.get(Calendar.YEAR);
						String anioS = Integer.toString(anio).substring(2,4);	
						fecha = Integer.toString(dia)+"/"+Integer.toString(mes+1)+"/"+anioS;	
					}
					// hallamos los datos correspondientes al fichero de comisiones
					if (fichero.getTipofichero().equals('C')){
						
						// Obtenemos la linea generica una vez por cada fichero
						lin999 = getLineaByCodLinea(new BigDecimal(999),fase.getPlan());
						//Guardamos en un map todas las lineas del plan del fichero para posteriormente acceder a ellas mas rapido
						HashMap<BigDecimal ,Long> lineasPlan = getLineasPlan(fase.getPlan());
						//Hallamos la ggentidad del plan del fichero
						GGEEntidades ggeEntidad = new GGEEntidades();
						ggeEntidad = getEntidadByPlan(fase.getPlan().longValue());
						
						rgacomisionesListC=   hallarDatosRgaFicheroComisiones(fichero, cierre, fase,fecha,lin999,lineasPlan,ggeEntidad);	
					}
					else if (fichero.getTipofichero().equals('R'))
					{
						
						Reglamento rg = getReglamentoByPlan(fase.getPlan().longValue());
						rgacomisionesListR = hallarDatosRgaFicheroReglamento(fichero, cierre, fase,fecha,rg);	
					}
					else if (fichero.getTipofichero().equals('I'))
					{
						rgacomisionesListI = hallarDatosRgaFicheroImpagados(fichero, cierre, fase);
					}
				
				}
				rgacomisionesList = hallarDatosRgaComisiones(rgacomisionesListR,rgacomisionesListC,rgacomisionesListI);
				this.saveOrUpdateList(rgacomisionesList);	
				
		} catch (Exception ex) {
			LOGGER.error("Se ha producido un error en el acceso a la BBDD: " + ex.getMessage());
			throw new DAOException("Se ha producido un error en el acceso a la BBDD",ex);
		}
		
		
	}
	
	/**
	 *  halla la lista a guardar de los ficheros de comisiones
	 * @param fichero, cierre, fase y fecha de carga
	 * @return listado de obj RgaComisiones
	 * @throws DAOException
	 */
	private List <RgaComisiones> hallarDatosRgaFicheroComisiones(Fichero fichero, Cierre cierre, 
			Fase fase, String fechacarga,Linea lin999,HashMap<BigDecimal ,Long> lineasPlan,GGEEntidades ggeEntidad) throws DAOException{
		List <RgaComisiones> rgacomisionesListC  = new ArrayList<RgaComisiones>();	
		RgaComisiones rgacomisiones = new RgaComisiones();
		String nombre ="" , apellido1 ="", apellido2="";
		
		float globalEntidad;
		float globalEntidadCC;
		float importeCCMediador,importeCCEntidad,importeGGEMediador,importeGGEEntidad;
		Map<String, SubentidadMediadora> subEntidadMap = new HashMap<String, SubentidadMediadora>();
		
		//long tiempoInicio = System.currentTimeMillis();
		
		try{
			
			Set<Comision> reciboComisionesSet= fichero.getReciboComisioneses();
			// solo tenemos un recibo comisiones por fase
			Iterator<Comision> itrc = reciboComisionesSet.iterator();
			while (itrc.hasNext())
			{
				Comision reciboComisiones = new Comision();
				reciboComisiones = itrc.next();
				Set<ComisionAplicacion> reciboComisionesAplicacions	= reciboComisiones.getComisionAplicacions();
				Iterator<ComisionAplicacion> iterator = reciboComisionesAplicacions.iterator();
				while( iterator.hasNext()){
					ComisionAplicacion comisionaplicacion = new ComisionAplicacion();
					comisionaplicacion = iterator.next();
					//datos aplicacion
					
					rgacomisiones = new RgaComisiones();
					// datos propios del fichero
					rgacomisiones.setCierre(cierre);
					rgacomisiones.setNumfas(fase.getFase());
					rgacomisiones.setCodpln(fase.getPlan());	
					rgacomisiones.setFeccar(fechacarga);
					// datos colectivo fichero comisiones
				
					// LINEA
					if (reciboComisiones.getLinea() != null)
						rgacomisiones.setCodlin(reciboComisiones.getLinea());
					if (reciboComisiones.getColectivoreferencia() != null)
					{
						String refcolectivo = reciboComisiones.getColectivoreferencia();
						
						rgacomisiones.setCodcol(refcolectivo);
						
						BigDecimal entidad = getEntidadByRef(refcolectivo);
						if (entidad != null)
								rgacomisiones.setCodent(entidad);
						
					}
					 if(comisionaplicacion.getReferencia() != null)
					 {
						 
						 
						 Poliza poliza = getPolizaByReferencia(comisionaplicacion.getReferencia());
						 if ( poliza != null)
						 {
							 rgacomisiones.setTippol(poliza.getTipoReferencia());
							 rgacomisiones.setCodlin(poliza.getLinea().getCodlinea());
							 rgacomisiones.setRefplz(comisionaplicacion.getReferencia());
							 if(poliza.getAsegurado()!= null)
							 {
						
								Asegurado asegurado = poliza.getAsegurado();
								
								if (asegurado.getTipoidentificacion().equals("CIF")){ 
									if (asegurado.getRazonsocial()!= null){
										rgacomisiones.setNomasg(asegurado.getRazonsocial());
									}
								}else{ //Puede ser NIF o NIE
									if (asegurado.getNombre() != null ) 
										nombre = asegurado.getNombre() ;
									if ( asegurado.getApellido1() != null)
										apellido1 = asegurado.getApellido1();
									if (asegurado.getApellido2() != null)
										apellido2 = asegurado.getApellido2();	
									rgacomisiones.setNomasg(apellido1+ " " +apellido2 + " " +nombre);
								}
								if (asegurado.getNifcif() != null)
									rgacomisiones.setNifasg(asegurado.getNifcif() );
							}
							 if (poliza.getOficina() != null){
								 BigDecimal codoficina = new BigDecimal(poliza.getOficina());
								 rgacomisiones.setCodofi(codoficina);
							 } 
						 }
					}
					float pctMediador = 0;
					float pctcMediador = 0;
					float pctGlobalEntidadesGGE = 0;
					float pctGlobalEntidadesCCE=0;
					SubentidadMediadora subEntidad = null;
					
					// TMR lo guardamos en un map para no ir siempre a bbdd
					subEntidad = subEntidadMap.get(reciboComisiones.getColectivoreferencia());
					if (subEntidad == null) {
						subEntidad = (SubentidadMediadora) getDatosCol(reciboComisiones.getColectivoreferencia());
						if (subEntidad != null)
							subEntidadMap.put(reciboComisiones.getColectivoreferencia(), subEntidad);
					}
					
					if (subEntidad != null  )
					{
						SubentidadMediadoraId subid = subEntidad.getId();
					
							if (subEntidad.getEntidadMediadora() != null)
							{
								EntidadMediadora entidadMediadora = subEntidad.getEntidadMediadora();
								if (entidadMediadora.getCodentidad() != null)
									rgacomisiones.setCodentmed(entidadMediadora.getCodentidad() );
								
								Linea lin = new Linea();
								
								//Long planGGe = fase.getPlan().longValue();
								CultivosEntidades CCEentidad = null;
								CultivosSubentidades CCEsubentidad = null;
								
								BigDecimal importeCCRga = new BigDecimal(0);
								
								lin.setLineaseguroid(lineasPlan.get(rgacomisiones.getCodlin()));
								
								//lo saco fuera para que se haga solo una vez
								//lin = getLineaByCodLinea(rgacomisiones.getCodlin(),rgacomisiones.getCodpln()) ;
								//lin999 = getLineaByCodLinea(new BigDecimal("999"),rgacomisiones.getCodpln()) ;
								
								CCEsubentidad = getCCBySubEntidad(subEntidad,lin);
								if (CCEsubentidad==null)
									CCEsubentidad = getCCBySubEntidad(subEntidad,lin999);
		
								CCEentidad  = getCCEntidadByLinea(lin.getLineaseguroid());
								if(CCEentidad==null){
									CCEentidad  = getCCEntidadByLinea(lin999.getLineaseguroid());
								}
							
								
								if 	(CCEentidad != null && CCEentidad.getPctgeneralentidad() != null){
									pctGlobalEntidadesCCE = CCEentidad.getPctgeneralentidad().floatValue();		
								}
								if (CCEsubentidad != null && CCEsubentidad.getPctmediador() != null)
									pctcMediador = CCEsubentidad.getPctmediador().floatValue();
								
								if (comisionaplicacion.getDnComisiones() != null)
								{
									//[Gastos Comisiones] -(Nuevos)
									BigDecimal  gastosComisionesNuevos = comisionaplicacion.getDnComisiones();
									
									//[Global Entidad CC] =  [Gastos Comisiones]  * [%Global_Entidades_CC] / 100
									globalEntidadCC = (gastosComisionesNuevos.multiply(new BigDecimal(pctGlobalEntidadesCCE))).divide(new BigDecimal(100)).floatValue();
									
									//[Importe CC Mediador] = [Global Entidad CC] * [%Mediador CC] / 100
									importeCCMediador = (globalEntidadCC * pctcMediador)/100;
									
									//[Importe CC Entidad]  = [Global Entidad CC] – [Importe CC Mediador]
									importeCCEntidad = globalEntidadCC - importeCCMediador;
									
									//[Importe CC RGA] = [Gastos Comisiones] – [Global Entidad CC]
									importeCCRga = gastosComisionesNuevos.subtract(new BigDecimal (globalEntidadCC));

									rgacomisiones.setCommentnue(new BigDecimal(importeCCEntidad));
									rgacomisiones.setCommendnue(new BigDecimal(importeCCMediador));
									rgacomisiones.setComrganue(importeCCRga);			
								}
								if (comisionaplicacion.getDrComisiones() != null)
								{
									//[Gastos Comisiones] -(REG)
									BigDecimal  gastosComisionesREG = comisionaplicacion.getDrComisiones();
									
									//[Global Entidad CC] =  [Gastos Comisiones]  * [%Global_Entidades_CC] / 100
									globalEntidadCC = (gastosComisionesREG.multiply(new BigDecimal(pctGlobalEntidadesCCE))).divide(new BigDecimal(100)).floatValue();
									
									//[Importe CC Mediador] = [Global Entidad CC] * [%Mediador CC] / 100
									importeCCMediador = (globalEntidadCC * pctcMediador)/100;
									
									//[Importe CC Entidad]  = [Global Entidad CC] – [Importe CC Mediador]
									importeCCEntidad = globalEntidadCC - importeCCMediador;
									
									//[Importe CC RGA] = [Gastos Comisiones] – [Global Entidad CC]
									importeCCRga = gastosComisionesREG.subtract(new BigDecimal (globalEntidadCC));
									
									rgacomisiones.setComentreg(new BigDecimal(importeCCEntidad));
									rgacomisiones.setCommendreg(new BigDecimal(importeCCMediador));
									rgacomisiones.setComrgareg(importeCCRga);
								}
								if (comisionaplicacion.getDtComisiones() != null)
								{	
									//[Gastos Comisiones] -(Totales)
									BigDecimal  gastosComisionesTOT = comisionaplicacion.getDtComisiones();
									
									//[Global Entidad CC] =  [Gastos Comisiones]  * [%Global_Entidades_CC] / 100
									globalEntidadCC = (gastosComisionesTOT.multiply(new BigDecimal(pctGlobalEntidadesCCE))).divide(new BigDecimal(100)).floatValue();
									
									//[Importe CC Mediador] = [Global Entidad CC] * [%Mediador CC] / 100
									importeCCMediador = (globalEntidadCC * pctcMediador)/100;
									
									//[Importe CC Entidad]  = [Global Entidad CC] – [Importe CC Mediador]
									importeCCEntidad = globalEntidadCC - importeCCMediador;
									
									//[Importe CC RGA] = [Gastos Comisiones] – [Global Entidad CC]
									importeCCRga = gastosComisionesTOT.subtract(new BigDecimal (globalEntidadCC));
									
									rgacomisiones.setComentsum(new BigDecimal(importeCCEntidad));
									rgacomisiones.setComsubsum(new BigDecimal(importeCCMediador));
									rgacomisiones.setComrgasum(importeCCRga);
								}
								if (comisionaplicacion.getDnTotal() != null)
									rgacomisiones.setTotnue(comisionaplicacion.getDnTotal());
								
								if (comisionaplicacion.getDrTotal() != null)
									rgacomisiones.setTotreg(comisionaplicacion.getDrTotal()); 
								
								if (comisionaplicacion.getDtPrimabasecalculo()!= null){
									BigDecimal primaBase = comisionaplicacion.getDtPrimabasecalculo() ;
									rgacomisiones.setCostot(primaBase);
								}
								
								//DATOS GGE
								GGESubentidades ggesubentidad = new GGESubentidades();
								ggesubentidad = getSubentidadBySubEntidad(subEntidad);
								
								if (ggeEntidad.getPctentidades() != null){
									pctGlobalEntidadesGGE = ggeEntidad.getPctentidades().floatValue();		
								}
								if (ggesubentidad.getPctmediador() != null)
									pctMediador = ggesubentidad.getPctmediador().floatValue();
								
								//Datos totales
								if (comisionaplicacion.getDtPrimabasecalculo() != null)
								{	
									//PrimaBaseCalculo
									BigDecimal primaBaseCalculoSum = comisionaplicacion.getDtPrimabasecalculo();
									//GastosExternosEntidad
									BigDecimal gastosExternosEntidadTOT = comisionaplicacion.getDtGastosextentidad();
									
									//Calculamos [Global Entidad] =  [Prima Base Cálculo]  * [%Global_Entidades_GGE] / 100
									globalEntidad = (primaBaseCalculoSum.multiply(new BigDecimal(pctGlobalEntidadesGGE))).divide(new BigDecimal(100)).floatValue();
									
									//calculamos [Importe GGE Mediador] = [Global Entidad] * [%Mediador GGE] / 100
									importeGGEMediador = (globalEntidad * pctMediador)/100;
									
									//calculamos [Importe GGE Entidad]  = [Global Entidad] – [Importe GGE Mediador]
									importeGGEEntidad = globalEntidad - importeGGEMediador;
									
									//calculamos [Importe GGE RGA] = [Gastos Externos Entidad] – [Global Entidad]
									float importeGGERga = gastosExternosEntidadTOT.subtract(new BigDecimal (globalEntidad)).floatValue();
									
									//guardamos los importes en sus campos
									rgacomisiones.setPrisum(primaBaseCalculoSum);
									rgacomisiones.setGassubsum(new BigDecimal(importeGGEMediador));
									rgacomisiones.setGasentsum(new BigDecimal(importeGGEEntidad));
									rgacomisiones.setGasrgasum(new BigDecimal(importeGGERga));
								}
								//Datos nuevos
								if (comisionaplicacion.getDnPrimabasecalculo() != null  )
								{	
									//PrimaBaseCalculoNuevo
									BigDecimal primaBaseCalculoNuevo = comisionaplicacion.getDnPrimabasecalculo();
									//GastosExternosEntidad
									BigDecimal gastosExternosEntidadNUEVOS = comisionaplicacion.getDnGastosextentidad();
									
									//Calculamos [Global Entidad] =  [Prima Base Cálculo]  * [%Global_Entidades_GGE] / 100
									globalEntidad = (primaBaseCalculoNuevo.multiply(new BigDecimal(pctGlobalEntidadesGGE))).divide(new BigDecimal(100)).floatValue();
									
									//calculamos [Importe GGE Mediador] = [Global Entidad] * [%Mediador GGE] / 100
									importeGGEMediador = (globalEntidad * pctMediador) / 100;
									
									//calculamos [Importe GGE Entidad]  = [Global Entidad] – [Importe GGE Mediador]
									importeGGEEntidad = globalEntidad - importeGGEMediador;
									
									//calculamos [Importe GGE RGA] = [Gastos Externos Entidad] – [Global Entidad]
									float importeGGERga = gastosExternosEntidadNUEVOS.subtract(new BigDecimal (globalEntidad)).floatValue();
									
									//guardamos los importes en sus campos
									rgacomisiones.setPrinue(primaBaseCalculoNuevo);
									rgacomisiones.setGassubnue(new BigDecimal(importeGGEMediador));
									rgacomisiones.setGasentnue(new BigDecimal(importeGGEEntidad));
									rgacomisiones.setGasrganue(new BigDecimal(importeGGERga));
								
								}
								//Datos REG
								if (comisionaplicacion.getDrPrimabasecalculo() != null)
								{
									
									//PrimaBaseCalculoNuevo
									BigDecimal primaBaseCalculoReg = comisionaplicacion.getDrPrimabasecalculo();
									//GastosExternosEntidad
									BigDecimal gastosExternosEntidadREG = comisionaplicacion.getDrGastosextentidad();
									
									//Calculamos [Global Entidad] =  [Prima Base Cálculo]  * [%Global_Entidades_GGE] / 100
									globalEntidad = (primaBaseCalculoReg.multiply(new BigDecimal(pctGlobalEntidadesGGE))).divide(new BigDecimal(100)).floatValue();
									
									//calculamos [Importe GGE Mediador] = [Global Entidad] * [%Mediador GGE] / 100
									importeGGEMediador =(globalEntidad * pctMediador) / 100;
									
									//calculamos [Importe GGE Entidad]  = [Global Entidad] – [Importe GGE Mediador]
									importeGGEEntidad = globalEntidad - importeGGEMediador;
									
									//calculamos [Importe GGE RGA] = [Gastos Externos Entidad] – [Global Entidad]
									float importeGGERga = gastosExternosEntidadREG.subtract(new BigDecimal (globalEntidad)).floatValue();
									
									rgacomisiones.setPrireg(primaBaseCalculoReg);	
									rgacomisiones.setGassubreg( new BigDecimal(importeGGEMediador));
									rgacomisiones.setGasentreg(new BigDecimal(importeGGEEntidad));
									rgacomisiones.setGasrgareg(new BigDecimal(importeGGERga));
								}
							// fin calculos gge
							
							}
							if (subid != null)
								rgacomisiones.setCodsubmed(subid.getCodsubentidad());
							
							if (subEntidad.getEntidad() != null)
							{
								Entidad entidad = subEntidad.getEntidad();
								rgacomisiones.setCodent(entidad.getCodentidad());
							}
							if (comisionaplicacion.getDnGastosextentidad() != null)
								rgacomisiones.setGasnue(comisionaplicacion.getDnGastosextentidad() );
							if (comisionaplicacion.getDrGastosextentidad() != null)
								rgacomisiones.setGasreg(comisionaplicacion.getDrGastosextentidad());
							if (comisionaplicacion.getDtGastosextentidad() != null)
								rgacomisiones.setGassum(comisionaplicacion.getDtGastosextentidad());
							
							if (comisionaplicacion.getTiporeferencia()!= null)
								rgacomisiones.setTippol(comisionaplicacion.getTiporeferencia());
							 
							 rgacomisionesListC.add(rgacomisiones);	
				
					}
					
	
				}
				//long totalTiempo = System.currentTimeMillis() - tiempoInicio;
				//logger.info("Total tiempo hallarDatosRgaFicheroComisiones:"+totalTiempo);
			}
		}catch (Exception ex) {
					LOGGER.error("Se ha producido un error en el acceso a la BBDD  : "  + ex.getMessage());
					throw new DAOException("Se ha producido un error en el acceso a la BBDD al Fichero de comisiones"+fichero.getNombrefichero() ,ex);
		}
		return rgacomisionesListC;
		
	}
	/**
	 *  halla la lista a guardar de los ficheros de reglamento
	 * @param fichero, cierre, fase y fecha de carga
	 * @return listado de obj RgaComisiones
	 * @throws DAOException
	 */
	private List <RgaComisiones> hallarDatosRgaFicheroReglamento(Fichero fichero, Cierre cierre, Fase fase, String fechaC,Reglamento rg) throws DAOException{
	
		List <RgaComisiones> rgacomisionesListR  = new ArrayList<RgaComisiones>();	
	
		try{
				Iterator<ReglamentoProduccionEmitida> rglProduccionEmitida = fichero.getFicheroReglamentos().iterator();
				ReglamentoProduccionEmitida fich = new ReglamentoProduccionEmitida();
				float dtImporteAplicado = 0 ;
				float dcImporteAplicado = 0 ;
				
				while(rglProduccionEmitida.hasNext()){
					fich = rglProduccionEmitida.next();
					Set<ReglamentoProduccionEmitidaSituacion> rglProducionEmitidaSituacion =  fich.getReglamentoProduccionEmitidaSituacions();
					Iterator<ReglamentoProduccionEmitidaSituacion> situacionIterator = rglProducionEmitidaSituacion.iterator();
					Character tipoRef = null;
					if (fich.getTiporeferencia()!= null)
						 tipoRef = fich.getTiporeferencia();
					float pctEntidad = 1;
					
					//Lo sacamos fuera para optimizar ya que el plan de un fichero es siempre el mismo
					//Long planGGe = fase.getPlan().longValue();
					//Reglamento rg = getReglamentoByPlan(planGGe);
					
					if (rg.getPctentidad()!= null)
						pctEntidad = rg.getPctentidad().floatValue();
					
					while(situacionIterator.hasNext()){
						
						ReglamentoProduccionEmitidaSituacion situacionBean = new ReglamentoProduccionEmitidaSituacion();
						situacionBean = situacionIterator.next();
						RgaComisiones rgacomisiones = new RgaComisiones();
						
						
						
						if(fich.getReferencia() != null)
						{
							 Poliza poliza = getPolizaByReferencia(fich.getReferencia());
							 if ( poliza != null)
								 rgacomisiones.setRefplz(fich.getReferencia());

						}
						if (situacionBean.getDtMedreglamento()!= null)
							rgacomisiones.setTipmdtra(situacionBean.getDtMedreglamento());
						
						
						if (situacionBean.getDcMedida() != null)
							// Esto no es solo un caracter será un string se debera cambiar ya que vienen dos caracteres
							rgacomisiones.setCodmedcal(situacionBean.getDcMedida());
						
						if (situacionBean.getDcMedreglamento() != null)
							rgacomisiones.setTipmedcal(situacionBean.getDcMedreglamento());
						
						if (situacionBean.getDtMedida() != null)
							rgacomisiones.setCodmetra1(situacionBean.getDtMedida());
						
						if (situacionBean.getDcPorcentaje()!= null)
							rgacomisiones.setPorcal(situacionBean.getDcPorcentaje());
						
						if (situacionBean.getDtPorcentaje()!= null)
							rgacomisiones.setPortra(situacionBean.getDtPorcentaje());
						
						if (situacionBean.getDtImporteApl() != null)
						{
							dtImporteAplicado = situacionBean.getDtImporteApl().floatValue();
							dtImporteAplicado = dtImporteAplicado * pctEntidad / 100;
							rgacomisiones.setImptra(new BigDecimal(dtImporteAplicado));
							dtImporteAplicado = 0 ;
						}
						if (situacionBean.getDcImporteApl() != null)
						{
							dcImporteAplicado = situacionBean.getDcImporteApl().floatValue();
							dcImporteAplicado = dcImporteAplicado * pctEntidad /100;
							rgacomisiones.setImpcal(new BigDecimal(dcImporteAplicado));
							dcImporteAplicado = 0 ;
							
						}
						rgacomisiones.setTippol(tipoRef);
						rgacomisionesListR.add(rgacomisiones);
						
					}
					
				}
		}	
		catch (Exception ex) {
			LOGGER.error("Se ha producido un error en el acceso a la BBDD  : "  + ex.getMessage());
			throw new DAOException("Se ha producido un error en el acceso a la BBDD al Fichero de comisiones"+fichero.getNombrefichero() ,ex);
		}
		return rgacomisionesListR;
		
	}
	
	/**
	 *  halla la lista de los datos necesarios de impagados
	 * @param fichero, cierre, fase y fecha de carga
	 * @return listado de obj RgaComisiones
	 * @throws DAOException
	 */

	private List <RgaComisiones> hallarDatosRgaFicheroImpagados(Fichero fichero, Cierre cierre , Fase fase)throws DAOException {
		List <RgaComisiones> rgacomisionesListI  = new ArrayList<RgaComisiones>();	
		long tiempoInicio = System.currentTimeMillis();
		try{
			Set<ReciboImpagado> ficherosImpagados= (Set<ReciboImpagado>) fichero.getReciboImpagados();
			Iterator<ReciboImpagado> iterator = ficherosImpagados.iterator();
			while (iterator.hasNext())
			{
				RgaComisiones rgacomisiones = new RgaComisiones();
				ReciboImpagado fichImpagado = new ReciboImpagado();
				fichImpagado = iterator.next();
				if (fichImpagado.getIndividualreferencia()!= null)
				{
					 Poliza poliza = getPolizaByReferencia(fichImpagado.getIndividualreferencia());
					 if ( poliza != null)
						 rgacomisiones.setRefplz(fichImpagado.getIndividualreferencia());
				}
				if (fichImpagado.getCaGastoscomisiones() != null)
					rgacomisiones.setGastoscomisiones(fichImpagado.getCaGastoscomisiones() );
				if (fichImpagado.getCaGastosentidad() != null)
					rgacomisiones.setGastosentidad(fichImpagado.getCaGastosentidad());
				
				rgacomisionesListI.add(rgacomisiones);
			}
			long totalTiempo = System.currentTimeMillis() - tiempoInicio;
			logger.info("Total tiempo hallarDatosRgaFicheroImpagados:"+totalTiempo);	
		
		}catch (Exception ex) {
			LOGGER.error("Se ha producido un error en el acceso a la BBDD  : "  + ex.getMessage());
			throw new DAOException("Se ha producido un error en el acceso a la BBDD al Fichero de comisiones"+fichero.getNombrefichero() ,ex);
		}
		return rgacomisionesListI;
	}
	
	@SuppressWarnings("unchecked")
	private CultivosSubentidades getCCBySubEntidad(SubentidadMediadora subentidadMediadora , Linea linea)throws DAOException {
		Session session = obtenerSession(); 
		List<CultivosSubentidades> cultivosSubEntList = new ArrayList<CultivosSubentidades>();
		
		try {
			
			Criteria criteria = session.createCriteria(CultivosSubentidades.class);
			criteria.createAlias("linea","linea");
			criteria.createAlias("subentidadMediadora", "subentidadMediadora");
			criteria.createAlias("subentidadMediadora.id", "subentidadMediadora.id");
			criteria.add(Restrictions.eq("subentidadMediadora.id.codentidad", subentidadMediadora.getId().getCodentidad()));
			criteria.add(Restrictions.eq("subentidadMediadora.id.codsubentidad",subentidadMediadora.getId().getCodsubentidad() ));
			criteria.add(Restrictions.eq("linea.lineaseguroid", linea.getLineaseguroid()));
			
			cultivosSubEntList = criteria.list();
			
			if ( cultivosSubEntList.size() > 0)
				return cultivosSubEntList.get(0);
			
		}
		catch (Exception ex) {
			LOGGER.error("Se ha producido un error en el acceso a la BBDD: " + ex.getMessage());
			throw new DAOException("Se ha producido un error en el acceso a la BBDD",ex);
		}
		return null;
	}
	

	
	@SuppressWarnings("unchecked")
	private BigDecimal getEntidadByRef(String refColectivo)throws DAOException {
		Session session = obtenerSession(); 
		BigDecimal entidad;
		try {
			
			String Sql = "select codentidad from o02agpe0.tb_colectivos where idcolectivo='"+refColectivo+"'";
			List<BigDecimal> list = session.createSQLQuery(Sql).list();
			entidad =  list.get(0);
			
		}
		catch (Exception ex) {
			LOGGER.error("Se ha producido un error en el acceso a la BBDD: " + ex.getMessage());
			throw new DAOException("Se ha producido un error en el acceso a la BBDD",ex);
		}
		return entidad;
	}
	
	
	@SuppressWarnings("unchecked")
	private CultivosEntidades getCCEntidadByLinea (Long idlinea)throws DAOException {
		Session session = obtenerSession(); 
		List<CultivosEntidades> cultivosEntList = new ArrayList<CultivosEntidades>();
		try {
			
			Criteria criteria = session.createCriteria(CultivosEntidades.class);
			criteria.createAlias("linea", "linea");
			criteria.add(Restrictions.eq("linea.lineaseguroid",idlinea));
			
			cultivosEntList = criteria.list();
			if ( cultivosEntList.size() > 0)
				return cultivosEntList.get(0);
					
		}catch (Exception ex) {
			LOGGER.error("Se ha producido un error en el acceso a la BBDD: " + ex.getMessage());
			throw new DAOException("Se ha producido un error en el acceso a la BBDD",ex);
		}
		return null;
		
	}
	@SuppressWarnings("unchecked")
	private Linea getLineaByCodLinea(BigDecimal idlinea,BigDecimal plan) throws DAOException {
		Session session = obtenerSession(); 
		
		List<Linea> listaLineas = new ArrayList<Linea>();
		try {
			Criteria criteria = session.createCriteria(Linea.class);
			criteria.add(Restrictions.eq("codlinea", idlinea));
			criteria.add(Restrictions.eq("codplan", plan));
			
			listaLineas = criteria.list();
			if (listaLineas.size() > 0)	
				return listaLineas.get(0);
		
		}catch (Exception ex) {
			LOGGER.error("Se ha producido un error en el acceso a la BBDD: " + ex.getMessage());
			throw new DAOException("Se ha producido un error en el acceso a la BBDD",ex);
		}
		return new Linea();			
		
	}
	
	@SuppressWarnings("unchecked")
	private GGESubentidades getSubentidadBySubEntidad(SubentidadMediadora subentidadMediadora) throws DAOException
	{
		List<GGESubentidades> listaGGESubentidades = new ArrayList<GGESubentidades>();
		try {
			Session session = obtenerSession(); 
			
			Criteria criteria = session.createCriteria(GGESubentidades.class);
			criteria.createAlias("subentidadMediadora", "subentidadMediadora");
			criteria.createAlias("subentidadMediadora.entidadMediadora", "entidadmediadora");
			criteria.add(Restrictions.eq("entidadmediadora.codentidad", subentidadMediadora.getEntidadMediadora().getCodentidad()));
			criteria.add(Restrictions.eq("subentidadMediadora.id", subentidadMediadora.getId()));
			
			listaGGESubentidades = criteria.list();
			if ( listaGGESubentidades.size() > 0)
				return listaGGESubentidades.get(0);
		}catch (Exception ex) {
			LOGGER.error("Se ha producido un error en el acceso a la BBDD: " + ex.getMessage());
			throw new DAOException("Se ha producido un error en el acceso a la BBDD",ex);
		}
		
		return new GGESubentidades();
	}
	

	@SuppressWarnings("unchecked")
	private GGEEntidades getEntidadByPlan(Long  plan) throws DAOException
	{	
		List<GGEEntidades> listaGGEEntidades = new ArrayList<GGEEntidades>();
		try {
			Session session = obtenerSession(); 
			
			Criteria criteria = session.createCriteria(GGEEntidades.class);
			criteria.add(Restrictions.eq("plan", plan));
			
			listaGGEEntidades = criteria.list();
			if ( listaGGEEntidades.size() > 0)
				return listaGGEEntidades.get(0);
		
		}catch (Exception ex) {
			LOGGER.error("Se ha producido un error en el acceso a la BBDD: " + ex.getMessage());
			throw new DAOException("Se ha producido un error en el acceso a la BBDD",ex);
		}
		return new GGEEntidades();
	}
	
	@SuppressWarnings("unchecked")
	private Reglamento getReglamentoByPlan(Long  plan) throws DAOException
	{
		List<Reglamento> listaReglamento = new ArrayList<Reglamento>();
		try {
			Session session = obtenerSession(); 
			Criteria criteria = session.createCriteria(Reglamento.class);
			criteria.add(Restrictions.eq("plan", plan));
			
			listaReglamento = criteria.list();
			if ( listaReglamento.size() > 0)
				return listaReglamento.get(0);
		
		}catch (Exception ex) {
			LOGGER.error("Se ha producido un error en el acceso a la BBDD: " + ex.getMessage());
			throw new DAOException("Se ha producido un error en el acceso a la BBDD",ex);
		}
		return new Reglamento();
	}
	
	@SuppressWarnings("unchecked")
	private  SubentidadMediadora getDatosCol(String referenciaColectivo) throws DAOException {
		Session session = obtenerSession(); 
		SubentidadMediadora subEntidad =null;
		Colectivo colectivo = new Colectivo();
		List<Colectivo> listCol = new ArrayList<Colectivo>();
		try {
			
			Criteria criteria = session.createCriteria(Colectivo.class);
			criteria.add(Restrictions.eq("idcolectivo", referenciaColectivo));
			
			listCol = criteria.list();
			if ( listCol.size() > 0)
			{
				colectivo =(Colectivo) listCol.get(0);
				subEntidad = colectivo.getSubentidadMediadora();
				
			}
			
		}
		catch (Exception ex) {
			LOGGER.error("Se ha producido un error en el acceso a la BBDD: " + ex.getMessage());
			throw new DAOException("Se ha producido un error en el acceso a la BBDD",ex);
		}
		return subEntidad;
	}
	
	@SuppressWarnings("unchecked")
	private HashMap<BigDecimal, Long> getLineasPlan(BigDecimal plan) throws DAOException {
		Session session = obtenerSession(); 
		HashMap<BigDecimal, Long> lineasPlan = new HashMap<BigDecimal, Long>();
		try {
			Criteria criteria = session.createCriteria(Linea.class);
			criteria.add(Restrictions.eq("codplan", plan));
			
			List<Linea> lineas = criteria.list();
			if (lineas.size()>0){
				for (int i = 0; i<lineas.size();i++){
					Linea l = lineas.get(i);
					lineasPlan.put(l.getCodlinea(), l.getLineaseguroid());
				}
			}
				
		}
		catch (Exception ex) {
			LOGGER.error("Se ha producido un error en el acceso a la BBDD: " + ex.getMessage());
			throw new DAOException("Se ha producido un error en el acceso a la BBDD",ex);
		}
		return 	lineasPlan;		
	}  
	
	@SuppressWarnings("unchecked")
	private Poliza getPolizaByReferencia(String referencia)throws DAOException{
		
		Session session = obtenerSession(); 
		List<Poliza> listPol = new ArrayList<Poliza>();
		
		try {
			Criteria criteria = session.createCriteria(Poliza.class);
			criteria.add(Restrictions.eq("referencia", referencia));
			
			listPol = criteria.list();
			
			if ( listPol.size() > 0)
				return listPol.get(0);
		}
		catch (Exception ex) {
			LOGGER.error("Se ha producido un error en el acceso a la BBDD: " + ex.getMessage());
			throw new DAOException("Se ha producido un error en el acceso a la BBDD",ex);
		}
		return null;
	}
	// redondea a dos decimales 
	public BigDecimal redondear(BigDecimal numero)
	{
		Double num ;
		BigDecimal resultado = new BigDecimal(0);
		if( numero != null)
		{
			   num =  Double.parseDouble(numero.toString());
			   double m =  Math.rint(num*100)/100;
			   resultado = new BigDecimal(m);
		}
		return resultado;
	}
	/**
	 *  se recorre por referencia de poliza los listados para crear el lista de obj rgaComisiones a guardar
	 * @param fichero, cierre, fase y fecha de carga
	 * @return listado de obj RgaComisiones
	 * @throws DAOException
	 */
	private List <RgaComisiones>  hallarDatosRgaComisiones( List <RgaComisiones> rgacomisionesListR ,List <RgaComisiones> rgacomisionesListC ,List <RgaComisiones> rgacomisionesListI	)throws DAOException{
	
		List <RgaComisiones> rgacomisionesList =  new ArrayList<RgaComisiones>();	
		
		
		if (rgacomisionesListC.size() > 0)
		{
			for (int i=0 ; i < rgacomisionesListC.size() ;i++)
			{
				RgaComisiones rga = new RgaComisiones();
				RgaComisiones rgaC = new RgaComisiones();
				RgaComisiones rgaI = new RgaComisiones();
				RgaComisiones rgaR = new RgaComisiones();
				
				rgaC = rgacomisionesListC.get(i);
				for (int j = 0 ; j < rgacomisionesListR.size() ; j++)
				{
					rgaR = new RgaComisiones();
					rgaR = rgacomisionesListR.get(j);
					if (rgaC.getRefplz().equals(rgaR.getRefplz()) && rgaC.getTippol().equals(rgaR.getTippol()) )
					{
					    rgaR = rgacomisionesListR.get(j);
						break;	
					}
					else
						rgaR = new RgaComisiones();
					
				}	
				if (rgacomisionesListI.size() > 0)
				{
					
					for (int k = 0 ; k < rgacomisionesListI.size() ; k++)
					{	
						rgaI = new RgaComisiones();
						rgaI = rgacomisionesListI.get(k);
						if (rgaC.getRefplz().equals(rgaI.getRefplz()))
						{
							rgaI = rgacomisionesListI.get(k);
							break;
						}
						else
							rgaI = new RgaComisiones();
							
					}
				}
			
				rga = rellenarObjRga(rgaC,rgaR,rgaI);
				rgacomisionesList.add(rga);
			}
		}		
	
		return rgacomisionesList;
	}
	/**
	 *  toma de cada objeto de las distintas listas , los datos a rellenar en la tabla rgaCOmisiones
	 * @param listas de obj rga relativos a datos a guardar de cada fichero
	 * @return  obj RgaComisiones a guardar
	 * @throws
	 */
	private RgaComisiones rellenarObjRga (RgaComisiones rgaC, RgaComisiones rgaR, RgaComisiones  RgaI )
	{
		RgaComisiones rga = new RgaComisiones();
		rga.setCierre(rgaC.getCierre());
		rga.setCodcol(rgaC.getCodcol());
		rga.setCodent(rgaC.getCodent());
		rga.setCodentmed(rgaC.getCodentmed());
		rga.setCodlin(rgaC.getCodlin());
		rga.setCodmedcal(rgaR.getCodmedcal());
		rga.setCodmetra1(rgaR.getCodmetra1());
		rga.setCodofi(rgaC.getCodofi());
		rga.setCodpln(rgaC.getCodpln());
		rga.setCodsubmed(redondear(rgaC.getCodsubmed()));
		rga.setComentreg(redondear(rgaC.getComentreg()));
		rga.setComentsum(redondear(rgaC.getComentsum()));
		rga.setCommendnue(redondear(rgaC.getCommendnue()));
		rga.setCommendreg(redondear(rgaC.getCommendreg()));
		rga.setCommentnue(redondear(rgaC.getCommentnue()));
		rga.setComrganue(redondear(rgaC.getComrganue()));
		rga.setComrgareg(redondear(rgaC.getComrgareg()));
		rga.setComrgasum(redondear(rgaC.getComrgasum()));
		rga.setComsubsum(redondear(rgaC.getComsubsum()));
		rga.setCostot(rgaC.getCostot());
		rga.setFeccar(rgaC.getFeccar());
		rga.setGasentnue(redondear(rgaC.getGasentnue()));
		rga.setGasentreg(redondear(rgaC.getGasentreg()));
		rga.setGasentsum(redondear(rgaC.getGasentsum()));
		rga.setGasrganue(redondear(rgaC.getGasrganue()));
		rga.setGasrgareg(redondear(rgaC.getGasrgareg()));
		rga.setGasrgasum(redondear(rgaC.getGasrgasum()));
		rga.setGassubnue(redondear(rgaC.getGassubnue()));
		rga.setGassubreg(redondear(rgaC.getGassubreg()));
		rga.setGassubsum(redondear(rgaC.getGassubsum()));
		rga.setGassum(redondear(rgaC.getGassum()));
		rga.setGasnue(redondear(rgaC.getGasnue()));
		rga.setGasreg(redondear(rgaC.getGasreg()));
		rga.setImpcal(rgaR.getImpcal());
		rga.setImptra(rgaR.getImptra());
		rga.setNifasg(rgaC.getNifasg());
		rga.setNomasg(rgaC.getNomasg());
		rga.setNumfas(rgaC.getNumfas());
		rga.setPorcal(rgaR.getPorcal());
		rga.setPortra(rgaR.getPortra());
		rga.setPrinue(rgaC.getPrinue());
		rga.setPrireg(redondear(rgaC.getPrireg()));
		rga.setPrisum(rgaC.getPrisum());
		rga.setRefplz(rgaC.getRefplz());
		rga.setTipmdtra(rgaR.getTipmdtra());
		rga.setTipmedcal(rgaR.getTipmedcal());
		rga.setTippol(rgaC.getTippol());
		rga.setTotnue(redondear(rgaC.getTotnue()));
		rga.setTotreg(redondear(rgaC.getTotreg()));		
		if(RgaI != null)
		{
			rga.setGastoscomisiones(RgaI.getGastoscomisiones() );
			rga.setGastosentidad(RgaI.getGastosentidad());
		}
		
		return rga;
	}
	
	
}
