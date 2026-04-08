# PWS — Test Cases

> **296** test cases with label `PWS`.

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

### SPKB-532 — PWS - Verify re-upload order functionality of an updated offer

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`, `PWS`, `QA-Automation`, `Regression`
- **Linked Issues**: SPKB-291

### SPKB-629 — PWS - Pricing View - Verify pricing upload

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`, `PWS`, `QA-Automation`, `Regression`

### SPKB-638 — PWS - Inventory/Pricing views - Verify Typeahead functionality on filters

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`, `PWS`, `QA-Automation`, `Regression`
- **Linked Issues**: SPKB-589

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

### SPKB-788 — PWS Offer Details | Verify Default Offer Status Set to 'Sales Review' Once an Offer is Submitted.

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`, `PWS`, `Regression`, `automated-playwright`
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

### SPKB-813 — PWS Offer | Sales Action - Verify Offer Status is Pending if It Failed to Submit the Offer

- **Status**: Done
- **Priority**: P2
- **Labels**: `Functional`, `PWS`, `QA-Automation`, `Regression`
- **Linked Issues**: SPKB-450

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

### SPKB-920 — PWS Email | Verify the Email Sent when Sales Rep Completed Review with Countered SKUs 

- **Status**: Done
- **Priority**: P2
- **Labels**: `6.1.0`, `PWS`, `Regression`, `automated-playwright`
- **Linked Issues**: SPKB-675

**Description:**
Verify when
- All SKUs are countering.
- Some are accepted, declined and some are countering.

### SPKB-922 — PWS Email | Verify the Email Not Sent when Order is All Declined by Sales Rep or Buyer

- **Status**: Done
- **Priority**: P2
- **Labels**: `6.1.0`, `PWS`, `QA-Automation`, `Regression`
- **Linked Issues**: SPKB-675

### SPKB-923 — PWS Email | Verify the Email Sent when Order is Created with at Least One SKU Accepted by Sales Rep

- **Status**: Done
- **Priority**: P2
- **Labels**: `6.1.0`, `PWS`, `Regression`
- **Linked Issues**: SPKB-675

### SPKB-925 — PWS Email | Verify the Email Sent when Order is Created with at Least One Finalized SKU by Sales Rep

- **Status**: Done
- **Priority**: P2
- **Labels**: `6.1.0`, `PWS`, `Regression`

### SPKB-933 — PWS - Verify Redirect to Counter Offers Page on Login When > 1 Pending Offers Exist

- **Status**: Done
- **Priority**: P2
- **Labels**: `6.1.0`, `Functional`, `PWS`, `QA-Automation`, `Regression`
- **Linked Issues**: SPKB-895

**Description:**
Precondition: Buyer has 2 or more counter offers for selected buyer code.
Verify that the buyer is redirected to the Counter Offers landing page upon successful login if there are pending counter offers for the selected buyer code
Expected:Buyer lands on the Counter Offers page.

### SPKB-935 — PWS - Redirect to Offer Detail Page on Login When Exactly One Counter Offer Exists

- **Status**: Done
- **Priority**: P2
- **Labels**: `6.1.0`, `Functional`, `PWS`, `QA-Automation`, `Regression`
- **Linked Issues**: SPKB-895

**Description:**
Precondition: Buyer has 1 counter offer.
Verify that the buyer is redirected to the Offer Detail page directly when there is exactly one counter offer in the queue.
Expected: Buyer lands directly on the Counter Offer Detail page.

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

### SPKB-965 — PWS OfferID | Verify OfferID format

- **Status**: Done
- **Priority**: P2
- **Labels**: `6.1.0`, `PWS`, `Regression`, `automated-playwright`
- **Linked Issues**: SPKB-677

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

### SPKB-1026 — PWS - Offer Reviews - Verify offers in Sales Review for more than 2 business days are tagged with Beyond SLA tag

- **Status**: Done
- **Priority**: P2
- **Labels**: `6.1.0`, `PWS`, `QA-Automation`, `Regression`
- **Linked Issues**: SPKB-961

**Description:**
Prerequisite: SLA tag is set to 2days under Feature Flags in PWS Control Center
1. Place an offer
2. Let it stay in Sales Review for 2 business days
Expected: SLA tag appears on the offer

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

### SPKB-1124 — Offer Queue Download | Verify Export Spread Sheet File Name and Formatting 

- **Status**: Done
- **Priority**: P2
- **Labels**: `6.2.0`, `PWS`, `QA-Automation`, `Regression`
- **Linked Issues**: SPKB-1065

### SPKB-1128 — PWS - Offer Details - Verify Pending For Same SKU Tag is removed on the corresponding SKU line 

- **Status**: Done
- **Priority**: P2
- **Labels**: `6.2.0`, `PWS`, `QA-Automation`, `Regression`
- **Linked Issues**: SPKB-1015

**Description:**
Verify tag is removed when SKU doesn't qualify anymore

### SPKB-1141 — Offer Queue Download | Validate Excel Sheet Reflect the Offers that Shows on Data Grid

- **Status**: Done
- **Priority**: P2
- **Labels**: `6.2.0`, `PWS`, `QA-Automation`, `Regression`
- **Linked Issues**: SPKB-1065

### SPKB-1167 — Offer Detail | Verify Download from More-Action Dropdown

- **Status**: Done
- **Priority**: P2
- **Labels**: `6.2.0`, `Functional`, `PWS`, `Regression`, `automated-playwright`
- **Linked Issues**: SPKB-1072

### SPKB-1173 — PWS - Offer Queue - Verify Sticky Header

- **Status**: Done
- **Priority**: P2
- **Labels**: `6.2.0`, `PWS`, `QA-Automation`, `Regression`
- **Linked Issues**: SPKB-1068

**Description:**
1. Sticky header is displayed for all offer status views
2. Sticky header buttons/tabs are clickable

### SPKB-1176 — PWS - Offer Queue - Verify offer line is highlighted on hover

- **Status**: Done
- **Priority**: P2
- **Labels**: `6.2.0`, `PWS`, `QA-Automation`, `Regression`
- **Linked Issues**: SPKB-1067

**Description:**
When hovering over the offer line it gets highlighted color is matching tab selected

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

### SPKB-1224 — PWS - Cart Page - Verify Buyer Code and Company Name are displayed

- **Status**: Done
- **Priority**: P2
- **Labels**: `PWS`, `Regression`, `automated-playwright`
- **Linked Issues**: SPKB-1223

### SPKB-1225 — PWS - Inventory View - Verify screen is locked when switching the buyer code

- **Status**: Done
- **Priority**: P2
- **Labels**: `PWS`, `QA-Automation`, `Regression`
- **Linked Issues**: SPKB-1222

**Description:**
1. Go to Inventory view and switch buyer code
Expected: processing modal is displayed, user is not able to make edits until the code is switched

### SPKB-1236 — PWS - Inventory Reservation - Buyer Submits Order (Buy Now)

- **Status**: Done
- **Priority**: P2
- **Labels**: `6.3.0`, `PWS`, `QA-Automation`, `Regression`
- **Linked Issues**: SPKB-1022

