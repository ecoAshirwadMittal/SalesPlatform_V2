# Functional — Test Cases

> **323** test cases with label `Functional`.

> Source: Jira project **SPKB** (issuetype = Test)

---

### SPKB-369 — PWS Inventory View | Sales Team Create a New PWS Buyer Code

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`, `PWS`, `QA-Automation`, `Regression`
- **Linked Issues**: SPKB-269

**Description:**
- Sales team is able to create a new PWS buyer code for a new buyer.
- Sales team is able to create a new PWS buyer code for an existing buyer.

### SPKB-375 — PWS Inventory View | Sales Team Access and View PWS Inventory

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`, `automated-playwright`
- **Linked Issues**: SPKB-269

**Description:**
- Sales team is able to access to PWS page when choosing PWS buyer code from Bid-As-Bider page.
- Sales team is able to view:
- PWS inventory table contains columns of SKU, Brand, Model, Carrier, Capacity, Color, Grade, List Price, Avl.Qty, Total.
- Left navigation bar with icons of Inventory, FAQ, Grading Details
- Filter bar and column’s sorting
- Sales team is able to switch buyer code from PWS page.

### SPKB-376 — PWS Inventory View | Validate Inventory Table

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`
- **Linked Issues**: SPKB-269, SPKB-579, SPKB-549

**Description:**
- SKU is pulled from Snowflake: “…. “ (Realtime from Snowflake)
- No duplication.
- Available Q’ty is pulled from Snowflake: “ … “
- Only SKUs with Available Q’ty greater than zero display are pulled from Snowflake
- Available Q’ty displays ‘100+’ if SKU has more than 100 available.

### SPKB-377 — PWS Inventory View | Verify Filter and Sort By

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`, `QA-Automation`
- **Linked Issues**: SPKB-269

**Description:**
- Sales team is able to see the result using filter
- Sales team is able to see the result using sorting on each column

### SPKB-378 — PWS Inventory View | Verify Add/Change/Delete Offer Price and Q’ty 

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`, `QA-Automation`
- **Linked Issues**: SPKB-269, SPKB-1082

**Description:**
- Sales team is able to add offer price and Q’ty on behalf of buyer.
- Sales team is able to change offer price and Q’ty on behalf of buyer.
- Sales team is able to delete offer price and Q’ty on behalf of buyer.
- Sales team is able to see highlight on offer price:
- Highlight orange when offer price lower than List price.
- Highlight green when offer price the same as List price
- Sales team is able to view inline total for each SKU.
- Sales team is able to view the sum of individual totals.

### SPKB-379 — [Outdated] PWS Inventory View | Verify Download Order & Download Listing

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`
- **Linked Issues**: SPKB-269

**Description:**
- Sales team is able to download the Order Listing file
- Sales team is able to download the Order Details file using ‘Download Order’ button
- Only the SKU’s with offer price and Q’ty display on the Order Detail sheet.

### SPKB-380 — PWS Inventory View |Verify PWS Buyer Code Access to PWS Page

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`, `automated-playwright`
- **Linked Issues**: SPKB-269, SPKB-690

**Description:**
- Buyer choose PWS buyer code is able to access to PWS page

### SPKB-381 — PWS Inventory View | Buyer Use All PWS Inventory Page Features

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`
- **Linked Issues**: SPKB-269

**Description:**
Repeat test cases using buyer role account.

### SPKB-399 — PWS Inventory View | Verify realtime inventory

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`
- **Linked Issues**: SPKB-269

**Description:**
Available Quantity - VW_FACT_INVENTORY_PWS_CURRENT

### SPKB-407 — Auction Round 1 | Verify Inventory on Hand-on Table Does Not Contains Duplicate Entries

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`, `Regression`, `automated-playwright`
- **Linked Issues**: SPKB-80

### SPKB-408 — Auction Round 1 | Verify Carryover Bid Price and Q'ty from the Previous Auction for Non-Data Wipe Buyer 

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`, `QA-Automation`, `Regression`
- **Linked Issues**: SPKB-80

### SPKB-409 — Auction Round 1 | Verify Buyer Can Export/Modify/Import and Submit Bids

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`, `Regression`, `automated-playwright`
- **Linked Issues**: SPKB-80

### SPKB-410 — Auction Round 1 | Verify Buyer Can Place Bid Price / Q'ty and Submit Bids 

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`, `Regression`, `automated-playwright`
- **Linked Issues**: SPKB-80

### SPKB-411 — Auction Round 1 | Verify Buyer Can Only Input Positive Real Number for Bid Price and Whole Number for Q'ty 

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`, `Regression`, `automated-playwright`
- **Linked Issues**: SPKB-80

### SPKB-412 — Auction Round 1 | Verify Buyer Can Add/Change/Delete and Re-Submit Bids

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`, `Regression`, `automated-playwright`
- **Linked Issues**: SPKB-80

### SPKB-413 — Auction Round 1 | Verify Submitted Bid File is Created and Transfer to SharePoint

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`, `Regression`, `automated-playwright`
- **Linked Issues**: SPKB-80

### SPKB-414 — Auction Round 1 | Verify Re-Submitted Bid Updates the Existing File in SharePoint 

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`, `Regression`, `automated-playwright`
- **Linked Issues**: SPKB-80

### SPKB-415 — Auction Round 1 | Verify Auction Round 1 Closes According to the Schedule

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`, `Regression`
- **Linked Issues**: SPKB-80

### SPKB-416 — [Outdated] Auction Round 1 | Verify Zero-Bid Files are Created in SharePoint When Round 1 Ends

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`
- **Linked Issues**: SPKB-80

### SPKB-417 — Auction Round 1 | Verify Buyer Can Download Bid File after Round 1 Ends

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`, `Regression`, `automated-playwright`
- **Linked Issues**: SPKB-80

### SPKB-418 — [Outdated] Auction Round 1 | Verify Buyer Receive E-mail Notifications 

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`
- **Linked Issues**: SPKB-80, SPKB-619

### SPKB-463 — Auction Round 2 | Verify Deleted Bid From Round 1 Not Reappear in Round 2

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`, `Regression`
- **Linked Issues**: SPKB-348

**Description:**
- Delete bid using handon table.
- Delete bid on excel sheet and import.

### SPKB-475 — [Outdated] Auction Round 3 | Verify Target Price When EB is Higher Than Max Bid Price from Round 2

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`
- **Linked Issues**: SPKB-267

**Description:**
Note: Target Price round 2 rules can be found in the ticket [SPKB-266] Target Price for Round 2 and Selection Criteria - JIRA
Using this logic below that show in the tables to test Data wipe and non data wipe.  (The yellow highlighted cells have EB higher than Max bid price)

### SPKB-476 — [Outdated] Auction Round 3 | Verify Target Price When 'Max Bid Price from Round 2' is Higher Than 'EB'

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`
- **Linked Issues**: SPKB-267

**Description:**
Note: Target Price round 2 rules can be found in the ticket [SPKB-266] Target Price for Round 2 and Selection Criteria - JIRA
Using this logic below that show in the tables to test Data wipe and non data wipe.  (The yellow highlighted cells have EB higher than Max bid price)

### SPKB-477 — [Outdated] Auction Round 3 | Verify SKU's & Buyers Selected to Round 3

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`
- **Linked Issues**: SPKB-267

**Description:**
Using this logic below that show in the tables to test Data wipe and non data wipe.  (The yellow highlighted cells have EB higher than Max bid price)

### SPKB-478 — [Outdated] Auction Round 3 | Verify Target Price in Export File

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`
- **Linked Issues**: SPKB-267

### SPKB-479 — [Outdated] Auction Round 3 | Verify Submitted Bids in SharePoint File

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`
- **Linked Issues**: SPKB-267

### SPKB-481 — SPT Buyer Start from Round 1| Verify SPT Buyer is Treated the Same as Non-Special Buyer 

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`, `Regression`
- **Linked Issues**: SPKB-265

**Description:**
All inventory display in Round 1
 |  | Target price rule and selection criteria for round 2 apply to this SP buyer
 |  | Target price rule and selection criteria for round 3 apply to this SP buyer
 |  |

### SPKB-482 — Email: SPT Buyer Start from Round 2 | Verify Buyer Receive Invitation Email for Round 2

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`, `Regression`
- **Linked Issues**: SPKB-265

### SPKB-483 — SPT Buyer Start from Round 2 | Verify that Only Special Buyer is Able to Participate in Round 2

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`, `Regression`
- **Linked Issues**: SPKB-265

### SPKB-484 — SPT Buyer Start from Round 2 | Verify Inventory and Target Price

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`, `Regression`
- **Linked Issues**: SPKB-265

**Description:**
All inventory display.   
 |  | Target price for inventories that had bids submitted by other bidders in round 1 should be displayin Max bid + %increment (TGP2 rules)
 |  | Target price for inventories that no bid submitted in round 1 should be displaying the same value of Target Price round 1
 |  |

### SPKB-485 — SPT Buyer Start from Round 2 | Verify Submitted Bid File Transfer to SharePoint

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`, `Regression`
- **Linked Issues**: SPKB-265

### SPKB-486 — SPT Buyer Start from Round 2 | Verify SKU's in Round 3 Should Be Only What Submitted in Round 2 

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`, `Regression`
- **Linked Issues**: SPKB-265

### SPKB-487 — Email: SPT Buyer Start from Round 3 | Verify Special Buyer Receive Invitation Email for Round 3

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`
- **Linked Issues**: SPKB-265

### SPKB-488 — SPT Buyer Start from Round 3 | Verify that Only Special Buyer Able to Patriciate in Round 3

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`, `Regression`
- **Linked Issues**: SPKB-265

**Description:**
Positive: SP buyer able to access round 3.   
 |  | Negative: non-SP buyer should not be able to access if they didn’t submit bid from Round 1
 |  |

### SPKB-489 — SPT Buyer Start from Round 3 | Verify Inventory and Target Price

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`, `Regression`
- **Linked Issues**: SPKB-265

**Description:**
All inventory display.   
 |  | Target price for inventories that had bids submitted by other bidders in round 1,2:  Display Max bid + %increment (Target Price 2,3 rules)
 |  | Target price for inventories that no bid submitted in round 1: Display the same value of Target Price round 1
 |  |

### SPKB-490 — SPT Buyer Start from Round 3 | Verify Submitted Bid File Transfer to SharePoint

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`, `QA-Automation`, `Regression`
- **Linked Issues**: SPKB-265

### SPKB-492 — Verify Week Data is available in Snowflake

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`
- **Linked Issues**: SPKB-285

### SPKB-493 — Verify Auction, Auction Scheduling By Round is available in Snowflake

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`
- **Linked Issues**: SPKB-285

### SPKB-494 — Verify Buyer Data is available in Snowflake

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`
- **Linked Issues**: SPKB-285

### SPKB-495 — Verify Bids submitted by Bidders are available in Snowflake

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`
- **Linked Issues**: SPKB-285

### SPKB-511 — PWS Inventory view - Verify Filter values order by rank

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`, `PWS`, `QA-Automation`, `Regression`
- **Linked Issues**: SPKB-397

**Description:**
Filter dropdown is displayed in the following default sort order
Brand (A-Z)
Category (A-Z)
Carrier (A-Z)
Capacity (Lowest capacity-highest capacity)
Color (A-Z)
Grade (A, B+, B, Value)

### SPKB-512 — PWS Pricing view - Verify Filter values order by rank

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`, `PWS`, `QA-Automation`, `Regression`
- **Linked Issues**: SPKB-397

**Description:**
Filter dropdown is displayed in the following default sort order
Brand (A-Z)
Category (A-Z)
Carrier (A-Z)
Capacity (Lowest capacity-highest capacity)
Color (A-Z)
Grade (A, B+, B, Value)

### SPKB-514 — Email Round 1 is Open | Verify that Only DW/WHS Buyers Receive Invitation Email

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`, `Regression`
- **Linked Issues**: SPKB-468

### SPKB-515 — Email: Round 1 Closing | Verify that DW/WHS Buyers Receive 3 Times Notifications if They Haven't Submit Bid Yet

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`, `Regression`
- **Linked Issues**: SPKB-468

### SPKB-516 — Email: Round 2 is Open | Verify that Only DW/WHS Buyers Who Qualified Receive Invitation Email

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`, `Regression`
- **Linked Issues**: SPKB-468

### SPKB-517 — Email: Round 2 Closing | Verify that DW/WHS Buyers Receive 3 Times Notifications if They Haven't Submit Bid in Round 2 Yet

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`, `Regression`
- **Linked Issues**: SPKB-468

### SPKB-528 — PWS - Verify Upload order functionality with valid order

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`, `PWS`, `Regression`, `automated-playwright`
- **Linked Issues**: SPKB-291

