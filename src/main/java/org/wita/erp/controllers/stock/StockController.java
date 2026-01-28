package org.wita.erp.controllers.stock;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.wita.erp.domain.entities.stock.StockMovement;
import org.wita.erp.domain.entities.stock.dtos.CreateStockRequestDTO;
import org.wita.erp.domain.entities.stock.dtos.UpdateStockRequestDTO;
import org.wita.erp.services.stock.StockService;

import java.util.UUID;

@RestController
@RequestMapping("/stock")
@RequiredArgsConstructor
public class StockController {
    private final StockService stockService;

    @GetMapping
    @PreAuthorize("hasAuthority('STOCK_READ')")
    public ResponseEntity<Page<StockMovement>> getAllStock(@PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return stockService.getAllStock(pageable);
    }

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('STOCK_CREATE')")
    public ResponseEntity<StockMovement> create(@Valid @RequestBody CreateStockRequestDTO data) {
        return stockService.save(data);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('STOCK_UPDATE')")
    public ResponseEntity<StockMovement> update(@PathVariable UUID id, @RequestBody @Valid UpdateStockRequestDTO data) {
        return stockService.update(id, data);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('STOCK_DELETE')")
    public ResponseEntity<StockMovement> delete(@PathVariable UUID id) {
        return stockService.delete(id);
    }
}
