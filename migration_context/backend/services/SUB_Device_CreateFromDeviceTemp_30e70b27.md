# Microflow Analysis: SUB_Device_CreateFromDeviceTemp

### Requirements (Inputs):
- **$DeviceTemp** (A record of type: EcoATM_PWSMDM.DeviceTemp)

### Execution Steps:
1. **Run another process: "EcoATM_PWS.SUB_Color_GetOrCreate"
      - Store the result in a new variable called **$Color****
2. **Run another process: "EcoATM_PWS.SUB_Grade_GetOrCreate"
      - Store the result in a new variable called **$Grade****
3. **Run another process: "EcoATM_PWS.SUB_Category_GetOrCreate"
      - Store the result in a new variable called **$Category****
4. **Run another process: "EcoATM_PWS.SUB_Condition_GetOrCreate"
      - Store the result in a new variable called **$Condition****
5. **Run another process: "EcoATM_PWS.SUB_Model_GetOrCreate"
      - Store the result in a new variable called **$Model****
6. **Run another process: "EcoATM_PWS.SUB_Brand_GetOrCreate"
      - Store the result in a new variable called **$Brand****
7. **Run another process: "EcoATM_PWS.SUB_Capacity_GetOrCreate"
      - Store the result in a new variable called **$Capacity****
8. **Run another process: "EcoATM_PWS.SUB_Carrier_GetOrCreate"
      - Store the result in a new variable called **$Carrier****
9. **Create Object
      - Store the result in a new variable called **$NewDevice****
10. **Process successfully completed.**

**Conclusion:** This process sends back a [Object] result.
