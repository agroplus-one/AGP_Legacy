var ModalConfiguration = function (header, footer) {
    this.header = header;
    this.footer = footer;
    this.width = '6000px';
    this.height = '1000px';
};

var apiParams = {
    CONTEXT: 'API_NTF_CONTEXT',
    IFRAMEID: 'API_NTF_CONTEXT' + 'runtime5Container',
    NTF_CONTEXT_RESPONSE_TYPE: 'API_NTF_CONTEXT_RESPONSE',
    ruleInfo: '',
    responseFunction: 'defaultResponseFunction',
    modalConfiguration: new ModalConfiguration('Modal Header', 'Modal Footer', 500, 500)
};
var defaultResponseFunction = function (response) {
    var date = new Date();
    var strDate = date.getFullYear() + "-" + (date.getMonth() + 1) + "-" + date.getDate() + " " + date.getHours() + ":" + date.getMinutes() + ":" + date.getSeconds();
    console.log('***** ' + strDate + ' Funcion por defecto, debe declarar un manejador de la respuesta -> receptor mensaje de vuelta :' + JSON.stringify(response));
};
var apiNtfEnvironmentResponse = function () {
    this.domain;
    this.port;
    this.urlStaticContent;
    this.irisUrl;
    this.dbname;
    this.userLogin;
    this.userTerminal;
    this.wsUrl;
    this.wsProtocol;
    this.type;
};
var logUtils = new function () {
    this.withTime = true;
    this.log = function (strSalida) {
        if (this.withTime) {
            strSalida = this.prependTimeStr(strSalida);
        }
        console.log(strSalida);
    };
    this.error = function (strSalida) {
        if (this.withTime) {
            strSalida = this.prependTimeStr(strSalida);
        }
        console.error(strSalida);
    };
    this.prependTimeStr = function (strSalida) {
        var date = new Date();
        strSalida = date.getDate() + "-" + (date.getMonth() + 1) + "-" + date.getFullYear() + " " + date.getHours() + ":" + date.getMinutes() + ":" + date.getSeconds() + '----' + strSalida;
        return strSalida;
    };
};
var utils = new function () {
    this.getElement = function (id) {
        return document.getElementById(id);
    };
    this.createElement = function (id) {
        var element = document.createElement("div");
        element.id = id;
        document.body.appendChild(element);
        return element;
    };

    this.createStyleTag = function () {
        var css = '.modalRSIStyle {background:rgba(0,0,0,0.3);position: fixed; z-index: 1; left: 0;top: 0;width: 100%; height: 100%;min-height:100%!important; overflow: hidden; }';
        css = css.concat('.modal-content {position: relative;margin: auto;padding: 0;border: 0px;width: 100%;box-shadow: none;-webkit-animation-name: animatetop;-webkit-animation-duration: 0.4s;animation-name: animatetop;animation-duration: 0.4s;background-color: rgba(0,0,0,0.3);}');
        css = css.concat('@-webkit-keyframes animatetop {from {top:-300px; opacity:0} to {top:0; opacity:1}}');
        css = css.concat('@keyframes animatetop {from {top:-300px; opacity:0} to {top:0; opacity:1}}');
        css = css.concat('.close {float: right;font-weight: bold;opacity: 0.5;font-family: -apple-system,BlinkMacSystemFont,Segoe UI,Roboto,Helvetica Neue,Arial,sans-serif;width: 29px;color: #004C77;font-size: 22px;text-align: center;height: 25px;line-height: 1;}');
        css = css.concat('.close:hover,.close:focus {opacity: 0.75;text-decoration: none;cursor: pointer;}');
        css = css.concat('.modal-header {background-color: #fff;color: #004C77;border-radius: 0px;height: 27px;padding: 0px;border-bottom: 2px solid #004C77;font-family: -apple-system,BlinkMacSystemFont,Segoe UI,Roboto,Helvetica Neue,Arial,sans-serif!important;font-weight: normal!important;padding-left: 10px;padding-bottom: 5px;}');
        css = css.concat('.operation-body body{background: transparent!important;}');
        css = css.concat('.modal-footer {padding: 2px 16px;background-color: #5cb85c;color: white;}');
        css = css.concat('.operation-body{width:100%;height:calc(100vh - 44px)!important;min-height:calc(100vh - 44px)!important;max-height:calc(100vh - 44px)!important;border:0px solid!important;overflow:hidden!important;}');
        var head = document.head || document.getElementsByTagName('head')[0];
        var style = document.createElement('style');
        style.type = 'text/css';
        if (style.styleSheet) {
            style.styleSheet.cssText = css;
        } else {
            style.appendChild(document.createTextNode(css));
        }
        head.appendChild(style);
    };
    this.decodeMessage = function (messageString) {
        if (typeof JSON.parse !== undefined)
            return JSON.parse(messageString);
        else
            return JSON.decode(messageString);
    };
};
var UserData = function (url, data_base, terminalConnectionInfo) {
    this.IRIS_URL = url;
    this.DBNAME = data_base;
    this.USER_LOGIN = terminalConnectionInfo.USER_LOGIN;
    this.TERMINAL = terminalConnectionInfo.TERMINAL;
	this.SFD_VER = terminalConnectionInfo.SFD_VER;
}
var TerminalConectionInfo = function (user, terminal,sfd_ver) {
    this.USER_LOGIN = user;
    this.TERMINAL = terminal;
	this.SFD_VER = sfd_ver;
}
var RuleInfo = function (rule, data) {
    this.rule = rule;
    this.data = data;
}
var NtfEnvironmentValues = function (domain, port, url) {
    this.DOMAIN = domain;
    this.PORT = port;
    this.URL_STATICFILE_CONTEXT = url;
}
var OperationData = function () {
//Descripcio_Zamudio	
    this.ruleInfo = apiParams.ruleInfo;
    this.Zamudio = "";
    this.addInsaculationParams = true;
    this.loader = null;
    this.processMessageResponse = function (params) {
        window[apiParams.responseFunction](params);
    };
    this.qtip = "Ventana ABSIDE";
    this.saveTreePath = false;
}
var WsParams = function (url, protocol) {
    this.url = url;
    this.protocol = protocol;
}

