# JSON Structure: JSON_AuctionData

## Sample JSON

```json
{
  "Week": {
    "WeekNumber": 1,
    "Year": 2024,
    "Auction": {
      "AuctionTitle": "Holiday Auction 2024",
      "AuctionStatus": "Started",
      "Created_By": "user1",
      "Updated_By": "user1",
      "SchedulingAuction": [
        {
          "AuctionWeekYear": "2024-W01",
          "Round": 1,
          "StartDateTime": "2024-01-01T10:00:00",
          "EndDateTime": "2024-01-01T12:00:00",
          "RoundStatus": "Scheduled",
          "HasRound": true,
          "Created_By": "user1",
          "Updated_By": "user1"
        },
        {
          "AuctionWeekYear": "2024-W01",
          "Round": 2,
          "StartDateTime": "2024-01-01T14:00:00",
          "EndDateTime": "2024-01-01T16:00:00",
          "RoundStatus": "Scheduled",
          "HasRound": true,
          "Created_By": "user1",
          "Updated_By": "user1"
        }
      ]
    }
  }
}
```
