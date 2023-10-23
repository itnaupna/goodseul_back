package data.service.file;

import data.config.FileStorageProperties;
import data.exception.IllegalMimeTypeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class StorageService {

    private String uploadPath;

    public StorageService(FileStorageProperties fileStorageProperties) {
        this.uploadPath = fileStorageProperties.getUploadDir();
    }

    public List<String> saveFiles(MultipartFile[] files, String postName) throws IOException {
        List<String> fileNames = new ArrayList<>();

        for(MultipartFile file : files) {
            fileNames.add(String.valueOf(UUID.randomUUID()));
        }

        Path uploadPath = Paths.get(this.uploadPath + "/" + postName);
        if(!Files.exists(uploadPath)){
            Files.createDirectories(uploadPath);
            log.debug("make dir : " + uploadPath.toString());
        }

        for(int i = 0; i < files.length; i++) {
            try(InputStream inputStream = files[i].getInputStream()){
                Path filePath = uploadPath.resolve(fileNames.get(i));
                Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
            } catch (Exception exception){
                throw new IOException("Could not save image file : " + fileNames.get(i), exception);
            }
        }
        return fileNames;
    }

    public String saveFile(MultipartFile file, String userName) throws IOException {
        String fileName = String.valueOf(UUID.randomUUID());

        Path uploadPath = Paths.get(this.uploadPath + "/" + userName);
        if(!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        try(InputStream inputStream = file.getInputStream()) {
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
            return fileName;
        } catch (IOException ioe) {
            throw new IOException("Could not save image file : " + fileName, ioe);
        }
    }

}
