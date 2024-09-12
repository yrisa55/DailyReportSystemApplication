package com.techacademy.repository;
import com.techacademy.entity.Employee;
import com.techacademy.entity.Report;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface ReportRepository extends JpaRepository<Report, Integer>  {
    @Query("SELECT r FROM Report r WHERE r.employee.code = :employeeCode AND r.reportDate = :reportDate AND r.id <> :id")
    List<Report> findByEmployeeCodeAndReportDateExcludingId(@Param("employeeCode") String code, @Param("reportDate") LocalDate reportDate, @Param("id") int id);

    List<Report> findByEmployee(Employee employee);
    
    List<Report> findReportsByEmployee(Employee employee);
    
    List<Report> findByEmployeeCodeAndReportDate(String code, LocalDate reportDate);
    
}

