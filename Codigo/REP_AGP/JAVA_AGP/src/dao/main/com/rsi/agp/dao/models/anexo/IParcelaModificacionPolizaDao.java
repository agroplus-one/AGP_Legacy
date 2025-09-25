package com.rsi.agp.dao.models.anexo;

import java.util.List;
import java.util.Map;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.dao.models.GenericDao;
import com.rsi.agp.dao.tables.anexo.CapitalAsegurado;
import com.rsi.agp.dao.tables.anexo.Parcela;
import com.rsi.agp.dao.tables.cpl.Modulo;
import com.rsi.agp.dao.tables.cpl.RiesgoCubiertoModulo;
import com.rsi.agp.dao.tables.poliza.SWModulosCobParcelaAnexo;

@SuppressWarnings("rawtypes")
public interface IParcelaModificacionPolizaDao extends GenericDao {

	public List<CapitalAsegurado> list(CapitalAsegurado capitalAsegurado) throws DAOException;

	public List<Parcela> getParcelasAnexo(CapitalAsegurado capitalAsegurado, String columna, String orden)
			throws DAOException;

	public List<Parcela> getParcelasAnexo(Parcela parcela) throws DAOException;

	public boolean existenParcelasEnAnexo(Long idAnexoModificacion) throws DAOException;

	public List getModulosPoliza(Long idPoliza) throws BusinessException;

	public void setEstadoParcelas(List<Parcela> parcelasAnexo, Character estadoParcela) throws DAOException;

	public void deshacerParcelas(List<Long> idsParcelasAnexo) throws DAOException;

	public void copiarParcelasPolizaCopy(Long anexoModificacion) throws DAOException;

	public List getPantallaVarAnexo(Long lineaSeguroId, Long idPantalla);

	public List<Modulo> getCoberturasNivelParcela(List<String> listCodigosModulos, Long lineaseguroid)
			throws DAOException;

	public List<RiesgoCubiertoModulo> getRiesgosCubiertosModulo(Long lineaseguroid, String codmodulo);

	public void renumerarHoja(Parcela parcela) throws DAOException;

	public void actualizaInstalaciones(List<Parcela> listInstalaciones, Long idNuevaParcela);

	public Parcela getParcelaAnexo(Long id);

	public List<Parcela> getParcelasAnexoMismoSigpac(Parcela parcelaBaja, Long idAnexo);

	public Map<Object, Object> getMaxNumPorHoja(Long idAnexo, String codParcelaVO) throws DAOException;

	public Map<Object, Object> getClaveTerminoPorHoja(Long idAnexo, String codParcelaVO) throws DAOException;

	public void asignarInstalacionesFromPolizaActualizada(Long idAnexo);
	
	/* Pet.50776_63485-Fase II ** MODIF TAM (17.11.2020) ** Inicio */
	public SWModulosCobParcelaAnexo saveEnvioCobParcelaAnx(SWModulosCobParcelaAnexo envio)	throws DAOException;
	public void actualizaXmlCoberturasParcAnx(Long idEnvio, final String xml, final String respuesta) throws DAOException;
	public List<RiesgoCubiertoModulo> getRiesgosCubiertosModuloAnx(final Long lineaseguroid, final String codmodulo,
			final Character nivelEleccion);
	/* Pet.50776_63485-Fase II ** MODIF TAM (17.11.2020) ** Fin */

	/* Pet. 78691 ** MODIF TAM (22/12/2021) ** Inicio */
	public String getdescSistCultivo(String sistCultivo) throws DAOException;
	
}
