package org.yearup.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.yearup.models.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer>
{
}
