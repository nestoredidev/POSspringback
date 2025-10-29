package com.api.pos_backend.service;

import com.api.pos_backend.dto.SaleDTO;
import com.api.pos_backend.shared.pagination.PageResponse;

import org.springframework.data.domain.Pageable;

public interface SaleService {

    PageResponse<SaleDTO> createSale(SaleDTO saleDTO);

    PageResponse<SaleDTO> getAllSales(Pageable pageable);

    PageResponse<SaleDTO> getSaleById(Long id);

    String deleteSale(Long id);

    PageResponse<SaleDTO> getSalesByDateRange(String startDate, String endDate, Pageable pageable);

    byte[] generateSalePdf(Long saleId);
}
