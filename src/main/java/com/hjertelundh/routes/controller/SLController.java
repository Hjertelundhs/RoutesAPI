package com.hjertelundh.routes.controller;

import java.util.List;

import com.hjertelundh.routes.dto.BusLineDto;
import com.hjertelundh.routes.logic.SLLogic;
import lombok.AllArgsConstructor;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@CrossOrigin(origins = "*")
@AllArgsConstructor
@RestController
@RequestMapping("/routes")
public class SLController {

    private final SLLogic slLogic;

    @GetMapping("/topten")
    public List<BusLineDto> getTopTestBusRoutes() {
        return slLogic.aggregateLogic();
    }
}