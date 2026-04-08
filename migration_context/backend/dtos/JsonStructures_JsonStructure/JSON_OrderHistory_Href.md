# JSON Structure: JSON_OrderHistory_Href

## Sample JSON

```json
{
  "data": [
    {
      "self": {
        "id": 202780,
        "businessKey": {
          "orderHeader.number": "5003807",
          "lineNumber": "5003807-26",
          "orderHeader.businessUnit.code": "ECOATM"
        },
        "href": "https://api.deposco.com/latest/orders/salesOrders/5267/orderLines/202780"
      },
      "businessUnit": {
        "id": 73,
        "businessKey": {
          "code": "ECOATM"
        },
        "href": "https://api.deposco.com/latest/companies/73"
      },
      "lineNumber": "5003807-26",
      "customerLineNumber": "26",
      "relatedOrderLine": null,
      "sortOrder": 0,
      "orderHeader": {
        "id": 5267,
        "businessKey": {
          "number": "5003807",
          "businessUnit.code": "ECOATM"
        },
        "href": "https://api.deposco.com/latest/orders/salesOrders/5267"
      },
      "item": {
        "id": 25478,
        "businessKey": {
          "number": "PWS899160940849",
          "businessUnit.code": "ECOATM"
        },
        "href": "https://api.deposco.com/latest/items/25478"
      },
      "pack": {
        "id": 25478,
        "businessKey": {
          "item.businessUnit.code": "ECOATM",
          "quantity": 1,
          "item.number": "PWS899160940849"
        },
        "href": "https://api.deposco.com/latest/items/25478/packs/25478"
      },
      "description": null,
      "orderPackQuantity": 3,
      "canceledPackQuantity": 0,
      "lotNumber": null,
      "bornOnDate": null,
      "expirationDate": null,
      "productCode": null,
      "inventoryCondition": null,
      "inventoryAttribute1": null,
      "inventoryAttribute2": null,
      "plannedShipDate": null,
      "plannedArrivalDate": null,
      "unitPrice": 340,
      "unitCost": 0,
      "priceCode": "Sales Order",
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
      "importReference": null,
      "exportReference": null,
      "customAttribute1": null,
      "customAttribute2": null,
      "customAttribute3": null,
      "customFields": [],
      "notes": [],
      "channels": [
        {
          "self": {
            "id": 138602,
            "businessKey": {
              "integration.name": "ECOATM API",
              "ref1": "5003807-26"
            }
          },
          "integration": {
            "id": 1,
            "businessKey": {
              "name": "ECOATM API"
            }
          },
          "schedulerTask": null,
          "ref1": "5003807-26",
          "createdDate": "2025-12-08T12:00:56-06:00",
          "updatedDate": "2025-12-08T12:00:56-06:00",
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
      "createdDate": "2025-12-08T12:00:56-06:00",
      "updatedDate": "2025-12-12T15:03:43-06:00",
      "createdBy": {
        "id": 1425,
        "businessKey": {
          "username": "harish.reddy"
        },
        "href": "https://api.deposco.com/latest/users/1425"
      },
      "updatedBy": {
        "id": 1557,
        "businessKey": {
          "username": "carlos.batista"
        },
        "href": "https://api.deposco.com/latest/users/1557"
      },
      "orderLineStatus": "Complete",
      "shipFromFacility": null,
      "coLine": null,
      "allocatedQuantity": 3,
      "unallocatedQuantity": 0,
      "pickedPackQuantity": 3,
      "shortagePackQuantity": 0,
      "shippedPackQuantity": 3,
      "returnedPackQuantity": 0,
      "preallocated": false,
      "isBackOrdered": null,
      "platinumLineNo": 0,
      "directedToZone": {
        "id": 40,
        "businessKey": {
          "facility.number": "LVL",
          "name": "PWS_AppleCellPhone"
        },
        "href": "https://api.deposco.com/latest/zones/40"
      },
      "allocateFromZone": false,
      "carrierSpecialServices": []
    },
    {
      "self": {
        "id": 202779,
        "businessKey": {
          "orderHeader.number": "5003807",
          "lineNumber": "5003807-25",
          "orderHeader.businessUnit.code": "ECOATM"
        },
        "href": "https://api.deposco.com/latest/orders/salesOrders/5267/orderLines/202779"
      },
      "businessUnit": {
        "id": 73,
        "businessKey": {
          "code": "ECOATM"
        },
        "href": "https://api.deposco.com/latest/companies/73"
      },
      "lineNumber": "5003807-25",
      "customerLineNumber": "25",
      "relatedOrderLine": null,
      "sortOrder": 0,
      "orderHeader": {
        "id": 5267,
        "businessKey": {
          "number": "5003807",
          "businessUnit.code": "ECOATM"
        },
        "href": "https://api.deposco.com/latest/orders/salesOrders/5267"
      },
      "item": {
        "id": 6279,
        "businessKey": {
          "number": "PWS034606135039",
          "businessUnit.code": "ECOATM"
        },
        "href": "https://api.deposco.com/latest/items/6279"
      },
      "pack": {
        "id": 6279,
        "businessKey": {
          "item.businessUnit.code": "ECOATM",
          "quantity": 1,
          "item.number": "PWS034606135039"
        },
        "href": "https://api.deposco.com/latest/items/6279/packs/6279"
      },
      "description": null,
      "orderPackQuantity": 10,
      "canceledPackQuantity": 0,
      "lotNumber": null,
      "bornOnDate": null,
      "expirationDate": null,
      "productCode": null,
      "inventoryCondition": null,
      "inventoryAttribute1": null,
      "inventoryAttribute2": null,
      "plannedShipDate": null,
      "plannedArrivalDate": null,
      "unitPrice": 575,
      "unitCost": 0,
      "priceCode": "Sales Order",
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
      "importReference": null,
      "exportReference": null,
      "customAttribute1": null,
      "customAttribute2": null,
      "customAttribute3": null,
      "customFields": [],
      "notes": [],
      "channels": [
        {
          "self": {
            "id": 138601,
            "businessKey": {
              "integration.name": "ECOATM API",
              "ref1": "5003807-25"
            }
          },
          "integration": {
            "id": 1,
            "businessKey": {
              "name": "ECOATM API"
            }
          },
          "schedulerTask": null,
          "ref1": "5003807-25",
          "createdDate": "2025-12-08T12:00:56-06:00",
          "updatedDate": "2025-12-08T12:00:56-06:00",
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
      "createdDate": "2025-12-08T12:00:56-06:00",
      "updatedDate": "2025-12-12T15:03:41-06:00",
      "createdBy": {
        "id": 1425,
        "businessKey": {
          "username": "harish.reddy"
        },
        "href": "https://api.deposco.com/latest/users/1425"
      },
      "updatedBy": {
        "id": 1557,
        "businessKey": {
          "username": "carlos.batista"
        },
        "href": "https://api.deposco.com/latest/users/1557"
      },
      "orderLineStatus": "Complete",
      "shipFromFacility": null,
      "coLine": null,
      "allocatedQuantity": 10,
      "unallocatedQuantity": 0,
      "pickedPackQuantity": 10,
      "shortagePackQuantity": 0,
      "shippedPackQuantity": 10,
      "returnedPackQuantity": 0,
      "preallocated": false,
      "isBackOrdered": null,
      "platinumLineNo": 0,
      "directedToZone": {
        "id": 40,
        "businessKey": {
          "facility.number": "LVL",
          "name": "PWS_AppleCellPhone"
        },
        "href": "https://api.deposco.com/latest/zones/40"
      },
      "allocateFromZone": false,
      "carrierSpecialServices": []
    },
    {
      "self": {
        "id": 202778,
        "businessKey": {
          "orderHeader.number": "5003807",
          "lineNumber": "5003807-24",
          "orderHeader.businessUnit.code": "ECOATM"
        },
        "href": "https://api.deposco.com/latest/orders/salesOrders/5267/orderLines/202778"
      },
      "businessUnit": {
        "id": 73,
        "businessKey": {
          "code": "ECOATM"
        },
        "href": "https://api.deposco.com/latest/companies/73"
      },
      "lineNumber": "5003807-24",
      "customerLineNumber": "24",
      "relatedOrderLine": null,
      "sortOrder": 0,
      "orderHeader": {
        "id": 5267,
        "businessKey": {
          "number": "5003807",
          "businessUnit.code": "ECOATM"
        },
        "href": "https://api.deposco.com/latest/orders/salesOrders/5267"
      },
      "item": {
        "id": 23525,
        "businessKey": {
          "number": "PWS622552556543",
          "businessUnit.code": "ECOATM"
        },
        "href": "https://api.deposco.com/latest/items/23525"
      },
      "pack": {
        "id": 23525,
        "businessKey": {
          "item.businessUnit.code": "ECOATM",
          "quantity": 1,
          "item.number": "PWS622552556543"
        },
        "href": "https://api.deposco.com/latest/items/23525/packs/23525"
      },
      "description": null,
      "orderPackQuantity": 1,
      "canceledPackQuantity": 0,
      "lotNumber": null,
      "bornOnDate": null,
      "expirationDate": null,
      "productCode": null,
      "inventoryCondition": null,
      "inventoryAttribute1": null,
      "inventoryAttribute2": null,
      "plannedShipDate": null,
      "plannedArrivalDate": null,
      "unitPrice": 475,
      "unitCost": 0,
      "priceCode": "Sales Order",
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
      "importReference": null,
      "exportReference": null,
      "customAttribute1": null,
      "customAttribute2": null,
      "customAttribute3": null,
      "customFields": [],
      "notes": [],
      "channels": [
        {
          "self": {
            "id": 138600,
            "businessKey": {
              "integration.name": "ECOATM API",
              "ref1": "5003807-24"
            }
          },
          "integration": {
            "id": 1,
            "businessKey": {
              "name": "ECOATM API"
            }
          },
          "schedulerTask": null,
          "ref1": "5003807-24",
          "createdDate": "2025-12-08T12:00:56-06:00",
          "updatedDate": "2025-12-08T12:00:56-06:00",
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
      "createdDate": "2025-12-08T12:00:56-06:00",
      "updatedDate": "2025-12-12T15:03:43-06:00",
      "createdBy": {
        "id": 1425,
        "businessKey": {
          "username": "harish.reddy"
        },
        "href": "https://api.deposco.com/latest/users/1425"
      },
      "updatedBy": {
        "id": 1557,
        "businessKey": {
          "username": "carlos.batista"
        },
        "href": "https://api.deposco.com/latest/users/1557"
      },
      "orderLineStatus": "Complete",
      "shipFromFacility": null,
      "coLine": null,
      "allocatedQuantity": 1,
      "unallocatedQuantity": 0,
      "pickedPackQuantity": 1,
      "shortagePackQuantity": 0,
      "shippedPackQuantity": 1,
      "returnedPackQuantity": 0,
      "preallocated": false,
      "isBackOrdered": null,
      "platinumLineNo": 0,
      "directedToZone": {
        "id": 40,
        "businessKey": {
          "facility.number": "LVL",
          "name": "PWS_AppleCellPhone"
        },
        "href": "https://api.deposco.com/latest/zones/40"
      },
      "allocateFromZone": false,
      "carrierSpecialServices": []
    },
    {
      "self": {
        "id": 202777,
        "businessKey": {
          "orderHeader.number": "5003807",
          "lineNumber": "5003807-23",
          "orderHeader.businessUnit.code": "ECOATM"
        },
        "href": "https://api.deposco.com/latest/orders/salesOrders/5267/orderLines/202777"
      },
      "businessUnit": {
        "id": 73,
        "businessKey": {
          "code": "ECOATM"
        },
        "href": "https://api.deposco.com/latest/companies/73"
      },
      "lineNumber": "5003807-23",
      "customerLineNumber": "23",
      "relatedOrderLine": null,
      "sortOrder": 0,
      "orderHeader": {
        "id": 5267,
        "businessKey": {
          "number": "5003807",
          "businessUnit.code": "ECOATM"
        },
        "href": "https://api.deposco.com/latest/orders/salesOrders/5267"
      },
      "item": {
        "id": 24113,
        "businessKey": {
          "number": "PWS705701730407",
          "businessUnit.code": "ECOATM"
        },
        "href": "https://api.deposco.com/latest/items/24113"
      },
      "pack": {
        "id": 24113,
        "businessKey": {
          "item.businessUnit.code": "ECOATM",
          "quantity": 1,
          "item.number": "PWS705701730407"
        },
        "href": "https://api.deposco.com/latest/items/24113/packs/24113"
      },
      "description": null,
      "orderPackQuantity": 2,
      "canceledPackQuantity": 0,
      "lotNumber": null,
      "bornOnDate": null,
      "expirationDate": null,
      "productCode": null,
      "inventoryCondition": null,
      "inventoryAttribute1": null,
      "inventoryAttribute2": null,
      "plannedShipDate": null,
      "plannedArrivalDate": null,
      "unitPrice": 175,
      "unitCost": 0,
      "priceCode": "Sales Order",
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
      "importReference": null,
      "exportReference": null,
      "customAttribute1": null,
      "customAttribute2": null,
      "customAttribute3": null,
      "customFields": [],
      "notes": [],
      "channels": [
        {
          "self": {
            "id": 138599,
            "businessKey": {
              "integration.name": "ECOATM API",
              "ref1": "5003807-23"
            }
          },
          "integration": {
            "id": 1,
            "businessKey": {
              "name": "ECOATM API"
            }
          },
          "schedulerTask": null,
          "ref1": "5003807-23",
          "createdDate": "2025-12-08T12:00:56-06:00",
          "updatedDate": "2025-12-08T12:00:56-06:00",
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
      "createdDate": "2025-12-08T12:00:56-06:00",
      "updatedDate": "2025-12-12T15:03:43-06:00",
      "createdBy": {
        "id": 1425,
        "businessKey": {
          "username": "harish.reddy"
        },
        "href": "https://api.deposco.com/latest/users/1425"
      },
      "updatedBy": {
        "id": 1557,
        "businessKey": {
          "username": "carlos.batista"
        },
        "href": "https://api.deposco.com/latest/users/1557"
      },
      "orderLineStatus": "Complete",
      "shipFromFacility": null,
      "coLine": null,
      "allocatedQuantity": 2,
      "unallocatedQuantity": 0,
      "pickedPackQuantity": 2,
      "shortagePackQuantity": 0,
      "shippedPackQuantity": 2,
      "returnedPackQuantity": 0,
      "preallocated": false,
      "isBackOrdered": null,
      "platinumLineNo": 0,
      "directedToZone": {
        "id": 40,
        "businessKey": {
          "facility.number": "LVL",
          "name": "PWS_AppleCellPhone"
        },
        "href": "https://api.deposco.com/latest/zones/40"
      },
      "allocateFromZone": false,
      "carrierSpecialServices": []
    },
    {
      "self": {
        "id": 202776,
        "businessKey": {
          "orderHeader.number": "5003807",
          "lineNumber": "5003807-22",
          "orderHeader.businessUnit.code": "ECOATM"
        },
        "href": "https://api.deposco.com/latest/orders/salesOrders/5267/orderLines/202776"
      },
      "businessUnit": {
        "id": 73,
        "businessKey": {
          "code": "ECOATM"
        },
        "href": "https://api.deposco.com/latest/companies/73"
      },
      "lineNumber": "5003807-22",
      "customerLineNumber": "22",
      "relatedOrderLine": null,
      "sortOrder": 0,
      "orderHeader": {
        "id": 5267,
        "businessKey": {
          "number": "5003807",
          "businessUnit.code": "ECOATM"
        },
        "href": "https://api.deposco.com/latest/orders/salesOrders/5267"
      },
      "item": {
        "id": 20858,
        "businessKey": {
          "number": "PWS231099935051",
          "businessUnit.code": "ECOATM"
        },
        "href": "https://api.deposco.com/latest/items/20858"
      },
      "pack": {
        "id": 20858,
        "businessKey": {
          "item.businessUnit.code": "ECOATM",
          "quantity": 1,
          "item.number": "PWS231099935051"
        },
        "href": "https://api.deposco.com/latest/items/20858/packs/20858"
      },
      "description": null,
      "orderPackQuantity": 7,
      "canceledPackQuantity": 4,
      "lotNumber": null,
      "bornOnDate": null,
      "expirationDate": null,
      "productCode": null,
      "inventoryCondition": null,
      "inventoryAttribute1": null,
      "inventoryAttribute2": null,
      "plannedShipDate": null,
      "plannedArrivalDate": null,
      "unitPrice": 340,
      "unitCost": 0,
      "priceCode": "Sales Order",
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
      "importReference": null,
      "exportReference": null,
      "customAttribute1": null,
      "customAttribute2": null,
      "customAttribute3": null,
      "customFields": [],
      "notes": [],
      "channels": [
        {
          "self": {
            "id": 138598,
            "businessKey": {
              "integration.name": "ECOATM API",
              "ref1": "5003807-22"
            }
          },
          "integration": {
            "id": 1,
            "businessKey": {
              "name": "ECOATM API"
            }
          },
          "schedulerTask": null,
          "ref1": "5003807-22",
          "createdDate": "2025-12-08T12:00:56-06:00",
          "updatedDate": "2025-12-08T12:00:56-06:00",
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
      "createdDate": "2025-12-08T12:00:56-06:00",
      "updatedDate": "2025-12-12T15:03:43-06:00",
      "createdBy": {
        "id": 1425,
        "businessKey": {
          "username": "harish.reddy"
        },
        "href": "https://api.deposco.com/latest/users/1425"
      },
      "updatedBy": {
        "id": 1557,
        "businessKey": {
          "username": "carlos.batista"
        },
        "href": "https://api.deposco.com/latest/users/1557"
      },
      "orderLineStatus": "Complete",
      "shipFromFacility": null,
      "coLine": null,
      "allocatedQuantity": 3,
      "unallocatedQuantity": 0,
      "pickedPackQuantity": 3,
      "shortagePackQuantity": 0,
      "shippedPackQuantity": 3,
      "returnedPackQuantity": 0,
      "preallocated": false,
      "isBackOrdered": null,
      "platinumLineNo": 0,
      "directedToZone": null,
      "allocateFromZone": false,
      "carrierSpecialServices": []
    },
    {
      "self": {
        "id": 202775,
        "businessKey": {
          "orderHeader.number": "5003807",
          "lineNumber": "5003807-21",
          "orderHeader.businessUnit.code": "ECOATM"
        },
        "href": "https://api.deposco.com/latest/orders/salesOrders/5267/orderLines/202775"
      },
      "businessUnit": {
        "id": 73,
        "businessKey": {
          "code": "ECOATM"
        },
        "href": "https://api.deposco.com/latest/companies/73"
      },
      "lineNumber": "5003807-21",
      "customerLineNumber": "21",
      "relatedOrderLine": null,
      "sortOrder": 0,
      "orderHeader": {
        "id": 5267,
        "businessKey": {
          "number": "5003807",
          "businessUnit.code": "ECOATM"
        },
        "href": "https://api.deposco.com/latest/orders/salesOrders/5267"
      },
      "item": {
        "id": 20675,
        "businessKey": {
          "number": "PWS204841878520",
          "businessUnit.code": "ECOATM"
        },
        "href": "https://api.deposco.com/latest/items/20675"
      },
      "pack": {
        "id": 20675,
        "businessKey": {
          "item.businessUnit.code": "ECOATM",
          "quantity": 1,
          "item.number": "PWS204841878520"
        },
        "href": "https://api.deposco.com/latest/items/20675/packs/20675"
      },
      "description": null,
      "orderPackQuantity": 2,
      "canceledPackQuantity": 0,
      "lotNumber": null,
      "bornOnDate": null,
      "expirationDate": null,
      "productCode": null,
      "inventoryCondition": null,
      "inventoryAttribute1": null,
      "inventoryAttribute2": null,
      "plannedShipDate": null,
      "plannedArrivalDate": null,
      "unitPrice": 360,
      "unitCost": 0,
      "priceCode": "Sales Order",
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
      "importReference": null,
      "exportReference": null,
      "customAttribute1": null,
      "customAttribute2": null,
      "customAttribute3": null,
      "customFields": [],
      "notes": [],
      "channels": [
        {
          "self": {
            "id": 138597,
            "businessKey": {
              "integration.name": "ECOATM API",
              "ref1": "5003807-21"
            }
          },
          "integration": {
            "id": 1,
            "businessKey": {
              "name": "ECOATM API"
            }
          },
          "schedulerTask": null,
          "ref1": "5003807-21",
          "createdDate": "2025-12-08T12:00:56-06:00",
          "updatedDate": "2025-12-08T12:00:56-06:00",
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
      "createdDate": "2025-12-08T12:00:56-06:00",
      "updatedDate": "2025-12-12T15:03:42-06:00",
      "createdBy": {
        "id": 1425,
        "businessKey": {
          "username": "harish.reddy"
        },
        "href": "https://api.deposco.com/latest/users/1425"
      },
      "updatedBy": {
        "id": 1557,
        "businessKey": {
          "username": "carlos.batista"
        },
        "href": "https://api.deposco.com/latest/users/1557"
      },
      "orderLineStatus": "Complete",
      "shipFromFacility": null,
      "coLine": null,
      "allocatedQuantity": 2,
      "unallocatedQuantity": 0,
      "pickedPackQuantity": 2,
      "shortagePackQuantity": 0,
      "shippedPackQuantity": 2,
      "returnedPackQuantity": 0,
      "preallocated": false,
      "isBackOrdered": null,
      "platinumLineNo": 0,
      "directedToZone": {
        "id": 40,
        "businessKey": {
          "facility.number": "LVL",
          "name": "PWS_AppleCellPhone"
        },
        "href": "https://api.deposco.com/latest/zones/40"
      },
      "allocateFromZone": false,
      "carrierSpecialServices": []
    },
    {
      "self": {
        "id": 202774,
        "businessKey": {
          "orderHeader.number": "5003807",
          "lineNumber": "5003807-20",
          "orderHeader.businessUnit.code": "ECOATM"
        },
        "href": "https://api.deposco.com/latest/orders/salesOrders/5267/orderLines/202774"
      },
      "businessUnit": {
        "id": 73,
        "businessKey": {
          "code": "ECOATM"
        },
        "href": "https://api.deposco.com/latest/companies/73"
      },
      "lineNumber": "5003807-20",
      "customerLineNumber": "20",
      "relatedOrderLine": null,
      "sortOrder": 0,
      "orderHeader": {
        "id": 5267,
        "businessKey": {
          "number": "5003807",
          "businessUnit.code": "ECOATM"
        },
        "href": "https://api.deposco.com/latest/orders/salesOrders/5267"
      },
      "item": {
        "id": 20757,
        "businessKey": {
          "number": "PWS216444677565",
          "businessUnit.code": "ECOATM"
        },
        "href": "https://api.deposco.com/latest/items/20757"
      },
      "pack": {
        "id": 20757,
        "businessKey": {
          "item.businessUnit.code": "ECOATM",
          "quantity": 1,
          "item.number": "PWS216444677565"
        },
        "href": "https://api.deposco.com/latest/items/20757/packs/20757"
      },
      "description": null,
      "orderPackQuantity": 5,
      "canceledPackQuantity": 0,
      "lotNumber": null,
      "bornOnDate": null,
      "expirationDate": null,
      "productCode": null,
      "inventoryCondition": null,
      "inventoryAttribute1": null,
      "inventoryAttribute2": null,
      "plannedShipDate": null,
      "plannedArrivalDate": null,
      "unitPrice": 475,
      "unitCost": 0,
      "priceCode": "Sales Order",
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
      "importReference": null,
      "exportReference": null,
      "customAttribute1": null,
      "customAttribute2": null,
      "customAttribute3": null,
      "customFields": [],
      "notes": [],
      "channels": [
        {
          "self": {
            "id": 138596,
            "businessKey": {
              "integration.name": "ECOATM API",
              "ref1": "5003807-20"
            }
          },
          "integration": {
            "id": 1,
            "businessKey": {
              "name": "ECOATM API"
            }
          },
          "schedulerTask": null,
          "ref1": "5003807-20",
          "createdDate": "2025-12-08T12:00:56-06:00",
          "updatedDate": "2025-12-08T12:00:56-06:00",
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
      "createdDate": "2025-12-08T12:00:56-06:00",
      "updatedDate": "2025-12-12T15:03:42-06:00",
      "createdBy": {
        "id": 1425,
        "businessKey": {
          "username": "harish.reddy"
        },
        "href": "https://api.deposco.com/latest/users/1425"
      },
      "updatedBy": {
        "id": 1557,
        "businessKey": {
          "username": "carlos.batista"
        },
        "href": "https://api.deposco.com/latest/users/1557"
      },
      "orderLineStatus": "Complete",
      "shipFromFacility": null,
      "coLine": null,
      "allocatedQuantity": 5,
      "unallocatedQuantity": 0,
      "pickedPackQuantity": 5,
      "shortagePackQuantity": 0,
      "shippedPackQuantity": 5,
      "returnedPackQuantity": 0,
      "preallocated": false,
      "isBackOrdered": null,
      "platinumLineNo": 0,
      "directedToZone": {
        "id": 40,
        "businessKey": {
          "facility.number": "LVL",
          "name": "PWS_AppleCellPhone"
        },
        "href": "https://api.deposco.com/latest/zones/40"
      },
      "allocateFromZone": false,
      "carrierSpecialServices": []
    },
    {
      "self": {
        "id": 202773,
        "businessKey": {
          "orderHeader.number": "5003807",
          "lineNumber": "5003807-19",
          "orderHeader.businessUnit.code": "ECOATM"
        },
        "href": "https://api.deposco.com/latest/orders/salesOrders/5267/orderLines/202773"
      },
      "businessUnit": {
        "id": 73,
        "businessKey": {
          "code": "ECOATM"
        },
        "href": "https://api.deposco.com/latest/companies/73"
      },
      "lineNumber": "5003807-19",
      "customerLineNumber": "19",
      "relatedOrderLine": null,
      "sortOrder": 0,
      "orderHeader": {
        "id": 5267,
        "businessKey": {
          "number": "5003807",
          "businessUnit.code": "ECOATM"
        },
        "href": "https://api.deposco.com/latest/orders/salesOrders/5267"
      },
      "item": {
        "id": 23076,
        "businessKey": {
          "number": "PWS557024382683",
          "businessUnit.code": "ECOATM"
        },
        "href": "https://api.deposco.com/latest/items/23076"
      },
      "pack": {
        "id": 23076,
        "businessKey": {
          "item.businessUnit.code": "ECOATM",
          "quantity": 1,
          "item.number": "PWS557024382683"
        },
        "href": "https://api.deposco.com/latest/items/23076/packs/23076"
      },
      "description": null,
      "orderPackQuantity": 5,
      "canceledPackQuantity": 0,
      "lotNumber": null,
      "bornOnDate": null,
      "expirationDate": null,
      "productCode": null,
      "inventoryCondition": null,
      "inventoryAttribute1": null,
      "inventoryAttribute2": null,
      "plannedShipDate": null,
      "plannedArrivalDate": null,
      "unitPrice": 340,
      "unitCost": 0,
      "priceCode": "Sales Order",
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
      "importReference": null,
      "exportReference": null,
      "customAttribute1": null,
      "customAttribute2": null,
      "customAttribute3": null,
      "customFields": [],
      "notes": [],
      "channels": [
        {
          "self": {
            "id": 138595,
            "businessKey": {
              "integration.name": "ECOATM API",
              "ref1": "5003807-19"
            }
          },
          "integration": {
            "id": 1,
            "businessKey": {
              "name": "ECOATM API"
            }
          },
          "schedulerTask": null,
          "ref1": "5003807-19",
          "createdDate": "2025-12-08T12:00:56-06:00",
          "updatedDate": "2025-12-08T12:00:56-06:00",
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
      "createdDate": "2025-12-08T12:00:56-06:00",
      "updatedDate": "2025-12-12T15:03:42-06:00",
      "createdBy": {
        "id": 1425,
        "businessKey": {
          "username": "harish.reddy"
        },
        "href": "https://api.deposco.com/latest/users/1425"
      },
      "updatedBy": {
        "id": 1557,
        "businessKey": {
          "username": "carlos.batista"
        },
        "href": "https://api.deposco.com/latest/users/1557"
      },
      "orderLineStatus": "Complete",
      "shipFromFacility": null,
      "coLine": null,
      "allocatedQuantity": 5,
      "unallocatedQuantity": 0,
      "pickedPackQuantity": 5,
      "shortagePackQuantity": 0,
      "shippedPackQuantity": 5,
      "returnedPackQuantity": 0,
      "preallocated": false,
      "isBackOrdered": null,
      "platinumLineNo": 0,
      "directedToZone": {
        "id": 40,
        "businessKey": {
          "facility.number": "LVL",
          "name": "PWS_AppleCellPhone"
        },
        "href": "https://api.deposco.com/latest/zones/40"
      },
      "allocateFromZone": false,
      "carrierSpecialServices": []
    },
    {
      "self": {
        "id": 202772,
        "businessKey": {
          "orderHeader.number": "5003807",
          "lineNumber": "5003807-18",
          "orderHeader.businessUnit.code": "ECOATM"
        },
        "href": "https://api.deposco.com/latest/orders/salesOrders/5267/orderLines/202772"
      },
      "businessUnit": {
        "id": 73,
        "businessKey": {
          "code": "ECOATM"
        },
        "href": "https://api.deposco.com/latest/companies/73"
      },
      "lineNumber": "5003807-18",
      "customerLineNumber": "18",
      "relatedOrderLine": null,
      "sortOrder": 0,
      "orderHeader": {
        "id": 5267,
        "businessKey": {
          "number": "5003807",
          "businessUnit.code": "ECOATM"
        },
        "href": "https://api.deposco.com/latest/orders/salesOrders/5267"
      },
      "item": {
        "id": 20501,
        "businessKey": {
          "number": "PWS182777566430",
          "businessUnit.code": "ECOATM"
        },
        "href": "https://api.deposco.com/latest/items/20501"
      },
      "pack": {
        "id": 20501,
        "businessKey": {
          "item.businessUnit.code": "ECOATM",
          "quantity": 1,
          "item.number": "PWS182777566430"
        },
        "href": "https://api.deposco.com/latest/items/20501/packs/20501"
      },
      "description": null,
      "orderPackQuantity": 7,
      "canceledPackQuantity": 0,
      "lotNumber": null,
      "bornOnDate": null,
      "expirationDate": null,
      "productCode": null,
      "inventoryCondition": null,
      "inventoryAttribute1": null,
      "inventoryAttribute2": null,
      "plannedShipDate": null,
      "plannedArrivalDate": null,
      "unitPrice": 365,
      "unitCost": 0,
      "priceCode": "Sales Order",
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
      "importReference": null,
      "exportReference": null,
      "customAttribute1": null,
      "customAttribute2": null,
      "customAttribute3": null,
      "customFields": [],
      "notes": [],
      "channels": [
        {
          "self": {
            "id": 138594,
            "businessKey": {
              "integration.name": "ECOATM API",
              "ref1": "5003807-18"
            }
          },
          "integration": {
            "id": 1,
            "businessKey": {
              "name": "ECOATM API"
            }
          },
          "schedulerTask": null,
          "ref1": "5003807-18",
          "createdDate": "2025-12-08T12:00:56-06:00",
          "updatedDate": "2025-12-08T12:00:56-06:00",
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
      "createdDate": "2025-12-08T12:00:56-06:00",
      "updatedDate": "2025-12-12T15:03:42-06:00",
      "createdBy": {
        "id": 1425,
        "businessKey": {
          "username": "harish.reddy"
        },
        "href": "https://api.deposco.com/latest/users/1425"
      },
      "updatedBy": {
        "id": 1557,
        "businessKey": {
          "username": "carlos.batista"
        },
        "href": "https://api.deposco.com/latest/users/1557"
      },
      "orderLineStatus": "Complete",
      "shipFromFacility": null,
      "coLine": null,
      "allocatedQuantity": 7,
      "unallocatedQuantity": 0,
      "pickedPackQuantity": 7,
      "shortagePackQuantity": 0,
      "shippedPackQuantity": 7,
      "returnedPackQuantity": 0,
      "preallocated": false,
      "isBackOrdered": null,
      "platinumLineNo": 0,
      "directedToZone": {
        "id": 40,
        "businessKey": {
          "facility.number": "LVL",
          "name": "PWS_AppleCellPhone"
        },
        "href": "https://api.deposco.com/latest/zones/40"
      },
      "allocateFromZone": false,
      "carrierSpecialServices": []
    },
    {
      "self": {
        "id": 202771,
        "businessKey": {
          "orderHeader.number": "5003807",
          "lineNumber": "5003807-17",
          "orderHeader.businessUnit.code": "ECOATM"
        },
        "href": "https://api.deposco.com/latest/orders/salesOrders/5267/orderLines/202771"
      },
      "businessUnit": {
        "id": 73,
        "businessKey": {
          "code": "ECOATM"
        },
        "href": "https://api.deposco.com/latest/companies/73"
      },
      "lineNumber": "5003807-17",
      "customerLineNumber": "17",
      "relatedOrderLine": null,
      "sortOrder": 0,
      "orderHeader": {
        "id": 5267,
        "businessKey": {
          "number": "5003807",
          "businessUnit.code": "ECOATM"
        },
        "href": "https://api.deposco.com/latest/orders/salesOrders/5267"
      },
      "item": {
        "id": 16123,
        "businessKey": {
          "number": "PWS10009632",
          "businessUnit.code": "ECOATM"
        },
        "href": "https://api.deposco.com/latest/items/16123"
      },
      "pack": {
        "id": 16123,
        "businessKey": {
          "item.businessUnit.code": "ECOATM",
          "quantity": 1,
          "item.number": "PWS10009632"
        },
        "href": "https://api.deposco.com/latest/items/16123/packs/16123"
      },
      "description": null,
      "orderPackQuantity": 5,
      "canceledPackQuantity": 0,
      "lotNumber": null,
      "bornOnDate": null,
      "expirationDate": null,
      "productCode": null,
      "inventoryCondition": null,
      "inventoryAttribute1": null,
      "inventoryAttribute2": null,
      "plannedShipDate": null,
      "plannedArrivalDate": null,
      "unitPrice": 520,
      "unitCost": 0,
      "priceCode": "Sales Order",
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
      "importReference": null,
      "exportReference": null,
      "customAttribute1": null,
      "customAttribute2": null,
      "customAttribute3": null,
      "customFields": [],
      "notes": [],
      "channels": [
        {
          "self": {
            "id": 138593,
            "businessKey": {
              "integration.name": "ECOATM API",
              "ref1": "5003807-17"
            }
          },
          "integration": {
            "id": 1,
            "businessKey": {
              "name": "ECOATM API"
            }
          },
          "schedulerTask": null,
          "ref1": "5003807-17",
          "createdDate": "2025-12-08T12:00:56-06:00",
          "updatedDate": "2025-12-08T12:00:56-06:00",
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
      "createdDate": "2025-12-08T12:00:56-06:00",
      "updatedDate": "2025-12-12T15:03:42-06:00",
      "createdBy": {
        "id": 1425,
        "businessKey": {
          "username": "harish.reddy"
        },
        "href": "https://api.deposco.com/latest/users/1425"
      },
      "updatedBy": {
        "id": 1557,
        "businessKey": {
          "username": "carlos.batista"
        },
        "href": "https://api.deposco.com/latest/users/1557"
      },
      "orderLineStatus": "Complete",
      "shipFromFacility": null,
      "coLine": null,
      "allocatedQuantity": 5,
      "unallocatedQuantity": 0,
      "pickedPackQuantity": 5,
      "shortagePackQuantity": 0,
      "shippedPackQuantity": 5,
      "returnedPackQuantity": 0,
      "preallocated": false,
      "isBackOrdered": null,
      "platinumLineNo": 0,
      "directedToZone": {
        "id": 40,
        "businessKey": {
          "facility.number": "LVL",
          "name": "PWS_AppleCellPhone"
        },
        "href": "https://api.deposco.com/latest/zones/40"
      },
      "allocateFromZone": false,
      "carrierSpecialServices": []
    },
    {
      "self": {
        "id": 202770,
        "businessKey": {
          "orderHeader.number": "5003807",
          "lineNumber": "5003807-16",
          "orderHeader.businessUnit.code": "ECOATM"
        },
        "href": "https://api.deposco.com/latest/orders/salesOrders/5267/orderLines/202770"
      },
      "businessUnit": {
        "id": 73,
        "businessKey": {
          "code": "ECOATM"
        },
        "href": "https://api.deposco.com/latest/companies/73"
      },
      "lineNumber": "5003807-16",
      "customerLineNumber": "16",
      "relatedOrderLine": null,
      "sortOrder": 0,
      "orderHeader": {
        "id": 5267,
        "businessKey": {
          "number": "5003807",
          "businessUnit.code": "ECOATM"
        },
        "href": "https://api.deposco.com/latest/orders/salesOrders/5267"
      },
      "item": {
        "id": 22689,
        "businessKey": {
          "number": "PWS503333325631",
          "businessUnit.code": "ECOATM"
        },
        "href": "https://api.deposco.com/latest/items/22689"
      },
      "pack": {
        "id": 22689,
        "businessKey": {
          "item.businessUnit.code": "ECOATM",
          "quantity": 1,
          "item.number": "PWS503333325631"
        },
        "href": "https://api.deposco.com/latest/items/22689/packs/22689"
      },
      "description": null,
      "orderPackQuantity": 14,
      "canceledPackQuantity": 0,
      "lotNumber": null,
      "bornOnDate": null,
      "expirationDate": null,
      "productCode": null,
      "inventoryCondition": null,
      "inventoryAttribute1": null,
      "inventoryAttribute2": null,
      "plannedShipDate": null,
      "plannedArrivalDate": null,
      "unitPrice": 365,
      "unitCost": 0,
      "priceCode": "Sales Order",
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
      "importReference": null,
      "exportReference": null,
      "customAttribute1": null,
      "customAttribute2": null,
      "customAttribute3": null,
      "customFields": [],
      "notes": [],
      "channels": [
        {
          "self": {
            "id": 138592,
            "businessKey": {
              "integration.name": "ECOATM API",
              "ref1": "5003807-16"
            }
          },
          "integration": {
            "id": 1,
            "businessKey": {
              "name": "ECOATM API"
            }
          },
          "schedulerTask": null,
          "ref1": "5003807-16",
          "createdDate": "2025-12-08T12:00:56-06:00",
          "updatedDate": "2025-12-08T12:00:56-06:00",
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
      "createdDate": "2025-12-08T12:00:56-06:00",
      "updatedDate": "2025-12-12T15:03:41-06:00",
      "createdBy": {
        "id": 1425,
        "businessKey": {
          "username": "harish.reddy"
        },
        "href": "https://api.deposco.com/latest/users/1425"
      },
      "updatedBy": {
        "id": 1557,
        "businessKey": {
          "username": "carlos.batista"
        },
        "href": "https://api.deposco.com/latest/users/1557"
      },
      "orderLineStatus": "Complete",
      "shipFromFacility": null,
      "coLine": null,
      "allocatedQuantity": 14,
      "unallocatedQuantity": 0,
      "pickedPackQuantity": 14,
      "shortagePackQuantity": 0,
      "shippedPackQuantity": 14,
      "returnedPackQuantity": 0,
      "preallocated": false,
      "isBackOrdered": null,
      "platinumLineNo": 0,
      "directedToZone": {
        "id": 40,
        "businessKey": {
          "facility.number": "LVL",
          "name": "PWS_AppleCellPhone"
        },
        "href": "https://api.deposco.com/latest/zones/40"
      },
      "allocateFromZone": false,
      "carrierSpecialServices": []
    },
    {
      "self": {
        "id": 202769,
        "businessKey": {
          "orderHeader.number": "5003807",
          "lineNumber": "5003807-15",
          "orderHeader.businessUnit.code": "ECOATM"
        },
        "href": "https://api.deposco.com/latest/orders/salesOrders/5267/orderLines/202769"
      },
      "businessUnit": {
        "id": 73,
        "businessKey": {
          "code": "ECOATM"
        },
        "href": "https://api.deposco.com/latest/companies/73"
      },
      "lineNumber": "5003807-15",
      "customerLineNumber": "15",
      "relatedOrderLine": null,
      "sortOrder": 0,
      "orderHeader": {
        "id": 5267,
        "businessKey": {
          "number": "5003807",
          "businessUnit.code": "ECOATM"
        },
        "href": "https://api.deposco.com/latest/orders/salesOrders/5267"
      },
      "item": {
        "id": 21560,
        "businessKey": {
          "number": "PWS336800731343",
          "businessUnit.code": "ECOATM"
        },
        "href": "https://api.deposco.com/latest/items/21560"
      },
      "pack": {
        "id": 21560,
        "businessKey": {
          "item.businessUnit.code": "ECOATM",
          "quantity": 1,
          "item.number": "PWS336800731343"
        },
        "href": "https://api.deposco.com/latest/items/21560/packs/21560"
      },
      "description": null,
      "orderPackQuantity": 2,
      "canceledPackQuantity": 0,
      "lotNumber": null,
      "bornOnDate": null,
      "expirationDate": null,
      "productCode": null,
      "inventoryCondition": null,
      "inventoryAttribute1": null,
      "inventoryAttribute2": null,
      "plannedShipDate": null,
      "plannedArrivalDate": null,
      "unitPrice": 575,
      "unitCost": 0,
      "priceCode": "Sales Order",
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
      "importReference": null,
      "exportReference": null,
      "customAttribute1": null,
      "customAttribute2": null,
      "customAttribute3": null,
      "customFields": [],
      "notes": [],
      "channels": [
        {
          "self": {
            "id": 138591,
            "businessKey": {
              "integration.name": "ECOATM API",
              "ref1": "5003807-15"
            }
          },
          "integration": {
            "id": 1,
            "businessKey": {
              "name": "ECOATM API"
            }
          },
          "schedulerTask": null,
          "ref1": "5003807-15",
          "createdDate": "2025-12-08T12:00:56-06:00",
          "updatedDate": "2025-12-08T12:00:56-06:00",
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
      "createdDate": "2025-12-08T12:00:56-06:00",
      "updatedDate": "2025-12-12T15:03:41-06:00",
      "createdBy": {
        "id": 1425,
        "businessKey": {
          "username": "harish.reddy"
        },
        "href": "https://api.deposco.com/latest/users/1425"
      },
      "updatedBy": {
        "id": 1557,
        "businessKey": {
          "username": "carlos.batista"
        },
        "href": "https://api.deposco.com/latest/users/1557"
      },
      "orderLineStatus": "Complete",
      "shipFromFacility": null,
      "coLine": null,
      "allocatedQuantity": 2,
      "unallocatedQuantity": 0,
      "pickedPackQuantity": 2,
      "shortagePackQuantity": 0,
      "shippedPackQuantity": 2,
      "returnedPackQuantity": 0,
      "preallocated": false,
      "isBackOrdered": null,
      "platinumLineNo": 0,
      "directedToZone": {
        "id": 40,
        "businessKey": {
          "facility.number": "LVL",
          "name": "PWS_AppleCellPhone"
        },
        "href": "https://api.deposco.com/latest/zones/40"
      },
      "allocateFromZone": false,
      "carrierSpecialServices": []
    },
    {
      "self": {
        "id": 202768,
        "businessKey": {
          "orderHeader.number": "5003807",
          "lineNumber": "5003807-14",
          "orderHeader.businessUnit.code": "ECOATM"
        },
        "href": "https://api.deposco.com/latest/orders/salesOrders/5267/orderLines/202768"
      },
      "businessUnit": {
        "id": 73,
        "businessKey": {
          "code": "ECOATM"
        },
        "href": "https://api.deposco.com/latest/companies/73"
      },
      "lineNumber": "5003807-14",
      "customerLineNumber": "14",
      "relatedOrderLine": null,
      "sortOrder": 0,
      "orderHeader": {
        "id": 5267,
        "businessKey": {
          "number": "5003807",
          "businessUnit.code": "ECOATM"
        },
        "href": "https://api.deposco.com/latest/orders/salesOrders/5267"
      },
      "item": {
        "id": 22409,
        "businessKey": {
          "number": "PWS462354029198",
          "businessUnit.code": "ECOATM"
        },
        "href": "https://api.deposco.com/latest/items/22409"
      },
      "pack": {
        "id": 22409,
        "businessKey": {
          "item.businessUnit.code": "ECOATM",
          "quantity": 1,
          "item.number": "PWS462354029198"
        },
        "href": "https://api.deposco.com/latest/items/22409/packs/22409"
      },
      "description": null,
      "orderPackQuantity": 15,
      "canceledPackQuantity": 0,
      "lotNumber": null,
      "bornOnDate": null,
      "expirationDate": null,
      "productCode": null,
      "inventoryCondition": null,
      "inventoryAttribute1": null,
      "inventoryAttribute2": null,
      "plannedShipDate": null,
      "plannedArrivalDate": null,
      "unitPrice": 340,
      "unitCost": 0,
      "priceCode": "Sales Order",
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
      "importReference": null,
      "exportReference": null,
      "customAttribute1": null,
      "customAttribute2": null,
      "customAttribute3": null,
      "customFields": [],
      "notes": [],
      "channels": [
        {
          "self": {
            "id": 138590,
            "businessKey": {
              "integration.name": "ECOATM API",
              "ref1": "5003807-14"
            }
          },
          "integration": {
            "id": 1,
            "businessKey": {
              "name": "ECOATM API"
            }
          },
          "schedulerTask": null,
          "ref1": "5003807-14",
          "createdDate": "2025-12-08T12:00:56-06:00",
          "updatedDate": "2025-12-08T12:00:56-06:00",
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
      "createdDate": "2025-12-08T12:00:56-06:00",
      "updatedDate": "2025-12-12T15:03:43-06:00",
      "createdBy": {
        "id": 1425,
        "businessKey": {
          "username": "harish.reddy"
        },
        "href": "https://api.deposco.com/latest/users/1425"
      },
      "updatedBy": {
        "id": 1557,
        "businessKey": {
          "username": "carlos.batista"
        },
        "href": "https://api.deposco.com/latest/users/1557"
      },
      "orderLineStatus": "Complete",
      "shipFromFacility": null,
      "coLine": null,
      "allocatedQuantity": 15,
      "unallocatedQuantity": 0,
      "pickedPackQuantity": 15,
      "shortagePackQuantity": 0,
      "shippedPackQuantity": 15,
      "returnedPackQuantity": 0,
      "preallocated": false,
      "isBackOrdered": null,
      "platinumLineNo": 0,
      "directedToZone": {
        "id": 40,
        "businessKey": {
          "facility.number": "LVL",
          "name": "PWS_AppleCellPhone"
        },
        "href": "https://api.deposco.com/latest/zones/40"
      },
      "allocateFromZone": false,
      "carrierSpecialServices": []
    },
    {
      "self": {
        "id": 202767,
        "businessKey": {
          "orderHeader.number": "5003807",
          "lineNumber": "5003807-13",
          "orderHeader.businessUnit.code": "ECOATM"
        },
        "href": "https://api.deposco.com/latest/orders/salesOrders/5267/orderLines/202767"
      },
      "businessUnit": {
        "id": 73,
        "businessKey": {
          "code": "ECOATM"
        },
        "href": "https://api.deposco.com/latest/companies/73"
      },
      "lineNumber": "5003807-13",
      "customerLineNumber": "13",
      "relatedOrderLine": null,
      "sortOrder": 0,
      "orderHeader": {
        "id": 5267,
        "businessKey": {
          "number": "5003807",
          "businessUnit.code": "ECOATM"
        },
        "href": "https://api.deposco.com/latest/orders/salesOrders/5267"
      },
      "item": {
        "id": 15891,
        "businessKey": {
          "number": "PWS10009400",
          "businessUnit.code": "ECOATM"
        },
        "href": "https://api.deposco.com/latest/items/15891"
      },
      "pack": {
        "id": 15891,
        "businessKey": {
          "item.businessUnit.code": "ECOATM",
          "quantity": 1,
          "item.number": "PWS10009400"
        },
        "href": "https://api.deposco.com/latest/items/15891/packs/15891"
      },
      "description": null,
      "orderPackQuantity": 1,
      "canceledPackQuantity": 0,
      "lotNumber": null,
      "bornOnDate": null,
      "expirationDate": null,
      "productCode": null,
      "inventoryCondition": null,
      "inventoryAttribute1": null,
      "inventoryAttribute2": null,
      "plannedShipDate": null,
      "plannedArrivalDate": null,
      "unitPrice": 485,
      "unitCost": 0,
      "priceCode": "Sales Order",
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
      "importReference": null,
      "exportReference": null,
      "customAttribute1": null,
      "customAttribute2": null,
      "customAttribute3": null,
      "customFields": [],
      "notes": [],
      "channels": [
        {
          "self": {
            "id": 138589,
            "businessKey": {
              "integration.name": "ECOATM API",
              "ref1": "5003807-13"
            }
          },
          "integration": {
            "id": 1,
            "businessKey": {
              "name": "ECOATM API"
            }
          },
          "schedulerTask": null,
          "ref1": "5003807-13",
          "createdDate": "2025-12-08T12:00:56-06:00",
          "updatedDate": "2025-12-08T12:00:56-06:00",
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
      "createdDate": "2025-12-08T12:00:56-06:00",
      "updatedDate": "2025-12-12T15:03:41-06:00",
      "createdBy": {
        "id": 1425,
        "businessKey": {
          "username": "harish.reddy"
        },
        "href": "https://api.deposco.com/latest/users/1425"
      },
      "updatedBy": {
        "id": 1557,
        "businessKey": {
          "username": "carlos.batista"
        },
        "href": "https://api.deposco.com/latest/users/1557"
      },
      "orderLineStatus": "Complete",
      "shipFromFacility": null,
      "coLine": null,
      "allocatedQuantity": 1,
      "unallocatedQuantity": 0,
      "pickedPackQuantity": 1,
      "shortagePackQuantity": 0,
      "shippedPackQuantity": 1,
      "returnedPackQuantity": 0,
      "preallocated": false,
      "isBackOrdered": null,
      "platinumLineNo": 0,
      "directedToZone": {
        "id": 40,
        "businessKey": {
          "facility.number": "LVL",
          "name": "PWS_AppleCellPhone"
        },
        "href": "https://api.deposco.com/latest/zones/40"
      },
      "allocateFromZone": false,
      "carrierSpecialServices": []
    },
    {
      "self": {
        "id": 202766,
        "businessKey": {
          "orderHeader.number": "5003807",
          "lineNumber": "5003807-12",
          "orderHeader.businessUnit.code": "ECOATM"
        },
        "href": "https://api.deposco.com/latest/orders/salesOrders/5267/orderLines/202766"
      },
      "businessUnit": {
        "id": 73,
        "businessKey": {
          "code": "ECOATM"
        },
        "href": "https://api.deposco.com/latest/companies/73"
      },
      "lineNumber": "5003807-12",
      "customerLineNumber": "12",
      "relatedOrderLine": null,
      "sortOrder": 0,
      "orderHeader": {
        "id": 5267,
        "businessKey": {
          "number": "5003807",
          "businessUnit.code": "ECOATM"
        },
        "href": "https://api.deposco.com/latest/orders/salesOrders/5267"
      },
      "item": {
        "id": 22315,
        "businessKey": {
          "number": "PWS445683456697",
          "businessUnit.code": "ECOATM"
        },
        "href": "https://api.deposco.com/latest/items/22315"
      },
      "pack": {
        "id": 22315,
        "businessKey": {
          "item.businessUnit.code": "ECOATM",
          "quantity": 1,
          "item.number": "PWS445683456697"
        },
        "href": "https://api.deposco.com/latest/items/22315/packs/22315"
      },
      "description": null,
      "orderPackQuantity": 2,
      "canceledPackQuantity": 0,
      "lotNumber": null,
      "bornOnDate": null,
      "expirationDate": null,
      "productCode": null,
      "inventoryCondition": null,
      "inventoryAttribute1": null,
      "inventoryAttribute2": null,
      "plannedShipDate": null,
      "plannedArrivalDate": null,
      "unitPrice": 350,
      "unitCost": 0,
      "priceCode": "Sales Order",
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
      "importReference": null,
      "exportReference": null,
      "customAttribute1": null,
      "customAttribute2": null,
      "customAttribute3": null,
      "customFields": [],
      "notes": [],
      "channels": [
        {
          "self": {
            "id": 138588,
            "businessKey": {
              "integration.name": "ECOATM API",
              "ref1": "5003807-12"
            }
          },
          "integration": {
            "id": 1,
            "businessKey": {
              "name": "ECOATM API"
            }
          },
          "schedulerTask": null,
          "ref1": "5003807-12",
          "createdDate": "2025-12-08T12:00:56-06:00",
          "updatedDate": "2025-12-08T12:00:56-06:00",
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
      "createdDate": "2025-12-08T12:00:56-06:00",
      "updatedDate": "2025-12-12T15:03:42-06:00",
      "createdBy": {
        "id": 1425,
        "businessKey": {
          "username": "harish.reddy"
        },
        "href": "https://api.deposco.com/latest/users/1425"
      },
      "updatedBy": {
        "id": 1557,
        "businessKey": {
          "username": "carlos.batista"
        },
        "href": "https://api.deposco.com/latest/users/1557"
      },
      "orderLineStatus": "Complete",
      "shipFromFacility": null,
      "coLine": null,
      "allocatedQuantity": 2,
      "unallocatedQuantity": 0,
      "pickedPackQuantity": 2,
      "shortagePackQuantity": 0,
      "shippedPackQuantity": 2,
      "returnedPackQuantity": 0,
      "preallocated": false,
      "isBackOrdered": null,
      "platinumLineNo": 0,
      "directedToZone": {
        "id": 40,
        "businessKey": {
          "facility.number": "LVL",
          "name": "PWS_AppleCellPhone"
        },
        "href": "https://api.deposco.com/latest/zones/40"
      },
      "allocateFromZone": false,
      "carrierSpecialServices": []
    },
    {
      "self": {
        "id": 202765,
        "businessKey": {
          "orderHeader.number": "5003807",
          "lineNumber": "5003807-11",
          "orderHeader.businessUnit.code": "ECOATM"
        },
        "href": "https://api.deposco.com/latest/orders/salesOrders/5267/orderLines/202765"
      },
      "businessUnit": {
        "id": 73,
        "businessKey": {
          "code": "ECOATM"
        },
        "href": "https://api.deposco.com/latest/companies/73"
      },
      "lineNumber": "5003807-11",
      "customerLineNumber": "11",
      "relatedOrderLine": null,
      "sortOrder": 0,
      "orderHeader": {
        "id": 5267,
        "businessKey": {
          "number": "5003807",
          "businessUnit.code": "ECOATM"
        },
        "href": "https://api.deposco.com/latest/orders/salesOrders/5267"
      },
      "item": {
        "id": 23425,
        "businessKey": {
          "number": "PWS607691094991",
          "businessUnit.code": "ECOATM"
        },
        "href": "https://api.deposco.com/latest/items/23425"
      },
      "pack": {
        "id": 23425,
        "businessKey": {
          "item.businessUnit.code": "ECOATM",
          "quantity": 1,
          "item.number": "PWS607691094991"
        },
        "href": "https://api.deposco.com/latest/items/23425/packs/23425"
      },
      "description": null,
      "orderPackQuantity": 2,
      "canceledPackQuantity": 0,
      "lotNumber": null,
      "bornOnDate": null,
      "expirationDate": null,
      "productCode": null,
      "inventoryCondition": null,
      "inventoryAttribute1": null,
      "inventoryAttribute2": null,
      "plannedShipDate": null,
      "plannedArrivalDate": null,
      "unitPrice": 360,
      "unitCost": 0,
      "priceCode": "Sales Order",
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
      "importReference": null,
      "exportReference": null,
      "customAttribute1": null,
      "customAttribute2": null,
      "customAttribute3": null,
      "customFields": [],
      "notes": [],
      "channels": [
        {
          "self": {
            "id": 138587,
            "businessKey": {
              "integration.name": "ECOATM API",
              "ref1": "5003807-11"
            }
          },
          "integration": {
            "id": 1,
            "businessKey": {
              "name": "ECOATM API"
            }
          },
          "schedulerTask": null,
          "ref1": "5003807-11",
          "createdDate": "2025-12-08T12:00:56-06:00",
          "updatedDate": "2025-12-08T12:00:56-06:00",
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
      "createdDate": "2025-12-08T12:00:56-06:00",
      "updatedDate": "2025-12-12T15:03:42-06:00",
      "createdBy": {
        "id": 1425,
        "businessKey": {
          "username": "harish.reddy"
        },
        "href": "https://api.deposco.com/latest/users/1425"
      },
      "updatedBy": {
        "id": 1557,
        "businessKey": {
          "username": "carlos.batista"
        },
        "href": "https://api.deposco.com/latest/users/1557"
      },
      "orderLineStatus": "Complete",
      "shipFromFacility": null,
      "coLine": null,
      "allocatedQuantity": 2,
      "unallocatedQuantity": 0,
      "pickedPackQuantity": 2,
      "shortagePackQuantity": 0,
      "shippedPackQuantity": 2,
      "returnedPackQuantity": 0,
      "preallocated": false,
      "isBackOrdered": null,
      "platinumLineNo": 0,
      "directedToZone": {
        "id": 40,
        "businessKey": {
          "facility.number": "LVL",
          "name": "PWS_AppleCellPhone"
        },
        "href": "https://api.deposco.com/latest/zones/40"
      },
      "allocateFromZone": false,
      "carrierSpecialServices": []
    },
    {
      "self": {
        "id": 202764,
        "businessKey": {
          "orderHeader.number": "5003807",
          "lineNumber": "5003807-10",
          "orderHeader.businessUnit.code": "ECOATM"
        },
        "href": "https://api.deposco.com/latest/orders/salesOrders/5267/orderLines/202764"
      },
      "businessUnit": {
        "id": 73,
        "businessKey": {
          "code": "ECOATM"
        },
        "href": "https://api.deposco.com/latest/companies/73"
      },
      "lineNumber": "5003807-10",
      "customerLineNumber": "10",
      "relatedOrderLine": null,
      "sortOrder": 0,
      "orderHeader": {
        "id": 5267,
        "businessKey": {
          "number": "5003807",
          "businessUnit.code": "ECOATM"
        },
        "href": "https://api.deposco.com/latest/orders/salesOrders/5267"
      },
      "item": {
        "id": 6568,
        "businessKey": {
          "number": "PWS078130102942",
          "businessUnit.code": "ECOATM"
        },
        "href": "https://api.deposco.com/latest/items/6568"
      },
      "pack": {
        "id": 6568,
        "businessKey": {
          "item.businessUnit.code": "ECOATM",
          "quantity": 1,
          "item.number": "PWS078130102942"
        },
        "href": "https://api.deposco.com/latest/items/6568/packs/6568"
      },
      "description": null,
      "orderPackQuantity": 4,
      "canceledPackQuantity": 0,
      "lotNumber": null,
      "bornOnDate": null,
      "expirationDate": null,
      "productCode": null,
      "inventoryCondition": null,
      "inventoryAttribute1": null,
      "inventoryAttribute2": null,
      "plannedShipDate": null,
      "plannedArrivalDate": null,
      "unitPrice": 605,
      "unitCost": 0,
      "priceCode": "Sales Order",
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
      "importReference": null,
      "exportReference": null,
      "customAttribute1": null,
      "customAttribute2": null,
      "customAttribute3": null,
      "customFields": [],
      "notes": [],
      "channels": [
        {
          "self": {
            "id": 138586,
            "businessKey": {
              "integration.name": "ECOATM API",
              "ref1": "5003807-10"
            }
          },
          "integration": {
            "id": 1,
            "businessKey": {
              "name": "ECOATM API"
            }
          },
          "schedulerTask": null,
          "ref1": "5003807-10",
          "createdDate": "2025-12-08T12:00:56-06:00",
          "updatedDate": "2025-12-08T12:00:56-06:00",
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
      "createdDate": "2025-12-08T12:00:56-06:00",
      "updatedDate": "2025-12-12T15:03:42-06:00",
      "createdBy": {
        "id": 1425,
        "businessKey": {
          "username": "harish.reddy"
        },
        "href": "https://api.deposco.com/latest/users/1425"
      },
      "updatedBy": {
        "id": 1557,
        "businessKey": {
          "username": "carlos.batista"
        },
        "href": "https://api.deposco.com/latest/users/1557"
      },
      "orderLineStatus": "Complete",
      "shipFromFacility": null,
      "coLine": null,
      "allocatedQuantity": 4,
      "unallocatedQuantity": 0,
      "pickedPackQuantity": 4,
      "shortagePackQuantity": 0,
      "shippedPackQuantity": 4,
      "returnedPackQuantity": 0,
      "preallocated": false,
      "isBackOrdered": null,
      "platinumLineNo": 0,
      "directedToZone": {
        "id": 40,
        "businessKey": {
          "facility.number": "LVL",
          "name": "PWS_AppleCellPhone"
        },
        "href": "https://api.deposco.com/latest/zones/40"
      },
      "allocateFromZone": false,
      "carrierSpecialServices": []
    },
    {
      "self": {
        "id": 202763,
        "businessKey": {
          "orderHeader.number": "5003807",
          "lineNumber": "5003807-9",
          "orderHeader.businessUnit.code": "ECOATM"
        },
        "href": "https://api.deposco.com/latest/orders/salesOrders/5267/orderLines/202763"
      },
      "businessUnit": {
        "id": 73,
        "businessKey": {
          "code": "ECOATM"
        },
        "href": "https://api.deposco.com/latest/companies/73"
      },
      "lineNumber": "5003807-9",
      "customerLineNumber": "9",
      "relatedOrderLine": null,
      "sortOrder": 0,
      "orderHeader": {
        "id": 5267,
        "businessKey": {
          "number": "5003807",
          "businessUnit.code": "ECOATM"
        },
        "href": "https://api.deposco.com/latest/orders/salesOrders/5267"
      },
      "item": {
        "id": 16683,
        "businessKey": {
          "number": "PWS10010192",
          "businessUnit.code": "ECOATM"
        },
        "href": "https://api.deposco.com/latest/items/16683"
      },
      "pack": {
        "id": 16683,
        "businessKey": {
          "item.businessUnit.code": "ECOATM",
          "quantity": 1,
          "item.number": "PWS10010192"
        },
        "href": "https://api.deposco.com/latest/items/16683/packs/16683"
      },
      "description": null,
      "orderPackQuantity": 1,
      "canceledPackQuantity": 0,
      "lotNumber": null,
      "bornOnDate": null,
      "expirationDate": null,
      "productCode": null,
      "inventoryCondition": null,
      "inventoryAttribute1": null,
      "inventoryAttribute2": null,
      "plannedShipDate": null,
      "plannedArrivalDate": null,
      "unitPrice": 740,
      "unitCost": 0,
      "priceCode": "Sales Order",
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
      "importReference": null,
      "exportReference": null,
      "customAttribute1": null,
      "customAttribute2": null,
      "customAttribute3": null,
      "customFields": [],
      "notes": [],
      "channels": [
        {
          "self": {
            "id": 138585,
            "businessKey": {
              "integration.name": "ECOATM API",
              "ref1": "5003807-9"
            }
          },
          "integration": {
            "id": 1,
            "businessKey": {
              "name": "ECOATM API"
            }
          },
          "schedulerTask": null,
          "ref1": "5003807-9",
          "createdDate": "2025-12-08T12:00:56-06:00",
          "updatedDate": "2025-12-08T12:00:56-06:00",
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
      "createdDate": "2025-12-08T12:00:56-06:00",
      "updatedDate": "2025-12-12T15:03:42-06:00",
      "createdBy": {
        "id": 1425,
        "businessKey": {
          "username": "harish.reddy"
        },
        "href": "https://api.deposco.com/latest/users/1425"
      },
      "updatedBy": {
        "id": 1557,
        "businessKey": {
          "username": "carlos.batista"
        },
        "href": "https://api.deposco.com/latest/users/1557"
      },
      "orderLineStatus": "Complete",
      "shipFromFacility": null,
      "coLine": null,
      "allocatedQuantity": 1,
      "unallocatedQuantity": 0,
      "pickedPackQuantity": 1,
      "shortagePackQuantity": 0,
      "shippedPackQuantity": 1,
      "returnedPackQuantity": 0,
      "preallocated": false,
      "isBackOrdered": null,
      "platinumLineNo": 0,
      "directedToZone": {
        "id": 40,
        "businessKey": {
          "facility.number": "LVL",
          "name": "PWS_AppleCellPhone"
        },
        "href": "https://api.deposco.com/latest/zones/40"
      },
      "allocateFromZone": false,
      "carrierSpecialServices": []
    },
    {
      "self": {
        "id": 202762,
        "businessKey": {
          "orderHeader.number": "5003807",
          "lineNumber": "5003807-8",
          "orderHeader.businessUnit.code": "ECOATM"
        },
        "href": "https://api.deposco.com/latest/orders/salesOrders/5267/orderLines/202762"
      },
      "businessUnit": {
        "id": 73,
        "businessKey": {
          "code": "ECOATM"
        },
        "href": "https://api.deposco.com/latest/companies/73"
      },
      "lineNumber": "5003807-8",
      "customerLineNumber": "8",
      "relatedOrderLine": null,
      "sortOrder": 0,
      "orderHeader": {
        "id": 5267,
        "businessKey": {
          "number": "5003807",
          "businessUnit.code": "ECOATM"
        },
        "href": "https://api.deposco.com/latest/orders/salesOrders/5267"
      },
      "item": {
        "id": 15895,
        "businessKey": {
          "number": "PWS10009404",
          "businessUnit.code": "ECOATM"
        },
        "href": "https://api.deposco.com/latest/items/15895"
      },
      "pack": {
        "id": 15895,
        "businessKey": {
          "item.businessUnit.code": "ECOATM",
          "quantity": 1,
          "item.number": "PWS10009404"
        },
        "href": "https://api.deposco.com/latest/items/15895/packs/15895"
      },
      "description": null,
      "orderPackQuantity": 1,
      "canceledPackQuantity": 0,
      "lotNumber": null,
      "bornOnDate": null,
      "expirationDate": null,
      "productCode": null,
      "inventoryCondition": null,
      "inventoryAttribute1": null,
      "inventoryAttribute2": null,
      "plannedShipDate": null,
      "plannedArrivalDate": null,
      "unitPrice": 485,
      "unitCost": 0,
      "priceCode": "Sales Order",
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
      "importReference": null,
      "exportReference": null,
      "customAttribute1": null,
      "customAttribute2": null,
      "customAttribute3": null,
      "customFields": [],
      "notes": [],
      "channels": [
        {
          "self": {
            "id": 138584,
            "businessKey": {
              "integration.name": "ECOATM API",
              "ref1": "5003807-8"
            }
          },
          "integration": {
            "id": 1,
            "businessKey": {
              "name": "ECOATM API"
            }
          },
          "schedulerTask": null,
          "ref1": "5003807-8",
          "createdDate": "2025-12-08T12:00:56-06:00",
          "updatedDate": "2025-12-08T12:00:56-06:00",
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
      "createdDate": "2025-12-08T12:00:56-06:00",
      "updatedDate": "2025-12-12T15:03:42-06:00",
      "createdBy": {
        "id": 1425,
        "businessKey": {
          "username": "harish.reddy"
        },
        "href": "https://api.deposco.com/latest/users/1425"
      },
      "updatedBy": {
        "id": 1557,
        "businessKey": {
          "username": "carlos.batista"
        },
        "href": "https://api.deposco.com/latest/users/1557"
      },
      "orderLineStatus": "Complete",
      "shipFromFacility": null,
      "coLine": null,
      "allocatedQuantity": 1,
      "unallocatedQuantity": 0,
      "pickedPackQuantity": 1,
      "shortagePackQuantity": 0,
      "shippedPackQuantity": 1,
      "returnedPackQuantity": 0,
      "preallocated": false,
      "isBackOrdered": null,
      "platinumLineNo": 0,
      "directedToZone": {
        "id": 40,
        "businessKey": {
          "facility.number": "LVL",
          "name": "PWS_AppleCellPhone"
        },
        "href": "https://api.deposco.com/latest/zones/40"
      },
      "allocateFromZone": false,
      "carrierSpecialServices": []
    },
    {
      "self": {
        "id": 202761,
        "businessKey": {
          "orderHeader.number": "5003807",
          "lineNumber": "5003807-7",
          "orderHeader.businessUnit.code": "ECOATM"
        },
        "href": "https://api.deposco.com/latest/orders/salesOrders/5267/orderLines/202761"
      },
      "businessUnit": {
        "id": 73,
        "businessKey": {
          "code": "ECOATM"
        },
        "href": "https://api.deposco.com/latest/companies/73"
      },
      "lineNumber": "5003807-7",
      "customerLineNumber": "7",
      "relatedOrderLine": null,
      "sortOrder": 0,
      "orderHeader": {
        "id": 5267,
        "businessKey": {
          "number": "5003807",
          "businessUnit.code": "ECOATM"
        },
        "href": "https://api.deposco.com/latest/orders/salesOrders/5267"
      },
      "item": {
        "id": 16131,
        "businessKey": {
          "number": "PWS10009640",
          "businessUnit.code": "ECOATM"
        },
        "href": "https://api.deposco.com/latest/items/16131"
      },
      "pack": {
        "id": 16131,
        "businessKey": {
          "item.businessUnit.code": "ECOATM",
          "quantity": 1,
          "item.number": "PWS10009640"
        },
        "href": "https://api.deposco.com/latest/items/16131/packs/16131"
      },
      "description": null,
      "orderPackQuantity": 2,
      "canceledPackQuantity": 0,
      "lotNumber": null,
      "bornOnDate": null,
      "expirationDate": null,
      "productCode": null,
      "inventoryCondition": null,
      "inventoryAttribute1": null,
      "inventoryAttribute2": null,
      "plannedShipDate": null,
      "plannedArrivalDate": null,
      "unitPrice": 520,
      "unitCost": 0,
      "priceCode": "Sales Order",
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
      "importReference": null,
      "exportReference": null,
      "customAttribute1": null,
      "customAttribute2": null,
      "customAttribute3": null,
      "customFields": [],
      "notes": [],
      "channels": [
        {
          "self": {
            "id": 138583,
            "businessKey": {
              "integration.name": "ECOATM API",
              "ref1": "5003807-7"
            }
          },
          "integration": {
            "id": 1,
            "businessKey": {
              "name": "ECOATM API"
            }
          },
          "schedulerTask": null,
          "ref1": "5003807-7",
          "createdDate": "2025-12-08T12:00:56-06:00",
          "updatedDate": "2025-12-08T12:00:56-06:00",
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
      "createdDate": "2025-12-08T12:00:56-06:00",
      "updatedDate": "2025-12-12T15:03:43-06:00",
      "createdBy": {
        "id": 1425,
        "businessKey": {
          "username": "harish.reddy"
        },
        "href": "https://api.deposco.com/latest/users/1425"
      },
      "updatedBy": {
        "id": 1557,
        "businessKey": {
          "username": "carlos.batista"
        },
        "href": "https://api.deposco.com/latest/users/1557"
      },
      "orderLineStatus": "Complete",
      "shipFromFacility": null,
      "coLine": null,
      "allocatedQuantity": 2,
      "unallocatedQuantity": 0,
      "pickedPackQuantity": 2,
      "shortagePackQuantity": 0,
      "shippedPackQuantity": 2,
      "returnedPackQuantity": 0,
      "preallocated": false,
      "isBackOrdered": null,
      "platinumLineNo": 0,
      "directedToZone": {
        "id": 40,
        "businessKey": {
          "facility.number": "LVL",
          "name": "PWS_AppleCellPhone"
        },
        "href": "https://api.deposco.com/latest/zones/40"
      },
      "allocateFromZone": false,
      "carrierSpecialServices": []
    },
    {
      "self": {
        "id": 202760,
        "businessKey": {
          "orderHeader.number": "5003807",
          "lineNumber": "5003807-6",
          "orderHeader.businessUnit.code": "ECOATM"
        },
        "href": "https://api.deposco.com/latest/orders/salesOrders/5267/orderLines/202760"
      },
      "businessUnit": {
        "id": 73,
        "businessKey": {
          "code": "ECOATM"
        },
        "href": "https://api.deposco.com/latest/companies/73"
      },
      "lineNumber": "5003807-6",
      "customerLineNumber": "6",
      "relatedOrderLine": null,
      "sortOrder": 0,
      "orderHeader": {
        "id": 5267,
        "businessKey": {
          "number": "5003807",
          "businessUnit.code": "ECOATM"
        },
        "href": "https://api.deposco.com/latest/orders/salesOrders/5267"
      },
      "item": {
        "id": 20376,
        "businessKey": {
          "number": "PWS166651379018",
          "businessUnit.code": "ECOATM"
        },
        "href": "https://api.deposco.com/latest/items/20376"
      },
      "pack": {
        "id": 20376,
        "businessKey": {
          "item.businessUnit.code": "ECOATM",
          "quantity": 1,
          "item.number": "PWS166651379018"
        },
        "href": "https://api.deposco.com/latest/items/20376/packs/20376"
      },
      "description": null,
      "orderPackQuantity": 3,
      "canceledPackQuantity": 0,
      "lotNumber": null,
      "bornOnDate": null,
      "expirationDate": null,
      "productCode": null,
      "inventoryCondition": null,
      "inventoryAttribute1": null,
      "inventoryAttribute2": null,
      "plannedShipDate": null,
      "plannedArrivalDate": null,
      "unitPrice": 200,
      "unitCost": 0,
      "priceCode": "Sales Order",
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
      "importReference": null,
      "exportReference": null,
      "customAttribute1": null,
      "customAttribute2": null,
      "customAttribute3": null,
      "customFields": [],
      "notes": [],
      "channels": [
        {
          "self": {
            "id": 138582,
            "businessKey": {
              "integration.name": "ECOATM API",
              "ref1": "5003807-6"
            }
          },
          "integration": {
            "id": 1,
            "businessKey": {
              "name": "ECOATM API"
            }
          },
          "schedulerTask": null,
          "ref1": "5003807-6",
          "createdDate": "2025-12-08T12:00:56-06:00",
          "updatedDate": "2025-12-08T12:00:56-06:00",
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
      "createdDate": "2025-12-08T12:00:56-06:00",
      "updatedDate": "2025-12-12T15:03:42-06:00",
      "createdBy": {
        "id": 1425,
        "businessKey": {
          "username": "harish.reddy"
        },
        "href": "https://api.deposco.com/latest/users/1425"
      },
      "updatedBy": {
        "id": 1557,
        "businessKey": {
          "username": "carlos.batista"
        },
        "href": "https://api.deposco.com/latest/users/1557"
      },
      "orderLineStatus": "Complete",
      "shipFromFacility": null,
      "coLine": null,
      "allocatedQuantity": 3,
      "unallocatedQuantity": 0,
      "pickedPackQuantity": 3,
      "shortagePackQuantity": 0,
      "shippedPackQuantity": 3,
      "returnedPackQuantity": 0,
      "preallocated": false,
      "isBackOrdered": null,
      "platinumLineNo": 0,
      "directedToZone": {
        "id": 40,
        "businessKey": {
          "facility.number": "LVL",
          "name": "PWS_AppleCellPhone"
        },
        "href": "https://api.deposco.com/latest/zones/40"
      },
      "allocateFromZone": false,
      "carrierSpecialServices": []
    },
    {
      "self": {
        "id": 202759,
        "businessKey": {
          "orderHeader.number": "5003807",
          "lineNumber": "5003807-5",
          "orderHeader.businessUnit.code": "ECOATM"
        },
        "href": "https://api.deposco.com/latest/orders/salesOrders/5267/orderLines/202759"
      },
      "businessUnit": {
        "id": 73,
        "businessKey": {
          "code": "ECOATM"
        },
        "href": "https://api.deposco.com/latest/companies/73"
      },
      "lineNumber": "5003807-5",
      "customerLineNumber": "5",
      "relatedOrderLine": null,
      "sortOrder": 0,
      "orderHeader": {
        "id": 5267,
        "businessKey": {
          "number": "5003807",
          "businessUnit.code": "ECOATM"
        },
        "href": "https://api.deposco.com/latest/orders/salesOrders/5267"
      },
      "item": {
        "id": 25878,
        "businessKey": {
          "number": "PWS953906146123",
          "businessUnit.code": "ECOATM"
        },
        "href": "https://api.deposco.com/latest/items/25878"
      },
      "pack": {
        "id": 25878,
        "businessKey": {
          "item.businessUnit.code": "ECOATM",
          "quantity": 1,
          "item.number": "PWS953906146123"
        },
        "href": "https://api.deposco.com/latest/items/25878/packs/25878"
      },
      "description": null,
      "orderPackQuantity": 9,
      "canceledPackQuantity": 0,
      "lotNumber": null,
      "bornOnDate": null,
      "expirationDate": null,
      "productCode": null,
      "inventoryCondition": null,
      "inventoryAttribute1": null,
      "inventoryAttribute2": null,
      "plannedShipDate": null,
      "plannedArrivalDate": null,
      "unitPrice": 365,
      "unitCost": 0,
      "priceCode": "Sales Order",
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
      "importReference": null,
      "exportReference": null,
      "customAttribute1": null,
      "customAttribute2": null,
      "customAttribute3": null,
      "customFields": [],
      "notes": [],
      "channels": [
        {
          "self": {
            "id": 138581,
            "businessKey": {
              "integration.name": "ECOATM API",
              "ref1": "5003807-5"
            }
          },
          "integration": {
            "id": 1,
            "businessKey": {
              "name": "ECOATM API"
            }
          },
          "schedulerTask": null,
          "ref1": "5003807-5",
          "createdDate": "2025-12-08T12:00:56-06:00",
          "updatedDate": "2025-12-08T12:00:56-06:00",
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
      "createdDate": "2025-12-08T12:00:56-06:00",
      "updatedDate": "2025-12-12T15:03:41-06:00",
      "createdBy": {
        "id": 1425,
        "businessKey": {
          "username": "harish.reddy"
        },
        "href": "https://api.deposco.com/latest/users/1425"
      },
      "updatedBy": {
        "id": 1557,
        "businessKey": {
          "username": "carlos.batista"
        },
        "href": "https://api.deposco.com/latest/users/1557"
      },
      "orderLineStatus": "Complete",
      "shipFromFacility": null,
      "coLine": null,
      "allocatedQuantity": 9,
      "unallocatedQuantity": 0,
      "pickedPackQuantity": 9,
      "shortagePackQuantity": 0,
      "shippedPackQuantity": 9,
      "returnedPackQuantity": 0,
      "preallocated": false,
      "isBackOrdered": null,
      "platinumLineNo": 0,
      "directedToZone": {
        "id": 40,
        "businessKey": {
          "facility.number": "LVL",
          "name": "PWS_AppleCellPhone"
        },
        "href": "https://api.deposco.com/latest/zones/40"
      },
      "allocateFromZone": false,
      "carrierSpecialServices": []
    },
    {
      "self": {
        "id": 202758,
        "businessKey": {
          "orderHeader.number": "5003807",
          "lineNumber": "5003807-4",
          "orderHeader.businessUnit.code": "ECOATM"
        },
        "href": "https://api.deposco.com/latest/orders/salesOrders/5267/orderLines/202758"
      },
      "businessUnit": {
        "id": 73,
        "businessKey": {
          "code": "ECOATM"
        },
        "href": "https://api.deposco.com/latest/companies/73"
      },
      "lineNumber": "5003807-4",
      "customerLineNumber": "4",
      "relatedOrderLine": null,
      "sortOrder": 0,
      "orderHeader": {
        "id": 5267,
        "businessKey": {
          "number": "5003807",
          "businessUnit.code": "ECOATM"
        },
        "href": "https://api.deposco.com/latest/orders/salesOrders/5267"
      },
      "item": {
        "id": 22660,
        "businessKey": {
          "number": "PWS498595699190",
          "businessUnit.code": "ECOATM"
        },
        "href": "https://api.deposco.com/latest/items/22660"
      },
      "pack": {
        "id": 22660,
        "businessKey": {
          "item.businessUnit.code": "ECOATM",
          "quantity": 1,
          "item.number": "PWS498595699190"
        },
        "href": "https://api.deposco.com/latest/items/22660/packs/22660"
      },
      "description": null,
      "orderPackQuantity": 9,
      "canceledPackQuantity": 0,
      "lotNumber": null,
      "bornOnDate": null,
      "expirationDate": null,
      "productCode": null,
      "inventoryCondition": null,
      "inventoryAttribute1": null,
      "inventoryAttribute2": null,
      "plannedShipDate": null,
      "plannedArrivalDate": null,
      "unitPrice": 365,
      "unitCost": 0,
      "priceCode": "Sales Order",
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
      "importReference": null,
      "exportReference": null,
      "customAttribute1": null,
      "customAttribute2": null,
      "customAttribute3": null,
      "customFields": [],
      "notes": [],
      "channels": [
        {
          "self": {
            "id": 138580,
            "businessKey": {
              "integration.name": "ECOATM API",
              "ref1": "5003807-4"
            }
          },
          "integration": {
            "id": 1,
            "businessKey": {
              "name": "ECOATM API"
            }
          },
          "schedulerTask": null,
          "ref1": "5003807-4",
          "createdDate": "2025-12-08T12:00:56-06:00",
          "updatedDate": "2025-12-08T12:00:56-06:00",
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
      "createdDate": "2025-12-08T12:00:56-06:00",
      "updatedDate": "2025-12-12T15:03:42-06:00",
      "createdBy": {
        "id": 1425,
        "businessKey": {
          "username": "harish.reddy"
        },
        "href": "https://api.deposco.com/latest/users/1425"
      },
      "updatedBy": {
        "id": 1557,
        "businessKey": {
          "username": "carlos.batista"
        },
        "href": "https://api.deposco.com/latest/users/1557"
      },
      "orderLineStatus": "Complete",
      "shipFromFacility": null,
      "coLine": null,
      "allocatedQuantity": 9,
      "unallocatedQuantity": 0,
      "pickedPackQuantity": 9,
      "shortagePackQuantity": 0,
      "shippedPackQuantity": 9,
      "returnedPackQuantity": 0,
      "preallocated": false,
      "isBackOrdered": null,
      "platinumLineNo": 0,
      "directedToZone": {
        "id": 40,
        "businessKey": {
          "facility.number": "LVL",
          "name": "PWS_AppleCellPhone"
        },
        "href": "https://api.deposco.com/latest/zones/40"
      },
      "allocateFromZone": false,
      "carrierSpecialServices": []
    },
    {
      "self": {
        "id": 202757,
        "businessKey": {
          "orderHeader.number": "5003807",
          "lineNumber": "5003807-3",
          "orderHeader.businessUnit.code": "ECOATM"
        },
        "href": "https://api.deposco.com/latest/orders/salesOrders/5267/orderLines/202757"
      },
      "businessUnit": {
        "id": 73,
        "businessKey": {
          "code": "ECOATM"
        },
        "href": "https://api.deposco.com/latest/companies/73"
      },
      "lineNumber": "5003807-3",
      "customerLineNumber": "3",
      "relatedOrderLine": null,
      "sortOrder": 0,
      "orderHeader": {
        "id": 5267,
        "businessKey": {
          "number": "5003807",
          "businessUnit.code": "ECOATM"
        },
        "href": "https://api.deposco.com/latest/orders/salesOrders/5267"
      },
      "item": {
        "id": 22253,
        "businessKey": {
          "number": "PWS435449975154",
          "businessUnit.code": "ECOATM"
        },
        "href": "https://api.deposco.com/latest/items/22253"
      },
      "pack": {
        "id": 22253,
        "businessKey": {
          "item.businessUnit.code": "ECOATM",
          "quantity": 1,
          "item.number": "PWS435449975154"
        },
        "href": "https://api.deposco.com/latest/items/22253/packs/22253"
      },
      "description": null,
      "orderPackQuantity": 3,
      "canceledPackQuantity": 0,
      "lotNumber": null,
      "bornOnDate": null,
      "expirationDate": null,
      "productCode": null,
      "inventoryCondition": null,
      "inventoryAttribute1": null,
      "inventoryAttribute2": null,
      "plannedShipDate": null,
      "plannedArrivalDate": null,
      "unitPrice": 360,
      "unitCost": 0,
      "priceCode": "Sales Order",
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
      "importReference": null,
      "exportReference": null,
      "customAttribute1": null,
      "customAttribute2": null,
      "customAttribute3": null,
      "customFields": [],
      "notes": [],
      "channels": [
        {
          "self": {
            "id": 138579,
            "businessKey": {
              "integration.name": "ECOATM API",
              "ref1": "5003807-3"
            }
          },
          "integration": {
            "id": 1,
            "businessKey": {
              "name": "ECOATM API"
            }
          },
          "schedulerTask": null,
          "ref1": "5003807-3",
          "createdDate": "2025-12-08T12:00:56-06:00",
          "updatedDate": "2025-12-08T12:00:56-06:00",
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
      "createdDate": "2025-12-08T12:00:56-06:00",
      "updatedDate": "2025-12-12T15:03:42-06:00",
      "createdBy": {
        "id": 1425,
        "businessKey": {
          "username": "harish.reddy"
        },
        "href": "https://api.deposco.com/latest/users/1425"
      },
      "updatedBy": {
        "id": 1557,
        "businessKey": {
          "username": "carlos.batista"
        },
        "href": "https://api.deposco.com/latest/users/1557"
      },
      "orderLineStatus": "Complete",
      "shipFromFacility": null,
      "coLine": null,
      "allocatedQuantity": 3,
      "unallocatedQuantity": 0,
      "pickedPackQuantity": 3,
      "shortagePackQuantity": 0,
      "shippedPackQuantity": 3,
      "returnedPackQuantity": 0,
      "preallocated": false,
      "isBackOrdered": null,
      "platinumLineNo": 0,
      "directedToZone": {
        "id": 40,
        "businessKey": {
          "facility.number": "LVL",
          "name": "PWS_AppleCellPhone"
        },
        "href": "https://api.deposco.com/latest/zones/40"
      },
      "allocateFromZone": false,
      "carrierSpecialServices": []
    },
    {
      "self": {
        "id": 202756,
        "businessKey": {
          "orderHeader.number": "5003807",
          "lineNumber": "5003807-2",
          "orderHeader.businessUnit.code": "ECOATM"
        },
        "href": "https://api.deposco.com/latest/orders/salesOrders/5267/orderLines/202756"
      },
      "businessUnit": {
        "id": 73,
        "businessKey": {
          "code": "ECOATM"
        },
        "href": "https://api.deposco.com/latest/companies/73"
      },
      "lineNumber": "5003807-2",
      "customerLineNumber": "2",
      "relatedOrderLine": null,
      "sortOrder": 0,
      "orderHeader": {
        "id": 5267,
        "businessKey": {
          "number": "5003807",
          "businessUnit.code": "ECOATM"
        },
        "href": "https://api.deposco.com/latest/orders/salesOrders/5267"
      },
      "item": {
        "id": 21003,
        "businessKey": {
          "number": "PWS253774925842",
          "businessUnit.code": "ECOATM"
        },
        "href": "https://api.deposco.com/latest/items/21003"
      },
      "pack": {
        "id": 21003,
        "businessKey": {
          "item.businessUnit.code": "ECOATM",
          "quantity": 1,
          "item.number": "PWS253774925842"
        },
        "href": "https://api.deposco.com/latest/items/21003/packs/21003"
      },
      "description": null,
      "orderPackQuantity": 15,
      "canceledPackQuantity": 0,
      "lotNumber": null,
      "bornOnDate": null,
      "expirationDate": null,
      "productCode": null,
      "inventoryCondition": null,
      "inventoryAttribute1": null,
      "inventoryAttribute2": null,
      "plannedShipDate": null,
      "plannedArrivalDate": null,
      "unitPrice": 175,
      "unitCost": 0,
      "priceCode": "Sales Order",
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
      "importReference": null,
      "exportReference": null,
      "customAttribute1": null,
      "customAttribute2": null,
      "customAttribute3": null,
      "customFields": [],
      "notes": [],
      "channels": [
        {
          "self": {
            "id": 138578,
            "businessKey": {
              "integration.name": "ECOATM API",
              "ref1": "5003807-2"
            }
          },
          "integration": {
            "id": 1,
            "businessKey": {
              "name": "ECOATM API"
            }
          },
          "schedulerTask": null,
          "ref1": "5003807-2",
          "createdDate": "2025-12-08T12:00:56-06:00",
          "updatedDate": "2025-12-08T12:00:56-06:00",
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
      "createdDate": "2025-12-08T12:00:56-06:00",
      "updatedDate": "2025-12-12T15:03:41-06:00",
      "createdBy": {
        "id": 1425,
        "businessKey": {
          "username": "harish.reddy"
        },
        "href": "https://api.deposco.com/latest/users/1425"
      },
      "updatedBy": {
        "id": 1557,
        "businessKey": {
          "username": "carlos.batista"
        },
        "href": "https://api.deposco.com/latest/users/1557"
      },
      "orderLineStatus": "Complete",
      "shipFromFacility": null,
      "coLine": null,
      "allocatedQuantity": 15,
      "unallocatedQuantity": 0,
      "pickedPackQuantity": 15,
      "shortagePackQuantity": 0,
      "shippedPackQuantity": 15,
      "returnedPackQuantity": 0,
      "preallocated": false,
      "isBackOrdered": null,
      "platinumLineNo": 0,
      "directedToZone": {
        "id": 40,
        "businessKey": {
          "facility.number": "LVL",
          "name": "PWS_AppleCellPhone"
        },
        "href": "https://api.deposco.com/latest/zones/40"
      },
      "allocateFromZone": false,
      "carrierSpecialServices": []
    }
  ],
  "links": [
    {
      "rel": "prev",
      "href": "https://api.deposco.com/latest/orders/salesOrders/5267/orderLines?searchId=bWluSWR8Z3R8MjAyNzgwfGlubmVySm9pbiZvcmRlckhlYWRlci5pZHxlcXw1MjY3fGlubmVySm9pbiZvcmRlckhlYWRlci50eXBlfGVxfFNhbGVzIE9yZGVyfGlubmVySm9pbiZwYWdlU2l6ZXxlcXwyNXxpbm5lckpvaW4mdG90YWxQYWdlc3xlcXw0fGlubmVySm9pbiZ0b3RhbFBhZ2VzfGVxfDQ"
    },
    {
      "rel": "next",
      "href": "https://api.deposco.com/latest/orders/salesOrders/5267/orderLines?searchId=bWF4SWR8bHR8MjAyNzU2fGlubmVySm9pbiZvcmRlckhlYWRlci5pZHxlcXw1MjY3fGlubmVySm9pbiZvcmRlckhlYWRlci50eXBlfGVxfFNhbGVzIE9yZGVyfGlubmVySm9pbiZwYWdlU2l6ZXxlcXwyNXxpbm5lckpvaW4mdG90YWxQYWdlc3xlcXw0fGlubmVySm9pbiZ0b3RhbFBhZ2VzfGVxfDQ"
    }
  ],
  "complete": false,
  "pages": 4
}
```
