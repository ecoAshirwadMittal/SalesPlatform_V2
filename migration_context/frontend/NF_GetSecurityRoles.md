# Nanoflow: NF_GetSecurityRoles

**Allowed Roles:** AuctionUI.Administrator, AuctionUI.Anonymous, AuctionUI.Bidder, AuctionUI.Compliance, AuctionUI.ecoAtmDirectAdmin, AuctionUI.Executive, AuctionUI.SalesLeader, AuctionUI.SalesOps, AuctionUI.SalesRep, AuctionUI.User

## ⚙️ Execution Flow

1. **Call Microflow **AuctionUI.MF_GetSecurityRoles** (Result: **$UserRoleList**)**
2. 🏁 **END:** Return `$UserRoleList`

## 🏁 Returns
`String`
