package hung.haduc.picturesharingnetwork.utilities;

import hung.haduc.picturesharingnetwork.exceptions.InvalidParameterException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class FileUtilities {

    public String storeFile(MultipartFile file) throws IOException {
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename())); // get the file name
        // append a random unique string to identify each images
        String uniqueFileName = UUID.randomUUID().toString() + "_" + fileName;
        // get the file path
        Path uploadDir = Paths.get("uploads");
        // if file path doesn't exist, create a new file path with path name is uploads
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }
        // get the full file path to save the file
        Path destination = Paths.get(uploadDir.toString(), uniqueFileName);
        // then save the file
        Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
        return uniqueFileName;
    }

    public MultipartFile handleMultipartFile(MultipartFile file) throws InvalidParameterException {
        if (file == null || file.getSize() == 0) {
            assert file != null;
            throw new InvalidParameterException(file.getOriginalFilename() + " is invalid image");
        }
        if (file.getSize() > 10 * 1024 * 1024) {
            throw new InvalidParameterException(file.getOriginalFilename()
                    + " is too large! Maximum size is 10MB");
        }
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new InvalidParameterException(file.getOriginalFilename() + " is invalid image");
        }
        return file;
    }

}
