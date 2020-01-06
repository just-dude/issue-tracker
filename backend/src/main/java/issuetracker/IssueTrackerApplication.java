package issuetracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.ApplicationPidFileWriter;

import java.nio.file.Path;
import java.nio.file.Paths;

@SpringBootApplication
public class IssueTrackerApplication {

	public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(IssueTrackerApplication.class);
        springApplication.addListeners(new ApplicationPidFileWriter(System.getProperty("pid.path")));
        springApplication.run(args);
	}

}
