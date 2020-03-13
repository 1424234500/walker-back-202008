package com.walker.system;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;
import com.walker.common.util.Tools;
import com.walker.core.exception.InfoException;
import com.walker.core.mode.Server;

import java.io.*;
import java.util.LinkedHashMap;
import java.util.Map;


/**
 * ssh工具
 * 
 * @author Walker
 *
 */
public class Ssh {
    Server server;

    Map<String, Action> history = new LinkedHashMap<>();


    Connection conn;



    public Ssh(Server server){
        this.server = server;
    }

    public Ssh(String ip, String id, String pwd){
        this.server = new Server(ip, id, pwd);
    }

    public Ssh close(){
        if(this.conn != null){
            this.conn.close();
            this.conn = null;
        }
        return this;
    }
    /**
     * 登录主机
     * @return
     *      登录成功返回true，否则返回false
     */
    public Connection getConnection() throws InfoException {
        if(this.conn != null && this.conn.isAuthenticationComplete()){
            return this.conn;
        }

        boolean isAuthenticated = false;
        this.conn = null;
        long startTime = System.currentTimeMillis();
        try {
            this.conn = new Connection(this.server.getIp());
            this.conn.connect(); // 连接主机

            isAuthenticated = conn.authenticateWithPassword(this.server.getId(), this.server.getPwd()); // 认证
            if(isAuthenticated){
                Tools.out("auth ok ", this.server);
            } else {
                this.conn = null;
                throw  new InfoException("auth error ");
            }
        } catch (IOException e) {
            this.conn = null;
            throw new InfoException(e);
        }

        return this.conn;
    }

    /**
     * 远程执行shell脚本或者命令
     * @param cmd
     *      即将执行的命令
     * @return
     *      命令执行完后返回的结果值
     */
    public String execute(String cmd) throws InfoException {
        String result = "";
        Session session = null;
        Action action = new Action();
        action.cmd = cmd;

        try {
            session = this.getConnection().openSession();  // 打开一个会话
            session.execCommand(cmd);      // 执行命令
            result = processStdout(session.getStdout(), this.server.getEncode());

            //如果为得到标准输出为空，说明脚本执行出错了
            if(result == null || result.length() == 0){
                result = processStdout(session.getStderr(), this.server.getEncode());
            }
            action.res = result;
        } catch (IOException e) {
            Tools.out("【执行命令失败】\n执行的命令如下：\n" + cmd + "\n" + e.getMessage());
            throw new InfoException(e);
        } finally {
            action.cost = System.currentTimeMillis() - action.time;
            Tools.out(action );
            this.history.put(cmd, action);
            if (session != null) {
                session.close();
            }
        }

        return result;
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
            while((line = br.readLine()) != null){
                buffer.append(line + "\n");
            }
        } catch (UnsupportedEncodingException e) {
            Tools.out("解析脚本出错：" + e.getMessage());
            throw new RuntimeException(e);
        } catch (IOException e) {
            Tools.out("解析脚本出错：" + e.getMessage());
            throw new RuntimeException(e);
        }
        return buffer.toString();
    }


}

class Action{
    long time = System.currentTimeMillis();
    String cmd;
    String res;
    long cost;

    @Override
    public String toString() {
        return "Action{" +
                "time=" + time +
                ", cmd='" + cmd + '\'' +
                ", res='" + res + '\'' +
                ", cost=" + cost +
                '}';
    }
}
