# Microflow Analysis: ACT_Tile_Create

### Requirements (Inputs):
- **$Tile** (A record of type: Eco_Core.Tile)

### Execution Steps:
1. **Decision:** "Has Image?"
   - If [true] -> Move to: **Finish**
   - If [false] -> Move to: **Activity**
2. **Decision:** "Check condition"
   - If [Deeplink] -> Move to: **Activity**
   - If [(empty)] -> Move to: **Finish**
   - If [URL] -> Move to: **Finish**
3. **Java Action Call
      - Store the result in a new variable called **$ReturnValueName****
4. **Create Variable**
5. **Update the **$undefined** (Object):
      - Change [Eco_Core.Tile.URL] to: "$Variable
"**
6. **Permanently save **$undefined** to the database.**
7. **Show Page**
8. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
