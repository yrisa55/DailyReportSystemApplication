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
import com.techacademy.entity.Employee;
import com.techacademy.entity.Report;
import com.techacademy.repository.ReportRepository;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReportService {
    
    private final ReportRepository reportRepository;
    
    @Autowired
    public ReportService(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }
    
    // 日報一覧表示処理 全件を検索して返す
    public List<Report> findAll() {
        // リポジトリのfindAllメソッドを呼び出す
        return reportRepository.findAll();
    }
    
    // 日報保存
    //public Errorkinds save (Report report) {
        
    //}
    
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
