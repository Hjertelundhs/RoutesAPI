package com.hjertelundh.routes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RouteService {
    @Autowired
    private RouteRepository routeRepository;
    public List<Route> allRoutes() {
        return routeRepository.findAll();
    }
}
