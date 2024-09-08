package com.techacademy.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.techacademy.constants.ErrorKinds;
import com.techacademy.constants.ErrorMessage;

import jakarta.validation.Valid;

import com.techacademy.entity.Employee;
import com.techacademy.entity.Report;
import com.techacademy.service.EmployeeService;
import com.techacademy.repository.EmployeeRepository;
import com.techacademy.service.ReportService;
import com.techacademy.service.UserDetail;

@Controller
@RequestMapping("reports")
public class ReportController {

    private final ReportService reportService;
    private final EmployeeRepository employeeRepository;

    @Autowired
    public ReportController(ReportService reportService, EmployeeRepository employeeRepository) {
        this.reportService = reportService;
        this.employeeRepository = employeeRepository;
    }
    
    // 日報一覧画面
    @GetMapping
    public String list(Model model) {
         List<Report> report = reportService.findAll();
         model.addAttribute("listSize", report.size());
         model.addAttribute("reportList", report);

        return "reports/list";
    }
    
    // 日報詳細画面
    @GetMapping(value = "/{id}/")
    public String detail(@PathVariable int id, Model model) {

        model.addAttribute("report", reportService.findById(id));
        return "reports/detail";
    }
    
      // 日報新規登録画面
      @GetMapping(value = "/add")
      public String create(Model model, @AuthenticationPrincipal UserDetail userDetail) {
          Report report = new Report();
          
          // ログイン中の従業員を取得
          Employee employee = employeeRepository.findByCode(userDetail.getUsername());
          
          report.setEmployee(employee);
          
          model.addAttribute("report", report);

         return "reports/new";
      }
    
      // 日報新規登録処理
      @PostMapping("/add")
      public String add(@Validated Report report, BindingResult res, Model model, @AuthenticationPrincipal UserDetail userDetail) {
          // ログインしているユーザーを設定
          Employee employee = employeeRepository.findByCode(userDetail.getUsername());
          report.setEmployee(employee);
          
          // 現在の日時をcreatedAtとupdatedAtに変更
          LocalDateTime now = LocalDateTime.now();
              report.setCreatedAt(now);
              report.setUpdatedAt(now);
          
          
          // デバッグ用
          System.out.println("Received report:" + report.getTitle());
         
          if (res.hasErrors()) {
          System.out.println("BindingResultエラー" + res.hasErrors());
          System.out.println("バリデーションエラー" + res.getAllErrors());
          return "reports/new";
          }
          
          try {
              
              // Reportを保存する
              System.out.println("保存前のレポート" + report);
              reportService.save(report);
              System.out.println("保存成功");
          } catch (DataIntegrityViolationException e) {
              System.out.println("レポートセーブ中にエラー" + e.getMessage());
              model.addAttribute("error", "登録に失敗しました。");
              return "reports/new";
              
          }
          
          
          return "redirect:/reports";
      }
    
    
      // 日報更新画面
      @GetMapping(value = "/{id}/update")
      public String edit(@PathVariable int id, Model model) {
          
          Report report = reportService.findById(id);
          //System.out.println(report);
        
          model.addAttribute("report", report);
          
          return "reports/update";
      }
      
      @PostMapping("/{id}/update")
      public String update(@PathVariable int id, @Validated @ModelAttribute Report report, BindingResult res, Model model ) {
          if (res.hasErrors()) {
              System.out.println(res.getAllErrors());
              return "reports/edit";
          }
          
          try {
              reportService.update(report);
           } catch (DataIntegrityViolationException e) {
           // エラーが発生した場合はエラーメッセージを追加して更新画面に戻す
           System.out.println("エラー発生: " + e);
               e.printStackTrace(); // スタックトレースを表示
               model.addAttribute("error", e.getMessage());
               return "reports/edit";
          }
  
         // 一覧画面にリダイレクト
         return "redirect:/reports";
       }
      
      // 日報削除処理
}
