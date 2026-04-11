package com.cafe.cafeapp.service;

import com.cafe.cafeapp.cache.QueryCacheService;
import com.cafe.cafeapp.dto.ProductRequestDto;
import com.cafe.cafeapp.dto.ProductResponseDto;
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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private IngredientRepository ingredientRepository;

    @Mock
    private TagRepository tagRepository;

    @Mock
    private QueryCacheService queryCacheService;

    @InjectMocks
    private ProductService productService;

    @Test
    void getAll() {
        List<Product> products = List.of(new Product());
        List<ProductResponseDto> dtos = List.of(new ProductResponseDto());

        when(productRepository.findAll()).thenReturn(products);
        when(productMapper.toResponseDto(products)).thenReturn(dtos);

        assertEquals(dtos, productService.getAllProducts());
    }

    @Test
    void getById_success() {
        Product product = new Product();
        ProductResponseDto dto = new ProductResponseDto();

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productMapper.toResponseDto(product)).thenReturn(dto);

        assertEquals(dto, productService.getById(1L));
    }

    @Test
    void getById_notFound() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> productService.getById(1L));
    }

    @Test
    void getByName() {
        List<Product> products = List.of(new Product());
        List<ProductResponseDto> dtos = List.of(new ProductResponseDto());

        when(productRepository.findByName("test")).thenReturn(products);
        when(productMapper.toResponseDto(products)).thenReturn(dtos);

        assertEquals(dtos, productService.getByName("test"));
    }

    @Test
    void create_success() {
        ProductRequestDto dto = new ProductRequestDto("A", BigDecimal.TEN, 1L, Set.of(1L), 1L);

        Product product = new Product();
        Category category = new Category();
        Tag tag = new Tag();
        ProductResponseDto response = new ProductResponseDto();

        when(productRepository.existsByName("A")).thenReturn(false);
        when(productMapper.toEntity(dto)).thenReturn(product);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(ingredientRepository.findAllById(any())).thenReturn(List.of(new Ingredient()));
        when(tagRepository.findById(1L)).thenReturn(Optional.of(tag));
        when(productRepository.save(product)).thenReturn(product);
        when(productMapper.toResponseDto(product)).thenReturn(response);

        assertEquals(response, productService.create(dto));
    }

    @Test
    void create_alreadyExists() {
        ProductRequestDto dto = new ProductRequestDto("A", BigDecimal.TEN, 1L, Set.of(), 1L);

        when(productRepository.existsByName("A")).thenReturn(true);

        assertThrows(AlreadyExistsException.class,
                () -> productService.create(dto));
    }

    @Test
    void create_tagNotFound() {
        ProductRequestDto dto = new ProductRequestDto("A", BigDecimal.TEN, 1L, Set.of(), 1L);

        when(productRepository.existsByName("A")).thenReturn(false);
        when(productMapper.toEntity(dto)).thenReturn(new Product());
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(new Category()));
        when(ingredientRepository.findAllById(any())).thenReturn(List.of());
        when(tagRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class,
                () -> productService.create(dto));
    }

    @Test
    void update_success() {
        Product product = new Product();
        ProductResponseDto response = new ProductResponseDto();

        ProductRequestDto dto = new ProductRequestDto("A", BigDecimal.TEN, 1L, Set.of(1L), 1L);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(new Category()));
        when(ingredientRepository.findAllById(any())).thenReturn(List.of(new Ingredient()));
        when(tagRepository.findById(1L)).thenReturn(Optional.of(new Tag()));
        when(productRepository.save(product)).thenReturn(product);
        when(productMapper.toResponseDto(product)).thenReturn(response);

        ProductResponseDto result = productService.update(1L, dto);

        assertEquals(response, result);
        verify(queryCacheService).invalidateByProductId(any());
    }

    @Test
    void update_notFound() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class,
                () -> productService.update(1L, new ProductRequestDto()));
    }

    @Test
    void update_tagNotFound() {
        Product product = new Product();

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(new Category()));
        when(ingredientRepository.findAllById(any())).thenReturn(List.of());
        when(tagRepository.findById(1L)).thenReturn(Optional.empty());

        ProductRequestDto dto = new ProductRequestDto("A", BigDecimal.TEN, 1L, Set.of(), 1L);

        assertThrows(NotFoundException.class,
                () -> productService.update(1L, dto));
    }

    @Test
    void delete_notFound() {
        when(productRepository.existsById(1L)).thenReturn(false);

        assertThrows(NotFoundException.class,
                () -> productService.delete(1L));
    }

    @Test
    void delete_success() {
        Product product = new Product();
        product.setName("Coffee");

        when(productRepository.existsById(1L)).thenReturn(true);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        productService.delete(1L);

        verify(productRepository).deleteById(1L);
        verify(queryCacheService).invalidateByProductId("Coffee");
    }

    @Test
    void createBulk_duplicateNames() {
        List<ProductRequestDto> dtos = List.of(
                new ProductRequestDto("A", BigDecimal.ONE, 1L, Set.of(), 1L),
                new ProductRequestDto("A", BigDecimal.ONE, 1L, Set.of(), 1L)
        );

        assertThrows(AlreadyExistsException.class,
                () -> productService.createBulk(dtos));
    }

    @Test
    void createBulk_success() {
        ProductService spy = spy(productService);

        List<ProductRequestDto> dtos = List.of(
                new ProductRequestDto("A", BigDecimal.ONE, 1L, Set.of(), 1L)
        );

        doReturn(new ProductResponseDto()).when(spy).create(any());

        assertEquals(1, spy.createBulk(dtos).size());
    }

    @Test
    void bulkWithoutTransaction_error() {
        List<ProductRequestDto> dtos = List.of(
                new ProductRequestDto("ERROR", BigDecimal.ONE, 1L, Set.of(), 1L)
        );

        assertThrows(RuntimeException.class,
                () -> productService.createBulkWithoutTransaction(dtos));
    }

    @Test
    void bulkWithoutTransaction_success() {
        ProductService spy = spy(productService);

        List<ProductRequestDto> dtos = List.of(
                new ProductRequestDto("A", BigDecimal.ONE, 1L, Set.of(), 1L)
        );

        doReturn(new ProductResponseDto()).when(spy).create(any());

        assertEquals(1, spy.createBulkWithoutTransaction(dtos).size());
    }

    @Test
    void bulkWithTransaction_error() {
        List<ProductRequestDto> dtos = List.of(
                new ProductRequestDto("ERROR", BigDecimal.ONE, 1L, Set.of(), 1L)
        );

        assertThrows(RuntimeException.class,
                () -> productService.createBulkWithTransaction(dtos));
    }

    @Test
    void bulkWithTransaction_success() {
        ProductService spy = spy(productService);

        List<ProductRequestDto> dtos = List.of(
                new ProductRequestDto("A", BigDecimal.ONE, 1L, Set.of(), 1L)
        );

        doReturn(new ProductResponseDto()).when(spy).create(any());

        assertEquals(1, spy.createBulkWithTransaction(dtos).size());
    }

}
