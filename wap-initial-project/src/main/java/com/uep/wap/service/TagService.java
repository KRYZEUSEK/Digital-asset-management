package com.uep.wap.service;

import com.uep.wap.dto.TagDTO;
import com.uep.wap.model.Tag;
import com.uep.wap.repository.TagRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TagService {

    private final TagRepository tagRepository;

    public TagService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    public List<TagDTO> getAllTags() {
        return tagRepository.findAll().stream().map(this::mapToDto).collect(Collectors.toList());
    }

    private TagDTO mapToDto(Tag tag) {
        TagDTO dto = new TagDTO();
        dto.setId(tag.getId());
        dto.setName(tag.getName());
        dto.setDescription(tag.getDescription());
        return dto;
    }
}
