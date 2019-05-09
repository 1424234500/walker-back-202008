
package com.walker.core.service.webservice.jdk7.client.ServiceClass;

import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceFeature;

import com.walker.common.util.Tools;
import com.walker.core.service.webservice.WebServiceTool;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.2.4-b01
 * Generated source version: 2.2
 * 
 */
@WebServiceClient(name = "ServiceClassImplService", targetNamespace = "http://serviceImpl.service.util/", wsdlLocation = "http://localhost:8091/ServiceClass?wsdl")
public class ServiceClassImplService
    extends Service
{

    private final static URL SERVICECLASSIMPLSERVICE_WSDL_LOCATION;
    private final static WebServiceException SERVICECLASSIMPLSERVICE_EXCEPTION;
    private final static QName SERVICECLASSIMPLSERVICE_QNAME = new QName("http://serviceImpl.service.util/", "ServiceClassImplService");

    static {
        URL url = null;
        WebServiceException e = null;
        try {
            url = new URL("http://localhost:8091/ServiceClass?wsdl");
        } catch (MalformedURLException ex) {
            e = new WebServiceException(ex);
        }
        SERVICECLASSIMPLSERVICE_WSDL_LOCATION = url;
        SERVICECLASSIMPLSERVICE_EXCEPTION = e;
    }

    public ServiceClassImplService() {
        super(__getWsdlLocation(), SERVICECLASSIMPLSERVICE_QNAME);
    }

    public ServiceClassImplService(WebServiceFeature... features) {
        super(__getWsdlLocation(), SERVICECLASSIMPLSERVICE_QNAME, features);
    }

    public ServiceClassImplService(URL wsdlLocation) {
        super(wsdlLocation, SERVICECLASSIMPLSERVICE_QNAME);
    }

    public ServiceClassImplService(URL wsdlLocation, WebServiceFeature... features) {
        super(wsdlLocation, SERVICECLASSIMPLSERVICE_QNAME, features);
    }

    public ServiceClassImplService(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public ServiceClassImplService(URL wsdlLocation, QName serviceName, WebServiceFeature... features) {
        super(wsdlLocation, serviceName, features);
    }

    /**
     * 
     * @return
     *     returns ServiceClassImpl
     */
    @WebEndpoint(name = "ServiceClassImplPort")
    public ServiceClassImpl getServiceClassImplPort() {
        return WebServiceTool.webserviceConfig(super.getPort(new QName("http://serviceImpl.service.util/", "ServiceClassImplPort"), ServiceClassImpl.class));
    }

    /**
     * 
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns ServiceClassImpl
     */
    @WebEndpoint(name = "ServiceClassImplPort")
    public ServiceClassImpl getServiceClassImplPort(WebServiceFeature... features) {
        return WebServiceTool.webserviceConfig(super.getPort(new QName("http://serviceImpl.service.util/", "ServiceClassImplPort"), ServiceClassImpl.class, features));
    }

    private static URL __getWsdlLocation() {
        if (SERVICECLASSIMPLSERVICE_EXCEPTION!= null) {
            throw SERVICECLASSIMPLSERVICE_EXCEPTION;
        }
        return SERVICECLASSIMPLSERVICE_WSDL_LOCATION;
    }

}