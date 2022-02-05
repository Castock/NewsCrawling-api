package com.example.demo.persistence;

import com.example.demo.model.NewsEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NewsRepository extends JpaRepository<NewsEntity, String>{
    
}