**Description:**
Precondition: OfferItems have OfferPrice >= ListPrice and qty =< avlqty
1. Submit an offer.
2. Order is place 
Expected:
1. Reserved = true
2. ReservedOn is populated
3. ReservedQty = OfferQty (only if there is no other eligible for reservation offers against that SKU)
4. ATPQty = SyncQty - ReservedQty

### SPKB-1237 — PWS - Inventory Reservation - Buyer Submits Offer (Countered SKUs)

- **Status**: Done
- **Priority**: P2
- **Labels**: `6.3.0`, `PWS`, `QA-Automation`, `Regression`
- **Linked Issues**: SPKB-1022

**Description:**
Precondition: OfferItems have OfferPrice < ListPrice
1. Submit an offer.
2. Verify OfferItem status is Countered.
Expected:
1. No reservation is made (Reserved = false)
2. No changes to ReservedQty or ATPQty

### SPKB-1238 — PWS - Inventory Reservation - SalesRep Completes Review (Accepted Items)

- **Status**: Done
- **Priority**: P2
- **Labels**: `6.3.0`, `PWS`, `QA-Automation`, `Regression`
- **Linked Issues**: SPKB-1022

**Description:**
1. Buyer submits an offer.
2. SalesRep completes review by Accepting the SKU
Expected:
ReservedQty = OfferQty
ATPQty recalculated correctly

### SPKB-1239 — PWS - Inventory Reservation - SalesRep Completes Review (Finalized Items)

- **Status**: Done
- **Priority**: P2
- **Labels**: `6.3.0`, `PWS`, `QA-Automation`, `Regression`
- **Linked Issues**: SPKB-1022

**Description:**
1. Buyer submits an offer.
2. SalesRep completes review by Finalizing 
Expected:
1. ReservedQty = FinalizedQty
2. ATPQty recalculated correctly

### SPKB-1240 — PWS - Inventory Reservation - SalesRep Declines All Items

- **Status**: Done
- **Priority**: P2
- **Labels**: `6.3.0`, `PWS`, `QA-Automation`, `Regression`
- **Linked Issues**: SPKB-1022

**Description:**
1. Buyer submits offer.
2. SalesRep declines all OfferItems and clicks “Complete Review”.
Expected:
1. No reservation is made
2. Offer status is Declined
3. ReservedQty and ATPQty remain unchanged

### SPKB-1241 — PWS - Inventory Reservation - Buyer Accepts Counter

- **Status**: Done
- **Priority**: P2
- **Labels**: `6.3.0`, `PWS`, `QA-Automation`, `Regression`
- **Linked Issues**: SPKB-1022

**Description:**
1. Buyer submits an offer.
2. SalesRep Counter the offer.
3. Buyer accepts the counter
Expected:
- Reserved = true
- ReservedOn is updated
- ReservedQty = CounterQty
- ATP recalculated

### SPKB-1242 — PWS - Inventory Reservation - Buyer Declines Counter

- **Status**: Done
- **Priority**: P2
- **Labels**: `6.3.0`, `PWS`, `QA-Automation`, `Regression`
- **Linked Issues**: SPKB-1022, SPKB-1243

**Description:**
Buyer declines a countered item.
Expected:
1. Reserved = false
2. Offer.Status = Declined
3. No changes to ReservedQty or ATPQty

### SPKB-1243 — PWS - Inventory Reservation - Buyer Cancels an Offer

- **Status**: Done
- **Priority**: P2
- **Labels**: `6.3.0`, `PWS`, `QA-Automation`, `Regression`
- **Linked Issues**: SPKB-1242, SPKB-1022

**Description:**
Buyer cancels a countered offer.
Expected:
1. Reserved = false
2. Offer.Status = Declined
3. No changes to ReservedQty or ATPQtycalc

### SPKB-1244 — PWS - Inventory Reservation - ATP Zero Enforcement

- **Status**: Done
- **Priority**: P2
- **Labels**: `6.3.0`, `PWS`, `QA-Automation`, `Regression`
- **Linked Issues**: SPKB-1022

**Description:**
Precondition: SyncQty = ReservedQty
Expected:
1. buyer is not able to submit an offer
2.  ATPQty = 0

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

### SPKB-1291 — PWS Inventory Download | Verify Download Files Formatting and Styling

- **Status**: Done
- **Priority**: P2
- **Labels**: `6.3.0`, `PWS`, `Regression`
- **Linked Issues**: SPKB-1020

### SPKB-1295 — PWS - Inventory Listing - Verify Offer quantity above available (≤ 100) is highlighted in red

- **Status**: Done
- **Priority**: P2
- **Labels**: `6.3.0`, `PWS`, `QA-Automation`, `Regression`
- **Linked Issues**: SPKB-1220

**Description:**
Steps:
1. Navigate to Inventory Listing page
2. Enter an offer quantity greater than available quantity
Expected:
1. Quantity input is marked in red to indicate overage
2. Styling matches behavior in offer review page

### SPKB-1296 — PWS - Cart Page - Verify Offer submission is blocked when quantity exceeds available

- **Status**: Done
- **Priority**: P2
- **Labels**: `6.3.0`, `PWS`, `Regression`, `automated-playwright`
- **Linked Issues**: SPKB-1220

**Description:**
Steps:
1. Add SKU to cart
2. Set offer quantity greater than available
3. Click Submit
Expected:
1. Quantity field is marked in red
2. Error message is shown: “Quantity exceeds available items”
3. Submission is blocked

### SPKB-1297 — PWS - Cart Page - Verify valid quantity offer submission

- **Status**: Done
- **Priority**: P2
- **Labels**: `6.3.0`, `PWS`, `Regression`, `automated-playwright`
- **Linked Issues**: SPKB-1220

**Description:**
Steps:
1. Navigate to Cart View
2. Enter offer quantity less than or equal to available (≤ 100)
3. Click Submit
Expected Result:
1. No red highlight or error
2. Offer is submitted successfully

### SPKB-1298 — PWS - Cart Page - Verify Allow offers above availability when quantity > 100 

- **Status**: Done
- **Priority**: P2
- **Labels**: `6.3.0`, `PWS`, `Regression`, `automated-playwright`
- **Linked Issues**: SPKB-1220

**Description:**
Steps:
1. Navigate to Inventory Listing page with SKU that has >100 available
2. Enter offer quantity above available
Expected Result:
1. No red marking
2. No error on Cart submission
3. Offer can be submitted

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

### SPKB-1319 — Verify AUCTIONS.PWS_OFFER table is created with correct schema and column types

- **Status**: Done
- **Priority**: P2
- **Labels**: `6.4.0`, `PWS`
- **Linked Issues**: SPKB-1177

**Description:**
Validate that the PWS_OFFER table is created with all required columns and correct data types as per specification

Table is created successfully with all specified columns:
- OFFER_ID (STRING)
- OFFER_STATUS (STRING)
- All numeric fields (INTEGER/DECIMAL)
- All timestamp fields (TIMESTAMP_NTZ)
- All boolean fields (BOOLEAN)