### SPKB-529 —  PWS - Verify Upload order functionality with invalid order(missing columns)

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`, `PWS`, `QA-Automation`, `Regression`
- **Linked Issues**: SPKB-291

**Description:**
duplicate SKU


invalid SKU

empty file

### SPKB-530 — PWS - Order SKU validation

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`, `PWS`, `QA-Automation`, `Regression`
- **Linked Issues**: SPKB-291

### SPKB-531 — PWS - UI validation of an uploaded offer

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`
- **Linked Issues**: SPKB-291

### SPKB-532 — PWS - Verify re-upload order functionality of an updated offer

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`, `PWS`, `QA-Automation`, `Regression`
- **Linked Issues**: SPKB-291

### SPKB-536 — Submitted Bid Files | Verify Round 1,2,3 Submission and Re-Submission File Name in Share Point

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`
- **Linked Issues**: SPKB-471

### SPKB-556 — Buyer Bid Detail Report - DW Buyer

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`
- **Linked Issues**: SPKB-436, SPKB-557, SPKB-558, SPKB-559

**Description:**
As a Sales Rep, Sales Ops or Admin 
I need the bid submissions of the wholesale auction buyers at ecoID-MergeGrade while the auction is live 
So that I can follow up with buyers to increase the participation rate
Acceptance Criteria
Buyer Bid Detail Report is available for Sales Rep, Sales Ops or Admin roles 
Under Reports menu from the left navigation bar
Click on the buyer code hyper link from Buyer Bid Summary report
The report is displayed as shown in the below screen shot 

The report shall consists of bids submitted from current and the previous auction

### SPKB-557 — Buyer Bid Detail Report - non DW Buyer

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`
- **Linked Issues**: SPKB-556, SPKB-436

**Description:**
As a Sales Rep, Sales Ops or Admin 
I need the bid submissions of the wholesale auction buyers at ecoID-MergeGrade while the auction is live 
So that I can follow up with buyers to increase the participation rate
Acceptance Criteria
Buyer Bid Detail Report is available for Sales Rep, Sales Ops or Admin roles 
Under Reports menu from the left navigation bar
Click on the buyer code hyper link from Buyer Bid Summary report
The report is displayed as shown in the below screen shot 

The report shall consists of bids submitted from current and the previous auction

### SPKB-558 — Buyer Bid Detail Report - Special Treatment DW Buyer

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`
- **Linked Issues**: SPKB-556, SPKB-436

**Description:**
As a Sales Rep, Sales Ops or Admin 
I need the bid submissions of the wholesale auction buyers at ecoID-MergeGrade while the auction is live 
So that I can follow up with buyers to increase the participation rate
Acceptance Criteria
Buyer Bid Detail Report is available for Sales Rep, Sales Ops or Admin roles 
Under Reports menu from the left navigation bar
Click on the buyer code hyper link from Buyer Bid Summary report
The report is displayed as shown in the below screen shot 

The report shall consists of bids submitted from current and the previous auction

### SPKB-559 — Buyer Bid Detail Report - Special Treatment non DW Buyer

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`
- **Linked Issues**: SPKB-556, SPKB-436

**Description:**
As a Sales Rep, Sales Ops or Admin 
I need the bid submissions of the wholesale auction buyers at ecoID-MergeGrade while the auction is live 
So that I can follow up with buyers to increase the participation rate
Acceptance Criteria
Buyer Bid Detail Report is available for Sales Rep, Sales Ops or Admin roles 
Under Reports menu from the left navigation bar
Click on the buyer code hyper link from Buyer Bid Summary report
The report is displayed as shown in the below screen shot 

The report shall consists of bids submitted from current and the previous auction

### SPKB-562 — PWS - Verify successful order submission by sales rep

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`
- **Linked Issues**: SPKB-292, SPKB-633

### SPKB-563 — PWS - Verify a proper message is displayed when order submission fails

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`, `QA-Automation`
- **Linked Issues**: SPKB-292

### SPKB-593 — [Outdated]Auction Round 2 | Validate Target Price Round 2

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`

### SPKB-594 — Auction Round 2 | Verify Selected for Round 2: Submitted Bid R1 are Within 15% or $15 

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`, `QA-Automation`, `Regression`

### SPKB-595 — Auction Round 1 | Validate No Duplicated Inventory

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`, `Regression`, `automated-playwright`

### SPKB-623 — Auction Round 2 | Blank Q'ty in Round 1 Not Causing User to Be Able to Lower Q'ty In R2

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`, `QA-Automation`, `Regression`

### SPKB-624 — Auction Round 2 | Verify Sorting Applied Not Cause Bid Price and Q'ty Discrepancy

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`, `QA-Automation`, `Regression`

### SPKB-625 — Auction Round 3 | Verify Bid Price and Q'ty Can Not Be Decreased

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`, `QA-Automation`, `Regression`
- **Linked Issues**: SPKB-871, SPKB-871

### SPKB-626 — SPT Round 2,3  | Verify Multiple-Code Buyers Should Be Able to Re-Access After Submitted Bids 

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`, `QA-Automation`, `Regression`

### SPKB-628 — Auction Round 2 | Verify Target Price Across DW and Non-DataWipe Buyers

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`, `QA-Automation`, `Regression`

### SPKB-629 — PWS - Pricing View - Verify pricing upload

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`, `PWS`, `QA-Automation`, `Regression`

### SPKB-631 — PO - Verify user can create purchase order

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`
- **Linked Issues**: SPKB-632

### SPKB-634 — Auction Rond 1,2 | Verify Display of Ends Time on Dashboard

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`
- **Linked Issues**: SPKB-620

### SPKB-638 — PWS - Inventory/Pricing views - Verify Typeahead functionality on filters

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`, `PWS`, `QA-Automation`, `Regression`
- **Linked Issues**: SPKB-589

### SPKB-644 — PWS Cart Page | Verify Display of Dashboard

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`
- **Linked Issues**: SPKB-575

### SPKB-645 — PWS Cart Page | Verify Display of My-Offer Page When No Item Added to Cart

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`, `PWS`, `Regression`, `automated-playwright`
- **Linked Issues**: SPKB-575

### SPKB-646 — PWS Cart Page | Verify My-Offer Page, SKUs, Qty, Total are Displayed after User Updated Price and Qty on Cart Page

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`, `PWS`, `Regression`, `automated-playwright`
- **Linked Issues**: SPKB-575

### SPKB-647 — PWS Cart Page | Verify 'Reset Offer'

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`, `PWS`, `Regression`, `automated-playwright`
- **Linked Issues**: SPKB-575

### SPKB-648 — PWS Cart Page | Verify 'Download Offer'

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`, `PWS`, `Regression`, `automated-playwright`
- **Linked Issues**: SPKB-575

**Description:**
Verify both locations of ‘Download Offer’
- Download Offer Button on My Offer page
- Download Offer Link under the three-dot drop down on Inventory listing page

### SPKB-649 — [Updated] PWS Inventory Page | Verify 'Download Current View'

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`, `PWS`, `QA-Automation`, `Regression`
- **Linked Issues**: SPKB-575

**Description:**
Updated:  ‘Download Current View’ move to inventory page.
Download Current View shall download the current view of the data grid (filtered view) to a spreadsheet

### SPKB-650 — [Updated] PWS Inventory Page | Verify 'Download All Items'

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`, `PWS`, `QA-Automation`, `Regression`
- **Linked Issues**: SPKB-575

**Description:**
Download All Items move to Inventory page

### SPKB-690 — PWS Inventory View | Verify Non PWS Buyer Code Can't Access to PWS Page

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`, `PWS`, `Regression`, `automated-playwright`
- **Linked Issues**: SPKB-380

**Description:**
- Buyer choose non PWS buyer code is not able to access to PWS page

### SPKB-697 — PWS - Verify Submit Offer Button is Enabled when Items Have Qty > 0

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`, `PWS`, `Regression`, `automated-playwright`
- **Linked Issues**: SPKB-320

**Description:**
Preconditions: At least one item in the order has Qty > 0.
Steps:
1. Navigate to the order page.
2. Observe the "Submit Offer" button state.
Expected Result: "Submit Offer" button is enabled.

### SPKB-698 — PWS - Verify Offer Creation on Clicking Submit Offer Button

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`, `PWS`, `Regression`, `automated-playwright`
- **Linked Issues**: SPKB-320

**Description:**
Preconditions: At least one item in the order has Qty > 0.
Steps:
1. Click on the "Submit Offer" button.
2. Verify an offer is created with a unique Offer ID.
Expected Result: Offer is created successfully, and confirmation message is displayed.

### SPKB-699 — PWS - Verify Offer Data is Saved Correctly

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`, `PWS`, `QA-Automation`, `Regression`
- **Linked Issues**: SPKB-320

**Description:**
Preconditions: Offer is created.
Steps:
1. Retrieve the saved offer details.
Verify that the stored offer contains:
Offer ID
Offer Status as “Sales Review”
Buyer Name
Sales Rep as Blank
Total number of SKUs
Total Qty across all SKUs
Offer Total Price
Offer Date
Updated Date
Tags as Blank
Expected Result: Offer details are saved as per the acceptance criteria.

### SPKB-700 — PWS - Verify Reset Order and Inventory Display on Close (after offer submission)

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`, `PWS`, `QA-Automation`, `Regression`
- **Linked Issues**: SPKB-320

**Description:**
Preconditions: Offer is successfully submitted, and confirmation message is displayed.
Steps:
1. Click the "Close" option on the “Offer Submitted” message.
2. Verify that the order is reset.
3. Verify that the inventory screen is displayed with all items having available Qty > 0.
Expected Result: Inventory screen is displayed with reset items.

### SPKB-701 — PWS - Verify Error Handling when Offer Submission Fails

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`
- **Linked Issues**: SPKB-320

**Description:**
Preconditions: Simulate an error during offer submission
Steps:
1. Click the "Submit Offer" button.
Expected: error message is displayed: “Failed to submit the offer. Please try Re-Submitting” with a Close option.

### SPKB-702 — PWS - Verify Re-Submission After Failure

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`
- **Linked Issues**: SPKB-320

**Description:**
Preconditions: Offer submission previously failed.
Steps:
1. Click the "Close" button on the error message.
2. Verify that the inventory/review page is displayed.
3. Click "Submit Offer" again.
Expected Result: User is able to resubmit the offer.

### SPKB-703 — PWS - Verify “Offers” Menu Item Visibility for Sales Ops, Sales Rep, and Admin

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`, `QA-Automation`
- **Linked Issues**: SPKB-321

**Description:**
User is logged in as Sales Ops, Sales Rep, or Admin under PWS buyer code
Expected:
"Offers" menu item is visible in the left navigation bar for all three roles.

### SPKB-704 — PWS - Offers Queue - Verify Clicking on “Offers” Menu Displays Sales Review Screen with Default Filter

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`, `QA-Automation`
- **Linked Issues**: SPKB-321

**Description:**
Preconditions: User is logged in as Sales Ops, Sales Rep, or Admin.
Steps:
1. Click on the "Offers" menu item.
Expected Result: "Sales Review" status is pre-selected, and the tab is highlighted in yellow.

### SPKB-705 — PWS - Offers Queue - Verify Message Displayed When No Offers are Available for Review

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`
- **Linked Issues**: SPKB-321

**Description:**
Preconditions: No offers are currently in "Sales Review" status.
Steps:
1. Login as SalesOps and click on the "Offers" menu item.
Expected Result: Message “There are currently no offers in this stage” is displayed.

### SPKB-707 — PWS - Offers Queue - Verify Offers Displayed in the Data Grid When Orders in Sales Review Status Available

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`, `PWS`, `QA-Automation`, `Regression`
- **Linked Issues**: SPKB-321

**Description:**
Preconditions: At least one offer is in "Sales Review" status.
Steps:
1. Click on the "Offers" menu item.
Expected: All offers in "Sales Review" status are displayed in the grid.

### SPKB-708 — PWS - Offers queue - Verify Header Level Data Aggregation for Sales Review Status

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`, `PWS`, `QA-Automation`, `Regression`
- **Linked Issues**: SPKB-321

**Description:**
Preconditions: Multiple offers exist in "Sales Review" status.
Click on the "Offers" menu item.
Verify that the following fields display correct totals across all offers pending for Sales Review:
1. Total number of offers
2. Sum of SKU’s
3. Sum of Quantity
4. Sum of Offer Totals
Expected Result: The total values displayed in the header match the expected sums from all offers.

### SPKB-709 — PWS - Offers Queue - Verify Offer Data Grid Displays Correct Fields for Sales Review Status Orders

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`, `PWS`, `QA-Automation`, `Regression`
- **Linked Issues**: SPKB-321

**Description:**
Preconditions: At least one offer is in "Sales Review" status.
Click on the "Offers" menu item.
Verify that each row in the data grid contains the following:
1. Offer ID (Unique)
2. Order Number (Blank if not received from Oracle)
3. Offer Status ("Sales Review")
4. Buyer Name
5. Sales Rep (Blank)
6. Sum of SKU’s in the offer
7. Sum of Quantity in the offer
8. Offer Total Price (Formatted as $xx,xxx)
9. Offer Date (mm/dd/yy, EST)
10. Last Updated Date (mm/dd/yy, EST)
11. Tags (Blank)
Expected Result: Data grid accurately displays all required fields.

### SPKB-710 — PWS - Offers Queue - Verify Offer Order Number is Blank Until Received from Oracle

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`
- **Linked Issues**: SPKB-321

