
package com.walker.core.service.webservice.client.ServiceClass;

import java.util.List;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.Action;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.2.4-b01
 * Generated source version: 2.2
 * 
 */
@WebService(name = "ServiceClassImpl", targetNamespace = "http://serviceImpl.service.util/")
@XmlSeeAlso({
    ObjectFactory.class
})
public interface ServiceClassImpl {


    /**
     * 
     * @param arg0
     * @return
     *     returns java.lang.String
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "test", targetNamespace = "http://serviceImpl.service.util/", className = "util.service.webservice.client.ServiceClass.Test")
    @ResponseWrapper(localName = "testResponse", targetNamespace = "http://serviceImpl.service.util/", className = "util.service.webservice.client.ServiceClass.TestResponse")
    @Action(input = "http://serviceImpl.service.util/ServiceClassImpl/testRequest", output = "http://serviceImpl.service.util/ServiceClassImpl/testResponse")
    public String test(
        @WebParam(name = "arg0", targetNamespace = "")
        String arg0);

    /**
     * 
     * @param arg2
     * @param arg1
     * @param arg0
     * @return
     *     returns java.lang.Object
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "doClassMethod", targetNamespace = "http://serviceImpl.service.util/", className = "util.service.webservice.client.ServiceClass.DoClassMethod")
    @ResponseWrapper(localName = "doClassMethodResponse", targetNamespace = "http://serviceImpl.service.util/", className = "util.service.webservice.client.ServiceClass.DoClassMethodResponse")
    @Action(input = "http://serviceImpl.service.util/ServiceClassImpl/doClassMethodRequest", output = "http://serviceImpl.service.util/ServiceClassImpl/doClassMethodResponse")
    public Object doClassMethod(
        @WebParam(name = "arg0", targetNamespace = "")
        String arg0,
        @WebParam(name = "arg1", targetNamespace = "")
        String arg1,
        @WebParam(name = "arg2", targetNamespace = "")
        List<Object> arg2);

}
