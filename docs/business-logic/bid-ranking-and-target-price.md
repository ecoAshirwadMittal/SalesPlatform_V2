# Bid Ranking + Target-Price Recalc

When a round of an auction closes (round 1 or 2), two follow-on processes run:

## RANKING
For every (ecoid, merged_grade) in scope, computes a DENSE_RANK on
`submitted_bid_amount DESC` over the just-closed round's bids. The result
populates `bid_data.round{N+1}_bid_rank` and a clamped
`display_round{N+1}_bid_rank` (NULL when calculated rank falls outside
`[bid_ranking_config.display_rank, .maximum_rank]`).

When `bid_ranking_config.include_reserve_floor = TRUE`, `auctions.reserve_bid`
rows participate in the rank as priority bidders — i.e. the EB floor for
(ecoid, grade) sits at the top of the rank if higher than every auction
bidder.

## TARGET_PRICE
For every (ecoid, merged_grade) in scope, computes:

```
MaxBid           = MAX(submitted_bid_amount) over closed round
MaxBidPlusFactor = MaxBid + matching target_price_factor band amount
EvaluatedBid     = GREATEST(MaxBidPlusFactor, COALESCE(EB, 0), COALESCE(PO_MAX, 0))
```

Result lands on `aggregated_inventory.round{N+1}_target_price` plus the
factor + EB columns. Round R's max-bid + max-bid-buyer-code are recorded.

## Recovery
Each process has its own status flags on `scheduling_auctions`. If RANKING
fails but TARGET_PRICE succeeds, ops re-run only the failed process via
`POST /api/v1/admin/auctions/scheduling-auctions/{id}/re-rank`. The
endpoint rejects 409 if a recalc is already running.
