package hung.haduc.picturesharingnetwork.services;

import hung.haduc.picturesharingnetwork.dtos.CommentDTO;
import hung.haduc.picturesharingnetwork.exceptions.DataNotFoundException;
import hung.haduc.picturesharingnetwork.exceptions.InvalidParameterException;
import hung.haduc.picturesharingnetwork.models.Comment;
import hung.haduc.picturesharingnetwork.models.Post;
import hung.haduc.picturesharingnetwork.models.User;
import hung.haduc.picturesharingnetwork.repositories.CommentRepository;
import hung.haduc.picturesharingnetwork.repositories.PostRepository;
import hung.haduc.picturesharingnetwork.responses.CommentResponse;
import hung.haduc.picturesharingnetwork.utilities.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;


@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final CurrentUser currentUser;

    public Page<CommentResponse> getAllCommentsByPostId(Long postId,
                                                        Integer sortOption,
                                                        Integer page,
                                                        Integer limit) throws DataNotFoundException {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new DataNotFoundException("Post not found !"));
        Sort.by(Sort.Direction.DESC, "createdAt");
        Sort sort = switch (sortOption) {
            case 1 -> Sort.by(Sort.Direction.DESC, "createdAt");
            case 2 -> Sort.by(Sort.Direction.ASC, "likes");
            default -> Sort.by(Sort.Direction.DESC, "createdAt");
        };
        return commentRepository.findAllByPost(post, PageRequest.of(page, limit, sort))
                .map(this::convertCommentToCommentResponse);
    }

    @Transactional
    public CommentResponse addComment(Long postId, CommentDTO commentDTO) throws DataNotFoundException {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new DataNotFoundException("Post not found !"));
        Comment newComment = commentRepository.save(this.convertCommentDTOToComment(commentDTO, post));
        return this.convertCommentToCommentResponse(newComment);
    }

    @Transactional
    public void deleteComment(Long id) throws DataNotFoundException, InvalidParameterException {
        Comment deletedComment = commentRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Comment not found !"));
        if (commentIsOwnUser(deletedComment)) {
            commentRepository.delete(deletedComment);
        } else {
            throw new InvalidParameterException("You have no permission to delete comment !");
        }
    }

    @Transactional
    public CommentResponse updateComment(Long id, CommentDTO commentDTO)
            throws DataNotFoundException, InvalidParameterException {
        Comment updatedComment = commentRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Comment not found !"));
        if (commentIsOwnUser(updatedComment)) {
            updatedComment.setText(commentDTO.getText());
            return this.convertCommentToCommentResponse(commentRepository.save(updatedComment));
        } else {
            throw new InvalidParameterException("You have no permission to delete comment !");
        }
    }

    private boolean commentIsOwnUser(Comment comment) throws DataNotFoundException {
        User user = currentUser.getCurrentUser();
        return Objects.equals(user.getId(), comment.getUserId().getId());
    }

    private Comment convertCommentDTOToComment(CommentDTO commentDTO, Post post)
            throws DataNotFoundException {
        User user = currentUser.getCurrentUser();
        return Comment.builder()
                .parent(commentDTO.getParentId())
                .text(commentDTO.getText())
                .userId(user)
                .post(post)
                .build();
    }

    private CommentResponse convertCommentToCommentResponse(Comment comment) {
        return CommentResponse.builder()
                .createdAt(comment.getCreatedAt())
                .parentId(comment.getParent())
                .text(comment.getText())
                .user(comment.getUserId())
                .id(comment.getId())
                .build();
    }

}
