package hung.haduc.picturesharingnetwork.services;

import hung.haduc.picturesharingnetwork.exceptions.DataNotFoundException;
import hung.haduc.picturesharingnetwork.exceptions.InvalidParameterException;
import hung.haduc.picturesharingnetwork.models.Post;
import hung.haduc.picturesharingnetwork.models.PostImage;
import hung.haduc.picturesharingnetwork.models.User;
import hung.haduc.picturesharingnetwork.repositories.PostImageRepository;
import hung.haduc.picturesharingnetwork.repositories.PostRepository;
import hung.haduc.picturesharingnetwork.repositories.UserRepository;
import hung.haduc.picturesharingnetwork.utilities.FileUtilities;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostImageService {

    private final PostRepository postRepository;
    private final PostImageRepository postImageRepository;
    private final UserRepository userRepository;
    private final FileUtilities fileUtilities;

    @Transactional
    public void saveAllImages(Long postId, ArrayList<MultipartFile> files)
            throws DataNotFoundException, InvalidParameterException, IOException {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findUserByEmail(authentication.getName())
                .orElseThrow(() -> new DataNotFoundException("User not found !"));
        Post post = postRepository.findByIdAndUserId(postId, user)
                .orElseThrow(() -> new DataNotFoundException("Post not found !"));
        for (int i = 0; i < files.size(); i++) {
            String fileUrl = fileUtilities
                    .storeFile(fileUtilities.handleMultipartFile(files.get(i)));
            postImageRepository.save(PostImage.builder()
                            .post(post)
                            .imageUrl(fileUrl)
                            .build());
            if (i == 0) {
                post.setThumbnail(fileUrl);
                postRepository.save(post);
            }
        }
    }

}
