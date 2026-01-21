package ec.edu.ups.icc.Springboot01.products.controllers;

import ec.edu.ups.icc.Springboot01.products.dtos.*;
import ec.edu.ups.icc.Springboot01.products.services.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // --- ENDPOINTS DE PAGINACIÓN (PRÁCTICA 10) ---

    @GetMapping("/paginated")
    public ResponseEntity<Page<ProductResponseDto>> findAllPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id,asc") String[] sort) {
        return ResponseEntity.ok(productService.findAllPaged(page, size, sort));
    }

    @GetMapping("/slice")
    public ResponseEntity<Slice<ProductResponseDto>> findAllSlice(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id,asc") String[] sort) {
        return ResponseEntity.ok(productService.findAllSlice(page, size, sort));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<ProductResponseDto>> findWithFilters(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id,asc") String[] sort) {
        return ResponseEntity.ok(productService.findWithFiltersPaged(name, minPrice, maxPrice, categoryId, page, size, sort));
    }

    // --- ENDPOINTS CRUD Y OTROS ---

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDto> findOne(@PathVariable int id) {
        return ResponseEntity.ok(productService.findOne(id));
    }

    @PostMapping
    public ResponseEntity<ProductResponseDto> create(@RequestBody CreateProductDto dto) {
        return ResponseEntity.ok(productService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDto> update(@PathVariable int id, @RequestBody UpdateProductDto dto) {
        return ResponseEntity.ok(productService.update(id, dto));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ProductResponseDto> partialUpdate(@PathVariable int id, @RequestBody PartialUpdateProductDto dto) {
        return ResponseEntity.ok(productService.partialUpdate(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable int id) {
        return ResponseEntity.ok(productService.delete(id));
    }

    @PostMapping("/validate-name")
    public ResponseEntity<Boolean> validateName(@RequestBody ValidateProductNameDto dto) {
        return ResponseEntity.ok(productService.validateName(dto.id, dto.name));
    }
}