package com.georent.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.HttpMethod;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.amazonaws.services.s3.model.DeleteObjectsResult;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.georent.config.S3ConfigurationProperties;
import com.georent.exception.MultiPartFileValidationException;
import com.georent.message.GeoRentIHttpStatus;
import com.georent.message.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.AssertTrue;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;


@Slf4j
@Service
public class AWSS3Service {

    public final AmazonS3 s3Client;
    public final S3ConfigurationProperties s3Properties;

    @Autowired
    public AWSS3Service(AmazonS3 s3Client,
                        S3ConfigurationProperties s3ConfigurationProperties) {
        this.s3Client = s3Client;
        this.s3Properties = s3ConfigurationProperties;
    }

    /**
     * if file null - exception
     * if multipart.getContentType() not MediaType.IMAGE_JPEG_VALUE - exception
     * if size file more 200 kb - - exception
     *
     * @param multipart
     * @return
     */
    public boolean multiPartFileValidation (MultipartFile multipart) {
        Assert.notNull(multipart, Message.INVALID_FILE_NULL.getDescription());
        if (!multipart.getContentType().equals(MediaType.IMAGE_JPEG_VALUE)) {
            throw new MultiPartFileValidationException(GeoRentIHttpStatus.INVALID_FILE_EXTENSION_JPG.getReasonPhrase());
        }
        if (multipart.getSize() > this.s3Properties.getFileSizeMax()) {
            throw new MultiPartFileValidationException(GeoRentIHttpStatus.INVALID_FILE_SIZE.getReasonPhrase());
        }
        return true;
    }

    /**
     *
     * @param object
     * @return
     */
    public String base64Validation(String object) {
        int pos = (object.indexOf("image/jpeg", 0));
        if (pos < 0) {
            throw new MultiPartFileValidationException(GeoRentIHttpStatus.INVALID_FILE_EXTENSION_JPG.getReasonPhrase());
        }
        String objectStr = object.replaceAll("data:image/jpeg;base64", "");
        if (objectStr.isEmpty()) {
            return object;
        }
        String objectS = object.replaceAll("data:image/jpeg;base64,", "");
        if (objectS.length() > this.s3Properties.getFileSizeMax()) {
            throw new MultiPartFileValidationException(GeoRentIHttpStatus.INVALID_FILE_SIZE.getReasonPhrase());
        }
        return objectS;
    }


    public String generateKeyFileName() {
        return UUID.randomUUID().toString();
    }

    /**
     *
     * @param multipartFile
     * @param keyFileName
     * @throws SdkClientException     If any errors are encountered in the client while making the
     *                                request or handling the response. return null
     * @throws AmazonServiceException If any errors occurred in Amazon S3 while processing the
     *                                request. return null
     */
    public String uploadFileToS3bucket(MultipartFile multipartFile, String keyFileName) {
        try {
            InputStream inputStream = new ByteArrayInputStream(multipartFile.getBytes());
            ObjectMetadata meta = new ObjectMetadata();
            meta.setContentLength(multipartFile.getBytes().length);
            meta.setContentType(MediaType.IMAGE_JPEG_VALUE);
            s3Client.putObject(new PutObjectRequest(s3Properties.getBucketName(), keyFileName, inputStream, meta));
            return  keyFileName;
        } catch (IOException e) {
            log.error("Unable to upload  File To S3bucket", e);
            return null;
        }
    }

    /**
     *
     * @param encodedString
     * @param keyFileName
     * @return
     */
    public String uploadFileBase64ToS3bucket(String encodedString, String keyFileName) {
        try {
            byte[] decodedBytes = Base64.getDecoder().decode(new String(encodedString).getBytes("UTF-8"));
            InputStream inputStream = new ByteArrayInputStream(decodedBytes);
            ObjectMetadata meta = new ObjectMetadata();
            meta.setContentLength(decodedBytes.length);
            meta.setContentType(MediaType.IMAGE_JPEG_VALUE);
            s3Client.putObject(new PutObjectRequest(s3Properties.getBucketName(), keyFileName, inputStream, meta));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return keyFileName;
    }

    /**
     * @param keyFileName
     * @return URL or null if error
     */

    public URL generatePresignedURL(String keyFileName) {
        URL url = null;
        try {
            // Set the presigned URL to expire after expires-in pref: aws.
            java.util.Date expiration = new java.util.Date();
            expiration.setTime(expiration.getTime() + s3Properties.getExpiresIn());
            GeneratePresignedUrlRequest generatePresignedUrlRequest =
                    new GeneratePresignedUrlRequest(s3Properties.getBucketName(), keyFileName)
                            .withMethod(HttpMethod.GET)
                            .withExpiration(expiration);
            url = s3Client.generatePresignedUrl(generatePresignedUrlRequest);
        }catch (SdkClientException e) {
            log.error("Unable to upload picture, generate Presigned URL", e);
        }
        return url;
    }

    /**
     * delete all Pictures with  filter lotId   *
     * @param lotId
     * @return successfulDeletes - count of deleted files by prefix
     */

    public int deleteLotPictures(Long lotId) {
        int successfulDeletes = 0;
        // test
        ListObjectsRequest listObjectsRequestTest = new ListObjectsRequest().withBucketName(s3Properties.getBucketName());
        ObjectListing objectListingTest = s3Client.listObjects(listObjectsRequestTest);

        List<DeleteObjectsRequest.KeyVersion> keys = getKeysLot(lotId);
        if (keys.size() > 0) {
            successfulDeletes = delObjRequest(keys);
        }

        // test
        objectListingTest = s3Client.listObjects(listObjectsRequestTest);

        return successfulDeletes;
    }

    /**
     * Structure of Image key: {lotId}/pictureName
     * @param lotId
     * @return keys all Pictures withPrefix(lotId + "/")
     */
    public List<DeleteObjectsRequest.KeyVersion> getKeysLot(Long lotId) {
        List<DeleteObjectsRequest.KeyVersion> keys = new ArrayList<>();
        ListObjectsRequest listObjectsRequest = new ListObjectsRequest().withBucketName(s3Properties.getBucketName())
                .withPrefix(lotId + "/");
        try {
            ObjectListing objectListing = s3Client.listObjects(listObjectsRequest);
            objectListing
                    .getObjectSummaries()
                    .forEach(summary -> keys.add(new DeleteObjectsRequest.KeyVersion(summary.getKey())));
        } catch (SdkClientException e) {
            log.error("Bucket exception, keys all Pictures", e);
        }
        return keys;
    }

    private int delObjRequest (List<DeleteObjectsRequest.KeyVersion> keys){
        // Delete the sample objects without specifying versions.
        DeleteObjectsRequest multiObjectDeleteRequest = new DeleteObjectsRequest(this.s3Properties.getBucketName()).withKeys(keys)
                .withQuiet(false);
        //  Verify that delete markers were successfully added to the objects.
        DeleteObjectsResult delObjRes = s3Client.deleteObjects(multiObjectDeleteRequest);
        return delObjRes.getDeletedObjects().size();
    }

}
