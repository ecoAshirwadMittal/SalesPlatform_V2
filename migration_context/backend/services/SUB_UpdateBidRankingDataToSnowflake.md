# Microflow Detailed Specification: SUB_UpdateBidRankingDataToSnowflake

### 📥 Inputs (Parameters)
- **$SchedulingAuction** (Type: AuctionUI.SchedulingAuction)

### ⚙️ Execution Flow (Logic Steps)
1. **Create Variable **$Round** = `$SchedulingAuction/Round-1`**
2. **Create Variable **$SnowflakeDataGenerationQuery** = `'UPDATE auctionui$schedulingauction sa SET SnowflakeJson = src.payload FROM ( SELECT sa_inner.id AS schedulingauction_id, json_agg( json_build_object( ''eco_id'', bd.ecoid, ''buyer_code'', bd.code, ''merged_grade'', bd.merged_grade, ''round2_rank'', bd.round2bidrank, ''round3_rank'', bd.round3bidrank, ''round2_display_rank'', bd.displayround2bidrank, ''round3_display_rank'', bd.displayround3bidrank, ''auction_week_year'', sa_inner.auction_week_year, ''round_number'', bd.bidround ) ) AS payload FROM auctionui$schedulingauction sa_inner JOIN "auctionui$bidround_schedulingauction" bsa ON bsa."auctionui$schedulingauctionid" = sa_inner.id JOIN "auctionui$biddata_bidround" br ON br."auctionui$bidroundid" = bsa."auctionui$bidroundid" JOIN auctionui$biddata bd ON bd.id = br."auctionui$biddataid" WHERE sa_inner.auction_week_year = '''+$SchedulingAuction/Auction_Week_Year+''' AND bd.bidround = '+$Round+' AND bd.submittedbidamount IS NOT NULL GROUP BY sa_inner.id ) src WHERE sa.id = src.schedulingauction_id; '`**
3. **Call Microflow **Custom_Logging.SUB_Log_Info****
4. **JavaCallAction**
5. **DB Retrieve **AuctionUI.SchedulingAuction** Filter: `[Auction_Week_Year=$SchedulingAuction/Auction_Week_Year] [Round=$Round-1]` (Result: **$SchedulingAuction_DB**)**
6. **ExecuteDatabaseQuery**
7. 🏁 **END:** Return `$SchedulingAuction_DB`

**Final Result:** This process concludes by returning a [Object] value.