package com.kogo.kologbackend.application.log.external;

import com.kogo.kologbackend.domain.log.Log;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface LogRepository extends JpaRepository<Log,Long> {

    @Query("select l from Log l where l.date <= :date")
    List<Log> findByDate(String date);
}
