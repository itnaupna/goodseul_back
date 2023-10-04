package data.controller.file;

import data.dto.Response;
import data.service.file.StorageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin
@Slf4j
@Api(tags = "파일 API")
@RequestMapping("/api/lv1/image")
public class FileController {

    private final StorageService storageService;

    public FileController(StorageService storageService) {
        this.storageService = storageService;
    }

    @ApiOperation(value = "File Upload API", notes = "파일 단건 업로드 API")
    @PostMapping
    public ResponseEntity<Response> uploadImage(
            @ApiParam(value = "업로드할 파일", required = true)
            @RequestParam("file") MultipartFile file,

            @ApiParam(value = "파일 저장 경로", required = true)
            @RequestParam("path") String path) throws IOException {

        Response res = new Response();
        try{
            String result = storageService.saveFile(file,path);
            res.setImageLocation("/" + path + "/" +result);
            res.setMessage("done");
            res.setSuccess(true);
            return new ResponseEntity<>(res, HttpStatus.OK);
        } catch (Exception e) {
            res.setMessage("failed");
            res.setSuccess(false);
            return new ResponseEntity<>(res, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @ApiOperation(value = "MultiPle File Upload API", notes = "파일 다건 업로드 API")
    @PostMapping("/multiple")
    public ResponseEntity<Response> postImageUpload (
            @ApiParam(value = "업로드할 파일들", required = true)
            @RequestParam("files") MultipartFile[] files,

            @ApiParam(value = "파일 저장 경로", required = true)
            @RequestParam("path") String path) {
        Response res = new Response();
        List<String> results = new ArrayList<>();
        List<String> imageLocations = new ArrayList<>();

        try {
            results = storageService.saveFiles(files,path);
            for(String result : results) {
                imageLocations.add("/" + path + "/" + result);
            }
            res.setImageLocations(imageLocations);
            res.setMessage("done");
            res.setSuccess(true);
            return new ResponseEntity<Response>(res,HttpStatus.OK);
        } catch (Exception e) {
            res.setMessage("failed");
            res.setSuccess(false);
            return new ResponseEntity<Response>(res, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
