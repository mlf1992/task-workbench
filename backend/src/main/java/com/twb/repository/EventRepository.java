package com.twb.repository;

import com.twb.entity.ScheduleEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventRepository extends JpaRepository<ScheduleEvent, Long> {
    List<ScheduleEvent> findByDkey(String dkey);
}
