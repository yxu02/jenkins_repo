package net.genebolt.webserv.service;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.time.Instant;
import java.util.Date;

import net.genebolt.webserv.exception.ImageServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import net.genebolt.webserv.model.UserImage;

@Service
public class ImageService {

	@Autowired
	private AmazonS3Client s3Client;

	private static final String S3_BUCKET_NAME = "movielovers-cmpe281proj1";


	/**
	 * Save image to S3 and return UserImage containing key and public URL
	 * 
	 * @param multipartFile
	 * @return
	 * @throws IOException
	 */
	public UserImage saveImageToS3(MultipartFile multipartFile) throws ImageServiceException {

		try{
			File uploadImage = convertFromMultiPart(multipartFile);

            /* get timestamp instant instance */
			Instant instant = Instant.now();
			String key = instant.getEpochSecond() + "_" + uploadImage.getName();

			/* save image */
			s3Client.putObject(new PutObjectRequest(S3_BUCKET_NAME, key, uploadImage));

			/* get signed URL and set httpMethod */
			GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(S3_BUCKET_NAME, key);
			generatePresignedUrlRequest.setMethod(HttpMethod.GET);
			URL imageUrl = s3Client.generatePresignedUrl(generatePresignedUrlRequest);

            /* get last-modified timestamp from s3 obj metadata */
			Date lastModified = s3Client.getObjectMetadata(S3_BUCKET_NAME, key).getLastModified();

			return new UserImage(key, Date.from(instant), lastModified, imageUrl.toString());
		}
		catch(Exception ex){			
			throw new ImageServiceException("An error occurred saving image to S3", ex);
		}		
	}

	/**
	 * Update file on S3 using specified key
	 *
	 * @param key
	 * @param url
	 * @param multipartFile
	 * @return
	 * @throws IOException
	 */
	public UserImage updateImageFromS3(Long iid, String key, String url, MultipartFile multipartFile) throws ImageServiceException {

		try{
			File uploadFile = convertFromMultiPart(multipartFile);

			/* update file using userOldImage key */
			s3Client.putObject(new PutObjectRequest(S3_BUCKET_NAME, key, uploadFile));

            /* get last-modified timestamp from userOldContent metadata, and update */
			Date lastModified = s3Client.getObjectMetadata(S3_BUCKET_NAME, key).getLastModified();
			return new UserImage(iid, key, lastModified, lastModified, url);
		}
		catch(Exception ex){
			throw new ImageServiceException("Error at updating a file to S3", ex);
		}
	}

	/**
	 * Delete image from S3 using specified key
	 * 
	 * @param userImage
	 */
	public void deleteImageFromS3(UserImage userImage){
		s3Client.deleteObject(new DeleteObjectRequest(S3_BUCKET_NAME, userImage.getKey()));
	}

	/**
	 * Convert MultiPartFile to ordinary File
	 * 
	 * @param multipartFile
	 * @return
	 * @throws IOException
	 */
	private File convertFromMultiPart(MultipartFile multipartFile) throws IOException {

		File file = new File(multipartFile.getOriginalFilename());
		file.createNewFile(); 
		FileOutputStream fos = new FileOutputStream(file); 
		fos.write(multipartFile.getBytes());
		fos.close(); 

		return file;
	}
}