package tutorials4j.springboot3;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 仓库接口
 *
 * @author Yun Jiao
 */
@Repository
public interface PostRepository extends MongoRepository<Post, String> {

    // 根据作者查询
    List<Post> findByAuthor(String author);

    // 根据标题模糊查询（忽略大小写）
    List<Post> findByTitleContainingIgnoreCase(String keyword);

    // 使用 JSON 查询语句：查询 comments 中某个 author 的评论
    @Query("{ 'comments.author': ?0 }")
    List<Post> findPostsByCommentAuthor(String author);
}