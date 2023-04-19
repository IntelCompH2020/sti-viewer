## Keycloack Configuration

### Clients

#### Core App Client (Required)

This client is used by web app to authenticate the users.

*   **Client ID:** The client id (ex. my-app-client) (Required)
*   **Access Type:** Public (Required)
*   **Standard Flow:** Enabled (Required)
*   **Implicit Flow:** Enabled (Required)
*   **Direct Access Grants:** Enabled (Required)
*   **Valid Redirect URIs:** Should be the web app base url (ex. https://example.com/app/*)  (Required)
*   **Base URL:** Should be the web app base url (ex. https://example.com/app) 
*   **Web Origins:** Should be the web app domain url (ex. https://example.com) (Required)
*   **Roles:** Create two roles, one for app admin and one for guests (ex. superuser, guest). Use ic-sti-guest, ic-sti-superuser if you don't want to override permissions  (Required)
*   **Full Scope Allowed:** Enabled (Required)
*   **Mappers:** Auth Mapper (Required)
    *   **Mapper Type:** User Attribute (Required)
    *   **User Attribute:** The attribute name (ex. auth) (Required)
    *   **Token Claim Name:** The claim name (ex. authorities) (Required)
    *   **Claim JSON Type :** String (Required)
    *   **Add to ID token :** On (Required)
    *   **Add to access token :** On (Required)
    *   **Add to userinfo:** On (Required)
    *   **Multivalued:** On (Required)
    *   **Aggregate attribute value:** On (Required)
*   **Mappers:** Tenant Mapper (Required)
    *   **Mapper Type:** User Attribute (Required)
    *   **User Attribute:** The attribute name (ex. tenant) (Required)
    *   **Token Claim Name:** The claim name (ex. x-tenant) (Required)
    *   **Claim JSON Type :** String (Required)
    *   **Add to ID token :** On (Required)
    *   **Add to access token :** On (Required)
    *   **Add to userinfo:** On (Required)
    *   **Multivalued:** On (Required)
    *   **Aggregate attribute value:** On (Required)

#### Client for tenant auto creation (Optional)

This client is used for auto-creating tenants and is required if the app uses the flow of tenant approval 

*   **Client ID:** The client id (ex. my-app-api-client) (Required)
*   **Access Type:** Confidential (Required)
*   **Direct Access Grants Enabled:** Enabled (Required)
*   **Client Authenticator:** Client Id and Secret  (Required)
*   **Secret:** The client secret (ex. my-app-api-client-secret) (Required)
*   **Roles:** Create two roles, one for app admin and one for guests (ex. superuser, quest) use ic-sti-guest, ic-sti-superuser if you don't want to override permissions  (Required)
*   **Full Scope Allowed:** Enabled (Required)

### Groups

#### Administrator Group (Required)

The group of administrators. You should keep the group Guid from URL to add it to the configuration. 

*   **Name:** The name of the group (ex. administrators)  (Required)
*   **Role Mappings:** The roles that are mapped for the group (Required)
    *   **Client Roles:** For the [core app client](#core-app-client-required)  add Assigned Roles the superuser role (ex. superuser)  (Required)

#### Guests Group (Required)

The group of guests. You should keep the group Guid from URL to add it to the configuration. 

*   **Name:** The name of the group (ex. guests)  (Required)
*   **Role Mappings:** The roles that are mapped for the group (Required)
    *   **Client Roles:** For the [core app client](#core-app-client-required)  add Assigned Roles the guest role (ex. guest)  (Required)

#### Tenants Admins Root Group (Required)

This group is used by web app to add inside this new tenant admin groups. You should keep the group Guid from URL to add it to the configuration. 

#### Tenants Users Root Group (Required)

This group is used by web app to add inside this new tenant user groups. You should keep the group Guid from URL to add it to the configuration. 

**Specific Tenant Admin Group (Optional)**

The group of specific tenant admins. This group is required if the management of tenants and tenants request is disabled and the user are mapped to tenants directly from keycloack. It is required if the [client for tenant auto creation](#client-for-tenant-auto-creation-optional) is not set.

*   **Name:** The name of the group. The name should follow the configuration pattern  (ex. pattern: ‘tenant-{tenantCode}’, tenant code: demo, name: tenant-demo)  (Required)
*   **Attributes:** The attributes for the group (Required)
    *   **Auth attribute:** This attribute should have the same name as the [core app client](#core-app-client-required) Auth Mapper User Attribute value (ex. auth). The value should follow the configuration pattern  (ex. pattern: 'tenantadmin:{tenantCode}', tenant code: demo, value: tenantadmin:demo)(Required). The tenantadmin part of pattern is also configurable, but if changed the pattern and permission configuration should also be changed.

**Specific Tenant User Group (Optional)**

The group of specific tenant users. This group is required if the management of tenants and tenants request is disabled and the user are mapped to tenants directly from keycloack. It is required if the [client for tenant auto creation](#client-for-tenant-auto-creation-optional) is not set.

*   **Name:** The name of the group. The name should follow the configuration pattern  (ex. pattern: ‘tenant-{tenantCode}’, tenant code: demo, name: tenant-demo)  (Required)
*   **Attributes:** The attributes for the group (Required)
    *   **Auth attribute:** This attribute should have the same name as the [core app client](#core-app-client-required) Auth Mapper User Attribute value (ex. auth). The value should follow the configuration pattern  (ex. pattern: 'tenantuser:{tenantCode}', tenant code: demo, value: tenantuser:demo)(Required). The tenantadmin part of pattern is also configurable, but if changed the pattern and permission configuration should also be changed.

#### Rest Api Users (**Optional**)

The group of web app rest client that manages the users/tenanats. This group is required if the web app manages the of tenants and tenants request=It is required if the [client for tenant auto creation](#client-for-tenant-auto-creation-optional) is set.

The group of guests. You should keep the group Guid from URL to add it to the configuration. 

*   **Name:** The name of the group (ex. rest-api-users)  (Required)
*   **Attributes:** The attributes for the group (Required)
    *   **RealUser attribute:** This attribute should be set to false (Required)
*   **Role Mappings:** The roles that are mapped for the group (Required)
    *   **Client Roles:** For the realm-management  add Assigned Roles to the realm admin  (Required)

### Default Groups  (Optional)

If the installation is for a single tenant and the realm is only for the sti-viewer app, a default group [guests](#specific-tenant-user-group-optional) can be added. For auto assign new users as guests of the tenant. 

## ElasticSearch Configuration

### Global Configuration

Change max bucket size 

```plaintext
PUT _cluster/settings
{
  "persistent": {
    "search.max_buckets": 200000
  }
} 
```

Install the icu analyzer

```plaintext
sudo bin/elasticsearch-plugin install analysis-icu
```

### Indicator Index

Add a mapping for the indicator index. And than create the index 

```plaintext
{
    "dynamic_date_formats": [
        "strict_date_optional_time",
        "yyyy/MM/dd HH:mm:ss Z||yyyy/MM/dd Z"
    ],
    "dynamic_templates": [],
    "date_detection": true,
    "numeric_detection": false,
    "properties": {
        "id": {
            "type": "keyword"
        },
        "metadata": {
            "properties": {
                "alt_descriptions": {
                    "type": "nested",
                    "properties": {
                        "lang": {
                            "type": "keyword"
                        },
                        "text": {
                            "type": "text"
                        }
                    }
                },
                "alt_labels": {
                    "type": "nested",
                    "properties": {
                        "lang": {
                            "type": "keyword"
                        },
                        "text": {
                            "type": "text"
                        }
                    }
                },
                "code": {
                    "type": "keyword"
                },
                "coverage": {
                    "type": "nested",
                    "properties": {
                        "label": {
                            "type": "text"
                        },
                        "max": {
                            "type": "double"
                        },
                        "min": {
                            "type": "double"
                        }
                    }
                },
                "date": {
                    "type": "date"
                },
                "description": {
                    "type": "text"
                },
                "label": {
                    "type": "text",
                    "fields": {
                        "keyword": {
                            "type": "keyword"
                        }
                    }
                },
                "semantic_labels": {
                    "type": "nested",
                    "properties": {
                        "label": {
                            "type": "keyword"
                        }
                    }
                },
                "url": {
                    "type": "keyword"
                }
            }
        },
        "schema": {
            "properties": {
                "fields": {
                    "type": "nested",
                    "properties": {
                        "alt_descriptions": {
                            "type": "nested",
                            "properties": {
                                "lang": {
                                    "type": "keyword"
                                },
                                "text": {
                                    "type": "text"
                                }
                            }
                        },
                        "alt_labels": {
                            "type": "nested",
                            "properties": {
                                "lang": {
                                    "type": "keyword"
                                },
                                "text": {
                                    "type": "text"
                                }
                            }
                        },
                        "base_type": {
                            "type": "keyword"
                        },
                        "code": {
                            "type": "keyword"
                        },
                        "description": {
                            "type": "text"
                        },
                        "id": {
                            "type": "keyword"
                        },
                        "label": {
                            "type": "text"
                        },
                        "name": {
                            "type": "text",
                            "fields": {
                                "keyword": {
                                    "type": "keyword"
                                }
                            }
                        },
                        "operations": {
                            "type": "nested",
                            "properties": {
                                "op": {
                                    "type": "keyword"
                                }
                            }
                        },
                        "sub_field_of": {
                            "type": "keyword"
                        },
                        "type_id": {
                            "type": "keyword"
                        },
                        "type_semantics": {
                            "type": "keyword"
                        },
                        "use_as": {
                            "type": "keyword"
                        },
                        "validation": {
                            "type": "nested",
                            "properties": {
                                "dateValues": {
                                    "type": "date"
                                },
                                "doubleValues": {
                                    "type": "double"
                                },
                                "intValues": {
                                    "type": "integer"
                                },
                                "maxDate": {
                                    "type": "date"
                                },
                                "maxDouble": {
                                    "type": "double"
                                },
                                "maxInt": {
                                    "type": "integer"
                                },
                                "minDate": {
                                    "type": "date"
                                },
                                "minDouble": {
                                    "type": "double"
                                },
                                "minInt": {
                                    "type": "integer"
                                },
                                "stringValues": {
                                    "type": "keyword"
                                },
                                "type": {
                                    "type": "keyword"
                                }
                            }
                        },
                        "value_field": {
                            "type": "keyword"
                        },
                        "value_range": {
                            "properties": {
                                "max": {
                                    "type": "double"
                                },
                                "min": {
                                    "type": "double"
                                },
                                "values": {
                                    "type": "nested",
                                    "properties": {
                                        "label": {
                                            "type": "text"
                                        },
                                        "value": {
                                            "type": "text"
                                        }
                                    }
                                }
                            }
                        }
                    }
                },
                "id": {
                    "type": "keyword"
                }
            }
        }
    }
}
```

## Api Configuration

### app.env

*   **WEB\_PORT:** The api port (ex. 8080) (Required)
*   **DB\_CONNECTION\_STRING:** Database connection string (ex. jdbc:postgresql://db:5432/sti-viewer?stringtype=unspecified) (Required)
*   **DB\_USER:** Database user (ex. user) (Required)
*   **DB\_PASSWORD:** Database password (ex. pass) (Required)
*   **THE\_API\_ID:** An Identifier for the app (ex. StiViewer.App.Web) (Required)
*   **KEYCLOAK\_API\_SERVER\_URL:** The keycloack url. It is required if we want the app to manage the tenants (ex. http://keycloack:8080/auth) (Optional)
*   **KEYCLOAK\_API\_REALM:** The keycloack realm name. It is required if we want the app to manage the tenants (ex. realmname) (Optional)
*   **KEYCLOAK\_API\_USERNAME:** The user name of the user that manages the tenants. The user should be member of [Rest Api Users](#rest-api-users-optional) group. It is required if we want the app to manage the tenants  (ex. username) (Optional)
*   **KEYCLOAK\_API\_PASSWORD:** The user name of the user that manages the tenants. The user should be member of [Rest Api Users](#rest-api-users-optional) group. It is required if we want the app to manage the tenants   (ex. pass) (Optional)
*   **KEYCLOAK\_API\_CLIENT\_ID:** The api client id for [client for tenant auto creation](#client-for-tenant-auto-creation-optional). It is required if we want the app to manage the tenants  (ex. my-app-api-client) (Optional)
*   **KEYCLOAK\_API\_CLIENT\_SECRET:** The api client secret for [client for tenant auto creation](#client-for-tenant-auto-creation-optional). It is required if we want the app to manage the tenants  (ex. my-app-api-client-secret) (Optional)
*   **RABBIT\_HOST:** The rabbitmq host (ex. rabbit) (Required if queue is enabled)
*   **RABBIT\_PORT:** The rabbitmq port (ex. 5672) (Required if queue is enabled)
*   **RABBIT\_USER:** The rabbitmq user (ex. guest) (Required if queue is enabled)
*   **RABBIT\_PASS:** The rabbitmq password (ex. guest) (Required if queue is enabled)
*   **ES\_ENABLED:** Sets if the elasticsearch is enabled  (ex. true) (Required)
*   **ES\_USE\_SSL:** Sets if the elasticsearch uses ssl (ex. true) (Required)
*   **ES\_HOST:** The elasticsearch url (ex. elastic:9200) (Required)
*   **ES\_SCHEME:** The elasticsearch scheme (ex. https) (Required)
*   **ES\_USERNAME:** The elasticsearch user (ex. guest) (Optional)
*   **ES\_PASSWORD:** The elasticsearch password (ex. guest) (Optional)
*   **ES\_CREDENTIAL\_CERTIFICATE\_PATH:** The elasticsearch authentication certificate path (ex. /certificates/elastic.ppk) (Optional)
*   **ES\_CREDENTIAL\_CERTIFICATE\_PASSWORD:** The elasticsearch authentication certificate password (ex. pass) (Optional)

### cors-\[profile\].yml

*   **web.cors**.**allowed-origins:** Add the web app base url (ex. https://example.com) (Required)

### elastic-\[profile\].yml

*   **elastic.disableHostnameVerifier:** Disables the host name ssl verification (ex. false) (Optional)
*   **elastic.disableHostnameVerifier:** Adds elastic certificate (ex. file:/certificates/elasticsearch.crt) (Optional)
*   **app-elastic.indicatorIndexName:** The index names that have the indicators (ex. sti-indicator) (Required)
*   **app-elastic.indicatorPointIndexNamePattern:** The pattern of the index names of the indicators data (ex. ic-sti-{{code}}-point) (Required)

### data-access-request-\[profile\].yml

*   **data-access-request.dataAccessRequestApprovedNotificationKey:** The notification id of the approved request (ex. dbbfd9af-435f-4f0c-ae4e-8f11d3afca15) (Required)
*   **data-access-request.dataAccessRequestRejectedNotificationKey:** The notification id of the rejected request (ex. 37744959-234a-4805-8e33-3b6dab6e9017) (Required)

### idpclaims-\[profile\].yml

Keep the mapping of the access token claims to the app claims. In most cases, the only change that should be made is under the Roles (Type resource\_access) to change the path for match with the [core app client](#core-app-client-required) name. If the patterns of the [keycloak-\[profile\].yml](#keycloak-profileyml) namingStrategy change also the Roles (Type authorities) and TenantCodes (Type authorities) should be changed.

### indicator-groups-\[profile\].yml

The groups of indicators that are used for app to aggregate data from multiple indexes. Example file :

```plaintext
indicator-group:
  groups:
    - groupId: BE240B1A-3D3B-4769-BC50-0D2345BFFD0F
      name: data-tree
      code: data-tree
      indicator-codes:
        - ai
        - climate
    - groupId: EA39A344-29C4-4EAC-BF7D-93DAD2CD5550
      name: portofolio
      code: portofolio
      indicator-codes:
        - portofolio
```

### keycloak-\[profile\].yml

*   **keycloak-resources.tenantGroupsNamingStrategy:** The naming strategy for the tenant specific groups that are created to the keycloack (ex. tenant-{tenantCode}) (Required)
*   **keycloak-resources.guestsGroup:** The [guests](#guests-group-required) group id  (ex. d51aab3c-c95e-475b-8201-4311b5d4f254) (Required)
*   **keycloak-resources.administratorsGroup:** The [admins](#tenants-admins-root-group-required) group id  (ex. 4dba77fd-3386-4cba-844f-101c03ee6b47) (Required)
*   **keycloak-resources.authorities.user.parent:** The [tenant user root](#tenants-users-root-group-required) group id  (ex. d51aab3c-c95e-475b-8201-4311b5d4f254) (Required)
*   **keycloak-resources.authorities.user.namingStrategy:** The [tenant user root](#tenants-users-root-group-required) group auth attribute value pattern (ex. 'tenantuser:{tenantCode}') (Required)
*   **keycloak-resources.authorities.user.title:** The title (ex. tenantuser) (Required)
*   **keycloak-resources.authorities.admin.parent:** The [tenant admin root](#tenants-admins-root-group-required) group id  (ex. d51aab3c-c95e-475b-8201-4311b5d4f254) (Required)
*   **keycloak-resources.authorities.admin.namingStrategy:** The [tenant admin root](#tenants-admins-root-group-required) group auth attribute value pattern (ex. 'tenantuser:{tenantCode}') (Required)
*   **keycloak-resources.authorities.admin.title:** The title (ex. tenantadmin) (Required)

### logging-\[profile\].yml

*   **logging.config:** Set the path of the logger config file (ex. /config/logback-${spring.profiles.active}.xml) (Required)

### queue-\[profile\].yml

*   **queue.rabbitmq.listenerEnabled:** Set the rabbitmq listener enabled/disabled (ex. false) (Required)
*   **queue.rabbitmq.publisherEnabled:** Set the rabbitmq publisher enabled/disabled (ex. false) (Required)
*   **queue.rabbitmq.publisherEnabled:** Set if the rabbitmq is durable (ex. false) (Required)
*   **queue.rabbitmq.queue:** Set the queue name (ex. core\_stiviewer\_inbox\_queue) (Required)
*   **queue.rabbitmq.exchange:** Set the exchange queue name (ex. app\_queue) (Required)
*   **queue.task.publisher.rabbitmq.enable:** Set the rabbitmq publisher enabled/disabled (ex. false) (Required)
*   **queue.task.publisher.options.exchange:** Set the exchange queue name (ex. app\_queue) (Required)
*   **queue.task.listener.rabbitmq.enable:** Set the rabbitmq listener enabled/disabled (ex. false) (Required)
*   **queue.task.listener.options.exchange:** Set the exchange queue name (ex. app\_queue) (Required)

### security-\[profile\].yml

*   **web.security.idp.resource.token-type:** Set the token type (ex. JWT) (Required)
*   **web.security.idp.resource.jwt.issuer-uri:** Set the exchange queue name (ex. http://keycloack:8080/auth/realms/realmname) (Required)

### server-\[profile\].yml

*   **server.forward-headers-strategy:** The forward headers strategy (ex. FRAMEWORK) (Required)
*   **server.ssl.enabled:** Set if the server has ssl enabled (ex. false) (Required)
*   **server.ssl.key-store-type:** Set the ssl certificate key store type (ex. PKCS12) (Optional)
*   **server.ssl.key-store:** Set the ssl certificate key store path (ex. /certificates/client-identity.p12) (Optional)
*   **server.ssl.key-store:** Set the ssl certificate key store password (ex. password) (Optional)
*   **server.ssl.key-alias:** Set the ssl certificate key alias (ex. key\_alias) (Optional)
*   **server.error.include-message:** Set if the server error includes the message (ex. false) (Required)

### tenant-request-dev-\[profile\].yml

*   **tenant-request.tenantRequestApprovedNotificationKey:** The notification id of the approved request (ex. 8cf6df0c-9de7-4465-9a1f-a32fb9a528b4) (Required)
*   **tenant-request.tenantRequestRejectedNotificationKey:** The notification id of the rejected request (ex. 8b95fcf9-8584-4963-b1e7-00a6ae4a6007) (Required)

### logback-\[profile\].xml

The logger configuration. Example file:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration debug="true">
<appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
        <encoder class="ch.qos.logback.classic.encoder.PatternLayoutEncoder">
            <Pattern>%date{ISO8601} [%thread] %-5level %logger{36} [%X{req.id}] - %message%n</Pattern>
        </encoder>
    </appender>
    <appender name="TROUBLESHOOTING" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>/logs/logging.log</file>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>/logs/logging.%d{yyyy-MM-dd}.%i.log</fileNamePattern>
            <timeBasedFileNamingAndTriggeringPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedFNATP">
                <maxFileSize>100MB</maxFileSize>
            </timeBasedFileNamingAndTriggeringPolicy>
            <maxHistory>15</maxHistory>
        </rollingPolicy>
        <encoder class="ch.qos.logback.classic.encoder.PatternLayoutEncoder">
            <Pattern>%date{ISO8601} [%thread] %-5level %logger{36} [%X{req.id}] - %message%n</Pattern>
        </encoder>
    </appender>

    <appender name="AUDITING" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>/logs/auditing.log</file>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>/logs/auditing.%d{yyyy-MM-dd}.%i.log</fileNamePattern>
            <timeBasedFileNamingAndTriggeringPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedFNATP">
                <maxFileSize>100MB</maxFileSize>
            </timeBasedFileNamingAndTriggeringPolicy>
            <maxHistory>15</maxHistory>
        </rollingPolicy>
        <encoder class="ch.qos.logback.classic.encoder.PatternLayoutEncoder">
            <Pattern>%date{ISO8601} - %X{req.id} - %message%n</Pattern>
        </encoder>
    </appender>

    <logger name="org.springframework.web" level="INFO" additivity="false">
        <appender-ref ref="TROUBLESHOOTING"/>
    </logger>
    <logger name="org.hibernate" level="INFO" additivity="false">
        <appender-ref ref="TROUBLESHOOTING"/>
    </logger>
    <logger name="gr.cite" level="INFO" additivity="false">
        <appender-ref ref="TROUBLESHOOTING"/>
    </logger>
    <logger name="org.springframework.data.elasticsearch.client.WIRE" level="INFO" additivity="false">
        <appender-ref ref="TROUBLESHOOTING"/>
    </logger>
    <logger name="audit" level="INFO" additivity="false">
        <appender-ref ref="AUDITING"/>
    </logger>
    <root level="info">
        <appender-ref ref="TROUBLESHOOTING"/>
    </root>
</configuration>
```

## Notification Api Configuration

### app.env

*   **WEB\_PORT:** The api port (ex. 8080) (Required)
*   **DB\_CONNECTION\_STRING:** Database connection string (ex. jdbc:postgresql://db:5432/sti-viewer?stringtype=unspecified) (Required)
*   **DB\_USER:** Database user (ex. user) (Required)
*   **DB\_PASSWORD:** Database password (ex. pass) (Required)
*   **THE\_API\_ID:** An Identifier for the app (ex. StiViewer.App.Web) (Required)
*   **CACHE\_DISAMBIGUATION:** The cache key for the app (ex. stiviewer\_ntf\_dev) (Required)
*   **RABBIT\_HOST:** The rabbitmq host (ex. rabbit) (Required if queue is enabled)
*   **RABBIT\_PORT:** The rabbitmq port (ex. 5672) (Required if queue is enabled)
*   **RABBIT\_USER:** The rabbitmq user (ex. guest) (Required if queue is enabled)
*   **RABBIT\_PASS:** The rabbitmq password (ex. guest) (Required if queue is enabled)
*   **MAIL\_HOST:** The mail server host (ex. mail.com) (Required)
*   **MAIL\_PORT:** The mail server port (ex. 25) (Required)
*   **MAIL\_USERNAME:** The mail server user (ex. guest) (Optional)
*   **MAIL\_PASSWORD:** The mail server password (ex. guest) (Optional)
*   **MAIL\_ADDRESS:** The mail sender address (ex. mail@app.gr) (Optional)

### cors-\[profile\].yml

*   **web.cors**.**allowed-origins:** Add the web app base url (ex. https://example.com) (Required)

### idpclaims-\[profile\].yml

Keep the mapping of the access token claims to the app claims. In most cases, the only change that should be made is under the Roles (Type resource\_access) to change the path for match with the [core app client](#core-app-client-required) name. If the patterns of the [keycloak-\[profile\].yml](#keycloak-profileyml) namingStrategy change also the Roles (Type authorities) and TenantCodes (Type authorities) should be changed.

### logging-\[profile\].yml

*   **logging.config:** Set the path of the logger config file (ex. /config/logback-${spring.profiles.active}.xml) (Required)

### queue-\[profile\].yml

*   **queue.rabbitmq.listenerEnabled:** Set the rabbitmq listener enabled/disabled (ex. false) (Required)
*   **queue.rabbitmq.publisherEnabled:** Set the rabbitmq publisher enabled/disabled (ex. false) (Required)
*   **queue.rabbitmq.publisherEnabled:** Set if the rabbitmq is durable (ex. false) (Required)
*   **queue.rabbitmq.queue:** Set the queue name (ex. core\_stiviewer\_inbox\_queue) (Required)
*   **queue.rabbitmq.exchange:** Set the exchange queue name (ex. app\_queue) (Required)
*   **queue.task.publisher.rabbitmq.enable:** Set the rabbitmq publisher enabled/disabled (ex. false) (Required)
*   **queue.task.publisher.options.exchange:** Set the exchange queue name (ex. app\_queue) (Required)
*   **queue.task.listener.rabbitmq.enable:** Set the rabbitmq listener enabled/disabled (ex. false) (Required)
*   **queue.task.listener.options.exchange:** Set the exchange queue name (ex. app\_queue) (Required)

### security-\[profile\].yml

*   **web.security.idp.resource.token-type:** Set the token type (ex. JWT) (Required)
*   **web.security.idp.resource.jwt.issuer-uri:** Set the exchange queue name (ex. http://keycloack:8080/auth/realms/realmname) (Required)

### server-\[profile\].yml

*   **server.forward-headers-strategy:** The forward headers strategy (ex. FRAMEWORK) (Required)
*   **server.ssl.enabled:** Set if the server has ssl enabled (ex. false) (Required)
*   **server.ssl.key-store-type:** Set the ssl certificate key store type (ex. PKCS12) (Optional)
*   **server.ssl.key-store:** Set the ssl certificate key store path (ex. /certificates/client-identity.p12) (Optional)
*   **server.ssl.key-store:** Set the ssl certificate key store password (ex. password) (Optional)
*   **server.ssl.key-alias:** Set the ssl certificate key alias (ex. key\_alias) (Optional)
*   **server.error.include-message:** Set if the server error includes the message (ex. false) (Required)

### logback-\[profile\].xml

The logger configuration. Example file:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration debug="true">
<appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
        <encoder class="ch.qos.logback.classic.encoder.PatternLayoutEncoder">
            <Pattern>%date{ISO8601} [%thread] %-5level %logger{36} [%X{req.id}] - %message%n</Pattern>
        </encoder>
    </appender>
    <appender name="TROUBLESHOOTING" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>/logs/logging.log</file>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>/logs/logging.%d{yyyy-MM-dd}.%i.log</fileNamePattern>
            <timeBasedFileNamingAndTriggeringPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedFNATP">
                <maxFileSize>100MB</maxFileSize>
            </timeBasedFileNamingAndTriggeringPolicy>
            <maxHistory>15</maxHistory>
        </rollingPolicy>
        <encoder class="ch.qos.logback.classic.encoder.PatternLayoutEncoder">
            <Pattern>%date{ISO8601} [%thread] %-5level %logger{36} [%X{req.id}] - %message%n</Pattern>
        </encoder>
    </appender>

    <appender name="AUDITING" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>/logs/auditing.log</file>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>/logs/auditing.%d{yyyy-MM-dd}.%i.log</fileNamePattern>
            <timeBasedFileNamingAndTriggeringPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedFNATP">
                <maxFileSize>100MB</maxFileSize>
            </timeBasedFileNamingAndTriggeringPolicy>
            <maxHistory>15</maxHistory>
        </rollingPolicy>
        <encoder class="ch.qos.logback.classic.encoder.PatternLayoutEncoder">
            <Pattern>%date{ISO8601} - %X{req.id} - %message%n</Pattern>
        </encoder>
    </appender>

    <logger name="org.springframework.web" level="INFO" additivity="false">
        <appender-ref ref="TROUBLESHOOTING"/>
    </logger>
    <logger name="org.hibernate" level="INFO" additivity="false">
        <appender-ref ref="TROUBLESHOOTING"/>
    </logger>
    <logger name="gr.cite" level="DEBUG" additivity="false">
        <appender-ref ref="TROUBLESHOOTING"/>
    </logger>
    <logger name="org.springframework.data.elasticsearch.client.WIRE" level="TRACE" additivity="false">
        <appender-ref ref="TROUBLESHOOTING"/>
    </logger>
    <logger name="audit" level="INFO" additivity="false">
        <appender-ref ref="AUDITING"/>
    </logger>
    <root level="info">
        <appender-ref ref="TROUBLESHOOTING"/>
    </root>
</configuration>
```

## User Api Configuration

### app.env

*   **WEB\_PORT:** The api port (ex. 8080) (Required)
*   **DB\_CONNECTION\_STRING:** Database connection string (ex. jdbc:postgresql://db:5432/sti-viewer?stringtype=unspecified) (Required)
*   **DB\_USER:** Database user (ex. user) (Required)
*   **DB\_PASSWORD:** Database password (ex. pass) (Required)
*   **THE\_API\_ID:** An Identifier for the app (ex. StiViewer.App.Web) (Required)
*   **CACHE\_DISAMBIGUATION:** The cache key for the app (ex. stiviewer\_ntf\_dev) (Required)
*   **RABBIT\_HOST:** The rabbitmq host (ex. rabbit) (Required if queue is enabled)
*   **RABBIT\_PORT:** The rabbitmq port (ex. 5672) (Required if queue is enabled)
*   **RABBIT\_USER:** The rabbitmq user (ex. guest) (Required if queue is enabled)
*   **RABBIT\_PASS:** The rabbitmq password (ex. guest) (Required if queue is enabled)

### cors-\[profile\].yml

*   **web.cors**.**allowed-origins:** Add the web app base url (ex. https://example.com) (Required)

### idpclaims-\[profile\].yml

Keep the mapping of the access token claims to the app claims. In most cases, the only change that should be made is under the Roles (Type resource\_access) to change the path for match with the [core app client](#core-app-client-required) name. If the patterns of the [keycloak-\[profile\].yml](#keycloak-profileyml) namingStrategy change also the Roles (Type authorities) and TenantCodes (Type authorities) should be changed.

### logging-\[profile\].yml

*   **logging.config:** Set the path of the logger config file (ex. /config/logback-${spring.profiles.active}.xml) (Required)

### queue-\[profile\].yml

*   **queue.rabbitmq.listenerEnabled:** Set the rabbitmq listener enabled/disabled (ex. false) (Required)
*   **queue.rabbitmq.publisherEnabled:** Set the rabbitmq publisher enabled/disabled (ex. false) (Required)
*   **queue.rabbitmq.publisherEnabled:** Set if the rabbitmq is durable (ex. false) (Required)
*   **queue.rabbitmq.queue:** Set the queue name (ex. core\_stiviewer\_inbox\_queue) (Required)
*   **queue.rabbitmq.exchange:** Set the exchange queue name (ex. app\_queue) (Required)
*   **queue.task.publisher.rabbitmq.enable:** Set the rabbitmq publisher enabled/disabled (ex. false) (Required)
*   **queue.task.publisher.options.exchange:** Set the exchange queue name (ex. app\_queue) (Required)
*   **queue.task.listener.rabbitmq.enable:** Set the rabbitmq listener enabled/disabled (ex. false) (Required)
*   **queue.task.listener.options.exchange:** Set the exchange queue name (ex. app\_queue) (Required)

### security-\[profile\].yml

*   **web.security.idp.resource.token-type:** Set the token type (ex. JWT) (Required)
*   **web.security.idp.resource.jwt.issuer-uri:** Set the exchange queue name (ex. http://keycloack:8080/auth/realms/realmname) (Required)

### server-\[profile\].yml

*   **server.forward-headers-strategy:** The forward headers strategy (ex. FRAMEWORK) (Required)
*   **server.ssl.enabled:** Set if the server has ssl enabled (ex. false) (Required)
*   **server.ssl.key-store-type:** Set the ssl certificate key store type (ex. PKCS12) (Optional)
*   **server.ssl.key-store:** Set the ssl certificate key store path (ex. /certificates/client-identity.p12) (Optional)
*   **server.ssl.key-store:** Set the ssl certificate key store password (ex. password) (Optional)
*   **server.ssl.key-alias:** Set the ssl certificate key alias (ex. key\_alias) (Optional)
*   **server.error.include-message:** Set if the server error includes the message (ex. false) (Required)

### logback-\[profile\].xml

The logger configuration. Example file:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration debug="true">
    <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
        <encoder class="ch.qos.logback.classic.encoder.PatternLayoutEncoder">
            <Pattern>%date{ISO8601} [%thread] %-5level %logger{36} [%X{req.id}] - %message%n</Pattern>
        </encoder>
    </appender>
    <appender name="TROUBLESHOOTING" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>/logs/logging.log</file>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>/logs/logging.%d{yyyy-MM-dd}.%i.log</fileNamePattern>
            <timeBasedFileNamingAndTriggeringPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedFNATP">
                <maxFileSize>100MB</maxFileSize>
            </timeBasedFileNamingAndTriggeringPolicy>
            <maxHistory>15</maxHistory>
        </rollingPolicy>
        <encoder class="ch.qos.logback.classic.encoder.PatternLayoutEncoder">
            <Pattern>%date{ISO8601} [%thread] %-5level %logger{36} [%X{req.id}] - %message%n</Pattern>
        </encoder>
    </appender>

    <appender name="AUDITING" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>/logs/auditing.log</file>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>/logs/auditing.%d{yyyy-MM-dd}.%i.log</fileNamePattern>
            <timeBasedFileNamingAndTriggeringPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedFNATP">
                <maxFileSize>100MB</maxFileSize>
            </timeBasedFileNamingAndTriggeringPolicy>
            <maxHistory>15</maxHistory>
        </rollingPolicy>
        <encoder class="ch.qos.logback.classic.encoder.PatternLayoutEncoder">
            <Pattern>%date{ISO8601} - %X{req.id} - %message%n</Pattern>
        </encoder>
    </appender>

    <logger name="org.springframework.web" level="INFO" additivity="false">
        <appender-ref ref="TROUBLESHOOTING"/>
    </logger>
    <logger name="org.hibernate" level="INFO" additivity="false">
        <appender-ref ref="TROUBLESHOOTING"/>
    </logger>
    <logger name="gr.cite" level="DEBUG" additivity="false">
        <appender-ref ref="TROUBLESHOOTING"/>
    </logger>
    <logger name="org.springframework.data.elasticsearch.client.WIRE" level="TRACE" additivity="false">
        <appender-ref ref="TROUBLESHOOTING"/>
    </logger>
    <logger name="audit" level="INFO" additivity="false">
        <appender-ref ref="AUDITING"/>
    </logger>
    <root level="info">
        <appender-ref ref="TROUBLESHOOTING"/>
    </root>
</configuration>
```

## Web App Configuration

### config.json

*   **idp\_service.address:** The keycloack url (ex. [http://keycloack:8080/auth](http://keycloack:8080/auth)) (Required)
*   **idp\_service.realm:** The keycloack realm name(ex. realmname) (Required)
*   **idp\_service.clientId:** The api client id for [Core App Client](#core-app-client-required) (ex. my-app-client) (Required)
*   **idp\_service.redirectUri:** The idp redirect url is the app\_path/login/post (ex.  https://example.com/app/login/post) (Required)
*   **notification\_service.address:** The notification api base url (ex.  https://example.com/api/notification/) (Required)
*   **app\_service.address:** The api base url (ex.  https://example.com/api/) (Required)
*   **user\_service.address:** The user api base url (ex.  https://example.com/api/user/) (Required)
*   **portofolioConfigurationKey:** The key of the portofolio config
*   **dataTreeConfigurationKey:** The key of the data tree config

The other options can be unmodified.