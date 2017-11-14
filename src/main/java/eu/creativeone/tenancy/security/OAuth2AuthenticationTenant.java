package eu.creativeone.tenancy.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;

import java.util.Map;

public class OAuth2AuthenticationTenant extends OAuth2Authentication
{
    private String tenantId;

    public OAuth2AuthenticationTenant(OAuth2Request storedRequest, Authentication userAuthentication, Map<String, ?> map)
    {
        super(storedRequest, userAuthentication);
        String tenantId = (String) map.get("tid");
        setTenantId(tenantId);
    }

    public String getTenantId()
    {
        return tenantId;
    }

    public void setTenantId(String tenantId)
    {
        this.tenantId = tenantId;
    }
}
