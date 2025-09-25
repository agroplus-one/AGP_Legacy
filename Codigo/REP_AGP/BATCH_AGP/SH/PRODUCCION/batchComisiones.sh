#Funcion principal

main()
{
	#echo "init"
	PATH_EJECUTION="/aplicaciones2/AGP_AGROPLUS/batchs"
   	PATH_LOG="/aplicaciones2/AGP_AGROPLUS/batchs"
  	fecha=`date '+%d%m%y%H%M%S'`
	#echo "fin init"
	
   	## Se cargan las propiedades definidas en agp.batch.properties
   	if [ ! -f ${PATH_EJECUTION}/agp.batch.properties ]; then
      		#echo "\nERROR000: Imposible localizar el fichero de configuracion ${PATH_EJECUTION}/agp.batch.properties>\n"
      		exit 1
    	fi
	
	## Se cargan variables y funciones comunes
    . ${PATH_EJECUTION}/agp_common.sh
	
	LOG_NAME_1="${PATH_LOG}"/AGP_COMISIONES_"${fecha}".log
    log "" | tee -a $LOG_NAME_1;
    log "Las variables de entrada leidas del fichero de configuracion son:"  | tee -a $LOG_NAME_1;
    log "javaPath64Prop: ${javaPath64Prop}"  | tee -a $LOG_NAME_1;
    log "fileManagerProp: ${fileManagerProp}"  | tee -a $LOG_NAME_1;
	
	# Ruta de LD_LIBRARY_PATH para conexiones a Oracle con autoconnect
    #echo ${ORACLE_HOME}
    export LD_LIBRARY_PATH=$ORACLE_HOME/lib
	
	log "ORACLE_HOME - $ORACLE_HOME" | tee -a $LOG_NAME_1;			
	log "LD_LIBRARY_PATH - $LD_LIBRARY_PATH" | tee -a $LOG_NAME_1;			
      
    log "" | tee -a $LOG_NAME_1;	    	
	log "Inicio del proceso de descarga de comisiones: `date '+%d/%m/%y %H:%M:%S'`"  | tee -a $LOG_NAME_1;        
	
	$javaPath64Prop/java -jar -Xms512m -Xmx2048m -Djava.library.path=$LD_LIBRARY_PATH -cp . $fileManagerProp/BatchComisiones.jar
	
	ret_val=$?
	
	log "Return value: $ret_val"  | tee -a $LOG_NAME_1;
	
	#log "Resultado de la ejecucion: $resultado"  | tee -a $LOG_NAME_1;
	
	if [ $ret_val -eq 0 ]; then
		#echo "Importacion y clasificacion de ficheros correcta."
		log "Importacion y clasificacion de ficheros correcta."  | tee -a $LOG_NAME_1;
		exit 0
	
	elif [ $ret_val -eq 1 ]; then
		#echo "Error al recuperar el listado del directorio."
		log "Error al recuperar el listado del directorio."  | tee -a $LOG_NAME_1;
		exit 1
	
	elif [ $ret_val -eq 2 ]; then
		#echo "Error en BBDD al clasificar los ficheros descargados."
		log "Error en BBDD al clasificar los ficheros descargados."  | tee -a $LOG_NAME_1;
		exit 1

	elif [ $ret_val -eq 3 ]; then
		#echo "No se encuentra el fichero/directorio al clasificar los ficheros."
		log "No se encuentra el fichero/directorio al clasificar los ficheros."  | tee -a $LOG_NAME_1;
		exit 1

	elif [ $ret_val -eq 4 ]; then		
		#echo "Error de I/O al clasificar los ficheros."
		log "Error de I/O al clasificar los ficheros."  | tee -a $LOG_NAME_1;
		exit 1
		
	elif [ $ret_val -eq 6 ]; then
		#echo "Se ha excedido el tiempo de ejecucion."
		log "Se ha excedido el tiempo de ejecucion."  | tee -a $LOG_NAME_1;
		exit 1
		
	else
		#echo "Codigo de respuesta inesperado: '$batchComisiones'"
		log "Codigo de respuesta inesperado."  | tee -a $LOG_NAME_1;
		exit 1
	fi
}

main $1

