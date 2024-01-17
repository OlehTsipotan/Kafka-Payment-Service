package com.service.payment.controller;

import com.service.payment.dto.CustomerDto;
import com.service.payment.dto.DtoSearchResponse;
import com.service.payment.service.CustomerService;
import com.service.payment.utils.PaginationSortingUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/customers")
@Slf4j
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Long create(@RequestBody CustomerDto customerDto) {
        return service.save(customerDto);
    }

    @PatchMapping("/{customerId}")
    @ResponseStatus(HttpStatus.OK)
    public CustomerDto update(@RequestBody CustomerDto customerDto, @PathVariable Long customerId) {
        return service.update(customerDto, customerId);
    }

    @GetMapping("/{customerId}")
    @ResponseStatus(HttpStatus.OK)
    public CustomerDto getById(@PathVariable Long customerId) {
        return service.findByIdAsDto(customerId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public DtoSearchResponse getAll(@RequestParam(defaultValue = "100") int limit,
                                    @RequestParam(defaultValue = "0") int offset,
                                    @RequestParam(defaultValue = "id,asc") String[] sort) {
        Pageable pageable = PaginationSortingUtils.getPageable(limit, offset, sort);
        return service.findAll(pageable);
    }

    @DeleteMapping("/{customerId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long customerId) {
        service.deleteById(customerId);
    }
}
