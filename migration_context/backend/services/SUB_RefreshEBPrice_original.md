# Microflow Detailed Specification: SUB_RefreshEBPrice_original

### ⚙️ Execution Flow (Logic Steps)
1. **LogMessage**
2. **ExecuteDatabaseQuery**
3. **List Operation: **Head** on **$undefined** (Result: **$MaxuploadTime**)**
4. 🔀 **DECISION:** `$MaxUploadtime != empty and $MaxuploadTime/MAXUPLOADTIME !=empty`
   ➔ **If [true]:**
      1. **DB Retrieve **EcoATM_EB.ReserveBidSync**  (Result: **$ReserveBidSync**)**
      2. 🔀 **DECISION:** `$ReserveBidSync =empty or $ReserveBidSync/LastSyncDateTime !=$MaxuploadTime/MAXUPLOADTIME`
         ➔ **If [true]:**
            1. **JavaCallAction**
            2. 🔀 **DECISION:** `false`
               ➔ **If [false]:**
                  1. **Call Microflow **EcoATM_EB.SUB_ReserveBid_DeleteAll****
                  2. **JavaCallAction**
                  3. **Call Microflow **EcoATM_EB.SCH_UpdateEBPrice****
                  4. 🔀 **DECISION:** `$ReserveBidSync !=empty`
                     ➔ **If [true]:**
                        1. **Update **$ReserveBidSync** (and Save to DB)
      - Set **LastSyncDateTime** = `$MaxuploadTime/MAXUPLOADTIME`**
                        2. 🏁 **END:** Return empty
                     ➔ **If [false]:**
                        1. **Create **EcoATM_EB.ReserveBidSync** (Result: **$NewReserveBidSync**)
      - Set **LastSyncDateTime** = `$MaxuploadTime/MAXUPLOADTIME`**
                        2. 🏁 **END:** Return empty
               ➔ **If [true]:**
                  1. **DB Retrieve **EcoATM_EB.ReserveBid**  (Result: **$ReserveBidList**)**
                  2. **Delete**
                  3. **JavaCallAction**
                  4. **Call Microflow **EcoATM_EB.SCH_UpdateEBPrice****
                  5. 🔀 **DECISION:** `$ReserveBidSync !=empty`
                     ➔ **If [true]:**
                        1. **Update **$ReserveBidSync** (and Save to DB)
      - Set **LastSyncDateTime** = `$MaxuploadTime/MAXUPLOADTIME`**
                        2. 🏁 **END:** Return empty
                     ➔ **If [false]:**
                        1. **Create **EcoATM_EB.ReserveBidSync** (Result: **$NewReserveBidSync**)
      - Set **LastSyncDateTime** = `$MaxuploadTime/MAXUPLOADTIME`**
                        2. 🏁 **END:** Return empty
         ➔ **If [false]:**
            1. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.