package com.twb.repository;

import com.twb.entity.Milestone;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MilestoneRepository extends JpaRepository<Milestone, Long> {
}
