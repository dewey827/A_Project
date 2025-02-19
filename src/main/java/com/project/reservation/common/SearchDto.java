package com.project.reservation.common;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@Setter
public class SearchDto {

    private String title;
    private String content;
    private String writerName;

    @Builder
    public SearchDto(String title, String content, String writerName) {
        this.title = title;
        this.content = content;
        this.writerName = writerName;
    }

    public static SearchDto searchNotice(String title, String content){
        return SearchDto.builder()
                .title(title)
                .content(content)
                .build();
    }

    public static SearchDto searchData(String title, String content, String writerName) {
        return SearchDto.builder()
                .title(title)
                .content(content)
                .writerName(writerName)
                .build();
    }
}
