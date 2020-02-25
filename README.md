# Using MSAL4J, Azure AD, Client Credentials Grant Flow, and Spring Boot to authenticate a Java service daemon with a protected Java web API

|   [Configuration](#setup-and-configuration)   | [Docker/Azure](DockerConfiguration.md)  |
| ---                       | ---   |

## About this sample

This example provides the necessary code, configuration guidance, and tests associated with calling a protected web API
using the [Java Spring Boot framework](https://spring.io/projects/spring-boot). We use Azure Active Directory as our
authorization server to provide a token to the calling application which passes it to the web server where it is
validated. The web API then either permits or denys the call to the URL using Spring Security.

A common way of providing Azure Active Directory authentication to a web API from a Single Page Application ("SPA") is
to use
[OAuth 2.0 implicit grant flow](https://docs.microsoft.com/en-us/azure/active-directory/develop/v2-oauth2-implicit-grant-flow).
However, if you needed to authenticate an application running as a service (daemon) without a real person providing, we rely on the
[OAuth 2.0 client credentials grant flow](https://docs.microsoft.com/en-us/azure/active-directory/develop/v2-oauth2-client-creds-grant-flow).

**NOTE:** This sample was recently updated to use the newer [MSAL4J](https://github.com/AzureAD/microsoft-authentication-library-for-java) library instead of the older [ADAL4J](https://github.com/AzureAD/azure-activedirectory-library-for-java).

## Scenario

We have the calling application, (the "Client") requesting a JSON Web Token ("JWT") from Azure Active Directory ("AAD").
The Client sends the JWT to the web API (the "Server") where the Server validates it and either permits or denys access
to the requested path.

![Solution](images/JavaWebAPIDaemon.png)

This flow relies on credentials used by the Client to request the JWT from AAD. The Client's credentials are obtained by
registering its identity in ADD and obtaining a "clientId" and "secret". Combining these two pieces together with
the tenantId, a JSON Web Token ("JWT") is obtained which contains the claims issued by the authorization server; in this
case, AAD. A more detailed explanation of this flow is outlined below.

## Project organization

This project is organized into two modules; the Client and the Server. Since this is a Java project, a root `pom.xml`
defines the parent and subordinate child modules each with their own `pom.xml` files.

### The "Client" (service daemon)

The [clientprototype](https://github.com/jasonshave/JavaClientCredentialFlowWebAPI/tree/master/clientprototype) folder
contains the code for running the Client. The client relies on the Microsoft Authentication Library for Java,
[MSAL4J](https://github.com/AzureAD/microsoft-authentication-library-for-java) to make the call to AAD (authorization server) in order to get the
JWT. It is the responsibility of the client to perform the following high-level tasks:

- Provide the clientId, client secret, scope, and resource URI to AAD
- Receive back a token from the AAD
- Call the web API with the "Bearer" token (JWT)

### The "Server" (web API)

The [serverprototype](https://github.com/jasonshave/JavaClientCredentialFlowWebAPI/tree/master/serverprototype) folder
contains most of the code for this sample as it has significantly more responsibility than the client as follows:

- Filter all HTTP calls and apply necessary security check
- Receive token from the Client
- Parse the token and validate it against AAD
- Permit (HTTP/200 OK) or deny (HTTP/401 Unauthorized) access to the requested URL

### Secrets and Settings

NOTE: This sample currently uses the `application-local.properties` file for both the serverprototype and
clientprototype modules which has been ignored by Git to store secrets and configuration information.
A future update will include instructions for incorporating Azure Key Vault's secret store to pull this information using a Managed Service Identity.

## Setup and Configuration

The setup of this sample involves the following high-level tasks which are broken down into detailed steps below:

1. [Configure the **Client** daemon application in Azure Active Directory](#configure-the-client-daemon-application-in-azure-active-directory).
    - Register and configure the Client application as a **registered application**
    - Create a secret
    - Obtain necessary additional parameters from AAD
2. [Configure the **Server** application in Azure Active Directory](#configure-the-server-application-in-azure-active-directory).
    - Register and configure the Server application as a **registered application**
3. Configure the `serverprototype` application settings.
4. Configure the `clientprototype` application settings.

### Configure the Client (daemon) application in Azure Active Directory

In this section you will configure the client application representation, obtain your client ID, secret, and later use this object to grant access to the server API (defined below).

NOTE: If you don't yet have access to Azure Active Directory, you can obtain a free tenant by
[clicking here](https://azure.microsoft.com/en-ca/free).

1. [Sign into Azure](https://portal.azure.com) and click on the **Azure Active Directory** link.

    ![AAD](images/aad.png)

2. Click on **App Registrations**.

    ![AppRegistrations](images/appregistrations.png)

3. Click **New registration**.

    ![NewRegistration](images/newregistration.png)

4. Type a **Name** for your application (i.e. clientprototype).

5. For the **Supported account types**, leave the default of **Accounts in this organizational directory only (*your tenant name* - Single tenant)**.

    - **NOTE:** This sample was only tested with single tenants in mind, however it may work with multi-tenant configurations as well.

6. Click the **Register** button.

7. Click the **Certificates & Secrets** menu item.

    ![CertificatesAndSecrets](images/certificatesandsecrets.png)

8. Under the **Client secrets** section click **New client secret**.

    ![NewClientSecret](images/newclientsecret.png)

9. Type a name for the **description** and select an expiration, or set it to never and click **Save**.

    ![AddClientSecret](images/addclientsecret.png)

10. Copy the client secret value somewhere and be sure to keep it outside of your code repository.

    **NOTE:** You will never be able to view this secret again if you navigate away from this page.
    However, you can always delete it and/or create another one.

### Configure the Server application in Azure Active Directory

In this section you will configure the server application in Azure AD, expose the API, and provide access for the client applicaiton you defined above.

1. From the same Azure Active Directory section in the portal, click on **App Registrations**.

    ![AppRegistrations](images/appregistrations.png)

2. Click **New registration**.

    ![NewRegistration](images/newregistration.png)

3. Type a **Name** for your application (i.e. serverprototype).

4. For the **Supported account types**, leave the default of **Accounts in this organizational directory only (*your tenant name* - Single tenant)**.

5. Click the **Register** button.

### Configure the 'serverprototype' application settings

1. From within the newly created server application registration, click on **Expose an API**

2. Click on the **Set** button to define the **Application ID URI**

3. Enter the FQDN of your application ID using your *tenant domain name* and *application name*, for example:

    ```plaintext
    https://mydomain.onmicrosoft.com/serverprototype
    ```

    ![applicationiduri](images/applicationiduri.png)

### Configure the 'clientprototype' application settings

If you are running this application locally on your workstation (i.e. in IntellIJ), follow these steps to configure your
`application-local.properties' file:

1. Under `/clientprototype/src/main/java/resources` create a file called `application-local.properties` and add this file to your `.gitignore`.

2. You will need to gather the following information from the previous steps:

    - Tenant ID
    - Client ID
    - Client secret

3. To obtain this information, go back to the Azure portal and locate your Client registered application, then click the **Overview** menu and retrieve the following:

    |   Name    |   Property    | Value | Purpose
    |   ---         | ---   | ---   | ---
    |   Application (client) ID     |   clientId   | *your_client_id*  | Used by MSAL4J client to identify your application
    |   Directory (tenant) ID       |   authority    | *your_tenant_id*  | Used by MSAL4J client to locate your tenant

4. In addition to the above values, you will retrieve the following:

    |   Property    | Value                 | Purpose
    |   ---         | ---                   | ---
    |   default-scope       | *your_application_id_uri*         | Used by MSAL4J to obtain a token from AAD
    |   clientSecret        | *your_client_secret*              | Used by MSAL4J to obtain a token from AAD
    |   resource-api-url    | http://localhost:8080/api/gettime | The URL you will use to test the web API on the "server"

5. In the `application-local.properties` file, populate it as follows:

    ```plaintext
    authority=https://login.microsoftonline.com/*your_tenant_id*/
    resource-api-url=http://localhost:8080/api/gettime
    default-scope=https://*your_domain_fqdn*.onmicrosoft.com/serverprototype
    clientId=*your_client_id*
    clientSecret=*your_client_secret*
    ```

    **NOTE:** When deploying your server code to Azure, be sure to modify the `resource-api-url` to match the URL of your App Service Plan.

6. Within IntelliJ click the **Edit Configurations** option on the top navigation bar.

    ![EditConfigurations](images/editconfiguration.png)

7. For the **ClientPrototypeApplication** set the **Active profiles** to "local".

    ![LocalProfile](images/intellij.png)

## Build and Test

Build both the Client and Server applications and run the Server; you should see the server listening on port 8080. When you run the client it will automatically talk to AAD, obtain a token, pass it to the server's `/api/gettime` URL. You should see the `Bearer` JWT (token) show up in the console as well.

### Checking your JWT Bearer token from Azure AD

1. Navigate to [JWT IO](https://jwt.io).

2. Locate your `Bearer` token in the client console window and paste it into the JWT website.
    - The signature should be validated
    - Check the issuer, audience, and application ID

### Verify your URL is protected

1. Using [Postman](https://www.postman.com/), test a `GET` request to your server's FQDN and path, for example: http://localhost:8080/api/gettime.

2. You should see the following:

    ```json
    {
        "timestamp": "2020-02-25T20:40:50.064+0000",
        "status": 401,
        "error": "Unauthorized",
        "message": "Access Denied",
        "path": "/api/gettime"
    }
    ```