### SPKB-1320 — Verify AUCTIONS.PWS_OFFER_ITEM table is created with correct schema and column types

- **Status**: Done
- **Priority**: P2
- **Labels**: `6.4.0`, `PWS`
- **Linked Issues**: SPKB-1177

**Description:**
Validate that the PWS_OFFER_ITEM table is created with all required columns and correct data types

### SPKB-1321 — Verify all required columns exist including audit columns (ROW_CREATED_ON, ROW_UPDATED_BY, etc.)

- **Status**: Done
- **Priority**: P2
- **Labels**: `6.4.0`, `PWS`
- **Linked Issues**: SPKB-1177

**Description:**
Validate that audit trail columns are present in both tables for version control

All audit columns present:
- ROW_ACTIVE (BOOLEAN)
- ROW_CREATED_ON (TIMESTAMP_NTZ)
- ROW_UPDATED_ON (TIMESTAMP_NTZ)
- ROW_CREATED_BY (STRING)
- ROW_UPDATED_BY (STRING)

### SPKB-1322 — Verify rows are inserted with ROW_ACTIVE = TRUE on Submit a new offer with items

- **Status**: Done
- **Priority**: P2
- **Labels**: `6.4.0`, `PWS`, `QA-Automation`, `Regression`
- **Linked Issues**: SPKB-1177

**Description:**
Test initial offer submission creates active records

Records inserted successfully:
- PWS_OFFER record with ROW_ACTIVE = TRUE
- PWS_OFFER_ITEM records with ROW_ACTIVE = TRUE
- All business fields populated correctly

### SPKB-1323 — Verify audit columns (ROW_CREATED_ON, ROW_CREATED_BY) are correctly populated on insert

- **Status**: Done
- **Priority**: P2
- **Labels**: `6.4.0`, `PWS`
- **Linked Issues**: SPKB-1177

**Description:**
Ensure audit metadata is captured during record creation

Audit columns populated:
- ROW_CREATED_ON contains timestamp of operation
- ROW_CREATED_BY contains user who performed operation
- ROW_UPDATED_ON initially equals ROW_CREATED_ON

### SPKB-1324 — Verify all actor metadata (buyer code, submitted by, etc.) is stored accurately

- **Status**: Done
- **Priority**: P2
- **Labels**: `6.4.0`, `PWS`, `QA-Automation`, `Regression`
- **Linked Issues**: SPKB-1177

**Description:**
Validate that all business actor information is captured correctly

All actor metadata fields populated:
- OFFER_BUYER_CODE
- OFFER_SUBMITTED_BY
- OFFER_SALES_REPRESENTATIVE
- Other actor fields as provided

### SPKB-1325 — Verify old item row is expired (ROW_ACTIVE = FALSE) when Counter an offer item

- **Status**: Done
- **Priority**: P2
- **Labels**: `6.4.0`, `PWS`, `QA-Automation`, `Regression`
- **Linked Issues**: SPKB-1177

**Description:**
Test that countering an offer properly expires the original item record

Steps:
- Create initial offer with items
- Submit counter offer for specific item
- Query PWS_OFFER_ITEM for original record
- Verify ROW_ACTIVE = FALSE for original record

Expected:
Original item record:
- ROW_ACTIVE = FALSE
- ROW_UPDATED_ON updated to counter timestamp
- ROW_UPDATED_BY populated with counter user

### SPKB-1326 — Verify new row is inserted with updated COUNTER_* values and ROW_ACTIVE = TRUE when Counter an offer item

- **Status**: Done
- **Priority**: P2
- **Labels**: `6.4.0`, `PWS`, `QA-Automation`, `Regression`
- **Linked Issues**: SPKB-1177

**Description:**
Test that countering creates new version with counter data

### SPKB-1327 — Verify old item row is expired, new item row inserted with FINAL_* values when Buyer accepts a counter

- **Status**: Done
- **Priority**: P2
- **Labels**: `6.4.0`, `PWS`, `QA-Automation`, `Regression`
- **Linked Issues**: SPKB-1177

**Description:**
Test buyer acceptance workflow creates final version

### SPKB-1328 — Verify offer row expired and new version inserted with updated metrics when Sales rep completes review

- **Status**: Done
- **Priority**: P2
- **Labels**: `6.4.0`, `PWS`, `QA-Automation`, `Regression`
- **Linked Issues**: SPKB-1177

**Description:**
Test sales review completion updates offer-level metrics

### SPKB-1329 — Verify rows are expired only if values change when Offer is reverted

- **Status**: Done
- **Priority**: P2
- **Labels**: `6.4.0`, `PWS`
- **Linked Issues**: SPKB-1177

**Description:**
Test that revert operation only creates new versions when values actually change

### SPKB-1330 — Verify that rows not needing changes are not duplicated during revert logic

- **Status**: Done
- **Priority**: P2
- **Labels**: `6.4.0`, `PWS`
- **Linked Issues**: SPKB-1177

**Description:**
Ensure revert operations don't create unnecessary duplicate records

### SPKB-1331 — Validate partial update (only some SKUs change) works as expected

- **Status**: Done
- **Priority**: P2
- **Labels**: `6.4.0`, `PWS`, `QA-Automation`, `Regression`
- **Linked Issues**: SPKB-1177

**Description:**
Test that partial updates only version changed items

### SPKB-1336 — Verify 3 rows total: initial, countered, final when Submit offer > Counter SKU A > Accept SKU A

- **Status**: Done
- **Priority**: P2
- **Labels**: `6.4.0`, `PWS`, `Regression`
- **Linked Issues**: SPKB-1177

**Description:**
Test complete offer lifecycle creates proper version history

### SPKB-1337 — Verify that countering specific SKUs doesn't affect others

- **Status**: Done
- **Priority**: P2
- **Labels**: `6.4.0`, `PWS`
- **Linked Issues**: SPKB-1177

**Description:**
Submit offer with multiple SKUs, counter 1 SKU verify only that SKU is versioned
- Submit offer with SKUs A, B, C
- Counter only SKU B
- Verify SKUs A and C unchanged (1 version each)
- Verify SKU B has 2 versions (original + counter)

Expected:
Selective versioning:
- SKUs A, C: single versions, ROW_ACTIVE = TRUE
- SKU B: original expired, counter active
- No unnecessary versioning of unchanged items

### SPKB-1338 — Validate ORDER_ID fields are populated but no new rows are added when order is created

- **Status**: Done
- **Priority**: P2
- **Labels**: `6.4.0`, `PWS`
- **Linked Issues**: SPKB-1177

**Description:**
Order is created 
validate ORDER_ID fields are populated but no new rows are added
Verify that order creation links without creating versions

### SPKB-1339 — Verify final row with BUYER_COUNTER_STATUS = Rejected when Counter rejected by buyer

- **Status**: Done
- **Priority**: P2
- **Labels**: `6.4.0`, `PWS`, `QA-Automation`, `Regression`
- **Linked Issues**: SPKB-1177

**Description:**
Test buyer rejection of counter creates proper final status