var NtfApiRequest = function () {
    this.userData;
    this.ntfEnvironmentValues;
    this.operationData;
    this.wsParams;
}

var getNtfApiRequest = function () {
    console.log('ntfApiLib.getNtfApiRequest --- BEGIN');
    var findedEnvironment = false;
    var terminalConnectionInfo = new TerminalConectionInfo(apiNtfEnvironmentResponse.userName, apiNtfEnvironmentResponse.userTerminal,apiNtfEnvironmentResponse.sfd_ver );
    var ntfApiRequest = new NtfApiRequest();
    ntfApiRequest.operationData = new OperationData();
    ntfApiRequest.ntfEnvironmentValues = new NtfEnvironmentValues(apiNtfEnvironmentResponse.domain, apiNtfEnvironmentResponse.port, apiNtfEnvironmentResponse.urlStaticContent);
    ntfApiRequest.userData = new UserData(apiNtfEnvironmentResponse.irisUrl, null, terminalConnectionInfo);
    ntfApiRequest.wsParams = new WsParams(apiNtfEnvironmentResponse.wsUrl, apiNtfEnvironmentResponse.wsProtocol);
    ntfApiRequest.jwtToken = apiNtfEnvironmentResponse.jwtToken;
    ntfApiRequest.jwtTokenIrisUrl = apiNtfEnvironmentResponse.jwtTokenIrisUrl;
    return ntfApiRequest;
};

