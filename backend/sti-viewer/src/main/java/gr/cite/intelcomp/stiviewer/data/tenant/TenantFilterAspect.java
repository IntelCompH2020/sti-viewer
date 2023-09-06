package gr.cite.intelcomp.stiviewer.data.tenant;

import gr.cite.intelcomp.stiviewer.common.scope.tenant.TenantScope;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.management.InvalidApplicationException;
import javax.persistence.EntityManager;

@Aspect
@Component
public class TenantFilterAspect {

    private final TenantScope tenantScope;

    @Autowired
    public TenantFilterAspect(
            TenantScope tenantScope
    ) {
        this.tenantScope = tenantScope;
    }

    @AfterReturning(
            pointcut = "bean(entityManagerFactory) && execution(* createEntityManager(..))",
            returning = "retVal")
    public void getSessionAfter(JoinPoint joinPoint, Object retVal) throws InvalidApplicationException {
        if (retVal != null && retVal instanceof EntityManager && tenantScope.isSet()) {
            Session session = ((EntityManager) retVal).unwrap(Session.class);
            session
                    .enableFilter(TenantScopedBaseEntity.tenantFilter)
                    .setParameter(TenantScopedBaseEntity.tenantFilterTenantParam, tenantScope.getTenant().toString());
        }
    }

}
