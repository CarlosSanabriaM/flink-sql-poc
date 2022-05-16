package org.myorg.quickstart.operators.map;

import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.types.Row;

import java.util.LinkedHashSet;

public class DirectorsHistoryMap extends RichMapFunction<Row, LinkedHashSet<String>> {

    // LinkedHashSet remembers the order in which the elements were inserted into the set,
    // and returns its elements in that order.

    private transient ValueState<LinkedHashSet<String>> directorsState;

    @Override
    public void open(Configuration config) {
        // Initialize/Create/Retrieve the states
        directorsState = getRuntimeContext().getState(
                new ValueStateDescriptor<>(
                        "directorsState",
                        TypeInformation.of(new TypeHint<>() {
                        })
                ));
    }

    @Override
    public LinkedHashSet<String> map(Row event) throws Exception {
        LinkedHashSet<String> directors;

        if (directorsState.value() == null)
            // If the state is empty, create an empty set
            directors = new LinkedHashSet<>();
        else
            // If not, obtain the set from state
            directors = directorsState.value();

        // Add the current director to the set
        directors.add(event.getFieldAs("director"));

        // Update the state
        directorsState.update(directors);

        // Return the set
        return directors;
    }
}
