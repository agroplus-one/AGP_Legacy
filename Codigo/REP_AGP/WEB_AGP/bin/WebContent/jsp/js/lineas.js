function validarLineaparaReplica(codPlan, codLinea, panelAlertasValidacion){
	
	// Valida el campo 'Plan' si esta informado
	
	if (codPlan != ''){
		var planOk = false;
		try {
			var auxPlan =  parseFloat(codPlan);			
			if(/^([0-9])*$/.test(codPlan) && codPlan.length == 4 && auxPlan > 0){
				codPlan=auxPlan;
				planOk = true;
			}
		}
		catch (ex) {}
		
		// Si ha habido error en la validación muestra el mensaje
		if (!planOk) {			
			panelAlertasValidacion.html("Valor para el plan no válido");
			panelAlertasValidacion.show();
			return false;
		}
	}
	else{
		panelAlertasValidacion.html("Debe introducir un plan");
		panelAlertasValidacion.show();
		return false;
	}
	
	// Valida el campo 'Linea' si esta informado
	if (codLinea != ''){
		var lineaOk = false;
		try {
			var auxLinea =  parseFloat(codLinea);
			if(/^([0-9])*$/.test(codLinea) && auxLinea > 0){
				codLinea=auxLinea;
				lineaOk = true;
			}
		}
		catch (ex) {}
		
		// Si ha habido error en la validación muestra el mensaje
		if (!lineaOk) {
			panelAlertasValidacion.html("Valor para la línea no válido");
			panelAlertasValidacion.show();
			return false;
		}
	}
	else{
		panelAlertasValidacion.html("Debe seleccionar una línea");
		panelAlertasValidacion.show();
		return false;
	}	
	return true;
	
}

function replicaPlanLineaDiferentes (planReplica, plan, lineaReplica, linea) {	
	if (planReplica == plan && lineaReplica == linea) return false
	else return true;
}