- Create and counter an offer
- Submit buyer rejection
- Verify new version created
- Verify BUYER_COUNTER_STATUS = 'Rejected'
Expected:
Rejection processed:
- New final version created
- BUYER_COUNTER_STATUS = 'Rejected'
- Previous version expired
- Final workflow status captured

### SPKB-1340 — Verify SAME_SKU_OFFER_AVAILABLE toggles correctly based on open offer logic

- **Status**: Done
- **Priority**: P2
- **Labels**: `6.4.0`, `PWS`, `QA-Automation`, `Regression`

**Description:**
Test business rule for same SKU offer availability
1. Create offer with SKU X
2. Create second offer with same SKU X
3. Verify SAME_SKU_OFFER_AVAILABLE flags
4. Complete first offer
5. Verify flags update accordingly
Expected:
Same SKU logic:
- Flag = TRUE when multiple offers exist for same SKU
- Flag = FALSE when only one offer exists
- Updates correctly as offers complete

### SPKB-1341 — Verify only one active row per OFFER_ID and OFFER_ITEM_ID exists at a time

- **Status**: Done
- **Priority**: P2
- **Labels**: `6.4.0`, `PWS`, `QA-Automation`, `Regression`
- **Linked Issues**: SPKB-1177

**Description:**
Ensure data integrity constraint of single active version
1. Query PWS_OFFER grouped by OFFER_ID and ROW_ACTIVE
2. Query PWS_OFFER_ITEM grouped by OFFER_ITEM_ID and ROW_ACTIVE
3. Verify count = 1 for all ROW_ACTIVE = TRUE groups
Expected:
Data integrity maintained:
- Each OFFER_ID has exactly 1 ROW_ACTIVE = TRUE
- Each OFFER_ITEM_ID has exactly 1 ROW_ACTIVE = TRUE
- No duplicate active versions

### SPKB-1342 — Verify all updates result in exactly one expired (ROW_ACTIVE = FALSE) and one active (TRUE) row

- **Status**: Done
- **Priority**: P2
- **Labels**: `6.4.0`, `PWS`
- **Linked Issues**: SPKB-1177

**Description:**
Validate update operations maintain proper versioning
Steps:
1. Perform various update operations
2. After each update, query affected records
3. Verify old version: ROW_ACTIVE = FALSE
4. Verify new version: ROW_ACTIVE = TRUE
Expected:
Update integrity:
- Each update creates exactly 1 new active version
- Each update expires exactly 1 previous version
- No missing or extra versions created

### SPKB-1343 — Verify RESERVED and RESERVED_ON fields update only under reservation logic

- **Status**: Done
- **Priority**: P2
- **Labels**: `6.4.0`, `PWS`, `QA-Automation`, `Regression`
- **Linked Issues**: SPKB-1177

**Description:**
Test Available-to-Promise reservation field updates
1. Create offer items
2. Execute reservation logic
3. Verify RESERVED = TRUE and RESERVED_ON populated
4. Execute unreservation
5. Verify fields reset appropriately
Expected:
Reservation logic:
- RESERVED = TRUE when item reserved
- RESERVED_ON populated with reservation timestamp
- Fields update only during reservation operations

### SPKB-1368 — Verify SKU record in PWS_DEVICE_PRICE table when Upload Future Price for SKU with Existing Active Record

- **Status**: Done
- **Priority**: P2
- **Labels**: `6.4.0`, `PWS`, `QA-Automation`, `Regression`
- **Linked Issues**: SPKB-1178

**Description:**
Prerequisite: SKU exists in the table with ROW_ACTIVE = TRUE
When a future price file is uploaded with a new FUTURE_LIST_PRICE and FUTURE_LIST_PRICE_DATE for the SKU
Then the current active row should be set to ROW_ACTIVE = FALSE
And a new row should be inserted with:
1. updated future price details
2. ROW_ACTIVE = TRUE
3. audit fields populated

### SPKB-1369 — Verify SKU record in PWS_DEVICE_PRICE table when Upload Future Price for New SKU (No Active Record)

- **Status**: Done
- **Priority**: P2
- **Labels**: `6.4.0`, `PWS`
- **Linked Issues**: SPKB-1178

**Description:**
Prerequisite: the SKU does not exist in the table.
1. future price file is uploaded
Expected:
a new row should be inserted directly with:
1. the future price details
2. ROW_ACTIVE = TRUE
3. audit fields populated

### SPKB-1370 — Verify SKU record in PWS_DEVICE_PRICE - Future Price Applied When Date Matches Current Date

- **Status**: Done
- **Priority**: P2
- **Labels**: `6.4.0`, `PWS`
- **Linked Issues**: SPKB-1178

**Description:**
Prerequisite: row with FUTURE_LIST_PRICE_DATE <= CURRENT_DATE and ROW_ACTIVE = TRUE exists
Steps:
1. Scheduled job runs.
Expected:
1. current row is expired (ROW_ACTIVE = FALSE)
2. a new row is inserted with:
- CURRENT_LIST_PRICE = FUTURE_LIST_PRICE
- FUTURE_LIST_PRICE = NULL
- FUTURE_LIST_PRICE_DATE = NULL
- ROW_ACTIVE = TRUE

### SPKB-1371 — Verify no updates when Future Price Date is in Future

- **Status**: Done
- **Priority**: P2
- **Labels**: `6.4.0`, `PWS`
- **Linked Issues**: SPKB-1178

**Description:**
Prerequisite: a row with FUTURE_LIST_PRICE_DATE > CURRENT_DATE exists
Steps:
1. scheduled job runs
Expected:
1. no updates made to that row

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

### SPKB-1399 — PWS - Verify Offer Details page for All Stages

- **Status**: Done
- **Priority**: P2
- **Labels**: `6.4.0`, `PWS`, `QA-Automation`, `Regression`
- **Linked Issues**: SPKB-1016

**Description:**
Offer Details for
Buyer Acceptance: read only, similar to sales review offer detail page, 
Ordered: 
Declined
Total
Acceptance criteria
all are read only except download 
download button
no drawer
maintain same sku tag

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

### SPKB-1408 — Order History | No Offer Display when No Buyer Code is Selected

- **Status**: Done
- **Priority**: P2
- **Labels**: `6.4.0`, `PWS`, `QA-Automation`, `Regression`
- **Linked Issues**: SPKB-1406

### SPKB-1410 — Order History Detail | Verify Order Details Header and Summary Sections

- **Status**: Done
- **Priority**: P2
- **Labels**: `6.4.0`, `PWS`, `QA-Automation`, `Regression`
- **Linked Issues**: SPKB-736

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

### SPKB-1453 — PWS Review Drawer - Verify Offer ID column is present and clickable

- **Status**: Done
- **Priority**: P2
- **Labels**: `6.5.0`, `PWS`, `QA-Automation`, `Regression`
- **Linked Issues**: SPKB-1137

**Description:**
“Offer ID” is available as the first column in the grid (before Offer Date) with a hyperlink. Click on the Offer ID link shall open the offer details screen in a new tab
Offer ID is present in both "This SKU" and "Similar SKU" tabs

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

