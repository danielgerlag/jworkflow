package net.jworkflow.sample08;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import net.jworkflow.kernel.interfaces.WorkflowHost;
import net.jworkflow.WorkflowModule;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    public static void main(String[] args) throws Exception {        
        Logger rootLogger = Logger.getLogger("");
        rootLogger.setLevel(Level.SEVERE); 
        
        String str = readResource("workflow5.json");
        //System.out.println(str);
        WorkflowModule module = new WorkflowModule();
        module.build();
        WorkflowHost host = module.getHost();
        module.getLoader().loadFromJson(str);
        
        host.start();
        
        MyData data = new MyData();
        data.value1 = 2;
        data.value2 = 3;
        data.collection1 = new Object[2];
        String id = host.startWorkflow("test-workflow", 1, data);
        System.out.println("started workflow " + id);
        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();        
        
        System.out.println("shutting down...");
        host.stop();
    }

    private static String readResource(String name) throws IOException, UnsupportedEncodingException {
        
        URL url = Main.class.getClassLoader().getResource(name);
        System.out.println(url.getFile());
        URLConnection conn = url.openConnection();
        byte[] data;
        try (InputStream stream = (InputStream)conn.getContent()) {
            data = new byte[(int) conn.getContentLength()];
            stream.read(data);
        }
        String str = new String(data, "UTF-8");
        return str;
    }
}
