package com.service.payment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.service.payment.dto.CustomerDto;
import com.service.payment.dto.DtoSearchResponse;
import com.service.payment.service.CustomerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({CustomerController.class})
public class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CustomerService customerService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void create_whenCustomerDTOIsValid_success() throws Exception {
        when(customerService.create(any(CustomerDto.class))).thenReturn(1L);

        CustomerDto customerDto = new CustomerDto();
        customerDto.setName("name");
        customerDto.setBalanceAvailable(1000L);
        customerDto.setBalanceReserved(1000L);

        mockMvc.perform(post("/api/v1/customers").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customerDto))).andExpect(status().isCreated())
                .andExpect(content().string("1"));

        verify(customerService).create(customerDto);
        verifyNoMoreInteractions(customerService);
    }

    @ParameterizedTest
    @NullSource
    public void create_whenCustomerDTOIsNull_statusIsBadRequest(CustomerDto customerDto) throws Exception {
        mockMvc.perform(post("/api/v1/customers").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(customerDto))).andExpect(status().isBadRequest());

        verifyNoInteractions(customerService);
    }

    @Test
    public void delete_success() throws Exception {
        mockMvc.perform(delete("/api/v1/customers/1")).andExpect(status().isNoContent());

        verify(customerService).deleteById(1L);
        verifyNoMoreInteractions(customerService);
    }

    @Test
    public void delete_whenCustomerIdIsInvalid_statusIsBadRequest() throws Exception {
        mockMvc.perform(delete("/api/v1/customers/invalid")).andExpect(status().isBadRequest());

        verifyNoInteractions(customerService);
    }

    @Test
    public void update_whenCustomerDTOIsValid_success() throws Exception {
        CustomerDto customerDTOToDisplay = new CustomerDto();
        customerDTOToDisplay.setName("name");
        customerDTOToDisplay.setBalanceAvailable(1000L);
        customerDTOToDisplay.setBalanceReserved(1000L);
        CustomerDto customerDto = new CustomerDto();
        customerDto.setName("name");
        customerDto.setBalanceAvailable(1000L);
        customerDto.setBalanceReserved(1000L);

        when(customerService.update(any(CustomerDto.class), any(Long.class))).thenReturn(customerDTOToDisplay);

        mockMvc.perform(patch("/api/v1/customers/1").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customerDto))).andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(customerDTOToDisplay)));

        verify(customerService).update(customerDto, 1L);
        verifyNoMoreInteractions(customerService);
    }

    @ParameterizedTest
    @NullSource
    public void update_whenCustomerDTOIsNull_statusIsBadRequest(CustomerDto customerDto) throws Exception {
        mockMvc.perform(patch("/api/v1/customers/1").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(customerDto))).andExpect(status().isBadRequest());

        verifyNoInteractions(customerService);
    }

    @Test
    public void update_whenCustomerIdIsInvalid_statusIsBadRequest() throws Exception {
        CustomerDto customerDto = new CustomerDto();
        customerDto.setName("name");
        customerDto.setBalanceAvailable(1000L);
        customerDto.setBalanceReserved(1000L);

        mockMvc.perform(patch("/api/v1/customers/invalid").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(customerDto))).andExpect(status().isBadRequest());

        verifyNoInteractions(customerService);
    }

    @Test
    public void getById_success() throws Exception {
        CustomerDto customerDto = new CustomerDto();
        customerDto.setName("name");
        customerDto.setBalanceAvailable(1000L);
        customerDto.setBalanceReserved(1000L);

        when(customerService.findByIdAsDto(any(Long.class))).thenReturn(customerDto);

        mockMvc.perform(get("/api/v1/customers/1")).andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(customerDto)));

        verify(customerService).findByIdAsDto(1L);
        verifyNoMoreInteractions(customerService);
    }

    @Test
    public void getById_whenCustomerIdIsInvalid_statusIsBadRequest() throws Exception {
        mockMvc.perform(get("/api/v1/customers/invalid")).andExpect(status().isBadRequest());

        verifyNoInteractions(customerService);
    }

    @Test
    public void getAll_success() throws Exception {
        CustomerDto customerDto = new CustomerDto();
        customerDto.setName("name");
        customerDto.setBalanceAvailable(1000L);
        customerDto.setBalanceReserved(1000L);
        List<CustomerDto> customerDTOList = List.of(customerDto);
        DtoSearchResponse dtoSearchResponse = DtoSearchResponse.builder().data(customerDTOList).build();

        when(customerService.findAll(any())).thenReturn(dtoSearchResponse);

        mockMvc.perform(get("/api/v1/customers")).andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(dtoSearchResponse)));

        verify(customerService).findAll(any());
        verifyNoMoreInteractions(customerService);
    }

    // It is not depends on the parameter selection.
    @Test
    public void getAll_whenLimitIsInvalid_statusIsBadRequest() throws Exception {
        mockMvc.perform(get("/api/v1/customers?limit=invalid")).andExpect(status().isBadRequest());

        verifyNoInteractions(customerService);
    }

}