### SPKB-1483 — Verify that ecoATM user on PWS Inventory Page sees only Premium Wholesale buyer codes in Switch Buyer Code dropdown

- **Status**: Done
- **Priority**: P2
- **Labels**: `6.5.0`, `PWS`, `Regression`, `automated-playwright`
- **Linked Issues**: SPKB-1383

### SPKB-1484 — Verify that ecoATM user on WHSL sees only Wholesale buyer codes

- **Status**: Done
- **Priority**: P2
- **Labels**: `6.5.0`, `PWS`, `QA-Automation`, `Regression`
- **Linked Issues**: SPKB-1383

### SPKB-1485 — Verify that non-ecoATM buyers can see all their buyer codes (Wholesale + Premium Wholesale) on WHSL side

- **Status**: Done
- **Priority**: P2
- **Labels**: `6.5.0`, `PWS`, `QA-Automation`, `Regression`
- **Linked Issues**: SPKB-1383

### SPKB-1486 — Verify that non-ecoATM buyers can see all their buyer codes (Wholesale + Premium Wholesale) on PWS side

- **Status**: Done
- **Priority**: P2
- **Labels**: `6.5.0`, `PWS`, `QA-Automation`, `Regression`
- **Linked Issues**: SPKB-1383

### SPKB-1568 — PWS Deposco | Verify ATP Inventory - Sales Rep Completes Review (Countered -> Declined)

- **Status**: Done
- **Priority**: P2
- **Labels**: `7.0.0`, `PWS`
- **Linked Issues**: SPKB-733

### SPKB-1569 — PWS Deposco | Verify ATP Inventory - Buyer Responses to Counteroffer (Countered -> Accepted)

- **Status**: Done
- **Priority**: P2
- **Labels**: `7.0.0`, `PWS`
- **Linked Issues**: SPKB-733

### SPKB-1570 — PWS Deposco | Verify ATP Inventory - Buyer Responses to Counteroffer (Countered -> Declined)

- **Status**: Done
- **Priority**: P2
- **Labels**: `7.0.0`, `PWS`
- **Linked Issues**: SPKB-733

### SPKB-1571 — PWS Deposco | Verify ATP Inventory - Buyer Responses to Counteroffer (Cancelled Offer)

- **Status**: Done
- **Priority**: P2
- **Labels**: `7.0.0`, `PWS`
- **Linked Issues**: SPKB-733

### SPKB-1572 — PWS Deposco | Verify ATP Inventory - ATP Zero Enforcement

- **Status**: Done
- **Priority**: P2
- **Labels**: `7.0.0`, `PWS`
- **Linked Issues**: SPKB-733

**Description:**
Expected:
- buyer is not able to submit an offer
- ATPQty = 0

### SPKB-1573 — PWS Deposco | Verify ATP Inventory Update During Checkout-with-BuyNow Attempt

- **Status**: Done
- **Priority**: P2
- **Labels**: `7.0.0`, `PWS`

### SPKB-1574 — PWS Deposco | Verify ATP Inventory Update During Sales Rep Finalizing the Offer

- **Status**: Done
- **Priority**: P2
- **Labels**: `7.0.0`, `PWS`

### SPKB-1582 — PWS Deposco | Verify ATP Inventory Update During Sales Rep Reviewing the Offer

- **Status**: Done
- **Priority**: P2
- **Labels**: `7.0.0`, `PWS`

### SPKB-1583 — PWS Deposco | Verify ATP Inventory Update During Buyer Responding to Counter Offer

- **Status**: Done
- **Priority**: P2
- **Labels**: `7.0.0`, `PWS`

### SPKB-1590 — PWS Navigation Bar | Verify Navigation Menu Items for Buyer Role

- **Status**: Done
- **Priority**: P2
- **Labels**: `7.0.0`, `PWS`
- **Linked Issues**: SPKB-1414

**Description:**
expected:
- Inventory
- Counters
- Orders
- FAQ
- Grading

### SPKB-1591 — PWS Navigation Bar | Verify Navigation Menu Items for Sale Role

- **Status**: Done
- **Priority**: P2
- **Labels**: `7.0.0`, `PWS`
- **Linked Issues**: SPKB-1414

**Description:**
Expected: 
- Inventory
- Counters
- Offers
- Orders
- Pricing
- FAQ
- Grading

### SPKB-1596 — Validate Deposco API Admin Configuration & Inventory Sync

- **Status**: Done
- **Priority**: P2
- **Labels**: `7.0.0`, `PWS`, `SalesPlatform`
- **Linked Issues**: SPKB-1363

**Description:**
### Steps & Expected Results
- Navigate & Log In
- Go to https://buy-qa.ecoatmdirect.com/p/orderdashboard.
- Sign in with your Sales Platform credentials.
- Expect: You land on the Order Dashboard with the “Premium Wholesale” grid visible.
- Switch to Wholesale
- Click the Profile menu (top right) → Switch to Wholesale.
- Expect: The “Premium Wholesale” page is active, showing columns like SKU, Avl. Qty, etc.
- Open Deposco Config
- From the left nav, click Settings → PWS Control Center → Deposco tile.
- Expect: The Deposco Config screen appears, showing:
- Base URL = https://ecoatm-ua.deposco.com/
- Username / Password fields populated (password masked)
- Last Sync Time and Page Count filled in (per your screenshot)
- Capture Network Traffic
- Open DevTools → Network tab (or configure your proxy).
- Clear existing logs so you only see new calls.
- Load Inventory from Deposco
- Click the Load PWS inventory Deposco button.
- Expect:
- A GET request fires to
```
php-template
```
CopyEdit
https://ecoatm-ua.deposco.com/integration/ecoatm/inventory/full   ?startActivityTime=<timestamp>&pageNo=<n>
- The response is 200 OK with JSON containing your test SKU and fields: total, availableToPromise, unallocated.
- Validate Initial Sync
- Without touching anything in Deposco, verify the Sales Platform grid’s Avl. Qty, ATPQty, Reserved match what the full API returned.
- Expect: The numbers align exactly with the JSON payload.
- Make an Inventory Change in Deposco
- In the Deposco UI (ecoatm-ua.deposco.com), locate your test SKU.
- Change its On-Hand quantity (e.g., +1) or add a new SKU with a known quantity.
- Save/apply the change.
- Re-run the Load
- Back in PWS, click Load PWS inventory Deposco again.
- Expect:
- The same API endpoint is called (with a new startActivityTime).
- The returned JSON now reflects your updated quantity.
- The Sales Platform grid updates to show the new Avl. Qty, ATPQty, Reserved values.
- Cleanup
- Revert any test changes in Deposco (return your SKU to its original On-Hand).
- Optionally, clear or reset the PWS page count or last-sync timestamp to avoid interfering with other tests.
Pass Criteria:
- Config screen shows exactly the values in your screenshots.
- “Load PWS inventory” triggers the correct full-inventory API call.
- Initial and subsequent syncs accurately pull and display Deposco data in PWS.
- No errors appear in the UI or network logs.

