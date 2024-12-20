package com.FALineBot.EndPoint.Controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.FALineBot.EndPoint.Model.Bento;
import com.FALineBot.EndPoint.Model.Cook;
import com.FALineBot.EndPoint.Model.Vocabulary;
import com.FALineBot.EndPoint.Service.CookingService;
import com.FALineBot.EndPoint.Service.VocabularyService;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.coobird.thumbnailator.Thumbnails;


@RequestMapping("/service")
@RestController
public class ServiceController {

	@Autowired
	private CookingService cookingService;
	@Autowired
	private VocabularyService vocabularyService;
	// Update the upload directory path
	private static String root = System.getProperty("user.dir");
    private static final String UPLOAD_DIR = root+"/src/main/resources/static/CookPic/";
	
	@GetMapping("/ShowCookingList/{wisher}")
    public List<Cook> ShowCookingList(@PathVariable String wisher,Model model) {
		System.out.println("wisher: "+wisher);
        List<Cook> cookingList = cookingService.getCookingList(wisher);

        return cookingList; // 返回HTML模板的名稱
    }

	@GetMapping("/BentoInfo/{wisher}/{dateString}")
	public ResponseEntity<?> ShowBentoInfo(@PathVariable String wisher, @PathVariable String dateString, Model model) {
	    try {
	        Bento bento = cookingService.getBentoInfo(dateString, wisher);
	        if (bento == null) {
	            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No Bento found for the provided wisher and dateString.");
	        }
	        return ResponseEntity.ok(bento);
	    } catch (Exception e) {
	        // Log the error
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while processing the request: " + e.getMessage());
	    }
	}
	
    @PostMapping("/AddBento")
    public ResponseEntity<?> addBento(@RequestBody Bento bento) {
        try {
        	//System.out.println("我執行了這個動作");
        	// 創建 ObjectMapper 實例
        	ObjectMapper objectMapper = new ObjectMapper();
        	 String bentoJson = objectMapper.writeValueAsString(bento);
        	System.out.println("bento: "+bentoJson.toString());
        	cookingService.addBento(bento);
        	
            return ResponseEntity.status(HttpStatus.CREATED).body("Bento added successfully.");
        } catch (Exception e) {
            // Log the error
            e.printStackTrace();
            // 如果出現錯誤，則返回錯誤訊息
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while adding the Bento: " + e.getMessage());
        }
    }
	
    @PostMapping("/uploadfile/{bentoID}")
    public ResponseEntity<String> handleFileUpload(@PathVariable String bentoID,@RequestParam("file") MultipartFile file) {
    	
    	//String test = System.getProperty("user.dir");
    	//System.out.println(test);
    	
        if (file.isEmpty()) {
            return new ResponseEntity<>("Please select a file to upload.", HttpStatus.BAD_REQUEST);
        }

        try {
            // Create the directory if it does not exist
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Save the original file
            String originalFileName = file.getOriginalFilename();
            File originalFile = new File(UPLOAD_DIR + originalFileName);
            file.transferTo(originalFile);
            
            Bento bento = cookingService.getBentoById(bentoID);
            
         // Extract the file extension from the original file name
            String fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));

            
            // Compress the file by 40%
            File compressedFile = new File(UPLOAD_DIR + "compressed_" + bentoID);
            Thumbnails.of(originalFile)
                      .scale(0.6) // 60% of the original size, which is a 40% reduction
                      .toFile(compressedFile);
            
            bento.setBentoPicName( "compressed_" + bentoID+ fileExtension);
            System.out.println(bento.getBentoPicName());
            cookingService.addBento(bento);

            // Delete the original file after compression
            if (originalFile.delete()) {
                System.out.println("Original file deleted successfully.");
            } else {
                System.out.println("Failed to delete the original file.");
            }
            
            return new ResponseEntity<>("File uploaded and compressed successfully", HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>("File upload failed: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping("/deleteVocabulary/{id}")
    public ResponseEntity<?> deleteVoc (@PathVariable int id){
    	vocabularyService.deleteVocabulary(id);
    	return ResponseEntity.status(HttpStatus.CREATED).body("Bento added successfully.");
    }
    
	
	//@GetMapping("/RecordSmoke")
	//public void RecordSmoke() {
	//	smokeservice.RecordSmokeTime("123");
	//}
	
	//@GetMapping("/ReplySmoke")
	//public void ReplySmoke() {
	//	smokeservice.ReplySmokeTime("123");
	//}
}
