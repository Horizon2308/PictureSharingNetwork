package hung.haduc.picturesharingnetwork.repositories;

import hung.haduc.picturesharingnetwork.models.Comment;
import hung.haduc.picturesharingnetwork.models.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;


public interface CommentRepository extends JpaRepository<Comment, Long> {
    Page<Comment> findAllByPost(Post post, PageRequest pageRequest);
}
