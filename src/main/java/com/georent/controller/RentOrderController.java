package com.georent.controller;

import com.georent.dto.GenericResponseDTO;
import com.georent.dto.RentOrderDTO;
import com.georent.dto.RentOrderRequestDTO;
import com.georent.service.RentOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("rent")
public class RentOrderController {

//    POST	    /rentee/order	            Initial posting of the order (registration)
//    GET	    /rentee/order	            Get all orders, submitted by this rentee
//    GET	    /rentee/order/{orderId}	    Get this order of this rentee
//    GET	    /rentee/order/lot/{lotId}    Get all orders for this lot by this rentee.
//    PATCH	    /rentee/order/{orderId}	    Edit dates in this order with PENDING
//    DELETE	/rentee/order	            Delete all orders of this rentee
//    DELETE	/rentee/order/{orderId}	    Delete this order
//    DELETE	/rentee/order/lot/{lotId}	Delete all orders for this lot

    private final RentOrderService rentOrderService;

    @Autowired
    public RentOrderController(RentOrderService rentOrderService) {
        this.rentOrderService = rentOrderService;
    }

    /**
     * Processes POST requests to the endpoint "rent/rentee/order"
     * Saves the provided order to the database.
     * Validates that the start time is before end time.
     * @param orderRequestDTO The order to save to the database in RentOrderRequestDTO format.
     * @param principal Current user identifier.
     * @return Response, containing the saved order in the RentOrderDTO format.
     */
    @PostMapping("/rentee/order")
    public ResponseEntity<GenericResponseDTO<RentOrderDTO>> saveRentOrder(
            @Valid @RequestBody final RentOrderRequestDTO orderRequestDTO,
            Principal principal){
        return ResponseEntity.ok(rentOrderService.saveRentOrder(principal, orderRequestDTO));
    }

    /**
     * Processes GET requests to the endpoint "rent/rentee/order"
     * Retrieves the list of user (rentee) orders from the database.
     * @param principal Current user (rentee) identifier
     * @return response, containing the list
     * of user (rentee) orders in the RentOrderDTO format.
     */
    @GetMapping("/rentee/order")
    public ResponseEntity<List<RentOrderDTO>> getRenteeOrders(Principal principal){
        return ResponseEntity.ok(rentOrderService.getRenteeOrders(principal));
    }

    /**
     * Processes GET requests to the endpoint "rent/rentee/order/{id}"
     * Reads the order with provided id from the database.
     * Checks if this user has the access to this order,
     * if not, throws OrderNotFoundException.
     * @param orderId the id of the order.
     * @param principal current user (rentee) identifier.
     * @return response, containing the order in the RentOrderDTO format.
     */
    @GetMapping("/rentee/order/{orderId}")
    public ResponseEntity<RentOrderDTO> getRenteeOrderById(
            @PathVariable(value = "orderId") Long orderId,
            Principal principal) {
        return ResponseEntity.ok(rentOrderService.getRenteeOrderById(orderId, principal));
    }

    /**
     * Processes GET requests to the endpoint "rent/rentee/order/lot/{id}"
     * Reads the list of user (rentee) orders to the specified lot from the database.
     * @param lotId lot identifier.
     * @param principal Current user (rentee) identifier.
     * @return response, containing the list of user (rentee) orders in the RentOrderDTO format.
     */
    @GetMapping("/rentee/order/lot/{lotId}")
    public ResponseEntity<List<RentOrderDTO>> getRenteeOrdersByLotId(
            @PathVariable(value = "lotId") Long lotId,
            Principal principal) {
        return ResponseEntity.ok(rentOrderService.getRenteeOrdersByLotId(lotId, principal));
    }

    /**
     * Processes PATCH requests to the endpoint "rent/rentee/order/{id}"
     * Updates start and end dates in the order with provided id.
     * Checks if this user has the access to this order.
     * If not, throws OrderNotFoundException.
     * Checks if this order status is pending.
     * If not, throws RentOrderUpdateException.
     * @param orderId the id of the order to update.
     * @param updateOrderDTO - the new values.
     * @param principal - user (rentee) identifier.
     * @return response, containing the updated order.
     */
    @PatchMapping("/rentee/order/{orderId}")
    public ResponseEntity<GenericResponseDTO<RentOrderDTO>> patchRenteeOrderById(
            @PathVariable(value = "orderId") Long orderId,
            @Valid @RequestBody final RentOrderDTO updateOrderDTO,
            Principal principal) {
        return ResponseEntity.ok(rentOrderService
                .patchRenteeOrderById(orderId, updateOrderDTO, principal));
    }

    /**
     * Processes DELETE requests to the endpoint "rent/rentee/order"
     * Deletes all the orders of this user.
     * @param principal current user (rentee) identifier.
     * @return the response with delete successful message.
     */
    @DeleteMapping("/rentee/order")
    public ResponseEntity<GenericResponseDTO<String>> deleteRenteeOrders(Principal principal){
        return ResponseEntity.ok(rentOrderService.deleteRenteeOrders(principal));
    }

    /**
     * Processes DELETE requests to the endpoint "rent/rentee/order/{id}"
     * Deletes the order with provided id from the database.
     * Checks if this user has the access to this order.
     * If not, throws OrderNotFoundException.
     * @param orderId the id of the order to delete.
     * @param principal current user (rentee) identifier.
     * @return the response with delete successful message.
     */
    @DeleteMapping("/rentee/order/{orderId}")
    public ResponseEntity<GenericResponseDTO<String>> deleteRenteeOrderById(
            @PathVariable(value = "orderId") Long orderId,
            Principal principal) {
        return ResponseEntity.ok(rentOrderService.deleteRenteeOrderById(orderId, principal));
    }

