### 背景

OAuth（开放授权）是一个开放标准，允许用户授权第三方应用访问他们存储在另外的服务提供者上的信息，而不需要将用户名和密码提供给第三方应用或分享他们数据的所有内容。 `OAuth2.0` 是OAuth协议的延续版本，但不向后兼容 `OAuth 1.0` ，即完全废止了 `OAuth1.0` 。很多大公司，国外的如Google，Netflix，Microsoft等，国内的像ByteDance，Alibaba，Tencent等都提供了OAuth认证服务（开放平台），这些都足以说明OAuth标准逐渐成为开放资源授权的标准。

该代码仓库主要结合 `SpringBoot` 、 `SpringSecurity` 、 `OAuth2.0` 等技术实现了 `OAuth2.0` 的四种授权方式的Demo，内容如下：

* OAuth2. 0四种授权方式（客户端信息存储在内存）
  + 授权码模式，源码在master分支：[https://github.com/heartsuit/demo-springboot-oauth2/tree/master/in-memory](https://github.com/heartsuit/demo-springboot-oauth2/tree/master/in-memory)
  + 简化模式：源码在implicit分支：[https://github.com/heartsuit/demo-springboot-oauth2/tree/implicit/in-memory](https://github.com/heartsuit/demo-springboot-oauth2/tree/implicit/in-memory)
  + 密码模式：源码在password分支：[https://github.com/heartsuit/demo-springboot-oauth2/tree/password/in-memory](https://github.com/heartsuit/demo-springboot-oauth2/tree/password/in-memory)
  + 客户端模式：源码在client分支：[https://github.com/heartsuit/demo-springboot-oauth2/tree/client/in-memory](https://github.com/heartsuit/demo-springboot-oauth2/tree/client/in-memory)
* OAuth2. 0客户端信息存储在MySQL数据库，access_token存储在内存、MySQL、Redis，源码路径：[https://github.com/heartsuit/demo-springboot-oauth2/tree/master/in-db](https://github.com/heartsuit/demo-springboot-oauth2/tree/master/in-db)
* OAuth2. 0客户端信息存储在MySQL数据库，access_token存储在JWT，源码路径：[https://github.com/heartsuit/demo-springboot-oauth2/tree/master/in-db-jwt/springboot-oauth2-authorization-server](https://github.com/heartsuit/demo-springboot-oauth2/tree/master/in-db-jwt/springboot-oauth2-authorization-server)  

### 四种授权方式

| 模式               | 名称       | 是否支持refresh_token | 是否需要结合浏览器测试 |
| ------------------ | ---------- | --------------------- | ---------------------- |
| authorization_code | 授权码模式 | 支持                  | 是                     |
| implicit           | 简化模式   | 不支持                | 是                     |
| password           | 密码模式   | 支持                  | 否                     |
| client_credentials | 客户端模式 | 不支持                | 否                     |

#### authorization_code 授权码模式 需结合浏览器测试

GET http://localhost:9000/oauth/authorize?client_id=client&response_type=code

1. 获取授权码，会回调至目标地址

http://localhost:9000/oauth/authorize?client_id=client&response_type=code

2. 认证之后，点击允许，获取授到权码

3. 通过授权码向授权服务获取令牌：access_token

http://client:secret@localhost:9000/oauth/token

将上一步获取的授权码作为x-www-form-urlencoded的一个参数：

grant_type:authorization_code
code:wgs8XF

获取到access_token

``` json
{
    "access_token": "d21a37e3-d423-4419-bb41-5712e23aee6b",
    "token_type": "bearer",
    "expires_in": 43200,
    "scope": "app"
}
```

4. 后续对资源服务的请求，附带access_token即可

http://localhost:8000/private/hi?access_token=d69bc334-3d5c-4c86-972c-11826f44c4af

源码在master分支：[https://github.com/heartsuit/demo-springboot-oauth2/tree/master/in-memory](https://github.com/heartsuit/demo-springboot-oauth2/tree/master/in-memory)

#### implicit 简化模式 不支持refresh token 需结合浏览器测试

GET http://localhost:9000/oauth/authorize?grant_type=implicit&response_type=token&scope=pc&client_id=client0&client_secret=secret0

源码在implicit分支：[https://github.com/heartsuit/demo-springboot-oauth2/tree/implicit/in-memory](https://github.com/heartsuit/demo-springboot-oauth2/tree/implicit/in-memory)

#### password 密码模式

POST http://localhost:9000/oauth/token?username=admin&password=123456&grant_type=password&scope=inner&client_id=client2&client_secret=secret2

源码在password分支：[https://github.com/heartsuit/demo-springboot-oauth2/tree/password/in-memory](https://github.com/heartsuit/demo-springboot-oauth2/tree/password/in-memory)

#### client_credentials 客户端模式 不支持refresh token

POST http://localhost:9000/oauth/token?grant_type=client_credentials&scope=api&client_id=client1&client_secret=secret1

源码在client分支：[https://github.com/heartsuit/demo-springboot-oauth2/tree/client/in-memory](https://github.com/heartsuit/demo-springboot-oauth2/tree/client/in-memory)

### 四种方式的授权流程

以下流程来自：[https://tools.ietf.org/html/rfc6749](https://tools.ietf.org/html/rfc6749)

#### authorization_code 授权码模式 

     +----------+
     | Resource |
     |   Owner  |
     |          |
     +----------+
          ^
          |
         (B)
     +----|-----+          Client Identifier      +---------------+
     |         -+----(A)-- & Redirection URI ---->|               |
     |  User-   |                                 | Authorization |
     |  Agent  -+----(B)-- User authenticates --->|     Server    |
     |          |                                 |               |
     |         -+----(C)-- Authorization Code ---<|               |
     +-|----|---+                                 +---------------+
       |    |                                         ^      v
      (A)  (C)                                        |      |
       |    |                                         |      |
       ^    v                                         |      |
     +---------+                                      |      |
     |         |>---(D)-- Authorization Code ---------'      |
     |  Client |          & Redirection URI                  |
     |         |                                             |
     |         |<---(E)----- Access Token -------------------'
     +---------+       (w/ Optional Refresh Token)

   Note: The lines illustrating steps (A), (B), and (C) are broken into
   two parts as they pass through the user-agent. 
                     Figure 3: Authorization Code Flow

  The flow illustrated in Figure 3 includes the following steps:

   (A)  The client initiates the flow by directing the resource owner's user-agent to the authorization endpoint.  The client includes its client identifier, requested scope, local state, and a redirection URI to which the authorization server will send the user-agent back once access is granted (or denied).
   
   (B)  The authorization server authenticates the resource owner (via the user-agent) and establishes whether the resource owner grants or denies the client's access request.

   (C)  Assuming the resource owner grants access, the authorization server redirects the user-agent back to the client using the redirection URI provided earlier (in the request or during client registration).  The redirection URI includes an authorization code and any local state provided by the client earlier.

   (D)  The client requests an access token from the authorization server's token endpoint by including the authorization code received in the previous step.  When making the request, the client authenticates with the authorization server.  The client includes the redirection URI used to obtain the authorization code for verification.

   (E)  The authorization server authenticates the client, validates the authorization code, and ensures that the redirection URI received matches the URI used to redirect the client in step (C).  If valid, the authorization server responds back with an access token and, optionally, a refresh token.

#### implicit 简化模式

     +----------+
     | Resource |
     |  Owner   |
     |          |
     +----------+
          ^
          |
         (B)
     +----|-----+          Client Identifier     +---------------+
     |         -+----(A)-- & Redirection URI --->|               |
     |  User-   |                                | Authorization |
     |  Agent  -|----(B)-- User authenticates -->|     Server    |
     |          |                                |               |
     |          |<---(C)--- Redirection URI ----<|               |
     |          |          with Access Token     +---------------+
     |          |            in Fragment
     |          |                                +---------------+
     |          |----(D)--- Redirection URI ---->|   Web-Hosted  |
     |          |          without Fragment      |     Client    |
     |          |                                |    Resource   |
     |     (F)  |<---(E)------- Script ---------<|               |
     |          |                                +---------------+
     +-|--------+
       |    |
      (A)  (G) Access Token
       |    |
       ^    v
     +---------+
     |         |
     |  Client |
     |         |
     +---------+

   Note: The lines illustrating steps (A) and (B) are broken into two
   parts as they pass through the user-agent. 
                       Figure 4: Implicit Grant Flow

   The flow illustrated in Figure 4 includes the following steps:

   (A)  The client initiates the flow by directing the resource owner's user-agent to the authorization endpoint.  The client includes its client identifier, requested scope, local state, and a redirection URI to which the authorization server will send the user-agent back once access is granted (or denied). 

   (B)  The authorization server authenticates the resource owner (via the user-agent) and establishes whether the resource owner grants or denies the client's access request.

   (C)  Assuming the resource owner grants access, the authorization server redirects the user-agent back to the client using the redirection URI provided earlier.  The redirection URI includes the access token in the URI fragment.

   (D)  The user-agent follows the redirection instructions by making a request to the web-hosted client resource (which does not include the fragment per [RFC2616]).  The user-agent retains the fragment information locally.

   (E)  The web-hosted client resource returns a web page (typically an HTML document with an embedded script) capable of accessing the full redirection URI including the fragment retained by the user-agent, and extracting the access token (and other parameters) contained in the fragment.

   (F)  The user-agent executes the script provided by the web-hosted client resource locally, which extracts the access token.

   (G)  The user-agent passes the access token to the client.

#### password 密码模式

     +----------+
     | Resource |
     |  Owner   |
     |          |
     +----------+
          v
          |    Resource Owner
         (A) Password Credentials
          |
          v
     +---------+                                  +---------------+
     |         |>--(B)---- Resource Owner ------->|               |
     |         |         Password Credentials     | Authorization |
     | Client  |                                  |     Server    |
     |         |<--(C)---- Access Token ---------<|               |
     |         |    (w/ Optional Refresh Token)   |               |
     +---------+                                  +---------------+
            Figure 5: Resource Owner Password Credentials Flow

   The flow illustrated in Figure 5 includes the following steps:

   (A)  The resource owner provides the client with its username and password.

   (B)  The client requests an access token from the authorization server's token endpoint by including the credentials received from the resource owner.  When making the request, the client authenticates with the authorization server.

   (C)  The authorization server authenticates the client and validates the resource owner credentials, and if valid, issues an access token.

#### client_credentials 客户端模式

     +---------+                                  +---------------+
     |         |                                  |               |
     |         |>--(A)- Client Authentication --->| Authorization |
     | Client  |                                  |     Server    |
     |         |<--(B)---- Access Token ---------<|               |
     |         |                                  |               |
     +---------+                                  +---------------+
                     Figure 6: Client Credentials Flow

   The flow illustrated in Figure 6 includes the following steps:

   (A)  The client authenticates with the authorization server and requests an access token from the token endpoint.

   (B)  The authorization server authenticates the client, and if valid, issues an access token.

### Reference

* [https://oauth.net/2/](https://oauth.net/2/)
* [https://tools.ietf.org/html/rfc6749](https://tools.ietf.org/html/rfc6749)
* [http://www.ruanyifeng.com/blog/2019/04/oauth-grant-types.html](http://www.ruanyifeng.com/blog/2019/04/oauth-grant-types.html)
