package com.bearhive.bearhive.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bearhive.bearhive.Model.Part;

@Repository
public interface PartRepository extends JpaRepository<Part, Long> {
    List<Part> findByTestId(Long testId);
}
