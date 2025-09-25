package com.rsi.agp.dao.models.comisiones;

import java.math.BigDecimal;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.xmlbeans.XmlObject;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.dao.models.GenericDao;
import com.rsi.agp.dao.tables.comisiones.Fichero;
import com.rsi.agp.dao.tables.comisiones.FicheroContenido;
import com.rsi.agp.dao.tables.comisiones.FormFicheroComisionesBean;
import com.rsi.agp.dao.tables.comisiones.deudaAplazada.FicheroMult;
import com.rsi.agp.dao.tables.comisiones.deudaAplazada.FicheroMultContenido;
import com.rsi.agp.dao.tables.commons.Usuario;

@SuppressWarnings("rawtypes")
public interface IImportacionFicherosComisionesDao extends GenericDao {
	
	public Long importarYValidarFicheroComisiones(XmlObject xmlObject,Usuario usuario,Character tipo,String nombre,HttpServletRequest request) throws DAOException;
	public List<Fichero> list(FormFicheroComisionesBean ffcb,Character tipo) throws DAOException;
	public List<FicheroMult> listDeuda(FormFicheroComisionesBean ffcb) throws DAOException;
	public Integer ficheroImportado(String nomFichero) throws DAOException;
	public FicheroContenido getFicheroContenido(Long idfichero)throws DAOException;
	public FicheroMultContenido getFicheroMultContenido(Long idfichero)throws DAOException;
	public int obtenerFicherosFase(Long idfase) throws DAOException;
	public void deleteFromTablaInformesRecibos(Long id, BigDecimal codPlan) throws DAOException;
	
}