### SPKB-1597 — Validate 3-minute Polling Job Fetches Real-Time Inventory from Deposco

- **Status**: Done
- **Priority**: P2
- **Labels**: `7.0.0`, `PWS`
- **Linked Issues**: SPKB-1586

**Description:**
- Precondition: SKU exists in Oracle and is synced to Depasco.
- Steps:
- Adding Qty on Deposco.
- Wait for a polling cycle to complete.
- Query the Sales Platform for SKU inventory.
- Expected Result: ATP Inventory reflects latest data from Depasco.

### SPKB-1598 — Validate No Inventory is Fetched from Snowflake Post-Cutover

- **Status**: Done
- **Priority**: P2
- **Labels**: `7.0.0`, `PWS`
- **Linked Issues**: SPKB-1586

**Description:**
- Steps:
- Create new SKU in Oracle.
- Submitted order in Sales Platform
- Expected Result: 
- New SKU should not be sent to Snowflake.
- Qty of SKU should not be updated when order created in Sales Platform.

### SPKB-1599 — Validate new SKU Created in Oracle and Push via WMS to Sales Platform

- **Status**: Done
- **Priority**: P2
- **Labels**: `7.0.0`, `PWS`
- **Linked Issues**: SPKB-1586

**Description:**
- Steps:
- Create a new SKU in Oracle.
- Trigger WMS → Sales Platform API.
- Expected Result: Sales Platform logs or DB shows SKU entry creation via API.

### SPKB-1600 — Verify New SKU Appear and Values Match Deposco 

- **Status**: Done
- **Priority**: P2
- **Labels**: `7.0.0`, `PWS`
- **Linked Issues**: SPKB-1586

**Description:**
- Steps:
- Ensure new SKU is visible in Depasco.
- Wait for the next polling job.
- Query inventory for the new SKU from Sales Platform.
- Expected Result: SKU inventory is visible and matches Deposco value.

### SPKB-1601 — Validate New-SKU Order Creation and End-to-End Sync of Quantity and Status

- **Status**: Done
- **Priority**: P2
- **Labels**: `7.0.0`, `PWS`
- **Linked Issues**: SPKB-1586

**Description:**
- Steps:
- Place an order on Sales Platform for an in-stock SKU.
- Verify Oracle receives the order.
- Confirm order is forwarded to Depasco.
- Validate shipping label generation in Depasco.
- Confirm fulfillment status is sent back to Oracle.
- Check Sales Platform for real-time status updates via Depasco polling.
- Expected Result: Each step completes successfully and reflects on Sales Platform.

### SPKB-1602 — Validate Existing-SKU Order Creation and End-to-End Sync of Quantity and Status

- **Status**: Done
- **Priority**: P2
- **Labels**: `7.0.0`, `PWS`
- **Linked Issues**: SPKB-1586

**Description:**
- Steps:
- Place order on Sales Platform.
- Cancel before it reaches Depasco.
- Expected Result: Order is not forwarded to Depasco. No label is generated.

### SPKB-1603 — Validate Multiple-SKUs Order Creation and End-to-End Sync of Quantity and Status

- **Status**: Done
- **Priority**: P2
- **Labels**: `7.0.0`, `PWS`
- **Linked Issues**: SPKB-1586

**Description:**
- Steps:
- Place multi-SKU order.
- Track fulfillment status across Oracle and Depasco.
- Expected Result: Aggregated order status is correctly updated in Sales Platform.

### SPKB-1604 — Verify Error Handling when Deposco API Returns 500 

- **Status**: Done
- **Priority**: P2
- **Labels**: `7.0.0`, `PWS`
- **Linked Issues**: SPKB-1586

**Description:**
- Steps:
- Simulate or force Depasco API outage.
- Trigger polling.
- Expected Result: Sales Platform displays appropriate error/logs and retries after interval.

### SPKB-1605 — Verify Sales Platform's Inventory when There's Mismatch between Oracle and Deposco

- **Status**: Done
- **Priority**: P2
- **Labels**: `7.0.0`, `PWS`

**Description:**
- Steps:
- Force an intentional mismatch (e.g., stock is 5 in Oracle, 0 in Depasco).
- Validate behavior.
- Expected Result: System logs alert or discrepancy. No incorrect inventory shown on Sales Platform.

### SPKB-1606 — Verify Response when Invalid or Missing Data Pushed by WMS to Sales Platform

- **Status**: Done
- **Priority**: P2
- **Labels**: `7.0.0`, `PWS`
- **Linked Issues**: SPKB-1586

**Description:**
- Steps:
- Send malformed or non-existent SKU from WMS.
- Expected Result: API responds with validation error, error is logged and rejected gracefully.

### SPKB-1607 — Validate Consistent ATP Inventory Display Across Sales and Buyer Review Pages

- **Status**: Done
- **Priority**: P2
- **Labels**: `7.0.0`, `PWS`
- **Linked Issues**: SPKB-1586

**Description:**
- Steps:
-
- Expected Result:

### SPKB-1608 — Validate Core Platform Functionality and Order Placement UI Stability

- **Status**: Done
- **Priority**: P2
- **Labels**: `7.0.0`, `PWS`
- **Linked Issues**: SPKB-1586

**Description:**
- Test Steps:
- Test unrelated core features:
- User login/logout
- Catalog browsing and search
- Promotions or discount visibility
- Place an order through the UI using multiple browsers and devices (e.g., Chrome, Safari, mobile).
- Monitor overall user experience during each action.
Expected Result:
- All core platform features operate without errors.
- Order placement UI behaves consistently across devices and browsers.
- No degradation in user experience or order completion flow.

### SPKB-1609 — SP API | Verify Create New SKU

- **Status**: Done
- **Priority**: P2
- **Labels**: `7.0.0`, `PWS`
- **Linked Issues**: SPKB-1361

### SPKB-1610 — SP API | Verify Update Existing SKU

- **Status**: Done
- **Priority**: P2
- **Labels**: `7.0.0`, `PWS`
- **Linked Issues**: SPKB-1361

### SPKB-1611 — SP API | Verify Invalid or Missing Data for Field

- **Status**: Done
- **Priority**: P2
- **Labels**: `7.0.0`, `PWS`
- **Linked Issues**: SPKB-1361

**Description:**
Expected Result:
- 400 Bad Request
- Validation error indicating sku is required

### SPKB-1612 — SP API | Verify No Duplicate SKU Created 

- **Status**: Done
- **Priority**: P2
- **Labels**: `7.0.0`, `PWS`
- **Linked Issues**: SPKB-1361

### SPKB-1613 — SP API | Verify Authentication Failure When Credentials Are Missing or Invalid

- **Status**: Done
- **Priority**: P2
- **Labels**: `7.0.0`, `PWS`
- **Linked Issues**: SPKB-1361

**Description:**
Expected: response message -> Inactive Item <sku> already exists

### SPKB-1633 — Order History | Verify Ship Date and Ship Method Columns are Added to All the Tabs and Download Files

