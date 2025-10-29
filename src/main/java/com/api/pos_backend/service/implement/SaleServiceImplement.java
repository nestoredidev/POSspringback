package com.api.pos_backend.service.implement;

import com.api.pos_backend.dto.SaleDTO;
import com.api.pos_backend.entity.Coupon;
import com.api.pos_backend.entity.Product;
import com.api.pos_backend.entity.Sale;
import com.api.pos_backend.entity.SaleDetails;
import com.api.pos_backend.entity.Users;
import com.api.pos_backend.mapper.SaleDetailsMapper;
import com.api.pos_backend.mapper.SaleMapper;
import com.api.pos_backend.repository.CouponRepository;
import com.api.pos_backend.repository.ProductRepository;
import com.api.pos_backend.repository.SaleDetailsRepository;
import com.api.pos_backend.repository.SaleRepository;
import com.api.pos_backend.repository.UserRepository;
import com.api.pos_backend.service.SaleService;
import com.api.pos_backend.shared.PDF.PdfService;
import com.api.pos_backend.shared.pagination.PageResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SaleServiceImplement implements SaleService {

    private final SaleRepository saleRepository;
    private final SaleDetailsRepository saleDetailsRepository;
    private final UserRepository usersRepository;
    private final CouponRepository couponRepository;
    private final ProductRepository productRepository;
    private final SaleDetailsMapper saleDetailsMapper;
    private final SaleMapper saleMapper;
    private final PdfService pdfService;

    @Transactional
    @Override
    public PageResponse<SaleDTO> createSale(SaleDTO saleDTO) {
        // Validar que existen detalles de venta
        if (saleDTO.getSaleDetails() == null || saleDTO.getSaleDetails().isEmpty()) {
            throw new RuntimeException("La venta debe tener al menos un detalle");
        }

        // Obtener usuario
        Users user = usersRepository.findById(saleDTO.getUser().getId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Obtener cupón (opcional)
        Coupon coupon = null;
        if (saleDTO.getCoupon() != null && saleDTO.getCoupon().getId() != null) {
            coupon = couponRepository.findById(saleDTO.getCoupon().getId())
                    .orElseThrow(() -> new RuntimeException("Cupón no encontrado"));
        }

        // Validar stock de todos los productos antes de proceder
        validateProductStock(saleDTO.getSaleDetails());

        // Crear la venta principal
        Sale sale = Sale.builder()
                .totalAmount(saleDTO.getTotalAmount())
                .user(user)
                .coupon(coupon)
                .saleDetails(new ArrayList<>())
                .build();

        Sale savedSale = saleRepository.save(sale);

        // Crear y guardar los detalles de venta
        List<SaleDetails> savedDetails = new ArrayList<>();
        for (var detailDTO : saleDTO.getSaleDetails()) {
            // Convertir DTO a entidad (incluye buscar el producto)
            SaleDetails detail = saleDetailsMapper.toEntity(detailDTO);
            detail.setSale(savedSale);

            // Actualizar stock del producto
            Product product = detail.getProduct();
            int newStock = product.getStock() - detail.getQuantity();
            product.setStock(newStock);
            productRepository.save(product);

            // Guardar el detalle de venta
            SaleDetails savedDetail = saleDetailsRepository.save(detail);
            savedDetails.add(savedDetail);
        }

        // Actualizar la venta con los detalles guardados
        savedSale.setSaleDetails(savedDetails);
        savedSale = saleRepository.save(savedSale);

        // Convertir a DTO y retornar
        saleMapper.toDTO(savedSale);
        return PageResponse.single(
                SaleDTO.builder()
                        .id(savedSale.getId())
                        .totalAmount(savedSale.getTotalAmount())
                        .userId(savedSale.getUser().getId())
                        .userName(savedSale.getUser().getUsername())
                        .date(savedSale.getDate())
                        .couponCode(savedSale.getCoupon() != null ? savedSale.getCoupon().getName() : null)
                        .saleDetails(savedDetails.stream()
                                .map(saleDetailsMapper::toDTO)
                                .toList())
                        .build(),
                "Venta creada exitosamente",
                201
        );
    }

    @Override
    public PageResponse<SaleDTO> getAllSales(Pageable pageable) {
        Page<Sale> sales = saleRepository.findAll(pageable);

        Page<SaleDTO> saleDTOs = sales.map(sale ->
                SaleDTO.builder()
                        .id(sale.getId())
                        .totalAmount(sale.getTotalAmount())
                        .userId(sale.getUser().getId())
                        .userName(sale.getUser().getUsername())
                        .date(sale.getDate())
                        .couponCode(sale.getCoupon() != null ?
                                sale.getCoupon().getName() : null)
                        .saleDetails(sale.getSaleDetails().stream()
                                .map(saleDetailsMapper::toDTO)
                                .toList())
                        .build()
        );

        if (saleDTOs.getTotalElements() == 0) {
            return PageResponse.empty(
                    saleDTOs,
                    "No hay ventas registradas",
                    204
            );
        }

        return PageResponse.fromPage(
                saleDTOs,
                "Lista de ventas obtenida exitosamente",
                200
        );
    }

    @Override
    public PageResponse<SaleDTO> getSaleById(Long id) {
        Sale sale = saleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Venta no encontrada"));
        SaleDTO saleDTO = SaleDTO.builder()
                .id(sale.getId())
                .totalAmount(sale.getTotalAmount())
                .userId(sale.getUser().getId())
                .userName(sale.getUser().getUsername())
                .date(sale.getDate())
                .couponCode(sale.getCoupon() != null ?
                        sale.getCoupon().getName() : null)
                .saleDetails(sale.getSaleDetails().stream()
                        .map(saleDetailsMapper::toDTO)
                        .toList())
                .build();
        return PageResponse.single(
                saleDTO,
                "Venta obtenida exitosamente",
                200
        );

    }

    /*@Override
    public List<SaleDTO> getSalesByUserId(Long userId) {
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        List<Sale> sales = saleRepository.findByUser(user);
        return sales.stream()
                .map(saleMapper::toDTO)
                .toList();
    }*/

    @Override
    @Transactional
    public String deleteSale(Long id) {
        Sale sale = saleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Venta no encontrada"));

        // Restaurar stock de productos antes de eliminar
        for (SaleDetails detail : sale.getSaleDetails()) {
            Product product = detail.getProduct();
            product.setStock(product.getStock() + detail.getQuantity());
            productRepository.save(product);
        }

        saleRepository.delete(sale);
        return "Venta eliminada exitosamente";
    }

    @Override
    public PageResponse<SaleDTO> getSalesByDateRange(String startDate, String endDate, Pageable pageable) {
        Page<Sale> sales = saleRepository.findByDateBetween(startDate, endDate, pageable);
        Page<SaleDTO> saleDTOs = sales.map(sale ->
                SaleDTO.builder()
                        .id(sale.getId())
                        .totalAmount(sale.getTotalAmount())
                        .userId(sale.getUser().getId())
                        .userName(sale.getUser().getUsername())
                        .date(sale.getDate())
                        .couponCode(sale.getCoupon() != null ?
                                sale.getCoupon().getName() : null)
                        .saleDetails(sale.getSaleDetails().stream()
                                .map(saleDetailsMapper::toDTO)
                                .toList())
                        .build()
        );

        if (saleDTOs.getTotalElements() == 0) {
            return PageResponse.empty(
                    saleDTOs,
                    "No hay ventas en el rango de fechas especificado",
                    204
            );
        }

        return PageResponse.fromPage(
                saleDTOs,
                "Ventas en el rango de fechas obtenidas exitosamente",
                200
        );
    }



    private void validateProductStock(List<com.api.pos_backend.dto.SaleDetailsDTO> saleDetails) {
        for (var detailDTO : saleDetails) {
            Product product = productRepository.findById(detailDTO.getProductId())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + detailDTO.getProductId()));

            if (product.getStock() < detailDTO.getQuantity()) {
                throw new RuntimeException(
                        String.format("Stock insuficiente para el producto '%s'. Stock disponible: %d, Cantidad solicitada: %d",
                                product.getName(),
                                product.getStock(),
                                detailDTO.getQuantity())
                );
            }

            if (detailDTO.getQuantity() <= 0) {
                throw new RuntimeException("La cantidad debe ser mayor a 0");
            }
        }
    }

    private void updateProductStock(Long productId, Integer quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        int newStock = product.getStock() - quantity;
        if (newStock < 0) {
            throw new RuntimeException("El stock no puede ser negativo");
        }
        product.setStock(newStock);
        productRepository.save(product);
    }
    @Override
    public byte[] generateSalePdf(Long saleId) {

        PageResponse<SaleDTO> response = getSaleById(saleId);
        SaleDTO sale = response.getContent().get(0);
        return pdfService.generateSalePdf(sale);
    }

}

