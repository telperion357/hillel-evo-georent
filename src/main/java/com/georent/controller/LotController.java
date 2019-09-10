package com.georent.controller;


import com.georent.service.LotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.ResponseEntity.status;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * Request controllers to the lot endpoints, that do not require authentication.
 */
@Controller
@RequestMapping("lot")
public class LotController {

    private final LotService lotService;

    @Autowired
    public LotController(final LotService lotService) {
        this.lotService = lotService;
    }

    /**
     * Processes the GET request to "/lot" URI.
     * @return Response, containing the list of coordinates of all lots, stored in the database.
     */
//    @GetMapping
    @RequestMapping(
            method = GET,
            headers = "Accept=application/json; charset=UTF-8",
            produces = "application/json; charset=UTF-8")
    @ResponseBody
    public ResponseEntity<?> getLots(){
        ResponseEntity<?> re = ResponseEntity.ok(lotService.getLotsDto());
        return re;
    }

    /**
     * Processes the GET request to "/lot/{id}" URI.
     * @param lotId the id of the lot, specified in the request path.
     * @return Response, containing the requested lot in the format of LotDTO.
     */
//   @GetMapping ("/{id}")
    @RequestMapping(
            value = "/{id}",
            method = GET,
            headers = "Accept=application/json; charset=UTF-8",
            produces = "application/json; charset=UTF-8")
    @ResponseBody
    public ResponseEntity<?> getLotId(@PathVariable(value = "id") Long lotId) {
        return status(OK).body(lotService.getLotDto(lotId));
    }


    /**
     * Processes the GET request to "/lot/page/{number}/{itemsPerPage}/{rel}" URI.
     * @param numberPage
     * @param itemsPerPage
     * @param rel
     * @return Response, containing the list of all lots one page in the format of List<LotPageDTO> with pageNumber, totalPages.
     */
//    @GetMapping ("/page/{number}/{itemsPerPage}/{rel}")
    @RequestMapping(
            value = "/page/{number}/{itemsPerPage}/{rel}",
            method = GET,
            headers = "Accept=application/json; charset=UTF-8",
            produces = "application/json; charset=UTF-8")
    @ResponseBody
    public ResponseEntity<?> getPage(@PathVariable(value = "number") int numberPage,
                                     @PathVariable(value = "itemsPerPage") int itemsPerPage,
                                     @PathVariable(value = "rel") String rel) {
        return status(OK).body(lotService.getPage(numberPage-1, itemsPerPage, rel, null));
    }
}
