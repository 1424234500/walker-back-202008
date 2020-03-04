package com.walker.core.tool;

import com.walker.common.util.Tools;
import com.walker.core.exception.InfoException;
import com.walker.core.mode.Server;
import org.junit.Test;

import static org.junit.Assert.*;

public class SshTest {

    @Test
    public void getConnection() throws InfoException {

        Server server = new Server("39.106.111.11", "walker", "");
        Ssh ssh = new Ssh(server);
        Tools.out(ssh.execute("ls;date;ll;echo $PATH"));
        Tools.out(ssh.execute("echo 1"));
        Tools.out(ssh.execute("echo 2"));
        ssh.close();


    }
}