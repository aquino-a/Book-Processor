package com.aquino.webParser.fx.builder;

import com.aquino.webParser.fx.viewModel.MainViewModel;

import javafx.util.Builder;
import javafx.util.BuilderFactory;
import org.apache.commons.lang3.NotImplementedException;

public class AppBuilderFactory implements BuilderFactory {

    @Override
    public Builder<?> getBuilder(Class<?> type) {
        if (MainViewModel.class.equals(type)) {
            return () -> new MainViewModel();
        }
        throw new NotImplementedException("");
    }
}
