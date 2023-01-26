package gr.cite.intelcomp.stiviewer.model.indicatorgroup;


import java.util.List;

public class IndicatorGroupAccessColumnConfigView {

    public static final String _code = "code";
    private String code;
    
    public static final String _items = "items";
    private List<IndicatorGroupAccessColumnConfigItemView> items;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<IndicatorGroupAccessColumnConfigItemView> getItems() {
        return items;
    }

    public void setItems(List<IndicatorGroupAccessColumnConfigItemView> items) {
        this.items = items;
    }
}
