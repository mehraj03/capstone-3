package org.yearup.service;

import org.springframework.stereotype.Service;
import org.yearup.models.Category;
import org.yearup.repository.CategoryRepository;

import java.util.List;

@Service
public class CategoryService
{
    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository)
    {
        this.categoryRepository = categoryRepository;
    }

    public List<Category> getAllCategories()
    {
        // get all categories
        return null;
    }

    public Category getById(int categoryId)
    {
        // get category by id
        return null;
    }

    public Category create(Category category)
    {
        // create a new category
        return null;
    }

    public Category update(int categoryId, Category category)
    {
        // update category and return the updated category
        return null;
    }

    public void delete(int categoryId)
    {
        // delete category
    }
}
