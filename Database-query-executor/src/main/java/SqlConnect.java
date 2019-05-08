
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

public class SqlConnect {
    private static final File f = new File("init.txt");


    public static Connection start() {
        String address = "", user = "", password = "";
        List<String> strings = null;
        String[] splitLine;
        try {
            strings = FileUtils.readLines(f, Charset.defaultCharset());
            for (String s : strings) {
                splitLine = s.split("=");
                switch (splitLine[0]) {
                    case "username":
                        user = splitLine[1].trim();
                        System.out.println("username= "+user);
                        break;
                    case "password":
                        password = splitLine[1].trim();
                        System.out.println("password= "+password);
                        break;
                    case "hostname":
                        address ="jdbc:mysql://"+ splitLine[1].trim();
                        System.out.println("hostname= "+splitLine[1]);
                        break;
                    case "port":
                        address +=(":"+splitLine[1].trim());
                        System.out.println("port= "+splitLine[1]);
                        break;
                    case "database_name":
                        address+="/"+splitLine[1];
                        System.out.println("database_name= "+splitLine[1]);
                        break;
                }

            }
            System.out.println("address: "+ address );
        } catch (IOException e) {
            System.out.println("error io: " + e);
        }catch (Exception e){
            System.out.println(e.getMessage());
        }



        try {
            Connection con = DriverManager.getConnection(address, user, password);
            System.out.println("Connection Success");
            return con;
        } catch (SQLException e) {
            System.out.println("Connection Failed " +e.getMessage());
            Log.log("Connection Failed : "+e.getMessage()+"\rError Code : "+e.getErrorCode());
            return null;
        }


    }
}
