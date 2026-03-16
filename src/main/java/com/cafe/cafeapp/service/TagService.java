package com.cafe.cafeapp.service;


import com.cafe.cafeapp.dto.TagDto;
import com.cafe.cafeapp.mapper.TagMapper;
import com.cafe.cafeapp.model.Tag;
import com.cafe.cafeapp.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TagService {
    private final TagRepository tagRepository;
    private final TagMapper tagMapper;

    @Transactional(readOnly = true)
    public List<TagDto> getAll() {
        return tagMapper.toDtoList(tagRepository.findAll());
    }

    @Transactional(readOnly = true)
    public TagDto getById (Long id) {
        Tag tag = tagRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Tag not found with id " + id));

        return tagMapper.toDto(tag);
    }

    @Transactional(readOnly = true)
    public List<TagDto> getByName (String name) {
        List<Tag> tag = tagRepository.findByName(name);

        return tagMapper.toDtoList(tag);
    }

    @Transactional
    public TagDto create (TagDto dto) {
        if (tagRepository.existsByName(dto.getName())) {
            throw new RuntimeException("Tag with this name already exists");
        }

        Tag tag = tagMapper.toEntity(dto);
        Tag saved = tagRepository.save(tag);

        return tagMapper.toDto(saved);
    }

    @Transactional
    public TagDto update (Long id, TagDto dto) {
        Tag existing = tagRepository.findById(id).orElseThrow();

        existing.setName(dto.getName());

        return tagMapper.toDto(tagRepository.save(existing));
    }

    @Transactional
    public void delete (Long id) {
        if (! tagRepository.existsById(id)) {
            throw new RuntimeException("Tag not found with id " + id);
        }

        tagRepository.deleteById(id);
    }
}
