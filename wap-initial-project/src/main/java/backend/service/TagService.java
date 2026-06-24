package backend.service;

import backend.dto.CreateTagDTO;
import backend.dto.TagDTO;
import backend.dto.UpdateTagDTO;
import backend.model.Tag;
import backend.repository.TagRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class TagService {

    private final TagRepository tagRepository;

    public TagService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    public List<TagDTO> getAllTags() {
        return tagRepository.findAll().stream().map(this::mapToDto).collect(Collectors.toList());
    }

    public TagDTO getTag(Long id) {
        return mapToDto(findTag(id));
    }

    public TagDTO createTag(CreateTagDTO dto) {
        validate(dto.getName());
        tagRepository.findByName(dto.getName().trim())
                .ifPresent(existing -> {
                    throw new IllegalArgumentException("Tag name is already in use");
                });

        Tag tag = new Tag();
        tag.setName(dto.getName().trim());
        tag.setDescription(dto.getDescription());
        return mapToDto(tagRepository.save(tag));
    }

    public TagDTO updateTag(Long id, UpdateTagDTO dto) {
        Tag tag = findTag(id);
        if (dto.getName() != null) {
            validate(dto.getName());
            tagRepository.findByName(dto.getName().trim())
                    .filter(existing -> !existing.getId().equals(id))
                    .ifPresent(existing -> {
                        throw new IllegalArgumentException("Tag name is already in use");
                    });
            tag.setName(dto.getName().trim());
        }
        if (dto.getDescription() != null) {
            tag.setDescription(dto.getDescription());
        }
        return mapToDto(tagRepository.save(tag));
    }

    public void deleteTag(Long id) {
        tagRepository.delete(findTag(id));
    }

    private TagDTO mapToDto(Tag tag) {
        TagDTO dto = new TagDTO();
        dto.setId(tag.getId());
        dto.setName(tag.getName());
        dto.setDescription(tag.getDescription());
        return dto;
    }

    private Tag findTag(Long id) {
        return tagRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Tag not found"));
    }

    private void validate(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Tag name is required");
        }
    }
}
