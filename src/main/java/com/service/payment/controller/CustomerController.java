package com.service.payment.controller;

import com.service.payment.dto.CustomerDto;
import com.service.payment.dto.DtoSearchResponse;
import com.service.payment.service.CustomerService;
import com.service.payment.utils.PaginationSortingUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/customers")
@Slf4j
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService service;

    @Operation(summary = "Create the Customer")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Customer created successfully", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = CustomerDto.class))}),
            @ApiResponse(responseCode = "409", description = "Customer already exists", content = @Content)})
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Long create(@RequestBody CustomerDto customerDto) {
        return service.create(customerDto);
    }

    @Operation(summary = "Update the Customer")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Customer updated successfully", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = CustomerDto.class))}),
            @ApiResponse(responseCode = "404", description = "Customer not found", content = @Content)})
    @PatchMapping("/{customerId}")
    @ResponseStatus(HttpStatus.OK)
    public CustomerDto update(@RequestBody CustomerDto customerDto, @PathVariable Long customerId) {
        return service.update(customerDto, customerId);
    }

    @Operation(summary = "Retrieve the Customer by Id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Customer retrieved successfully", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = CustomerDto.class))}),
            @ApiResponse(responseCode = "404", description = "Customer not found", content = @Content)})
    @GetMapping("/{customerId}")
    @ResponseStatus(HttpStatus.OK)
    public CustomerDto getById(@PathVariable Long customerId) {
        return service.findByIdAsDto(customerId);
    }

    @Operation(summary = "Retrieve the Customers")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Customers retrieved successfully", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = CustomerDto.class))})})
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public DtoSearchResponse getAll(@RequestParam(defaultValue = "100") int limit,
                                    @RequestParam(defaultValue = "0") int offset,
                                    @RequestParam(defaultValue = "id,asc") String[] sort) {
        Pageable pageable = PaginationSortingUtils.getPageable(limit, offset, sort);
        return service.findAll(pageable);
    }

    @Operation(summary = "Delete the Customer by Id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Customer deleted successfully", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = CustomerDto.class))}),
            @ApiResponse(responseCode = "404", description = "Customer not found", content = @Content)})
    @DeleteMapping("/{customerId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long customerId) {
        service.deleteById(customerId);
    }
}
