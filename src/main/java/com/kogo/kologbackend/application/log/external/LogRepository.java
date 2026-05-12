package com.kogo.kologbackend.application.log.external;

import com.kogo.kologbackend.application.log.dto.response.LogGetHourList;
import com.kogo.kologbackend.application.log.dto.response.LogHourRaw;
import com.kogo.kologbackend.domain.log.Log;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LogRepository extends JpaRepository<Log,Long> {

    @Query("select l from Log l where l.date <= :date")
    List<Log> findByDate(String date);

    List<Log> findByDateAndHour(@Param("date") String date, @Param("hour") Integer hour);

    boolean existsByUserIdAndDateAndHour(Long userId, String date, Integer hour);

    @Query("select l.date, l.hour from Log l group by l.date, l.hour order by l.date, l.hour")
    List<LogHourRaw> findByHour();
}
