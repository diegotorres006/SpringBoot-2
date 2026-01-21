package ec.edu.ups.icc.Springboot01.products.services;

import ec.edu.ups.icc.Springboot01.products.dtos.*;
import ec.edu.ups.icc.Springboot01.products.entities.ProductEntity;
import ec.edu.ups.icc.Springboot01.products.repositories.ProductRepository;
import ec.edu.ups.icc.Springboot01.users.repositories.UserRepository;
import ec.edu.ups.icc.Springboot01.categories.repositories.CategoryRepository;
import ec.edu.ups.icc.Springboot01.users.entities.UserEntity;
import ec.edu.ups.icc.Springboot01.categories.entities.CategoryEntity;
import ec.edu.ups.icc.Springboot01.products.mappers.ProductMapper;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepo;
    private final UserRepository userRepo;
    private final CategoryRepository categoryRepo;

    public ProductServiceImpl(ProductRepository productRepo, UserRepository userRepo, CategoryRepository categoryRepo) {
        this.productRepo = productRepo;
        this.userRepo = userRepo;
        this.categoryRepo = categoryRepo;
    }

    @Override
    public ProductResponseDto create(CreateProductDto dto) {
        UserEntity owner = userRepo.findById(dto.userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + dto.userId));
        
        Set<CategoryEntity> categories = new HashSet<>();
        if (dto.categoryIds != null) {
            for (Long catId : dto.categoryIds) {
                categoryRepo.findById(catId).ifPresent(categories::add);
            }
        }

        ProductEntity entity = new ProductEntity();
        entity.setName(dto.name);
        entity.setPrice(dto.price);
        entity.setDescription(dto.description);
        entity.setOwner(owner);
        entity.setCategories(categories);
        entity.setDeleted(false);
        
        return ProductMapper.toResponse(productRepo.save(entity));
    }

    @Override
    public List<ProductResponseDto> findAll() {
        return productRepo.findAll().stream()
                .map(ProductMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ProductResponseDto findOne(int id) {
        return productRepo.findById((long) id)
                .map(ProductMapper::toResponse)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + id));
    }

    @Override
    public ProductResponseDto update(int id, UpdateProductDto dto) {
        ProductEntity entity = productRepo.findById((long) id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        entity.setName(dto.name);
        entity.setPrice(dto.price);
        entity.setDescription(dto.description);
        return ProductMapper.toResponse(productRepo.save(entity));
    }

    @Override
    public ProductResponseDto partialUpdate(int id, PartialUpdateProductDto dto) {
        ProductEntity entity = productRepo.findById((long) id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        if (dto.name != null) entity.setName(dto.name);
        if (dto.price != null) entity.setPrice(dto.price);
        return ProductMapper.toResponse(productRepo.save(entity));
    }

    @Override
    public Object delete(int id) {
        ProductEntity entity = productRepo.findById((long) id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        entity.setDeleted(true); // Borrado lógico
        productRepo.save(entity);
        return new Object() { public String message = "Producto eliminado lógicamente"; };
    }

    @Override
    public boolean validateName(Integer id, String name) {
        // Implementación básica de validación
        return name != null && !name.trim().isEmpty();
    }

    // --- MÉTODOS DE PAGINACIÓN ---

    @Override
    public Page<ProductResponseDto> findAllPaged(int page, int size, String[] sort) {
        Pageable pageable = createPageable(page, size, sort);
        return productRepo.findAll(pageable).map(ProductMapper::toResponse);
    }

    @Override
    public Slice<ProductResponseDto> findAllSlice(int page, int size, String[] sort) {
        Pageable pageable = createPageable(page, size, sort);
        return productRepo.findBy(pageable).map(ProductMapper::toResponse);
    }

    @Override
    public Page<ProductResponseDto> findWithFiltersPaged(String name, Double minPrice, Double maxPrice, Long categoryId, int page, int size, String[] sort) {
        Pageable pageable = createPageable(page, size, sort);
        return productRepo.findAllWithFilters(name, minPrice, maxPrice, categoryId, pageable).map(ProductMapper::toResponse);
    }

    private Pageable createPageable(int page, int size, String[] sort) {
        if (sort.length == 2 && (sort[1].equalsIgnoreCase("asc") || sort[1].equalsIgnoreCase("desc"))) {
            Sort.Direction direction = sort[1].equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
            return PageRequest.of(page, size, Sort.by(direction, sort[0]));
        }
        return PageRequest.of(page, size, Sort.by(sort[0]).ascending());
    }
}