- **Status**: Done
- **Priority**: P2
- **Labels**: `7.0.0`, `Functional`, `PWS`, `QA-Automation`, `Regression`
- **Linked Issues**: SPKB-1487

### SPKB-1663 — PWS - Verify Site Under Maintenance banner is displayed when enabled

- **Status**: Done
- **Priority**: P2
- **Labels**: `7.0.0`, `PWS`
- **Linked Issues**: SPKB-1631

### SPKB-1664 — PWS - Verify Site Under Maintenance banner is not displayed when disabled

- **Status**: Done
- **Priority**: P2
- **Labels**: `7.0.0`, `PWS`
- **Linked Issues**: SPKB-1631

### SPKB-1666 — Order Status | Verify "Offer" Status Mapping

- **Status**: Done
- **Priority**: P2
- **Labels**: `7.0.0`, `PWS`
- **Linked Issues**: SPKB-737

**Description:**
Covers:
- Sales Review
- Buyer Acceptance
- Declined
- Offer Cancelled

### SPKB-1667 — Order Status | Verify "Pending Order" Status Mapping

- **Status**: Done
- **Priority**: P2
- **Labels**: `7.0.0`, `PWS`
- **Linked Issues**: SPKB-737

### SPKB-1668 — Order Status | Verify "In Process and Awaiting Carrier Pickup" from WMS to Sales Platform Status Mapping

- **Status**: Done
- **Priority**: P2
- **Labels**: `7.0.0`, `PWS`
- **Linked Issues**: SPKB-737

### SPKB-1669 — Order Status | Verify "Shipped and Order Cancelled" from WMS to Sales Platform Status Mapping

- **Status**: Done
- **Priority**: P2
- **Labels**: `7.0.0`, `PWS`
- **Linked Issues**: SPKB-737

### SPKB-1671 — PWS - Verify PWS side is not accessible when in maintenance mode

- **Status**: Done
- **Priority**: P2
- **Labels**: `7.0.0`, `PWS`
- **Linked Issues**: SPKB-1631

### SPKB-1672 — PWS - Verify WHSL is operational when pws is in maintenance mode

- **Status**: Done
- **Priority**: P2
- **Labels**: `7.0.0`, `PWS`
- **Linked Issues**: SPKB-1631

### SPKB-1673 — PWS - Verify Maintenance mode schedule works correctly

- **Status**: Done
- **Priority**: P2
- **Labels**: `7.0.0`, `PWS`
- **Linked Issues**: SPKB-1631

### SPKB-1678 — Shipment Tracking | Verify Single Box Shipment – One SKU

- **Status**: Done
- **Priority**: P2
- **Labels**: `7.0.0`, `PWS`
- **Linked Issues**: SPKB-738

### SPKB-1679 — Shipment Tracking | Verify Single Box Shipment - Multiple SKUs

- **Status**: Done
- **Priority**: P2
- **Labels**: `7.0.0`, `PWS`
- **Linked Issues**: SPKB-738

### SPKB-1680 — Shipment Tracking | Verify Multiple Box Shipment - One SKU

- **Status**: Done
- **Priority**: P2
- **Labels**: `7.0.0`, `PWS`
- **Linked Issues**: SPKB-738

### SPKB-1681 — Shipment Tracking | Verify Multiple Box Shipment - Multiple SKUs

- **Status**: Done
- **Priority**: P2
- **Labels**: `7.0.0`, `PWS`
- **Linked Issues**: SPKB-738

### SPKB-1682 — Shipment Tracking | Verify Tracking Number Per Box

- **Status**: Done
- **Priority**: P2
- **Labels**: `7.0.0`, `PWS`
- **Linked Issues**: SPKB-738

### SPKB-1684 — Order History Detail | Verify Inline Item Order Qty, Shipped Qty, Total Price

- **Status**: Done
- **Priority**: P2
- **Labels**: `7.0.0`, `PWS`
- **Linked Issues**: SPKB-1488

### SPKB-1685 — Order History Detail | Verify Values in Summary Box

- **Status**: Done
- **Priority**: P2
- **Labels**: `7.0.0`, `PWS`
- **Linked Issues**: SPKB-1488

### SPKB-1686 — Order History Detail | Verify Displays of Order Details

- **Status**: Done
- **Priority**: P2
- **Labels**: `7.0.0`, `PWS`
- **Linked Issues**: SPKB-1488

### SPKB-1688 — API Log | Verify Deposco API Log, Feature Flag and Downloaded File

- **Status**: Done
- **Priority**: P2
- **Labels**: `7.0.0`, `PWS`
- **Linked Issues**: SPKB-1658

### SPKB-1692 — Deposco | Verify Order Status Sync From Deposco to Sales Platform

- **Status**: Done
- **Priority**: P2
- **Labels**: `7.0.0`, `PWS`
- **Linked Issues**: SPKB-734

### SPKB-1693 — Deposco | Verify Shipment Tracking Sync From Deposco to Sales Platform

- **Status**: Done
- **Priority**: P2
- **Labels**: `7.0.0`, `PWS`
- **Linked Issues**: SPKB-734

### SPKB-1697 — Order-Offer Status | Verify Status on Offer Landing and Details Pages 

- **Status**: Done
- **Priority**: P2
- **Labels**: `7.0.0`, `PWS`
- **Linked Issues**: SPKB-1520

### SPKB-1698 — Order-Offer Status | Verify Status on Order Landing and Details Pages 

- **Status**: Done
- **Priority**: P2
- **Labels**: `7.0.0`, `PWS`
- **Linked Issues**: SPKB-1520

### SPKB-1699 — Order-Offer Status | Verify Status on Counter Landing and Details Pages 

- **Status**: Done
- **Priority**: P2
- **Labels**: `7.0.0`, `PWS`
- **Linked Issues**: SPKB-1520

### SPKB-1709 — Legacy Order Tracking | Verify Status Changed from Pending Order/ Ordered to Ship Completed

- **Status**: Done
- **Priority**: P2
- **Labels**: `7.0.0`, `PWS`
- **Linked Issues**: SPKB-1521

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

### SPKB-1741 — RMA Landing - Buyer View | Verify Navigation and Menu

- **Status**: Done
- **Priority**: P2
- **Labels**: `8.0.0`, `PWS`
- **Linked Issues**: SPKB-741

**Description:**
1. Verify RMAs menu item is displayed in the left navigation bar when a buyer logs in.
2. Verify clicking RMAs menu item navigates to the Returns Landing Page.
3. Verify empty state – when no returns are submitted, PWS Dashboard shows appropriate message/UI.
4. Verify populated state – when returns exist, PWS Dashboard displays data grid with return records.

### SPKB-1742 — RMA Landing - Buyer View | Verify Global Search

- **Status**: Done
- **Priority**: P2
- **Labels**: `8.0.0`, `PWS`
- **Linked Issues**: SPKB-741

**Description:**
Search by RMA Number – results should return correct RMA(s).
Search by RMA Status – results filter by matching status.
Search by SKU – results filter by SKU(s).
Search with invalid criteria – no results should be displayed.

### SPKB-1743 — RMA Landing page - Verify Data Grid Columns

