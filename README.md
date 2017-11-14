
  
# Multi tenant jhipster microservice

This is the same as https://github.com/hcouplet/jhipster-multi-tenancy-poc2
but in microservices

For calls between services you can either use :
1. @AuthorizedFeignClient as usual, this will call external service without tenant info
2. @AuthorizedFeignClient with "X-Tenant-ID" requestHeader. For instance see example :

       @RequestMapping(value = "/api/tenants/")
       List<String> getAllTenants(@RequestHeader("X-Tenant-ID") String tenantId);
3. @AuthorizedUserFeignClient : this will forward the jwt token and you will also forward the tenant information
4. In case your service need to do some work on muliple tenant (for instance multi-tenant cron) use static :

       MyCurrentTenantIdentifierResolver.forceTenantId(String tenantId);

# jhipster
This application was generated using JHipster 4.10.2, you can find documentation and help at [http://www.jhipster.tech/documentation-archive/v4.10.2](http://www.jhipster.tech/documentation-archive/v4.10.2).

