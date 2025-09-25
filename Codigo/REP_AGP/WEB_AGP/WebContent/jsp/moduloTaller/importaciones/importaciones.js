$(document).ready(function(){
	var URL = UTIL.antiCacheRand($("#frmImportacion").attr("action"));
	$("#frmImportacion").attr("action", URL);
	importaciones.inicio(); 
});

var importaciones = 
{    	
    comprobarPlanLinea:function(){
    	if ($('#plan').val() == "" || $('#linea').val() == ""){
			//Próximamente habrá que quitar el alert y poner lo de comprobar obligatorios.
			alert("Debe introducir un plan y una linea");
			return false;
		}
		else
			return true;
    },
	importar:function(){
		var checkImp = $('input:radio[name=chkTipoImportacion]:checked').val();
		
		switch (checkImp){
			case '1': this.impCompleta(); break;
			case '2': this.impOrganizador(); break;
			case '3': this.impCondGeneral(); break;
			case '4': this.impCondPL(); break;
		}
	},
	impCompleta:function(){
		if (this.comprobarPlanLinea()){
			
			this.marcarTodosOrg();
			this.marcarTodosCond();
			this.marcarTodosPL()
			
			if (($("#tsOrganizador").val() || []).length > 0 || ($("#tsCondGeneral").val() || []).length > 0 || ($("#tsCondPL").val() || []).length > 0){
				
				$("#tablas").val(($("#tsOrganizador").val() || []).join(',') + "," + ($("#tsCondGeneral").val() || []).join(',') + "," + ($("#tsCondPL").val() || []).join(','));
				
				$("#operacion").val("importarTodo");
				$("#frmImportacion").submit();
			}
			else{
				this.inicio();
				alert("No hay ninguna tabla que importar");
			}
		}
	},
	impOrganizador:function(){
		if (this.comprobarPlanLinea()){
			var valorChk = $('input:radio[name=chkOrganizador]:checked').val();

			if ($("#tsOrganizador option").length > 0){
				var seleccionadosOrganizador = "";
	
				//Si tenemos marcada la opción "Todas" del organizador, desbloqueamos el select multi y marcamos todo
				if (valorChk == '1'){
					this.marcarTodosOrg();
				}
	
				seleccionadosOrganizador = ($("#tsOrganizador").val() || []).join(',');
				if (seleccionadosOrganizador != ""){
					$("#tablas").val(seleccionadosOrganizador);
					$("#operacion").val("importarOrganizador");
					$("#frmImportacion").submit();
				}
				else{
					if (valorChk == '2'){
						alert("Debe seleccionar al menos una tabla que importar");
					}
					else{
						alert("Error al cargar las tablas");
					}
				}
			}
			else{
				if (valorChk == 1){
					this.habilitaOrg();
				}
				alert("No hay ninguna tabla del organizador para importar");
			}
		}
	},
	impCondGeneral:function(){
		var valorChk = $('input:radio[name=chkCondGeneral]:checked').val();

		if ($("#tsCondGeneral option").length > 0){
			var seleccionadosOrganizador = "";

			//Si tenemos marcada la opción "Todas" del organizador, desbloqueamos el select multi y marcamos todo
			if (valorChk == '1'){
				this.marcarTodosCond();
			}

			seleccionadosOrganizador = ($("#tsCondGeneral").val() || []).join(',');
			if (seleccionadosOrganizador != ""){
				$("#tablas").val(seleccionadosOrganizador);
				$("#operacion").val("importarCondGeneral");
				$("#frmImportacion").submit();
			}
			else{
				if (valorChk == '2'){
					alert("Debe seleccionar al menos una tabla que importar");
				}
				else{
					alert("Error al cargar las tablas");
				}
			}
		}
		else{
			if (valorChk == 1){
				this.habilitaCond();
			}
			alert("No hay ninguna tabla del condicionado general para importar");
		}
	},
	impCondPL:function(){
		if (this.comprobarPlanLinea()){
			
			var valorChk = $('input:radio[name=chkCondPL]:checked').val();

			if ($("#tsCondPL option").length > 0){
				var seleccionadosOrganizador = "";
	
				//Si tenemos marcada la opción "Todas" del organizador, desbloqueamos el select multi y marcamos todo
				if (valorChk == '1'){
					this.marcarTodosPL();
				}
	
				seleccionadosOrganizador = ($("#tsCondPL").val() || []).join(',');
				if (seleccionadosOrganizador != ""){
					$("#tablas").val(seleccionadosOrganizador);
					$("#operacion").val("importarCondPL");
					$("#frmImportacion").submit();
				}
				else{
					if (valorChk == '2'){
						alert("Debe seleccionar al menos una tabla que importar");
					}
					else{
						alert("Error al cargar las tablas");
					}
				}
			}
			else{
				if (valorChk == 1){
					this.habilitaPL();
				}
				alert("No hay ninguna tabla del condicionado específico para importar");
			}
		}
	},
	marcarTodosOrg:function(){
		//seleccionar todos los valores del organizador
		$("#tsOrganizador").attr("disabled", false);
		$("#tsOrganizador").each(function(){
			$("#tsOrganizador option").attr("selected","selected"); });
	},
	bloqueaOrg:function(){
		$("#tsOrganizador").each(function(){
			$("#tsOrganizador option").attr("selected",""); });
		$("#tsOrganizador").attr("disabled", true);
		$("#chkOrganizador_1").attr("disabled", true);
		$("#chkOrganizador_2").attr("disabled", true);
	},
	desbloqueaOrg:function(){
		$("#chkOrganizador_1").attr("disabled", false);
		$("#chkOrganizador_2").attr("disabled", false);
	},
	marcarTodosCond:function(){
		//seleccionar todos los valores del condicionado general
		$("#tsCondGeneral").attr("disabled", false);
		$("#tsCondGeneral").each(function(){
			$("#tsCondGeneral option").attr("selected","selected"); });
	},
	bloqueaCond:function(){
		$("#tsCondGeneral").each(function(){
			$("#tsCondGeneral option").attr("selected",""); });
		$("#tsCondGeneral").attr("disabled", true);
		$("#chkCondGeneral_1").attr("disabled", true);
		$("#chkCondGeneral_2").attr("disabled", true);
	},
	desbloqueaCond:function(){
		$("#chkCondGeneral_1").attr("disabled", false);
		$("#chkCondGeneral_2").attr("disabled", false);
	},
	marcarTodosPL:function(){
		//seleccionar todos los valores del condicionado especifico
		$("#tsCondPL").attr("disabled", false);
		$("#tsCondPL").each(function(){
			$("#tsCondPL option").attr("selected","selected"); });
	},
	bloqueaPL:function(){
		$("#tsCondPL").each(function(){
			$("#tsCondPL option").attr("selected",""); });
		$("#tsCondPL").attr("disabled", true);
		$("#chkCondPL_1").attr("disabled", true);
		$("#chkCondPL_2").attr("disabled", true);
	},
	desbloqueaPL:function(){
		$("#chkCondPL_1").attr("disabled", false);
		$("#chkCondPL_2").attr("disabled", false);
	},
	habilitaOrg:function(){
		this.bloqueaCond();
		this.bloqueaPL();
		this.desbloqueaOrg();
	},
	habilitaCond:function(){
		this.bloqueaOrg();
		this.bloqueaPL();
		this.desbloqueaCond();
	},
	habilitaPL:function(){
		this.bloqueaCond();
		this.bloqueaOrg();
		this.desbloqueaPL();
	},
	habilitaTodo:function(){
		this.bloqueaOrg();
		this.bloqueaCond();
		this.bloqueaPL();
	},
	inicio:function(){
		this.bloqueaOrg();	
		this.bloqueaCond();	
		this.bloqueaPL();
	}
	
}