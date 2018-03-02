package org.jax.mgi.mtb.ei.util;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.File;
import java.io.FileInputStream;
import com.jcraft.jsch.*;
import com.jcraft.jsch.JSchException;

/**
 * SFTP file transfer
 * @author sbn* 
 *  *Almost exactly the code from JCraft sftp example 
 */
public class SFTP {

    public static final int PORT = 22;
    private String host;
    private int port;
    private String user;
    private String pwd;

    public static void main(String[] args) {
        try {
            SFTP test = new SFTP("bhmtbdb01.jax.org", SFTP.PORT);
            test.login(args[0],args[1]);
            test.send("C:/sftp.test", "/usr/local/mgi/mtb/live/www/pathpics/");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public SFTP(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void login(String user, String password) {
        this.user = user;
        this.pwd = password;

    }

    public void send(String fileName, String serverPath) throws Exception {
      
        Session session = null;
        Channel channel = null;
        ChannelSftp channelSftp = null;
        
            try {
                JSch jsch = new JSch();
                session = jsch.getSession(this.user, this.host, this.port);
                session.setPassword(this.pwd);
                java.util.Properties config = new java.util.Properties();
                config.put("StrictHostKeyChecking", "no");
                session.setConfig(config);
                session.connect();
                channel = session.openChannel("sftp");
                channel.connect();
                channelSftp = (ChannelSftp) channel;
                channelSftp.cd(serverPath);
                File f = new File(fileName);
                if(checkNoFileExists(channelSftp, serverPath+"/"+f.getName())){
                    channelSftp.put(new FileInputStream(f), f.getName());
                }else{
                    throw new Exception("File "+f.getName()+" allready exists.");
                }    
            
            } finally {
                channelSftp.exit();
                channel.disconnect();
                session.disconnect();
                
            }
           
    }

    public void mkdir(String dir) throws Exception {

        Session session = null;
        Channel channel = null;
        ChannelSftp channelSftp = null;
        try{
            JSch jsch = new JSch();
            session = jsch.getSession(this.user, this.host, this.port);
            session.setPassword(this.pwd);
            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            session.connect();
            channel = session.openChannel("sftp");
            channel.connect();
            channelSftp = (ChannelSftp) channel;
            if(checkNoFileExists(channelSftp, dir)){
                channelSftp.mkdir(dir);
            }else{
                throw new Exception("Directory "+dir+" allready exists.");
            }
        
        } finally {
            channelSftp.exit();
            channel.disconnect();
            session.disconnect();

        }

    }
    
    private boolean checkNoFileExists(ChannelSftp channel, String file){
        boolean noFile = false;
        try{
            channel.ls(file);
        }catch(Exception e){
            // no such file, we can continue
            noFile = true;
        }
        
        return noFile;
    }
}
