package com.techacademy.controller;

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
import com.techacademy.service.EmployeeService;
import com.techacademy.service.UserDetail;

@Controller
@RequestMapping("employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    @Autowired
    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    // 従業員一覧画面
    @GetMapping
    public String list(Model model) {

        model.addAttribute("listSize", employeeService.findAll().size());
        model.addAttribute("employeeList", employeeService.findAll());

        return "employees/list";
    }

    // 従業員詳細画面
    @GetMapping(value = "/{code}/")
    public String detail(@PathVariable String code, Model model) {

        model.addAttribute("employee", employeeService.findByCode(code));
        return "employees/detail";
    }

    // 従業員新規登録画面
    @GetMapping(value = "/add")
    public String create(@ModelAttribute Employee employee) {

        return "employees/new";
    }

    // 従業員新規登録処理
    @PostMapping(value = "/add")
    public String add(@Validated Employee employee, BindingResult res, Model model) {

        // パスワード空白チェック
        /*
         * エンティティ側の入力チェックでも実装は行えるが、更新の方でパスワードが空白でもチェックエラーを出さずに
         * 更新出来る仕様となっているため上記を考慮した場合に別でエラーメッセージを出す方法が簡単だと判断
         */
        if ("".equals(employee.getPassword())) {
            // パスワードが空白だった場合
            model.addAttribute(ErrorMessage.getErrorName(ErrorKinds.BLANK_ERROR),
                    ErrorMessage.getErrorValue(ErrorKinds.BLANK_ERROR));

            return create(employee);

        }
        
        // 入力チェック
        if (res.hasErrors()) {
            return create(employee);
        }

        // 論理削除を行った従業員番号を指定すると例外となるためtry~catchで対応
        // (findByIdでは削除フラグがTRUEのデータが取得出来ないため)
        try {
            ErrorKinds result = employeeService.save(employee);

            if (ErrorMessage.contains(result)) {
                model.addAttribute(ErrorMessage.getErrorName(result), ErrorMessage.getErrorValue(result));
                return create(employee);
            }

        } catch (DataIntegrityViolationException e) {
            model.addAttribute(ErrorMessage.getErrorName(ErrorKinds.DUPLICATE_EXCEPTION_ERROR),
                    ErrorMessage.getErrorValue(ErrorKinds.DUPLICATE_EXCEPTION_ERROR));
            return create(employee);
        }

        return "redirect:/employees";
    }
    // Chapter6課題ここから追加
    // 従業員更新画面を表示
    @GetMapping(value = "/{code}/update")
    public String edit(@PathVariable String code, Model model) {
        System.out.println("更新画面表示処理");
        // Modelに登録
        model.addAttribute("employee", employeeService.findByCode(code));
        // 修正：更新画面を表示
        return "employees/update";
    }
    
    // 従業員更新処理
    @PostMapping(value = "/{code}/update")
    public String update(@PathVariable String code, @Validated Employee employee, BindingResult res, Model model) {
        System.out.println("Update request received for code: " + code);
        
        if ("".equals(employee.getPassword())) {
            System.out.println("パスワードが空白なので、既存のパスワードを設定");
            // パスワードが空白だった場合
            Employee existingEmployee = employeeService.findByCode(code);
            System.out.println(existingEmployee);
            
            if (existingEmployee != null) {
                employee.setPassword(existingEmployee.getPassword());
                System.out.println("既存のパスワードをセット setPassword");
            }
        } else {
           //パスワードが入力されている場合にのみチェック
           String password = employee.getPassword();
           System.out.println("Password provided: " + password);
           
           //パスワードの桁数チェック
           if (password.length() < 8 || password.length() > 16) {
            model.addAttribute(ErrorMessage.getErrorName(ErrorKinds.RANGECHECK_ERROR),
                    ErrorMessage.getErrorValue(ErrorKinds.RANGECHECK_ERROR));
            System.out.println("パスワードの桁数エラー");     
            return "employees/update";
           }
        
           // パスワードの形式チェック（半角英数字以外の文字が含まれているかどうか）
           if (!password.matches("^[a-zA-Z0-9]+$")) {
               System.out.println("パスワード形式チェック");
               model.addAttribute(ErrorMessage.getErrorName(ErrorKinds.HALFSIZE_ERROR),
                       ErrorMessage.getErrorValue(ErrorKinds.HALFSIZE_ERROR));
               System.out.println("パスワードの形式エラー");
                return "employees/update";
           }
        
            
           //パスワードが入力されているときだけバリデーションチェック
           if (res.hasErrors()) {
            System.out.println("バリデーションエラーあり");
            //エラーがあれば、再度更新画面に戻す
            return "employees/update";
           }
        }
        
        try {
            // 従業員情報を更新
            employee.setCode(code); // URLパスから取得したコードを試用
            ErrorKinds result = employeeService.update(employee);
            System.out.println("Employee updated successfully. Result: " + result);
        } catch (Exception e) {
            // エラーが発生した場合はエラーメッセージを追加して更新画面に戻す
            System.out.println("エラー発生: " + e);
            e.printStackTrace(); // スタックトレースを表示
            model.addAttribute("error", e.getMessage());
            model.addAttribute("employee", employee);
            return "employees/update";
            
        }
     
        // 一覧画面にリダイレクト
        return "redirect:/employees";
    }
    //ここまで
    
    
    // 従業員削除処理
    @PostMapping(value = "/{code}/delete")
    public String delete(@PathVariable String code, @AuthenticationPrincipal UserDetail userDetail, Model model) {

        ErrorKinds result = employeeService.delete(code, userDetail);

        if (ErrorMessage.contains(result)) {
            model.addAttribute(ErrorMessage.getErrorName(result), ErrorMessage.getErrorValue(result));
            model.addAttribute("employee", employeeService.findByCode(code));
            return detail(code, model);
        }

        return "redirect:/employees";
    }

}
