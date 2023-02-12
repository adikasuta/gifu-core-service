package com.gifu.coreservice.utils;

import com.gifu.coreservice.entity.Status;
import com.gifu.coreservice.entity.Step;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SortUtilsTest {

    private List<Status> mockOrderedFromTopStatus() {
        List<Status> statuses = new ArrayList<>();
        Status st1 = new Status();
        st1.setId(1);
        st1.setNextStatusId(2L);
        statuses.add(st1);

        Status st2 = new Status();
        st2.setId(2);
        st2.setNextStatusId(3L);
        statuses.add(st2);

        Status st3 = new Status();
        st3.setId(3);
        st3.setNextStatusId(null);
        statuses.add(st3);

        return statuses;
    }

    private List<Step> mockUnsortedStep() {
        List<Step> steps = new ArrayList<>();
        Step st1 = new Step();
        st1.setId(1);
        st1.setNextStepId(2L);

        Step st2 = new Step();
        st2.setId(2);
        st2.setNextStepId(3L);

        Step st3 = new Step();
        st3.setId(3);
        st3.setNextStepId(null);
        steps.add(st2);
        steps.add(st1);
        steps.add(st3);

        return steps;
    }


    @Test
    public void test_orderFromTheBottom() {
        List<Status> statuses = mockOrderedFromTopStatus();
        statuses.sort(SortUtils.orderFlowFromBottom());
        Assertions.assertThat(statuses.get(0).getId()).isEqualTo(3);
        Assertions.assertThat(statuses.get(1).getId()).isEqualTo(2);
        Assertions.assertThat(statuses.get(2).getId()).isEqualTo(1);

        List<Step> steps = mockUnsortedStep();
        steps.sort(SortUtils.orderFlowFromBottom());
        Assertions.assertThat(steps.get(0).getId()).isEqualTo(3);
        Assertions.assertThat(steps.get(1).getId()).isEqualTo(2);
        Assertions.assertThat(steps.get(2).getId()).isEqualTo(1);
    }

    @Test
    public void test_orderFromTheTop() {
        List<Status> statuses = mockOrderedFromTopStatus();
        statuses.sort(SortUtils.orderFlowFromTop());
        Assertions.assertThat(statuses.get(0).getId()).isEqualTo(1);
        Assertions.assertThat(statuses.get(1).getId()).isEqualTo(2);
        Assertions.assertThat(statuses.get(2).getId()).isEqualTo(3);

        List<Step> steps = mockUnsortedStep();
        steps.sort(SortUtils.orderFlowFromTop());
        Assertions.assertThat(steps.get(0).getId()).isEqualTo(1);
        Assertions.assertThat(steps.get(1).getId()).isEqualTo(2);
        Assertions.assertThat(steps.get(2).getId()).isEqualTo(3);
    }
}