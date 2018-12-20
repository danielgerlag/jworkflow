package net.jworkflow.sample08;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import net.jworkflow.kernel.interfaces.WorkflowHost;
import net.jworkflow.WorkflowModule;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    public static void main(String[] args) throws Exception {        
        
        
        String str = readResource("workflow.json");
        //System.out.println(str);
        WorkflowModule module = new WorkflowModule();
        module.build();
        WorkflowHost host = module.getHost();
        module.getLoader().loadDefinition(str);
        
        host.start();
        
        String id = host.startWorkflow("test-workflow", 1, null);
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
