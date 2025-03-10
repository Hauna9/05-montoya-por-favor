package com.example.service;


import com.example.repository.MainRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Primary
@Service
public abstract class MainService<T> {
    MainRepository<T> repository;
    @Autowired
    public MainService(MainRepository<T> repository) {
        this.repository = repository;
    }
    public ArrayList<T> getAll() {
        return repository.findAll();
    }

}
