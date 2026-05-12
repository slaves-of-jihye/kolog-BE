package com.kogo.kologbackend.application.log.external;

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

    @Query("select distinct l.hour from Log l where l.date=:date order by l.hour asc")
    List<Integer> findHourByDate(@Param("date") String date);
}
