package com.cafe.cafeapp.service;

import com.cafe.cafeapp.cache.QueryCacheService;
import com.cafe.cafeapp.dto.ProductResponseDto;
import com.cafe.cafeapp.dto.ProductRequestDto;
import com.cafe.cafeapp.exception.AlreadyExistsException;
import com.cafe.cafeapp.exception.NotFoundException;
import com.cafe.cafeapp.mapper.ProductMapper;
import com.cafe.cafeapp.model.Category;
import com.cafe.cafeapp.model.Ingredient;
import com.cafe.cafeapp.model.Product;
import com.cafe.cafeapp.model.Tag;
import com.cafe.cafeapp.repository.CategoryRepository;
import com.cafe.cafeapp.repository.IngredientRepository;
import com.cafe.cafeapp.repository.ProductRepository;
import com.cafe.cafeapp.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final IngredientRepository ingredientRepository;
    private final TagRepository tagRepository;
    private final ProductMapper productMapper;
    private final QueryCacheService queryCacheService;

    @Transactional(readOnly = true)
    public List<ProductResponseDto> getAllProducts() {
        return productMapper.toResponseDto(productRepository.findAllByOrderByIdDesc());
    }

    @Transactional(readOnly = true)
    public ProductResponseDto getById (Long id) {
        Product product = productRepository.findById(id).orElseThrow(
                () -> new NotFoundException(id));

        return productMapper.toResponseDto(product);
    }

    @Transactional(readOnly = true)
    public List<ProductResponseDto> getByName (String name) {
        List<Product> products = productRepository.findByName(name);

        return productMapper.toResponseDto(products);
    }

    @Transactional
    public ProductResponseDto create (ProductRequestDto dto) {

        if (productRepository.existsByName(dto.getName())) {
            throw new AlreadyExistsException("Товар с таким названием уже существует");
        }

        Product product = productMapper.toEntity(dto);

        Category category = categoryRepository.findById(dto.getCategoryId()).orElseThrow();

        Set<Ingredient> ingredients = new HashSet<>(ingredientRepository.findAllById(dto.getIngredientsId()));

        Tag tag = tagRepository.findById(dto.getTagId()).orElseThrow();

        product.setCategory(category);
        product.setIngredients(ingredients);
        product.setTag(tag);

        Product saved = productRepository.save(product);
        return productMapper.toResponseDto(saved);
    }

    @Transactional
    public ProductResponseDto update (Long id, ProductRequestDto dto) {
        Product existing = productRepository.findById(id).orElseThrow();

        existing.setName(dto.getName());
        existing.setPrice(dto.getPrice());

        Category category = categoryRepository.findById(dto.getCategoryId()).orElseThrow();
        Set<Ingredient> ingredients = new HashSet<>(ingredientRepository.findAllById(dto.getIngredientsId()));
        Tag tag = tagRepository.findById(dto.getTagId()).orElseThrow(
                () -> new NotFoundException("Тег не найден"));

        existing.setCategory(category);
        existing.setIngredients(ingredients);
        existing.setTag(tag);


        queryCacheService.invalidateByProductId(existing.getName());

        return productMapper.toResponseDto(productRepository.save(existing));
    }

    @Transactional
    public void delete (Long id) {

        if (!productRepository.existsById(id)) {
            throw new NotFoundException("Продукт не найден с id" + id);
        }

        Product product = productRepository.findById(id).orElseThrow();

        productRepository.deleteById(id);

        queryCacheService.invalidateByProductId(product.getName());
    }

    @Transactional
    public List<ProductResponseDto> createBulk(List<ProductRequestDto> dtos) {

        Set<String> uniqueNames = dtos.stream()
                .map(ProductRequestDto::getName)
                .collect(Collectors.toSet());

        if (uniqueNames.size() != dtos.size()) {
            throw new AlreadyExistsException("В одном запросе не может быть продуктов с одинаковыми названиями");
        }

        return dtos.stream()
                .map(this::create)
                .toList();
    }


    public List<ProductResponseDto> createBulkWithoutTransaction(List<ProductRequestDto> dtos) {
        List<ProductResponseDto> result = new ArrayList<>();

        for (ProductRequestDto dto : dtos) {

            if ("ERROR".equals(dto.getName())) {
                throw new RuntimeException("Ошибка для демонстрации");
            }

            ProductResponseDto response = create(dto);
            result.add(response);
        }

        return result;
    }


    @Transactional
    public List<ProductResponseDto> createBulkWithTransaction(List<ProductRequestDto> dtos) {
        List<ProductResponseDto> result = new ArrayList<>();

        for (ProductRequestDto dto : dtos) {

            if ("ERROR".equals(dto.getName())) {
                throw new RuntimeException("Ошибка для демонстрации");
            }

            ProductResponseDto response = create(dto);
            result.add(response);
        }

        return result;
    }

}
