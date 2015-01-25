package geologger.saints.com.geologger.foursquare;

import org.androidannotations.annotations.EBean;

/**
 * Created by Mizuno on 2015/01/26.
 */
@EBean
public class FourSquarePoiStates {

    private int checkinsCount;
    private int usersCount;
    private int tipCount;

    public FourSquarePoiStates() {}

    public int getCheckinsCount() {
        return checkinsCount;
    }

    public void setCheckinsCount(int checkinsCount) {
        this.checkinsCount = checkinsCount;
    }

    public int getUsersCount() {
        return usersCount;
    }

    public void setUsersCount(int usersCount) {
        this.usersCount = usersCount;
    }

    public int getTipCount() {
        return tipCount;
    }

    public void setTipCount(int tipCount) {
        this.tipCount = tipCount;
    }

}
