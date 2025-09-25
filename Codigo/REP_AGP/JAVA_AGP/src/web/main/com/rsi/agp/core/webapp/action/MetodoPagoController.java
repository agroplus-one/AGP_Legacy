package com.rsi.agp.core.webapp.action;

import java.math.BigDecimal;

import org.springframework.web.servlet.mvc.Controller;

import com.rsi.agp.core.managers.impl.SeleccionPolizaManager;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.poliza.Poliza;

public class MetodoPagoController extends BaseSimpleController implements
		Controller {

	private SeleccionPolizaManager seleccionPolizaManager;
	
	public boolean isPagoCCAllowed(final Usuario usuario, final Poliza poliza) {
		boolean mpPagoC = true;
		logger.debug("usuario.getPerfil(): " + usuario.getPerfil());
		logger.debug("usuario.isUsuarioExterno(): " + usuario.isUsuarioExterno());
		if (usuario.getPerfil().equals(Constants.PERFIL_USUARIO_ADMINISTRADOR) || 
				usuario.getPerfil().equals(Constants.PERFIL_USUARIO_SEMIADMINISTRADOR) ||
				(usuario.getPerfil().equals(Constants.PERFIL_USUARIO_SERVICIOS_CENTRALES) && !usuario.isUsuarioExterno())){
			mpPagoC=true;
		} else {
			logger.debug("poliza.getColectivo().getEnvioIbanAgro(): " + poliza.getColectivo().getEnvioIbanAgro());
			// SOLO SI NO ES OBLIGATORIO EL PAGO DOMICILIADO SEGUN COLECTIVO
			mpPagoC = !(poliza.getColectivo().getEnvioIbanAgro() == 'O')
					&& (poliza.getColectivo().getSubentidadMediadora().getCargoCuenta() != null
							&& poliza.getColectivo().getSubentidadMediadora().getCargoCuenta().intValue() == 1);
		}		
		logger.debug("isPagoCCAllowed: " + mpPagoC);
		return mpPagoC;		
	}

	public boolean isPagoMAllowed(final Usuario usuario, final Poliza poliza) {
		try {
			boolean mpPagoM = false;
			
			BigDecimal codEntidad=null;
			BigDecimal codOficina = null;
			
			if (null!=usuario.getOficina() && null!=usuario.getOficina().getId()) {
				if (null!=usuario.getOficina().getId().getCodentidad())
					codEntidad=usuario.getOficina().getId().getCodentidad();
				if (null!=usuario.getOficina().getId().getCodoficina())
					codOficina=usuario.getOficina().getId().getCodoficina();
			}
			
			if (null!=codEntidad && null!=codOficina) {
				if ((usuario.getPerfil().equals(Constants.PERFIL_USUARIO_OFICINA) || usuario.getPerfil().equals(Constants.PERFIL_USUARIO_JEFE_ZONA))  && !usuario.isUsuarioExterno()) {
					mpPagoM = seleccionPolizaManager.isOficinaPagoManual(codOficina, codEntidad);
				} else {
					// SOLO SI NO ES OBLIGATORIO EL PAGO DOMICILIADO SEGUN COLECTIVO
					mpPagoM = !(poliza.getColectivo().getEnvioIbanAgro() == 'O');
				}
			}			
			logger.debug("isPagoMAllowed: " + mpPagoM);
			return mpPagoM;
		} catch (Exception e) {
			logger.error("Ocurrio un error al comprobar si se permite hacer pago manual", e);
			return false;
		}
	}
	
	public boolean isDomiciAgroAllowed(final Usuario usuario, final Poliza poliza, boolean lineaContrSup2019) {
		
		boolean mpDomiAgro = true;		
		if (lineaContrSup2019) {
		
			if(usuario.getPerfil().equals(Constants.PERFIL_USUARIO_ADMINISTRADOR) || 
					usuario.getPerfil().equals(Constants.PERFIL_USUARIO_SEMIADMINISTRADOR) ||
					(usuario.getPerfil().equals(Constants.PERFIL_USUARIO_SERVICIOS_CENTRALES) && !usuario.isUsuarioExterno())){
				mpDomiAgro = true;
			}else{
				if (poliza.getColectivo().getEnvioIbanAgro() != null){
					if (poliza.getColectivo().getEnvioIbanAgro() == 'N') {
						logger.debug("Valor de envio_iban (colectivo):"+poliza.getColectivo().getEnvioIbanAgro());
						mpDomiAgro = false; 
					} else {						
						mpDomiAgro = true;
					}
				} else {
					mpDomiAgro = false;
				}
			}
			
		} else {
			mpDomiAgro = false;
		}
		logger.debug("isDomiciAgroAllowed: " + mpDomiAgro);
		return mpDomiAgro;
	}

	public final void setSeleccionPolizaManager(
			final SeleccionPolizaManager seleccionPolizaManager) {
		this.seleccionPolizaManager = seleccionPolizaManager;
	}
	
	public final SeleccionPolizaManager getSeleccionPolizaManager() {
		return this.seleccionPolizaManager;
	}
}