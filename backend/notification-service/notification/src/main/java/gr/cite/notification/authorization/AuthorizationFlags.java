package gr.cite.notification.authorization;

import java.util.EnumSet;

public enum AuthorizationFlags {
	None, Permission, Owner;
	public static final EnumSet<AuthorizationFlags> OwnerOrPermission = EnumSet.of(Owner, Permission);
}
