#Funcion principal

main()
{
	echo "init"
	PATH_EJECUTION="/aplicaciones2/AGP_AGROPLUS/batchs"
   	PATH_LOG="/aplicaciones2/AGP_AGROPLUS/batchs"
  	fecha=`date '+%d%m%y%H%M%S'`
	echo "fin init"
	
   	## Se cargan las propiedades definidas en agp.batch.properties
   	if [ ! -f ${PATH_EJECUTION}/agp.batch.properties ]; then
      		echo "\nERROR000: Imposible localizar el fichero de configuracion ${PATH_EJECUTION}/agp.batch.properties>\n"
      		exit 1
    	fi
	
	echo "end if"
	
	
	# Ruta de LD_LIBRARY_PATH para conexiones a Oracle con autoconnect
        echo ${ORACLE_HOME}
        export LD_LIBRARY_PATH=$ORACLE_HOME/lib32
        
        
	
    	## Se cargan variables y funciones comunes
    	. ${PATH_EJECUTION}/agp_common.sh
	
	echo "Comenzamos la ejecucion."
	batchComisiones=`$javaPathProp/java -jar -Djava.library.path=$LD_LIBRARY_PATH $fileManagerProp/BatchComisiones.jar`
	
	if [ $batchComisiones == 0 ]
	then
		echo "Importacion y clasificacion de ficheros correcta."
		exit 0
	
	elif [ $batchComisiones == 1 ]
	then
		echo "Error al recuperar el listado del directorio."
		exit 1
	
	elif [ $batchComisiones == 2 ]
	then
		echo "Error en BBDD al clasificar los ficheros descargados."
		exit 1

	elif [ $batchComisiones == 3 ]
	then
		echo "No se encuentra el fichero/directorio al clasificar los ficheros."
		exit 1

	elif [ $batchComisiones == 4 ]
	then
		echo "Error de I/O al clasificar los ficheros."
		exit 1
	elif [ $batchComisiones == 6 ]
	then
		echo "Se ha excedido el tiempo de ejecucion."
		exit 1
	else
		echo "Codigo de respuesta inesperado."
		echo $batchComisiones
	fi
}

main $1

