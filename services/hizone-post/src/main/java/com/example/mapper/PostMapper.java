package com.example.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.example.hizone.request.post.ModifyPost;
import com.example.hizone.request.post.UploadPost;
import com.example.hizone.table.post.Post;

@Mapper
public interface PostMapper {

    @Select("select * FROM post WHERE post_id = #{postId}")
    Post selectPostById(Long postId);

    @Select("select * from post where author_id = #{authorId}")
    List<Post> selectPostListByAuthorId(Long authorId);

    @Select("select * from post order by post_time desc limit 50")
    List<Post> selectPush();

    @Options(useGeneratedKeys = true, keyProperty = "postId")
    @Insert("insert into post(author_id, post_title, post_content) values(#{authorId} , #{postTitle}, #{postContent})")
    void insertPost(UploadPost uploadPost);

    @Update("update post set post_title = #{postTitle}, post_content = #{postContent} where post_id = #{postId}")
    void updatePostById(ModifyPost modifyPost);

    @Delete("delete from post where post_id = #{postId}")
    void deletePostById(Long postId);
}
