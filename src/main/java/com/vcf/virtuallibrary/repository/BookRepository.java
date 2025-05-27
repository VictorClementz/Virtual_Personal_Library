package com.vcf.virtuallibrary.repository;


import com.vcf.virtuallibrary.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;


public interface BookRepository extends JpaRepository<Book, Long> {

}