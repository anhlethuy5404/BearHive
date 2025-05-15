package com.bearhive.bearhive.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bearhive.bearhive.Model.CourseModule;

@Repository
public interface CourseModuleRepository extends JpaRepository<CourseModule, Long>{

}
