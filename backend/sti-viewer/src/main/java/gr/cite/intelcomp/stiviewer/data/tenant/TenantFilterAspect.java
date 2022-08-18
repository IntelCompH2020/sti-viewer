package gr.cite.intelcomp.stiviewer.data.tenant;

import gr.cite.commons.web.oidc.principal.CurrentPrincipalResolver;
import gr.cite.commons.web.oidc.principal.extractor.ClaimExtractor;
import gr.cite.intelcomp.stiviewer.common.scope.tenant.TenantScope;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.management.InvalidApplicationException;
import javax.persistence.EntityManager;

@Aspect
@Component
public class TenantFilterAspect {

    private final TenantScope tenantScope;
    private final ClaimExtractor claimExtractor;
    private final CurrentPrincipalResolver currentPrincipalResolver;
    private final ApplicationContext applicationContext;

    @Autowired
    public TenantFilterAspect(
            TenantScope tenantScope,
            ClaimExtractor claimExtractor,
            CurrentPrincipalResolver currentPrincipalResolver,
            ApplicationContext applicationContext
    ) {
        this.tenantScope = tenantScope;
        this.currentPrincipalResolver = currentPrincipalResolver;
        this.claimExtractor = claimExtractor;
        this.applicationContext = applicationContext;
    }

    @AfterReturning(
            pointcut="bean(entityManagerFactory) && execution(* createEntityManager(..))",
            returning="retVal")
    public void getSessionAfter(JoinPoint joinPoint, Object retVal) throws InvalidApplicationException {
        if (retVal != null && EntityManager.class.isInstance(retVal) && tenantScope.isSet()) {
            Session session = ((EntityManager) retVal).unwrap(Session.class);
            session.enableFilter(TenantScopedBaseEntity.tenantFilter).setParameter(TenantScopedBaseEntity.tenantFilterTenantParam, tenantScope.getTenant().toString());
        }
    }

}
