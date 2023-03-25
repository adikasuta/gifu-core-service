package com.gifu.coreservice.service;

import com.gifu.coreservice.entity.Bill;
import com.gifu.coreservice.repository.TimelineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TimelineService {
    @Autowired
    private TimelineRepository timelineRepository;


    public void startProductionTimeline(Bill bill){

    }
}
