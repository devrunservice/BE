package com.devrun.dto;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CommentDTO {
    private int commentNo, noticeNo, parentCommentNo, userNo;
    private String content, id, profileimgsrc;
    private Date createdDate, modifiedDate;
}
