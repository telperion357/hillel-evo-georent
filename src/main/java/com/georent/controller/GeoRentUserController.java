package com.georent.controller;

import com.georent.dto.ForgotUpdatePasswordDTO;
import com.georent.dto.GeoRentUserUpdateDto;
import com.georent.dto.RegistrationLotDto;
import com.georent.service.GeoRentUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import java.security.Principal;

import static org.springframework.web.bind.annotation.RequestMethod.*;

/**
 * Request controllers to the user and lot endpoints, that do require authentication.
 */
@Controller
@RequestMapping("user")
public class GeoRentUserController {

    private final GeoRentUserService userService;

    @Autowired
    public GeoRentUserController(final GeoRentUserService userService) {
        this.userService = userService;
    }

    /**
     * Processes GET requests to endpoint "/user".
     * Returns the user information from the database.
     * @param principal current user identifier.
     * @return Response, containing the user information in the format of GeoRentUserInfoDTO.
     */

//    @GetMapping
    @RequestMapping(
            method = GET,
            headers = "Accept=application/json; charset=UTF-8",
            produces = "application/json; charset=UTF-8")
    @ResponseBody
    public ResponseEntity<?> getUserInfo(Principal principal){
        return ResponseEntity.ok(userService.getUserInfo(principal));
    }

    /**
     * Processes PATCH requests to endpoint "/user".
     * Updates user information in the database.
     * @param geoRentUserUpdateDto - User information to update in the format of GeoRentUserUpdateDto.
     * @param principal current user identifier.
     * @return Response, containing the updated user information in the format of GeoRentUserUpdateDto.
     */
//    @PatchMapping
    @RequestMapping(
            method = PATCH,
            headers = "Accept=application/json; charset=UTF-8",
            produces = "application/json; charset=UTF-8")
    @ResponseBody
    public ResponseEntity<?> updateUser(@Valid @RequestBody GeoRentUserUpdateDto geoRentUserUpdateDto, Principal principal) {
        return ResponseEntity.ok(userService.updateUser(principal, geoRentUserUpdateDto));
    }

    /**
     * Processes PATCH requests to endpoint "/user/password".
     * @param forgotUpdatePasswordDTO
     * @param principal
     * @return
     */
//    @PatchMapping ("/password")
    @RequestMapping(
            value = "/password",
            method = PATCH,
            headers = "Accept=application/json; charset=UTF-8",
            produces = "application/json; charset=UTF-8")
    @ResponseBody
    public ResponseEntity<?> updatePasswordUser(@Valid @RequestBody ForgotUpdatePasswordDTO forgotUpdatePasswordDTO, Principal principal) {
        return ResponseEntity.ok(userService.updatePasswordUser(principal, forgotUpdatePasswordDTO));
    }

    /**
     * Processes DELETE requests to endpoint "/user".
     * Permanently deletes the user, user lots, and all relative info from the database.
     * @param principal current user identifier.
     * @return Response, containing the user information in the format of GeoRentUserInfoDTO.
     */
    @DeleteMapping ("/{userName}")
    @RolesAllowed("ROLE_ADMIN")
    public ResponseEntity<?> deletetUser(@PathVariable(value = "userName") String userName, Principal principal){
        return ResponseEntity.ok(userService.deleteUser(userName, principal));
    }

    /**
     * Processes GET requests to endpoint "/lots".
     * Reads the list of user lots from the database.
     * @param principal current user identifier.
     * @return Response, containing the list of user lots in the format of LotDTO.
     */

//    @GetMapping("/lots")
//    @Secured({UserRole.Code.ADMIN, UserRole.Code.USER })
    @RequestMapping(
            value = "/lots",
            method = GET,
            headers = "Accept=application/json; charset=UTF-8",
            produces = "application/json; charset=UTF-8")
    @ResponseBody
    public ResponseEntity<?> getUserLots(Principal principal){
        return ResponseEntity.ok(userService.getUserLots(principal));
    }

    /**
     * Processes GET requests to endpoint "/lots/{id}".
     * Reads the user lot with specified id from the database.
     * @param principal Current user identifier.
     * @param lotId The id of the specified lot.
     * @return Response, containing the requested lot in the format of LotDTO.
     */
//    @GetMapping("/lot/{id}")
    @RequestMapping(
            value = "/lot/{id}",
            method = GET,
            headers = "Accept=application/json; charset=UTF-8",
            produces = "application/json; charset=UTF-8")
    @ResponseBody
    public ResponseEntity<?> getUserLotId(@PathVariable(value = "id") Long lotId, Principal principal){
        return ResponseEntity.ok(userService.getUserLotId(principal, lotId));
    }

