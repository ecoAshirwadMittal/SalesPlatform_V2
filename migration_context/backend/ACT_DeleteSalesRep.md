# Microflow Detailed Specification: ACT_DeleteSalesRep

### 📥 Inputs (Parameters)
- **$SalesRepresentative** (Type: EcoATM_BuyerManagement.SalesRepresentative)

### ⚙️ Execution Flow (Logic Steps)
1. **Retrieve related **Offer_SalesRepresentative** via Association from **$SalesRepresentative** (Result: **$OfferList**)**
2. 🔀 **DECISION:** `$OfferList = empty`
   ➔ **If [true]:**
      1. **Delete**
      2. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **Show Message (Error): `This Sales Rep has Offers Associated with it . Cannot be Deleted.`**
      2. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.