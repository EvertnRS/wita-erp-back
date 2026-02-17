package org.wita.erp.domain.entities.audit;

import lombok.Getter;
import org.wita.erp.domain.entities.customer.Customer;
import org.wita.erp.domain.entities.payment.PaymentType;
import org.wita.erp.domain.entities.product.Category;
import org.wita.erp.domain.entities.product.Product;
import org.wita.erp.domain.entities.stock.MovementReason;
import org.wita.erp.domain.entities.stock.StockMovement;
import org.wita.erp.domain.entities.supplier.Supplier;
import org.wita.erp.domain.entities.transaction.Transaction;
import org.wita.erp.domain.entities.transaction.order.Receivable;
import org.wita.erp.domain.entities.transaction.purchase.Payable;
import org.wita.erp.domain.entities.user.User;
import org.wita.erp.domain.entities.user.role.Role;

@Getter
public enum EntityType {

    USER("users", User.class),
    ROLE("role", Role.class),
    CATEGORY("category", Category.class),
    PRODUCT("product", Product.class),
    CUSTOMER("customer", Customer.class),
    SUPPLIER("supplier", Supplier.class),
    PAYMENT_TYPE("payment_type", PaymentType.class),
    TRANSACTION("transaction", Transaction.class),
    RECEIVABLE("receivable", Receivable.class),
    PAYABLE("payable", Payable.class),
    MOVEMENT_REASON("movement_reason", MovementReason.class),
    STOCK_MOVEMENT("stock_movement", StockMovement.class);

    private final String entityType;
    private final Class<?> entityClass;

    EntityType(String entityType, Class<?> entityClass) {
        this.entityType = entityType;
        this.entityClass = entityClass;
    }

    public static EntityType fromPath(String value) {
        for (EntityType type : values()) {
            if (type.entityType.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Entity not auditable: " + value);
    }
}
