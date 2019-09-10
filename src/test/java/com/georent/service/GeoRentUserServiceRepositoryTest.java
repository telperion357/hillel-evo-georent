package com.georent.service;

import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.georent.domain.GeoRentUser;
import com.georent.domain.Lot;
import com.georent.dto.*;
import com.georent.message.Message;
import com.georent.repository.CoordinatesRepository;
import com.georent.repository.DescriptionRepository;
import com.georent.repository.GeoRentUserRepository;
import com.georent.repository.LotRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.georent.service.ServiceTestUtils.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class GeoRentUserServiceRepositoryTest {

    private static final Lot TEST_LOT = ServiceTestUtils.createTestLot();
    private static final LotDTO TEST_LOT_DTO = ServiceTestUtils.createTestLotDTO();
    private static final GeoRentUser TEST_USER = ServiceTestUtils.createTestUser();

    GeoRentUserService userService;

    private GeoRentUserRepository mockUserRepository = mock(GeoRentUserRepository.class);
    private LotRepository mockLotRepository = mock(LotRepository.class);
    private CoordinatesRepository mockCordinatesRepository = mock(CoordinatesRepository.class);
    private DescriptionRepository mockDescriptionRepository = mock(DescriptionRepository.class);
    private AWSS3Service mockDAWSS3Service = mock(AWSS3Service.class);

    private PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
    private String email = "mkyong@gmail23.com.aa";
    private String passPrincipal = "$2a$10$2O/w2twGJFNoLcnlOyJp0..IeZ2Wn3JXNts2wC62FT/TgTlQ9oqO6";
    GeoRentUser user = TEST_USER;
    GeoRentUser userPrincipal = TEST_USER;
    Principal principal = new Principal() {
        @Override
        public String getName() {
            return "";
        }
    };

    @Before
    public void setup() {
        userService = new GeoRentUserService(
                mockUserRepository,
                passwordEncoder,
                mockLotRepository,
                mockCordinatesRepository,
                mockDescriptionRepository,
                mockDAWSS3Service
        );
        userPrincipal.setPassword(passPrincipal);

        when(mockUserRepository.findByEmail(any(String.class))).thenReturn(Optional.of(userPrincipal));
    }

    @Test
    public void WhenGetUserByEmail_Return_GeoRentUser() {
        // given
        when(mockUserRepository.findByEmail(any(String.class))).thenReturn(Optional.of(TEST_USER));
        // when
        GeoRentUser geoRentUserOut = userService.getUserByEmail(email).get();
        verify(mockUserRepository, times(1)).findByEmail(any(String.class));
        // then
        Assert.assertEquals(TEST_USER, geoRentUserOut);
    }

    @Test
    public void WhenExistsUserByEmail_Ok_Return_True() {
        when(mockUserRepository.existsByEmail(any(String.class))).thenReturn(true);
        Assert.assertTrue(userService.existsUserByEmail(email));
        verify(mockUserRepository, times(1)).existsByEmail(any(String.class));
    }

    @Test
    public void WhenExistsUserByEmail_Err_Return_Fasle() {
        when(mockUserRepository.existsByEmail(any(String.class))).thenReturn(false);
        Assert.assertFalse(userService.existsUserByEmail(email));
        verify(mockUserRepository, times(1)).existsByEmail(any(String.class));
    }

    @Test
    public void WhenSaveNewUser_Save_User_With_EncodePassword() {
        when(passwordEncoder.encode(any(String.class))).thenReturn(passPrincipal);
        when(mockUserRepository.save(any(GeoRentUser.class))).thenReturn(user);
        GeoRentUser userOut = userService.saveUser(user);
        verify(mockUserRepository, times(1)).save(any(GeoRentUser.class));
        Assert.assertEquals(userOut, user);
    }

    @Test
    public void WhenGetUserInfo_Return_GeoRentUserInfoDtoWithPassPrincipal() {
        // given
        final GeoRentUserInfoDto expectedUserInfoDto = ServiceTestUtils.createTestUserInfoDTO();
        // when
        GeoRentUserInfoDto actualUserInfoDTO = userService.getUserInfo(principal);
        // then
        verify(mockUserRepository, times(1)).findByEmail(any(String.class));
        Assert.assertEquals(expectedUserInfoDto, actualUserInfoDTO);
    }

    @Test
    public void WhenUpdateUser_Return_responseDTO_WithGeoRentUserUpdateDto() {
        // given
        final GeoRentUserUpdateDto testUserUpdateDto = ServiceTestUtils.createUserUpdateDto();
        when(mockUserRepository.save(any(GeoRentUser.class))).thenReturn(userPrincipal);
        // when
        GenericResponseDTO<GeoRentUserUpdateDto> responseDTO = userService.updateUser(principal, testUserUpdateDto);
        verify(mockUserRepository, times(1)).findByEmail(any(String.class));
        verify(mockUserRepository, times(1)).save(any(GeoRentUser.class));
        // then
        Assert.assertEquals(Message.SUCCESS_UPDATE_USER.getDescription(), responseDTO.getMessage());
        Assert.assertEquals(testUserUpdateDto, responseDTO.getBody());
    }

    @Test
    public void WhengetUserLots_Return_List_LotDto() {
        // given
        List<Lot> lotsIn = Arrays.asList(TEST_LOT);
         // when
        when(mockLotRepository.findAllByGeoRentUser_Id(any(Long.class))).thenReturn(lotsIn);
        // then
        userService.getUserLots(principal);
        verify(mockUserRepository, times(1)).findByEmail(any(String.class));
        verify(mockLotRepository, times(1)).findAllByGeoRentUser_Id(any(Long.class));
    }

    @Test
    public void WhenGetUserLotId_Return_LotDto() {
        // given
        // when
        when(mockLotRepository.findByIdAndGeoRentUser_Id(any(Long.class), any(Long.class))).thenReturn(Optional.of(TEST_LOT));
        // then
        userService.getUserLotId(principal, TEST_LOT.getId());
        verify(mockUserRepository, times(1)).findByEmail(any(String.class));
        verify(mockLotRepository, times(1)).findByIdAndGeoRentUser_Id(any(Long.class), any(Long.class));
    }

    @Test
    public void WhenGetUserLotIdUploadPicture_Return_LotDto() throws MalformedURLException {
        // given
        Long picrureId = 1L;
        String keyFileName = "/geo-rent-bucket.s3.eu-west-1.amazonaws.com/" + TEST_LOT.getId() + "/" + TEST_LOT.getGeoRentUser().getId() + "/" + picrureId;
        URL url = getUrl(keyFileName);
        List<DeleteObjectsRequest.KeyVersion> keys = new ArrayList<DeleteObjectsRequest.KeyVersion>();
        keys.add(new DeleteObjectsRequest.KeyVersion(keyFileName));
        // when
        when(mockLotRepository.findByIdAndGeoRentUser_Id(any(Long.class), any(Long.class))).thenReturn(Optional.of(TEST_LOT));
        when(mockDAWSS3Service.getKeysLot(TEST_LOT.getId())).thenReturn(keys);
        when(mockDAWSS3Service.generatePresignedURL(any(String.class))).thenReturn(url);
        // then
        LotDTO lotDTOOut = userService.getUserLotIdUploadPicture(principal, TEST_LOT.getId());
        verify(mockUserRepository, times(1)).findByEmail(any(String.class));
        verify(mockLotRepository, times(1)).findByIdAndGeoRentUser_Id(any(Long.class), any(Long.class));
        TEST_LOT_DTO.getDescription().getURLs().add(url);
        Assert.assertEquals(TEST_LOT_DTO, lotDTOOut);
    }

    @Test
    public void WhenSaveUserLotWithoutPicture_Return_LotDto() {
        // given
        RegistrationLotDto registrationLotDto = ServiceTestUtils.registrationLotDto (TEST_LOT);
        // when
        when(mockLotRepository.save(any(Lot.class))).thenReturn(TEST_LOT);
        // then
        GenericResponseDTO responseDTO = userService.saveUserLotWithoutPicture(principal, registrationLotDto);
        verify(mockUserRepository, times(1)).findByEmail(any(String.class));
        verify(mockLotRepository, times(1)).save(any(Lot.class));
        Assert.assertEquals(Message.SUCCESS_SAVE_LOT.getDescription(), responseDTO.getMessage());
    }

    @Test
    public void WhenSaveUserLotUploadPicture_Return_LotDto() {
        // given
        Long picrureIdNext = 1L;
        String keyFileName = TEST_LOT.getId()  + "/" + TEST_LOT.getGeoRentUser().getId()+ "/" + picrureIdNext;
        // when
        when(mockDAWSS3Service.uploadFileToS3bucket(any(MultipartFile.class), any(String.class))).thenReturn(keyFileName);
        when(mockLotRepository.save(any(Lot.class))).thenReturn(TEST_LOT);
        when(mockDAWSS3Service.multiPartFileValidation(any(MultipartFile.class))).thenReturn(true);
        // then
        org.springframework.http.ResponseEntity <?> bodyBuilder =  userService.saveUserLotUploadPicture(getMultipartFiles (), principal, getRegistrationLotDtoStr());
        verify(mockUserRepository, times(1)).findByEmail(any(String.class));
        verify(mockLotRepository, times(2)).save(any(Lot.class));
        Assert.assertEquals(HttpStatus.OK, bodyBuilder.getStatusCode());
        GenericResponseDTO lotDTO = (GenericResponseDTO) bodyBuilder.getBody();
        Assert.assertEquals(Message.SUCCESS_SAVE_LOT.getDescription(), lotDTO.getMessage());
    }

    @Test
    public void WhenDeleteUser_DeleteUserLots_DeleteUser_Return_MsgMessage_SUCCESS_DELETE_USER() {
        String userName = "user2@gmail.com.ua";
        GenericResponseDTO responseDTO =  userService.deleteUser(userName, principal);
        verify(mockUserRepository, times(2)).findByEmail(any(String.class));
        Assert.assertEquals(userName + Message.SUCCESS_DELETE_USER.getDescription(), responseDTO.getMessage());
        Assert.assertNull(responseDTO.getBody());
    }

    @Test
    public void WhenDeleteteUserLotId_findByIdAndGeoRentUser_Id_Ok_Return_MsgMessage_SUCCESS_DELETE_LOT() {
        // given
        when(mockLotRepository.findByIdAndGeoRentUser_Id(any(Long.class), any(Long.class)))
                .thenReturn(Optional.of(TEST_LOT));
        // when
        GenericResponseDTO responseDTO =  userService.deleteteUserLotId (principal, TEST_LOT.getId());
        verify(mockUserRepository, times(1)).findByEmail(any(String.class));
        verify(mockLotRepository, times(1))
                .findByIdAndGeoRentUser_Id(any(Long.class), any(Long.class));
        // then
        Assert.assertEquals(Message.SUCCESS_DELETE_LOT.getDescription(), responseDTO.getMessage());
        Assert.assertNull(responseDTO.getBody());
    }

    @Test
    public void WhenDeleteteUserLotAll_Return_MsgMessage_SUCCESS_DELETE_LOT() {
        GenericResponseDTO responseDTO =  userService.deleteteUserLotAll (principal);
        verify(mockUserRepository, times(1)).findByEmail(any(String.class));
        Assert.assertEquals(Message.SUCCESS_DELETE_LOTS.getDescription(), responseDTO.getMessage());
        Assert.assertNull(responseDTO.getBody());
    }
}