**Description:**
Preconditions: Offer is created but not submitted.
Steps:
1. Click on the "Offers" menu item.
Expected: Order Number field is blank in the data grid for such offers.

### SPKB-711 — PWS - Offers Queue - Verify Role-Based Access to Offers

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`, `QA-Automation`
- **Linked Issues**: SPKB-321

**Description:**
Steps:
1. Log in as a PWS Buyer and check if the "Offers" menu is available.
2. Log in as Sales Ops, Sales Rep, and Admin, and check if they can access the "Offers" menu.
Expected Result:
1. "Offers" menu should not be visible to PWS Buyers.
2. "Offers" menu should be visible to Sales Ops, Sales Rep, and Admin.

### SPKB-719 — Verify TGP-R2 When PO Higher Than EB and MaxBid

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`, `QA-Automation`, `Regression`
- **Linked Issues**: SPKB-682

### SPKB-720 — Verify TGP-R2 When EB Higher Than PO and MaxBid

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`, `QA-Automation`, `Regression`
- **Linked Issues**: SPKB-682

### SPKB-721 — Verify TGP-R2 When MaxBid Higher Than EB and PO

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`, `QA-Automation`, `Regression`
- **Linked Issues**: SPKB-682

### SPKB-722 — Verify TGP-R3 When PO Higher Than EB and MaxBid

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`, `QA-Automation`, `Regression`
- **Linked Issues**: SPKB-682

### SPKB-723 — Verify TGP-R3 When EB Higher Than PO and MaxBid

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`, `QA-Automation`, `Regression`
- **Linked Issues**: SPKB-682

### SPKB-724 — Verify TGP-R3 When MaxBid Higher Than EB and PO

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`, `QA-Automation`, `Regression`
- **Linked Issues**: SPKB-682

### SPKB-726 — Verify Submitted Bid Round2 File in SharePoint  

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`, `QA-Automation`, `Regression`
- **Linked Issues**: SPKB-715

**Description:**
All submitted bids from round 1 should also display in a SharePoint file Round 2

### SPKB-727 — Verify TGP-R2 and TGP-R3 on Special Buyers

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`, `QA-Automation`, `Regression`
- **Linked Issues**: SPKB-682

### SPKB-744 — Remove PO | Verify Single Code DW/WHS Buyer is Able to Access to Auction Round 1, 2 

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`
- **Linked Issues**: SPKB-469

### SPKB-745 — Remove PO | Verify Single Code PWS Buyer is Able to Access to PWS Page

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`
- **Linked Issues**: SPKB-469

### SPKB-746 — Remove PO | Verify Multiple Code Buyer Who Has PO Code is Able to Access to Auction Round 1,2 Using DW/WHS Buyer Codes

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`
- **Linked Issues**: SPKB-469

### SPKB-747 — Remove PO | Verify Multiple Code Buyer Who Has PO Code is Able to Access to PWS Page Using PWS Code

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`
- **Linked Issues**: SPKB-469

### SPKB-748 — Remove PO | Verify Choose-Buyer-Code Page Should Not Display PO Buyer Code

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`
- **Linked Issues**: SPKB-469

### SPKB-749 — Remove PO | Verify Dropdown on Choose-Buyer-Code Page Should Not Display PO Buyer Code

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`
- **Linked Issues**: SPKB-469

### SPKB-750 — Remove PO | Verify Dropdown on PWS Page Should Not Display PO Buyer Code

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`
- **Linked Issues**: SPKB-469

### SPKB-751 — Add Submit Button | Verify Message, File Name and Total Row on the Import Modal

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`, `automated-playwright`
- **Linked Issues**: SPKB-470

### SPKB-752 — Add Submit Button | Verify Submit-Bids Button on Import Modal

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`, `automated-playwright`
- **Linked Issues**: SPKB-470

### SPKB-759 — PWS - Offers Queue - Verify Message Displayed When No Offers are in Buyer Acceptance Stage

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`
- **Linked Issues**: SPKB-446

**Description:**
Preconditions: No offers are currently in "Buyer Acceptance" status.
Steps:
1. Click on the "Buyer Acceptance" tab.
Expected: Message “There are currently no offers in this stage” is displayed.

### SPKB-760 — PWS - Offers Queue - Verify Offers Displayed in the Data Grid When Available in Buyer Acceptance Stage

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`, `PWS`, `QA-Automation`, `Regression`
- **Linked Issues**: SPKB-446

**Description:**
Preconditions: At least one offer is in "Buyer Acceptance" status.
Steps:
1. Click on the "Buyer Acceptance" tab.
Expected: All offers in "Buyer Acceptance" status are displayed in the grid.

### SPKB-761 — PWS - Offers Queue - Verify Header Level Data Aggregation in Buyer Acceptance Tab

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`, `PWS`, `QA-Automation`, `Regression`
- **Linked Issues**: SPKB-446, SPKB-1127

**Description:**
Preconditions: Multiple offers exist in "Buyer Acceptance" status.
Steps:
1. Click on the "Buyer Acceptance" tab.
The following fields display correct totals across all offers pending for Buyer Acceptance:
1. Total number of offers
2. Sum of SKU’s
3. Sum of Quantity
4. Sum of Offer Totals
Expected: The total values displayed in the header match the expected sums from all offers.

### SPKB-762 — PWS - Offers Queue - Verify Offer Data Grid Displays Correct Fields for Buyer Acceptance

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`, `PWS`, `QA-Automation`, `Regression`
- **Linked Issues**: SPKB-446

**Description:**
Preconditions: At least one offer is in "Buyer Acceptance" status.
Steps:
1. Click on the "Buyer Acceptance" tab.
Verify that each row in the data grid contains the following:
1. Offer ID (Unique)
2. Order Number (Blank if not received from Oracle)
3. Offer Status ("Buyer Acceptance")
4. Buyer Name
5. Sales Rep (Blank)
6. Sum of SKU’s in the offer
7. Sum of Quantity in the offer
8. Offer Total Price (Formatted as $xx,xxx)
9. Offer Date (mm/dd/yy format)
10. Last Updated Date (mm/dd/yy format)
11. Tags (Blank)
Expected: Data grid accurately displays all required fields, and the "Messages" column is not present.

### SPKB-763 — PWS - Offers Queue - Verify Message Displayed When No Offers are in Ordered Stage

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`
- **Linked Issues**: SPKB-447

**Description:**
Preconditions: No offers are currently in "Ordered" or "Pending Order" status.
Steps:
1. Click on the "Ordered" tab.
Expected: Message “There are currently no orders in this stage” is displayed.

### SPKB-764 — PWS - Offers Queue - Verify Offers Displayed in the Data Grid When Available in Ordered Stag

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`, `PWS`, `QA-Automation`, `Regression`
- **Linked Issues**: SPKB-447

**Description:**
Preconditions: At least one offer is in "Ordered" or "Pending Order" status.
Steps:
1. Click on the "Ordered" tab.
Expected: All offers in "Ordered" or "Pending Order" status are displayed in the grid.

### SPKB-765 — PWS - Offers Queue - Verify Header Level Data Aggregation in Ordered Tab

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`, `PWS`, `QA-Automation`, `Regression`
- **Linked Issues**: SPKB-447, SPKB-1143

**Description:**
Preconditions: Multiple offers exist in "Ordered" or "Pending Order" status.
Steps:
1. Click on the "Ordered" tab.
Verify that the following fields display correct totals across all offers pending for Ordered stage:
1. Total number of orders
2. Sum of SKU’s
3. Sum of Quantity
4. Sum of Offer Totals
Expected: The total values displayed in the header match the expected sums from all orders.

### SPKB-766 — PWS - Offers Queue - Verify Offer Data Grid Displays Correct Fields for Ordered Status

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`, `PWS`, `QA-Automation`, `Regression`
- **Linked Issues**: SPKB-447

**Description:**
Preconditions: At least one offer is in "Ordered" or "Pending Order" status.
Steps:
1. Click on the "Ordered" tab.
Verify that each row in the data grid contains the following:
1. Offer ID (Unique)
2. Order Number (Received from Oracle for the corresponding offer)
3. Offer Status ("Ordered" or "Pending Order")
4. Buyer Name
5. Sales Rep (Blank)
6. Sum of SKU’s in the offer
7. Sum of Quantity in the offer
8. Offer Total Price (Formatted as $xx,xxx)
9. Offer Date (mm/dd/yy format)
10. Last Updated Date (mm/dd/yy format)
11. Tags (Blank)
Expected: Data grid accurately displays all required fields.

### SPKB-767 — PWS - Offers Queue - Verify Offer Order Number is Received from Oracle

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`
- **Linked Issues**: SPKB-447

**Description:**
Preconditions: Offer is submitted to Oracle.
Steps:
1. Click on the "Ordered" tab.
2. Locate an offer that has completed Oracle processing.
Expected Result: Order Number field is populated once received from Oracle.

### SPKB-768 — PWS - Offers Queue - Verify Message When No Offers Are in Rejected Stage

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`
- **Linked Issues**: SPKB-448

**Description:**
Preconditions: No offers with status "Rejected By Buyer" or "Rejected By Seller".
Steps:
1. Click on the “Rejected” tab.
Expected: Show message: “There are currently no offers in this stage”.

### SPKB-769 — PWS - Offers Queue - Verify Rejected Offers Are Displayed When Available

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`, `PWS`, `QA-Automation`, `Regression`
- **Linked Issues**: SPKB-448

**Description:**
Preconditions: At least one offer has status “Rejected By Buyer” or “Rejected By Seller”.
Steps:
1. Click on the “Rejected” tab.
Expected: Offers with correct rejected status are shown.

### SPKB-770 — PWS - Offers Queue - Verify Header Level Data Aggregation in Rejected Status

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`, `PWS`, `QA-Automation`, `Regression`
- **Linked Issues**: SPKB-448, SPKB-776, SPKB-1135

**Description:**
Header Level
Total - Total number of offers and orders (Offer status as “Sales Review” + “Buyer Acceptance“ + “Ordered“ & “Pending Order“ + “Rejected By Buyer“ & “Rejected By Seller“)
SKU’s - Sum of SKU’s across all offers and orders with Offer status as “Sales Review” + “Buyer Acceptance“ + “Ordered“ & “Pending Order“ + “Rejected By Buyer“ & “Rejected By Seller“
Qty - Sum of Quantity across all offers and orders with Offer status as “Sales Review” + “Buyer Acceptance“ + “Ordered“ & “Pending Order“ + “Rejected By Buyer“ & “Rejected By Seller“
Price - Sum of Offer and Order Totals across all offers with Offer status as “Sales Review” + “Buyer Acceptance“ + “Ordered“ & “Pending Order“ + “Rejected By Buyer“ & “Rejected By Seller“

### SPKB-771 — PWS - Offers Queue - Verify Offer Data Grid Displays Correct Fields for Rejected Status

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`, `PWS`, `QA-Automation`, `Regression`
- **Linked Issues**: SPKB-448

**Description:**
Data Grid
Offer ID - Unique offer id generated for each offer
Order Number - Blank
Offer Status - The current status of the offer (“Rejected By Buyer“ or “Rejected By Seller“ at this stage)
Buyer - Display the buyer name of submitted offer
Sales Rep - Blank for now
SKUs - Sum of SKU’s in the current offer line
Qty - Sum of quantity in the current offer line
Offer Price - Sum of offer total in $xx,xxx format
Messages - Drop this column for now
Offer Date - Offer created date in mm/dd/yy format
Last Updated - Last updated date in mm/dd/yy format
Tags - Blank for now

### SPKB-775 — PWS - Offers Queue - Verify Total Offers Are Displayed When Available

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`, `PWS`, `QA-Automation`, `Regression`
- **Linked Issues**: SPKB-449

**Description:**
Preconditions: At least one offer is submitted
Steps:
1. Click on the “Total” tab.
Expected: All submitted offers are shown.

### SPKB-776 — PWS - Offers Queue - Verify Header Level Data Aggregation in Total Tab

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`, `PWS`, `QA-Automation`, `Regression`
- **Linked Issues**: SPKB-770, SPKB-449

**Description:**
Rejected - Total number of offers rejected by buyers or sales team (Offer status as “Rejected By Buyer“ & “Rejected By Seller“)
SKU’s - Sum of SKU’s across all offers with Offer status as “Rejected By Buyer“ & “Rejected By Seller“
Qty - Sum of Quantity across all offers with Offer status as “Rejected By Buyer“ & “Rejected By Seller“
Price - Sum of Offer Totals across all offers with Offer status as “Rejected By Buyer“ & “Rejected By Seller“

### SPKB-777 — Inventory Update | Verify Message Added to the Create-Auction Modal

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`
- **Linked Issues**: SPKB-564

