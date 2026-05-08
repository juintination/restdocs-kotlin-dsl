package com.example.restdocskotlindsl.product.repository

import com.example.restdocskotlindsl.product.domain.Product
import org.springframework.data.jpa.repository.JpaRepository

interface ProductRepository : JpaRepository<Product, Long>
