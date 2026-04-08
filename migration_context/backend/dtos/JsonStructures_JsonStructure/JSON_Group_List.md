# JSON Structure: JSON_Group_List

## Sample JSON

```json
{
    "@odata.context": "https://graph.microsoft.com/v1.0/$metadata#groups",
    "value": [
        {
            "id": "30cd601c-c16e-45c7-8ea9-907b8b881818",
            "deletedDateTime": "2021-03-09T12:31:33.0000000Z",
            "classification": null,
            "createdDateTime": "2021-03-09T12:31:33.0000000Z",
            "creationOptions": [
                "YammerProvisioning"
            ],
            "description": "This is the default group for everyone in the network",
            "displayName": "All Company",
            "expirationDateTime": "2021-03-09T12:31:33.0000000Z",
            "groupTypes": [
                "Unified"
            ],
            "isAssignableToRole": null,
            "mail": "allcompany@mendixappfactory.onmicrosoft.com",
            "mailEnabled": true,
            "mailNickname": "allcompany",
            "membershipRule": "string",
            "membershipRuleProcessingState": "string",
            "onPremisesDomainName": null,
            "onPremisesLastSyncDateTime": null,
            "onPremisesNetBiosName": null,
            "onPremisesSamAccountName": null,
            "onPremisesSecurityIdentifier": null,
            "onPremisesSyncEnabled": null,
            "preferredDataLocation": null,
            "preferredLanguage": "string",
            "proxyAddresses": [
                "SMTP:allcompany@mendixappfactory.onmicrosoft.com"
            ],
            "renewedDateTime": "2021-03-09T12:31:33.0000000Z",
            "resourceBehaviorOptions": [
                "CalendarMemberReadOnly"
            ],
            "resourceProvisioningOptions": [],
            "securityEnabled": false,
            "securityIdentifier": "S-1-12-1-818765852-1170719086-2073078158-404261003",
            "theme": "String",
            "visibility": "Public",
            "onPremisesProvisioningErrors": []
        },
        {
            "id": "7245ebc0-23cd-4794-9e3c-6d866f064687",
            "deletedDateTime": null,
            "classification": null,
            "createdDateTime": "2021-03-09T15:24:27.0000000Z",
            "creationOptions": [
                "Team",
                "ExchangeProvisioningFlags:3552"
            ],
            "description": "mendixappfactory",
            "displayName": "mendixappfactory",
            "expirationDateTime": null,
            "groupTypes": [
                "Unified"
            ],
            "isAssignableToRole": null,
            "mail": "mendixappfactory@mendixappfactory.onmicrosoft.com",
            "mailEnabled": true,
            "mailNickname": "mendixappfactory",
            "membershipRule": null,
            "membershipRuleProcessingState": null,
            "onPremisesDomainName": null,
            "onPremisesLastSyncDateTime": null,
            "onPremisesNetBiosName": null,
            "onPremisesSamAccountName": null,
            "onPremisesSecurityIdentifier": null,
            "onPremisesSyncEnabled": null,
            "preferredDataLocation": null,
            "preferredLanguage": null,
            "proxyAddresses": [
                "SMTP:mendixappfactory@mendixappfactory.onmicrosoft.com"
            ],
            "renewedDateTime": "2021-03-09T15:24:27.0000000Z",
            "resourceBehaviorOptions": [
                "HideGroupInOutlook",
                "SubscribeMembersToCalendarEventsDisabled",
                "WelcomeEmailDisabled"
            ],
            "resourceProvisioningOptions": [
                "Team"
            ],
            "securityEnabled": false,
            "securityIdentifier": "S-1-12-1-1917184960-1200890829-2255305886-2269513327",
            "theme": null,
            "visibility": "Public",
            "onPremisesProvisioningErrors": []
        }
    ]
}
```
