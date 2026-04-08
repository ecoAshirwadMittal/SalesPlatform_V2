# JSON Structure: JSON_RMAStatusDeposco

## Sample JSON

```json
{
  "data": [
    {
      "self": {
        "id": 4377,
        "businessKey": {
          "number": "4500003",
          "businessUnit.code": "ECOATM"
        },
        "href": "https://api.deposco.com/latest/orders/customerReturns/4377"
      },
      "businessUnit": {
        "id": 73,
        "businessKey": {
          "code": "ECOATM"
        },
        "href": "https://api.deposco.com/latest/companies/73"
      },
      "number": "4500003",
      "orderPriority": 0,
      "orderSummary": null,
      "orderSource": "PWS",
      "secondaryOrderSource": null,
      "customerOrderNumber": "PWS-RMA-RMA2237925012",
      "relatedOrderNumber": null,
      "partnerInvoiceNumber": null,
      "consigneePartner": {
        "id": 17,
        "businessKey": {
          "code": "22379",
          "businessUnit.code": "ECOATM"
        },
        "href": "https://api.deposco.com/latest/tradingPartners/17"
      },
      "tradingPartnerSite": null,
      "shipToContact": {},
      "shipToAddress": {},
      "billToPartner": null,
      "billToContact": {},
      "billToAccountNumber": null,
      "billToAddress": {},
      "shipFromContact": {},
      "shipFromAddress": {
        "line3": null,
        "line4": null
      },
      "placedDate": "2025-09-16T15:00:47-05:00",
      "plannedArrivalDate": null,
      "plannedReleaseDate": null,
      "plannedShipDate": null,
      "earliestShipDate": null,
      "earliestDeliveryDate": null,
      "cancelByArrivalDate": null,
      "cancelByReleaseDate": null,
      "cancelByShipDate": null,
      "actualArrivalDate": null,
      "actualReleaseDate": null,
      "actualShipDate": null,
      "priceList": null,
      "orderTotal": 0,
      "orderSubtotal": 0,
      "orderDiscountSubtotal": 0,
      "orderShippingTotal": 0,
      "orderShipTotal": 0,
      "taxable": false,
      "orderTaxTotal": 0,
      "orderTaxableTotal": 0,
      "orderUntaxableTotal": 0,
      "shippingTaxTotal": 0,
      "shipVia": "",
      "shipVendor": null,
      "shipMethod": null,
      "freightTermsType": null,
      "freightBillToContact": {
        "email": null
      },
      "freightBillToAccount": null,
      "freightBillToAddress": {},
      "weight": {
        "weight": 0,
        "units": null
      },
      "returnRequired": false,
      "returnShipVia": null,
      "returnShipOption": null,
      "deliveryConfirmation": "0",
      "insuranceRequired": false,
      "homeDelivery": false,
      "residential": false,
      "saturdayDelivery": false,
      "hubId": null,
      "routingNumber": null,
      "incotermsType": null,
      "importType": "Unit",
      "importReference": null,
      "exportReference": null,
      "otherReferenceNumber": null,
      "otherReferenceNumber2": "",
      "integrationSource": null,
      "sendASN": null,
      "dropShip": false,
      "pickWave": null,
      "postReleaseState": "DEFAULT",
      "entryName": null,
      "entryPhone1": null,
      "saleConditionCode": null,
      "seller": null,
      "notificationCode1": null,
      "notificationCode2": null,
      "notificationCode3": null,
      "verbalConfirmationName": null,
      "verbalConfirmationPhoneNumber": null,
      "customAttribute1": null,
      "customAttribute2": null,
      "customAttribute3": null,
      "customAttribute4": null,
      "customAttribute5": null,
      "customAttribute6": null,
      "customAttribute7": "true",
      "customFields": [],
      "notes": [],
      "channels": [
        {
          "self": {
            "id": 21437,
            "businessKey": {
              "integration.name": "ECOATM API",
              "ref1": "4500003"
            }
          },
          "integration": {
            "id": 1,
            "businessKey": {
              "name": "ECOATM API"
            }
          },
          "schedulerTask": null,
          "ref1": "4500003",
          "createdDate": "2025-09-16T15:00:47-05:00",
          "updatedDate": "2025-09-16T15:00:47-05:00",
          "createdBy": {
            "id": 1425,
            "businessKey": {
              "username": "harish.reddy"
            },
            "href": "https://api.deposco.com/latest/users/1425"
          },
          "updatedBy": {
            "id": 1425,
            "businessKey": {
              "username": "harish.reddy"
            },
            "href": "https://api.deposco.com/latest/users/1425"
          }
        }
      ],
      "createdDate": "2025-09-16T15:00:47-05:00",
      "updatedDate": "2025-09-19T12:22:02-05:00",
      "createdBy": {
        "id": 1425,
        "businessKey": {
          "username": "harish.reddy"
        },
        "href": "https://api.deposco.com/latest/users/1425"
      },
      "updatedBy": {
        "id": 999,
        "businessKey": {
          "username": "scheduler"
        },
        "href": "https://api.deposco.com/latest/users/999"
      },
      "type": "Customer Return",
      "orderStatus": "New",
      "shipToFacility": {
        "id": 1,
        "businessKey": {
          "number": "LVL"
        },
        "href": "https://api.deposco.com/latest/facilities/1"
      },
      "parentOrder": null,
      "coHeader": null,
      "orderLines": {
        "data": [
          {
            "self": {
              "id": 42575,
              "businessKey": {
                "orderHeader.number": "4500003",
                "lineNumber": "4500003-1",
                "orderHeader.businessUnit.code": "ECOATM"
              },
              "href": "https://api.deposco.com/latest/orders/customerReturns/4377/orderLines/42575"
            },
            "businessUnit": {
              "id": 73,
              "businessKey": {
                "code": "ECOATM"
              },
              "href": "https://api.deposco.com/latest/companies/73"
            },
            "lineNumber": "4500003-1",
            "customerLineNumber": "356760089230681",
            "relatedOrderLine": null,
            "sortOrder": 0,
            "orderHeader": {
              "id": 4377,
              "businessKey": {
                "number": "4500003",
                "businessUnit.code": "ECOATM"
              },
              "href": "https://api.deposco.com/latest/orders/customerReturns/4377"
            },
            "item": {
              "id": 173,
              "businessKey": {
                "number": "PWS10000000",
                "businessUnit.code": "ECOATM"
              },
              "href": "https://api.deposco.com/latest/items/173"
            },
            "pack": {
              "id": 180,
              "businessKey": {
                "item.businessUnit.code": "ECOATM",
                "quantity": 1,
                "item.number": "PWS10000000"
              },
              "href": "https://api.deposco.com/latest/items/173/packs/180"
            },
            "description": null,
            "orderPackQuantity": 1,
            "canceledPackQuantity": 0,
            "lotNumber": null,
            "bornOnDate": null,
            "expirationDate": null,
            "productCode": null,
            "inventoryCondition": null,
            "inventoryAttribute1": "356760089230681",
            "inventoryAttribute2": null,
            "plannedShipDate": null,
            "plannedArrivalDate": null,
            "unitPrice": 999,
            "unitCost": 0,
            "priceCode": "Customer Return",
            "lineTotal": 0,
            "taxable": false,
            "taxCost": 0,
            "taxableTotal": 0,
            "untaxableTotal": 0,
            "extendedTotal": 0,
            "discountAmount": 0,
            "shippingAmount": 0,
            "weight": {
              "weight": 0,
              "units": null
            },
            "importReference": "4500003",
            "exportReference": null,
            "customAttribute1": "999.00",
            "customAttribute2": null,
            "customAttribute3": null,
            "customFields": [],
            "notes": [
              {
                "self": {
                  "id": 2733,
                  "businessKey": {
                    "orderLine.lineNumber": "4500003-1",
                    "orderHeader.number": "4500003",
                    "orderLine.orderHeader.number": "4500003",
                    "title": "RMA Reason",
                    "orderHeader.businessUnit.code": "ECOATM",
                    "orderLine.orderHeader.businessUnit.code": "ECOATM"
                  }
                },
                "title": "RMA Reason",
                "body": "Broken",
                "priority": null,
                "orderHeader": {
                  "id": 4377,
                  "businessKey": {
                    "number": "4500003",
                    "businessUnit.code": "ECOATM"
                  },
                  "href": "https://api.deposco.com/latest/orders/customerReturns/4377"
                },
                "orderLine": {
                  "id": 42575,
                  "businessKey": {
                    "orderHeader.number": "4500003",
                    "lineNumber": "4500003-1",
                    "orderHeader.businessUnit.code": "ECOATM"
                  },
                  "href": "https://api.deposco.com/latest/orders/customerReturns/4377/orderLines/42575"
                }
              }
            ],
            "channels": [
              {
                "self": {
                  "id": 21438,
                  "businessKey": {
                    "integration.name": "ECOATM API",
                    "ref1": "4500003-1"
                  }
                },
                "integration": {
                  "id": 1,
                  "businessKey": {
                    "name": "ECOATM API"
                  }
                },
                "schedulerTask": null,
                "ref1": "4500003-1",
                "createdDate": "2025-09-16T15:00:47-05:00",
                "updatedDate": "2025-09-16T15:00:47-05:00",
                "createdBy": {
                  "id": 1425,
                  "businessKey": {
                    "username": "harish.reddy"
                  },
                  "href": "https://api.deposco.com/latest/users/1425"
                },
                "updatedBy": {
                  "id": 1425,
                  "businessKey": {
                    "username": "harish.reddy"
                  },
                  "href": "https://api.deposco.com/latest/users/1425"
                }
              }
            ],
            "createdDate": "2025-09-16T15:00:47-05:00",
            "updatedDate": "2025-09-19T12:22:02-05:00",
            "createdBy": {
              "id": 1425,
              "businessKey": {
                "username": "harish.reddy"
              },
              "href": "https://api.deposco.com/latest/users/1425"
            },
            "updatedBy": {
              "id": 999,
              "businessKey": {
                "username": "scheduler"
              },
              "href": "https://api.deposco.com/latest/users/999"
            },
            "orderLineStatus": "New",
            "shipToFacility": null,
            "coLine": null,
            "receivedPackQuantity": 0,
            "receivedDamagedPackQuantity": 0
          }
        ],
        "links": [],
        "complete": true,
        "pages": 1
      }
    }
  ],
  "links": [],
  "complete": true,
  "pages": 1
}
```
