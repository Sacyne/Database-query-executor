
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.DateFormat;

public class Log {


    public static void log(String log){
        java.util.Date aujourdhui = new java.util.Date();
        DateFormat mediumDateFormat = DateFormat.getDateTimeInstance(
                DateFormat.MEDIUM,
                DateFormat.MEDIUM);
        File f = new File("log.txt");
        System.out.println(log);
        try{
            FileUtils.write(f,mediumDateFormat.format(aujourdhui)+"\r", Charset.defaultCharset(),true);
            FileUtils.write(f,log+"\r", Charset.defaultCharset(),true);
            System.out.println("Log Write Successfully");
        }catch(IOException e){
            System.out.println(e.getMessage());
            e.printStackTrace();
        }


    }
}
