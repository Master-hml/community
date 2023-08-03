package com.nowcoder.community.dao;

import com.nowcoder.community.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CommentMapper {

    List<Comment> selectCommentsByEntity(int entityType, int entityId, int offset, int limit);

    List<Comment> selectCommentsByUser(int userId, int offset, int limit);

    Comment selectCommentById(int id);

    int selectCountsByEntity(int entityType, int entityId);

    int selectCountsByUser(int entityType, int userId);

    int insertComment(Comment comment);

}
