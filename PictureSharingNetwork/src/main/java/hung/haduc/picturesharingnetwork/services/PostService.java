package hung.haduc.picturesharingnetwork.services;

import hung.haduc.picturesharingnetwork.dtos.PostDTO;
import hung.haduc.picturesharingnetwork.exceptions.DataNotFoundException;
import hung.haduc.picturesharingnetwork.models.Post;
import hung.haduc.picturesharingnetwork.models.User;
import hung.haduc.picturesharingnetwork.repositories.PostRepository;
import hung.haduc.picturesharingnetwork.repositories.UserRepository;
import hung.haduc.picturesharingnetwork.responses.PostDetailResponse;
import hung.haduc.picturesharingnetwork.responses.PostResponse;
import hung.haduc.picturesharingnetwork.utilities.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CurrentUser currentUser;


    public Page<PostResponse> getAllPostsFollowed(Integer sortOption,
                                                  String keyword,
                                                  Integer page,
                                                  Integer limit)
            throws DataNotFoundException {
        User user = currentUser.getCurrentUser();
        return postRepository
                .searchPostsByFollowedUser(keyword,
                        user.getId(),
                        PageRequest.of(page, limit, this.getSortOption(sortOption)))
                .map(post -> {
                    try {
                        return this.convertPostToPostResponse(post);
                    } catch (DataNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    public Page<PostResponse> getAllPostsByUserId(Integer sortOption,
                                    String keyword,
                                    Long userId,
                                    Integer page,
                                    Integer limit)
            throws DataNotFoundException {
        User user = currentUser.getCurrentUser();
        if (Objects.equals(userId, user.getId())) {
            return postRepository
                    .searchPostsForOwnProfileUser(keyword,
                            userId,
                            PageRequest.of(page, limit, this.getSortOption(sortOption)))
                    .map(post -> {
                        try {
                            return this.convertPostToPostResponse(post);
                        } catch (DataNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    });
        } else {
            return postRepository
                    .searchPostsByUserId(keyword,
                            userId,
                            PageRequest.of(page, limit, this.getSortOption(sortOption)))
                    .map(post -> {
                        try {
                            return this.convertPostToPostResponse(post);
                        } catch (DataNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    });
        }
    }

    @Transactional
    public PostResponse createPost(PostDTO postDTO) throws DataNotFoundException {
        return convertPostToPostResponse(postRepository.save(this.convertPostDTOToPost(postDTO)));
    }

    public PostDetailResponse getPostDetails(Long postId) throws DataNotFoundException {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new DataNotFoundException("Post not found !"));
        return PostDetailResponse.builder()
                .postImages(post.getPostImages())
                .comments(post.getComments())
                .description(post.getDescription())
                .likes(post.getLikes())
                .location(post.getLocation())
                .tag(post.getTag())
                .thumbnail(post.getThumbnail())
                .title(post.getTitle())
                .build();
    }

    @Transactional
    public void updatePost(Long id, PostDTO postDTO) throws DataNotFoundException {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Post not found !"));
        post.setDescription(postDTO.getDescription());
        post.setLocation(postDTO.getLocation());
        post.setShareFor(postDTO.getShareFor());
        post.setTag(postDTO.getTag());
        post.setTitle(postDTO.getTitle());
        postRepository.save(post);
    }

    @Transactional
    public void deletePost(Long id) throws DataNotFoundException {
        Post deletedPost = postRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Post not found !"));
        postRepository.delete(deletedPost);
    }

    private Sort getSortOption(Integer sortOption) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        switch (sortOption) {
            case 1:
                sort = Sort.by(Sort.Direction.DESC, "createdAt");
                break;
            case 2:
                sort = Sort.by(Sort.Direction.ASC, "likes");
                break;
            case 3:
                sort = Sort.by(Sort.Direction.ASC, "comments");
                break;
        }
        return sort;
    }



    private Post convertPostDTOToPost(PostDTO postDTO) throws DataNotFoundException {
        User user = currentUser.getCurrentUser();
        return Post.builder()
                .comments(0L)
                .description(postDTO.getDescription())
                .title(postDTO.getTitle())
                .tag(postDTO.getTag())
                .location(postDTO.getLocation())
                .shareFor(postDTO.getShareFor())
                .likes(0L)
                .userId(user)
                .build();
    }

    private PostResponse convertPostToPostResponse(Post post) throws DataNotFoundException {
        User user = currentUser.getCurrentUser();
        return PostResponse.builder()
                .id(post.getId())
                .likes(post.getLikes())
                .comments(post.getComments())
                .tag(post.getTag())
                .nickname(user.getNickname())
                .build();
    }

}
