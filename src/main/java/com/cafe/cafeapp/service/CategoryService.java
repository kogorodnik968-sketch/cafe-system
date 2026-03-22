package com.cafe.cafeapp.service;

import com.cafe.cafeapp.dto.CategoryDto;
import com.cafe.cafeapp.exception.CategoryNotFoundException;
import com.cafe.cafeapp.mapper.CategoryMapper;
import com.cafe.cafeapp.model.Category;
import com.cafe.cafeapp.model.Product;
import com.cafe.cafeapp.repository.CategoryRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Transactional(readOnly = true)
    public List<CategoryDto> getAll() {
        return categoryMapper.toDtoList(categoryRepository.findAll());
    }

    @Transactional(readOnly = true)
    public CategoryDto getById (Long id) {
        Category category = categoryRepository.findById(id).orElseThrow(
                () -> new CategoryNotFoundException(id));

        return categoryMapper.toDto(category);
    }

    @Transactional(readOnly = true)
    public List<CategoryDto> getByName (String name) {
        List<Category> category = categoryRepository.findByName(name);

        return categoryMapper.toDtoList(category);
    }

    @Transactional
    public CategoryDto create (CategoryDto dto) {
        if (categoryRepository.existsByName(dto.getName())) {
            throw new CategoryNotFoundException("Категория с таким именем уже существует");
        }

        Category category = categoryMapper.toEntity(dto);
        Category saved = categoryRepository.save(category);

        return categoryMapper.toDto(saved);
    }

    @Transactional
    public CategoryDto update (Long id, CategoryDto dto) {
        Category existing = categoryRepository.findById(id).orElseThrow();

        existing.setName(dto.getName());

        return categoryMapper.toDto(categoryRepository.save(existing));
    }

    @Transactional
    public void delete (Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new CategoryNotFoundException(id);
        }

        Category category = categoryRepository.findById(id).get();
        List < Product > products = category.getProducts();

        if (!products.isEmpty()) {
            throw new CategoryNotFoundException("Невозможно удалить категорию с существующими товарами");
        }

        categoryRepository.deleteById(id);
    }
}
