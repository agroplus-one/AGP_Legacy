# Reading properties file

fileManagerProp=`sed '/^\#/d' /aplicaciones/AGP_AGROPLUS/batchs/agp.batch.properties | grep "fileManager" | tail -n 1 | sed 's/^.*=//'`
userAgroseguroProp=`sed '/^\#/d' /aplicaciones/AGP_AGROPLUS/batchs/agp.batch.properties | grep "userAgroseguro" | tail -n 1 | sed 's/^.*=//'`
pwdAgroseguroProp=`sed '/^\#/d' /aplicaciones/AGP_AGROPLUS/batchs/agp.batch.properties | grep "pwdAgroseguro" | tail -n 1 | sed 's/^.*=//'`
urlUploadProp=`sed '/^\#/d' /aplicaciones/AGP_AGROPLUS/batchs/agp.batch.properties | grep "urlUpload" | tail -n 1 | sed 's/^.*=//'`
urlDownloadProp=`sed '/^\#/d' /aplicaciones/AGP_AGROPLUS/batchs/agp.batch.properties | grep "urlDownload" | tail -n 1 | sed 's/^.*=//'`
userDBProp=`sed '/^\#/d' /aplicaciones/AGP_AGROPLUS/batchs/agp.batch.properties | grep "userDB" | tail -n 1 | sed 's/^.*=//'`
pwdDBProp=`sed '/^\#/d' /aplicaciones/AGP_AGROPLUS/batchs/agp.batch.properties | grep "pwdDB" | tail -n 1 | sed 's/^.*=//'`
directoryNameProp=`sed '/^\#/d' /aplicaciones/AGP_AGROPLUS/batchs/agp.batch.properties | grep "directoryName" | tail -n 1 | sed 's/^.*=//'`
oracleSIDProp=`sed '/^\#/d' /aplicaciones/AGP_AGROPLUS/batchs/agp.batch.properties | grep "oracleSID" | tail -n 1 | sed 's/^.*=//'`
debug=`sed '/^\#/d' /aplicaciones/AGP_AGROPLUS/batchs/agp.batch.properties | grep "debug" | tail -n 1 | sed 's/^.*=//'`
javaPathProp=`sed '/^\#/d' /aplicaciones/AGP_AGROPLUS/batchs/agp.batch.properties | grep "javaPath" | tail -n 1 | sed 's/^.*=//'`

# Funcion que obtiene el directorio real asociado al directorio de Oracle
function GetDirectory
{
        ## Se recupera el directorio donde se están generando los archivos
        ## PQ_Utl.getcfg esta en el paquete de utilidades
        ## accede a la tabla config, el campo valor (TB_CONFIG_AGP.AGP_valor).
        directoryName=`sqlplus -s /@$oracleSIDProp << EOF
                set heading off;
                set feedback off;
                set serveroutput on;
                whenever OSERROR exit OSCODE rollback;
                whenever SQLERROR exit SQL.SQLCODE rollback;
                declare
                        l_dir VARCHAR2(100);
                        l_dir_name VARCHAR2(255);
                begin
                        l_dir  := o02agpe0.PQ_Utl.getcfg('$directoryNameProp');
                        -- Se guarda el path fisico del directorio
                        SELECT DIRECTORY_PATH into l_dir_name FROM ALL_DIRECTORIES WHERE DIRECTORY_NAME=l_dir;
                        dbms_output.put_line(l_dir_name);
                end;
                /
                exit;
		EOF`

        echo $directoryName
}

# Funcion que obtiene el parametro de configuracion indicado de la TABLA TB_CONFIG_AGP
function GetConfigParam
{
        ## Se recuperan los parametros indicados
        ## PQ_Utl.getcfg esta en el paquete de utilidades
        ## accede a la tabla config, el campo valor (TB_CONFIG_AGP.AGP_valor).
        paramValue=`sqlplus -s /@$oracleSIDProp << EOF
                set heading off;
                set feedback off;
                set serveroutput on;
                whenever OSERROR exit OSCODE rollback;
                whenever SQLERROR exit SQL.SQLCODE rollback;
                declare
                        l_paramValue VARCHAR2(100);
                begin
                        l_paramValue  := o02agpe0.PQ_Utl.getcfg('$1');
                        dbms_output.put_line(l_paramValue);
                end;
                /
                exit;
		EOF`

        echo $paramValue
}

# Funcion que devuelve la posicion del parametro 2 en el string 1
indexOf()
{
    case $1 in
        *$2*)
            idx=${1%$2*};
            _RINDEX=$(( ${#idx} + 1 ))
        ;;
        *)
            _RINDEX=0;
            return 1
        ;;
    esac
}


# Funcion que saca mensajes para depurar el script
function log
{
	if [ $debug = "1" ]; then
		echo $1
	fi
}

# Funcion que comprueba la conexion a la BBDD
function CompruebaConexion
{
    #Recibe el login y la password del usuario, y la instancia de la BD
    CONECTION=${1}
    LOG_NAME=${2}
    ERROR=0
    
    log "\n\n    CONECTION: $CONECTION       LOG_NAME: $LOG_NAME"
    
    
    typeset -i I=0
    {
        print "set pause off;"
        print "set head off;"
        print "set PAGESIZE 0;"
        print "select 1 from dual;"
        print "commit;"

        print "prompt EOF_MARK"
    } |
    sqlplus -s \
        ${CONECTION} |
    while read LINE
    do
        [ "${LINE}" = "EOF_MARK" ] && break
        if [ "`echo "${LINE}" | cut -c1-4`" = "ORA-" ]; then
            echo "${LINE}" | tee -a ${LOG_NAME}
            let ERROR="${ERROR}"+1
        fi
    done

    if [ $ERROR -ne 0 ]; then
        #El fichero contiene la cadena 'ORA-' -> Error
        echo "\n\nERROR000: Imposible la conexion a la base de datos ---"
        exit 1
    fi

    log "\n\n --- Conexión a la base de datos realizada con éxito ---" | tee -a $LOG_NAME_1
}


