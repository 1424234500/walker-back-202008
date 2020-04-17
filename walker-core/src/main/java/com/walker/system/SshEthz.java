package com.walker.system;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;
import com.walker.common.util.Tools;
import com.walker.core.exception.InfoException;
import com.walker.core.mode.Result;
import com.walker.core.mode.Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


/**
 * ssh工具
 * 
 * @author Walker
 *
 */
public class SshEthz {
    Server server;
    Result result = null;
    int timeout = 5000;


    public SshEthz(Server server){
        this.server = server;
    }

    public SshEthz(String ip, String id, String pwd){
        this.server = new Server(ip, id, pwd);
    }

    public int getTimeout() {
        return timeout;
    }

    public SshEthz setTimeout(int timeout) {
        this.timeout = timeout;
        return this;
    }
    /**
     * 远程执行shell脚本或者命令
     * @param cmd
     *      即将执行的命令
     * @return
     *      命令执行完后返回的结果值
     */
    public String execute(String cmd)  {
        result = new Result();
        result.setKey(cmd);
        Session session = null;
        Connection conn = null;
        try {
            conn = this.getConnection();
            session = conn.openSession();  // 打开一个会话
            session.execCommand(cmd);      // 执行命令
            String res = processStdout(session.getStdout(), this.server.getEncode());

            //如果为得到标准输出为空，说明脚本执行出错了
            if(res == null || res.length() == 0){
                res = processStdout(session.getStderr(), this.server.getEncode());
            }
            this.result.setRes(res);
        } catch (Exception e) {
            result.setIsOk(0).addInfo(e.getMessage());
        } finally {
            result.setCost( System.currentTimeMillis() - result.getTimeStart() );
            if(conn != null ){
                conn.close();
            }
            if (session != null) {
                session.close();
            }
        }
        return String.valueOf( result.getRes() );
    }
    /**
     * 登录主机
     * @return
     *      登录成功返回true，否则返回false
     */
    private Connection getConnection() throws InfoException {
        boolean isAuthenticated = false;
        String info = this.server.toSsh();
        Connection conn = null;
        try {
            conn = new Connection(this.server.getIp());
            conn.connect(); // 连接主机
            isAuthenticated = conn.authenticateWithPassword(this.server.getId(), this.server.getPwd()); // 认证
            if(isAuthenticated){
                Tools.out("auth ok ", this.server);
            } else {
                conn = null;
                info += " auth err ";
            }
        } catch (IOException e) {
            conn = null;
            info += e.getMessage();
        }
        if(conn == null){
            throw new InfoException(info);
        }

        return conn;
    }


    /**
     * 解析脚本执行返回的结果集
     * @param in 输入流对象
     * @param charset 编码
     * @return
     *       以纯文本的格式返回
     */
    private static String processStdout(InputStream in, String charset){
        InputStream stdout = new StreamGobbler(in);
        StringBuffer buffer = new StringBuffer();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(stdout, charset));
            String line = null;
            int i = 0;
            while((line = br.readLine()) != null){
                i++;
                if(i > 1){
                    buffer.append("\n");
                }
                buffer.append(line);
            }
        } catch (Exception e) {
            Tools.out("解析脚本出错：" + e.getMessage());
            throw new RuntimeException(e);
        }
        return buffer.toString();
    }

    @Override
    public String toString() {
        return this.server.toSsh( this.result != null ? this.result.toString() : "" );
    }
}