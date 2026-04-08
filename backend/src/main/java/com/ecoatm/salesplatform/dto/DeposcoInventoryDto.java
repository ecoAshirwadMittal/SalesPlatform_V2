package com.ecoatm.salesplatform.dto;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTOs for the Deposco Inventory API response structure.
 * Maps to the paginated facility → inventory → item hierarchy.
 */
public class DeposcoInventoryDto {

    /**
     * Top-level wrapper returned by the Deposco /inventory endpoint.
     */
    public static class InventoryPage {
        private List<ItemInventory> items;
        private int pageNumber;
        private int totalPages;

        public List<ItemInventory> getItems() { return items; }
        public void setItems(List<ItemInventory> items) { this.items = items; }
        public int getPageNumber() { return pageNumber; }
        public void setPageNumber(int pageNumber) { this.pageNumber = pageNumber; }
        public int getTotalPages() { return totalPages; }
        public void setTotalPages(int totalPages) { this.totalPages = totalPages; }
    }

    /**
     * One item (SKU) from the Deposco response, potentially spanning multiple facilities.
     */
    public static class ItemInventory {
        private String itemNumber;
        private int pageNo;
        private List<FacilityInventory> facilities;

        public String getItemNumber() { return itemNumber; }
        public void setItemNumber(String itemNumber) { this.itemNumber = itemNumber; }
        public int getPageNo() { return pageNo; }
        public void setPageNo(int pageNo) { this.pageNo = pageNo; }
        public List<FacilityInventory> getFacilities() { return facilities; }
        public void setFacilities(List<FacilityInventory> facilities) { this.facilities = facilities; }
    }

    /**
     * Facility-level inventory breakdown for an item.
     */
    public static class FacilityInventory {
        private String facility;
        private BigDecimal total;
        private BigDecimal availableToPromise;
        private BigDecimal unallocated;
        private BigDecimal allocated;

        public String getFacility() { return facility; }
        public void setFacility(String facility) { this.facility = facility; }
        public BigDecimal getTotal() { return total; }
        public void setTotal(BigDecimal total) { this.total = total; }
        public BigDecimal getAvailableToPromise() { return availableToPromise; }
        public void setAvailableToPromise(BigDecimal availableToPromise) { this.availableToPromise = availableToPromise; }
        public BigDecimal getUnallocated() { return unallocated; }
        public void setUnallocated(BigDecimal unallocated) { this.unallocated = unallocated; }
        public BigDecimal getAllocated() { return allocated; }
        public void setAllocated(BigDecimal allocated) { this.allocated = allocated; }
    }
}