var ComunicationUtils = function (ruleInfo, terminal) {
    this.launchRt5Rule = function (webSocket, iframeId, data) {
        console.log('ntfApiLib.launchRt5Rule --- BEGIN --- iframeId ' + iframeId + " CONTEXT "
                + apiParams.CONTEXT);
        var websocketMsg = {
            terminal: terminal,
            contexto: apiParams.CONTEXT,
            action: 'set',
            data: data
        };
        webSocket.contextHandlers = {};
        webSocket.contextHandlers[apiParams.CONTEXT] = {rule: ruleInfo.rule, iframe: iframeId};
        webSocket.send(JSON.stringify(websocketMsg));
    };
    this.initWebSocketConnection = function (wsParams, processMessageResponse) {
        var webSocket = new WebSocket(wsParams.url, wsParams.protocol);
        webSocket.onopen = function () {
            console.log('ntfApiLib.runWebSocket --- Abierta conexión con el Websocket');
            var json = {
                terminal: terminal,
                contexto: apiParams.CONTEXT,
                action: 'set',
                data: {'accion_realizada': 'apertura'}
            };
            webSocket.send(JSON.stringify(json));
        };
        webSocket.onmessage = function (event) {
            {
                var receivedMsg = event.data;
                var jsonMsg = JSON.parse(receivedMsg);
                console.log("ntfApiLib.OnMessage -> Mensaje recibido: " + JSON.stringify(jsonMsg));
                if (jsonMsg.data !== undefined) {
                    if (webSocket.contextHandlers !== undefined) {
                        processMessageResponse(jsonMsg.data);
                    }
                } else {
                    var messageObject = {
                        message: 'MIDAS_OPEN_RULE',
                        messageCode: '',
                        data: {
                            context: apiParams.CONTEXT,
                            ruleName: 'Vvv_Inicia_Regla_Pre_J_Evt',
                            terminal: terminal,
                            jwtToken: getNtfApiRequest().jwtToken
                        },
                        noCss: ''
                    };
                    if (webSocket.contextHandlers !== undefined) {
                        sendMessageHtml5(apiParams.IFRAMEID, messageObject);
                    }
                }
            }

        };
        return webSocket;
    };
    this.leeWebSocket = function (webSocket) {
        console.log('ntfApiLib.leeWebSocket --- Lectura de la respuesta de la regla-------');
        var json = {
            terminal: terminal,
            contexto: apiParams.CONTEXT,
            action: 'get'
        };
        webSocket.send(JSON.stringify(json));
    };
}
var sendMessageHtml5 = function (destiny, messageObject) {
    console.log('ntfApiLib.sendMessageHtml5 --- iFrameCommunicator.sendMessage  --- from NTF: Se ha modificado la forma de obtener el ' +
            ' contextLabel. Ojo a posibles incidencias debido a este cambio.');
    console.log('ntfApiLib.sendMessageHtml5 --- iFrameCommunicator.sendMessage  --- from NTF: iFrameId', apiParams.IFRAMEID);
    console.log('ntfApiLib.sendMessageHtml5 --- iFrameCommunicator.sendMessage  --- from NTF: messageObject', messageObject);
    const targetOrigin = window.apiNtfEnvironmentResponse.irisUrl.split('.risa')[0] + '.risa';
    console.log('ntfApiLib sendMessageHtml5 targetOrigin: ' + targetOrigin);
    document.getElementById(destiny).contentWindow.postMessage(JSON.stringify(messageObject), targetOrigin);
};
var receiveHtml5Message = function (event) {
    console.log('ntfApiLib.receiveHtml5Message --- BEGIN')
    if (event.data !== undefined) {
        let responseType = utils.decodeMessage(event.data);
        console.log('ntfApiLib.receiveHtml5Message --- ' +event.data);
        if (responseType !== undefined) {
            if (responseType.type === apiParams.NTF_CONTEXT_RESPONSE_TYPE) {
                apiNtfEnvironmentResponse = utils.decodeMessage(event.data);
                console.log("ntfApiLib.receiveHtml5Message --- Recibimos los datos del entorno y el usuario", apiNtfEnvironmentResponse);
                var request = getNtfApiRequest();
                launchMidasRule(request);
            } else {
                sendMessageToNTF(JSON.parse(event.data));
            }
        }
    }
};

var sendMessageToNTF = function(object){
    const targetOrigin = document.location.ancestorOrigins[document.location.ancestorOrigins.length -1];
    console.log(`ntfApiLib.sendMessageToNTF --- targetOrigin: ${targetOrigin} message: ${JSON.stringify(object)}`);
    window.parent.postMessage(JSON.stringify(object), targetOrigin);
}

