package com.techacademy.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.techacademy.constants.ErrorKinds;
import com.techacademy.entity.Report;
import com.techacademy.repository.ReportRepository;
import com.techacademy.repository.EmployeeRepository;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReportService {
    
    private final ReportRepository reportRepository;
    
    @Autowired
    public ReportService(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }
        
    @Autowired
    private EmployeeRepository employeeRepository;
    
    
    // 日報一覧表示処理 全件を検索して返す
    public List<Report> findAll() {
        // リポジトリのfindAllメソッドを呼び出す
        return reportRepository.findAll();
    }
    
    @Transactional
    // 日報保存
    public ErrorKinds save (Report report) {
        
        // 送信された社員番号と日付を取得
        String employeeCode = report.getEmployee().getCode();
        System.out.println("employeeCode:" + employeeCode);
        System.out.println("report:" + report.getReportDate());
        System.out.println("report.getReportDate() before save:" + report.getReportDate());
        
        // 重複チェック
        List<Report> existingReport = reportRepository.findByEmployeeCodeAndReportDate(employeeCode, report.getReportDate());
        
        if (existingReport.size() != 0) {
                System.out.println("重複");
                return ErrorKinds.DUPLICATE_EXCEPTION_ERROR; // 重複エラーを返す
  
        }
       
        System.out.println("report.getReportDate() after 重複チェック:" + report.getReportDate());
        
        report.setDeleteFlg(false);

        LocalDateTime now = LocalDateTime.now();
        report.setCreatedAt(now);
        report.setUpdatedAt(now);
        
        System.out.println("report before save:" + report.getReportDate());

        reportRepository.save(report);
        return ErrorKinds.SUCCESS;
    }
    

    
 //　従業員更新
    // --- 追加ここから ----
    public ErrorKinds update(Report report) {
        
            String employeeCode = report.getEmployee().getCode();
        
            List<Report> existingReport = reportRepository.findByEmployeeCodeAndReportDate(employeeCode, report.getReportDate());
            
            if (existingReport.size() != 0) {
                System.out.println("重複");
                return ErrorKinds.DATECHECK_ERROR;
            }
            
            report.setDeleteFlg(false);
            
            LocalDateTime now = LocalDateTime.now();
            report.setUpdatedAt(now);
            
            this.reportRepository.save(report);
            
            return ErrorKinds.SUCCESS;

    }

    // 1件を検索
    public Report findById(int id) {
        // findByIdで検索
        Optional<Report> option = reportRepository.findById(id);
        // 取得できなかった場合はnullを返す
        Report report = option.orElse(null);
        
        if (report != null) {
            System.out.println("データベースから取得したレポート：");
            System.out.println("ID:" + report.getId());
            System.out.println("タイトル:" + report.getTitle());
            System.out.println("日付:" + report.getReportDate());
            System.out.println("内容:" + report.getContent());
        }
        return report;
    }
    
    

}
