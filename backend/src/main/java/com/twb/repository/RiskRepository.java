package com.twb.repository;

import com.twb.entity.ProjectRisk;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RiskRepository extends JpaRepository<ProjectRisk, Long> {
}
