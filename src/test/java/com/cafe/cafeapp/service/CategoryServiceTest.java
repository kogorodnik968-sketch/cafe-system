package com.cafe.cafeapp.service;


import com.cafe.cafeapp.dto.CategoryDto;
import com.cafe.cafeapp.exception.AlreadyExistsException;
import com.cafe.cafeapp.exception.DeleteNotAllowedException;
import com.cafe.cafeapp.exception.NotFoundException;
import com.cafe.cafeapp.mapper.CategoryMapper;
import com.cafe.cafeapp.model.Category;
import com.cafe.cafeapp.model.Product;
import com.cafe.cafeapp.repository.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    void getAll_shouldReturnDtoList() {
        List<Category> categories = List.of(new Category());
        List<CategoryDto> dtos = List.of(new CategoryDto());

        when(categoryRepository.findAll()).thenReturn(categories);
        when(categoryMapper.toDtoList(categories)).thenReturn(dtos);

        List<CategoryDto> result = categoryService.getAll();

        assertEquals(dtos, result);
        verify(categoryRepository).findAll();
        verify(categoryMapper).toDtoList(categories);
    }

    @Test
    void getById_shouldReturnDto_whenFound() {
        Category category = new Category();
        CategoryDto dto = new CategoryDto();

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(categoryMapper.toDto(category)).thenReturn(dto);

        CategoryDto result = categoryService.getById(1L);

        assertEquals(dto, result);
    }

    @Test
    void getById_shouldThrow_whenNotFound() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> categoryService.getById(1L));
    }

    @Test
    void getByName_shouldReturnList() {
        List<Category> categories = List.of(new Category());
        List<CategoryDto> dtos = List.of(new CategoryDto());

        when(categoryRepository.findByName("test")).thenReturn(categories);
        when(categoryMapper.toDtoList(categories)).thenReturn(dtos);

        List<CategoryDto> result = categoryService.getByName("test");

        assertEquals(dtos, result);
    }

    @Test
    void create_shouldSaveAndReturnDto() {
        CategoryDto dto = new CategoryDto(null, "Coffee");
        Category entity = new Category();
        Category saved = new Category();
        CategoryDto resultDto = new CategoryDto(1L, "Coffee");

        when(categoryRepository.existsByName("Coffee")).thenReturn(false);
        when(categoryMapper.toEntity(dto)).thenReturn(entity);
        when(categoryRepository.save(entity)).thenReturn(saved);
        when(categoryMapper.toDto(saved)).thenReturn(resultDto);

        CategoryDto result = categoryService.create(dto);

        assertEquals(resultDto, result);
    }

    @Test
    void create_shouldThrow_whenNameExists() {
        CategoryDto dto = new CategoryDto(null, "Coffee");

        when(categoryRepository.existsByName("Coffee")).thenReturn(true);

        assertThrows(AlreadyExistsException.class,
                () -> categoryService.create(dto));
    }

    @Test
    void update_shouldUpdateAndReturnDto() {
        Category existing = new Category();
        CategoryDto dto = new CategoryDto(null, "NewName");
        Category saved = new Category();
        CategoryDto resultDto = new CategoryDto(1L, "NewName");

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(categoryRepository.save(existing)).thenReturn(saved);
        when(categoryMapper.toDto(saved)).thenReturn(resultDto);

        CategoryDto result = categoryService.update(1L, dto);

        assertEquals(resultDto, result);
        assertEquals("NewName", existing.getName());
    }

    @Test
    void delete_shouldThrow_whenNotExists() {
        when(categoryRepository.existsById(1L)).thenReturn(false);

        assertThrows(NotFoundException.class,
                () -> categoryService.delete(1L));
    }

    @Test
    void delete_shouldThrow_whenHasProducts() {
        Category category = new Category();
        category.setProducts(List.of(new Product()));

        when(categoryRepository.existsById(1L)).thenReturn(true);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        assertThrows(DeleteNotAllowedException.class,
                () -> categoryService.delete(1L));
    }

    @Test
    void delete_shouldDelete_whenValid() {
        Category category = new Category();
        category.setProducts(List.of());

        when(categoryRepository.existsById(1L)).thenReturn(true);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        categoryService.delete(1L);

        verify(categoryRepository).deleteById(1L);
    }
}
