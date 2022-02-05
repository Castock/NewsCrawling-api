package com.example.demo.dto;

import com.example.demo.model.NewsEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NewsDTO {
    private String id;
    private String title;
    private String content;
    private boolean done;

    public NewsDTO(final NewsEntity entity){
        this.id = entity.getId();
        this.title = entity.getTitle();
        this.content = entity.getContent();
        this.done = entity.isDone();
    }
}
