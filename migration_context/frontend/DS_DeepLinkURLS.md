# Nanoflow: DS_DeepLinkURLS

**Allowed Roles:** DeepLink.Admin, DeepLink.User

## ⚙️ Execution Flow

1. **DB Retrieve **DeepLink.DeepLink**  (Result: **$DeepLinkList**)**
2. **Create List **$DeepLinkURLList****
3. 🔄 **LOOP:** For each **$IteratorDeepLink** in **$DeepLinkList**
   │ 1. **Call Nanoflow **DeepLink.GetURLByDeeplink** (Result: **$DeepLinkURL**)**
   │ 2. **Add **$$DeepLinkURL** to/from list **$DeepLinkURLList****
   └─ **End Loop**
4. 🏁 **END:** Return `$DeepLinkURLList`

## 🏁 Returns
`List`
