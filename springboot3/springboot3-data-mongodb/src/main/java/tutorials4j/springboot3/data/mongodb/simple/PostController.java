package tutorials4j.springboot3.data.mongodb.simple;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 控制器层
 *
 * @author Yun Jiao
 */
@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {
  private final PostService postService;

  // 创建文章
  @PostMapping
  public ResponseEntity<Post> createPost(@RequestBody Post post) {
    Post created = postService.createPost(post);
    return new ResponseEntity<>(created, HttpStatus.CREATED);
  }

  // 获取所有文章
  @GetMapping
  public List<Post> getAllPosts() {
    return postService.getAllPosts();
  }

  // 根据ID获取文章
  @GetMapping("/{id}")
  public ResponseEntity<Post> getPostById(@PathVariable("id") String id) {
    return postService
        .getPostById(id)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  // 更新文章
  @PutMapping("/{id}")
  public ResponseEntity<Post> updatePost(@PathVariable("id") String id, @RequestBody Post post) {
    try {
      Post updated = postService.updatePost(id, post);
      return ResponseEntity.ok(updated);
    } catch (RuntimeException e) {
      return ResponseEntity.notFound().build();
    }
  }

  // 删除文章
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deletePost(@PathVariable("id") String id) {
    postService.deletePost(id);
    return ResponseEntity.noContent().build();
  }

  // 添加评论
  @PostMapping("/{postId}/comments")
  public ResponseEntity<Post> addComment(
      @PathVariable("postId") String postId, @RequestBody Comment comment) {
    try {
      Post updated = postService.addComment(postId, comment);
      return ResponseEntity.ok(updated);
    } catch (RuntimeException e) {
      return ResponseEntity.notFound().build();
    }
  }

  // 按作者查询
  @GetMapping("/author/{author}")
  public List<Post> getPostsByAuthor(@PathVariable("author") String author) {
    return postService.getPostsByAuthor(author);
  }

  // 按标题关键词搜索
  @GetMapping("/search")
  public List<Post> searchByTitle(@RequestParam String keyword) {
    return postService.searchPostsByTitle(keyword);
  }

  // 根据评论作者查询文章
  @GetMapping("/comment-author/{commentAuthor}")
  public List<Post> getPostsByCommentAuthor(@PathVariable("commentAuthor") String commentAuthor) {
    return postService.getPostsByCommentAuthor(commentAuthor);
  }
}
