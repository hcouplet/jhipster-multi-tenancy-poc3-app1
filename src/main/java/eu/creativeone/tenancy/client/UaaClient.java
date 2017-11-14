package eu.creativeone.tenancy.client;

import eu.creativeone.client.AuthorizedFeignClient;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@AuthorizedFeignClient(name = "uaa")
public interface UaaClient
{
    @RequestMapping(value = "/api/tenants/")
    List<String> getAllTenants();

    @RequestMapping(value = "/api/tenants/")
    List<String> getAllTenants(@RequestHeader("X-Tenant-ID") String tenantId);
}
