package com.example.service;

import com.example.entity.Currency;
import com.example.repository.CurrencyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CurrencyService {

    @Autowired
    CurrencyRepository repository;

    public List<Currency> findAll() {
        return repository.findAll();
    }

    public Currency findByCode(String code) {
        return repository.findByCode(code);
    }

    public Currency save(Currency currency) {
        return repository.save(currency);
    }

    public Optional<Currency> findById(Long id) {
        return repository.findById(id);
    }

    public void deleteByCode(String code) {
        Currency currency = repository.findByCode(code);
        if (currency != null) {
            repository.delete(currency);
        }
    }
}
