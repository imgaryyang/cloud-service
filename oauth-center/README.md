# 授权中心
access_token的有效期在表oauth_client_details的字段access_token_validity设置，单位是秒
修改该字段的话，请务必更新或者删除redis的key，是client_details，最好通过管理后台的管理界面进行修改


#登陆
1. TokenEndpoint 中 @RequestMapping(value = "/oauth/token", method=RequestMethod.POST)
2. 校验密码 AbstractUserDetailsAuthenticationProvider 中authenticate方法 additionalAuthenticationChecks 方法中


# 05.5 生成access_token的核心源码
TokenEndpoint 中OAuth2AccessToken token = getTokenGranter().grant(tokenRequest.getGrantType(), tokenRequest);
实现类  ClientCredentialsTokenGranter 中super.grant(grantType, tokenRequest);中getAccessToken(client, tokenRequest);
tokenServices.createAccessToken(getOAuth2Authentication(client, tokenRequest))
DefaultTokenServices 中createAccessToken方法


  
# 根据access_token获取当前用户的核心源码
3.注解@EnableResourceServer帮我们加入了org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationProcessingFilter<br>
 * 该filter帮我们从request里解析出access_token<br>
 * 并通过org.springframework.security.oauth2.provider.token.DefaultTokenServices根据access_token和认证服务器配置里的TokenStore从redis或者jwt里解析出用户
 * 
 * 注意认证中心的@EnableResourceServer和别的微服务里的@EnableResourceServer有些不同<br>
 * 别的微服务是通过org.springframework.boot.autoconfigure.security.oauth2.resource.UserInfoTokenServices来获取用户的