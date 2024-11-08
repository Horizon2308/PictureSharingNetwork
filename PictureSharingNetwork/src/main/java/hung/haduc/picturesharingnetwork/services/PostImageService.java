package hung.haduc.picturesharingnetwork.services;

import hung.haduc.picturesharingnetwork.repositories.PostImageRepository;
import hung.haduc.picturesharingnetwork.repositories.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostImageService {

    private final PostRepository postRepository;
    private final PostImageRepository postImageRepository;


}