var ApiNtfUtils = {
    initRuleLaunch : function (modalConfiguration, ruleInfoData, responseManagementFunction) {
        console.log(`ntfApiLib.initRuleLaunch --- BEGIN`);
        console.log(`ntfApiLib.initRuleLaunch --- ruleInfoData: ${JSON.stringify(ruleInfoData)}`);
        apiParams.responseFunction = responseManagementFunction;
        apiParams.ruleInfo = ruleInfoData;
        apiParams.modalConfiguration = modalConfiguration;
        // Enviamos los datos de la regla que se desea abrir
        var message = {'message': 'NOTIFICA_API_NTF_CONTEXT_REQUEST', 'data': ruleInfoData};
        const targetOrigin = document.location.ancestorOrigins[document.location.ancestorOrigins.length -1];
        console.log(`ntfApiLib.initRuleLaunch --- targetOrigin: ${targetOrigin} message: ${JSON.stringify(message)}`);
        window.parent.postMessage(JSON.stringify(message), targetOrigin);
    },
    getTerminalInfo: function () {
        // En este metodo SIEMPRE devolvemos una promesa, por lo que hay que capturar los datos con then y capturar el error con catch
        if (terminalInfoStatus === 'WAIT') {
            console.log('Inside wait');
            return userDataPromise.then(response => {
                terminalInfoData = response;
                terminalInfoStatus = 'SUCCESS';
                return response;
            });
        } else if (terminalInfoStatus === 'SUCCESS') {
            console.log('Inside SUCCESS');
            return Promise.resolve(terminalInfoData);
        } else {
            console.log('Inside Failure');
            return Promise.reject('Error al obtener datos');
        }        
    },
	closeNTFContext: function () {
		var object = {};
		object.message = 'CLOSE_CONTEXT';
		object.data = {};
        const targetOrigin = document.location.ancestorOrigins[document.location.ancestorOrigins.length -1];
        console.log(`ntfApiLib.closeNTFContext --- targetOrigin: ${targetOrigin} message: ${JSON.stringify(object)}`);
		window.parent.postMessage(JSON.stringify(object), targetOrigin);
	},
    openRuleInNTFContext: function (on, windowName, ruleLongName) {
        let object = {};
		object.message = 'OPEN_RULE_IN_NTFCONTEXT';
		object.data = {
            on: on,
            windowName: windowName,
            ruleLongName: ruleLongName
        };
        const targetOrigin = document.location.ancestorOrigins[document.location.ancestorOrigins.length -1];
        console.log(`ntfApiLib.openRuleInNTFContext --- targetOrigin: ${targetOrigin} message: ${JSON.stringify(object)}`);
		window.parent.postMessage(JSON.stringify(object), targetOrigin);
    },
    openGEDContext: function (action, onSearchObject) {
    	let object = {};
    	object.message = 'OPEN_GED_CONTEXT';
    	object.data = {
    		action: action,
    		onSearchObject: onSearchObject
    	};
        const targetOrigin = document.location.ancestorOrigins[document.location.ancestorOrigins.length -1];
        console.log(`ntfApiLib.openGEDContext --- targetOrigin: ${targetOrigin} message: ${JSON.stringify(object)}`);
    	window.parent.postMessage(JSON.stringify(object), targetOrigin);
    },
    open360: function (jsonData){
        var object = {};
        object.message = 'NOTIFICA_VC360';
        object.data = {};
        object.data.on = {"COD_NRBE_EN":"","COD_ID_EXT_PERS":"","COD_PE":jsonData.COD_PE,"ID_EXT_PE":"","ID_INTERNO_PE":jsonData.ID_INTERNO_PE,"NOMBRE_PERSONA":"","TIPO_ACUERDO":"","VALOR_TIPO_ACUERDO":"","COD_CSB_OF":"","COD_DIG_CR_UO":"","COD_LINEA":"","ID_GRP_PD":"","ID_PDV":"","ID_TRFA_PDV":"","NOMB_TRFA_PDV":"","ID_INTERNO_EMPL_EP":"","USUARIO":"","NUM_SOCIO_ON":"","NUM_EFECTO":"","NOMBRE_LIBRADO":"","ID_CTA_CTBLE":"","NOMBRE_CTA_CTBLE":"","NUM_TARJETA_ON":"","POLIZA_ON":"","PLAN_DE_PENSIONES":"","NUM_TARJETA_TOKEN":"","NUM_TARJETA_ASTE":"","NOMBRE_DOC":"","FORMATO_DOC":"","SCAN_DUPLEX":"","TRANSMITIR_DOC":"","VALOR_CCC":"","NUMERO_EXPEDIENTE":"","TIPO_EXPEDIENTE":"","CRM_COD_NRBE_EN":"","CRM_ID_EXTERNO_PE":"","CRM_ID_INTERNO_PE":"","CRM_ID_INTERNO_EMPL_EP":"","CRM_COD_PE":"","CRM_NOM_CLIENTE":"","CRM_NOM_GT":"","COD_SIT_IRREG":"","IND_PERTN_GRP_PE":""};
        if(jsonData.COD_PE === 'J') object.data.action = 'OPEN_360_LEGAL_PERSON';
        else object.data.action = 'OPEN_360_PHYSICAL_PERSON'
        sendMessageToNTF(object);
    } 
};
var attachHtml5Event = function () {
    if (window.attachEvent) {
        window.attachEvent('onmessage', receiveHtml5Message);
    } else {
        addEventListener('message', receiveHtml5Message);
    }
}
var removeHtml5Event = function () {
    if (window.attachEvent) {
        window.removeEventListener('onmessage', receiveHtml5Message);
    } else {
        removeEventListener('message', receiveHtml5Message);
    }
}
var functionAux;
var terminalInfoStatus = 'WAIT';
var terminalInfoData;
var timestamp = Date.now();
const userDataPromise = new Promise(function (resolve, reject) {
    // Creamos un timeout a 200ms, para en el caso de que no reciba los datos del NTF, finalice la promesa con error.
    let myTimeOut = setTimeout(function() {
        if (terminalInfoData === 'WAIT') {
            reject('No hay comunicación con el NTF');
        }
    }, 200);
    functionAux = function (event) {
        clearTimeout(myTimeOut);
        if (event.data !== undefined) {
            let datosUsuario = utils.decodeMessage(event.data);
            if (datosUsuario && datosUsuario.userLogin) {
                let infoAux = {
                    userCenter: datosUsuario.userCenter,
                    userEntity: datosUsuario.userEntity,
                    userIp: datosUsuario.userIp,
                    userLogin: datosUsuario.userLogin,
                    userName: datosUsuario.userName,
                    userPuesto: datosUsuario.userPuesto,
                    userTerminal: datosUsuario.userTerminal
                };
                resolve(infoAux);
            } else {
                reject('Error al obtener datos');
            }
        } else {
            reject('Error al obtener datos');
        }
    };
    // Se añade temporalmente está función que es la que recoge los datos del terminal
    if (window.attachEvent) {
        window.attachEvent('onmessage', functionAux);
    } else {
        addEventListener('message', functionAux);
    }
    sendMessageToNTF({ message: 'NOTIFICA_API_NTF_CONTEXT_REQUEST', data: {} });
});
userDataPromise
    .then(function (response) {   
        timestamp = Date.now() - timestamp;
        console.log(timestamp + ': ', response);
        terminalInfoData = response;
        terminalInfoStatus = 'SUCCESS';
        return response;
    })
    .catch(function (reason) {
        console.error(reason);
        terminalInfoStatus = 'FAILURE';
        return reason;
    })
    .finally(function () {
        // Una vez que la promesa finalice, eliminamos los eventListener para añadir los que hay en attachHtml5Event.
        if (window.attachEvent) {
            window.removeEventListener('onmessage', functionAux);
        } else {
            removeEventListener('message', functionAux);
        }
        attachHtml5Event();
    });

