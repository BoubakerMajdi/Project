package org.project;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RootController {

    @GetMapping("/")
    public String home() {
        return "Order Orchestrator is running. Use /orders endpoints or open ui/index.html.";
    }
}
