package hung.haduc.picturesharingnetwork.services;

import hung.haduc.picturesharingnetwork.dtos.PostDTO;
import hung.haduc.picturesharingnetwork.exceptions.DataNotFoundException;
import hung.haduc.picturesharingnetwork.models.Post;
import hung.haduc.picturesharingnetwork.models.User;
import hung.haduc.picturesharingnetwork.repositories.PostRepository;
import hung.haduc.picturesharingnetwork.repositories.UserRepository;
import hung.haduc.picturesharingnetwork.responses.PostResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;


    public List<PostResponse> getAllPostsFollowed(Integer sortOption,
                                                  String keyword,
                                                  Integer page,
                                                  Integer limit)
            throws DataNotFoundException {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findUserByEmail(authentication.getName())
                .orElseThrow(() -> new DataNotFoundException("User not found !"));
        return postRepository
                .searchPostsByFollowedUser(keyword,
                        user.getId(),
                        PageRequest.of(page, limit, this.getSortOption(sortOption)))
                .stream()
                .map(post -> this.convertPostToPostResponse(post, user))
                .toList();
    }

    public List<PostResponse> getAllPostsByUserId(Integer sortOption,
                                              String keyword,
                                              Long userId,
                                              Integer page,
                                              Integer limit)
            throws DataNotFoundException {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findUserByEmail(authentication.getName())
                .orElseThrow(() -> new DataNotFoundException("User not found !"));
        if (Objects.equals(userId, user.getId())) {
            return postRepository
                    .searchPostsForOwnProfileUser(keyword,
                            userId,
                            PageRequest.of(page, limit, this.getSortOption(sortOption)))
                    .map(post -> this.convertPostToPostResponse(post, user))
                    .toList();
        } else {
            return postRepository
                    .searchPostsByUserId(keyword,
                            userId,
                            PageRequest.of(page, limit, this.getSortOption(sortOption)))
                    .map(post -> this.convertPostToPostResponse(post, user))
                    .toList();
        }
    }

    public void createPost(PostDTO postDTO) throws DataNotFoundException {
        postRepository.save(this.convertPostDTOToPost(postDTO));
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
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findUserByEmail(authentication.getName())
                .orElseThrow(() -> new DataNotFoundException("User not found !"));
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

    private PostResponse convertPostToPostResponse(Post post, User user) {
        return PostResponse.builder()
                .likes(post.getLikes())
                .comments(post.getComments())
                .tag(post.getTag())
                .nickname(user.getNickname())
                .build();
    }

}
