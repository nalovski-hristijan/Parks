package com.hnalovski.parks.data;

import  com.hnalovski.parks.model.Park;
import java.util.List;

public interface AsyncResponse {
    void processPark(List<Park> parks);
}
