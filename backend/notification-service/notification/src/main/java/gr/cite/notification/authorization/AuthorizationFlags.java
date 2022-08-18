package gr.cite.notification.authorization;

import java.util.EnumSet;

public enum AuthorizationFlags {
	None, Permission, Owner, Indicator;
	public static final EnumSet<AuthorizationFlags> OwnerOrPermission = EnumSet.of(Owner, Permission);
	public static final EnumSet<AuthorizationFlags> OwnerOrPermissionOrIndicator = EnumSet.of(Owner, Permission, Indicator);
}
