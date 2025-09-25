package com.rsi.agp.core.decorators;




import org.displaytag.decorator.TableDecorator;

import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.familias.Familia;
import com.rsi.agp.dao.tables.familias.LineaFamilia;

public class ModelTableDecoratorMtoFamilias extends TableDecorator{
		
	public String getAcciones(){

		LineaFamilia fam = getFamilia();

		String codFamilia = this.getCodFamilia();
		String nomFamilia = this.getNomFamilia();
		String codGrupo = this.getGrupo();
		String codGrupoNegocio = StringUtils.nullToString(fam.getId().getGrupoNegocio());

		String codLinea = this.getCodLinea();


		String modif = String.format("<a href=\"javascript:modificar('%s','%s','%s','%s','%s','%s')\"><img src=\"jsp/img/displaytag/edit.png\" alt=\"Editar\" title=\"Editar\"/></a>",
				codFamilia,
				nomFamilia,
				fam.getId().getCodGrupoFamilia(),
				codGrupoNegocio,
				codLinea,
				fam.getLinea().getDeslinea()
				);

		// Build HTML buttons
		String baja = String.format("<a href=\"javascript:baja('%s','%s','%s','%s')\"><img src=\"jsp/img/displaytag/delete.png\" alt=\"Borrar\" title=\"Borrar\"/></a>",
				codFamilia,
				fam.getId().getCodGrupoFamilia(),
				codGrupoNegocio,
				codLinea
				);

		return modif + baja;
	}
	
	public String getCodFamilia(){
		LineaFamilia fam = getFamilia();
		return StringUtils.nullToString(fam.getFamilia().getCodFamilia());
	}
	
	public String getNomFamilia(){
		LineaFamilia fam = getFamilia();
		return StringUtils.nullToString(fam.getFamilia().getNomFamilia());
	}
	

	
	public String getCodLinea(){
		LineaFamilia fam = getFamilia();
		return StringUtils.nullToString(fam.getId().getCodLinea());
	}
	

	
	public String getGrupoNegocio(){
		LineaFamilia fam = getFamilia();
		return StringUtils.nullToString(fam.getGruposNegocio().getDescripcion());
	}
	public String getGrupo(){
		LineaFamilia fam = getFamilia();
		return StringUtils.nullToString(fam.getGrupoFamilia().getNomGrupo());
	}

	
	
	private LineaFamilia getFamilia() {
		return (LineaFamilia) getCurrentRowObject();
	}
	
}
