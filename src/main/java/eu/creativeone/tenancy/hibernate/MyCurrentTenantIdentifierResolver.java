package eu.creativeone.tenancy.hibernate;

import eu.creativeone.tenancy.security.OAuth2AuthenticationTenant;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

public class MyCurrentTenantIdentifierResolver implements CurrentTenantIdentifierResolver
{
    private final Logger log = LoggerFactory.getLogger(MyCurrentTenantIdentifierResolver.class);

    @Autowired
    private HttpServletRequest request;

    public final static String UNDEFINED_TENANT_ID = "undefined";

    private static final ThreadLocal<String> tenantIdOverride = new ThreadLocal<>();

    //Allow to override the tenant to used in the next calls.
    public static void forceTenantId(String tenantId)
    {
        MyCurrentTenantIdentifierResolver.tenantIdOverride.set(tenantId);
    }

    public static void resetTenantId()
    {
        MyCurrentTenantIdentifierResolver.tenantIdOverride.remove();
    }

    /**
     *
     * If the current principal is not a TenantUserDetails Object
     * classify him as anonymous user and only give him access to the
     * public database.
     */
    @Override
    public String resolveCurrentTenantIdentifier()
    {
        //allow to overide the tenant to be used
        String overridedTenantId = tenantIdOverride.get();
        if (overridedTenantId != null)
            return overridedTenantId;

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication != null && authentication.getPrincipal() != null)
        {
            if (authentication instanceof OAuth2AuthenticationTenant)
            {
                OAuth2AuthenticationTenant oAuth2AuthenticationTenant = (OAuth2AuthenticationTenant) authentication;

                log.info("MyCurrentTenantIdentifierResolver resolved tenant: " + oAuth2AuthenticationTenant.getTenantId());
                return oAuth2AuthenticationTenant.getTenantId();
            }

            //for internal service call, check X-Tenant-ID header
            Object principal = authentication.getPrincipal();
            if (principal != null && principal.equals("internal"))
            {
                RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
                if (requestAttributes instanceof ServletRequestAttributes)
                {
                    ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) requestAttributes;
                    String tenantId = servletRequestAttributes.getRequest().getHeader("X-Tenant-ID");
                    if (tenantId != null && !tenantId.isEmpty())
                        return tenantId;
                }
            }
        }

        log.info("MyCurrentTenantIdentifierResolver. No tenant Specified.");
        return UNDEFINED_TENANT_ID;
    }

    @Override
    public boolean validateExistingCurrentSessions() {
        return false;
    }
}
