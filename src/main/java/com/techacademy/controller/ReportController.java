package com.techacademy.controller;

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
// import com.techacademy.service.EmployeeService;
import com.techacademy.service.ReportService;
import com.techacademy.service.UserDetail;

@Controller
@RequestMapping("reports")
public class ReportController {

    private final ReportService reportService;

    @Autowired
    public ReportController(ReportService reportService) {
        this.reportService = reportService;
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
      public String create(Model model) {
          Report report = new Report();
          
          report.setEmployee(new Employee());
          
          model.addAttribute("report", report);

         return "reports/new";
      }
//    
//    // 日報新規登録処理
//    @PostMapping("/add")
//    public String create(@Validated Report report, BindingResult res, Model model) {
//        if (res.hasErrors()) {
//        return "reports/new";
//        }
//        
//        try {
//            reportService.save(report);
//        } catch (DataIntegrityViolationException e) {
//            model.addAttribute("error", "登録に失敗しました。");
//            return "reports/new";
//            
//        }
//        
//        return "redirect:/reports";
//    }
//    
//    
      // 日報更新画面
      @GetMapping(value = "/{id}/update")
      public String edit(@PathVariable int id, Model model) {
          
          Report report = reportService.findById(id);
          System.out.println(report);
        
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
}
