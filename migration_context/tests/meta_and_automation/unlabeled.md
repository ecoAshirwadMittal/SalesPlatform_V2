# Unlabeled — Test Cases

> **617** test cases with label `Unlabeled`.

> Source: Jira project **SPKB** (issuetype = Test)

---

### SPKB-796 — [Outdated] PWS Offer | Sales Action - Verify Complete-Review Submission Not Allow If Offer-Action Status is Blank

- **Status**: Done
- **Priority**: P2
- **Linked Issues**: SPKB-450

### SPKB-1419 — Order History | Verify Sorting by Recent Update Date

- **Status**: Done
- **Priority**: P2
- **Linked Issues**: SPKB-1406

### SPKB-1465 — PWS Email: Pending Order | Verify Alert Email Sent to Sales Rep when Oracle Order Creation Failure

- **Status**: Done
- **Priority**: P2
- **Linked Issues**: SPKB-1366

**Description:**
Sales Reps
Order Creation Flows
 | Action By
 | isOfferAboveListPrice
 | isOfferExceedAvailQty
 | Email (Order Created)
 | If Order is Pending,
Expected Email 
 | Actual Result
 |  | - Buy Now
 | Buyer
 | Y
 | Y (If Avail Qty is 100+)
 | Adjust Qty Email → Buyer
 | Alert Email → Sales Rep
 | PASSED
 |  | - Buy Now
 | Buyer
 | Y
 | N 
 | Order Submitted Email → Buyer
 | Alert Email → Sales Rep
 | PASSED
 |  | - Finalize Order Cart Page
 | Sales Rep
 | n/a
 | n/a
 | Order Submitted Email → Buyer
 | Alert Email → Sales Rep
 | PASSED
 |  | - Sales Review:
- At least 1 SKU Accepted/ Finalized
- No Countering SKU
 | Sales Rep
 | n/a
 | n/a
 | Order Submitted Email → Buyer
 | Alert Email → Sales Rep
 |   Email Not Sent 
 |  | - Buyer-Acceptance
- At least 1 countered-SKU Accepted
 | Buyer

 | n/a
 | n/a
 | Order Submitted Email → Buyer
 | Alert Email → Sales Rep
 | Email Not Sent
 |  |

### SPKB-1529 — PWS Inventory Page | Verify Column Name Renamed as Per New Design

- **Status**: Done
- **Priority**: P2
- **Linked Issues**: SPKB-1384

### SPKB-1542 — PWS Deposco | Verify ATP Inventory - Buyer Submit Order (Buy Now) 

- **Status**: Done
- **Priority**: P2
- **Linked Issues**: SPKB-733

### SPKB-1543 — PWS Deposco | Verify ATP Inventory - Buyer Submit Order (Buy Now with Adjusted Qty) 

- **Status**: Done
- **Priority**: P2
- **Linked Issues**: SPKB-733

### SPKB-1544 — PWS Deposco | Verify ATP Inventory - Sales Rep Completes Review (Initial Accepted)

- **Status**: Done
- **Priority**: P2
- **Linked Issues**: SPKB-733

### SPKB-1545 — PWS Deposco | Verify ATP Inventory - Sales Rep Completes Review (Initial Countered)

- **Status**: Done
- **Priority**: P2
- **Linked Issues**: SPKB-733

### SPKB-1546 — PWS Deposco | Verify ATP Inventory - Sales Rep Completes Review (Accepted -> Countered)

- **Status**: Done
- **Priority**: P2
- **Linked Issues**: SPKB-733

### SPKB-1547 — PWS Deposco | Verify ATP Inventory - Sales Rep Completes Review (Accepted -> Finalized)

- **Status**: Done
- **Priority**: P2
- **Linked Issues**: SPKB-733

### SPKB-1548 — PWS Deposco | Verify ATP Inventory - Sales Rep Completes Review (Accepted -> Declined)

- **Status**: Done
- **Priority**: P2
- **Linked Issues**: SPKB-733

### SPKB-1549 — PWS Deposco | Verify ATP Inventory - Sales Rep Completes Review (Countered -> Accepted)

- **Status**: Done
- **Priority**: P2
- **Linked Issues**: SPKB-733

### SPKB-1550 — PWS Deposco | Verify ATP Inventory - Sales Rep Completes Review (Countered -> Finalized)

- **Status**: Done
- **Priority**: P2
- **Linked Issues**: SPKB-733

### SPKB-1728 — Account | Verify Admin Can Create New User and Buyer

- **Status**: Done
- **Priority**: P2
- **Linked Issues**: SPKB-1718

### SPKB-1729 — Account | Verify Buyer Can Activate Account and Login

- **Status**: Done
- **Priority**: P2
- **Linked Issues**: SPKB-1718

### SPKB-1730 — Account | Verify New User and Buyer Data Sent to Snowflake

- **Status**: Done
- **Priority**: P2
- **Linked Issues**: SPKB-1718

### SPKB-1746 — RMA | Verify Instruction Drawer under MoreAction

- **Status**: Done
- **Priority**: P2
- **Linked Issues**: SPKB-1489

### SPKB-1747 — RMA | Verify Return Policy Link under MoreAction

- **Status**: Done
- **Priority**: P2
- **Linked Issues**: SPKB-1489

### SPKB-1751 — RMA | Verify Complete-Review Validation on Missing Inline Return Status

- **Status**: Done
- **Priority**: P2
- **Linked Issues**: SPKB-1497

### SPKB-1752 — RMA | Verify Approve-All and Decline All from More-Action Menu

- **Status**: Done
- **Priority**: P2
- **Linked Issues**: SPKB-1497

### SPKB-1753 — RMA | Verify Inline Action Option Dropdown

- **Status**: Done
- **Priority**: P2
- **Linked Issues**: SPKB-1497

### SPKB-1754 — RMA | Verify RMA Confirmation Modal Display Upon Completing Review

- **Status**: Done
- **Priority**: P2
- **Linked Issues**: SPKB-1497

### SPKB-1755 — RMA | Verify Download from More-Action Menu

- **Status**: Done
- **Priority**: P2
- **Linked Issues**: SPKB-1497

### SPKB-1756 — RMA | Validate IMEI Statuses after Completing Review both Approved and Declined

- **Status**: Done
- **Priority**: P2
- **Linked Issues**: SPKB-1497

### SPKB-1757 — RMA | Validate Approved RMA Summary Box Reflects Inline Status Change

- **Status**: Done
- **Priority**: P2
- **Linked Issues**: SPKB-1497

### SPKB-1758 — RMA Detail Page - Sales View | Validate Return Information on Header

- **Status**: Done
- **Priority**: P2
- **Linked Issues**: SPKB-1496

### SPKB-1759 — RMA Detail Page - Sales View | Validate RMA Statuses

- **Status**: Done
- **Priority**: P2
- **Linked Issues**: SPKB-1496

### SPKB-1760 — RMA Detial Page - Sales View | Verify Download Filename and Format

- **Status**: Done
- **Priority**: P2
- **Linked Issues**: SPKB-1496

### SPKB-1762 — RMA Detail Page - Buyer View | Validate Return Information on Header

- **Status**: Done
- **Priority**: P2
- **Linked Issues**: SPKB-1493

### SPKB-1763 — RMA Detail Page - Buyer View | Validate Values in Summary Boxes

- **Status**: Done
- **Priority**: P2
- **Linked Issues**: SPKB-1493

### SPKB-1764 — RMA Detail Page - Buyer View | Validate Data Grid and Values

- **Status**: Done
- **Priority**: P2
- **Linked Issues**: SPKB-1493

### SPKB-1765 — RMA Detail Page - Buyer View | Verify Download Filename and Format

- **Status**: Done
- **Priority**: P2
- **Linked Issues**: SPKB-1493

### SPKB-1779 — Snowflake | Validate Order Status Change and Timestamp on PWS_OFFER

- **Status**: Done
- **Priority**: P2
- **Linked Issues**: SPKB-1649

### SPKB-1780 — Snowflake | Validate Offer Status Change and Timestamp on PWS_OFFER_ITEM

- **Status**: Done
- **Priority**: P2
- **Linked Issues**: SPKB-1649

### SPKB-1781 — Snowflake | Validate Shipment Qty and IMEI on PWS_ORDER_IMEI

- **Status**: Done
- **Priority**: P2
- **Linked Issues**: SPKB-1649

### SPKB-1789 — RMA | Verify "Print Return Label" Display Only on RMA Status "Approved to Ship"

- **Status**: Done
- **Priority**: P2
- **Linked Issues**: SPKB-1494

### SPKB-1790 — RMA | Verify Approved Tab Display as Default on Partially Approved Items

- **Status**: Done
- **Priority**: P2
- **Linked Issues**: SPKB-1494

### SPKB-1791 — RMA | Validate IMEI on Partially Approved Return Items

- **Status**: Done
- **Priority**: P2
- **Linked Issues**: SPKB-1494

### SPKB-1792 — RMA | Verfiy Clicking "Print Return Label" Opens New Tab and Display RMA Label

- **Status**: Done
- **Priority**: P2
- **Linked Issues**: SPKB-1494

### SPKB-1793 — RMA | Validate RMA Number on the Label

- **Status**: Done
- **Priority**: P2
- **Linked Issues**: SPKB-1494

### SPKB-1824 — Inventory Page | Verify "By Device" Tab Display Unselected Buyer Code as Default

- **Status**: Done
- **Priority**: P2
- **Linked Issues**: SPKB-1500

### SPKB-1825 — Inventory Page | Verify "By Device" Tab Display 3 Active Columns after Selected Buyer Code

- **Status**: Done
- **Priority**: P2
- **Linked Issues**: SPKB-1500

### SPKB-1827 — Auction Inventory | Validate Avail Qty on Auction Table after Adding DW Additional Quantity

- **Status**: Done
- **Priority**: P2
- **Linked Issues**: SPKB-1784

### SPKB-1828 — Auction Inventory | Validate Avail Qty on Auction Table after Adding Non-DW Additional Quantity

- **Status**: Done
- **Priority**: P2
- **Linked Issues**: SPKB-1784

### SPKB-1837 — Case Lot | Verify Step Function's Response Should Not Return Error for New Case-Lot SKU 

- **Status**: Done
- **Priority**: P2
- **Linked Issues**: SPKB-1826

### SPKB-1934 — RMA Validation States - Enhanced

- **Status**: Done
- **Priority**: P2
- **Linked Issues**: SPKB-1893

**Description:**
## 🧩 Feature: RMA Request Creation via Excel Upload (PWS Dashboard)
### Module: Orders → Submit RMAs
### User Roles: Buyer / Sales Ops
### Prerequisite:
- User has valid login credentials.
- At least one Sales Order in “Shipped” status is available.
- RMA Excel file (.xls/.xlsx) includes:
- IMEI Number
- Reason Code
## ✅ Acceptance Criteria
- The RMA Request modal text should match:
“Once your RMA request is approved, all devices must be received by ecoATM/Gazelle within 10 business days for domestic buyers and 15 business days for international buyers.”
- The system must validate that:
- The uploaded RMA file has no duplicate IMEIs in the same submission.
- The uploaded file contains both IMEI Number and Reason Code columns.
- The validation message modal and RMA Request submitted modal must follow the provided design mockups.
## 🧪 Test Cases
TC ID
 | Test Case Title
 | Precondition
 | Steps
 | Expected Result
 | Status Type
 |  | TC01
 | Validate RMA Request Modal Text
 | Logged in as Buyer
 | - Navigate to Orders → Submit RMAs.
- Click Request an RMA.
 | Modal text matches exactly:
“Once your RMA request is approved…” per AC.
 | UI Validation
 |  | TC02
 | Validate Upload File Type Restriction
 | File formats ready (.xls, .xlsx, .csv)
 | - Open RMA Request modal.
- Attempt to upload .csv file.
 | System displays validation: “Invalid file format. Supported format: .xlsx”
 | Negative
 |  | TC03
 | Upload RMA File with Valid IMEIs and Reasons
 | Valid Excel file (no duplicates)
 | - Upload file with 2 columns: IMEI, Reason Code.
- Click Submit RMA.
- Wait for progress bar to complete.
 | Modal “Uploading RMA” displayed → then “RMA Request Submitted!” confirmation appears.
Entry visible under RMA Requests grid.
 | Positive
 |  | TC04
 | Upload File with Missing IMEI Column
 | Excel file missing IMEI header
 | - Upload Excel missing IMEI column.
- Click Submit RMA.
 | Modal shows “Your file is incomplete – X items have invalid IMEIs.”
 | Negative
 |  | TC05
 | Upload File with Missing Reason Code Column
 | Excel missing Reason column
 | - Upload Excel missing Reason Code.
- Click Submit RMA.
 | Modal shows “Your file is incomplete – X items are missing a return reason.”
 | Negative
 |  | TC06
 | Validate Duplicate IMEI Detection
 | Excel file contains duplicate IMEIs
 | - Upload Excel with duplicate IMEIs.
- Submit file.
 | Modal “Your file is incomplete” appears with expandable section:
“X items have duplicate IMEIs.”
Copy IMEIs option available.
 | Negative
 |  | TC07
 | Verify “Copy IMEIs” Functionality
 | Duplicate IMEIs present
 | - Expand “Duplicate IMEIs” section.
- Click Copy IMEIs.
 | All IMEIs (including hidden ones) copied to clipboard.
 | UI Functional
 |  | TC08
 | Validate “Start Over” Button Behavior
 | Validation modal displayed
 | - In error modal, click Start Over.
 | User redirected to “Request RMA” modal (Step 1).
 | Navigation
 |  | TC09
 | Validate Loading Modal Behavior
 | Upload in progress
 | - Upload large Excel file.
- Observe progress bar.
 | Modal “Uploading RMA” appears with file name and progress bar until upload completes.
 | UI/Functional
 |  | TC10
 | Validate Successful RMA Submission
 | Valid RMA file uploaded
 | - Upload valid file.
- Confirm “RMA Request Submitted!” message displayed.
 | Success modal per design:
“Do not ship devices. ecoATM will review your request…”
 | Positive
 |  | TC11
 | Validate Request Appears in RMA Requests Grid
 | Successful RMA submission
 | - After confirmation modal, navigate to Review RMAs tab.
- Verify the latest RMA entry.
 | RMA Number, Submit Date, Buyer, and Status = “Submitted” displayed correctly.
 | Regression
 |  | TC12
 | Validate Error Modal Text Formatting
 | Error modal triggered
 | - Trigger validation (invalid IMEI).
- Observe modal styling.
 | Matches design: header icon ⚠️, text “Your file is incomplete,” expandable sections with dropdown arrows.
 | UI Validation
 |  | TC13
 | Verify Domestic vs. International Instruction Text
 | Logged in as Buyer
 | - Open Request RMA modal.
- Read instruction text.
 | Text correctly differentiates “10 business days (domestic)” and “15 business days (international).”
 | UI Validation
 |  | TC14
 | Validate Multiple Error Conditions
 | Excel contains invalid + duplicate + missing reasons
 | - Upload mixed-error file.
- Click Submit RMA.
 | All 3 validation sections appear:
Invalid IMEIs
Duplicate IMEIs
Missing Reasons
 | Negative
 |  | TC15
 | Validate Response Timeout Handling
 | Simulate slow upload
 | - Upload large Excel file on throttled network.
- Observe behavior.
 | System keeps modal open and shows loading indicator without freezing or throwing console errors.
 | 
 |  |

### SPKB-1935 — Auction Admin | Verify Admin Can Manage Qualified Buyer Round 3 and Upload File Sent to SharePoint

- **Status**: Done
- **Priority**: P2
- **Linked Issues**: SPKB-1806

**Description:**
- Check/ Unchecked buyer qualifications after Round 2 ends

### SPKB-2026 — Validate system behavior when duplicate IMEIs are uploaded in the same RMA Excel file

- **Status**: Done
- **Priority**: P2
- **Linked Issues**: SPKB-1885

