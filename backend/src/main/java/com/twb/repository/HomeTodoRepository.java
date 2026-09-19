package com.twb.repository;

import com.twb.entity.HomeTodo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HomeTodoRepository extends JpaRepository<HomeTodo, Long> {
}
