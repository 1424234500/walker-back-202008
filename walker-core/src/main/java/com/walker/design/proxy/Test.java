package com.walker.design.proxy;


import com.walker.design.state.GumballMachine;
import com.walker.design.state.State;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Test {
    private static final Logger log = LoggerFactory.getLogger(Test.class);

    public static void main(String[] argv){

//        UserMapper userMapper = UserMapperFactory.getUserMapper();
//        log.info(userMapper.getName());
//        log.info(userMapper.hello());

        UserMapper userMapper1 = UserMapperFactory.getMapper(UserMapper.class);
        log.info(userMapper1.getName());
        log.info(userMapper1.hello());




    }



}



