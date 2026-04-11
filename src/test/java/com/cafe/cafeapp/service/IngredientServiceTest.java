package com.cafe.cafeapp.service;

import com.cafe.cafeapp.dto.IngredientDto;
import com.cafe.cafeapp.exception.AlreadyExistsException;
import com.cafe.cafeapp.exception.NotFoundException;
import com.cafe.cafeapp.mapper.IngredientMapper;
import com.cafe.cafeapp.model.Ingredient;
import com.cafe.cafeapp.repository.IngredientRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IngredientServiceTest {

    @Mock
    private IngredientRepository ingredientRepository;

    @Mock
    private IngredientMapper ingredientMapper;

    @InjectMocks
    private IngredientService ingredientService;

    // ------------------- getAll -------------------
    @Test
    void getAll_success() {
        List<Ingredient> ingredients = List.of(new Ingredient());
        List<IngredientDto> dtos = List.of(new IngredientDto());

        when(ingredientRepository.findAll()).thenReturn(ingredients);
        when(ingredientMapper.toDtoList(ingredients)).thenReturn(dtos);

        List<IngredientDto> result = ingredientService.getAll();

        assertEquals(dtos, result);
    }

    // ------------------- getById -------------------
    @Test
    void getById_success() {
        Ingredient ingredient = new Ingredient();
        IngredientDto dto = new IngredientDto();

        when(ingredientRepository.findById(1L)).thenReturn(Optional.of(ingredient));
        when(ingredientMapper.toDto(ingredient)).thenReturn(dto);

        IngredientDto result = ingredientService.getById(1L);

        assertEquals(dto, result);
    }

    @Test
    void getById_notFound() {
        when(ingredientRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> ingredientService.getById(1L));
    }

    // ------------------- create -------------------
    @Test
    void create_success() {
        IngredientDto dto = new IngredientDto(null, "Milk");
        Ingredient entity = new Ingredient();
        Ingredient saved = new Ingredient();
        IngredientDto resultDto = new IngredientDto(1L, "Milk");

        when(ingredientRepository.existsByName("Milk")).thenReturn(false);
        when(ingredientMapper.toEntity(dto)).thenReturn(entity);
        when(ingredientRepository.save(entity)).thenReturn(saved);
        when(ingredientMapper.toDto(saved)).thenReturn(resultDto);

        IngredientDto result = ingredientService.create(dto);

        assertEquals(resultDto, result);
    }

    @Test
    void create_alreadyExists() {
        IngredientDto dto = new IngredientDto(null, "Milk");

        when(ingredientRepository.existsByName("Milk")).thenReturn(true);

        assertThrows(AlreadyExistsException.class,
                () -> ingredientService.create(dto));
    }

    // ------------------- update -------------------
    @Test
    void update_success() {
        Ingredient existing = new Ingredient();
        IngredientDto dto = new IngredientDto(null, "Sugar");
        IngredientDto resultDto = new IngredientDto(1L, "Sugar");

        when(ingredientRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(ingredientMapper.toDto(existing)).thenReturn(resultDto);

        IngredientDto result = ingredientService.update(1L, dto);

        assertEquals(resultDto, result);
        assertEquals("Sugar", existing.getName());
    }

    @Test
    void update_notFound() {
        IngredientDto dto = new IngredientDto();

        when(ingredientRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> ingredientService.update(1L, dto));
    }
}
