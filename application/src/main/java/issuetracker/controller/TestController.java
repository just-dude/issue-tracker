package issuetracker.controller;

import issuetracker.AppConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
public class TestController {

    private static final Logger LOG = LogManager.getLogger(TestController.class);

    @GetMapping(path = "/")
    public ModelAndView rootToFrontend() {
        return new ModelAndView("redirect:" + "/frontend/index.html");
    }
}
