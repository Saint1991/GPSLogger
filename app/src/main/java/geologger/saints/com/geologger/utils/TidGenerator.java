package geologger.saints.com.geologger.utils;

import android.content.Context;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

import java.util.UUID;

import geologger.saints.com.geologger.database.TrajectorySpanSQLite;

/**
 * Created by Mizuno on 2015/04/04.
 */

@EBean
public class TidGenerator {

    @RootContext
    Context mContext;

    @Bean
    TrajectorySpanSQLite mTrajectorySpanDbHandler;

    public TidGenerator() {}

    public String generateUniqueTid() {

        String tidCandidate = null;
        while ( true ) {
            tidCandidate = UUID.randomUUID().toString();
            if (!mTrajectorySpanDbHandler.isExistTid(tidCandidate)) {
                break;
            }
        }

        return tidCandidate;
    }
}
