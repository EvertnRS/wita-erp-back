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
import org.wita.erp.domain.entities.stock.dtos.DeleteStockRequestDTO;
import org.wita.erp.domain.entities.stock.dtos.StockMovementDTO;
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
    public ResponseEntity<Page<StockMovementDTO>> getAllStock(@PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable, @RequestParam(required = false) String searchTerm) {
        return stockService.getAllStock(pageable, searchTerm);
    }

    /*@PostMapping("/create")
    @PreAuthorize("hasAuthority('STOCK_CREATE')")
    public ResponseEntity<StockMovementDTO> create(@Valid @RequestBody CreateStockRequestDTO data) {
        return stockService.save(data);
    }*/

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('STOCK_UPDATE')")
    public ResponseEntity<StockMovementDTO> update(@PathVariable UUID id, @RequestBody @Valid UpdateStockRequestDTO data) {
        return stockService.update(id, data);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('STOCK_DELETE')")
    public ResponseEntity<StockMovementDTO> delete(@PathVariable UUID id, @RequestBody @Valid DeleteStockRequestDTO data) {
        return stockService.delete(id, data);
    }
}