    /**
     * Processes GET requests to endpoint "/lot/upload-picture/{id}".
     * Downloads the requested lot picture from the pic repository to temp file.
     * @param lotId The id of the requested lot.
     * @param principal Current user identifier.
     * @return Response, containing the lot with specified id in the format of LotDTO.
     */
//    @GetMapping("/lot/upload-picture/{id}")
    @RequestMapping(
            value = "/lot/upload-picture/{id}",
            method = GET,
            headers = "Accept=application/json; charset=UTF-8",
            produces = "application/json; charset=UTF-8")
    @ResponseBody
    public ResponseEntity<?> getUploadPicture(@PathVariable(value = "id") Long lotId, Principal principal){
        return ResponseEntity.ok(userService.getUserLotIdUploadPicture(principal, lotId));
    }

    /**
     * Processes POST requests to the endpoint "/lot"
     * Saves the provided lot to the database.
     * @param registrationLotDto The lot to save to the database in RegistrationLotDto format.
     * @param principal Current user identifier.
     * @return Response, containing the saved lot in the LotDTO format.
     */
//    @PostMapping("/lot")
    @RequestMapping(
            value = "/lot",
            method = POST,
            headers = "Accept=application/json; charset=UTF-8",
            produces = "application/json; charset=UTF-8")
    @ResponseBody
    public ResponseEntity<?> setUserLot(@Valid @RequestBody final RegistrationLotDto registrationLotDto, Principal principal){
        return ResponseEntity.ok(userService.saveUserLotWithoutPicture(principal, registrationLotDto));
    }

    /**
     * Processes POST requests to the endpoint "/lot/upload-picture".
     * Uploads the lot picture to the pictures repository.
     * @param multipartFiles The files to upload format  multipart.
     * @param base64Files    The files to upload format  bas64.
     * @param registrationLotDtoStr JSON new lot
     * @param principal Current user identifier.ForgotUpdatePasswordDTO
     * @return Response, containing the saved lot in the LotDTO format.
     */
//    @PostMapping("/lot/upload-picture")
    @RequestMapping(
            value = "/lot/upload-picture",
            method = POST,
            headers = "Accept=application/json; charset=UTF-8",
            produces = "application/json; charset=UTF-8")
    @ResponseBody
    public ResponseEntity<?> setUploadPicture(@Valid
                                              @RequestParam(name = "files") MultipartFile[] multipartFiles,
                                              @RequestParam(name = "filesBase64") String[] base64Files,
                                              @RequestParam(name = "lot") String registrationLotDtoStr,
                                              Principal principal) {
        return (multipartFiles.length > 0) ?
                userService.saveUserLotUploadPicture(multipartFiles, principal, registrationLotDtoStr) :
                userService.saveUserLotUploadBase64(base64Files, principal, registrationLotDtoStr);
    }

    /**
     * Processes DELETE requests to endpoint "/lots/{id}".
     * Deletes the specified lot from the database.
     * @param lotId The id of the specified lot.
     * @param principal Current user identifier.
     * @return Response, containing the proper message.
     */
    @DeleteMapping ("/lot/{id}")
    public ResponseEntity<?> deletetLotId(@PathVariable(value = "id") Long lotId, Principal principal){
        return ResponseEntity.ok(userService.deleteteUserLotId(principal, lotId));
    }

    /**
     * Processes DELETE requests to endpoint "/lots".
     * Deletes all the user lots from the database.
     * @param principal Current user identifier.
     * @return Response, containing the proper message.
     */
    @DeleteMapping ("/lots")
    public ResponseEntity<?> deletetLots(Principal principal){
        return ResponseEntity.ok(userService.deleteteUserLotAll(principal));
    }

    /**
     * Only with role === "ROLE_ADMIN"
     * @param principal
     * @return
     */
//    @GetMapping("/userAll")
//    @Secured(UserRole.Code.ADMIN)
    @RequestMapping(
            value = "/userAll",
            method = GET,
            headers = "Accept=application/json; charset=UTF-8",
            produces = "application/json; charset=UTF-8")
    @ResponseBody
    @RolesAllowed("ROLE_ADMIN")
    public ResponseEntity<?> getUserAll(Principal principal){
        return ResponseEntity.ok(userService.getUserAll(principal));
    }

}
