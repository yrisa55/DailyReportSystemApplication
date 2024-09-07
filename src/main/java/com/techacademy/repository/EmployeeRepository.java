package com.techacademy.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.techacademy.entity.Employee;

public interface EmployeeRepository extends JpaRepository<Employee, String> {
    //　従業員コードで従業員を検索するメソッド
    Employee findByCode(String code);
}