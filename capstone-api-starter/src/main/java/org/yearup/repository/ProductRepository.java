package org.yearup.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.yearup.models.Product;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer>
{
    List<Product> findByCategoryId(int categoryId);
}
