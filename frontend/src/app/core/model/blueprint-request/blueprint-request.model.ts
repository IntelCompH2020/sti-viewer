import { BlueprintRequestState } from '@app/core/enum/blueprint-request-state.enum';
import { Guid } from '@common/types/guid';
import { IsActive } from '@idp-service/core/enum/is-active.enum';
import { UserServiceUser } from '@user-service/core/model/user.model';

export interface BlueprintRequest {
	id: Guid;
	templateKey: Guid;
	fileId: Guid;
	state: BlueprintRequestState;
	isActive?: IsActive;
	createdAt?: Date;
	updatedAt?: Date;
	hash: string;
	language: string;
	user: UserServiceUser;
}
