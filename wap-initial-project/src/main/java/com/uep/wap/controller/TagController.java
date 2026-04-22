package com.uep.wap.controller;

import com.uep.wap.dto.CreateTagDTO;
import com.uep.wap.dto.TagDTO;
import com.uep.wap.dto.UpdateTagDTO;
import com.uep.wap.service.TagService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/tags")
public class TagController {

    private final TagService tagService;

    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    @GetMapping
    public List<TagDTO> getTags() {
        return tagService.getAllTags();
    }

    @GetMapping("/{id}")
    public TagDTO getTag(@PathVariable Long id) {
        return tagService.getTag(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TagDTO createTag(@RequestBody CreateTagDTO dto) {
        return tagService.createTag(dto);
    }

    @PutMapping("/{id}")
    public TagDTO updateTag(@PathVariable Long id, @RequestBody UpdateTagDTO dto) {
        return tagService.updateTag(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTag(@PathVariable Long id) {
        tagService.deleteTag(id);
    }
}
