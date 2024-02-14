package com.hjertelundh.routes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/routes")
public class RouteController {
    @Autowired
    private RouteService routeService;
    @GetMapping
    public ResponseEntity<List<Route>> getAllRoutes() {
        return new ResponseEntity<List<Route>>(routeService.allRoutes(), HttpStatus.OK);
    }
}
