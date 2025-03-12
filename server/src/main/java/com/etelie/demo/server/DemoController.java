package com.etelie.demo.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/demo")
public class DemoController {

    Logger logger = LoggerFactory.getLogger(DemoController.class);

    @RequestMapping(path = "/hello", method = RequestMethod.GET)
    public ResponseEntity<String> hello(
            @RequestParam("target") String target
    ) {
        String message = "Hello %s!".formatted(target);

        logger.debug(message);
        return ResponseEntity.ok().body(message);
    }

}
