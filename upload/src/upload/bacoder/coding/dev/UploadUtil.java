package upload.bacoder.coding.dev;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import org.apache.commons.codec.binary.Base64;
import org.imgscalr.Scalr;

import upload.bacoder.coding.bean.Person;

public class UploadUtil {
	Logger logger = Logger.getLogger(UploadUtil.class.getSimpleName());
	
	public String setPhoto(String path, String imgEncodedStr, String fileName, int patientId) {
		StringBuilder photoUrl = new StringBuilder();
		//photoUrl.append("http://hsbong.synology.me:7070/upload/img/");
		String ext = fileName.substring(fileName.lastIndexOf("."));
		String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss", Locale.KOREA).format(new Date());
		String pId = String.valueOf(patientId);
		
		File parentPath = new File(path + File.separator + pId);
		if(!parentPath.exists()) {
			parentPath.mkdirs();
		}
		// Write Image into File system - Make sure you update the path
		File file = new File(path + "/" + pId + File.separator + pId + "_" + timeStamp + ext);
		photoUrl.append(file.getName());
		String thumbnailBase = parentPath.getAbsolutePath() + "/" + pId + "_" + timeStamp;
		
		int fileSize = 0;
		int thumbnailSize = 0;
		String thumbnailFilename = thumbnailBase + "-thumbnail.JPG";
		String thumbnailUrl = "";
		
		logger.info("thumbnailFilename : "+ thumbnailFilename);
		
		try(FileOutputStream imageOutFile = new FileOutputStream(file)){
			byte[] imageByteArray = Base64.decodeBase64(imgEncodedStr); 
			imageOutFile.write(imageByteArray);	
			fileSize = (int)file.length();
			imageOutFile.close();
		}catch(FileNotFoundException e) {
			e.printStackTrace();
		}catch(IOException e) {
			e.printStackTrace();
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		try {
			BufferedImage thumbnail = Scalr.resize(ImageIO.read(file), Scalr.Method.QUALITY, 400, 300); 
		//	BufferedImage thumbnail = Scalr.resize(ImageIO.read(file), 290);
            File thumbnailFile = new File(thumbnailFilename);
            ImageIO.write(thumbnail, "jpg", thumbnailFile);
            
            thumbnailUrl = pId + "/" + thumbnailFile.getName().toString();
            thumbnailSize = (int)thumbnailFile.length();
//		}catch(FileNotFoundException e) {
//			e.printStackTrace();
//		}catch(IOException e) {
//			e.printStackTrace();
		}catch(Exception e) {
			e.printStackTrace();
		}
		logger.info(photoUrl.toString());
		
		return pId + "/" + photoUrl.toString() + ";" + thumbnailUrl + ";" + fileSize + ";" + thumbnailSize;
	}
	
//	public String makeThumbnail(int thumbnail_height){ //높이 비율로 가로를 조정
//		   String thumbnailName = null;
//		  
//		    try{
//		        BufferedImage originImage = ImageIO.read(newFile);
//		        thumbnailName="thumb_"+newFilename;
//		        thumbFile = new File(savePath,thumbnailName);
//		        if(!(originImage.getHeight()<=thumbnail_height)){
//		           //Scalr 라이브러리 : 그리는 과정을 resize로 생략해줌
//		           BufferedImage thumbImage = Scalr.resize(originImage, Scalr.Method.QUALITY, Mode.FIT_TO_HEIGHT, thumbnail_height);
//		           ImageIO.write(thumbImage, extension, thumbFile);
//		        }else{//이미 파일이 썸네일보다 작을 때 그냥 복사한다.
//		           byte[] newFiledata = FileCopyUtils.copyToByteArray(newFile);
//		           FileCopyUtils.copy(newFiledata, thumbFile);
//		        }
//		     }catch(Exception e){e.printStackTrace();}
//		   
//		   return thumbnailName;
//	}

	
	public Person setFile(String path, String imgEncodedStr, String fileName, Person person){
		
		try {
			// Decode String using Base64 Class
			byte[] imageByteArray = Base64.decodeBase64(imgEncodedStr); 
			
			String ext = fileName.substring(fileName.lastIndexOf("."));
			String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss", Locale.KOREA).format(new Date());
			String name = person.getName();
			
			// Write Image into File system - Make sure you update the path
			File file = new File(path + File.separator + name + "_" + timeStamp + ext);
			FileOutputStream imageOutFile = new FileOutputStream(file);
			imageOutFile.write(imageByteArray);
			person.setPhoto("http://hsbong.synology.me:7070/upload/" + file.getName());
			imageOutFile.close();
		} catch (FileNotFoundException fnfe) {
			System.out.println("Image Path not found" + fnfe);
		} catch (IOException ioe) {
			System.out.println("Exception while converting the Image " + ioe);
		}
		return person;
	}
}
