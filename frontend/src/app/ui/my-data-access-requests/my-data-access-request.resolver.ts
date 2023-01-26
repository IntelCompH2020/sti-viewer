import { Injectable } from "@angular/core";
import { ActivatedRouteSnapshot, Resolve, RouterStateSnapshot } from "@angular/router";
import { PortofolioConfigService } from "@app/core/services/http/portofolio-config.service";
import { InstallationConfigurationService } from "@common/installation-configuration/installation-configuration.service";
import { Subject } from "rxjs";
import { filter, map, takeUntil } from "rxjs/operators";

@Injectable()
export class MyDataAccessRequestResolver implements Resolve<any> {


    public static RESOLVER_KEY = 'portofolio_config_key';

    constructor(
        private portofolioConfigService:PortofolioConfigService,
        private installationConfigurationService: InstallationConfigurationService,
    ){

    }

    protected _destroyed = new Subject<boolean>();
    ngOnDestroy(): void {
        this._destroyed.next(true);
		this._destroyed.complete();
    }



  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
    return this.portofolioConfigService.getMyConfigByKey(
        this.installationConfigurationService.portofolioConfigurationKey
    )
    .pipe(
        filter(x => !!x),
        map(x => deepFreeze(x)),
        takeUntil(this._destroyed)
    )
  }
}



function deepFreeze(obj1){
    Object.keys(obj1).forEach((property) => {
      if (
        typeof obj1[property] === "object" &&
        !Object.isFrozen(obj1[property])
      )
        deepFreeze(obj1[property]);
    });
    return Object.freeze(obj1);
};