package com.example.controller;

import com.example.entity.Currency;
import com.example.repository.CurrencyRepository;
import com.example.service.CurrencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/currency")
public class CurrencyController {

    @Autowired
    private CurrencyService currencyService;

    //    查詢
    @GetMapping("/list")
    public List<Currency> getAllCurrencies() {
        return currencyService.findAll();
    }

    @GetMapping("/{code}")
    public Currency getCurrencyByCode(@PathVariable String code) {
        return currencyService.findByCode(code);
    }

    // / 新增
    @PostMapping("/add")
    public Currency addCurrency(@RequestBody Currency currency) {
        return currencyService.save(currency);
    }

    // 修改幣別資料
    @PutMapping("/{id}")
    public Currency updateCurrency( @RequestBody Currency currency) {
        Currency existingCurrency = currencyService.findByCode(currency.getCode());
        if (existingCurrency != null) {
            existingCurrency.setCode(currency.getCode());
            existingCurrency.setName(currency.getName());
            existingCurrency.setPrice(currency.getPrice());
            return currencyService.save(existingCurrency);
        }
        return null;
    }

    //刪除
    @DeleteMapping("/{code}")
    public void deleteCurrency(@PathVariable String code) {
        currencyService.deleteByCode(code);
    }

}

