package com.crashdata.back.domain.crash;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CrashRepository extends JpaRepository<Crash, Long> {
}
