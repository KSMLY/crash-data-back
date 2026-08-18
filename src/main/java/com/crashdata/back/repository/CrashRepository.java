package com.crashdata.back.repository;

import com.crashdata.back.entity.Crash;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CrashRepository extends JpaRepository<Crash, Long> {
}
