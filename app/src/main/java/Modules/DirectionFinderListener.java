package Modules;

import android.os.Bundle;
import androidx.annotation.Nullable;

import java.util.List;


public interface DirectionFinderListener {

    void onConnected(@Nullable Bundle bundle);


    void onDirectionFinderStart();
    void onDirectionFinderSuccess(List<Route> route);
}
