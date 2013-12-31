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

INSERT INTO retail_customers (username, first_name, last_name, password, enabled, role) VALUES ('alan',    'Alan',    'Turing',      'koala', TRUE, 'ROLE_USER');
INSERT INTO retail_customers (username, first_name, last_name, password, enabled, role) VALUES ('donald',  'Donald',  'Knuth',       'koala', TRUE, 'ROLE_USER');
INSERT INTO retail_customers (username, first_name, last_name, password, enabled, role) VALUES ('paul',    'Paul',    'Dirac',       'koala', TRUE, 'ROLE_USER');
INSERT INTO retail_customers (username, first_name, last_name, password, enabled, role) VALUES ('alonzo',  'Alonzo',  'Church',      'koala', TRUE, 'ROLE_USER');
INSERT INTO retail_customers (username, first_name, last_name, password, enabled, role) VALUES ('charles', 'Charles', 'Babbage',     'koala', TRUE, 'ROLE_USER');
INSERT INTO retail_customers (username, first_name, last_name, password, enabled, role) VALUES ('john',    'John',    'von Neumann', 'koala', TRUE, 'ROLE_USER');
INSERT INTO retail_customers (username, first_name, last_name, password, enabled, role) VALUES ('marian',  'Marian',  'Rejewski',    'koala', TRUE, 'ROLE_USER');
INSERT INTO retail_customers (username, first_name, last_name, password, enabled, role) VALUES ('grace',   'Grace',   'Hopper',      'koala', TRUE, 'ROLE_CUSTODIAN');

INSERT INTO application_information  (id, uuid, clientId, thirdPartyApplicationName, thirdPartyScopeSelectionScreenURI, thirdPartyNotifyUri, redirectURI, clientSecret) VALUES (1,'550e8400-e29b-41d4-a716-4466554413a0', 'third_party', 'Third Party (localhost)', 'http://localhost:8080/ThirdParty/RetailCustomer/ScopeSelection', 'http://localhost:8080/ThirdParty/espi/1_1/Notification', 'http://localhost:8080/ThirdParty/espi/1_1/OAuthCallBack', 'secret');
INSERT INTO application_information_scopes (application_information_id, scope) VALUES (1, 'FB=4_5_15;IntervalDuration=3600;BlockDuration=monthly;HistoryLength=13');
INSERT INTO application_information_scopes (application_information_id, scope) VALUES (1, 'FB=4_5_16;IntervalDuration=3600;BlockDuration=monthly;HistoryLength=13');