**Description:**
## 🧩 RMA Duplicate IMEI Validation – Test Case Combinations
#
 | Scenario
 | Test Data Setup
 | Expected Result
 |  | 1
 | ✅ Unique IMEIs in file
 | IMEI1, IMEI2, IMEI3 all different
 | RMA created successfully.
 |  | 2
 | ❌ Duplicate IMEIs within same RMA file
 | IMEI1 repeated twice with different or same return reasons
 | System should block upload and show message “Duplicate IMEI detected in RMA upload.”
 |  | 3
 | ❌ Same IMEI repeated with leading/trailing spaces
 | “IMEI1” and “ IMEI1 ”
 | System should trim spaces and still treat as duplicate. Error displayed.
 |  | 4
 | ❌ Same IMEI in different formats
 | IMEI as text vs number (e.g. '354345294417945 and 354345294417945)
 | System should normalize format and flag as duplicate.
 |  | 5
 | ✅ Duplicate IMEIs across different RMAs (separate uploads)
 | Same IMEI appears in RMA#1 and RMA#2 uploads
 | Allowed. Validation is only per RMA file, not global.
 |  | 6
 | ❌ Same IMEI appears twice due to copy-paste error in Excel
 | Two identical rows
 | Error: “Duplicate IMEI detected.”
 |  | 7
 | ❌ Mixed valid and duplicate IMEIs
 | IMEI1 unique, IMEI2 duplicated
 | Entire file should be blocked until duplicates are removed.
 |  | 8
 | ✅ Same IMEI in different RMA requests by different buyers
 | BuyerA uses IMEI1, BuyerB uses IMEI1
 | Allowed. Validation should be buyer/RMA-specific.
 |  | 9
 | ❌ Duplicate IMEIs separated by blank rows
 | IMEI1 in Row2 and again in Row6
 | Still flagged as duplicate.
 |  | 10
 | ❌ Duplicate IMEIs with different casing or hidden characters
 | IMEI1 and IMEI1(space/unicode)
 | Should still detect duplicate after normalization.
 |  | 11
 | ✅ Duplicate IMEIs across different files uploaded days apart
 | IMEI reused after previous RMA submission
 | Allowed. Validation limited to same upload.
 |  | 12
 | ❌ Duplicate IMEIs with empty “Return Reason” field for one
 | IMEI repeated, one row missing reason
 | Error triggered for duplicate regardless of reason.
 |  | 13
 | ✅ Unique IMEIs but same “Return Reason”
 | Different IMEIs, same return reason
 | Allowed. Reason doesn’t affect uniqueness.
 |  | 14
 | ❌ Duplicate IMEIs but different letter casing in file name or header
 | Same IMEI repeated under “imei/Serial Number” and “IMEI/Serial Number”
 | System should standardize header check and block duplicates.
 |  | 15
 | ❌ Duplicate IMEIs within merged cell or multi-line entry
 | IMEI appears twice due to Excel formatting
 | System should detect duplicate post-parsing.
 |  | 16
 | ❌ Duplicate IMEIs due to manual re-entry during RMA edit
 | User uploads valid file, edits and readds same IMEI
 | Validation should trigger again on edit before saving.
 |  | 


Testing is complete for this one. Working as expected.    

Scenario 1: Duplicate IMEIs within same RMA file
Scenario 2: Same IMEI repeated with leading/trailing spaces
Scenario 3: Same IMEI in different formats
Scenario 4: Same IMEI appears twice due to copy-paste error in Excel
Scenario 5: Mixed valid and duplicate IMEIs
Scenario 6: Duplicate IMEIs across different RMAs (separate uploads)
Test Data: RMA2237925084 RMA2237925083
Scenario 7: Duplicate IMEIs separated by blank rows
Scenario 8: Duplicate IMEIs with empty “Return Reason” field for one

Scenario 9: Unique IMEIs but same “Return Reason”
Test Data: RMA2237925085

### SPKB-2029 — Price to Snowflake | Validate Upload Price for New SKU

- **Status**: Done
- **Priority**: P2
- **Linked Issues**: SPKB-1915

**Description:**
Description:  Upload Excel with new SKU (not existing in Snowflake).
Expected:
New record inserted with:
- StartDate = 2000/01/01
- EndDate = NULL
- FutureListPrice/FutureMinPrice/FutureEffectiveDate populated
- Audit columns set correctly.

### SPKB-2030 — Price to Snowflake | Validate Upload Price for Existing SKU 

- **Status**: Done
- **Priority**: P2
- **Linked Issues**: SPKB-1915

**Description:**
Expected:   New record inserted with updated FutureListPrice/FutureMinPrice/FutureEffectiveDate.

### SPKB-2031 — Price to Snowflake | Validate Upload Excel with Duplicate SKU Rows

- **Status**: Done
- **Priority**: P2
- **Linked Issues**: SPKB-1915

**Description:**
Expected: Sales should not able to upload pricing file

### SPKB-2032 — Price to Snowflake | Verify Activate Future Prices on Effective Date Trigger as Scheduled

- **Status**: Done
- **Priority**: P2
- **Linked Issues**: SPKB-1915

### SPKB-2059 — [Invalid] Case Lot Inventory: Sales View | Download Case Lot File

- **Status**: Done
- **Priority**: P2

**Description:**
This download button will be in a future story

Description: Verify downloaded inventory file matches on-screen data and includes new columns.
Steps:
- Click “Download” button on Inventory Page.
- Open the file.
Expected Result:
- Download includes Case Pack Qty and Case Price columns.
- No editable price/qty columns in file when no buyer selected.
- Data matches what’s displayed on screen.

### SPKB-2285 — Test Case: PWS Order with Multiple Shipments

- **Status**: Done
- **Priority**: P2
- **Linked Issues**: SPKB-2108

**Description:**
### Test Case 1 – PWS Order with Two Shipments – UI Validation on Sales Platform
Objective
Validate that for PWS order 5006831 with two shipments (860 and 861), the Sales Platform UI shows a single aggregated order with correct status, ship date, SKUs, quantities, and tracking links after the order is shipped from Deposco.
#### Step-by-Step
- Open Order History on Sales Platform
- Login as a Sales Platform user.
- Navigate to Orders → Order History.
- Search for Order 5006831 and open Order Details.
Expected Result:
- A single order with number 5006831 is displayed (no duplicate orders).
- Status = Shipped.
- Ship Method = FedEx Ground.
- Ship Date = date corresponding to the earliest shipment (min of 860 & 861 ship dates).
- Verify Ship Summary Aggregation
- In the Ship Summary panel, review SKUs / Qty / Price.
Expected Result:
- SKUs = 3.
- Qty = 3.
- Price = $3,897 (sum of all 3 devices).
- Values are aggregated across both shipments 860 & 861, not split into separate orders.
- Verify Line-level Details (By Device tab)
- On By Device view, review the table of IMEI / SKU / Description / Unit Price / Serial Number / Tracking Number.
Expected Result:
- 3 rows are shown (one per device).
- Each row displays correct:
- IMEI
- SKU (e.g., PWS10013376, PWS10012060, PWS10012064)
- Description text for each device
- Unit Price = $1,299 for each
- Serial Number values (e.g., 5006831_Tab, 5006831_cELLPHONE, etc.)
- Tracking Number populated (e.g., 396355382637, 396355384125, etc.).
- Validate Tracking / Shipment Links
- Click Track Order and/or individual Tracking Number links (if clickable).
Expected Result:
- Tracking opens FedEx (or tracking page) and corresponds to shipments created in Deposco.
- Both shipment tracking numbers are represented across the 3 devices (some devices may share a tracking number if in the same box).
- Backend Consistency (optional but recommended)
- Verify in Oracle/Deposco that:
- Order 5006831 has 2 shipment IDs: 860 & 861.
- Each device IMEI is tied to one of those shipments.
Expected Result:
- All 3 IMEIs from backend match the 3 devices shown on the Sales Platform.
- The combination of shipments 860 & 861 fully accounts for all devices and totals shown on the UI.

### Test Case 2 – PWS Order with Ship Outside System – No Tracking & No Shipping Label
### Objective
To verify that for PWS Sales Order 5006910, when Ship Via = Ship Outside System, the system:
- Creates a Shipment ID (865)
- Sets Order Status = Ship Complete
- Does NOT generate a Shipping Label
- Does NOT populate Tracking Number / Tracking Links
This behavior is expected and correct.
### Preconditions
- Sales Platform is accessible.
- Oracle ERP is accessible.
- PWS Order 5006910 is created.
- Ship Via / Ship Method = Ship Outside System.
- Order is released and shipped from backend.
### Test Data
- Order Number: 5006910
- Order Source: PWS
- Ship Method: Ship Outside System
- Shipment ID: 865
- Devices: 3
- Unit Price: $1299 each
- Total Order Price: $3,897
### Step-by-Step Execution
### Step 1 – Create Sales Order
- Create a PWS Sales Order with Ship Method = Ship Outside System.
- Confirm order number is generated as 5006910.
✅ Expected Result
- Order is successfully created in Sales Platform.
- Ship Method shows Ship Outside System.
### Step 2 – Ship the Order from Backend
- Ship the order via Oracle/WMS.
- Confirm shipment creation.
✅ Expected Result
- Shipment ID = 865 is created.
- Order moves to Ship Complete in Oracle.
### Step 3 – Validate Order in Sales Platform (UI)
- Login to Sales Platform.
- Navigate to Orders → Order History.
- Search and open Order 5006910.
✅ Expected Result
- Order Status = Shipped
- Ship Method = Ship Outside System
- Ship To Address = null, null, null ✅ (as shown in UI)
- Ship Summary shows:
- SKUs = 3
- Qty = 3
- Price = $3,897
### Step 4 – Validate Tracking in Sales Platform (By Device View)
- Open By Device tab.
- Review the Tracking Number column.
✅ Expected Result
- Tracking Number is EMPTY for all 3 devices
- Track Order button exists but does not navigate to any carrier
- This is EXPECTED behavior for Ship Outside System
### Step 5 – Validate in Oracle (Backend)
- Open Oracle → Sales Order 5006910.
- Review the Header Info.
✅ Expected Result
- Order Status = Ship Complete
- Shipping Status = Shipped
- Shipment ID = 865 is present
- Shipping Label = EMPTY
- Tracking Link(s) = EMPTY
- Order Source = PWS
- Parcel/Freight = Parcel
## Final Expected Outcome (Pass Criteria)
✅ Shipment ID 865 is created
✅ Order moves to Ship Complete / Shipped
✅ Shipping Label is EMPTY
✅ Tracking Link is EMPTY
✅ Sales Platform correctly shows:
- No tracking numbers
- No carrier tracking links
- Correct ship summary aggregation
✅ Behavior matches Ship Outside System business rule
## Test Status
🟢 PASS – Expected Behavior Confirmed
## Test Case 3 – Verify Cancelled Order Line Is Not Displayed on Sales Platform
### Objective
Validate that for PWS order 5006908, when one order line is cancelled in Deposco, the Sales Platform UI displays only the remaining completed devices, and the cancelled SKU is not visible after the order is shipped.
### Step-by-Step
### Open Order History on Sales Platform
Login as a Sales Platform user.
Navigate to Orders → Order History.
Search for Order 5006908 and open Order Details.
Expected Result:
Order number 5006908 is displayed.
Status = Shipped.
Ship Method = Ship Outside System.
Ship Date = 12/08/2025.
### Verify Deposco Order Line Cancellation
Login to Deposco.
Navigate to Orders → Sales Orders.
Search for Order 5006908 and open the Order Lines tab.
Expected Result:
Total Order Lines = 3.
Order Line 5006908-3 with SKU PWS10013466 shows Status = Canceled.
Order Lines 5006908-2 (PWS10010972) and 5006908-1 (PWS10012517) show Status = Complete.
Cancelled line has:
Allocated Qty = 0
Picked Qty = 0
### Verify Ship Summary on Sales Platform
In the Ship Summary panel, review SKUs / Qty / Price.
Expected Result:
SKUs = 2
Qty = 2
Price = $2,598
Only completed devices are included in the total.
Cancelled SKU PWS10013466 is NOT included in the summary.
### Verify Line-level Details (By Device tab)
On By Device view, review the table of
IMEI / SKU / Description / Unit Price / Serial Number / Tracking Number.
Expected Result:
Exactly 2 rows are displayed (one per completed device).
Each row displays correct:
IMEI
SKU =
- PWS10012517
- PWS10010972
Correct device description for each SKU.
Unit Price = $1,299 for each device.
Serial Numbers populated (e.g., 5006908_tab, 5006908_cELL).
Tracking Number populated for both devices.
Cancelled SKU PWS10013466 is NOT displayed in this list.
### Validate That Cancelled Device Is Excluded
Confirm that no row exists on the Sales Platform UI for:
SKU = PWS10013466
Order Line = 5006908-3
Expected Result:
Cancelled device is completely hidden from the Sales Platform UI.
Customer only sees 2 shipped devices.
### Backend Consistency (optional but recommended)
Verify in Deposco / Oracle that:
Order 5006908 contains 3 order lines.
Only 2 lines are marked Complete and Shipped.
Cancelled line 5006908-3 is not associated with any shipment.
Expected Result:
Only completed lines are tied to shipment records.
Backend state fully matches what is displayed on the Sales Platform UI.

### SPKB-2426 — Auction R3 | Verify R3 File Exclude Buyer PO SKUs

- **Status**: Done
- **Priority**: P2
- **Linked Issues**: SPKB-2136

**Description:**
Given a buyer has an active PO containing specific SKUs (ecoID-Grade)
And Round 3 (R3) bid file is generated for the same buyer code
When the R3 bid file is created
Then SKUs (ecoID-Grade) that exist in the active PO are excluded from the R3 bid file
And only eligible SKUs are included

### SPKB-2427 — Auction R3 | Verify R3 File Include SKUs When No Active PO Exists

- **Status**: Done
- **Priority**: P2
- **Linked Issues**: SPKB-2136

**Description:**
Given a buyer does not have an active PO for certain SKUs
When the R3 bid file is generated
Then those SKUs (ecoID-Grade) are included in the R3 bid file
And no SKUs are incorrectly removed

### SPKB-2547 — R2: Regular DW Buyer Can View Correct R2 Target Price 

- **Status**: Done
- **Priority**: P2

### SPKB-2548 — R2: Regular DW Buyer (R1 No Bid) Can Submit Bids on DataGrid

- **Status**: Done
- **Priority**: P2

### SPKB-2549 — R2: Regular DW Can See Correct Bid Ranking on DataGrid and Export File

- **Status**: Done
- **Priority**: P2

### SPKB-2550 — R2: Regular DW Buyer Can See Minimum Bid Validation Popup on Bid Submission

- **Status**: Done
- **Priority**: P2

### SPKB-2551 — R2: Regular DW Buyer (R1 No Bid) Can Re-Submit Bids on Data Grid

- **Status**: Done
- **Priority**: P2

### SPKB-2552 — R2: Regular DW Buyer (R1 No Bid) Can Re-Access Auction without Losing Unsubmitted Bids

- **Status**: Done
- **Priority**: P2

### SPKB-2553 — R2: Regular DW Buyer (R1 No Bid) Can Export, Import File and Submit Bids

- **Status**: Done
- **Priority**: P2

### SPKB-2554 — R2: Regular DW Buyer (R1 No Bid) Can Re-Import File Before Submission

- **Status**: Done
- **Priority**: P2

### SPKB-2555 — R2: Regular DW Buyer (R1 No Bid) Can Re-Import File After Submission

- **Status**: Done
- **Priority**: P2

### SPKB-2556 — R2: Regular DW Buyer (R1 Bid) Can Access and View Info on Dashboard

- **Status**: Done
- **Priority**: P2

### SPKB-2557 — R2: Regular DW Buyer (R1 Bid) Can View Correct Avail Qty Cap

- **Status**: Done
- **Priority**: P2

### SPKB-2558 — R2: Regular DW Buyer (R1 Bid) Can View Correct Target Price

- **Status**: Done
- **Priority**: P2

### SPKB-2559 — R2: Regular DW Buyer (R1 Bid) Can Submit Bids New Bids on Data Grid

- **Status**: Done
- **Priority**: P2

### SPKB-2560 — R2: Regular DW Buyer (R1 Bid) Can Re-Submit Bids on Data Grid

- **Status**: Done
- **Priority**: P2

### SPKB-2561 — R2: Regular DW Buyer (R1 Bid) Can Export, Import File and Submit Bids

- **Status**: Done
- **Priority**: P2

### SPKB-2562 — R2: Regular DW Buyer (R1 Bid) Can Re-Import File and Re-Submit Bids

- **Status**: Done
- **Priority**: P2

### SPKB-2563 — R2-R3: Regular DW Buyer Cannot Access Auction R2 after Round 2 Ended

- **Status**: Done
- **Priority**: P2

### SPKB-2564 — R2-R3: Regular DW Buyer Can Download Submitted Bids File after Round 2 Ended

- **Status**: Done
- **Priority**: P2

### SPKB-2565 — R2-R3: Admin Can Submit on Behalf of Regular DW Buyer

- **Status**: Done
- **Priority**: P2

### SPKB-2566 — R2-R3: Admin Can Re-Submit Bids on Behalf of Regular DW Buyer

- **Status**: Done
- **Priority**: P2

### SPKB-2567 — R2-R3: Admin Can Delete Submitted R2 Bids of Regular DW Buyer

- **Status**: Done
- **Priority**: P2

### SPKB-2568 — R3: Admin Can See Regular DW Buyer (R1, R2 Bids) Display on R3-Report Page 

- **Status**: Done
- **Priority**: P2

### SPKB-2569 — R3: Admin Cannot See Regular DW Buyer (R1, R2 NO Bids) Display on R3-Report Page

- **Status**: Done
- **Priority**: P2

### SPKB-2570 — R3: Admin Can View Correct Bid Data of Regular DW Buyer on "View Bid Data" Screen

- **Status**: Done
- **Priority**: P2

### SPKB-2571 — R3: Regular DW Buyer's Bid Data Has Correct Column Header and Formats

- **Status**: Done
- **Priority**: P2

### SPKB-2572 — R3: Admin Can See Correct Bid Ranking on Bid Data of Regular DW 

- **Status**: Done
- **Priority**: P2

### SPKB-2573 — R3: Admin Can Download and View Correct Bid Data from the Regular DW File

- **Status**: Done
- **Priority**: P2

### SPKB-2574 — R3: Bid Data Contains Only Submitted Bids from R1 and R2 of Regular DW File

- **Status**: Done
- **Priority**: P2

### SPKB-2575 — R3: Admin Can Upload Bid File on Behalf of Regular DW Buyer

- **Status**: Done
- **Priority**: P2

### SPKB-2576 — R3: Admin Can Re-Upload File on Behalf of Regular DW Buyer

- **Status**: Done
- **Priority**: P2

### SPKB-2577 — R1: Special Non-DW Buyer Receive Email - Round One Has Open

- **Status**: Done
- **Priority**: P2

### SPKB-2578 — R1: Special Non-DW Buyer Can View Auction Info on Dashboard

- **Status**: Done
- **Priority**: P2

### SPKB-2579 — R1: Special Non-DW Can View Correct Format, Column Header on Data Grid and Excel File

- **Status**: Done
- **Priority**: P2

### SPKB-2580 — R1: Special Non-DW Buyer Can View Correct Non-DW Inventory and Total Qty

- **Status**: Done
- **Priority**: P2

### SPKB-2581 — R1: Special Non-DW Buyer Can View Correct Non-DW Target Price

- **Status**: Done
- **Priority**: P2

### SPKB-2582 — R1: Special Non-DW Can Carry Over Bids from Previous Week

- **Status**: Done
- **Priority**: P2

### SPKB-2583 — R1: Special Non-DW Buyer Can Submit Bids on Data Grid

- **Status**: Done
- **Priority**: P2

### SPKB-2584 — R1: Special Non-DW Buyer Can See Minimum Bid Validation Popup on Bid Submission

- **Status**: Done
- **Priority**: P2

### SPKB-2585 — R1: Special Non-DW Buyer Can Lower Bids and Re-Submit on Data Grid

- **Status**: Done
- **Priority**: P2

### SPKB-2586 — R1: Special Non-DW Buyer Can Re-Access Auction without Losing Unsubmitted Bids

- **Status**: Done
- **Priority**: P2

### SPKB-2587 — R1: Special Non-DW Buyer Can Export, Import File and Submit Bids

- **Status**: Done
- **Priority**: P2

### SPKB-2588 — R1: Special Non-DW Buyer Can Re-Import File with Higher Bids

- **Status**: Done
- **Priority**: P2

### SPKB-2589 — R1: Special Non-DW Buyer Can Re-Import File with Lower Bids

- **Status**: Done
- **Priority**: P2

### SPKB-2590 — R1: Special Non-DW Buyer Receive Email - "Round 1 is Closing" 3 Times If They Haven't Submit Bid Yet

- **Status**: Done
- **Priority**: P2

### SPKB-2591 — R1-R2: Special Non-DW Buyer Cannot Access Auction Round 1

- **Status**: Done
- **Priority**: P2

### SPKB-2592 — R1-R2: Special Non-DW Buyer Can Download Submitted Bids after Round 1 Ended

- **Status**: Done
- **Priority**: P2

### SPKB-2593 — R1-R2: Admin Can Submit on Behalf of Special Non-DW Buyer

- **Status**: Done
- **Priority**: P2

### SPKB-2594 — R1-R2: Admin Can Re-Submit Bids on Behalf of Special Non-DW Buyer

- **Status**: Done
- **Priority**: P2

### SPKB-2595 — R1-R2: Admin Can Delete Submitted R1 Bid of Special Non-DW Buyer

- **Status**: Done
- **Priority**: P2

### SPKB-2596 — R1-R2: Admin Can Manually Unqualified Special Non-DW Buyer to Participate Auction Round 2

- **Status**: Done
- **Priority**: P2

### SPKB-2597 — R1-R2: Admin Can Manually Qualified Special Non-DW Buyer to Participate Auction Round 2

- **Status**: Done
- **Priority**: P2

### SPKB-2598 — R2: Only Qualified Special Non-DW Receive Email - "Round-2 Open"

- **Status**: Done
- **Priority**: P2

### SPKB-2599 — R2: Qualified Special Non-DW Can Access and View Auction Info on Dashboard Regardless of R1-Bid Submission

- **Status**: Done
- **Priority**: P2

### SPKB-2600 — R2: Special Non-DW Buyer Can View Non-DW Inventory with Total Qty

- **Status**: Done
- **Priority**: P2

### SPKB-2601 — R2: Special Non-DW Buyer Can View Correct R2 Target Price 

- **Status**: Done
- **Priority**: P2

### SPKB-2602 — R2: Special Non-DW Buyer (R1 No Bid) Can Submit Bids on DataGrid

- **Status**: Done
- **Priority**: P2

### SPKB-2603 — R2: Special Non-DW Can See Correct Bid Ranking on DataGrid and Export File

- **Status**: Done
- **Priority**: P2

### SPKB-2604 — R2: Special Non-DW Buyer Can See Minimum Bid Validation Popup on Bid Submission

- **Status**: Done
- **Priority**: P2

### SPKB-2605 — R2: Special Non-DW Buyer (R1 No Bid) Can Re-Submit Bids on Data Grid

- **Status**: Done
- **Priority**: P2

### SPKB-2606 — R2: Special Non-DW Buyer (R1 No Bid) Can Re-Access Auction without Losing Unsubmitted Bids

- **Status**: Done
- **Priority**: P2

### SPKB-2607 — R2: Special Non-DW Buyer (R1 No Bid) Can Export, Import File and Submit Bids

- **Status**: Done
- **Priority**: P2

### SPKB-2608 — R2: Special Non-DW Buyer (R1 No Bid) Can Re-Import File Before Submission

- **Status**: Done
- **Priority**: P2

### SPKB-2609 — R2: Special Non-DW Buyer (R1 No Bid) Can Re-Import File After Submission

- **Status**: Done
- **Priority**: P2

### SPKB-2610 — R2: Special Non-DW Buyer (R1 Bid) Can Access and View Info on Dashboard

- **Status**: Done
- **Priority**: P2

### SPKB-2611 — R2: Special Non-DW Buyer (R1 Bid) Can View Correct Avail Qty Cap

- **Status**: Done
- **Priority**: P2

### SPKB-2612 — R2: Special Non-DW Buyer (R1 Bid) Can View Correct Target Price

- **Status**: Done
- **Priority**: P2

### SPKB-2613 — R2: Special Non-DW Buyer (R1 Bid) Can Submit Bids New Bids on Data Grid

- **Status**: Done
- **Priority**: P2

### SPKB-2614 — R2: Special Non-DW Buyer (R1 Bid) Can Re-Submit Bids on Data Grid

- **Status**: Done
- **Priority**: P2

### SPKB-2615 — R2: Special Non-DW Buyer (R1 Bid) Can Export, Import File and Submit Bids

- **Status**: Done
- **Priority**: P2

### SPKB-2616 — R2: Special Non-DW Buyer (R1 Bid) Can Re-Import File and Re-Submit Bids

- **Status**: Done
- **Priority**: P2

### SPKB-2617 — R2-R3: Special Non-DW Buyer Cannot Access Auction R2 after Round 2 Ended

- **Status**: Done
- **Priority**: P2

### SPKB-2618 — R2-R3:Special Non-DW Buyer Can Download Submitted Bids File after Round 2 Ended

- **Status**: Done
- **Priority**: P2

### SPKB-2619 — R2-R3: Admin Can Submit on Behalf of Special Non-DW Buyer

- **Status**: Done
- **Priority**: P2

### SPKB-2620 — R2-R3: Admin Can Re-Submit Bids on Behalf of Special Non-DW Buyer

- **Status**: Done
- **Priority**: P2

### SPKB-2621 — R2-R3: Admin Can Delete Submitted R2 Bids of Special Non-DW Buyer

- **Status**: Done
- **Priority**: P2

### SPKB-2622 — R3: Admin Can See Special Non-DW Buyer (R1, R2 Bids) Display on R3-Report Page 

- **Status**: Done
- **Priority**: P2

### SPKB-2623 — R3: Admin Cannot See Special Non-DW Buyer (R1, R2 NO Bids) Display on R3-Report Page

- **Status**: Done
- **Priority**: P2

### SPKB-2624 — R3: Admin Can View Correct Bid Data of Special Non-DW Buyer on "View Bid Data" Screen

- **Status**: Done
- **Priority**: P2

### SPKB-2625 — R3: Special Non-DW Buyer's Bid Data Has Correct Column Header and Formats

- **Status**: Done
- **Priority**: P2

### SPKB-2626 — R3: Admin Can See Correct Bid Ranking on Bid Data of Special Non-DW 

- **Status**: Done
- **Priority**: P2

### SPKB-2627 — R3: Admin Can Download and View Correct Bid Data from the Special Non-DW File

- **Status**: Done
- **Priority**: P2

### SPKB-2628 — R3: Bid Data Contains Only Submitted Bids from R1 and R2 of Special Non-Dw Buyer

- **Status**: Done
- **Priority**: P2

### SPKB-2629 — R3: Admin Can Upload Bid File on Behalf of Special Non-DW Buyer

- **Status**: Done
- **Priority**: P2

### SPKB-2630 — R3: Admin Can Re-Upload File on Behalf of Special Non-DW Buyer

- **Status**: Done
- **Priority**: P2

### SPKB-2632 — R1: Special DW Buyer Receive Email - Round One Has Open

- **Status**: Done
- **Priority**: P2

### SPKB-2633 — R1: Special DW Buyer Can View Auction Info on Dashboard

- **Status**: Done
- **Priority**: P2

### SPKB-2634 — R1: Special DW Can View Correct Format, Column Header on Data Grid and Excel File

- **Status**: Done
- **Priority**: P2

### SPKB-2635 — R1: Special DW Buyer Can View Correct DW Inventory and DW Qty

- **Status**: Done
- **Priority**: P2

### SPKB-2636 — R1: Special DW Buyer Can View Correct DW Target Price

- **Status**: Done
- **Priority**: P2

### SPKB-2637 — R1: Special DW Can Carry Over Bids from Previous Week

- **Status**: Done
- **Priority**: P2

### SPKB-2638 — R1: Special DW Buyer Can Submit Bids on Data Grid

- **Status**: Done
- **Priority**: P2

### SPKB-2639 — R1: Special DW Buyer Can See Minimum Bid Validation Popup on Bid Submission

- **Status**: Done
- **Priority**: P2

### SPKB-2640 — R1: Special DW Buyer Can Lower Bids and Re-Submit on Data Grid

- **Status**: Done
- **Priority**: P2

### SPKB-2641 — R1: Special DW Buyer Can Re-Access Auction without Losing Unsubmitted Bids

- **Status**: Done
- **Priority**: P2

### SPKB-2642 — R1: Special DW Buyer Can Export, Import File and Submit Bids

- **Status**: Done
- **Priority**: P2

### SPKB-2643 — R1: Special DW Buyer Can Re-Import File with Higher Bids

- **Status**: Done
- **Priority**: P2

### SPKB-2644 — R1: Special DW Buyer Can Re-Import File with Lower Bids

- **Status**: Done
- **Priority**: P2

### SPKB-2645 — R1: Special DW Buyer Receive Email - "Round 1 is Closing" 3 Times If They Haven't Submit Bid Yet

- **Status**: Done
- **Priority**: P2

### SPKB-2646 — R1-R2: Special DW Buyer Cannot Access Auction Round 1

- **Status**: Done
- **Priority**: P2

### SPKB-2647 — R1-R2: Special DW Buyer Can Download Submitted Bids after Round 1 Ended

- **Status**: Done
- **Priority**: P2

### SPKB-2648 — R1-R2: Admin Can Submit on Behalf of Special DW Buyer

- **Status**: Done
- **Priority**: P2

### SPKB-2649 — R1-R2: Admin Can Re-Submit Bids on Behalf of Special DW Buyer

- **Status**: Done
- **Priority**: P2

### SPKB-2650 — R1-R2: Admin Can Delete Submitted R1 Bid of Special DW Buyer

- **Status**: Done
- **Priority**: P2

### SPKB-2651 — R1-R2: Admin Can Manually Unqualified Special DW Buyer to Participate Auction Round 2

- **Status**: Done
- **Priority**: P2

### SPKB-2652 — R1-R2: Admin Can Manually Qualified Special DW Buyer to Participate Auction Round 2

- **Status**: Done
- **Priority**: P2

### SPKB-2653 — R2: Only Qualified Special DW Receive Email - "Round-2 Open"

- **Status**: Done
- **Priority**: P2

### SPKB-2654 — R2: Qualified Special DW Can Access and View Auction Info on Dashboard Regardless of R1-Bid Submission

- **Status**: Done
- **Priority**: P2

### SPKB-2655 — R2: Special DW Buyer Can View Non-DW Inventory with Total Qty

- **Status**: Done
- **Priority**: P2

### SPKB-2656 — R2: Special DW Buyer Can View Correct R2 Target Price 

- **Status**: Done
- **Priority**: P2

### SPKB-2657 — R2: Special DW Buyer (R1 No Bid) Can Submit Bids on DataGrid

- **Status**: Done
- **Priority**: P2

### SPKB-2658 — R2: Special DW Can See Correct Bid Ranking on DataGrid and Export File

- **Status**: Done
- **Priority**: P2

### SPKB-2659 — R2: Special DW Buyer Can See Minimum Bid Validation Popup on Bid Submission

- **Status**: Done
- **Priority**: P2

### SPKB-2660 — R2: Special DW Buyer (R1 No Bid) Can Re-Submit Bids on Data Grid

- **Status**: Done
- **Priority**: P2

### SPKB-2661 — R2: Special DW Buyer (R1 No Bid) Can Re-Access Auction without Losing Unsubmitted Bids

- **Status**: Done
- **Priority**: P2

### SPKB-2662 — R2: Special DW Buyer (R1 No Bid) Can Export, Import File and Submit Bids

- **Status**: Done
- **Priority**: P2

### SPKB-2663 — R2: Special DW Buyer (R1 No Bid) Can Re-Import File Before Submission

- **Status**: Done
- **Priority**: P2

### SPKB-2664 — R2: Special DW Buyer (R1 No Bid) Can Re-Import File After Submission

- **Status**: Done
- **Priority**: P2

### SPKB-2665 — R2: Special DW Buyer (R1 Bid) Can Access and View Info on Dashboard

- **Status**: Done
- **Priority**: P2

### SPKB-2666 — R2: Special DW Buyer (R1 Bid) Can View Correct Avail Qty Cap

- **Status**: Done
- **Priority**: P2

### SPKB-2667 — R2: Special DW Buyer (R1 Bid) Can View Correct Target Price

- **Status**: Done
- **Priority**: P2

### SPKB-2668 — R2: Special DW Buyer (R1 Bid) Can Submit Bids New Bids on Data Grid

- **Status**: Done
- **Priority**: P2

### SPKB-2669 — R2: Special DW Buyer (R1 Bid) Can Re-Submit Bids on Data Grid

- **Status**: Done
- **Priority**: P2

### SPKB-2670 — R2: Special DW Buyer (R1 Bid) Can Export, Import File and Submit Bids

- **Status**: Done
- **Priority**: P2

### SPKB-2671 — R2: Special DW Buyer (R1 Bid) Can Re-Import File and Re-Submit Bids

- **Status**: Done
- **Priority**: P2

### SPKB-2672 — R2-R3: Special DW Buyer Cannot Access Auction R2 after Round 2 Ended

- **Status**: Done
- **Priority**: P2

### SPKB-2673 — R2-R3:Special DW Buyer Can Download Submitted Bids File after Round 2 Ended

- **Status**: Done
- **Priority**: P2

### SPKB-2674 — R2-R3: Admin Can Submit on Behalf of Special DW Buyer

- **Status**: Done
- **Priority**: P2

### SPKB-2675 — R2-R3: Admin Can Re-Submit Bids on Behalf of Special DW Buyer

- **Status**: Done
- **Priority**: P2

### SPKB-2676 — R2-R3: Admin Can Delete Submitted R2 Bids of Special DW Buyer

- **Status**: Done
- **Priority**: P2

### SPKB-2677 — R3: Admin Can See Special DW Buyer (R1, R2 Bids) Display on R3-Report Page 

- **Status**: Done
- **Priority**: P2

### SPKB-2678 — R3: Admin Cannot See Special DW Buyer (R1, R2 NO Bids) Display on R3-Report Page

- **Status**: Done
- **Priority**: P2

### SPKB-2679 — R3: Admin Can View Correct Bid Data of Special DW Buyer on "View Bid Data" Screen

- **Status**: Done
- **Priority**: P2

### SPKB-2680 — R3: Special DW Buyer's Bid Data Has Correct Column Header and Formats

- **Status**: Done
- **Priority**: P2

### SPKB-2681 — R3: Admin Can See Correct Bid Ranking on Bid Data of Special DW 

- **Status**: Done
- **Priority**: P2

### SPKB-2682 — R3: Admin Can Download and View Correct Bid Data from the Special DW File

- **Status**: Done
- **Priority**: P2

### SPKB-2683 — R3: Bid Data Contains Only Submitted Bids from R1 and R2 of Special Dw Buyer

- **Status**: Done
- **Priority**: P2

### SPKB-2684 — R3: Admin Can Upload Bid File on Behalf of Special DW Buyer

- **Status**: Done
- **Priority**: P2

### SPKB-2685 — R3: Admin Can Re-Upload File on Behalf of Special DW Buyer

- **Status**: Done
- **Priority**: P2

### SPKB-2686 — R1: Regular (Multi) Buyer Receive Email - Round One Has Open

- **Status**: Done
- **Priority**: P2

### SPKB-2687 — R1: Regular (Multi) Non-DW Buyer Can View Auction Info on Dashboard

- **Status**: Done
- **Priority**: P2

### SPKB-2688 — R1: Regular (Multi) Non-DW Can View Correct Format, Column Header on Data Grid and Excel File

- **Status**: Done
- **Priority**: P2

### SPKB-2689 — R1: Regular (Multi) Non-DW Buyer Can View Correct Non-DW Inventory and Total Qty

- **Status**: Done
- **Priority**: P2

### SPKB-2690 — R1: Regular (Multi) Non-DW Buyer Can View Correct Non-DW Target Price

- **Status**: Done
- **Priority**: P2

### SPKB-2691 — R1: Regular (Multi) Non-DW Can Carry Over Bids from Previous Week

- **Status**: Done
- **Priority**: P2

### SPKB-2692 — R1: Regular (Multi) Non-DW Buyer Can Submit Bids on Data Grid

- **Status**: Done
- **Priority**: P2

### SPKB-2693 — R1: Regular (Multi) Non-DW Buyer Can See Minimum Bid Validation Popup on Bid Submission

- **Status**: Done
- **Priority**: P2

### SPKB-2694 — R1: Regular (Multi) Non-DW Buyer Can Lower Bids and Re-Submit on Data Grid

- **Status**: Done
- **Priority**: P2

### SPKB-2695 — R1: Regular (Multi) Non-DW Buyer Can Re-Access Auction without Losing Unsubmitted Bids

- **Status**: Done
- **Priority**: P2

### SPKB-2696 — R1: Regular (Multi) Non-DW Buyer Can Export, Import File and Submit Bids

- **Status**: Done
- **Priority**: P2

### SPKB-2697 — R1: Regular (Multi) Non-DW Buyer Can Re-Import File with Higher Bids

- **Status**: Done
- **Priority**: P2

### SPKB-2698 — R1: Regular (Multi) Non-DW Buyer Can Re-Import File with Lower Bids

- **Status**: Done
- **Priority**: P2

### SPKB-2699 — R1: Regular (Multi) Non-DW Buyer Receive Email - "Round 1 is Closing" 3 Times If They Haven't Submit Bid Yet

- **Status**: Done
- **Priority**: P2

### SPKB-2700 — R1-R2: Regular (Multi) Non-DW Buyer Cannot Access Auction Round 1

- **Status**: Done
- **Priority**: P2

### SPKB-2701 — R1-R2: Regular  (Multi) Non-DW Buyer Can Download Submitted Bids after Round 1 Ended

- **Status**: Done
- **Priority**: P2

### SPKB-2702 — R1-R2: Admin Can Submit on Behalf of Regular (Multi) Non-DW Buyer

- **Status**: Done
- **Priority**: P2

### SPKB-2703 — R1-R2: Admin Can Re-Submit Bids on Behalf of Regular (Multi) Non-DW Buyer

- **Status**: Done
- **Priority**: P2

### SPKB-2704 — R1-R2: Admin Can Delete Submitted R1 Bid of Regular (Multi) Non-DW Buyer

- **Status**: Done
- **Priority**: P2

### SPKB-2705 — R1-R2: Admin Can Manually Unqualified (Multi) Non-DW Buyer to Participate Auction Round 2

- **Status**: Done
- **Priority**: P2

### SPKB-2706 — R1-R2: Admin Can Manually Qualified  (Multi) Non-DW Buyer to Participate Auction Round 2

- **Status**: Done
- **Priority**: P2

### SPKB-2707 — R2: Only Qualified Regular (Multi) Non-DW Receive Email - "Round-2 Open"

- **Status**: Done
- **Priority**: P2

### SPKB-2708 — R2: Qualified Regular (Multi) Non-DW Can Access and View Auction Info on Dashboard Regardless of R1-Bid Submission

- **Status**: Done
- **Priority**: P2

### SPKB-2709 — R2: Regular (Multi) Non-DW Buyer Can View Non-DW Inventory with Total Qty

- **Status**: Done
- **Priority**: P2

### SPKB-2710 — R2: Regular (Multi) Non-DW Buyer Can View Correct R2 Target Price 

- **Status**: Done
- **Priority**: P2

### SPKB-2711 — R2: Regular (Multi) Non-DW Buyer (R1 No Bid) Can Submit Bids on DataGrid

- **Status**: Done
- **Priority**: P2

### SPKB-2712 — R2: Regular (Multi) Non-DW Can See Correct Bid Ranking on DataGrid and Export File

- **Status**: Done
- **Priority**: P2

### SPKB-2713 — R2: Regular (Multi) Non-DW Buyer Can See Minimum Bid Validation Popup on Bid Submission

- **Status**: Done
- **Priority**: P2

### SPKB-2714 — R2: Regular (Multi) Non-DW Buyer (R1 No Bid) Can Re-Submit Bids on Data Grid

- **Status**: Done
- **Priority**: P2

### SPKB-2715 — R2: Regular (Multi) Non-DW Buyer (R1 No Bid) Can Re-Access Auction without Losing Unsubmitted Bids

- **Status**: Done
- **Priority**: P2

### SPKB-2716 — R2: Regular (Multi) Non-DW Buyer (R1 No Bid) Can Export, Import File and Submit Bids

- **Status**: Done
- **Priority**: P2

### SPKB-2717 — R2: Regular (Multi) Non-DW Buyer (R1 No Bid) Can Re-Import File Before Submission

- **Status**: Done
- **Priority**: P2

### SPKB-2718 — R2: Regular (Multi) Non-DW Buyer (R1 No Bid) Can Re-Import File After Submission

- **Status**: Done
- **Priority**: P2

### SPKB-2719 — R2: Regular (Multi) Non-DW Buyer (R1 Bid) Can Access and View Info on Dashboard

- **Status**: Done
- **Priority**: P2

### SPKB-2720 — R2: Regular (Multi) Non-DW Buyer (R1 Bid) Can View Correct Avail Qty Cap

- **Status**: Done
- **Priority**: P2

### SPKB-2721 — R2: Regular (Multi) Non-DW Buyer (R1 Bid) Can View Correct Target Price

- **Status**: Done
- **Priority**: P2

### SPKB-2722 — R2: Regular (Multi) Non-DW Buyer (R1 Bid) Can Submit Bids New Bids on Data Grid

- **Status**: Done
- **Priority**: P2

### SPKB-2723 — R2: Regular (Multi) Non-DW Buyer (R1 Bid) Can Re-Submit Bids on Data Grid

- **Status**: Done
- **Priority**: P2

### SPKB-2724 — R2: Regular (Multi) Non-DW Buyer (R1 Bid) Can Export, Import File and Submit Bids

- **Status**: Done
- **Priority**: P2

### SPKB-2725 — R2: Regular (Multi) Non-DW Buyer (R1 Bid) Can Re-Import File and Re-Submit Bids

- **Status**: Done
- **Priority**: P2

### SPKB-2726 — R2-R3: Regular (Multi) Non-DW Buyer Cannot Access Auction R2 after Round 2 Ended

- **Status**: Done
- **Priority**: P2

### SPKB-2727 — R2-R3: Regular (Multi) Non-DW Buyer Can Download Submitted Bids File after Round 2 Ended

- **Status**: Done
- **Priority**: P2

### SPKB-2728 — R2-R3: Admin Can Submit on Behalf of Regular (Multi) Non-DW Buyer

- **Status**: Done
- **Priority**: P2

### SPKB-2729 — R2-R3: Admin Can Re-Submit Bids on Behalf of Regular (Multi) Non-DW Buyer

- **Status**: Done
- **Priority**: P2

### SPKB-2730 — R2-R3: Admin Can Delete Submitted R2 Bids of Regular (Multi) Non-DW Buyer

- **Status**: Done
- **Priority**: P2

### SPKB-2731 — R3: Admin Can See Regular (Multi) Non-DW Buyer (R1, R2 Bids) Display on R3-Report Page 

- **Status**: Done
- **Priority**: P2

### SPKB-2732 — R3: Admin Cannot See Regular (Multi) Non-DW Buyer (R1, R2 NO Bids) Display on R3-Report Page

- **Status**: Done
- **Priority**: P2

### SPKB-2733 — R3: Admin Can View Correct Bid Data of Regular (Multi) Non-DW Buyer on "View Bid Data" Screen

- **Status**: Done
- **Priority**: P2

### SPKB-2734 — R3: Regular (Multi) Non-DW Buyer's Bid Data Has Correct Column Header and Formats

- **Status**: Done
- **Priority**: P2

### SPKB-2735 — R3: Admin Can See Correct Bid Ranking on Bid Data of Regular (Multi) Non-DW 

- **Status**: Done
- **Priority**: P2

### SPKB-2736 — R3: Admin Can Download and View Correct Bid Data from the Regular (Multi) Non-DW File

- **Status**: Done
- **Priority**: P2

### SPKB-2737 — R3: Bid Data Contains Only Submitted Bids from R1 and R2 of Regular (Multi) Non-Dw Buyer

- **Status**: Done
- **Priority**: P2

### SPKB-2738 — R3: Admin Can Upload Bid File on Behalf of Regular (Multi) Non-DW Buyer

- **Status**: Done
- **Priority**: P2

### SPKB-2739 — R3: Admin Can Re-Upload File on Behalf of Regular (Multi) Non-DW Buyer

- **Status**: Done
- **Priority**: P2

### SPKB-2740 — R1: Regular (Multi) Buyer Receive Email - Round One Has Open

- **Status**: Done
- **Priority**: P2

### SPKB-2741 — R1: Regular (Multi) DW Buyer Can View Auction Info on Dashboard

- **Status**: Done
- **Priority**: P2

### SPKB-2742 — R1: Regular (Multi) DW Can View Correct Format, Column Header on Data Grid and Excel File

- **Status**: Done
- **Priority**: P2

### SPKB-2743 — R1: Regular (Multi) DW Buyer Can View Correct DW Inventory and DW Qty

- **Status**: Done
- **Priority**: P2

### SPKB-2744 — R1: Regular (Multi) DW Buyer Can View Correct DW Target Price

- **Status**: Done
- **Priority**: P2

### SPKB-2745 — R1: Regular (Multi) DW Can Carry Over Bids from Previous Week

- **Status**: Done
- **Priority**: P2

### SPKB-2746 — R1: Regular (Multi) DW Buyer Can Submit Bids on Data Grid

- **Status**: Done
- **Priority**: P2

### SPKB-2747 — R1: Regular (Multi) DW Buyer Can See Minimum Bid Validation Popup on Bid Submission

- **Status**: Done
- **Priority**: P2

### SPKB-2748 — R1: Regular (Multi) DW Buyer Can Lower Bids and Re-Submit on Data Grid

- **Status**: Done
- **Priority**: P2

### SPKB-2749 — R1: Regular (Multi) DW Buyer Can Re-Access Auction without Losing Unsubmitted Bids

- **Status**: Done
- **Priority**: P2

### SPKB-2750 — R1: Regular (Multi) DW Buyer Can Export, Import File and Submit Bids

- **Status**: Done
- **Priority**: P2

### SPKB-2751 — R1: Regular (Multi) DW Buyer Can Re-Import File with Higher Bids

- **Status**: Done
- **Priority**: P2

### SPKB-2752 — R1: Regular (Multi) DW Buyer Can Re-Import File with Lower Bids

- **Status**: Done
- **Priority**: P2

### SPKB-2753 — R1: Regular (Multi) DW Buyer Receive Email - "Round 1 is Closing" 3 Times If They Haven't Submit Bid Yet

- **Status**: Done
- **Priority**: P2

### SPKB-2754 — R1-R2: Regular (Multi) DW Buyer Cannot Access Auction Round 1

- **Status**: Done
- **Priority**: P2

### SPKB-2755 — R1-R2: Regular  (Multi) DW Buyer Can Download Submitted Bids after Round 1 Ended

- **Status**: Done
- **Priority**: P2

### SPKB-2756 — R1-R2: Admin Can Submit on Behalf of Regular (Multi) DW Buyer

- **Status**: Done
- **Priority**: P2

### SPKB-2757 — R1-R2: Admin Can Re-Submit Bids on Behalf of Regular (Multi) DW Buyer

- **Status**: Done
- **Priority**: P2

### SPKB-2758 — R1-R2: Admin Can Delete Submitted R1 Bid of Regular (Multi) DW Buyer

- **Status**: Done
- **Priority**: P2

### SPKB-2759 — R1-R2: Admin Can Manually Unqualified (Multi) DW Buyer to Participate Auction Round 2

- **Status**: Done
- **Priority**: P2

### SPKB-2760 — R1-R2: Admin Can Manually Qualified  (Multi) DW Buyer to Participate Auction Round 2

- **Status**: Done
- **Priority**: P2

### SPKB-2761 — R2: Only Qualified Regular (Multi) DW Receive Email - "Round-2 Open"

- **Status**: Done
- **Priority**: P2

### SPKB-2762 — R2: Qualified Regular (Multi) DW Can Access and View Auction Info on Dashboard Regardless of R1-Bid Submission

- **Status**: Done
- **Priority**: P2

### SPKB-2763 — R2: Regular (Multi) DW Buyer Can View Non-DW Inventory with Total Qty

- **Status**: Done
- **Priority**: P2

### SPKB-2764 — R2: Regular (Multi) DW Buyer Can View Correct R2 Target Price 

- **Status**: Done
- **Priority**: P2

### SPKB-2765 — R2: Regular (Multi) DW Buyer (R1 No Bid) Can Submit Bids on DataGrid

- **Status**: Done
- **Priority**: P2

### SPKB-2766 — R2: Regular (Multi) DW Can See Correct Bid Ranking on DataGrid and Export File

- **Status**: Done
- **Priority**: P2

### SPKB-2767 — R2: Regular (Multi) DW Buyer Can See Minimum Bid Validation Popup on Bid Submission

- **Status**: Done
- **Priority**: P2

### SPKB-2768 — R2: Regular (Multi) DW Buyer (R1 No Bid) Can Re-Submit Bids on Data Grid

- **Status**: Done
- **Priority**: P2

### SPKB-2769 — R2: Regular (Multi) DW Buyer (R1 No Bid) Can Re-Access Auction without Losing Unsubmitted Bids

- **Status**: Done
- **Priority**: P2

### SPKB-2770 — R2: Regular (Multi) DW Buyer (R1 No Bid) Can Export, Import File and Submit Bids

- **Status**: Done
- **Priority**: P2

### SPKB-2771 — R2: Regular (Multi) DW Buyer (R1 No Bid) Can Re-Import File Before Submission

- **Status**: Done
- **Priority**: P2

### SPKB-2772 — R2: Regular (Multi) DW Buyer (R1 No Bid) Can Re-Import File After Submission

- **Status**: Done
- **Priority**: P2

### SPKB-2773 — R2: Regular (Multi) DW Buyer (R1 Bid) Can Access and View Info on Dashboard

- **Status**: Done
- **Priority**: P2

### SPKB-2774 — R2: Regular (Multi) DW Buyer (R1 Bid) Can View Correct Avail Qty Cap

- **Status**: Done
- **Priority**: P2

### SPKB-2775 — R2: Regular (Multi) DW Buyer (R1 Bid) Can View Correct Target Price

- **Status**: Done
- **Priority**: P2

### SPKB-2776 — R2: Regular (Multi) DW Buyer (R1 Bid) Can Submit Bids New Bids on Data Grid

- **Status**: Done
- **Priority**: P2

### SPKB-2777 — R2: Regular (Multi) DW Buyer (R1 Bid) Can Re-Submit Bids on Data Grid

- **Status**: Done
- **Priority**: P2

### SPKB-2778 — R2: Regular (Multi) DW Buyer (R1 Bid) Can Export, Import File and Submit Bids

- **Status**: Done
- **Priority**: P2

### SPKB-2779 — R2: Regular (Multi) DW Buyer (R1 Bid) Can Re-Import File and Re-Submit Bids

- **Status**: Done
- **Priority**: P2

### SPKB-2780 — R2-R3: Regular (Multi) DW Buyer Cannot Access Auction R2 after Round 2 Ended

- **Status**: Done
- **Priority**: P2

### SPKB-2781 — R2-R3: Regular (Multi) DW Buyer Can Download Submitted Bids File after Round 2 Ended

- **Status**: Done
- **Priority**: P2

### SPKB-2782 — R2-R3: Admin Can Submit on Behalf of Regular (Multi) DW Buyer

- **Status**: Done
- **Priority**: P2

### SPKB-2783 — R2-R3: Admin Can Re-Submit Bids on Behalf of Regular (Multi) DW Buyer

- **Status**: Done
- **Priority**: P2

### SPKB-2784 — R2-R3: Admin Can Delete Submitted R2 Bids of Regular (Multi) DW Buyer

- **Status**: Done
- **Priority**: P2

### SPKB-2785 — R3: Admin Can See Regular (Multi) DW Buyer (R1, R2 Bids) Display on R3-Report Page 

- **Status**: Done
- **Priority**: P2

### SPKB-2786 — R3: Admin Cannot See Regular (Multi) DW Buyer (R1, R2 NO Bids) Display on R3-Report Page

- **Status**: Done
- **Priority**: P2

### SPKB-2787 — R3: Admin Can View Correct Bid Data of Regular (Multi) DW Buyer on "View Bid Data" Screen

- **Status**: Done
- **Priority**: P2

### SPKB-2788 — R3: Regular (Multi) DW Buyer's Bid Data Has Correct Column Header and Formats

- **Status**: Done
- **Priority**: P2

### SPKB-2789 — R3: Admin Can See Correct Bid Ranking on Bid Data of Regular (Multi) DW 

- **Status**: Done
- **Priority**: P2

### SPKB-2790 — R3: Admin Can Download and View Correct Bid Data from the Regular (Multi) DW File

- **Status**: Done
- **Priority**: P2

### SPKB-2791 — R3: Bid Data Contains Only Submitted Bids from R1 and R2 of Regular (Multi) Dw Buyer

- **Status**: Done
- **Priority**: P2

### SPKB-2792 — R3: Admin Can Upload Bid File on Behalf of Regular (Multi) DW Buyer

- **Status**: Done
- **Priority**: P2

### SPKB-2793 — R3: Admin Can Re-Upload File on Behalf of Regular (Multi) DW Buyer

- **Status**: Done
- **Priority**: P2

### SPKB-2796 — R1: Special (Multi) Non-DW Buyer Receive Email - Round One Has Open

- **Status**: Done
- **Priority**: P2

### SPKB-2797 — R1: Special (Multi) Non-DW Buyer Can View Auction Info on Dashboard

- **Status**: Done
- **Priority**: P2

### SPKB-2798 — R1: Special (Multi) Non-DW Can View Correct Format, Column Header on Data Grid and Excel File

- **Status**: Done
- **Priority**: P2

### SPKB-2799 — R1: Special (Multi) Non-DW Buyer Can View Correct Non-DW Inventory and Total Qty

- **Status**: Done
- **Priority**: P2

### SPKB-2800 — R1: Special (Multi) Non-DW Buyer Can View Correct Non-DW Target Price

- **Status**: Done
- **Priority**: P2

### SPKB-2801 — R1: Special (Multi) Non-DW Can Carry Over Bids from Previous Week

- **Status**: Done
- **Priority**: P2

### SPKB-2802 — R1: Special (Multi) Non-DW Buyer Can Submit Bids on Data Grid

- **Status**: Done
- **Priority**: P2

### SPKB-2803 — R1: Special (Multi) Non-DW Buyer Can See Minimum Bid Validation Popup on Bid Submission

- **Status**: Done
- **Priority**: P2

### SPKB-2804 — R1: Special (Multi) Non-DW Buyer Can Lower Bids and Re-Submit on Data Grid

- **Status**: Done
- **Priority**: P2

### SPKB-2805 — R1: Special (Multi) Non-DW Buyer Can Re-Access Auction without Losing Unsubmitted Bids

- **Status**: Done
- **Priority**: P2

### SPKB-2806 — R1: Special (Multi) Non-DW Buyer Can Export, Import File and Submit Bids

- **Status**: Done
- **Priority**: P2

### SPKB-2807 — R1: Special (Multi) Non-DW Buyer Can Re-Import File with Higher Bids

- **Status**: Done
- **Priority**: P2

### SPKB-2808 — R1: Special (Multi) Non-DW Buyer Can Re-Import File with Lower Bids

- **Status**: Done
- **Priority**: P2

### SPKB-2809 — R1: Special (Multi) Non-DW Buyer Receive Email - "Round 1 is Closing" 3 Times If They Haven't Submit Bid Yet

- **Status**: Done
- **Priority**: P2

### SPKB-2810 — R1-R2: Special (Multi) Non-DW Buyer Cannot Access Auction Round 1

- **Status**: Done
- **Priority**: P2

### SPKB-2811 — R1-R2: Special (Multi) Non-DW Buyer Can Download Submitted Bids after Round 1 Ended

- **Status**: Done
- **Priority**: P2

### SPKB-2817 — R3 Qualification: Reg DW or Non-DW (Only R1 Bids) |Verify the Buyer Code Displays in R3 Qulified Buyer List

- **Status**: Done
- **Priority**: P2
- **Linked Issues**: SPKB-2816

### SPKB-2818 — R3 Qualification: Reg DW or Non-DW (Only R2 Bids) |Verify the Buyer Code Displays in R3 Qulified Buyer List

- **Status**: Done
- **Priority**: P2
- **Linked Issues**: SPKB-2816

### SPKB-2819 — R3 Qualification: Reg DW or Non-DW (R1 and R2 Bids) |Verify the Buyer Code Displays in R3 Qualified Buyer List

- **Status**: Done
- **Priority**: P2
- **Linked Issues**: SPKB-2816

### SPKB-2820 — R3 Qualification: Reg DW or Non-DW (No R1,R2 Bids) |Verify the Buyer Code NOT Displays in R3 Qualified Buyer List

- **Status**: Done
- **Priority**: P2
- **Linked Issues**: SPKB-2816

### SPKB-2822 — R3 Qualification: Reg Multi BuyerCodes (R1,R2 Bids) |Verify Only Buyer Code Displays in R3 Qualified Buyer List

- **Status**: Done
- **Priority**: P2
- **Linked Issues**: SPKB-2816

### SPKB-2823 — R3 Qualification: Reg Multi BuyerCodes (No R1,R2 Bids) |Verify Buyer Code NOT Displays in R3 Qualified Buyer List

- **Status**: Done
- **Priority**: P2
- **Linked Issues**: SPKB-2816

### SPKB-2824 — R3 Qualification: Special DW or Non-DW (Only R1 Bids) |Verify the Buyer Code Displays in R3 Qualified Buyer List

- **Status**: Done
- **Priority**: P2
- **Linked Issues**: SPKB-2816

### SPKB-2825 — R3 Qualification: Special DW or Non-DW (Only R2 Bids) |Verify the Buyer Code Displays in R3 Qualified Buyer List

- **Status**: Done
- **Priority**: P2
- **Linked Issues**: SPKB-2816

### SPKB-2826 — R3 Qualification: Special DW or Non-DW (R1 and R2 Bids) |Verify the Buyer Code Displays in R3 Qualified Buyer List

- **Status**: Done
- **Priority**: P2
- **Linked Issues**: SPKB-2816

### SPKB-2827 — R3 Qualification: Special DW or Non-DW (No R1,R2 Bids) |Verify the Buyer Code Still Displays in R3 Qualified Buyer List Despite No R1/R2 Bids

- **Status**: Done
- **Priority**: P2
- **Linked Issues**: SPKB-2816

### SPKB-2828 — R3 Qualification: Special Multi BuyerCodes (R1,R2 Bids) |Verify ONLY Buyer Code (with R1/R2 Bids) Displays in R3 Qualified Buyer List

- **Status**: Done
- **Priority**: P2
- **Linked Issues**: SPKB-2816

### SPKB-2829 — R3 Qualification: Special Multi BuyerCodes (No R1,R2 Bids) |Verify ALL Buyer Codes Still Display in R3 Qualified Buyer List Despite No R1/R2 Bids

- **Status**: Done
- **Priority**: P2
- **Linked Issues**: SPKB-2816

### SPKB-2835 — R3: Download: Reg Single DW or Non-DW | Validate Excel File with Correct File Name, Column Headers and Formats

- **Status**: Done
- **Priority**: P2
- **Linked Issues**: SPKB-2816

### SPKB-2836 — R3: Download: Reg Single DW or Non-DW | Validate No Duplication ecoID + Grade

- **Status**: Done
- **Priority**: P2
- **Linked Issues**: SPKB-2816

### SPKB-2837 — R3 Download: Reg Single DW or Non-DW | Validate Minimum Target Price

- **Status**: Done
- **Priority**: P2
- **Linked Issues**: SPKB-2816

### SPKB-2838 — R3 Download: Reg Single DW or Non-DW | Validate Qty Cap (Total + Additional Qty)

- **Status**: Done
- **Priority**: P2
- **Linked Issues**: SPKB-2816

### SPKB-2839 — R3 Download: Reg Single DW or Non-DW | Validate Bid Ranking 

- **Status**: Done
- **Priority**: P2
- **Linked Issues**: SPKB-2816

### SPKB-2840 — R3 Download: Reg Single DW or Non-DW (Only R1 or R2 Bids)| Validate Bid Data Display ONLY Submitted Bids from R1 or R2 

- **Status**: Done
- **Priority**: P2
- **Linked Issues**: SPKB-2816

### SPKB-2841 — R3 Download: Reg Single DW or Non-DW (R1 and R2 Bids)| Validate Bid Data Display All Submitted Bids from R1 and R2

- **Status**: Done
- **Priority**: P2
- **Linked Issues**: SPKB-2816

### SPKB-2842 — R3 Download: Reg Multi Buyer Code (R1,R2 Bids)| Validate Bid Data Display ONLY Submitted Bids Associate to the Buyer Code

- **Status**: Done
- **Priority**: P2
- **Linked Issues**: SPKB-2816

### SPKB-2843 — R3 Download: Reg Multi Buyer Code (R1,R2 Bids)| Validiate No Inventory Display for Buyer Code without R1,R2 Bids

- **Status**: Done
- **Priority**: P2
- **Linked Issues**: SPKB-2816

### SPKB-2844 — R3 Download: Reg Multi Buyer Code (No R1,R2 Bids)| Verify Admin Cannot Download File Because Buyer Not on the Qualified Buyer List

- **Status**: Done
- **Priority**: P2
- **Linked Issues**: SPKB-2816

### SPKB-2845 — R3: Download: Special Single DW or Non-DW | Validate Excel File with Correct File Name, Column Headers and Formats

- **Status**: Done
- **Priority**: P2
- **Linked Issues**: SPKB-2816

### SPKB-2846 — R3: Download: Special Single DW or Non-DW | Validate No Duplication ecoID + Grade

- **Status**: Done
- **Priority**: P2
- **Linked Issues**: SPKB-2816

### SPKB-2847 — R3 Download: Special Single DW or Non-DW | Validate Minimum Target Price

- **Status**: Done
- **Priority**: P2

### SPKB-2848 — R3 Download: Special Single DW or Non-DW | Validate Qty Cap (Total + Additional Qty)

- **Status**: Done
- **Priority**: P2
- **Linked Issues**: SPKB-2816

### SPKB-2849 — R3 Download: Special Single DW or Non-DW | Validate Bid Ranking 

- **Status**: Done
- **Priority**: P2
- **Linked Issues**: SPKB-2816

### SPKB-2850 — R3 Download: Special Single DW or Non-DW (Only R1 or R2 Bids)| Validate Bid Data Display ONLY Submitted Bids from R1 or R2 

- **Status**: Done
- **Priority**: P2
- **Linked Issues**: SPKB-2816

### SPKB-2851 — R3 Download: Special Single DW or Non-DW (R1 and R2 Bids)| Validate Bid Data Display All Submitted Bids from R1 and R2

- **Status**: Done
- **Priority**: P2
- **Linked Issues**: SPKB-2816

### SPKB-2852 — R3 Download: Special Multi Buyer Code (R1,R2 Bids)| Validate Bid Data Display ONLY Submitted Bids Associate to the Buyer Code

- **Status**: Done
- **Priority**: P2
- **Linked Issues**: SPKB-2816

### SPKB-2853 — R3 Download: Special Multi Buyer Code (R1,R2 Bids)| Validate No Inventory Display for Buyer Code without R1,R2 Bids

- **Status**: Done
- **Priority**: P2
- **Linked Issues**: SPKB-2816

### SPKB-2854 — R3 Download: Special Multi Buyer Code (No R1,R2 Bids)| Validate Full Inventory Display for ALL Buyer Codes Associate to the Buyer

- **Status**: Done
- **Priority**: P2
- **Linked Issues**: SPKB-2816

### SPKB-2855 — R3 Upload: Any Single Buyer Code | Verify Buyer Can Only Enter Y / N / Positive Real Number (Whole or Decimal)

- **Status**: Done
- **Priority**: P2
- **Linked Issues**: SPKB-2816

### SPKB-2856 — R3 Upload: Any Single Buyer Code | Verify Buyer Can Blank “Accept Max Bid?” Does Not Override Prior Bid Price

- **Status**: Done
- **Priority**: P2
- **Linked Issues**: SPKB-2816

### SPKB-2857 — R3 Upload: Any Single Buyer Code | Verify Buyer Attempts to Enter Zero to “Accept Max Bid?” Column Does Not Override Change Prior Bid Price

- **Status**: Done
- **Priority**: P2
- **Linked Issues**: SPKB-2816

### SPKB-2858 — R3 Upload: Any Single Buyer Code | Verify Buyer Attempts to Enter Invalid Text, Special Characters, Negative Value in "Accept Max Bid?" Column

- **Status**: Done
- **Priority**: P2
- **Linked Issues**: SPKB-2816

### SPKB-2859 — R3 Upload: Any Single Buyer Code | Verify Buyer Attempts to Lower "Bid Price" Does Not Override Current Highest Bid from Previous Rounds in SharePoint and Snowflake

- **Status**: Done
- **Priority**: P2
- **Linked Issues**: SPKB-2816

### SPKB-2860 — R3 Upload: Any Single Buyer Code | Verify Buyer Attempts to Increase/Decrease/Delete/Zero "Estimate Unit Count" on Submitted SKU Does Not Override the Values in SharePoint and Snowflake

- **Status**: Done
- **Priority**: P2
- **Linked Issues**: SPKB-2816

### SPKB-2861 — R3 Upload: Any Single Buyer Code | Verify Buyer Attempts to Increase/Decrease/Delete/Zero "Your Qty Cap" on Submitted SKUs Does Not Override the Values in SharePoint and Snowflake

- **Status**: Done
- **Priority**: P2
- **Linked Issues**: SPKB-2816

### SPKB-2862 — R3 Upload: Any Single Buyer Code | Verify Buyer Attempts to Increase/Decrease/Delete/Zero "Bid" on Submitted SKU Does Not Override the Values in SharePoint and Snowflake

- **Status**: Done
- **Priority**: P2
- **Linked Issues**: SPKB-2816

### SPKB-2863 — R3 Upload: Any Single Buyer Code | Verify Buyer Attempts to Decrease/Delete "Max Bid" on Submitted SKU Does Not Override the Values in SharePoint and Snowflake

- **Status**: Done
- **Priority**: P2
- **Linked Issues**: SPKB-2816

### SPKB-2864 — R3 Upload: Any Single Buyer Code | Verify Buyer Attempts to Increase/Decrease/Delete/Zero "Estimate Unit Count" on NEW SKU Does Not Override the Values in SharePoint and Snowflake

- **Status**: Done
- **Priority**: P2
- **Linked Issues**: SPKB-2816

### SPKB-2865 — R3 Upload: Any Single Buyer Code | Verify Buyer Attempts to Increase/Decrease/Delete/Zero "Bid" on NEW SKU Does Not Override the Values in SharePoint and Snowflake

- **Status**: Done
- **Priority**: P2
- **Linked Issues**: SPKB-2816

### SPKB-2866 — R3 Upload: Any Single Buyer Code | Verify Buyer Attempts to Decrease/Delete "Max Bid" on NEW SKU Does Not Override the Values in SharePoint and Snowflake

- **Status**: Done
- **Priority**: P2
- **Linked Issues**: SPKB-2816

### SPKB-2867 — R3 Upload: Any Single Buyer Code | Verify Buyer Attempts to Change "Bid Rank" Values Does Not Override the Values in SharePoint and Snowflake

- **Status**: Done
- **Priority**: P2
- **Linked Issues**: SPKB-2816

### SPKB-2868 — R3 Upload: Any Single Buyer Code | Verify Buyer Attempts to Change "ecoID / Grade / Part Name" Values Does Not Override the Values in SharePoint and Snowflake

- **Status**: Done
- **Priority**: P2
- **Linked Issues**: SPKB-2816

### SPKB-2869 — R3 Upload: Any Single Buyer Code | Verify Sheet Name Validation - Case Sensitive, Misspelling, Space, Extra Sheets

- **Status**: Done
- **Priority**: P2
- **Linked Issues**: SPKB-2816

### SPKB-2870 — R3 Upload: Any Single Buyer Code | Verify Column Header Validation - Case Sensitive, Misspelling

- **Status**: Done
- **Priority**: P2
- **Linked Issues**: SPKB-2816

### SPKB-2871 — R3 Upload: Any Single Buyer Code | Verify Insert Extra Row/Column Validation

- **Status**: Done
- **Priority**: P2
- **Linked Issues**: SPKB-2816

### SPKB-2872 — R3 Upload: Any Single Buyer Code | Verify Remove Column Validation - Code Grade, Brand, Part Name, Est Unit Count, Your Qty Cap, Bid,…

- **Status**: Done
- **Priority**: P2
- **Linked Issues**: SPKB-2816

### SPKB-2873 — R3 Upload: Any Single Buyer Code | Verify Remove Any SKUs by Deleting Rows Does Not Override the Values in SharePoint and Snowflake

- **Status**: Done
- **Priority**: P2
- **Linked Issues**: SPKB-2816

### SPKB-2874 — R3 Upload: Any Single Buyer Code | Verify Column Order Changed

- **Status**: Done
- **Priority**: P2
- **Linked Issues**: SPKB-2816

### SPKB-2875 — R3 Upload: Any Single Buyer Code | Verify Uploading Non-Excel File (CSV/ PDF/ XLS)

- **Status**: Done
- **Priority**: P2
- **Linked Issues**: SPKB-2816

### SPKB-2876 — R3 Upload: Any Single Buyer Code | Verify Concurrent Uploads by Multiple Users

- **Status**: Done
- **Priority**: P2
- **Linked Issues**: SPKB-2816

### SPKB-2877 — R3 Upload: Multi Buyer Codes | Verify Upload File with 20,000+ Rows (Multiple Buyer Codes)

- **Status**: Done
- **Priority**: P2
- **Linked Issues**: SPKB-2816

### SPKB-2887 — R1: Regular DW Buyer Can View Additional Qty Correctly Added to Total Qty

- **Status**: Done
- **Priority**: P2

### SPKB-2888 — R1-Regular DW Buyer Cannot See Any Target Price Below Minimum Target Price

- **Status**: Done
- **Priority**: P2

### SPKB-2889 — R2: Regular DW Buyer Can View Additional Qty Correctly Added to Total Qty

- **Status**: Done
- **Priority**: P2

### SPKB-2890 — R2-Regular DW Buyer Cannot See Any Target Price Below Minimum Target Price

- **Status**: Done
- **Priority**: P2

### SPKB-2891 — R1: Special Non-DW Buyer Can View Additional Qty Correctly Added to Total Qty

- **Status**: Done
- **Priority**: P2

### SPKB-2892 — R1-Special Non-DW Buyer Cannot See Any Target Price Below Minimum Target Price

- **Status**: Done
- **Priority**: P2

### SPKB-2893 — R2: Special Non-DW Buyer Can View Additional Qty Correctly Added to Total Qty

- **Status**: Done
- **Priority**: P2

### SPKB-2894 — R2-Special Non-DW Buyer Cannot See Any Target Price Below Minimum Target Price

- **Status**: Done
- **Priority**: P2

### SPKB-2895 — R1: Special DW Buyer Can View Additional Qty Correctly Added to Total Qty

- **Status**: Done
- **Priority**: P2

### SPKB-2896 — R1: Special DW Buyer Cannot See Any Target Price Below Minimum Target Price

- **Status**: Done
- **Priority**: P2

### SPKB-2897 — R2: Special DW Buyer Can View Additional Qty Correctly Added to Total Qty

- **Status**: Done
- **Priority**: P2

### SPKB-2898 — R2: Special DW Buyer Cannot See Any Target Price Below Minimum Target Price

- **Status**: Done
- **Priority**: P2

### SPKB-2899 — R1: Regular (Multi) DW Buyer Can View Additional Qty Correctly Added to Total Qty

- **Status**: Done
- **Priority**: P2

### SPKB-2900 — R1: Regular (Multi) DW Buyer Cannot See Any Target Price Below Minimum Target Price

- **Status**: Done
- **Priority**: P2

### SPKB-2901 — R2: Regular (Multi) DW Buyer (R1 No Bid) Can View Additional Qty Correctly Added to Total Qty

- **Status**: Done
- **Priority**: P2

### SPKB-2902 — R2: Regular (Multi) DW Buyer (R1 No Bid) Cannot See Any Target Price Below Minimum Target Price

- **Status**: Done
- **Priority**: P2

### SPKB-2903 — R2: Regular (Multi) DW Buyer (R1 Bid) Can View Additional Qty Correctly Added to Total Qty

- **Status**: Done
- **Priority**: P2

### SPKB-2904 — R2: Regular (Multi) DW Buyer (R1 Bid) Cannot See Any Target Price Below Minimum Target Price

- **Status**: Done
- **Priority**: P2

### SPKB-2905 — R1: Regular (Multi) Non-DW Buyer Can View Additional Qty Correctly Added to Total Qty

- **Status**: Done
- **Priority**: P2

### SPKB-2906 — R1: Regular (Multi) Non-DW Buyer Cannot See Any Target Price Below Minimum Target Price

- **Status**: Done
- **Priority**: P2

### SPKB-2907 — R2: Regular (Multi) Non-DW Buyer (R1 No Bid) Can View Additional Qty Correctly Added to Total Qty

- **Status**: Done
- **Priority**: P2

### SPKB-2908 — R2: Regular (Multi) Non-DW Buyer (R1 No Bid) Cannot See Any Target Price Below Minimum Target Price

- **Status**: Done
- **Priority**: P2

### SPKB-2909 — R2: Regular (Multi) Non-DW Buyer (R1 Bid) Can View Additional Qty Correctly Added to Total Qty

- **Status**: Done
- **Priority**: P2

### SPKB-2910 — R2: Regular (Multi) Non-DW Buyer (R1 Bid) Can View Additional Qty Correctly Added to Total Qty

- **Status**: Done
- **Priority**: P2

### SPKB-2911 — R1: Special (Multi) Non-DW Buyer Can View Additional Qty Correctly Added to Total Qty

- **Status**: Done
- **Priority**: P2

### SPKB-2912 — R1: Special (Multi) Non-DW Buyer Cannot See Any Target Price Below Minimum Target Price

- **Status**: Done
- **Priority**: P2

### SPKB-2913 — R1-R2: Admin Can Submit on Behalf of Special (Multi) Non-DW Buyer

- **Status**: Done
- **Priority**: P2

### SPKB-2914 — R1-R2: Admin Can Re-Submit Bids on Behalf of Special (Multi) Non-DW Buyer

- **Status**: Done
- **Priority**: P2

### SPKB-2915 — R1-R2: Admin Can Delete Submitted R1 Bid of Special (Multi) Non-DW Buyer

- **Status**: Done
- **Priority**: P2

### SPKB-2916 — R1-R2: Admin Can Manually Unqualified Special (Multi) Non-DW Buyer to Participate Auction Round 2

- **Status**: Done
- **Priority**: P2

### SPKB-2917 — R1-R2: Admin Can Manually Qualified Special (Multi) Non-DW Buyer to Participate Auction Round 2

- **Status**: Done
- **Priority**: P2

### SPKB-2918 — R2: Only Qualified Special (Multi) Non-DW Receive Email - "Round-2 Open"

- **Status**: Done
- **Priority**: P2

### SPKB-2919 — R2: Qualified Special (Multi) Non-DW Can Access and View Auction Info on Dashboard Regardless of R1-Bid Submission

- **Status**: Done
- **Priority**: P2

### SPKB-2920 — R2: Special (Multi) Non-DW Buyer Can View Non-DW Inventory with Total Qty

- **Status**: Done
- **Priority**: P2

### SPKB-2921 — R2: Special (Multi) Non-DW Buyer (R1 No Bid) Can View Additional Qty Correctly Added to Total Qty

- **Status**: Done
- **Priority**: P2

### SPKB-2922 — R2: Special (Multi) Non-DW Buyer Can View Correct R2 Target Price 

- **Status**: Done
- **Priority**: P2

### SPKB-2923 — R2: Special (Multi) Non-DW Buyer (R1 No Bid) Cannot See Any Target Price Below Minimum Target Price

- **Status**: Done
- **Priority**: P2

### SPKB-2924 — R2: Special (Multi) Non-DW Buyer (R1 No Bid) Can Submit Bids on DataGrid

- **Status**: Done
- **Priority**: P2

### SPKB-2925 — R2: Special (Multi) Non-DW Can See Correct Bid Ranking on DataGrid and Export File

- **Status**: Done
- **Priority**: P2

### SPKB-2926 — R2: Special (Multi) Non-DW Buyer Can See Minimum Bid Validation Popup on Bid Submission

- **Status**: Done
- **Priority**: P2

### SPKB-2927 — R2: Special (Multi) Non-DW Buyer (R1 No Bid) Can Re-Submit Bids on Data Grid

- **Status**: Done
- **Priority**: P2

### SPKB-2928 — R2: Special (Multi) Non-DW Buyer (R1 No Bid) Can Re-Access Auction without Losing Unsubmitted Bids

- **Status**: Done
- **Priority**: P2

### SPKB-2929 — R2: Special (Multi) Non-DW Buyer (R1 No Bid) Can Export, Import File and Submit Bids

- **Status**: Done
- **Priority**: P2

### SPKB-2930 — R2: Special (Multi) Non-DW Buyer (R1 No Bid) Can Re-Import File Before Submission

- **Status**: Done
- **Priority**: P2

### SPKB-2931 — R2: Special (Multi) Non-DW Buyer (R1 No Bid) Can Re-Import File After Submission

- **Status**: Done
- **Priority**: P2

### SPKB-2932 — R2: Special (Multi) Non-DW Buyer (R1 Bid) Can Access and View Info on Dashboard

- **Status**: Done
- **Priority**: P2

### SPKB-2933 — R2: Special (Multi) Non-DW Buyer (R1 Bid) Can View Correct Avail Qty Cap

- **Status**: Done
- **Priority**: P2

### SPKB-2934 — R2: Special (Multi) Non-DW Buyer (R1 Bid) Can View Additional Qty Correctly Added to Total Qty

- **Status**: Done
- **Priority**: P2

### SPKB-2935 — R2: Special (Multi) Non-DW Buyer (R1 Bid) Can View Correct Target Price

- **Status**: Done
- **Priority**: P2

### SPKB-2936 — R2: Special (Multi) Non-DW Buyer (R1 Bid) Cannot See Any Target Price Below Minimum Target Price

- **Status**: Done
- **Priority**: P2

### SPKB-2937 — R2: Special (Multi) Non-DW Buyer (R1 Bid) Can Submit Bids New Bids on Data Grid

- **Status**: Done
- **Priority**: P2

### SPKB-2938 — R2: Special (Multi) Non-DW Buyer (R1 Bid) Can Re-Submit Bids on Data Grid

- **Status**: Done
- **Priority**: P2

### SPKB-2939 — R2: Special (Multi) Non-DW Buyer (R1 Bid) Can Export, Import File and Submit Bids

- **Status**: Done
- **Priority**: P2

### SPKB-2940 — R2: Special (Multi) Non-DW Buyer (R1 Bid) Can Re-Import File and Re-Submit Bids

- **Status**: Done
- **Priority**: P2

### SPKB-2941 — R2-R3: Special (Multi) Non-DW Buyer Cannot Access Auction R2 after Round 2 Ended

- **Status**: Done
- **Priority**: P2

### SPKB-2942 — R2-R3: Special (Multi) Non-DW Buyer Can Download Submitted Bids File after Round 2 Ended

- **Status**: Done
- **Priority**: P2

### SPKB-2943 — R2-R3: Admin Can Submit on Behalf of Special (Multi) Non-DW Buyer

- **Status**: Done
- **Priority**: P2

### SPKB-2944 — R2-R3: Admin Can Re-Submit Bids on Behalf of Special (Multi) Non-DW Buyer

- **Status**: Done
- **Priority**: P2

### SPKB-2945 — R2-R3: Admin Can Delete Submitted R2 Bids of Special (Multi) Non-DW Buyer

- **Status**: Done
- **Priority**: P2

### SPKB-2946 — R3: Admin Can See Special (Multi) Non-DW Buyer (R1, R2 Bids) Display on R3-Report Page 

- **Status**: Done
- **Priority**: P2

### SPKB-2947 — R3: Admin Cannot See Special (Multi) Non-DW Buyer Code (R1, R2 NO Bids) Display on R3-Report Page ** 

- **Status**: Done
- **Priority**: P2

**Description:**
If one of the buyer codes has bids submitted from R1/R2, the rest of the buyer codes will become non-special.  Those buyer codes with no R1/R2 bid won’t be qualified for R3

### SPKB-2948 — R3: Admin Can View Correct Bid Data of Special (Multi) Non-DW Buyer on "View Bid Data" Screen

- **Status**: Done
- **Priority**: P2

### SPKB-2949 — R3: Special (Multi) Non-DW Buyer's Bid Data Has Correct Column Header and Formats

- **Status**: Done
- **Priority**: P2

### SPKB-2950 — R3: Admin Can See Correct Bid Ranking on Bid Data of Special (Multi) Non-DW 

- **Status**: Done
- **Priority**: P2

### SPKB-2951 — R3: Admin Can Download and View Correct Bid Data from the Special (Multi) Non-DW File

- **Status**: Done
- **Priority**: P2

### SPKB-2952 — R3: Bid Data Contains Only Submitted Bids from R1 and R2 of Special (Multi) Non-Dw Buyer

- **Status**: Done
- **Priority**: P2

### SPKB-2953 — R3: Admin Can Upload Bid File on Behalf of Special (Multi) Non-DW Buyer

- **Status**: Done
- **Priority**: P2

### SPKB-2954 — R3: Admin Can Re-Upload File on Behalf of Special (Multi) Non-DW Buyer

- **Status**: Done
- **Priority**: P2

### SPKB-2955 — R1: Special (Multi) DW Buyer Receive Email - Round One Has Open

- **Status**: Done
- **Priority**: P2

### SPKB-2956 — R1: Special (Multi) DW Buyer Can View Auction Info on Dashboard

- **Status**: Done
- **Priority**: P2

### SPKB-2957 — R1: Special (Multi) DW Can View Correct Format, Column Header on Data Grid and Excel File

- **Status**: Done
- **Priority**: P2

### SPKB-2958 — R1: Special (Multi) DW Buyer Can View Correct DW Inventory and DW Qty

- **Status**: Done
- **Priority**: P2

### SPKB-2959 — R1: Special (Multi) DW Buyer Can View Additional Qty Correctly Added to Total Qty

- **Status**: Done
- **Priority**: P2

### SPKB-2960 — R1: Special (Multi) DW Buyer Can View Correct DW Target Price

- **Status**: Done
- **Priority**: P2

### SPKB-2961 — R1: Special (Multi) DW Buyer Cannot See Any Target Price Below Minimum Target Price

- **Status**: Done
- **Priority**: P2

### SPKB-2962 — R1: Special (Multi) DW Can Carry Over Bids from Previous Week

- **Status**: Done
- **Priority**: P2

### SPKB-2963 — R1: Special (Multi) DW Buyer Can Submit Bids on Data Grid

- **Status**: Done
- **Priority**: P2

### SPKB-2964 — R1: Special (Multi) DW Buyer Can See Minimum Bid Validation Popup on Bid Submission

- **Status**: Done
- **Priority**: P2

### SPKB-2965 — R1: Special (Multi) DW Buyer Can Lower Bids and Re-Submit on Data Grid

- **Status**: Done
- **Priority**: P2

### SPKB-2966 — R1: Special (Multi) DW Buyer Can Re-Access Auction without Losing Unsubmitted Bids

- **Status**: Done
- **Priority**: P2

### SPKB-2967 — R1: Special (Multi) DW Buyer Can Export, Import File and Submit Bids

- **Status**: Done
- **Priority**: P2

### SPKB-2968 — R1: Special (Multi) DW Buyer Can Re-Import File with Higher Bids

- **Status**: Done
- **Priority**: P2

### SPKB-2969 — R1: Special (Multi) DW Buyer Can Re-Import File with Lower Bids

- **Status**: Done
- **Priority**: P2

### SPKB-2970 — R1: Special DW Buyer Receive Email - "Round 1 is Closing" 3 Times If They Haven't Submit Bid Yet

- **Status**: Done
- **Priority**: P2

### SPKB-2971 — R1-R2: Special DW Buyer Cannot Access Auction Round 1

- **Status**: Done
- **Priority**: P2

### SPKB-2972 — R1-R2: Special DW Buyer Can Download Submitted Bids after Round 1 Ended

- **Status**: Done
- **Priority**: P2

### SPKB-2973 — R1-R2: Admin Can Submit on Behalf of Special DW Buyer

- **Status**: Done
- **Priority**: P2

### SPKB-2974 — R1-R2: Admin Can Re-Submit Bids on Behalf of Special DW Buyer

- **Status**: Done
- **Priority**: P2

### SPKB-2975 — R1-R2: Admin Can Delete Submitted R1 Bid of Special DW Buyer

- **Status**: Done
- **Priority**: P2

### SPKB-2976 — R1-R2: Admin Can Manually Unqualified Special DW Buyer to Participate Auction Round 2

- **Status**: Done
- **Priority**: P2

### SPKB-2977 — R1-R2: Admin Can Manually Qualified Special DW Buyer to Participate Auction Round 2

- **Status**: Done
- **Priority**: P2

### SPKB-2978 — R2: Only Qualified Special DW Receive Email - "Round-2 Open"

- **Status**: Done
- **Priority**: P2

### SPKB-2979 — R2: Qualified Special DW Can Access and View Auction Info on Dashboard Regardless of R1-Bid Submission

- **Status**: Done
- **Priority**: P2

### SPKB-2980 — R2: Special DW Buyer Can View Non-DW Inventory with Total Qty

- **Status**: Done
- **Priority**: P2

### SPKB-2981 — R2: Special (Multi) DW Buyer (R1 No Bid) Can View Additional Qty Correctly Added to Total Qty

- **Status**: Done
- **Priority**: P2

### SPKB-2982 — R2: Special DW Buyer Can View Correct R2 Target Price 

- **Status**: Done
- **Priority**: P2

### SPKB-2983 — R2: Special (Multi) DW Buyer (R1 No Bid) Cannot See Any Target Price Below Minimum Target Price

- **Status**: Done
- **Priority**: P2

### SPKB-2984 — R2: Special DW Buyer (R1 No Bid) Can Submit Bids on DataGrid

- **Status**: Done
- **Priority**: P2

### SPKB-2985 — R2: Special DW Can See Correct Bid Ranking on DataGrid and Export File

- **Status**: Done
- **Priority**: P2

### SPKB-2986 — R2: Special DW Buyer Can See Minimum Bid Validation Popup on Bid Submission

- **Status**: Done
- **Priority**: P2

### SPKB-2987 — R2: Special DW Buyer (R1 No Bid) Can Re-Submit Bids on Data Grid

- **Status**: Done
- **Priority**: P2

### SPKB-2988 — R2: Special DW Buyer (R1 No Bid) Can Re-Access Auction without Losing Unsubmitted Bids

- **Status**: Done
- **Priority**: P2

### SPKB-2989 — R2: Special DW Buyer (R1 No Bid) Can Export, Import File and Submit Bids

- **Status**: Done
- **Priority**: P2

### SPKB-2990 — R2: Special DW Buyer (R1 No Bid) Can Re-Import File Before Submission

- **Status**: Done
- **Priority**: P2

### SPKB-2991 — R2: Special DW Buyer (R1 No Bid) Can Re-Import File After Submission

- **Status**: Done
- **Priority**: P2

### SPKB-2992 — R2: Special DW Buyer (R1 Bid) Can Access and View Info on Dashboard

- **Status**: Done
- **Priority**: P2

### SPKB-2993 — R2: Special DW Buyer (R1 Bid) Can View Correct Avail Qty Cap

- **Status**: Done
- **Priority**: P2

### SPKB-2994 — R2: Special (Multi) DW Buyer (R1 Bid) Can View Additional Qty Correctly Added to Total Qty

- **Status**: Done
- **Priority**: P2

### SPKB-2995 — R2: Special DW Buyer (R1 Bid) Can View Correct Target Price

- **Status**: Done
- **Priority**: P2

### SPKB-2996 — R2: Special (Multi) DW Buyer (R1 Bid) Cannot See Any Target Price Below Minimum Target Price

- **Status**: Done
- **Priority**: P2

### SPKB-2997 — R2: Special DW Buyer (R1 Bid) Can Submit Bids New Bids on Data Grid

- **Status**: Done
- **Priority**: P2

### SPKB-2998 — R2: Special DW Buyer (R1 Bid) Can Re-Submit Bids on Data Grid

- **Status**: Done
- **Priority**: P2

### SPKB-2999 — R2: Special DW Buyer (R1 Bid) Can Export, Import File and Submit Bids

- **Status**: Done
- **Priority**: P2

### SPKB-3000 — R2: Special DW Buyer (R1 Bid) Can Re-Import File and Re-Submit Bids

- **Status**: Done
- **Priority**: P2

### SPKB-3001 — R2-R3: Special DW Buyer Cannot Access Auction R2 after Round 2 Ended

- **Status**: Done
- **Priority**: P2

### SPKB-3002 — R2-R3: Special DW Buyer Can Download Submitted Bids File after Round 2 Ended

- **Status**: Done
- **Priority**: P2

### SPKB-3003 — R2-R3: Admin Can Submit on Behalf of Special DW Buyer

- **Status**: Done
- **Priority**: P2

### SPKB-3004 — R2-R3: Admin Can Re-Submit Bids on Behalf of Special DW Buyer

- **Status**: Done
- **Priority**: P2

### SPKB-3005 — R2-R3: Admin Can Delete Submitted R2 Bids of Special DW Buyer

- **Status**: Done
- **Priority**: P2

### SPKB-3006 — R3: Admin Can See Special DW Buyer (R1, R2 Bids) Display on R3-Report Page 

- **Status**: Done
- **Priority**: P2

### SPKB-3007 — R3: Admin Cannot See Special DW Buyer (R1, R2 NO Bids) Display on R3-Report Page***

- **Status**: Done
- **Priority**: P2

### SPKB-3008 — R3: Admin Can View Correct Bid Data of Special DW Buyer on "View Bid Data" Screen

- **Status**: Done
- **Priority**: P2

### SPKB-3009 — R3: Special DW Buyer's Bid Data Has Correct Column Header and Formats

- **Status**: Done
- **Priority**: P2

### SPKB-3010 — R3: Admin Can See Correct Bid Ranking on Bid Data of Special DW 

- **Status**: Done
- **Priority**: P2

### SPKB-3011 — R3: Admin Can Download and View Correct Bid Data from the Special DW File

- **Status**: Done
- **Priority**: P2

### SPKB-3012 — R3: Bid Data Contains Only Submitted Bids from R1 and R2 of Special Dw Buyer

- **Status**: Done
- **Priority**: P2

### SPKB-3013 — R3: Admin Can Upload Bid File on Behalf of Special DW Buyer

- **Status**: Done
- **Priority**: P2

### SPKB-3014 — R3: Admin Can Re-Upload File on Behalf of Special DW Buyer

- **Status**: Done
- **Priority**: P2

### SPKB-3025 — R2: Regular Non-DW | Verify Admin Can Increase R1-Submitted Bid Price on DataGrid

- **Status**: Done
- **Priority**: P2
- **Linked Issues**: SPKB-2363

### SPKB-3026 — R2: Regular Non-DW | Verify Admin Can Increase R1-Submitted Bid Qty on DataGrid

- **Status**: Done
- **Priority**: P2
- **Linked Issues**: SPKB-2363

### SPKB-3027 — R2: Regular Non-DW | Verify Admin Can Increase R1-Submitted Bid Price via Import File

- **Status**: Done
- **Priority**: P2
- **Linked Issues**: SPKB-2363

### SPKB-3028 — R2: Regular Non-DW | Verify Admin Can Increase R1-Submitted Bid Qty via Import File

- **Status**: Done
- **Priority**: P2
- **Linked Issues**: SPKB-2363

### SPKB-3029 — R2: Regular Non-DW | Verify Admin Can Decrease R1-Submitted Bid Price on DataGrid

- **Status**: Done
- **Priority**: P2
- **Linked Issues**: SPKB-2363

### SPKB-3030 — R2: Regular Non-DW | Verify Admin Can Decrease R1-Submitted Bid Qty on DataGrid

- **Status**: Done
- **Priority**: P2
- **Linked Issues**: SPKB-2363

### SPKB-3031 — R2: Regular Non-DW | Verify Admin Can Decrease R1-Submitted Bid Qty via Import File

- **Status**: Done
- **Priority**: P2
- **Linked Issues**: SPKB-2363

### SPKB-3032 — R2: Regular Non-DW | Verify Admin Can Decrease R1-Submitted Bid Qty via Import File

- **Status**: Done
- **Priority**: P2
- **Linked Issues**: SPKB-2363

### SPKB-3033 — R2: Regular Non-DW | Verify Admin Can Empty R1-Submitted Bid on DataGrid

- **Status**: Done
- **Priority**: P2
- **Linked Issues**: SPKB-2363

### SPKB-3034 — R2: Regular Non-DW | Verify Admin Can Empty R1-Submitted Bid Qty via Import File

- **Status**: In Testing
- **Priority**: P2
- **Linked Issues**: SPKB-2363

### SPKB-3035 — R2: Regular Non-DW | Verify Bidder CANNOT Decrease R1-Submitted Bid Price on DataGrid

- **Status**: Done
- **Priority**: P2
- **Linked Issues**: SPKB-2363

### SPKB-3036 — R2: Regular Non-DW | Verify Bidder CANNOT Decrease R1-Submitted Bid Qty on DataGrid

- **Status**: Done
- **Priority**: P2
- **Linked Issues**: SPKB-2363

### SPKB-3037 — R2: Regular Non-DW | Verify Bidder CANNOT Decrease R1-Submitted Bid Price via Import File

- **Status**: Done
- **Priority**: P2
- **Linked Issues**: SPKB-2363

### SPKB-3038 — R2: Regular Non-DW | Verify Bidder CANNOT Decrease R1-Submitted Bid Qty via Import File

- **Status**: Done
- **Priority**: P2
- **Linked Issues**: SPKB-2363

### SPKB-3039 — R2: Regular Non-DW | Verify Bidder CANNOT Remove R1-Submitted Bid on DataGrid

- **Status**: Done
- **Priority**: P2
- **Linked Issues**: SPKB-2363

### SPKB-3040 — R2: Regular Non-DW | Verify Bidder CANNOT Remove R1-Submitted Bid via Import File

- **Status**: Done
- **Priority**: P2
- **Linked Issues**: SPKB-2363

### SPKB-3041 — R2: Regular DW | Verify Admin Can Increase R1-Submitted Bid Price on DataGrid

- **Status**: Done
- **Priority**: P2
- **Linked Issues**: SPKB-2363

### SPKB-3042 — R2: Regular DW | Verify Admin Can Increase R1-Submitted Bid Qty on DataGrid

- **Status**: Done
- **Priority**: P2
- **Linked Issues**: SPKB-2363

### SPKB-3043 — R2: Regular DW | Verify Admin Can Increase R1-Submitted Bid Qty via Import File

- **Status**: Done
- **Priority**: P2
- **Linked Issues**: SPKB-2363

### SPKB-3044 — R2: Regular DW | Verify Admin Can Increase R1-Submitted Bid Qty via Import File

- **Status**: Done
- **Priority**: P2
- **Linked Issues**: SPKB-2363

### SPKB-3045 — R2: Regular DW | Verify Admin Can Decrease R1-Submitted Bid Price on DataGrid

- **Status**: Done
- **Priority**: P2
- **Linked Issues**: SPKB-2363

### SPKB-3046 — R2: Regular DW | Verify Admin Can Decrease R1-Submitted Bid Qty on DataGrid

- **Status**: Done
- **Priority**: P2
- **Linked Issues**: SPKB-2363

### SPKB-3047 — R2: Regular DW | Verify Admin Can Decrease R1-Submitted Bid Qty via Import File

- **Status**: Done
- **Priority**: P2
- **Linked Issues**: SPKB-2363

### SPKB-3048 — R2: Regular DW | Verify Admin Can Decrease R1-Submitted Bid Qty via Import File

- **Status**: Done
- **Priority**: P2
- **Linked Issues**: SPKB-2363

### SPKB-3049 — R2: Regular DW | Verify Admin Can Remove R1-Submitted Bid on DataGrid

- **Status**: Done
- **Priority**: P2
- **Linked Issues**: SPKB-2363

### SPKB-3050 — R2: Regular DW | Verify Admin Can Remove R1-Submitted Bid Qty via Import File

- **Status**: In Testing
- **Priority**: P2
- **Linked Issues**: SPKB-2363

### SPKB-3051 — R2: Regular DW | Verify Bidder CANNOT Decrease R1-Submitted Bid Price on DataGrid

- **Status**: Done
- **Priority**: P2
- **Linked Issues**: SPKB-2363

### SPKB-3052 — R2: Regular DW | Verify Bidder CANNOT Decrease R1-Submitted Bid Qty on DataGrid

- **Status**: Done
- **Priority**: P2
- **Linked Issues**: SPKB-2363

### SPKB-3053 — R2: Regular DW | Verify Bidder CANNOT Decrease R1-Submitted Bid Price via Import File

- **Status**: Done
- **Priority**: P2
- **Linked Issues**: SPKB-2363

### SPKB-3054 — R2: Regular DW | Verify Bidder CANNOT Decrease R1-Submitted Bid Qty via Import File

- **Status**: Done
- **Priority**: P2
- **Linked Issues**: SPKB-2363

### SPKB-3055 — R2: Regular DW | Verify Bidder CANNOT Remove R1-Submitted Bid on DataGrid

- **Status**: Done
- **Priority**: P2
- **Linked Issues**: SPKB-2363

### SPKB-3056 — R2: Regular DW | Verify Bidder CANNOT Remove R1-Submitted Bid via Import File

- **Status**: Done
- **Priority**: P2
- **Linked Issues**: SPKB-2363

### SPKB-3057 — R2: Special Non-DW | Verify Admin Can Increase R1-Submitted Bid Price on DataGrid

- **Status**: Done
- **Priority**: P2
- **Linked Issues**: SPKB-2363

### SPKB-3058 — R2: Special Non-DW | Verify Admin Can Increase R1-Submitted Bid Qty on DataGrid

- **Status**: Done
- **Priority**: P2
- **Linked Issues**: SPKB-2363

### SPKB-3059 — R2: Special Non-DW | Verify Admin Can Increase R1-Submitted Bid Qty via Import File

- **Status**: Done
- **Priority**: P2
- **Linked Issues**: SPKB-2363

### SPKB-3060 — R2: Special Non-DW | Verify Admin Can Increase R1-Submitted Bid Qty via Import File

- **Status**: Done
- **Priority**: P2
- **Linked Issues**: SPKB-2363

### SPKB-3061 — R2: Special Non-DW | Verify Admin Can Decrease R1-Submitted Bid Price on DataGrid

- **Status**: Done
- **Priority**: P2
- **Linked Issues**: SPKB-2363

### SPKB-3062 — R2: Special Non-DW | Verify Admin Can Decrease R1-Submitted Bid Qty on DataGrid

- **Status**: Done
- **Priority**: P2
- **Linked Issues**: SPKB-2363

### SPKB-3063 — R2: Special Non-DW | Verify Admin Can Decrease R1-Submitted Bid Qty via Import File

- **Status**: Done
- **Priority**: P2
- **Linked Issues**: SPKB-2363

### SPKB-3064 — R2: Special Non-DW | Verify Admin Can Decrease R1-Submitted Bid Qty via Import File

- **Status**: Done
- **Priority**: P2
- **Linked Issues**: SPKB-2363

### SPKB-3065 — R2: Special Non-DW | Verify Admin Can Remove R1-Submitted Bid on DataGrid

- **Status**: Done
- **Priority**: P2
- **Linked Issues**: SPKB-2363

### SPKB-3066 — R2: Special Non-DW | Verify Admin Can Remove R1-Submitted Bid Qty via Import File

- **Status**: In Testing
- **Priority**: P2
- **Linked Issues**: SPKB-2363

### SPKB-3067 — R2: Special Non-DW | Verify Bidder CANNOT Decrease R1-Submitted Bid Price on DataGrid

- **Status**: Done
- **Priority**: P2
- **Linked Issues**: SPKB-2363

### SPKB-3068 — R2: Special Non-DW | Verify Bidder CANNOT Decrease R1-Submitted Bid Qty on DataGrid

- **Status**: Done
- **Priority**: P2
- **Linked Issues**: SPKB-2363

### SPKB-3069 — R2: Special Non-DW | Verify Bidder CANNOT Decrease R1-Submitted Bid Price via Import File

- **Status**: In Testing
- **Priority**: P2
- **Linked Issues**: SPKB-2363

### SPKB-3070 — R2: Special Non-DW | Verify Bidder CANNOT Decrease R1-Submitted Bid Qty via Import File

- **Status**: In Testing
- **Priority**: P2
- **Linked Issues**: SPKB-2363

### SPKB-3071 — R2: Special Non-DW | Verify Bidder CANNOT Remove R1-Submitted Bid on DataGrid

- **Status**: Done
- **Priority**: P2
- **Linked Issues**: SPKB-2363

### SPKB-3072 — R2: Special Non-DW | Verify Bidder CANNOT Remove R1-Submitted Bid via Import File

- **Status**: In Testing
- **Priority**: P2
- **Linked Issues**: SPKB-2363

### SPKB-3073 — R2: Special DW | Verify Admin Can Increase R1-Submitted Bid Price on DataGrid

- **Status**: Done
- **Priority**: P2
- **Linked Issues**: SPKB-2363

### SPKB-3074 — R2: Special DW | Verify Admin Can Increase R1-Submitted Bid Qty on DataGrid

- **Status**: Done
- **Priority**: P2
- **Linked Issues**: SPKB-2363

### SPKB-3075 — R2: Special DW | Verify Admin Can Increase R1-Submitted Bid Qty via Import File

- **Status**: Done
- **Priority**: P2
- **Linked Issues**: SPKB-2363

### SPKB-3076 — R2: Special DW | Verify Admin Can Increase R1-Submitted Bid Qty via Import File

- **Status**: Done
- **Priority**: P2
- **Linked Issues**: SPKB-2363

### SPKB-3077 — R2: Special DW | Verify Admin Can Decrease R1-Submitted Bid Price on DataGrid

- **Status**: Done
- **Priority**: P2
- **Linked Issues**: SPKB-2363

### SPKB-3078 — R2: Special DW | Verify Admin Can Decrease R1-Submitted Bid Qty on DataGrid

- **Status**: Done
- **Priority**: P2
- **Linked Issues**: SPKB-2363

### SPKB-3079 — R2: Special DW | Verify Admin Can Decrease R1-Submitted Bid Qty via Import File

- **Status**: Done
- **Priority**: P2
- **Linked Issues**: SPKB-2363

### SPKB-3080 — R2: Special DW | Verify Admin Can Decrease R1-Submitted Bid Qty via Import File

- **Status**: Done
- **Priority**: P2
- **Linked Issues**: SPKB-2363

### SPKB-3081 — R2: Special DW | Verify Admin Can Remove R1-Submitted Bid on DataGrid

- **Status**: Done
- **Priority**: P2
- **Linked Issues**: SPKB-2363

### SPKB-3082 — R2: Special DW | Verify Admin Can Remove R1-Submitted Bid Qty via Import File

- **Status**: In Testing
- **Priority**: P2
- **Linked Issues**: SPKB-2363

### SPKB-3083 — R2: Special DW | Verify Bidder CANNOT Decrease R1-Submitted Bid Price on DataGrid

- **Status**: Done
- **Priority**: P2
- **Linked Issues**: SPKB-2363

### SPKB-3084 — R2: Special DW | Verify Bidder CANNOT Decrease R1-Submitted Bid Qty on DataGrid

- **Status**: Done
- **Priority**: P2
- **Linked Issues**: SPKB-2363

### SPKB-3085 — R2: Special DW | Verify Bidder CANNOT Decrease R1-Submitted Bid Price via Import File

- **Status**: In Testing
- **Priority**: P2
- **Linked Issues**: SPKB-2363

### SPKB-3086 — R2: Special DW | Verify Bidder CANNOT Decrease R1-Submitted Bid Qty via Import File

- **Status**: In Testing
- **Priority**: P2
- **Linked Issues**: SPKB-2363

### SPKB-3087 — R2: Special DW | Verify Bidder CANNOT Remove R1-Submitted Bid on DataGrid

- **Status**: Done
- **Priority**: P2
- **Linked Issues**: SPKB-2363

### SPKB-3088 — R2: Special DW | Verify Bidder CANNOT Remove R1-Submitted Bid via Import File

- **Status**: In Testing
- **Priority**: P2
- **Linked Issues**: SPKB-2363

### SPKB-3105 — R2: Regular Non-DW | Verify Decrease Bid NOT Revert Back to Original Value

- **Status**: In Testing
- **Priority**: P2
- **Linked Issues**: SPKB-2363

### SPKB-3106 — R2: Regular DW | Verify Decrease Bid NOT Revert Back to Original Value

- **Status**: In Testing
- **Priority**: P2
- **Linked Issues**: SPKB-2363

### SPKB-3107 — R2: Special Non-DW | Verify Decrease Bid NOT Revert Back to Original Value

- **Status**: In Testing
- **Priority**: P2
- **Linked Issues**: SPKB-2363

### SPKB-3108 — R2: Special DW | Verify Decrease Bid NOT Revert Back to Original Value

- **Status**: In Testing
- **Priority**: P2
- **Linked Issues**: SPKB-2363

### SPKB-3112 — R3: Regular Non-DW | Admin Can Decrease Bid Price and Qty on Behalf of Buyer

- **Status**: In Testing
- **Priority**: P2

### SPKB-3113 — R3: Regular Non-DW | Admin Can Zero/Remove Bid Price and Qty on Behalf of Buyer

- **Status**: In Testing
- **Priority**: P2

### SPKB-3114 — R3: Regular DW | Admin Can Decrease Bid Price and Qty on Behalf of Buyer

- **Status**: In Testing
- **Priority**: P2

### SPKB-3115 — R3: Regular DW | Admin Can Zero/Remove Bid Price and Qty on Behalf of Buyer

- **Status**: In Testing
- **Priority**: P2

### SPKB-3116 — R3: Special Non-DW | Admin Can Decrease Bid Price and Qty on Behalf of Buyer

- **Status**: In Testing
- **Priority**: P2

### SPKB-3117 — R3: Special Non-DW | Admin Can Zero/Remove Bid Price and Qty on Behalf of Buyer

- **Status**: In Testing
- **Priority**: P2

### SPKB-3118 — R3: Special DW | Admin Can Decrease Bid Price and Qty on Behalf of Buyer

- **Status**: In Testing
- **Priority**: P2

### SPKB-3119 — R3: Special DW | Admin Can Zero/Remove Bid Price and Qty on Behalf of Buyer

- **Status**: In Testing
- **Priority**: P2

### SPKB-3124 — Auction Round 2 | Verify Bid Data Generating for Special Buyer DW and Non-DW (No R1 Bid)

- **Status**: Done
- **Priority**: P2
- **Linked Issues**: SPKB-3022

### SPKB-3125 — Auction Round 2 | Verify Bid Data Generating for Special Buyer DW and Non-DW (R1 Bid)

- **Status**: Done
- **Priority**: P2
- **Linked Issues**: SPKB-3022

### SPKB-3126 — Auction Round 3 | Verify Bid Data Generating for Special Buyer DW and Non-DW (No R1, R2 Bid)

- **Status**: In Testing
- **Priority**: P2
- **Linked Issues**: SPKB-3022

### SPKB-3127 — Auction Round 3 | Verify Bid Data Generating for Special Buyer DW and Non-DW (R1 or R2 Bid)

- **Status**: Done
- **Priority**: P2
- **Linked Issues**: SPKB-3022

### SPKB-3208 — R1: Regular Non-DW Buyer Cannot Enter Negative Value on Bid Price and Qty

- **Status**: Done
- **Priority**: P2

### SPKB-3209 — R1: Regular Non-DW Buyer Cannot Enter Decimal Value on Bid Qty

- **Status**: Done
- **Priority**: P2

### SPKB-3210 — R1: Regular Non-DW Buyer Cannot Submit Zero Bid

- **Status**: Done
- **Priority**: P2

### SPKB-3211 — R1: Regular Non-DW Buyer Can Sort by Avail Qty and Target Price

- **Status**: Done
- **Priority**: P2

### SPKB-3212 — R1: Regular Non-DW Buyer Can Filter by Product ID and Grade

- **Status**: Done
- **Priority**: P2

### SPKB-3213 — R1: Regular Non-DW Buyer Can See No-CarryOver Message Display if They Don't Have Carryover from Previous Week

- **Status**: Done
- **Priority**: P2

### SPKB-3214 — R1: Regular DW Buyer Cannot Enter Negative Value on Bid Price and Qty

- **Status**: Done
- **Priority**: P2

### SPKB-3215 — R1: Regular DW Buyer Cannot Enter Decimal Value on Bid Qty

- **Status**: Done
- **Priority**: P2

### SPKB-3216 — R1: Regular DW Buyer Cannot Submit Zero Bid

- **Status**: Done
- **Priority**: P2

### SPKB-3217 — R1: Regular DW Buyer Can Sort by Avail Qty and Target Price

- **Status**: Done
- **Priority**: P2

### SPKB-3218 — R1: Regular DW Buyer Can Filter by Product ID and Grade

- **Status**: Done
- **Priority**: P2

### SPKB-3219 — R1: Regular DW Buyer Can See No-Carryover Message Display if They Don't Have Carryover from Previous Week

- **Status**: Done
- **Priority**: P2

### SPKB-3220 — R1: Special Non-DW Buyer Cannot Enter Negative Value on Bid Price and Qty

- **Status**: Done
- **Priority**: P2

### SPKB-3221 — R1: Special Non-DW Buyer Cannot Enter Decimal Value on Bid Qty

- **Status**: Done
- **Priority**: P2

### SPKB-3222 — R1: Special Non-DW Buyer Cannot Submit Zero Bid

- **Status**: Done
- **Priority**: P2

### SPKB-3223 — R1: Special Non-DW Buyer Can Sort by Avail Qty and Target Price

- **Status**: Done
- **Priority**: P2

### SPKB-3224 — R1: Special Non-DW Buyer Can Filter by Product ID and Grade

- **Status**: Done
- **Priority**: P2

### SPKB-3225 — R1: Special Non-DW Buyer Can See No-Carryover Message Display if They Don't Have Carryover from Previous Week

- **Status**: Done
- **Priority**: P2

### SPKB-3226 — R1: Special DW Buyer Cannot Enter Negative Value on Bid Price and Qty

- **Status**: Done
- **Priority**: P2

### SPKB-3227 — R1: Special DW Buyer Cannot Enter Decimal Value on Bid Qty

- **Status**: Done
- **Priority**: P2

### SPKB-3228 — R1: Special DW Buyer Cannot Submit Zero Bid

- **Status**: Done
- **Priority**: P2

### SPKB-3229 — R1: Special DW Buyer Can Sort by Avail Qty and Target Price

- **Status**: Done
- **Priority**: P2

### SPKB-3230 — R1: Special DW Buyer Can Filter by Product ID and Grade

- **Status**: Done
- **Priority**: P2

### SPKB-3231 — R1: Special DW Buyer Can See No-Carryover Message Display if They Don't Have Carryover from Previous Week

- **Status**: Done
- **Priority**: P2

### SPKB-3232 — R1: Special (Multi) Non-DW Buyer Cannot Enter Negative Value on Bid Price and Qty

- **Status**: Done
- **Priority**: P2

### SPKB-3233 — R1: Special (Multi) Non-DW Buyer Cannot Enter Decimal Value on Bid Qty

- **Status**: Done
- **Priority**: P2

### SPKB-3234 — R1: Special (Multi) Non-DW Buyer Cannot Submit Zero Bid

- **Status**: Done
- **Priority**: P2

### SPKB-3235 — R1: Special (Multi) Non-DW Buyer Can Sort by Avail Qty and Target Price

- **Status**: Done
- **Priority**: P2

### SPKB-3236 — R1: Special (Multi) Non-DW Buyer Can Filter by Product ID and Grade

- **Status**: Done
- **Priority**: P2

### SPKB-3237 — R1: Special (Multi) Non-DW Buyer Can See No-Carryover Message Display if They Don't Have Carryover from Previous Week

- **Status**: Done
- **Priority**: P2

### SPKB-3238 — R1: Special (Multi) DW Buyer Cannot Enter Negative Value on Bid Price and Qty

- **Status**: Done
- **Priority**: P2

### SPKB-3239 — R1: Special (Multi) DW Buyer Cannot Enter Decimal Value on Bid Qty

- **Status**: Done
- **Priority**: P2

### SPKB-3240 — R1: Special (Multi) DW Buyer Cannot Submit Zero Bid

- **Status**: Done
- **Priority**: P2

### SPKB-3241 — R1: Special (Multi) DW Buyer Can Sort by Avail Qty and Target Price

- **Status**: Done
- **Priority**: P2

### SPKB-3242 — R1: Special (Multi) DW Buyer Can Filter by Product ID and Grade

- **Status**: Done
- **Priority**: P2

### SPKB-3243 — R1: Special (Multi) DW Buyer Can See No-Carryover Message Display if They Don't Have Carryover from Previous Week

- **Status**: Done
- **Priority**: P2
