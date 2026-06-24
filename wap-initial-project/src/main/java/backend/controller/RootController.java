package backend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller that handles root path '/' and forwards to the frontend index.html
 */
@Controller
public class RootController {

    /**
     * Root endpoint - forwards to the frontend index.html
     * @return forward path to index.html
     */
    @GetMapping("/")
    public String index() {
        return "forward:/frontend/index.html";
    }
}

