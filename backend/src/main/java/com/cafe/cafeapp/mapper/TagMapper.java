package com.cafe.cafeapp.mapper;

import com.cafe.cafeapp.dto.TagDto;
import com.cafe.cafeapp.model.Tag;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TagMapper {

    TagDto toDto (Tag tag);

    List<TagDto> toDtoList (List<Tag> tags);

    @Mapping(target = "id", ignore = true)
    Tag toEntity (TagDto dto);
}
