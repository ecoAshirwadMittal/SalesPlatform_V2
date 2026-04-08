# Microflow Detailed Specification: VAL_Create_Auction

### 📥 Inputs (Parameters)
- **$SchedulingAuction_Helper** (Type: AuctionUI.SchedulingAuction_Helper)

### ⚙️ Execution Flow (Logic Steps)
1. **Create Variable **$IsValid** = `true`**
2. **Create Variable **$Auction_TitleValidationFeedback** = `''`**
3. 🔀 **DECISION:** `trim($SchedulingAuction_Helper/Auction_Name) != ''`
   ➔ **If [true]:**
      1. **Create Variable **$Variable** = `length($SchedulingAuction_Helper/Auction_Name)`**
      2. **Create Variable **$CompleteTitle** = `'Auction'+' '+$SchedulingAuction_Helper/Auction_Week_Year +' '+ $SchedulingAuction_Helper/Auction_Name`**
      3. **DB Retrieve **AuctionUI.Auction**  (Result: **$Auction**)**
      4. **Create Variable **$TitleUnique** = `true`**
      5. 🔄 **LOOP:** For each **$IteratorAuction** in **$Auction**
         │ 1. 🔀 **DECISION:** `toLowerCase(trim($IteratorAuction/AuctionTitle))=toLowerCase(trim($CompleteTitle))`
         │    ➔ **If [true]:**
         │       1. **Update Variable **$TitleUnique** = `false`**
         │    ➔ **If [false]:**
         └─ **End Loop**
      6. 🔀 **DECISION:** `$TitleUnique`
         ➔ **If [false]:**
            1. **Update Variable **$IsValid** = `false`**
            2. **Update **$SchedulingAuction_Helper**
      - Set **Name_Unique** = `false`**
            3. 🏁 **END:** Return `$IsValid`
         ➔ **If [true]:**
            1. **Update **$SchedulingAuction_Helper**
      - Set **Name_Unique** = `true`**
            2. 🏁 **END:** Return `$IsValid`
   ➔ **If [false]:**
      1. **Update **$SchedulingAuction_Helper** (and Save to DB)
      - Set **Name_Unique** = `true`**
      2. 🏁 **END:** Return `$IsValid`

**Final Result:** This process concludes by returning a [Boolean] value.