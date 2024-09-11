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
    public String list(Model model, @AuthenticationPrincipal UserDetail userDetail) {
         List<Report> reports;
         
         if (userDetail.getAuthorities().stream()
                       .anyMatch(authority -> "ADMIN".equals(authority.getAuthority()))) {
             // 管理者の場合は全日報を取得
             reports = reportService.findAll();
         } else {
             //一般ユーザーの場合は自分の登録した日報のみを取得
             Employee employee = userDetail.getEmployee();
             reports = reportService.findReportsByEmployee(employee);
         }
         
         model.addAttribute("listSize", reports.size());
         model.addAttribute("reportList", reports);

        return "reports/list";
    }
    
    // 日報詳細画面
    @GetMapping(value = "/{id}/")
    public String detail(@PathVariable int id, Model model) {
        
        Report report = reportService.findById(id);
        
        System.out.println("日付:" + report.getReportDate());

        model.addAttribute("report", report);
        return "reports/detail";
    }
    
      // 日報新規登録画面
      @GetMapping(value = "/add")
      public String create(Model model, @AuthenticationPrincipal UserDetail userDetail) {
          Report report = new Report();
          
          // ログイン中の従業員を取得 serviceに
          Employee employee = employeeRepository.findByCode(userDetail.getUsername());       
          report.setEmployee(employee);
          
          model.addAttribute("report", report);

         return "reports/new";
      }
    
      // 日報新規登録処理
      @PostMapping("/add")
      public String add(@Validated Report report, BindingResult res, Model model, @AuthenticationPrincipal UserDetail userDetail) {
            // ログインしているユーザーを設定
            Employee employee = userDetail.getEmployee();
            report.setEmployee(employee);
         
          if (res.hasErrors()) {
          System.out.println("BindingResultエラー" + res.hasErrors());
          System.out.println("バリデーションエラー" + res.getAllErrors());
         
          
          return "reports/new";
          }
          
          try {
              System.out.println("保存成功");
              ErrorKinds result = reportService.save(report);
              
              if (ErrorMessage.contains(result)) {
                  
                  String errorName = ErrorMessage.getErrorName(result);
                  String errorValue = ErrorMessage.getErrorValue(result);
                  model.addAttribute(errorName, errorValue);
                  System.out.println("Error Name:" + errorName + ", Error Message:" + errorValue);
                  return "reports/new";
              }
              
              System.out.println("保存成功");
              return "redirect:/reports";
              
          } catch (Exception e) {
             
              System.out.println("レポートセーブ中にエラー" + e.getMessage());
              e.printStackTrace();
              model.addAttribute("error", "登録に失敗しました。エラー" + e.getMessage());
              return "reports/new";
              
          }
          
//          return "redirect:/reports";
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
      public String update(@PathVariable int id, @Validated Report report, BindingResult res, Model model, @AuthenticationPrincipal UserDetail userDetail) {
          
          if (res.hasErrors()) {
              System.out.println(res.getAllErrors());
              return "reports/update";
          }
          
          try {
              // 現在の従業員情報を取得
              Employee employee = employeeRepository.findByCode(userDetail.getUsername());
              // 取得したEmployeeをReportに設定
              report.setEmployee(employee);
              
              ErrorKinds result = reportService.update(report);
              System.out.println("ErrorKinds Result:" + result);
              
              if (ErrorMessage.contains(result)) {
                  String errorName = ErrorMessage.getErrorName(result);
                  String errorValue = ErrorMessage.getErrorValue(result);
                  model.addAttribute(errorName, errorValue);
                  System.out.println("Error Name:" + errorName + ", Error Message:" + errorValue);
                  // 更新成功した場合、一覧画面へ
                  return "reports/update";
                  } else {
                      System.out.println("No error message found for ErrroKindls Result:" + result);
              }
              
           } catch (Exception e) {
           // エラーが発生した場合はエラーメッセージを追加して更新画面に戻す
           System.out.println("エラー発生: " + e);
               e.printStackTrace(); 
               model.addAttribute("error", e.getMessage());
               return "reports/update";
          }
  
          //一覧画面にリダイレクト
         return "redirect:/reports";
       }
      
      // 日報削除処理
      @PostMapping("/{id}/delete")
      public String delete(@PathVariable int id, @AuthenticationPrincipal UserDetail userDetail, Model model) {
          ErrorKinds result = reportService.delete(id, userDetail);
                  if (ErrorMessage.contains(result)) {
                      model.addAttribute(ErrorMessage.getErrorName(result), ErrorMessage.getErrorValue(result));
                      model.addAttribute("report", reportService.findById(id));
                      return detail(id, model);
                  }

                  return "redirect:/reports";
              }

          }
