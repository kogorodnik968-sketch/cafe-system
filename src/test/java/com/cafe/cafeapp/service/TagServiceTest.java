package com.cafe.cafeapp.service;

import com.cafe.cafeapp.dto.TagDto;
import com.cafe.cafeapp.exception.AlreadyExistsException;
import com.cafe.cafeapp.exception.DeleteNotAllowedException;
import com.cafe.cafeapp.exception.NotFoundException;
import com.cafe.cafeapp.mapper.TagMapper;
import com.cafe.cafeapp.model.Product;
import com.cafe.cafeapp.model.Tag;
import com.cafe.cafeapp.repository.TagRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TagServiceTest {

    @Mock
    private TagRepository tagRepository;

    @Mock
    private TagMapper tagMapper;

    @InjectMocks
    private TagService tagService;

    @Test
    void getAll_shouldReturnDtoList() {
        List<Tag> tags = List.of(new Tag());
        List<TagDto> dtos = List.of(new TagDto());

        when(tagRepository.findAll()).thenReturn(tags);
        when(tagMapper.toDtoList(tags)).thenReturn(dtos);

        List<TagDto> result = tagService.getAll();

        assertEquals(dtos, result);
        verify(tagRepository).findAll();
        verify(tagMapper).toDtoList(tags);
    }

    @Test
    void getById_shouldReturnDto_whenFound() {
        Tag tag = new Tag();
        TagDto dto = new TagDto();

        when(tagRepository.findById(1L)).thenReturn(Optional.of(tag));
        when(tagMapper.toDto(tag)).thenReturn(dto);

        TagDto result = tagService.getById(1L);

        assertEquals(dto, result);
        verify(tagRepository).findById(1L);
        verify(tagMapper).toDto(tag);
    }

    @Test
    void getById_shouldThrow_whenNotFound() {
        when(tagRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> tagService.getById(1L));

        verify(tagRepository).findById(1L);
        verify(tagMapper, never()).toDto(any());
    }

    @Test
    void getByName_shouldReturnDtoList() {
        List<Tag> tags = List.of(new Tag());
        List<TagDto> dtos = List.of(new TagDto());

        when(tagRepository.findByName("Coffee")).thenReturn(tags);
        when(tagMapper.toDtoList(tags)).thenReturn(dtos);

        List<TagDto> result = tagService.getByName("Coffee");

        assertEquals(dtos, result);
        verify(tagRepository).findByName("Coffee");
        verify(tagMapper).toDtoList(tags);
    }

    @Test
    void getByName_shouldReturnEmptyList_whenNotFound() {
        when(tagRepository.findByName("NonExistent")).thenReturn(List.of());

        List<TagDto> result = tagService.getByName("NonExistent");

        assertEquals(List.of(), result);
        verify(tagRepository).findByName("NonExistent");
        verify(tagMapper).toDtoList(List.of());
    }

    @Test
    void create_shouldSaveAndReturnDto() {
        TagDto dto = new TagDto(null, "Coffee");
        Tag entity = new Tag();
        Tag saved = new Tag();
        TagDto resultDto = new TagDto(1L, "Coffee");

        when(tagRepository.existsByName("Coffee")).thenReturn(false);
        when(tagMapper.toEntity(dto)).thenReturn(entity);
        when(tagRepository.save(entity)).thenReturn(saved);
        when(tagMapper.toDto(saved)).thenReturn(resultDto);

        TagDto result = tagService.create(dto);

        assertEquals(resultDto, result);
        verify(tagRepository).existsByName("Coffee");
        verify(tagMapper).toEntity(dto);
        verify(tagRepository).save(entity);
        verify(tagMapper).toDto(saved);
    }

    @Test
    void create_shouldThrow_whenNameExists() {
        TagDto dto = new TagDto(null, "Coffee");

        when(tagRepository.existsByName("Coffee")).thenReturn(true);

        assertThrows(AlreadyExistsException.class,
                () -> tagService.create(dto));

        verify(tagRepository).existsByName("Coffee");
        verify(tagMapper, never()).toEntity(any());
        verify(tagRepository, never()).save(any());
    }

    @Test
    void update_shouldUpdateAndReturnDto() {
        Tag existing = new Tag();
        existing.setName("OldName");
        TagDto dto = new TagDto(null, "NewName");
        Tag saved = new Tag();
        saved.setName("NewName");
        TagDto resultDto = new TagDto(1L, "NewName");

        when(tagRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(tagRepository.save(existing)).thenReturn(saved);
        when(tagMapper.toDto(saved)).thenReturn(resultDto);

        TagDto result = tagService.update(1L, dto);

        assertEquals(resultDto, result);
        assertEquals("NewName", existing.getName());
        verify(tagRepository).findById(1L);
        verify(tagRepository).save(existing);
        verify(tagMapper).toDto(saved);
    }

    @Test
    void update_shouldThrow_whenNotFound() {
        TagDto dto = new TagDto(null, "NewName");

        when(tagRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> tagService.update(1L, dto));

        verify(tagRepository).findById(1L);
        verify(tagRepository, never()).save(any());
        verify(tagMapper, never()).toDto(any());
    }

    @Test
    void delete_shouldThrow_whenNotExists() {
        when(tagRepository.existsById(1L)).thenReturn(false);

        assertThrows(NotFoundException.class,
                () -> tagService.delete(1L));

        verify(tagRepository).existsById(1L);
        verify(tagRepository, never()).findById(any());
        verify(tagRepository, never()).deleteById(any());
    }

    @Test
    void delete_shouldThrow_whenHasProducts() {
        Tag tag = new Tag();
        tag.setProducts(List.of(new Product()));

        when(tagRepository.existsById(1L)).thenReturn(true);
        when(tagRepository.findById(1L)).thenReturn(Optional.of(tag));

        assertThrows(DeleteNotAllowedException.class,
                () -> tagService.delete(1L));

        verify(tagRepository).existsById(1L);
        verify(tagRepository).findById(1L);
        verify(tagRepository, never()).deleteById(any());
    }

    @Test
    void delete_shouldDelete_whenValid() {
        Tag tag = new Tag();
        tag.setProducts(List.of());

        when(tagRepository.existsById(1L)).thenReturn(true);
        when(tagRepository.findById(1L)).thenReturn(Optional.of(tag));

        tagService.delete(1L);

        verify(tagRepository).existsById(1L);
        verify(tagRepository).findById(1L);
        verify(tagRepository).deleteById(1L);
    }
}