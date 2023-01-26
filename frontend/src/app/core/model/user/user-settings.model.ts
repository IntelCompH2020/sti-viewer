import { BaseEntity} from '@common/base/base-entity.model';
import { Guid } from '@common/types/guid';


export interface UserSettings extends BaseEntity {
    key: string;
    value: string;
    entityType: UserSettingsEntityType;
    entityId: Guid;
    type: UserSettingsType;

}


export enum UserSettingsEntityType{
    User = 'User',
    Application = 'Application'
}

export enum UserSettingsType{
    Settings = 'Settings',
    Config = 'Config',
    Dashboard = 'Dashboard',
    BrowseDataTree = 'BrowseDataTree',
    GlobalSearch = 'GlobalSearch'
}


export interface UserSettingsPersist{
    id?: Guid;
    key: string;
    value: string;
    entityType: UserSettingsEntityType;
    entityId?: Guid;
    type: UserSettingsType;
    hash? :string;
}