# JSON Structure: JSON_OrderHistory

## Sample JSON

```json
{
    "data": [
        {
            "self": {
                "id": 5203,
                "businessKey": {
                    "number": "5003767",
                    "businessUnit.code": "ECOATM"
                },
                "href": "https://api.deposco.com/latest/orders/salesOrders/5203"
            },
            "businessUnit": {
                "id": 73,
                "businessKey": {
                    "code": "ECOATM"
                },
                "href": "https://api.deposco.com/latest/companies/73"
            },
            "number": "5003767",
            "orderPriority": 0,
            "orderSummary": null,
            "orderSource": "PWS",
            "secondaryOrderSource": null,
            "customerOrderNumber": "PWS-2235925057",
            "relatedOrderNumber": null,
            "partnerInvoiceNumber": null,
            "consigneePartner": {
                "id": 206,
                "businessKey": {
                    "code": "22359",
                    "businessUnit.code": "ECOATM"
                },
                "href": "https://api.deposco.com/latest/tradingPartners/206"
            },
            "tradingPartnerSite": null,
            "shipToContact": {
                "firstName": "Receiving",
                "lastName": "Manager",
                "phone": "8183399999",
                "email": "hayk@yourcellparts.com"
            },
            "shipToAddress": {
                "line1": "16009 Arminta St.",
                "line3": "",
                "city": "Van Nuys",
                "stateProvince": "CA",
                "postalCode": "91406",
                "country": "USA"
            },
            "billToPartner": null,
            "billToContact": {
                "email": "hayk@yourcellparts.com"
            },
            "billToAccountNumber": null,
            "billToAddress": {
                "line1": "16009 Arminta St.",
                "city": "Van Nuys",
                "stateProvince": "CA",
                "postalCode": "91406",
                "country": "United States"
            },
            "shipFromContact": {},
            "shipFromAddress": {
                "line3": null,
                "line4": null
            },
            "placedDate": "2025-12-03T12:00:38-06:00",
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
            "actualShipDate": "2025-12-04T15:29:37-06:00",
            "priceList": null,
            "orderTotal": 14210.0,
            "orderSubtotal": 0.0,
            "orderDiscountSubtotal": 0.0,
            "orderShippingTotal": 0.0,
            "orderShipTotal": 0.0,
            "taxable": false,
            "orderTaxTotal": 0.0,
            "orderTaxableTotal": 0.0,
            "orderUntaxableTotal": 0.0,
            "shippingTaxTotal": 0.0,
            "shipVia": "Ship Outside System",
            "shipVendor": "Ship Outside System",
            "shipMethod": null,
            "freightTermsType": "Third Party",
            "freightBillToContact": {
                "email": null
            },
            "freightBillToAccount": null,
            "freightBillToAddress": {},
            "weight": {
                "weight": 0.0,
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
            "importType": null,
            "importReference": null,
            "exportReference": "not processed",
            "otherReferenceNumber": null,
            "otherReferenceNumber2": null,
            "integrationSource": null,
            "sendASN": null,
            "dropShip": false,
            "pickWave": {
                "id": 1530,
                "businessKey": {}
            },
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
            "customAttribute1": "Payment Received",
            "customAttribute2": null,
            "customAttribute3": "Parcel",
            "customAttribute4": "true",
            "customAttribute5": "1",
            "customAttribute6": "0",
            "customAttribute7": "true",
            "customFields": [],
            "notes": [
                {
                    "self": {
                        "id": 7307,
                        "businessKey": {
                            "orderLine.lineNumber": null,
                            "orderHeader.number": "5003767",
                            "orderLine.orderHeader.number": null,
                            "title": "Shipping Instructions",
                            "orderHeader.businessUnit.code": "ECOATM",
                            "orderLine.orderHeader.businessUnit.code": null
                        }
                    },
                    "title": "Shipping Instructions",
                    "body": "Eco Buyer Guide",
                    "priority": null,
                    "orderHeader": {
                        "id": 5203,
                        "businessKey": {
                            "number": "5003767",
                            "businessUnit.code": "ECOATM"
                        },
                        "href": "https://api.deposco.com/latest/orders/salesOrders/5203"
                    },
                    "orderLine": null
                }
            ],
            "channels": [
                {
                    "self": {
                        "id": 138034,
                        "businessKey": {
                            "integration.name": "ECOATM API",
                            "ref1": "5003767"
                        }
                    },
                    "integration": {
                        "id": 1,
                        "businessKey": {
                            "name": "ECOATM API"
                        }
                    },
                    "schedulerTask": null,
                    "ref1": "5003767",
                    "createdDate": "2025-12-03T12:00:47-06:00",
                    "updatedDate": "2025-12-03T12:00:47-06:00",
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
            "createdDate": "2025-12-03T12:00:46-06:00",
            "updatedDate": "2025-12-04T15:29:37-06:00",
            "createdBy": {
                "id": 1425,
                "businessKey": {
                    "username": "harish.reddy"
                },
                "href": "https://api.deposco.com/latest/users/1425"
            },
            "updatedBy": {
                "id": 1664,
                "businessKey": {
                    "username": "raul.carmona"
                },
                "href": "https://api.deposco.com/latest/users/1664"
            },
            "type": "Sales Order",
            "orderStatus": "Ship Complete",
            "shipFromFacility": {
                "id": 1,
                "businessKey": {
                    "number": "LVL"
                },
                "href": "https://api.deposco.com/latest/facilities/1"
            },
            "dutyPaidBy": null,
            "dutyPaidByType": null,
            "dutyPaidByContactName": null,
            "dutyPaidByAccount": null,
            "dutyPaidByPostalCode": null,
            "dutyPaidByCountry": null,
            "directedToZone": null,
            "customerOrderHeaders": null,
            "orderLines": {
                "data": [
                    {
                        "self": {
                            "id": 195913,
                            "businessKey": {
                                "orderHeader.number": "5003767",
                                "lineNumber": "5003767-15",
                                "orderHeader.businessUnit.code": "ECOATM"
                            },
                            "href": "https://api.deposco.com/latest/orders/salesOrders/5203/orderLines/195913"
                        },
                        "businessUnit": {
                            "id": 73,
                            "businessKey": {
                                "code": "ECOATM"
                            },
                            "href": "https://api.deposco.com/latest/companies/73"
                        },
                        "lineNumber": "5003767-15",
                        "customerLineNumber": "15",
                        "relatedOrderLine": null,
                        "sortOrder": 0,
                        "orderHeader": {
                            "id": 5203,
                            "businessKey": {
                                "number": "5003767",
                                "businessUnit.code": "ECOATM"
                            },
                            "href": "https://api.deposco.com/latest/orders/salesOrders/5203"
                        },
                        "item": {
                            "id": 20929,
                            "businessKey": {
                                "number": "PWS243490134358",
                                "businessUnit.code": "ECOATM"
                            },
                            "href": "https://api.deposco.com/latest/items/20929"
                        },
                        "pack": {
                            "id": 20929,
                            "businessKey": {
                                "item.businessUnit.code": "ECOATM",
                                "quantity": 1,
                                "item.number": "PWS243490134358"
                            },
                            "href": "https://api.deposco.com/latest/items/20929/packs/20929"
                        },
                        "description": null,
                        "orderPackQuantity": 15.0,
                        "canceledPackQuantity": 0.0,
                        "lotNumber": null,
                        "bornOnDate": null,
                        "expirationDate": null,
                        "productCode": null,
                        "inventoryCondition": null,
                        "inventoryAttribute1": null,
                        "inventoryAttribute2": null,
                        "plannedShipDate": null,
                        "plannedArrivalDate": null,
                        "unitPrice": 365.0,
                        "unitCost": 0.0,
                        "priceCode": "Sales Order",
                        "lineTotal": 0.0,
                        "taxable": false,
                        "taxCost": 0.0,
                        "taxableTotal": 0.0,
                        "untaxableTotal": 0.0,
                        "extendedTotal": 0.0,
                        "discountAmount": 0.0,
                        "shippingAmount": 0.0,
                        "weight": {
                            "weight": 0.0,
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
                                    "id": 138049,
                                    "businessKey": {
                                        "integration.name": "ECOATM API",
                                        "ref1": "5003767-15"
                                    }
                                },
                                "integration": {
                                    "id": 1,
                                    "businessKey": {
                                        "name": "ECOATM API"
                                    }
                                },
                                "schedulerTask": null,
                                "ref1": "5003767-15",
                                "createdDate": "2025-12-03T12:00:47-06:00",
                                "updatedDate": "2025-12-03T12:00:47-06:00",
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
                        "createdDate": "2025-12-03T12:00:47-06:00",
                        "updatedDate": "2025-12-04T15:29:37-06:00",
                        "createdBy": {
                            "id": 1425,
                            "businessKey": {
                                "username": "harish.reddy"
                            },
                            "href": "https://api.deposco.com/latest/users/1425"
                        },
                        "updatedBy": {
                            "id": 1664,
                            "businessKey": {
                                "username": "raul.carmona"
                            },
                            "href": "https://api.deposco.com/latest/users/1664"
                        },
                        "orderLineStatus": "Complete",
                        "shipFromFacility": null,
                        "coLine": null,
                        "allocatedQuantity": 15.0,
                        "unallocatedQuantity": 0.0,
                        "pickedPackQuantity": 15.0,
                        "shortagePackQuantity": 0.0,
                        "shippedPackQuantity": 15.0,
                        "returnedPackQuantity": 0.0,
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
                            "id": 195912,
                            "businessKey": {
                                "orderHeader.number": "5003767",
                                "lineNumber": "5003767-14",
                                "orderHeader.businessUnit.code": "ECOATM"
                            },
                            "href": "https://api.deposco.com/latest/orders/salesOrders/5203/orderLines/195912"
                        },
                        "businessUnit": {
                            "id": 73,
                            "businessKey": {
                                "code": "ECOATM"
                            },
                            "href": "https://api.deposco.com/latest/companies/73"
                        },
                        "lineNumber": "5003767-14",
                        "customerLineNumber": "14",
                        "relatedOrderLine": null,
                        "sortOrder": 0,
                        "orderHeader": {
                            "id": 5203,
                            "businessKey": {
                                "number": "5003767",
                                "businessUnit.code": "ECOATM"
                            },
                            "href": "https://api.deposco.com/latest/orders/salesOrders/5203"
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
                        "orderPackQuantity": 1.0,
                        "canceledPackQuantity": 1.0,
                        "lotNumber": null,
                        "bornOnDate": null,
                        "expirationDate": null,
                        "productCode": null,
                        "inventoryCondition": null,
                        "inventoryAttribute1": null,
                        "inventoryAttribute2": null,
                        "plannedShipDate": null,
                        "plannedArrivalDate": null,
                        "unitPrice": 365.0,
                        "unitCost": 0.0,
                        "priceCode": "Sales Order",
                        "lineTotal": 0.0,
                        "taxable": false,
                        "taxCost": 0.0,
                        "taxableTotal": 0.0,
                        "untaxableTotal": 0.0,
                        "extendedTotal": 0.0,
                        "discountAmount": 0.0,
                        "shippingAmount": 0.0,
                        "weight": {
                            "weight": 0.0,
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
                                    "id": 138048,
                                    "businessKey": {
                                        "integration.name": "ECOATM API",
                                        "ref1": "5003767-14"
                                    }
                                },
                                "integration": {
                                    "id": 1,
                                    "businessKey": {
                                        "name": "ECOATM API"
                                    }
                                },
                                "schedulerTask": null,
                                "ref1": "5003767-14",
                                "createdDate": "2025-12-03T12:00:47-06:00",
                                "updatedDate": "2025-12-03T12:00:47-06:00",
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
                        "createdDate": "2025-12-03T12:00:47-06:00",
                        "updatedDate": "2025-12-03T14:08:56-06:00",
                        "createdBy": {
                            "id": 1425,
                            "businessKey": {
                                "username": "harish.reddy"
                            },
                            "href": "https://api.deposco.com/latest/users/1425"
                        },
                        "updatedBy": {
                            "id": 1474,
                            "businessKey": {
                                "username": "chris.wise"
                            },
                            "href": "https://api.deposco.com/latest/users/1474"
                        },
                        "orderLineStatus": "Canceled",
                        "shipFromFacility": null,
                        "coLine": null,
                        "allocatedQuantity": 0.0,
                        "unallocatedQuantity": 0.0,
                        "pickedPackQuantity": 0.0,
                        "shortagePackQuantity": 0.0,
                        "shippedPackQuantity": 0.0,
                        "returnedPackQuantity": 0.0,
                        "preallocated": false,
                        "isBackOrdered": null,
                        "platinumLineNo": 0,
                        "directedToZone": null,
                        "allocateFromZone": false,
                        "carrierSpecialServices": []
                    },
                    {
                        "self": {
                            "id": 195911,
                            "businessKey": {
                                "orderHeader.number": "5003767",
                                "lineNumber": "5003767-13",
                                "orderHeader.businessUnit.code": "ECOATM"
                            },
                            "href": "https://api.deposco.com/latest/orders/salesOrders/5203/orderLines/195911"
                        },
                        "businessUnit": {
                            "id": 73,
                            "businessKey": {
                                "code": "ECOATM"
                            },
                            "href": "https://api.deposco.com/latest/companies/73"
                        },
                        "lineNumber": "5003767-13",
                        "customerLineNumber": "13",
                        "relatedOrderLine": null,
                        "sortOrder": 0,
                        "orderHeader": {
                            "id": 5203,
                            "businessKey": {
                                "number": "5003767",
                                "businessUnit.code": "ECOATM"
                            },
                            "href": "https://api.deposco.com/latest/orders/salesOrders/5203"
                        },
                        "item": {
                            "id": 21346,
                            "businessKey": {
                                "number": "PWS307398101053",
                                "businessUnit.code": "ECOATM"
                            },
                            "href": "https://api.deposco.com/latest/items/21346"
                        },
                        "pack": {
                            "id": 21346,
                            "businessKey": {
                                "item.businessUnit.code": "ECOATM",
                                "quantity": 1,
                                "item.number": "PWS307398101053"
                            },
                            "href": "https://api.deposco.com/latest/items/21346/packs/21346"
                        },
                        "description": null,
                        "orderPackQuantity": 6.0,
                        "canceledPackQuantity": 0.0,
                        "lotNumber": null,
                        "bornOnDate": null,
                        "expirationDate": null,
                        "productCode": null,
                        "inventoryCondition": null,
                        "inventoryAttribute1": null,
                        "inventoryAttribute2": null,
                        "plannedShipDate": null,
                        "plannedArrivalDate": null,
                        "unitPrice": 275.0,
                        "unitCost": 0.0,
                        "priceCode": "Sales Order",
                        "lineTotal": 0.0,
                        "taxable": false,
                        "taxCost": 0.0,
                        "taxableTotal": 0.0,
                        "untaxableTotal": 0.0,
                        "extendedTotal": 0.0,
                        "discountAmount": 0.0,
                        "shippingAmount": 0.0,
                        "weight": {
                            "weight": 0.0,
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
                                    "id": 138047,
                                    "businessKey": {
                                        "integration.name": "ECOATM API",
                                        "ref1": "5003767-13"
                                    }
                                },
                                "integration": {
                                    "id": 1,
                                    "businessKey": {
                                        "name": "ECOATM API"
                                    }
                                },
                                "schedulerTask": null,
                                "ref1": "5003767-13",
                                "createdDate": "2025-12-03T12:00:47-06:00",
                                "updatedDate": "2025-12-03T12:00:47-06:00",
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
                        "createdDate": "2025-12-03T12:00:47-06:00",
                        "updatedDate": "2025-12-04T15:29:37-06:00",
                        "createdBy": {
                            "id": 1425,
                            "businessKey": {
                                "username": "harish.reddy"
                            },
                            "href": "https://api.deposco.com/latest/users/1425"
                        },
                        "updatedBy": {
                            "id": 1664,
                            "businessKey": {
                                "username": "raul.carmona"
                            },
                            "href": "https://api.deposco.com/latest/users/1664"
                        },
                        "orderLineStatus": "Complete",
                        "shipFromFacility": null,
                        "coLine": null,
                        "allocatedQuantity": 6.0,
                        "unallocatedQuantity": 0.0,
                        "pickedPackQuantity": 6.0,
                        "shortagePackQuantity": 0.0,
                        "shippedPackQuantity": 6.0,
                        "returnedPackQuantity": 0.0,
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
                            "id": 195910,
                            "businessKey": {
                                "orderHeader.number": "5003767",
                                "lineNumber": "5003767-12",
                                "orderHeader.businessUnit.code": "ECOATM"
                            },
                            "href": "https://api.deposco.com/latest/orders/salesOrders/5203/orderLines/195910"
                        },
                        "businessUnit": {
                            "id": 73,
                            "businessKey": {
                                "code": "ECOATM"
                            },
                            "href": "https://api.deposco.com/latest/companies/73"
                        },
                        "lineNumber": "5003767-12",
                        "customerLineNumber": "12",
                        "relatedOrderLine": null,
                        "sortOrder": 0,
                        "orderHeader": {
                            "id": 5203,
                            "businessKey": {
                                "number": "5003767",
                                "businessUnit.code": "ECOATM"
                            },
                            "href": "https://api.deposco.com/latest/orders/salesOrders/5203"
                        },
                        "item": {
                            "id": 21764,
                            "businessKey": {
                                "number": "PWS367694826134",
                                "businessUnit.code": "ECOATM"
                            },
                            "href": "https://api.deposco.com/latest/items/21764"
                        },
                        "pack": {
                            "id": 21764,
                            "businessKey": {
                                "item.businessUnit.code": "ECOATM",
                                "quantity": 1,
                                "item.number": "PWS367694826134"
                            },
                            "href": "https://api.deposco.com/latest/items/21764/packs/21764"
                        },
                        "description": null,
                        "orderPackQuantity": 1.0,
                        "canceledPackQuantity": 1.0,
                        "lotNumber": null,
                        "bornOnDate": null,
                        "expirationDate": null,
                        "productCode": null,
                        "inventoryCondition": null,
                        "inventoryAttribute1": null,
                        "inventoryAttribute2": null,
                        "plannedShipDate": null,
                        "plannedArrivalDate": null,
                        "unitPrice": 395.0,
                        "unitCost": 0.0,
                        "priceCode": "Sales Order",
                        "lineTotal": 0.0,
                        "taxable": false,
                        "taxCost": 0.0,
                        "taxableTotal": 0.0,
                        "untaxableTotal": 0.0,
                        "extendedTotal": 0.0,
                        "discountAmount": 0.0,
                        "shippingAmount": 0.0,
                        "weight": {
                            "weight": 0.0,
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
                                    "id": 138046,
                                    "businessKey": {
                                        "integration.name": "ECOATM API",
                                        "ref1": "5003767-12"
                                    }
                                },
                                "integration": {
                                    "id": 1,
                                    "businessKey": {
                                        "name": "ECOATM API"
                                    }
                                },
                                "schedulerTask": null,
                                "ref1": "5003767-12",
                                "createdDate": "2025-12-03T12:00:47-06:00",
                                "updatedDate": "2025-12-03T12:00:47-06:00",
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
                        "createdDate": "2025-12-03T12:00:47-06:00",
                        "updatedDate": "2025-12-03T14:08:54-06:00",
                        "createdBy": {
                            "id": 1425,
                            "businessKey": {
                                "username": "harish.reddy"
                            },
                            "href": "https://api.deposco.com/latest/users/1425"
                        },
                        "updatedBy": {
                            "id": 1474,
                            "businessKey": {
                                "username": "chris.wise"
                            },
                            "href": "https://api.deposco.com/latest/users/1474"
                        },
                        "orderLineStatus": "Canceled",
                        "shipFromFacility": null,
                        "coLine": null,
                        "allocatedQuantity": 0.0,
                        "unallocatedQuantity": 0.0,
                        "pickedPackQuantity": 0.0,
                        "shortagePackQuantity": 0.0,
                        "shippedPackQuantity": 0.0,
                        "returnedPackQuantity": 0.0,
                        "preallocated": false,
                        "isBackOrdered": null,
                        "platinumLineNo": 0,
                        "directedToZone": null,
                        "allocateFromZone": false,
                        "carrierSpecialServices": []
                    },
                    {
                        "self": {
                            "id": 195909,
                            "businessKey": {
                                "orderHeader.number": "5003767",
                                "lineNumber": "5003767-11",
                                "orderHeader.businessUnit.code": "ECOATM"
                            },
                            "href": "https://api.deposco.com/latest/orders/salesOrders/5203/orderLines/195909"
                        },
                        "businessUnit": {
                            "id": 73,
                            "businessKey": {
                                "code": "ECOATM"
                            },
                            "href": "https://api.deposco.com/latest/companies/73"
                        },
                        "lineNumber": "5003767-11",
                        "customerLineNumber": "11",
                        "relatedOrderLine": null,
                        "sortOrder": 0,
                        "orderHeader": {
                            "id": 5203,
                            "businessKey": {
                                "number": "5003767",
                                "businessUnit.code": "ECOATM"
                            },
                            "href": "https://api.deposco.com/latest/orders/salesOrders/5203"
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
                        "orderPackQuantity": 1.0,
                        "canceledPackQuantity": 1.0,
                        "lotNumber": null,
                        "bornOnDate": null,
                        "expirationDate": null,
                        "productCode": null,
                        "inventoryCondition": null,
                        "inventoryAttribute1": null,
                        "inventoryAttribute2": null,
                        "plannedShipDate": null,
                        "plannedArrivalDate": null,
                        "unitPrice": 365.0,
                        "unitCost": 0.0,
                        "priceCode": "Sales Order",
                        "lineTotal": 0.0,
                        "taxable": false,
                        "taxCost": 0.0,
                        "taxableTotal": 0.0,
                        "untaxableTotal": 0.0,
                        "extendedTotal": 0.0,
                        "discountAmount": 0.0,
                        "shippingAmount": 0.0,
                        "weight": {
                            "weight": 0.0,
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
                                    "id": 138045,
                                    "businessKey": {
                                        "integration.name": "ECOATM API",
                                        "ref1": "5003767-11"
                                    }
                                },
                                "integration": {
                                    "id": 1,
                                    "businessKey": {
                                        "name": "ECOATM API"
                                    }
                                },
                                "schedulerTask": null,
                                "ref1": "5003767-11",
                                "createdDate": "2025-12-03T12:00:47-06:00",
                                "updatedDate": "2025-12-03T12:00:47-06:00",
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
                        "createdDate": "2025-12-03T12:00:47-06:00",
                        "updatedDate": "2025-12-03T14:08:52-06:00",
                        "createdBy": {
                            "id": 1425,
                            "businessKey": {
                                "username": "harish.reddy"
                            },
                            "href": "https://api.deposco.com/latest/users/1425"
                        },
                        "updatedBy": {
                            "id": 1474,
                            "businessKey": {
                                "username": "chris.wise"
                            },
                            "href": "https://api.deposco.com/latest/users/1474"
                        },
                        "orderLineStatus": "Canceled",
                        "shipFromFacility": null,
                        "coLine": null,
                        "allocatedQuantity": 0.0,
                        "unallocatedQuantity": 0.0,
                        "pickedPackQuantity": 0.0,
                        "shortagePackQuantity": 0.0,
                        "shippedPackQuantity": 0.0,
                        "returnedPackQuantity": 0.0,
                        "preallocated": false,
                        "isBackOrdered": null,
                        "platinumLineNo": 0,
                        "directedToZone": null,
                        "allocateFromZone": false,
                        "carrierSpecialServices": []
                    },
                    {
                        "self": {
                            "id": 195908,
                            "businessKey": {
                                "orderHeader.number": "5003767",
                                "lineNumber": "5003767-10",
                                "orderHeader.businessUnit.code": "ECOATM"
                            },
                            "href": "https://api.deposco.com/latest/orders/salesOrders/5203/orderLines/195908"
                        },
                        "businessUnit": {
                            "id": 73,
                            "businessKey": {
                                "code": "ECOATM"
                            },
                            "href": "https://api.deposco.com/latest/companies/73"
                        },
                        "lineNumber": "5003767-10",
                        "customerLineNumber": "10",
                        "relatedOrderLine": null,
                        "sortOrder": 0,
                        "orderHeader": {
                            "id": 5203,
                            "businessKey": {
                                "number": "5003767",
                                "businessUnit.code": "ECOATM"
                            },
                            "href": "https://api.deposco.com/latest/orders/salesOrders/5203"
                        },
                        "item": {
                            "id": 24610,
                            "businessKey": {
                                "number": "PWS774514466187",
                                "businessUnit.code": "ECOATM"
                            },
                            "href": "https://api.deposco.com/latest/items/24610"
                        },
                        "pack": {
                            "id": 24610,
                            "businessKey": {
                                "item.businessUnit.code": "ECOATM",
                                "quantity": 1,
                                "item.number": "PWS774514466187"
                            },
                            "href": "https://api.deposco.com/latest/items/24610/packs/24610"
                        },
                        "description": null,
                        "orderPackQuantity": 1.0,
                        "canceledPackQuantity": 0.0,
                        "lotNumber": null,
                        "bornOnDate": null,
                        "expirationDate": null,
                        "productCode": null,
                        "inventoryCondition": null,
                        "inventoryAttribute1": null,
                        "inventoryAttribute2": null,
                        "plannedShipDate": null,
                        "plannedArrivalDate": null,
                        "unitPrice": 380.0,
                        "unitCost": 0.0,
                        "priceCode": "Sales Order",
                        "lineTotal": 0.0,
                        "taxable": false,
                        "taxCost": 0.0,
                        "taxableTotal": 0.0,
                        "untaxableTotal": 0.0,
                        "extendedTotal": 0.0,
                        "discountAmount": 0.0,
                        "shippingAmount": 0.0,
                        "weight": {
                            "weight": 0.0,
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
                                    "id": 138044,
                                    "businessKey": {
                                        "integration.name": "ECOATM API",
                                        "ref1": "5003767-10"
                                    }
                                },
                                "integration": {
                                    "id": 1,
                                    "businessKey": {
                                        "name": "ECOATM API"
                                    }
                                },
                                "schedulerTask": null,
                                "ref1": "5003767-10",
                                "createdDate": "2025-12-03T12:00:47-06:00",
                                "updatedDate": "2025-12-03T12:00:47-06:00",
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
                        "createdDate": "2025-12-03T12:00:47-06:00",
                        "updatedDate": "2025-12-04T15:29:37-06:00",
                        "createdBy": {
                            "id": 1425,
                            "businessKey": {
                                "username": "harish.reddy"
                            },
                            "href": "https://api.deposco.com/latest/users/1425"
                        },
                        "updatedBy": {
                            "id": 1664,
                            "businessKey": {
                                "username": "raul.carmona"
                            },
                            "href": "https://api.deposco.com/latest/users/1664"
                        },
                        "orderLineStatus": "Complete",
                        "shipFromFacility": null,
                        "coLine": null,
                        "allocatedQuantity": 1.0,
                        "unallocatedQuantity": 0.0,
                        "pickedPackQuantity": 1.0,
                        "shortagePackQuantity": 0.0,
                        "shippedPackQuantity": 1.0,
                        "returnedPackQuantity": 0.0,
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
                            "id": 195907,
                            "businessKey": {
                                "orderHeader.number": "5003767",
                                "lineNumber": "5003767-9",
                                "orderHeader.businessUnit.code": "ECOATM"
                            },
                            "href": "https://api.deposco.com/latest/orders/salesOrders/5203/orderLines/195907"
                        },
                        "businessUnit": {
                            "id": 73,
                            "businessKey": {
                                "code": "ECOATM"
                            },
                            "href": "https://api.deposco.com/latest/companies/73"
                        },
                        "lineNumber": "5003767-9",
                        "customerLineNumber": "9",
                        "relatedOrderLine": null,
                        "sortOrder": 0,
                        "orderHeader": {
                            "id": 5203,
                            "businessKey": {
                                "number": "5003767",
                                "businessUnit.code": "ECOATM"
                            },
                            "href": "https://api.deposco.com/latest/orders/salesOrders/5203"
                        },
                        "item": {
                            "id": 16283,
                            "businessKey": {
                                "number": "PWS10009792",
                                "businessUnit.code": "ECOATM"
                            },
                            "href": "https://api.deposco.com/latest/items/16283"
                        },
                        "pack": {
                            "id": 16283,
                            "businessKey": {
                                "item.businessUnit.code": "ECOATM",
                                "quantity": 1,
                                "item.number": "PWS10009792"
                            },
                            "href": "https://api.deposco.com/latest/items/16283/packs/16283"
                        },
                        "description": null,
                        "orderPackQuantity": 1.0,
                        "canceledPackQuantity": 0.0,
                        "lotNumber": null,
                        "bornOnDate": null,
                        "expirationDate": null,
                        "productCode": null,
                        "inventoryCondition": null,
                        "inventoryAttribute1": null,
                        "inventoryAttribute2": null,
                        "plannedShipDate": null,
                        "plannedArrivalDate": null,
                        "unitPrice": 585.0,
                        "unitCost": 0.0,
                        "priceCode": "Sales Order",
                        "lineTotal": 0.0,
                        "taxable": false,
                        "taxCost": 0.0,
                        "taxableTotal": 0.0,
                        "untaxableTotal": 0.0,
                        "extendedTotal": 0.0,
                        "discountAmount": 0.0,
                        "shippingAmount": 0.0,
                        "weight": {
                            "weight": 0.0,
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
                                    "id": 138043,
                                    "businessKey": {
                                        "integration.name": "ECOATM API",
                                        "ref1": "5003767-9"
                                    }
                                },
                                "integration": {
                                    "id": 1,
                                    "businessKey": {
                                        "name": "ECOATM API"
                                    }
                                },
                                "schedulerTask": null,
                                "ref1": "5003767-9",
                                "createdDate": "2025-12-03T12:00:47-06:00",
                                "updatedDate": "2025-12-03T12:00:47-06:00",
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
                        "createdDate": "2025-12-03T12:00:47-06:00",
                        "updatedDate": "2025-12-04T15:29:37-06:00",
                        "createdBy": {
                            "id": 1425,
                            "businessKey": {
                                "username": "harish.reddy"
                            },
                            "href": "https://api.deposco.com/latest/users/1425"
                        },
                        "updatedBy": {
                            "id": 1664,
                            "businessKey": {
                                "username": "raul.carmona"
                            },
                            "href": "https://api.deposco.com/latest/users/1664"
                        },
                        "orderLineStatus": "Complete",
                        "shipFromFacility": null,
                        "coLine": null,
                        "allocatedQuantity": 1.0,
                        "unallocatedQuantity": 0.0,
                        "pickedPackQuantity": 1.0,
                        "shortagePackQuantity": 0.0,
                        "shippedPackQuantity": 1.0,
                        "returnedPackQuantity": 0.0,
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
                            "id": 195906,
                            "businessKey": {
                                "orderHeader.number": "5003767",
                                "lineNumber": "5003767-8",
                                "orderHeader.businessUnit.code": "ECOATM"
                            },
                            "href": "https://api.deposco.com/latest/orders/salesOrders/5203/orderLines/195906"
                        },
                        "businessUnit": {
                            "id": 73,
                            "businessKey": {
                                "code": "ECOATM"
                            },
                            "href": "https://api.deposco.com/latest/companies/73"
                        },
                        "lineNumber": "5003767-8",
                        "customerLineNumber": "8",
                        "relatedOrderLine": null,
                        "sortOrder": 0,
                        "orderHeader": {
                            "id": 5203,
                            "businessKey": {
                                "number": "5003767",
                                "businessUnit.code": "ECOATM"
                            },
                            "href": "https://api.deposco.com/latest/orders/salesOrders/5203"
                        },
                        "item": {
                            "id": 22367,
                            "businessKey": {
                                "number": "PWS453631306630",
                                "businessUnit.code": "ECOATM"
                            },
                            "href": "https://api.deposco.com/latest/items/22367"
                        },
                        "pack": {
                            "id": 22367,
                            "businessKey": {
                                "item.businessUnit.code": "ECOATM",
                                "quantity": 1,
                                "item.number": "PWS453631306630"
                            },
                            "href": "https://api.deposco.com/latest/items/22367/packs/22367"
                        },
                        "description": null,
                        "orderPackQuantity": 3.0,
                        "canceledPackQuantity": 0.0,
                        "lotNumber": null,
                        "bornOnDate": null,
                        "expirationDate": null,
                        "productCode": null,
                        "inventoryCondition": null,
                        "inventoryAttribute1": null,
                        "inventoryAttribute2": null,
                        "plannedShipDate": null,
                        "plannedArrivalDate": null,
                        "unitPrice": 275.0,
                        "unitCost": 0.0,
                        "priceCode": "Sales Order",
                        "lineTotal": 0.0,
                        "taxable": false,
                        "taxCost": 0.0,
                        "taxableTotal": 0.0,
                        "untaxableTotal": 0.0,
                        "extendedTotal": 0.0,
                        "discountAmount": 0.0,
                        "shippingAmount": 0.0,
                        "weight": {
                            "weight": 0.0,
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
                                    "id": 138042,
                                    "businessKey": {
                                        "integration.name": "ECOATM API",
                                        "ref1": "5003767-8"
                                    }
                                },
                                "integration": {
                                    "id": 1,
                                    "businessKey": {
                                        "name": "ECOATM API"
                                    }
                                },
                                "schedulerTask": null,
                                "ref1": "5003767-8",
                                "createdDate": "2025-12-03T12:00:47-06:00",
                                "updatedDate": "2025-12-03T12:00:47-06:00",
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
                        "createdDate": "2025-12-03T12:00:47-06:00",
                        "updatedDate": "2025-12-04T15:29:37-06:00",
                        "createdBy": {
                            "id": 1425,
                            "businessKey": {
                                "username": "harish.reddy"
                            },
                            "href": "https://api.deposco.com/latest/users/1425"
                        },
                        "updatedBy": {
                            "id": 1664,
                            "businessKey": {
                                "username": "raul.carmona"
                            },
                            "href": "https://api.deposco.com/latest/users/1664"
                        },
                        "orderLineStatus": "Complete",
                        "shipFromFacility": null,
                        "coLine": null,
                        "allocatedQuantity": 3.0,
                        "unallocatedQuantity": 0.0,
                        "pickedPackQuantity": 3.0,
                        "shortagePackQuantity": 0.0,
                        "shippedPackQuantity": 3.0,
                        "returnedPackQuantity": 0.0,
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
                            "id": 195905,
                            "businessKey": {
                                "orderHeader.number": "5003767",
                                "lineNumber": "5003767-7",
                                "orderHeader.businessUnit.code": "ECOATM"
                            },
                            "href": "https://api.deposco.com/latest/orders/salesOrders/5203/orderLines/195905"
                        },
                        "businessUnit": {
                            "id": 73,
                            "businessKey": {
                                "code": "ECOATM"
                            },
                            "href": "https://api.deposco.com/latest/companies/73"
                        },
                        "lineNumber": "5003767-7",
                        "customerLineNumber": "7",
                        "relatedOrderLine": null,
                        "sortOrder": 0,
                        "orderHeader": {
                            "id": 5203,
                            "businessKey": {
                                "number": "5003767",
                                "businessUnit.code": "ECOATM"
                            },
                            "href": "https://api.deposco.com/latest/orders/salesOrders/5203"
                        },
                        "item": {
                            "id": 24332,
                            "businessKey": {
                                "number": "PWS735426952581",
                                "businessUnit.code": "ECOATM"
                            },
                            "href": "https://api.deposco.com/latest/items/24332"
                        },
                        "pack": {
                            "id": 24332,
                            "businessKey": {
                                "item.businessUnit.code": "ECOATM",
                                "quantity": 1,
                                "item.number": "PWS735426952581"
                            },
                            "href": "https://api.deposco.com/latest/items/24332/packs/24332"
                        },
                        "description": null,
                        "orderPackQuantity": 1.0,
                        "canceledPackQuantity": 0.0,
                        "lotNumber": null,
                        "bornOnDate": null,
                        "expirationDate": null,
                        "productCode": null,
                        "inventoryCondition": null,
                        "inventoryAttribute1": null,
                        "inventoryAttribute2": null,
                        "plannedShipDate": null,
                        "plannedArrivalDate": null,
                        "unitPrice": 380.0,
                        "unitCost": 0.0,
                        "priceCode": "Sales Order",
                        "lineTotal": 0.0,
                        "taxable": false,
                        "taxCost": 0.0,
                        "taxableTotal": 0.0,
                        "untaxableTotal": 0.0,
                        "extendedTotal": 0.0,
                        "discountAmount": 0.0,
                        "shippingAmount": 0.0,
                        "weight": {
                            "weight": 0.0,
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
                                    "id": 138041,
                                    "businessKey": {
                                        "integration.name": "ECOATM API",
                                        "ref1": "5003767-7"
                                    }
                                },
                                "integration": {
                                    "id": 1,
                                    "businessKey": {
                                        "name": "ECOATM API"
                                    }
                                },
                                "schedulerTask": null,
                                "ref1": "5003767-7",
                                "createdDate": "2025-12-03T12:00:47-06:00",
                                "updatedDate": "2025-12-03T12:00:47-06:00",
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
                        "createdDate": "2025-12-03T12:00:47-06:00",
                        "updatedDate": "2025-12-04T15:29:37-06:00",
                        "createdBy": {
                            "id": 1425,
                            "businessKey": {
                                "username": "harish.reddy"
                            },
                            "href": "https://api.deposco.com/latest/users/1425"
                        },
                        "updatedBy": {
                            "id": 1664,
                            "businessKey": {
                                "username": "raul.carmona"
                            },
                            "href": "https://api.deposco.com/latest/users/1664"
                        },
                        "orderLineStatus": "Complete",
                        "shipFromFacility": null,
                        "coLine": null,
                        "allocatedQuantity": 1.0,
                        "unallocatedQuantity": 0.0,
                        "pickedPackQuantity": 1.0,
                        "shortagePackQuantity": 0.0,
                        "shippedPackQuantity": 1.0,
                        "returnedPackQuantity": 0.0,
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
                            "id": 195904,
                            "businessKey": {
                                "orderHeader.number": "5003767",
                                "lineNumber": "5003767-6",
                                "orderHeader.businessUnit.code": "ECOATM"
                            },
                            "href": "https://api.deposco.com/latest/orders/salesOrders/5203/orderLines/195904"
                        },
                        "businessUnit": {
                            "id": 73,
                            "businessKey": {
                                "code": "ECOATM"
                            },
                            "href": "https://api.deposco.com/latest/companies/73"
                        },
                        "lineNumber": "5003767-6",
                        "customerLineNumber": "6",
                        "relatedOrderLine": null,
                        "sortOrder": 0,
                        "orderHeader": {
                            "id": 5203,
                            "businessKey": {
                                "number": "5003767",
                                "businessUnit.code": "ECOATM"
                            },
                            "href": "https://api.deposco.com/latest/orders/salesOrders/5203"
                        },
                        "item": {
                            "id": 16287,
                            "businessKey": {
                                "number": "PWS10009796",
                                "businessUnit.code": "ECOATM"
                            },
                            "href": "https://api.deposco.com/latest/items/16287"
                        },
                        "pack": {
                            "id": 16287,
                            "businessKey": {
                                "item.businessUnit.code": "ECOATM",
                                "quantity": 1,
                                "item.number": "PWS10009796"
                            },
                            "href": "https://api.deposco.com/latest/items/16287/packs/16287"
                        },
                        "description": null,
                        "orderPackQuantity": 2.0,
                        "canceledPackQuantity": 0.0,
                        "lotNumber": null,
                        "bornOnDate": null,
                        "expirationDate": null,
                        "productCode": null,
                        "inventoryCondition": null,
                        "inventoryAttribute1": null,
                        "inventoryAttribute2": null,
                        "plannedShipDate": null,
                        "plannedArrivalDate": null,
                        "unitPrice": 585.0,
                        "unitCost": 0.0,
                        "priceCode": "Sales Order",
                        "lineTotal": 0.0,
                        "taxable": false,
                        "taxCost": 0.0,
                        "taxableTotal": 0.0,
                        "untaxableTotal": 0.0,
                        "extendedTotal": 0.0,
                        "discountAmount": 0.0,
                        "shippingAmount": 0.0,
                        "weight": {
                            "weight": 0.0,
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
                                    "id": 138040,
                                    "businessKey": {
                                        "integration.name": "ECOATM API",
                                        "ref1": "5003767-6"
                                    }
                                },
                                "integration": {
                                    "id": 1,
                                    "businessKey": {
                                        "name": "ECOATM API"
                                    }
                                },
                                "schedulerTask": null,
                                "ref1": "5003767-6",
                                "createdDate": "2025-12-03T12:00:47-06:00",
                                "updatedDate": "2025-12-03T12:00:47-06:00",
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
                        "createdDate": "2025-12-03T12:00:47-06:00",
                        "updatedDate": "2025-12-04T15:29:37-06:00",
                        "createdBy": {
                            "id": 1425,
                            "businessKey": {
                                "username": "harish.reddy"
                            },
                            "href": "https://api.deposco.com/latest/users/1425"
                        },
                        "updatedBy": {
                            "id": 1664,
                            "businessKey": {
                                "username": "raul.carmona"
                            },
                            "href": "https://api.deposco.com/latest/users/1664"
                        },
                        "orderLineStatus": "Complete",
                        "shipFromFacility": null,
                        "coLine": null,
                        "allocatedQuantity": 2.0,
                        "unallocatedQuantity": 0.0,
                        "pickedPackQuantity": 2.0,
                        "shortagePackQuantity": 0.0,
                        "shippedPackQuantity": 2.0,
                        "returnedPackQuantity": 0.0,
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
                        "rel": "next",
                        "href": "https://api.deposco.com/latest/orders/salesOrders/5203/orderLines?searchId=bWF4SWR8bHR8MTk1OTA0fGlubmVySm9pbiZvcmRlckhlYWRlci5pZHxlcXw1MjAzfGlubmVySm9pbiZ0b3RhbFBhZ2VzfGVxfDI"
                    }
                ],
                "complete": false,
                "pages": 2
            },
            "carrierSpecialServices": [],
            "shipments": {
                "data": [
                    {
                        "id": 1035,
                        "businessKey": {
                            "number": "1035",
                            "businessUnit.code": "ECOATM"
                        },
                        "href": "https://api.deposco.com/latest/shipments/outboundShipments/1035"
                    }
                ],
                "links": [
                    {
                        "rel": "next",
                        "href": "https://api.deposco.com/latest/shipments/outboundShipments?pageSize=1035"
                    }
                ],
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
