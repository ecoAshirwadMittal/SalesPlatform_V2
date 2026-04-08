# Nanoflow: BidTemplate_Assign

**Allowed Roles:** EcoATM_BidData.User

## 📥 Inputs

- **$BidData_ImportSettings** (AuctionUI.BidData_ImportSettings)

## ⚙️ Execution Flow

1. **Retrieve related **BidData_ImportSettings_DefaultTemplate** via Association from **$BidData_ImportSettings** (Result: **$DefaultTemplate_Selection**)**
2. **Update **$BidData_ImportSettings** (and Save to DB)
      - Set **TemplateName** = `$DefaultTemplate_Selection/Title`
      - Set **BidData_ImportSettings_DefaultTemplate** = `$DefaultTemplate_Selection`**
3. **Show Message (Information): `Default Template for Bid Upload Changed to {1}`**
4. 🏁 **END:** Return empty

## 🏁 Returns
`Void`
