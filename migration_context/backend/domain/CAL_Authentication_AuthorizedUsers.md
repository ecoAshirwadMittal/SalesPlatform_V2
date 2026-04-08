# Microflow Detailed Specification: CAL_Authentication_AuthorizedUsers

### 📥 Inputs (Parameters)
- **$Authentication** (Type: MicrosoftGraph.Authentication)

### ⚙️ Execution Flow (Logic Steps)
1. **Retrieve related **Authorization_Authentication** via Association from **$Authentication** (Result: **$AuthorizationList**)**
2. **List Operation: **Filter** on **$undefined** where `true` (Result: **$NewAuthorizationList**)**
3. **AggregateList**
4. 🏁 **END:** Return `$Count`

**Final Result:** This process concludes by returning a [Integer] value.