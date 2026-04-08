# Microflow Analysis: SUB_ProcessAvailableQuantityForFilters

### Execution Steps:
1. **Search the Database for **EcoATM_PWSMDM.Category** using filter: { [EcoATM_PWSMDM.Device_Category/EcoATM_PWSMDM.Device
	[
		ATPQty> 0
		and
		IsActive
	]
]
 } (Call this list **$Category_AvlQtyList**)**
2. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
3. **Search the Database for **EcoATM_PWSMDM.Brand** using filter: { [EcoATM_PWSMDM.Device_Brand/EcoATM_PWSMDM.Device
	[
		ATPQty> 0
		and
		IsActive
	]
]
 } (Call this list **$Brand_AvlQtyList**)**
4. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
5. **Search the Database for **EcoATM_PWSMDM.Carrier** using filter: { [EcoATM_PWSMDM.Device_Carrier/EcoATM_PWSMDM.Device
	[
		ATPQty> 0
		and
		IsActive
	]
]
 } (Call this list **$Carrier_AvlQtyList**)**
6. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
7. **Search the Database for **EcoATM_PWSMDM.Capacity** using filter: { [EcoATM_PWSMDM.Device_Capacity/EcoATM_PWSMDM.Device
	[
		ATPQty> 0
		and
		IsActive
	]
]
 } (Call this list **$Capacity_AvlQtyList**)**
8. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
9. **Search the Database for **EcoATM_PWSMDM.Color** using filter: { [EcoATM_PWSMDM.Device_Color/EcoATM_PWSMDM.Device
	[
		ATPQty> 0
		and
		IsActive
	]
]
 } (Call this list **$Color_AvlQtyList**)**
10. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
11. **Search the Database for **EcoATM_PWSMDM.Grade** using filter: { [EcoATM_PWSMDM.Device_Grade/EcoATM_PWSMDM.Device
	[
		ATPQty> 0
		and
		IsActive
	]
]
 } (Call this list **$Grade_AvlQtyList**)**
12. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
13. **Search the Database for **EcoATM_PWSMDM.Model** using filter: { [EcoATM_PWSMDM.Device_Model/EcoATM_PWSMDM.Device
	[
		ATPQty> 0
		and
		IsActive
	]
]
 } (Call this list **$Model_AvlQtyList**)**
14. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
15. **Permanently save **$undefined** to the database.**
16. **Permanently save **$undefined** to the database.**
17. **Permanently save **$undefined** to the database.**
18. **Permanently save **$undefined** to the database.**
19. **Permanently save **$undefined** to the database.**
20. **Permanently save **$undefined** to the database.**
21. **Permanently save **$undefined** to the database.**
22. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
