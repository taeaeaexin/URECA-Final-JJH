package com.ureca.ocean.jjh.user.repository;

import com.ureca.ocean.jjh.user.entity.Attendance;
import com.ureca.ocean.jjh.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface AttendanceRepository extends JpaRepository<Attendance,Long> {
    boolean existsByDateAndUser(LocalDate today, User user);

    @Query("""
        SELECT a
          FROM Attendance a
         WHERE FUNCTION('MONTH', a.date) = :month
           AND FUNCTION('YEAR', a.date) = :year
           AND a.user = :user
    """)
    List<Attendance> findByMonthAndYear(@Param("year") int year, @Param("month") int month, @Param("user")User user);

    List<Attendance> findByUserIdAndDate(UUID userId, LocalDate date);
}
