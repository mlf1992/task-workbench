package com.twb.repository;

import com.twb.entity.FocusItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FocusRepository extends JpaRepository<FocusItem, Long> {
    List<FocusItem> findByScope(String scope);
}
