package geologger.saints.com.geologger.models;

/**
 * Created by Mizuno on 2015/01/29.
 */
public class CheckinEntry extends CheckinFreeFormEntry {

    public static final String PLACEID = "place_id";
    public static final String CATEGORYID = "category_id";

    private String placeId = null;
    private String categoryId = null;

    public CheckinEntry(){
        super();
    }
    public CheckinEntry(String tid, String placeId, String categoryId, String timestamp, float latitude, float longitude, String placeName) {
        super(tid, placeName, timestamp, latitude, longitude);
        this.setPlaceId(placeId);
        this.setCategoryId(categoryId);
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

}
