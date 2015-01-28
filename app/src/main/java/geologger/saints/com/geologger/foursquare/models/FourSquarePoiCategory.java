package geologger.saints.com.geologger.foursquare.models;

import org.androidannotations.annotations.EBean;

/**
 * Created by Mizuno on 2015/01/26.
 */
@EBean
public class FourSquarePoiCategory {

    private String id;
    private String name;
    private String pluralName;
    private String shortName;

    public FourSquarePoiCategory() {

    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

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

    public String getPluralName() {
        return pluralName;
    }

    public void setPluralName(String pluralName) {
        this.pluralName = pluralName;
    }


}
