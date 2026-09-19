package com.twb.repository;

import com.twb.entity.DoneTask;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DoneTaskRepository extends JpaRepository<DoneTask, Long> {
}