function Modal(webSocket, iframeUrl, comunicationUtils, data) {
    var runtimeIframe;
    utils.createStyleTag();
    this.getModal = function () {
        var modal = document.createElement("DIV");
        modal.className = "modalRSIStyle ntfApi-modalRSIStyle";
        modal.id = "operModal";
        modal.appendChild(this.getModalContent(webSocket, iframeUrl));
        return modal;
    };
    this.getModalContent = function (webSocket, iframeUrl) {
        var modalContent = document.createElement("DIV");
        modalContent.id = "operModalContent";
        modalContent.className = "modal-content ntfApi-modal-content";
        modalContent.appendChild(this.getModalHeader());
        modalContent.appendChild(this.getMoldaBody(webSocket, iframeUrl));
        //modalContent.appendChild(this.getModalFooter());
        return modalContent;
    };
    this.getModalHeader = function () {
        var modalHeader = document.createElement("DIV");
        modalHeader.className = "modal-header ntfApi-modal-header";
        var spanCloseButtonTag = document.createElement('SPAN');
        spanCloseButtonTag.className = 'close ntfApi-close';
        spanCloseButtonTag.appendChild(document.createTextNode("x"));
        var modalHeaderText = document.createElement("H2");
        modalHeaderText.appendChild(document.createTextNode(apiParams.modalConfiguration.header));
        modalHeader.appendChild(spanCloseButtonTag);
        modalHeader.appendChild(modalHeaderText);
        // When the user clicks on <span> (x), close the modal
        spanCloseButtonTag.onclick = function () {
            var modal = document.getElementById("operModal");
            if (modal !== undefined) {
                document.body.removeChild(modal);
            }
        };
        return modalHeader;
    };
    this.getMoldaBody = function (webSocket, iframeUrl) {
        var modalBody = document.createElement('DIV');
        modalBody.className = 'modal-body ntfApi-modal-body';
        runtimeIframe = document.createElement('IFRAME');
        runtimeIframe.id = apiParams.IFRAMEID;
        runtimeIframe.className = 'operation-body ntfApi-operation-body';
        runtimeIframe.src = iframeUrl;
        runtimeIframe.onload = function () {
            comunicationUtils.launchRt5Rule(webSocket, runtimeIframe.id, data);
        };
        modalBody.appendChild(runtimeIframe);
        return modalBody;
    };
    this.getModalFooter = function () {
        var modalFooter = document.createElement('DIV');
        modalFooter.className = 'modal-footer ntfApi-modal-footer';
        var footerContent = document.createElement('H3');
        footerContent.appendChild(document.createTextNode(apiParams.modalConfiguration.footer));
        modalFooter.appendChild(footerContent);
        return modalFooter;
    };
}

