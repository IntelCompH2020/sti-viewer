package gr.cite.intelcomp.stiviewer.authorization;

import java.util.EnumSet;

public enum AuthorizationFlags {
	None, Permission, Owner, Indicator, IndicatorAccess;
	public static final EnumSet<AuthorizationFlags> OwnerOrPermission = EnumSet.of(Owner, Permission);
	public static final EnumSet<AuthorizationFlags> OwnerOrPermissionOrIndicator = EnumSet.of(Owner, Permission, Indicator);
	public static final EnumSet<AuthorizationFlags> OwnerOrPermissionOrIndicatorOrIndicatorAccess = EnumSet.of(Owner, Permission, Indicator, IndicatorAccess);
}
