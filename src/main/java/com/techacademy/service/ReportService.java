package com.techacademy.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.techacademy.constants.ErrorKinds;
import com.techacademy.constants.ErrorMessage;
import com.techacademy.entity.Employee;
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
    
    // 日報保存
    public ErrorKinds save (Report report) {
       
        report.setDeleteFlg(false);

        LocalDateTime now = LocalDateTime.now();
        report.setCreatedAt(now);
        report.setUpdatedAt(now);

        reportRepository.save(report);
        return ErrorKinds.SUCCESS;
    }
    
    //　社員番号重複チェック
    public ErrorKinds create(Employee employee) {
        // 同じ社員番号が存在するか確認
        Employee existingEmployee = employeeRepository.findByCode(employee.getCode());
        if(existingEmployee != null) {
           return ErrorKinds.DUPLICATE_EXCEPTION_ERROR;
                    
        }
        return ErrorKinds.SUCCESS;  
        }
    
 //　従業員更新
    // --- 追加ここから ----
    public ErrorKinds update(Report report) {
        
        Report existingReport = findById(report.getId());
        
        if (existingReport != null) {
            // 作成日時は既存のものを保持
            report.setCreatedAt(existingReport.getCreatedAt());
  
        }
        
        //よく分からないけど上を真似した
        report.setDeleteFlg(false);
        
        LocalDateTime now = LocalDateTime.now();
        report.setUpdatedAt(now);

        this.reportRepository.save(report);
        System.out.println(report);
        
        return ErrorKinds.SUCCESS;
    }

    // 1件を検索
    public Report findById(int id) {
        // findByIdで検索
        Optional<Report> option = reportRepository.findById(id);
        // 取得できなかった場合はnullを返す
        Report report = option.orElse(null);
        return report;
    }
    
    

}