### SPKB-778 — Inventory Update | Verify Inventory Config Page that Sales Reps Able to Add, Modify and Delete SKU, Grade, Q'ty

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`
- **Linked Issues**: SPKB-564

### SPKB-779 — PWS - Offers Queue - Verify Offer Data Grid Displays Correct Fields for Total Tab

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`, `PWS`, `QA-Automation`, `Regression`
- **Linked Issues**: SPKB-449

**Description:**
Data Grid
Offer ID - Unique offer id generated for each offer
Order Number - Order number received from Oracle
Offer Status - The current status of the offer (“Sales Review” or “Buyer Acceptance“ or “Ordered“ or “Pending Order“ or “Rejected By Buyer“ or “Rejected By Seller“)
Buyer - Display the buyer name of submitted offer
Sales Rep - Blank for now
SKUs - Sum of SKU’s in the current offer line
Qty - Sum of quantity in the current offer line
Offer Price - Sum of offer total in $xx,xxx format
Messages - Drop this column for now
Offer Date - Offer created date in mm/dd/yy format
Last Updated - Last updated date in mm/dd/yy format
Tags - Blank for now

### SPKB-780 — Inventory Update | Verify Additional Qty Apply to Future Auctions Until It Got Changed 

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`
- **Linked Issues**: SPKB-564

### SPKB-781 — Inventory Update | Verify Inventory Page Not Change after Added Additional Qty Config

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`
- **Linked Issues**: SPKB-564

### SPKB-783 — Auction Round 1 | Verify Time that Submitted Bid File Sent to SharePoint

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`, `Regression`

### SPKB-785 — Inventory Update | Verify Additional Qty is Applied only Wholesale Buyer, Not DataWipe Buyer

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`
- **Linked Issues**: SPKB-564

### SPKB-787 — Inventory Update | Verify Q'ty on Export file and Submitted Bid File on SharePoint

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`
- **Linked Issues**: SPKB-564

**Description:**
Additional qty apply on hand-on table and export file, but not SharePoint file.

### SPKB-788 — PWS Offer Details | Verify Default Offer Status Set to 'Sales Review' Once an Offer is Submitted.

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`, `PWS`, `Regression`, `automated-playwright`
- **Linked Issues**: SPKB-322

### SPKB-789 — PWS Offer Details | Verify Default Actions Status Set to Either 'Accept' or 'Counter' Depends on Offer and Listing Prices

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`
- **Linked Issues**: SPKB-322

### SPKB-790 — PWS Offer Details | Verify Counter-Offer Items Allows Sales Rep to Enter Price, Q'ty

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`, `PWS`, `Regression`, `automated-playwright`
- **Linked Issues**: SPKB-322

### SPKB-791 — PWS Offer Details | Verify Accept/Reject Items Not Allows Sales Rep to Enter Price, Q'ty

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`, `PWS`, `Regression`, `automated-playwright`
- **Linked Issues**: SPKB-322

### SPKB-792 — PWS Offer Details | Verify Original Offer Summary Box Not Change at Anytime

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`, `PWS`, `Regression`, `automated-playwright`
- **Linked Issues**: SPKB-322

### SPKB-793 — PWS Offer Details | Verify Counteroffer Summary Box Updating When Sales Rep Enter Counter-Offer Price, Qty

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`, `PWS`, `Regression`, `automated-playwright`
- **Linked Issues**: SPKB-322

### SPKB-794 — PWS Offer Details | Verify Calculation of Counter Inline Total,  Counter Summary Box

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`, `PWS`, `Regression`, `automated-playwright`
- **Linked Issues**: SPKB-322

### SPKB-795 — PWS Offer | Sales Action - Verify Complete-Review Submission Not Allow If Counter Price/Qty Missing

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`, `PWS`, `Regression`, `automated-playwright`
- **Linked Issues**: SPKB-450

### SPKB-797 — PWS Offer | Sales Action - Verify Complete Review with All SKUs are Accepted or Declined or Counter

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`, `PWS`, `Regression`, `automated-playwright`
- **Linked Issues**: SPKB-450

### SPKB-798 — PWS Offer | Sales Action - Verify Complete Review with Mix Status of Accepted, Rejected and Counter

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`, `PWS`, `Regression`, `automated-playwright`
- **Linked Issues**: SPKB-450

**Description:**
Mix status.

### SPKB-799 — PWS Offer | Sales Action - Verify Oracle Create Order when Offer Accepted

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`, `PWS`, `QA-Automation`, `Regression`
- **Linked Issues**: SPKB-450

### SPKB-800 — PWS Offer | Sales Action - Verify Oracle Not Create Order when Offer Declined/ Cancelled

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`, `PWS`, `QA-Automation`, `Regression`
- **Linked Issues**: SPKB-450

### SPKB-801 — PWS Offer | Sales Action - Verify Oracle Not Create Order while Counter is Still Ongoing

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`, `PWS`, `QA-Automation`, `Regression`
- **Linked Issues**: SPKB-450

### SPKB-806 — PWS Offer Details | Verify Dashboard Displaying All Info as Per Mockup

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`
- **Linked Issues**: SPKB-322

### SPKB-813 — PWS Offer | Sales Action - Verify Offer Status is Pending if It Failed to Submit the Offer

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`, `PWS`, `QA-Automation`, `Regression`
- **Linked Issues**: SPKB-450

### SPKB-828 — PWS - Offer Queue - Verify “Counter Offers” Menu Item Is Visible to PWS Buyers

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`
- **Linked Issues**: SPKB-637

**Description:**
1. Login as PWS buyer
Expected: “Counter Offers” menu item is visible and clickable

### SPKB-829 — PWS - Offer Queue - Verify “Counter Offers” Displays Grid View of Countered Offers (multiple offers)

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`, `PWS`, `QA-Automation`, `Regression`
- **Linked Issues**: SPKB-637

**Description:**
Precondition: Multiple counter offers exist for the logged-in buyer.
Steps:
1. Click on "Counter Offers" menu item
Expected: 
1. Grid with countered offers is displayed
2. Only offers with status “Buyer Acceptance” are listed
3. Offers are sorted in ascending order of Offer Date
4. Offer ID Is Clickable and Navigates to Details Page

### SPKB-837 — Round 3 Enhancements | Verify Target Price Round 3

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`, `QA-Automation`, `Regression`
- **Linked Issues**: SPKB-670

### SPKB-838 — Round 3 Enhancements | Verify Selection Criteria for Qualified Buyer Codes and SKUs

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`, `QA-Automation`, `Regression`
- **Linked Issues**: SPKB-670

### SPKB-839 — Round 3 Enhancements | Verify Removal of Unnecessary Steps Before Round 3 Starts

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`
- **Linked Issues**: SPKB-670

### SPKB-840 — Round 3 Enhancements | Verify Round 3 Starts After Target Price Calculations are Completed

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`, `Regression`
- **Linked Issues**: SPKB-670

### SPKB-841 — Round 3 Enhancements | Verify Email Send to Sales Rep When Round 3 Starts

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`, `Regression`
- **Linked Issues**: SPKB-670

### SPKB-843 — PWS - Counter Offer Details - Verify that clicking an offer line on the Counter Offer queue opens the Counter Offer detail page

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`, `PWS`, `Regression`, `automated-playwright`
- **Linked Issues**: SPKB-451

**Description:**
Expected:
Counter Offer detail page is displayed with correct offer details

### SPKB-844 — PWS - Counter Offer Details - Verify Accepted items are displayed as non-editable

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`, `PWS`, `Regression`, `automated-playwright`
- **Linked Issues**: SPKB-451

**Description:**
Expected: Fields for Accepted items are read-only

### SPKB-845 — PWS - Counter Offer Details - Verify Action Buttons (Accept/Reject) are visible and selectable for Countered items	

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`, `PWS`, `Regression`, `automated-playwright`
- **Linked Issues**: SPKB-451

**Description:**
Expected: Each Countered item has Accept and Reject selction

### SPKB-846 — PWS - Counter Offer Details - Verify Original Offer displays buyer’s initial submission

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`, `PWS`, `Regression`, `automated-playwright`
- **Linked Issues**: SPKB-451

**Description:**
Expected: Static details of original offer are displayed

### SPKB-847 — PWS - Counter Offer Details - Verify Counter Offer displays Sales Rep’s response

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`, `PWS`, `Regression`, `automated-playwright`
- **Linked Issues**: SPKB-451

**Description:**
Expected: Static details of counter offer are shown

### SPKB-848 — PWS - Counter Offer Details - Verify Final Offer updates dynamically as items are Accepted or Rejected 

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`, `PWS`, `Regression`, `automated-playwright`
- **Linked Issues**: SPKB-451

**Description:**
Expected: Final Offer updates real-time, includes only Accepted items

### SPKB-849 — PWS - Counter Offer Details - Verify Cancel Order Functionality

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`, `PWS`, `Regression`, `automated-playwright`
- **Linked Issues**: SPKB-451

**Description:**
1. Verify “...” button displays “Cancel Order” when clicked
Expected: Cancel Order option is shown
2. Verify Cancel Order confirmation popup appears
Expected: Popup message: "Are you sure you want to cancel this order?"
3. Verify clicking “NO” in the popup closes it without changes
Expected: Popup is closed, no status change
4. Verify clicking “YES” sets Offer Status to Declined and does not send to Oracle
Expected: Status is set to Declined, no Oracle interaction triggered

### SPKB-850 — PWS - Counter Offer Details - Verify validation prevents submission if any Countered SKU is not Accepted or Rejected

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`, `PWS`, `Regression`, `automated-playwright`
- **Linked Issues**: SPKB-451

**Description:**
Expected: Error popup: "All countered SKUs must be either Accepted or Rejected before submitting."

### SPKB-851 — PWS - Counter Offer Details - Verify submission proceeds if all Countered SKUs are Accepted or Rejected

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`, `PWS`, `Regression`, `automated-playwright`
- **Linked Issues**: SPKB-451

**Description:**
Expected: No error popup shown

### SPKB-852 — PWS - Counter Offer Details - Verify submission with all SKUs Accepted sets Offer Status to Ordered and sends to Oracle

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`, `PWS`, `Regression`, `automated-playwright`
- **Linked Issues**: SPKB-451

**Description:**
Expected: Status = Ordered, all SKUs sent to Oracle

### SPKB-853 — PWS - Counter Offer Details - Verify submission with all SKUs Rejected sets Offer Status to Declined and does not send to Oracle

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`, `PWS`, `Regression`, `automated-playwright`
- **Linked Issues**: SPKB-451

**Description:**
Expected: Status = Declined, no Oracle call

### SPKB-854 — PWS - Counter Offer Details - Verify partial Accept/Reject submission sets Offer Status to Ordered and only Accepted SKUs are sent to Oracle

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`, `PWS`, `Regression`, `automated-playwright`
- **Linked Issues**: SPKB-451

**Description:**
Expected: Status = Ordered, only Accepted SKUs sent

### SPKB-855 — PWS - Counter Offer Details - Verify canceled order sets status to Declined without Oracle interaction

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`, `PWS`, `Regression`, `automated-playwright`

**Description:**
Expected: Status = Declined, nothing sent

### SPKB-861 — Round 3 Download | Verify Filtering by Week for Round 3Qualified Buyers  (Display a grid data)

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`, `QA-Automation`, `Regression`
- **Linked Issues**: SPKB-671

### SPKB-862 — Round 3 Download | Verify Sales Rep is Able to Select Options of "Download" or "View Data"

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`, `QA-Automation`, `Regression`
- **Linked Issues**: SPKB-671

### SPKB-863 — Round 3 Download | Validate Bid Data Show ONLY SKUs that Buyer Codes Submitted from R1, R2 

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`, `QA-Automation`, `Regression`
- **Linked Issues**: SPKB-671

### SPKB-864 — Round 3 Download | Verify Bid Data for Special Buyer Display All SKUs with Correct Target Price

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`, `QA-Automation`, `Regression`
- **Linked Issues**: SPKB-671

### SPKB-865 — Round 3 Download | Verify Downloading Excel File with Correct File Name and Relevant Bid Data

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`, `QA-Automation`, `Regression`
- **Linked Issues**: SPKB-671

### SPKB-866 — Round 3 Download | Verify Download-To-Excel File Has Columns of Company Name, Buyer Codes, SubmittedBy/On

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`, `Regression`
- **Linked Issues**: SPKB-671

