package geologger.saints.com.geologger.foursquare.models;

import org.androidannotations.annotations.EBean;

/**
 * Created by Mizuno on 2015/01/26.
 */
@EBean
public class FourSquarePoi {

    public FourSquarePoi() {

    }

    private String id;
    private String name;
    private FourSquareLocation location;
    private FourSquarePoiCategory[] categories;
    private FourSquarePoiStates stats;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public FourSquareLocation getLocation() {
        return location;
    }

    public void setLocation(FourSquareLocation location) {
        this.location = location;
    }

    public FourSquarePoiCategory[] getCategories() {
        return categories;
    }

    public void setCategories(FourSquarePoiCategory[] categories) {
        this.categories = categories;
    }

    public FourSquarePoiStates getStats() {
        return stats;
    }

    public void setStats(FourSquarePoiStates stats) {
        this.stats = stats;
    }
}
