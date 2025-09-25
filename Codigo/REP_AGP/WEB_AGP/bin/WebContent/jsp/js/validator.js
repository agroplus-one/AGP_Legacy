// =========================================
// Validator Object (singleton/namespace)
//
// Usage:
//     result = Validator.nif("14308425D");
//       if(result)
//           // ...
//       else
//           // ...
// ==========================================

//
// Todas las funciones retornan:
//     true --> si es valido
//     false --> no valido 
//
var Validator = {
   /**
 	* Funcion que 
 	* @param 
 	* @return 
	*/
    nifNie:function(value){
        result = false;
        
        // TODO
        
        return result;
    },
    /**
 	* Funcion que 
 	* @param 
 	* @return 
	*/
    nif:function(value){
        result = false;
        
        // TODO
        
        return result;
    },
    /**
 	* Funcion que 
 	* @param 
 	* @return 
	*/
    string:function(value){
        result = false;
        
        // TODO
        
        return result;
    },
    /**
 	* Funcion que 
 	* @param 
 	* @return 
	*/
    numeric:function(value){
        result = false;
        
        // TODO
        
        return result;
    },
    /**
 	* Funcion que 
 	* @param 
 	* @return 
	*/
    size:function(value,tam){
        result = false;
        
        // TODO
        
        return result;
    },
    /**
 	* Funcion para validar que el valor del cod postal no acabe en 000
 	* @param value = el valor de entrada del cod postal
 	* @return 
	*/
    postalCode:function(value){        
		var expRegular=/0{3}$/;
		
		if(expRegular.test(value)){
			result = true;
		}else{
			result = false;
		}       
        return result;
    },
    /**
 	* Funcion que 
 	* @param 
 	* @return 
	*/
    houseTelephone:function(value){
        result = false;
        
        // TODO
        
        return result;
    },
    /**
 	* Funcion que 
 	* @param 
 	* @return 
	*/
    movilTelephone:function(value){
        result = false;
        
        // TODO
        
        return result;
    },
    /**
 	* Funcion que 
 	* @param 
 	* @return 
	*/
    provincia:function(value){
        result = false;
        
        // TODO
        
        return result;
    
    },
    /**
 	* Funcion que 
 	* @param 
 	* @return 
	*/
    provinciaPostalCode:function(value,value){
        result = false;
        
        // TODO
        
        return result;
    }
}