### SPKB-872 — [Invalid]Round 3 Upload | Verify Auction Dashboard Display SKUs that Has "Y" (AcceptMaxBid) and Entered Bid Price When Uploaded the File

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`, `QA-Automation`
- **Linked Issues**: SPKB-672

**Description:**
Note:  Sales Rep’s not using Auction dashboard to submit bid round 3.  This test case is invalid

### SPKB-873 — [Invalid] Round 3 Upload | Verify Any Buyer Codes that Not Uploaded AccepMaxBid Values are Not Able to Access to Auction Upsell Dashboard

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`

### SPKB-874 — Round 3 Upload | Verify Display Submitted Time and Sales Reps Who Uploaded the File

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`, `Regression`
- **Linked Issues**: SPKB-672

### SPKB-875 — Round 3 Upload | Verify Only Buyer Codes that Submitted Bid File Send to SharePoint After Uploaded

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`, `QA-Automation`, `Regression`
- **Linked Issues**: SPKB-672

### SPKB-876 — Round 3 Upload | Verify SharePoint File Shows SKUs Included R1, R2 

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`, `QA-Automation`, `Regression`
- **Linked Issues**: SPKB-672

### SPKB-877 — Round 3 Upload | Verify Re-Uploads File Updates the Existing SharePoint Files 

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`, `QA-Automation`, `Regression`
- **Linked Issues**: SPKB-672

### SPKB-879 — Round 3 Upload | Verify Submit Bids on SKUs with Multiple Grades

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`, `QA-Automation`, `Regression`
- **Linked Issues**: SPKB-672

### SPKB-889 — Round 2 | Verify Export/ Import Bid Sheet 

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`, `QA-Automation`, `Regression`

### SPKB-890 — Auction Round 2 | Verify Submitted or Re-Submitted Bid Sent to SharePoint

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`, `QA-Automation`, `Regression`

### SPKB-891 — Performance Test Auction Round 1

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`, `Regression`

### SPKB-894 — PWS - Verify Display Sales Rep in Offer Summary, Details and Counter Pages

- **Status**: Done
- **Priority**: P2
- **Labels**: `6.1.0`, `Functional`, `PWS`, `QA-Automation`, `Regression`
- **Linked Issues**: SPKB-807

**Description:**
Add a Sales Rep in the buyer management at buyer level

### SPKB-896 — PWS - Verify all PWS page URLs are protected and enforce authentication/authorization.

- **Status**: Done
- **Priority**: P2
- **Labels**: `6.1.0`, `Functional`, `PWS`, `QA-Automation`, `Regression`
- **Linked Issues**: SPKB-881

**Description:**
Expected: Authenticated PWS buyer can access PWS pages via direct URL
Order: https://buy-qa.ecoatmdirect.com/p/order
Offers: https://buy-qa.ecoatmdirect.com/p/offers
Offer Details: https://buy-qa.ecoatmdirect.com/p/offer/<OfferId>
Counter Offers: https://buy-qa.ecoatmdirect.com/p/counteroffers
Offer Details: https://buy-qa.ecoatmdirect.com/p/counteroffer/<OfferId>
Pricing: https://buy-qa.ecoatmdirect.com/p/pricing

### SPKB-897 — PWS - Verify Access Control for Offers & Orders

- **Status**: Done
- **Priority**: P2
- **Labels**: `6.1.0`, `Functional`, `PWS`, `QA-Automation`, `Regression`
- **Linked Issues**: SPKB-881

**Description:**
Expected:
1. Logged-in PWS Buyer sees only Offers related to associated Buyer.
2. Buyer accessing not belonging Offer ID is redirected to landing page.
3. Browser back/forward navigation doesn’t allow access after logout.

### SPKB-901 — Session Time out Widget - Verify the Admin can set the platform session timeout in seconds.

- **Status**: Done
- **Priority**: P2
- **Labels**: `6.1.0`, `Functional`
- **Linked Issues**: SPKB-590

**Description:**
Expected: Timeout is updated and reflected in user session duration.

### SPKB-902 — Session Timeout Widget - Verify the Admin can set how frequently user activity is checked.

- **Status**: Done
- **Priority**: P2
- **Labels**: `6.1.0`, `Functional`
- **Linked Issues**: SPKB-590

**Description:**
Expected: Platform checks session activity at the defined interval.

### SPKB-903 — Session Timeout Widget - Verify the Admin can set the warning threshold (in seconds)

- **Status**: Done
- **Priority**: P2
- **Labels**: `6.1.0`, `Functional`
- **Linked Issues**: SPKB-590

**Description:**
Expected: User receives a warning popup X seconds before timeout.

### SPKB-904 — Session Time out Widget - Verify that a warning popup is shown X seconds before session expires

- **Status**: Done
- **Priority**: P2
- **Labels**: `6.1.0`, `Functional`
- **Linked Issues**: SPKB-590

**Description:**
Expected: Popup appears with countdown timer, extend and logout buttons.

### SPKB-905 — Session Time out Widget - Verify the warning popup layout matches the Figma design.

- **Status**: Done
- **Priority**: P2
- **Labels**: `6.1.0`, `Functional`
- **Linked Issues**: SPKB-590

**Description:**
Expected: All UI elements and styling match the provided design spec

### SPKB-906 — Session Time out Widget - Verify Widget Popup functionalities

- **Status**: Done
- **Priority**: P2
- **Labels**: `6.1.0`, `Functional`, `PWS`, `QA-Automation`, `Regression`
- **Linked Issues**: SPKB-590

**Description:**
1. Verify the timer counts down dynamically every second.
Expected: Timer decreases in real-time until user takes action or timeout occurs.
2. Verify that clicking “Keep Me Signed In” resets session timeout and closes popup.
Expected: Timer resets, popup disappears, user remains logged in.
3. Verify clicking “Sign out” ends the session immediately.
Expected: User is logged out and redirected to the login page.
4. Verify the session expires and user is logged out if no action is taken before countdown ends.
Expected: User is logged out and redirected to login page.

### SPKB-907 — Session Timeout Widget - Verify Session Behavior Across Tabs

- **Status**: Done
- **Priority**: P2
- **Labels**: `6.1.0`, `Functional`
- **Linked Issues**: SPKB-590

**Description:**
1. Verify session is shared across all open tabs in the same browser.
Expected: Activity in one tab resets timeout for all tabs.
2. Verify the warning popup appears in all tabs when timeout threshold is reached.
Expected: Synchronized warning popups show with consistent timer.
3. Extend session in one tab and verify popups disappear in others.
Expected: All popups close and timer resets across all tabs.
4. Verify that when no interaction with any popup - all tabs logout after timer ends.
Expected: All tabs show login screen simultaneously.

### SPKB-933 — PWS - Verify Redirect to Counter Offers Page on Login When > 1 Pending Offers Exist

- **Status**: Done
- **Priority**: P2
- **Labels**: `6.1.0`, `Functional`, `PWS`, `QA-Automation`, `Regression`
- **Linked Issues**: SPKB-895

**Description:**
Precondition: Buyer has 2 or more counter offers for selected buyer code.
Verify that the buyer is redirected to the Counter Offers landing page upon successful login if there are pending counter offers for the selected buyer code
Expected:Buyer lands on the Counter Offers page.

### SPKB-934 — PWS - Verify Redirect to Inventory Listing Page on Login When No Counter Offers Exist

- **Status**: Done
- **Priority**: P2
- **Labels**: `6.1.0`, `Functional`
- **Linked Issues**: SPKB-895

**Description:**
Precondition: Buyer has 0 counter offers for selected buyer code.
Verify that the buyer is redirected to the PWS Inventory listing page if there are no counter offers for the selected buyer code.
Expected: Buyer lands on the PWS Inventory page.

### SPKB-935 — PWS - Redirect to Offer Detail Page on Login When Exactly One Counter Offer Exists

- **Status**: Done
- **Priority**: P2
- **Labels**: `6.1.0`, `Functional`, `PWS`, `QA-Automation`, `Regression`
- **Linked Issues**: SPKB-895

**Description:**
Precondition: Buyer has 1 counter offer.
Verify that the buyer is redirected to the Offer Detail page directly when there is exactly one counter offer in the queue.
Expected: Buyer lands directly on the Counter Offer Detail page.

### SPKB-936 — PWS - Buyer with Multiple Buyer Codes Selects PWS Code and Sees Correct Redirect

- **Status**: Done
- **Priority**: P2
- **Labels**: `6.1.0`, `Functional`
- **Linked Issues**: SPKB-895

**Description:**
Verify correct redirection when a buyer with multiple buyer codes selects a PWS code.
Expected: Redirect logic follows the same as , ,  based on number of offers for selected code.

### SPKB-945 — Verify Toggle Flag Enabled/Disabled Submit Flow During Auction Round 1

- **Status**: Done
- **Priority**: P2
- **Labels**: `6.1.0`, `Functional`
- **Linked Issues**: SPKB-443

### SPKB-946 — Verify Toggle Flag Enabled/Disabled Submit Flow During Auction Round 2

- **Status**: Done
- **Priority**: P2
- **Labels**: `6.1.0`, `Functional`
- **Linked Issues**: SPKB-443

### SPKB-947 — Verify Toggle Flag Enabled/Disabled Submit Flow During Auction Round 3

- **Status**: Done
- **Priority**: P2
- **Labels**: `6.1.0`, `Functional`
- **Linked Issues**: SPKB-443

### SPKB-952 — PWS - Offers Queue - Verify that the "Revert back to Sales Review" button is visible under Buyer Acceptance tab

- **Status**: Done
- **Priority**: P2
- **Labels**: `6.1.0`, `Functional`, `PWS`, `QA-Automation`, `Regression`
- **Linked Issues**: SPKB-676

**Description:**
Steps:
1. Log in as Sales Rep, Sales Ops, or Admin.
2. Navigate to Offer Queue.
3. Select the “Buyer Acceptance” tab.
Expected Result: The “Revert back to Sales Review” button is visible.

### SPKB-953 — [OutDated] PWS - Offer Queue - Verify that each offer in Buyer Acceptance status has a checkbox.

- **Status**: Done
- **Priority**: P2
- **Labels**: `6.1.0`, `Functional`, `PWS`, `QA-Automation`
- **Linked Issues**: SPKB-676

**Description:**
1. Login as SalesRep
2. Navigate to Buyer Acceptance tab in Offer Queue
Expected: Each offer row has a checkbox.

### SPKB-954 — PWS - Offer Queue - Verify the "Revert back to Sales Review" button is disabled when no offers are selected

- **Status**: Done
- **Priority**: P2
- **Labels**: `6.1.0`, `Functional`, `PWS`, `QA-Automation`, `Regression`
- **Linked Issues**: SPKB-676

**Description:**
1. Go to Buyer Acceptance tab.
2. Do not select any offers.
Expected: Button is disabled

### SPKB-957 — End Round Process | Verify ZeroBid-File Generation Flag is Removed

- **Status**: Done
- **Priority**: P2
- **Labels**: `6.1.0`, `Functional`
- **Linked Issues**: SPKB-467

### SPKB-958 — End Round Process | Verify ZeroBid-File Not Create and Send to SharePoint after Auction Rounds End

- **Status**: Done
- **Priority**: P2
- **Labels**: `6.1.0`, `Functional`
- **Linked Issues**: SPKB-467

### SPKB-959 — PWS - Offer Queue - Verify "Revert back to Sales Review” functionality

- **Status**: Done
- **Priority**: P2
- **Labels**: `6.1.0`, `Functional`, `PWS`, `QA-Automation`, `Regression`
- **Linked Issues**: SPKB-676

**Description:**
Steps:
1. Select at least one offer checkbox.
Expected: Button becomes enabled.
2. Click “Revert back to Sales Review”.
Expected:
Offers' status updates to Sales Review.
Reverted offers are no longer listed

### SPKB-960 — PWS - Offer Queue - Verify that reverted offers appear in the Sales Review tab.

- **Status**: Done
- **Priority**: P2
- **Labels**: `6.1.0`, `Functional`, `PWS`, `Regression`
- **Linked Issues**: SPKB-676

**Description:**
Prerequisite: have at least one reverted offer
1. Go to Sales Review tab
Expected: reverted offers are listed

### SPKB-966 — PWS Finalize | Verify 'Finalize' Option is Added Under Action Dropdown

- **Status**: Done
- **Priority**: P2
- **Labels**: `6.1.0`, `Functional`, `PWS`, `Regression`, `automated-playwright`
- **Linked Issues**: SPKB-773

### SPKB-967 — PWS Finalize | Verify Sales Rep is Able to Enter Price and Q'ty when Selecting the Finalize Option

- **Status**: Done
- **Priority**: P2
- **Labels**: `6.1.0`, `Functional`, `PWS`, `Regression`, `automated-playwright`
- **Linked Issues**: SPKB-773

### SPKB-968 — PWS Finalize | Verify More-Action Option 'Finalize All' Should Set All Inline Items to "Finalize"

