package com.walker.core.service.dubbo;

import java.rmi.RemoteException;


import com.alibaba.dubbo.rpc.RpcContext;
import com.walker.core.service.service.*;

/**
 * Consumer
 *
 */
public class Consumer {
//	@Autowired
//    private ServiceClass ss;
	
    public static void main(String[] args) throws Exception {
//        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
//        		new String[] {"dubbo-dubbo.xml"});
//        context.start();
//        new Consumer(context);
    }
//    Consumer(ClassPathXmlApplicationContext context) throws RemoteException{
//    	
//    	//rpc cookie?
//    	RpcContext.getContext().getAttachment("index"); // 隐式传参，后面的远程调用都会隐式将这些参数发送到服务器端，类似cookie，用于框架集成，不建议常规业务使用
//    	
//    	ServiceDubbo service = (ServiceDubbo)context.getBean("serviceDubbo");
//        System.out.println(service.sayHello("args[]"));
//        
//        while(true) {
//        	
//        }
//    }
}