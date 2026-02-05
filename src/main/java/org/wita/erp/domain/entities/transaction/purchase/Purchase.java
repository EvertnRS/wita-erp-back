package org.wita.erp.domain.entities.transaction.purchase;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.wita.erp.domain.entities.supplier.Supplier;
import org.wita.erp.domain.entities.transaction.Transaction;
import org.wita.erp.domain.entities.user.User;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "purchase")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@PrimaryKeyJoinColumn(name = "id")
public class Purchase extends Transaction {

    @ManyToOne
    @JoinColumn(name = "buyer_id", nullable = false)
    private User buyer;

    @ManyToOne
    @JoinColumn(name = "supplier_id", nullable = false)
    private Supplier supplier;

    @OneToMany(mappedBy = "purchase", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PurchaseItem> items = new ArrayList<>();

    public void addItem(PurchaseItem item) {
        this.items.add(item);
        item.setPurchase(this);
    }

    public void removeItens() {
        this.items.clear();
    }

    public void calculateSubTotal() {
        this.value = items.stream()
                .map(PurchaseItem::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

    }


}