    /**
     * Processes DELETE requests to the endpoint "rent/rentee/order/lot/{id}"
     * Deletes all the orders of this user to the lot with provided id.
     * @param lotId the id of the lot, from which to delete the orders
     * @param principal current user (rentee) identifier.
     * @return the response with delete successful message.
     */
    @DeleteMapping("/rentee/order/lot/{lotId}")
    public ResponseEntity<GenericResponseDTO<String>> deleteRenteeOrdersByLotId(
            @PathVariable(value = "lotId") Long lotId,
            Principal principal) {
        return ResponseEntity.ok(rentOrderService.deleteRenteeOrdersToLot(lotId, principal));
    }

//    GET	    /owner/order	            Get all orders for this owner lots
//    GET	    /owner/order/{orderId}	Get this order by id
//    GET	    /owner/lot/{lotId}/order	Get all orders for this lot
//    PATCH	    /owner/order/{orderId}	Change the status of this order
//    DELETE	/owner/order	            Delete all orders for the lots of this owner
//    DELETE	/owner/order/{orderId}	Delete this order
//    DELETE	/owner/lot/{lotId}/order	Delete all orders for this lot

    /**
     * Processes GET requests to the endpoint "rent/owner/order"
     * Retrieves the list of user (lot owner) orders from the database.
     * @param principal Current user (lot owner) identifier
     * @return response, containing the list
     * of user (lot owner) orders in the RentOrderDTO format.
     */
    @GetMapping("/owner/order")
    public ResponseEntity<List<RentOrderDTO>> getOwnerOrdersByOwnerId(Principal principal){
        return ResponseEntity.ok(rentOrderService.getOwnerOrders(principal));
    }

    /**
     * Processes GET requests to the endpoint "rent/owner/order/{id}"
     * Reads the order with provided id from the database.
     * Checks if this user has the access to this order,
     * if not, throws OrderNotFoundException.
     * @param orderId the id of the order.
     * @param principal current user (lot owner) identifier.
     * @return response, containing the order in the RentOrderDTO format.
     */
    @GetMapping("/owner/order/{orderId}")
    public ResponseEntity<RentOrderDTO> getOwnerOrderByOrderId(
            @PathVariable(value = "orderId") Long orderId,
            Principal principal) {
        return ResponseEntity.ok(rentOrderService.getOwnerOrderByOrderId(orderId, principal));
    }

    /**
     * Processes GET requests to the endpoint "rent/owner/order/lot/{id}"
     * Reads the list of user (lot owner) orders to the specified lot from the database.
     * @param lotId lot identifier.
     * @param principal Current user (lot owner) identifier.
     * @return response, containing the list of user (lot owner) orders in the RentOrderDTO format.
     */
    @GetMapping("/owner/order/lot/{lotId}")
    public ResponseEntity<List<RentOrderDTO>> getOwnerOrdersByLotId(
            @PathVariable(value = "lotId") Long lotId,
            Principal principal) {
        return ResponseEntity.ok(rentOrderService.getOwnerOrdersByLotId(lotId, principal));
    }

    /**
     * Processes PATCH requests to the endpoint "rent/owner/order/{id}"
     * Updates status in the order with provided id.
     * Checks if this user has the access to this order.
     * If not, throws OrderNotFoundException.
     * Checks if status change is valid.
     * If not, throws RentOrderUpdateException.
     * @param orderId the id of the order to update.
     * @param updateOrderDTO - the new values.
     * @param principal - user (lot owner) identifier.
     * @return response, containing the updated order.
     */
    @PatchMapping("/owner/order/{orderId}")
    public ResponseEntity<GenericResponseDTO<RentOrderDTO>> patchOwnerOrderById(
            @PathVariable(value = "orderId") Long orderId,
            @RequestBody final RentOrderDTO updateOrderDTO,
            Principal principal) {
        return ResponseEntity.ok(rentOrderService
                .patchOwnerOrderById(orderId, updateOrderDTO, principal));
    }

    /**
     * Processes DELETE requests to the endpoint "rent/owner/order"
     * Deletes all the orders to the lots of this user.
     * @param principal current user (lot owner) identifier.
     * @return the response with delete successful message.
     */
    @DeleteMapping("/owner/order")
    public ResponseEntity<GenericResponseDTO<String>> deleteAllOrdersToOwnerLots(Principal principal){
        return ResponseEntity.ok(rentOrderService.deleteAllOrdersToOwnerLots(principal));
    }

    /**
     * Processes DELETE requests to the endpoint "rent/owner/order/{id}"
     * Deletes the order with provided id from the database.
     * Checks if this user has the access to this order.
     * If not, throws OrderNotFoundException.
     * @param orderId the id of the order to delete.
     * @param principal current user (lot owner) identifier.
     * @return the response with delete successful message.
     */
    @DeleteMapping("/owner/order/{orderId}")
    public ResponseEntity<GenericResponseDTO<String>> deleteOwnerOrderById(
            @PathVariable(value = "orderId") Long orderId,
            Principal principal) {
        return ResponseEntity.ok(rentOrderService.deleteOwnerOrderById(orderId, principal));
    }

    /**
     * Processes DELETE requests to the endpoint "rent/owner/order/lot/{id}"
     * Deletes all the orders to the lot with provided id.
     * If the user is not the owner of this lot, throws LotNotFoundException.
     * @param lotId the id of the lot, from which to delete the orders
     * @param principal current user (lot owner) identifier.
     * @return the response with delete successful message.
     */
    @DeleteMapping("/owner/order/lot/{lotId}")
    public ResponseEntity<GenericResponseDTO<String>> deleteOrderByLotId(
            @PathVariable(value = "lotId") Long lotId,
            Principal principal) {
        return ResponseEntity.ok(rentOrderService.deleteOwnerOrderByLotId(lotId, principal));
    }

}
