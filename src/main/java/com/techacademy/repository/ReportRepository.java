package com.techacademy.repository;
import com.techacademy.entity.Employee;
import com.techacademy.entity.Report;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;


public interface ReportRepository extends JpaRepository<Report, Integer>  {
    List<Report> findByEmployeeCodeAndReportDate(String code, LocalDate reportDate);


}

