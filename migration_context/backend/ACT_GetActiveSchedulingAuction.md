# Microflow Detailed Specification: ACT_GetActiveSchedulingAuction

### 📥 Inputs (Parameters)
- **$Auction** (Type: AuctionUI.Auction)

### ⚙️ Execution Flow (Logic Steps)
1. **Retrieve related **SchedulingAuction_Auction** via Association from **$Auction** (Result: **$SchedulingAuctions**)**
2. **List Operation: **Filter** on **$undefined** where `AuctionUI.enum_SchedulingAuctionStatus.Started` (Result: **$SchedulingAuctionStartedList**)**
3. **List Operation: **Head** on **$undefined** (Result: **$SchedulingAuctionStarted**)**
4. 🔀 **DECISION:** `$SchedulingAuctionStarted != empty`
   ➔ **If [true]:**
      1. 🏁 **END:** Return `$SchedulingAuctionStarted`
   ➔ **If [false]:**
      1. **DB Retrieve **System.UserRole** Filter: `[System.UserRoles = $currentUser and (Name='SalesRep' or Name='Administrator' or Name='SalesOps')]` (Result: **$ElevatedUserRole**)**
      2. 🔀 **DECISION:** `$ElevatedUserRole != empty`
         ➔ **If [false]:**
            1. 🏁 **END:** Return `empty`
         ➔ **If [true]:**
            1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/RoundStatus = AuctionUI.enum_SchedulingAuctionStatus.Closed and $currentObject/Round = 1` (Result: **$SchedulingAuction_ClosedRoundOne**)**
            2. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/RoundStatus = AuctionUI.enum_SchedulingAuctionStatus.Scheduled and $currentObject/Round = 2` (Result: **$SchedulingAuction_ScheduledRoundTwo**)**
            3. 🔀 **DECISION:** `$SchedulingAuction_ClosedRoundOne != empty and $SchedulingAuction_ScheduledRoundTwo != empty`
               ➔ **If [true]:**
                  1. 🏁 **END:** Return `$SchedulingAuction_ClosedRoundOne`
               ➔ **If [false]:**
                  1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/RoundStatus = AuctionUI.enum_SchedulingAuctionStatus.Closed and $currentObject/Round = 2` (Result: **$SchedulingAuction_ClosedRoundTwo**)**
                  2. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/RoundStatus = AuctionUI.enum_SchedulingAuctionStatus.Scheduled and $currentObject/Round = 3` (Result: **$SchedulingAuction_ScheduledRoundThree**)**
                  3. 🔀 **DECISION:** `$SchedulingAuction_ClosedRoundTwo != empty and $SchedulingAuction_ScheduledRoundThree != empty`
                     ➔ **If [true]:**
                        1. 🏁 **END:** Return `$SchedulingAuction_ClosedRoundTwo`
                     ➔ **If [false]:**
                        1. 🏁 **END:** Return `empty`

**Final Result:** This process concludes by returning a [Object] value.