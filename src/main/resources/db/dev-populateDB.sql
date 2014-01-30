/* Add service kind */ 
INSERT INTO service_categories (kind) VALUES (0);
INSERT INTO service_categories (kind) VALUES (1);
INSERT INTO service_categories (kind) VALUES (2);
INSERT INTO service_categories (kind) VALUES (3);
INSERT INTO service_categories (kind) VALUES (4);
INSERT INTO service_categories (kind) VALUES (5);
INSERT INTO service_categories (kind) VALUES (6);
INSERT INTO service_categories (kind) VALUES (7);
INSERT INTO service_categories (kind) VALUES (8);
INSERT INTO service_categories (kind) VALUES (9);

/* Add retail customers */ 
INSERT INTO retail_customers (username, first_name, last_name, password, enabled, role) VALUES ('alan',    'Alan',    'Turing',      'koala', TRUE, 'ROLE_USER');
INSERT INTO retail_customers (username, first_name, last_name, password, enabled, role) VALUES ('donald',  'Donald',  'Knuth',       'koala', TRUE, 'ROLE_USER');
INSERT INTO retail_customers (username, first_name, last_name, password, enabled, role) VALUES ('paul',    'Paul',    'Dirac',       'koala', TRUE, 'ROLE_USER');
INSERT INTO retail_customers (username, first_name, last_name, password, enabled, role) VALUES ('alonzo',  'Alonzo',  'Church',      'koala', TRUE, 'ROLE_USER');
INSERT INTO retail_customers (username, first_name, last_name, password, enabled, role) VALUES ('charles', 'Charles', 'Babbage',     'koala', TRUE, 'ROLE_USER');
INSERT INTO retail_customers (username, first_name, last_name, password, enabled, role) VALUES ('john',    'John',    'von Neumann', 'koala', TRUE, 'ROLE_USER');
INSERT INTO retail_customers (username, first_name, last_name, password, enabled, role) VALUES ('marian',  'Marian',  'Rejewski',    'koala', TRUE, 'ROLE_USER');
INSERT INTO retail_customers (username, first_name, last_name, password, enabled, role) VALUES ('grace',   'Grace',   'Hopper',      'koala', TRUE, 'ROLE_CUSTODIAN');

/* Add application_information */ 
/*INSERT INTO application_information (`id`,`description`,`published`,`self_link_href`,`self_link_rel`,`up_link_href`,`up_link_rel`,`updated`,`uuid`,`authorizationServerAuthorizationEndpoint`,`authorizationServerRegistrationEndpoint`,`authorizationServerTokenEndpoint`,`authorizationServerUri`,`clientId`,`clientIdIssuedAt`,`clientName`,`clientSecret`,`clientSecretExpiresAt`,`clientUri`,`contacts`,`dataCustodianApplicationStatus`,`dataCustodianBulkRequestURI`,`dataCustodianDefaultBatchResource`,`dataCustodianDefaultSubscriptionResource`,`dataCustodianId`,`dataCustodianResourceEndpoint`,`dataCustodianThirdPartySelectionScreenURI`,`grantTypes`,`logoUri`,`policyUri`,`redirectUri`,`registrationAccessToken`,`registrationClientUri`,`responseTypes`,`softwareId`,`softwareVersion`,`thirdPartyApplicationDescription`,`thirdPartyApplicationName`,`thirdPartyApplicationStatus`,`thirdPartyApplicationType`,`thirdPartyApplicationUse`,`thirdPartyDataCustodianSelectionScreenURI`,`thirdPartyLoginScreenURI`,`thirdPartyNotifyUri`,`thirdPartyPhone`,`thirdPartyScopeSelectionScreenURI`,`thirdPartyUserPortalScreenURI`,`tokenEndpointAuthMethod`,`tosUri`) VALUES (1,'GreenButtonData.org  DataCustodian Application','2014-01-02 05:00:00','/espi/1_1/resource/DataCustodian/ApplicationInformation/1','self','/espi/1_1/resource/DataCustodian/ApplicationInformation','up','2014-01-02 05:00:00','AF6E8B03-0299-467E-972A-A883ECDCC575',NULL,NULL,NULL,NULL,'third_party',NULL,NULL,'secret',NULL,NULL,NULL,'','',NULL,NULL,'data_custodian','http://localhost:8080/DataCustodian/espi/1_1/resource',NULL,NULL,NULL,NULL,'http://localhost:8080/ThirdParty/espi/1_1/OAuthCallBack',NULL,NULL,NULL,NULL,NULL,NULL,'Third Party (localhost)',NULL,NULL,NULL,NULL,NULL,'http://localhost:8080/ThirdParty/espi/1_1/Notification',NULL,'http://localhost:8080/ThirdParty/RetailCustomer/ScopeSelection',NULL,NULL,NULL);*/
INSERT INTO application_information  (id, uuid, clientId, thirdPartyApplicationName, thirdPartyScopeSelectionScreenURI, thirdPartyNotifyUri, redirectURI, clientSecret) VALUES (1,'550e8400-e29b-41d4-a716-4466554413a0', 'third_party', 'Third Party (localhost)', 'http://localhost:8080/ThirdParty/RetailCustomer/ScopeSelection', 'http://localhost:8080/ThirdParty/espi/1_1/Notification', 'http://localhost:8080/ThirdParty/espi/1_1/OAuthCallBack', 'secret');

/* Add application_information_scopes */ 
INSERT INTO application_information_scopes (application_information_id, scope) VALUES (1, 'FB=4_5_15;IntervalDuration=3600;BlockDuration=monthly;HistoryLength=13');
INSERT INTO application_information_scopes (application_information_id, scope) VALUES (1, 'FB=4_5_16;IntervalDuration=3600;BlockDuration=monthly;HistoryLength=13');

