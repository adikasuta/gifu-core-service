package com.gifu.coreservice.utils;

import com.gifu.coreservice.model.util.Flow;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class SortUtils {

    public static Comparator<Flow> orderFlowFromTop() {
        return ((flow, o) -> {
            if (o.getNextStepId() == null) {
                return -1;
            }
            if (flow.getNextStepId() == null) {
                return 1;
            }
            if (Objects.equals(flow.getNextStepId(), o.getCurrentStepId())) {
                return -1;
            }
            if (Objects.equals(o.getNextStepId(), flow.getCurrentStepId())) {
                return 1;
            }
            return 0;
        });
    }
    public static Comparator<Flow> orderFlowFromBottom() {
        return ((flow, o) -> {
            if (o.getNextStepId() == null) {
                return 1;
            }
            if (flow.getNextStepId() == null) {
                return -1;
            }
            if (Objects.equals(flow.getNextStepId(), o.getCurrentStepId())) {
                return 1;
            }
            if (Objects.equals(o.getNextStepId(), flow.getCurrentStepId())) {
                return -1;
            }
            return 0;
        });
    }

}