- **Status**: Done
- **Priority**: P2
- **Labels**: `6.1.0`, `Functional`, `PWS`, `Regression`, `automated-playwright`
- **Linked Issues**: SPKB-773

### SPKB-969 — PWS Finalize | Verify Selecting Finalize-All Set Finalize for All Inline Items and Complete Review

- **Status**: Done
- **Priority**: P2
- **Labels**: `6.1.0`, `Functional`, `PWS`, `Regression`, `automated-playwright`
- **Linked Issues**: SPKB-773

### SPKB-970 — PWS Finalize | Verify Error Handling for Missing Price/Quantity on Inline Finalize

- **Status**: Done
- **Priority**: P2
- **Labels**: `6.1.0`, `Functional`, `PWS`, `Regression`, `automated-playwright`
- **Linked Issues**: SPKB-773

### SPKB-971 — PWS Finalize | Verify Error Handling Blocks Complete-Review for Mixed Accept/Finalize Item Statuses

- **Status**: Done
- **Priority**: P2
- **Labels**: `6.1.0`, `Functional`, `PWS`, `Regression`, `automated-playwright`
- **Linked Issues**: SPKB-773

### SPKB-972 — PWS Finalize | Validate Inline and Summary Total of the Offer Order

- **Status**: Done
- **Priority**: P2
- **Labels**: `6.1.0`, `Functional`, `PWS`, `Regression`, `automated-playwright`
- **Linked Issues**: SPKB-773

### SPKB-973 — PWS Finalize | Verify the Offer Finalized Offer Send to Ordered Stage

- **Status**: Done
- **Priority**: P2
- **Labels**: `6.1.0`, `Functional`, `PWS`, `Regression`, `automated-playwright`
- **Linked Issues**: SPKB-773

### SPKB-975 — PWS - Offer Review - Verify that offers in "Sales Review" or "Buyer Acceptance" are tagged if the same SKU exists in another open offer

- **Status**: Done
- **Priority**: P2
- **Labels**: `6.1.0`, `Functional`, `PWS`, `QA-Automation`, `Regression`
- **Linked Issues**: SPKB-674

**Description:**
Precondition: Two or more offers exist in open statuses with the same SKU
Steps:
1. Navigate to Offer Queue.
2. Check offers in "Sales Review" and "Buyer Acceptance."
Expected: Offers are tagged with "Pending offer for same SKU."

### SPKB-977 — PWS - Offers Review - Verify Tag is Present on Offers Only When Inventory is Insufficient

- **Status**: Done
- **Priority**: P2
- **Labels**: `6.1.0`, `Functional`
- **Linked Issues**: SPKB-674

**Description:**
Precondition: Same SKU in multiple offers but insufficient inventory.
Steps:
1. Create multiple offers for the same SKU.
2. Reduce inventory below total quantity needed.
Expected: Tag appears on the offers

### SPKB-978 — PWS - Offers Review - Verify UI around Pending Offer Tag (Icon, Tooltip, Tag Legend)

- **Status**: Done
- **Priority**: P2
- **Labels**: `6.1.0`, `Functional`, `PWS`, `QA-Automation`, `Regression`
- **Linked Issues**: SPKB-674

**Description:**
1. Verify that the Tags column correctly shows tag icons.
Expected: Appropriate tag icons are displayed.
2. Verify that when hovering over a tag icon, a tooltip with the description appears.
Expected: Tooltip with the correct description is shown.
3. Verify the tag legend is always visible at the bottom of the Offer Queue screen.
Expected: Legend explaining tag meanings is visible.

### SPKB-979 — PWS - Offer Reviews - Verify that tags are removed if the same SKU is no longer in multiple open offers.

- **Status**: Done
- **Priority**: P2
- **Labels**: `6.1.0`, `Functional`
- **Linked Issues**: SPKB-674

**Description:**
Precondition: Initially tagged offers.
Steps: 
1. Accept one of the offers with the shared SKU.
2. Return to the Offer Queue.
Expected: Tag is removed from the remaining open offer.

### SPKB-980 — PWS - Offers Review - Verify that tag operations (adding/removing) do not affect the "Last Updated" column value.

- **Status**: Done
- **Priority**: P2
- **Labels**: `6.1.0`, `Functional`, `PWS`, `QA-Automation`, `Regression`
- **Linked Issues**: SPKB-674

**Description:**
Steps:
1. Cause a tag to be added or removed (without any manual action on the offer).
2. Observe "Last Updated" value.
Expected: Last Updated timestamp remains unchanged.

### SPKB-995 — PWS Review Drawer | Verify Drawer is Opened When Clicking on SKU's Row and Action Default is Either 'Accept' or 'Counter' Based on Prices

- **Status**: Done
- **Priority**: P2
- **Labels**: `6.1.0`, `Functional`, `PWS`, `QA-Automation`, `Regression`
- **Linked Issues**: SPKB-452

### SPKB-997 — PWS Review Drawer | Verify User Able to Close Drawer without Saving By Clicking 'x' Icon

- **Status**: Done
- **Priority**: P2
- **Labels**: `6.1.0`, `Functional`, `PWS`, `QA-Automation`, `Regression`
- **Linked Issues**: SPKB-452

### SPKB-999 — PWS Review Drawer | Verify 'Save' Button is Enabled When Selecting Accept/Decline/Finalize from the Dropdown

- **Status**: Done
- **Priority**: P2
- **Labels**: `6.1.0`, `Functional`, `PWS`, `QA-Automation`, `Regression`
- **Linked Issues**: SPKB-452

### SPKB-1000 — PWS Review Drawer | Verify 'Save' Button is Disable When Selecting Counter and Counter Fields are Unfilled

- **Status**: Done
- **Priority**: P2
- **Labels**: `6.1.0`, `Functional`, `PWS`, `QA-Automation`, `Regression`
- **Linked Issues**: SPKB-452

### SPKB-1001 — PWS Review Drawer | Verify Selected Action Status and Saved from the Drawer Updates the Corresponding SKU on the Table

- **Status**: Done
- **Priority**: P2
- **Labels**: `6.1.0`, `Functional`, `PWS`, `QA-Automation`, `Regression`
- **Linked Issues**: SPKB-452

### SPKB-1002 — PWS Review Drawer | Verify Counter Price and Q'ty Saved from the Drawer Populates the Corresponding SKU on the Table

- **Status**: Done
- **Priority**: P2
- **Labels**: `6.1.0`, `Functional`, `PWS`, `QA-Automation`, `Regression`
- **Linked Issues**: SPKB-452

### SPKB-1003 — PWS Review Drawer | Verify 'Same-SKU' View Displays the Same SKU that Currently Being Viewed (Last 90 Days Offers)

- **Status**: Done
- **Priority**: P2
- **Labels**: `6.1.0`, `Functional`, `PWS`, `QA-Automation`, `Regression`
- **Linked Issues**: SPKB-452

### SPKB-1004 — PWS Review Drawer | Verify 'Similar-SKU' List Displays the SKU that Currently Being Viewed with Different Colors (Last 90 Days Offers)

- **Status**: Done
- **Priority**: P2
- **Labels**: `6.1.0`, `Functional`, `PWS`, `QA-Automation`, `Regression`
- **Linked Issues**: SPKB-452

### SPKB-1005 — PWS Review Drawer | Verify the Offer-Last-90-Days Table's Columns are Sortable

- **Status**: Done
- **Priority**: P2
- **Labels**: `6.1.0`, `Functional`, `PWS`, `QA-Automation`, `Regression`
- **Linked Issues**: SPKB-452

### SPKB-1006 — PWS Review Drawer | Verify Download 'This SKU' and 'Similar SKU' Files

- **Status**: Done
- **Priority**: P2
- **Labels**: `6.1.0`, `Functional`, `PWS`, `QA-Automation`, `Regression`
- **Linked Issues**: SPKB-452

### SPKB-1027 — PWS - Offer Reviews - Verify offers in Buyer Acceptance for more than 2 business days are tagged with Beyond SLA tag

- **Status**: Done
- **Priority**: P2
- **Labels**: `6.1.0`, `Functional`, `PWS`, `QA-Automation`, `Regression`
- **Linked Issues**: SPKB-961

**Description:**
Prerequisite: SLA tag is set to 2days under Feature Flags in PWS Control Center
1. Place an offer
2. Counter the offer
2. Let it stay in Buyer Acceptance for 2 business days
Expected: SLA tag appears on the offer

### SPKB-1028 — PWS - Offer Reviews - Verify Offers are not tagged within SLA

- **Status**: Done
- **Priority**: P2
- **Labels**: `6.1.0`, `Functional`
- **Linked Issues**: SPKB-961

**Description:**
Prerequisite: SLA is set for 2 days
1. Place an offer
2. Check the offer next day
Expected: offer is not tagged with SLA tag

### SPKB-1029 — PWS - Offer Reviews - Verify  that weekends are excluded in SLA calculation

- **Status**: Done
- **Priority**: P2
- **Labels**: `6.1.0`, `Functional`
- **Linked Issues**: SPKB-961

**Description:**
Prerequisite: SLA is set for 2 days
1. Place an offer on Friday
2. Check the offer on Monday
Expected: offer is not tagged with SLA tag

### SPKB-1030 — PWS - Offer Reviews - Verify SLA tag is removed once Sales Rep takes any action on the offer

- **Status**: Done
- **Priority**: P2
- **Labels**: `6.1.0`, `Functional`
- **Linked Issues**: SPKB-961

**Description:**
Prerequisite: have a SLA tagged offer
1. Login as SalesRep and counter the offer
Expected: tag is no longer present

### SPKB-1031 — PWS - Offer Reviews - Verify SLA Tag is Removed After Buyer Takes Action

- **Status**: Done
- **Priority**: P2
- **Labels**: `6.1.0`, `Functional`, `PWS`, `QA-Automation`, `Regression`
- **Linked Issues**: SPKB-961

**Description:**
Prerequisite: have an SLA tagged offer in Buyer Acceptance state
1. Login as buyer and respond to counter
Expected: tag is removed

### SPKB-1032 — PWS Offer Avail Qty | Verify 'Avail Q'ty' Column Added to Offer Detail Grid with Actual Available Qty of the SKU

- **Status**: Done
- **Priority**: P2
- **Labels**: `6.1.0`, `Functional`, `PWS`, `QA-Automation`, `Regression`
- **Linked Issues**: SPKB-678

### SPKB-1033 — PWS Offer Avail Q'ty | Validate Displayed Available Q'ty Against Snowflake 

- **Status**: Done
- **Priority**: P2
- **Labels**: `6.1.0`, `Functional`, `PWS`, `Regression`
- **Linked Issues**: SPKB-678

### SPKB-1034 — PWS Offer Avail Q'ty | Verify Highlight Avail Q'ty in Red when Offer Q'ty is More Than Avail Q'ty

- **Status**: Done
- **Priority**: P2
- **Labels**: `6.1.0`, `Functional`, `PWS`, `Regression`
- **Linked Issues**: SPKB-678

### SPKB-1035 — PWS Offer Avail Q'ty | Verify Highlight Avail Q'ty in Red Removed when Counter Q'ty is Less Than Avail Q'ty, and Sales Rep Able to Complete Review

- **Status**: Done
- **Priority**: P2
- **Labels**: `6.1.0`, `Functional`, `PWS`, `QA-Automation`, `Regression`
- **Linked Issues**: SPKB-678

### SPKB-1036 — PWS Offer Avail Q'ty | Verify Alert Message Handling Upon Completing Review When Offered or Countered Q'ty is More Than Avail Q'ty

- **Status**: Done
- **Priority**: P2
- **Labels**: `6.1.0`, `Functional`, `PWS`, `QA-Automation`, `Regression`
- **Linked Issues**: SPKB-678

### SPKB-1052 — PWS Email - Verify Counter Email - Mixed Item States

- **Status**: Done
- **Priority**: P2
- **Labels**: `6.1.0`, `Functional`, `PWS`, `Regression`, `automated-playwright`
- **Linked Issues**: SPKB-964

**Description:**
Verify email can show a mix of item states (some accepted, some countered, some declined).
Expected: Each item row shows accurate values based on action taken

### SPKB-1053 — PWS Email - Verify Counter Email Includes Action Link

- **Status**: Done
- **Priority**: P2
- **Labels**: `6.1.0`, `Functional`, `PWS`, `Regression`, `automated-playwright`
- **Linked Issues**: SPKB-964

**Description:**
Verify the email contains a clickable link to view and respond to the counter offer.
Expected: Link directs buyer to correct counter offer page

### SPKB-1054 — PWS Email - Verify Email on Offer Approval / Order Creation

- **Status**: Done
- **Priority**: P2
- **Labels**: `6.1.0`, `Functional`, `PWS`, `QA-Automation`, `Regression`
- **Linked Issues**: SPKB-964

