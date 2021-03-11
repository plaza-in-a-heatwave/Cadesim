package com.benberi.cadesim.util;

import java.sql.Connection;  
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import com.benberi.cadesim.GameContext;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;  
   
public class SqlDatabase {
	
	static int lport;
    static String rhost;
    static int rport;
    public static void createSession(){
        String user = "admin";
        String password = "";
        String host = "198.54.115.142";
        int port=22;
        try
            {
            JSch jsch = new JSch();
            Session session = jsch.getSession(user, host, port);
            lport = 3306;
            rhost = "localhost";
            rport = 3306;
            session.setPassword(password);
            session.setConfig("StrictHostKeyChecking", "no");
            System.out.println("Establishing Connection...");
            session.connect();
            int assinged_port=session.setPortForwardingL(lport, rhost, rport);
            System.out.println("localhost:"+assinged_port+" -> "+rhost+":"+rport);
            }
        catch(Exception e){System.err.print(e);}
    }
    
    public static void connect(String username, String password, GameContext context) {  
	    try{
	    	createSession();
	    } catch(Exception ex){
	        ex.printStackTrace();
	    }
        Connection con = null;
        String driver = "com.mysql.jdbc.Driver";
        String url = "jdbc:mysql://" + rhost +":" + lport + "/";
        String db = "globjpqo_globalcadesim";
        String dbUser = "globjpqo_gcuser1";
        String dbPasswd = "kT+wbxD!#}ps";
        try{
	        Class.forName(driver);
	        con = DriverManager.getConnection(url+db, dbUser, dbPasswd);
	        try{
	        	Statement st = con.createStatement();
		        String sql = "select * from account where aname=? and password=?";
		
		        int update = st.executeUpdate(sql);
		        if(update >= 1){
		        	System.out.println("Row is updated.");
		        }
		        else{
		        	System.out.println("Row is not updated.");
		        }
	        }catch (SQLException s){
	        	System.out.println("SQL statement is not executed!");
	        }
         }catch (Exception e){
        	  e.printStackTrace();
         }
    }
	/** 
//	* Connect to a sample database 
//	*/  
//	public static void connect(String username, String password, GameContext context) {  
//        Connection conn = null;  
//        try {  
//            // db parameters  
//        	
//            String url = "jdbc:mysql://198.54.115.142:3306/globjpqo_globalcadesim?" + "user=globjpqo_gcuser1&password=kT+wbxD!#}ps";  
//            conn = DriverManager.getConnection(url);  
//              
//            System.out.println("Connection to database has been established.");
////            String query = "select * from account where aname=? and password=?";
////            PreparedStatement stmt = conn.prepareStatement(query);
////            //Parameters
////            stmt.setString(1, username);
////            stmt.setString(2, password);
////            //Execute
////            ResultSet rs = stmt.executeQuery();
////	        if(rs.next()) {
////	        	Gdx.app.postRunnable(new Runnable() {
////        			@Override
////        			public void run() {
////        	        	ScreenManager.getInstance().showScreen(ScreenEnum.SELECTION, context);
////        			}
////            		
////            	});
////	        }else {
////	        	Gdx.app.postRunnable(new Runnable() {
////        			@Override
////        			public void run() {
////        	        	ScreenManager.getInstance().showScreen(ScreenEnum.LOBBY, context);
////                    	context.getLobbyScreen().setPopupMessage("Unable to login.");
////                    	context.getLobbyScreen().showPopup();
////        			}
////            		
////            	});
////	        }
//        }catch(Exception e) {
//        	System.out.println(e);
//        }
//    }
}  
