package gr.cite.intelcomp.stiviewer.query.lookup;

import gr.cite.intelcomp.stiviewer.common.enums.BookmarkType;
import gr.cite.intelcomp.stiviewer.common.enums.IsActive;
import gr.cite.intelcomp.stiviewer.query.BookmarkQuery;
import gr.cite.tools.data.query.Lookup;
import gr.cite.tools.data.query.QueryFactory;

import java.util.List;
import java.util.UUID;

public class BookmarkLookup extends Lookup {
	private String like;
	private List<IsActive> isActive;
	private List<BookmarkType> types;
	private List<UUID> ids;
	private List<UUID> userIds;
	private UserLookup userSubQuery;

	public List<IsActive> getIsActive() {
		return isActive;
	}

	public void setIsActive(List<IsActive> isActive) {
		this.isActive = isActive;
	}

	public List<UUID> getIds() {
		return ids;
	}

	public void setIds(List<UUID> ids) {
		this.ids = ids;
	}

	public List<UUID> getUserIds() {
		return userIds;
	}

	public void setUserIds(List<UUID> userIds) {
		this.userIds = userIds;
	}


	public UserLookup getUserSubQuery() {
		return userSubQuery;
	}

	public void setUserSubQuery(UserLookup userSubQuery) {
		this.userSubQuery = userSubQuery;
	}

	public List<BookmarkType> getTypes() {
		return types;
	}

	public void setTypes(List<BookmarkType> types) {
		this.types = types;
	}

	public String getLike() {
		return like;
	}

	public void setLike(String like) {
		this.like = like;
	}

	public BookmarkQuery enrich(QueryFactory queryFactory) {
		BookmarkQuery query = queryFactory.query(BookmarkQuery.class);
		if (this.like != null) query.like(this.like);
		if (this.isActive != null) query.isActive(this.isActive);
		if (this.ids != null) query.ids(this.ids);
		if (this.userIds != null) query.userIds(this.userIds);
		if (this.types != null) query.types(this.types);
		if (this.userSubQuery != null) query.userSubQuery(this.userSubQuery.enrich(queryFactory));

		this.enrichCommon(query);

		return query;
	}
}