function launchMidasRule(ntfApiRequest) {

    var comunicationUtils = new ComunicationUtils(ntfApiRequest.operationData.ruleInfo, ntfApiRequest.userData.TERMINAL);
    var webSocket = comunicationUtils.initWebSocketConnection(ntfApiRequest.wsParams, ntfApiRequest.operationData.processMessageResponse);
    var modal;
    function NtfApiTools() {
        this.openRule = function () {
            var contextContainerId = "_contenedorPrincipal";

            if (utils.getElement(contextContainerId) !== null && typeof utils.getElement(contextContainerId) !== 'undefined')
                utils.getElement(contextContainerId).style.display = 'block';
            var contextMidasOperationsContainerId = apiParams.CONTEXT + contextContainerId;
            var contextContainer = utils.createElement(contextMidasOperationsContainerId);
            if (contextContainer !== null) {
                //Mostrar capa de carga en área de trabajo
                var iframeUrl = null;
                if (ntfApiRequest.jwtTokenIrisUrl) {
                    iframeUrl = ntfApiRequest.jwtTokenIrisUrl;
                } else {
                    // Método viejo
                    iframeUrl = ntfApiRequest.userData.IRIS_URL + '?';
                    if (ntfApiRequest.userData.DBNAME !== null) {
                        iframeUrl = iframeUrl + 'dbname=' + ntfApiRequest.userData.DBNAME + "&";
                    }
                    iframeUrl = iframeUrl + 'EMPLC_USUARIO=' + ntfApiRequest.userData.USER_LOGIN;
                    iframeUrl = iframeUrl + '&EMPLC_TERMINAL=' + ntfApiRequest.userData.TERMINAL;
                    iframeUrl = iframeUrl + '&NVM_CONTEXTO=' + apiParams.CONTEXT;
                    iframeUrl = iframeUrl + '&RULE_LONG_NAME=' + ntfApiRequest.operationData.ruleInfo.rule;
                    iframeUrl = iframeUrl + '&RULE_MODE=0';
                    if (ntfApiRequest.operationData.Zamudio !== '') {
                        iframeUrl = iframeUrl + '&RULE_IMPLEM_NAME=' + ntfApiRequest.operationData.Zamudio;
                    } else {
                        iframeUrl = iframeUrl + '&RULE_IMPLEM_NAME=dumpData';
                    }
                    iframeUrl = iframeUrl + '&SFD_VER=' + ntfApiRequest.userData.SFD_VER;
                    iframeUrl = iframeUrl + '&NTF_SVR=http://' + ntfApiRequest.ntfEnvironmentValues.DOMAIN + ':' + ntfApiRequest.ntfEnvironmentValues.PORT + ntfApiRequest.ntfEnvironmentValues.URL_STATICFILE_CONTEXT;
                    iframeUrl = iframeUrl + '&type=ntfContext';
                }
                console.log('ntfApiLib.openRule--- Loading iframe url = ' + iframeUrl);
                modal = new Modal(webSocket, iframeUrl, comunicationUtils, ntfApiRequest.operationData.ruleInfo.data);
                document.body.appendChild(modal.getModal());
            } else {
                logUtils.error('ntfApiLib.openRule -- No se ha recuperado correctamente el nombre del contenedor donde mostrar la ventana');
            }
        };
    }

    var htmlEventHandler = {};
    htmlEventHandler.encodeMessage = function (object) {
        if (typeof JSON.stringify !== undefined)
            return JSON.stringify(object);
        else
            return JSON.encode(object);
    };
    /**
     * This method take a JSON string and returns the corresponding Javascript object or an array
     *
     * @param messageString - The JSON string to be decoded into a Javascript object
     */
    htmlEventHandler.processMessageEvent = function (eventObject) {
        //var domain=window.location.protocol + '//' + window.location.hostname + ':' + window.location.port;
        var messageObject = utils.decodeMessage(eventObject.data);
        if (messageObject !== undefined && messageObject.data !== undefined) {
            var object = messageObject.data.data;
            if (object !== undefined && object.NTF_CLAVE !== undefined && object.NTF_CLAVE === '@NOTIFY' && object.NTF_CONTEXTO !== undefined && object.NTF_CONTEXTO === apiParams.CONTEXT) {
                console.log("*** He escrito en memoria compartida -> " + object);
                comunicationUtils.leeWebSocket(webSocket);
                htmlEventHandler.removeEvent();
                if (object.NTF_CONTEXT_ACTION === 'CLOSE') {
                    var modal = document.getElementById("operModal");
                    if (modal !== undefined) {
                        document.body.removeChild(modal);
                    }
                }
            }
        }
        ;
    };
    htmlEventHandler.attachEvent = function () {
        if (window.attachEvent) {
            window.attachEvent('onmessage', htmlEventHandler.processMessageEvent);
        } else {
            addEventListener('message', htmlEventHandler.processMessageEvent);
        }
    };
    htmlEventHandler.removeEvent = function () {
        if (window.attachEvent) {
            window.removeEventListener('onmessage', htmlEventHandler.processMessageEvent);
        } else {
            removeEventListener('message', htmlEventHandler.processMessageEvent);
        }
    };
    htmlEventHandler.attachEvent();
    var ntfApi = new NtfApiTools();
    if (ntfApiRequest.operationData.ruleInfo !== "") {
        ntfApi.openRule();
    }
}