package org.wita.erp.controllers.transaction.order.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.wita.erp.domain.entities.transaction.dtos.OrderDTO;
import org.wita.erp.domain.entities.transaction.order.dtos.CreateOrderRequestDTO;
import org.wita.erp.domain.entities.transaction.order.dtos.DeleteOrderRequestDTO;
import org.wita.erp.domain.entities.transaction.order.dtos.ProductInOrderDTO;
import org.wita.erp.domain.entities.transaction.order.dtos.UpdateOrderRequestDTO;

import java.util.UUID;

@Tag(name = "Order management", description = "Endpoints to list, create, update and delete order on ERP system")
public interface OrderDocs {

    @Operation(summary = "List Paged Order", description = "Return a order list with pagination support and customer's name filter. \nRequires ORDER_READ authority.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied - user does not have ORDER_READ authority", content = @Content)
    })
    ResponseEntity<Page<OrderDTO>> getAllOrders(@ParameterObject
                                                Pageable pageable,
                                                @Parameter(description = "Term used to filter orders by customer's name", example = "John Doe")
                                                String searchTerm);

    @Operation(summary = "Create a order", description = "Create a new order with discount, installments, seller, payment type, transaction code, description, movement reason and products. Requires ORDER_CREATE authority.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order created successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = OrderDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied - user does not have ORDER_CREATE authority", content = @Content),
    })
    ResponseEntity<OrderDTO> create(CreateOrderRequestDTO data);

    @Operation(summary = "Add product to a order", description = "Add a new product of a specific order. \nRequires ORDER_UPDATE authority.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product added to order successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = OrderDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied - user does not have ORDER_UPDATE authority", content = @Content),
            @ApiResponse(responseCode = "404", description = "Order not found", content = @Content),
            @ApiResponse(responseCode = "404", description = "Product not found", content = @Content)
    })
    ResponseEntity<OrderDTO> addProductInOrder(@Parameter(description = "UUID of the order to add a product", example = "123e4567-e89b-12d3-a456-426614174000")
                                               UUID orderId,
                                               ProductInOrderDTO data);

    @Operation(summary = "Remove product to a order", description = "Remove a product of a specific order. \nRequires ORDER_UPDATE authority.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product removed to order successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = OrderDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied - user does not have ORDER_UPDATE authority", content = @Content),
            @ApiResponse(responseCode = "404", description = "Order not found", content = @Content),
            @ApiResponse(responseCode = "404", description = "Product not found", content = @Content)
    })
    ResponseEntity<OrderDTO> removeProductInOrder(@Parameter(description = "UUID of the order to remove a product", example = "123e4567-e89b-12d3-a456-426614174000")
                                                  UUID orderId,
                                                  ProductInOrderDTO data);

    @Operation(summary = "Update order data", description = "Update the value, installments, discount, seller, payment type, transaction code and description for a specific order. \nRequires ORDER_UPDATE authority.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order updated successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = OrderDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied - user does not have ORDER_UPDATE authority", content = @Content),
            @ApiResponse(responseCode = "404", description = "Order not found", content = @Content)
    })
    ResponseEntity<OrderDTO> update(@Parameter(description = "UUID of the order to update", example = "123e4567-e89b-12d3-a456-426614174000")
                                    UUID id,
                                    UpdateOrderRequestDTO data);

    @Operation(summary = "Remove order", description = "Inactivate a specific order from the system. \nRequires ORDER_DELETE authority.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order deleted successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = OrderDTO.class))),
            @ApiResponse(responseCode = "403", description = "Access denied - user does not have ORDER_DELETE authority", content = @Content),
            @ApiResponse(responseCode = "404", description = "Order not found", content = @Content)
    })
    ResponseEntity<OrderDTO> delete(@Parameter(description = "UUID of the order to remove and reason of delete", example = "123e4567-e89b-12d3-a456-426614174000")
                                    UUID id, DeleteOrderRequestDTO data);

}