**Description:**
Verify that once an offer is approved and converted to an order, the buyer receives an order confirmation email.
Steps:
1. Submit offer.
2. Sales rep approves it.
Expected: email is sent. Includes order ID, item details, pricing, and confirmation text. etc 
https://www.figma.com/design/YKeZdbfQGjXRZ0vbd8rTwM/PWS-Dashboard?node-id=20601-22993&p=f&t=4c4xb3900v6K6u3H-0

### SPKB-1056 — PWS Email - General Email Template Validation

- **Status**: Done
- **Priority**: P2
- **Labels**: `6.1.0`, `Functional`, `PWS`, `QA-Automation`, `Regression`
- **Linked Issues**: SPKB-964

**Description:**
Validate layout and styling matches the approved Figma design.
Expected: https://www.figma.com/design/YKeZdbfQGjXRZ0vbd8rTwM/PWS-Dashboard?node-id=20601-22993&p=f&t=4c4xb3900v6K6u3H-0

### SPKB-1059 — EB Calibration Report | Verify Cohort Mapping Download Function

- **Status**: Done
- **Priority**: P2
- **Labels**: `6.1.0`, `Functional`
- **Linked Issues**: SPKB-681

### SPKB-1060 — EB Calibration Report | Verify Cohort Mapping Upload Function

- **Status**: Done
- **Priority**: P2
- **Labels**: `6.1.0`, `Functional`
- **Linked Issues**: SPKB-681

### SPKB-1061 — EB Calibration Report | Verify Selecting Week from Dropdown Retrieve Data Correctly

- **Status**: Done
- **Priority**: P2
- **Labels**: `6.1.0`, `Functional`
- **Linked Issues**: SPKB-681

### SPKB-1063 — EB Calibration Report | Verify Download EB Report with Correct File Name and Sheet Name

- **Status**: Done
- **Priority**: P2
- **Labels**: `6.1.0`, `Functional`
- **Linked Issues**: SPKB-681

### SPKB-1082 — PWS Inventory View | Verify Buyer can Add/Change/Delete Offer Price and Q’ty

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`, `PWS`, `Regression`, `automated-playwright`
- **Linked Issues**: SPKB-378

**Description:**
- Buyer is able to add offer price and Q’ty.
- Buyer team is able to change offer price and Q’ty.
- Buyer is able to delete offer price and Q’ty.
- Buyer is able to see highlight on offer price:
- Highlight orange when offer price lower than List price.
- Highlight green when offer price the same as List price
- Buyer is able to view inline total for each SKU.
- Buyer is able to view the sum of individual totals.

### SPKB-1118 — PWS Offer | Verify More-Action Option 'Decline All' Should Set All Inline Items to "Decline"

- **Status**: Done
- **Priority**: P2
- **Labels**: `6.2.0`, `Functional`, `PWS`, `Regression`, `automated-playwright`
- **Linked Issues**: SPKB-1097

### SPKB-1120 — PWS - Offer Details - Verify Pending For Same SKU Tag is present on the corresponding SKU line on the left hand side of the grid

- **Status**: Done
- **Priority**: P2
- **Labels**: `6.2.0`, `Functional`, `PWS`, `QA-Automation`, `Regression`
- **Linked Issues**: SPKB-1015

**Description:**
Prerequisite:
Have an offer with pending For Same SKU Tag 
1. Click on the offer
Expected: corresponding SKU line has tag present on left hand side of the grid

### SPKB-1122 — Offer Queue Download | Verify Download Button Exports All Offers from the Current Offer Stage 

- **Status**: Done
- **Priority**: P2
- **Labels**: `6.2.0`, `Functional`, `PWS`, `QA-Automation`, `Regression`
- **Linked Issues**: SPKB-1065

### SPKB-1123 — Offer Queue Download | Verify Download Button is Greyed Out When No Offer Exist in the Current Stage Queue

- **Status**: Done
- **Priority**: P2
- **Labels**: `6.2.0`, `Functional`, `QA-Automation`, `Regression`
- **Linked Issues**: SPKB-1065

### SPKB-1131 — Qualified Buyer Round 2 | Verify Remove Qualified Buyer from the List Should Disqualified Them to Receive Email and Not Able to Access to Round 2 Dashboard

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`, `Regression`

### SPKB-1132 — Qualified Buyer Round 2 | Verify Reopen and Submit Bid Should Re-Calculate Qualified Buyer List

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`, `Regression`

### SPKB-1167 — Offer Detail | Verify Download from More-Action Dropdown

- **Status**: Done
- **Priority**: P2
- **Labels**: `6.2.0`, `Functional`, `PWS`, `Regression`, `automated-playwright`
- **Linked Issues**: SPKB-1072

### SPKB-1183 — Offer Review Drawer | Verify SKU's Status When the Offer is in 'Sales Review' Stage

- **Status**: Done
- **Priority**: P2
- **Labels**: `6.2.0`, `Functional`, `PWS`, `QA-Automation`, `Regression`
- **Linked Issues**: SPKB-1043

### SPKB-1184 — Offer Review Drawer | Verify SKU's Status When the Offer is in 'Buyer Acceptance' Stage

- **Status**: Done
- **Priority**: P2
- **Labels**: `6.2.0`, `Functional`, `PWS`, `QA-Automation`, `Regression`
- **Linked Issues**: SPKB-1043

### SPKB-1185 — Offer Review Drawer | Verify SKU's Status When the Offer is in 'Ordered' Stage

- **Status**: Done
- **Priority**: P2
- **Labels**: `6.2.0`, `Functional`, `PWS`, `QA-Automation`, `Regression`
- **Linked Issues**: SPKB-1043

### SPKB-1186 — Offer Review Drawer | Verify SKU's Status When the Offer is in 'Declined' Stage

- **Status**: Done
- **Priority**: P2
- **Labels**: `6.2.0`, `Functional`, `PWS`, `QA-Automation`, `Regression`
- **Linked Issues**: SPKB-1043

### SPKB-1199 — PWS Offer | Sales Action - Verify Sales Reps Can Accept/Decline/Counter Offer at SKU Level

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`, `PWS`, `Regression`, `automated-playwright`

### SPKB-1255 — PWS Buy Now | Verify Submit Button on Cart Page

- **Status**: Done
- **Priority**: P2
- **Labels**: `6.3.0`, `Functional`, `PWS`, `Regression`, `automated-playwright`
- **Linked Issues**: SPKB-1134

**Description:**
Expected: 
- Enable when there’s offer items in cart.
- Disable when there’s no item in cart

### SPKB-1256 — PWS Buy Now | Verify Order Status after Buyer Submit All SKUs Above List Price

- **Status**: Done
- **Priority**: P2
- **Labels**: `6.3.0`, `Functional`, `PWS`, `Regression`, `automated-playwright`
- **Linked Issues**: SPKB-1134

**Description:**
Expected:  
- Order created successfully
- Order status should be in Ordered stage.
- Drawer should show ‘ordered’ status on This-SKU, Similar-SKU views

### SPKB-1257 — PWS Buy Now | Verify Order Status after Buyer Submit with Any SKU Below List Price

- **Status**: Done
- **Priority**: P2
- **Labels**: `6.3.0`, `Functional`, `PWS`, `Regression`, `automated-playwright`
- **Linked Issues**: SPKB-1134

**Description:**
Expected: Order send to “Sales Review” status

### SPKB-1258 — PWS Buy Now | Verify Popup Modals Upon Submission

- **Status**: Done
- **Priority**: P2
- **Labels**: `6.3.0`, `Functional`, `PWS`, `Regression`, `automated-playwright`
- **Linked Issues**: SPKB-1134

**Description:**
Expected: 
- “Thank you…” modal, if offer price >= list price.
- “Almost done!” modal, if offer price < list price.

### SPKB-1259 — PWS Buy Now | Verify Email Sends after Order Submission

- **Status**: Done
- **Priority**: P2
- **Labels**: `6.3.0`, `Functional`, `PWS`, `Regression`, `automated-playwright`
- **Linked Issues**: SPKB-1134

**Description:**
Expected. 
- Email “Thank you…” send to buyer if offer price >= list price
- Email “We received your offer!” send to buyer if offer price < list price.

### SPKB-1260 — PWS Buy Now | Verify Reserved Quantity after Order Submission

- **Status**: Done
- **Priority**: P2
- **Labels**: `6.3.0`, `Functional`, `PWS`, `Regression`, `automated-playwright`
- **Linked Issues**: SPKB-1134

**Description:**
Expected.  
- SKU with Offer price >= List price
- Sales Action Status = ‘Accept’
- Reserved = true
- SKU with Offer price < List price
- Sales Action Status = ‘Counter’
- Reserved = false

