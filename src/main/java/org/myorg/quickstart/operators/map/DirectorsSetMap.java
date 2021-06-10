package org.myorg.quickstart.operators.map;

import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.configuration.Configuration;
import org.myorg.quickstart.model.DirectorsMovies;

import java.util.HashSet;
import java.util.Set;

public class DirectorsSetMap extends RichMapFunction<DirectorsMovies, Set<String>> {

    private transient ValueState<Set<String>> directorsSetState;

    @Override
    public void open(Configuration config) {
        // Initialize/Create/Retrieve the states
        directorsSetState = getRuntimeContext().getState(
                new ValueStateDescriptor<>(
                        "directorsSetState",
                        TypeInformation.of(new TypeHint<Set<String>>() {
                        })
                ));
    }

    @Override
    public Set<String> map(DirectorsMovies event) throws Exception {
        Set<String> directorsSet;

        if (directorsSetState.value() == null)
            // If the state is empty, create an empty set
            directorsSet = new HashSet<>();
        else
            // If not, obtain the set from state
            directorsSet = directorsSetState.value();

        // Add the current director to the set
        directorsSet.add(event.getDirector());

        // Update the state
        directorsSetState.update(directorsSet);

        // Return the set
        return directorsSet;
    }
}