- **Status**: Done
- **Priority**: P2
- **Labels**: `8.0.0`, `PWS`
- **Linked Issues**: SPKB-741

**Description:**
Data Grid shall contain the following columns (Infinite scroll)
RMA Number - The RMS Number generated from Sales Platform / RMA Number received from Oracle 
RMA Number Format from Sales Platform - RMA<BuyerCode><Two Digit Year><Three Digit Number>
Example RMA number created by Sales Platform - RMA2462525001
Submit Date - The date of RMA submission by the buyer in mm/dd/yy format
Buyer - First and Last name of the user who submitted the RMA
Company - The buyer company name
RMA Status - The status of the RMA
SKUs - Number of SKUs included in the return
Qty - Total number of devices across all the SKUs included in the return
Sales Total - The total final offer price of all SKUs*Qty included in the return

### SPKB-1744 — RMA Landing - Buyer View | Verify Download Functionality

- **Status**: Done
- **Priority**: P2
- **Labels**: `8.0.0`, `PWS`
- **Linked Issues**: SPKB-741

**Description:**
Verify More Actions button is available with a Download option.
Verify clicking Download generates file with all grid records.
Verify file naming convention matches PWS Returns_YYYYMMDD.xlsx.
Verify downloaded file contains correct data matching what is displayed in the grid.
Verify date fields in file are formatted as dates.
Verify currency fields are formatted correctly in file.
Verify numeric fields (Qty, SKUs) are formatted as numbers in file.

### SPKB-1768 — RMA Review Page - Sales View | Verify Navigation and Landing Page

- **Status**: Done
- **Priority**: P2
- **Labels**: `8.0.0`, `PWS`
- **Linked Issues**: SPKB-1495

**Description:**
1. Verify RMAs menu item is displayed in the left navigation bar for Sales Ops, Rep, and Admin roles.
2. Verify clicking on RMAs menu item opens the landing page as per Figma design.
3. Verify landing page layout matches the design (headers, tabs, styling, empty state message).

### SPKB-1769 — RMA Review Page - Sales View | Verify Tabs and Status Filtering

- **Status**: Done
- **Priority**: P2
- **Labels**: `8.0.0`, `PWS`
- **Linked Issues**: SPKB-1495

**Description:**
1. Verify “Pending Approval” tab displays all RMA requests pending Sales approval.
2. Verify “Open” tab displays all RMA requests approved by Sales but pending buyer shipment.
3. Verify “Closed” tab displays all RMA requests received from Deposco with Closed status.
4. Verify “Declined” tab displays all declined RMA requests.
5. Verify “Total” tab displays all RMA requests across all statuses.
6. Verify counts displayed on each tab match the number of records retrieved.
7. Verify tab switching updates the grid with the correct data.
8. Verify when no RMA requests exist, an empty state message is shown (check per tab).

### SPKB-1770 — RMA Review Page - Sales View | Verify Download Functionality

- **Status**: Done
- **Priority**: P2
- **Labels**: `8.0.0`, `PWS`
- **Linked Issues**: SPKB-1495

**Description:**
1. Verify Download button exists for each non-empty tab.
2. Verify clicking Download button generates an Excel file.
3. Verify downloaded file contains only data relevant to the selected tab.
4. Verify file naming convention is followed:
Format: RMA_Returns_<Request Status>_YYYYMMDD.xlsx
Example: RMA_Returns_PendingApproval_20250827.xlsx
5. Verify downloaded Excel file matches styling & format of other platform downloads.
6. Verify all columns and rows in the grid are exported correctly into the Excel file.

### SPKB-1772 — RMA Review Page - Sales View | Verify Role-Based Access

- **Status**: Done
- **Priority**: P2
- **Labels**: `8.0.0`, `PWS`
- **Linked Issues**: SPKB-1495

**Description:**
Verify that only Sales Ops, Sales Rep and Admin can view RMA Review Page

### SPKB-1811 — E-mail Reminders For Counter Offer - Verify Counter Offer reminder (24h) email template matches the design

- **Status**: Done
- **Priority**: P2
- **Labels**: `8.0.0`, `PWS`
- **Linked Issues**: SPKB-1777

### SPKB-1812 — E-mail Reminders For Counter Offer - Verify Counter Offer Expiring (48h) email template matches the design

- **Status**: Done
- **Priority**: P2
- **Labels**: `8.0.0`, `PWS`
- **Linked Issues**: SPKB-1777

### SPKB-1813 — E-mail Reminders For Counter Offer - Verify Counter Offer reminder (24h) email is sent after sales rep submits a counter

- **Status**: Done
- **Priority**: P2
- **Labels**: `8.0.0`, `PWS`
- **Linked Issues**: SPKB-1777

### SPKB-1814 — E-mail Reminders For Counter Offer - Verify Counter Offer Expiring (48h) email is sent after sales rep submits a counter

- **Status**: Done
- **Priority**: P2
- **Labels**: `8.0.0`, `PWS`
- **Linked Issues**: SPKB-1777

### SPKB-1815 — E-mail Reminders For Counter Offer - Verify Counter Offer reminder (24h) email is NOT sent when buyer accepts/declines the counter

- **Status**: Done
- **Priority**: P2
- **Labels**: `8.0.0`, `PWS`
- **Linked Issues**: SPKB-1777

### SPKB-1816 — E-mail Reminders For Counter Offer - Verify Counter Offer Expiring (48h) email is NOT sent when buyer accepts/declines the counter

- **Status**: Done
- **Priority**: P2
- **Labels**: `8.0.0`, `PWS`
- **Linked Issues**: SPKB-1777

### SPKB-1845 — PWS - Verify SyncOrderHistory job runs with no errors

- **Status**: Done
- **Priority**: P2
- **Labels**: `8.0.0`, `PWS`
- **Linked Issues**: SPKB-1809

**Description:**
Go to DataDog and observe the logs of SyncOrderHistory job
https://app.datadoghq.com/logs?query=service%3AAuctionUI%20env%3Aaccp%20%40method%3AACT_SyncOrderHistory&agg_m=count&agg_m_source=base&agg_t=count&clustering_pattern_field_path=message&cols=host%2Cservice&fromUser=true&link_source=monitor_notif&messageDisplay=inline&refresh_mode=sliding&storage=hot&stream_sort=desc&viz=stream&from_ts=1753994537321&to_ts=1753998137321&live=true
Expected: no errors

### SPKB-1850 — PWS Inventory Page | Verify Item Properties and Filters

- **Status**: Done
- **Priority**: P2
- **Labels**: `7.2.0`, `PWS`
- **Linked Issues**: SPKB-1843

### SPKB-1851 — PWS Pricing Page | Verify Item Properties and Filters

- **Status**: Done
- **Priority**: P2
- **Labels**: `7.2.0`, `PWS`
- **Linked Issues**: SPKB-1843

### SPKB-1852 — Offer Drawer | Verify Column's Status Displaying Current Status

- **Status**: Done
- **Priority**: P2
- **Labels**: `7.2.0`, `PWS`
- **Linked Issues**: SPKB-1843