### SPKB-1277 — PWS Offer | Verify More-Action Option 'Accept All' Should Set All Inline Items to "Accept"

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`, `PWS`, `Regression`, `automated-playwright`
- **Linked Issues**: SPKB-1097

### SPKB-1279 — PWS Cart Page | Verify Remove SKU by Clicking X Icon

- **Status**: Done
- **Priority**: P2
- **Labels**: `6.3.0`, `Functional`, `PWS`, `Regression`, `automated-playwright`
- **Linked Issues**: SPKB-1133

### SPKB-1280 — PWS Cart Page | Verify Remove SKU by Update Qty to Zero

- **Status**: Done
- **Priority**: P2
- **Labels**: `6.3.0`, `Functional`, `PWS`, `Regression`, `automated-playwright`
- **Linked Issues**: SPKB-1133

### SPKB-1281 — PWS Cart Page | Verify Remove SKU Does Not Reappear After Page Reload

- **Status**: Done
- **Priority**: P2
- **Labels**: `6.3.0`, `Functional`, `PWS`, `Regression`, `automated-playwright`
- **Linked Issues**: SPKB-1133

### SPKB-1282 — PWS Cart Page | Verify Summary of SKU, Qty and Total Update Correctly

- **Status**: Done
- **Priority**: P2
- **Labels**: `6.3.0`, `Functional`, `PWS`, `Regression`, `automated-playwright`
- **Linked Issues**: SPKB-1133

### SPKB-1283 — PWS Cart Page | Verify Order Submission Only Submit the Remaining SKUs in Cart

- **Status**: Done
- **Priority**: P2
- **Labels**: `6.3.0`, `Functional`, `PWS`, `Regression`, `automated-playwright`
- **Linked Issues**: SPKB-1133

**Description:**
Steps
- Add items and have offer price >= list price to cart
- Removed some SKUs from cart
- Submit order.   The order should be created because offer price above list price.
Expected:  Removed SKUs should not include in the order creation.  Only the remaining items in cart when submitting

### SPKB-1315 — PWS Cart Page | Verify 'Finalize Order' Submission with All SKUs have Offer Price Above List Price

- **Status**: Done
- **Priority**: P2
- **Labels**: `6.3.1`, `Functional`, `PWS`, `QA-Automation`, `Regression`
- **Linked Issues**: SPKB-808, SPKB-1314

**Description:**
Expected: Order is created with all SKUs in the order

### SPKB-1316 — PWS Cart Page | Verify 'Finalize Order' Submission with All SKUs have Offer Price Below List Price

- **Status**: Done
- **Priority**: P2
- **Labels**: `6.3.1`, `Functional`, `PWS`, `QA-Automation`, `Regression`
- **Linked Issues**: SPKB-808, SPKB-1314

**Description:**
Expected: Order is created with all SKUs in the order

### SPKB-1317 — PWS Cart Page | Verify 'Finalize Order' Submission with Any SKU has Offer Price Below List Price

- **Status**: Done
- **Priority**: P2
- **Labels**: `6.3.1`, `Functional`, `PWS`, `QA-Automation`, `Regression`
- **Linked Issues**: SPKB-808, SPKB-1314

**Description:**
Expected: Order is created with all SKUs in the order

### SPKB-1391 — Order History | Verify Order Icon Visible for Buyer in the Left Navigation Bar

- **Status**: Done
- **Priority**: P2
- **Labels**: `6.4.0`, `Functional`, `PWS`, `QA-Automation`, `Regression`
- **Linked Issues**: SPKB-735

### SPKB-1392 — (Outdated) Order History | Verify Recent-Tab Display as Default on Landing Page

- **Status**: Done
- **Priority**: P2
- **Labels**: `6.4.0`, `Functional`, `PWS`, `QA-Automation`, `Regression`
- **Linked Issues**: SPKB-735

### SPKB-1393 — Order History | Verify 'Recent' Tab Displays Offers Updated in the Last 7 Days and Is Selected by Default

- **Status**: Done
- **Priority**: P2
- **Labels**: `6.4.0`, `Functional`, `PWS`, `QA-Automation`, `Regression`
- **Linked Issues**: SPKB-735

### SPKB-1394 — Order History | Verify 'In Process' Tab Displays Offers in 'Sales Review' or 'Buyer Acceptance' Status

- **Status**: Done
- **Priority**: P2
- **Labels**: `6.4.0`, `Functional`, `PWS`, `QA-Automation`, `Regression`
- **Linked Issues**: SPKB-735

### SPKB-1395 — Order History | Verify 'Complete' Tab Displays Offers with Statuses: 'Ordered', 'Pending Order', 'Declined', or 'Cancelled'

- **Status**: Done
- **Priority**: P2
- **Labels**: `6.4.0`, `Functional`, `PWS`, `QA-Automation`, `Regression`
- **Linked Issues**: SPKB-735

### SPKB-1396 — Order History | Verify 'All' Tab Displays All Offers and Orders in Sales Platform

- **Status**: Done
- **Priority**: P2
- **Labels**: `6.4.0`, `Functional`, `PWS`, `QA-Automation`, `Regression`
- **Linked Issues**: SPKB-735

### SPKB-1397 — Order History | Verify Order History Data Grid Displays Correct Fields with Correct Order Status

- **Status**: Done
- **Priority**: P2
- **Labels**: `6.4.0`, `Functional`, `PWS`, `QA-Automation`, `Regression`
- **Linked Issues**: SPKB-735

**Description:**
- Order Number Column - should display order number if it’s ordered
- Order Number Column - should display offerId if it’s not ordered yet.
- Order Status Column - It should be “Offer Pending / In Process / Ordered / Cancelled / Declined”
- SKU/Qty should dynamically update once some accepted or declined

### SPKB-1398 — Order History | Verify Download Functionality Exports Tab-Specific Data with Correct File Naming and Formatting

- **Status**: Done
- **Priority**: P2
- **Labels**: `6.4.0`, `Functional`, `PWS`, `QA-Automation`, `Regression`
- **Linked Issues**: SPKB-735

### SPKB-1403 — Landing Page Preference | Verify Home Page Preference Direct for ecoATM Users

- **Status**: Done
- **Priority**: P2
- **Labels**: `6.4.0`, `Functional`, `PWS`, `Regression`, `automated-playwright`
- **Linked Issues**: SPKB-1013

### SPKB-1404 — Landing Page Preference | Verify Elements Hidden when No Buyer Code Selected

- **Status**: Done
- **Priority**: P2
- **Labels**: `6.4.0`, `Functional`, `PWS`, `Regression`, `automated-playwright`
- **Linked Issues**: SPKB-1013

### SPKB-1407 — Order History | Verify "All" Tab Displays as Landing Page Default and Be the First on the Left 

- **Status**: Done
- **Priority**: P2
- **Labels**: `6.4.0`, `Functional`, `PWS`, `QA-Automation`, `Regression`
- **Linked Issues**: SPKB-1406

### SPKB-1411 — Order History Detail | Verify “Review Counter Offer” Button

- **Status**: Done
- **Priority**: P2
- **Labels**: `6.4.0`, `Functional`, `PWS`, `QA-Automation`, `Regression`
- **Linked Issues**: SPKB-736

### SPKB-1412 — Order History Detail | Verify “Download” Button Functionality

- **Status**: Done
- **Priority**: P2
- **Labels**: `6.4.0`, `Functional`, `PWS`, `QA-Automation`, `Regression`
- **Linked Issues**: SPKB-736

### SPKB-1413 — Order History Detail | Verify Data Grid Columns in Order Details

- **Status**: Done
- **Priority**: P2
- **Labels**: `6.4.0`, `Functional`, `PWS`, `QA-Automation`, `Regression`
- **Linked Issues**: SPKB-736

### SPKB-1450 — PWS Email | Verify No-Adjusted-Qty Email Sent when Submitted Order with All SKUs below Avail Qty

- **Status**: Done
- **Priority**: P2
- **Labels**: `6.5.0`, `Functional`, `PWS`, `Regression`
- **Linked Issues**: SPKB-1365

### SPKB-1451 — PWS Email | Verify Adjusted-Qty Email Sent when Submitted Order with Any SKU Above Avail Qty (100+)

- **Status**: Done
- **Priority**: P2
- **Labels**: `6.5.0`, `Functional`, `PWS`, `Regression`
- **Linked Issues**: SPKB-1365

### SPKB-1452 — PWS Email | Verify Order Details, Calculation and Content

- **Status**: Done
- **Priority**: P2
- **Labels**: `6.5.0`, `Functional`, `PWS`, `Regression`
- **Linked Issues**: SPKB-1365

### SPKB-1457 — PWS Revert Offer | Verify 'Revert to Sales Review' and Checkbox Removed from Buyer-Accpt Main Tab

- **Status**: Done
- **Priority**: P2
- **Labels**: `6.5.0`, `Functional`, `PWS`, `QA-Automation`, `Regression`
- **Linked Issues**: SPKB-1136

### SPKB-1458 — PWS Revert Offer | Verify 'Revert to Sales Review' Button Displays on Offer Detail Page

- **Status**: Done
- **Priority**: P2
- **Labels**: `6.5.0`, `Functional`, `PWS`, `QA-Automation`, `Regression`
- **Linked Issues**: SPKB-1136

### SPKB-1459 — PWS Revert Offer | Verify Clicking 'Revert to Sales Review' Button Reverts the Offer to Sales Review Stage

- **Status**: Done
- **Priority**: P2
- **Labels**: `6.5.0`, `Functional`, `PWS`, `QA-Automation`, `Regression`
- **Linked Issues**: SPKB-1136

### SPKB-1460 — PWS Revert Offer | Verify Redirecting User Back to 'Buyer-Acceptance' Main Page after Reverted Offer

- **Status**: Done
- **Priority**: P2
- **Labels**: `6.5.0`, `Functional`, `PWS`, `QA-Automation`, `Regression`
- **Linked Issues**: SPKB-1136

### SPKB-1470 — Offer Details | Verify "In Stock Qty" Column Added on Offer Details Pages Across All Offer Statuses

- **Status**: Done
- **Priority**: P2
- **Labels**: `6.5.0`, `Functional`, `PWS`, `QA-Automation`, `Regression`
- **Linked Issues**: SPKB-1380

### SPKB-1471 — Offer Details | Validate "In Stock" and "Avail" Qty when Concurrent Order Submission by Another User

- **Status**: Done
- **Priority**: P2
- **Labels**: `6.5.0`, `Functional`, `PWS`, `QA-Automation`, `Regression`
- **Linked Issues**: SPKB-1380

### SPKB-1472 — Offer Details | Validate In-Stock Qty Displaying Sync Qty from Snowflake

- **Status**: Done
- **Priority**: P2
- **Labels**: `6.5.0`, `Functional`, `PWS`, `QA-Automation`, `Regression`
- **Linked Issues**: SPKB-1380

### SPKB-1575 — Offer Details | Verify Pre-Populate Counter Qty When Landing to Offer Detail Page

- **Status**: Done
- **Priority**: P2
- **Labels**: `7.0.0`, `Functional`, `QA-Automation`, `Regression`
- **Linked Issues**: SPKB-1074

### SPKB-1576 — Offer Details | Verify Counter Qty Remain Visible When Switching to Finalize

- **Status**: Done
- **Priority**: P2
- **Labels**: `7.0.0`, `Functional`, `QA-Automation`, `Regression`
- **Linked Issues**: SPKB-1074

### SPKB-1577 — Offer Details | Verify Counter Qty Removed When Switching to Accept/Decline 

- **Status**: Done
- **Priority**: P2
- **Labels**: `7.0.0`, `Functional`, `QA-Automation`, `Regression`
- **Linked Issues**: SPKB-1074

### SPKB-1578 — Offer Details | Verify Counter Qty Re-Populated When Switching Back to Counter/Finalize

- **Status**: Done
- **Priority**: P2
- **Labels**: `7.0.0`, `Functional`, `QA-Automation`, `Regression`
- **Linked Issues**: SPKB-1074

### SPKB-1579 — Offer Details | Verify Alert Message Display when Counter Qty Exceed Avail Qty

- **Status**: Done
- **Priority**: P2
- **Labels**: `7.0.0`, `Functional`, `QA-Automation`, `Regression`
- **Linked Issues**: SPKB-1074

### SPKB-1580 — Offer Details | Verify Complete-Review Button Disabled when Counter Qty Exceed Avail Qty

- **Status**: Done
- **Priority**: P2
- **Labels**: `7.0.0`, `Functional`, `QA-Automation`, `Regression`
- **Linked Issues**: SPKB-1074

### SPKB-1633 — Order History | Verify Ship Date and Ship Method Columns are Added to All the Tabs and Download Files

- **Status**: Done
- **Priority**: P2
- **Labels**: `7.0.0`, `Functional`, `PWS`, `QA-Automation`, `Regression`
- **Linked Issues**: SPKB-1487

### SPKB-1634 — Order History | Verify Ship Date is Displayed and Formatted Correctly in All Tabs and Downloaded Files

- **Status**: Done
- **Priority**: P2
- **Labels**: `7.0.0`, `Functional`, `QA-Automation`, `Regression`
- **Linked Issues**: SPKB-1487

### SPKB-1719 — RMA - Buyer View | Verify Request Button Displays and Opens Modal on Click

- **Status**: Done
- **Priority**: P2
- **Labels**: `8.0.0`, `Functional`, `PWS`, `QA-Automation`
- **Linked Issues**: SPKB-742

### SPKB-1720 — RAM - Buyer View | Verify Downloading Template Work and File has Correct Name and Format

- **Status**: Done
- **Priority**: P2
- **Labels**: `8.0.0`, `Functional`, `PWS`, `QA-Automation`
- **Linked Issues**: SPKB-742

**Description:**
- Click on the Download Template button in the model shall download the template excel file as attached.
- Download file name shall follow the naming convention RMA_Request_YYYYMMDD.xlsx and formatting

### SPKB-1721 — RMA - Buyer View | Verify Uploading File by Choosing File and Clicking Submit Button

- **Status**: Done
- **Priority**: P2
- **Labels**: `8.0.0`, `Functional`, `PWS`, `QA-Automation`
- **Linked Issues**: SPKB-742

### SPKB-1722 — RMA - Buyer View | Verify Modals Display Correctly for Uploading, Failure, and Success States

- **Status**: Done
- **Priority**: P2
- **Labels**: `8.0.0`, `Functional`, `PWS`, `QA-Automation`
- **Linked Issues**: SPKB-742

### SPKB-1723 — RMA - Buyer View | Verify Start-Over Button on Failure Modal Reopens the Uploading Modal

- **Status**: Done
- **Priority**: P2
- **Labels**: `8.0.0`, `Functional`, `PWS`, `QA-Automation`
- **Linked Issues**: SPKB-742

### SPKB-1724 — RMA | Validate Submission of Empty or Incorrect File Format

- **Status**: Done
- **Priority**: P2
- **Labels**: `8.0.0`, `Functional`, `PWS`, `QA-Automation`
- **Linked Issues**: SPKB-1491

### SPKB-1735 — RMA | Validate Missing IMEI in Uploaded File

- **Status**: Done
- **Priority**: P2
- **Labels**: `8.0.0`, `Functional`, `PWS`, `QA-Automation`
- **Linked Issues**: SPKB-1491

### SPKB-1736 — RMA | Validate Missing Return Reason in Uploaded File

- **Status**: Done
- **Priority**: P2
- **Labels**: `8.0.0`, `Functional`, `PWS`, `QA-Automation`
- **Linked Issues**: SPKB-1491

### SPKB-1737 — RMA | Validate Invalid IMEI Format in Uploaded File

- **Status**: Done
- **Priority**: P2
- **Labels**: `8.0.0`, `Functional`, `PWS`, `QA-Automation`
- **Linked Issues**: SPKB-1491

### SPKB-1738 — RMA | Validate IMEI That Does Not Belong to Buyer

- **Status**: Done
- **Priority**: P2
- **Labels**: `8.0.0`, `Functional`, `PWS`, `QA-Automation`
- **Linked Issues**: SPKB-1491

### SPKB-1833 — Order History | Verify By-Device Tab and Data Grid Match Mockup

- **Status**: Done
- **Priority**: P2
- **Labels**: `8.0.0`, `Functional`, `QA-Automation`
- **Linked Issues**: SPKB-740

### SPKB-1834 — Order History | Validate IMEI, SKU and Tracking Number

- **Status**: Done
- **Priority**: P2
- **Labels**: `8.0.0`, `Functional`, `QA-Automation`
- **Linked Issues**: SPKB-740

### SPKB-1835 — Order History | Verify Clicking Tracking Number Open Track-My-Order Drawer

- **Status**: Done
- **Priority**: P2
- **Labels**: `8.0.0`, `Functional`, `QA-Automation`
- **Linked Issues**: SPKB-740

### SPKB-1836 — Order History | Verify Downloading Order-History-By-Device

- **Status**: Done
- **Priority**: P2
- **Labels**: `8.0.0`, `Functional`, `QA-Automation`
- **Linked Issues**: SPKB-740
