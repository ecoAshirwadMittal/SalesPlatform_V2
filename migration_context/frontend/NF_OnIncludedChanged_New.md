# Nanoflow: NF_OnIncludedChanged_New

## 📥 Inputs

- **$QualifiedBuyerCodes** (EcoATM_BuyerManagement.QualifiedBuyerCodes)

## ⚙️ Execution Flow

1. **Retrieve related **QualifiedBuyerCodes_SchedulingAuction** via Association from **$QualifiedBuyerCodes** (Result: **$SchedulingAuction**)**
2. 🔀 **DECISION:** `$SchedulingAuction/RoundStatus = AuctionUI.enum_SchedulingAuctionStatus.Closed`
   ➔ **If [true]:**
      1. **Update **$QualifiedBuyerCodes** (and Save to DB)
      - Set **Included** = `not($QualifiedBuyerCodes/Included)`**
      2. **Show Message (Information): `Round cannot be modified if it is closed.`**
      3. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. 🔀 **DECISION:** `$QualifiedBuyerCodes/Included = true`
         ➔ **If [true]:**
            1. **Update **$QualifiedBuyerCodes** (and Save to DB)
      - Set **Included** = `$QualifiedBuyerCodes/Included`
      - Set **Qualificationtype** = `EcoATM_BuyerManagement.enum_BuyerCodeQualificationType.Manual`**
            2. 🔀 **DECISION:** `$SchedulingAuction/RoundStatus = AuctionUI.enum_SchedulingAuctionStatus.Started`
               ➔ **If [true]:**
                  1. **Call Microflow **AuctionUI.SUB_SendManualQualificationEmail****
                  2. 🏁 **END:** Return empty
               ➔ **If [false]:**
                  1. 🏁 **END:** Return empty
         ➔ **If [false]:**
            1. **Update **$QualifiedBuyerCodes** (and Save to DB)
      - Set **Included** = `$QualifiedBuyerCodes/Included`
      - Set **Qualificationtype** = `EcoATM_BuyerManagement.enum_BuyerCodeQualificationType.Not_Qualified`**
            2. 🏁 **END:** Return empty

## 🏁 Returns
`Void`
