package com.adof.gameserver.tas;

/**
 * Created by 15692 on 23/06/16.
 *
 * Used for spring-boot to run it as a standalone application.
 *
 Note:
 -----
 Make the application executable
 Although it is possible to package this service as a traditional WAR file for deployment to an
 external application server, the simpler approach demonstrated below creates a standalone
 application. You package everything in a single, executable JAR file, driven by a good old Java
 main() method. Along the way, you use Spring’s support for embedding the Tomcat servlet container
 as the HTTP runtime, instead of deploying to an external instance.

 */
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;

@SpringBootApplication
public class Application extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(Application.class);
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(Application.class, args);
    }

}