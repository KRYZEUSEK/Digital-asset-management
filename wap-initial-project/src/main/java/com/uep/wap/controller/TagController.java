package com.uep.wap.controller;

import com.uep.wap.dto.TagDTO;
import com.uep.wap.service.TagService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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
}
