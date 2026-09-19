package com.twb.repository;

import com.twb.entity.WaitingItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WaitingRepository extends JpaRepository<WaitingItem, Long> {
}
