# Microflow Detailed Specification: SUB_RetrieveLatestSummaryReportBySession

### 📥 Inputs (Parameters)
- **$Session** (Type: System.Session)

### ⚙️ Execution Flow (Logic Steps)
1. **Retrieve related **BuyerBidSummaryReportHelper_Session** via Association from **$Session** (Result: **$BuyerBidSummaryReportHelperList**)**
2. **List Operation: **Sort** on **$undefined** sorted by: createdDate (Descending) (Result: **$SortedReportObjects**)**
3. **List Operation: **Head** on **$undefined** (Result: **$RecentSummaryReport**)**
4. 🏁 **END:** Return `$RecentSummaryReport`

**Final Result:** This process concludes by returning a [Object] value.