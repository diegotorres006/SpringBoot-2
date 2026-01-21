package ec.edu.ups.icc.Springboot01.products.services;

import ec.edu.ups.icc.Springboot01.products.dtos.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;
import java.util.List;

public interface ProductService {
    // CRUD Básico
    ProductResponseDto create(CreateProductDto dto);
    List<ProductResponseDto> findAll();
    ProductResponseDto findOne(int id);
    ProductResponseDto update(int id, UpdateProductDto dto);
    ProductResponseDto partialUpdate(int id, PartialUpdateProductDto dto);
    Object delete(int id);
    
    // Validaciones y Negocio
    boolean validateName(Integer id, String name);

    // Paginación (Práctica 10)
    Page<ProductResponseDto> findAllPaged(int page, int size, String[] sort);
    Slice<ProductResponseDto> findAllSlice(int page, int size, String[] sort);
    Page<ProductResponseDto> findWithFiltersPaged(String name, Double minPrice, Double maxPrice, Long categoryId, int page, int size, String[] sort);
}