package com.georent.service;

import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.georent.domain.Coordinates;
import com.georent.domain.Description;
import com.georent.domain.GeoRentUser;
import com.georent.domain.Lot;
import com.georent.dto.*;
import com.georent.exception.LotNotFoundException;
import com.georent.exception.RegistrationSuchUserExistsException;
import com.georent.message.GeoRentIHttpStatus;
import com.georent.message.Message;
import com.georent.repository.CoordinatesRepository;
import com.georent.repository.DescriptionRepository;
import com.georent.repository.GeoRentUserRepository;
import com.georent.repository.LotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.net.URL;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.ResponseEntity.status;

@Service
public class GeoRentUserService {

    private final transient GeoRentUserRepository userRepository;
    private final transient PasswordEncoder passwordEncoder;
    private final transient LotRepository lotRepository;
    private final transient CoordinatesRepository coordinatesRepository;
    private final transient DescriptionRepository descriptionRepository;
    private final transient AWSS3Service awss3Service;

    @Autowired
    public GeoRentUserService(final GeoRentUserRepository userRepository,
                              final PasswordEncoder passwordEncoder,
                              final LotRepository lotRepository,
                              final CoordinatesRepository coordinatesRepository,
                              final DescriptionRepository descriptionRepository,
                              final AWSS3Service awss3Service) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.lotRepository = lotRepository;
        this.coordinatesRepository = coordinatesRepository;
        this.descriptionRepository = descriptionRepository;
        this.awss3Service = awss3Service;
    }

    /**
     * @param id
     * @return
     */
    public Optional<GeoRentUser> getUserById(final Long id) {
        return userRepository.findById(id);
    }


    /**
     * @param email
     * @return GeoRentUser if user with this email is registered
     */

    public Optional<GeoRentUser> getUserByEmail(final String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * @param recoveryToken
     * @return GeoRentUser if user with this recoveryToken is registered
     */
    public Optional<GeoRentUser> getUserByRecoveryToken(final String recoveryToken) {
        return userRepository.findByRecoveryToken(recoveryToken);
    }

    /**
     * @param email
     * @return true if email is registered, false if email is not registered
     */
    public Boolean existsUserByEmail(final String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * @param user
     * @return GeoRentUser
     */

    @Transactional
    public GeoRentUser saveUser(final GeoRentUser user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    /**
     *
     * @param user
     * @return
     */
    @Transactional
    public GeoRentUser saveRecoveryTokenUser(final GeoRentUser user) {
         return userRepository.save(user);
    }

    /**
     * Updates user info (only firstName, lastName,phoneNumber) in the database.
     *
     * @param principal            Current user identifier.
     * @param geoRentUserUpdateDto Information to update.
     * @return The generic response, containing the proper message and incoming GeoRentUserUpdateDto object.
     */
    @Transactional
    public GenericResponseDTO<GeoRentUserUpdateDto> updateUser(Principal principal,
                                                               final GeoRentUserUpdateDto geoRentUserUpdateDto) {
        userRepository.save(mapFromUpdateUserDTO(principal, geoRentUserUpdateDto));
        GenericResponseDTO<GeoRentUserUpdateDto> responseDTO = new GenericResponseDTO<>();
        responseDTO.setMessage(Message.SUCCESS_UPDATE_USER.getDescription());
        responseDTO.setBody(geoRentUserUpdateDto);
        return responseDTO;
    }

    /**
     * update only Password User in the database
     * @param principal
     * @param forgotUpdatePasswordDTO
     * @return
     */
    @Transactional
    public GenericResponseDTO<GeoRentUserUpdateDto> updatePasswordUser(Principal principal,
                                                               final ForgotUpdatePasswordDTO forgotUpdatePasswordDTO) {
        GeoRentUser geoRentUser = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException(Message.INVALID_GET_USER_EMAIL.getDescription() + principal.getName()));
        geoRentUser.setPassword(forgotUpdatePasswordDTO.getPassword());
        this.saveUser(geoRentUser);
        GenericResponseDTO<GeoRentUserUpdateDto> responseDTO = new GenericResponseDTO<>();
        responseDTO.setMessage(Message.SUCCESS_UPDATE_USER.getDescription());
        return responseDTO;
    }

    /**
     * Reads user info from the database and maps it to GeoRentUserInfoDto object.
     *
     * @param principal Current user identifier.
     * @return The user info in the format of GeoRentUserInfoDto.
     */
    public GeoRentUserInfoDto getUserInfo(Principal principal) {
        GeoRentUser geoRentUser = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException(Message.INVALID_GET_USER_EMAIL.getDescription() + principal.getName()));
        return mapToUserInfoDTO(geoRentUser);
    }

    /**
     * @param principal
     * @return
     */
    public List<GeoRentUserInfoDto> getUserAll(Principal principal) {
        if (!userRepository.existsByEmail(principal.getName())) {
            throw new UsernameNotFoundException(Message.INVALID_GET_USER_EMAIL.getDescription() + principal.getName());
        }
        return userRepository.findAll()
                .stream()
                .map(this::mapToUserInfoDTO)
                .collect(Collectors.toList());
    }


    /**
     * Reads the list of user lots from the database and maps them to the LotDTO format.
     *
     * @param principal Current user identifier.
     * @return The list of user lots in the LotDTO format.
     */
    public List<LotPageDTO> getUserLots(Principal principal) {
        GeoRentUser geoRentUser = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException(Message.INVALID_GET_USER_EMAIL.getDescription() + principal.getName()));
        return lotRepository.findAllByGeoRentUser_Id(geoRentUser.getId())
                .stream()
                .map(this::mapToLotDTOAddPicture)
                .collect(Collectors.toList());
    }

    /**
     * Reads the lot with specified id from the database and maps it to the LotDTO format.
     *
     * @param principal Current user identifier.
     * @param id        - The id of the specified lot.
     * @return The requested lot in the LotDTO format.
     */
    public LotDTO getUserLotId(Principal principal, long id) {
        GeoRentUser geoRentUser = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException(Message.INVALID_GET_USER_EMAIL.getDescription() + principal.getName()));
        Lot lot = lotRepository.findByIdAndGeoRentUser_Id(id, geoRentUser.getId())
                .orElseThrow(() -> new LotNotFoundException(Message.INVALID_GET_LOT_ID.getDescription() + Long.toString(id)
                        + Message.INVALID_GET_LOT_ID_USER.getDescription(), geoRentUser.getId()));
        return mapToLotDTO(lot);
    }

    /**
     * Downloads the lot and temp URL picture from pictures repository (Amazon S3).
     * key = lotId/userId /picrureId
     *
     * @param principal Current user identifier.
     * @param id        The id of the specified lot.
     * @return The lot with specified id in the format of LotDTO.
     */
    public LotDTO getUserLotIdUploadPicture(Principal principal, long id) {
        GeoRentUser geoRentUser = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException(Message.INVALID_GET_USER_EMAIL.getDescription() + principal.getName()));
        Lot lot = lotRepository.findByIdAndGeoRentUser_Id(id, geoRentUser.getId())
                .orElseThrow(() -> new LotNotFoundException(Message.INVALID_GET_LOT_ID.getDescription() + Long.toString(id)
                        + Message.INVALID_GET_LOT_ID_USER.getDescription(), geoRentUser.getId()));
        LotDTO lotDTO = mapToLotDTO(lot);
        List<DeleteObjectsRequest.KeyVersion> keys = this.awss3Service.getKeysLot(lot.getId());
        for (DeleteObjectsRequest.KeyVersion keyFileName : keys) {
            URL url = this.awss3Service.generatePresignedURL(keyFileName.getKey());
            if (url != null) lotDTO.getDescription().getURLs().add(url);
        }
        return lotDTO;
    }


    /**
     * Saves the provided lot to the database.
     *
     * @param principal          Current user identifier.
     * @param registrationLotDto The lot to save in the registrationLotDto format.
     * @return The saved lot in the LotDTO format.
     */
    @Transactional
    public GenericResponseDTO<LotDTO> saveUserLotWithoutPicture(Principal principal, final RegistrationLotDto registrationLotDto) {
        GeoRentUser geoRentUser = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException(Message.INVALID_GET_USER_EMAIL.getDescription() + principal.getName()));
        Lot lot = lotRepository.save(mapRegistrationLotDtoToLot(registrationLotDto, geoRentUser));
        GenericResponseDTO<LotDTO> responseDTO = new GenericResponseDTO<>();
        responseDTO.setMessage(Message.SUCCESS_SAVE_LOT.getDescription());
        responseDTO.setBody(mapToLotDTO(lot));
        return responseDTO;
    }

    /**
     * keyFileName = {lotId}/{userId}/{(max value from PictureIds) +1}"
     * picrureIdNext (for the fileUrl -> keyFileName)  is saved to list pictureIds - >  in Description
     * picrureIdNext  not is saved to list pictureIds if AmazonServiceException or SdkClientException
     *
     * @param multipartFiles
     * @param principal
     * @param registrationLotDtoStr
     * @return
     */
    @Transactional
    public ResponseEntity<?> saveUserLotUploadPicture(MultipartFile[] multipartFiles, Principal principal, String registrationLotDtoStr) {
        GeoRentUser geoRentUser = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException(Message.INVALID_GET_USER_EMAIL.getDescription() + principal.getName()));
        ObjectMapper mapper = new ObjectMapper();
        RegistrationLotDto registrationLotDto = null;
        try {
            registrationLotDto = mapper.readValue(registrationLotDtoStr, RegistrationLotDto.class);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Message.INVALID_SAVE_LOT.getDescription() + " " + e.getMessage());
        }
        Lot lot = lotRepository.save(mapRegistrationLotDtoToLot(registrationLotDto, geoRentUser));
        for (MultipartFile multipartFile : multipartFiles) {
            if (this.awss3Service.multiPartFileValidation(multipartFile)) {
                Long picrureIdNext = 1L;
                if (lot.getDescription().getPictureIds().size() > 0)
                    picrureIdNext = Collections.max(lot.getDescription().getPictureIds()) + 1;
                String keyFileName = lot.getId() + "/" + picrureIdNext;
                String keyFileNameS3 = this.awss3Service.uploadFileToS3bucket(multipartFile, keyFileName);
                if (keyFileNameS3 != null && !keyFileNameS3.isEmpty()) {
                    lot.getDescription().getPictureIds().add(Long.valueOf(picrureIdNext));
                }
            }
        }
        lotRepository.save(lot);
        GenericResponseDTO<LotDTO> responseDTO = new GenericResponseDTO<>();
        responseDTO.setMessage(Message.SUCCESS_SAVE_LOT.getDescription());
        responseDTO.setBody(mapToLotDTO(lot));
        return status(OK).body(responseDTO);
    }

    /**
     *
     * @param base64files
     * @param principal
     * @param registrationLotDtoStr
     * @return
     */
    @Transactional
    public ResponseEntity<?> saveUserLotUploadBase64(String[] base64files, Principal principal, String registrationLotDtoStr) {
        GeoRentUser geoRentUser = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException(Message.INVALID_GET_USER_EMAIL.getDescription() + principal.getName()));
        ObjectMapper mapper = new ObjectMapper();
        RegistrationLotDto registrationLotDto = null;
        try {
            registrationLotDto = mapper.readValue(registrationLotDtoStr, RegistrationLotDto.class);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Message.INVALID_SAVE_LOT.getDescription() + " " + e.getMessage());
        }
        Lot lot = lotRepository.save(mapRegistrationLotDtoToLot(registrationLotDto, geoRentUser));
        if (base64files != null) {
            String base64Header = "";
            for (String base64File : base64files) {
                base64File = base64Header + base64File;
                String objectStr = this.awss3Service.base64Validation(base64File);
                if (objectStr.equals("data:image/jpeg;base64")) {
                    base64Header =  "data:image/jpeg;base64,";
                }
                else {
                    Long picrureIdNext = 1L;
                    if (lot.getDescription().getPictureIds().size() > 0)
                        picrureIdNext = Collections.max(lot.getDescription().getPictureIds()) + 1;
                    String keyFileName = lot.getId() + "/" + picrureIdNext;
                    String keyFileNameS3 = this.awss3Service.uploadFileBase64ToS3bucket(objectStr, keyFileName);
                    if (keyFileNameS3 != null && !keyFileNameS3.isEmpty()) {
                        lot.getDescription().getPictureIds().add(Long.valueOf(picrureIdNext));
                        base64Header = "";
                    }
                }
            }
        }
        lotRepository.save(lot);
        GenericResponseDTO<LotDTO> responseDTO = new GenericResponseDTO<>();
        responseDTO.setMessage(Message.SUCCESS_SAVE_LOT.getDescription());
        responseDTO.setBody(mapToLotDTO(lot));
        return status(OK).body(responseDTO);
    }

    /**
     * Deletes the specified user and all its lots from the database.
     *
     * @param principal Current user identifier.
     * @return Generic response, containing  the proper message.
     */
    @Transactional
    public GenericResponseDTO<LotDTO> deleteUser(String userName, Principal principal) {
        if (!userRepository.existsByEmail(principal.getName())) {
            throw new UsernameNotFoundException(Message.INVALID_GET_USER_EMAIL.getDescription() + principal.getName());
        }
        GeoRentUser geoRentUser = userRepository.findByEmail(userName)
                .orElseThrow(() -> new UsernameNotFoundException(Message.INVALID_GET_USER_EMAIL.getDescription() + principal.getName()));
        deleteAllLotUser(geoRentUser);
        userRepository.delete(geoRentUser);
        GenericResponseDTO<LotDTO> responseDTO = new GenericResponseDTO<>();
        responseDTO.setMessage(userName + Message.SUCCESS_DELETE_USER.getDescription());
        return responseDTO;
    }


    /**
     * Deletes the user lot with the specified id.
     *
     * @param principal Current user identifier.
     * @param id        - The id of the lot to delete.
     * @return Generic response, containing the proper message.
     */
    @Transactional
    public GenericResponseDTO<LotDTO> deleteteUserLotId(Principal principal, long id) {
        GeoRentUser geoRentUser = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException(Message.INVALID_GET_USER_EMAIL.getDescription() + principal.getName()));
        Lot lot = lotRepository.findByIdAndGeoRentUser_Id(id, geoRentUser.getId())
                .orElseThrow(() -> new LotNotFoundException(Message.INVALID_DELETE_LOT_ID.getDescription() + Long.toString(id) +
                        Message.INVALID_GET_LOT_ID_USER.getDescription(), geoRentUser.getId()));
        deleteOneLot(lot);
        GenericResponseDTO<LotDTO> responseDTO = new GenericResponseDTO<>();
        responseDTO.setMessage(Message.SUCCESS_DELETE_LOT.getDescription());
        return responseDTO;
    }

    /**
     * Deletes all lots of the current user from the database.
     *
     * @param principal Current user identifier.
     * @return Generic response, containing the proper message.
     */
    @Transactional
    public GenericResponseDTO<LotDTO> deleteteUserLotAll(Principal principal) {
        GeoRentUser geoRentUser = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException(Message.INVALID_GET_USER_EMAIL.getDescription() + principal.getName()));
        deleteAllLotUser(geoRentUser);
        GenericResponseDTO<LotDTO> responseDTO = new GenericResponseDTO<>();
        responseDTO.setMessage(Message.SUCCESS_DELETE_LOTS.getDescription());
        return responseDTO;
    }

    private LotDTO mapToLotDTO(Lot lot) {
        LotDTO dto = new LotDTO();
        Long id = lot.getId();
        dto.setId(id);
        dto.setPrice(lot.getPrice());
        if (lot.getCoordinates() != null) {
            Coordinates coordinates = lot.getCoordinates();
            CoordinatesDTO coordinatesDTO = new CoordinatesDTO();
            coordinatesDTO.setLatitude(coordinates.getLatitude());
            coordinatesDTO.setLongitude(coordinates.getLongitude());
            coordinatesDTO.setAddress(coordinates.getAddress());
            dto.setCoordinates(coordinatesDTO);
        }
        if (lot.getDescription() != null) {
            Description description = lot.getDescription();
            DescriptionDTO descriptionDTO = new DescriptionDTO();
            descriptionDTO.setLotName(description.getLotName());
            descriptionDTO.setLotDescription(description.getLotDescription());
            descriptionDTO.setPictureIds(new ArrayList<Long>(description.getPictureIds()));
            dto.setDescription(descriptionDTO);
        }
        return dto;
    }

    private LotPageDTO mapToLotDTOAddPicture(Lot lot) {

        LotPageDTO dto = new LotPageDTO();
        dto.setId(lot.getId());
        dto.setPrice(lot.getPrice());
        dto.setAddress(lot.getCoordinates().getAddress());
        dto.setLotName(lot.getDescription().getLotName());
        List<DeleteObjectsRequest.KeyVersion> keys = this.awss3Service.getKeysLot(lot.getId());
        URL imageUrl = this.awss3Service.generatePresignedURL(lot.getId() + "/1");
        if (imageUrl!= null)  dto.setImageUrl(imageUrl);
        return dto;
    }

    private GeoRentUser mapFromUpdateUserDTO(Principal principal, final GeoRentUserUpdateDto geoRentUserUpdateDto) {
        GeoRentUser geoRentUser = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException(Message.INVALID_GET_USER_EMAIL.getDescription() + principal.getName()));
        geoRentUser.setLastName(geoRentUserUpdateDto.getLastName());
        geoRentUser.setFirstName(geoRentUserUpdateDto.getFirstName());
        geoRentUser.setPhoneNumber(geoRentUserUpdateDto.getPhoneNumber());
        return geoRentUser;
    }

    private GeoRentUserInfoDto mapToUserInfoDTO(GeoRentUser geoRentUser) {
        GeoRentUserInfoDto geoRentUserInfoDto = new GeoRentUserInfoDto();
        geoRentUserInfoDto.setId(geoRentUser.getId());
        geoRentUserInfoDto.setEmail(geoRentUser.getEmail());
        geoRentUserInfoDto.setFirstName(geoRentUser.getFirstName());
        geoRentUserInfoDto.setLastName(geoRentUser.getLastName());
        geoRentUserInfoDto.setPhoneNumber(geoRentUser.getPhoneNumber());
        geoRentUserInfoDto.setRole(geoRentUser.getRole());
        return geoRentUserInfoDto;
    }

    private Lot mapRegistrationLotDtoToLot(RegistrationLotDto registrationLotDto, GeoRentUser geoRentUser) {
        Lot lot = new Lot();
        lot.setPrice(registrationLotDto.getPrice());
        lot.setGeoRentUser(geoRentUser);
        Coordinates coordinates = new Coordinates();
        coordinates.setLatitude(registrationLotDto.getLatitude());
        coordinates.setLongitude(registrationLotDto.getLongitude());
        coordinates.setAddress(registrationLotDto.getAddress());
        lot.setCoordinates(coordinates);
        Description description = new Description();
        description.setLotName(registrationLotDto.getLotName());
        description.setLotDescription(registrationLotDto.getLotDescription());
        lot.setDescription(description);
        return lot;
    }

    private void deleteOneLot(Lot lot) {
        this.awss3Service.deleteLotPictures(lot.getId());
        this.lotRepository.deleteById(lot.getId());

    }

    private void deleteAllLotUser(GeoRentUser geoRentUser) {
//        this.awss3Service.deletePicturesFromAllLotsUser(geoRentUser.getId());
        lotRepository.findAllByGeoRentUser_Id(geoRentUser.getId())
                .stream()
                .collect(Collectors.toList())
                .stream()
                .forEach(lot -> this.awss3Service.deleteLotPictures(lot.getId()));
        lotRepository.deleteAllByGeoRentUser_Id(geoRentUser.getId());
    }

}
