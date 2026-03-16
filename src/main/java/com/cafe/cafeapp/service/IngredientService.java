package com.cafe.cafeapp.service;

import com.cafe.cafeapp.dto.IngredientDto;
import com.cafe.cafeapp.mapper.IngredientMapper;
import com.cafe.cafeapp.model.Ingredient;
import com.cafe.cafeapp.repository.IngredientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class IngredientService {

    private final IngredientRepository ingredientRepository;
    private final IngredientMapper ingredientMapper;

    @Transactional(readOnly = true)
    public List<IngredientDto> getAll() {
        return ingredientMapper.toDtoList(ingredientRepository.findAll());
    }

    @Transactional(readOnly = true)
    public IngredientDto getById(Long id) {
        Ingredient ingredient = ingredientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ingredient not found"));

        return ingredientMapper.toDto(ingredient);
    }

    @Transactional
    public IngredientDto create(IngredientDto dto) {
        if (ingredientRepository.existsByName(dto.getName())) {
            throw new RuntimeException("Ingredient with this name already exists");
        }
        Ingredient ingredient = ingredientMapper.toEntity(dto);

        Ingredient saved = ingredientRepository.save(ingredient);

        return ingredientMapper.toDto(saved);
    }

    @Transactional
    public IngredientDto update(Long id, IngredientDto dto) {
        Ingredient existing = ingredientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ingredient not found"));

        existing.setName(dto.getName());

        return ingredientMapper.toDto(existing);
    }

    @Transactional
    public void delete(Long id) {
        if (!ingredientRepository.existsById(id)) {
            throw new RuntimeException("Ingredient not found");
        }

        ingredientRepository.deleteById(id);
    }
}

