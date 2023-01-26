package gr.cite.intelcomp.stiviewer.authorization;

import java.util.*;

public class HierarchyIndicatorColumnAccess {
	private String code;
	private String value;
	private List<HierarchyIndicatorColumnAccess> childItems;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public List<HierarchyIndicatorColumnAccess> getChildItems() {
		return childItems;
	}

	public void setChildItems(List<HierarchyIndicatorColumnAccess> childItems) {
		this.childItems = childItems;
	}

	private void addChild(HierarchyIndicatorColumnAccess child) {
		if (this.childItems == null) this.childItems = new ArrayList<>();
		this.childItems.add(child);
	}

	public List<HierarchyIndicatorColumnAccess> getChildItemsWithCode(String code) {
		List<HierarchyIndicatorColumnAccess> response = new ArrayList<>();
		if (this.childItems != null) {
			for (HierarchyIndicatorColumnAccess child : this.childItems) {
				if (child.hasTheSameCode(code)) response.add(child);
			}
		}
		return response;
	}


	public boolean isLastChild() {
		return this.childItems == null || this.childItems.isEmpty();
	}

	private HierarchyIndicatorColumnAccess findChild(HierarchyIndicatorColumnAccess hierarchyIndicatorColumnAccess) {
		if (this.childItems != null) {
			for (HierarchyIndicatorColumnAccess child : this.childItems) {
				if (hierarchyIndicatorColumnAccess.hasTheSameParent(child)) return child;
			}
		}
		return null;
	}

	public boolean hasTheSameParent(HierarchyIndicatorColumnAccess hierarchyIndicatorColumnAccess) {
		return this.hasTheSameCode(hierarchyIndicatorColumnAccess.getCode()) && this.value != null && this.value.equalsIgnoreCase(hierarchyIndicatorColumnAccess.value);
	}

	public boolean hasTheSameCode(String code) {
		return this.code != null && this.code.equalsIgnoreCase(code);
	}

	public boolean tryMerge(HierarchyIndicatorColumnAccess hierarchyIndicatorColumnAccess) {
		if (this.hasTheSameParent(hierarchyIndicatorColumnAccess)) {
			if (this.isLastChild() && !hierarchyIndicatorColumnAccess.isLastChild()) {
				this.setChildItems(hierarchyIndicatorColumnAccess.getChildItems());
			} else if (!this.isLastChild() && !hierarchyIndicatorColumnAccess.isLastChild()) {
				for (HierarchyIndicatorColumnAccess externalChild : hierarchyIndicatorColumnAccess.getChildItems()) {
					HierarchyIndicatorColumnAccess localChild = this.findChild(externalChild);
					if (localChild == null) {
						this.addChild(externalChild);
					} else {
						localChild.tryMerge(externalChild);
					}
				}
			}
			return true;
		}
		return false;
	}



	public static Map<String, List<HierarchyIndicatorColumnAccess>> groupByCode(List<HierarchyIndicatorColumnAccess> indicatorColumnAccesses){
		Map<String, List<HierarchyIndicatorColumnAccess>> map = new HashMap<>();
		if (indicatorColumnAccesses != null) {
			for (HierarchyIndicatorColumnAccess indicatorColumnAccess: indicatorColumnAccesses) {
				if (map.containsKey(indicatorColumnAccess.getCode().toLowerCase(Locale.ROOT))){
					map.get(indicatorColumnAccess.getCode().toLowerCase(Locale.ROOT)).add(indicatorColumnAccess);
				} else {
					List<HierarchyIndicatorColumnAccess> newItems = new ArrayList<>();
					newItems.add(indicatorColumnAccess);
					map.put(indicatorColumnAccess.getCode().toLowerCase(Locale.ROOT), newItems);
				}
			}

		}
		return map;
	}
}
