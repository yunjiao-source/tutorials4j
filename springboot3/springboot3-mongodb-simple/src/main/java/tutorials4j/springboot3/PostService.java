package tutorials4j.springboot3;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 服务层
 *
 * @author Yun Jiao
 */
@Service
@RequiredArgsConstructor
public class PostService {
  private final PostRepository postRepository;

  // 创建文章
  public Post createPost(Post post) {
    post.setCreatedAt(LocalDateTime.now());
    post.setUpdatedAt(LocalDateTime.now());
    return postRepository.save(post);
  }

  // 获取所有文章
  public List<Post> getAllPosts() {
    return postRepository.findAll();
  }

  // 根据ID获取文章
  public Optional<Post> getPostById(String id) {
    return postRepository.findById(id);
  }

  // 更新文章
  public Post updatePost(String id, Post updatedPost) {
    Optional<Post> existingOpt = postRepository.findById(id);
    if (existingOpt.isPresent()) {
      Post existing = existingOpt.get();
      existing.setTitle(updatedPost.getTitle());
      existing.setContent(updatedPost.getContent());
      existing.setAuthor(updatedPost.getAuthor());
      existing.setUpdatedAt(LocalDateTime.now());
      return postRepository.save(existing);
    }
    throw new RuntimeException("Post not found with id: " + id);
  }

  // 删除文章
  public void deletePost(String id) {
    postRepository.deleteById(id);
  }

  // 添加评论
  public Post addComment(String postId, Comment comment) {
    Optional<Post> postOpt = postRepository.findById(postId);
    if (postOpt.isPresent()) {
      Post post = postOpt.get();
      comment.setCreatedAt(LocalDateTime.now());
      post.getComments().add(comment);
      post.setUpdatedAt(LocalDateTime.now());
      return postRepository.save(post);
    }
    throw new RuntimeException("Post not found with id: " + postId);
  }

  // 按作者查询
  public List<Post> getPostsByAuthor(String author) {
    return postRepository.findByAuthor(author);
  }

  // 按标题关键词查询
  public List<Post> searchPostsByTitle(String keyword) {
    return postRepository.findByTitleContainingIgnoreCase(keyword);
  }

  // 查询包含特定评论作者的文章
  public List<Post> getPostsByCommentAuthor(String commentAuthor) {
    return postRepository.findPostsByCommentAuthor(commentAuthor);
  }
}
